"License"
"AS IS"

import physicalgraph.zwave.commands.*

metadata {
 definition (name: "Philio Multiple Sound Siren", namespace: "smartthings", author: "SmartThings", ocfDeviceType: "x.com.st.d.siren") {
	capability "Actuator"
	capability "Alarm"
	capability "Switch"
	capability "Health Check"
	capability "Chime"
	capability "Tamper Alert"

	command "test"

	fingerprint mfr: "013C", prod: "0004", model: "000A", deviceJoinName: "Philio Multiple Sound Siren PSE02"
 }

 simulator {
	
	reply "9881002001FF,9881002002": "command: 9881, payload: 002003FF"
	reply "988100200100,9881002002": "command: 9881, payload: 00200300"
	reply "9881002001FF,delay 3000,988100200100,9881002002": "command: 9881, payload: 00200300"
 }

 tiles(scale: 2) {
	multiAttributeTile(name:"alarm", type: "generic", width: 6, height: 4){
		tileAttribute ("device.alarm", key: "PRIMARY_CONTROL") {
			attributeState "off", label:'off', action:'alarm.both', icon:"st.alarm.alarm.alarm", backgroundColor:"#ffffff"
			attributeState "both", label:'alarm!', action:'alarm.off', icon:"st.alarm.alarm.alarm", backgroundColor:"#e86d13"
			attributeState "siren", label:'alarm!', action:'alarm.off', icon:"st.alarm.alarm.alarm", backgroundColor:"#e86d13"
		}
	}
	standardTile("test", "device.alarm", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
		state "default", label:'', action:"test", icon:"st.secondary.test"
	}
	standardTile("off", "device.alarm", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
		state "default", label:'', action:"alarm.off", icon:"st.secondary.off"
	}
	standardTile("chime", "device.chime", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
		state "default", label:'chime', action:"chime.chime", icon:"st.illuminance.illuminance.dark"
	}
	valueTile("tamper", "device.tamper", inactiveLabel: false, width: 2, height: 2, decoration: "flat") {
		state "detected", label:'tampered', backgroundColor: "#ff0000"
		state "clear", label:'tamper clear', backgroundColor: "#ffffff"
	}

	preferences {
		
		input "sound", "enum", title: "What sound should play for an alarm event?", description: "Default is 'Emergency'", options: ["Smoke", "Emergency", "Police", "Fire", "Ambulance"]
		input "duration", "enum", title: "How long should the sound play?", description: "Default is 'Forever'", options: ["Forever", "30 seconds", "1 minute", "2 minutes", "3 minutes", "5 minutes", "10 minutes", "20 minutes", "30 minutes", "45 minutes", "1 hour"]
	}

	main "alarm"
	details(["alarm", "test", "off", "chime", "tamper"])
 }
}

def getSoundMap() {[
	Smoke: [
			notificationType: 0x01,
			event: 0x01
		],
	
	
	Chime: [
			notificationType: 0x06,
			event: 0x16
		],
	Emergency: [
			notificationType: 0x07,
			event: 0x01
		],
	Police: [
			notificationType: 0x0A,
			event: 0x01
		],
	Fire: [
			notificationType: 0x0A,
			event: 0x02
		],
	Ambulance: [
			notificationType: 0x0A,
			event: 0x03
		]
]}


def getDurationMap() {[
	"Forever": 0,
	"30 seconds": 1,
	"1 minute": 2,
	"2 minutes": 4,
	"3 minutes": 6,
	"5 minutes": 10,
	"10 minutes": 20,
	"20 minutes": 40,
	"30 minutes": 60,
	"45 minutes": 90,
	"1 hour": 120
]}

def getDefaultSound() { "Emergency" }
def getDefaultDuration() { "Forever" }

def setupHealthCheck() {
	
	sendEvent(name: "checkInterval", value: 2 * 15 * 60 + 2 * 60, displayed: false, data: [protocol: "zwave", hubHardwareId: device.hub.hardwareID, offlinePingable: "1"])
}

def installed() {
	setupHealthCheck()

	state.sound = defaultSound
	state.duration = defaultDuration

	
	response([
			secure(zwave.basicV1.basicGet()),
			secure(zwave.configurationV1.configurationSet(parameterNumber: 31, size: 1, configurationValue: [durationMap[state.duration]]))
		])
}

def updated() {
	def commands = []

	setupHealthCheck()

	log.debug "settings: ${settings.inspect()}, state: ${state.inspect()}"

	def sound = settings.sound ?: state.sound
	def duration = settings.duration ?: state.duration

	if (sound != state.sound || duration != state.duration) {
		state.sound = sound
		state.duration = duration
		commands << secure(zwave.configurationV1.configurationSet(parameterNumber: 31, size: 1, configurationValue: [durationMap[duration]]))
	}

	response(commands)
}


private getCommandClassVersions() {
	[
		0x20: 1,  
		0x70: 1,  
		0x85: 2,  
		0x98: 1,  
	]
}


def parse(String description) {
	log.debug "parse($description)"
	def result = null

	if (description.startsWith("Err")) {
		if (zwInfo?.zw?.contains("s")) {
			result = createEvent(descriptionText:description, displayed:false)
		} else {
			result = createEvent(
					descriptionText: "This device failed to complete the network security key exchange. If you are unable to control it via SmartThings, you must remove it from your network and add it again.",
					eventType: "ALERT",
					name: "secureInclusion",
					value: "failed",
					displayed: true,
			)
		}
	} else {
		def cmd = zwave.parse(description, commandClassVersions)
		if (cmd) {
			result = zwaveEvent(cmd)
		}
	}
	log.debug "Parse returned ${result?.inspect()}"
	return result
}

def zwaveEvent(securityv1.SecurityMessageEncapsulation cmd) {
	def encapsulatedCommand = cmd.encapsulatedCommand(commandClassVersions)
	
	if (encapsulatedCommand) {
		zwaveEvent(encapsulatedCommand)
	}
}

def zwaveEvent(basicv1.BasicReport cmd) {
	log.debug "rx $cmd"
	handleDeviceEvent(cmd.value)
}

def zwaveEvent(sensorbinaryv2.SensorBinaryReport cmd) {
	log.debug "rx $cmd"
	def result = []

	if (cmd.sensorType == sensorbinaryv2.SensorBinaryReport.SENSOR_TYPE_TAMPER) {
		result << createEvent(name: "tamper", value: "detected")
	} else {
		result = handleDeviceEvent(cmd.sensorValue)
	}
	result
}

def zwaveEvent(notificationv3.NotificationReport cmd) {
	def result = []

	log.debug "rx $cmd"

	if (cmd.notificationType == notificationv3.NotificationReport.NOTIFICATION_TYPE_BURGLAR) {
		if (cmd.event == 3) {
			result << createEvent(name: "tamper", value: "detected")
		}
	} else {
		log.warn "Unknown cmd.notificationType: ${cmd.notificationType}"
		result << createEvent(descriptionText: cmd.toString(), isStateChange: false)
	}
	result
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	log.debug "Unhandled Z-Wave command $cmd"
	createEvent(displayed: false, descriptionText: "$device.displayName: $cmd")
}

def handleDeviceEvent(value) {
	log.debug "handleDeviceEvent $value"
	def result = [
		createEvent([name: "switch", value: value == 0xFF ? "on" : "off", displayed: false]),
		createEvent([name: "alarm", value: value == 0xFF ? "both" : "off"])
	]

	
	
	if (value == 0) {
		result << createEvent([name: "chime", value: "off"])
		result << createEvent([name: "tamper", value: "clear"])
	}

	result
}

def generateCommand(command) {
	def sound = (command && soundMap.containsKey(command)) ? soundMap[command] : soundMap[defaultSound]
	log.debug "Sending $command"

	secure(zwave.notificationV3.notificationReport(notificationType: sound.notificationType, event: sound.event))
}

def chimeOff() {
	log.debug "chimeOff()"
	sendEvent(name: "chime", value: "off")

	
	
	def currentAlarm = device.currentValue("alarm")
	if (currentAlarm && currentAlarm != "off") {
		log.debug "resetting alarm..."

		sendHubCommand(on())
	}
}

def chime() {
	def results = []
	log.debug "chime!"

	
	
	sendEvent(name: "chime", value: "chime")
	runIn(1, "chimeOff", [overwrite: true])

	generateCommand("Chime")
}

def on() {
	log.debug "sending on"
	generateCommand(state.sound)
}

def off() {
	log.debug "sending off"

	secure(zwave.basicV1.basicSet(value: 0x00))
}

def strobe() {
	on()
}

def siren() {
	on()
}

def both() {
	on()
}

def test() {
	[
		on(),
		"delay 3000",
		off()
	]
}

private secure(physicalgraph.zwave.Command cmd) {
	def zwInfo = zwaveInfo
	
	if (!zwInfo || (zwInfo?.zw?.contains("s") && zwInfo.sec?.contains(String.format("%02X", cmd.commandClassId)))) {
		log.debug "securely sending $cmd"
		zwave.securityV1.securityMessageEncapsulation().encapsulate(cmd).format()
	} else {
		log.debug "insecurely sending $cmd"
		cmd.format()
	}
}


def ping() {
	secure(zwave.basicV1.basicGet())
}
