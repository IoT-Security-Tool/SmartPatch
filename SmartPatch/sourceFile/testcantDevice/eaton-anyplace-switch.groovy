"License"
"AS IS"

metadata {
	definition(name: "Eaton Anyplace Switch", namespace: "smartthings", author: "SmartThings") {
		capability "Actuator"
		capability "Sensor"
		capability "Switch"

		
		fingerprint mfr: "001A", prod: "4243", model: "0000", deviceJoinName: "Eaton Switch" 
	}

	tiles {
		multiAttributeTile(name: "switch", type: "generic", width: 6, height: 4, canChangeIcon: true) {
			tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#00A0DC", nextState: "off"
				attributeState "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState: "on"
			}
		}

		main "switch"
		details(["switch"])
	}
}

def installed() {
	
	sendEvent(name: "switch", value: "off")
}

def parse(String description) {
	def result = []
	def cmd = zwave.parse(description)
	if (cmd) {
		result += zwaveEvent(cmd)
	}
	return result
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicGet cmd) {
	def currentValue = device.currentState("switch").value.equals("on") ? 255 : 0
	response zwave.basicV1.basicReport(value: currentValue).format()
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd) {
	createEvent(name: "switch", value: cmd.value ? "on" : "off")
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	[:]
}

def on() {
	sendEvent(name: "switch", value: "on")
}

def off() {
	sendEvent(name: "switch", value: "off")
}
