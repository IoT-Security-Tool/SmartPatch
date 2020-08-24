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
        log.error "something went wrong: $e"
    }
    if("$verify".contentEquals('false\n')){
        log.trace "event verification failed..."
        return
    }
