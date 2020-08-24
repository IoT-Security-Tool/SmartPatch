"License"
"AS IS"
import physicalgraph.zigbee.zcl.DataType

metadata {
	definition (name: "ZigBee Lock", namespace: "smartthings", author: "SmartThings", genericHandler: "Zigbee") {
		capability "Actuator"
		capability "Lock"
		capability "Polling"
		capability "Refresh"
		capability "Sensor"
		capability "Lock Codes"
		capability "Battery"
		capability "Configuration"
		capability "Health Check"

		fingerprint profileId: "0104", inClusters: "0000,0001,0009,000A,0101,0020", outClusters: "000A,0019", manufacturer: "Yale", model: "YRD220/240 TSDB", deviceJoinName: "Yale Touch Screen Deadbolt Lock"
		fingerprint profileId: "0104", inClusters: "0000,0001,0003,0009,000A,0101,0020", outClusters: "000A,0019", manufacturer: "Yale", model: "YRL220 TS LL", deviceJoinName: "Yale Touch Screen Lever Lock"
		fingerprint profileId: "0104", inClusters: "0000,0001,0003,0009,000A,0101,0020", outClusters: "000A,0019", manufacturer: "Yale", model: "YRD210 PB DB", deviceJoinName: "Yale Push Button Deadbolt Lock"
		fingerprint profileId: "0104", inClusters: "0000,0001,0003,0009,000A,0101,0020", outClusters: "000A,0019", manufacturer: "Yale", model: "YRD220/240 TSDB", deviceJoinName: "Yale Touch Screen Deadbolt Lock"
		fingerprint profileId: "0104", inClusters: "0000,0001,0003,0009,000A,0101,0020", outClusters: "000A,0019", manufacturer: "Yale", model: "YRL210 PB LL", deviceJoinName: "Yale Push Button Lever Lock"
		fingerprint profileId: "0104", inClusters: "0000,0001,0003,0009,000A,0101,0020", outClusters: "000A,0019", manufacturer: "Yale", model: "YRD226/246 TSDB", deviceJoinName: "Yale Assure Lock"
		fingerprint profileId: "0104", inClusters: "0000,0001,0003,0009,000A,0101,0020,0B05", outClusters: "000A,0019", manufacturer: "Yale", model: "YRD226 TSDB", deviceJoinName: "Yale Assure Lock"
		fingerprint profileId: "0104", inClusters: "0000,0001,0003,0009,000A,0101,0020,0B05", outClusters: "000A,0019", manufacturer: "Yale", model: "YRD446 BLE TSDB", deviceJoinName: "Yale Assure Lock"
		fingerprint profileId: "0104", inClusters: "0000,0001,0003,0009,000A,0101,0020", outClusters: "000A,0019", manufacturer: "Yale", model: "YRD216 PBDB", deviceJoinName: "Yale Push Button Deadbolt Lock"
		fingerprint profileId: "0104", inClusters: "0000,0001,0003,0009,000A,0101,0020", outClusters: "000A,0019", manufacturer: "Yale", model: "YRL226 TSLL", deviceJoinName: "Yale Assure Touch Screen Lever Lock"
		fingerprint profileId: "0104", inClusters: "0000,0001,0003,0009,000A,0101,0020,0B05", outClusters: "000A,0019", manufacturer: "Yale", model: "YRL216 PB", deviceJoinName: "Yale Assure Keypad Lever Lock"
		fingerprint profileId: "0104", inClusters: "0000,0001,0003,0004,0005,0009,0020,0101,0402,0B05,FDBD", outClusters: "000A,0019", manufacturer: "Kwikset", model: "SMARTCODE_DEADBOLT_5", deviceJoinName: "Kwikset 5-Button Deadbolt"
		fingerprint profileId: "0104", inClusters: "0000,0001,0003,0004,0005,0009,0020,0101,0402,0B05,FDBD", outClusters: "000A,0019", manufacturer: "Kwikset", model: "SMARTCODE_LEVER_5", deviceJoinName: "Kwikset 5-Button Lever"
		fingerprint profileId: "0104", inClusters: "0000,0001,0003,0004,0005,0009,0020,0101,0402,0B05,FDBD", outClusters: "000A,0019", manufacturer: "Kwikset", model: "SMARTCODE_DEADBOLT_10", deviceJoinName: "Kwikset 10-Button Deadbolt"
		fingerprint profileId: "0104", inClusters: "0000,0001,0003,0004,0005,0009,0020,0101,0402,0B05,FDBD", outClusters: "000A,0019", manufacturer: "Kwikset", model: "SMARTCODE_DEADBOLT_10T", deviceJoinName: "Kwikset 10-Button Touch Deadbolt"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0101", manufacturer:"Kwikset", model:"Smartcode", deviceJoinName: "Kwikset Smartcode Lock"
		fingerprint profileId: "0104", inClusters: "0000, 0001, 0003, 0009, 0020, 0101, 0B05, FC00", outClusters: "000A, 0019", manufacturer: "Schlage", model: "BE468", deviceJoinName: "Schlage Connect Smart Deadbolt"
		fingerprint profileId: "0104", inClusters: "0000,0001,0003,0009,000A,0101,0020,0B05", outClusters: "000A,0019", manufacturer: "Yale", model: "YDD-D4F0 TSDB", deviceJoinName: "Lockwood Smart Lock"
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"toggle", type:"generic", width:6, height:4) {
			tileAttribute ("device.lock", key:"PRIMARY_CONTROL") {
				attributeState "locked", label:'locked', action:"lock.unlock", icon:"st.locks.lock.locked", backgroundColor:"#00A0DC", nextState:"unlocking"
				attributeState "unlocked", label:'unlocked', action:"lock.lock", icon:"st.locks.lock.unlocked", backgroundColor:"#ffffff", nextState:"locking"
				attributeState "unknown", label:"unknown", action:"lock.lock", icon:"st.locks.lock.unknown", backgroundColor:"#ffffff", nextState:"locking"
				attributeState "locking", label:'locking', icon:"st.locks.lock.locked", backgroundColor:"#00A0DC"
				attributeState "unlocking", label:'unlocking', icon:"st.locks.lock.unlocked", backgroundColor:"#ffffff"
			}
		}
		standardTile("lock", "device.lock", inactiveLabel:false, decoration:"flat", width:2, height:2) {
			state "default", label:'lock', action:"lock.lock", icon:"st.locks.lock.locked", nextState:"locking"
		}
		standardTile("unlock", "device.lock", inactiveLabel:false, decoration:"flat", width:2, height:2) {
			state "default", label:'unlock', action:"lock.unlock", icon:"st.locks.lock.unlocked", nextState:"unlocking"
		}
		valueTile("battery", "device.battery", inactiveLabel:false, decoration:"flat", width:2, height:2) {
			state "battery", label:'${currentValue}% battery', unit:""
		}
		standardTile("refresh", "device.refresh", inactiveLabel:false, decoration:"flat", width:2, height:2) {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}

		main "toggle"
		details(["toggle", "lock", "unlock", "battery", "refresh"])
	}
}


private getCLUSTER_POWER() { 0x0001 }
private getCLUSTER_DOORLOCK() { 0x0101 }
private getCLUSTER_ALARM() { 0x0009 }


private getDOORLOCK_CMD_LOCK_DOOR() { 0x00 }
private getDOORLOCK_CMD_UNLOCK_DOOR() { 0x01 }
private getDOORLOCK_CMD_USER_CODE_SET() { 0x05 }
private getDOORLOCK_CMD_USER_CODE_GET() { 0x06 }
private getDOORLOCK_CMD_CLEAR_USER_CODE() { 0x07 }
private getDOORLOCK_RESPONSE_OPERATION_EVENT() { 0x20 }
private getDOORLOCK_RESPONSE_PROGRAMMING_EVENT() { 0x21 }
private getPOWER_ATTR_BATTERY_PERCENTAGE_REMAINING() { 0x0021 }
private getDOORLOCK_ATTR_LOCKSTATE() { 0x0000 }
private getDOORLOCK_ATTR_NUM_PIN_USERS() { 0x0012 }
private getDOORLOCK_ATTR_MAX_PIN_LENGTH() { 0x0017 }
private getDOORLOCK_ATTR_MIN_PIN_LENGTH() { 0x0018 }
private getDOORLOCK_ATTR_SEND_PIN_OTA() { 0x0032 }
private getALARM_ATTR_ALARM_COUNT() { 0x0000 }
private getALARM_CMD_ALARM() { 0x00 }


def installed() {
	log.trace "ZigBee DTH - Executing installed() for device ${device.displayName}"
}


def uninstalled() {
	def deviceName = device.displayName
	log.trace "ZigBee DTH - Executing uninstalled() for device $deviceName"
	sendEvent(name: "lockRemoved", value: device.id, isStateChange: true, displayed: false)
}


def updated() {
	try {
		if (!state.init || !state.configured) {
			
			state.init = true
			log.trace "ZigBee DTH - Returning commands for lock operation get and battery get"
			def cmds = []
			if (!state.configured) {
				cmds << doConfigure()
			}
			cmds << zigbee.readAttribute(CLUSTER_DOORLOCK, DOORLOCK_ATTR_LOCKSTATE)
			cmds << zigbee.readAttribute(CLUSTER_POWER, POWER_ATTR_BATTERY_PERCENTAGE_REMAINING)
			cmds = cmds.flatten()
			log.info "ZigBee DTH - updated() returning with cmds:- $cmds"
			return response(cmds)
		}
	} catch (e) {
		log.warn "ZigBee DTH - updated() threw exception:- $e"
	}
	return null
}


def ping() {
	log.trace "ZigBee DTH - Executing ping() for device ${device.displayName}"
	refresh()
}


def poll() {
	log.trace "ZigBee DTH - Executing poll() for device ${device.displayName}"
	def cmds = []
	def latest = device.currentState("lock")?.date?.time
	if (!latest || !secondsPast(latest, 6 * 60) || secondsPast(state.lastPoll, 55 * 60)) {
		cmds << zigbee.readAttribute(CLUSTER_DOORLOCK, DOORLOCK_ATTR_LOCKSTATE)
		state.lastPoll = now()
	} else if (!state.lastbatt || now() - state.lastbatt > 53*60*60*1000) {
		cmds << zigbee.readAttribute(CLUSTER_POWER, POWER_ATTR_BATTERY_PERCENTAGE_REMAINING)
		state.lastbatt = now()
	}

	if (cmds) {
		log.info "ZigBee DTH - poll() returning with cmds:- $cmds"
		return cmds
	} else {
		
		sendEvent(descriptionText: "skipping poll", isStateChange: true, displayed: false)
		return null
	}
}


def refresh() {
	log.trace "ZigBee DTH - Executing refresh() for device ${device.displayName}"
	def cmds =
		zigbee.readAttribute(CLUSTER_DOORLOCK, DOORLOCK_ATTR_LOCKSTATE) +
		zigbee.readAttribute(CLUSTER_POWER, POWER_ATTR_BATTERY_PERCENTAGE_REMAINING)
	log.info "ZigBee DTH - refresh() returning with cmds:- $cmds"
	return cmds
}


def configure() {
	log.trace "ZigBee DTH - Executing configure() for device ${device.displayName}"
	def cmds = doConfigure()
	log.info "ZigBee DTH - configure() returning with cmds:- $cmds"
	cmds
}


def doConfigure() {
	log.trace "ZigBee DTH - Executing doConfigure() for device ${device.displayName}"
	state.configured = true
	
	sendEvent(name: "checkInterval", value: 2 * 60 * 60 + 2 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID, offlinePingable: "1"])

	def cmds =
		zigbee.configureReporting(CLUSTER_DOORLOCK, DOORLOCK_ATTR_LOCKSTATE,
								  DataType.ENUM8, 0, 3600, null) +
		zigbee.configureReporting(CLUSTER_POWER, POWER_ATTR_BATTERY_PERCENTAGE_REMAINING,
								  DataType.UINT8, 600, 21600, 0x01) +
		zigbee.configureReporting(CLUSTER_ALARM, ALARM_ATTR_ALARM_COUNT,
								  DataType.UINT16, 0, 21600, null)

	def allCmds = refresh() + cmds + reloadAllCodes()
	log.info "ZigBee DTH - doConfigure() returning with cmds:- $allCmds"
	allCmds 
}


def lock() {
	log.trace "ZigBee DTH - Executing lock() for device ${device.displayName}"
	def cmds = zigbee.command(CLUSTER_DOORLOCK, DOORLOCK_CMD_LOCK_DOOR)
	log.info "ZigBee DTH - lock() returning with cmds:- $cmds"
	return cmds
}


def unlock() {
	log.trace "ZigBee DTH - Executing unlock() for device ${device.displayName}"
	def cmds = zigbee.command(CLUSTER_DOORLOCK, DOORLOCK_CMD_UNLOCK_DOOR)
	log.info "ZigBee DTH - unlock() returning with cmds:- $cmds"
	return cmds
}


def reloadAllCodes() {
	log.trace "ZigBee DTH - Executing reloadAllCodes() for device ${device.displayName}"
	sendEvent(name: "scanCodes", value: "Scanning", descriptionText: "Code scan in progress", displayed: false)
	def lockCodes = loadLockCodes()
	sendEvent(lockCodesEvent(lockCodes))
	def cmds = validateAttributes()
	if (isYaleLock()) {
		state.checkCode = state.checkCode ?: 1
	} else {
		state.checkCode = state.checkCode ?: 0
	}
	cmds += requestCode(state.checkCode)

	log.info "ZigBee DTH - reloadAllCodes() returning with cmds:- $cmds"
	return cmds
}


def setCode(codeID, code, codeName = null) {
	if (!code) {
		log.trace "ZigBee DTH - Executing nameSlot() for device ${device.displayName}"
		nameSlot(codeID, codeName)
		return
	}

	log.trace "ZigBee DTH - Executing setCode() for device ${device.displayName}"
	if (isValidCodeID(codeID) && isValidCode(code)) {
		log.debug "Zigbee DTH - setting code in slot number $codeID"
		def cmds = []
		def attrCmds = validateAttributes()
		def setPayload = getPayloadToSetCode(codeID, code)
		if (isYaleLock()) {
			
			
			cmds << zigbee.command(CLUSTER_DOORLOCK, DOORLOCK_CMD_USER_CODE_SET, setPayload).first()
			cmds << requestCode(codeID).first()
			state["setcode$codeID"] = encrypt(code.toString())
			cmds = delayBetween(cmds, 4200)
		} else {
			cmds << zigbee.command(CLUSTER_DOORLOCK, DOORLOCK_CMD_USER_CODE_SET, setPayload).first()
		}

		def strname = (codeName ?: "Code $codeID")
		state["setname$codeID"] = strname
		if(attrCmds) {
			cmds = attrCmds + cmds
		}
		return cmds
	} else {
		log.warn "Zigbee DTH - Invalid input: Unable to set code in slot number $codeID"
		return null
	}
}


def validateAttributes() {
	def cmds = []
	if (!state.attrAlarmCountSet) {
		state.attrAlarmCountSet = true
		cmds += zigbee.configureReporting(CLUSTER_ALARM, ALARM_ATTR_ALARM_COUNT,
				DataType.UINT16, 0, 21600, null)
	}
	
	cmds += zigbee.writeAttribute(CLUSTER_DOORLOCK, DOORLOCK_ATTR_SEND_PIN_OTA, DataType.BOOLEAN, 1)
	if(!device.currentValue("maxCodes")) {
		cmds += zigbee.readAttribute(CLUSTER_DOORLOCK, DOORLOCK_ATTR_NUM_PIN_USERS)
	}
	if(!device.currentValue("minCodeLength")) {
		cmds += zigbee.readAttribute(CLUSTER_DOORLOCK, DOORLOCK_ATTR_MIN_PIN_LENGTH)
	}
	if(!device.currentValue("maxCodeLength")) {
		cmds += zigbee.readAttribute(CLUSTER_DOORLOCK, DOORLOCK_ATTR_MAX_PIN_LENGTH)
	}
	cmds = cmds.flatten()
	log.trace "validateAttributes returning commands list: " + cmds
	cmds
}


def deleteCode(codeID) {
	log.trace "ZigBee DTH - Executing deleteCode() for device ${device.displayName}"
	def cmds = []
	if (isValidCodeID(codeID)) {
		log.debug "Zigbee DTH - deleting code slot number $codeID"
		
		
		
		cmds = zigbee.writeAttribute(CLUSTER_DOORLOCK, DOORLOCK_ATTR_SEND_PIN_OTA, DataType.BOOLEAN, 1)
		cmds += zigbee.command(CLUSTER_DOORLOCK, DOORLOCK_CMD_CLEAR_USER_CODE, getLittleEndianHexString(codeID))
		cmds += requestCode(codeID)
	} else {
		log.warn "Zigbee DTH - Invalid input: Unable to delete slot number $codeID"
	}
	log.info "ZigBee DTH - deleteCode() returning with cmds:- $cmds"
	return cmds
}


def updateCodes(codeSettings) {
	log.trace "ZigBee DTH - Executing updateCodes() for device ${device.displayName}"
	if(codeSettings instanceof String) codeSettings = util.parseJson(codeSettings)
	def set_cmds = []
	def get_cmds = []
	codeSettings.each { name, updated ->
		if (name.startsWith("code")) {
			def n = name[4..-1].toInteger()
			if (updated && updated.size() >= 4 && updated.size() <= 8) {
				log.debug "Setting code number $n"
				def setPayload = getPayloadToSetCode(n, updated)
				set_cmds << zigbee.command(CLUSTER_DOORLOCK, DOORLOCK_CMD_USER_CODE_SET, setPayload).first()
				if (isYaleLock()) {
					get_cmds << requestCode(n).first()
				}
			} else if (updated == null || updated == "" || updated == "0") {
				log.debug "Deleting code number $n"
				set_cmds << zigbee.command(CLUSTER_DOORLOCK, DOORLOCK_CMD_CLEAR_USER_CODE, getLittleEndianHexString(n)).first()
				get_cmds << requestCode(n).first()
			}
		} else log.warn("unexpected entry $name: $updated")
	}

	if (set_cmds && get_cmds) {
		def allCmds = []
		allCmds = delayBetween(set_cmds, 2200) + ["delay 2200"] + delayBetween(get_cmds, 4200)
		return response(allCmds)
	} else if (set_cmds) {
		return response(delayBetween(set_cmds, 4200))
	}
	return null
}


def nameSlot(codeSlot, codeName) {
	def lockCodes = loadLockCodes()
	codeSlot = codeSlot.toString()
	if (lockCodes[codeSlot]) {
		def deviceName = device.displayName
		log.trace "ZigBee DTH - Executing nameSlot() for device $deviceName"
		def oldCodeName = getCodeName(lockCodes, codeSlot)
		def newCodeName = codeName ?: "Code $codeSlot"
		lockCodes[codeSlot] = newCodeName
		sendEvent(lockCodesEvent(lockCodes))
		sendEvent(name: "codeChanged", value: "$codeSlot renamed", data: [ lockName: deviceName, notify: false, notificationText: "Renamed \"$oldCodeName\" to \"$newCodeName\" in $deviceName at ${location.name}" ],
			descriptionText: "Renamed \"$oldCodeName\" to \"$newCodeName\"", displayed: true, isStateChange: true)
	}
}


def requestCode(codeID) {
	return zigbee.command(CLUSTER_DOORLOCK, DOORLOCK_CMD_USER_CODE_GET, getLittleEndianHexString(codeID))
}


def parse(String description) {
	log.trace "ZigBee DTH - Executing parse() for device ${device.displayName}"
	def result = null
	if (description) {
		if (description.startsWith('read attr -')) {
			result = parseAttributeResponse(description)
		} else {
			result = parseCommandResponse(description)
		}
	}
	return result
}


private def parseAttributeResponse(String description) {
	Map descMap = zigbee.parseDescriptionAsMap(description)
	log.trace "ZigBee DTH - Executing parseAttributeResponse() for device ${device.displayName} with description map:- $descMap"
	def result = []
	Map responseMap = [:]
	def clusterInt = descMap.clusterInt
	def attrInt = descMap.attrInt
	def deviceName = device.displayName
	if (clusterInt == CLUSTER_POWER && attrInt == POWER_ATTR_BATTERY_PERCENTAGE_REMAINING) {
		responseMap.name = "battery"
		responseMap.value = Math.round(Integer.parseInt(descMap.value, 16) / 2)
		
		if (reportsBatteryIncorrectly()) {
			responseMap.value = Integer.parseInt(descMap.value, 16)
		}
		responseMap.descriptionText = "Battery is at ${responseMap.value}%"
	} else if (clusterInt == CLUSTER_DOORLOCK && attrInt == DOORLOCK_ATTR_LOCKSTATE) {
		def value = Integer.parseInt(descMap.value, 16)
		responseMap.name = "lock"
		if (value == 0) {
			responseMap.value = "unknown"
			responseMap.descriptionText = "Unknown state"
		} else if (value == 1) {
			responseMap.value = "locked"
			responseMap.descriptionText = "Locked"
		} else if (value == 2) {
			responseMap.value = "unlocked"
			responseMap.descriptionText = "Unlocked"
		} else {
			responseMap.value = "unknown"
			responseMap.descriptionText = "Unknown state"
		}
	} else if (clusterInt == CLUSTER_DOORLOCK && attrInt == DOORLOCK_ATTR_MIN_PIN_LENGTH && descMap.value) {
		def minCodeLength = Integer.parseInt(descMap.value, 16)
		responseMap = [name: "minCodeLength", value: minCodeLength, descriptionText: "Minimum PIN length is ${minCodeLength}", displayed: false]
	} else if (clusterInt == CLUSTER_DOORLOCK && attrInt == DOORLOCK_ATTR_MAX_PIN_LENGTH && descMap.value) {
		def maxCodeLength = Integer.parseInt(descMap.value, 16)
		responseMap = [name: "maxCodeLength", value: maxCodeLength, descriptionText: "Maximum PIN length is ${maxCodeLength}", displayed: false]
	} else if (clusterInt == CLUSTER_DOORLOCK && attrInt == DOORLOCK_ATTR_NUM_PIN_USERS && descMap.value) {
		def maxCodes = Integer.parseInt(descMap.value, 16)
		responseMap = [name: "maxCodes", value: maxCodes, descriptionText: "Maximum Number of user codes supported is ${maxCodes}", displayed: false]
	} else {
		log.trace "ZigBee DTH - parseAttributeResponse() - ignoring attribute response"
		return null
	}

	if (responseMap.data) {
		responseMap.data.lockName = deviceName
	} else {
		responseMap.data = [ lockName: deviceName ]
	}
	result << createEvent(responseMap)
	log.info "ZigBee DTH - parseAttributeResponse() returning with result:- $result"
	return result
}


private def parseCommandResponse(String description) {
	Map descMap = zigbee.parseDescriptionAsMap(description)
	def deviceName = device.displayName
	log.trace "ZigBee DTH - Executing parseCommandResponse() for device ${deviceName}"

	def result = []
	Map responseMap = [:]
	def data = descMap.data
	def lockCodes = loadLockCodes()

	def cmd = descMap.commandInt
	def clusterInt = descMap.clusterInt

	if (clusterInt == CLUSTER_DOORLOCK && (cmd == DOORLOCK_CMD_LOCK_DOOR || cmd == DOORLOCK_CMD_UNLOCK_DOOR)) {
		log.trace "ZigBee DTH - Executing DOOR LOCK/UNLOCK SUCCESS for device ${deviceName} with description map:- $descMap"
		
		def cmdList = []
		cmdList << "delay 4200"
		cmdList << zigbee.readAttribute(CLUSTER_DOORLOCK, DOORLOCK_ATTR_LOCKSTATE).first()
		result << response(cmdList)
	} else if (clusterInt == CLUSTER_DOORLOCK && cmd == DOORLOCK_RESPONSE_OPERATION_EVENT) {
		log.trace "ZigBee DTH - Executing DOORLOCK_RESPONSE_OPERATION_EVENT for device ${deviceName} with description map:- $descMap"
		def eventSource = Integer.parseInt(data[0], 16)
		def eventCode = Integer.parseInt(data[1], 16)

		responseMap.name = "lock"
		responseMap.displayed = true
		responseMap.isStateChange = true

		def desc = ""
		def codeName = ""

		if (eventSource == 0) {
			def codeID = Integer.parseInt(data[3] + data[2], 16)
			if (!isValidCodeID(codeID, true)) {
				
				log.debug "Invalid slot number := $codeID"
				return null
			}
			codeName = getCodeName(lockCodes, codeID)
			responseMap.data = [ codeId: codeID as String, usedCode: codeID, codeName: codeName, method: "keypad" ]
		} else if (eventSource == 1) {
			responseMap.data = [ method: "command" ]
		} else if (eventSource == 2) {
			desc = "manually"
			responseMap.data = [ method: "manual" ]
		}

		switch (eventCode) {
			case 1:
				responseMap.value = "locked"
				if(codeName) {
					responseMap.descriptionText = "Locked by \"$codeName\""
				} else {
					responseMap.descriptionText = "Locked ${desc}"
				}
				break
			case 2:
				responseMap.value = "unlocked"
				if(codeName) {
					responseMap.descriptionText = "Unlocked by \"$codeName\""
				} else {
					responseMap.descriptionText = "Unlocked ${desc}"
				}
				break
			case 3: 
				break
			case 4: 
				break
			case 5: 
				break
			case 6: 
				break
			case 7: 
			case 8: 
			case 13: 
				responseMap.value = "locked"
				responseMap.descriptionText = "Locked ${desc}"
				break
			case 9: 
			case 14: 
				responseMap.value = "unlocked"
				responseMap.descriptionText = "Unlocked ${desc}"
				break
			case 10: 
				responseMap.value = "locked"
				responseMap.descriptionText = "Auto locked"
				responseMap.data = [ method: "auto" ]
				break
			default:
				break
		}
	} else if (clusterInt == CLUSTER_DOORLOCK && cmd == DOORLOCK_CMD_USER_CODE_SET) {
		log.trace "ZigBee DTH - Executing DOORLOCK_CMD_USER_CODE_SET for device ${deviceName} with description map:- $descMap"
		def status = Integer.parseInt(data[0], 16)
		switch (status) {
			case 0:
				log.debug "Lock code creation successful"
				
				
				break
			case 1:
				log.debug "Lock code creation failed - General failure"
				break
			case 2:
				log.debug "Lock code creation failed - Memory full"
				break
			case 3:
				log.debug "Lock code creation failed - Duplicate Code error"
				break
			default:
				break
		}
	} else if (clusterInt == CLUSTER_DOORLOCK && cmd == DOORLOCK_RESPONSE_PROGRAMMING_EVENT) {
		log.trace "ZigBee DTH - Executing DOORLOCK_RESPONSE_PROGRAMMING_EVENT for device ${deviceName} with description map:- $descMap"
		
		
		

		responseMap.name = "codeChanged"
		responseMap.isStateChange = true
		responseMap.displayed = true

		def codeID = Integer.parseInt(data[3] + data[2], 16)
		def codeName

		def eventCode = Integer.parseInt(data[1], 16)
		switch (eventCode) {
			case 1: 
				codeName = "Master Code"
				responseMap.value = "0 set"
				responseMap.descriptionText = "${getStatusForDescription('set')} \"Master Code\""
				responseMap.data = [ codeName: codeName, notify: true, notificationText: "${getStatusForDescription('set')} \"$codeName\" in $deviceName at ${location.name}" ]
				break
			case 3: 
				if (codeID == 255) {
					result = allCodesDeletedEvent()
					responseMap.value = "all deleted"
					responseMap.descriptionText = "Deleted all user codes"
					responseMap.data = [notify: true, notificationText: "Deleted all user codes in $deviceName at ${location.name}"]
					result << createEvent(name: "lockCodes", value: util.toJson([:]), displayed: false, descriptionText: "'lockCodes' attribute updated")
				} else {
					if (lockCodes[codeID.toString()]) {
						codeName = getCodeName(lockCodes, codeID)
						responseMap.value = "$codeID deleted"
						responseMap.descriptionText = "Deleted \"$codeName\""
						responseMap.data = [ codeName: codeName, notify: true, notificationText: "Deleted \"$codeName\" in $deviceName at ${location.name}" ]
						result << codeDeletedEvent(lockCodes, codeID)
					}
				}
				break
			case 2: 
			case 4: 
				if (isValidCodeID(codeID)) {
					codeName = getCodeNameFromState(lockCodes, codeID)
					def changeType = getChangeType(lockCodes, codeID)
					responseMap.value = "$codeID $changeType"
					responseMap.descriptionText = "${getStatusForDescription(changeType)} \"$codeName\""
					responseMap.data = [ codeName: codeName, notify: true, notificationText: "${getStatusForDescription(changeType)} \"$codeName\" in $deviceName at ${location.name}" ]
					result << codeSetEvent(lockCodes, codeID, codeName)
				} else {
					
					log.debug "Invalid slot number := $codeID"
				}
				break
			default:
				break
		}
	} else if (clusterInt == CLUSTER_DOORLOCK && cmd == DOORLOCK_CMD_USER_CODE_GET) {
		log.trace "ZigBee DTH - Executing DOORLOCK_CMD_USER_CODE_GET for device ${deviceName}"
		
		

		def userStatus = Integer.parseInt(data[2], 16)
		def codeID = Integer.parseInt(data[1] + data[0], 16)
		def codeName = getCodeNameFromState(lockCodes, codeID)

		
		def localCode = decrypt(state["setcode$codeID"])

		responseMap.name = "codeChanged"
		responseMap.isStateChange = true
		responseMap.displayed = true

		
		if (userStatus == 1) {
			if (localCode && isYaleLock()) {
				

				
				def serverCode = getCodeFromOctet(data)
				if (localCode == serverCode) {
					
					log.debug "Code matches - lock code creation successful"
					def changeType = getChangeType(lockCodes, codeID)
					responseMap.value = "$codeID $changeType"
					responseMap.descriptionText = "${getStatusForDescription(changeType)} \"$codeName\""
					responseMap.data = [ codeName: codeName, notify: true, notificationText: "${getStatusForDescription(changeType)} \"$codeName\" in $deviceName at ${location.name}" ]
					result << codeSetEvent(lockCodes, codeID, codeName)
				} else {
					
					log.debug "Code update failed"
					responseMap.value = "$codeID failed"
					responseMap.descriptionText = "Failed to update code '$codeName'"
					
					
					
					responseMap.data = [isCodeDuplicate: true]
				}
			} else {
				
				
				log.debug "Scanning lock - code $codeID is occupied"
				def changeType = getChangeType(lockCodes, codeID)
				responseMap.value = "$codeID $changeType"
				responseMap.descriptionText = "${getStatusForDescription(changeType)} \"$codeName\""
				responseMap.data = [ codeName: codeName ]
				if ("set" == changeType) {
					result << codeSetEvent(lockCodes, codeID, codeName)
				} else {
					responseMap.displayed = false
				}
			}
		} else {
			
			if (localCode != null && isYaleLock()) {
				
				log.debug "Code creation failed"
				responseMap.value = "$codeID failed"
				responseMap.descriptionText = "Failed to set code '$codeName'"
				
				
				
				responseMap.data = [isCodeDuplicate: true]

				def codeReportMap = [ name: "codeReport", value: codeID, data: [ code: "" ], isStateChange: true, displayed: false ]
				codeReportMap.descriptionText = "Code $codeID is not set"
				result << createEvent(codeReportMap)
			} else if (lockCodes[codeID.toString()]) {
				codeName = getCodeName(lockCodes, codeID)
				responseMap.value = "$codeID deleted"
				responseMap.descriptionText = "Deleted \"$codeName\""
				responseMap.data = [ codeName: codeName, notify: true, notificationText: "Deleted \"$codeName\" in $deviceName at ${location.name}" ]
				result << codeDeletedEvent(lockCodes, codeID)
			} else {
				
				responseMap.value = "$codeID unset"
				responseMap.descriptionText = "Code slot $codeID found empty during scanning"
				responseMap.displayed = false
			}
		}
		clearStateForSlot(codeID)

		if (codeID == state.checkCode) {
			log.debug "Code scanning in progress..."
			def defaultMaxCodes = isYaleLock() ? 8 : 7
			def maxCodes = device.currentValue("maxCodes") ?: defaultMaxCodes
			
			maxCodes = defaultMaxCodes
			if (state.checkCode >= maxCodes) {
				log.debug "Code scanning complete"
				state["checkCode"] = null
				sendEvent(name: "scanCodes", value: "Complete", descriptionText: "Code scan completed", displayed: false)
			} else {
				log.debug "More codes to scan..."
				state.checkCode = state.checkCode + 1
				result << response(requestCode(state.checkCode))
			}
		}
	} else if (clusterInt == CLUSTER_ALARM && cmd == ALARM_CMD_ALARM) {
		log.trace "ZigBee DTH - Executing ALARM_CMD_ALARM for device ${deviceName} with description map:- $descMap"
		def alarmCode = Integer.parseInt(data[0], 16)
		switch (alarmCode) {
			case 0: 
				responseMap = [ name: "lock", value: "unknown", descriptionText: "Was in unknown state" ]
				break
			case 1: 
				responseMap = [ name: "lock", value: "unknown", descriptionText: "Has been reset to factory defaults" ]
				break
			case 2: 
				break
			case 3: 
				responseMap = [ descriptionText: "Batteries replaced", isStateChange: true ]
				break
			case 4: 
				responseMap = [ name: "tamper", value: "detected", descriptionText: "Keypad attempts exceed code entry limit", isStateChange: true ]
				break
			case 5: 
				responseMap = [ name: "tamper", value: "detected", descriptionText: "Front escutcheon removed", isStateChange: true ]
				break
			case 6: 
				responseMap = [ name: "tamper", value: "detected", descriptionText: "Door forced open under door locked condition", isStateChange: true ]
				break
			case 16: 
				responseMap = [ name: "battery", value: device.currentValue("battery"), descriptionText: "Battery too low to operate lock", isStateChange: true ]
				break
			case 17: 
				responseMap = [ name: "battery", value: device.currentValue("battery"), descriptionText: "Battery level critical", isStateChange: true ]
				break
			case 18: 
				responseMap = [ name: "battery", value: device.currentValue("battery"), descriptionText: "Battery very low", isStateChange: true ]
				break
			case 19: 
				responseMap = [ name: "battery", value: device.currentValue("battery"), descriptionText: "Battery low", isStateChange: true ]
				break
			default:
				break
		}
	} else {
		log.trace "ZigBee DTH - parseCommandResponse() - ignoring command response"
	}

	if(responseMap["value"]) {
		if (responseMap.data) {
			responseMap.data.lockName = deviceName
		} else {
			responseMap.data = [ lockName: deviceName ]
		}
		result << createEvent(responseMap)
	}
	if (result) {
		result = result.flatten()
	} else {
		result = null
	}
	log.debug "ZigBee DTH - parseCommandResponse() returning with result:- $result"
	return result
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
		result << createEvent(name: "codeChanged", value: "$id deleted",
		data: [ codeName: codeName, lockName: deviceName, notify: true,
			notificationText: "Deleted \"$codeName\" in $deviceName at ${location.name}" ],
		descriptionText: "Deleted \"$codeName\"",
		displayed: true, isStateChange: true)
		clearStateForSlot(id)
	}
	result
}


private Map lockCodesEvent(lockCodes) {
	createEvent(name: "lockCodes", value: util.toJson(lockCodes), displayed: false, descriptionText: "'lockCodes' attribute updated")
}


private Map loadLockCodes() {
	parseJson(device.currentValue("lockCodes") ?: "{}") ?: [:]
}


private def getCodeFromOctet(data) {
	def code = ""
	def codeLength = Integer.parseInt(data[4], 16)
	if (codeLength >= device.currentValue("minCodeLength") && codeLength <= device.currentValue("maxCodeLength")) {
		for (def i = 5; i < (5 + codeLength); i++) {
			code += (char) (zigbee.convertHexToInt(data[i]))
		}
	}
	return code
}


private boolean isValidCodeID(codeID, allowMasterCode = false) {
	def defaultMaxCodes = isYaleLock() ? 250 : 30
	def minCodeId = isYaleLock() ? 1 : 0
	if (allowMasterCode) {
		minCodeId = 0
	}
	def maxCodes = device.currentValue("maxCodes") ?: defaultMaxCodes
	if (codeID.toInteger() >= minCodeId && codeID.toInteger() <= maxCodes) {
		return true
	}
	return false
}


private boolean isValidCode(code) {
	def minCodeLength = device.currentValue("minCodeLength") ?: 4
	def maxCodeLength = device.currentValue("maxCodeLength") ?: 8
	if (code.toString().size() <= maxCodeLength && code.toString().size() >= minCodeLength && code.isNumber()) {
		return true
	}
	return false
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


def clearStateForSlot(codeID) {
	state.remove("setname$codeID")
	state["setname$codeID"] = null
	if (isYaleLock()) {
		state.remove("setcode$codeID")
		state["setcode$codeID"] = null
	}
}


def getPayloadToSetCode(codeID, code) {
	def payload = "" + getLittleEndianHexString(codeID)
	payload += " " + getUserStatusForOccupied() + " " + getDefaultUserType()
	payload += " " + getOctetStringForCode(code)
	payload
}


def getUserStatusForOccupied() {
	return "01"
}


def getDefaultUserType() {
	return "00"
}


def getOctetStringForCode(code) {
	def octetStr = "" + zigbee.convertToHexString(code.length(), 2)
	for(int i = 0; i < code.length(); i++) {
		octetStr += " " +  zigbee.convertToHexString((int) code.charAt(i), 2)
	}
	octetStr
}


def getLittleEndianHexString(numStr) {
	return zigbee.swapEndianHex(zigbee.convertToHexString(numStr.toInteger(), 4))
}


def isYaleLock() {
	return "Yale" == device.getDataValue("manufacturer")
}


def reportsBatteryIncorrectly() {
	def badModels = [
			"YRD220/240 TSDB",
			"YRL220 TS LL",
			"YRD210 PB DB",
			"YRD220/240 TSDB",
			"YRL210 PB LL",
	]
	return (isYaleLock() && device.getDataValue("model") in badModels)
}


private String getCodeNameFromState(lockCodes, codeID) {
	if (isMasterCode(codeID) && isYaleLock()) {
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


private String getCodeName(lockCodes, codeID) {
	if (isMasterCode(codeID) && isYaleLock()) {
		return "Master Code"
	}
	lockCodes[codeID.toString()] ?: "Code $codeID"
}


private boolean isMasterCode(codeID) {
	if(codeID instanceof String) {
		codeID = codeID.toInteger()
	}
	(codeID == 0) ? true : false
}
