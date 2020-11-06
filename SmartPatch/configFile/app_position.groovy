//begin
//check the evt.displayName whether it equals to the location name
//spoofed position event always has a displayName of "position"
//real    position event always has a displayName of "$location.name"
//if (evt.displayName == "position")
if (evt.displayName != location.name){
    log.trace "evt.displayName is $evt.displayName, it shoudl be ${location.name}"
    return
}
//end
