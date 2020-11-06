



metadata {
	
	definition (name: "Hue Bridge", namespace: "smartthings", author: "SmartThings") {
		capability "Bridge"
		capability "Health Check"

		attribute "networkAddress", "string"
		
		
		attribute "status", "string"
		
		
		attribute "idNumber", "string"
	}

	simulator {
		
	}

	tiles(scale: 2) {
     	multiAttributeTile(name: "rich-control", type: "generic", width: 6, height: 4, canChangeIcon: true) {
			tileAttribute ("device.status", key: "PRIMARY_CONTROL") {
				attributeState "Offline", label: '${currentValue}', action: "", icon: "st.Lighting.light99-hue", backgroundColor: "#ffffff"
	            attributeState "Online", label: '${currentValue}', action: "", icon: "st.Lighting.light99-hue", backgroundColor: "#00A0DC"
			}
			}
		valueTile("doNotRemove", "v", decoration: "flat", height: 2, width: 6, inactiveLabel: false) {
			state "default", label:'If removed, Hue lights will not work properly'
		}
		valueTile("idNumber", "device.idNumber", decoration: "flat", height: 2, width: 6, inactiveLabel: false) {
			state "default", label:'ID: ${currentValue}'
		}
		valueTile("networkAddress", "device.networkAddress", decoration: "flat", height: 2, width: 6, inactiveLabel: false) {
			state "default", label:'IP: ${currentValue}'
		}

		main (["rich-control"])
		details(["rich-control", "doNotRemove", "idNumber", "networkAddress"])
	}
}

def initialize() {
	sendEvent(name: "DeviceWatch-Enroll", value: "{\"protocol\": \"LAN\", \"scheme\":\"untracked\", \"hubHardwareId\": \"${device.hub.hardwareID}\"}", displayed: false)
}

void installed() {
	log.debug "installed()"
	initialize()
}

def updated() {
	log.debug "updated()"
	initialize()
}


def parse(description) {
	log.debug "Parsing '${description}'"
	def results = []
	def result = parent.parse(this, description)
	if (result instanceof physicalgraph.device.HubAction){
		log.trace "HUE BRIDGE HubAction received -- DOES THIS EVER HAPPEN?"
		results << result
	} else if (description == "updated") {
		
		log.trace "HUE BRIDGE was updated"
	} else {
		def map = description
		if (description instanceof String)  {
			map = stringToMap(description)
		}
		if (map?.name && map?.value) {
			log.trace "HUE BRIDGE, GENERATING EVENT: $map.name: $map.value"
			results << createEvent(name: "${map.name}", value: "${map.value}")
		} else {
			log.trace "Parsing description"
			def msg = parseLanMessage(description)
			if (msg.body) {
				def contentType = msg.headers["Content-Type"]
				if (contentType?.contains("json")) {
					def bulbs = new groovy.json.JsonSlurper().parseText(msg.body)
					if (bulbs.state) {
						log.info "Bridge response: $msg.body"
					}
				} else if (contentType?.contains("xml")) {
					log.debug "HUE BRIDGE ALREADY PRESENT"
					parent.hubVerification(device.hub.id, msg.body)
				}
			}
		}
	}
	results
}
