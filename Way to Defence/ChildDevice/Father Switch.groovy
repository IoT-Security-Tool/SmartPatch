import java.util.UUID
metadata {

	definition (name: "Father Switch", namespace: "000", author: "SmartThings") {
		capability "Switch"
	}

	// simulator metadata
	simulator {

		// status messages

		status "on": "switch:on"

		status "off": "switch:off"

		// reply messages

		reply "on": "switch:on"

		reply "off": "switch:off"

	}

	// UI tile definitions

	tiles {

		standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {

			state "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"

			state "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#00A0DC"

		}

		main "switch"

		details "switch"

	}
}

def installed() {
    log.trace "Executing 'installed'"
    state.URL = "http://xxx.com" 
    state.PATH = "/DeviceServer/FirstServlet"
    state.ID0 = UUID.randomUUID().toString()
    initialize()
}

def updated() {
    log.trace "Executing 'updated'"
    initialize()
}

private initialize() {
    log.trace "Executing 'initialize'"
}

def parse(String description) {

}
def on() {

	//'on'
    log.debug "father on()"
    log.debug "ID0: $state.ID0"
    def params = null
    def signatureResult = null
    //log.debug "$device.id"
    params = [
        uri: state.URL,
        path: state.PATH,
        query: [ID0: state.ID0, deviceID: device.id, func: "sign", name: "switch", value: "on",isStateChange: true]
    ]
    
    signatureResult = null
    try {
        httpGet(params) { resp ->
            signatureResult = resp.data
            log.debug "response data: ${signatureResult}"
        }
    } catch (e) {
        log.error "something went wrong on(): $e"
    }

    sendEvent(name: "switch", value: "on",isStateChange: true, data: [sign: "${signatureResult}"])
}



def off() {

	//'off'
	 log.debug "father off()"
     sendEvent(name: "switch", value: "off")
}

def getSignature(name,value){
    def params = null
    def signatureResult = null
    log.debug "name: $name"
    log.debug "value: $value"
    params = [
        uri: state.URL,
        path: state.PATH,
        query: [ID0: state.ID0, deviceID: device.id, func: "sign", name: name, value: value,isStateChange: true]
    ]

    signatureResult = null
    try {
        httpGet(params) { resp ->
            signatureResult = resp.data
            log.debug "response data: ${signatureResult}"
        }
    } catch (e) {
        log.error "something went wrong on(): $e"
    }
    return signatureResult
}