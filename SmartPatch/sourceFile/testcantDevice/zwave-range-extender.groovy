"License"
"AS IS"
metadata {
	definition (name: "Z-Wave Range Extender", namespace: "smartthings", author: "SmartThings", ocfDeviceType: "oic.d.networking") {
		capability "Health Check"

		fingerprint mfr: "0086", prod: "0104", model: "0075", deviceJoinName: "Aeotec Range Extender 6" 
		fingerprint mfr: "0086", prod: "0204", model: "0075", deviceJoinName: "Aeotec Range Extender 6" 
		fingerprint mfr: "0086", prod: "0004", model: "0075", deviceJoinName: "Aeotec Range Extender 6" 
		fingerprint mfr: "0246", prod: "0001", model: "0001", deviceJoinName: "Iris Z-Wave Range Extender (Smart Plug)"
		fingerprint mfr: "021F", prod: "0003", model: "0108", deviceJoinName: "Dome Range Extender DMEX1" 
		fingerprint mfr: "0371", prod: "0104", model: "00BD", deviceJoinName: "Aeotec Range Extender 7" 
		fingerprint mfr: "0371", prod: "0004", model: "00BD", deviceJoinName: "Aeotec Range Extender 7" 
	}

	tiles(scale: 2) {
		multiAttributeTile(name: "status", type: "generic", width: 6, height: 4) {
			tileAttribute("device.status", key: "PRIMARY_CONTROL") {
				attributeState "online", label: 'online', icon: "st.motion.motion.active", backgroundColor: "#00A0DC"
			}
		}
		main "status"
		details(["status"])
	}
}

def installed() {
	runEvery5Minutes(ping)
	sendEvent(name: "checkInterval", value: 1930, displayed: false, data: [protocol: "zwave", hubHardwareId: device.hub.hardwareID])
}

def parse(String description) {
	def cmd = zwave.parse(description)
	log.debug "Parse returned ${cmd}"
	cmd ? zwaveEvent(cmd) : null
}

def zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv2.ManufacturerSpecificReport cmd) {
	createEvent(name: "status", displayed: true, value: 'online', descriptionText: "$device.displayName is online")
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	createEvent(descriptionText: "$device.displayName: $cmd", displayed: false)
}

def ping() {
	sendHubCommand(new physicalgraph.device.HubAction(zwave.manufacturerSpecificV2.manufacturerSpecificGet().format()))
}
