"License"
"AS IS"
metadata {
	definition(name: "Z-Wave Dual Switch", namespace: "smartthings", author: "SmartThings", mnmn: "SmartThings", vid: "generic-switch") {
		capability "Actuator"
		capability "Health Check"
		capability "Refresh"
		capability "Sensor"
		capability "Switch"
		capability "Configuration"

		
		
		fingerprint mfr: "0086", prod: "0103", model: "008C", deviceJoinName: "Aeotec Dual Nano Switch 1" 
		fingerprint mfr: "0086", prod: "0003", model: "008C", deviceJoinName: "Aeotec Dual Nano Switch 1" 
		
		fingerprint mfr: "0000", cc: "0x5E,0x25,0x27,0x81,0x71,0x60,0x8E,0x2C,0x2B,0x70,0x86,0x72,0x73,0x85,0x59,0x98,0x7A,0x5A", ccOut: "0x82", ui: "0x8700", deviceJoinName: "Aeotec Dual Nano Switch 1"
		fingerprint mfr: "0258", prod: "0003", model: "008B", deviceJoinName: "NEO Coolcam Light Switch 1"
		fingerprint mfr: "0258", prod: "0003", model: "108B", deviceJoinName: "NEO coolcam Light Switch 1"
		fingerprint mfr: "0312", prod: "C000", model: "C004", deviceJoinName: "EVA LOGIK Smart Plug 2CH 1"
		fingerprint mfr: "0312", prod: "FF00", model: "FF05", deviceJoinName: "Minoston Smart Plug 2CH 1"
		fingerprint mfr: "0312", prod: "C000", model: "C007", deviceJoinName: "Evalogik Outdoor Smart Plug 2CH 1"
	}

	
	tiles(scale: 2) {
		multiAttributeTile(name: "switch", type: "lighting", width: 6, height: 4, canChangeIcon: true) {
			tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#00A0DC"
				attributeState "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
			}
		}

		standardTile("refresh", "device.switch", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state "default", label: '', action: "refresh.refresh", icon: "st.secondary.refresh"
		}

		main "switch"
		details(["switch", "refresh"])
	}
}

def installed() {
	def componentLabel
	if (device.displayName.endsWith('1')) {
		componentLabel = "${device.displayName[0..-2]}2"
	} else {
		
		componentLabel = "$device.displayName 2"
	}
	try {
		String dni = "${device.deviceNetworkId}-ep2"
		addChildDevice("Z-Wave Binary Switch Endpoint", dni, device.hub.id,
			[completedSetup: true, label: "${componentLabel}", isComponent: false])
		log.debug "Endpoint 2 (Z-Wave Binary Switch Endpoint) added as $componentLabel"
	} catch (e) {
		log.warn "Failed to add endpoint 2 ($desc) as Z-Wave Binary Switch Endpoint - $e"
	}
	configure()
}

def updated() {
	configure()
}

def configure() {
	
	sendEvent(name: "checkInterval", value: 30 * 60 + 2 * 60, displayed: false, data: [protocol: "zwave", hubHardwareId: device.hub.hardwareID, offlinePingable: "1"])
	def commands = []
	if (zwaveInfo.mfr.equals("0258")) {
		commands << zwave.configurationV1.configurationSet(parameterNumber: 4, size: 1, configurationValue: [0]).format()
		commands << "delay 100"
	}
	if (zwaveInfo.mfr.equals("0086")) {
		
		commands << command(zwave.configurationV1.configurationSet(parameterNumber: 0x50, scaledConfigurationValue: 2, size: 1))
	}
	commands << command(zwave.basicV1.basicGet())
	response(commands + refresh())
}


private getCommandClassVersions() {
	[
		0x20: 1,  
		0x25: 1,  
		0x30: 1,  
		0x31: 2,  
		0x32: 3,  
		0x56: 1,  
		0x60: 3,  
		0x70: 2,  
		0x84: 1,  
		0x98: 1,  
		0x9C: 1   
	]
}

def parse(String description) {
	def result = null
	def cmd = zwave.parse(description, commandClassVersions)
	if (cmd) {
		result = zwaveEvent(cmd)
	}
	log.debug("'$description' parsed to $result")
	return createEvent(result)
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd, endpoint=null) {
	(endpoint == 1) ? [name: "switch", value: cmd.value ? "on" : "off"] : [:]
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd, endpoint=null) {
	(endpoint == 1) ? [name: "switch", value: cmd.value ? "on" : "off"] : [:]
}

def zwaveEvent(physicalgraph.zwave.commands.switchbinaryv1.SwitchBinaryReport cmd, endpoint=null) {
	(endpoint == 1) ? [name: "switch", value: cmd.value ? "on" : "off"] : [:]
}

def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation cmd) {
	def encapsulatedCommand = cmd.encapsulatedCommand(commandClassVersions)
	if (encapsulatedCommand) {
		zwaveEvent(encapsulatedCommand)
	} else {
		log.warn "Unable to extract encapsulated cmd from $cmd"
		createEvent(descriptionText: cmd.toString())
	}
}

def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiChannelCmdEncap cmd) {
	if (cmd.commandClass == 0x6C && cmd.parameter.size >= 4) { 
		
		cmd.parameter = cmd.parameter.drop(2)
		
		cmd.commandClass = cmd.parameter[0]
		cmd.command = cmd.parameter[1]
		cmd.parameter = cmd.parameter.drop(2)
	}
	def encapsulatedCommand = cmd.encapsulatedCommand([0x32: 3, 0x25: 1, 0x20: 1])
	if (cmd.sourceEndPoint == 1) {
		zwaveEvent(encapsulatedCommand, 1)
	} else { 
		childDevices[0]?.handleZWave(encapsulatedCommand)
		[:]
	}
}

def zwaveEvent(physicalgraph.zwave.commands.crc16encapv1.Crc16Encap cmd) {
	def versions = commandClassVersions
	def version = versions[cmd.commandClass as Integer]
	def ccObj = version ? zwave.commandClass(cmd.commandClass, version) : zwave.commandClass(cmd.commandClass)
	def encapsulatedCommand = ccObj?.command(cmd.command)?.parse(cmd.data)
	if (encapsulatedCommand) {
		zwaveEvent(encapsulatedCommand)
	}
	[:]
}

def zwaveEvent(physicalgraph.zwave.Command cmd, endpoint = null) {
	if (endpoint == null) log.debug("$device.displayName: $cmd")
	else log.debug("$device.displayName: $cmd endpoint: $endpoint")
}

def on() {
	
	def endpointNumber = 1
	delayBetween([
		encap(endpointNumber, zwave.switchBinaryV1.switchBinarySet(switchValue: 0xFF)),
		encap(endpointNumber, zwave.switchBinaryV1.switchBinaryGet())
	])
}

def off() {
	
	def endpointNumber = 1
	delayBetween([
		encap(endpointNumber, zwave.switchBinaryV1.switchBinarySet(switchValue: 0x00)),
		encap(endpointNumber, zwave.switchBinaryV1.switchBinaryGet())
	])
}


def ping() {
	refresh()
}

def refresh() {
	
	[encap(1, zwave.switchBinaryV1.switchBinaryGet()), encap(2, zwave.switchBinaryV1.switchBinaryGet())]
}


def sendCommand(endpointDevice, commands) {
	
	def endpointNumber = 2
	def result
	if (commands instanceof String) {
		commands = commands.split(',') as List
	}
	result = commands.collect { encap(endpointNumber, it) }
	sendHubCommand(result, 100)
}

def encap(endpointNumber, cmd) {
	if (cmd instanceof physicalgraph.zwave.Command) {
		command(zwave.multiChannelV3.multiChannelCmdEncap(destinationEndPoint: endpointNumber).encapsulate(cmd))
	} else if (cmd.startsWith("delay")) {
		cmd
	} else {
		def header = "600D00"
		String.format("%s%02X%s", header, endpointNumber, cmd)
	}
}

private command(physicalgraph.zwave.Command cmd) {
	if (zwaveInfo.zw.contains("s")) {
		secEncap(cmd)
	} else if (zwaveInfo?.cc?.contains("56")){
		crcEncap(cmd)
	} else {
		cmd.format()
	}
}

private secEncap(physicalgraph.zwave.Command cmd) {
	zwave.securityV1.securityMessageEncapsulation().encapsulate(cmd).format()
}

private crcEncap(physicalgraph.zwave.Command cmd) {
	zwave.crc16EncapV1.crc16Encap().encapsulate(cmd).format()
}
