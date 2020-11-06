"License"
"AS IS"
import physicalgraph.zigbee.zcl.DataType

metadata {
	definition (name: "ZigBee Battery Accessory Dimmer", namespace: "smartthings", author: "SmartThings", ocfDeviceType: "oic.d.switch") {
		capability "Actuator"
		capability "Battery"
		capability "Configuration"
		capability "Health Check"
		capability "Switch"
		capability "Switch Level"

		
		fingerprint profileId: "0104", inClusters: "0000,0001,0003,0020,FC11", outClusters: "0003,0004,0006,0008,FC10", manufacturer: "sengled", model: "E1E-G7F", deviceJoinName: "Sengled Smart Switch", mnmn:"SmartThings", vid: "generic-dimmer"
		fingerprint manufacturer: "IKEA of Sweden", model: "TRADFRI wireless dimmer", deviceJoinName: "IKEA TRÅDFRI Wireless dimmer" 
		fingerprint profileId: "0104", inClusters: "0000,0001,0003,0020,0B05", outClusters: "0003,0006,0008,0019", manufacturer: "Centralite Systems", model: "3131-G", deviceJoinName: "Centralite Smart Switch"
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label:'${name}', action:"switch.off", icon:"st.switches.light.on", backgroundColor:"#00A0DC", nextState:"turningOff"
				attributeState "off", label:'${name}', action:"switch.on", icon:"st.switches.light.off", backgroundColor:"#ffffff", nextState:"turningOn"
				attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.switches.light.on", backgroundColor:"#00A0DC", nextState:"turningOff"
				attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.switches.light.off", backgroundColor:"#ffffff", nextState:"turningOn"
			}
			tileAttribute ("device.level", key: "SLIDER_CONTROL") {
				attributeState "level", action:"switch level.setLevel"
			}
			tileAttribute ("device.battery", key: "SECONDARY_CONTROL") {
				attributeState "battery", label: 'battery ${currentValue}%', unit: "%"
			}
		}
		main "switch"
		details(["switch"])
	}
}

def getDOUBLE_STEP() { 10 }
def getSTEP() { 5 }

def getONOFF_ON_COMMAND() { 0x0001 }
def getONOFF_OFF_COMMAND() { 0x0000 }
def getLEVEL_MOVE_LEVEL_COMMAND() { 0x0000 }
def getLEVEL_MOVE_COMMAND() { 0x0001 }
def getLEVEL_STEP_COMMAND() { 0x0002 }
def getLEVEL_STOP_COMMAND() { 0x0003 }
def getLEVEL_MOVE_LEVEL_ONOFF_COMMAND() { 0x0004 }
def getLEVEL_MOVE_ONOFF_COMMAND() { 0x0005 }
def getLEVEL_STEP_ONOFF_COMMAND() { 0x0006 }
def getLEVEL_STOP_ONOFF_COMMAND() { 0x0007 }
def getLEVEL_DIRECTION_UP() { "00" }
def getLEVEL_DIRECTION_DOWN() { "01" }

def getBATTERY_VOLTAGE_ATTR() { 0x0020 }
def getBATTERY_PERCENT_ATTR() { 0x0021 }

def getMFR_SPECIFIC_CLUSTER() { 0xFC10 }

def getUINT8_STR() { "20" }


private boolean isIkeaDimmer() {
	device.getDataValue("model") == "TRADFRI wireless dimmer"
}
private boolean isSengledSwitch() {
	device.getDataValue("model") == "E1E-G7F"
}
private boolean isCentraliteSwitch() {
	device.getDataValue("model") == "3131-G"
}

def parse(String description) {
	log.debug "description is $description"
	def results = []

	def event = zigbee.getEvent(description)
	if (event) {
		results << createEvent(event)
	} else {
		def descMap = zigbee.parseDescriptionAsMap(description)

		if (descMap.clusterInt == zigbee.POWER_CONFIGURATION_CLUSTER) {
			results = handleBatteryEvents(descMap)
		} else if (isSengledSwitch()) {
			results = handleSengledSwitchEvents(descMap)
		} else if (descMap.clusterInt == zigbee.ONOFF_CLUSTER) {
			results = handleSwitchEvent(descMap)
		} else if (descMap.clusterInt == zigbee.LEVEL_CONTROL_CLUSTER) {
			if (isCentraliteSwitch()) {
				results = handleCentraliteSmartSwitchLevelEvent(descMap)
			} else if (isIkeaDimmer()) {
				results = handleIkeaDimmerLevelEvent(descMap)
			}
		} else {
			log.warn "DID NOT PARSE MESSAGE for description : $description"
			log.debug "${descMap}"
		}
	}

	log.debug "parse returned $results"
	return results
}

def handleSengledSwitchEvents(descMap) {
	def results = []

	if (descMap?.clusterInt == MFR_SPECIFIC_CLUSTER && descMap.data) {
		def currentLevel = device.currentValue("level") as Integer ?: 0
		def value = currentLevel

		switch (descMap.data[0]) {
			case '01':
				
				results << createEvent(name: "switch", value: "on")
				break
			case '02':
				
				if (descMap.data[2] == '02') {
					
					value = Math.min(currentLevel + DOUBLE_STEP, 100)
				} else if (descMap.data[2] == '01') {
					
					value = Math.min(currentLevel + STEP, 100)
				} else {
					log.info "Invalid value ${descMap.data[2]} received for descMap.data[2]"
				}

				results << createEvent(name: "switch", value: "on")
				results << createEvent(name: "level", value: value)
				break
			case '03':
				
				if (descMap.data[2] == '02') {
					
					value = Math.max(currentLevel - DOUBLE_STEP, 0)
				} else if (descMap.data[2] == '01') {
					
					value = Math.max(currentLevel - STEP, 0)
				} else {
					log.info "Invalid value ${descMap.data[2]} received for descMap.data[2]"
				}

				if (value == 0) {
					results << createEvent(name: "switch", value: "off")
				} else {
					results << createEvent(name: "level", value: value)
				}
				break
			case '04':
				
				results << createEvent(name: "switch", value: "off")
				break
			case '06':
				
				results << createEvent(name: "switch", value: "on")
				break
			case '08':
				
				results << createEvent(name: "switch", value: "off")
				break
			default:
				break
		}
	}

	return results
}

def handleCentraliteSmartSwitchLevelEvent(descMap) {
	def results = []

	if (descMap.commandInt == LEVEL_MOVE_ONOFF_COMMAND) {
		
		results = handleStepEvent(LEVEL_DIRECTION_UP, descMap)
	} else if (descMap.commandInt == LEVEL_MOVE_COMMAND) {
		
		results = handleStepEvent(LEVEL_DIRECTION_DOWN, descMap)
	}

	return results
}

def handleIkeaDimmerLevelEvent(descMap) {
	def results = []

	if (descMap.commandInt == LEVEL_STEP_COMMAND) {
		results = handleStepEvent(descMap.data[0], descMap)
	} else if (descMap.commandInt == LEVEL_MOVE_COMMAND || descMap.commandInt == LEVEL_MOVE_ONOFF_COMMAND) {
		
		results = handleStepEvent(descMap.data[0], descMap)
	} else if (descMap.commandInt == LEVEL_STOP_COMMAND || descMap.commandInt == LEVEL_STOP_ONOFF_COMMAND) {
		
		log.debug "Received stop move - not handling"
	} else if (descMap.commandInt == LEVEL_MOVE_LEVEL_ONOFF_COMMAND) {
		
		
		if (descMap.data[0] == "00") {
			results << createEvent(name: "switch", value: "off", isStateChange: true)
		} else if (descMap.data[0] == "FF") {
			
			if (device.currentValue("level") == 0) {
				results << createEvent(name: "level", value: DOUBLE_STEP)
			}

			results << createEvent(name: "switch", value: "on", isStateChange: true)
		} else {
			results << createEvent(name: "switch", value: "on", isStateChange: true)
			
			
			results << createEvent(zigbee.getEventFromAttrData(descMap.clusterInt, descMap.commandInt, UINT8_STR, descMap.data[0]))
		}
	}

	return results
}

def handleSwitchEvent(descMap) {
	def results = []

	if (descMap.commandInt == ONOFF_ON_COMMAND) {
		if (device.currentValue("level") == 0) {
			results << createEvent(name: "level", value: DOUBLE_STEP)
		}
		results << createEvent(name: "switch", value: "on")
	} else if (descMap.commandInt == ONOFF_OFF_COMMAND) {
		results << createEvent(name: "switch", value: "off")
	}

	return results
}

def handleStepEvent(direction, descMap) {
	def results = []
	def currentLevel = device.currentValue("level") as Integer ?: 0
	def value = null

	if (direction == LEVEL_DIRECTION_UP) {
		value = Math.min(currentLevel + DOUBLE_STEP, 100)
	} else if (direction == LEVEL_DIRECTION_DOWN) {
		value = Math.max(currentLevel - DOUBLE_STEP, 0)
	}

	if (value != null) {
		log.debug "Step ${direction == LEVEL_DIRECTION_UP ? "up" : "down"} by $DOUBLE_STEP to $value"

		
		if (value == 0) {
			results << createEvent(name: "switch", value: "off")
		} else {
			results << createEvent(name: "switch", value: "on")
			results << createEvent(name: "level", value: value)
		}
	} else {
		log.debug "Received invalid direction ${direction} - descMap.data = ${descMap.data}"
	}

	return results
}

def handleBatteryEvents(descMap) {
	def results = []

	if (descMap.value) {
		def rawValue = zigbee.convertHexToInt(descMap.value)
		def batteryValue = null

		if (rawValue == 0xFF) {
			
			
			log.info "Invalid battery reading returned"
		} else if (descMap.attrInt == BATTERY_VOLTAGE_ATTR && !isIkeaDimmer()) { 
			def minVolts = 2.3
			def maxVolts = 3.0
			def batteryValueVoltage = rawValue / 10

			batteryValue = Math.round(((batteryValueVoltage - minVolts) / (maxVolts - minVolts)) * 100)
		} else if (descMap.attrInt == BATTERY_PERCENT_ATTR) {
			
			batteryValue = Math.round(rawValue / (isIkeaDimmer() ? 1 : 2))
		}

		if (batteryValue != null) {
			batteryValue = Math.min(100, Math.max(0, batteryValue))

			results << createEvent(name: "battery", value: batteryValue, unit: "%", descriptionText: "{{ device.displayName }} battery was {{ value }}%", translatable: true)
		}
	}

	return results
}

def off() {
	sendEvent(name: "switch", value: "off", isStateChange: true)
}

def on() {
	sendEvent(name: "switch", value: "on", isStateChange: true)
}

def setLevel(value, rate = null) {
	if (value == 0) {
		sendEvent(name: "switch", value: "off")
		
		value = device.currentValue("level")
	} else {
		sendEvent(name: "switch", value: "on")
	}
	runIn(1, delayedSend, [data: createEvent(name: "level", value: value), overwrite: true])
}

def delayedSend(data) {
	sendEvent(data)
}

def ping() {
	if (isCentraliteSwitch()) {
		zigbee.readAttribute(zigbee.POWER_CONFIGURATION_CLUSTER, BATTERY_VOLTAGE_ATTR)
	} else {
		zigbee.readAttribute(zigbee.POWER_CONFIGURATION_CLUSTER, BATTERY_PERCENT_ATTR)
	}
}

def installed() {
	sendEvent(name: "switch", value: "on")
	sendEvent(name: "level", value: 100)
}

def configure() {
	def offlinePingable = isIkeaDimmer() ? "0" : "1" 
	int reportInterval = 3 * 60 * 60

	
	sendEvent(name: "checkInterval", value: 2 * 60 + 2 * reportInterval, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID, offlinePingable: offlinePingable], displayed: false)

	if (isCentraliteSwitch()) {
		zigbee.addBinding(zigbee.ONOFF_CLUSTER) + zigbee.addBinding(zigbee.LEVEL_CONTROL_CLUSTER) +
			zigbee.readAttribute(zigbee.POWER_CONFIGURATION_CLUSTER, BATTERY_VOLTAGE_ATTR) +
			zigbee.batteryConfig(0, reportInterval, null)
	} else {
		zigbee.readAttribute(zigbee.POWER_CONFIGURATION_CLUSTER, BATTERY_PERCENT_ATTR) +
			
			zigbee.configureReporting(zigbee.POWER_CONFIGURATION_CLUSTER, BATTERY_PERCENT_ATTR, DataType.UINT8, 30, reportInterval, 20)
	}
}

