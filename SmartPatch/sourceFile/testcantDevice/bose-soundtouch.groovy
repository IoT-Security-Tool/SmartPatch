

"License"
"AS IS"


import groovy.xml.XmlUtil


metadata {
    definition (name: "Bose SoundTouch", namespace: "smartthings", author: "SmartThings") {
        
        capability "Switch"
        capability "Refresh"
        capability "Music Player"
        capability "Health Check"
        capability "Sensor"
        capability "Actuator"

        "refresh.refresh"
        command "preset1"
        command "preset2"
        command "preset3"
        command "preset4"
        command "preset5"
        command "preset6"
        command "aux"

        command "everywhereJoin"
        command "everywhereLeave"

        command "forceOff"
        command "forceOn"
    }

    
    valueTile("nowplaying", "device.nowplaying", width: 2, height: 1, decoration:"flat") {
        state "nowplaying", label:'${currentValue}', action:"refresh.refresh"
    }

    standardTile("switch", "device.switch", width: 1, height: 1, canChangeIcon: true) {
        state "on", label: '${name}', action: "forceOff", icon: "st.Electronics.electronics16", backgroundColor: "#00a0dc", nextState:"turningOff"
        state "turningOff", label:'TURNING OFF', icon:"st.Electronics.electronics16", backgroundColor:"#ffffff"
        state "off", label: '${name}', action: "forceOn", icon: "st.Electronics.electronics16", backgroundColor: "#ffffff", nextState:"turningOn"
        state "turningOn", label:'TURNING ON', icon:"st.Electronics.electronics16", backgroundColor:"#00a0dc"
    }
    valueTile("1", "device.station1", decoration: "flat", canChangeIcon: false) {
        state "station1", label:'${currentValue}', action:"preset1"
    }
    valueTile("2", "device.station2", decoration: "flat", canChangeIcon: false) {
        state "station2", label:'${currentValue}', action:"preset2"
    }
    valueTile("3", "device.station3", decoration: "flat", canChangeIcon: false) {
        state "station3", label:'${currentValue}', action:"preset3"
    }
    valueTile("4", "device.station4", decoration: "flat", canChangeIcon: false) {
        state "station4", label:'${currentValue}', action:"preset4"
    }
    valueTile("5", "device.station5", decoration: "flat", canChangeIcon: false) {
        state "station5", label:'${currentValue}', action:"preset5"
    }
    valueTile("6", "device.station6", decoration: "flat", canChangeIcon: false) {
        state "station6", label:'${currentValue}', action:"preset6"
    }
    valueTile("aux", "device.switch", decoration: "flat", canChangeIcon: false) {
        state "default", label:'Auxillary\nInput', action:"aux"
    }

    standardTile("refresh", "device.nowplaying", decoration: "flat", canChangeIcon: false) {
        state "default", label:'', action:"refresh", icon:"st.secondary.refresh"
    }

    controlTile("volume", "device.volume", "slider", height:1, width:3, range:"(0..100)") {
        state "volume", action:"music Player.setLevel"
    }

    standardTile("playpause", "device.playpause", decoration: "flat") {
        state "pause", label:'', icon:'st.sonos.play-btn', action:'music Player.play'
        state "play", label:'', icon:'st.sonos.pause-btn', action:'music Player.pause'
    }

    standardTile("prev", "device.switch", decoration: "flat", canChangeIcon: false) {
        state "default", label:'', action:"music Player.previousTrack", icon:"st.sonos.previous-btn"
    }
    standardTile("next", "device.switch", decoration: "flat", canChangeIcon: false) {
        state "default", label:'', action:"music Player.nextTrack", icon:"st.sonos.next-btn"
    }

    valueTile("everywhere", "device.everywhere", width:2, height:1, decoration:"flat") {
        state "join", label:"Join\nEverywhere", action:"everywhereJoin"
        state "leave", label:"Leave\nEverywhere", action:"everywhereLeave"
        
        state "unavailable", label:"Not Available"
    }

    
    main "switch"

    
    details ([
        "nowplaying", "refresh",        
        "prev", "playpause", "next",    
        "volume",                       
        "1", "2", "3",                  
        "4", "5", "6",                  
        "aux", "everywhere"])           
}


def off() {
    if (device.currentState("switch")?.value == "on") {
        onAction("off")
    }
}
def forceOff() {
    onAction("off")
}
def on() {
    if (device.currentState("switch")?.value == "off") {
        onAction("on")
    }
}
def forceOn() {
    onAction("on")
}
def volup() { onAction("volup") }
def voldown() { onAction("voldown") }
def preset1() { onAction("1") }
def preset2() { onAction("2") }
def preset3() { onAction("3") }
def preset4() { onAction("4") }
def preset5() { onAction("5") }
def preset6() { onAction("6") }
def aux() { onAction("aux") }
def refresh() { onAction("refresh") }
def setLevel(level) { onAction("volume", level) }
def play() { onAction("play") }
def pause() { onAction("pause") }
def mute() { onAction("mute") }
def unmute() { onAction("unmute") }
def previousTrack() { onAction("previous") }
def nextTrack() { onAction("next") }
def everywhereJoin() { onAction("ejoin") }
def everywhereLeave() { onAction("eleave") }



def parse(String event) {
    def data = parseLanMessage(event)
    def actions = []

    
    def handlers = [
        "nowPlaying" : "boseParseNowPlaying",
        "volume" : "boseParseVolume",
        "presets" : "boseParsePresets",
        "zone" : "boseParseEverywhere",
    ]

    
    if (!data.headers || !data.headers?."content-type".contains("xml"))
        return null

    
    prepareCallbacks()

    def xml = new XmlSlurper().parseText(data.body)
    
    handlers.each { node,func ->
        if (xml.name() == node)
            actions << "$func"(xml)
    }
    
    actions << processCallbacks(xml)

    
    if (actions.size() == 0) {
        log.warn "parse(): Unhandled data = " + lan
        return null
    }

    
    return actions.flatten()
}


def installed() {
    
    sendEvent(name: "checkInterval", value: 12 * 60, data: [protocol: "lan", hubHardwareId: device.hub.hardwareID], displayed: false)
    startPoll()
}


def ping() {
    TRACE("ping")
    boseSendGetNowPlaying()
}


def startPoll() {
    TRACE("startPoll")
    unschedule()
    
    def sec = Math.round(Math.floor(Math.random() * 60))
    
    def cron = "$sec 0/2 * * * ?" 
    log.debug "schedule('$cron', boseSendGetNowPlaying)"
    schedule(cron, boseSendGetNowPlaying)
}


def onAction(String user, data=null) {
    log.info "onAction(${user})"

    
    state.address = parent.resolveDNI2Address(device.deviceNetworkId)

    
    def actions = null
    switch (user) {
        case "on":
            boseSetPowerState(true)
            break
        case "off":
            boseSetNowPlaying(null, "STANDBY")
            boseSetPowerState(false)
            break
        case "volume":
            actions = boseSetVolume(data)
            break
        case "aux":
            boseSetNowPlaying(null, "AUX")
            boseZoneReset()
            sendEvent(name:"everywhere", value:"unavailable")
        case "1":
        case "2":
        case "3":
        case "4":
        case "5":
        case "6":
            actions = boseSetInput(user)
            break
        case "refresh":
            boseSetNowPlaying(null, "REFRESH")
            actions = [boseRefreshNowPlaying(), boseGetPresets(), boseGetVolume(), boseGetEverywhereState()]
            break
        case "play":
            actions = [boseSetPlayMode(true), boseRefreshNowPlaying()]
            break
        case "pause":
            actions = [boseSetPlayMode(false), boseRefreshNowPlaying()]
            break
        case "previous":
            actions = [boseChangeTrack(-1), boseRefreshNowPlaying()]
            break
        case "next":
            actions = [boseChangeTrack(1), boseRefreshNowPlaying()]
            break
        case "mute":
            actions = boseSetMute(true)
            break
        case "unmute":
            actions = boseSetMute(false)
            break
        case "ejoin":
            actions = boseZoneJoin()
            break
        case "eleave":
            actions = boseZoneLeave()
            break
        default:
            log.error "Unhandled action: " + user
    }

    
    if (actions instanceof List)
        return actions.flatten()
    return actions
}


def boseZoneJoin() {
    def results = []
    def posts = parent.boseZoneJoin(this)

    for (post in posts) {
        if (post['endpoint'])
            results << bosePOST(post['endpoint'], post['body'], post['host'])
    }
    sendEvent(name:"everywhere", value:"leave")
    results << boseRefreshNowPlaying()

    return results
}


def boseZoneLeave() {
    def results = []
    def posts = parent.boseZoneLeave(this)

    for (post in posts) {
        if (post['endpoint'])
            results << bosePOST(post['endpoint'], post['body'], post['host'])
    }
    sendEvent(name:"everywhere", value:"join")
    results << boseRefreshNowPlaying()

    return results
}


def boseZoneReset() {
    parent.boseZoneReset()
}


def boseParseNowPlaying(xmlData) {
    def result = []

    
    if (boseSetNowPlaying(xmlData)) {
        result << boseRefreshNowPlaying()
    }

    return result
}


def boseParseVolume(xmlData) {
    def result = []

    sendEvent(name:"volume", value:xmlData.actualvolume.text())
    sendEvent(name:"mute", value:(Boolean.toBoolean(xmlData.muteenabled.text()) ? "unmuted" : "muted"))

    return result
}


def boseParseEverywhere(xmlData) {
    
}


def boseParsePresets(xmlData) {
    def result = []

    state.preset = [:]

    def missing = ["1", "2", "3", "4", "5", "6"]
    for (preset in xmlData.preset) {
        def id = preset.attributes()['id']
        def name = preset.ContentItem.itemName[0].text().replaceAll(~/ +/, "\n")
        if (name == "##TRANS_SONGS##")
            name = "Local\nPlaylist"
        sendEvent(name:"station${id}", value:name)
        missing = missing.findAll { it -> it != id }

        
        state.preset["$id"] = XmlUtil.serialize(preset.ContentItem)
    }

    for (id in missing) {
        state.preset["$id"] = null
        sendEvent(name:"station${id}", value:"Preset $id\n\nNot set")
    }

    return result
}


def boseSetNowPlaying(xmlData, override=null) {
    def needrefresh = false
    def nowplaying = null

    if (xmlData && xmlData.playStatus) {
        switch(xmlData.playStatus) {
            case "BUFFERING_STATE":
                nowplaying = "Please wait\nBuffering..."
                needrefresh = true
                break
            case "PLAY_STATE":
                sendEvent(name:"playpause", value:"play")
                break
            case "PAUSE_STATE":
            case "STOP_STATE":
                sendEvent(name:"playpause", value:"pause")
                break
        }
    }

    
    if (!nowplaying) {
        nowplaying = ""
        switch (override ? override : xmlData.attributes()['source']) {
            case "AUX":
                nowplaying = "Auxiliary Input"
                break
            case "AIRPLAY":
                nowplaying = "Air Play"
                break
            case "STANDBY":
                nowplaying = "Standby"
                break
            case "INTERNET_RADIO":
                nowplaying = "${xmlData.stationName.text()}\n\n${xmlData.description.text()}"
                break
            case "REFRESH":
                nowplaying = "Please wait"
                break
            case "SPOTIFY":
            case "DEEZER":
            case "PANDORA":
            case "IHEART":
                if (xmlData.ContentItem.itemName[0])
                    nowplaying += "[${xmlData.ContentItem.itemName[0].text()}]\n\n"
            case "STORED_MUSIC":
                nowplaying += "${xmlData.track.text()}"
                if (xmlData.artist)
                    nowplaying += "\nby\n${xmlData.artist.text()}"
                if (xmlData.album)
                    nowplaying += "\n\n(${xmlData.album.text()})"
                break
            default:
                if (xmlData != null)
                    nowplaying = "${xmlData.ContentItem.itemName[0].text()}"
                else
                    nowplaying = "Unknown"
        }
    }

    
    if (xmlData) {
        if (xmlData.attributes()['source'] == "STANDBY") {
            log.trace "nowPlaying reports standby: " + XmlUtil.serialize(xmlData)
            sendEvent(name:"switch", value:"off")
        } else {
            sendEvent(name:"switch", value:"on")
        }
        boseSetPlayerAttributes(xmlData)
    }

    
    if (!parent.boseZoneHasMaster() && (override ? override : xmlData.attributes()['source']) == "STANDBY")
        sendEvent(name:"everywhere", value:"unavailable")
    else if ((override ? override : xmlData.attributes()['source']) == "AUX")
        sendEvent(name:"everywhere", value:"unavailable")
    else if (boseGetZone()) {
        log.info "We're in the zone: " + boseGetZone()
        sendEvent(name:"everywhere", value:"leave")
    } else
        sendEvent(name:"everywhere", value:"join")

    sendEvent(name:"nowplaying", value:nowplaying)

    return needrefresh
}


def boseSetPlayerAttributes(xmlData) {
    
    def trackText = ""
    def trackDesc = ""
    def trackData = [:]

    switch (xmlData.attributes()['source']) {
        case "STANDBY":
            trackData["station"] = trackText = trackDesc = "Standby"
            break
        case "AUX":
            trackData["station"] = trackText = trackDesc = "Auxiliary Input"
            break
        case "AIRPLAY":
            trackData["station"] = trackText = trackDesc = "Air Play"
            break
        case "SPOTIFY":
        case "DEEZER":
        case "PANDORA":
        case "IHEART":
        case "STORED_MUSIC":
            trackText = trackDesc = "${xmlData.track.text()}"
            trackData["name"] = xmlData.track.text()
            if (xmlData.artist) {
                trackText += " by ${xmlData.artist.text()}"
                trackDesc += " - ${xmlData.artist.text()}"
                trackData["artist"] = xmlData.artist.text()
            }
            if (xmlData.album) {
                trackText += " (${xmlData.album.text()})"
                trackData["album"] = xmlData.album.text()
            }
            break
        case "INTERNET_RADIO":
            trackDesc = xmlData.stationName.text()
            trackText = xmlData.stationName.text() + ": " + xmlData.description.text()
            trackData["station"] = xmlData.stationName.text()
            break
        default:
            trackText = trackDesc = xmlData.ContentItem.itemName[0].text()
    }

    sendEvent(name:"trackDescription", value:trackDesc, descriptionText:trackText)
}

"play everywhere"
def boseGetEverywhereState() {
    return boseGET("/getZone")
}


def boseKeypress(key) {
    def press = "<key state=\"press\" sender=\"Gabbo\">${key}</key>"
    def release = "<key state=\"release\" sender=\"Gabbo\">${key}</key>"

    return [bosePOST("/key", press), bosePOST("/key", release)]
}


def boseSetPlayMode(boolean play) {
    log.trace "Sending " + (play ? "PLAY" : "PAUSE")
    return boseKeypress(play ? "PLAY" : "PAUSE")
}


def boseSetVolume(int level) {
    def result = []
    int vol = Math.min(100, Math.max(level, 0))

    sendEvent(name:"volume", value:"${vol}")

    return [bosePOST("/volume", "<volume>${vol}</volume>"), boseGetVolume()]
}


def boseSetMute(boolean mute) {
    queueCallback('volume', 'cb_boseSetMute', mute ? 'MUTE' : 'UNMUTE')
    return boseGetVolume()
}


def cb_boseSetMute(xml, mute) {
    def result = []
    if ((xml.muteenabled.text() == 'false' && mute == 'MUTE') ||
        (xml.muteenabled.text() == 'true' && mute == 'UNMUTE'))
    {
        result << boseKeypress("MUTE")
    }
    log.trace("muteunmute: " + ((mute == "MUTE") ? "unmute" : "mute"))
    sendEvent(name:"muteunmute", value:((mute == "MUTE") ? "unmute" : "mute"))
    return result
}


def boseGetVolume() {
    return boseGET("/volume")
}


def boseChangeTrack(int direction) {
    if (direction < 0) {
        return boseKeypress("PREV_TRACK")
    } else if (direction > 0) {
        return boseKeypress("NEXT_TRACK")
    }
    return []
}


def boseSetInput(input) {
    log.info "boseSetInput(${input})"
    def result = []

    if (!state.preset) {
        result << boseGetPresets()
        queueCallback('presets', 'cb_boseSetInput', input)
    } else {
        result << cb_boseSetInput(null, input)
    }
    return result
}


def cb_boseSetInput(xml, input) {
    def result = []

    if (input >= "1" && input <= "6" && state.preset["$input"])
        result << bosePOST("/select", state.preset["$input"])
    else if (input.toLowerCase() == "aux") {
        result << boseKeypress("AUX_INPUT")
    }

    
    
    result << boseRefreshNowPlaying(3000)
    return result
}


def boseSetPowerState(boolean enable) {
    log.info "boseSetPowerState(${enable})"
    
    
    
    
    sendHubCommand(bosePOST("/key", "<key state=\"press\" sender=\"Gabbo\">POWER</key>")) 
    sendHubCommand(bosePOST("/key", "<key state=\"release\" sender=\"Gabbo\">POWER</key>"))
    sendHubCommand(boseGET("/now_playing"))
    if (enable) {
        queueCallback('nowPlaying', "cb_boseConfirmPowerOn", 5)
    }
}


def cb_boseSetPowerState(xml, state) {
    def result = []
    if ( (xml.attributes()['source'] == "STANDBY" && state == "POWERON") ||
         (xml.attributes()['source'] != "STANDBY" && state == "POWEROFF") )
    {
        result << boseKeypress("POWER")
        if (state == "POWERON") {
            result << boseRefreshNowPlaying()
            queueCallback('nowPlaying', "cb_boseConfirmPowerOn", 5)
        }
    }
    return result.flatten()
}


def cb_boseConfirmPowerOn(xml, tries) {
    def result = []
    def attempt = tries as Integer
    log.warn "boseConfirmPowerOn() attempt #$attempt"
    if (xml.attributes()['source'] == "STANDBY" && attempt > 0) {
        result << boseRefreshNowPlaying()
        queueCallback('nowPlaying', "cb_boseConfirmPowerOn", attempt-1)
    }
    return result
}


def boseRefreshNowPlaying(delay=0) {
    if (delay > 0) {
        return ["delay ${delay}", boseGET("/now_playing")]
    }
    return boseGET("/now_playing")
}

def boseSendGetNowPlaying() {
    sendHubCommand(boseGET("/now_playing"))
}


def boseGetPresets() {
    return boseGET("/presets")
}


def boseGET(String path) {
    new physicalgraph.device.HubAction([
        method: "GET",
        path: path,
        headers: [
            HOST: state.address + ":8090",
        ]])
}


def bosePOST(String path, String data, String address=null) {
    new physicalgraph.device.HubAction([
        method: "POST",
        path: path,
        body: data,
        headers: [
            HOST: address ?: (state.address + ":8090"),
        ]])
}


def queueCallback(String root, String func, param=null) {
    if (!state.pending)
        state.pending = [:]
    if (!state.pending[root])
        state.pending[root] = []
    state.pending[root] << ["$func":"$param"]
}


def prepareCallbacks() {
    if (!state.pending)
        return
    if (!state.ready)
        state.ready = [:]
    state.ready << state.pending
    state.pending = [:]
}


def processCallbacks(xml) {
    def result = []

    if (!state.ready)
        return result

    if (state.ready[xml.name()]) {
        state.ready[xml.name()].each { callback ->
            callback.each { func, param ->
                if (func != "func") {
                    if (param)
                    result << "$func"(xml, param)
                    else
                        result << "$func"(xml)
                }
            }
        }
        state.ready.remove(xml.name())
    }
    return result.flatten()
}


def boseSetZone(String newstate) {
    log.debug "boseSetZone($newstate)"
    state.zone = newstate

    
    if (newstate) {
        sendEvent(name:"everywhere", value:"leave")
    } else {
        sendEvent(name:"everywhere", value:"join")
    }
}


def boseGetZone() {
    return state.zone
}


def boseSetDeviceID(String devID) {
    state.deviceID = devID
}


def boseGetDeviceID() {
    return state.deviceID
}


def getDeviceIP() {
    return parent.resolveDNI2Address(device.deviceNetworkId)
}

def TRACE(text) {
    log.trace "${text}"
}
