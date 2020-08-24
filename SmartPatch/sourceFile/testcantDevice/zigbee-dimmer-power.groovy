"License"
"AS IS"

metadata {
    definition (name: "ZigBee Dimmer Power", namespace: "smartthings", author: "SmartThings", ocfDeviceType: "oic.d.light", runLocally: true, minHubCoreVersion: '000.019.00012', executeCommandsLocally: true, genericHandler: "Zigbee") {
        capability "Actuator"
        capability "Configuration"
        capability "Refresh"
        capability "Power Meter"
        capability "Sensor"
        capability "Switch"
        capability "Switch Level"
        capability "Health Check"
        capability "Light"

        
        fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0B04"
        fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0702"

        
        fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0702, 0B05", outClusters: "000A, 0019", manufacturer: "Jasco Products", model: "45852", deviceJoinName: "GE Zigbee Plug-In Dimmer", ocfDeviceType: "oic.d.smartplug"
        fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0702, 0B05", outClusters: "000A, 0019", manufacturer: "Jasco Products", model: "45857", deviceJoinName: "GE Zigbee In-Wall Dimmer", ocfDeviceType: "oic.d.switch"

        
        fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0702, 0B05", outClusters: "0019", manufacturer: "sengled", model: "Z01-CIA19NAE26", deviceJoinName: "Sengled Element touch"
    }

    tiles(scale: 2) {
        multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
            tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'${name}', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#00A0DC", nextState:"turningOff"
                attributeState "off", label:'${name}', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff", nextState:"turningOn"
                attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#00A0DC", nextState:"turningOff"
                attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff", nextState:"turningOn"
            }
            tileAttribute ("device.level", key: "SLIDER_CONTROL") {
                attributeState "level", action:"switch level.setLevel"
            }
            tileAttribute ("power", key: "SECONDARY_CONTROL") {
                attributeState "power", label:'${currentValue} W'
            }
        }
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
        }
        main "switch"
        details(["switch", "refresh"])
    }
}


def parse(String description) {
    log.debug "description is $description"

    def event = zigbee.getEvent(description)
    if (event) {
        log.info event
        if (event.name == "power") {
            if (device.getDataValue("manufacturer") != "OSRAM") {       
                event.value = (event.value as Integer) / 10             
                sendEvent(event)
            }
        }
        else {
            sendEvent(event)
        }
    } else {
        def descMap = zigbee.parseDescriptionAsMap(description)
        if (descMap && descMap.clusterInt == 0x0006 && descMap.commandInt == 0x07) {
            if (descMap.data[0] == "00") {
                log.debug "ON/OFF REPORTING CONFIG RESPONSE: " + cluster
                sendEvent(name: "checkInterval", value: 60 * 12, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
            } else {
                log.warn "ON/OFF REPORTING CONFIG FAILED- error code:${cluster.data[0]}"
            }
        } else if (device.getDataValue("manufacturer") == "sengled" && descMap && descMap.clusterInt == 0x0008 && descMap.attrInt == 0x0000) {
            
            
            
            
            
            if (descMap.value.toUpperCase() == "FF") {
                descMap.value = "FE"
            }
            sendHubCommand(zigbee.command(zigbee.LEVEL_CONTROL_CLUSTER, 0x00, "FE0000").collect { new physicalgraph.device.HubAction(it) }, 0)
            sendEvent(zigbee.getEventFromAttrData(descMap.clusterInt, descMap.attrInt, descMap.encoding, descMap.value))
        } else {
            log.warn "DID NOT PARSE MESSAGE for description : $description"
            log.debug zigbee.parseDescriptionAsMap(description)
        }
    }
}

def off() {
    zigbee.off()
}

def on() {
    zigbee.on()
}

def setLevel(value, rate = null) {
    zigbee.setLevel(value) + (value?.toInteger() > 0 ? zigbee.on() : [])
}


def ping() {
    return zigbee.onOffRefresh()
}

def refresh() {
    def cmds = zigbee.onOffRefresh() + zigbee.levelRefresh() + zigbee.simpleMeteringPowerRefresh() + zigbee.electricMeasurementPowerRefresh()
    if (device.getDataValue("manufacturer") == "Jasco Products") {
        
        
        cmds += ["zdo bind 0x${device.deviceNetworkId} 2 1 0x0006 {${device.zigbeeId}} {${device.zigbeeId}}", "delay 2000",
                 "zdo bind 0x${device.deviceNetworkId} 2 1 0x0008 {${device.zigbeeId}} {${device.zigbeeId}}", "delay 2000"]
    }
    cmds + zigbee.onOffConfig(0, 300) + zigbee.levelConfig() + zigbee.simpleMeteringPowerConfig() + zigbee.electricMeasurementPowerConfig()
}

def configure() {
    log.debug "Configuring Reporting and Bindings."
    def cmds = []
    if (device.getDataValue("manufacturer") == "sengled") {
        def startLevel = 0xFE
        if ((device.currentState("level")?.value != null)) {
            startLevel = Math.round(Integer.parseInt(device.currentState("level").value) * 0xFE / 100)
        }
        
        cmds << zigbee.command(zigbee.LEVEL_CONTROL_CLUSTER, 0x00, sprintf("%02X0000", startLevel))
    }
    
    
    sendEvent(name: "checkInterval", value: 2 * 60 * 60 + 1 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
    cmds + refresh()
}
