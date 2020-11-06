//begin - 4
//check whether sunset event happens twice in a short period of time
if (null != state.lastEventTime){ //if null, this is the first sunset since the SmartAPP is installed, go ahead and check the event time
    def timePeriod = state.timePeriod
    def allowHappenTime = null
    use( TimeCategory ) {
        allowHappenTime = Date.parseToStringDate("$state.lastEventTime") + timePeriod.minutes
    }
    if (Date.parseToStringDate("$evt.date") < allowHappenTime){
        log.trace "evt.date is $evt.date, last sunset time is $state.lastEventTime, less than $timePeriod minutes"
        return
    }
}
//begin - 4

//begin - 2
//check the evt.date whether it is near to getSunriseandSunset

def correctTime = getSunriseAndSunset().sunset

//when want to explictly let the event pass, set the groundtruth to current time
//correctTime = new Date()
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
    return
}
//end - 2

//begin - 5
//check OK, record the latest sunset event time
state.lastEventTime = evt.date.toString()
//end - 5

