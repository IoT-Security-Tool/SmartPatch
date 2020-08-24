"License"
"AS IS"
metadata {
    definition (name: "Springs Window Fashions Remote", namespace: "smartthings", author: "SmartThings", ocfDeviceType: "x.com.st.d.remotecontroller", hidden: true) {

        capability "Battery"

        fingerprint mfr:"026E", prod:"5643", model:"5A31", deviceJoinName: "2 Button Window Remote"
        fingerprint mfr:"026E", prod:"4252", model:"5A31", deviceJoinName: "3 Button Window Remote"
    }

    simulator {

    }

    tiles {
        standardTile("state", "device.state", width: 2, height: 2) {
            state 'connected', icon: "st.unknown.zwave.remote-controller", backgroundColor:"#ffffff"
        }

        valueTile("battery", "device.battery", decoration: "flat", inactiveLabel: false, width: 2, height: 2) {
            state "battery", label:'batt.', unit:"",
                    backgroundColors:[
                            [value: 0, color: "#bc2323"],
                            [value: 6, color: "#44b621"]
                    ]
        }

        main "state"
        details(["state", "battery"])
    }

}

def installed() {
    if (zwaveInfo.cc?.contains("84")) {
        response(zwave.wakeUpV1.wakeUpNoMoreInformation())
    }
}

def parse(String description) {
    def result = null
    if (description.startsWith("Err")) {
        result = createEvent(descriptionText:description, displayed:true)
    } else {
        def cmd = zwave.parse(description)
        if (cmd) {
            result = zwaveEvent(cmd)
        }
    }
    return result
}

def zwaveEvent(physicalgraph.zwave.commands.wakeupv1.WakeUpNotification cmd) {
    def result = []
    result << createEvent(descriptionText: "${device.displayName} woke up", isStateChange: true)
    result << response(command(zwave.batteryV1.batteryGet()))
    result << response(zwave.wakeUpV1.wakeUpNoMoreInformation())
    result
}

def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation cmd) {
    def encapsulatedCommand = cmd.encapsulatedCommand()
    if (encapsulatedCommand) {
        return zwaveEvent(encapsulatedCommand)
    } else {
        log.warn "Unable to extract encapsulated cmd from $cmd"
        createEvent(descriptionText: cmd.toString())
    }
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
    [:]
}

def zwaveEvent(physicalgraph.zwave.commands.batteryv1.BatteryReport cmd) {
    def map = [ name: "battery", unit: "%" ]
    if (cmd.batteryLevel == 0xFF) {
        map.value = 1
        map.descriptionText = "${device.displayName} has a low battery"
        map.isStateChange = true
    } else {
        map.value = cmd.batteryLevel
    }
    state.lastbatt = now()
    createEvent(map)
}

private command(physicalgraph.zwave.Command cmd) {
    if (deviceIsSecure) {
        zwave.securityV1.securityMessageEncapsulation().encapsulate(cmd).format()
    } else {
        cmd.format()
    }
}

private getDeviceIsSecure() {
    if (zwaveInfo && zwaveInfo.zw) {
        return zwaveInfo.zw.contains("s")
    } else {
        return state.sec ? true : false
    }
}
