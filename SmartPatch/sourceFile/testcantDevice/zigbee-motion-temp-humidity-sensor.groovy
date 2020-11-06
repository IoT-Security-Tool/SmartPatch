"License"
"AS IS"
import physicalgraph.zigbee.clusters.iaszone.ZoneStatus
import physicalgraph.zigbee.zcl.DataType

metadata {
	definition(name: "Zigbee Motion/Temp/Humidity Sensor", namespace: "smartthings", author: "SmartThings", mnmn: "SmartThings", vid: "generic-motion-6") {
		capability "Motion Sensor"
		capability "Configuration"
		capability "Battery"
		capability "Temperature Measurement"
		capability "Relative Humidity Measurement"
		capability "Refresh"
		capability "Health Check"
		capability "Sensor"
		
		fingerprint inClusters: "0000,0001,0003,0020,0402,0405,0500,0B05,FC01,FC02", outClusters: "0019,0003", manufacturer: "iMagic by GreatStar", model: "1117-S", deviceJoinName: "Iris Motion Sensor"	
	}

	simulator {
		status "active": "zone report :: type: 19 value: 0031"
		status "inactive": "zone report :: type: 19 value: 0030"
	}

	preferences {
		section {
			image(name: 'educationalcontent', multiple: true, images: [
				"http://cdn.device-gse.smartthings.com/Motion/Motion1.jpg",
				"http://cdn.device-gse.smartthings.com/Motion/Motion2.jpg",
				"http://cdn.device-gse.smartthings.com/Motion/Motion3.jpg"
			])
		}
		section {
			input "tempOffset", "number", title: "Temperature offset", description: "Select how many degrees to adjust the temperature.", range: "*..*", displayDuringSetup: false
			input "humidityOffset", "number", title: "Humidity offset", description: "Enter a percentage to adjust the humidity.", range: "*..*", displayDuringSetup: false
		}
	}

	tiles(scale: 2) {
		multiAttributeTile(name: "motion", type: "generic", width: 6, height: 4) {
			tileAttribute("device.motion", key: "PRIMARY_CONTROL") {
				attributeState "active", label: 'motion', icon: "st.motion.motion.active", backgroundColor: "#00A0DC"
				attributeState "inactive", label: 'no motion', icon: "st.motion.motion.inactive", backgroundColor: "#cccccc"
			}
		}
		valueTile("temperature", "device.temperature", width: 2, height: 2) {
			state("temperature", label: '${currentValue}°', unit: "F",
					backgroundColors: [
							[value: 31, color: "#153591"],
							[value: 44, color: "#1e9cbb"],
							[value: 59, color: "#90d2a7"],
							[value: 74, color: "#44b621"],
							[value: 84, color: "#f1d801"],
							[value: 95, color: "#d04e00"],
							[value: 96, color: "#bc2323"]
					]
			)
		}
		valueTile("humidity", "device.humidity", decoration: "flat", inactiveLabel: false, width: 2, height: 2) {
			state "humidity", label: '${currentValue}% humidity', unit: ""
		}
		valueTile("battery", "device.battery", decoration: "flat", inactiveLabel: false, width: 2, height: 2) {
			state "battery", label: '${currentValue}% battery', unit: ""
		}
		standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", action: "refresh.refresh", icon: "st.secondary.refresh"
		}

		main(["motion", "temperature"])
		details(["motion", "temperature", "humidity", "battery", "refresh"])
	}
}

private List<Map> collectAttributes(Map descMap) {
	List<Map> descMaps = new ArrayList<Map>()

	descMaps.add(descMap)

	if (descMap.additionalAttrs) {
		descMaps.addAll(descMap.additionalAttrs)
	}

	return  descMaps
}

def parse(String description) {
	log.debug "description: $description"
	Map map = zigbee.getEvent(description)
	if (!map) {
		if (description?.startsWith('zone status')) {
			map = parseIasMessage(description)
		} else {
			Map descMap = zigbee.parseDescriptionAsMap(description)

			if (descMap?.clusterInt == zigbee.POWER_CONFIGURATION_CLUSTER && descMap.commandInt != 0x07 && descMap.value) {
				log.info "BATT METRICS - attr: ${descMap?.attrInt}, value: ${descMap?.value}, decValue: ${Integer.parseInt(descMap.value, 16)}, currPercent: ${device.currentState("battery")?.value}, device: ${device.getDataValue("manufacturer")} ${device.getDataValue("model")}"
				List<Map> descMaps = collectAttributes(descMap)
				def battMap = descMaps.find { it.attrInt == 0x0020 }

				if (battMap) {
					map = getBatteryResult(Integer.parseInt(battMap.value, 16))
				}
			} else if (descMap?.clusterInt == 0x0500 && descMap.attrInt == 0x0002 && descMap.commandInt != 0x07) {
				def zs = new ZoneStatus(zigbee.convertToInt(descMap.value, 16))
				map = translateZoneStatus(zs)
			} else if (descMap?.clusterInt == zigbee.TEMPERATURE_MEASUREMENT_CLUSTER && descMap.commandInt == 0x07) {
				if (descMap.data[0] == "00") {
					log.debug "TEMP REPORTING CONFIG RESPONSE: $descMap"
					sendEvent(name: "checkInterval", value: 60 * 12, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID, offlinePingable: "1"])
				} else {
					log.warn "TEMP REPORTING CONFIG FAILED- error code: ${descMap.data[0]}"
				}
			} else if (descMap?.clusterInt == zigbee.IAS_ZONE_CLUSTER && descMap.attrInt == zigbee.ATTRIBUTE_IAS_ZONE_STATUS && descMap?.value) {
				map = translateZoneStatus(new ZoneStatus(zigbee.convertToInt(descMap?.value)))
			}
		}
	} else if (map.name == "temperature") {
		if (tempOffset) {
			map.value = (int) map.value + (int) tempOffset
		}
		map.descriptionText = temperatureScale == 'C' ? "${device.displayName} temperature was ${map.value}°C" : "${device.displayName} temperature was ${map.value}°F"
		map.translatable = true
	} else if (map.name == "humidity") {
		if (humidityOffset) {
			map.value = (int) map.value + (int) humidityOffset
		}
		map.descriptionText = "${device.displayName} humidity was ${map.value}%"
	}

	log.debug "Parse returned $map"
	def result = map ? createEvent(map) : [:]

	if (description?.startsWith('enroll request')) {
		List cmds = zigbee.enrollResponse()
		log.debug "enroll response: ${cmds}"
		result = cmds?.collect { new physicalgraph.device.HubAction(it) }
	}
	return result
}

private Map parseIasMessage(String description) {
	ZoneStatus zs = zigbee.parseZoneStatus(description)

	translateZoneStatus(zs)
}

private Map translateZoneStatus(ZoneStatus zs) {
	
	return (zs.isAlarm1Set() || zs.isAlarm2Set()) ? getMotionResult('active') : getMotionResult('inactive')
}

private Map getBatteryResult(rawValue) {
	log.debug "Battery rawValue = ${rawValue}"
	def linkText = getLinkText(device)

	def result = [:]

	def volts = rawValue / 10

	if (!(rawValue == 0 || rawValue == 255)) {
		result.name = 'battery'
		result.translatable = true
		def minVolts =  2.4
		def maxVolts =  2.7
		
		def curValVolts = Integer.parseInt(device.currentState("battery")?.value ?: "100") / 100.0
		
		curValVolts = curValVolts * (maxVolts - minVolts) + minVolts
		
		curValVolts = Math.round(10 * curValVolts) / 10.0
		
		
		
		
		if (state?.lastVolts == null || state?.lastVolts == volts || device.currentState("battery")?.value == null || Math.abs(curValVolts - volts) > 0.1) {
			def pct = (volts - minVolts) / (maxVolts - minVolts)
			def roundedPct = Math.round(pct * 100)
			if (roundedPct <= 0)
				roundedPct = 1
			result.value = Math.min(100, roundedPct)
		} else {
			
			result.value = device.currentState("battery").value
		}
		result.descriptionText = "${device.displayName} battery was ${result.value}%"
		state.lastVolts = volts
	}

	return result
}

private Map getMotionResult(value) {
	log.debug 'motion'
	String descriptionText = value == 'active' ? "${device.displayName} detected motion" : "${device.displayName} motion has stopped"
	return [
		name           : 'motion',
		value          : value,
		descriptionText: descriptionText,
		translatable   : true
	]
}


def ping() {
	zigbee.readAttribute(zigbee.IAS_ZONE_CLUSTER, zigbee.ATTRIBUTE_IAS_ZONE_STATUS)
}

def refresh() {
	log.debug "Refreshing Values"
	def refreshCmds = []

	refreshCmds += zigbee.readAttribute(zigbee.POWER_CONFIGURATION_CLUSTER, 0x0020) +
		zigbee.readAttribute(zigbee.RELATIVE_HUMIDITY_CLUSTER, 0x0000) +
		zigbee.readAttribute(zigbee.TEMPERATURE_MEASUREMENT_CLUSTER, 0x0000) +
		zigbee.readAttribute(zigbee.IAS_ZONE_CLUSTER, zigbee.ATTRIBUTE_IAS_ZONE_STATUS) +
		zigbee.enrollResponse()

	return refreshCmds
}

def configure() {
	
	
	sendEvent(name: "checkInterval", value: 2 * 60 * 60 + 1 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID, offlinePingable: "1"])

	log.debug "Configuring Reporting"
	
	
	
	
	def configCmds = []

	configCmds += zigbee.batteryConfig() +
		zigbee.temperatureConfig(30, 300) +
		zigbee.configureReporting(zigbee.RELATIVE_HUMIDITY_CLUSTER, 0x0000, DataType.UINT16, 30, 3600, 100)

	return refresh() + configCmds
}
