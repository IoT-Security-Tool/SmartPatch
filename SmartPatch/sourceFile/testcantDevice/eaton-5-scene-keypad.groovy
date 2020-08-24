"License"
"AS IS"
metadata {
	definition(name: "Eaton 5-Scene Keypad", namespace: "smartthings", author: "SmartThings", mcdSync: true, mnmn: "SmartThings", vid: "SmartThings-smartthings-Eaton_5-Scene_Keypad") {
		capability "Actuator"
		capability "Health Check"
		capability "Refresh"
		capability "Sensor"
		capability "Switch"

		
		fingerprint mfr: "001A", prod: "574D", model: "0000", deviceJoinName: "Eaton Switch" 
	}

	tiles(scale: 2) {
		multiAttributeTile(name: "switch", type: "lighting", width: 6, height: 4, canChangeIcon: true) {
			tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#00A0DC"
				attributeState "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
			}
		}

		childDeviceTiles("outlets")

		standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat", width: 6, height: 2, backgroundColor: "#00a0dc") {
			state "default", label: '', action: "refresh.refresh", icon: "st.secondary.refresh"
		}

		main "switch"
	}
}

def installed() {
	log.debug "Installed $device.displayName"
	addChildSwitches()
	def cmds = []
	
	
	
	
	def indicator = 0
	for (group in 1..5) {
		cmds << zwave.indicatorV1.indicatorSet(value: indicator)
		cmds << zwave.associationV1.associationSet(groupingIdentifier: group, nodeId: [zwaveHubNodeId])
		cmds << zwave.sceneControllerConfV1.sceneControllerConfSet(dimmingDuration: 0, groupId: group, sceneId: group)
		indicator += 2 ** (5 - group)
	}
	cmds << zwave.indicatorV1.indicatorSet(value: indicator)
	cmds << zwave.manufacturerSpecificV2.manufacturerSpecificGet()

	
	sendEvent(name: "checkInterval", value: 2 * 15 * 60 + 2 * 60, displayed: false, data: [protocol: "zwave", hubHardwareId: device.hub.hardwareID, offlinePingable: "1"])
	sendEvent(name: "switch", value: "off")

	runIn(52, "initialize", [overwrite: true])
	

	sendHubCommand cmds, 3000
}

def updated() {
	
	if (!getDataValue("manufacturer")) {
		runIn(52, "initialize", [overwrite: true])  
	} else {
		
		
		
		
		initialize()
	}
	
	sendEvent(name: "checkInterval", value: 2 * 15 * 60 + 1 * 60, displayed: false, data: [protocol: "zwave", hubHardwareId: device.hub.hardwareID, offlinePingable: "1"])
}

def initialize() {
	if (!childDevices) {
		addChildSwitches()
	}
	def cmds = []
	
	
	
	
	for (retries in 1..2) {
		int indicator = 0
		for (group in 1..5) {
			cmds << zwave.indicatorV1.indicatorSet(value: indicator)
			cmds << zwave.associationV1.associationGet(groupingIdentifier: group)
			cmds << zwave.sceneControllerConfV1.sceneControllerConfGet(groupId: group)
			indicator += (2 ** (5 - group))
		}
		cmds << zwave.indicatorV1.indicatorSet(value: indicator)
	}

	if (!getDataValue("manufacturer")) {
		cmds << zwave.manufacturerSpecificV2.manufacturerSpecificGet()
	}

	cmds << zwave.indicatorV1.indicatorSet(value: 0)
	
	cmds << zwave.indicatorV1.indicatorGet()

	
	sendHubCommand cmds, 3100
}

def on() {
	def switchId = 1
	def state = "on"
	
	updateLocalSwitchState(switchId, state)
}

def off() {
	def switchId = 1
	def state = "off"
	
	updateLocalSwitchState(switchId, state)
}

def refresh() {
	
	response zwave.indicatorV1.indicatorGet()
}

def poll() {
	refresh()
}


def ping() {
	refresh()
}

def parse(String description) {
	def result = []
	def cmd = zwave.parse(description)
	log.debug "Parse [$description] to \"$cmd\""
	if (cmd) {
		result += zwaveEvent(cmd)
	}
	result
}

def zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv2.ManufacturerSpecificReport cmd) {
	log.debug "manufacturerId  : $cmd.manufacturerId"
	log.debug "manufacturerName: $cmd.manufacturerName"
	log.debug "productId       : $cmd.productId"
	log.debug "productTypeId   : $cmd.productTypeId"
	def msr = String.format("%04X-%04X-%04X", cmd.manufacturerId, cmd.productTypeId, cmd.productId)
	updateDataValue("MSR", msr)
	updateDataValue("manufacturer", cmd.manufacturerName)
	createEvent(descriptionText: "$device.displayName MSR: $msr", isStateChange: false)
}

def zwaveEvent(physicalgraph.zwave.commands.associationv2.AssociationReport cmd) {
	def event = [:]
	if (cmd.nodeId.any { it == zwaveHubNodeId }) {
		event = createEvent(descriptionText: "$device.displayName is associated in group ${cmd.groupingIdentifier}")
	} else {
		
		def cmds = []
		
		cmds << zwave.associationV1.associationSet(groupingIdentifier: cmd.groupingIdentifier, nodeId: [zwaveHubNodeId])
		cmds << zwave.associationV1.associationSet(groupingIdentifier: cmd.groupingIdentifier, nodeId: [zwaveHubNodeId])
		sendHubCommand cmds, 1500
	}
	event
}


def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd) {
	def resp = null
	if (cmd.value == 0) {
		
		
		resp = refresh()
		
	}
	resp
}

def zwaveEvent(physicalgraph.zwave.commands.sceneactivationv1.SceneActivationSet cmd) {
	
	setSwitchState(cmd.sceneId, "on")
}

def zwaveEvent(physicalgraph.zwave.commands.indicatorv1.IndicatorReport cmd) {
	def events = []
	
	
	events << setSwitchState(1, (cmd.value & 1) ? "on" : "off")
	
	events << setSwitchState(2, (cmd.value & 2) ? "on" : "off")
	
	events << setSwitchState(3, (cmd.value & 4) ? "on" : "off")
	
	events << setSwitchState(4, (cmd.value & 8) ? "on" : "off")
	
	events << setSwitchState(5, (cmd.value & 16) ? "on" : "off")
	events
}

def zwaveEvent(physicalgraph.zwave.commands.switchmultilevelv3.SwitchMultilevelStartLevelChange cmd) {
	
	
	return null
}

def zwaveEvent(physicalgraph.zwave.commands.switchmultilevelv3.SwitchMultilevelStopLevelChange cmd) {
	
	
	return null
}

def zwaveEvent(physicalgraph.zwave.commands.applicationstatusv1.ApplicationBusy cmd) {
	
	return null
}

def zwaveEvent(physicalgraph.zwave.commands.scenecontrollerconfv1.SceneControllerConfReport cmd) {
	if (cmd.groupId != cmd.sceneId) {
		
		def cmds = []
		cmds << zwave.sceneControllerConfV1.sceneControllerConfSet(dimmingDuration: 0, groupId: cmd.groupId, sceneId: cmd.groupId)
		cmds << zwave.sceneControllerConfV1.sceneControllerConfSet(dimmingDuration: 0, groupId: cmd.groupId, sceneId: cmd.groupId)
		sendHubCommand cmds, 1500
	}
	return null
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	log.debug "Unexpected zwave command $cmd"
	return null
}


void childOn(deviceNetworkId) {
	def switchId = deviceNetworkId?.split("/")[1] as Integer
	def state = "on"
	
	updateLocalSwitchState(switchId, state)
}


void childOff(deviceNetworkId) {
	def switchId = deviceNetworkId?.split("/")[1] as Integer
	def state = "off"
	
	updateLocalSwitchState(switchId, state)
}


private setSwitchState(switchId, state) {
	def event
	if (switchId == 1) {
		
		event = createEvent(name: "switch", value: "$state", descriptionText: "Switch $switchId was switched $state")
	} else {
		String childDni = "${device.deviceNetworkId}/$switchId"
		def child = childDevices.find { it.deviceNetworkId == childDni }
		if (!child) {
			log.error "Child device $childDni not found"
		}
		if (state != child?.currentState("switch")?.value) {
			
			child?.sendEvent(name: "switch", value: "$state")
			
			event = createEvent(descriptionText: "Switch $switchId was switched $state", isStateChange: true)
		}
	}
	event
}



private updateLocalSwitchState(childId, state) {
	def binarySwitchState = 0

	
	if (state == "on") {
		binarySwitchState += 2 ** (childId - 1)
	}

	
	if (childId != 1 && device?.currentState("switch")?.value == "on") {
		++binarySwitchState
	}
	for (i in 2..5) {
		
		if (i != childId) {
			String childDni = "${device.deviceNetworkId}/$i"
			def child = childDevices.find { it.deviceNetworkId == childDni }
			if (child?.device?.currentState("switch")?.value == "on") {
				binarySwitchState += 2 ** (i - 1)
			}
		}
	}

	def commands = []
	commands << zwave.indicatorV1.indicatorSet(value: binarySwitchState)
	commands << zwave.indicatorV1.indicatorGet()
	sendHubCommand commands, 100
}

private addChildSwitches() {
	for (i in 2..5) {
		String childDni = "${device.deviceNetworkId}/$i"
		def child = addChildDevice("Child Switch",
				childDni,
				device.hubId,
				[completedSetup: true,
				 label         : "$device.displayName Switch $i",
				 isComponent   : true,
				 componentName : "switch$i",
				 componentLabel: "Switch $i"
				])
		child.sendEvent(name: "switch", value: "off")
	}
}
