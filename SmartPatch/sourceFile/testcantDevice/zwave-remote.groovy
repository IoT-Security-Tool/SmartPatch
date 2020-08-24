"License"
"AS IS"
metadata {
	definition (name: "Z-Wave Remote", namespace: "smartthings", author: "SmartThings", runLocally: true, minHubCoreVersion: '000.017.0012', executeCommandsLocally: false) {

		fingerprint deviceId: "0x01"
	}

	simulator {

	}

	tiles {
		standardTile("state", "device.state", width: 2, height: 2) {
			state "connected", label: "", icon: "st.unknown.zwave.remote-controller", backgroundColor: "#ffffff"
		}

		main "state"
		details "state"
	}
}

def installed() {
	if (zwaveInfo.cc?.contains("84")) {
		response(zwave.wakeUpV1.wakeUpNoMoreInformation())
	}
}

def parse(String description) {
	def result = null
	def cmd = zwave.parse(description)
	if (cmd) {
		result = zwaveEvent(cmd)
	}
	return result
}

def zwaveEvent(physicalgraph.zwave.commands.wakeupv1.WakeUpNotification cmd) {
	def result = []
	result << createEvent(descriptionText: "${device.displayName} woke up", isStateChange: true)
	result << response(zwave.wakeUpV1.wakeUpNoMoreInformation())
	result
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	
	log.debug "$device.displayName unhandled $cmd"
}
