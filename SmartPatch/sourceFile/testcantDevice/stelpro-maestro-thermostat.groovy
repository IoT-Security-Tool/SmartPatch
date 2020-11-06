"License"
"AS IS"

import physicalgraph.zigbee.zcl.DataType

metadata {
	definition (name: "Stelpro Maestro Thermostat", namespace: "stelpro", author: "Stelpro", ocfDeviceType: "oic.d.thermostat") {
		capability "Actuator"
		capability "Temperature Measurement"
		capability "Temperature Alarm"
		capability "Relative Humidity Measurement"
		capability "Thermostat"
		capability "Thermostat Mode"
		capability "Thermostat Operating State"
		capability "Thermostat Heating Setpoint"
		capability "Configuration"
		capability "Sensor"
		capability "Refresh"
		capability "Health Check"

		attribute "outsideTemp", "number"

		command "setOutdoorTemperature"
		command "quickSetOutTemp" 
		command "increaseHeatSetpoint"
		command "decreaseHeatSetpoint"
		command "parameterSetting"
		command "updateWeather"

		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0201, 0204, 0405", outClusters: "0003, 000A, 0402", manufacturer: "Stelpro", model: "MaestroStat", deviceJoinName: "Stelpro Thermostat" 
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0201, 0204, 0405", outClusters: "0003, 000A, 0402", manufacturer: "Stelpro", model: "SORB", deviceJoinName: "Stelpro Thermostat", mnmn: "SmartThings", vid: "SmartThings-smartthings-Stelpro_Orleans_Sonoma_Fan_Thermostat" 
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0201, 0204, 0405", outClusters: "0003, 000A, 0402", manufacturer: "Stelpro", model: "SonomaStyle", deviceJoinName: "Stelpro Thermostat", mnmn: "SmartThings", vid: "SmartThings-smartthings-Stelpro_Orleans_Sonoma_Fan_Thermostat" 
	}

	
	simulator { }

	preferences {
		section {
			input("lock", "enum", title: "Do you want to lock your thermostat's physical keypad?", options: ["No", "Yes"], defaultValue: "No", required: false, displayDuringSetup: false)
			input("heatdetails", "enum", title: "Do you want a detailed operating state notification?", options: ["No", "Yes"], defaultValue: "No", required: false, displayDuringSetup: true)
		}
		section {
			input title: "Outdoor Temperature", description: "To get the current outdoor temperature to display on your thermostat enter your zip code or postal code below and make sure that your SmartThings location has a Geolocation configured (typically used for geofencing).", displayDuringSetup: false, type: "paragraph", element: "paragraph"
			input("zipcode", "text", title: "ZipCode (Outdoor Temperature)", description: "[Do not use space](Blank = No Forecast)")
		}
		"away_setpoint""enum""Away setpoint""5""5.5""6""6.5""7""7.5""8""8.5""9""9.5""10""10.5""11""11.5""12""12.5""13""13.5""14""14.5""15""5.5""15.5""16""16.5""17""17.5""18""18.5""19""19.5""20""20.5""21""21.5""22""22.5""23""24""24.5""25""25.5""26""26.5""27""27.5""28""28.5""29""29.5""30""21""away_setpoint""enum""Away Setpoint""5""5.5""6""6.5""7""7.5""8""8.5""9""9.5""10""10.5""11""11.5""12""12.5""13""13.5""14""14.5""15""5.5""15.5""16""16.5""17""17.5""18""18.5""19""19.5""20""20.5""21""21.5""22""22.5""23""24""24.5""25""25.5""26""26.5""27""27.5""28""28.5""29""29.5""30""17""vacation_setpoint""enum""Vacation Setpoint""5""5.5""6""6.5""7""7.5""8""8.5""9""9.5""10""10.5""11""11.5""12""12.5""13""13.5""14""14.5""15""5.5""15.5""16""16.5""17""17.5""18""18.5""19""19.5""20""20.5""21""21.5""22""22.5""23""24""24.5""25""25.5""26""26.5""27""27.5""28""28.5""29""29.5""30""13""standby_setpoint""enum""Standby Setpoint""5""5.5""6""6.5""7""7.5""8""8.5""9""9.5""10""10.5""11""11.5""12""12.5""13""13.5""14""14.5""15""5.5""15.5""16""16.5""17""17.5""18""18.5""19""19.5""20""20.5""21""21.5""22""22.5""23""24""24.5""25""25.5""26""26.5""27""27.5""28""28.5""29""29.5""30""5"
	}

	tiles(scale : 2) {
		multiAttributeTile(name:"thermostatMulti", type:"thermostat", width:6, height:4, canChangeIcon: true) {
			tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
				attributeState("temperature", label:'${currentValue}°', icon: "st.alarm.temperature.normal")
			}
			tileAttribute("device.heatingSetpoint", key: "VALUE_CONTROL") {
				attributeState("VALUE_UP", action: "increaseHeatSetpoint")
				attributeState("VALUE_DOWN", action: "decreaseHeatSetpoint")
			}
			tileAttribute("device.thermostatOperatingState", key: "OPERATING_STATE") {
				attributeState("idle", backgroundColor:"#44b621")
				attributeState("heating", backgroundColor:"#ffa81e")
			}"device.thermostatMode""THERMOSTAT_MODE""home""away""vacation""standby"
			tileAttribute("device.heatingSetpoint", key: "HEATING_SETPOINT") {
				attributeState("heatingSetpoint", label:'${currentValue}°')
			}
			tileAttribute("device.humidity", key: "SECONDARY_CONTROL") {
				attributeState("humidity", label:'${currentValue}%', unit:"%", defaultState: true)
			}
		}
		"mode""device.thermostatMode""home""switchMode""away""http://cdn.device-icons.smartthings.com/Home/home2-icn@2x.png""away""switchMode""vacation""http://cdn.device-icons.smartthings.com/Home/home15-icn@2x.png""vacation""switchMode""standby""http://cdn.device-icons.smartthings.com/Transportation/transportation2-icn@2x.png""standby""switchMode""home"
		valueTile("humidity", "device.humidity", width: 2, height: 2) {
			state "humidity", label:'Humidity ${currentValue}%', backgroundColor:"#4286f4", defaultState: true
		}
		valueTile("heatingSetpoint", "device.heatingSetpoint", width: 2, height: 2) {
			state "heatingSetpoint", label:'Setpoint ${currentValue}°', backgroundColors:[
					
					[value: 0, color: "#153591"],
					[value: 7, color: "#1e9cbb"],
					[value: 15, color: "#90d2a7"],
					[value: 23, color: "#44b621"],
					[value: 28, color: "#f1d801"],
					[value: 35, color: "#d04e00"],
					[value: 37, color: "#bc2323"],
					
					[value: 40, color: "#153591"],
					[value: 44, color: "#1e9cbb"],
					[value: 59, color: "#90d2a7"],
					[value: 74, color: "#44b621"],
					[value: 84, color: "#f1d801"],
					[value: 95, color: "#d04e00"],
					[value: 96, color: "#bc2323"]
				]
		}
		standardTile("temperatureAlarm", "device.temperatureAlarm", decoration: "flat", width: 2, height: 2) {
			state "default", label: 'No Alarm', icon: "st.alarm.temperature.normal", backgroundColor: "#ffffff"
			state "cleared", label: 'No Alarm', icon: "st.alarm.temperature.normal", backgroundColor: "#ffffff"
			state "freeze", label: 'Freeze', icon: "st.alarm.temperature.freeze", backgroundColor: "#bc2323"
			state "heat", label: 'Overheat', icon: "st.alarm.temperature.overheat", backgroundColor: "#bc2323"
		}
		standardTile("refresh", "device.refresh", decoration: "flat", width: 2, height: 2) {
			state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
		standardTile("configure", "device.configure", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "configure", label:'', action:"configuration.configure", icon:"st.secondary.configure"
		}

		main ("thermostatMulti")
		details(["thermostatMulti", "humidity", "heatingSetpoint", "temperatureAlarm", "refresh", "configure"])
	}
}

def getTHERMOSTAT_CLUSTER() { 0x0201 }
def getATTRIBUTE_LOCAL_TEMP() { 0x0000 }
def getATTRIBUTE_PI_HEATING_STATE() { 0x0008 }
def getATTRIBUTE_HEAT_SETPOINT() { 0x0012 }
def getATTRIBUTE_SYSTEM_MODE() { 0x001C }
def getATTRIBUTE_MFR_SPEC_SETPOINT_MODE() { 0x401C }
def getATTRIBUTE_MFR_SPEC_OUT_TEMP() { 0x4001 }

def getTHERMOSTAT_UI_CONFIG_CLUSTER() { 0x0204 }
def getATTRIBUTE_TEMP_DISP_MODE() { 0x0000 }
def getATTRIBUTE_KEYPAD_LOCKOUT() { 0x0001 }

def getATTRIBUTE_HUMIDITY_INFO() { 0x0000 }


def getSupportedThermostatModes() {
	["heat"]
}

def getMinSetpointIndex() {
	0
}
def getMaxSetpointIndex() {
	1
}

def getThermostatSetpointRange() {
	(getTemperatureScale() == "C") ? [5, 30] : [41, 86]
}

def getHeatingSetpointRange() {
	thermostatSetpointRange
}

def getSetpointStep() {
	(getTemperatureScale() == "C") ? 0.5 : 1.0
}

def setupHealthCheck() {
	
	sendEvent(name: "checkInterval", value: 2 * 15 * 60 + 2 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
}

def configureSupportedRanges() {
	sendEvent(name: "supportedThermostatModes", value: supportedThermostatModes, displayed: false)
	
	sendEvent(name: "thermostatSetpointRange", value: thermostatSetpointRange, displayed: false)
	sendEvent(name: "heatingSetpointRange", value: heatingSetpointRange, displayed: false)
}

def installed() {
	sendEvent(name: "temperatureAlarm", value: "cleared", displayed: false)

	setupHealthCheck()

	configureSupportedRanges()
}

def updated() {
	def requests = []
	setupHealthCheck()

	configureSupportedRanges()

	unschedule(scheduledUpdateWeather)
	if (settings.zipcode) {
		state.invalidZip = false 
		requests += updateWeather()
		runEvery1Hour(scheduledUpdateWeather)
	}

	requests += parameterSetting()
	response(requests)
}

def parameterSetting() {
	def lockmode = null
	def valid_lock = false

	log.debug "lock : $settings.lock"
	if (settings.lock == "Yes") {
		lockmode = 0x01
		valid_lock = true
	} else if (settings.lock == "No") {
		lockmode = 0x00
		valid_lock = true
	}

	if (valid_lock) {
		log.debug "lock valid"
		zigbee.writeAttribute(THERMOSTAT_UI_CONFIG_CLUSTER, ATTRIBUTE_KEYPAD_LOCKOUT, DataType.ENUM8, lockmode) +
			poll()
	} else {
		log.debug "nothing valid"
	}
}

def parse(String description) {
	log.debug "Parse description $description"
	def map = [:]

	
	if (!device.currentValue("supportedThermostatModes")) {
		configureSupportedRanges()
	}
	
	if (device.currentValue("temperatureAlarm") == null) {
		sendEvent(name: "temperatureAlarm", value: "cleared", displayed: false)
	}

	if (description?.startsWith("read attr -") || description?.startsWith("catchall: ")) {
		def descMap = zigbee.parseDescriptionAsMap(description)
		log.debug "Desc Map: $descMap"
		if (descMap.clusterInt == THERMOSTAT_CLUSTER) {
			if (descMap.attrInt == ATTRIBUTE_LOCAL_TEMP) {
				map = handleTemperature(descMap)
			} else if (descMap.attrInt == ATTRIBUTE_HEAT_SETPOINT) {
				def intVal = Integer.parseInt(descMap.value, 16)
				
				if (intVal != 0x8000) {
					log.debug "HEATING SETPOINT"
					map.name = "heatingSetpoint"
					map.value = getTemperature(descMap.value)
					map.unit = getTemperatureScale()
					map.data = [heatingSetpointRange: heatingSetpointRange]
				}
			} else if (descMap.attrInt == ATTRIBUTE_PI_HEATING_STATE) {
				def intVal = Integer.parseInt(descMap.value, 16)
				log.debug "HEAT DEMAND"
				map.name = "thermostatOperatingState"
				if (intVal < 10) {
					map.value = "idle"
				} else {
					map.value = "heating"
				}

				
				
				if (settings.heatdetails == "No" ||
					!secondsPast(device.currentState("thermostatOperatingState")?.getLastUpdated(), 30)) {
					map.displayed = false
				}
			}
		} else if (descMap.clusterInt == zigbee.RELATIVE_HUMIDITY_CLUSTER) {
			if (descMap.attrInt == ATTRIBUTE_HUMIDITY_INFO) {
				def intVal = Integer.parseInt(descMap.value, 16)
				log.debug "DEVICE HUMIDITY"
				map.name = "humidity"
				map.value = intVal / 100
				map.units = "%"
			}
		}
	} else if (description?.startsWith("humidity")) {
		log.debug "DEVICE HUMIDITY"
		map.name = "humidity"
		map.value = (description - "humidity: " - "%").trim()
		map.units = "%"
	}

	def result = null
	if (map) {
		result = createEvent(map)
	}
	log.debug "Parse returned $map"
	return result
}

private getFREEZE_ALARM_TEMP() { getTemperatureScale() == "C" ? 0 : 32 }
private getHEAT_ALARM_TEMP() { getTemperatureScale() == "C" ? 50 : 122 }

def handleTemperature(descMap) {
	def map = [:]
	def intVal = Integer.parseInt(descMap.value, 16)

	
	if (intVal == 0x7ffd) { 
		map.name = "temperatureAlarm"
		map.value = "freeze"
		sendEvent(name: "temperature", value: FREEZE_ALARM_TEMP, unit: getTemperatureScale())
	} else if (intVal == 0x7fff) { 
		map.name = "temperatureAlarm"
		map.value = "heat"
		sendEvent(name: "temperature", value: HEAT_ALARM_TEMP, unit: getTemperatureScale())
	} else if (intVal == 0x8000) { 
		map.descriptionText = "Received a temperature error"
	} else {
		if (intVal > 0x8000) { 
			intVal = -(Math.round(2 * (65536 - intVal)) / 2)
		}
		map.name = "temperature"
		map.value = getTemperature(intVal)
		map.unit = getTemperatureScale()

		def lastTemp = device.currentValue("temperature")
		def lastAlarm = device.currentValue("temperatureAlarm")
		if (lastAlarm != "cleared") {
			def cleared = false

			if (lastTemp != null) {
				lastTemp = convertTemperatureIfNeeded(lastTemp, device.currentState("temperature").unit).toFloat()
				
				
				
				
				if ((lastAlarm == "freeze" &&
						map.value > FREEZE_ALARM_TEMP &&
						lastTemp < map.value) ||
					(lastAlarm == "heat" &&
						map.value < HEAT_ALARM_TEMP &&
						lastTemp > map.value)) {
							log.debug "Clearing $lastAlarm temp alarm"
							sendEvent(name: "temperatureAlarm", value: "cleared")
							cleared = true
				}
			}

			
			
			if (!cleared &&
					((lastAlarm == "freeze" && map.value > FREEZE_ALARM_TEMP) ||
					 (lastAlarm == "heat" && map.value < HEAT_ALARM_TEMP))) {
				log.debug "Hiding stale temperature ${map.value} because of ${lastAlarm} alarm"
				map.value = (lastAlarm == "freeze") ? FREEZE_ALARM_TEMP : HEAT_ALARM_TEMP
			}
		} else { 
			if (map.value <= FREEZE_ALARM_TEMP) {
				log.debug "EARLY FREEZE ALARM @ $map.value $map.unit (raw $intVal)"
				sendEvent(name: "temperatureAlarm", value: "freeze")
			} else if (map.value >= HEAT_ALARM_TEMP) {
				log.debug "EARLY HEAT ALARM @  $map.value $map.unit (raw $intVal)"
				sendEvent(name: "temperatureAlarm", value: "heat")
			}
		}
	}

	map
}

def updateWeather() {
	log.debug "updating weather"
	def weather
	
	if (settings.zipcode) {
		log.debug "ZipCode: ${settings.zipcode}"
		try {
			
			if (!state.invalidZip) {
				weather = getTwcConditions(settings.zipcode)
			}
		} catch (e) {
			log.debug "getTwcConditions exception: $e"
			
			state.invalidZip = true
		}

		if (!weather) {
			try {
				
				if (location?.latitude && location?.longitude) {
					
					weather = getTwcConditions(sprintf("%.2f,%.2f", location.latitude, location.longitude))
				}
			} catch (e2) {
				log.debug "getTwcConditions exception: $e2"
				weather = null
			}
		}

		
		if (!weather) {
			log.debug("Something went wrong, no data found.")
		} else {
			def locationScale = getTemperatureScale()
			def tempToSend = weather.temperature
			log.debug("Outdoor Temperature: ${tempToSend} ${locationScale}")
			
			
			
			setOutdoorTemperature(tempToSend)
		}
	}
}

def scheduledUpdateWeather() {
	def actions = updateWeather()

	if (actions) {
		sendHubCommand(actions)
	}
}


def ping() {
	log.debug "ping()"
	zigbee.readAttribute(THERMOSTAT_CLUSTER, ATTRIBUTE_LOCAL_TEMP)
}

def poll() {
	log.debug "poll()"
}

def refresh() {
	def requests = []
	log.debug "refresh()"

	requests += updateWeather()
	requests += zigbee.readAttribute(THERMOSTAT_CLUSTER, ATTRIBUTE_LOCAL_TEMP)

	if (!isOrleansOrSonoma()) {
		requests += zigbee.readAttribute(zigbee.RELATIVE_HUMIDITY_CLUSTER, ATTRIBUTE_HUMIDITY_INFO)
	}

	requests += zigbee.readAttribute(THERMOSTAT_CLUSTER, ATTRIBUTE_PI_HEATING_STATE)
	requests += zigbee.readAttribute(THERMOSTAT_CLUSTER, ATTRIBUTE_HEAT_SETPOINT)
	requests += zigbee.readAttribute(THERMOSTAT_UI_CONFIG_CLUSTER, ATTRIBUTE_TEMP_DISP_MODE)
	requests += zigbee.readAttribute(THERMOSTAT_UI_CONFIG_CLUSTER, ATTRIBUTE_KEYPAD_LOCKOUT)

	if (!isOrleansOrSonoma()) {
		requests += zigbee.readAttribute(zigbee.RELATIVE_HUMIDITY_CLUSTER, ATTRIBUTE_HUMIDITY_INFO)
	}

	requests
}


def getTemperature(value) {
	if (value != null) {
		log.debug("value $value")
		def celsius = (value instanceof String ? Integer.parseInt(value, 16) : value) / 100
		if (getTemperatureScale() == "C") {
			return celsius
		} else {
			def rounded = new BigDecimal(celsiusToFahrenheit(celsius)).setScale(0, BigDecimal.ROUND_HALF_UP)
			return rounded
		}
	}
}

def setHeatingSetpoint(preciseDegrees) {
	if (preciseDegrees != null) {
		def temperatureScale = getTemperatureScale()
		float minSetpoint = thermostatSetpointRange[minSetpointIndex]
		float maxSetpoint = thermostatSetpointRange[maxSetpointIndex]

		if (preciseDegrees >= minSetpoint && preciseDegrees <= maxSetpoint) {
			def degrees = new BigDecimal(preciseDegrees).setScale(1, BigDecimal.ROUND_HALF_UP)
			def celsius = (getTemperatureScale() == "C") ? degrees : (fahrenheitToCelsius(degrees) as Float).round(2)

			log.debug "setHeatingSetpoint({$degrees} ${temperatureScale})"

			zigbee.writeAttribute(THERMOSTAT_CLUSTER, ATTRIBUTE_HEAT_SETPOINT, DataType.INT16, zigbee.convertToHexString(celsius * 100, 4)) +
				zigbee.readAttribute(THERMOSTAT_CLUSTER, ATTRIBUTE_HEAT_SETPOINT) +
				zigbee.readAttribute(THERMOSTAT_CLUSTER, ATTRIBUTE_PI_HEATING_STATE)
		} else {
			log.debug "heatingSetpoint $preciseDegrees out of range! (supported: $minSetpoint - $maxSetpoint ${getTemperatureScale()})"
		}
	}
}


def quickSetOutTemp(outsideTemp) {
	setOutdoorTemperature(outsideTemp)
}

def setOutdoorTemperature(outsideTemp) {
	def degrees = outsideTemp as Double
	Integer tempToSend
	def celsius = (getTemperatureScale() == "C") ? degrees : (fahrenheitToCelsius(degrees) as Float).round(2)

	if (celsius < 0) {
		tempToSend = (celsius*100) + 65536
	} else {
		tempToSend = (celsius*100)
	}
	
	zigbee.writeAttribute(THERMOSTAT_CLUSTER, ATTRIBUTE_MFR_SPEC_OUT_TEMP, DataType.INT16, zigbee.swapEndianHex(zigbee.convertToHexString(tempToSend, 4)), ["mfgCode": "0x1185"])
}

def increaseHeatSetpoint() {
	def currentMode = device.currentState("thermostatMode")?.value
	if (currentMode != "off") {
		float currentSetpoint = device.currentValue("heatingSetpoint")

		currentSetpoint = currentSetpoint + setpointStep
		setHeatingSetpoint(currentSetpoint)
	}
}

def decreaseHeatSetpoint() {
	def currentMode = device.currentState("thermostatMode")?.value
	if (currentMode != "off") {
		float currentSetpoint = device.currentValue("heatingSetpoint")

		currentSetpoint = currentSetpoint - setpointStep
		setHeatingSetpoint(currentSetpoint)
	}
}

def setThermostatMode(value) {
	log.debug "setThermostatMode($value)"
	
}

def heat() {
	log.debug "heat"
	
	
}

def configure() {
	def requests = []

	unschedule(scheduledUpdateWeather)
	if (settings.zipcode) {
		state.invalidZip = false 
		requests += updateWeather()
		runEvery1Hour(scheduledUpdateWeather)
	}

	
	sendEvent("name":"thermostatMode", "value":"heat")

	log.debug "binding to Thermostat cluster"
	requests += zigbee.addBinding(THERMOSTAT_CLUSTER)
	
	requests += zigbee.configureReporting(THERMOSTAT_CLUSTER, ATTRIBUTE_LOCAL_TEMP, DataType.INT16, 10, 60, 50)
	requests += zigbee.configureReporting(THERMOSTAT_CLUSTER, ATTRIBUTE_HEAT_SETPOINT, DataType.INT16, 1, 0, 50)
	requests += zigbee.configureReporting(THERMOSTAT_CLUSTER, ATTRIBUTE_PI_HEATING_STATE, DataType.UINT8, 1, 900, 1)

	
	requests += zigbee.configureReporting(THERMOSTAT_UI_CONFIG_CLUSTER, ATTRIBUTE_TEMP_DISP_MODE, DataType.ENUM8, 1, 0, 1)
	requests += zigbee.configureReporting(THERMOSTAT_UI_CONFIG_CLUSTER, ATTRIBUTE_KEYPAD_LOCKOUT, DataType.ENUM8, 1, 0, 1)

	if (!isOrleansOrSonoma()) {
		requests += zigbee.configureReporting(zigbee.RELATIVE_HUMIDITY_CLUSTER, ATTRIBUTE_HUMIDITY_INFO, DataType.UINT16, 10, 300, 1)
	}
	
	requests += refresh()

	requests
}


def emergencyHeat() {
	log.debug "${device.displayName} does not support emergency heat mode"
}

def cool() {
	log.debug "${device.displayName} does not support cool mode"
}

def setCoolingSetpoint(degrees) {
	log.debug "${device.displayName} does not support cool setpoint"
}

def on() {
	heat()
}

def off() {
	log.debug "${device.displayName} does not support off"
}

def setThermostatFanMode(value) {
	log.debug "${device.displayName} does not support $value"
}

def fanOn() {
	log.debug "${device.displayName} does not support fan on"
}

def auto() {
	fanAuto()
}

def fanAuto() {
	log.debug "${device.displayName} does not support fan auto"
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

private Boolean isOrleansOrSonoma() {
	device.getDataValue("model") == "SORB" || device.getDataValue("model") == "SonomaStyle"
}
