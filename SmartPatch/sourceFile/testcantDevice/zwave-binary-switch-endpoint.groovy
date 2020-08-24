"License"
"AS IS"
metadata {
	definition(name: "Z-Wave Binary Switch Endpoint", namespace: "smartthings", author: "SmartThings", mnmn: "SmartThings", vid: "generic-switch") {
		capability "Actuator"
		capability "Health Check"
		capability "Refresh"
		capability "Sensor"
		capability "Switch"
	}

	simulator {
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
	configure()
}

def updated() {
	configure()
}

def configure() {
	
	sendEvent(name: "checkInterval", value: 30 * 60 + 2 * 60, displayed: false, data: [protocol: "zwave", hubHardwareId: parent.hubID, offlinePingable: "1"])
	refresh()
}

def handleZWave(physicalgraph.zwave.commands.basicv1.BasicReport cmd) {
	switchEvents(cmd)
}

def handleZWave(physicalgraph.zwave.commands.basicv1.BasicSet cmd) {
	switchEvents(cmd)
}

def handleZWave(physicalgraph.zwave.commands.switchbinaryv1.SwitchBinaryReport cmd) {
	switchEvents(cmd)
}

def switchEvents(physicalgraph.zwave.Command cmd) {
	def value = (cmd.value ? "on" : "off")
	sendEvent(name: "switch", value: value, descriptionText: "$device.displayName was turned $value")
}

def handleZWave(physicalgraph.zwave.Command cmd) {
	sendEvent(descriptionText: "$device.displayName: $cmd", isStateChange: true, displayed: false)
}

def on() {
	
	parent.sendCommand(device, [zwave.switchBinaryV1.switchBinarySet(switchValue: 0xFF),
								zwave.switchBinaryV1.switchBinaryGet()])
}

def off() {
	
	parent.sendCommand(device, [zwave.switchBinaryV1.switchBinarySet(switchValue: 0x00),
								zwave.switchBinaryV1.switchBinaryGet()])
}


def ping() {
	refresh()
}

def refresh() {
	parent.sendCommand(device, zwave.switchBinaryV1.switchBinaryGet())
}
