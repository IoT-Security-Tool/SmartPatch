"License"
"AS IS"
metadata {
	definition (name: "Fibaro Smoke Sensor", namespace: "smartthings", author: "SmartThings", mnmn: "SmartThings", vid: "SmartThings-smartthings-Fibaro_Smoke_Sensor", ocfDeviceType: "x.com.st.d.sensor.smoke") {
		capability "Battery" 
		capability "Configuration"	
		capability "Sensor"
		capability "Smoke Detector" 
		capability "Temperature Measurement" 
		capability "Health Check"
		capability "Tamper Alert"
		capability "Temperature Alarm"

		fingerprint mfr:"010F", prod:"0C02", model:"1002", deviceJoinName: "Fibaro Smoke Detector"
		fingerprint mfr:"010F", prod:"0C02", model:"4002", deviceJoinName: "Fibaro Smoke Detector"
		fingerprint mfr:"010F", prod:"0C02", model:"1003", deviceJoinName: "Fibaro Smoke Detector"
		fingerprint mfr:"010F", prod:"0C02", deviceJoinName: "Fibaro Smoke Detector"
		fingerprint mfr:"010F", prod:"0C02", model:"3002", deviceJoinName: "Fibaro Smoke Detector"
	}
	simulator {
		
		for (int i in [0, 5, 10, 15, 50, 99, 100]) {
			status "battery ${i}%":	 new physicalgraph.zwave.Zwave().securityV1.securityMessageEncapsulation().encapsulate(
					new physicalgraph.zwave.Zwave().batteryV1.batteryReport(batteryLevel: i)
			).incomingMessage()
		}
		status "battery 100%": "command: 8003, payload: 64"
		status "battery 5%": "command: 8003, payload: 05"
		
		status "smoke detected": "command: 7105, payload: 01 01"
		status "smoke clear": "command: 7105, payload: 01 00"
		status "smoke tested": "command: 7105, payload: 01 03"
		
		for (int i = 0; i <= 100; i += 20) {
			status "temperature ${i}F": new physicalgraph.zwave.Zwave().securityV1.securityMessageEncapsulation().encapsulate(
					new physicalgraph.zwave.Zwave().sensorMultilevelV5.sensorMultilevelReport(scaledSensorValue: i, precision: 1, sensorType: 1, scale: 1)
			).incomingMessage()
		}
	}
	preferences {
		input description: "After successful installation, please click B-button at the Fibaro Smoke Sensor to update device status and configuration",
				title: "Instructions", displayDuringSetup: true, type: "paragraph", element: "paragraph"
		input description: "Enter the menu by press and hold B-button for 3 seconds. Once indicator glows WHITE, release the B-button. Visual indicator will start changing colours in sequence. Press B-button briefly when visual indicator glows GREEN",
				title: "To check smoke detection state", displayDuringSetup: true, type: "paragraph", element: "paragraph"
		input description: "Please consult Fibaro Smoke Sensor operating manual for advanced setting options. You can skip this configuration to use default settings",
				title: "Advanced Configuration", displayDuringSetup: true, type: "paragraph", element: "paragraph"
		input "smokeSensorSensitivity", "enum", title: "Smoke Sensor Sensitivity", options: ["High","Medium","Low"], defaultValue: "${smokeSensorSensitivity}", displayDuringSetup: true
		input "zwaveNotificationStatus", "enum", title: "Notifications Status", options: ["disabled","casing opened","exceeding temperature threshold", "lack of Z-Wave range", "all notifications"],
			   
			   
				defaultValue: "casing opened", displayDuringSetup: true
		input "visualIndicatorNotificationStatus", "enum", title: "Visual Indicator Notifications Status",
				options: ["disabled","casing opened","exceeding temperature threshold", "lack of Z-Wave range", "all notifications"],
				defaultValue: "${visualIndicatorNotificationStatus}", displayDuringSetup: true
		input "soundNotificationStatus", "enum", title: "Sound Notifications Status",
				options: ["disabled","casing opened","exceeding temperature threshold", "lack of Z-Wave range", "all notifications"],
				defaultValue: "${soundNotificationStatus}", displayDuringSetup: true
		input "temperatureReportInterval", "enum", title: "Temperature Report Interval",
				options: ["reports inactive", "5 minutes", "15 minutes", "30 minutes", "1 hour", "6 hours", "12 hours", "18 hours", "24 hours"], defaultValue: "${temperatureReportInterval}", displayDuringSetup: true
		input "temperatureReportHysteresis", "number", title: "Temperature Report Hysteresis", description: "Available settings: 1-100 C", range: "1..100", displayDuringSetup: true
		input "temperatureThreshold", "number", title: "Overheat Temperature Threshold", description: "Available settings: 0 or 2-100 C", range: "0..100", displayDuringSetup: true
		input "excessTemperatureSignalingInterval", "enum", title: "Excess Temperature Signaling Interval",
				options: ["5 minutes", "15 minutes", "30 minutes", "1 hour", "6 hours", "12 hours", "18 hours", "24 hours"], defaultValue: "${excessTemperatureSignalingInterval}", displayDuringSetup: true
		input "lackOfZwaveRangeIndicationInterval", "enum", title: "Lack of Z-Wave Range Indication Interval",
				options: ["5 minutes", "15 minutes", "30 minutes", "1 hour", "6 hours", "12 hours", "18 hours", "24 hours"], defaultValue: "${lackOfZwaveRangeIndicationInterval}", displayDuringSetup: true
	}
	tiles (scale: 2){
		multiAttributeTile(name:"smoke", type: "lighting", width: 6, height: 4){
			tileAttribute ("device.smoke", key: "PRIMARY_CONTROL") {
				attributeState("clear", label:"CLEAR", icon:"st.alarm.smoke.clear", backgroundColor:"#ffffff")
				attributeState("detected", label:"SMOKE", icon:"st.alarm.smoke.smoke", backgroundColor:"#e86d13")
				attributeState("tested", label:"TEST", icon:"st.alarm.smoke.test", backgroundColor:"#e86d13")
				attributeState("replacement required", label:"REPLACE", icon:"st.alarm.smoke.test", backgroundColor:"#FFFF66")
				attributeState("unknown", label:"UNKNOWN", icon:"st.alarm.smoke.test", backgroundColor:"#ffffff")
			}
		}
		valueTile("battery", "device.battery", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "battery", label:'${currentValue}% battery', unit:"%"
		}
		valueTile("temperature", "device.temperature", inactiveLabel: false, width: 2, height: 2) {
			state "temperature", label: '${currentValue}°',
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
		valueTile("temperatureAlarm", "device.temperatureAlarm", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "cleared", label:'TEMPERATURE OK', backgroundColor:"#ffffff"
			state "heat", label:'OVERHEAT DETECTED', backgroundColor:"#ffffff"
			state "rateOfRise", label:'RAPID TEMP RISE', backgroundColor:"#ffffff"
			state "freeze", label:'UNDERHEAT DETECTED', backgroundColor:"#ffffff"
		}
		valueTile("tamper", "device.tamper", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
		 state "clear", label:'NO TAMPER', backgroundColor:"#ffffff"
		 state "detected", label:'TAMPER DETECTED', backgroundColor:"#ffffff"
		
		}

		main "smoke"
		details(["smoke","temperature","battery", "tamper", "temperatureAlarm"])
	}
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	setConfigured("false") 
}



def parse(String description) {
    log.debug "parse() >> description: $description"
    def result = null
    if (description.startsWith("Err 106")) {
        log.debug "parse() >> Err 106"
        result = createEvent( name: "secureInclusion", value: "failed", isStateChange: true,
                descriptionText: "This sensor failed to complete the network security key exchange. " +
                        "If you are unable to control it via SmartThings, you must remove it from your network and add it again.")
    } else if (description != "updated") {
        log.debug "parse() >> $description"
        def cmd = zwave.parse(description, [0x31: 5, 0x71: 3, 0x84: 1])
        if (cmd) {
            result = zwaveEvent(cmd)
        }
    }
    log.debug "After zwaveEvent(cmd) >> Parsed '${description}' to ${result.inspect()}"
    return result
}

def zwaveEvent(physicalgraph.zwave.commands.versionv1.VersionReport cmd) {
	log.info "Executing zwaveEvent 86 (VersionV1): 12 (VersionReport) with cmd: $cmd"
	def fw = "${cmd.applicationVersion}.${cmd.applicationSubVersion}"
	updateDataValue("fw", fw)
	def text = "$device.displayName: firmware version: $fw, Z-Wave version: ${cmd.zWaveProtocolVersion}.${cmd.zWaveProtocolSubVersion}"
	createEvent(descriptionText: text, isStateChange: false)
}


def zwaveEvent(physicalgraph.zwave.commands.batteryv1.BatteryReport cmd) {
	def map = [ name: "battery", unit: "%" ]
	if (cmd.batteryLevel == 0xFF) {
		map.value = 1
		map.descriptionText = "${device.displayName} battery is low"
		map.isStateChange = true
	} else {
		map.value = cmd.batteryLevel
	}
	setConfigured("true")  
	
	state.lastbatt = now()
	createEvent(map)
}

def zwaveEvent(physicalgraph.zwave.commands.applicationstatusv1.ApplicationBusy cmd) {
	def msg = cmd.status == 0 ? "try again later" :
			cmd.status == 1 ? "try again in $cmd.waitTime seconds" :
					cmd.status == 2 ? "request queued" : "sorry"
	createEvent(displayed: true, descriptionText: "$device.displayName is busy, $msg")
}

def zwaveEvent(physicalgraph.zwave.commands.applicationstatusv1.ApplicationRejectedRequest cmd) {
	createEvent(displayed: true, descriptionText: "$device.displayName rejected the last request")
}


def zwaveEvent(physicalgraph.zwave.commands.crc16encapv1.Crc16Encap cmd) {
	def versions = [0x31: 5, 0x71: 3, 0x84: 1]
	def version = versions[cmd.commandClass as Integer]
	def ccObj = version ? zwave.commandClass(cmd.commandClass, version) : zwave.commandClass(cmd.commandClass)
	def encapsulatedCommand = ccObj?.command(cmd.command)?.parse(cmd.data)
	if (!encapsulatedCommand) {
		log.debug "Could not extract command from $cmd"
	} else {
		zwaveEvent(encapsulatedCommand)
	}
}

def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation cmd) {
	setSecured()
	def encapsulatedCommand = cmd.encapsulatedCommand([0x31: 5, 0x71: 3, 0x84: 1])
	if (encapsulatedCommand) {
		log.debug "command: 98 (Security) 81(SecurityMessageEncapsulation) encapsulatedCommand:	 $encapsulatedCommand"
		zwaveEvent(encapsulatedCommand)
	} else {
		log.warn "Unable to extract encapsulated cmd from $cmd"
		createEvent(descriptionText: cmd.toString())
	}
}

def isFibaro() {
	(zwaveInfo?.mfr?.equals("010F") && zwaveInfo?.prod?.equals("0C02"))
}

def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityCommandsSupportedReport cmd) {
	log.info "Executing zwaveEvent 98 (SecurityV1): 03 (SecurityCommandsSupportedReport) with cmd: $cmd"
	setSecured()
	log.info "checking this MSR : ${getDataValue("MSR")} before sending configuration to device"
	if (isFibaro()) {
		response(configure()) 
  }
}

def zwaveEvent(physicalgraph.zwave.commands.securityv1.NetworkKeyVerify cmd) {
	log.info "Executing zwaveEvent 98 (SecurityV1): 07 (NetworkKeyVerify) with cmd: $cmd (node is securely included)"
	createEvent(name:"secureInclusion", value:"success", descriptionText:"Secure inclusion was successful", isStateChange: true, displayed: true)
	
	setSecured()
	log.info "checking this MSR : ${getDataValue("MSR")} before sending configuration to device"
	if (isFibaro()) {
		response(configure()) 
	}
}

def zwaveEvent(physicalgraph.zwave.commands.notificationv3.NotificationReport cmd) {
	log.info "Executing zwaveEvent 71 (NotificationV3): 05 (NotificationReport) with cmd: $cmd"
	def result = []
	if (cmd.notificationType == 7) {
		switch (cmd.event) {
			case 0:
				log.debug "tamper inactive"
				sendEvent(name: "tamper", value: "clear")
				break
			case 3:
				 log.debug "tamper active"
				 sendEvent(name: "tamper", value: "detected")
				break
		}
	} else if (cmd.notificationType == 1) { 
		log.debug "notificationv3.NotificationReport: for Smoke Alarm (V2)"
		result << smokeAlarmEvent(cmd.event)
	}  else if (cmd.notificationType == 4) { 
		log.debug "notificationv3.NotificationReport: for Heat Alarm (V2)"
		result << heatAlarmEvent(cmd.event)
	} else {
		log.warn "Need to handle this cmd.notificationType: ${cmd.notificationType}"
		result << createEvent(descriptionText: cmd.toString(), isStateChange: false)
	}
	result
}

def smokeAlarmEvent(value) {
	log.debug "smokeAlarmEvent(value): $value"
	def map = [name: "smoke"]
	if (value == 1 || value == 2) {
		map.value = "detected"
		map.descriptionText = "$device.displayName detected smoke"
	} else if (value == 0) {
		map.value = "clear"
		map.descriptionText = "$device.displayName is clear (no smoke)"
	} else if (value == 3) {
		map.value = "tested"
		map.descriptionText = "$device.displayName smoke alarm test"
	} else if (value == 4) {
		map.value = "replacement required"
		map.descriptionText = "$device.displayName replacement required"
	} else {
		map.value = "unknown"
		map.descriptionText = "$device.displayName unknown event"
	}
	createEvent(map)
}

def heatAlarmEvent(value) {
	log.debug "heatAlarmEvent(value): $value"
	def map = [name: "temperatureAlarm"]
	if (value == 1 || value == 2) {
		map.value = "heat"
		map.descriptionText = "$device.displayName overheat detected"
	} else if (value == 0) {
		map.value = "cleared"
		map.descriptionText = "$device.displayName heat alarm cleared (no overheat)"
	} else if (value == 3 || value == 4) {
		map.value = "rateOfRise"
		map.descriptionText = "$device.displayName rapid temperature rise"
	} else if (value == 5 || value == 6) {
		map.value = "freeze"
		map.descriptionText = "$device.displayName underheat detected"
	} else {
		map.value = "unknown"
		map.descriptionText = "$device.displayName unknown event"
	}
	createEvent(map)
}

def zwaveEvent(physicalgraph.zwave.commands.wakeupv1.WakeUpNotification cmd) {
	log.info "Executing zwaveEvent 84 (WakeUpV1): 07 (WakeUpNotification) with cmd: $cmd"
	log.info "checking this MSR : ${getDataValue("MSR")} before sending configuration to device"
	def result = [createEvent(descriptionText: "${device.displayName} woke up", isStateChange: false)]
	def cmds = []
	"manufacturerId-productTypeId"
	if (isFibaro()) {
		result << response(configure()) 
	} else {
		
		if (!state.lastbatt || (new Date().time) - state.lastbatt > 24*60*60*1000) {
			log.debug("Device has been configured sending >> batteryGet()")
			cmds << zwave.securityV1.securityMessageEncapsulation().encapsulate(zwave.batteryV1.batteryGet()).format()
			cmds << "delay 1200"
		}
		log.debug("Device has been configured sending >> wakeUpNoMoreInformation()")
		cmds << zwave.wakeUpV1.wakeUpNoMoreInformation().format()
		result << response(cmds) 
	}
	result
}

def zwaveEvent(physicalgraph.zwave.commands.sensormultilevelv5.SensorMultilevelReport cmd) {
	log.info "Executing zwaveEvent 31 (SensorMultilevelV5): 05 (SensorMultilevelReport) with cmd: $cmd"
	def map = [:]
	switch (cmd.sensorType) {
		case 1:
			map.name = "temperature"
			def cmdScale = cmd.scale == 1 ? "F" : "C"
			map.value = convertTemperatureIfNeeded(cmd.scaledSensorValue, cmdScale, cmd.precision)
			map.unit = getTemperatureScale()
			break
		default:
			map.descriptionText = cmd.toString()
	}
	createEvent(map)
}

def zwaveEvent(physicalgraph.zwave.commands.deviceresetlocallyv1.DeviceResetLocallyNotification cmd) {
	log.info "Executing zwaveEvent 5A (DeviceResetLocallyV1) : 01 (DeviceResetLocallyNotification) with cmd: $cmd"
	createEvent(descriptionText: cmd.toString(), isStateChange: true, displayed: true)
}

def zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv2.ManufacturerSpecificReport cmd) {
	log.info "Executing zwaveEvent 72 (ManufacturerSpecificV2) : 05 (ManufacturerSpecificReport) with cmd: $cmd"
	log.debug "manufacturerId:	 ${cmd.manufacturerId}"
	log.debug "manufacturerName: ${cmd.manufacturerName}"
	log.debug "productId:		 ${cmd.productId}"
	log.debug "productTypeId:	 ${cmd.productTypeId}"
	def result = []
	def msr = String.format("%04X-%04X-%04X", cmd.manufacturerId, cmd.productTypeId, cmd.productId)
	updateDataValue("MSR", msr)
	log.debug "After device is securely joined, send commands to update tiles"
	result << zwave.batteryV1.batteryGet()
	result << zwave.sensorMultilevelV5.sensorMultilevelGet(sensorType: 0x01)
	result << zwave.wakeUpV1.wakeUpNoMoreInformation()
	[[descriptionText:"${device.displayName} MSR report"], response(commands(result, 5000))]
}

def zwaveEvent(physicalgraph.zwave.commands.associationv2.AssociationReport cmd) {
	def result = []
	if (cmd.nodeId.any { it == zwaveHubNodeId }) {
		result << createEvent(descriptionText: "$device.displayName is associated in group ${cmd.groupingIdentifier}")
	} else if (cmd.groupingIdentifier == 1) {
		result << createEvent(descriptionText: "Associating $device.displayName in group ${cmd.groupingIdentifier}")
		result << response(zwave.associationV1.associationSet(groupingIdentifier:cmd.groupingIdentifier, nodeId:zwaveHubNodeId))
	}
	result
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	log.warn "General zwaveEvent cmd: ${cmd}"
	createEvent(descriptionText: cmd.toString(), isStateChange: false)
}

def installed(){
	log.debug "installed()"
	state.initDefault = true
	sendEvent(name: "tamper", value: "clear", displayed: false)	 
}

def configure() {
	
	sendEvent(name: "checkInterval", value: 8 * 60 * 60 + 2 * 60, displayed: false, data: [protocol: "zwave", hubHardwareId: device.hub.hardwareID])
	
	sendEvent(name: "smoke", value: "clear", displayed: false)
	
	log.debug "configure() >> isSecured() : ${isSecured()}"
	if (!isSecured()) {
		log.debug "Fibaro smoke sensor not sending configure until secure"
		return []
	} else {
		log.info "${device.displayName} is configuring its settings"
		def request = []

		
		request += zwave.wakeUpV1.wakeUpIntervalSet(seconds:6*3600, nodeid:zwaveHubNodeId)

		
		if (smokeSensorSensitivity && smokeSensorSensitivity != "null") {
			request += zwave.configurationV1.configurationSet(parameterNumber: 1, size: 1,
					scaledConfigurationValue:
							smokeSensorSensitivity == "High" ? 1 :
									smokeSensorSensitivity == "Medium" ? 2 :
											smokeSensorSensitivity == "Low" ? 3 : 2)
		}
		
		
		
		
		
		
		
		
		
		
		
		if (zwaveNotificationStatus && zwaveNotificationStatus != "null") {
			log.debug "2- else zwave notification "+zwave.configurationV1.configurationSet(parameterNumber: 2, size: 1, scaledConfigurationValue: notificationOptionValueMap[zwaveNotificationStatus] ?: 0)
			request += zwave.configurationV1.configurationSet(parameterNumber: 2, size: 1, scaledConfigurationValue: notificationOptionValueMap[zwaveNotificationStatus] ?: 0)
		} else	{
			log.debug "1- Setting zwave notification default value to 1: "+zwave.configurationV1.configurationSet(parameterNumber: 2, size: 1, scaledConfigurationValue: 1)
			request += zwave.configurationV1.configurationSet(parameterNumber: 2, size: 1, scaledConfigurationValue: 1)
		 }
		
		
		if (visualIndicatorNotificationStatus && visualIndicatorNotificationStatus != "null") {
			log.debug "Adding visual notification: "+zwave.configurationV1.configurationSet(parameterNumber: 3, size: 1, scaledConfigurationValue: notificationOptionValueMap[visualIndicatorNotificationStatus] ?: 0).format()
			request += zwave.configurationV1.configurationSet(parameterNumber: 3, size: 1, scaledConfigurationValue: notificationOptionValueMap[visualIndicatorNotificationStatus] ?: 0)
		}
		
		if (soundNotificationStatus && soundNotificationStatus != "null") {
			log.debug "Adding sound notification: "+zwave.configurationV1.configurationSet(parameterNumber: 4, size: 1, scaledConfigurationValue: notificationOptionValueMap[soundNotificationStatus] ?: 0).format()
			request += zwave.configurationV1.configurationSet(parameterNumber: 4, size: 1, scaledConfigurationValue: notificationOptionValueMap[soundNotificationStatus] ?: 0)
		}
		
		if (temperatureReportInterval && temperatureReportInterval != "null") {
			request += zwave.configurationV1.configurationSet(parameterNumber: 20, size: 2, scaledConfigurationValue: timeOptionValueMap[temperatureReportInterval] ?: 180)
		} else { 
			request += zwave.configurationV1.configurationSet(parameterNumber: 20, size: 2, scaledConfigurationValue: 180)
		}
		
		if (temperatureReportHysteresis && temperatureReportHysteresis != null) {
			request += zwave.configurationV1.configurationSet(parameterNumber: 21, size: 1, scaledConfigurationValue: temperatureReportHysteresis < 1 ? 1 : temperatureReportHysteresis > 100 ? 100 : temperatureReportHysteresis)
		}
		
		if (temperatureThreshold && temperatureThreshold != null) {
			request += zwave.configurationV1.configurationSet(parameterNumber: 30, size: 1, scaledConfigurationValue: temperatureThreshold < 1 ? 1 : temperatureThreshold > 100 ? 100 : temperatureThreshold)
		}
		
		if (excessTemperatureSignalingInterval && excessTemperatureSignalingInterval != "null") {
			request += zwave.configurationV1.configurationSet(parameterNumber: 31, size: 2, scaledConfigurationValue: timeOptionValueMap[excessTemperatureSignalingInterval] ?: 180)
		} else { 
			request += zwave.configurationV1.configurationSet(parameterNumber: 31, size: 2, scaledConfigurationValue: 180)
		}
		
		if (lackOfZwaveRangeIndicationInterval && lackOfZwaveRangeIndicationInterval != "null") {
			request += zwave.configurationV1.configurationSet(parameterNumber: 32, size: 2, scaledConfigurationValue: timeOptionValueMap[lackOfZwaveRangeIndicationInterval] ?: 2160)
		} else {
			request += zwave.configurationV1.configurationSet(parameterNumber: 32, size: 2, scaledConfigurationValue: 2160)
		}
		log.debug "zwave config: "+request

		
		request += zwave.batteryV1.batteryGet()

		
		request += zwave.sensorMultilevelV5.sensorMultilevelGet(sensorType: 0x01)

		commands(request) + ["delay 10000", zwave.wakeUpV1.wakeUpNoMoreInformation().format()]

	}
}

private def getTimeOptionValueMap() { [
		"5 minutes"	 : 30,
		"15 minutes" : 90,
		"30 minutes" : 180,
		"1 hour"	 : 360,
		"6 hours"	 : 2160,
		"12 hours"	 : 4320,
		"18 hours"	 : 6480,
		"24 hours"	 : 8640,
		"reports inactive" : 0,
]}

private def getNotificationOptionValueMap() { [
		"disabled" : 0,
		"casing opened" : 1,
		"exceeding temperature threshold" : 2,
		"lack of Z-Wave range" : 4,
		"all notifications" : 7,
]}

private command(physicalgraph.zwave.Command cmd) {
	if (isSecured()) {
		log.info "Sending secured command: ${cmd}"
		zwave.securityV1.securityMessageEncapsulation().encapsulate(cmd).format()
	} else {
		log.info "Sending unsecured command: ${cmd}"
		cmd.format()
	}
}

private commands(commands, delay=200) {
	log.info "inside commands: ${commands}"
	delayBetween(commands.collect{ command(it) }, delay)
}

private setConfigured(configure) {
	updateDataValue("configured", configure)
}
private isConfigured() {
	getDataValue("configured") == "true"
}
private setSecured() {
	updateDataValue("secured", "true")
}

private isSecured() {
    if (zwaveInfo && zwaveInfo.zw) {
        return zwaveInfo.zw.contains("s")
    } else {
        return getDataValue("secured") == "true"
    }
}
