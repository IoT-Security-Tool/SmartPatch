"License"
"AS IS"
import physicalgraph.zigbee.clusters.iaszone.ZoneStatus
import physicalgraph.zigbee.zcl.DataType

metadata {
	definition(name: "Orvibo Gas Detector", namespace: "smartthings", author: "SmartThings", runLocally: false, minHubCoreVersion: '000.017.0012', executeCommandsLocally: false, mnmn: "SmartThings", vid: "SmartThings-smartthings-Orvibo_Gas_Sensor") {
		capability "Smoke Detector"
		capability "Configuration"
		capability "Health Check"
		capability "Sensor"
		capability "Refresh"
		fingerprint profileId: "0104", deviceId: "0402", inClusters: "0000, 0003, 0500, 0009", outClusters: "0019", manufacturer: "Heiman", model:"d0e857bfd54f4a12816295db3945a421", deviceJoinName: "欧瑞博 可燃气体报警器(SG21)"
		fingerprint profileId: "0104", deviceId: "0402", inClusters: "0000, 0003, 0500, 0009", outClusters: "0019", manufacturer: "HEIMAN", model:"358e4e3e03c644709905034dae81433e", deviceJoinName: "欧瑞博 可燃气体报警器(SG21)"
		fingerprint profileId: "0104", deviceId: "0402", inClusters: "0000, 0003, 0500", outClusters: "0019", manufacturer: "HEIMAN", model:"GASSensor-N", deviceJoinName: "HEIMAN Gas Detector (HS3CG)"
	}

	simulator {
		status "active": "zone status 0x0001 -- extended status 0x00"
	}

	tiles {
		standardTile("smoke", "device.smoke", width: 2, height: 2) {
			state("clear", label: "Clear", icon:"st.alarm.smoke.clear", backgroundColor:"#ffffff")
			state("detected", label: "Smoke!", icon:"st.alarm.smoke.smoke", backgroundColor:"#e86d13")
		}
		standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
			state "default", action: "refresh.refresh", icon: "st.secondary.refresh"
		}
		main "smoke"
		details(["smoke","refresh"])
	}
}
def installed() {
	log.debug "installed"
	refresh()
}
def parse(String description) {
	log.debug "description(): $description"
	def map = zigbee.getEvent(description)
	if (!map) {
		if (description?.startsWith('zone status')) {
			map = parseIasMessage(description)
		} else {
			map = parseAttrMessage(description)
		}
	}
	log.debug "Parse returned $map"
	def result = map ? createEvent(map) : [:]
	if (description?.startsWith('enroll request')) {
		List cmds = zigbee.enrollResponse()
		log.debug "enroll response: ${cmds}"
		result = cmds?.collect { new physicalgraph.device.HubAction(it)}
	}

	return result
}

def parseAttrMessage(String description){
	def descMap = zigbee.parseDescriptionAsMap(description)
	def map = [:]
	if (descMap?.clusterInt == zigbee.IAS_ZONE_CLUSTER && descMap.attrInt == zigbee.ATTRIBUTE_IAS_ZONE_STATUS) {
		def zs = new ZoneStatus(zigbee.convertToInt(descMap.value, 16))
		map = getDetectedResult(zs.isAlarm1Set() || zs.isAlarm2Set())
	}
	return map;
}

def parseIasMessage(String description) {
	ZoneStatus zs = zigbee.parseZoneStatus(description)
	return getDetectedResult(zs.isAlarm1Set() || zs.isAlarm2Set())
}
def getDetectedResult(value) {
	def detected = value ? 'detected': 'clear'
	String descriptionText = "${device.displayName} smoke ${detected}"
	return [name:'smoke',
			value: detected,
			descriptionText:descriptionText,
			translatable:true]
}
def refresh() {
	log.debug "Refreshing Values"
	def refreshCmds = []
	refreshCmds += zigbee.readAttribute(zigbee.IAS_ZONE_CLUSTER, zigbee.ATTRIBUTE_IAS_ZONE_STATUS)
	return refreshCmds
}

def ping() {
	log.debug "ping"
	refresh()
}
def configure() {
	log.debug "configure"
	sendEvent(name: "checkInterval", value: 30 * 60 + 2 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID, offlinePingable: "1"])
	return refresh() + zigbee.enrollResponse()
}