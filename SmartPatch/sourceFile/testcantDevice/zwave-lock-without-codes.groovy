"License"
"AS IS"
metadata {
	definition(name: "Z-Wave Lock Without Codes", namespace: "smartthings", author: "SmartThings", runLocally: true, minHubCoreVersion: '000.017.0012', executeCommandsLocally: false, mnmn: "SmartThings", vid: "generic-lock-2") {
		capability "Actuator"
		capability "Lock"
		capability "Refresh"
		capability "Sensor"
		capability "Battery"
		capability "Health Check"
		capability "Configuration"

		fingerprint inClusters: "0x62"
		fingerprint mfr: "010E", prod: "0009", model: "0001", deviceJoinName: "Danalock V3 Smart Lock"
		fingerprint mfr: "0090", prod: "0003", model: "0446", deviceJoinName: "Kwikset Convert Deadbolt Door Lock" 
		fingerprint mfr: "033F", prod: "0001", model: "0001", deviceJoinName: "August Smart Lock Pro"
		fingerprint mfr: "021D", prod: "0003", model: "0001", deviceJoinName: "Alfred Smart Home Touchscreen Deadbolt" 
	}

	simulator {
	}

	tiles(scale: 2) {
		multiAttributeTile(name: "toggle", type: "generic", width: 6, height: 4) {
			tileAttribute("device.lock", key: "PRIMARY_CONTROL") {
				attributeState "locked", label: 'locked', action: "lock.unlock", icon: "st.locks.lock.locked", backgroundColor: "#00A0DC", nextState: "unlocking"
				attributeState "unlocked", label: 'unlocked', action: "lock.lock", icon: "st.locks.lock.unlocked", backgroundColor: "#ffffff", nextState: "locking"
				attributeState "unlocked with timeout", label: 'unlocked', action: "lock.lock", icon: "st.locks.lock.unlocked", backgroundColor: "#ffffff", nextState: "locking"
				attributeState "unknown", label: "unknown", action: "lock.lock", icon: "st.locks.lock.unknown", backgroundColor: "#ffffff", nextState: "locking"
				attributeState "locking", label: 'locking', icon: "st.locks.lock.locked", backgroundColor: "#00A0DC"
				attributeState "unlocking", label: 'unlocking', icon: "st.locks.lock.unlocked", backgroundColor: "#ffffff"
			}
		}
		standardTile("lock", "device.lock", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label: 'lock', action: "lock.lock", icon: "st.locks.lock.locked", nextState: "locking"
		}
		standardTile("unlock", "device.lock", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label: 'unlock', action: "lock.unlock", icon: "st.locks.lock.unlocked", nextState: "unlocking"
		}
		valueTile("battery", "device.battery", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "battery", label: '${currentValue}% battery', unit: ""
		}
		standardTile("refresh", "device.lock", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label: '', action: "refresh.refresh", icon: "st.secondary.refresh"
		}

		main "toggle"
		details(["toggle", "lock", "unlock", "battery", "refresh"])
	}
}

import physicalgraph.zwave.commands.doorlockv1.*


private getCommandClassVersions() {
	[
		0x62: 1,  
		0x63: 1,  
		0x71: 2,  
		0x72: 2,  
		0x80: 1,  
		0x85: 2,  
		0x86: 1,  
		0x98: 1   
	]
}


def installed() {
	
	sendEvent(name: "checkInterval", value: 1 * 60 * 60, displayed: false, data: [protocol: "zwave", hubHardwareId: device.hub.hardwareID, offlinePingable: "1"])
	scheduleInstalledCheck()
}


def scheduleInstalledCheck() {
	runIn(120, "installedCheck", [forceForLocallyExecuting: true])
}

def installedCheck() {
	if (device.currentState("lock") && device.currentState("battery")) {
		unschedule("installedCheck")
	} else {
		
		if (!state.lastLockDetailsQuery || secondsPast(state.lastLockDetailsQuery, 2 * 60)) {
			def actions = updated()

			if (actions) {
				sendHubCommand(actions.toHubAction())
			}
		}

		scheduleInstalledCheck()
	}
}


def uninstalled() {
	def deviceName = device.displayName
	log.trace "[DTH] Executing 'uninstalled()' for device $deviceName"
	sendEvent(name: "lockRemoved", value: device.id, isStateChange: true, displayed: false)
}


def updated() {
	
	sendEvent(name: "checkInterval", value: 1 * 60 * 60, displayed: false, data: [protocol: "zwave", hubHardwareId: device.hub.hardwareID, offlinePingable: "1"])

	def hubAction = null
	try {
		def cmds = []
		if (!device.currentState("lock") || !state.configured) {
			log.debug "Returning commands for lock operation get and battery get"
			if (!state.configured) {
				cmds << doConfigure()
			}
			cmds << refresh()
			hubAction = response(delayBetween(cmds, 30 * 1000))
		}
	} catch (e) {
		log.warn "updated() threw $e"
	}
	hubAction
}


def configure() {
	log.trace "[DTH] Executing 'configure()' for device ${device.displayName}"
	def cmds = doConfigure()
	log.debug "Configure returning with commands := $cmds"
	cmds
}


def doConfigure() {
	log.trace "[DTH] Executing 'doConfigure()' for device ${device.displayName}"
	state.configured = true
	def cmds = []
	cmds << secure(zwave.doorLockV1.doorLockOperationGet())
	if (zwaveInfo.mfr != "010E") {
		cmds << secure(zwave.batteryV1.batteryGet())
		cmds = delayBetween(cmds, 30 * 1000)
	}

	state.lastLockDetailsQuery = now()

	log.debug "Do configure returning with commands := $cmds"
	cmds
}


def parse(String description) {
	log.trace "[DTH] Executing 'parse(String description)' for device ${device.displayName} with description = $description"

	def result = null
	if (description.startsWith("Err")) {
		if (state.sec) {
			result = createEvent(descriptionText: description, isStateChange: true, displayed: false)
		} else {
			result = createEvent(
				descriptionText: "This lock failed to complete the network security key exchange. If you are unable to control it via SmartThings, you must remove it from your network and add it again.",
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
	log.debug "[DTH] parse() - returning result=$result"
	result
}


def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation cmd) {
	log.trace "[DTH] Executing 'zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation)' with cmd = $cmd"
	def encapsulatedCommand = cmd.encapsulatedCommand(commandClassVersions)
	if (encapsulatedCommand) {
		zwaveEvent(encapsulatedCommand)
	}
}


def zwaveEvent(physicalgraph.zwave.commands.securityv1.NetworkKeyVerify cmd) {
	log.trace "[DTH] Executing 'zwaveEvent(physicalgraph.zwave.commands.securityv1.NetworkKeyVerify)' with cmd = $cmd"
	createEvent(name: "secureInclusion", value: "success", descriptionText: "Secure inclusion was successful", isStateChange: true)
}


def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityCommandsSupportedReport cmd) {
	log.trace "[DTH] Executing 'zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityCommandsSupportedReport)' with cmd = $cmd"
	state.sec = cmd.commandClassSupport.collect { String.format("%02X ", it) }.join()
	if (cmd.commandClassControl) {
		state.secCon = cmd.commandClassControl.collect { String.format("%02X ", it) }.join()
	}
	createEvent(name: "secureInclusion", value: "success", descriptionText: "Lock is securely included", isStateChange: true)
}


def zwaveEvent(DoorLockOperationReport cmd) {
	log.trace "[DTH] Executing 'zwaveEvent(DoorLockOperationReport)' with cmd = $cmd"
	def result = []

	unschedule("followupStateCheck")
	unschedule("stateCheck")

	
	def map = [name: "lock"]
	map.data = [lockName: device.displayName]
	if (cmd.doorLockMode == 0xFF) {
		map.value = "locked"
		map.descriptionText = "Locked"
	} else if (cmd.doorLockMode >= 0x40) {
		map.value = "unknown"
		map.descriptionText = "Unknown state"
	} else if (cmd.doorLockMode == 0x01) {
		map.value = "unlocked with timeout"
		map.descriptionText = "Unlocked with timeout"
	} else {
		map.value = "unlocked"
		map.descriptionText = "Unlocked"
	}
	return result ? [createEvent(map), *result] : createEvent(map)
}


def zwaveEvent(physicalgraph.zwave.commands.alarmv2.AlarmReport cmd) {
	log.trace "[DTH] Executing 'zwaveEvent(physicalgraph.zwave.commands.alarmv2.AlarmReport)' with cmd = $cmd"
	def result = []

	if (cmd.zwaveAlarmType == 6) {
		result = handleAccessAlarmReport(cmd)
	} else if (cmd.zwaveAlarmType == 8) {
		
		result = handleBatteryAlarmReport(cmd)
	} else {
		result = handleAlarmReportUsingAlarmType(cmd)
	}

	result = result ?: null
	log.debug "[DTH] zwaveEvent(physicalgraph.zwave.commands.alarmv2.AlarmReport) returning with result = $result"
	result
}


private def handleAccessAlarmReport(cmd) {
	log.trace "[DTH] Executing 'handleAccessAlarmReport' with cmd = $cmd"
	def result = []
	def map = null
	def codeID, changeType, codeName
	def deviceName = device.displayName
	if (1 <= cmd.zwaveAlarmEvent && cmd.zwaveAlarmEvent < 10) {
		map = [name: "lock", value: (cmd.zwaveAlarmEvent & 1) ? "locked" : "unlocked"]
	}
	switch (cmd.zwaveAlarmEvent) {
		case 1: 
			map.descriptionText = "Locked manually"
			map.data = [method: (cmd.alarmLevel == 2) ? "keypad" : "manual"]
			break
		case 2: 
			map.descriptionText = "Unlocked manually"
			map.data = [method: "manual"]
			break
		case 3: 
			map.descriptionText = "Locked"
			map.data = [method: "command"]
			break
		case 4: 
			map.descriptionText = "Unlocked"
			map.data = [method: "command"]
			break
		case 7:
			map = [name: "lock", value: "unknown", descriptionText: "Unknown state"]
			map.data = [method: "manual"]
			break
		case 8:
			map = [name: "lock", value: "unknown", descriptionText: "Unknown state"]
			map.data = [method: "command"]
			break
		case 9: 
			map = [name: "lock", value: "locked", data: [method: "auto"]]
			map.descriptionText = "Auto locked"
			break
		case 0xA:
			map = [name: "lock", value: "unknown", descriptionText: "Unknown state"]
			map.data = [method: "auto"]
			break
		case 0xB:
			map = [name: "lock", value: "unknown", descriptionText: "Unknown state"]
			break
		case 0x13:
			map = [name: "tamper", value: "detected", descriptionText: "Keypad attempts exceed code entry limit", isStateChange: true, displayed: true]
			break
		default:
			map = [displayed: false, descriptionText: "Alarm event ${cmd.alarmType} level ${cmd.alarmLevel}"]
			break
	}

	if (map) {
		if (map.data) {
			map.data.lockName = deviceName
		} else {
			map.data = [lockName: deviceName]
		}
		result << createEvent(map)
	}
	result = result.flatten()
	result
}


private def handleBatteryAlarmReport(cmd) {
	log.trace "[DTH] Executing 'handleBatteryAlarmReport' with cmd = $cmd"
	def result = []
	def deviceName = device.displayName
	def map = null
	switch (cmd.zwaveAlarmEvent) {
		case 0x01: 
			log.debug "Batteries replaced. Queueing a battery get."
			runIn(10, "queryBattery", [overwrite: true, forceForLocallyExecuting: true])
			result << response(secure(zwave.batteryV1.batteryGet()))
			break;
		case 0x0A:
			map = [name: "battery", value: 1, descriptionText: "Battery level critical", displayed: true, data: [lockName: deviceName]]
			break
		case 0x0B:
			map = [name: "battery", value: 0, descriptionText: "Battery too low to operate lock", isStateChange: true, displayed: true, data: [lockName: deviceName]]
			break
		default:
			map = [displayed: false, descriptionText: "Alarm event ${cmd.alarmType} level ${cmd.alarmLevel}"]
			break
	}
	result << createEvent(map)
	result
}


private def handleAlarmReportUsingAlarmType(cmd) {
	log.trace "[DTH] Executing 'handleAlarmReportUsingAlarmType' with cmd = $cmd"
	def result = []
	def map = null
	def deviceName = device.displayName
	switch(cmd.alarmType) {
		case 9:
		case 17:
			map = [ name: "lock", value: "unknown", descriptionText: "Unknown state" ]
			break
		case 16: 
		case 19: 
			map = [ name: "lock", value: "unlocked" , method: "keypad"]
			map.descriptionText = "Unlocked by keypad"
			break
		case 18: 
			codeID = readCodeSlotId(cmd)
			map = [ name: "lock", value: "locked" ]
			map.descriptionText = "Locked by keypad"
			map.data = [ method: "keypad" ]
			break
		case 21: 
			map = [ name: "lock", value: "locked", data: [ method: (cmd.alarmLevel == 2) ? "keypad" : "manual" ] ]
			map.descriptionText = "Locked manually"
			break
		case 22: 
			map = [ name: "lock", value: "unlocked", data: [ method: "manual" ] ]
			map.descriptionText = "Unlocked manually"
			break
		case 23:
			map = [ name: "lock", value: "unknown", descriptionText: "Unknown state" ]
			map.data = [ method: "command" ]
			break
		case 24: 
			map = [ name: "lock", value: "locked", data: [ method: "command" ] ]
			map.descriptionText = "Locked"
			break
		case 25: 
			map = [ name: "lock", value: "unlocked", data: [ method: "command" ] ]
			map.descriptionText = "Unlocked"
			break
		case 26:
			map = [ name: "lock", value: "unknown", descriptionText: "Unknown state" ]
			map.data = [ method: "auto" ]
			break
		case 27: 
			map = [ name: "lock", value: "locked", data: [ method: "auto" ] ]
			map.descriptionText = "Auto locked"
			break
		case 130:  
			map = [ descriptionText: "Batteries replaced", isStateChange: true ]
			break
		case 161: 
			if (cmd.alarmLevel == 2) {
				map = [ name: "tamper", value: "detected", descriptionText: "Front escutcheon removed", isStateChange: true ]
			}
			break
		case 167: 
			if (!state.lastbatt || now() - state.lastbatt > 12*60*60*1000) {
				map = [ descriptionText: "Battery low", isStateChange: true ]
				result << response(secure(zwave.batteryV1.batteryGet()))
			} else {
				map = [ name: "battery", value: device.currentValue("battery"), descriptionText: "Battery low", isStateChange: true ]
			}
			break
		case 168: 
			map = [ name: "battery", value: 1, descriptionText: "Battery level critical", displayed: true ]
			break
		case 169: 
			map = [ name: "battery", value: 0, descriptionText: "Battery too low to operate lock", isStateChange: true, displayed: true ]
			break
		default:
			map = [ displayed: false, descriptionText: "Alarm event ${cmd.alarmType} level ${cmd.alarmLevel}" ]
			break
	}

	if (map) {
		if (map.data) {
			map.data.lockName = deviceName
		} else {
			map.data = [ lockName: deviceName ]
		}
		result << createEvent(map)
	}
	result = result.flatten()
	result
}


def zwaveEvent(physicalgraph.zwave.commands.batteryv1.BatteryReport cmd) {
	log.trace "[DTH] Executing 'zwaveEvent(physicalgraph.zwave.commands.batteryv1.BatteryReport)' with cmd = $cmd"
	def map = [name: "battery", unit: "%"]
	if (cmd.batteryLevel == 0xFF) {
		map.value = 1
		map.descriptionText = "${device.displayName} has a low battery"
		map.isStateChange = true
	} else {
		map.value = cmd.batteryLevel
	}
	state.lastbatt = now()
	unschedule("queryBattery")
	if (cmd.batteryLevel == 0 && device.latestValue("battery") > 20) {
		
		
		log.warn "Erroneous battery report dropped from ${device.latestValue("battery")} to $map.value. Not reporting"
	} else {
		createEvent(map)
	}

}



def zwaveEvent(physicalgraph.zwave.Command cmd) {
	log.trace "[DTH] Executing 'zwaveEvent(physicalgraph.zwave.Command)' with cmd = $cmd"
	createEvent(displayed: false, descriptionText: "$cmd")
}


def lockAndCheck(doorLockMode) {
	def cmds = []
	cmds << zwave.doorLockV1.doorLockOperationSet(doorLockMode: doorLockMode)
	cmds << zwave.doorLockV1.doorLockOperationGet()
	if (zwaveInfo.mfr == "010E") {
		
		cmds << zwave.batteryV1.batteryGet()
	}
	secureSequence(cmds, 4200)
}


def lock() {
	log.trace "[DTH] Executing lock() for device ${device.displayName}"
	lockAndCheck(DoorLockOperationSet.DOOR_LOCK_MODE_DOOR_SECURED)
}


def unlock() {
	log.trace "[DTH] Executing unlock() for device ${device.displayName}"
	lockAndCheck(DoorLockOperationSet.DOOR_LOCK_MODE_DOOR_UNSECURED)
}


def unlockWithTimeout() {
	if (zwaveInfo.mfr == "010E") {
		
		log.trace "[DTH] Executing unlock() for device ${device.displayName}"
	} else {
		log.trace "[DTH] Executing unlockWithTimeout() for device ${device.displayName}"
		lockAndCheck(DoorLockOperationSet.DOOR_LOCK_MODE_DOOR_UNSECURED_WITH_TIMEOUT)
	}
}


def ping() {
	log.trace "[DTH] Executing ping() for device ${device.displayName}"
	runIn(30, "followupStateCheck")
	if (zwaveInfo.mfr == "010E") {
		secure(zwave.doorLockV1.doorLockOperationGet())
	} else {
		secureSequence([zwave.doorLockV1.doorLockOperationGet(), zwave.batteryV1.batteryGet()])
	}
}


def followupStateCheck() {
	runEvery1Hour(stateCheck)
	stateCheck()
}


def stateCheck() {
	sendHubCommand(new physicalgraph.device.HubAction(secure(zwave.doorLockV1.doorLockOperationGet())))
}


def refresh() {
	log.trace "[DTH] Executing refresh() for device ${device.displayName}"
	if (zwaveInfo.mfr == "010E") {
		secure(zwave.doorLockV1.doorLockOperationGet())
	} else {
		secureSequence([zwave.doorLockV1.doorLockOperationGet(), zwave.batteryV1.batteryGet()])
	}
}


private secure(physicalgraph.zwave.Command cmd) {
	zwave.securityV1.securityMessageEncapsulation().encapsulate(cmd).format()
}


private secureSequence(commands, delay = 4200) {
	delayBetween(commands.collect { secure(it) }, delay)
}


private Boolean secondsPast(timestamp, seconds) {
	if (!(timestamp instanceof Number)) {
		if (timestamp instanceof Date) {
			timestamp = timestamp.time
		} else if ((timestamp instanceof String) && timestamp.isNumber()) {
			timestamp = timestamp.toLong()
		} else {
			return true
		}
	}
	return (now() - timestamp) > (seconds * 1000)
}

private queryBattery() {
	log.debug "Running queryBattery"
	if (!state.lastbatt || now() - state.lastbatt > 10*1000) {
		log.debug "It's been more than 10s since battery was updated after a replacement. Querying battery."
		runIn(10, "queryBattery", [overwrite: true, forceForLocallyExecuting: true])
		sendHubCommand(secure(zwave.batteryV1.batteryGet()))
	}
}

