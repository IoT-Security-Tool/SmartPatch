"License"
"AS IS"
 metadata {
	definition (name: "Foscam", namespace: "smartthings", author: "SmartThings") {
		capability "Actuator"
		capability "Sensor"
		capability "Image Capture"
	}

	simulator {
		
	}

	
	preferences {
		input "username", "text", title: "Username", description: "Your Foscam Username", required: false
		input "password", "password", title: "Password", description: "Your Foscam Password", required: false
	}

	tiles {
		standardTile("image", "device.image", width: 1, height: 1, canChangeIcon: false, inactiveLabel: true, canChangeBackground: true) {
			state "default", label: "", action: "", icon: "st.camera.dropcam-centered", backgroundColor: "#FFFFFF"
		}

		carouselTile("cameraDetails", "device.image", width: 3, height: 2) { }

		standardTile("take", "device.image", width: 1, height: 1, canChangeIcon: false, inactiveLabel: true, canChangeBackground: false) {
			state "take", label: "Take", action: "Image Capture.take", icon: "st.camera.dropcam", backgroundColor: "#FFFFFF", nextState:"taking"
			state "taking", label:'Taking', action: "", icon: "st.camera.dropcam", backgroundColor: "#00A0DC"
			state "image", label: "Take", action: "Image Capture.take", icon: "st.camera.dropcam", backgroundColor: "#FFFFFF", nextState:"taking"
		}

		"refresh""device.switch""flat""default""getDeviceInfo""st.secondary.refresh"

		main "image"
		details(["cameraDetails", "take"])
	}
}


def parse(String description) {
	log.debug "Parsing '${description}'"

	def map = stringToMap(description)
	log.debug map

	def result = []

	if (map.bucket && map.key)
	{ 
		putImageInS3(map)
	}
	else if (map.headers && map.body)
	{ 

		

		def headerString = new String(map.headers.decodeBase64())
		if (headerString.contains("404 Not Found")) {
			state.snapshot = "/snapshot.cgi"
		}

		if (map.body) {
			def bodyString = new String(map.body.decodeBase64())
			def body = new XmlSlurper().parseText(bodyString)
			def productName = body?.productName?.text()
			if (productName)
			{
				log.trace "Got Foscam Product Name: $productName"
				state.snapshot = "/cgi-bin/CGIProxy.fcgi"
			}
		}
	}

	result
}

def putImageInS3(map) {

	def s3ObjectContent

	try {
		def imageBytes = getS3Object(map.bucket, map.key + ".jpg")

		if(imageBytes)
		{
			s3ObjectContent = imageBytes.getObjectContent()
			def bytes = new ByteArrayInputStream(s3ObjectContent.bytes)
			storeImage(getPictureName(), bytes)
		}
	}
	catch(Exception e) {
		log.error e
	}
	finally {
		
		if (s3ObjectContent) { s3ObjectContent.close() }
	}
}


def take() {
	log.debug "Executing 'take'"
	
	
	

	def host = getHostAddress()
	def port = host.split(":")[1]
	def path = (port == "80") ? "/snapshot.cgi?user=${getUsername()}&pwd=${getPassword()}" : "/cgi-bin/CGIProxy.fcgi?usr=${getUsername()}&pwd=${getPassword()}&cmd=snapPicture2"


	def hubAction = new physicalgraph.device.HubAction(
		method: "GET",
		path: path,
		headers: [HOST:host]
	)
	hubAction.options = [outputMsgToS3:true]
	hubAction
}

"Executing 'getDeviceInfo'""/cgi-bin/CGIProxy.fcgi""GET""getDevInfo"


private getPictureName() {
	def pictureUuid = java.util.UUID.randomUUID().toString().replaceAll('-', '')
	return device.deviceNetworkId + "_$pictureUuid" + ".jpg"
}

private getUsername() {
	settings.username
}

private getPassword() {
	settings.password
}

private Integer convertHexToInt(hex) {
	Integer.parseInt(hex,16)
}

private String convertHexToIP(hex) {
	[convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}

private getHostAddress() {
	def parts = device.deviceNetworkId.split(":")
	def ip = convertHexToIP(parts[0])
	def port = convertHexToInt(parts[1])
	return ip + ":" + port
}

private hashMD5(String somethingToHash) {
	java.security.MessageDigest.getInstance("MD5").digest(somethingToHash.getBytes("UTF-8")).encodeHex().toString()
}

private calcDigestAuth(String method, String uri) {
	def HA1 =  hashMD5("${getUsername}::${getPassword}")
	def HA2 = hashMD5("${method}:${uri}")
	def response = hashMD5("${HA1}::::auth:${HA2}")

	'Digest username="'+ getUsername() + '", realm="", nonce="", uri="'+ uri +'", qop=auth, nc=, cnonce="", response="' + response + '", opaque=""'
}
