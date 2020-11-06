"License"
"AS IS"
metadata {
	definition(name: "Z-Wave Water Valve", namespace: "smartthings", author: "SmartThings", ocfDeviceType: "oic.d.watervalve", runLocally: true, executeCommandsLocally: true, minHubCoreVersion: "000.022.0004") {
		capability "Actuator"
		capability "Health Check"
		capability "Valve"
		capability "Polling"
		capability "Refresh"
		capability "Sensor"

		fingerprint deviceId: "0x1006", inClusters: "0x25"
		fingerprint mfr: "0173", prod: "0003", model: "0002", deviceJoinName: "Leak Intelligence Leak Gopher Water Shutoff Valve"
		fingerprint mfr: "021F", prod: "0003", model: "0002", deviceJoinName: "Dome Water Main Shut-off"
		fingerprint mfr: "0157", prod: "0003", model: "0002", deviceJoinName: "EcoNet Bulldog Valve Robot"
		fingerprint mfr: "0152", prod: "0003", model: "0512", deviceJoinName: "POPP Secure Flow Stop"
	}

	
	simulator {
		status "open": "command: 2503, payload: FF"
		status "close": "command: 2503, payload: 00"

		
		reply "2001FF,delay 100,2502": "command: 2503, payload: FF"
		reply "200100,delay 100,2502": "command: 2503, payload: 00"
	}

	
	tiles(scale: 2) {
		multiAttributeTile(name: "valve", type: "generic", width: 6, height: 4, canChangeIcon: true) {
			tileAttribute("device.valve", key: "PRIMARY_CONTROL") {
				attributeState "open", label: '${name}', action: "valve.close", icon: "st.valves.water.open", backgroundColor: "#00A0DC", nextState: "closing"
				attributeState "closed", label: '${name}', action: "valve.open", icon: "st.valves.water.closed", backgroundColor: "#ffffff", nextState: "opening"
				attributeState "opening", label: '${name}', action: "valve.close", icon: "st.valves.water.open", backgroundColor: "#00A0DC"
				attributeState "closing", label: '${name}', action: "valve.open", icon: "st.valves.water.closed", backgroundColor: "#ffffff"
			}
		}

		standardTile("refresh", "device.valve", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state "default", label: '', action: "refresh.refresh", icon: "st.secondary.refresh"
		}

		main "valve"
		details(["valve", "refresh"])
	}

}

def installed() {
	
	sendEvent(name: "checkInterval", value: 2 * 15 * 60 + 2 * 60, displayed: false, data: [protocol: "zwave", hubHardwareId: device.hub.hardwareID, offlinePingable: "1"])
	response(refresh())
}

def updated() {
	
	sendEvent(name: "checkInterval", value: 2 * 15 * 60 + 2 * 60, displayed: false, data: [protocol: "zwave", hubHardwareId: device.hub.hardwareID, offlinePingable: "1"])
	response(refresh())
}

def parse(String description) {
	log.trace "parse description : $description"
	def cmd = zwave.parse(description, [0x20: 1])
	if (cmd) {
		return zwaveEvent(cmd)
	}
	log.debug "Could not parse message"
	return null
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd) {
	def value = cmd.value == 0xFF ? "open" : cmd.value == 0x00 ? "closed" : "unknown"

	return [createEventWithDebug([name: "contact", value: value, descriptionText: "$device.displayName valve is $value"]),
			createEventWithDebug([name: "valve", value: value, descriptionText: "$device.displayName valve is $value"])]
}

def zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv2.ManufacturerSpecificReport cmd) {
	
	log.debug "manufacturerId:   ${cmd.manufacturerId}"
	log.debug "manufacturerName: ${cmd.manufacturerName}"
	log.debug "productId:        ${cmd.productId}"
	log.debug "productTypeId:    ${cmd.productTypeId}"
	def msr = String.format("%04X-%04X-%04X", cmd.manufacturerId, cmd.productTypeId, cmd.productId)
	updateDataValue("MSR", msr)
	return createEventWithDebug([descriptionText: "$device.displayName MSR: $msr", isStateChange: false])
}

def zwaveEvent(physicalgraph.zwave.commands.deviceresetlocallyv1.DeviceResetLocallyNotification cmd) {
	return createEventWithDebug([descriptionText: cmd.toString(), isStateChange: true, displayed: true])
}

def zwaveEvent(physicalgraph.zwave.commands.switchbinaryv1.SwitchBinaryReport cmd) {
	def value = cmd.value == 0xFF ? "open" : cmd.value == 0x00 ? "closed" : "unknown"

	return [createEventWithDebug([name: "contact", value: value, descriptionText: "$device.displayName valve is $value"]),
			createEventWithDebug([name: "valve", value: value, descriptionText: "$device.displayName valve is $value"])]
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	return createEvent([:]) 
}

def open() {
	delayBetween([
		zwave.basicV1.basicSet(value: 0xFF).format(),
		zwave.switchBinaryV1.switchBinaryGet().format()
	], 10000) 
}

def close() {
	delayBetween([
		zwave.basicV1.basicSet(value: 0x00).format(),
		zwave.switchBinaryV1.switchBinaryGet().format()
	], 10000) 
}

def poll() {
	zwave.switchBinaryV1.switchBinaryGet().format()
}


def ping() {
	refresh()
}

def refresh() {
	log.debug "refresh() is called"
	def commands = [zwave.switchBinaryV1.switchBinaryGet().format()]
	if (getDataValue("MSR") == null) {
		commands << zwave.manufacturerSpecificV1.manufacturerSpecificGet().format()
	}
	delayBetween(commands, 100)
}

def createEventWithDebug(eventMap) {
	def event = createEvent(eventMap)
	log.debug "Event created with ${event?.descriptionText}"
	return event
}
