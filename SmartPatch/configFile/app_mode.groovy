//begin
//check the evt.displayName whether it equals to the location name
//spoofed mode event always has a displayName of "mode"
//real    mode event always has a displayName of "$location.name"
//if (evt.displayName == "mode")
if (evt.displayName != location.name){
    log.trace "evt.displayName is $evt.displayName, it shoudl be ${location.name}"
    return
}
//end
