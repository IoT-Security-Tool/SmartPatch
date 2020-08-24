"License"
"AS IS"
metadata {
	definition (name: "Z-Wave Lock", namespace: "smartthings", author: "SmartThings", runLocally: true, minHubCoreVersion: '000.017.0012', executeCommandsLocally: false, genericHandler: "Z-Wave") {
		capability "Actuator"
		capability "Lock"
		capability "Polling"
		capability "Refresh"
		capability "Sensor"
		capability "Lock Codes"
		capability "Battery"
		capability "Health Check"
		capability "Configuration"

		
		fingerprint inClusters: "0x62, 0x63"
		fingerprint deviceId: "0x4003", inClusters: "0x98"
		fingerprint deviceId: "0x4004", inClusters: "0x98"
		
		fingerprint mfr:"0090", prod:"0001", model:"0236", deviceJoinName: "KwikSet SmartCode 910 Deadbolt Door Lock"
		fingerprint mfr:"0090", prod:"0003", model:"0238", deviceJoinName: "KwikSet SmartCode 910 Deadbolt Door Lock"
		fingerprint mfr:"0090", prod:"0001", model:"0001", deviceJoinName: "KwikSet SmartCode 910 Contemporary Deadbolt Door Lock"
		fingerprint mfr:"0090", prod:"0003", model:"0339", deviceJoinName: "KwikSet SmartCode 912 Lever Door Lock"
		fingerprint mfr:"0090", prod:"0003", model:"4006", deviceJoinName: "KwikSet SmartCode 914 Deadbolt Door Lock" 
		fingerprint mfr:"0090", prod:"0003", model:"0440", deviceJoinName: "KwikSet SmartCode 914 Deadbolt Door Lock"
		fingerprint mfr:"0090", prod:"0001", model:"0642", deviceJoinName: "KwikSet SmartCode 916 Touchscreen Deadbolt Door Lock"
		fingerprint mfr:"0090", prod:"0003", model:"0642", deviceJoinName: "KwikSet SmartCode 916 Touchscreen Deadbolt Door Lock"
		
		fingerprint mfr:"0090", prod:"0003", model:"0541", deviceJoinName: "KwikSet SmartCode 888 Touchpad Deadbolt Door Lock"
		
		fingerprint mfr:"0090", prod:"0003", model:"0742", deviceJoinName: "Kwikset Obsidian Lock"
		
		fingerprint mfr:"003B", prod:"6341", model:"0544", deviceJoinName: "Schlage Touchscreen Deadbolt Door Lock"
		fingerprint mfr:"003B", prod:"6341", model:"5044", deviceJoinName: "Schlage Touchscreen Deadbolt Door Lock"
		fingerprint mfr:"003B", prod:"634B", model:"504C", deviceJoinName: "Schlage Connected Keypad Lever Door Lock"
		fingerprint mfr:"003B", prod:"0001", model:"0468", deviceJoinName: "Schlage Connect Smart Deadbolt Door Lock" 
		fingerprint mfr:"003B", prod:"0001", model:"0469", deviceJoinName: "Schlage Connect Smart Deadbolt Door Lock" 
		
		fingerprint mfr:"0129", prod:"0002", model:"0800", deviceJoinName: "Yale Touchscreen Deadbolt Door Lock" 
		fingerprint mfr:"0129", prod:"0002", model:"0000", deviceJoinName: "Yale Touchscreen Deadbolt Door Lock" 
		fingerprint mfr:"0129", prod:"0002", model:"FFFF", deviceJoinName: "Yale Touchscreen Lever Door Lock" 
		fingerprint mfr:"0129", prod:"0004", model:"0800", deviceJoinName: "Yale Push Button Deadbolt Door Lock" 
		fingerprint mfr:"0129", prod:"0004", model:"0000", deviceJoinName: "Yale Push Button Deadbolt Door Lock" 
		fingerprint mfr:"0129", prod:"0001", model:"0000", deviceJoinName: "Yale Push Button Lever Door Lock" 
		fingerprint mfr:"0129", prod:"8002", model:"0600", deviceJoinName: "Yale Assure Lock" 
		fingerprint mfr:"0129", prod:"0007", model:"0001", deviceJoinName: "Yale Keyless Connected Smart Door Lock"
		fingerprint mfr:"0129", prod:"8004", model:"0600", deviceJoinName: "Yale Assure Lock Push Button Deadbolt" 
		fingerprint mfr:"0129", prod:"6600", model:"0002", deviceJoinName: "Yale Conexis Lock"
		fingerprint mfr:"0129", prod:"0001", model:"0409", deviceJoinName: "Yale Touchscreen Lever Door Lock" 
		fingerprint mfr:"0129", prod:"800B", model:"0F00", deviceJoinName: "Yale Assure Keypad Lever Door Lock" 
		fingerprint mfr:"0129", prod:"800C", model:"0F00", deviceJoinName: "Yale Assure Touchscreen Lever Door Lock" 
		fingerprint mfr:"0129", prod:"8002", model:"1000", deviceJoinName: "Yale Assure Lock" 
		fingerprint mfr:"0129", prod:"803A", model:"0508", deviceJoinName: "Yale Touchscreen Deadbolt with Integrated ZWave Plus" 
		
		fingerprint mfr:"022E", prod:"0001", model:"0001", deviceJoinName: "Samsung Digital Lock", mnmn: "SmartThings", vid: "SmartThings-smartthings-Samsung_Smart_Doorlock" 
		
		fingerprint mfr:"037B", prod:"0002", model:"0001", deviceJoinName: "KeyWe Lock" 
		fingerprint mfr:"037B", prod:"0003", model:"0001", deviceJoinName: "KeyWe Smart Rim Lock" 
		
		fingerprint mfr:"0366", prod:"0001", model:"0001", deviceJoinName: "Philia Smart Door Lock" 
	}

	simulator {
		status "locked": "command: 9881, payload: 00 62 03 FF 00 00 FE FE"
		status "unlocked": "command: 9881, payload: 00 62 03 00 00 00 FE FE"

		reply "9881006201FF,delay 4200,9881006202": "command: 9881, payload: 00 62 03 FF 00 00 FE FE"
		reply "988100620100,delay 4200,9881006202": "command: 9881, payload: 00 62 03 00 00 00 FE FE"
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"toggle", type: "generic", width: 6, height: 4){
			tileAttribute ("device.lock", key: "PRIMARY_CONTROL") {
				attributeState "locked", label:'locked', action:"lock.unlock", icon:"st.locks.lock.locked", backgroundColor:"#00A0DC", nextState:"unlocking"
				attributeState "unlocked", label:'unlocked', action:"lock.lock", icon:"st.locks.lock.unlocked", backgroundColor:"#ffffff", nextState:"locking"
				attributeState "unlocked with timeout", label:'unlocked', action:"lock.lock", icon:"st.locks.lock.unlocked", backgroundColor:"#ffffff", nextState:"locking"
				attributeState "unknown", label:"unknown", action:"lock.lock", icon:"st.locks.lock.unknown", backgroundColor:"#ffffff", nextState:"locking"
				attributeState "locking", label:'locking', icon:"st.locks.lock.locked", backgroundColor:"#00A0DC"
				attributeState "unlocking", label:'unlocking', icon:"st.locks.lock.unlocked", backgroundColor:"#ffffff"
			}
		}
		standardTile("lock", "device.lock", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:'lock', action:"lock.lock", icon:"st.locks.lock.locked", nextState:"locking"
		}
		standardTile("unlock", "device.lock", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:'unlock', action:"lock.unlock", icon:"st.locks.lock.unlocked", nextState:"unlocking"
		}
		valueTile("battery", "device.battery", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "battery", label:'${currentValue}% battery', unit:""
		}
		standardTile("refresh", "device.lock", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}

		main "toggle"
		details(["toggle", "lock", "unlock", "battery", "refresh"])
	}
}

import physicalgraph.zwave.commands.doorlockv1.*
import physicalgraph.zwave.commands.usercodev1.*


def installed() {
	
	sendEvent(name: "checkInterval", value: 1 * 60 * 60, displayed: false, data: [protocol: "zwave", hubHardwareId: device.hub.hardwareID, offlinePingable: "1"])

	if (isSamsungLock()) { 
		sendEvent(name: "lock", value: "unlocked", isStateChange: true, displayed: true)
	}

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
		if (!device.currentState("lock") || !device.currentState("battery") || !state.configured) {
			log.debug "Returning commands for lock operation get and battery get"
			if (!state.configured) {
				cmds << doConfigure()
			}
			cmds << refresh()
			cmds << reloadAllCodes()
			if (!state.MSR) {
				cmds << zwave.manufacturerSpecificV1.manufacturerSpecificGet().format()
			}
			if (!state.fw) {
				cmds << zwave.versionV1.versionGet().format()
			}
			hubAction = response(delayBetween(cmds, 30*1000))
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
	cmds << secure(zwave.batteryV1.batteryGet())
	if (isSchlageLock()) {
		cmds << secure(zwave.configurationV2.configurationGet(parameterNumber: getSchlageLockParam().codeLength.number))
	}
	cmds = delayBetween(cmds, 30*1000)

	state.lastLockDetailsQuery = now()

	log.debug "Do configure returning with commands := $cmds"
	cmds
}


def parse(String description) {
	log.trace "[DTH] Executing 'parse(String description)' for device ${device.displayName} with description = $description"

	def result = null
	if (description.startsWith("Err")) {
		if (state.sec) {
			result = createEvent(descriptionText:description, isStateChange:true, displayed:false)
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
		def cmd = zwave.parse(description, [ 0x98: 1, 0x62: 1, 0x63: 1, 0x71: 2, 0x72: 2, 0x80: 1, 0x85: 2, 0x86: 1 ])
		if (cmd) {
			result = zwaveEvent(cmd)
		}
	}
	log.info "[DTH] parse() - returning result=$result"
	result
}


def zwaveEvent(physicalgraph.zwave.commands.configurationv2.ConfigurationReport cmd) {
	log.trace "[DTH] Executing 'zwaveEvent(physicalgraph.zwave.commands.configurationv2.ConfigurationReport cmd)' with cmd = $cmd"
	if (isSchlageLock() && cmd.parameterNumber == getSchlageLockParam().codeLength.number) {
		def result = []
		def length = cmd.scaledConfigurationValue
		def deviceName = device.displayName
		log.trace "[DTH] Executing 'ConfigurationReport' for device $deviceName with code length := $length"
		def codeLength = device.currentValue("codeLength")
		if (codeLength && codeLength != length) {
			log.trace "[DTH] Executing 'ConfigurationReport' for device $deviceName - all codes deleted"
			result = allCodesDeletedEvent()
			result << createEvent(name: "codeChanged", value: "all deleted", descriptionText: "Deleted all user codes",
					isStateChange: true, data: [lockName: deviceName, notify: true,
												notificationText: "Deleted all user codes in $deviceName at ${location.name}"])
			result << createEvent(name: "lockCodes", value: util.toJson([:]), displayed: false, descriptionText: "'lockCodes' attribute updated")
		}
		result << createEvent(name:"codeLength", value: length, descriptionText: "Code length is $length", displayed: false)
		return result
	}
	return null
}


def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation cmd) {
	log.trace "[DTH] Executing 'zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation)' with cmd = $cmd"
	def encapsulatedCommand = cmd.encapsulatedCommand([0x62: 1, 0x71: 2, 0x80: 1, 0x85: 2, 0x63: 1, 0x98: 1, 0x86: 1])
	if (encapsulatedCommand) {
		zwaveEvent(encapsulatedCommand)
	}
}


def zwaveEvent(physicalgraph.zwave.commands.securityv1.NetworkKeyVerify cmd) {
	log.trace "[DTH] Executing 'zwaveEvent(physicalgraph.zwave.commands.securityv1.NetworkKeyVerify)' with cmd = $cmd"
	createEvent(name:"secureInclusion", value:"success", descriptionText:"Secure inclusion was successful", isStateChange: true)
}


def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityCommandsSupportedReport cmd) {
	log.trace "[DTH] Executing 'zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityCommandsSupportedReport)' with cmd = $cmd"
	state.sec = cmd.commandClassSupport.collect { String.format("%02X ", it) }.join()
	if (cmd.commandClassControl) {
		state.secCon = cmd.commandClassControl.collect { String.format("%02X ", it) }.join()
	}
	createEvent(name:"secureInclusion", value:"success", descriptionText:"Lock is securely included", isStateChange: true)
}


def zwaveEvent(DoorLockOperationReport cmd) {
	log.trace "[DTH] Executing 'zwaveEvent(DoorLockOperationReport)' with cmd = $cmd"
	def result = []

	unschedule("followupStateCheck")
	unschedule("stateCheck")

	
	def map = [ name: "lock" ]
	map.data = [ lockName: device.displayName ]
	if (isKeyweLock()) {
		map.value = cmd.doorCondition >> 1 ? "unlocked" : "locked"
		map.descriptionText = cmd.doorCondition >> 1 ? "Unlocked" : "Locked"
	} else if (cmd.doorLockMode == 0xFF) {
		map.value = "locked"
		map.descriptionText = "Locked"
	} else if (cmd.doorLockMode >= 0x40) {
		map.value = "unknown"
		map.descriptionText = "Unknown state"
	} else if (cmd.doorLockMode == 0x01) {
		map.value = "unlocked with timeout"
		map.descriptionText = "Unlocked with timeout"
	}  else {
		map.value = "unlocked"
		map.descriptionText = "Unlocked"
		if (state.assoc != zwaveHubNodeId) {
			result << response(secure(zwave.associationV1.associationSet(groupingIdentifier:1, nodeId:zwaveHubNodeId)))
			result << response(zwave.associationV1.associationSet(groupingIdentifier:2, nodeId:zwaveHubNodeId))
			result << response(secure(zwave.associationV1.associationGet(groupingIdentifier:1)))
		}
	}
	if (generatesDoorLockOperationReportBeforeAlarmReport()) {
		
		runIn(3, "delayLockEvent", [data: [map: map]])
		return [:]
	} else {
		return result ? [createEvent(map), *result] : createEvent(map)
	}
}

def delayLockEvent(data) {
	log.debug "Sending cached lock operation: $data.map"
	sendEvent(data.map)
}


def zwaveEvent(physicalgraph.zwave.commands.alarmv2.AlarmReport cmd) {
	log.trace "[DTH] Executing 'zwaveEvent(physicalgraph.zwave.commands.alarmv2.AlarmReport)' with cmd = $cmd"
	def result = []

	if (cmd.zwaveAlarmType == 6) {
		result = handleAccessAlarmReport(cmd)
	} else if (cmd.zwaveAlarmType == 7) {
		result = handleBurglarAlarmReport(cmd)
	} else if(cmd.zwaveAlarmType == 8) {
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
	def codeID, changeType, lockCodes, codeName
	def deviceName = device.displayName
	lockCodes = loadLockCodes()
	if (1 <= cmd.zwaveAlarmEvent && cmd.zwaveAlarmEvent < 10) {
		map = [ name: "lock", value: (cmd.zwaveAlarmEvent & 1) ? "locked" : "unlocked" ]
	}
	switch(cmd.zwaveAlarmEvent) {
		case 1: 
			map.descriptionText = "Locked manually"
			map.data = [ method: (cmd.alarmLevel == 2) ? "keypad" : "manual" ]
			break
		case 2: 
			map.descriptionText = "Unlocked manually"
			map.data = [ method: "manual" ]
			break
		case 3: 
			map.descriptionText = "Locked"
			map.data = [ method: "command" ]
			break
		case 4: 
			map.descriptionText = "Unlocked"
			map.data = [ method: "command" ]
			break
		case 5: 
			if (cmd.eventParameter || cmd.alarmLevel) {
				codeID = readCodeSlotId(cmd)
				codeName = getCodeName(lockCodes, codeID)
				map.descriptionText = "Locked by \"$codeName\""
				map.data = [ codeId: codeID as String, usedCode: codeID, codeName: codeName, method: "keypad" ]
			} else {
				
				map.descriptionText = "Locked manually"
				map.data = [ method: "keypad" ]
			}
			break
		case 6: 
			if (cmd.eventParameter || cmd.alarmLevel) {
				codeID = readCodeSlotId(cmd)
				codeName = getCodeName(lockCodes, codeID)
				map.descriptionText = "Unlocked by \"$codeName\""
				map.data = [ codeId: codeID as String, usedCode: codeID, codeName: codeName, method: "keypad" ]
			}
			break
		case 7:
			map = [ name: "lock", value: "unknown", descriptionText: "Unknown state" ]
			map.data = [ method: "manual" ]
			break
		case 8:
			map = [ name: "lock", value: "unknown", descriptionText: "Unknown state" ]
			map.data = [ method: "command" ]
			break
		case 9: 
			map = [ name: "lock", value: "locked", data: [ method: "auto" ] ]
			map.descriptionText = "Auto locked"
			break
		case 0xA:
			map = [ name: "lock", value: "unknown", descriptionText: "Unknown state" ]
			map.data = [ method: "auto" ]
			break
		case 0xB:
			map = [ name: "lock", value: "unknown", descriptionText: "Unknown state" ]
			break
		case 0xC: 
			result = allCodesDeletedEvent()
			map = [ name: "codeChanged", value: "all deleted", descriptionText: "Deleted all user codes", isStateChange: true ]
			map.data = [notify: true, notificationText: "Deleted all user codes in $deviceName at ${location.name}"]
			result << createEvent(name: "lockCodes", value: util.toJson([:]), displayed: false, descriptionText: "'lockCodes' attribute updated")
			break
		case 0xD: 
			if (cmd.eventParameter || cmd.alarmLevel) {
				codeID = readCodeSlotId(cmd)
				if (lockCodes[codeID.toString()]) {
					codeName = getCodeName(lockCodes, codeID)
					map = [ name: "codeChanged", value: "$codeID deleted", isStateChange: true ]
					map.descriptionText = "Deleted \"$codeName\""
					map.data = [ codeName: codeName, notify: true, notificationText: "Deleted \"$codeName\" in $deviceName at ${location.name}" ]
					result << codeDeletedEvent(lockCodes, codeID)
				}
			}
			break
		case 0xE: 
			if (cmd.eventParameter || cmd.alarmLevel) {
				codeID = readCodeSlotId(cmd)
				if(codeID == 0 && isKwiksetLock()) {
					
					
					log.trace "Ignoring this alarm report in case of Kwikset locks"
					break
				}
				codeName = getCodeNameFromState(lockCodes, codeID)
				changeType = getChangeType(lockCodes, codeID)
				map = [ name: "codeChanged", value: "$codeID $changeType",  descriptionText: "${getStatusForDescription(changeType)} \"$codeName\"", isStateChange: true ]
				map.data = [ codeName: codeName, notify: true, notificationText: "${getStatusForDescription(changeType)} \"$codeName\" in $deviceName at ${location.name}" ]
				if(!isMasterCode(codeID)) {
					result << codeSetEvent(lockCodes, codeID, codeName)
				} else {
					map.descriptionText = "${getStatusForDescription('set')} \"$codeName\""
					map.data.notificationText = "${getStatusForDescription('set')} \"$codeName\" in $deviceName at ${location.name}"
				}
			}
			break
		case 0xF: 
			if (cmd.eventParameter || cmd.alarmLevel) {
				codeID = readCodeSlotId(cmd)
				clearStateForSlot(codeID)
				map = [ name: "codeChanged", value: "$codeID failed", descriptionText: "User code is duplicate and not added",
						isStateChange: true, data: [isCodeDuplicate: true] ]
			}
			break
		case 0x10: 
		case 0x13:
			map = [ name: "tamper", value: "detected", descriptionText: "Keypad attempts exceed code entry limit", isStateChange: true, displayed: true ]
			break
		case 0x11: 
			map = [ descriptionText: "Keypad is busy" ]
			break
		case 0x12: 
			codeName = getCodeNameFromState(lockCodes, 0)
			map = [ name: "codeChanged", value: "0 set", descriptionText: "${getStatusForDescription('set')} \"$codeName\"", isStateChange: true ]
			map.data = [ codeName: codeName, notify: true, notificationText: "${getStatusForDescription('set')} \"$codeName\" in $deviceName at ${location.name}" ]
			break
		case 0x18: 
			map = [ name: "lock", value: "unlocked", data: [ method: "manual" ] ]
			map.descriptionText = "Unlocked manually"
			break
		case 0x19: 
			map = [ name: "lock", value: "locked", data: [ method: "manual" ] ]
			map.descriptionText = "Locked manually"
			break
		case 0xFE:
			
			return handleAlarmReportUsingAlarmType(cmd)
		default:
			
			return handleAlarmReportUsingAlarmType(cmd)
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


private def handleBurglarAlarmReport(cmd) {
	log.trace "[DTH] Executing 'handleBurglarAlarmReport' with cmd = $cmd"
	def result = []
	def deviceName = device.displayName

	def map = [ name: "tamper", value: "detected" ]
	map.data = [ lockName: deviceName ]
	switch (cmd.zwaveAlarmEvent) {
		case 0:
			map.value = "clear"
			map.descriptionText = "Tamper alert cleared"
			break
		case 1:
		case 2:
			map.descriptionText = "Intrusion attempt detected"
			break
		case 3:
			map.descriptionText = "Covering removed"
			break
		case 4:
			map.descriptionText = "Invalid code"
			break
		default:
			
			return handleAlarmReportUsingAlarmType(cmd)
	}

	result << createEvent(map)
	result
}


private def handleBatteryAlarmReport(cmd) {
	log.trace "[DTH] Executing 'handleBatteryAlarmReport' with cmd = $cmd"
	def result = []
	def deviceName = device.displayName
	def map = null
	switch(cmd.zwaveAlarmEvent) {
		case 0x01: 
			log.debug "Batteries replaced. Queueing a battery get."
			runIn(10, "queryBattery", [overwrite: true, forceForLocallyExecuting: true])
			result << response(secure(zwave.batteryV1.batteryGet()))
			break;
		case 0x0A:
			map = [ name: "battery", value: 1, descriptionText: "Battery level critical", displayed: true, data: [ lockName: deviceName ] ]
			break
		case 0x0B:
			map = [ name: "battery", value: 0, descriptionText: "Battery too low to operate lock", isStateChange: true, displayed: true, data: [ lockName: deviceName ] ]
			break
		default:
			
			return handleAlarmReportUsingAlarmType(cmd)
	}
	result << createEvent(map)
	result
}


private def handleAlarmReportUsingAlarmType(cmd) {
	log.trace "[DTH] Executing 'handleAlarmReportUsingAlarmType' with cmd = $cmd"
	def result = []
	def map = null
	def codeID, lockCodes, codeName
	def deviceName = device.displayName
	lockCodes = loadLockCodes()
	switch(cmd.alarmType) {
		case 9:
		case 17:
			map = [ name: "lock", value: "unknown", descriptionText: "Unknown state" ]
			break
		case 16: 
		case 19: 
			map = [ name: "lock", value: "unlocked" ]
			if (cmd.alarmLevel != null) {
				codeID = readCodeSlotId(cmd)
				codeName = getCodeName(lockCodes, codeID)
				map.isStateChange = true 
				map.descriptionText = "Unlocked by \"$codeName\""
				map.data = [ codeId: codeID as String, usedCode: codeID, codeName: codeName, method: "keypad" ]
			}
			break
		case 18: 
			codeID = readCodeSlotId(cmd)
			map = [ name: "lock", value: "locked" ]
			
			if (isKwiksetLock() && codeID == 0) {
				map.descriptionText = "Locked manually"
				map.data = [ method: "manual" ]
			} else {
				codeName = getCodeName(lockCodes, codeID)
				map.descriptionText = "Locked by \"$codeName\""
				map.data = [ codeId: codeID as String, usedCode: codeID, codeName: codeName, method: "keypad" ]
			}
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
		case 32: 
			result = allCodesDeletedEvent()
			map = [ name: "codeChanged", value: "all deleted", descriptionText: "Deleted all user codes", isStateChange: true ]
			map.data = [notify: true, notificationText: "Deleted all user codes in $deviceName at ${location.name}"]
			result << createEvent(name: "lockCodes", value: util.toJson([:]), displayed: false, descriptionText: "'lockCodes' attribute updated")
			break
		case 33: 
			codeID = readCodeSlotId(cmd)
			if (lockCodes[codeID.toString()]) {
				codeName = getCodeName(lockCodes, codeID)
				map = [ name: "codeChanged", value: "$codeID deleted", isStateChange: true ]
				map.descriptionText = "Deleted \"$codeName\""
				map.data = [ codeName: codeName, notify: true, notificationText: "Deleted \"$codeName\" in $deviceName at ${location.name}" ]
				result << codeDeletedEvent(lockCodes, codeID)
			}
			break
		case 38: 
			map = [ descriptionText: "A Non Access Code was entered at the lock", isStateChange: true ]
			break
		case 13:
		case 112: 
			codeID = readCodeSlotId(cmd)
			if(codeID == 0 && isKwiksetLock()) {
				
				
				log.trace "Ignoring this alarm report in case of Kwikset locks"
				break
			}
			codeName = getCodeNameFromState(lockCodes, codeID)
			def changeType = getChangeType(lockCodes, codeID)
			map = [ name: "codeChanged", value: "$codeID $changeType", descriptionText:
					"${getStatusForDescription(changeType)} \"$codeName\"", isStateChange: true ]
			map.data = [ codeName: codeName, notify: true, notificationText: "${getStatusForDescription(changeType)} \"$codeName\" in $deviceName at ${location.name}" ]
			if(!isMasterCode(codeID)) {
				result << codeSetEvent(lockCodes, codeID, codeName)
			} else {
				map.descriptionText = "${getStatusForDescription('set')} \"$codeName\""
				map.data.notificationText = "${getStatusForDescription('set')} \"$codeName\" in $deviceName at ${location.name}"
			}
			break
		case 34:
		case 113: 
			codeID = readCodeSlotId(cmd)
			clearStateForSlot(codeID)
			map = [ name: "codeChanged", value: "$codeID failed", descriptionText: "User code is duplicate and not added",
					isStateChange: true, data: [isCodeDuplicate: true] ]
			break
		case 130:  
			map = [ descriptionText: "Batteries replaced", isStateChange: true ]
			log.debug "Batteries replaced. Queueing a battery check."
			runIn(10, "queryBattery", [overwrite: true, forceForLocallyExecuting: true])
			result << response(secure(zwave.batteryV1.batteryGet()))
			break
		case 131: 
			map = [ descriptionText: "Code ${cmd.alarmLevel} is disabled", isStateChange: false ]
			break
		case 161: 
			if (cmd.alarmLevel == 2) {
				map = [ name: "tamper", value: "detected", descriptionText: "Front escutcheon removed", isStateChange: true ]
			} else {
				map = [ name: "tamper", value: "detected", descriptionText: "Keypad attempts exceed code entry limit", isStateChange: true, displayed: true ]
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


def zwaveEvent(UserCodeReport cmd) {
	log.trace "[DTH] Executing 'zwaveEvent(UserCodeReport)' with userIdentifier: ${cmd.userIdentifier} and status: ${cmd.userIdStatus}"
	def result = []
	
	def codeID = cmd.userIdentifier.toString()
	def lockCodes = loadLockCodes()
	def map = [ name: "codeChanged", isStateChange: true ]
	def deviceName = device.displayName
	def userIdStatus = cmd.userIdStatus

	if (userIdStatus == UserCodeReport.USER_ID_STATUS_OCCUPIED ||
			(userIdStatus == UserCodeReport.USER_ID_STATUS_STATUS_NOT_AVAILABLE && cmd.user)) {

		def codeName

		
		
		if ((!cmd.code || state["setname$codeID"]) && isSchlageLock()) {
			
			
			log.trace "[DTH] User code creation successful for Schlage lock"
			codeName = getCodeNameFromState(lockCodes, codeID)
			def changeType = getChangeType(lockCodes, codeID)

			map.value = "$codeID $changeType"
			map.isStateChange = true
			map.descriptionText = "${getStatusForDescription(changeType)} \"$codeName\""
			map.data = [ codeName: codeName, lockName: deviceName, notify: true, notificationText: "${getStatusForDescription(changeType)} \"$codeName\" in $deviceName at ${location.name}" ]
			if(!isMasterCode(codeID)) {
				result << codeSetEvent(lockCodes, codeID, codeName)
			} else {
				map.descriptionText = "${getStatusForDescription('set')} \"$codeName\""
				map.data.notificationText = "${getStatusForDescription('set')} \"$codeName\" in $deviceName at ${location.name}"
				map.data.lockName = deviceName
			}
		} else {
			
			codeName = getCodeName(lockCodes, codeID)
			def changeType = getChangeType(lockCodes, codeID)
			if (!lockCodes[codeID]) {
				result << codeSetEvent(lockCodes, codeID, codeName)
			} else {
				map.displayed = false
			}
			map.value = "$codeID $changeType"
			map.descriptionText = "${getStatusForDescription(changeType)} \"$codeName\""
			map.data = [ codeName: codeName, lockName: deviceName ]
		}
	} else if(userIdStatus == 254 && isSchlageLock()) {
		
		
		
		map = [ name: "codeChanged", value: "$codeID failed", descriptionText: "User code is not added", isStateChange: true,
				data: [ lockName: deviceName, isCodeDuplicate: true] ]
	} else {
		
		if (codeID == "0" && userIdStatus == UserCodeReport.USER_ID_STATUS_AVAILABLE_NOT_SET && isSchlageLock()) {
			
			log.trace "[DTH] All user codes deleted for Schlage lock"
			result << allCodesDeletedEvent()
			map = [ name: "codeChanged", value: "all deleted", descriptionText: "Deleted all user codes", isStateChange: true,
					data: [ lockName: deviceName, notify: true,
							notificationText: "Deleted all user codes in $deviceName at ${location.name}"] ]
			lockCodes = [:]
			result << lockCodesEvent(lockCodes)
		} else {
			
			if (lockCodes[codeID]) {
				def codeName = getCodeName(lockCodes, codeID)
				map.value = "$codeID deleted"
				map.descriptionText = "Deleted \"$codeName\""
				map.data = [ codeName: codeName, lockName: deviceName, notify: true, notificationText: "Deleted \"$codeName\" in $deviceName at ${location.name}" ]
				result << codeDeletedEvent(lockCodes, codeID)
			} else {
				map.value = "$codeID unset"
				map.displayed = false
				map.data = [ lockName: deviceName ]
			}
		}
	}

	clearStateForSlot(codeID)
	result << createEvent(map)

	if (codeID.toInteger() == state.checkCode) {  
		if (state.checkCode + 1 > state.codes || state.checkCode >= 8) {
			state.remove("checkCode")  
			state["checkCode"] = null
			sendEvent(name: "scanCodes", value: "Complete", descriptionText: "Code scan completed", displayed: false)
		} else {
			state.checkCode = state.checkCode + 1  
			result << response(requestCode(state.checkCode))
		}
	}
	if (codeID.toInteger() == state.pollCode) {
		if (state.pollCode + 1 > state.codes || state.pollCode >= 15) {
			state.remove("pollCode")  
			state["pollCode"] = null
		} else {
			state.pollCode = state.pollCode + 1
		}
	}

	result = result.flatten()
	result
}


def zwaveEvent(UsersNumberReport cmd) {
	log.trace "[DTH] Executing 'zwaveEvent(UsersNumberReport)' with cmd = $cmd"
	def result = [createEvent(name: "maxCodes", value: cmd.supportedUsers, displayed: false)]
	state.codes = cmd.supportedUsers
	if (state.checkCode) {
		if (state.checkCode <= cmd.supportedUsers) {
			result << response(requestCode(state.checkCode))
		} else {
			state.remove("checkCode")
			state["checkCode"] = null
		}
	}
	result
}


def zwaveEvent(physicalgraph.zwave.commands.associationv2.AssociationReport cmd) {
	log.trace "[DTH] Executing 'zwaveEvent(physicalgraph.zwave.commands.associationv2.AssociationReport)' with cmd = $cmd"
	def result = []
	if (cmd.nodeId.any { it == zwaveHubNodeId }) {
		state.remove("associationQuery")
		state["associationQuery"] = null
		result << createEvent(descriptionText: "Is associated")
		state.assoc = zwaveHubNodeId
		if (cmd.groupingIdentifier == 2) {
			result << response(zwave.associationV1.associationRemove(groupingIdentifier:1, nodeId:zwaveHubNodeId))
		}
	} else if (cmd.groupingIdentifier == 1) {
		result << response(secure(zwave.associationV1.associationSet(groupingIdentifier:1, nodeId:zwaveHubNodeId)))
	} else if (cmd.groupingIdentifier == 2) {
		result << response(zwave.associationV1.associationSet(groupingIdentifier:2, nodeId:zwaveHubNodeId))
	}
	result
}


def zwaveEvent(physicalgraph.zwave.commands.timev1.TimeGet cmd) {
	log.trace "[DTH] Executing 'zwaveEvent(physicalgraph.zwave.commands.timev1.TimeGet)' with cmd = $cmd"
	def result = []
	def now = new Date().toCalendar()
	if(location.timeZone) now.timeZone = location.timeZone
	result << createEvent(descriptionText: "Requested time update", displayed: false)
	result << response(secure(zwave.timeV1.timeReport(
			hourLocalTime: now.get(Calendar.HOUR_OF_DAY),
			minuteLocalTime: now.get(Calendar.MINUTE),
			secondLocalTime: now.get(Calendar.SECOND)))
	)
	result
}


def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd) {
	log.trace "[DTH] Executing 'zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet)' with cmd = $cmd"
	
	def result = [ createEvent(name: "lock", value: cmd.value ? "unlocked" : "locked") ]
	def cmds = [
			zwave.associationV1.associationRemove(groupingIdentifier:1, nodeId:zwaveHubNodeId).format(),
			"delay 1200",
			zwave.associationV1.associationGet(groupingIdentifier:2).format()
	]
	[result, response(cmds)]
}


def zwaveEvent(physicalgraph.zwave.commands.batteryv1.BatteryReport cmd) {
	log.trace "[DTH] Executing 'zwaveEvent(physicalgraph.zwave.commands.batteryv1.BatteryReport)' with cmd = $cmd"
	def map = [ name: "battery", unit: "%" ]
	if (cmd.batteryLevel == 0xFF) {
		map.value = 1
		map.descriptionText = "Has a low battery"
	} else {
		map.value = cmd.batteryLevel
		map.descriptionText = "Battery is at ${cmd.batteryLevel}%"
	}
	state.lastbatt = now()
	unschedule("queryBattery")
	createEvent(map)
}


def zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv2.ManufacturerSpecificReport cmd) {
	log.trace "[DTH] Executing 'zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv2.ManufacturerSpecificReport)' with cmd = $cmd"
	def result = []
	def msr = String.format("%04X-%04X-%04X", cmd.manufacturerId, cmd.productTypeId, cmd.productId)
	updateDataValue("MSR", msr)
	result << createEvent(descriptionText: "MSR: $msr", isStateChange: false)
	result
}


def zwaveEvent(physicalgraph.zwave.commands.versionv1.VersionReport cmd) {
	log.trace "[DTH] Executing 'zwaveEvent(physicalgraph.zwave.commands.versionv1.VersionReport)' with cmd = $cmd"
	def fw = "${cmd.applicationVersion}.${cmd.applicationSubVersion}"
	updateDataValue("fw", fw)
	if (getDataValue("MSR") == "003B-6341-5044") {
		updateDataValue("ver", "${cmd.applicationVersion >> 4}.${cmd.applicationVersion & 0xF}")
	}
	def text = "${device.displayName}: firmware version: $fw, Z-Wave version: ${cmd.zWaveProtocolVersion}.${cmd.zWaveProtocolSubVersion}"
	createEvent(descriptionText: text, isStateChange: false)
}


def zwaveEvent(physicalgraph.zwave.commands.applicationstatusv1.ApplicationBusy cmd) {
	log.trace "[DTH] Executing 'zwaveEvent(physicalgraph.zwave.commands.applicationstatusv1.ApplicationBusy)' with cmd = $cmd"
	def msg = cmd.status == 0 ? "try again later" :
			cmd.status == 1 ? "try again in ${cmd.waitTime} seconds" :
					cmd.status == 2 ? "request queued" : "sorry"
	createEvent(displayed: true, descriptionText: "Is busy, $msg")
}


def zwaveEvent(physicalgraph.zwave.commands.applicationstatusv1.ApplicationRejectedRequest cmd) {
	log.trace "[DTH] Executing 'zwaveEvent(physicalgraph.zwave.commands.applicationstatusv1.ApplicationRejectedRequest)' with cmd = $cmd"
	createEvent(displayed: true, descriptionText: "Rejected the last request")
}


def zwaveEvent(physicalgraph.zwave.Command cmd) {
	log.trace "[DTH] Executing 'zwaveEvent(physicalgraph.zwave.Command)' with cmd = $cmd"
	createEvent(displayed: false, descriptionText: "$cmd")
}


def lockAndCheck(doorLockMode) {
	secureSequence([
			zwave.doorLockV1.doorLockOperationSet(doorLockMode: doorLockMode),
			zwave.doorLockV1.doorLockOperationGet()
	], 4200)
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
	log.trace "[DTH] Executing unlockWithTimeout() for device ${device.displayName}"
	lockAndCheck(DoorLockOperationSet.DOOR_LOCK_MODE_DOOR_UNSECURED_WITH_TIMEOUT)
}


def ping() {
	log.trace "[DTH] Executing ping() for device ${device.displayName}"
	runIn(30, "followupStateCheck")
	secure(zwave.doorLockV1.doorLockOperationGet())
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

	def cmds = secureSequence([zwave.doorLockV1.doorLockOperationGet(), zwave.batteryV1.batteryGet()])
	if (!state.associationQuery) {
		cmds << "delay 4200"
		cmds << zwave.associationV1.associationGet(groupingIdentifier:2).format()  
		cmds << secure(zwave.associationV1.associationGet(groupingIdentifier:1))
		state.associationQuery = now()
	} else if (now() - state.associationQuery.toLong() > 9000) {
		cmds << "delay 6000"
		cmds << zwave.associationV1.associationSet(groupingIdentifier:2, nodeId:zwaveHubNodeId).format()
		cmds << secure(zwave.associationV1.associationSet(groupingIdentifier:1, nodeId:zwaveHubNodeId))
		cmds << zwave.associationV1.associationGet(groupingIdentifier:2).format()
		cmds << secure(zwave.associationV1.associationGet(groupingIdentifier:1))
		state.associationQuery = now()
	}
	state.lastLockDetailsQuery = now()

	cmds
}


def poll() {
	log.trace "[DTH] Executing poll() for device ${device.displayName}"
	def cmds = []
	
	def latest = device.currentState("lock")?.date?.time
	if (!latest || !secondsPast(latest, 6 * 60) || secondsPast(state.lastPoll, 55 * 60)) {
		cmds << secure(zwave.doorLockV1.doorLockOperationGet())
		state.lastPoll = now()
	} else if (!state.lastbatt || now() - state.lastbatt > 53*60*60*1000) {
		cmds << secure(zwave.batteryV1.batteryGet())
		state.lastbatt = now()  
	}
	if (state.assoc != zwaveHubNodeId && secondsPast(state.associationQuery, 19 * 60)) {
		cmds << zwave.associationV1.associationSet(groupingIdentifier:2, nodeId:zwaveHubNodeId).format()
		cmds << secure(zwave.associationV1.associationSet(groupingIdentifier:1, nodeId:zwaveHubNodeId))
		cmds << zwave.associationV1.associationGet(groupingIdentifier:2).format()
		cmds << "delay 6000"
		cmds << secure(zwave.associationV1.associationGet(groupingIdentifier:1))
		cmds << "delay 6000"
		state.associationQuery = now()
	} else {
		
		if (secondsPast(state.lastPoll, 55 * 60)) {
			cmds << secure(zwave.doorLockV1.doorLockOperationGet())
			state.lastPoll = now()
		} else if (!state.MSR) {
			cmds << zwave.manufacturerSpecificV1.manufacturerSpecificGet().format()
		} else if (!state.fw) {
			cmds << zwave.versionV1.versionGet().format()
		} else if (!device.currentValue("maxCodes")) {
			state.pollCode = 1
			cmds << secure(zwave.userCodeV1.usersNumberGet())
		} else if (state.pollCode && state.pollCode <= state.codes) {
			cmds << requestCode(state.pollCode)
		} else if (!state.lastbatt || now() - state.lastbatt > 53*60*60*1000) {
			cmds << secure(zwave.batteryV1.batteryGet())
		}
	}

	if (cmds) {
		log.debug "poll is sending ${cmds.inspect()}"
		cmds
	} else {
		
		sendEvent(descriptionText: "skipping poll", isStateChange: true, displayed: false)
		null
	}
}


def requestCode(codeID) {
	secure(zwave.userCodeV1.userCodeGet(userIdentifier: codeID))
}


def reloadAllCodes() {
	log.trace "[DTH] Executing 'reloadAllCodes()' by ${device.displayName}"
	sendEvent(name: "scanCodes", value: "Scanning", descriptionText: "Code scan in progress", displayed: false)
	def lockCodes = loadLockCodes()
	sendEvent(lockCodesEvent(lockCodes))
	state.checkCode = state.checkCode ?: 1

	def cmds = []
	
	if(!device.currentValue("codeLength") && isSchlageLock()) {
		cmds << secure(zwave.configurationV2.configurationGet(parameterNumber: getSchlageLockParam().codeLength.number))
	}
	if (!state.codes) {
		
		cmds << secure(zwave.userCodeV1.usersNumberGet())
	} else {
		sendEvent(name: "maxCodes", value: state.codes, displayed: false)
		cmds << requestCode(state.checkCode)
	}
	if(cmds.size() > 1) {
		cmds = delayBetween(cmds, 4200)
	}
	cmds
}


def setCodeLength(length) {
	if (isSchlageLock()) {
		length = length.toInteger()
		if (length >= 4 && length <= 8) {
			log.trace "[DTH] Executing 'setCodeLength()' by ${device.displayName}"
			def val = []
			val << length
			def param = getSchlageLockParam()
			return secure(zwave.configurationV2.configurationSet(parameterNumber: param.codeLength.number, size: param.codeLength.size, configurationValue: val))
		}
	}
	return null
}


def setCode(codeID, code, codeName = null) {
	if (!code) {
		log.trace "[DTH] Executing 'nameSlot()' by ${this.device.displayName}"
		nameSlot(codeID, codeName)
		return
	}

	log.trace "[DTH] Executing 'setCode()' by ${this.device.displayName}"
	def strcode = code
	if (code instanceof String) {
		code = code.toList().findResults { if(it > ' ' && it != ',' && it != '-') it.toCharacter() as Short }
	} else {
		strcode = code.collect{ it as Character }.join()
	}

	def strname = (codeName ?: "Code $codeID")
	state["setname$codeID"] = strname

	def cmds = validateAttributes()
	cmds << secure(zwave.userCodeV1.userCodeSet(userIdentifier:codeID, userIdStatus:1, user:code))
	if(cmds.size() > 1) {
		cmds = delayBetween(cmds, 4200)
	}
	cmds
}


def validateAttributes() {
	def cmds = []
	if(!device.currentValue("maxCodes")) {
		cmds << secure(zwave.userCodeV1.usersNumberGet())
	}
	if(!device.currentValue("codeLength") && isSchlageLock()) {
		cmds << secure(zwave.configurationV2.configurationGet(parameterNumber: getSchlageLockParam().codeLength.number))
	}
	log.trace "validateAttributes returning commands list: " + cmds
	cmds
}


def updateCodes(codeSettings) {
	log.trace "[DTH] Executing updateCodes() for device ${device.displayName}"
	if(codeSettings instanceof String) codeSettings = util.parseJson(codeSettings)
	def set_cmds = []
	codeSettings.each { name, updated ->
		if (name.startsWith("code")) {
			def n = name[4..-1].toInteger()
			if (updated && updated.size() >= 4 && updated.size() <= 8) {
				log.debug "Setting code number $n"
				set_cmds << secure(zwave.userCodeV1.userCodeSet(userIdentifier:n, userIdStatus:1, user:updated))
			} else if (updated == null || updated == "" || updated == "0") {
				log.debug "Deleting code number $n"
				set_cmds << deleteCode(n)
			}
		} else log.warn("unexpected entry $name: $updated")
	}
	if (set_cmds) {
		return response(delayBetween(set_cmds, 2200))
	}
	return null
}


void nameSlot(codeSlot, codeName) {
	codeSlot = codeSlot.toString()
	if (!isCodeSet(codeSlot)) {
		return
	}
	def deviceName = device.displayName
	log.trace "[DTH] - Executing nameSlot() for device $deviceName"
	def lockCodes = loadLockCodes()
	def oldCodeName = getCodeName(lockCodes, codeSlot)
	def newCodeName = codeName ?: "Code $codeSlot"
	lockCodes[codeSlot] = newCodeName
	sendEvent(lockCodesEvent(lockCodes))
	sendEvent(name: "codeChanged", value: "$codeSlot renamed", data: [ lockName: deviceName, notify: false, notificationText: "Renamed \"$oldCodeName\" to \"$newCodeName\" in $deviceName at ${location.name}" ],
			descriptionText: "Renamed \"$oldCodeName\" to \"$newCodeName\"", displayed: true, isStateChange: true)
}


def deleteCode(codeID) {
	log.trace "[DTH] Executing 'deleteCode()' by ${this.device.displayName}"
	
	
	secureSequence([
			zwave.userCodeV1.userCodeSet(userIdentifier:codeID, userIdStatus:0),
			zwave.userCodeV1.userCodeGet(userIdentifier:codeID)
	], 4200)
}


private secure(physicalgraph.zwave.Command cmd) {
	zwave.securityV1.securityMessageEncapsulation().encapsulate(cmd).format()
}


private secureSequence(commands, delay=4200) {
	delayBetween(commands.collect{ secure(it) }, delay)
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


private String getCodeName(lockCodes, codeID) {
	if (isMasterCode(codeID)) {
		return "Master Code"
	}
	lockCodes[codeID.toString()] ?: "Code $codeID"
}


private String getCodeNameFromState(lockCodes, codeID) {
	if (isMasterCode(codeID)) {
		return "Master Code"
	}
	def nameFromLockCodes = lockCodes[codeID.toString()]
	def nameFromState = state["setname$codeID"]
	if(nameFromLockCodes) {
		if(nameFromState) {
			
			return nameFromState
		} else {
			
			return nameFromLockCodes
		}
	} else if(nameFromState) {
		
		return nameFromState
	}
	
	return "Code $codeID"
}


private Boolean isCodeSet(codeID) {
	
	def lockCodes = loadLockCodes()
	lockCodes[codeID.toString()] ? true : false
}


private Map loadLockCodes() {
	parseJson(device.currentValue("lockCodes") ?: "{}") ?: [:]
}


private Map lockCodesEvent(lockCodes) {
	createEvent(name: "lockCodes", value: util.toJson(lockCodes), displayed: false,
			descriptionText: "'lockCodes' attribute updated")
}


private boolean isMasterCode(codeID) {
	if(codeID instanceof String) {
		codeID = codeID.toInteger()
	}
	(codeID == 0) ? true : false
}


private def codeSetEvent(lockCodes, codeID, codeName) {
	clearStateForSlot(codeID)
	
	lockCodes[codeID.toString()] = (codeName ?: "Code $codeID")
	def result = []
	result << lockCodesEvent(lockCodes)
	def codeReportMap = [ name: "codeReport", value: codeID, data: [ code: "" ], isStateChange: true, displayed: false ]
	codeReportMap.descriptionText = "${device.displayName} code $codeID is set"
	result << createEvent(codeReportMap)
	result
}


private def codeDeletedEvent(lockCodes, codeID) {
	lockCodes.remove("$codeID".toString())
	
	clearStateForSlot(codeID)
	def result = []
	result << lockCodesEvent(lockCodes)
	def codeReportMap = [ name: "codeReport", value: codeID, data: [ code: "" ], isStateChange: true, displayed: false ]
	codeReportMap.descriptionText = "${device.displayName} code $codeID was deleted"
	result << createEvent(codeReportMap)
	result
}


private def allCodesDeletedEvent() {
	def result = []
	def lockCodes = loadLockCodes()
	def deviceName = device.displayName
	lockCodes.each { id, code ->
		result << createEvent(name: "codeReport", value: id, data: [ code: "" ], descriptionText: "code $id was deleted",
				displayed: false, isStateChange: true)

		def codeName = code
		result << createEvent(name: "codeChanged", value: "$id deleted", data: [ codeName: codeName, lockName: deviceName,
																				 notify: true, notificationText: "Deleted \"$codeName\" in $deviceName at ${location.name}" ],
				descriptionText: "Deleted \"$codeName\"",
				displayed: true, isStateChange: true)
		clearStateForSlot(id)
	}
	result
}

"set""update"
private def getChangeType(lockCodes, codeID) {
	def changeType = "set"
	if (lockCodes[codeID.toString()]) {
		changeType = "changed"
	}
	changeType
}

"set""changed""Added""set""Updated""changed"""
private def getStatusForDescription(changeType) {
	if("set" == changeType) {
		return "Added"
	} else if("changed" == changeType) {
		return "Updated"
	}
	
	return ""
}


def clearStateForSlot(codeID) {
	state.remove("setname$codeID")
	state["setname$codeID"] = null
}


def getSchlageLockParam() {
	def map = [
			codeLength: [ number: 16, size: 1]
	]
	map
}


def isSchlageLock() {
	if ("003B" == zwaveInfo.mfr) {
		if("Schlage" != getDataValue("manufacturer")) {
			updateDataValue("manufacturer", "Schlage")
		}
		return true
	}
	return false
}


def isKwiksetLock() {
	if ("0090" == zwaveInfo.mfr) {
		if("Kwikset" != getDataValue("manufacturer")) {
			updateDataValue("manufacturer", "Kwikset")
		}
		return true
	}
	return false
}


def isYaleLock() {
	if ("0129" == zwaveInfo.mfr) {
		if("Yale" != getDataValue("manufacturer")) {
			updateDataValue("manufacturer", "Yale")
		}
		return true
	}
	return false
}


private isSamsungLock() {
	if ("022E" == zwaveInfo.mfr) {
		if ("Samsung" != getDataValue("manufacturer")) {
			updateDataValue("manufacturer", "Samsung")
		}
		return true
	}
	return false
}


private isKeyweLock() {
	if ("037B" == zwaveInfo.mfr) {
		if ("Keywe" != getDataValue("manufacturer")) {
			updateDataValue("manufacturer", "Keywe")
		}
		return true
	}
	return false
}


def generatesDoorLockOperationReportBeforeAlarmReport() {
	
	if(isYaleLock() &&
			(("0007" == zwaveInfo.prod && "0001" == zwaveInfo.model) ||
					("6600" == zwaveInfo.prod && "0002" == zwaveInfo.model) )) {
		
		return true
	}
	return false
}


def readCodeSlotId(physicalgraph.zwave.commands.alarmv2.AlarmReport cmd) {
	if(cmd.numberOfEventParameters == 1) {
		return cmd.eventParameter[0]
	} else if(cmd.numberOfEventParameters >= 3) {
		return cmd.eventParameter[2]
	}
	return cmd.alarmLevel
}

private queryBattery() {
	log.debug "Running queryBattery"
	if (!state.lastbatt || now() - state.lastbatt > 10*1000) {
		log.debug "It's been more than 10s since battery was updated after a replacement. Querying battery."
		runIn(10, "queryBattery", [overwrite: true, forceForLocallyExecuting: true])
		sendHubCommand(secure(zwave.batteryV1.batteryGet()))
	}
}
