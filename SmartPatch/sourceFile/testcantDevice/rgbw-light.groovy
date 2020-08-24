"License"
"AS IS"

private getAEOTEC_LED6_MFR() { "0371" }
private getAEOTEC_LED6_PROD_US() { "0103" }
private getAEOTEC_LED6_PROD_EU() { "0003" }
private getAEOTEC_LED6_MODEL() { "0002" }

metadata {
	definition (name: "RGBW Light", namespace: "smartthings", author: "SmartThings", ocfDeviceType: "oic.d.light", mnmn: "SmartThings", vid: "generic-rgbw-color-bulb") {
		capability "Switch Level"
		capability "Color Control"
		capability "Color Temperature"
		capability "Switch"
		capability "Refresh"
		capability "Actuator"
		capability "Sensor"
		capability "Health Check"

		

		
		fingerprint inClusters: "0x26,0x33", deviceJoinName: "Z-Wave RGBW Bulb"

		
		
		fingerprint deviceId: "0x1101", inClusters: "0x26,0x33", deviceJoinName: "Z-Wave RGBW Bulb"

		
		
		fingerprint deviceId: "0x1102", inClusters: "0x26,0x33", deviceJoinName: "Z-Wave RGBW Bulb"

		
		fingerprint mfr: "0086", prod: "0103", model: "0079", deviceJoinName: "Aeotec LED Strip" 
		fingerprint mfr: "0086", prod: "0003", model: "0079", deviceJoinName: "Aeotec LED Strip" 
		fingerprint mfr: "0086", prod: "0103", model: "0062", deviceJoinName: "Aeotec LED Bulb" 
		fingerprint mfr: "0086", prod: "0003", model: "0062", deviceJoinName: "Aeotec LED Bulb" 
		fingerprint mfr: AEOTEC_LED6_MFR, prod: AEOTEC_LED6_PROD_US, model: AEOTEC_LED6_MODEL, deviceJoinName: "Aeotec LED Bulb 6" 
		fingerprint mfr: AEOTEC_LED6_MFR, prod: AEOTEC_LED6_PROD_EU, model: AEOTEC_LED6_MODEL, deviceJoinName: "Aeotec LED Bulb 6" 
		fingerprint mfr: "0300", prod: "0003", model: "0003", deviceJoinName: "ilumin RGBW Bulb"
		fingerprint mfr: "031E", prod: "0005", model: "0001", deviceJoinName: "ilumin RGBW Bulb"
	}

	simulator {
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"switch", type: "lighting", width: 1, height: 1, canChangeIcon: true) {
			tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
				attributeState("on", label:'${name}', action:"switch.off", icon:"st.lights.philips.hue-single", backgroundColor:"#00a0dc", nextState:"turningOff")
				attributeState("off", label:'${name}', action:"switch.on", icon:"st.lights.philips.hue-single", backgroundColor:"#ffffff", nextState:"turningOn")
				attributeState("turningOn", label:'${name}', action:"switch.off", icon:"st.lights.philips.hue-single", backgroundColor:"#00a0dc", nextState:"turningOff")
				attributeState("turningOff", label:'${name}', action:"switch.on", icon:"st.lights.philips.hue-single", backgroundColor:"#ffffff", nextState:"turningOn")
			}

			tileAttribute ("device.level", key: "SLIDER_CONTROL") {
				attributeState "level", action:"switch level.setLevel"
			}

			tileAttribute ("device.color", key: "COLOR_CONTROL") {
				attributeState "color", action:"setColor"
			}
		}
	}

	controlTile("colorTempSliderControl", "device.colorTemperature", "slider", width: 4, height: 2, inactiveLabel: false, range:"(2700..6500)") {
		state "colorTemperature", action:"color temperature.setColorTemperature"
	}

	main(["switch"])
	details(["switch", "levelSliderControl", "colorTempSliderControl"])
}

private getCOLOR_TEMP_MIN() { 2700 }
private getCOLOR_TEMP_MAX() { 6500 }








private getWHITE_MIN() { 1 } 
private getWHITE_MAX() { 255 } 
private getCOLOR_TEMP_DIFF() { COLOR_TEMP_MAX - COLOR_TEMP_MIN }
private getRED() { "red" }
private getGREEN() { "green" }
private getBLUE() { "blue" }
private getWARM_WHITE() { "warmWhite" }
private getCOLD_WHITE() { "coldWhite" }
private getRGB_NAMES() { [RED, GREEN, BLUE] }
private getWHITE_NAMES() { [WARM_WHITE, COLD_WHITE] }
private getCOLOR_NAMES() { RGB_NAMES + WHITE_NAMES }
private getSWITCH_VALUE_ON() { 0xFF } 
private getSWITCH_VALUE_OFF() { 0 } 
private BOUND(x, floor, ceiling) { Math.max(Math.min(x, ceiling), floor) }

def updated() {
	log.debug "updated().."
	response(refresh())
}

def installed() {
	log.debug "installed()..."
	sendEvent(name: "checkInterval", value: 1860, displayed: false, data: [protocol: "zwave", hubHardwareId: device.hub.hardwareID])
	sendEvent(name: "level", value: 100, unit: "%", displayed: false)
	sendEvent(name: "colorTemperature", value: COLOR_TEMP_MIN, displayed: false)
	sendEvent(name: "color", value: "#000000", displayed: false)
	sendEvent(name: "hue", value: 0, displayed: false)
	sendEvent(name: "saturation", value: 0, displayed: false)
}

def parse(description) {
	def result = null
	if (description.startsWith("Err 106")) {
		state.sec = 0
	} else if (description != "updated") {
		def cmd = zwave.parse(description)
		if (cmd) {
			result = zwaveEvent(cmd)
			log.debug("'$description' parsed to $result")
		} else {
			log.debug("Couldn't zwave.parse '$description'")
		}
	}
	result
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd) {
	dimmerEvents(cmd)
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd) {
	dimmerEvents(cmd)
}

def zwaveEvent(physicalgraph.zwave.commands.switchmultilevelv3.SwitchMultilevelReport cmd) {
	dimmerEvents(cmd)
}

def zwaveEvent(physicalgraph.zwave.commands.switchcolorv3.SwitchColorReport cmd) {
	log.debug "got SwitchColorReport: $cmd"
	def result = []
	if (state.staged != null && cmd.colorComponent in RGB_NAMES) {
		
		
		state.staged.subMap("hue", "saturation", "color").each{ k, v -> result << createEvent(name: k, value: v) }
	} else if (state.staged != null && cmd.colorComponent in WHITE_NAMES) {
		
		
		state.staged.subMap("colorTemperature").each{ k, v -> result << createEvent(name: k, value: v) }
	}
	result
}

private dimmerEvents(physicalgraph.zwave.Command cmd) {
	def value = (cmd.value ? "on" : "off")
	def result = [createEvent(name: "switch", value: value, descriptionText: "$device.displayName was turned $value")]
	if (cmd.value) {
		result << createEvent(name: "level", value: cmd.value == 99 ? 100 : cmd.value , unit: "%")
	}
	return result
}

def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation cmd) {
	def encapsulatedCommand = cmd.encapsulatedCommand()
	if (encapsulatedCommand) {
		state.sec = 1
		def result = zwaveEvent(encapsulatedCommand)
		result = result.collect {
			if (it instanceof physicalgraph.device.HubAction && !it.toString().startsWith("9881")) {
				response(cmd.CMD + "00" + it.toString())
			} else {
				it
			}
		}
		result
	}
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	def linkText = device.label ?: device.name
	[linkText: linkText, descriptionText: "$linkText: $cmd", displayed: false]
}

private emitMultiLevelSet(level, duration=1) {
	log.debug "setLevel($level, $duration)"
	duration = duration < 128 ? duration : 127 + Math.round(duration / 60) 
	duration = Math.min(duration, 0xFE) 
	def tcallback = Math.min(duration * 1000 + 2500, 12000) 
	commands([
		zwave.switchMultilevelV3.switchMultilevelSet(value: level, dimmingDuration: duration),
		zwave.switchMultilevelV3.switchMultilevelGet(),
	], tcallback)
}

def on() {
	emitMultiLevelSet(SWITCH_VALUE_ON)
}

def off() {
	emitMultiLevelSet(SWITCH_VALUE_OFF)
}

def setLevel(level, duration=1) {
	level = BOUND(level, 1, 99) 
	emitMultiLevelSet(level, duration)
}

def refresh() {
	commands([zwave.switchMultilevelV3.switchMultilevelGet()] + queryAllColors())
}

def ping() {
	log.debug "ping().."
	refresh()
}

def setSaturation(percent) {
	log.debug "setSaturation($percent)"
	setColor(saturation: percent)
}

def setHue(value) {
	log.debug "setHue($value)"
	setColor(hue: value)
}

def setColor(value) {
	log.debug "setColor($value)"
	def rgb
	if (state.staged == null) {
		state.staged = [:]
	}
	if (value.hex) {
		state.staged << [color: value.hex] 
		def hsv = colorUtil.hexToHsv(value.hex) 
		state.staged << [hue: hsv[0], saturation: hsv[1]] 
		rgb = value.hex.findAll(/[0-9a-fA-F]{2}/).collect { Integer.parseInt(it, 16) } 
	} else {
		state.staged << value.subMap("hue", "saturation") 
		def hex = colorUtil.hsvToHex(Math.round(value.hue) as int, Math.round(value.saturation) as int) 
		state.staged << [color: hex] 
		rgb = colorUtil.hexToRgb(hex) 
	}
	commands([zwave.switchColorV3.switchColorSet(red: rgb[0], green: rgb[1], blue: rgb[2], warmWhite: 0, coldWhite: 0),
	          zwave.switchColorV3.switchColorGet(colorComponent: RGB_NAMES[0]), 
	], 3500)
}

private emitTemperatureSet(temp, cmds) {
	
	temp = BOUND(temp, COLOR_TEMP_MIN, COLOR_TEMP_MAX)
	
	if (state.staged == null) {
		state.staged = [:]
	}
	
	state.staged << [colorTemperature: temp]
	
	
	
	
	def prologue = cmds.init() 
	def epilogue = [] << cmds.last() 
	epilogue << zwave.switchColorV3.switchColorGet(colorComponent: WHITE_NAMES[0]) 
	def rv = prologue.size() > 0 ? commands(prologue, 0) : [] 
	rv << commands(epilogue, 3500) 
	return rv 
}

private tempToZwaveWarmWhite(temp) {
	temp = BOUND(temp, COLOR_TEMP_MIN, COLOR_TEMP_MAX)
	def warmValue = ((COLOR_TEMP_MAX - temp) / COLOR_TEMP_DIFF * WHITE_MAX) as Integer
	warmValue = Math.max(WHITE_MIN, warmValue)
	warmValue
}

private tempToZwaveColdWhite(temp) {
	def coldValue = (WHITE_MAX - tempToZwaveWarmWhite(temp))
	coldValue = Math.max(WHITE_MIN, coldValue)
	coldValue
}

private setZwaveColorTemperature(temp) {
	def warmValue = tempToZwaveWarmWhite(temp)
	def coldValue = tempToZwaveColdWhite(temp)
	emitTemperatureSet(temp, [zwave.switchColorV3.switchColorSet(red: 0, green: 0, blue: 0, warmWhite: warmValue, coldWhite: coldValue)])
}

private setAeotecLed6ColorTemperature(temp) {
	temp = BOUND(temp, COLOR_TEMP_MIN, COLOR_TEMP_MAX)
	def warmValue = temp < 5000 ? 255 : 0
	def coldValue = temp >= 5000 ? 255 : 0
	def WARM_WHITE_CONFIG = 0x51
	def COLD_WHITE_CONFIG = 0x52
	def parameterNumber = temp < 5000 ? WARM_WHITE_CONFIG : COLD_WHITE_CONFIG
	
	
	
	
	
	
	
	
	
	
	emitTemperatureSet(temp, [zwave.switchColorV3.switchColorSet(red: 0, green: 0, blue: 0, warmWhite: warmValue, coldWhite: coldValue),
	                          zwave.configurationV1.configurationSet([parameterNumber: parameterNumber, size: 2, scaledConfigurationValue: temp])])
}

def isAeotecLed6() {
	(   (zwaveInfo?.mfr?.equals(AEOTEC_LED6_MFR) && zwaveInfo?.prod?.equals(AEOTEC_LED6_PROD_US) && zwaveInfo?.model?.equals(AEOTEC_LED6_MODEL))
	 || (zwaveInfo?.mfr?.equals(AEOTEC_LED6_MFR) && zwaveInfo?.prod?.equals(AEOTEC_LED6_PROD_EU) && zwaveInfo?.model?.equals(AEOTEC_LED6_MODEL)))
}

def setColorTemperature(temp) {
	log.debug "setColorTemperature($temp)"
	if (isAeotecLed6()) {
		
		
		setAeotecLed6ColorTemperature(temp)
	} else {
		setZwaveColorTemperature(temp)
	}
}

private queryAllColors() {
	COLOR_NAMES.collect { zwave.switchColorV3.switchColorGet(colorComponent: it) }
}

private secEncap(physicalgraph.zwave.Command cmd) {
	zwave.securityV1.securityMessageEncapsulation().encapsulate(cmd).format()
}

private crcEncap(physicalgraph.zwave.Command cmd) {
	zwave.crc16EncapV1.crc16Encap().encapsulate(cmd).format()
}

private command(physicalgraph.zwave.Command cmd) {
	if (zwaveInfo.zw.contains("s") || state.sec == 1) {
		secEncap(cmd)
	} else if (zwaveInfo?.cc?.contains("56")){
		crcEncap(cmd)
	} else {
		cmd.format()
	}
}

private commands(commands, delay=200) {
	delayBetween(commands.collect{ command(it) }, delay)
}
