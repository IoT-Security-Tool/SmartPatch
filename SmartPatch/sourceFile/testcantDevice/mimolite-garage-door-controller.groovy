"License"
"AS IS""http://www.fortrezz.com/index.php/component/jdownloads/finish/4/17?Itemid=0""powered""powerOn""powerOff""Configure"
metadata {
	
	definition (name: "MimoLite Garage Door Controller", namespace: "smartthings", author: "Todd Wackford") {
		capability "Configuration"
		capability "Polling"
		capability "Switch"
		capability "Refresh"
		capability "Contact Sensor"
		capability "Light"

		attribute "powered", "string"

		command "on"
		command "off"
        
        fingerprint deviceId: "0x1000", inClusters: "0x72,0x86,0x71,0x30,0x31,0x35,0x70,0x85,0x25,0x03"
	}

	simulator {
	
    
	}

	
	tiles {
        standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {
			state "doorClosed", label: "Closed", action: "on", icon: "st.doors.garage.garage-closed", backgroundColor: "#00A0DC"
            state "doorOpen", label: "Open", action: "on", icon: "st.doors.garage.garage-open", backgroundColor: "#e86d13"
            state "doorOpening", label: "Opening", action: "on", icon: "st.doors.garage.garage-opening", backgroundColor: "#e86d13"
            state "doorClosing", label: "Closing", action: "on", icon: "st.doors.garage.garage-closing", backgroundColor: "#00A0DC"
            state "on", label: "Actuate", action: "off", icon: "st.doors.garage.garage-closed", backgroundColor: "#00A0DC"
			state "off", label: '${name}', action: "on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
        }
        standardTile("contact", "device.contact", inactiveLabel: false) {
			state "open", label: '${name}', icon: "st.contact.contact.open", backgroundColor: "#e86d13"
			state "closed", label: '${name}', icon: "st.contact.contact.closed", backgroundColor: "#00A0DC"
		}
        standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat") {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}
        standardTile("powered", "device.powered", inactiveLabel: false) {
			state "powerOn", label: "Power On", icon: "st.switches.switch.on", backgroundColor: "#79b821"
			state "powerOff", label: "Power Off", icon: "st.switches.switch.off", backgroundColor: "#ffa81e"
		}
		standardTile("configure", "device.configure", inactiveLabel: false, decoration: "flat") {
			state "configure", label:'', action:"configuration.configure", icon:"st.secondary.configure"
		}
		main (["switch", "contact"])
		details(["switch", "powered", "refresh", "configure"])
	}
}

def parse(String description) {
log.debug "description is: ${description}"

	def result = null
	def cmd = zwave.parse(description, [0x20: 1, 0x84: 1, 0x30: 1, 0x70: 1])
    
    log.debug "command value is: $cmd.CMD"
    
    if (cmd.CMD == "7105") {				
    	log.debug "Device lost power"
    	sendEvent(name: "powered", value: "powerOff", descriptionText: "$device.displayName lost power")
    } else {
    	sendEvent(name: "powered", value: "powerOn", descriptionText: "$device.displayName regained power")
    }
    
	if (cmd) {
		result = createEvent(zwaveEvent(cmd))
	}
	log.debug "Parse returned ${result?.descriptionText}"
	return result
}

def sensorValueEvent(Short value) {
	if (value) {
        sendEvent(name: "contact", value: "open")
        sendEvent(name: "switch", value: "doorOpen")
	} else {
        sendEvent(name: "contact", value: "closed")
        sendEvent(name: "switch", value: "doorClosed")
	}
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd) {
	[name: "switch", value: cmd.value ? "on" : "off", type: "physical"]
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd)
{
	sensorValueEvent(cmd.value)
}

def zwaveEvent(physicalgraph.zwave.commands.switchbinaryv1.SwitchBinaryReport cmd) {
	def doorState = device.currentValue('contact')
    if ( doorState == "closed")
		[name: "switch", value: cmd.value ? "on" : "doorOpening", type: "digital"]
    else
    	[name: "switch", value: cmd.value ? "on" : "doorClosing", type: "digital"]
}

def zwaveEvent(physicalgraph.zwave.commands.sensorbinaryv1.SensorBinaryReport cmd)
{
	sensorValueEvent(cmd.sensorValue)
}

def zwaveEvent(physicalgraph.zwave.commands.alarmv1.AlarmReport cmd)
{
    log.debug "We lost power" 
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	
	[:]
}

def configure() {
	log.debug "Configuring...." 
	delayBetween([
		zwave.associationV1.associationSet(groupingIdentifier:3, nodeId:[zwaveHubNodeId]).format(),
        zwave.configurationV1.configurationSet(configurationValue: [25], parameterNumber: 11, size: 1).format(),
        zwave.configurationV1.configurationGet(parameterNumber: 11).format()
	])
}

def on() {
	delayBetween([
		zwave.basicV1.basicSet(value: 0xFF).format(),
		zwave.switchBinaryV1.switchBinaryGet().format()
	])
}

def off() {
	delayBetween([
		zwave.basicV1.basicSet(value: 0x00).format(),
		zwave.switchBinaryV1.switchBinaryGet().format()
	])
}

def poll() {
	zwave.switchBinaryV1.switchBinaryGet().format()
}

def refresh() {
	zwave.switchBinaryV1.switchBinaryGet().format()
}
