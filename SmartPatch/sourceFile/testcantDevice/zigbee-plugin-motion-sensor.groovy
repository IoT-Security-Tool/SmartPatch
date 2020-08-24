"License"
"AS IS"
metadata {
    definition(name: "Zigbee Plugin Motion Sensor", namespace: "smartthings", author: "SmartThings", runLocally: false, mnmn: "SmartThings", vid: "SmartThings-smartthings-LAN_Wemo_Motion") {
        capability "Motion Sensor"
        capability "Configuration"
        capability "Refresh"
        capability "Health Check"
        capability "Sensor"
       
        fingerprint profileId: "0104", deviceId: "0107", inClusters: "0000, 0003, 0004, 0406", outClusters: "0006, 0019", model: "E280-KR0A0Z0-HA", deviceJoinName: "Smart Occupancy Sensor (AC Type)"
    }
    tiles(scale: 2) {
        multiAttributeTile(name: "motion", type: "generic", width: 6, height: 4) {
            tileAttribute("device.motion", key: "PRIMARY_CONTROL") {
                attributeState "active", label: 'motion', icon: "st.motion.motion.active", backgroundColor: "#00A0DC"
                attributeState "inactive", label: 'no motion', icon: "st.motion.motion.inactive", backgroundColor: "#cccccc"
            }
        }

        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", action: "refresh.refresh", icon: "st.secondary.refresh"
        }
        main(["motion"])
        details(["motion", "refresh"])
    }
}

def installed() {
    log.debug "installed"
}

def parse(String description) {
    log.debug "description(): $description"
    def map = zigbee.getEvent(description)
    if (!map) {
        def descMap = zigbee.parseDescriptionAsMap(description)
        if (descMap.clusterInt == 0x0406 && descMap.attrInt == 0x0000) { 
             map.name = "motion"
             map.value = descMap.value.endsWith("01") ? "active" : "inactive"
        }
    }
    log.debug "Parse returned $map"
    def result = map ? createEvent(map) : [:]
    if (description?.startsWith('enroll request')) {
        List cmds = zigbee.enrollResponse()
        log.debug "enroll response: ${cmds}"
        result = cmds?.collect { new physicalgraph.device.HubAction(it) }
    }
    return result
}


def ping() {
    log.debug "ping "
    refresh()
}

def refresh() {
    log.debug "Refreshing Values"
    zigbee.readAttribute(0x0406, 0x0000)
}

def configure() {
    log.debug "configure"
    
    sendEvent(name: "checkInterval", value: 10 * 60 + 2 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID, offlinePingable: "1"])
    return refresh()
}
