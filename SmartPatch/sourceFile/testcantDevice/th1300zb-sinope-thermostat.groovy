"AS IS"

metadata {

preferences {
		input("AirFloorModeParam", "enum", title: "Control mode (Default: Floor)", description:"Control mode using the floor or ambient temperature.", options: ["Ambient", "Floor"], multiple: false, required: false)
        input("BacklightAutoDimParam", "enum", title:"Backlight setting (Default: Sensing)", description: "On Demand or Sensing", options: ["Sensing", "On Demand"], multiple: false, required: false)
    	input("KbdLockParam", "enum", title: "Keypad lock (Default: Unlocked)", description: "Enable or disable the device's buttons.",options: ["Lock", "Unlock"], multiple: false, required: false)
    	input("TimeFormatParam", "enum", title:"Time Format (Default: 24h)", description: "Time format displayed by the device.", options:["24h", "12h AM/PM"], multiple: false, required: false)
    	input("DisableOutdorTemperatureParam", "bool", title: "enable/disable outdoor temperature", description: "Set it to true to Disable outdoor temperature on the thermostat")
        input("FloorSensorTypeParam", "enum", title:"Probe type (Default: 10k)", description: "Choose probe type.", options: ["10k", "12k"], multiple: false, required: false)
    	input("FloorMaxAirTemperatureParam", "number", title:"Ambient limit (5C to 36C / 41F to 96)", range:("5..96"),
    		description: "The maximum ambient temperature limit when in floor control mode.", required: false)
        input("FloorLimitMinParam", "number", title:"Floor low limit (5C to 36C / 41F to 96F)", range:("5..96"), description: "The minimum temperature limit of the floor when in ambient control mode.", required: false)
    	input("FloorLimitMaxParam", "number", title:"Floor high limit (5C to 36C / 41F to 96F)", range:("5..96"), description: "The maximum temperature limit of the floor when in ambient control mode.", required: false)
    	input("AuxLoadParam", "number", title:"Auxiliary load value (Default: 0)", range:("0..65535"),
        	description: "Enter the power in watts of the heating element connected to the auxiliary output.", required: false)
    	input("trace", "bool", title: "Trace (Only for debugging)", description: "Set it to true to enable tracing")
		input("logFilter", "number", title: "Trace level", range: "1..5",
			description: "1= ERROR only, 2= <1+WARNING>, 3= <2+INFO>, 4= <3+DEBUG>, 5= <4+TRACE>")
}


	definition(name: "TH1300ZB Sinope Thermostat", namespace: "Sinope Technologies", author: "Sinope Technologies", ocfDeviceType: "oic.d.thermostat") {
		capability "thermostatHeatingSetpoint"
		capability "thermostatMode"
		capability "thermostatOperatingState"
		capability "thermostatSetpoint"
		capability "Actuator"
		capability "Temperature Measurement"
		capability "Configuration"
		capability "Refresh"
		capability "Sensor"
		capability "lock"

		attribute "temperatureDisplayMode", "enum", ["Deg_C", "Deg_F"]
		attribute "occupancyStatus", "enum", ["unoccupy", "occupy"]
		attribute "outdoorTemp", "string"
		attribute "heatingSetpointRange", "VECTOR3"
		attribute "verboseTrace", "string"
        attribute "gfciStatus", "enum", ["OK", "error"]
        attribute "floorLimitStatus", "enum", ["OK", "floorLimitLowReached", "floorLimitMaxReached", "floorAirLimitLowReached", "floorAirLimitMaxReached"]

		command "heatLevelUp"
		command "heatLevelDown"
		
		fingerprint manufacturer: "Sinope Technologies", model: "TH1300ZB", deviceJoinName: "Sinope Thermostat" 
	}

	
	tiles(scale: 2) {
		multiAttributeTile(name: "thermostatMulti", type: "thermostat", width: 6, height: 4, canChangeIcon: true) {
			tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
				attributeState("default", label: '${currentValue}', unit: "dF", backgroundColor: "#269bd2")
			}
			tileAttribute("device.heatingSetpoint", key: "VALUE_CONTROL") {
				attributeState("VALUE_UP", action: "heatLevelUp")
				attributeState("VALUE_DOWN", action: "heatLevelDown")
			}
			tileAttribute("device.heatingDemand", key: "SECONDARY_CONTROL") {
				attributeState("default", label: '${currentValue}%', unit: "%")
			}
			tileAttribute("device.thermostatOperatingState", key: "OPERATING_STATE") {
				attributeState("idle", backgroundColor: "#44b621")
				attributeState("heating", backgroundColor: "#ffa81e")
			}
			tileAttribute("device.thermostatMode", key: "THERMOSTAT_MODE") {
				attributeState("off", label: '${name}')
				attributeState("heat", label: '${name}')
			}
			tileAttribute("device.heatingSetpoint", key: "HEATING_SETPOINT") {
				attributeState("default", label: '${currentValue}', unit: "dF")
			}
		}

		

		standardTile("thermostatMode", "device.thermostatMode", inactiveLabel: false, height: 2, width: 2, decoration: "flat") {
			state "off", label: '', action: "heat", icon: "st.thermostat.heating-cooling-off"
			state "heat", label: '', action: "off", icon: "st.thermostat.heat", defaultState: true
		}

		standardTile("refresh", "device.temperature", inactiveLabel: false, width: 2, height: 2, decoration: "flat") {
			state "default", action: "refresh.refresh", icon: "st.secondary.refresh"
		}

        standardTile("gfciStatus", "device.gfciStatus", inactiveLabel: false, width: 2, height: 2, decoration: "flat") {
        	state "OK", label: "GFCI", backgroundColor: "#44b621"
            state "error", label: "GFCI", backgroundColor: "#bc2323"
        }
		standardTile("floorLimitStatus", "device.floorLimitStatus", inactiveLabel: false, width: 2, height: 2, decoration: "flat") {
			state "OK", label: 'Limit OK', backgroundColor: "#44b621"
            state "floorLimitLowReached", label: 'Limit low', backgroundColor: "#153591"
            state "floorLimitMaxReached", label: 'Limit high', backgroundColor: "#ff8133"
            state "floorAirLimitLowReached", label: 'Limit low', backgroundColor: "#153591"
            state "floorAirLimitMaxReached", label: 'Limit high', backgroundColor: "#ff8133"
		}

		
        controlTile("heatingSetpointSlider", "device.heatingSetpoint", "slider", sliderType: "HEATING", debouncePeriod: 1500, range: "device.heatingSetpointRange", width: 2, height: 2) {
            state "default", action:"setHeatingSetpoint", label:'${currentValue}${unit}', backgroundColor: "#E86D13"
        }
		

		main("thermostatMulti")
		details(["thermostatMulti",
        	"heatingSetpointSlider",
			"thermostatMode",
            "gfciStatus",
            "floorLimitStatus",
			"refresh"
		])
	}
}

def getBackgroundColors() {
	def results
	if (state?.scale == 'C') {
		
		results = [
			[value: 0, color: "#153591"],
			[value: 7, color: "#1e9cbb"],
			[value: 15, color: "#90d2a7"],
			[value: 23, color: "#44b621"],
			[value: 29, color: "#f1d801"],
			[value: 35, color: "#d04e00"],
			[value: 37, color: "#bc2323"]
		]
	} else {
		results =
			
			[
				[value: 31, color: "#153591"],
				[value: 44, color: "#1e9cbb"],
				[value: 59, color: "#90d2a7"],
				[value: 74, color: "#44b621"],
				[value: 84, color: "#f1d801"],
				[value: 95, color: "#d04e00"],
				[value: 96, color: "#bc2323"]
			]
	}
	return results
       
}






def installed() {
	traceEvent(settings.logFilter, "installed>Device is now Installed", settings.trace)
	initialize()
}

def updated() {
	   
  	if (!state.updatedLastRanAt || now() >= state.updatedLastRanAt + 15000) {
		state.updatedLastRanAt = now()  
		def cmds = []

		traceEvent(settings.logFilter, "updated>Device is now updated", settings.trace)
		try {
			unschedule()
		} catch (e) {
			traceEvent(settings.logFilter, "updated>exception $e, continue processing", settings.trace, get_LOG_ERROR())
		}
        
        runEvery15Minutes(refresh_misc)

        if(AirFloorModeParam == "Ambient"){
            traceEvent(settings.logFilter,"Set to Ambient mode",settings.trace)
            cmds += zigbee.writeAttribute(0xFF01, 0x0105, 0x30, 0x0001)
        }
        else{
            traceEvent(settings.logFilter,"Set to Floor mode",settings.trace)
            cmds += zigbee.writeAttribute(0xFF01, 0x0105, 0x30, 0x0002)
        }
        
        if(KbdLockParam == "Lock"){
            traceEvent(settings.logFilter,"device lock",settings.trace)
            lock()
        }
        else{
            traceEvent(settings.logFilter,"device unlock",settings.trace)
            unlock()
        }
        
        if(TimeFormatParam == "12h AM/PM"){
            traceEvent(settings.logFilter,"Set to 12h AM/PM",settings.trace)
            cmds += zigbee.writeAttribute(0xFF01, 0x0114, 0x30, 0x0001)
        }
        else{
            traceEvent(settings.logFilter,"Set to 24h",settings.trace)
            cmds += zigbee.writeAttribute(0xFF01, 0x0114, 0x30, 0x0000)
        }
        
        if(BacklightAutoDimParam == "On Demand"){	
            traceEvent(settings.logFilter,"Backlight on press",settings.trace)
            cmds += zigbee.writeAttribute(0x0201, 0x0402, 0x30, 0x0000)
        }
        else{
            traceEvent(settings.logFilter,"Backlight sensing",settings.trace)
            cmds += zigbee.writeAttribute(0x0201, 0x0402, 0x30, 0x0001)
        }
        
        if(FloorSensorTypeParam == "12k"){
            traceEvent(settings.logFilter,"Sensor type is 12k",settings.trace)
            cmds += zigbee.writeAttribute(0xFF01, 0x010B, 0x30, 0x0001)
        }
        else{
            traceEvent(settings.logFilter,"Sensor type is 10k",settings.trace)
            cmds += zigbee.writeAttribute(0xFF01, 0x010B, 0x30, 0x0000)
        }
        
		state?.scale = getTemperatureScale()

        if(FloorMaxAirTemperatureParam){
        	def MaxAirTemperatureValue
			traceEvent(settings.logFilter,"FloorMaxAirTemperature param. scale: ${state?.scale}, Param value: ${FloorMaxAirTemperatureParam}",settings.trace)
        	if(state?.scale == 'F')
            {
            	MaxAirTemperatureValue = fahrenheitToCelsius(FloorMaxAirTemperatureParam).toInteger()
            }
            else
            {
            	MaxAirTemperatureValue = FloorMaxAirTemperatureParam.toInteger()
            }
            MaxAirTemperatureValue = checkTemperature(MaxAirTemperatureValue)
            MaxAirTemperatureValue =  MaxAirTemperatureValue * 100
            cmds += zigbee.writeAttribute(0xFF01, 0x0108, 0x29, MaxAirTemperatureValue)
        }
        else{
			traceEvent(settings.logFilter,"FloorMaxAirTemperature: sending default value",settings.trace)
            cmds += zigbee.writeAttribute(0xFF01, 0x0108, 0x29, 0x8000)
        }

        if(FloorLimitMinParam){
        	def FloorLimitMinValue
			traceEvent(settings.logFilter,"FloorLimitMin param. scale: ${state?.scale}, Param value: ${FloorLimitMinParam}",settings.trace)
            if(state?.scale == 'F')
            {
            	FloorLimitMinValue = fahrenheitToCelsius(FloorLimitMinParam).toInteger()
            }
        	else
            {
            	FloorLimitMinValue = FloorLimitMinParam.toInteger()
            }
            FloorLimitMinValue = checkTemperature(FloorLimitMinValue)
            FloorLimitMinValue =  FloorLimitMinValue * 100
            cmds += zigbee.writeAttribute(0xFF01, 0x0109, 0x29, FloorLimitMinValue)
        }
        else{
			traceEvent(settings.logFilter,"FloorLimitMin: sending default value",settings.trace)
            cmds += zigbee.writeAttribute(0xFF01, 0x0109, 0x29, 0x8000)
        }
        
        if(FloorLimitMaxParam){
        	def FloorLimitMaxValue
			traceEvent(settings.logFilter,"FloorLimitMax param. scale: ${state?.scale}, Param value: ${FloorLimitMaxParam}",settings.trace)
            if(state?.scale == 'F')
            {
            	FloorLimitMaxValue = fahrenheitToCelsius(FloorLimitMaxParam).toInteger()
            }
            else
            {
            	FloorLimitMaxValue = FloorLimitMaxParam.toInteger()
            }
            FloorLimitMaxValue = checkTemperature(FloorLimitMaxValue)
            FloorLimitMaxValue =  FloorLimitMaxValue * 100
            cmds += zigbee.writeAttribute(0xFF01, 0x010A, 0x29, FloorLimitMaxValue)
        }
        else{
			traceEvent(settings.logFilter,"FloorLimitMax: sending default value",settings.trace)
            cmds += zigbee.writeAttribute(0xFF01, 0x010A, 0x29, 0x8000)
        }
        
        if(AuxLoadParam){
            def AuxLoadValue = AuxLoadParam.toInteger()
            cmds += zigbee.writeAttribute(0xFF01, 0x0118, 0x21, AuxLoadValue)
        }
        else{
            cmds += zigbee.writeAttribute(0xFF01, 0x0118, 0x21, 0x0000)
        }
        
		sendZigbeeCommands(cmds)
       	refresh_misc()
    }
    
}

void initialize() {
	state?.scale = getTemperatureScale()
    
	def supportedThermostatModes = ['off', 'heat']
	state?.supportedThermostatModes = supportedThermostatModes
	sendEvent(name: "supportedThermostatModes", value: supportedThermostatModes, displayed: (settings.trace ?: false))

	updated()
			 

    if(state?.scale == 'C')
    {
        sendEvent(name: "heatingSetpointRange", value: [5.0, 36.0], scale: scale)
    }
    else if(state?.scale == 'F')
    {
        sendEvent(name: "heatingSetpointRangeLow", value: [41,96], scale: scale)
    }

	sendEvent(name: "lock", value: "unlocked")

	
	
	def cmds = []
	cmds += zigbee.readAttribute(0x0204, 0x0000)	
	if (state?.scale == 'C') {
		cmds += zigbee.writeAttribute(0x0204, 0x0000, 0x30, 0)	
		sendEvent(name: "heatingSetpointRange", value: [5,36], scale: state.scale)

	} else {
		cmds += zigbee.writeAttribute(0x0204, 0x0000, 0x30, 1)	
		sendEvent(name: "heatingSetpointRange", value: [41,96], scale: state.scale)
	}
	cmds += zigbee.readAttribute(0x0201, 0x0000)	
	cmds += zigbee.readAttribute(0x0201, 0x0012)	
	cmds += zigbee.readAttribute(0x0201, 0x0008)	
	cmds += zigbee.readAttribute(0x0201, 0x001C)	
	cmds += zigbee.readAttribute(0x0204, 0x0001) 	
	cmds += zigbee.readAttribute(0xFF01, 0x0105)	
	cmds += zigbee.readAttribute(0xFF01, 0x0115)	

	cmds += zigbee.configureReporting(0x0201, 0x0000, 0x29, 19, 300, 25) 	
	cmds += zigbee.configureReporting(0x0201, 0x0008, 0x0020, 11, 301, 10) 	
	cmds += zigbee.configureReporting(0x0201, 0x0012, 0x0029, 8, 302, 40) 	
	cmds += zigbee.configureReporting(0xFF01, 0x0115, 0x30, 10, 3600, 1) 	
	cmds += zigbee.configureReporting(0xFF01, 0x010C, 0x30, 10, 3600, 1) 	

	sendZigbeeCommands(cmds)

}

def configure()
{
	traceEvent(settings.logFilter, "Configuring Reporting and Bindings", settings.trace, get_LOG_DEBUG())
	
    return sendEvent(name: "checkInterval", value: 30*60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
}

def ping() {
	refresh()
}

def uninstalled() {
	unschedule()
}




def parse(String description) {
    def result = []
	def scale = getTemperatureScale()
	state?.scale = scale
	traceEvent(settings.logFilter, "parse>Description :( $description )", settings.trace)
	def cluster = zigbee.parse(description)
	traceEvent(settings.logFilter, "parse>Cluster : $cluster", settings.trace)
	if (description?.startsWith("read attr -")) {
    	def descMap = zigbee.parseDescriptionAsMap(description)
        result += createCustomMap(descMap)
        if(descMap.additionalAttrs){
       		def mapAdditionnalAttrs = descMap.additionalAttrs
            mapAdditionnalAttrs.each{add ->
            	traceEvent(settings.logFilter,"parse> mapAdditionnalAttributes : ( ${add} )",settings.trace)
                add.cluster = descMap.cluster
                result += createCustomMap(add)
            }
        }
    }
	traceEvent(settings.logFilter, "Parse returned $result", settings.trace)
	return result
}



def createCustomMap(descMap){
	def result = null
    def map = [: ]
	def scale = getTemperatureScale()
		if (descMap.cluster == "0201" && descMap.attrId == "0000") {
			map.name = "temperature"
			map.value = getTemperatureValue(descMap.value, true)
            if(map.value > 158)
            {
            	map.value = "Sensor Error"
            }
            else
            {
            	if(scale == "C")
                {
                    
                    map.value = String.format( "%.1f", map.value )
                }
                else
                {
                	map.value = String.format( "%d", map.value )
                }
            }
			traceEvent(settings.logFilter, "parse>ACTUAL TEMP:  ${map.value}", settings.trace)
			sendEvent(name: map.name, value: map.value, unit: scale)
			sendEvent(name: "checkInterval", value: 30*60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
		} 
		else if (descMap.cluster == "0201" && descMap.attrId == "0008") {
			map.name = "heatingDemand"
			map.value = getHeatingDemand(descMap.value)
            sendEvent(name: map.name, value: map.value)
            traceEvent(settings.logFilter, "parse>${map.name}: ${map.value}")
			def operatingState = (map.value.toInteger() < 10) ? "idle" : "heating"
			sendEvent(name: "thermostatOperatingState", value: operatingState)
			traceEvent(settings.logFilter,"thermostatOperatingState: ${operatingState}", settings.trace)

		} 
		else if (descMap.cluster == "0201" && descMap.attrId == "0012") {
            map.name = "heatingSetpoint"
            map.value = getTemperatureValue(descMap.value, true)
            sendEvent(name: map.name, value: map.value, unit: scale)
            traceEvent(settings.logFilter, "parse>OCCUPY: ${map.name}: ${map.value}, scale: ${scale} ", settings.trace)
		} 
		else if (descMap.cluster == "0201" && descMap.attrId == "001c") {
			map.name = "thermostatMode"
			map.value = getModeMap()[descMap.value]
            sendEvent(name: map.name, value: map.value)
			traceEvent(settings.logFilter, "parse>${map.name}: ${map.value}", settings.trace)
		} 
		else if (descMap.cluster == "0204" && descMap.attrId == "0001") {
			map.name = "keypadLockStatus"
			map.value = getLockMap()[descMap.value]
			traceEvent(settings.logFilter, "parse>KEYPAD LOCK STATUS: ${map.value}", settings.trace)
			sendEvent(name: map.name, value: map.value)
		} 
		else if (descMap.cluster == "FF01" && descMap.attrId == "010c") {
			map.name = "floorLimitStatus"
			if(descMap.value.toInteger() == 0){
            	map.value = "OK"
            }else if(descMap.value.toInteger() == 1){
            	map.value = "floorLimitLowReached"
            }else if(descMap.value.toInteger() == 2){
            	map.value = "floorLimitMaxReached"
            }else if(descMap.value.toInteger() == 3){
            	map.value = "floorAirLimitMaxReached"
            }else{
            	map.value = "floorAirLimitMaxReached"
            }
			traceEvent(settings.logFilter, "parse>floorLimitStatus: ${map.value}", settings.trace)
			sendEvent(name: map.name, value: map.value)
		} 
		else if (descMap.cluster == "FF01" && descMap.attrId == "0115") {
			map.name = "gfciStatus"
			if(descMap.value.toInteger() == 0){
            	map.value = "OK"
            }else if(descMap.value.toInteger() == 1){
            	map.value = "error"
            }
			traceEvent(settings.logFilter, "parse>gfciStatus: ${map.value}", settings.trace)
			sendEvent(name: map.name, value: map.value)
		}

    return result
}



def getTemperatureValue(value, doRounding = false) {
	def scale = getTemperatureScale()
	if (value != null) {
		double celsius = (Integer.parseInt(value, 16) / 100).toDouble()
		if (scale == "C") {
			if (doRounding) {
				def tempValueString = String.format('%2.1f', celsius)
                if (tempValueString.matches(".*([.,][25-74])")) {
					tempValueString = String.format('%2d.5', celsius.intValue())
					traceEvent(settings.logFilter, "getTemperatureValue>value of $tempValueString which ends with 456=> rounded to .5", settings.trace)
                } else if (tempValueString.matches(".*([.,][75-99])")) {
					traceEvent(settings.logFilter, "getTemperatureValue>value of$tempValueString which ends with 789=> rounded to next .0", settings.trace)
					celsius = celsius.intValue() + 1
					tempValueString = String.format('%2d.0', celsius.intValue())
				} else {
					traceEvent(settings.logFilter, "getTemperatureValue>value of $tempValueString which ends with 0123=> rounded to previous .0", settings.trace)
					tempValueString = String.format('%2d.0', celsius.intValue())
				}
				return tempValueString.toDouble().round(1)
			} 
            else {
				return celsius.round(1)
			}

		} else {
			return Math.round(celsiusToFahrenheit(celsius))
		}
	}
}



def getHeatingDemand(value) {
	if (value != null) {
		def demand = Integer.parseInt(value, 16)
		return demand.toString()
	}
}



def heatLevelUp() {
	def scale = getTemperatureScale()
	double nextLevel

	if (scale == 'C') {
		nextLevel = device.currentValue("heatingSetpoint").toDouble()
		nextLevel = (nextLevel + 0.5).round(1)
		nextLevel = checkTemperature(nextLevel)
		setHeatingSetpoint(nextLevel)
	} else {
		nextLevel = device.currentValue("heatingSetpoint")
		nextLevel = (nextLevel + 1)
		nextLevel = checkTemperature(nextLevel)
		setHeatingSetpoint(nextLevel.intValue())
	}

}

def heatLevelDown() {
	def scale = getTemperatureScale()
	double nextLevel

	if (scale == 'C') {
		nextLevel = device.currentValue("heatingSetpoint").toDouble()
		nextLevel = (nextLevel - 0.5).round(1)
		nextLevel = checkTemperature(nextLevel)
		setHeatingSetpoint(nextLevel)
	} else {
		nextLevel = device.currentValue("heatingSetpoint")
		nextLevel = (nextLevel - 1)
		nextLevel = checkTemperature(nextLevel)
		setHeatingSetpoint(nextLevel.intValue())
	}
}

def setHeatingSetpoint(degrees) {
	def scale = getTemperatureScale()
	degrees = checkTemperature(degrees)
	def degreesDouble = degrees as Double
	String tempValueString
	if (scale == "C") {
		tempValueString = String.format('%2.1f', degreesDouble)
	} else {
		tempValueString = String.format('%2d', degreesDouble.intValue())
	}
	sendEvent("name": "heatingSetpoint", "value": tempValueString, scale: scale)
	sendEvent("name": "thermostatSetpoint", "value": tempValueString, scale: scale)
	traceEvent(settings.logFilter, "setHeatingSetpoint> new setPoint: $tempValueString", settings.trace)
	def celsius = (scale == "C") ? degreesDouble : (fahrenheitToCelsius(degreesDouble) as Double).round(1)
	def cmds = []
	cmds += zigbee.writeAttribute(0x201, 0x12, 0x29, hex(celsius * 100))
	sendZigbeeCommands(cmds)
}


void off() {
	setThermostatMode('off')
}
void auto() {
	setThermostatMode('auto')
}
void heat() {
	setThermostatMode('heat')
}
void emergencyHeat() {
	setThermostatMode('heat')
}
void cool() {
	setThermostatMode('cool')
}


def modes() {
	["mode_off", "mode_heat"]
}

def getModeMap() {
	[
		"00": "off",
		"04": "heat"
	]
}

def getSupportedThermostatModes() {

	if (!state?.supportedThermostatModes) {
		state?.supportedThermostatModes = (device.currentValue("supportedThermostatModes")) ?
			device.currentValue("supportedThermostatModes").toString().minus('[').minus(']').tokenize(',') : ['off', 'heat']
	}
	return state?.supportedThermostatModes
}

def setThermostatMode(mode) {
	traceEvent(settings.logFilter, "setThermostatMode>switching thermostatMode", settings.trace)
	mode = mode?.toLowerCase()
	def supportedThermostatModes = getSupportedThermostatModes()

	if (mode in supportedThermostatModes) {
 		"mode_$mode" ()
	} else {
		traceEvent(settings.logFilter, "setThermostatMode to $mode is not supported by this thermostat", settings.trace, get_LOG_WARN())
	}
}

def mode_off() {
	traceEvent(settings.logFilter, "off>begin", settings.trace)
	sendEvent(name: "thermostatMode", value: "off", data: [supportedThermostatModes: getSupportedThermostatModes()])
 	def cmds = []
	cmds += zigbee.writeAttribute(0x0201, 0x001C, 0x30, 0)
	cmds += zigbee.readAttribute(0x0201, 0x0008)
	sendZigbeeCommands(cmds)
	traceEvent(settings.logFilter, "off>end", settings.trace)
}

def mode_heat() {
	traceEvent(settings.logFilter, "heat>begin", settings.trace)
	sendEvent(name: "thermostatMode", value: "heat", data: [supportedThermostatModes: getSupportedThermostatModes()])
 	def cmds = []
	cmds += zigbee.writeAttribute(0x0201, 0x001C, 0x30, 4)
	cmds += zigbee.readAttribute(0x0201, 0x0008)
	sendZigbeeCommands(cmds)
	traceEvent(settings.logFilter, "heat>end", settings.trace)
}


def keypadLockLevel() {
	["unlock", "lock"] 
}

def getLockMap() {
	[
		"00": "unlocked",
		"01": "locked",
	]
}

def unlock() {
	traceEvent(settings.logFilter, "unlock>begin", settings.trace)
	sendEvent(name: "lock", value: "unlocked")
	def cmds = []
	cmds += zigbee.writeAttribute(0x0204, 0x0001, 0x30, 0x00)
	sendZigbeeCommands(cmds)
	traceEvent(settings.logFilter, "unlock>end", settings.trace)
}

def lock() {
	traceEvent(settings.logFilter, "lock>begin", settings.trace)
	sendEvent(name: "lock", value: "locked")
	def cmds = []
	cmds += zigbee.writeAttribute(0x0204, 0x0001, 0x30, 0x01)
	sendZigbeeCommands(cmds)
	traceEvent(settings.logFilter, "lock>end", settings.trace)
}

def refresh() {
	if (!state.updatedLastRanAt || now() >= state.updatedLastRanAt + 20000) {			
		state.updatedLastRanAt = now() 
        
        state?.scale = getTemperatureScale()
        traceEvent(settings.logFilter, "refresh>scale=${state.scale}", settings.trace)
        def cmds = []

		def heatingSetpointRangeHigh
		def heatingSetpointRangeLow
		if(state?.scale == 'C')
		{
			heatingSetpointRangeLow = 5.0
			heatingSetpointRangeHigh = 36.0
		}
		else if(state?.scale == 'F')
		{
			heatingSetpointRangeLow = 41
			heatingSetpointRangeHigh = 96
		}
		def low = heatingSetpointRangeLow.toFloat().round(1)
		def high = heatingSetpointRangeHigh.toFloat().round(1)   
		def heatingSetpointRange= [low,high]
		sendEvent(name: "heatingSetpointRange", value: heatingSetpointRange, scale: state.scale)

        cmds += zigbee.readAttribute(0x0201, 0x0000)	
        cmds += zigbee.readAttribute(0x0201, 0x0012)	
        cmds += zigbee.readAttribute(0x0201, 0x0008)	
        cmds += zigbee.readAttribute(0x0201, 0x001C)	
        cmds += zigbee.readAttribute(0x0204, 0x0001) 	
        cmds += zigbee.readAttribute(0x0201, 0x0015)	
        cmds += zigbee.readAttribute(0x0201, 0x0016)	
        cmds += zigbee.readAttribute(0xFF01, 0x0105)	
		cmds += zigbee.readAttribute(0xFF01, 0x0115)	

        cmds += zigbee.configureReporting(0x0201, 0x0000, 0x29, 19, 300, 25) 	
        cmds += zigbee.configureReporting(0x0201, 0x0008, 0x0020, 11, 301, 10) 	
        cmds += zigbee.configureReporting(0x0201, 0x0012, 0x0029, 8, 302, 40) 	
        cmds += zigbee.configureReporting(0xFF01, 0x0115, 0x30, 10, 3600, 1) 	
        cmds += zigbee.configureReporting(0xFF01, 0x010C, 0x30, 10, 3600, 1) 	

        sendZigbeeCommands(cmds)
        refresh_misc()
    }
}

void refresh_misc() {

    def weather = get_weather()
	traceEvent(settings.logFilter,"refresh_misc>begin, settings.DisableOutdorTemperatureParam=${settings.DisableOutdorTemperatureParam}, weather=$weather", settings.trace)
	def cmds=[]

	if (weather) {
		double tempValue
        int outdoorTemp = weather.toInteger()
        if(state?.scale == 'F')
        {
        
        	outdoorTemp = fahrenheitToCelsius(outdoorTemp).toDouble().round()
        }
		int outdoorTempValue
		int outdoorTempToSend  
		
        if(!settings.DisableOutdorTemperatureParam)
        {
        	cmds += zigbee.writeAttribute(0xFF01, 0x0011, 0x21, 10800)
            if (outdoorTemp < 0) {
                outdoorTempValue = -outdoorTemp*100 - 65536
                outdoorTempValue = -outdoorTempValue
                outdoorTempToSend = zigbee.convertHexToInt(swapEndianHex(hex(outdoorTempValue)))
                cmds += zigbee.writeAttribute(0xFF01, 0x0010, 0x29, outdoorTempToSend, [mfgCode: 0x119C])
            } else {
                outdoorTempValue = outdoorTemp*100
                int tempa = outdoorTempValue.intdiv(256)
                int tempb = (outdoorTempValue % 256) * 256
                outdoorTempToSend = tempa + tempb
                cmds += zigbee.writeAttribute(0xFF01, 0x0010, 0x29, outdoorTempToSend, [mfgCode: 0x119C])
            }
        }
        else
        {
        	
            
        	cmds += zigbee.writeAttribute(0xFF01, 0x0011, 0x21, 30)
        }
		
        def mytimezone = location.getTimeZone()
        long dstSavings = 0
        if(mytimezone.useDaylightTime() && mytimezone.inDaylightTime(new Date())) {
          dstSavings = mytimezone.getDSTSavings()
		}
        
		long secFrom2000 = (((now().toBigInteger() + mytimezone.rawOffset + dstSavings ) / 1000) - (10957 * 24 * 3600)).toLong() 
		long secIndian = zigbee.convertHexToInt(swapEndianHex(hex(secFrom2000).toString())) 
		traceEvent(settings.logFilter, "refreshTime>myTime = ${secFrom2000}  reversed = ${secIndian}", settings.trace)
		cmds += zigbee.writeAttribute(0xFF01, 0x0020, 0x23, secIndian, [mfgCode: 0x119C])
        cmds += zigbee.readAttribute(0x0201, 0x001C)

	}

	if (state?.scale == 'C') {
		cmds += zigbee.writeAttribute(0x0204, 0x0000, 0x30, 0)	
	} else {
		cmds += zigbee.writeAttribute(0x0204, 0x0000, 0x30, 1)	
	}

	traceEvent(settings.logFilter,"refresh_misc> about to  refresh other misc variables, scale=${state.scale}", settings.trace)	
	sendZigbeeCommands(cmds)
	traceEvent(settings.logFilter,"refresh_misc>end", settings.trace)
 
}
 


void sendZigbeeCommands(cmds, delay = 1000) {
	cmds.removeAll { it.startsWith("delay") }
	
	cmds = cmds.collect { new physicalgraph.device.HubAction(it) }
	sendHubCommand(cmds, delay)
}


private def get_weather() {
	def mymap = getTwcConditions()
    traceEvent(settings.logFilter,"get_weather> $mymap",settings.trace)	
    def weather = mymap.temperature
    traceEvent(settings.logFilter,"get_weather> $weather",settings.trace)	
	return weather
}


private hex(value) {

	String hex=new BigInteger(Math.round(value).toString()).toString(16)
	traceEvent(settings.logFilter,"hex>value=$value, hex=$hex",settings.trace)	
	return hex
}

private String swapEndianHex(String hex) {
	reverseArray(hex.decodeHex()).encodeHex()
}

private byte[] reverseArray(byte[] array) {
	int i = 0;
	int j = array.length - 1;
	byte tmp;

	while (j > i) {
		tmp = array[j];
		array[j] = array[i];
		array[i] = tmp;
		j--;
		i++;
	}

	return array
}

private def checkTemperature(def number)
{
	def scale = getTemperatureScale()
	if(scale == 'F')
    {
    	if(number < 41)
        {
        	number = 41
        }
        else if(number > 96)
    	{
        	number = 96        }
    }
    else
    {
    	if(number < 5)
        {
        	number = 5
        }
        else if(number > 36)
    	{
        	number = 36
        }
    }
    return number
}

private int get_LOG_ERROR() {
	return 1
}
private int get_LOG_WARN() {
	return 2
}
private int get_LOG_INFO() {
	return 3
}
private int get_LOG_DEBUG() {
	return 4
}
private int get_LOG_TRACE() {
	return 5
}

def traceEvent(logFilter, message, displayEvent = false, traceLevel = 4, sendMessage = true) {
	int LOG_ERROR = get_LOG_ERROR()
	int LOG_WARN = get_LOG_WARN()
	int LOG_INFO = get_LOG_INFO()
	int LOG_DEBUG = get_LOG_DEBUG()
	int LOG_TRACE = get_LOG_TRACE()
	int filterLevel = (logFilter) ? logFilter.toInteger() : get_LOG_WARN()
    
	if ((displayEvent) || (sendMessage)) {
		def results = [
			name: "verboseTrace",
			value: message,
			displayed: ((displayEvent) ?: false)
		]

		if ((displayEvent) && (filterLevel >= traceLevel)) {
			switch (traceLevel) {
				case LOG_ERROR:
					log.error "${message}"
					break
				case LOG_WARN:
					log.warn "${message}"
					break
				case LOG_INFO:
					log.info "${message}"
					break
				case LOG_TRACE:
					log.trace "${message}"
					break
				case LOG_DEBUG:
				default:
					log.debug "${message}"
					break
			} 
			if (sendMessage) sendEvent(results)
		} 
	}
}
