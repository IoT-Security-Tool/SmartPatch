//before each sendEvent, collects the fileds of event, both the field names and field values
//then send the ID0 and event fields to the web server for signature
//query: copy the parameters in the sendEvent, and add the ID0 and deviceID into it

//get the ID0 of child device, if new child device, genetate the IDO and store in the state.IDMap
currentChildID = deviceAdded2.getId()
if(state.IDMap.containsKey(currentChildID))
    currentChildID0 = state.IDMap."$currentChildID"
else{
    currentChildID0 = UUID.randomUUID().toString()
    state.IDMap."$currentChildID" = currentChildID0
}
log.trace "state.IDMap is : $state.IDMap"

params = [
        uri: state.URL,
        path: state.PATH,
        query: [ID0: currentChildID0, deviceID: currentChildID, func: "sign", name: "$sendeventName",value:"$sendeventValue",descriptionText: "spoof event $sendeventName $sendeventValue", isStateChange: true]
]

//get the signature from the external web server
signature = null
try {
    httpGet(params) { resp ->
        signature = resp.data
        log.debug "response data: ${signature}"
    }
} catch (e) {
    log.error "something went wrong: $e"
}
