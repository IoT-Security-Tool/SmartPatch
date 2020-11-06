/**
 *
 *  Copyright 2019 
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
 import java.util.UUID
definition(
    name: "Subscribe Child",
    namespace: "000",
    author: "unknown",
    description: "test child device",
    category: "",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    section("Switch00:") {
        input "theswitch00", "capability.switch", required: true
    }
}

def installed() {
	state.URL = "http://xxx.com" 
    state.PATH = "/DeviceServer/FirstServlet"
    state.ID_handler = UUID.randomUUID().toString()
	log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	initialize()
}

def initialize() {
    subscribe(theswitch00, "switch", switchesHandler00)
}

// TODO: implement event handlers
def switchesHandler00(evt) {
//collect the evt.name evt.value evt.deviceId, evt.id, ID_h, signature, ID_e, and send to external web server for verificaiton
    //also remember to put  func: "verify", into the query parameter
    
    //get signature from evt.data
    
    def startIndex = evt.data.indexOf("-sign-") 
    def endIndex = evt.data.indexOf("+sign+") 
    if(startIndex == -1 || endIndex == -1){
    	log.trace "No signatrue in event"
        return
    }
    def signString = evt.data.substring(startIndex + "-sign-".length(),endIndex)
    
    //get ID_e from evt.data
    startIndex = evt.data.indexOf("-IDe-") 
    endIndex = evt.data.indexOf("+IDe+") 
    if(startIndex == -1 || endIndex == -1){
    	log.trace "No IDe in event"
        return
    }
    def ID_e = evt.data.substring(startIndex + "-IDe-".length(),endIndex)
    

    log.debug "evt.data: $evt.data \n sing is: $signString \n IDe is $ID_e \n"

    def params = [
        uri: state.URL,
        path: state.PATH,
        query: [func: "verify", name: "$evt.name", value: "$evt.value", deviceID: "$evt.deviceId", ID_e: ID_e, ID_h: state.ID_handler, sign: signString]
    ]
    
    def verify = null
    //get the verify result from the external web server
    try {
        httpGet(params) { resp ->
            verify = resp.data
            log.debug "response data: ${resp.data}"
        }
    } catch (e) {
        log.error "something went wrong verify(): $e"
        return
    }

	
	if("$verify".contentEquals('false\n')){
    	log.trace "event verification failed..."
        return
    }
    
    log.debug "verify handler..."
    log.debug "the name of this event: $evt.name"
    log.debug "The device id for switch00: ${evt.deviceId}"
    log.debug "The source of this event is: ${evt.source}"
    log.debug "The value of this event as a string: ${evt.value}"
    log.debug "Is this event a state change? ${evt.isStateChange()}"
}