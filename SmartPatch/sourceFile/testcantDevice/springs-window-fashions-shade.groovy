"License"
"AS IS"
import groovy.json.JsonOutput


metadata {
    definition (name: "Springs Window Fashions Shade", namespace: "smartthings", author: "SmartThings", ocfDeviceType: "oic.d.blind") {
        capability "Window Shade"
        capability "Window Shade Preset"
        capability "Battery"
        capability "Refresh"
        capability "Health Check"
        capability "Actuator"
        capability "Sensor"

        command "stop"

        capability "Switch Level"   

        
        


        fingerprint mfr:"026E", prod:"4353", model:"5A31", deviceJoinName: "Window Shade"
        fingerprint mfr:"026E", prod:"5253", model:"5A31", deviceJoinName: "Roller Shade"
    }

    simulator {
        status "open":  "command: 2603, payload: FF"
        status "closed": "command: 2603, payload: 00"
        status "10%": "command: 2603, payload: 0A"
        status "66%": "command: 2603, payload: 42"
        status "99%": "command: 2603, payload: 63"
        status "battery 100%": "command: 8003, payload: 64"
        status "battery low": "command: 8003, payload: FF"

        
        reply "2001FF,delay 1000,2602": "command: 2603, payload: 10 FF FE"
        reply "200100,delay 1000,2602": "command: 2603, payload: 60 00 FE"
        reply "200142,delay 1000,2602": "command: 2603, payload: 10 42 FE"
        reply "200163,delay 1000,2602": "command: 2603, payload: 10 63 FE"
    }

    tiles(scale: 2) {
        multiAttributeTile(name:"windowShade", type: "generic", width: 6, height: 4){
            tileAttribute ("device.windowShade", key: "PRIMARY_CONTROL") {
                attributeState "open", label:'${name}', action:"close", icon:"st.shades.shade-open", backgroundColor:"#79b821", nextState:"closing"
                attributeState "closed", label:'${name}', action:"open", icon:"st.shades.shade-closed", backgroundColor:"#ffffff", nextState:"opening"
                attributeState "partially open", label:'Open', action:"close", icon:"st.shades.shade-open", backgroundColor:"#79b821", nextState:"closing"
                attributeState "opening", label:'${name}', action:"stop", icon:"st.shades.shade-opening", backgroundColor:"#79b821", nextState:"partially open"
                attributeState "closing", label:'${name}', action:"stop", icon:"st.shades.shade-closing", backgroundColor:"#ffffff", nextState:"partially open"
            }
            tileAttribute ("device.level", key: "SLIDER_CONTROL") {
                attributeState "level", action:"setLevel"
            }
        }

        standardTile("home", "device.level", width: 2, height: 2, decoration: "flat") {
            state "default", label: "home", action:"presetPosition", icon:"st.Home.home2"
        }

        standardTile("refresh", "device.refresh", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
            state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh", nextState: "disabled"
            state "disabled", label:'', action:"", icon:"st.secondary.refresh"
        }

        valueTile("battery", "device.battery", decoration: "flat", inactiveLabel: false, width: 2, height: 2) {
            state "battery", label:'batt.', unit:"",
                    backgroundColors:[
                            [value: 0, color: "#bc2323"],
                            [value: 6, color: "#44b621"]
                    ]
        }

        preferences {
            input "switchDirection", "bool", title: "Flip the orientation of the shade", defaultValue: false, required: false, displayDuringSetup: false

        }

        main(["windowShade"])
        details(["windowShade", "home", "refresh", "battery"])

    }
}

def parse(String description) {
    def result = null
    
    
    def cmd = zwave.parse(description, [0x20: 1, 0x26: 3])  
    if (cmd) {
        result = zwaveEvent(cmd)
    }
    log.debug "Parsed '$description' to ${result.inspect()}"
    return result
}

def getCheckInterval() {
    
    
    4 * 60 * 60
}

def installed() {
    sendEvent(name: "checkInterval", value: checkInterval, displayed: false, data: [protocol: "zwave", hubHardwareId: device.hub.hardwareID, offlinePingable: "1"])
    sendEvent(name: "supportedWindowShadeCommands", value: JsonOutput.toJson(["open", "close", "pause"]), displayed: false)
    response(refresh())
}

def updated() {
    if (device.latestValue("checkInterval") != checkInterval) {
        sendEvent(name: "checkInterval", value: checkInterval, displayed: false)
    }
    def cmds = []
    if (!device.latestState("battery")) {
        cmds << zwave.batteryV1.batteryGet().format()
    }

    if (!device.getDataValue("MSR")) {
        cmds << zwave.manufacturerSpecificV1.manufacturerSpecificGet().format()
    }

    log.debug("Updated with settings $settings")
    cmds << zwave.switchMultilevelV1.switchMultilevelGet().format()
    response(cmds)
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd) {
    handleLevelReport(cmd)
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd) {
    handleLevelReport(cmd)
}

def zwaveEvent(physicalgraph.zwave.commands.switchmultilevelv3.SwitchMultilevelReport cmd) {
    handleLevelReport(cmd)
}

private handleLevelReport(physicalgraph.zwave.Command cmd) {
    def descriptionText = null
    def shadeValue = null

    def level = cmd.value as Integer
    level = switchDirection ? 99-level : level
    if (level >= 99) {
        level = 100
        shadeValue = "open"
    } else if (level <= 0) {
        level = 0  
        shadeValue = "closed"
    } else {
        shadeValue = "partially open"
        descriptionText = "${device.displayName} shade is ${level}% open"
    }
    def levelEvent = createEvent(name: "level", value: level, unit: "%", displayed: false)
    def stateEvent = createEvent(name: "windowShade", value: shadeValue, descriptionText: descriptionText, isStateChange: levelEvent.isStateChange)

    def result = [stateEvent, levelEvent]
    if (!state.lastbatt || now() - state.lastbatt > 24 * 60 * 60 * 1000) {
        log.debug "requesting battery"
        state.lastbatt = (now() - 23 * 60 * 60 * 1000) 
        result << response(["delay 15000", zwave.batteryV1.batteryGet().format()])
    }
    result
}

def zwaveEvent(physicalgraph.zwave.commands.switchmultilevelv3.SwitchMultilevelStopLevelChange cmd) {
    [ createEvent(name: "windowShade", value: "partially open", displayed: false, descriptionText: "$device.displayName shade stopped"),
      response(zwave.switchMultilevelV1.switchMultilevelGet().format()) ]
}

def zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv2.ManufacturerSpecificReport cmd) {
    def msr = String.format("%04X-%04X-%04X", cmd.manufacturerId, cmd.productTypeId, cmd.productId)
    updateDataValue("MSR", msr)
    if (cmd.manufacturerName) {
        updateDataValue("manufacturer", cmd.manufacturerName)
    }
    createEvent([descriptionText: "$device.displayName MSR: $msr", isStateChange: false])
}

def zwaveEvent(physicalgraph.zwave.commands.batteryv1.BatteryReport cmd) {
    def map = [ name: "battery", unit: "%" ]
    if (cmd.batteryLevel == 0xFF || cmd.batteryLevel == 0) {
        map.value = 1
        map.descriptionText = "${device.displayName} has a low battery"
        map.isStateChange = true
    } else {
        map.value = cmd.batteryLevel
    }
    state.lastbatt = now()
    if (map.value <= 1 && device.latestValue("battery") != null && device.latestValue("battery") - map.value > 20) {
        
        
        log.warn "Erroneous battery report dropped from ${device.latestValue("battery")} to $map.value. Not reporting"
    } else {
        createEvent(map)
    }
}

def zwaveEvent(physicalgraph.zwave.commands.notificationv3.NotificationReport cmd) {
    
    
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
    log.debug "unhandled $cmd"
    return []
}

def open() {
    log.debug "open()"
    def level = switchDirection ? 0 : 99
    zwave.basicV1.basicSet(value: level).format()
    
}

def close() {
    log.debug "close()"
    def level = switchDirection ? 99 : 0
    zwave.basicV1.basicSet(value: level).format()
    
}

def setLevel(value, duration = null) {
    log.debug "setLevel(${value.inspect()})"
    Integer level = value as Integer
    level = switchDirection ? 99-level : level
    if (level < 0) level = 0
    if (level > 99) level = 99
    zwave.basicV1.basicSet(value: level).format()
}

def presetPosition() {
    zwave.switchMultilevelV1.switchMultilevelSet(value: 0xFF).format()
}

def pause() {
    log.debug "pause()"
    stop()
}

def stop() {
    log.debug "stop()"
    zwave.switchMultilevelV3.switchMultilevelStopLevelChange().format()
}

def ping() {
    zwave.switchMultilevelV1.switchMultilevelGet().format()
}

def refresh() {
    log.debug "refresh()"
    delayBetween([
            zwave.switchMultilevelV1.switchMultilevelGet().format(),
            zwave.batteryV1.batteryGet().format()
    ], 1500)
}
