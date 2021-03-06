"License"
"AS IS"

metadata {
	definition (name: "ZigBee Dimmer", namespace: "smartthings", author: "SmartThings", ocfDeviceType: "oic.d.light", runLocally: true, minHubCoreVersion: '000.019.00012', executeCommandsLocally: true, genericHandler: "Zigbee") {
		capability "Actuator"
		capability "Configuration"
		capability "Refresh"
		capability "Switch"
		capability "Switch Level"
		capability "Health Check"
		capability "Light"

		
		fingerprint profileId: "0104", deviceId: "0101", inClusters: "0006, 0008", deviceJoinName: "Generic Dimmable Light"

		
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 1000", outClusters: "0019", deviceId: "0101", manufacturer: "AduroSmart Eria", model: "AD-DimmableLight3001", deviceJoinName: "Eria ZigBee Dimmable Bulb"

		
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008", outClusters: "0019", manufacturer: "Aurora", model: "LCBulb01UK", deviceJoinName: "Aurora AOne Control Dimmer (120w)", ocfDeviceType: "oic.d.switch"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0300", outClusters: "0003", manufacturer: "Aurora", model: "Dimmer", deviceJoinName: "Aurora AOne Control Dimmer (320w)", ocfDeviceType: "oic.d.switch"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0009", outClusters: "0019", manufacturer: "Aurora", model: "FWMPROZXBulb50AU", deviceJoinName: "Aurora MPro"
		fingerprint profileId: "0104", inClusters: "0000, 0004, 0003, 0006, 0008, 0005, FFFF, 1000", outClusters: "0019", manufacturer: "Aurora", model: "FWBulb51AU", deviceJoinName: "Aurora Smart Dimmable"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008", outClusters: "0019", manufacturer: "Aurora", model: "FWStrip50AU", deviceJoinName: "Aurora Dimmable Strip Controller"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0B05, 1000, FEDC", outClusters: "000A, 0019", manufacturer: "Aurora", model: "FWGU10Bulb50AU", deviceJoinName: "Aurora Smart Dimmable GU10"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008", outClusters: "0019", manufacturer: "Aurora", model: "NPD3032", deviceJoinName: "Aurora In-line Dimmer", ocfDeviceType: "oic.d.switch"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008", outClusters: "0019", manufacturer: "Aurora", model: "WallDimmerMaster", deviceJoinName: "Aurora Smart Rotary Dimmer", ocfDeviceType: "oic.d.switch"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0B05 ,1000, FEDC", outClusters: "000A, 0019", manufacturer: "Aurora", model: "FWST64Bulb50AU", deviceJoinName: "Aurora Dimmable Filament Vintage ST64"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0B05 ,1000, FEDC", outClusters: "000A, 0019", manufacturer: "Aurora", model: "FWG125Bulb50AU", deviceJoinName: "Aurora Dimmable Filament Vintage G125"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0B05 ,1000, FEDC", outClusters: "000A, 0019", manufacturer: "Aurora", model: "FWA60Bulb50AU", deviceJoinName: "Aurora Dimmable Filament Vintage GLS"

		
		fingerprint manufacturer: "IKEA of Sweden", model: "TRADFRI bulb E27 WW 806lm", deviceJoinName: "IKEA TRÅDFRI LED Bulb" 
		fingerprint manufacturer: "IKEA of Sweden", model: "TRADFRI bulb E27 WW clear 250lm", deviceJoinName: "IKEA TRÅDFRI LED Bulb" 

		
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0301, FC01", manufacturer: "ubisys", model: "D1 (5503)", deviceJoinName: "INGENIUM ZB Universal Dimming Module ZBM01d"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 1000", outClusters: "0019", manufacturer: "Megaman", model: "AD-DimmableLight3001", deviceJoinName: "INGENIUM ZB LED Classic"

		
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0B05, 1000, FC82", outClusters: "0019", manufacturer: "innr", model: "RF 263", deviceJoinName: "Innr Smart Filament Bulb Vintage"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0B05, 1000, FC82", outClusters: "0019", manufacturer: "innr", model: "BF 263", deviceJoinName: "Innr Smart Filament Bulb Vintage"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0B05, 1000, FC82", outClusters: "0019", manufacturer: "innr", model: "BF 265", deviceJoinName: "Innr Smart Filament Bulb"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0B05, 1000", outClusters: "0019", manufacturer: "innr", model: "AE 260", deviceJoinName: "Innr Smart Bulb"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0B05, 1000", outClusters: "0019", manufacturer: "innr", model: "BE 220", deviceJoinName: "Innr Smart Flood Light White"
		fingerprint manufacturer: "innr", model: "RF 265", deviceJoinName: "Innr Smart Filament Bulb White" 
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 1000", outClusters: "0019", manufacturer: "innr", model: "RB 265", deviceJoinName: "Innr Smart Bulb White"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 1000", outClusters: "0019", manufacturer: "innr", model: "RB 245", deviceJoinName: "Innr Smart Candle White"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 1000", outClusters: "0019", manufacturer: "innr", model: "RS 225", deviceJoinName: "Innr Smart Spot White"
		fingerprint manufacturer: "innr", model: "RF 261", deviceJoinName: "Light" 
		fingerprint manufacturer: "innr", model: "RF 264", deviceJoinName: "Light" 

		
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0B05, 1000, FEDC", outClusters: "000A, 0019", manufacturer: "Smarthome", model: "S111-201A", deviceJoinName: "Leedarson Dimmable White Bulb A19"
		fingerprint profileId: "0104", inClusters: "0000, 0004, 0003, 0006, 0008, 0005, FFFF, 1000", outClusters: "0019", manufacturer: "LDS", model: "ZBT-DIMLight-GLS0000", deviceJoinName: "Smart Bulb"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008", outClusters: "0019", manufacturer: "LDS", model: "ZHA-DIMLight-GLS0000", deviceJoinName: "Smart Bulb"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 1000", outClusters: "0019", manufacturer: "LDS", model: "ZBT-DIMLight-GLS", deviceJoinName: "Smart Bulb"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 1000", outClusters: "0019", manufacturer: "LDS", model: "ZBT-DIMLight-GLS0044", deviceJoinName: "Smart Bulb"

		
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008", outClusters: "0003, 0006, 0008, 0019, 0406", manufacturer: "Leviton", model: "DL6HD", deviceJoinName: "Leviton Dimmer Switch", ocfDeviceType: "oic.d.switch"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008", outClusters: "0003, 0006, 0008, 0019, 0406", manufacturer: "Leviton", model: "DL3HL", deviceJoinName: "Leviton Lumina RF Plug-In Dimmer", ocfDeviceType: "oic.d.switch"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008", outClusters: "0003, 0006, 0008, 0019, 0406", manufacturer: "Leviton", model: "DL1KD", deviceJoinName: "Leviton Lumina RF Dimmer Switch", ocfDeviceType: "oic.d.switch"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008", outClusters: "0003, 0006, 0008, 0019, 0406", manufacturer: "Leviton", model: "ZSD07", deviceJoinName: "Leviton Lumina RF 0-10V Dimming Wall Switch", ocfDeviceType: "oic.d.switch"

		
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 1000", outClusters: "0019", manufacturer: "MLI", model: "ZBT-DimmableLight", deviceJoinName: "Müller Licht Tint Bulb Dimming"

		
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0B04, FC0F", outClusters: "0019", manufacturer: "OSRAM", model: "LIGHTIFY A19 ON/OFF/DIM", deviceJoinName: "SYLVANIA Smart A19 Soft White"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, FC0F", outClusters: "0019", manufacturer: "OSRAM", model: "LIGHTIFY A19 ON/OFF/DIM 10 Year", deviceJoinName: "SYLVANIA Smart 10-Year A19"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0B05", outClusters: "0019", manufacturer: "OSRAM SYLVANIA", model: "iQBR30", deviceJoinName: "Sylvania Ultra iQ"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, FC0F", outClusters: "0019", manufacturer: "OSRAM", model: "LIGHTIFY PAR38 ON/OFF/DIM", deviceJoinName: "SYLVANIA Smart PAR38 Soft White"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0B04, FC0F", outClusters: "0019", manufacturer: "OSRAM", model: "LIGHTIFY BR ON/OFF/DIM", deviceJoinName: "SYLVANIA Smart BR30 Soft White"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0300, 0B04, 0B05, FC01, FC08", outClusters: "0003, 0019", manufacturer: "LEDVANCE", model: "A19 W 10 year", deviceJoinName: "SYLVANIA Smart 10Y A19 Soft White"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0300, 0B05, FC01", outClusters: "0003, 0019", manufacturer: "LEDVANCE", model: "BR30 W 10 year", deviceJoinName: "SYLVANIA Smart 10Y BR30 Soft White"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0300, 0B05, FC01", outClusters: "0003, 0019", manufacturer: "LEDVANCE", model: "PAR38 W 10 year", deviceJoinName: "SYLVANIA Smart 10Y PAR38 Soft White"

		
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008", outClusters: "0019", manufacturer: "LEEDARSON LIGHTING", model: "M350ST-W1R-01", deviceJoinName: "OZOM Dimmable LED Smart Light"

		
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0702, 0B05", outClusters: "0019", manufacturer: "sengled", model: "E11-G13", deviceJoinName: "Sengled Element Classic"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0702, 0B05", outClusters: "0019", manufacturer: "sengled", model: "E11-G14", deviceJoinName: "Sengled Element Classic"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0702, 0B05", outClusters: "0019", manufacturer: "sengled", model: "E11-G23", deviceJoinName: "Sengled Element Classic"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0702, 0B05", outClusters: "0019", manufacturer: "sengled", model: "E11-G33", deviceJoinName: "Sengled Element Classic"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0702, 0B05", outClusters: "0019", manufacturer: "sengled", model: "E12-N13", deviceJoinName: "Sengled Element Classic"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0702, 0B05", outClusters: "0019", manufacturer: "sengled", model: "E12-N14", deviceJoinName: "Sengled Element Classic"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0702, 0B05", outClusters: "0019", manufacturer: "sengled", model: "E12-N15", deviceJoinName: "Sengled Element Classic"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0702, 0B05", outClusters: "0019", manufacturer: "sengled", model: "E11-N13", deviceJoinName: "Sengled Element Classic"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0702, 0B05", outClusters: "0019", manufacturer: "sengled", model: "E11-N14", deviceJoinName: "Sengled Element Classic"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0300, 0702, 0B05", outClusters: "0019", manufacturer: "sengled", model: "E1A-AC2", deviceJoinName: "Sengled DownLight"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0702, 0B05", outClusters: "0019", manufacturer: "sengled", model: "E11-N13A", deviceJoinName: "Sengled Extra Bright Soft White"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0702, 0B05", outClusters: "0019", manufacturer: "sengled", model: "E11-N14A", deviceJoinName: "Sengled Extra Bright Daylight"
                fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0702, 0B05", outClusters: "0019", manufacturer: "sengled", model: "E21-N13A", deviceJoinName: "Sengled Soft White"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0702, 0B05", outClusters: "0019", manufacturer: "sengled", model: "E21-N14A", deviceJoinName: "Sengled Daylight"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0702, 0B05", outClusters: "0019", manufacturer: "sengled", model: "E11-U21U31", deviceJoinName: "Sengled Element Touch"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0702, 0B05, FC01", outClusters: "0019", manufacturer: "sengled", model: "E13-A21", deviceJoinName: "Sengled LED Flood Light"

		
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0B05, 1000, FEDC", outClusters: "000A, 0019", manufacturer: "LDS", model: "ZBT-DIMLight-GLS0006", deviceJoinName: "Smart Bulb"

		
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, FF00", outClusters: "0019", manufacturer: "MRVL", model: "MZ100", deviceJoinName: "Wemo Bulb"
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
		if (event.name=="level" && event.value==0) {}
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
			log.debug "${descMap}"
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
	def additionalCmds = []
	if (device.getDataValue("model") == "iQBR30" && value.toInteger() > 0) { 
		additionalCmds = zigbee.on()
	} else if (device.getDataValue("manufacturer") == "MRVL") { 
		additionalCmds = refresh()
	}
	zigbee.setLevel(value) + additionalCmds
}

def ping() {
	return zigbee.onOffRefresh()
}

def refresh() {
	zigbee.onOffRefresh() + zigbee.levelRefresh()
}

def installed() {
	if (((device.getDataValue("manufacturer") == "MRVL") && (device.getDataValue("model") == "MZ100")) || (device.getDataValue("manufacturer") == "OSRAM SYLVANIA") || (device.getDataValue("manufacturer") == "OSRAM")) {
		if ((device.currentState("level")?.value == null) || (device.currentState("level")?.value == 0)) {
			sendEvent(name: "level", value: 100)
		}
	}
}

def configure() {
	log.debug "Configuring Reporting and Bindings."
	
	
	sendEvent(name: "checkInterval", value: 2 * 10 * 60 + 1 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])

	
	refresh() + zigbee.onOffConfig(0, 300) + zigbee.levelConfig()
}
