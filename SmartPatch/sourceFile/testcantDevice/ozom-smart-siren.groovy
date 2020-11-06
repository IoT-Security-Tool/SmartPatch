"License"
"AS IS"

import physicalgraph.zigbee.clusters.iaszone.ZoneStatus
import physicalgraph.zigbee.zcl.DataType

metadata {
	definition(name: "Ozom Smart Siren", namespace: "smartthings", author: "SmartThings", mnmn: "SmartThings", vid: "generic-siren-2", ocfDeviceType: "x.com.st.d.siren") {
		capability "Actuator"
		capability "Alarm"
		capability "Switch"
		capability "Configuration"
		capability "Health Check"

		fingerprint profileId: "0104", inClusters: "0000,0003,0500,0502", outClusters: "0000", manufacturer: "ClimaxTechnology", model: "SRAC_00.00.00.16TC", vid: "generic-siren-8", deviceJoinName: "Ozom Smart Siren" 
		fingerprint profileId: "0104", inClusters: "0000,0001,0003,0004,0009,0500,0502", outClusters: "0003,0019", manufacturer: "Heiman", model: "WarningDevice", deviceJoinName: "HEIMAN Smart Siren"
	}

	tiles {
		standardTile("alarm", "device.alarm", width: 2, height: 2) {
			state "off", label:'off', action:'alarm.siren', icon:"st.secondary.siren", backgroundColor:"#ffffff"
			state "siren", label:'siren!', action:'alarm.off', icon:"st.secondary.siren", backgroundColor:"#e86d13"
		}

		main "alarm"
		details(["alarm"])
	}
}

private getDEFAULT_MAX_DURATION() { 0x00B4 }
private getDEFAULT_DURATION() { 0xFFFE }

private getIAS_WD_CLUSTER() { 0x0502 }

private getATTRIBUTE_IAS_WD_MAXDURATION() { 0x0000 }
private getATTRIBUTE_IAS_ZONE_STATUS() { 0x0002 }

private getCOMMAND_IAS_WD_START_WARNING() { 0x00 }
private getCOMMAND_DEFAULT_RESPONSE() { 0x0B }

private getMODE_SIREN() { "13" }
private getMODE_STROBE() { "04" }
private getMODE_BOTH() { "17" }
private getMODE_OFF() { "00" }
private getSTROBE_DUTY_CYCLE() { "40" }
private getSTROBE_LEVEL() { "03" }

private getALARM_OFF() { 0x00 }
private getALARM_SIREN() { 0x01 }
private getALARM_STROBE() { 0x02 }
private getALARM_BOTH() { 0x03 }

def turnOffAlarmTile() {
	sendEvent(name: "alarm", value: "off")
	sendEvent(name: "switch", value: "off")
}

def turnOnAlarmTile(cmd) {
	log.debug "turn on alarm tile ${cmd}"
	if (cmd == ALARM_SIREN) {
		sendEvent(name: "alarm", value: "siren")
	} else if (cmd == ALARM_STROBE) {
		sendEvent(name: "alarm", value: "strobe")
	} else if (cmd == ALARM_BOTH) {
		sendEvent(name: "alarm", value: "both")
	}
	sendEvent(name: "switch", value: "on")
}

def installed() {
	sendCheckIntervalEvent()
	state.maxDuration = DEFAULT_MAX_DURATION
	turnOffAlarmTile()
}

def parse(String description) {
	log.debug "Parsing '${description}'"

	Map map = zigbee.getEvent(description)
	if (!map) {
		if (description?.startsWith('enroll request')) {
			List cmds = zigbee.enrollResponse()
			log.debug "enroll response: ${cmds}"
			return cmds
		} else {
			Map descMap = zigbee.parseDescriptionAsMap(description)
			if (descMap?.clusterInt == IAS_WD_CLUSTER) {
				def data = descMap.data

				Integer parsedAttribute = descMap.attrInt
				Integer command = Integer.parseInt(descMap.command, 16)
				if (parsedAttribute == ATTRIBUTE_IAS_WD_MAXDURATION && descMap?.value) {
					state.maxDuration = Integer.parseInt(descMap.value, 16)
				} else if (command == COMMAND_DEFAULT_RESPONSE) {
					Boolean isSuccess = Integer.parseInt(data[-1], 16) == 0
					Integer receivedCommand = Integer.parseInt(data[-2], 16)
					if (receivedCommand == COMMAND_IAS_WD_START_WARNING && isSuccess){
						if (state.alarmCmd != ALARM_OFF) {
							turnOnAlarmTile(state.alarmCmd)
							runIn(state.lastDuration, turnOffAlarmTile)
						} else {
							turnOffAlarmTile()
						}
					}
				}
			}
		}
	}
	log.debug "Parse returned $map"
	def results = map ? createEvent(map) : null
	log.debug "parse results: " + results
	return results
}

private sendCheckIntervalEvent() {
	sendEvent(name: "checkInterval", value: 30 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
}

def ping() {
	return zigbee.readAttribute(zigbee.IAS_ZONE_CLUSTER, zigbee.ATTRIBUTE_IAS_ZONE_STATUS)
}

def configure() {
	sendCheckIntervalEvent()

	def cmds = zigbee.enrollResponse() +
			zigbee.writeAttribute(IAS_WD_CLUSTER, ATTRIBUTE_IAS_WD_MAXDURATION, DataType.UINT16, DEFAULT_DURATION) +
			zigbee.configureReporting(zigbee.IAS_ZONE_CLUSTER, zigbee.ATTRIBUTE_IAS_ZONE_STATUS, DataType.BITMAP16, 0, 180, null)
	log.debug "configure: " + cmds

	return cmds	
}

def both() {
	log.debug "both()"
	startCmd(ALARM_BOTH)
}

def siren() {
	log.debug "siren()"
	startCmd(ALARM_SIREN)
}

def strobe() {
	log.debug "strobe()"
	startCmd(ALARM_SIREN)
}

def startCmd(cmd) {
	log.debug "start command ${cmd}"

	state.alarmCmd = cmd
	def warningDuration = state.maxDuration ? state.maxDuration : DEFAULT_MAX_DURATION
	state.lastDuration = warningDuration

	def paramMode;
	def paramDutyCycle = STROBE_DUTY_CYCLE;
	def paramStrobeLevel = STROBE_LEVEL;
	if (cmd == ALARM_SIREN) {
		paramMode = MODE_SIREN
		paramDutyCycle = "00"
		paramStrobeLevel = "00"
	} else if (cmd == ALARM_STROBE) {
		paramMode = MODE_STROBE
	} else if (cmd == ALARM_BOTH) {
		paramMode = MODE_BOTH
	}
    
    zigbee.command(IAS_WD_CLUSTER, COMMAND_IAS_WD_START_WARNING, paramMode, DataType.pack(warningDuration, DataType.UINT16), paramDutyCycle, paramStrobeLevel)
}

def on() {
	log.debug "on()"

	if (isOzomSiren()) {
		siren()
	} else {
		both()
	}
}

def off() {
	log.debug "off()"

	state.alarmCmd = ALARM_OFF
	zigbee.command(IAS_WD_CLUSTER, COMMAND_IAS_WD_START_WARNING, "00", "0000", "00", "00")
}

private isOzomSiren() {
	device.getDataValue("manufacturer") == "ClimaxTechnology"
}