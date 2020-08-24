//log.trace "evt.descriptionText is $evt.descriptionText"
// added codes begin - 3

//does the sunriseTime or sunsetTime occurs twice at a short time
if(isTwice(evt.name,evt.date.toString()))
    return


//does the sunriseTime or sunsetTime occurs at the correct time
//resonable delay after sunrise/sunset/position event
if (!sunsetoriseTimeCheck(evt.name,evt.date.toString())){
    //log.debug "no position, sunrise or sunset event happens recently before $evt.name event hanppens"
    return
}

//
//the time of event passes verification
//now, let see whether the value is correct
if(!valueCheck(evt.name, evt.value)){
    return
}

//pass all check, record the time

switch(evt.name){
    case "sunsetTime":
        state.LastsunsetTime = evt.date.toString()
        break
    case "sunriseTime":
        state.LastsunriseTime = evt.date.toString()
        break
    default:
        log.trace "event name is not recognized in setting the time of last event: $evt.name, this probably never happens"
        return
}

// added codes end - 3
