// added codes begin - 2
def handlerAdded(evt){
    switch(evt.name){
        case "sunset":
            if(passSunsetCheck(evt)){
                state.PriorsunsetTime = evt.date.toString()
            }
            break
        case "sunrise":
            if(passSunriseCheck(evt)){
                state.PriorsunriseTime = evt.date.toString()
            }
            break
        case "position":
            if(passPositionCheck(evt.displayName)){
                state.PriorsunsetTime = evt.date.toString()
                state.PriorsunriseTime = evt.date.toString()
            }
            break
    }
}


def passSunsetCheck(evt){
    //return true

    //check whether sunset event happens twice in a short period of time
    if (null != state.lastsunset){ //if null, this is the first sunset since the SmartAPP is installed, go ahead and check the event time
        def timePeriod = state.timePeriod
        def allowHappenTime = null
        use( TimeCategory ) {
            allowHappenTime = Date.parseToStringDate("$state.lastsunset") + timePeriod.minutes
        }
        if (Date.parseToStringDate("$evt.date") < allowHappenTime){
            log.trace "evt.date is $evt.date, last sunset time is $state.lastsunset, less than $timePeriod minutes"
            return false
        }
    }

    //check the evt.date whether it is near to getSunriseandSunset

    def correctTime = getSunriseAndSunset().sunset

    correctTime = correctTime.toString()


    def offsetPositive = 5
    def offsetNegative = 0 - offsetPositive

    def correctTimewithoffsetPositive = null
    use( TimeCategory ) {
        correctTimewithoffsetPositive =  Date.parseToStringDate("$correctTime") + offsetPositive.minutes
    }

    def correctTimewithoffsetNegative = null
    use( TimeCategory ) {
        correctTimewithoffsetNegative = Date.parseToStringDate("$correctTime") + offsetNegative.minutes
    }



    if (evt.date > correctTimewithoffsetPositive || evt.date < correctTimewithoffsetNegative){
        log.trace "evt.date is $evt.date, the sunset event should happen near $correctTime (from $correctTimewithoffsetNegative to $correctTimewithoffsetPositive)"
        return false
    }


    //check OK, record the latest sunset event time
    state.lastsunset = evt.date.toString()
    retrun true
}

def passSunriseCheck(evt){
    //return true

    //check whether sunrise event happens twice in a short period of time
    if (null != state.lastsunrise){ //if null, this is the first sunrise since the SmartAPP is installed, go ahead and check the event time
        def timePeriod = state.timePeriod
        def allowHappenTime = null
        use( TimeCategory ) {
            allowHappenTime = Date.parseToStringDate("$state.lastsunrise") + timePeriod.minutes
        }
        if (Date.parseToStringDate("$evt.date") < allowHappenTime){
            log.trace "evt.date is $evt.date, last sunrise time is $state.lastsunrise, less than $timePeriod minutes"
            return false
        }
    }

    //check the evt.date whether it is near to getSunriseandSunset

    def correctTime = getSunriseAndSunset().sunrise

    correctTime = correctTime.toString()


    def offsetPositive = 5
    def offsetNegative = 0 - offsetPositive

    def correctTimewithoffsetPositive = null
    use( TimeCategory ) {
        correctTimewithoffsetPositive =  Date.parseToStringDate("$correctTime") + offsetPositive.minutes
    }

    def correctTimewithoffsetNegative = null
    use( TimeCategory ) {
        correctTimewithoffsetNegative = Date.parseToStringDate("$correctTime") + offsetNegative.minutes
    }



    if (evt.date > correctTimewithoffsetPositive || evt.date < correctTimewithoffsetNegative){
        log.trace "evt.date is $evt.date, the sunrise event should happen near $correctTime (from $correctTimewithoffsetNegative to $correctTimewithoffsetPositive)"
        return false
    }


    //check OK, record the latest sunrise event time
    state.lastsunrise = evt.date.toString()
    retrun true
}

def passPositionCheck(displayName){
    //return true
    if (displayName != location.name){
        log.trace "Position check fail: evt.displayName is displayName, it shoudl be ${location.name}"
        return false
    }
    return true
}

def sunsetoriseTimeCheck(name,date){
    //check ok, return true; otherwise returns false
    switch(name){
        case "sunsetTime":
            return isnearTime(date,state.PriorsunsetTime,name)
        case "sunriseTime":
            return isnearTime(date,state.PriorsunriseTime,name)
        default:
            log.trace "event name is not recognized in sunsetoriseTimeCheck: $name, this probably never happens"
            return false
    }
}

def isnearTime(currentTime, PriorTime,name){
    if(!PriorTime){
        log.trace "$name event fails: prior time is null"
        return false
    }
    //time of the prior event (postion, sunrise or sunset) should be less than timePast (set to 10) minutes before the current event

    def timePast = 10
    def PriorTimeADDtimePast = null
    use( TimeCategory ) {
        PriorTimeADDtimePast = Date.parseToStringDate("$PriorTime") + timePast.minutes
    }

    //log.debug "PriorTimeADDtimePast: $PriorTimeADDtimePast"
    //PriorTimeADDtimePast is the time of the moment which is timePast minutes after the prior event hapoens
    //if PriorTimeADDtimePast is still smaller than currentTime, then the sunriseTime/sunsetTime is probably spoofed

    //for example:


    // PriorTimeADDtimePast < currentTime, false
    //     PriorTime     timePast (10 minutes by default)  PriorTimeADDtimePast     currentTime
    //        |                                                    |                     |
    //sunrise event occured <------------------------------------->         sunriseTime event occurs
    //It has been too long, the the sunriseTime event is probably spoofed

    // PriorTimeADDtimePast >= currentTime, true
    //     PriorTime     timePast (10 minutes by default)   PriorTimeADDtimePast
    //        |                                                    |
    //sunrise event occured <------------------------------------->
    //                                     |
    //                                currentTime
    //                          sunriseTime event occurs
    //sunriseTime event occurs after the sunrise event with a reasonable delay, the sunriseTime event should be real


    if(PriorTimeADDtimePast < Date.parseToStringDate("$currentTime")){
        log.trace "it has been too long since last event (postion, sunrise or sunset)"
        return false
    }
    else{
        return true
    }
}

def valueCheck(name,value){
    def currentLocationSunriseandSunset = getSunriseAndSunset()
    def correctTime = null
    switch(name){
        case "sunsetTime":
            correctTime = currentLocationSunriseandSunset.sunset
            break
        case "sunriseTime":
            correctTime = currentLocationSunriseandSunset.sunrise
            break
        default:
            log.trace "event name is not recognized in valueCheck: $name, this probably never happens"
            return false
    }

    def correctTimeString = correctTime.toString()

    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    SimpleDateFormat outputFormat = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy")
    Date date = inputFormat.parse("$value")
    String eventValueString = outputFormat.format(date)

    //sunset sunrise time changes everyday
    //use offsetNegative and offsetPositive to cope with this
    //sunsetTime sunriseTime, the value is the next sunset sunrise, sometimes they are one day later
    //use offsetNegative+oneday offsetPositive+oneday to cope with this

    def offsetPositive = 10
    def offsetNegative = 0 - offsetPositive
    def oneday = 1

    def correctTimewithoffsetPositive = null
    use( TimeCategory ) {
        correctTimewithoffsetPositive = Date.parseToStringDate("$correctTimeString") + offsetPositive.minutes
    }

    def correctTimewithoffsetNegative = null
    use( TimeCategory ) {
        correctTimewithoffsetNegative = Date.parseToStringDate("$correctTimeString") + offsetNegative.minutes
    }

    def NextcorrectTimewithoffsetPositive = null
    use( TimeCategory ) {
        NextcorrectTimewithoffsetPositive = Date.parseToStringDate("$correctTimeString") + offsetPositive.minutes + oneday.days
    }

    def NextcorrectTimewithoffsetNegative = null
    use( TimeCategory ) {
        NextcorrectTimewithoffsetNegative = Date.parseToStringDate("$correctTimeString") + offsetNegative.minutes + oneday.days
    }

    log.trace "\n eventValueString: $eventValueString \n correctTimeString: $correctTimeString \n correctTimewithoffsetPositive: $correctTimewithoffsetPositive \n correctTimewithoffsetNegative: $correctTimewithoffsetNegative \n NextcorrectTimewithoffsetPositive: $NextcorrectTimewithoffsetPositive \n NextcorrectTimewithoffsetNegative: $NextcorrectTimewithoffsetNegative"

    def condition1_1 = correctTimewithoffsetNegative < Date.parseToStringDate("$eventValueString")
    def condition1_2 = Date.parseToStringDate("$eventValueString") < correctTimewithoffsetPositive
    def condition2_1 = NextcorrectTimewithoffsetNegative < Date.parseToStringDate("$eventValueString")
    def condition2_2 = Date.parseToStringDate("$eventValueString") < NextcorrectTimewithoffsetPositive

    if((condition1_1 && condition1_2) || (condition2_1 && condition2_2))
        return true
    else{
        log.trace "event value is wrong"
        return false
    }
}

def isTwice(name, currentTime){
    switch(name){
        case "sunsetTime":
            return issunsetTimeTwice(currentTime)
            break
        case "sunriseTime":
            return issunriseTimeTwice(currentTime)
            break
        default:
            log.trace "event name is not recognized in isTwice: $name"
            return true
    }
}

def issunsetTimeTwice(currentTime){
    if(!state.LastsunsetTime) //null, this is the first event, is not twice
        return false

    def timePeriod = state.timePeriod
    def allowHappenTime = null
    use( TimeCategory ) {
        allowHappenTime = Date.parseToStringDate("$state.LastsunsetTime") + timePeriod.minutes
    }
    if (Date.parseToStringDate(currentTime) < allowHappenTime){
        log.trace "evt.date is $currentTime, last sunrise time is $state.LastsunsetTime, less than $timePeriod minutes"
        return true
    }
    return false
}



def issunriseTimeTwice(currentTime){
    if(!state.LastsunriseTime) //null, this is the first event, is not twice
        return false

    def timePeriod = state.timePeriod
    def allowHappenTime = null
    use( TimeCategory ) {
        allowHappenTime = Date.parseToStringDate("$state.LastsunriseTime") + timePeriod.minutes
    }
    if (Date.parseToStringDate(currentTime) < allowHappenTime){
        log.trace "evt.date is $currentTime, last sunrise time is $state.LastsunriseTime, less than $timePeriod minutes"
        return true
    }
    return false
}



// added codes end - 2
