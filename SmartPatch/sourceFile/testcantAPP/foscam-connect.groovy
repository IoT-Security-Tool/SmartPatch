"License"
"AS IS"

definition(
    name: "Foscam (Connect)",
    namespace: "smartthings",
    author: "SmartThings",
    description: "Connect and take pictures using your Foscam camera from inside the Smartthings app.",
    category: "SmartThings Internal",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Partner/foscam.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Partner/foscam@2x.png",
    singleInstance: true
)

preferences {
	page(name: "cameraDiscovery", title:"Foscam Camera Setup", content:"cameraDiscovery")
	page(name: "loginToFoscam", title: "Foscam Login")
}



def cameraDiscovery()
{
	if(canInstallLabs())
	{
		int refreshCount = !state.refreshCount ? 0 : state.refreshCount as int
		state.refreshCount = refreshCount + 1
		def refreshInterval = 3

		def options = camerasDiscovered() ?: []
		def numFound = options.size() ?: 0

		if(!state.subscribe) {
			subscribe(location, null, locationHandler, [filterEvents:false])
			state.subscribe = true
		}

		
		if((refreshCount % 5) == 0) {
			discoverCameras()
		}

		return dynamicPage(name:"cameraDiscovery", title:"Discovery Started!", nextPage:"loginToFoscam", refreshInterval:refreshInterval, uninstall: true) {
			section("Please wait while we discover your Foscam. Discovery can take five minutes or more, so sit back and relax! Select your device below once discovered.") {
				input "selectedFoscam", "enum", required:false, title:"Select Foscam (${numFound} found)", multiple:true, options:options
			}
		}
	}
	else
	{
		def upgradeNeeded = """To use Foscam, your Hub should be completely up to date.

		To update your Hub, access Location Settings in the Main Menu (tap the gear next to your location name), select your Hub, and choose "Update Hub"."""

		return dynamicPage(name:"cameraDiscovery", title:"Upgrade needed!", nextPage:"", install:false, uninstall: true) {
			section("Upgrade") {
				paragraph "$upgradeNeeded"
			}
		}

	}
}

def loginToFoscam() {
	def showUninstall = username != null && password != null
	return dynamicPage(name: "loginToFoscam", title: "Foscam", uninstall:showUninstall, install:true,) {
		section("Log in to Foscam") {
			input "username", "text", title: "Username", required: true, autoCorrect:false
			input "password", "password", title: "Password", required: true, autoCorrect:false
		}
	}
}



private discoverCameras()
{
	
	def action = new physicalgraph.device.HubAction("0b4D4F5F490000000000000000000000040000000400000000000001", physicalgraph.device.Protocol.LAN, "FFFFFFFF:2710")
	action.options = [type:"LAN_TYPE_UDPCLIENT"]
	sendHubCommand(action)
}

def camerasDiscovered() {
	def cameras = getCameras()
	def map = [:]
	cameras.each {
		def value = it.value.name ?: "Foscam Camera"
		def key = it.value.ip + ":" + it.value.port
		map["${key}"] = value
	}
	map
}


def getCameras()
{
	state.cameras = state.cameras ?: [:]
}


def installed() {
	
	initialize()

	runIn(300, "doDeviceSync" , [overwrite: false]) 

	
	
	
}


def updated() {
	
	unsubscribe()
	initialize()
}


def initialize() {
	
	unsubscribe()
	state.subscribe = false

	if (selectedFoscam)
	{
		addCameras()
	}
}

def addCameras() {
	def cameras = getCameras()

	selectedFoscam.each { dni ->
		def d = getChildDevice(dni)

		if(!d)
		{
			def newFoscam = cameras.find { (it.value.ip + ":" + it.value.port) == dni }
			d = addChildDevice("smartthings", "Foscam", dni, newFoscam?.value?.hub, ["label":newFoscam?.value?.name ?: "Foscam Camera", "data":["mac": newFoscam?.value?.mac, "ip": newFoscam.value.ip, "port":newFoscam.value.port], "preferences":["username":username, "password":password]])

			log.debug "created ${d.displayName} with id $dni"
		}
		else
		{
			log.debug "found ${d.displayName} with id $dni already exists"
		}
	}
}

def getDeviceInfo() {
	def devices = getAllChildDevices()
	devices.each { d ->
		d.getDeviceInfo()
	}
}


def locationHandler(evt) {
	












	def description = evt.description
	def hub = evt?.hubId

	log.debug "GOT LOCATION EVT: $description"

	def parsedEvent = stringToMap(description)

	
	if (parsedEvent?.type == "LAN_TYPE_UDPCLIENT" && parsedEvent?.payload?.startsWith("4D4F5F49"))
	{
		def unpacked = [:]
		unpacked.mac = parsedEvent.mac.toString()
		unpacked.name = hexToString(parsedEvent.payload[72..113]).trim()
		unpacked.ip = parsedEvent.payload[114..121]
		unpacked.subnet = parsedEvent.payload[122..129]
		unpacked.gateway = parsedEvent.payload[130..137]
		unpacked.dns = parsedEvent.payload[138..145]
		unpacked.reserve = parsedEvent.payload[146..153]
		unpacked.sysVersion = parsedEvent.payload[154..161]
		unpacked.appVersion = parsedEvent.payload[162..169]
		unpacked.port = parsedEvent.payload[170..173]
		unpacked.dhcp = parsedEvent.payload[174..175]
		unpacked.hub = hub

		def cameras = getCameras()
		if (!(cameras."${parsedEvent.mac.toString()}"))
		{
			cameras << [("${parsedEvent.mac.toString()}"):unpacked]
		}
	}
}


private Boolean canInstallLabs()
{
	return hasAllHubsOver("000.011.00603")
}

private Boolean hasAllHubsOver(String desiredFirmware)
{
	return realHubFirmwareVersions.every { fw -> fw >= desiredFirmware }
}

private List getRealHubFirmwareVersions()
{
	return location.hubs*.firmwareVersionString.findAll { it }
}

private String hexToString(String txtInHex)
{
	byte [] txtInByte = new byte [txtInHex.length() / 2];
	int j = 0;
	for (int i = 0; i < txtInHex.length(); i += 2)
	{
			txtInByte[j++] = Byte.parseByte(txtInHex.substring(i, i + 2), 16);
	}
	return new String(txtInByte);
}
