definition(
    name: "Childdevicesendevent",
    namespace: "0000",
    author: "unknown",
    description: "SmartAPP send device event with childdevice",
    category: "My apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	input "switch2", "capability.switch", required: true, title: "monitor which siwtch?"
}

def installed() {
 state.URL = "http://xxx.com" 
    state.PATH = "/DeviceServer/FirstServlet"
	log.debug "Installed...\n"
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	log.debug "initialize   ..."

    subscribe(switch2,"switch",handler)
}


def handler(evt){
	log.debug "child handler"
    def deviceNetworkID = "XXXXXX"
    def deviceType = "Father Switch"
    //def deviceType = "Simulated Switch"
    /*def tryaddDevice = addChildDevice("XXXspace", deviceType,deviceNetworkID)
    if (tryaddDevice){
    	def idofaddeddevice = tryaddDevice.getId()
    	log.debug "addChildDevice id is $idofaddeddevice"
    }
    else
    	log.debug "addchilddevice failed"*/
        
    /*def deviceAdded = getChildDevice(deviceNetworkID)
    if(deviceAdded){
    	def idofswitch2 = deviceAdded.getId()
        log.debug "deviceAdded.deviceId is $idofswitch2"
    }
    else{
    	log.debug "no device found"
    }*/
    //def DeviceId = "dfc4ac53-31c8-48f8-b305-d95cee1ff90c"
    def name = "switch"
    def value = "on"
    //def deviceID  = DeviceId
    def eventParams = null
    eventParams =[
    	name: name,
        value: value,
        isStateChange: true
        //source: "DEVICE",
        //deviceId: deviceID
        ]
    //sendEventTest(deviceAdded,eventParams)
   

    //sendEvent(name: "switch", value: "on",isStateChange: true, data: [sign: "${signatureResult}"])
    getChildDevices().each { deviceAdded ->
    		def idofswitch2 = deviceAdded.getId()
        	log.debug "deviceAdded.deviceId is $idofswitch2"
            def ID_test = deviceAdded.getID0()
            log.debug "deviceAdded.ID0 is $ID_test"
            /*def params = null
    		def signatureResult = null
    		params = [
        		uri: state.URL,
        		path: state.PATH,
        		query: [ID0: deviceAdded.getID0(), deviceID: idofswitch2, func: "sign", name: "switch", value: "on",isStateChange: true]
    		]
    
    		signatureResult = null
            try {
                httpGet(params) { resp ->
                    signatureResult = resp.data
                    log.debug "response data: ${signatureResult}"
                }
            } catch (e) {
                log.error "something went wrong on(): $e"
            }*/
            def signatureResult = null
            signatureResult = deviceAdded.getSignature("switch","on")
			deviceAdded.sendEvent(name: "switch", value: "on",data: [sign: "${signatureResult}"])
		}
    //deviceAdded.sendEvent(eventParams)
}