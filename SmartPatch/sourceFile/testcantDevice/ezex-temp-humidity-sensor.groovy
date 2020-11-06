"License"
"AS IS"
import physicalgraph.zigbee.zcl.DataType

metadata {
    definition(name: "eZEX Temp & Humidity Sensor", namespace: "smartthings", author: "SmartThings", mnmn:"SmartThings", vid:"generic-humidity-3") {
        capability "Configuration"
        capability "Temperature Measurement"
        capability "Relative Humidity Measurement"
        capability "Sensor"
        capability "Health Check"
        
        fingerprint profileId: "0104", inClusters: "0000,0003,0402,0405,0500", outClusters: "0019", model: "E282-KR0B0Z1-HA", deviceJoinName: "eZEX Multipurpose Sensor" 
    }


    preferences {
        input "tempOffset", "number", title: "Temperature offset", description: "Select how many degrees to adjust the temperature.", range: "*..*", displayDuringSetup: false
        input "humidityOffset", "number", title: "Humidity offset", description: "Enter a percentage to adjust the humidity.", range: "*..*", displayDuringSetup: false
    }

    tiles(scale: 2) {
        multiAttributeTile(name: "temperature", type: "generic", width: 6, height: 4, canChangeIcon: true) {
            tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
                attributeState "temperature", label: '${currentValue}°',
                        backgroundColors: [
                                [value: 31, color: "#153591"],
                                [value: 44, color: "#1e9cbb"],
                                [value: 59, color: "#90d2a7"],
                                [value: 74, color: "#44b621"],
                                [value: 84, color: "#f1d801"],
                                [value: 95, color: "#d04e00"],
                                [value: 96, color: "#bc2323"]
                        ]
            }
        }
        valueTile("humidity", "device.humidity", inactiveLabel: false, width: 2, height: 2) {
            state "humidity", label: '${currentValue}% humidity', unit: ""
        }
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", action: "refresh.refresh", icon: "st.secondary.refresh"
        }

        main "temperature", "humidity"
        details(["temperature", "humidity", "refresh"])
    }
}

def parse(String description) {
    log.debug "description: $description"

    
    Map map = zigbee.getEvent(description)
    if (!map) {
        Map descMap = zigbee.parseDescriptionAsMap(description)
        if (descMap?.clusterInt == zigbee.TEMPERATURE_MEASUREMENT_CLUSTER && descMap.commandInt == 0x07) {
            if (descMap.data[0] == "00") {
                log.debug "TEMP REPORTING CONFIG RESPONSE: $descMap"
                sendEvent(name: "checkInterval", value: 60 * 12, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
            } else {
                log.warn "TEMP REPORTING CONFIG FAILED- error code: ${descMap.data[0]}"
            }
        }
    } else if (map.name == "temperature") {
        if (tempOffset) {
            map.value = (int) map.value + (int) tempOffset
        }
        map.descriptionText = temperatureScale == 'C' ? '{{ device.displayName }} was {{ value }}°C' : '{{ device.displayName }} was {{ value }}°F'
        map.translatable = true
    } else if (map.name == "humidity") {
        if (humidityOffset) {
            map.value = (int) map.value + (int) humidityOffset
        }
    }

    log.debug "Parse returned $map"
    return map ? createEvent(map) : [:]
}


def ping() {
    return refresh()
}

def refresh() {
    log.debug "refresh temperature, humidity"
    return zigbee.readAttribute(zigbee.RELATIVE_HUMIDITY_CLUSTER, 0x0000) +
           zigbee.readAttribute(zigbee.TEMPERATURE_MEASUREMENT_CLUSTER, 0x0000)
}

def configure() {
    
    sendEvent(name: "checkInterval", value: 2 * 60 * 60 + 1 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])

    log.debug "Configuring Reporting and Bindings."

    
    return refresh() +
           zigbee.configureReporting(zigbee.RELATIVE_HUMIDITY_CLUSTER, 0x0000, DataType.UINT16, 30, 3600, 100) +
           zigbee.configureReporting(zigbee.TEMPERATURE_MEASUREMENT_CLUSTER, 0x0000, DataType.UINT16, 30, 3600, 100)
}
