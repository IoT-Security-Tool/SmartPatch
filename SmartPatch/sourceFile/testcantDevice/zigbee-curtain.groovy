"License"
"AS IS"
import physicalgraph.zigbee.zcl.DataType
import physicalgraph.zigbee.clusters.iaszone.ZoneStatus
metadata {
    definition(name: "Zigbee Curtain", namespace: "smartthings", author: "SmartThings", ocfDeviceType: "oic.d.blind") {
        capability "Actuator"
        capability "Configuration"
        capability "Refresh"
        capability "Health Check"
        capability "Switch Level"
        capability "Stateless Curtain Power Button"
        capability "Window Shade"

        
    }
}

private getCLUSTER_WINDOW_COVERING() { 0x0102 }
private getATTRIBUTE_CURRENT_LEVEL() { 0x0000 }


def parse(String description) {
    log.debug "description:- ${description}"
    def map = [:]
    def resultMap = zigbee.getEvent(description)
    log.debug "resultMap:- ${resultMap}"
    if (resultMap) {
        map = resultMap
    } else {
        Map descMap = zigbee.parseDescriptionAsMap(description)
        log.debug "descMap:- ${descMap}"
        if (descMap?.clusterInt == zigbee.LEVEL_CONTROL_CLUSTER && descMap.value) {
            def valueInt = Math.round((zigbee.convertHexToInt(descMap.value)) / 255 * 100)
            map = [name: "level", value: valueInt]
        }
    }
    if (map?.name == "level") {
        if (0 == map.value) {
            sendEvent(name: "windowShade", value: "closed")
        } else if (100 == map.value) {
            sendEvent(name: "windowShade", value: "open")
        } else {
            sendEvent(name: "windowShade", value: "partially open")
        }
        log.debug "map:- ${map}"
        sendEvent(map)
    }
}

def close() {
    log.info "close()"
    sendEvent(name: "windowShade", value: "closing")
    zigbee.command(CLUSTER_WINDOW_COVERING, 0x01)
}

def open() {
    log.info "open()"
    sendEvent(name: "windowShade", value: "opening")
    zigbee.command(CLUSTER_WINDOW_COVERING, 0x00)
}

def setLevel(data, rate=null) {
    log.info "setLevel()"

    if (data == null) {
        data = 100
    }
    Integer currentLevel = device.currentValue("level")
    Integer level = data as Integer
    if (level > currentLevel) {
        sendEvent(name: "windowShade", value: "opening")
    } else if (level < currentLevel) {
        sendEvent(name: "windowShade", value: "closing")
    }
    data = Math.round(data * 255 / 100)
    if (rate == null) {
        zigbee.command(zigbee.LEVEL_CONTROL_CLUSTER, 0x04, zigbee.convertToHexString(data, 2))
    } else {
        rate = (rate > 100) ? 100 : rate
        rate = convertToHexString(Math.round(rate * 255 / 100))
        command(zigbee.LEVEL_CONTROL_CLUSTER, 0x04, rate)
    }
}

def setButton(value){
    log.info "setButton ${value}"
    if (value == "pause") {
        pause()
    }
}

def pause() {
    log.info "pause()"
    zigbee.command(CLUSTER_WINDOW_COVERING, 0x02)
}


def ping() {
    return refresh()
}

def refresh() {
    log.info "refresh()"
    return zigbee.readAttribute(zigbee.LEVEL_CONTROL_CLUSTER, ATTRIBUTE_CURRENT_LEVEL)
}

def configure() {
    
    log.info "configure()"
    sendEvent(name: "checkInterval", value: 10 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
    sendEvent(name: "availableCurtainPowerButtons", value: ["pause"])
    return zigbee.levelConfig() + refresh()
}
