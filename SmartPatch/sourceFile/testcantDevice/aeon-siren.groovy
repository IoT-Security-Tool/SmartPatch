"License"
"AS IS"
metadata {
 definition (name: "Aeon Siren", namespace: "smartthings", author: "SmartThings", ocfDeviceType: "x.com.st.d.siren", runLocally: true, minHubCoreVersion: '000.017.0012', executeCommandsLocally: false) {
	capability "Actuator"
	capability "Alarm"
	capability "Switch"
	capability "Health Check"

	command "test"

	fingerprint deviceId: "0x1005", inClusters: "0x5E,0x98", deviceJoinName: "Aeotec Siren" 
 }

 simulator {
	
	reply "9881002001FF,9881002002": "command: 9881, payload: 002003FF"
	reply "988100200100,9881002002": "command: 9881, payload: 00200300"
	reply "9881002001FF,delay 3000,988100200100,9881002002": "command: 9881, payload: 00200300"
 }

 tiles(scale: 2) {
	multiAttributeTile(name:"alarm", type: "generic", width: 6, height: 4){
		tileAttribute ("device.alarm", key: "PRIMARY_CONTROL") {
			attributeState "off", label:'off', action:'alarm.siren', icon:"st.alarm.alarm.alarm", backgroundColor:"#ffffff"
			attributeState "both", label:'alarm!', action:'alarm.off', icon:"st.alarm.alarm.alarm", backgroundColor:"#e86d13"
		}
	}
	standardTile("test", "device.alarm", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
		state "default", label:'', action:"test", icon:"st.secondary.test"
	}
	standardTile("off", "device.alarm", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
		state "default", label:'', action:"alarm.off", icon:"st.secondary.off"
	}

	preferences {
		
		input "sound", "number", title: "Siren sound", range: "1..5" 
		input "volume", "number", title: "Volume", range: "1..3" 
	}

	main "alarm"
	details(["alarm", "test", "off"])
 }
}

def installed() {

	sendEvent(name: "checkInterval", value: 2 * 15 * 60 + 2 * 60, displayed: false, data: [protocol: "zwave", hubHardwareId: device.hub.hardwareID, offlinePingable: "1"])

	
	response([secure(zwave.basicV1.basicGet()), secure(zwave.configurationV1.configurationSet(parameterNumber: 80, size: 1, configurationValue: [2]))])
}

def updated() {
	log.debug "updated()"
	def commands = []

	
	sendEvent(name: "checkInterval", value: 2 * 15 * 60 + 2 * 60, displayed: false, data: [protocol: "zwave", hubHardwareId: device.hub.hardwareID, offlinePingable: "1"])

	log.debug "Scheduling health check every 15 minutes"
	unschedule("healthPoll", [forceForLocallyExecuting: true])
	runEvery15Minutes("healthPoll", [forceForLocallyExecuting: true])

	if(!state.sound) state.sound = 1
	if(!state.volume) state.volume = 3

	log.debug "settings: ${settings.inspect()}, state: ${state.inspect()}"

	Short sound = (settings.sound as Short) ?: 1
	Short volume = (settings.volume as Short) ?: 3

	if (sound != state.sound || volume != state.volume) {
		state.sound = sound
		state.volume = volume
		commands << secure(zwave.configurationV1.configurationSet(parameterNumber: 37, size: 2, configurationValue: [sound, volume]))
		commands << "delay 1000"
		commands << secure(zwave.basicV1.basicSet(value: 0x00))
	}

	
	commands << secure(zwave.configurationV1.configurationSet(parameterNumber: 80, size: 1, configurationValue: [2]))

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
		if (state.sec) {
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

def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation cmd) {
	def encapsulatedCommand = cmd.encapsulatedCommand(commandClassVersions)
	
	if (encapsulatedCommand) {
		zwaveEvent(encapsulatedCommand)
	}
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd) {
	log.debug "rx $cmd"
	[
		createEvent([name: "switch", value: cmd.value ? "on" : "off", displayed: false]),
		createEvent([name: "alarm", value: cmd.value ? "both" : "off"])
	]
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	createEvent(displayed: false, descriptionText: "$device.displayName: $cmd")
}

def on() {
	log.debug "sending on"
	[
		secure(zwave.basicV1.basicSet(value: 0xFF)),
		secure(zwave.basicV1.basicGet())
	]
}

def off() {
	log.debug "sending off"
	[
		secure(zwave.basicV1.basicSet(value: 0x00)),
		secure(zwave.basicV1.basicGet())
	]
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
		secure(zwave.basicV1.basicSet(value: 0xFF)),
		"delay 3000",
		secure(zwave.basicV1.basicSet(value: 0x00)),
		secure(zwave.basicV1.basicGet())
	]
}

private secure(physicalgraph.zwave.Command cmd) {
	zwave.securityV1.securityMessageEncapsulation().encapsulate(cmd).format()
}


def ping() {
	secure(zwave.basicV1.basicGet())
}

def healthPoll() {
	log.debug "healthPoll()"
	sendHubCommand(ping())
}