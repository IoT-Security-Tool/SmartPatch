"License"
"AS IS"
metadata {
	definition(name: "Z-Wave Mouse Trap", namespace: "smartthings", author: "SmartThings", mnmn: "SmartThings", vid: "generic-pestcontrol-1", runLocally: false, executeCommandsLocally: false) {
		capability "Sensor"
		capability "Battery"
		capability "Configuration"
		capability "Health Check"
		capability "Pest Control"
		

		
		fingerprint mfr: "021F", prod: "0003", model: "0104", deviceJoinName: "Dome Mouser", mnmn: "SmartThings", vid: "SmartThings-smartthings-Dome_Mouser"
	}

	tiles(scale: 2) {
		multiAttributeTile(name: "pestControl", type: "generic", width: 6, height: 4) {
			tileAttribute("device.pestControl", key: "PRIMARY_CONTROL") {
				attributeState("idle", label: 'IDLE', icon: "st.contact.contact.open", backgroundColor: "#00FF00")
				attributeState("trapRearmRequired", label: 'TRAP RE-ARM REQUIRED', icon: "st.contact.contact.open", backgroundColor: "#00A0DC")
				attributeState("trapArmed", label: 'TRAP ARMED', icon: "st.contact.contact.open", backgroundColor: "#FF6600")
				attributeState("pestDetected", label: 'PEST DETECTED', icon: "st.contact.contact.open", backgroundColor: "#FF6600")
				attributeState("pestExterminated", label: 'PEST EXTERMINATED', icon: "st.contact.contact.closed", backgroundColor: "#FF0000")
			}
		}
		valueTile("battery", "device.battery", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "battery", label: '${currentValue}% battery', unit: ""
		}
		standardTile("configure", "device.configure", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "configure", label: '', action: "configuration.configure", icon: "st.secondary.configure"
		}
		main "pestControl"
		details(["pestControl", "battery", "configure"])
	}
}


def ping() {
	log.debug "ping() called"
}

def parse(String description) {
	def result = []
	log.debug "desc: $description"
	def cmd = zwave.parse(description)
	if (cmd) {
		result = zwaveEvent(cmd)
	}
	log.debug "parsed '$description' to $result"
	return result
}

def installed() {
	log.debug "installed()"
	
	sendEvent(name: "checkInterval", value: 24 * 60 * 60 + 2 * 60, displayed: false, data: [protocol: "zwave", hubHardwareId: device.hub.hardwareID])
	initialize()
}

def updated() {
	log.debug "updated()"
	
	sendEvent(name: "checkInterval", value: 24 * 60 * 60 + 2 * 60, displayed: false, data: [protocol: "zwave", hubHardwareId: device.hub.hardwareID])
}

def initialize() {
	log.debug "initialize()"
	def cmds = []
	cmds << zwave.batteryV1.batteryGet().format()
	cmds << getConfigurationCommands()
	sendHubCommand(cmds)
}

def zwaveEvent(physicalgraph.zwave.commands.switchbinaryv1.SwitchBinaryReport cmd) {
	
	[]
}

def zwaveEvent(physicalgraph.zwave.commands.sensorbinaryv1.SensorBinaryReport cmd) {
	
	[]
}

def zwaveEvent(physicalgraph.zwave.commands.notificationv3.NotificationReport cmd) {
	log.debug "Event: ${cmd.event}, Notification type: ${cmd.notificationType}"
	def result = []
	def value
	def description
	if (cmd.notificationType == 0x07) {
		
		switch (cmd.event) {
			case 0x00:
				value = "idle"
				description = "Trap cleared"
				break
			case 0x07:
				value = "pestExterminated"
				description = "Pest exterminated"
				break
			default:
				log.debug "Not handled event type: ${cmd.event}"
				break
		}
		result = createEvent(name: "pestControl", value: value, descriptionText: description)
	} else if (cmd.notificationType == 0x13) {
		
		switch (cmd.event) {
			case 0x00:
				value = "idle"
				description = "Trap cleared"
				break
			case 0x02:
				value = "trapArmed"
				description = "Trap armed"
				break
			case 0x04:
				value = "trapRearmRequired"
				description = "Trap re-arm required"
				break
			case 0x06:
				value = "pestDetected"
				description = "Pest detected"
				break
			case 0x08:
				value = "pestExterminated"
				description = "Pest exterminated"
				break
			default:
				log.debug "Not handled event type: ${cmd.event}"
				break
		}
		result = createEvent(name: "pestControl", value: value, descriptionText: description)
	}
	result
}

def zwaveEvent(physicalgraph.zwave.commands.wakeupv1.WakeUpNotification cmd) {
	log.debug "WakeUpNotification ${cmd}"
	def event = createEvent(descriptionText: "${device.displayName} woke up", isStateChange: false)
	def cmds = []

	if (device.currentValue("pestControl") == null) { 
		cmds << getConfigurationCommands()
	}
	if (!state.lastbat || now() - state.lastbat > (12 * 60 * 60 + 6 * 60) * 1000 ) {
		cmds << zwave.batteryV1.batteryGet().format()
	} else {
		
		cmds << zwave.wakeUpV1.wakeUpNoMoreInformation().format()
	}
	[event, response(cmds)]
}

def zwaveEvent(physicalgraph.zwave.commands.batteryv1.BatteryReport cmd) {
	def map = [name: "battery", unit: "%"]
	if (cmd.batteryLevel == 0xFF) {
		map.value = 1
		map.descriptionText = "${device.displayName} has a low battery"
		map.isStateChange = true
	} else {
		log.debug "Battery report: $cmd"
		map.value = cmd.batteryLevel
	}
	state.lastbat = now()
	[createEvent(map), response(zwave.wakeUpV1.wakeUpNoMoreInformation())]
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	createEvent(descriptionText: "$device.displayName: $cmd", displayed: true)
}

def configure() {
	log.debug "config"
	response(getConfigurationCommands())
}

def getConfigurationCommands() {
	log.debug "getConfigurationCommands"
	def cmds = []
	cmds << zwave.notificationV3.notificationGet(notificationType: 0x13).format()
	
	cmds << zwave.wakeUpV2.wakeUpIntervalSet(seconds: 12 * 3600, nodeid: zwaveHubNodeId).format()

	
	cmds << zwave.configurationV1.configurationSet(parameterNumber: 1, size: 1, configurationValue: [255]).format()
	
	cmds << zwave.configurationV1.configurationSet(parameterNumber: 2, size: 1, configurationValue: [2]).format()
	
	cmds << zwave.configurationV1.configurationSet(parameterNumber: 3, size: 2, configurationValue: [360]).format()
	
	cmds << zwave.configurationV1.configurationSet(parameterNumber: 4, size: 1, configurationValue: [1]).format()
	
	cmds << zwave.configurationV1.configurationSet(parameterNumber: 5, size: 1, configurationValue: [0]).format()
	cmds
}
