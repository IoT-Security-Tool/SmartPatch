"License"
"AS IS"

metadata {
    definition (name: "Cree Bulb", namespace: "smartthings", author: "SmartThings", ocfDeviceType: "oic.d.light", runLocally: true, executeCommandsLocally: true, minHubCoreVersion: "000.022.0004") {

        capability "Actuator"
        capability "Configuration"
        capability "Refresh"
        capability "Switch"
        capability "Switch Level"
        capability "Health Check"
        capability "Light"

        fingerprint manufacturer: "CREE", model: "Connected A-19 60W Equivalent" , deviceJoinName: "Cree Light"
    }

    
    simulator {
        
        status "on": "on/off: 1"
        status "off": "on/off: 0"

        
        reply "zcl on-off on": "on/off: 1"
        reply "zcl on-off off": "on/off: 0"
    }

    
    tiles(scale: 2) {
        multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
            tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'${name}', action:"switch.off", icon:"st.switches.light.on", backgroundColor:"#00A0DC", nextState:"turningOff"
                attributeState "off", label:'${name}', action:"switch.on", icon:"st.switches.light.off", backgroundColor:"#ffffff", nextState:"turningOn"
                attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.switches.light.on", backgroundColor:"#00A0DC", nextState:"turningOff"
                attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.switches.light.off", backgroundColor:"#ffffff", nextState:"turningOn"
            }
            tileAttribute ("device.level", key: "SLIDER_CONTROL") {
                attributeState "level", action:"switch level.setLevel"
            }
        }
        standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
        }
        main "switch"
        details(["switch", "refresh"])
    }
}


def parse(String description) {
    log.debug "description is $description"

    def resultMap = zigbee.getEvent(description)
    if (resultMap) {
        sendEvent(resultMap)
    }
    else {
        log.debug "DID NOT PARSE MESSAGE for description : $description"
        log.debug zigbee.parseDescriptionAsMap(description)
    }
}

def off() {
    zigbee.off()
}

def on() {
    zigbee.on()
}

def setLevel(value, rate = null) {
    zigbee.setLevel(value) + zigbee.onOffRefresh() + zigbee.levelRefresh()       
}


def ping() {
    return zigbee.levelRefresh()
}

def refresh() {
    zigbee.onOffRefresh() + zigbee.levelRefresh()
}

def healthPoll() {
    log.debug "healthPoll()"
    def cmds = zigbee.onOffRefresh() + zigbee.levelRefresh()
    cmds.each{ sendHubCommand(new physicalgraph.device.HubAction(it))}
}

def configure() {
    unschedule()
    runEvery5Minutes("healthPoll")
    
    sendEvent(name: "checkInterval", value: 60 * 12, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
    zigbee.onOffRefresh() + zigbee.levelRefresh()
}
