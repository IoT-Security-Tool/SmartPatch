"AS IS"

preferences {
	input("MinimalIntensityParam", "number", title:"Light bulb minimal intensity (1..10) (default: blank)", range:"1..10", description:"optional")
    
    

    input("LedIntensityParam", "number", title:"Indicator light intensity (1..100) (default: blank)", range:"1..100", description:"optional")
	input("trace", "bool", title: "Trace", description: "Set it to true to enable tracing")
	input("logFilter", "number", title: "Trace level", range: "1..5",
		description: "1= ERROR only, 2= <1+WARNING>, 3= <2+INFO>, 4= <3+DEBUG>, 5= <4+TRACE>")
}

metadata {
    definition (name: "DM2500ZB Sinope Dimmer", namespace: "Sinope Technologies", author: "Sinope Technologies",  ocfDeviceType: "oic.d.switch")
    {
        capability "Actuator"
        capability "Configuration"
        capability "Refresh"
        capability "Switch"
        capability "Switch Level"
        capability "Health Check"
        
        attribute "swBuild","string"
        
        fingerprint manufacturer: "Sinope Technologies", model: "DM2500ZB", deviceJoinName: "Sinope Dimmer Switch" 
    }

    tiles(scale: 2) 
    {
        multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true)
        {
            tileAttribute ("device.switch", key: "PRIMARY_CONTROL")
            {
                attributeState "on", label:'${name}', action:"switch.off", icon:"st.switches.light.on", backgroundColor:"#79b821", nextState:"turningOff"
                attributeState "off", label:'${name}', action:"switch.on", icon:"st.switches.light.off", backgroundColor:"#ffffff", nextState:"turningOn"
                attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.switches.light.on", backgroundColor:"#79b821", nextState:"turningOff"
                attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.switches.light.off", backgroundColor:"#ffffff", nextState:"turningOn"
            }
            tileAttribute ("device.level", key: "SLIDER_CONTROL") 
            {
                attributeState "level", action:"switch level.setLevel"
            }
        }

		standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat", width: 2, height: 2)
        {
            state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
        }
        main "switch"
        details(["switch","refresh"])
    }
}

def parse(String description)
{
    traceEvent(settings.logFilter, "description is $description", settings.trace, get_LOG_DEBUG())
    def event = zigbee.getEvent(description)
    traceEvent(settings.logFilter, "Event = $event", settings.trace, get_LOG_DEBUG())
	
    if(event)
    {
        if (event.name=="level" && event.value==0) {}
		else {
            traceEvent(settings.logFilter, "send event : $event", settings.trace, get_LOG_DEBUG())
			sendEvent(event)
            sendEvent(name: "checkInterval", value: 30*60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
		}
    }
    else
    {
        traceEvent(settings.logFilter, "DID NOT PARSE MESSAGE for description", settings.trace, get_LOG_WARN())
        if (description?.startsWith("read attr -")) 
        {
            def descMap = zigbee.parseDescriptionAsMap(description)
            def result = []
            result += createCustomMap(descMap)

            
            
            if(descMap.additionalAttrs)
            {
                def mapAdditionnalAttrs = descMap.additionalAttrs
                mapAdditionnalAttrs.each{add ->
                    traceEvent(settings.logFilter,"parse> mapAdditionnalAttributes : ( ${add} )",settings.trace)
                    add.cluster = descMap.cluster
                    result += createCustomMap(add)
                }
            }
        }
        else
        {
            traceEvent(settings.logFilter, "description did not start with 'read attr -'", settings.trace, get_LOG_WARN())
        }
    }
}

private def parseDescriptionAsMap(description)
{
    traceEvent(settings.logFilter, "parsing MAP ...", settings.trace, get_LOG_DEBUG())
	(description - "read attr - ").split(",").inject([:]) 
    {
    	map, param ->
		def nameAndValue = param.split(":")
		map += [(nameAndValue[0].trim()):nameAndValue[1].trim()]
	}
}

private def createCustomMap(descMap)
{
    def result = null
	def map = [:]

        if(descMap.cluster == "0000" && descMap.attrId == "0001")
        {
            traceEvent(settings.logFilter, "Parsing SwBuild Attribute", settings.trace, get_LOG_DEBUG())
            map.name = "swBuild"
            map.value = zigbee.convertHexToInt(descMap.value)
            sendEvent(name: map.name, value: map.value)
        }
    return result
}

def updated() {
    
	if (!state.updatedLastRanAt || now() >= state.updatedLastRanAt + 2000)
    {
		state.updatedLastRanAt = now()   
            
        def cmds = []
        if(checkSoftVersion() == true)
        {
            def MinLight = (MinimalIntensityParam)?MinimalIntensityParam.toInteger():0
            def Time = getTiming(MinLight)
            traceEvent(settings.logFilter, "Set timing to: $Time", settings.trace, get_LOG_DEBUG())
            cmds += zigbee.writeAttribute(0xff01, 0x0055, 0x21, Time)
                
        }
        else
        {
            traceEvent(settings.logFilter, "Minimal intensity is not supported by the device", settings.trace, get_LOG_DEBUG())
        }

        if(LedIntensityParam){
            cmds += zigbee.writeAttribute(0xff01, 0x0052, 0x20, LedIntensityParam)
            cmds += zigbee.writeAttribute(0xff01, 0x0053, 0x20, LedIntensityParam)
        }
        else{ 
            cmds += zigbee.writeAttribute(0xff01, 0x0052, 0x20, 50)
            cmds += zigbee.writeAttribute(0xff01, 0x0053, 0x20, 50)
        }
        sendZigbeeCommands(cmds)

	}
	else {
        traceEvent(settings.logFilter, "updated(): Ran within last 2 seconds so aborting", settings.trace, get_LOG_TRACE())
	}
    
}

def off()
{
    zigbee.off()
}

def on()
{
    zigbee.on()
}

def setLevel(level) 
{
    traceEvent(settings.logFilter, "setLevel value = $level", settings.trace, get_LOG_DEBUG())
    zigbee.setLevel(level,0)
}


def ping() {
	return zigbee.onOffRefresh()
}

def refresh()
{
	def cmds = []
    cmds += zigbee.readAttribute(0x0006, 0x0000) 
    cmds += zigbee.readAttribute(0x0008, 0x0000) 
    cmds += zigbee.readAttribute(0x0000, 0x0001) 
    cmds += zigbee.configureReporting(0x0006, 0x0000, 0x10, 0, 599, null) 
    cmds += zigbee.configureReporting(0x0008, 0x0000, 0x20, 3, 602, 0x01) 
    if(checkSoftVersion() == true){
    	cmds += zigbee.writeAttribute(0xff01, 0x0055, 0x21, getTiming((MinimalIntensityParam)?MinimalIntensityParam.toInteger():0))
    }
    return sendZigbeeCommands(cmds)
}

def configure()
{
    traceEvent(settings.logFilter, "Configuring Reporting and Bindings", settings.trace, get_LOG_DEBUG())

    
	sendEvent(name: "checkInterval", value: 2 * 10 * 60 + 1 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])

    return  zigbee.configureReporting(0x0006, 0x0000, 0x10, 0, 599, null) + 
            zigbee.configureReporting(0x0008, 0x0000, 0x20, 3, 602, 0x01) + 
            zigbee.readAttribute(0x0006, 0x0000) + 
            zigbee.readAttribute(0x0008, 0x0000) + 
            zigbee.readAttribute(0x0000, 0x0001) 
            
}




private int getTiming(def setting)
{
	def Timing
    	switch(setting)
    	{
    	case(1):
       		Timing = 100
       		break;
    	case(2):
       		Timing = 250
    		break;    
    	case(3):
       		Timing = 500
    		break;
    	case(4):
       		Timing = 750
    		break;
    	case(5):
       		Timing = 1000
    		break;
    	case(6):
       		Timing = 1250
    		break;
    	case(7):
       		Timing = 1500
    		break;
    	case(8):
       		Timing = 1750
    		break;
    	case(9):
       		Timing = 2000
    		break;
    	case(10):
       		Timing = 2250
    		break;
    	default:
       		Timing = 600
       		break;
    	}
        return Timing
}

private boolean checkSoftVersion()
{
	def version
    def versionMin = "106" 
    def Build = device.currentState("swBuild")?.value
    traceEvent(settings.logFilter, "soft version: $Build", settings.trace, get_LOG_DEBUG())
    
    if(Build > versionMin)
    {
        traceEvent(settings.logFilter, "intensity supported", settings.trace, get_LOG_DEBUG())
    	version = true
    }
    else
    {
        traceEvent(settings.logFilter, "intensity not supported", settings.trace, get_LOG_DEBUG())
        version = false
    }
    return version
}


private void sendZigbeeCommands(cmds, delay = 1000) {
	cmds.removeAll { it.startsWith("delay") }
	
	cmds = cmds.collect { new physicalgraph.device.HubAction(it) }
	sendHubCommand(cmds, delay)
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
