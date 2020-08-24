

"License"
"AS IS"
metadata {
	definition (name: "Zooz Power Strip", namespace: "smartthings", author: "SmartThings") {
		capability "Switch"
		capability "Refresh"
		capability "Actuator"
		capability "Sensor"
		capability "Configuration"

		fingerprint manufacturer: "015D", prod: "0651", model: "F51C", deviceJoinName: "Zooz ZEN 20 Power Strip"
	}

	tiles {
		multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState:"turningOn"
				attributeState "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#00A0DC", nextState:"turningOff"
				attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#00A0DC", nextState:"turningOff"
				attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff", nextState:"turningOn"
			}
		}
		childDeviceTiles("outlets")
		standardTile("refresh", "device.switch", width: 1, height: 1, inactiveLabel: false, decoration: "flat") {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}
	}
}




def installed() {
	createChildDevices()
}

def updated() {
	if (!childDevices) {
		createChildDevices()
	}
	else if (device.label != state.oldLabel) {
		childDevices.each {
			def newLabel = "${device.displayName} (CH${channelNumber(it.deviceNetworkId)})"
			it.setLabel(newLabel)
		}
		state.oldLabel = device.label
	}
}

def configure() {
	refresh()
}





def parse(String description) {
	trace "parse('$description')"
	def result = []
	if (description.startsWith("Err")) {
		result = createEvent(descriptionText:description, isStateChange:true)
	} else if (description != "updated") {
		def cmd = zwave.parse(description, [0x60: 3, 0x32: 3, 0x25: 1, 0x20: 1])
		if (cmd) {
			result += zwaveEvent(cmd, 1)
		}
		else {
			log.warn "Unparsed description $description"
		}
	}
	result
}

def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiChannelCmdEncap cmd, ep) {
	if (cmd.commandClass == 0x6C && cmd.parameter.size >= 4) { 
		
		cmd.parameter = cmd.parameter.drop(2)
		
		cmd.commandClass = cmd.parameter[0]
		cmd.command = cmd.parameter[1]
		cmd.parameter = cmd.parameter.drop(2)
	}
	def encapsulatedCommand = cmd.encapsulatedCommand([0x32: 3, 0x25: 1, 0x20: 1])
	if (encapsulatedCommand) {
		zwaveEvent(encapsulatedCommand, cmd.sourceEndPoint as Integer)
	}
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd, endpoint) {
	trace "zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport $cmd, $endpoint)"
	zwaveBinaryEvent(cmd, endpoint)
}

def zwaveEvent(physicalgraph.zwave.commands.switchbinaryv1.SwitchBinaryReport cmd, endpoint) {
	trace "zwaveEvent(physicalgraph.zwave.commands.switchbinaryv1.SwitchBinaryReport $cmd, $endpoint)"
	zwaveBinaryEvent(cmd, endpoint)
}

def zwaveBinaryEvent(cmd, endpoint) {
	def result = []
	def children = childDevices
	def childDevice = children.find{it.deviceNetworkId.endsWith("$endpoint")}
	if (childDevice) {
		childDevice.sendEvent(name: "switch", value: cmd.value ? "on" : "off")

		if (cmd.value) {
			
			result << createEvent(name: "switch", value: "on")
		} else {
			
			if (!children.any { it.currentValue("switch") == "on" }) {
				result << createEvent(name: "switch", value: "off")
			}
		}
	}
	result
}

def zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv2.ManufacturerSpecificReport cmd, ep) {
	updateDataValue("MSR", String.format("%04X-%04X-%04X", cmd.manufacturerId, cmd.productTypeId, cmd.productId))
	return null
}

def zwaveEvent(physicalgraph.zwave.commands.versionv1.VersionReport cmd, ep) {
	trace "applicationVersion $cmd.applicationVersion"
}

def zwaveEvent(physicalgraph.zwave.Command cmd, ep) {
	log.warn("${device.displayName}: Unhandled ${cmd}" + (ep ? " from endpoint $ep" : ""))
}




def on() {
	def cmds = []
	def cmd = zwave.switchBinaryV1.switchBinarySet(switchValue: 0xFF)
	cmds << zwave.multiChannelV3.multiChannelCmdEncap(bitAddress: true, destinationEndPoint:0x1F).encapsulate(cmd).format()
	cmds << "delay 400"
	cmds.addAll(refresh())
	return cmds
}

def off() {
	def cmds = []
	def cmd = zwave.switchBinaryV1.switchBinarySet(switchValue: 0x00)
	cmds << zwave.multiChannelV3.multiChannelCmdEncap(bitAddress: true, destinationEndPoint:0x1F).encapsulate(cmd).format()
	cmds << "delay 400"
	cmds.addAll(refresh())
	return cmds
}




void childOn(String dni) {
	onOffCmd(0xFF, channelNumber(dni))
}

void childOff(String dni) {
	onOffCmd(0, channelNumber(dni))
}

def refresh() {
	def cmds = (1..5).collect { endpoint ->
		encap(zwave.switchBinaryV1.switchBinaryGet(), endpoint)
	}
	delayBetween(cmds, 100)
}




private channelNumber(String dni) {
	dni.split("-ep")[-1] as Integer
}

private void onOffCmd(value, endpoint = null) {
	def actions = [
			new physicalgraph.device.HubAction(encap(zwave.basicV1.basicSet(value: value), endpoint)),
			new physicalgraph.device.HubAction(encap(zwave.switchBinaryV1.switchBinaryGet(), endpoint)),
	]
	sendHubCommand(actions, 500)
}

private void createChildDevices() {
	state.oldLabel = device.label
	for (i in 1..5) {
		addChildDevice("Zooz Power Strip Outlet",
				"${device.deviceNetworkId}-ep${i}",
				device.hubId,
				[completedSetup: true,
				 label: "${device.displayName} (CH${i})",
				 isComponent: true,
				 componentName: "ch$i",
				 componentLabel: "Channel $i"])
	}
}

private encap(cmd, endpoint) {
	if (endpoint) {
		zwave.multiChannelV3.multiChannelCmdEncap(destinationEndPoint:endpoint).encapsulate(cmd).format()
	} else {
		cmd.format()
	}
}

private trace(msg) {
	
}
