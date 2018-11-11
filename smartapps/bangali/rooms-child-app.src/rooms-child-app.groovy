/***********************************************************************************************************************
*
*  A SmartThings child smartapp which creates the "room" device using the rooms occupancy DTH and allows executing
*   various rules based on occupancy state. this alllows lights and other devices to be turned on and off based on
*   occupancy. it also allows many other actions like executing a routine or piston, turning on/off music and much
*   more. see the wiki for more details. (note wiki is still in progress. ok there is really no content in the wiki.
*   yet. but this is to reinforce my intention of putting the wiki together. ;-) will update with link once in place.)
*
*  Copyright (C) 2017 bangali
*
*  Contributors:
*   https://github.com/Johnwillliam
*   https://github.com/TonyFleisher
*   https://github.com/BamaRayne
*
*  License:
*  This program is free software: you can redistribute it and/or modify it under the terms of the GNU
*  General Public License as published by the Free Software Foundation, either version 3 of the License, or
*  (at your option) any later version.
*
*  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
*  implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
*  for more details.
*
*  You should have received a copy of the GNU General Public License along with this program.
*  If not, see <http://www.gnu.org/licenses/>.
*
*  Attribution:
*   icons licensed and used with permission from: https://www.iconfinder.com/aha-soft
*   H letter icon resused from user: https://www.flickr.com/photos/lwr/ under CC BY-NC-SA 2.0
*
*	convertRGBToHueSaturation(...) adpated from code by ady624 for webCoRE. original code can be found at:
*		https://github.com/ady624/webCoRE/blob/master/smartapps/ady624/webcore-piston.src/webcore-piston.groovy
*	colorsRGB array color name and RGB values from code by ady624 for webCoRE.
*
*  Name: Rooms Child App
*   Source: https://github.com/adey/bangali/blob/master/smartapps/bangali/rooms-child-app.src/rooms-child-app.groovy
*
***********************************************************************************************************************/

public static String version()		{  return "v0.96.0"  }
boolean isDebug()					{  return debugLogging  }

import groovy.transform.Field

@Field final String lastMotionActive   = '1'
@Field final String lastMotionInactive = '2'

@Field final String occupancy  = 'occupancy'

@Field final String asleep   = 'asleep'
@Field final String engaged  = 'engaged'
@Field final String occupied = 'occupied'
@Field final String vacant   = 'vacant'
@Field final String checking = 'checking'
@Field final String locked   = 'locked'

@Field final String open        = 'open'
@Field final String closed      = 'closed'
@Field final String active      = 'active'
@Field final String inactive    = 'inactive'
@Field final String on          = 'on'
@Field final String off         = 'off'
@Field final String present     = 'present'
@Field final String notpresent  = 'not present'

// @Field final String noTraffic       = '0'
@Field final String lightTraffic   = '5'
@Field final String mediumTraffic  = '7'
@Field final String heavyTraffic   = '9'

@Field final int    pauseMSecST = 10
@Field final int    pauseMSecHU = 50

@Field final int    fanLow      = 33
@Field final int    fanMedium   = 67
@Field final int    fanHigh     = 99

@Field final String _SmartThings = 'ST'
@Field final String _Hubitat     = 'HU'

@Field final Map   occupancyButtons =
		[1:"occupied",2:"checking",3:"vacant",4:"locked",5:"reserved",6:"kaput",7:"donotdisturb",8:"asleep",9:"engaged"]
@Field final Map    genericButtons =
		[1:"One",2:"Two",3:"Three",4:"Four",5:"Five",6:"Six",7:"Seven",8:"Eight",9:"Nine",10:"Ten",11:"Eleven",12:"Twelve",13:"Thirteen",14:"Fourteen",15:"Fifteen",16:"Sixteen"]

@Field final String padChar = '･'

@Field final String _ImgSize = '36'

@Field final String pushAButton = 'pushableButton'
@Field final String holdAButton = 'holdableButton'
@Field final String doubleTapAButton = 'doubleTapableButton'

@Field final Map    heCapToAttrMap = [(pushAButton):'pushed', (holdAButton):'held', (doubleTapAButton):'doubleTapped']

@Field final int    maxRules    = 10
@Field final int    maxHolis    = 10
@Field final int    maxButtons  = 5

@Field final long   _ProcessCHEvery = 30000L
@Field final long   _ProcessHMEvery = 30000L

@Field final String _ERule      = 'e'
@Field final String _TRule      = 't'
@Field final String _HRule      = 'h'

@Field final String _TapToChg   = 'Tap to change existing settings'
@Field final String _TapToCon   = 'Tap to configure'

@Field final String _timeSunrise = '1'
@Field final String _timeSunset	 = '2'
@Field final String _timeTime	 = '3'

definition	(
	name: "rooms child app",
	namespace: "bangali",
	parent: "bangali:rooms manager",
	author: "bangali",
	description: "DO NOT INSTALL DIRECTLY. Rooms child smartapp will create new rooms using 'rooms occupancy' DTH from the Rooms Manager smartapp.",
	category: "My Apps",
	iconUrl: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomOccupancy.png",
	iconX2Url: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomOccupancy@2x.png",
	iconX3Url: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomOccupancy@3x.png"
)

preferences {
	page(name: "roomName", title: "Room Name and Settings")
	page(name: "pageOnePager", title: "Easy Settings")
	page(name: "pageOccupiedSettings", title: "Occupied State Settings")
	page(name: "pageEngagedSettings", title: "Engaged State Settings")
	page(name: "pageCheckingSettings", title: "Checking State Settings")
	page(name: "pageVacantSettings", title: "Vacant State Settings")
	page(name: "pageOtherDevicesSettings", title: "Room Devices")
	page(name: "pageAutoLevelSettings", title: "Light Auto Level Settings")
	page(name: "pageHolidayLightPatterns", title: "Holiday Light Patterns")
	page(name: "pageHolidayLight", title: "Holiday Light Pattern")
	page(name: "pageRules", title: "Maintain Rules")
	page(name: "pageRuleDelete", title: "Delete Rules")
	page(name: "pageRule", title: "Edit Lighting Rule")
	page(name: "pageHumidity", title: "Edit Rule Humidity")
	page(name: "pageRuleDate", title: "Edit Rule Date")
	page(name: "pageRuleTime", title: "Edit Rule Time")
	page(name: "pageRuleCommands", title: "Edit Device Commands Rule Settings")
	page(name: "pageRuleOthers", title: "Edit Other Execution Rule Settings")
	page(name: "pageRuleTimer", title: "Edit Rule Timers")
	page(name: "pageAsleepSettings", title: "Asleep State Settings")
	page(name: "pageLockedSettings", title: "Locked State Settings")
	page(name: "pagePowerTime", title: "Power Time Range")
	page(name: "pageAdjacentRooms", title: "Adjacent Rooms Settings")
	page(name: "pageRoomTemperature", title: "Room Temperature Settings")
	page(name: "pageRoomHumidity", title: "Room Humidity Settings")
	page(name: "pageAnnouncementSettings", title: "Announcement Settings")
	page(name: "pageAnnouncementSpeakerTimeSettings", title: "Announcement Speaker Settings")
	page(name: "pageAnnouncementColorTimeSettings", title: "Announcement Color Settings")
	page(name: "pageAnnounceContacts", title: "Announce Contact Settings")
	page(name: "pageGeneralSettings", title: "General Settings")
	page(name: "pageAllSettings", title: "All Settings")
	page(name: "pageRoomButton", title: "Room Button Settings")
	page(name: "pageButtonDetails", title: "Button Details Settings")
}

/*
def roomName()	{
	def app = spawnChildApp(app.label)
	"${app.roomName()}"
}
*/

def roomName()	{
	def roomNames = parent.getRoomNames(app.id)
	def luxAndTimeSettings = (luxSensor || timeSettings)
	def autoLevelSettings = (minLevel || maxLevel || state.ruleHasAL || autoColorTemperature)
	def holidayLightsSettings = false
	for (def i = 1; i <= maxHolis; i++)
		if (settings["holiName$i"] || settings["holiColorString$i"])	{
			holidayLightsSettings = true
			break
		}
	def timeSettings = (fromTimeType || toTimeType)
	def adjRoomSettings = (adjRooms ? true : false)
	def miscSettings = (awayModes || pauseModes || dayOfWeek)
	def engagedSettings = (busyCheck || engagedButton || buttonIs || engagedSwitch || contactSensor || noMotionEngaged)
	def otherDevicesSettings = (personsPresence || luxAndTimeSettings || musicDevice || powerMeter)
	def asleepSettings = (asleepSensor || nightSwitches)
	state.passedOn = false
	state.holiPassedOn = false
	def roomIconURL
	roomIconURL = (iconURL ? iconURL : _RIimage)
	def hT = getHubType()
	def playerDevice = (speakerDevices || speechDevices || musicPlayers || (hT == _SmartThings && listOfMQs) ? true : false)
	dynamicPage(name:"roomName", title:"MAIN SETTINGS PAGE", install:true, uninstall:childCreated())		{
		section("")		{
			if (!childCreated())	{
				paragraph "ENTER ROOM NAME AND SAVE THE ROOM. THEN EDIT ROOM, TO ADD SETTINGS AND RULES. DO NOT ADD SETTINGS AND RULES WITHOUT FIRST SAVING THE ROOM ONCE."
				label title:"${addImage(roomIconURL)}Room Name:", required:true, image:(hT != _Hubitat ? "$roomIconURL" : null)
			}
			else
				label title:"${addImage(roomIconURL)}Room Name:", required:true, image:(hT != _Hubitat ? "$roomIconURL" : null)
		}
		section("")	{
			input "onePager", "bool", title:"${addImage(_OPimage)}Switch to easy settings?", required:false, defaultValue:false, submitOnChange:true, image:(hT != _Hubitat ? _OPimage : null)
		}
		if (onePager)	{
			section("")	{
				paragraph "App is in easy settings mode. Few settings are shown for first time users to get started quickly. For additional settings please unset the toggle above. Any settings entered will be preserved."
				href "pageOnePager", title:"${addImage(_REimage)}EASY SETTINGS", description:(motionSensors ? _TapToChg : _TapToCon), image:(hT != _Hubitat ? _REimage : null)
			}
		}
		else    {
			section("")	{
				input "hideAdvanced", "bool", title:"${addImage(_HAimage)}Hide advanced settings?", required:false, defaultValue:true, submitOnChange:true, image:(hT != _Hubitat ? _HAimage : null)
				if (hideAdvanced)
					paragraph "Advanced settings are currently hidden so if you are still getting familiar with the app you can start with commonly used settings. To see all settings please unset the toggle above. Any settings entered will be preserved."
			}
			section("")	{
					href "pageOtherDevicesSettings", title:"${addImage(_ODimage)}ROOM DEVICES", description:(otherDevicesSettings ? _TapToChg : _TapToCon), image:(hT != _Hubitat ? _ODimage : null)
			}
			section("")	{
					href "pageOccupiedSettings", title:"${addImage(_OCimage)}OCCUPIED SETTINGS", description:(hasOccupiedDevice() ? _TapToChg : _TapToCon), image:(hT != _Hubitat ? _OCimage : null)
			}
			section("")	{
					href "pageEngagedSettings", title:"${addImage(_ENimage)}ENGAGED SETTINGS", description:(engagedSettings ? _TapToChg : _TapToCon), image:(hT != _Hubitat ? _ENimage : null)
			}
			if (!hideAdvanced)	{
				section("")	{
						href "pageCheckingSettings", title:"${addImage(_CHimage)}CHECKING SETTINGS", description:((dimTimer || dimByLevel) ? _TapToChg : _TapToCon), image:(hT != _Hubitat ? _CHimage : null)
				}
				section("")	{
						href "pageVacantSettings", title:"${addImage(_VAimage)}VACANT SETTINGS", description:(vacantButton || vacantSwitches || turnOffMusic ? _TapToChg : _TapToCon), image:(hT != _Hubitat ? _VAimage : null)

				}
				section("")	{
						href "pageAsleepSettings", title:"${addImage(_ASimage)}ASLEEP SETTINGS", description:(asleepSettings ? _TapToChg : _TapToCon), image:(hT != _Hubitat ? _ASimage : null)
				}
				section("")	{
						href "pageLockedSettings", title:"${addImage(_LOimage)}LOCKED SETTINGS", description:(lockedSwitch ? _TapToChg : _TapToCon), image:(hT != _Hubitat ? _LOimage : null)
				}
			}
			section("")	{
					href "pageAutoLevelSettings", title:"${addImage(_ALimage)}AUTO LEVEL 'AL' SETTINGS", description:(autoLevelSettings ? _TapToChg : _TapToCon), image:(hT != _Hubitat ? _ALimage : null)
			}
			if (!hideAdvanced)	{
				section("")	{
						href "pageHolidayLightPatterns", title:"${addImage(_HLimage)}HOLIDAY LIGHTS 'HL' SETTINGS", description:(holidayLightsSettings ? _TapToChg : _TapToCon), image:(hT != _Hubitat ? _HLimage : null)
				}
				section("")	{
						href "pageRoomTemperature", title:"${addImage(_RTimage)}TEMPERATURE SETTINGS", description:(tempSensors || maintainRoomTemp ? _TapToChg : _TapToCon), image:(hT != _Hubitat ? _RTimage : null)
				}
				section("")	{
						href "pageRoomHumidity", title:"${addImage(_RHimage)}HUMIDITY SETTINGS", description:(tempSensors || maintainRoomTemp ? _TapToChg : _TapToCon), image:(hT != _Hubitat ? _RHimage : null)
				}
			}
			section("")	{
					href "pageRules", title: "${addImage(_RUimage)}MAINTAIN RULES", description:"Create/Edit/Disable rules", image:(hT != _Hubitat ? _RUimage : null)
			}
			if (!hideAdvanced)	{
				section("")	{
					href "pageAdjacentRooms", title:"${addImage(_ARimage)}ADJACENT ROOMS SETTINGS", description:(adjRoomSettings ? _TapToChg : _TapToCon), image:(hT != _Hubitat ? _ARimage : null)
				}
				section("")	{
					href "pageAnnouncementSettings", title:"${addImage(_ANimage)}ANNOUNCEMENT SETTINGS", description:(playerDevice || announceSwitches ? _TapToChg : _TapToCon), image:(hT != _Hubitat ? _ANimage : null)
				}
			}
			section("")	{
					href "pageGeneralSettings", title:"${addImage(_GEimage)}GENERAL SETTINGS", description:(miscSettings ? _TapToChg : _TapToCon), image:(hT != _Hubitat ? _GEimage : null)
			}
		}
		section("")	{
				href "pageAllSettings", title:"${addImage(_VIimage)}VIEW ALL SETTINGS", description:"Tap to view all settings", image:(hT != _Hubitat ? _VIimage : null)
		}
		section("")	{
			href "", title:"${addImage(_GHimage)}Detailed readme on Github", style:"external", url:_gitREADME, description:"Click link to open in browser", image:(hT != _Hubitat ? _GHimage : null), required:false
		}
		if (hT == _SmartThings)
			remove("Remove Room", "Remove Room ${app.label}")
		else
			remove
	}
}

private addImage(url)	{
	return (getHubType() == _Hubitat ? "<img src=$url height=$_ImgSize width=$_ImgSize>  " : '')
}

private getHubType()	{
	if (!state.hubId)	state.hubId = location.hubs[0].id.toString()
	if (state.hubId.length() > 5)	return _SmartThings;
	else							return _Hubitat;
}

def pageOnePager()	{
	dynamicPage(name:"pageOnePager", title:"One Pager", install:false, uninstall:false)	{
		section("Motion sensor for OCCUPIED:", hideable: false)	{
			inputDRMS('motionSensors', 'motionSensor', 'Room motion sensors?', true, true, true)
			if (motionSensors)
				inputERMSDO('whichNoMotion', 'Use which motion event for timeout?', true, false, true, 2, [[1:"Last Motion Active"], [2:"Last Motion Inactive"]])
			else
				paragraph "Use which motion event for timeout?\nselect motion sensor above to set"
		}
		section("Timeout for OCCUPIED:", hideable:fase)	{
				inputNRDRS('noMotionOccupied', "After how many seconds?", true, 300, "5..99999")
		}
		section("Change room to ENGAGED with traffic?", hideable: false)		{
			if (motionSensors)
				inputERMSDO('busyCheck', 'When room is busy?', false, false, false, 7, [[null:"No auto engaged"],[5:"Light traffic"],[7:"Medium Traffic"],[9:"Heavy Traffic"]])
			else
				paragraph "When room is busy?\nselect motion sensors above to set."
		}
		section("Timeout for ENGAGED:", hideable:false)	{
				inputNRDRS('noMotionEngaged', "After how many seconds?", false, 1800, "5..99999")
		}
		section("Timeout for CHECKING:", hideable: false)		{
			inputNRDRS('dimTimer', "After how many seconds?", true, 90, "5..99999", true)
			if (dimTimer)	{
//				inputERMSDO('dimByLevel', 'Dim lights that are on by what level?', false, false, false, null, [[10:"10%"],[20:"20%"],[30:"30%"],[40:"40%"],[50:"50%"],[60:"60%"],[70:"70%"],[80:"80%"],[90:"90%"]])
				inputNRDRS('dimByLevel', 'If any light is on dim by what level?', false, null, "1..99")
//				inputERMSDO('dimToLevel', 'If no light is on turn on at what level?', false, false, false, null, [[1:"1%"],[10:"10%"],[20:"20%"],[30:"30%"],[40:"40%"],[50:"50%"],[60:"60%"],[70:"70%"],[80:"80%"],[90:"90%"]])
				inputNRDRS('dimToLevel', 'If no light is on turn on and dim to what level?', false, null, "1..99")
			}
			else    {
				paragraph "If any light is on dim by what level?\nselect timer seconds above to set"
				paragraph "If no light is on turn on room lights and dim to what level?"
			}
		}
		section("States and switches:", hideable:false)	{
			inputERMSDO('state1', 'Which state?', true, true, false, [occupied, engaged], [occupied, engaged])
//			input "switchesOn1", "capability.switch", title: "Turn on which switches?", required: true, multiple: true
			inputDRMS('switchesOn1', 'switch', 'Turn on which switches?', true, true)
			inputERMSDO('setLevelTo1', 'Set level when turning on?', false, false, false, null, [[1:"1%"],[5:"5%"],[10:"10%"],[15:"15%"],[20:"20%"],[25:"25%"],[30:"30%"],[40:"40%"],[50:"50%"],[60:"60%"],[70:"70%"],[80:"80%"],[90:"90%"],[99:"99%"],[100:"100%"]])
		}
		section("Turn off all switches on no rule match?", hideable: false)		{
			input "allSwitchesOff", "bool", title:"Turn OFF all switches?", required:true, defaultValue:true
		}
	}
}

def pageOtherDevicesSettings()	{
	def buttonNames = genericButtons
	def roomButtonOptions = [:]
	if (roomButton)	{
		def roomButtonAttributes = roomButton.supportedAttributes
		def attributeNameFound = false
//		roomButtonAttributes.each	{ att ->
		for (def att : roomButtonAttributes)		{
			if (att.name == occupancy)		buttonNames = occupancyButtons;
			if (att.name == 'numberOfButtons')		attributeNameFound = true;
		}
		def numberOfButtons = roomButton.currentNumberOfButtons
		if (attributeNameFound && numberOfButtons)
			for (def i = 1; i <= numberOfButtons && i <= 16; i++)		roomButtonOptions << [(i.toString()):(buttonNames[i])];
		else
			roomButtonOptions << [null:"No buttons"]
	}
	def hT = getHubType()
	def roomMotionSensors = motionSensors.collect{ [(it.id): "${it.displayName}"] }
	if (ht == _Hubitat && roomButton && !roomButtonType)		app.updateSetting("roomButtonType", [type:"enum", value:"$pushAButton"]);
	dynamicPage(name: "pageOtherDevicesSettings", title:"Room Sensors", install:false, uninstall:false)	{
		section("MOTION SENSORS:", hideable:false)	{
//			input "motionSensors", "capability.motionSensor", title: "Room motion sensors?", required: false, multiple: true, submitOnChange: true
			inputDRMS('motionSensors', 'motionSensor', 'Room motion sensors?', false, true, true)
			if (!hideAdvanced)
				inputERMSDO('triggerMotionSensors', 'Room motion sensors that trigger from VACANT?', false, true, false, null, roomMotionSensors)
			if (motionSensors)
				inputERMSDO('whichNoMotion', 'Use which motion event for timeout?', true, false, false, 2, [[1:"Last Motion Active"],[2:"Last Motion Inactive"]])
			else
				paragraph "Use which motion event for timeout?\nselect motion sensor above to set"
		}
		section("ROOM BUTTONS:", hideable:false)	{
/*            if (hT == _Hubitat)
				input "roomButtonType", "enum", title: "Button type?", required: (roomButton ? true : false), multiple: false, defaultValue: null, submitOnChange: true, options: [["$pushAButton":'Push button'],["$holdAButton":'Hold button'],["$doubleTapAButton":'Double tap button']]
			if (hT == _SmartThings || roomButtonType)
				input "roomButton", "capability.${(hT == _SmartThings ? 'button' : roomButtonType)}", title: "Button to rotate states?", required: false, multiple: false, submitOnChange: true
			else
				paragraph "Button to rotate states?\nselect button type to set"
			if (roomButton)
				input "buttonForRoom", "enum", title: "Button Number?", required: true, multiple: false, defaultValue: null, options: roomButtonOptions
			else
				paragraph "Button number?\nselect button to set."
*/
			def hasButton = false
			for (def i = 0; i <= maxButtons; i++)	{
				if (settings["roomButton$i"] || settings["roomButtonNumber$i"])	{
					hasButton = true
					break
				}
			}
			href "pageRoomButton", title:"ROOM BUTTON SETTINGS", description:(hasButton ? "Tap to change existing settings" : "Tap to configure")
			if (hasButton)
				inputERMSDO('roomButtonStates', 'Rotate thru which room states?', true, true, false, null, [engaged, occupied, asleep, locked, vacant])
			else
				paragraph "Rotate thru which states?"
		}
		section("PRESENCE SENSORS:", hideable:false)	{
			inputDRMS('personsPresence', 'presenceSensor', 'Presence sensors?', false, true)
		}
		section("LUX SENSORS:", hideable:false)	{
			inputDRMS('luxSensor', 'illuminanceMeasurement', 'Room lux sensors?', false, true)
		}
		section("TEMPERATURE SENSORS:", hideable:false)	{
			inputDRMS('tempSensors', 'temperatureMeasurement', 'Room temperature sensors?', false, true)
		}
		section("HUMIDITY SENSORS:", hideable:false)	{
			inputDRMS('humiditySensor', 'relativeHumidityMeasurement', 'Room humidity sensors?', false, true)
		}
		section("POWER METER:", hideable:false)	{
			inputDRMS('powerDevice', 'powerMeter', 'Room power meter?', false, false)
		}
		section("MUSIC PLAYER:", hideable:false)	{
			inputDRMS('musicDevice', 'musicPlayer', 'Room music player?', false, false)
		}
		section("OUTDOOR TEMPERATURE SENSOR:", hideable:false)	{
			inputDRMS('outTempSensor', 'temperatureMeasurement', 'Outdoor temperature sensor?', false, false)
		}
		section("WINDOW SHADE:", hideable:false)	{
			inputDRMS('windowShades', 'windowShade', 'Room window shades?', false, true)
		}
	}
}

def pageRoomButton()	{
//    ifDebug("pageRoomButton")
	def hT = getHubType()
	state.buttonPassed = false
	def bName = 'roomButton'
	dynamicPage(name:"pageRoomButton", title:"Room Button Settings", install:false, uninstall:false)	{
		section("", hideable: false)	{
			def eCS = 99
			for (def i = 1; i <= maxButtons; i++)	{
				if (settings["$bName$i"] || settings["${bName}Number$i"])	{
					href "pageButtonDetails", title:("${settings["$bName$i"]} : ${(hT == _Hubitat ? heCapToAttrMap[settings["${bName}Type$i"]] + ' : ' : '')}${settings["${bName}Number$i"]}"), params:[bName:"roomButton", bNo:"$i"], required:false
					if (!(settings["$bName$i"] && settings["${bName}Number$i"]))		eCS = 88;
				}
			}
			if (eCS == 99)
				for (def i = 1; i <= maxButtons; i++)
					if (!(settings["$bName$i"] && settings["${bName}Number$i"]))	{
						eCS = i;
						break;
					}
			if (eCS <= maxButtons)
				href "pageButtonDetails", title:"Select new button", params:[bName:"roomButton", bNo:"$eCS"], required:false
		}
	}
}

def pageButtonDetails(params)	{
	def hT = getHubType()
	if (!state.buttonPassed && params)	{
		state.buttonPassed = true
		state.buttonPassedParams = params
	}
	if (params.bName)
		state.buttonName = params.bName
	else if (state.buttonPassedParams)
		state.buttonName = state.buttonPassedParams.bName
	if (params.bNo)
		state.buttonNumber = params.bNo
	else if (state.buttonPassedParams)
		state.buttonNumber = state.buttonPassedParams.bNo
	def bName = state.buttonName
	def bNo = state.buttonNumber
	ifDebug("bName: $bName | bNo: $bNo")
	def buttonNames = genericButtons
	def buttonOptions = [:]
	if (settings["$bName$bNo"])	{
		def buttonAttributes = settings["$bName$bNo"].supportedAttributes
		def attributeNameFound = false
//		buttonAttributes.each	{ att ->
		for (def att : buttonAttributes)		{
			if (att.name == occupancy)		buttonNames = occupancyButtons;
			if (att.name == 'numberOfButtons')		attributeNameFound = true;
		}
		def numberOfButtons = settings["$bName$bNo"].currentNumberOfButtons
		if (attributeNameFound && numberOfButtons)
			for (def i = 1; i <= numberOfButtons && i <= 16; i++)		buttonOptions << [(i.toString()):(buttonNames[i])];
		else
			buttonOptions << [null:"No buttons"]
//        ifDebug("${settings["$bName$bNo"]} | $numberOfButtons | $buttonOptions")
	}
//    ifDebug("pageHolidayLight: holiLightNo: $holiLightNo")
	dynamicPage(name:"pageButtonDetails", title:"Pick a button", install:false, uninstall:false)	{
		section()	{
			if (hT == _Hubitat)
				inputERMSDO("${bName}Type$bNo", 'Button type?', (settings["$bName$bNo"] ? true : false), false, true, null, [["$pushAButton":'Push button'], ["$holdAButton":'Hold button'], ["$doubleTapAButton":'Double tap button']])
			if (hT == _SmartThings || settings["${bName}Type$bNo"])
				inputDRMS("$bName$bNo", "${(hT == _SmartThings ? 'button' : settings["${bName}Type$bNo"])}", 'Button?', false, false, true)
			else
				paragraph "Button to rotate states?\nselect button type to set"
			if (settings["$bName$bNo"])
				inputERMSDO("${bName}Number$bNo", 'Button Number?', true, false, false, null, buttonOptions)
			else
				paragraph "Button number?\nselect button to set"
		}
	}
}

@Field final String pOS1 = 'Change room to OCCUPIED'
@Field final String pOS2 = 'Timeout for OCCUPIED'

def pageOccupiedSettings()	{
	def buttonNames = genericButtons
	def occupiedButtonOptions = [:]
	if (occupiedButton)	{
		def occupiedButtonAttributes = occupiedButton.supportedAttributes
		def attributeNameFound = false
//		occupiedButtonAttributes.each	{ att ->
		for (def att : occupiedButtonAttributes)		{
			if (att.name == occupancy)				buttonNames = occupancyButtons;
			if (att.name == 'numberOfButtons')		attributeNameFound = true;
		}
		def numberOfButtons = occupiedButton.currentNumberOfButtons
		if (attributeNameFound && numberOfButtons)
			for (def i = 1; i <= numberOfButtons && i <= 16; i++)		occupiedButtonOptions << [(i.toString()):(buttonNames[i])];
		else
			occupiedButtonOptions << [null:"No buttons"]
	}
	def hT = getHubType()
	if (ht == _Hubitat && occupiedButton && !occupiedButtonType)	app.updateSetting("occupiedButtonType", [type: "enum", value: "$pushAButton"]);
	dynamicPage(name:"pageOccupiedSettings", title:"Occupied Settings", install:false, uninstall:false)	{
		section((hT != _Hubitat ? pOS1 : ''), hideable:false)	{
			if (hT == _Hubitat)		{
				paragraph subHeaders(pOS1)
				inputERMSDO('occupiedButtonType', 'Button type?', (occupiedButton ? true : false), false, true, null, [["$pushAButton":'Push button'],["$holdAButton":'Hold button'],["$doubleTapAButton":'Double tap button']])
			}
			if (hT == _SmartThings || occupiedButtonType)
				inputDRMS('occupiedButton', "${(hT == _SmartThings ? 'button' : occupiedButtonType)}", 'Button to set OCCUPIED?', false, false, true)
			else
				paragraph "Button to set OCCUPIED?\nselect button type to set"
			if (occupiedButton)	{
				inputERMSDO('buttonIsOccupied', 'Button Number?', true, false, false, null, occupiedButtonOptions)
				input "buttonOnlySetsOccupied", "bool", title:"Button only sets Occupied?", description:"if false will toggle occupied and vacant", required:false, defaultValue:false
			}
			else		{
				paragraph "Button Number?\nselect button above to set"
				paragraph "Button only sets Occupied?"
			}
			inputDRMS('occSwitches', 'switch', 'If switch turns ON?', false, true, true)
		}
		section((hT != _Hubitat ? pOS2 : ''), hideable:fase)	{
			if (hT == _Hubitat)		paragraph subHeaders(pOS2);
			inputNRDRS('noMotionOccupied', "After how many seconds?", false, null, "5..99999")
		}
	}
}

@Field final String pES1 = 'Change room to ENGAGED'
@Field final String pES2 = 'Timeout for ENGAGED and other settings'
@Field final String pES3 = 'Via POWER WATTAGE'
@Field final String pES4 = 'Via CONTACT SENSOR'

def pageEngagedSettings()	{
	def buttonNames = genericButtons
	def engagedButtonOptions = [:]
	if (engagedButton)	{
		def engagedButtonAttributes = engagedButton.supportedAttributes
		def attributeNameFound = false
//		engagedButtonAttributes.each	{ att ->
		for (def att : engagedButtonAttributes)		{
			if (att.name == occupancy)			buttonNames = occupancyButtons;
			if (att.name == 'numberOfButtons')	attributeNameFound = true;
		}
		def numberOfButtons = engagedButton.currentNumberOfButtons
		if (attributeNameFound && numberOfButtons)
			for (def i = 1; i <= numberOfButtons && i <= 16; i++)	engagedButtonOptions << [(i.toString()):(buttonNames[i])];
		else
			engagedButtonOptions << [null:"No buttons"]
	}
	def roomDevices = parent.getRoomNames(app.id)
	def hT = getHubType()
	def powerFromTimeHHmm = (powerFromTime ? format24hrTime(timeToday(powerFromTime, location.timeZone)) : '')
	def powerToTimeHHmm = (powerToTime ? format24hrTime(timeToday(powerToTime, location.timeZone)) : '')
	if (ht == _Hubitat && engagedButton && !engagedButtonType)		app.updateSetting("engagedButtonType", [type: "enum", value: "$pushAButton"]);
	dynamicPage(name:"pageEngagedSettings", title:"Engaged Settings", install:false, uninstall:false)	{
		section((hT != _Hubitat ? pES1 : ''), hideable:false)		{
			if (hT == _Hubitat)		paragraph subHeaders(pES1);
			paragraph 'Settings are in order of priority in which they are checked.'
			if (motionSensors)
				inputERMSDO('busyCheck', 'When room is busy?', false, false, true, null, [[null:"No auto engaged"], [5:"Light traffic"], [7:"Medium Traffic"], [9:"Heavy Traffic"]])
			else
				paragraph "When room is busy?\nselect motion sensors above to set."
			if (motionSensors && busyCheck)
				input "repeatedMotion", "bool", title:"Repeated motion triggers busy check?", required:false, defaultValue:false
			else
				paragraph "Repeated motion triggers busy check?\nselect motion sensor and busy check to set"
			if (hT == _Hubitat)
				inputERMSDO('engagedButtonType', 'Button type?', (engagedButton ? true : false), false, true, null, [["$pushAButton":'Push button'], ["$holdAButton":'Hold button'], ["$doubleTapAButton":'Double tap button']])
			if (hT == _SmartThings || engagedButtonType)
				inputDRMS('engagedButton', "${(hT == _SmartThings ? 'button' : engagedButtonType)}", 'Button to set ENGAGED?', false, false, true)
			else
				paragraph "Button to set ENGAGED?\nselect button type to set"
			if (engagedButton)	{
				inputERMSDO('buttonIs', 'Button number?', true, false, false, null, engagedButtonOptions)
				input "buttonOnlySetsEngaged", "bool", title:"Button only sets Engaged?", description:"if false will toggle engaged and vacant", required:false, defaultValue:false
			}
			else		{
				paragraph "Button number?\nselect button above to set"
				paragraph "Button only sets Engaged?"
			}
			if (personsPresence)	{
				inputERMSDO('presenceAction', 'Presence sensor actions?', true, false, false, 3, [[1:"Set state to ENGAGED on Arrival"], [2:"Set state to VACANT on Departure"], [3:"Both actions"],[4:"Neither action"]])
				input "presenceActionContinuous", "bool", title:"Keep room engaged when presence sensor present?", required:false, defaultValue:false
			}
			else    {
				paragraph "Presence sensor actions?\nselect presence sensors to set"
				paragraph "Keep room engaged when presence sensor present?\nselect presence sensors to set"
			}
			inputDRMS('engagedSwitch', 'switch', 'If switch turns ON?', false, true)
			if (musicDevice)
				input "musicEngaged", "bool", title:"Set room to engaged when music starts playing?", required:false, defaultValue:false
			else
				paragraph "Set room to engaged when music is playing?\nselect music device in speaker settings to set."
			if (hT == _Hubitat)		paragraph subHeaders(pES3);
			if (powerDevice)	{
				if (!powerValueAsleep && !powerValueLocked)	{
					inputNRDRS('powerValueEngaged', "Power value to set room to ENGAGED?", false, null, "0..99999", true)
					href "pagePowerTime", title:"Power time range?", description:"${(powerFromTimeType || powerToTimeType ? (powerFromTimeType == _timeTime ? "$powerFromTimeHHmm" : (powerFromTimeType == _timeSunrise ? "Sunrise" : "Sunset") + (powerFromTimeOffset ? " $powerFromTimeOffset" : "")) + ' : ' + (powerToTimeType == _timeTime ? "$powerToTimeHHmm" : (powerToTimeType == _timeSunrise ? "Sunrise" : "Sunset") + (powerToTimeOffset ? " $powerToTimeOffset" : "")) : 'Add power time range')}"
					input "powerTriggerFromVacant", "bool", title:"Power value triggers ENGAGED from VACANT?", required:false, defaultValue:true
					inputNRDRS('powerStays', "Power stays below for how many seconds to reset ENGAGED?", (powerValueEngaged ? true : false), 30, "30..999")
				}
				else		{
					paragraph "Power value to set room to ENGAGED state?\npower already used to set room to ASLEEP or LOCKED."
					paragraph "Power time range?"
					paragraph "Power value triggers ENGAGED from VACANT?"
					paragraph "Power stays below for how many seconds to reset ENGAGED?"
				}
			}
			else		{
				paragraph "Power value to set room to ENGAGED?\nselect power device in other devices to set."
				paragraph "Power time range?"
				paragraph "Power value triggers ENGAGED from VACANT?"
				paragraph "Power stays below for how many seconds to reset ENGAGED?"
			}
			if (hT == _Hubitat)		paragraph subHeaders(pES4);
			inputDRMS('contactSensor', 'contactSensor', 'Contact sensor closes?', false, true, true)
			if (contactSensor && powerValueEngaged)
				inputERMSDO('resetEngagedWithContact', 'Reset ENGAGED with power when contact sensor open?', false, false, false, null, [null:"Never", 5:"5 mins", 10:"10 mins", 15:"15 mins", 30:"30 mins", 60:"60 mins"])
			else
				paragraph "Reset ENGAGED with power when contact sensor open?\nselect contact sensor and power value to set"
			if (contactSensor)	{
				input "contactSensorOutsideDoor", "bool", title:"Contact sensor on outside door?", required:false, defaultValue:false
				input "contactSensorNotTriggersEngaged", "bool", title:"Contact sensor does not trigger ENGAGED?", required:false, defaultValue:false
			}
			else		{
				paragraph "Contact sensor on outside door?\nselect contact sensor above to set"
				paragraph "Contact sensor does not trigger ENGAGED state?"
			}
		}
		section((hT != _Hubitat ? pES2 : ''), hideable:false)		{
			if (hT == _Hubitat)		paragraph subHeaders(pES2);
			inputNRDRS('noMotionEngaged', "Require motion within how many seconds when room is ENGAGED?", false, null, "5..99999")
			inputERMSDO('anotherRoomEngaged', 'Reset ENGAGED OR ASLEEP state for room when another room changes to ENGAGED OR ASLEEP?', false, true, false, null, roomDevices)
			input "resetEngagedDirectly", "bool", title:"When resetting room from ENGAGED directly move to VACANT?", required:false, defaultValue:false
			input "engagedOverrides", "bool", title:"ENGAGED overrides trigger devices for OCCUPIED?", required:false, defaultValue:false
		}
	}
}

def pageCheckingSettings()	{
	dynamicPage(name:"pageCheckingSettings", title:"Checking Settings", install:false, uninstall:false)	{
		section("CHECKING state timer before room changes to VACANT:", hideable: false)		{
			inputNRDRS('dimTimer', "How many seconds? (recommended 2x motion sensor blind window. doubles as dim timer.)", false, 5, "5..99999", true)
		}
		section("Light level", hideable:false)		{
			if (dimTimer)	{
//				inputERMSDO('dimByLevel', 'If any light is on dim by what level?', false, false, false, null, [[10:"10%"], [20:"20%"], [30:"30%"], [40:"40%"], [50:"50%"], [60:"60%"], [70:"70%"], [80:"80%"], [90:"90%"]])
				inputNRDRS('dimByLevel', 'If any light is on dim by what level?', false, null, "1..99")
//				inputERMSDO('dimToLevel', 'If no light is on turn on and dim to what level?', false, false, true, null, [[1:"1%"], [10:"10%"], [20:"20%"], [30:"30%"], [40:"40%"], [50:"50%"], [60:"60%"], [70:"70%"], [80:"80%"], [90:"90%"]])
				inputNRDRS('dimToLevel', 'If no light is on turn on and dim to what level?', false, null, "1..99")
			}
			else    {
				paragraph "If any light is on dim by what level?\nselect timer seconds above to set"
				paragraph "If no light is on turn on and dim to what level?"
			}
			if (dimTimer && dimToLevel && luxSensor)
				inputNRDRS('luxCheckingDimTo', "Below what lux value?", false, null, "0..*")
			else
				paragraph "What lux value?\nset dim timer, dim to level and lux sensor to select."
			input "notRestoreLL", "bool", title:"No restore light level after dimming during CHECKING if VACANT now?", required:false, defaultValue:false
		}
	}
}

def pageVacantSettings()	{
	def buttonNames = genericButtons
	def vacantButtonOptions = [:]
	if (vacantButton)	{
		def vacantButtonAttributes = vacantButton.supportedAttributes
		def attributeNameFound = false
//		vacantButtonAttributes.each	{ att ->
		for (def att : vacantButtonAttributes)		{
			if (att.name == occupancy)				buttonNames = occupancyButtons;
			if (att.name == 'numberOfButtons')		attributeNameFound = true;
		}
		def numberOfButtons = vacantButton.currentNumberOfButtons
		if (attributeNameFound && numberOfButtons)
			for (def i = 1; i <= numberOfButtons && i <= 16; i++)		vacantButtonOptions << [(i.toString()):(buttonNames[i])];
		else
			vacantButtonOptions << [null:"No buttons"]
	}
	def hT = getHubType()
	if (ht == _Hubitat && vacantButton && !vacantButtonType)		app.updateSetting("vacantButtonType", [type:"enum", value:"$pushAButton"]);
	dynamicPage(name:"pageVacantSettings", title:"Vacant Settings", install:false, uninstall:false)	{
		section("Change room to VACANT when?", hideable:false)		{
			if (hT == _Hubitat)
				inputERMSDO('vacantButtonType', 'Button type?', (vacantButton ? true : false), false, true, null, [["$pushAButton":'Push button'], ["$holdAButton":'Hold button'], ["$doubleTapAButton":'Double tap button']])
			if (hT == _SmartThings || vacantButtonType)
				inputDRMS('vacantButton', "${(hT == _SmartThings ? 'button' : vacantButtonType)}", 'Button to set VACANT?', false, false, true)
			else
				paragraph "Button to set VACANT?\nselect button type to set"
			if (vacantButton)	{
				inputERMSDO('buttonIsVacant', 'Button Number?', true, false, false, null, vacantButtonOptions)
			}
			else		{
				paragraph "Button Number?\nselect button to set"
			}
			inputDRMS('vacantSwitches', 'switch', 'If switch turns OFF?', false, true)
		}
		section("Pause music on VACANT:", hideable:false)	{
			if (musicDevice)
				input "turnOffMusic", "bool", title:"Pause speaker on VACANT?", required:false, defaultValue:false
			else
				paragraph "Pause speaker on VACANT?\nselect music player in speaker settings to set"
		}
	}
}

def pageAutoLevelSettings()	{
//    ifDebug("pageAutoLevelSettings")
	def wTime
	def sTime
	def hT = getHubType()
	if (state.ruleHasAL || wakeupTime || sleepTime)
		if (!wakeupTime || !sleepTime)
			if (hT == _SmartThings)		sendNotification("Invalid time range!", [method:"push"]);
	updateRulesToState()
	def levelRequired = (autoColorTemperature || state.ruleHasAL || minLevel || maxLevel ? true : false)
	dynamicPage(name: "pageAutoLevelSettings", title:"'AL' Level Settings", install:false, uninstall:false)	{
		section("Auto light level min/max:", hideable:false)		{
			inputNRDRS('minLevel', "Minimum level?", levelRequired, (levelRequired ? 1 : null), "1..${maxLevel ?: 100}", true)
			inputNRDRS('maxLevel', "Maximum level?", levelRequired, (levelRequired ? 100 : null), "${minLevel ?: 1}..100", true)
		}
		if (levelRequired)	{
			section("Wake and sleep time:", hideable:false)		{
				input "wakeupTime", "time", title:"Wakeup Time?", required: levelRequired, defaultValue:"07:00", submitOnChange:true
				input "sleepTime", "time", title:"Sleep Time?", required: levelRequired, defaultValue:"23:00", submitOnChange:true
			}
			section("Fade level up:", hideable:false)		{
				input "fadeLevelWake", "bool", title:"Fade up to wake time?", required:true, defaultValue:false, submitOnChange:true
				if (fadeLevelWake)	{
					inputNRDRS('fadeWakeBefore', "Starting how many hours before?", true, 1, "0..10", true)
					inputNRDRS('fadeWakeAfter', "Ending how many hours after?", true, 0, "0..10", true)
				}
				else		{
					paragraph "Starting how many hours before?\nset fade level up to set"
					paragraph "Ending how many hours after?"
				}
			}
			section("Fade level down:", hideable:false)		{
				input "fadeLevelSleep", "bool", title:"Fade down to sleep time?", required:true, defaultValue:false, submitOnChange:true
				if (fadeLevelSleep)	{
					inputNRDRS('fadeSleepBefore', "Starting how many hours before?", true, 2, "0..10", true)
					inputNRDRS('fadeSleepAfter', "Ending how many hours after?", true, 0, "0..10", true)
				}
				else		{
					paragraph "Starting how many hours before?\nset fade level down to set."
					paragraph "Ending how many hours after?"
				}
			}
			section("Auto color temperature:", hideable:false)		{
				input "autoColorTemperature", "bool", title:"Auto set color temperature?", required:false, defaultValue:false, submitOnChange:true
				if (autoColorTemperature)	{
					inputNRDRS('minKelvin', "Minimum kelvin?", true, 1900, "1500..${maxKelvin?:9000}", true)
					inputNRDRS('maxKelvin', "Maximum kelvin?", true, 6500, "$minKelvin..9000", true)
				}
				else    {
					paragraph "Minimum kelvin?\nenable auto color temperature above to set"
					paragraph "Maximum kelvin?"
				}
			}
			section("Fade color temperature up:", hideable:false)		{
				input "fadeCTWake", "bool", title: "Fade up to wake time?", required:true, defaultValue:false, submitOnChange:true
				if (autoColorTemperature && fadeCTWake)	{
					inputNRDRS('fadeKWakeBefore', "Starting how many hours before?", true, 1, "0..10", true)
					inputNRDRS('fadeKWakeAfter', "Ending how many hours after?", true, 0, "0..10", true)
				}
				else    {
					paragraph "Starting how many hours before?\nset fade auto color temperature up to set"
					paragraph "Ending how many hours after?"
				}
			}
			section("Fade color temperature down:", hideable:false)		{
				input "fadeCTSleep", "bool", title:"Fade down to sleep time?", required:true, defaultValue:false, submitOnChange:true
				if (autoColorTemperature && fadeCTSleep)	{
					inputNRDRS('fadeKSleepBefore', "Starting how many hours before?", true, 5, "0..10", true)
					inputNRDRS('fadeKSleepAfter', "Ending how many hours after?", true, 0, "0..10", true)
				}
				else    {
					paragraph "Starting how many hours before?\nset fade color temperature down to set."
					paragraph "Ending how many hours after?"
				}
			}
		}
	}
}

def pageHolidayLightPatterns()	{
//    ifDebug("pageHolidayLightPatterns")
	state.holiPassedOn = false
	updateRulesToState()
//    def hLR = (holiEvery || holiTwinkle || )
	dynamicPage(name:"pageHolidayLightPatterns", title:"'HL' Settings", install:false, uninstall:false)	{
		section("", hideable: false)	{
			def eCS = 99
			for (def i = 1; i <= maxHolis; i++)	{
				if (settings["holiName$i"] || settings["holiColorString$i"])	{
					href "pageHolidayLight", title: (settings["holiName$i"] ?: settings["holiColorString$i"]), params:[holiLightNo:"$i"], required:false
					if (!(settings["holiName$i"] && settings["holiColorString$i"]))		eCS = 88;
				}
			}
			if (eCS == 99)
				for (def i = 1; i <= maxHolis; i++)
					if (!(settings["holiName$i"] && settings["holiColorString$i"]))		{
						eCS = i;
						break;
					}
			if (eCS < 11)
				href "pageHolidayLight", title:"Create new holiday light pattern", params:[holiLightNo:"$eCS"], required:false
		}
	}
}

def pageHolidayLight(params)	{
	if (!state.holiPassedOn && params)	{
		state.holiPassedOn = true
		state.holiPassedParams = params
	}
	if (params.holiLightNo)
		state.pageHoliLightNo = params.holiLightNo
	else if (state.holiPassedParams)
		state.pageHoliLightNo = state.holiPassedParams.holiLightNo
	def holiLightNo = state.pageHoliLightNo
	def holiStyle = settings["holiStyle$holiLightNo"]
//    ifDebug("pageHolidayLight: holiLightNo: $holiLightNo")
	dynamicPage(name:"pageHolidayLight", title:"Holiday Light Pattern", install:false, uninstall:false)	{
		section()	{
			input "holiName$holiLightNo", "text", title:"Color string name?", required:true, submitOnChange:true
			input "holiColorString$holiLightNo", "text", title:"Comma delimited colors?", required:true, submitOnChange:true
			inputERMSDO("holiStyle$holiLightNo", 'Light routine?', true, false, true, null, [[RO:"Rotate"], [TW:"Twinkle"]])
			if (holiStyle)	{
				inputNRDRS("holiSeconds$holiLightNo", "${(settings["holiStyle$holiLightNo"] == 'RO' ? 'Rotate' : 'Twinkle')} every how many seconds?", true, "${(holiStyle == 'RO' ? 15 : 3)}", "${(holiStyle == 'RO' ? "5..300" : "2..10")}", true)
				inputERMSDO("holiLevel$holiLightNo", 'Set light level to?', false, false, false, null, [[1:"1%"], [10:"10%"], [20:"20%"], [30:"30%"], [40:"40%"], [50:"50%"], [60:"60%"], [70:"70%"], [80:"80%"], [90:"90%"], [99:"99%"], [100:"100%"]])
			}
			else		{
				paragraph "${(settings["holiStyle$holiLightNo"] == 'RO' ? 'Rotate' : 'Twinkle')} every how many seconds?\nSelect holiday light style to set"
				paragraph "Set light level to?"
			}
		}
	}
}

def pageRules()	{
	webCoRE_init()
	updateRulesToState()
	state.passedOn = false
	state.pList = []
	for (def wC : webCoRE_list())		state.pList << [(wC.id):(wC.name)];
	def hT = getHubType()
	dynamicPage(name: "pageRules", title: "Maintain Rules", install: false, uninstall: false)	{
		section()	{
			def emptyRule = null
			if (!state.rules)
				emptyRule = 1
			else    {
				for (def i = 1; i <= maxRules; i++)	{
					def ruleNo = String.valueOf(i)
					def thisRule = getRule(ruleNo, '*', false)
					if (thisRule)	{
						def ruleDesc = "$ruleNo: $thisRule.name -"
						ruleDesc = (thisRule.mode ? "$ruleDesc Mode=$thisRule.mode" : "$ruleDesc")
						ruleDesc = (thisRule.state ? "$ruleDesc State=$thisRule.state" : "$ruleDesc")
//                        ruleDesc = (thisRule.luxThreshold != null ? "$ruleDesc Lux=$thisRule.luxThreshold" : (luxThreshold ? "$ruleDesc Lux=$luxThreshold" : "$ruleDesc"))
						if (!thisRule.type || thisRule.type == _ERule)	{
							ruleDesc = (thisRule.luxThreshold != null ? "$ruleDesc Lux=$thisRule.luxThreshold" : "$ruleDesc")
							ruleDesc = (thisRule.powerThreshold ? "$ruleDesc Power=$thisRule.powerThreshold" : "$ruleDesc")
							ruleDesc = (thisRule.presence ? "$ruleDesc Presence=$thisRule.presence" : "$ruleDesc")
							ruleDesc = (thisRule.checkOn ? "$ruleDesc Check ON=$thisRule.checkOn" : "$ruleDesc")
							ruleDesc = (thisRule.checkOff ? "$ruleDesc Check OFF=$thisRule.checkOff" : "$ruleDesc")
							ruleDesc = (thisRule.piston ? "$ruleDesc Piston=$thisRule.piston" : "$ruleDesc")
							ruleDesc = (thisRule.actions ? "$ruleDesc Routines=$thisRule.actions" : "$ruleDesc")
						}
						if (thisRule.fromTimeType && thisRule.toTimeType)	{
							def ruleFromTimeHHmm = (thisRule.fromTime ? format24hrTime(timeToday(thisRule.fromTime, location.timeZone)) : '')
							def ruleToTimeHHmm = (thisRule.toTime ? format24hrTime(timeToday(thisRule.toTime, location.timeZone)) : '')
							ruleDesc = (thisRule.fromTimeType == _timeTime ? "$ruleDesc From=$ruleFromTimeHHmm" : (thisRule.fromTimeType == _timeSunrise ? "$ruleDesc From=Sunrise" : "$ruleDesc From=Sunset"))
							ruleDesc = (thisRule.toTimeType == _timeTime ? "$ruleDesc To=$ruleToTimeHHmm" : (thisRule.toTimeType == _timeSunrise ? "$ruleDesc To=Sunrise" : "$ruleDesc To=Sunset"))
						}
						if (thisRule.type == _HRule)	{
							ruleDesc = (thisRule.baseHumidity ? "$ruleDesc Baseline=$thisRule.baseHumidity" : "$ruleDesc")
							ruleDesc = (thisRule.humidity ? "$ruleDesc Heat=$thisRule.humidity" : "$ruleDesc")
						}
						else if (thisRule.type == _TRule)	{
							ruleDesc = (thisRule.coolTemp ? "$ruleDesc Cool=$thisRule.coolTemp" : "$ruleDesc")
							ruleDesc = (thisRule.heatTemp ? "$ruleDesc Heat=$thisRule.heatTemp" : "$ruleDesc")
						}
						else    {
							ruleDesc = (thisRule.switchesOn ? "$ruleDesc ON=$thisRule.switchesOn" : "$ruleDesc")
							ruleDesc = (thisRule.switchesOff ? "$ruleDesc OFF=$thisRule.switchesOff" : "$ruleDesc")
							ruleDesc = (thisRule.disabled ? "$ruleDesc Disabled=$thisRule.disabled" : "$ruleDesc")
						}
						href "pageRule", title: "$ruleDesc", params: [ruleNo: "$ruleNo"], required: false
					}
					else
						if (!emptyRule)
							emptyRule = i
				}
			}
			if (emptyRule)
				href "pageRule", title: "Create new rule", params: [ruleNo: emptyRule], required: false
			else
				paragraph "At max number of rules: 10"
			if (hT == _Hubitat && !hideAdvanced)
				href "pageRuleDelete", title: "DELETE rules?", description: "Selected rules will be DELETED on save. So DONT mark rules for deletion then edit them.", required: false
		}
	}
}

def pageRuleDelete()	{
	def hT = getHubType()
	def rulesList = []
	for (def i = 1; i <= maxRules; i++)	{
		def ruleNo = String.valueOf(i)
		def thisRule = getRule(ruleNo, '*', false)
		if (!thisRule)		continue;
		rulesList << [(ruleNo):"${varRuleString(thisRule)}"]
	}
	ifDebug("rulesList: $rulesList")
	dynamicPage(name: "pageRuleDelete", title: "", install: false, uninstall: false)	{
		section()	{
			inputERMSDO('rulesToDelete', 'DELETE which rules?', false, true, false, null, rulesList)
		}
	}
}

def pageRule(params)	{
	if (!state.passedOn && params)	{
		state.passedOn = true
		state.passedParams = params
	}
	if (params?.ruleNo)
		state.pageRuleNo = params.ruleNo
	else if (state.passedParams)
		state.pageRuleNo = state.passedParams.ruleNo
	def ruleNo = state.pageRuleNo
//log.debug "ruleNo: $ruleNo"
	def ruleFromTimeType = settings["fromTimeType$ruleNo"]
	def ruleToTimeType = settings["toTimeType$ruleNo"]
	def ruleFromTimeHHmm = (settings["fromTime$ruleNo"] ? format24hrTime(timeToday(settings["fromTime$ruleNo"], location.timeZone)) : '')
	def ruleToTimeHHmm = (settings["toTime$ruleNo"] ? format24hrTime(timeToday(settings["toTime$ruleNo"], location.timeZone)) : '')
	def ruleFromTimeOffset = settings["fromTimeOffset$ruleNo"]
	def ruleToTimeOffset = settings["toTimeOffset$ruleNo"]
	def ruleTimerOverride = (settings["noMotion$ruleNo"] || settings["noMotionEngaged$ruleNo"] || settings["dimTimer$ruleNo"] || settings["noMotionAsleep$ruleNo"])
	def ruleType = settings["type$ruleNo"]
	def levelOptions = []
	if (autoColorTemperature || minLevel || maxLevel)		levelOptions << [AL:"Auto Level (and color temperature)"];
	for (def i = 1; i <= maxHolis; i++)		{
		if (settings["holiName$i"] && settings["holiColorString$i"])		levelOptions << ["HL$i": settings["holiName$i"]];
	}
	[[1:"1%"], [5:"5%"], [10:"10%"], [15:"15%"], [20:"20%"], [25:"25%"], [30:"30%"], [40:"40%"], [50:"50%"], [60:"60%"], [70:"70%"], [80:"80%"], [90:"90%"], [99:"99%"], [100:"100%"]].each			{  levelOptions << it  }
	def colorsList = colorsRGB.collect { [(it.key):it.value[1]] }
	boolean isFarenheit = (location.temperatureScale == 'F' ? true : false)
	def hT = getHubType()
	def roomPresenceSensors = personsPresence.collect{ [(it.id): "${it.displayName}"] }
	dynamicPage(name: "pageRule", title: "Edit Rule", install: false, uninstall: false)	{
		section()	{
			ifDebug("rule number page ${ruleNo}")
			paragraph "Rule number: $ruleNo"
			inputERMSDO("type$ruleNo", 'Rule type?', true, false, true, null, [[e:"Execution Rule"],[h:"Humidity Rule"],[t:"Temperature Rule"]])
			input "name$ruleNo", "text", title: "Rule name?", required:false, capitalization: "none"
			input "disabled$ruleNo", "bool", title: "Rule disabled?", required: false, defaultValue: false
			if (hT == _Hubitat)		paragraph subHeaders("Rule triggers and conditions");
			inputERMSDO("state$ruleNo", 'Which state?', false, true, false, null, [asleep, engaged, occupied, vacant])
			input "mode$ruleNo", "mode", title: "Which mode?", required: false, multiple: true
			inputERMSDO("dayOfWeek$ruleNo", 'Which days of the week?', false, false, false, null, [[null:"All Days of Week"], [8:"Monday to Friday"], [9:"Saturday & Sunday"], [2:"Monday"], [3:"Tuesday"], [4:"Wednesday"], [5:"Thursday"], [6:"Friday"], [7:"Saturday"], [1:"Sunday"]])
			if (!ruleType || ruleType == _ERule)	{
				if (luxSensor)
					inputNRDRS("luxThreshold$ruleNo", "What lux value?", false, null, "0..*")
				else
					paragraph "What lux value?\nset lux sensors in main settings to select"
				if (powerDevice)
					inputNRDRS("powerThreshold$ruleNo", "What power value?", false, null, "1..*")
				else
					paragraph "What power value?\nset power meter in main settings to select"
				if (personsPresence && !presenceActionContinuous)
					inputERMSDO("presenceCheck$ruleNo", 'Which presence sensors present?', false, false, false, null, roomPresenceSensors)
				else
					paragraph "Which presence sensors present?\nset presence sensors in main settings to select"
				inputDRMS("checkOn$ruleNo", 'switch', 'Check switches are ON?', false, true)
				inputDRMS("checkOff$ruleNo", 'switch', 'Check switches are OFF?', false, true)
			}
		}

		if ((!ruleType || ruleType == _ERule) && !hideAdvanced)	{
			if (humiditySensor)	{
				section("")	{
					href "pageHumidity", title: "Humidity range? (condition)", description: "${(settings["fromHumidity$ruleNo"] || settings["toHumidity$ruleNo"] ? settings["fromHumidity$ruleNo"] + ' - ' + settings["toHumidity$ruleNo"] : 'Add humidity range')}", params: [ruleNo: "$ruleNo"]
				}
			}
			else
				section("")	{
					paragraph "What humidity range?\nset humidity sensor in main settings to select"
				}
		}

		if ((!ruleType || ruleType == _ERule) && !hideAdvanced)	{
			section("")	{
			   href "pageRuleDate", title: "Date filter? (condition)", description: "${(settings["fromDate$ruleNo"] || settings["toDate$ruleNo"] ? settings["fromDate$ruleNo"] + ' - ' + settings["toDate$ruleNo"] : 'Add date filtering')}", params: [ruleNo: "$ruleNo"]
			}
		}

		section("")	{
			href "pageRuleTime", title: "Time trigger?", description: "${(ruleFromTimeType || ruleToTimeType ? (ruleFromTimeType == _timeTime ? "$ruleFromTimeHHmm" : (ruleFromTimeType == _timeSunrise ? "Sunrise" : "Sunset") + (ruleFromTimeOffset ? " $ruleFromTimeOffset" : "")) + ' : ' + (ruleToTimeType == _timeTime ? "$ruleToTimeHHmm" : (ruleToTimeType == _timeSunrise ? "Sunrise" : "Sunset") + (ruleToTimeOffset ? " $ruleToTimeOffset" : "")) : 'Add time trigger')}", params: [ruleNo: "$ruleNo"]
		}

		if (!ruleType || ruleType == _ERule)	{
			section("Lights and switches to turn ON:", hideable: false)	{
				if (hT == _Hubitat)		paragraph subHeaders("Switches to turn on and off");
				inputDRMS("switchesOn$ruleNo", 'switch', 'Turn ON which switches?', false, true)
				inputERMSDO("setLevelTo$ruleNo", 'Set level?', false, false, true, null, levelOptions)
				inputERMSDO("setColorTo$ruleNo", 'Set color?', false, false, false, null, colorsList)
				if (settings["setLevelTo$ruleNo"] == 'AL' && autoColorTemperature)
					paragraph "Set color temperature?\ncannot set when level is set to 'AL'"
				else
					inputNRDRS("setColorTemperatureTo$ruleNo", "Set color temperature? (if light supports color & color is specified setting will be ignored.)", false, null, "1500..6500")
				inputDRMS("switchesOff$ruleNo", 'switch', 'Turn OFF which switches?', false, true)
			}
			if (!hideAdvanced)	{
				section("")	{
					if (hT == _Hubitat)		paragraph subHeaders("Other execution options");
				    href "pageRuleCommands", title: "Device commands",
						description: "${(settings["device$ruleNo"] ? settings["device$ruleNo"].toString() + ' : ' + (settings["cmds$ruleNo"] ?: '') : "Tap to configure")}", params: [ruleNo: "$ruleNo"]
				}
				section("")	{
					href "pageRuleOthers", title: "Routines/pistons and more",
						description: "${(settings["actions$ruleNo"] || settings["piston$ruleNo"] || settings["musicAction$ruleNo"]  || settings["shadePosition$ruleNo"] ? "Tap to change existing settings" : "Tap to configure")}", params: [ruleNo: "$ruleNo"]
				}
				section("")	{
					href "pageRuleTimer", title: "Timer overrides", description: "${(ruleTimerOverride ? (settings["noMotion$ruleNo"] ?: '') + ', ' + (settings["noMotionEngaged$ruleNo"] ?: '') + ', ' + (settings["dimTimer$ruleNo"] ?: '')  + ', ' + (settings["noMotionAsleep$ruleNo"] ?: '') : 'Add timer overrides')}", params: [ruleNo: "$ruleNo"]
				}
			}
		}
		else if (ruleType == _TRule)	{
			section("Maintain room temperature?", hideable: false)		{
				if (hT == _Hubitat)		paragraph subHeaders("Maintain temperature settings");
				if (['1', '3'].contains(maintainRoomTemp) && ((useThermostat && roomThermostat) || (!useThermostat && roomCoolSwitch)))
					input "coolTemp$ruleNo", "decimal", title: "Cool to what temperature?", required: (!settings["fanOnTemp$ruleNo"] || maintainRoomTemp == '5' ? true : false), range: "${(isFarenheit ? '32..99' : '0..38')}", submitOnChange: true
				else
					paragraph "Cool to what temperature?\nset thermostat or cool switch to set"
				if (['2', '3'].contains(maintainRoomTemp) && ((useThermostat && roomThermostat) || (!useThermostat && roomHeatSwitch)))
					input "heatTemp$ruleNo", "decimal", title: "Heat to what temperature?", required: true, range: "${(isFarenheit ? '32..99' : '0..38')}"
				else
					paragraph "Heat to what temperature?\nset thermostat or heat switch to set"
				if (['1', '2', '3'].contains(maintainRoomTemp) && ((useThermostat && roomThermostat) || (!useThermostat && (roomCoolSwitch || roomHeatSwitch))))
					input "tempRange$ruleNo", "decimal", title: "Within temperature range?", required: true, defaultValue: 0.4, trange: "0..3"
				else
					paragraph "Within temperature range?\nset thermostat or cool/heat switch to set"
			}
			if (!hideAdvanced)	{
				section("Fan control?", hideable: false)		{
					if (roomFanSwitch)	{
						input "fanOnTemp$ruleNo", "decimal", title: "Fan on at temperature?", required: false, defaultValue: null, range: "${(isFarenheit ? '32..99' : '0..38')}", submitOnChange: true
						input "fanSpeedIncTemp$ruleNo", "decimal", title: "Fan speed with what temperature increments?", required: (settings["fanOnTemp$ruleNo"] ? true : false), defaultValue: 1, range: "1..5"
					}
					else		{
						paragraph "Fan on at temperature?\nset fan switch to set"
						paragraph "Fan speed with what temperature increments?"
					}
				}
				section("Vent control?", hideable: false)		{
					if (useThermostat && roomVents)
						paragraph "Rooms vents will be automatically controlled with thermostat and room temperature."
					else
						paragraph "Enabled when using thermostat and room vents is set"
				}
			}
		}
		else		{
			section("Maintain room humidity?", hideable: false)		{
				if (hT == _Hubitat)		paragraph subHeaders("Maintain humidity settings");
				if (roomDehumidifierSwitch && !settings["humiOn$ruleNo"])
					input "dehumiOn$ruleNo", "bool", title: "Keep dehumidifier on?", required:true, defaultValue:false, submitOnChange:true
				else
					paragraph "Keep dehumidifier on?\nset dehumidifier switch to set"
				if (roomHumidifierSwitch && !settings["deHumiOn$ruleNo"])
					input "humiOn$ruleNo", "bool", title: "Keep humidifier on?", required:true, defaultValue:false, submitOnChange:true
				else
					paragraph "Keep humidifier on?\nset humidifier switch to set"
				if (hT == _Hubitat)		paragraph subHeaders("Maintain humidity settings");
				inputERMSDO("humiCmp$ruleNo", 'Compare to?', false, false, true, null, [[0:"Value"],
						[1:"Room humidity when last VACANT"], [2:"Hourly average"], [3:"Daily average"], [4:"Weekly average"]])
				if (settings["humiCmp$ruleNo"] == '0')
					input "humiValue$ruleNo", "decimal", title: "Humidity value?", required:true, range:"0.1..100"
				else
					if (settings["humiCmp$ruleNo"])
						inputNRDRS("humiPercent$ruleNo", "Percentage ${(['0', '1'].contains(settings["humiCmp$ruleNo"]) ? 'change' : 'delta')} for trigger?", true, 5, "1..99")
				if (settings["state$ruleNo"])
					inputNRDRS("humiMins$ruleNo", "For how many minutes?", true, 0, range:"0..60")
				else
					paragraph "For how many minutes?\nselect state to set"
				inputNRDRS("humiMinRun$ruleNo", "Minimum run time?", false, null, "1..60", true)
				inputNRDRS("humiMaxRun$ruleNo", "Maximum run time?", false, null, "${(settings["humiMinRun$ruleNo"] ?: 1)}..60")
			}
		}
	}
}

private subHeaders(str)		{
	if (str.size() > 50)	str = str.substring(0, 50);
	return "<div style='text-align:center;background-color:#bbbbbb;color:#ffffff;'>${str.toUpperCase().center(50)}</div>"
}

def pageHumidity(params)	{
	if (params?.ruleNo)				state.pageRuleNo = params.ruleNo;
	else if (state.passedParams)	state.pageRuleNo = state.passedParams.ruleNo;
	def ruleNo = state.pageRuleNo
//log.debug "ruleNo: $ruleNo"
	def fHum = settings["fromHumidity$ruleNo"]
	def tHum = settings["toHumidity$ruleNo"]
	dynamicPage(name: "pageHumidity", title: "Edit Rule Humidity", install: false, uninstall: false)	{
		section()	{
			input "fromHumidity$ruleNo", "decimal", title: "From humidity?", required: (tHum ? true : false), defaultValue: null, range: "0.1..$tHum", submitOnChange: true
			input "toHumidity$ruleNo", "decimal", title: "To humidity?", required: (fHum ? true : false), defaultValue: null, range: "$fHum..100", submitOnChange: true
		}
	}
}

def pageRuleDate(params)	{
	if (params?.ruleNo)				state.pageRuleNo = params.ruleNo;
	else if (state.passedParams)	state.pageRuleNo = state.passedParams.ruleNo;
	def ruleNo = state.pageRuleNo
//log.debug "ruleNo: $ruleNo"
	def ruleFromDate = settings["fromDate$ruleNo"]
	def ruleToDate = settings["toDate$ruleNo"]
	if (ruleFromDate && ruleToDate)	{
		def rD = dateInputValid(ruleFromDate, ruleToDate)
		def fTime = rD[0]
		def tTime = rD[1]
		def fTime2
		def tTime2
		if (fTime && tTime)	{
			fTime2 = new Date().parse("yyyy-MM-dd'T'HH:mm:ssZ", fTime)
			tTime2 = new Date().parse("yyyy-MM-dd'T'HH:mm:ssZ", tTime)
		}
		if ((fTime && !tTime) || (!fTime && tTime) || (fTime && tTime && tTime2 < fTime2))
			if (getHubType() == _SmartThings)		sendNotification("Invalid date range!", [method: "push"]);
	}
	dynamicPage(name: "pageRuleDate", title: "Edit Rule Date Filter", install: false, uninstall: false)	{
		section     {
			paragraph 'NO WAY TO VALIDATE DATE FORMAT ON INPUT. If invalid date checking for date will be skipped.'
			paragraph 'Date formats below support following special values for year to enable dynamic date ranges:\n"yyyy" = this year\n"YYYY" = next year'
			input "fromDate$ruleNo", "text", title: "From date? (yyyy/MM/dd format)", required: (ruleToDate ? true : false), defaultValue: null, submitOnChange: true
			input "toDate$ruleNo", "text", title: "To date? (yyyy/MM/dd format)", required: (ruleFromDate ? true : false), defaultValue: null, submitOnChange: true
		}
	}
}

def pageRuleTime(params)	{
	if (params?.ruleNo)				state.pageRuleNo = params.ruleNo;
	else if (state.passedParams)	state.pageRuleNo = state.passedParams.ruleNo;
	def ruleNo = state.pageRuleNo
//log.debug "ruleNo: $ruleNo"
	def ruleFromTimeType = settings["fromTimeType$ruleNo"]
	def ruleToTimeType = settings["toTimeType$ruleNo"]
	dynamicPage(name: "pageRuleTime", title: "Edit Rule Time Trigger", install: false, uninstall: false)	{
		section()	{
			if (ruleToTimeType)
				inputERMSDO("fromTimeType$ruleNo", 'Choose from time type?', true, false, true, null, [[1:"Sunrise"], [2:"Sunset"], [3:"Time"]])
			else
				inputERMSDO("fromTimeType$ruleNo", 'Choose from time type?', false, false, true, null, [[1:"Sunrise"], [2:"Sunset"], [3:"Time"]])
			if (ruleFromTimeType == '3')
				input "fromTime$ruleNo", "time", title: "From time?", required: true, defaultValue: null
			else if (ruleFromTimeType)
				inputNRDRS("fromTimeOffset$ruleNo", "Time offset?", false, 0, "-600..600")
			else
				paragraph "Choose from time type to select offset or time"
			if (ruleFromTimeType)
				inputERMSDO("toTimeType$ruleNo", 'Choose to time type?', true, false, true, null, [[1:"Sunrise"], [2:"Sunset"], [3:"Time"]])
			else
				inputERMSDO("toTimeType$ruleNo", 'Choose to time type?', false, false, true, null, [[1:"Sunrise"], [2:"Sunset"], [3:"Time"]])
			if (ruleToTimeType == '3')
				input "toTime$ruleNo", "time", title: "To time?", required: true, defaultValue: null
			else if (ruleToTimeType)
				inputNRDRS("toTimeOffset$ruleNo", "Time offset?", false, 0, "-600..600")
			else
				paragraph "Choose to time type to select offset or time"
		}
	}
}

def pageRuleTimer(params)	{
	if (params?.ruleNo)				state.pageRuleNo = params.ruleNo;
	else if (state.passedParams)	state.pageRuleNo = state.passedParams.ruleNo;
	def ruleNo = state.pageRuleNo
//log.debug "ruleNo: $ruleNo"
	dynamicPage(name: "pageRuleTimer", title: "Edit Rule Timer Overrides", install: false, uninstall: false)	{
		section()	{
			paragraph "These settings will temporarily replace the global settings when this rule is executed and reset back to the global settings when this rule no longer matches."
			inputNRDRS("noMotion$ruleNo", "Timeout after how many seconds when OCCUPIED?", false, null, "5..99999", true)
			inputNRDRS("noMotionEngaged$ruleNo", "Require motion within how many seconds when ENGAGED?", false, null, "5..99999")
			inputNRDRS("dimTimer$ruleNo", "CHECKING state timer in seconds?", false, null, "5..99999", true)
			inputNRDRS("noMotionAsleep$ruleNo", "Motion timeout for night switches when ASLEEP?", false, null, "5..99999")
		}
	}
}

def pageRuleCommands(params)	{
	if (params?.ruleNo)				state.pageRuleNo = params.ruleNo;
	else if (state.passedParams)	state.pageRuleNo = state.passedParams.ruleNo;
	def ruleNo = state.pageRuleNo
//log.debug "ruleNo: $ruleNo"
	def hT = getHubType()
	def deviceIs = settings["device$ruleNo"], allCmds
	if (deviceIs)
		allCmds = deviceIs.getSupportedCommands().collect{ [(it):it.toString().capitalize()] }
	ifDebug("deviceIs: $deviceIs | allCmds: $allCmds")
	dynamicPage(name: "pageRuleCommands", title: "Edit Rule Execute", install: false, uninstall: false)	{
		section("Device commands to issue:", hideable: false)	{
			inputDRMS("device$ruleNo", "${(hT == _Hubitat ? '*' : 'switch')}", 'Issue command to device?', false, false, true)
			inputERMSDO("cmds$ruleNo", 'Commands to call?', (deviceIs ? true : false), true, false, null, allCmds)
		}
	}
}

def pageRuleOthers(params)	{
	if (params?.ruleNo)				state.pageRuleNo = params.ruleNo;
	else if (state.passedParams)	state.pageRuleNo = state.passedParams.ruleNo;
	def ruleNo = state.pageRuleNo
//log.debug "ruleNo: $ruleNo"
	def hT = getHubType()
	def allActions = (hT == _SmartThings ? location.helloHome?.getPhrases()*.label : [])
	if (allActions)		allActions.sort();
	dynamicPage(name: "pageRuleOthers", title: "Edit Rule Execute", install: false, uninstall: false)	{
		section("", hideable: false)	{
			inputERMSDO("actions$ruleNo", 'Routines to execute?', false, true, false, null, allActions)
			inputERMSDO("piston$ruleNo", 'Piston to execute?', false, false, false, null, state.pList)
//			paragraph "Rules to execute?\nplaceholder for rule machine rule execution on HE"
		}
		section("", hideable: false)	{
			if (musicDevice)
				inputERMSDO("musicAction$ruleNo", 'Start or stop music player?', false, false, false, null, [[1:"Start music player"], [2:"Pause music player"], [3:"Neither"]])
			else
				paragraph "Start or stop music player?\nselect music player in speaker settings to set"
		}
		section("", hideable: false)	{
			if (windowShades)
				inputERMSDO("shadePosition$ruleNo", 'Shade position?', false, false, false, 99, [[99:"Leave it alone"], [0:"Close shade"], [10:"10% open"], [20:"20% open"], [30:"30% open"], [40:"40% open"], [50:"50% open"], [60:"60% open"], [70:"70% open"], [80:"80% open"], [90:"90% open"], [100:"Open shade"], [P1:"Preset position 1"], [P2:"Preset position 2"], [P3:"Preset position 3"]])
			else
				paragraph "Set window shade position?\nselect window shades in other devices to set"
		}
	}
}

private dateInputValid(dateInputStart, dateInputEnd)	{
	if ((!dateInputStart || dateInputStart.size() < 8 || dateInputStart.size() > 10) ||
		(!dateInputEnd || dateInputEnd.size() < 8 || dateInputEnd.size() > 10))
		return [null, null]
	def dPS, dPE
	if (dateInputStart.toLowerCase().substring(0, 5) == 'yyyy/' || dateInputEnd.toLowerCase().substring(0, 5) == 'yyyy/')	{
		def dateIS = yearTranslate(dateInputStart)
		def dIS = Date.parse("yyyy/M/d HH:mm:ss z", dateIS + ' 00:00:00 ' + location.timeZone.getDisplayName())
		def dateIE = yearTranslate(dateInputEnd)
		def dIE = Date.parse("yyyy/M/d HH:mm:ss z", dateIE + ' 23:59:59 ' + location.timeZone.getDisplayName())
		def cDate = new Date(now())
		if (cDate > dIE)	{
			Calendar c = Calendar.getInstance()
			c.setTime(dIS)
			c.add(Calendar.YEAR, 1)
			dIS = c.getTime()
			c.setTime(dIE)
			c.add(Calendar.YEAR, 1)
			dIE = c.getTime()
		}
		dPS = dIS.format("yyyy-MM-dd'T'HH:mm:ssZ")
		dPE = dIE.format("yyyy-MM-dd'T'HH:mm:ssZ")
	}
	else    {
		def dIS = Date.parse("yyyy/M/d HH:mm:ss z", dateInputStart + ' 00:00:00 ' + location.timeZone.getDisplayName())
		dPS = dIS.format("yyyy-MM-dd'T'HH:mm:ssZ")
		def dIE = Date.parse("yyyy/M/d HH:mm:ss z", dateInputEnd + ' 23:59:59 ' + location.timeZone.getDisplayName())
		dPE = dIE.format("yyyy-MM-dd'T'HH:mm:ssZ")
	}
	if (!dPS || !dPE)	return [null, null];
	return [dPS, dPE]
}

private yearTranslate(dateP)	{
	def rD
	def tY = (new Date(now())).getAt(Calendar.YEAR)
	def nY = tY + 1
	rD = (dateP.substring(0,5) == 'yyyy/' ? tY + dateP.substring(4) : (dateP.substring(0,5) == 'YYYY/' ? nY + dateP.substring(4) : dateP))
	return rD
}

@Field final String pAS1 = 'Power value to set room to ASLEEP?\n'
@Field final String pAS2 = 'Power time range?'
@Field final String pAS3 = 'Power value triggers ASLEEP from VACANT?'
@Field final String pAS4 = 'Power stays below for how many seconds to reset ASLEEP?'

def pageAsleepSettings()	{
	def buttonNames = genericButtons
	def asleepButtonOptions = [:]
	if (asleepButton)	{
		def buttonAttributes = asleepButton.supportedAttributes
		def attributeNameFound = false
//		buttonAttributes.each	{ att ->
		for (def att : buttonAttributes)		{
			if (att.name == occupancy)		buttonNames = occupancyButtons;
			if (att.name == 'numberOfButtons')		attributeNameFound = true;
		}
		def numberOfButtons = asleepButton.currentNumberOfButtons
		if (attributeNameFound && numberOfButtons)
			for (def i = 1; i <= numberOfButtons && i <= 16; i++)		asleepButtonOptions << [(i.toString()):(buttonNames[i])];
		else
			asleepButtonOptions << [null:"No buttons"]
	}
	def nightButtonOptions = [:]
	if (nightButton)	{
		def nightButtonAttributes = nightButton.supportedAttributes
		def attributeNameFound = false
//		nightButtonAttributes.each	{ att ->
		for (def att : nightButtonAttributes)		{
			if (att.name == occupancy)		buttonNames = occupancyButtons;
			if (att.name == 'numberOfButtons')		attributeNameFound = true;
		}
		def numberOfButtons = nightButton.currentNumberOfButtons
		if (attributeNameFound && numberOfButtons)	{
			for (def i = 1; i <= numberOfButtons && i <= 16; i++)		nightButtonOptions << [(i.toString()):(buttonNames[i])];
		}
		else
			nightButtonOptions << [null:"No buttons"]
	}
	def hT = getHubType()
	def roomMotionSensors = motionSensors.collect{ [(it.id): "${it.displayName}"] }
	def powerFromTimeHHmm = (powerFromTime ? format24hrTime(timeToday(powerFromTime, location.timeZone)) : '')
	def powerToTimeHHmm = (powerToTime ? format24hrTime(timeToday(powerToTime, location.timeZone)) : '')
	if (ht == _Hubitat)	{
		if (asleepButton && !asleepButtonType)		app.updateSetting("asleepButtonType", [type: "enum", value: "$pushAButton"]);
		if (nightButton && !nightButtonType)		app.updateSetting("nightButtonType", [type: "enum", value: "$pushAButton"]);
	}
	def colorsList = colorsRGB.collect { [(it.key):it.value[1]] }
	dynamicPage(name: "pageAsleepSettings", title: "Asleep Settings", install: false, uninstall: false)	{
		section("Change room to ASLEEP when?", hideable: false)		{
	    	inputDRMS('asleepSensor', 'sleepSensor', 'Sleep sensor to set room to ASLEEP?', false, true)
			if (hT == _Hubitat)
				inputERMSDO('asleepButtonType', 'Button type?', (asleepButton ? true : false), false, true, null, [["$pushAButton":'Push button'], ["$holdAButton":'Hold button'], ["$doubleTapAButton":'Double tap button']])
			if (hT == _SmartThings || asleepButtonType)
				inputDRMS('asleepButton', "${(hT == _SmartThings ? 'button' : asleepButtonType)}", 'Button to toggle ASLEEP?', false, false, true)
			else
				paragraph "Button to toggle ASLEEP?\nselect button type to set"
			if (asleepButton)	{
				inputERMSDO('buttonIsAsleep', 'Button Number?', true, false, false, null, asleepButtonOptions)
				input "buttonOnlySetsAsleep", "bool", title: "Button only sets Asleep?", description: "if false will toggle asleep and vacant", required: false, defaultValue: false
			}
			else		{
				paragraph "Button Number?\nselect button above to set"
				paragraph "Button only sets Asleep?"
			}
			inputDRMS('asleepSwitch', 'switch', 'If switch turns ON?', false, true)
			if (powerDevice)	{
				if (!powerValueEngaged && !powerValueLocked)	{
					inputNRDRS("powerValueAsleep", "Power value to set room to ASLEEP?", false, null, "0..99999", true)
					href "pagePowerTime", title: "Power time range?", description: "${(powerFromTimeType || powerToTimeType ? (powerFromTimeType == _timeTime ? "$powerFromTimeHHmm" : (powerFromTimeType == _timeSunrise ? "Sunrise" : "Sunset") + (powerFromTimeOffset ? " $powerFromTimeOffset" : "")) + ' : ' + (powerToTimeType == _timeTime ? "$powerToTimeHHmm" : (powerToTimeType == _timeSunrise ? "Sunrise" : "Sunset") + (powerToTimeOffset ? " $powerToTimeOffset" : "")) : 'Add power time range')}"
					input "powerTriggerFromVacant", "bool", title: "Power value triggers ASLEEP from VACANT?", required: false, defaultValue: true
					inputNRDRS("powerStays", "Power stays below for how many seconds to reset ASLEEP?", (powerValueAsleep ? true : false), 30, "30..999")
				}
				else		{
					paragraph pAS1 + 'power already used to set room to ENGAGED'
					paragraph pAS2
					paragraph pAS3
					paragraph pAS4
				}
			}
			else		{
				paragraph pAS1 + 'select power device in other devices to set'
				paragraph pAS2
				paragraph pAS3
				paragraph pAS4
			}
			inputNRDRS("noAsleep", "Timeout ASLEEP after how many hours?", false, null, "1..99")
			input "resetAsleepDirectly", "bool", title: "When resetting room from ASLEEP directly move to VACANT?", required: false, defaultValue: false
			if (contactSensor)
				inputERMSDO('resetAsleepWithContact', 'Reset ASLEEP when contact sensor open for?', false, false, false, null, [5:"5 mins", 10:"10 mins", 15:"15 mins", 30:"30 mins", 60:"60 mins"])
			else
				paragraph "Reset ASLEEP when contact sensor open for?\nselect contact sensor in engaged setttings to set."
			input "asleepOverrides", "bool", title: "ASLEEP overrides state trigger devices for ENGAGED and OCCUPIED?", required: false, defaultValue: false

		}
		section("${(hT != _Hubitat ? 'Night Lights:' : '')}", hideable: false)		{
			if (hT == _Hubitat)		paragraph subHeaders("Night Lights While Asleep");
			if (motionSensors)	{
				inputDRMS('nightSwitches', 'switch', 'Turn ON which night switches with motion?', false, true, true)
				inputERMSDO('nightMotionSensors', 'Use which room motion sensors?', false, true, false, null, roomMotionSensors)
			}
			else		{
				paragraph "Turn ON which night switches with motion?\nselect motion sensor in ROOM DEVICES to set."
				paragraph "Use which room motion sensors?\nselect night switch above to set"
			}
			if (nightSwitches)	{
//				inputERMSDO('nightSetLevelTo', 'Set Level When Turning ON?', false, false, false, null, [[1:"1%"], [5:"5%"], [10:"10%"], [20:"20%"], [30:"30%"], [40:"40%"], [50:"50%"], [60:"60%"], [70:"70%"], [80:"80%"], [90:"90%"], [99:"99%"], [100:"100%"]])
				inputNRDRS('nightSetLevelTo', "Set Level When Turning ON?", false, null, "1..100")
				inputNRDRS('nightSetCT', "Set color temperature when turning ON?", false, null, "1500..7500")
				inputERMSDO('nightSetColorTo', 'Set color?', false, false, false, null, colorsList)
				inputNRDRS("noMotionAsleep", "Timeout seconds for night lights?", false, null, "5..99999")
				inputERMSDO('nightTurnOn', 'Turn on night lights when?', true, true, false, null, [[1:"Motion in ASLEEP"], [2:"Changes to ASLEEP"], [3:"Changes away from ASLEEP"]])
				if (hT == _Hubitat)
					inputERMSDO('nightButtonType', 'Button type?', (nightButton ? true : false), false, true, null, [["$pushAButton":'Push button'], ["$holdAButton":'Hold button'], ["$doubleTapAButton":'Double tap button']])
				if (hT == _SmartThings || nightButtonType)
					inputDRMS('nightButton', "${(hT == _SmartThings ? 'button' : nightButtonType)}", 'Button to toggle night lights?', false, false, true)
				else
					paragraph "Button to toggle night lights?\nselect button type to set"
				if (nightButton)	{
					inputERMSDO('nightButtonIs', 'Button Number?', true, false, false, null, nightButtonOptions)
					inputERMSDO('nightButtonAction', 'Button Action?', true, false, true, null, [[1:"Turn on"], [2:"Turn off"], [3:"Toggle"]])
				}
				else		{
					paragraph "Button Number?\nselect button above to set"
					paragraph "Button Action?"
				}
			}
			else		{
				paragraph "Set level when turning ON?\nselect switches above to set"
				paragraph "Set color temperature when turning ON?"
				paragraph "Timeout seconds for night lights?"
				paragraph "Turn on night lights when?"
				paragraph "Button to toggle night lights?"
				paragraph "Button Number?\nselect button above to set"
				paragraph "Button Action?"
			}
		}
	}
}

@Field final String pLS1 = 'Power value to set room to LOCKED?\npower already used to set room to ASLEEP'
@Field final String pLS2 = 'Power time range?'
@Field final String pLS3 = 'Power value triggers LOCKED from VACANT?'
@Field final String pLS4 = 'Power stays below for how many seconds to reset LOCKED?'

def pageLockedSettings()	{
	def powerFromTimeHHmm = (powerFromTime ? format24hrTime(timeToday(powerFromTime, location.timeZone)) : '')
	def powerToTimeHHmm = (powerToTime ? format24hrTime(timeToday(powerToTime, location.timeZone)) : '')
	dynamicPage(name: "pageLockedSettings", title: "Locked Settings", install: false, uninstall: false)	{
		section("Change room to LOCKED when?", hideable:false)	{
			inputDRMS('lockedSwitch', 'switch', 'Which switches?', false, true, true)
			if (lockedSwitch)
				input "lockedSwitchCmd", "bool", title: "If switch turns ON? (otherwise when switch turns OFF)", required: true, defaultValue: true
			else
				paragraph "If switch turns ON?\nselect locked switch above to set"
			if (powerDevice)	{
				if (!powerValueEngaged && !powerValueAsleep)	{
					inputNRDRS('powerValueLocked', "Power value to set room to LOCKED?", false, null, "0..99999", true)
					href "pagePowerTime", title: "Power time range?", description: "${(powerFromTimeType || powerToTimeType ? (powerFromTimeType == _timeTime ? "$powerFromTimeHHmm" : (powerFromTimeType == _timeSunrise ? "Sunrise" : "Sunset") + (powerFromTimeOffset ? " $powerFromTimeOffset" : "")) + ' : ' + (powerToTimeType == _timeTime ? "$powerToTimeHHmm" : (powerToTimeType == _timeSunrise ? "Sunrise" : "Sunset") + (powerToTimeOffset ? " $powerToTimeOffset" : "")) : 'Add power time range')}"
					input "powerTriggerFromVacant", "bool", title: "Power value triggers LOCKED from VACANT?", required: false, defaultValue: true
					inputNRDRS('powerStays', "Power stays below for how many seconds to reset LOCKED?", (powerValueLocked ? true : false), 30, "30..999")
				}
				else		{
					paragraph pLS1
					paragraph pLS2
					paragraph pLS3
					paragraph pLS4
				}
			}
			else		{
				paragraph pLS1
				paragraph pLS2
				paragraph pLS3
				paragraph pLS4
			}
			inputDRMS('lockedContact', 'contactSensor', 'Which contact for LOCKED?', false, false, true)
			if (lockedContact)
				input "lockedContactCmd", "bool", title: "When contact closes? (otherwise when contact opens)", required: true, defaultValue: true
			else
				paragraph "When contact closes?\nselect locked contact above to set"
			input "lockedTurnOff", "bool", title: "Turn off switches when room changes to LOCKED?", required: false, defaultValue: false
			inputNRDRS('unLocked', "Timeout LOCKED after how many hours?", false, null, "1..99")
			input "lockedOverrides", "bool", title: "LOCKED overrides trigger devices for other states?", required: false, defaultValue: false
		}
	}
}

def pagePowerTime()	{
	dynamicPage(name: "pagePowerTime", title: "Power Time Range", install: false, uninstall: false)	{
		section()	{
			inputERMSDO('powerFromTimeType', 'Choose from time type?', (powerToTimeType ? true : false), false, true, null, [[1:"Sunrise"], [2:"Sunset"], [3:"Time"]])
			if (powerFromTimeType == '3')
				input "powerFromTime", "time", title: "From time?", required: true, defaultValue: null
			else if (powerFromTimeType)
				inputNRDRS('powerFromTimeOffset', "Time offset?", false, 0, "-600..600")
			else
				paragraph "Choose from time type to select offset or time"
			inputERMSDO('powerToTimeType', 'Choose to time type?', (powerFromTimeType ? true : false), false, true, null, [[1:"Sunrise"], [2:"Sunset"], [3:"Time"]])
			if (powerToTimeType == '3')
				input "powerToTime", "time", title: "To time?", required: true, defaultValue: null
			else if (powerToTimeType)
				inputNRDRS('powerToTimeOffset', "Time offset?", false, 0, "-600..600")
			else
				paragraph "Choose to time type to select offset or time"
		}
	}
}

def pageRoomTemperature()	{
	def validThermostat = true
	def otherRoom
	if (useThermostat && roomThermostat)	{
		otherRoom = parent.checkThermostatValid(app.id, roomThermostat)
		ifDebug("otherRoom: $otherRoom")
		if (otherRoom)		validThermostat = false;
	}
	boolean isFarenheit = (location.temperatureScale == 'F' ? true : false)
	dynamicPage(name: "pageRoomTemperature", title: "Temperature Settings", install: false, uninstall: false)	{
		if (validThermostat)	{
			section("Maintain room temperature:", hideable: false)		{
				if (tempSensors)
					inputERMSDO('maintainRoomTemp', 'Maintain room temperature?', (tempSensors ? true : false), false, true, 4, [[1:"Cool"], [2:"Heat"], [3:"Both"], [5:"Only manage vents"], [4:"Neither"]])
				else
					paragraph "Maintain room temperature?\nselect temperature sensor to set."
				if (['1', '2', '3', '5'].contains(maintainRoomTemp))	{
					input "useThermostat", "bool", title: "Use thermostat? (otherwise uses room ac and/or heater)", required: true, defaultValue: false, submitOnChange: true
					if (useThermostat)	{
						inputDRMS('roomThermostat', 'thermostat', 'Which thermostat?', true, false, true)
						inputNRDRS('thermoToTempSensor', "Delta (room - thermostat) temperature?", true, 0, "${(isFarenheit ? '-15..15' : '-9..9')}")
					}
				}
				if (!useThermostat && ['1', '3'].contains(maintainRoomTemp))
					inputDRMS('roomCoolSwitch', 'switch', 'AC switch?', true, false)
				if (!useThermostat && ['2', '3'].contains(maintainRoomTemp))
					inputDRMS('roomHeatSwitch', 'switch', 'Heater switch?', true, false)
				if (['1', '2', '3'].contains(maintainRoomTemp))		{
					if (personsPresence)
						input "checkPresence", "bool", title: "Check presence before maintaining temperature?", required: true, defaultValue: false
					else
						paragraph "Check presence before maintaining temperature?\nselect presence sensors to set"
					inputDRMS('contactSensorsRT', 'contactSensor', 'Check windows closed?', false, true)
					inputNRDRS('thermoOverride', "Allow thermostat or switch override for how many minutes?", true, 0, "1..30")
					paragraph "Remember to setup temperature rules for room cooling and/or heating."
// TODO parametize the adjustment
					if (outTempSensor)	{
						input "autoAdjustWithOutdoor", "bool", title: "Adjust temperature by ±0.5ºF?", description: "When outside temperature < 32ºF or > 90ºF", required: true, defaultValue: true
					}
					else		{
						paragraph "Adjust temperature by ±0.5ºF?\nselect outdoor door temperature sensor to set."
					}
				}
			}
			if (!hideAdvanced)	{
				section("Room vents:", hideable: false)		{
					if (useThermostat)	{
						inputDRMS('roomVents', 'switch', 'Room vents?', false, true, true)
						if (roomVents)
							inputNRDRS('delayedVentOff', "Allow thermostat or switch override for how many minutes?", true, null, "1..600")
						else
							paragraph "Delay vents off by?"
					}
					else		{
						paragraph "Room vents?\nenabled when using thermostat or manage vents"
						paragraph "Delay vents off by?"
					}
				}
				section("Room fan:", hideable: false)		{
					if (tempSensors)
						inputDRMS('roomFanSwitch', 'switch', 'Room fan switch?', false, false)
					else
						paragraph "Fan switch?\nselect temperature sensor to set"
				}
			}
		}
		else		{
			section("Warn About Thermostat", hideable: false)		{
				paragraph "This thermostat has already been selected in room: $otherRoom. Please select another thermostat or clear the theromstat setting.\n\nSAVING THESE SETTINGS WITHOUT CHANGING OR CLEARING THE THERMOSTAT SELECTION WILL HAVE UNPREDICTABLE RESULTS."
				inputDRMS('roomThermostat', 'thermostat', 'Which thermostat?', true, false, true)
			}
		}
	}
}

def pageRoomHumidity()	{
	dynamicPage(name: "pageRoomHumidity", title: "Humidity Settings", install: false, uninstall: false)	{
		section("", hideable: false)		{
			if (humiditySensor)	{
				inputERMSDO('maintainRoomHumidity', 'Maintain room humidity?', false, false, true, 4, [[1:"Dehumidify"], [2:"Humidify"], [3:"Both"], [4:"Neither"]])
				if (['1', '3'].contains(maintainRoomHumidity))	{
					inputDRMS('roomDehumidifierSwitch', 'switch', 'Dehumidifier switch?', true, false, true)
				}
				else		{
					paragraph "Dehumidifier switch?\nselect humidity sensor and maintain humidity to set"
				}
				if (['2', '3'].contains(maintainRoomHumidity))	{
					inputDRMS('roomHumidifierSwitch', 'switch', 'Humidifier switch?', true, false, true)
				}
				else		{
					paragraph "Humidifier switch?\nselect humidity sensor and maintain humidity to set"
				}
				if (['1', '2', '3'].contains(maintainRoomHumidity))
					inputNRDRS('humiOverride', "Allow switch override for how many minutes?", true, 0, "1..60")
				else
					paragraph "Allow switch override for how many minutes?"
				if (humiditySensor && ['1', '2', '3'].contains(maintainRoomHumidity))
					paragraph "Remember to setup humidity rules for room humidification."
			}
			else		{
				paragraph "Maintain room humidity?\nselect humidity sensors to set"
				paragraph "Dehumidifier switch?\nselect humidity sensor and maintain humidity to set"
				paragraph "Humidifier switch?"
				paragraph "Allow switch override for how many minutes?"
			}
		}
	}
}

def pageAdjacentRooms()	{
	def roomNames = parent.getRoomNames(app.id)
	dynamicPage(name: "pageAdjacentRooms", title: "Adjacent Rooms Settings", install: false, uninstall: false)	{
		section("Action when there is motion in ADJACENT rooms:", hideable: false)		{
			inputERMSDO('adjRooms', 'Adjacent Rooms?', false, true, true, null, roomNames)
			if (adjRooms)	{
				input "adjRoomsMotion", "bool", title: "If motion in adjacent room check if person is still in this room?", required: false, defaultValue: false
				input "adjRoomsPathway", "bool", title: "If moving through room turn on switches in adjacent rooms?", required: false, defaultValue: false
			}
			else    {
				paragraph "If motion in adjacent room check if person still in this room?\nselect adjacent rooms above to set"
				paragraph "If moving through room turn on switches in adjacent rooms?"
			}
		}
	}
}

def pageAnnouncementSettings()	{
	def hT = getHubType()
	def playerDevice = (speakerDevices || speechDevices || musicPlayers || (hT == _SmartThings && listOfMQs) ? true : false)
	dynamicPage(name: "pageAnnouncementSettings", title: "Announcement Settings", install: false, uninstall: false)	{
		section("")	{
			href "pageAnnouncementSpeakerTimeSettings", title: "Spoken announcement settings", description: (playerDevice ? "Tap to change existing settings" : "Tap to configure")
			href "pageAnnouncementColorTimeSettings", title: "Color announcement settings", description: (announceSwitches ? "Tap to change existing settings" : "Tap to configure")
			if (playerDevice || announceSwitches)
				input "announceInModes", "mode", title: "Announce in modes?", required: false, multiple: true
			else
				paragraph "Announce in modes?\nselect announce speaker or light to set."
		}
		section("")	{
			href "pageAnnounceContacts", title: "Announcement contacts settings", description: (announceDoor || announceContact || announceContactRT ? "Tap to change existing settings" : "Tap to configure")
		}
	}
}

def pageAnnouncementSpeakerTimeSettings()	{
	def hT = getHubType()
	def playerDevice = (speakerDevices || speechDevices || musicPlayers || (hT == _SmartThings && listOfMQs) ? true : false)
	if (hT == _Hubitat)		app.updateSetting("echoAccessCode", [type: "text", value: "${decrypt(settings['echoAccessCode'])}"]);
	dynamicPage(name: "pageAnnouncementSpeakerTimeSettings", title: "", install: false, uninstall: false)	{
		section("Speakers for announcement:")	{
			inputDRMS('speakerDevices', 'audioNotification', 'Which speakers?', false, true, true)
			inputDRMS('speechDevices', 'speechSynthesis', 'Which speech devices?', false, true, true)
			inputDRMS('musicPlayers', 'musicPlayer', 'Which media players?', false, true, true)
			if (hT == _SmartThings)
				inputERMSDO('listOfMQs', 'Select Ask Alexa Message Queues', true, false, false, null, state.askAlexaMQ)
			else
				input "echoAccessCode", "text", title: "Echo notify ACCESS CODE", required: false
			if (playerDevice)	{
				inputNRDRS('speakerVolume', "Speaker volume?", false, 33, "1..100")
				input "useVariableVolume", "bool", title: "Use variable volume?", required: true, defaultValue: false
			}
			else		{
				paragraph "Speaker volume?\nselect any speaker to set"
				paragraph "Use variable volume?"
			}
		}
		section("Spoken announcement during hours:")	{
			if (playerDevice)	{
				inputNRDRS('startHH', "From hour?", true, 7, "0..${(endHH < 24 ? endHH : 23)}", true)
				inputNRDRS('endHH', "To hour?", true, 23, "${(startHH ? startHH + 1 : 0)}..24", true)
			}
			else		{
				paragraph "Announce from hour?\nselect speaker to set"
				paragraph "Announce to hour?"
			}
		}
	}
}

def pageAnnouncementColorTimeSettings()	{
	dynamicPage(name: "pageAnnouncementColorTimeSettings", title: "", install: false, uninstall: false)	{
		section("Lights for announcement with color:")	{
			inputDRMS('announceSwitches', 'switch', 'Which switches?', false, true, true)
		}
		section("Color announcement during hours:")	{
			if (announceSwitches)	{
				inputNRDRS('startHHColor', "From hour?", true, 18, "0..${(endHHColor < 24 ? endHHColor : 23)}", true)
				inputNRDRS('endHHColor', "To hour?", true, 23, "${(startHHColor ? startHHColor + 1 : 0)}..24", true)
			}
			else		{
				paragraph "Announce from hour?\nselect announce switches to set"
				paragraph "Announce to hour?"
			}
		}
	}
}

def pageAnnounceContacts()	{
	def hT = getHubType()
	def playerDevice = (speakerDevices || speechDevices || musicPlayers || (hT == _SmartThings && listOfMQs) ? true : false)
	def colorsList = colorsRGB.collect { [(it.key):it.value[1]] }
//    colorsRGB.each	{ k, v ->   colorsList << ["$k":"${v[1]}"] }
	dynamicPage(name: "pageAnnounceContacts", title: "", install: false, uninstall: false)	{
		section("Door announcements:")	{
			if (contactSensor && (playerDevice || announceSwitches))
				input "announceDoor", "bool", title: "Announce when door opened or closed?", required: false, defaultValue: false, submitOnChange: true
			else
				paragraph "Announce when door opened or closed?\nset contact sensor and announce devices to set"
			if (announceDoor)
				input "announceDoorSpeaker", "bool", title: "Announce with speaker?", required: true, defaultValue: false, submitOnChange: true
			else
				paragraph "Announce with speaker?\nselect door announcement & speaker to set"
			if (announceDoor)
				inputERMSDO('announceDoorColor', 'Announce with color?', true, false, true, null, colorsList)
			else
				paragraph "Announce with color?\nselect door announcement & color bulb to set"
			if (contactSensor && (playerDevice || announceSwitches))
				inputERMSDO('announceContact', 'Announce when door stays open?', false, false, false, null, [[1:"Every 1 minute"], [2:"Every 2 minutes"], [3:"Every 3 minutes"], [5:"Every 5 minutes"], [10:"Every 10 minutes"], [15:"Every 15 minutes"], [30:"Every 30 minutes"]])
			else
				paragraph "Announce when door stays open?\nset contact sensor and announce devices to set"
			if (announceContact && playerDevice)
				input "announceContactSpeaker", "bool", title: "Announce with speaker?", required: (announceContactColor ? false : true), defaultValue: false, submitOnChange: true
			else
				paragraph "Announce with speaker?\nselect door stays open announcement & speaker to set"
			if (announceContact && announceSwitches)
				inputERMSDO('announceContactColor', 'Announce with color?', (announceContactSpeaker ? false : true), false, true, null, colorsList)
			else
				paragraph "Announce with color?\nselect door stays open announcement & color bulb to set"
		}
		section("Window announcements:")	{
			if (contactSensorsRT && (playerDevice || announceSwitches))
				input "announceContactRT", "bool", title: "Announce when window opened or closed?", required: false, defaultValue: false, submitOnChange: true
			else
				paragraph "Announce when window opened or closed?\nset contact sensor and announce devices to set"
			if (announceContactRT && playerDevice)
				input "announceContactRTSpeaker", "bool", title: "Announce with speaker?", required: (announceContactRTColor ? false : true), defaultValue: false, submitOnChange: true
			else
				paragraph "Announce with speaker?\nselect window announcement & speaker to set"
			if (announceContactRT && announceSwitches)
				inputERMSDO('announceContactRTColor', 'Announce with color?', (announceContactRTSpeaker ? false : true), false, true, null, colorsList)
			else
				paragraph "Announce with color?\nselect window announcement & color bulb to set"
		}
	}
}

def pageGeneralSettings()	{
	boolean isFarenheit = (location.temperatureScale == 'F' ? true : false)
	dynamicPage(name: "pageGeneralSettings", title: "General Settings", install: false, uninstall: false)	{
		section("Mode settings for AWAY and PAUSE modes?", hideable: false)		{
			input "awayModes", "mode", title: "Away modes to set Room to VACANT?", required: false, multiple: true
			input "pauseModes", "mode", title: "Modes in which to pause automation?", required: false, multiple: true
		}
		section("Run on which days of the week?\n(when blank runs on all days.)", hideable: false)		{
			inputERMSDO('dayOfWeek', 'Which days of the week?', false, false, false, null, [[null:"All Days of Week"], [8:"Monday to Friday"], [9:"Saturday & Sunday"], [2:"Monday"], [3:"Tuesday"], [4:"Wednesday"], [5:"Thursday"], [6:"Friday"], [7:"Saturday"], [1:"Sunday"]])
		}
		section("Temperature Scale", hideable: false)		{
			input "useCelsius", "bool", title: "Use Celsius?", required: false, defaultValue: "${(!isFarenheit)}"
		}
		section("Turn off all switches on no rule match?", hideable: false)		{
			input "allSwitchesOff", "bool", title: "Turn OFF?", required: false, defaultValue: true
		}
		section("When switching off lights dim to off?", hideable: false)		{
			inputERMSDO('dimOver', 'Dim over seconds?', false, false, false, null, [[null:"No dimming"], [3:"3 seconds"], [5:"5 seconds"], [10:"10 seconds"], [15:"15 seconds"], [30:"30 seconds"], [45:"45 seconds"], [60:"60 seconds"], [90:"90 seconds"]])
		}
		if (!hideAdvanced)	{
			section("Process execution rules only on state change?", hideable: false)		{
				input "onlyOnStateChange", "bool", title: "Only on state change?", required: false, defaultValue: false
				inputERMSDO("butNotInStates", 'But not in these states?', false, true, false, null, [asleep, engaged, occupied, vacant])
			}
			section("Debug logging?", hideable: false)		{
				input "debugLogging", "bool", title: "Turn on?", required: false, defaultValue: false
			}
		}
		if (!hideAdvanced)	{
			section("When room device switch capability turned on programatically (rooms_device.on()) set room to?\n(note: when room device switch is tuned off room state is set to VACANT.)", hideable: false)	{
				inputERMSDO('roomDeviceSwitchOn', 'Which room state?', false, false, false, ['occupied'], ['occupied', 'engaged', 'locked', 'asleep'])
			}
			section("Icon URL to use for this room?\nfor best results please use image of type 'png' and size 1024x1024 pixels. image url needs to be publicly accessible for ST to access.", hideable: false)		{
				input "iconURL", "text", title: "Icon URL?", required: false, defaultValue: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomOccupancySettings.png"
			}
		}
	}
}

private inputDRMS(var, capa, tex, req, mul, sub = false)	{
	return "${input "$var", "capability.$capa", title: tex, required: req, multiple: mul, submitOnChange: sub}"
}

private inputERMSDO(var, tex, req, mul, sub, dfv, opt)	{
//log.debug "input $var, 'enum', title: $tex, required: $req, multiple: $mul, submitOnChange: $sub, defaultValue: $dfv, options: $opt"
	return "${input "$var", 'enum', title: tex, required: req, multiple: mul, submitOnChange: sub, defaultValue: dfv, options: opt}"
}

private inputNRDRS(var, tex, req, dfv, ran, sub = false)	{
	return "${input "$var", 'number', title: tex, required: req, defaultValue: dfv, description: ran, range: ran, submitOnChange: sub}"
}

def pageAllSettings()	{
//    ifDebug("pageAllSettings")
	def hT = getHubType()
	dynamicPage(name: "pageAllSettings", title: "View All Settings", install: false, uninstall: false)	{
		section("")	{
			input "onlyHas", "bool", title: "Only show settings with value?", required: false, defaultValue: false, submitOnChange: true
			input "anonIt", "bool", title: "Anonymize settings?${(hT == _Hubitat ? '\t\t' : '')}", required: false, defaultValue: false, submitOnChange: true
		}
		section("", hideable: false)		{
			def varAllS, varTranslate
			def para = ''
			def sum, s
			for (def vit : allSettingsVar)		{
				if (vit[0] instanceof List)	{
					varAllS = vit[0]
					varTranslate = vit[1]
				}
				else		{
					varAllS = vit
					varTranslate = null
				}
				def vitCount = (varAlls instanceof String ? 1 : varAllS.size())
				if (vitCount == 1)	{
					if (para)	paragraph para + (ht == _Hubitat ? '</pre>' : '');
					para = (hT == _Hubitat ? "\n<pre>" : '') + varAllS[0].toUpperCase() + "\n"
				}
				else    {
					def var = (settings[varAllS[0]] || varTranslate ? (varTranslate ? "$varTranslate"(settings[varAllS[0]]) : settings[varAllS[0]]) : '')
					if (var && var.toString().isInteger())	{
						def vN = var.toInteger()
						if (vN > 0)		var = String.format("%,d", vN);
					}
					if (vitCount == 2)	{
						if (!onlyHas || var)
							para = para + "\n${(hT == _Hubitat ? '\t' : '')}${var ? (settings[varAllS[0]] ? padViewString(varAllS[1]) : varAllS[1]) : varAllS[1]}${var ?: ''}"
					}
					else if (vitCount == 3)	{
						if (!onlyHas || var)
							para = para + "\n${(hT == _Hubitat ? '\t' : '')}${var ? padViewString(varAllS[1]) : varAllS[1]}${var ?: ''}" + (var && varAllS[2] ? varAllS[2] : '')
					}
					else if (vitCount == 4)	{
						sum = varAllS[3]
						s = (sum && anonIt ? (var instanceof List ? var.size() : (var ? 1 : '')) : (var ?: ''))
//                        ifDebug(">>>>> 3 >>> $itCount | $varAllS | ${settings[varAllS[0]]} | $var | $sum | $check | ${['or', 'and'].contains(check)} | $varTranslate | $s")
						if (!onlyHas || s)
							para = para + "\n${(hT == _Hubitat ? '\t' : '')}${s ? padViewString(varAllS[1]) : varAllS[1]}$s" + (s && varAllS[2] ? varAllS[2] : '')
					}
					else if (vitCount > 4)	{
						sum = varAllS[3]
						def check = varAllS[4]?.trim().toLowerCase()
						s = (sum && anonIt ? (var instanceof List ? var.size() : (var ? 1 : '')) : (var ?: ''))
//                        ifDebug(">>>>> 4 >>> $vitCount | $check | ${['or', 'and'].contains(check)} | $s")
						if (vitCount > 5 && ['or', 'and'].contains(check))	{
							def pas = (check == 'and' ? true : false)
							for (def i = 5; i < vitCount; i++)	{
								def v = settings[varAllS[i]]
								if (check == 'or' && "$v")	{
									if (!pas)		pas = true;
								}
								else if (check == 'and')	{
									if (v == null)		pas = false;
								}
							}
							if (!pas)		s = '';
						}
						if (!onlyHas || s)
							para = para + "\n${(hT == _Hubitat ? '\t' : '')}${s ? padViewString(varAllS[1]) : varAllS[1]}$s" + (s && varAllS[2] ? varAllS[2] : '')
					}
				}
			}
			if (para)		paragraph para + (ht == _Hubitat ? '</pre>' : '');
		}
//        section()	{
//            href "pageSaveSettings", title: "Save settings", description: "Tap to view and copy all settings to save to a file."
//        }
//        if (hT == _Hubitat)	{
//            section()	{
//                href "pageRestoreSettings", title: "Restore settings", description: "Tap to restore all settings from saved settings from this room or another room."
//            }
//        }
	}
}

private padViewString(str, chars = 20)	{
	def padSz = ((chars ?: 20) * (getHubType() == _Hubitat ? 1.5 : 1)) as Integer
	def sz = (str ? str.size() : 0)
	def s = (sz > 0 ? str.substring(0, (sz > padSz ? (padSz - 1) : (sz - 1))) : '')
	s = s + ((' ' + padChar + ' ') * ((padSz - s.size() + 5f) / 3).round(0))
	return s.substring(0, padSz + 3) + ' '
}

private varRoomChild(x)		{ return (childCreated() ? "Child device OK" : "Child device NOT created") }

private varRoomButtons(x)	{
	def hT = getHubType()
	def bS = ''
	def bN = 'roomButton'
	def bT = "Room buttons:"
	def bC = false
	for (def i = 1; i <= maxButtons; i++)	{
		if (settings["$bN$i"] || settings["${bN}Number$i"])
			if (anonIt)		bC = (bC ? bC + 1 : 1);
			else			bS = bS + (bS ? "\n${(hT == _Hubitat ? '\t' : '')}" + padViewString('') : '') +
//                                                            padViewString(buttonTitle).substring(buttonTitle.size()+2) + ' ') +
//                                "${(buttonString ? padViewString('') : '')}" +
								"${settings["$bN$i"]} : ${(hT == _Hubitat ? heCapToAttrMap[settings["roomButtonType$i"]] + ' : ' : '')}${settings["${bN}Number$i"]}";
	}
	if (anonIt && buttonCount)		bS = "$bC buttons";
	ifDebug(bS)
	return bS
}

private varWhichNoMotion(wM)	{ return (wM == lastMotionActive ? "Last Motion Active" : "Last Motion Inactive") }

private varBusyCheck(bC)	{
	def bCS
	switch(bC)	{
		case lightTraffic:   bCS = 'Light traffic';      break
		case mediumTraffic:  bCS = 'Medium traffic';     break
		case heavyTraffic:   bCS = 'Heavy traffic';      break
		default:             bCS = 'No traffic check';   break
	}
	return bCS
}

private varDate(dT)	{
	def dateX = /^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3}-[0-9]{4}\z/
	def matcher
	return ((matcher = dT =~ dateX) ? dT.substring(11,16) : dT)
}

private varHoli(x)	{
	def hT = getHubType()
	def hS = ''
	for (def i = 1; i <= maxHolis; i++)	{
		if (settings["holiName$i"] || settings["holiColorString$i"])
			hS = hS + "\n${(hT == _Hubitat ? '\t\t' : '')}" + padViewString(settings["holiName$i"].trim() + ":", 15) + settings["holiColorString$i"]
	}
	return holiString
}

private varRule(x)	{
	def hT = getHubType()
	def rS = ''
	for (def i = 1; i <= maxRules; i++)	{
		def ruleNo = String.valueOf(i)
		def thisRule = getRule(ruleNo, '*', false)
		if (!thisRule || thisRule.disabled)		continue;
		rS = rS + "\n${(hT == _Hubitat ? '\t\t' : '')}" + varRuleString(thisRule)
	}
	return rS
}

private varRuleString(thisRule)	{
	def rD = "$thisRule.ruleNo:"
	rD = (thisRule.disabled ? "$rD Disabled＝$thisRule.disabled" : "$rD")
	rD = (thisRule.mode ? "$rD Mode＝$thisRule.mode" : "$rD")
	rD = (thisRule.state ? "$rD State＝$thisRule.state" : "$rD")
	rD = (thisRule.dayOfWeek ? "$rD Days of Week＝$thisRule.dayOfWeek" : "$rD")
	rD = (thisRule.luxThreshold != null ? "$rD Lux＝$thisRule.luxThreshold" : "$rD")
	rD = (thisRule.powerThreshold ? "$rD Power＝$thisRule.powerThreshold" : "$rD")
	rD = (thisRule.presence ? "$rD Presence＝${(anonIt ? thisRule.presence.size() : thisRule.presence)}" : "$rD")
	rD = (thisRule.checkOn ? "$rD Check ON＝${(anonIt ? thisRule.checkOn.size() : thisRule.checkOn)}" : "$rD")
	rD = (thisRule.checkOff ? "$rD Check OFF＝${(anonIt ? thisRule.checkOff.size() : thisRule.checkOff)}" : "$rD")
	if (thisRule.fromDate && thisRule.toDate)	{
		def rDFT = dateInputValid(settings["fromDate$ruleNo"], settings["toDate$ruleNo"])
		if (rDFT[0] && rDFT[1])	{
			rD = (thisRule.fromDate ? "$rD From＝${settings["fromDate$ruleNo"]}" : "$rD")
			rD = (thisRule.toDate ? "$rD To＝${settings["toDate$ruleNo"]}" : "$rD")
		}
	}
	if (thisRule.fromTimeType && thisRule.toTimeType)	{
		def rFT = (thisRule.fromTime ? format24hrTime(timeToday(thisRule.fromTime, location.timeZone)) : '')
		def rTT = (thisRule.toTime ? format24hrTime(timeToday(thisRule.toTime, location.timeZone)) : '')
		rD = (thisRule.fromTimeType == _timeTime ? "$rD From＝$rFT" : (thisRule.fromTimeType == _timeSunrise ? "$rD From＝Sunrise" + varRuleTimeOffset(thisRule.fromTimeOffset) : "$rD From＝Sunset" + varRuleTimeOffset(thisRule.fromTimeOffset)))
		rD = (thisRule.toTimeType == _timeTime ? "$rD To＝$rTT" : (thisRule.toTimeType == _timeSunrise ? "$rD To＝Sunrise" + varRuleTimeOffset(thisRule.toTimeOffset) : "$rD To＝Sunset" + varRuleTimeOffset(thisRule.toTimeOffset)))
	}
	rD = rD + ' Type＝' + (thisRule.type == _HRule ? 'Humidity' : (thisRule.type == _TRule ? 'Temperature' : 'Execution'))
	if (thisRule.type == _HRule)	{
		rD = (thisRule.deHumiOn ? "$rD Dehumidifier＝${(anonIt ? thisRule.deHumiOn.size() : thisRule.deHumiOn)}" : "$rD")
		rD = (thisRule.humiOn ? "$rD Humidifier＝${(anonIt ? thisRule.humiOn.size() : thisRule.humiOn)}" : "$rD")
		rD = (thisRule.humiCmp ? "$rD Compare＝$thisRule.humiCmp" : "$rD")
		rD = (thisRule.humiValue ? "$rD Value＝$thisRule.humiValue" : "$rD")
		rD = (thisRule.humiMins ? "$rD For minutes＝$thisRule.humiMins" : "$rD")
		rD = (thisRule.humiMinRun ? "$rD Minimum＝$thisRule.humiMinRun" : "$rD")
		rD = (thisRule.humiMaxRun ? "$rD Maximum＝$thisRule.humiMaxRun" : "$rD")
	}
	else if (thisRule.type == _TRule)	{
		rD = (thisRule.coolTemp ? "$rD Cool＝$thisRule.coolTemp" : "$rD")
		rD = (thisRule.heatTemp ? "$rD Heat＝$thisRule.heatTemp" : "$rD")
		rD = (thisRule.tempRange ? "$rD Range＝$thisRule.tempRange" : "$rD")
		rD = (thisRule.fanOnTemp ? "$rD Fan On＝$thisRule.fanOnTemp" : "$rD")
		rD = (thisRule.fanSpeedIncTemp ? "$rD Fan Increment＝$thisRule.fanSpeedIncTemp" : "$rD")
	}
	else		{
		rD = (thisRule.device ? "$rD Device＝$thisRule.device ${(thisRule.commands ? ": " + thisRule.commands : '')}" : "$rD")
		rD = (thisRule.piston ? "$rD Piston＝true" : "$rD")
		rD = (thisRule.actions ? "$rD Routines＝true" : "$rD")
		rD = (thisRule.musicAction ? "$rD Music＝${(thisRule.musicAction == '1' ? 'Start' : (thisRule.musicAction == '2' ? 'Pause' : 'Neither'))}" : "$rD")
		if (thisRule.switchesOn)	{
			rD = (thisRule.switchesOn ? "$rD ON＝${(anonIt ? thisRule.switchesOn.size() : thisRule.switchesOn)}" : "$rD")
			rD = (thisRule.level ? "$rD Level＝$thisRule.level" : "$rD")
			rD = (thisRule.color ? "$rD Color＝$thisRule.color" : "$rD")
			rD = (thisRule.colorTemperature ? "$rD Kelvin＝$thisRule.colorTemperature" : "$rD")
		}
		rD = (thisRule.switchesOff ? "$rD OFF＝${(anonIt ? thisRule.switchesOff.size() : thisRule.switchesOff)}" : "$rD")
		rD = (thisRule.noMotion ? "$rD Occupied Timer＝${thisRule.noMotion}" : "$rD")
		rD = (thisRule.noMotionEngaged ? "$rD Engaged Timer＝${thisRule.noMotionEngaged}" : "$rD")
		rD = (thisRule.dimTimer ? "$rD Checking Timer＝${thisRule.dimTimer}" : "$rD")
		rD = (thisRule.noMotionAsleep ? "$rD Asleep Timer＝${thisRule.noMotionAsleep}" : "$rD")
	}
	return rD
}

private varRuleTimeOffset(offset)	{ return String.format( " %+d", offset.toInteger()) }

private varNightLightOn(nLO)	{
	if (!nLO)	return ''
	def hT = getHubType()
	def nLOS = ''
	if (nLO.contains('1'))		nLOS = "With motion"
	if (nLO.contains('2'))		nLOS = (nLOS ? nLOS + "\n${(hT == _Hubitat ? '\t' : '')}" + padViewString('') : '') + "Changes to asleep"
	if (nLO.contains('3'))		nLOS = (nLOS ? nLOS + "\n${(hT == _Hubitat ? '\t' : '')}" + padViewString('') : '') + "Changes from asleep"
	ifDebug(nLOS)
	return nLOS + ' '
}

private varNightButton(nBA)		{  return (nBA ? [1:"Turn on",2:"Turn off",3:"Toggle"][nBA] : '')  }

private varMaintainTemp(mRT)	{  return (mRT ? [1:"Cool room",2:"Heat room",3:"Cool & heat room",4:"No"][mRT] : '')  }

private varPresenceAction(pAct)		{
	return (pAct ? ['1':"ENGAGED on Arrival",'2':"VACANT on Departure",'3':"Both actions",'4':"Neither action"][pAct] : '')
}

/*
private spawnChildApp(roomName)	{
	ifDebug("spawnChildApp")
	def app
	if (!childAppCreated())
		app = addChildApp("bangali", "rooms child inputs app", "$roomName inputs", [completedSetup: true])
	else
		app = getInputsChild()
	return app
}

private childAppCreated()	{  return (childApps.size() ? true : false)  }

private getInputsChild()	{
	def app
	for (def a : childApps)		app = a;
	return app
}
*/

def installed()		{}

def updated()	{
	def nowTime = now()
	ifDebug("updated", 'info')
	if (!childCreated())	spawnChildDevice(app.label);
	deleteRules()
	updateRoom()
	if (parent)		parent.handleAdjRooms();

	def child = getChildDevice(getRoom())
	def childLabel = child.getLabel()
	if (childLabel != app.label)	child.setLabel(app.label);
	child.setOnStateC(roomDeviceSwitchOn)

///	child."${(isRoomEngaged() ? engaged : vacant)}"()
	"${(isRoomEngaged() ? engaged : vacant)}"()
//log.debug "perf updated: ${now() - nowTime} ms"
}

private updateRoom()	{
	log.info "updateRoom $app.label"
	initialize()
	def hT = getHubType()
	if (debugLogging)		state.debugOffTime = now() + 86400000;
	boolean isFarenheit = (location.temperatureScale == 'F' ? true : false)
	subscribe(location, "mode", modeEventHandler)
	state.motionTraffic = 0
	state.noMotion = ((noMotionOccupied && noMotionOccupied >= 5) ? noMotionOccupied : null)
	state.noMotionEngaged = ((noMotionEngaged && noMotionEngaged >= 5) ? noMotionEngaged : null)
	if (motionSensors)	{
		subscribe(motionSensors, "motion.active", motionActiveEventHandler)
		subscribe(motionSensors, "motion.inactive", motionInactiveEventHandler)
	}
//    if (roomButton)
	for (def i = 0; i <= maxButtons; i++)	{
		if ((hT == _SmartThings || settings["roomButtonType$i"]) && settings["roomButton$i"] && settings["roomButtonNumber$i"])
			subscribe(settings["roomButton$i"], (hT == _Hubitat ? "${heCapToAttrMap[settings["roomButtonType$i"]]}.${settings["roomButtonNumber$i"]}" : 'button'), roomButtonPushedEventHandler)
	}
	if (occupiedButton)
		subscribe(occupiedButton, (hT == _Hubitat ? "${heCapToAttrMap[occupiedButtonType]}.$buttonIsOccupied" : 'button'), buttonPushedOccupiedEventHandler)
	if (occSwitches)	{
		subscribe(occSwitches, "switch.on", occupiedSwitchOnEventHandler)
		subscribe(occSwitches, "switch.off", occupiedSwitchOffEventHandler)
	}
	ifDebug("updateRoom 2", 'info')
	state.switchesHasLevel = [:]
	state.switchesHasColor = [:]
	state.switchesHasColorTemperature = [:]
	state.dimTimer = ((dimTimer && dimTimer >= 5) ? dimTimer as Integer : 5)
	state.dimByLevel = ((state.dimTimer && dimByLevel) ? dimByLevel : null)
	state.dimToLevel = ((state.dimTimer && dimToLevel) ? dimToLevel : null)
	if (engagedSwitch)	{
		subscribe(engagedSwitch, "switch.on", engagedSwitchOnEventHandler)
		subscribe(engagedSwitch, "switch.off", engagedSwitchOffEventHandler)
	}
	if (contactSensor)	{
		subscribe(contactSensor, (contactSensorOutsideDoor ? "contact.closed" : "contact.open"), contactOpenEventHandler)
		subscribe(contactSensor, (contactSensorOutsideDoor ? "contact.open" : "contact.closed"), contactClosedEventHandler)
	}
	if (musicDevice && musicEngaged)	{
		subscribe(musicDevice, "status.playing", musicPlayingEventHandler)
		subscribe(musicDevice, "status.paused", musicStoppedEventHandler)
		subscribe(musicDevice, "status.stopped", musicStoppedEventHandler)
	}
	state.busyCheck = (busyCheck && busyCheck.isInteger() ? busyCheck as Integer : null)
	if (engagedButton)
		subscribe(engagedButton, (hT == _Hubitat ? "${heCapToAttrMap[engagedButtonType]}.$buttonIs" : 'button'), buttonPushedEventHandler)
	if (personsPresence)	{
		subscribe(personsPresence, "presence.present", presencePresentEventHandler)
		subscribe(personsPresence, "presence.not present", presenceNotPresentEventHandler)
	}

	ifDebug("updateRoom 3", 'info')
	state.holidays = [:]
	for (def i = 1; i <= maxHolis; i++)	{
		if (!settings["holiName$i"] || !settings["holiColorString$i"])		continue;
		def holiColors = []
		def holiHues = [:]
		def holiColorCount = 0
		def str = settings["holiColorString$i"].split(',')
//		str.each	{
		for (def itc : str)		{
			holiColors << itc
			def hue = convertRGBToHueSaturation(itc)
			holiHues << [(holiColorCount):hue]
			holiColorCount = holiColorCount + 1
		}
		state.holidays << [(i):[name: settings["holiName$i"], count: holiColorCount, hues: holiHues, colors: holiColors, style: settings["holiStyle$i"], seconds: settings["holiSeconds$i"], level: settings["holiLevel$i"]]]
	}
	if (anotherRoomEngaged)	{
//        parent.subscribeChildrenToEngaged(app.id, anotherRoomEngaged)
//        subsribe(anotherRoomEngaged, "occupancy", anotherRoomEventHandler)
//		anotherRoomEngaged.each		{
		for (def rom : anotherRoomEngaged)		{
			def roomDeviceObject = parent.getChildRoomOccupancyDeviceObject(rom)
			if (roomDeviceObject)	{
				if (hT == _SmartThings)
					subscribe(roomDeviceObject, "button", anotherRoomEngagedButtonPushedEventHandler);
				else    {
					subscribe(roomDeviceObject, "pushed.8", anotherRoomEngagedButtonPushedEventHandler);        // asleep
					subscribe(roomDeviceObject, "pushed.9", anotherRoomEngagedButtonPushedEventHandler);        // engaged
				}
			}
		}
	}
	if (vacantButton)
		subscribe(vacantButton, (hT == _Hubitat ? "${heCapToAttrMap[vacantButtonType]}.$buttonIsVacant" : 'button'), buttonPushedVacantEventHandler)
	if (vacantSwitches)		subscribe(vacantSwitches, "switch.off", vacantSwitchOffEventHandler);

	ifDebug("updateRoom 4", 'info')
	if (luxSensor)		subscribe(luxSensor, "illuminance", luxEventHandler);
	if (powerDevice)	{
		subscribe(powerDevice, "power", powerEventHandler)
		state.previousPower = getIntfromStr((String) powerDevice.currentPower)
	}
	else
		state.previousPower = null
	if (speechDevice)		subscribe(speechDevice, "phraseSpoken", speechEventHandler);
	if (asleepSensor)		subscribe(asleepSensor, "sleeping", sleepEventHandler);
	if (asleepButton)
		subscribe(asleepButton, (hT == _Hubitat ? "${heCapToAttrMap[asleepButtonType]}.$buttonIsAsleep" : 'button'), buttonPushedAsleepEventHandler)
	if (asleepSwitch)	{
		subscribe(asleepSwitch, "switch.on", asleepSwitchOnEventHandler)
		subscribe(asleepSwitch, "switch.off", asleepSwitchOffEventHandler)
	}
	if (nightButton)
		subscribe(nightButton, (hT == _Hubitat ? "${heCapToAttrMap[nightButtonType]}.$nightButtonIs" : 'button'), nightButtonPushedEventHandler)
	state.noMotionAsleep = ((noMotionAsleep && noMotionAsleep >= 5) ? noMotionAsleep : null)
	state.nightSetLevelTo = (nightSetLevelTo ? nightSetLevelTo : null)
	state.nightSetCT = (nightSetCT ? nightSetCT as Integer : null)
	state.nightSetColorTo = (nightSetColorTo ?: null)
	state.nightSetHueTo = (state.nightSetColorTo && colorsRGB[state.nightSetColorTo] ? convertRGBToHueSaturation(colorsRGB[state.nightSetColorTo][1]) : [])
	state.noAsleep = ((noAsleep && noAsleep >= 1) ? (noAsleep * 60 * 60) : null)

	ifDebug("updateRoom 5", 'info')
	if (lockedContact)	{
		subscribe(lockedContact, "contact.${(lockedContactCmd ? 'open' : 'closed')}", lockedContactOpenEventHandler)
		subscribe(lockedContact, "contact.${(lockedContactCmd ? 'closed' : 'open')}", lockedContactClosedEventHandler)
	}

	if (lockedSwitch)	{
		subscribe(lockedSwitch, "switch.${(lockedSwitchCmd ? 'on' : 'off')}", lockedSwitchOnEventHandler)
		subscribe(lockedSwitch, "switch.${(lockedSwitchCmd ? 'off' : 'on')}", lockedSwitchOffEventHandler)
	}
	if (windowShades)		subscribe(windowShades, "windowShade", windowShadeEventHandler);
	if (thermoOverride)	{
		if (roomThermostat)		subscribe(roomThermostat, "thermostat", roomThermostatEventHandler);
		if (roomCoolSwitch)	{
			subscribe(roomCoolSwitch, "switch.on", roomCoolSwitchOnEventHandler)
			subscribe(roomCoolSwitch, "switch.off", roomCoolSwitchOffEventHandler)
		}
		if (roomHeatSwitch)	{
			subscribe(roomHeatSwitch, "switch.on", roomHeatSwitchOnEventHandler)
			subscribe(roomHeatSwitch, "switch.off", roomHeatSwitchOffEventHandler)
		}
	}
	state.roomThermoTurnedOn = false
	state.roomCoolTurnedOn = false
	state.roomHeatTurnedOn = false
	state.thermoOverride = false
	if (roomFanSwitch)	{
		subscribe(roomFanSwitch, "switch", updateFanIndP)
		subscribe(roomFanSwitch, "level", updateFanIndP)
	}
	state.unLocked = ((unLocked && unLocked >= 1) ? (unLocked * 60 * 60) : null)
	if (dayOfWeek)	{
		state.dayOfWeek = []
		switch(dayOfWeek)	{
			case '1':   case '2':   case '3':   case '4':   case '5':   case '6':   case '7':
						state.dayOfWeek << dayOfWeek;						break
			case '8':   state.dayOfWeek = state.dayOfWeek + [1,2,3,4,5];	break
			case '9':   state.dayOfWeek = state.dayOfWeek + [6,7];			break
			default:    state.dayOfWeek = null;								break
		}
	}
	else
		state.dayOfWeek = null

	ifDebug("updateRoom 6", 'info')
	if (contactSensorsRT)	{
		subscribe(contactSensorsRT, "contact.open", contactsRTOpenEventHandler)
		subscribe(contactSensorsRT, "contact.closed", contactsRTClosedEventHandler)
	}
	if (tempSensors)		subscribe(tempSensors, "temperature", temperatureEventHandler);
	if (outTempSensor)		subscribe(outTempSensor, "temperature", updateOutTempIndP)
	if (roomVents)	{
		subscribe(roomVents, "switch", updateVentIndP)
		subscribe(roomVents, "switchLevel", updateVentIndP)
		subscribe(roomVents, "level", updateVentIndP)
	}
	updateRulesToState()
	updateSwitchAttributesToStateAndSubscribe()

	if (humiditySensor)		{
		subscribe(humiditySensor, "humidity", humidityEventHandler,  [filterEvents:false])
		state.previousHumidity = getAvgHumidity()
	}
	else
		state.previousHumidity = null
	if (humiOverride)	{
		if (roomDehumidifierSwitch)	{
			subscribe(roomDehumidifierSwitch, "switch.on", roomDehumidifierSwitchOnEventHandler)
			subscribe(roomDehumidifierSwitch, "switch.off", roomDehumidifierSwitchOffEventHandler)
		}
		if (roomHumidifierSwitch)	{
			subscribe(roomHumidifierSwitch, "switch.on", roomHumidifierSwitchOnEventHandler)
			subscribe(roomHumidifierSwitch, "switch.off", roomHumidifierSwitchOffEventHandler)
		}
	}
	state.humidity = [dehumidifierTurnedOn:false, humidifierTurnedOn:false, override:false, offAt:false, offIn:false, forceOffIn:false, lastState:null, lastStateAt:0, hour:99, previous:false, lastRule:null, minsInState:0]
	humidityTimer()
	if (hT == _SmartThings)
		subscribe(location, "askAlexaMQ", askAlexaMQHandler)
	else		{
		state.askAlexaMQ = []
		if (echoAccessCode)		app.updateSetting("echoAccessCode", [type: "text", value: "${encrypt(settings['echoAccessCode'])}"]);
	}

//	def child = getChildDevice(getRoom())
//	subscribe(child, "occupancy", occupancyHandler)

	ifDebug("updateRoom final", 'info')
	if (hT != _Hubitat)		updateIndicators();
	state.processChild = (((new Date(now())).format("mm", location.timeZone).toInteger() + new Random().nextInt(60)) % 60)
	subscribe(location, "sunrise", scheduleFromToTimes)
	subscribe(location, "sunset", scheduleFromToTimes)
	runIn(1, scheduleFromToTimes)
	runIn(2, processCoolHeat)
//	processHumidity()
}

def updateRoomAdjMS(adjMotionSensors)	{
	ifDebug("updateRoomAdjMS", 'info')
//	state.adjMotionSensorsID = adjMotionSensors.collect{ it.id }
//log.debug adjMotionSensors
	state.adjMotionSensorsID = []
	if (adjMotionSensors && (adjRoomsMotion || adjRoomsPathway))	{
		for (def adjMS : adjMotionSensors)		{
			state.adjMotionSensorsID << adjMS.id;
			subscribe(adjMS, "motion.active", adjMotionActiveEventHandler)
			subscribe(adjMS, "motion.inactive", adjMotionInactiveEventHandler)
		}
	}
}

def	initialize()	{
	def hT = getHubType()
	unsubscribe()
	unschedule()
	sendLocationEvent(name: "AskAlexaMQRefresh", isStateChange: true)
	state.remove("pList")
	if (hT == _Hubitat)	{
//		while (settings["noMotion"])
//			app.removeSetting("noMotion");
		app.removeSetting("powerTimeType")
		app.removeSetting("roomButtonAction")
		app.removeSetting("occupiedButtonAction")
		app.removeSetting("engagedButtonAction")
		app.removeSetting("asleepButtonAction")
		app.removeSetting("vacantButtonAction")
		app.removeSetting("roomButton")
		app.removeSetting("buttonForRoom")
		app.removeSetting("roomButtonType")
	}
}

def askAlexaMQHandler(evt)	{
	if (evt)
		switch (evt.value)	{
			case "refresh":     (state.askAlexaMQ = evt.jsonData && evt.jsonData?.queues ? evt.jsonData.queues : []);   break;
		}
}

private deleteRules()	{
	if (getHubType() != _Hubitat || !rulesToDelete)		return;
	def ruleVars = ['name', 'disabled', 'state', 'mode', 'dayOfWeek', 'luxThreshold', 'powerThreshold', 'presenceCheck', 'checkOn', 'checkOff', 'fromHumidity', 'toHumidity', 'fromDate', 'toDate', 'fromTimeType', 'fromTime', 'fromTimeOffset', 'toTimeType', 'toTime', 'toTimeOffset', 'type', 'coolTemp', 'heatTemp', 'tempRange', 'fanOnTemp', 'fanSpeedIncTemp', 'device', 'cmds', 'piston', 'actions', 'musicAction', 'shade', 'switchesOn', 'setLevelTo', 'setColorTo', 'setColorTemperatureTo', 'switchesOff', 'noMotion', 'noMotionEngaged', 'dimTimer', 'noMotionAsleep']
	rulesToDelete.each	{ i ->
		ruleVars.each	{ app.removeSetting("$it$i") }
	}
	app.removeSetting("rulesToDelete")
	updateRulesToState()
}

private occupied()		{  hSs(occupied)  }

private checking()		{  hSs(checking)  }

private vacant()		{  hSs(vacant)  }

private asleep()		{  hSs(asleep)  }

private locked()		{  hSs(locked)  }

private engaged()		{  hSs(engaged)  }

private hSs(nSt)	{
	ifDebug("hSs: $nSt", 'info')
	def child = getChildDevice(getRoom())
	if (child)		{
		handleSwitches(child.currentValue(occupancy), nSt)
//		child."$nSt"(false)
		runIn(0, childAsync, [overwrite: false, data: [cFunction: nSt]])
	}
}

def childAsync(data)	{
	def nowTime = now()
	if (data.cFunction)		getChildDevice(getRoom())?."$data.cFunction"(false);
//log.debug "perf childAsync: ${now() - nowTime} ms"
}

def updateIndicators()	{
	ifDebug("updateIndicators", 'info')
	def child = getChildDevice(getRoom())
	def ind
	boolean isFarenheit = (location.temperatureScale == 'F' ? true : false)
	child.updateMotionInd((motionSensors ? (motionSensors.currentMotion.contains('active') ? 1 : 0) : -1))
	child.updateLuxInd(getAvgLux())
	child.updateHumidityInd(getAvgHumidity())
	child.updateContactInd((contactSensor ? (contactSensor.currentContact.contains('closed') ? 1 : 0) : -1))
	child.updateSwitchInd(isAnySwitchOn())
	child.updatePresenceInd((personsPresence ? (personsPresence.currentPresence.contains(present) ? 1 : 0) : -1))
	child.updatePresenceActionInd((personsPresence ? presenceAction : -1))
	child.updateDoWInd((dayOfWeek ?: -1))
	child.updateTemperatureInd(getAvgTemperature())
	child.updateRulesInd((state.rules ? state.rules.size() : -1))
	child.updateLastRuleInd(-1)
	child.updatePowerInd((powerDevice ? getIntfromStr((String) powerDevice.currentPower) : -1))
	if (pauseModes)	{
		ind = ''
		for (def mod : pauseModes)		{ ind = ind + (ind.size() > 0 ? ', ' : '') + mod }
	}
	else
		ind = -1
	child.updatePauseInd(ind)
	child.updateESwitchInd(isAnyESwitchOn())
	child.updateOSwitchInd(isAnyOSwitchOn())
	child.updateASwitchInd(isAnyASwitchOn())
	child.updateNSwitchInd(isAnyNSwitchOn())
	child.updatePresenceEngagedInd((personsPresence ? (presenceActionContinuous ? 'Yes' : 'No') : -1))
	child.updateBusyEngagedInd((busyCheck ? (busyCheck == lightTraffic ? 'Light' : (busyCheck == mediumTraffic ? 'Medium' : 'Heavy')) : -1))
	child.updateLSwitchInd(isAnyLSwitchOn())
	updateTimers()
	child.updateTurnAllOffInd(allSwitchesOff ? 'Yes' : 'No')
	child.updateDimByLevelInd((state.dimByLevel ?: -1), (state.dimToLevel ?: -1))
	child.updateEWattsInd(powerValueEngaged ?: -1)
	child.updateAWattsInd(powerValueAsleep ?: -1)
	child.updateContactRTIndC((contactSensorsRT ? (contactSensorsRT.currentContact.contains('closed') ? 1 : 0) : -1))
	child.updateAdjRoomsInd((adjRooms ? adjRooms.size() : -1))
	child.updateAdjMotionInd(-1)
	updateThermostatIndP()
	updateFanIndP()
	child.setupAlarmC()
	updateOutTempIndP()
	updateVentIndP()
}

private getAvgLux()	{
	int countLuxSensors = (luxSensor ? (luxSensor instanceof List ? luxSensor.size() : 1) : 0)
	if (countLuxSensors < 1)	return -1;
	def luxes = luxSensor.currentIlluminance
	def lux = 0.0f
	for (def lx : luxes)	lux = lux + lx;
	return (lux / countLuxSensors).round(1)
}

private getAvgTemperature()	{
	int countTempSensors = (tempSensors ? (tempSensors instanceof List ? tempSensors.size() : 1) : 0)
	if (countTempSensors < 1)	return -1;
	def temperatures = tempSensors.currentTemperature
	def temperature = 0.0f
	for (def tm : temperatures)		temperature = temperature + tm;
	return (temperature / countTempSensors).round(1)
}

def updateOutTempIndP(evt = null)	{
	ifDebug("updateOutTempIndP")
	def child = getChildDevice(getRoom())
	if (getHubType() != _Hubitat)	child.updateOutTempIndC((outTempSensor ? outTempSensor.currentTemperature : -1));
}

private getAvgHumidity()	{
	int countHumiditySensors = (humiditySensor ? (humiditySensor instanceof List ? humiditySensor.size() : 1) : 0)
	if (countHumiditySensors < 1)	return -1;
	def humidities = humiditySensor.currentHumidity
	def humidity = 0.0f
	for (def hm : humidities)		humidity = humidity + hm;
	return (humidity / countHumiditySensors).round(1)
}

private isAnySwitchOn()	{
	def ind = -1
	for (def i = 1; i <= maxRules; i++)	{
		def ruleNo = String.valueOf(i)
		def thisRule = getNextRule(ruleNo, _ERule)
		if (thisRule.ruleNo == 'EOR')	break;
		i = thisRule.ruleNo as Integer
		if (thisRule.switchesOn && thisRule.switchesOn.currentSwitch.contains('on'))	{
			ind = 1
			break
		}
		else
			ind = 0
	}
	return ind
}

private isAnyOSwitchOn()		{  return (occSwitches ? (occSwitches.currentSwitch.contains('on') ? 1 : 0) : -1)  }

private isAnyESwitchOn()		{  return (engagedSwitch ? (engagedSwitch.currentSwitch.contains('on') ? 1 : 0) : -1)  }

private isAnyASwitchOn()		{  return (asleepSwitch ? (asleepSwitch.currentSwitch.contains('on') ? 1 : 0) : -1)  }

private isAnyNSwitchOn()		{  return (nightSwitches ? (nightSwitches.currentSwitch.contains('on') ? 1 : 0) : -1)  }

private isAnyLSwitchOn()		{  return (lockedSwitch ? (lockedSwitch.currentSwitch.contains('on') ? 1 : 0) : -1)  }

def updateRulesToState()	{
	state.timeCheck = false
	state.ruleHasAL = false
	state.ruleHasHL = false
	state.vacant = false
	state.powerCheck = false
	state.execute = false
	state.maintainRoomTemp = false
	state.maintainRoomHumi = false
	state.rules = false
	for (def i = 1; i <= maxRules; i++)		{
		def ruleNo = String.valueOf(i)
		def thisRule = getRule(ruleNo, '*', false)
		if (thisRule && !thisRule.disabled)		{
			state.maxRuleNo = i
			if (!state.rules)		state.rules = [:];
			state.rules << ["$ruleNo":[isRule:true]]
			if (!thisRule.type || thisRule.type == _ERule)		{
				state.execute = true
				if (thisRule.state && thisRule.state.contains('vacant'))		state.vacant = true;
				if (thisRule.powerThreshold)		state.powerCheck = true;
			}
			else if (thisRule.type == _TRule)	state.maintainRoomTemp = true;
			else if (thisRule.type == _HRule)	state.maintainRoomHumi = true;
			if (thisRule.level == 'AL')			state.ruleHasAL = true;
			else if (thisRule.level?.startsWith('HL'))		state.ruleHasHL = true;
//			if (thisRule.fromTimeType && thisRule.toTimeType)		state.timeCheck = true;
			if ((thisRule.fromTimeType && (thisRule.fromTimeType != _timeTime || thisRule.fromTime)) &&
				(thisRule.toTimeType && (thisRule.toTimeType != _timeTime || thisRule.toTime)))
				state.timeCheck = true
		}
	}
}

def updateSwitchAttributesToStateAndSubscribe()	{
	ifDebug("updateSwitchAttributesToStateAndSubscribe", 'info')
	def hT = getHubType()
	def sOn = []
	def sOff = []
	def sID = []
	def checkSwitches = []
	def cID = []
	for (def i = 1; i <= maxRules; i++)	{
		def ruleNo = String.valueOf(i)
		def thisRule = getNextRule(ruleNo, _ERule, false, false)
		if (thisRule.ruleNo == 'EOR')	break;
		for (def swt : thisRule.switchesOn)		{
			def itID = swt.getId()
			if (!sID.contains(itID))	{
				sOn << swt
				sID << itID
				if (swt.hasCommand("setLevel"))		state.switchesHasLevel << ["$itID":true];
				if (swt.hasCommand("setColor"))		state.switchesHasColor << ["$itID":true];
				if (swt.hasCommand("setColorTemperature"))		state.switchesHasColorTemperature << ["$itID":true];
			}
		}
		for (def swt : thisRule.switchesOff)		{
			def itID = swt.getId()
			if (!sID.contains(itID))	{
				sOff << swt
				sID << itID
				if (swt.hasCommand("setLevel"))		state.switchesHasLevel << ["$itID":true];
				if (swt.hasCommand("setColor"))		state.switchesHasColor << ["$itID":true];
				if (swt.hasCommand("setColorTemperature"))		state.switchesHasColorTemperature << ["$itID":true];
			}
		}
		for (def swt : thisRule.checkOn)		{
			def itID = swt.getId()
			if (!cID.contains(itID))	{  checkSwitches << swt; cID << itID  }
		}
		for (def swt : thisRule.checkOff)		{
			def itID = swt.getId()
			if (!cID.contains(itID))	{  checkSwitches << swt; cID << itID  }
		}
	}
	if (hT != _Hubitat && sOn)	{
		subscribe(sOn, "switch.on", switchOnEventHandler)
		subscribe(sOn, "switch.off", switchOffEventHandler)
	}
	for (def swt : checkSwitches)	subscribe(swt, "switch", checkSwitchEventHandler);
	for (def swt : nightSwitches)	{
		def itID = swt.getId()
		if (!sID.contains(itID))	{
			sID << itID
			if (swt.hasCommand("setLevel"))		state.switchesHasLevel << ["$itID":true];
			if (swt.hasCommand("setColor"))		state.switchesHasColor << ["$itID":true];
			if (swt.hasCommand("setColorTemperature"))		state.switchesHasColorTemperature << ["$itID":true];
		}
	}
}

private getNextRule(ruleNo, ruleType = '*', checkState = true, getConditionsOnly = false)	{
	def thisRule
	for (def i = ruleNo as Integer; i <= maxRules; i++)		{
		if (checkState && i > state.maxRuleNo)	break;
		thisRule = getRule(String.valueOf(i), ruleType, checkState, getConditionsOnly)
		if (thisRule && !thisRule.disabled)		return thisRule;
	}
	return [ruleNo:'EOR']
}

private getRule(ruleNo, ruleTypeP = '*', checkState = true, getConditionsOnly = false)	{
	def nowTime = now()
	if (!ruleNo)	return null;
	if (checkState && (!state.rules || !state.rules[ruleNo.toString()]))	return null;
	if (ruleTypeP == _ERule)		ruleTypeP = null;
	if (checkState && ((!ruleTypeP && !state.execute) || (ruleTypeP == _TRule && !state.maintainRoomTemp) || (ruleTypeP == _HRule && !state.maintainRoomHumi)))
		return null;
	def ruleType = settings["type$ruleNo"]
	if (ruleType == _ERule)		ruleType = null;
	if (ruleTypeP != '*' && ruleType != ruleTypeP)	return null;
	def ruleName = settings["name$ruleNo"]
	def ruleDisabled = settings["disabled$ruleNo"]
	def ruleMode = settings["mode$ruleNo"]
	def ruleState = settings["state$ruleNo"]
	def ruleDayOfWeek = []
	def hT = getHubType()
	if (settings["dayOfWeek$ruleNo"])	{
		switch(settings["dayOfWeek$ruleNo"])	{
			case '1':	case '2':   case '3':   case '4':   case '5':   case '6':   case '7':
						ruleDayOfWeek << settings["dayOfWeek$ruleNo"];	break;
			case '8':	ruleDayOfWeek = ruleDayOfWeek + [1,2,3,4,5];	break;
			case '9':	ruleDayOfWeek = ruleDayOfWeek + [6,7];			break;
			default:	ruleDayOfWeek = null;							break;
		}
	}
	else
		ruleDayOfWeek = null
	def ruleLuxThreshold = settings["luxThreshold$ruleNo"]
	def rulePowerThreshold = settings["powerThreshold$ruleNo"]
	def rulePresenceCheck = settings["presenceCheck$ruleNo"]
	def ruleCheckOn = settings["checkOn$ruleNo"]
	def ruleCheckOff = settings["checkOff$ruleNo"]
	def ruleFromHumidity = settings["fromHumidity$ruleNo"]
	def ruleToHumidity = settings["toHumidity$ruleNo"]
	def rD = dateInputValid(settings["fromDate$ruleNo"], settings["toDate$ruleNo"])
	def ruleFromDate = (!ruleType || ruleType == _ERule ? rD[0] : null)
	def ruleToDate = (!ruleType || ruleType == _ERule ? rD[1] : null)
	def ruleFromTimeType = settings["fromTimeType$ruleNo"]
	def ruleFromTimeOffset = settings["fromTimeOffset$ruleNo"]
	def ruleFromTime = settings["fromTime$ruleNo"]
	def ruleToTimeType = settings["toTimeType$ruleNo"]
	def ruleToTimeOffset = settings["toTimeOffset$ruleNo"]
	def ruleToTime = settings["toTime$ruleNo"]
//	if (hT == _Hubitat)	{
//log.debug "ruleFromTime: $ruleFromTime | ruleToTime: $ruleToTime"
//		if (ruleFromTime)	ruleFromTime = ruleFromTime.replace("+0000", "-0800");
//		if (ruleToTime)		ruleToTime = ruleToTime.replace("+0000", "-0800");
//log.debug "ruleFromTime: $ruleFromTime | ruleToTime: $ruleToTime"
//	}

	def ret
	if (ruleType == _HRule)		{
		def ruleDehumiOn = settings["dehumiOn$ruleNo"]
		def ruleHumiOn = settings["humiOn$ruleNo"]
		def ruleHumiCmp = settings["humiCmp$ruleNo"]
		def ruleHumiValue = settings["humiValue$ruleNo"]
		def ruleHumiPercent = settings["humiPercent$ruleNo"]
        def ruleHumiMins = settings["humiMins$ruleNo"]
		def ruleHumiMinRun = settings["humiMinRun$ruleNo"]
		def ruleHumiMaxRun = settings["humiMaxRun$ruleNo"]
		def ruleHumiPrvState = settings["humiPrvState$ruleNo"]
		if (!(ruleName || ruleDisabled || ruleMode || ruleState || ruleDayOfWeek ||
						ruleFromTimeType || ruleToTimeType || ruleDehumiOn || ruleHumiOn || ruleHumiCmp || ruleHumiValue || ruleHumiPercent))
			ret = null
		else
			ret = [ruleNo:ruleNo, type:ruleType, name:ruleName, disabled:ruleDisabled, mode:ruleMode, state:ruleState, dayOfWeek:ruleDayOfWeek,
					fromTimeType:ruleFromTimeType, fromTimeOffset:ruleFromTimeOffset, fromTime:ruleFromTime,
					toTimeType:ruleToTimeType, toTimeOffset:ruleToTimeOffset, toTime:ruleToTime, deHumiOn:ruleDehumiOn, humiOn:ruleHumiOn,
					humiCmp:ruleHumiCmp, humiValue:ruleHumiValue, humiPercent:ruleHumiPercent, humiMins:ruleHumiMins, humiMinRun:ruleHumiMinRun, humiMaxRun:ruleHumiMaxRun, humiPrvState: ruleHumiPrvState]
	}
	else if (ruleType == _TRule)	{
		def ruleRoomCoolTemp = settings["coolTemp$ruleNo"]
		def ruleRoomHeatTemp = settings["heatTemp$ruleNo"]
		def ruleTempRange = settings["tempRange$ruleNo"]
		def ruleFanOnTemp = settings["fanOnTemp$ruleNo"]
		def ruleFanSpeedIncTemp = settings["fanSpeedIncTemp$ruleNo"]
		if (!(ruleName || ruleDisabled || ruleMode || ruleState || ruleDayOfWeek || ruleFromTimeType || ruleToTimeType || ruleRoomCoolTemp || ruleRoomHeatTemp || ruleFanOnTemp))
			ret = null
		else
			ret = [ruleNo:ruleNo, type:ruleType, name:ruleName, disabled:ruleDisabled, mode:ruleMode, state:ruleState, dayOfWeek:ruleDayOfWeek,
					fromTimeType:ruleFromTimeType, fromTimeOffset:ruleFromTimeOffset, fromTime:ruleFromTime,
					toTimeType:ruleToTimeType, toTimeOffset:ruleToTimeOffset, toTime:ruleToTime,
					coolTemp:ruleRoomCoolTemp, heatTemp:ruleRoomHeatTemp, tempRange:ruleTempRange, fanOnTemp:ruleFanOnTemp, fanSpeedIncTemp:ruleFanSpeedIncTemp]
	}
	else    {
		if (getConditionsOnly)	{
			if (!(ruleName || ruleDisabled || ruleMode || ruleState || ruleDayOfWeek || ruleLuxThreshold != null || rulePowerThreshold || rulePresenceCheck || ruleCheckOn || ruleCheckOff || (ruleFromHumidity && ruleToHumidity) || ruleFromDate || ruleToDate || ruleFromTimeType || ruleToTimeType))
				ret = null
			else
				ret = [ruleNo:ruleNo, type:ruleType, name:ruleName, disabled:ruleDisabled, mode:ruleMode, state:ruleState, dayOfWeek:ruleDayOfWeek,
						luxThreshold:ruleLuxThreshold, powerThreshold:rulePowerThreshold, presence:rulePresenceCheck,
						checkOn:ruleCheckOn, checkOff:ruleCheckOff,
						fromHumidity:ruleFromHumidity, toHumidity:ruleToHumidity,
						fromDate:ruleFromDate, toDate:ruleToDate,
						fromTimeType:ruleFromTimeType, fromTimeOffset:ruleFromTimeOffset, fromTime:ruleFromTime,
						toTimeType:ruleToTimeType, toTimeOffset:ruleToTimeOffset, toTime:ruleToTime]
		}
		else	{
			def ruleDevice = settings["device$ruleNo"]
			def ruleDeviceCmds = settings["cmds$ruleNo"]
			def rulePiston = settings["piston$ruleNo"]
			def ruleActions = settings["actions$ruleNo"]
			def ruleMusicAction = settings["musicAction$ruleNo"]
			def ruleShadePostion = settings["shadePosition$ruleNo"]
			def ruleSwitchesOn = settings["switchesOn$ruleNo"]
			def ruleSetLevelTo = settings["setLevelTo$ruleNo"]
			def ruleSetColorTo = settings["setColorTo$ruleNo"]
			def ruleSetHueTo = (ruleSetColorTo && colorsRGB[ruleSetColorTo] ? convertRGBToHueSaturation(colorsRGB[ruleSetColorTo][1]) : [])
			def ruleSetColorTemperatureTo = settings["setColorTemperatureTo$ruleNo"]
			def ruleSwitchesOff = settings["switchesOff$ruleNo"]
			def ruleNoMotion = settings["noMotion$ruleNo"]
			def ruleNoMotionEngaged = settings["noMotionEngaged$ruleNo"]
			def ruleDimTimer = settings["dimTimer$ruleNo"]
			def ruleNoMotionAsleep = settings["noMotionAsleep$ruleNo"]
			if (!(ruleName || ruleDisabled || ruleMode || ruleState || ruleDayOfWeek || ruleLuxThreshold != null || rulePowerThreshold || rulePresenceCheck || ruleCheckOn || ruleCheckOff || (ruleFromHumidity && ruleToHumidity) || ruleFromDate || ruleToDate || ruleFromTimeType || ruleToTimeType ||
					ruleDevice || ruleDeviceCmds || rulePiston || ruleActions || ruleMusicAction || ruleShadePostion ||
					ruleSwitchesOn || ruleSetLevelTo || ruleSetColorTo || ruleSetColorTemperatureTo || ruleSwitchesOff ||
					ruleNoMotion || ruleNoMotionEngaged || ruleDimTimer || ruleNoMotionAsleep))
				ret = null
			else
				ret = [ruleNo:ruleNo, type:ruleType, name:ruleName, disabled:ruleDisabled, mode:ruleMode, state:ruleState, dayOfWeek:ruleDayOfWeek,
						luxThreshold:ruleLuxThreshold, powerThreshold:rulePowerThreshold, presence:rulePresenceCheck,
						checkOn:ruleCheckOn, checkOff:ruleCheckOff,
						fromHumidity:ruleFromHumidity, toHumidity:ruleToHumidity,
						fromDate:ruleFromDate, toDate:ruleToDate,
						fromTimeType:ruleFromTimeType, fromTimeOffset:ruleFromTimeOffset, fromTime:ruleFromTime,
						toTimeType:ruleToTimeType, toTimeOffset:ruleToTimeOffset, toTime:ruleToTime,
						device:ruleDevice, commands:ruleDeviceCmds,
						piston:rulePiston, actions:ruleActions, musicAction:ruleMusicAction, shade:ruleShadePostion,
						switchesOn:ruleSwitchesOn, level:ruleSetLevelTo, color:ruleSetColorTo, hue:ruleSetHueTo,
						colorTemperature:ruleSetColorTemperatureTo, switchesOff:ruleSwitchesOff,
						noMotion:ruleNoMotion, noMotionEngaged:ruleNoMotionEngaged, dimTimer:ruleDimTimer, noMotionAsleep:ruleNoMotionAsleep]
		}
	}
	return ret
}

def	modeEventHandler(evt)	{
	ifDebug("modeEventHandler", 'info')
	if (state.dayOfWeek && !(checkRunDay()))	return;
	def rSt = getChildDevice(getRoom())?.currentValue(occupancy)
	if (awayModes && awayModes.contains(evt.value))				roomVacant(true);
	else if (pauseModes && pauseModes.contains(evt.value))		unscheduleAll("mode handler");
	else if (!onlyOnStateChange || (butNotInStates && butNotInStates.contains(rSt)))			switchesOnOrOff();
}

def	motionActiveEventHandler(evt)	{
	def nowTime = now()
	ifDebug("motionActiveEventHandler", 'info')
	def child = getChildDevice(getRoom())
	if (getHubType() != _Hubitat)		child.updateMotionInd(1);
	if (!checkPauseModesAndDoW())	return;
	def rSt = child?.currentValue(occupancy)
	if (rSt == vacant && triggerMotionSensors && !triggerMotionSensors.contains(evt.deviceId.toString()))	return;
	if (rSt == asleep)		{
//		ifDebug("$nightMotionSensors | $evt.deviceId | ${(nightMotionSensors ? !nightMotionSensors.contains(evt.deviceId.toString()) : false)}")
		if (nightMotionSensors && !nightMotionSensors.contains(evt.deviceId.toString()))	return;
		if (nightSwitches && nightTurnOn.contains('1'))	{
			dimNightLights()
			if (state.noMotionAsleep && whichNoMotion != lastMotionInactive)	{
				updateChildTimer(state.noMotionAsleep)
				runIn(state.noMotionAsleep, nightSwitchesOff)
			}
		}
		return
	}
	unscheduleAll("motion active handler")
	if (rSt == engaged)		{
		if (whichNoMotion == lastMotionActive && state.noMotionEngaged)		refreshEngagedTimer(engaged);
		return
	}
//	ifDebug("repeated motion: $state.busyCheck | $repeatedMotion | $state.motionTraffic | $rSt")
	if (state.busyCheck && repeatedMotion && rSt == occupied)	{
		state.motionTraffic = state.motionTraffic + 1
		if (state.motionTraffic >= state.busyCheck)		state.isBusy = true;
	}
	if (state.isBusy && ['occupied', 'checking', 'vacant'].contains(rSt))	{
		turnOffIsBusy(); engaged(); return
	}
	if (['checking', 'vacant'].contains(rSt))	{
		if (isRoomEngaged())
			engaged()
		else	{
			def cPwr = (powerDevice ? getIntfromStr((String) powerDevice.currentPower) : null)
			if (powerDevice && powerValueAsleep && cPwr >= powerValueAsleep && (powerTriggerFromVacant || rSt != vacant))
				asleep()
			else if (powerDevice && powerValueLocked && cPwr >= powerValueLocked && (powerTriggerFromVacant || rSt != vacant))
				locked()
			else
				occupied()
		}
	}
	else if (rSt == occupied && whichNoMotion == lastMotionActive && state.noMotion)
		refreshOccupiedTimer(occupied)
}

def	motionInactiveEventHandler(evt)	{
	ifDebug("motionInactiveEventHandler", 'info')
	def child = getChildDevice(getRoom())
	def motionActive = motionSensors.currentMotion.contains(active)
	if (getHubType() != _Hubitat)		child.updateMotionInd( motionActive ? 1 : 0);
	if (!checkPauseModesAndDoW())	return;
	def rSt = child?.currentValue(occupancy)
	if (rSt == engaged)	{
		if (whichNoMotion == lastMotionInactive && state.noMotionEngaged)		refreshEngagedTimer(engaged);
	}
	else if (rSt == occupied)	{
		if (state.noMotion && whichNoMotion == lastMotionInactive && !motionActive)		refreshOccupiedTimer(occupied);
	}
	else if (rSt == asleep && nightSwitches && nightTurnOn.contains('1'))	{
		if (!nightMotionSensors || nightMotionSensors.contains(evt.deviceId.toString()))	{
			if (whichNoMotion == lastMotionInactive && !motionActive)	{
				if (state.noMotionAsleep)		updateChildTimer(state.noMotionAsleep);
				runIn((state.noMotionAsleep ?: 1), nightSwitchesOff)
			}
		}
	}
}

def adjMotionActiveEventHandler(evt)	{
	ifDebug("adjMotionActiveEventHandler", 'info')
//log.debug "$state.adjMotionSensorsID | ${evt.device.id.toString()}"
	if (!state.adjMotionSensorsID || !state.adjMotionSensorsID.contains(evt.device.id.toString()))	return;
	def child = getChildDevice(getRoom())
	if (getHubType() != _Hubitat)		child.updateAdjMotionInd(1);
	if (!checkPauseModesAndDoW())	return;
	def rSt = child?.currentValue(occupancy)
	if (adjRoomsMotion && rSt == occupied)	{
		def mV = motionSensors?.currentMotion.contains(active)
		def mD = motionSensors?.getLastActivity().max()
		if (!(mV && mD > evt.date))		checking();
		return
	}
/*
	if (adjRoomsPathway && rSt == vacant)
		for (def adj : adjRooms)		{
			def lastStateDate = parent.getLastStateDate(adj)
			if (lastStateDate['state'])		{
				def evtDate = evt.date.getTime()
				def lsDate = lastStateDate['date']
				def dateDiff = (evtDate - lsDate) + 0
				if (lastStateDate['state'] == vacant)	{
//					processRules(occupied)
					checking()
					return
				}
			}
		}
*/
	if (adjRoomsPathway && rSt == vacant)
		for (def adj : adjRooms)		{
			def currentState = parent.getCurrentState(adj)
//log.debug currentState
			if (currentState == vacant)		{
				checking()
				return
			}
		}
}

def adjMotionInactiveEventHandler(evt)	{
	ifDebug("adjMotionInactiveEventHandler", 'info')
	if (!state.adjMotionSensorsID || !state.adjMotionSensorsID.contains(evt.device.id.toString()))	return;
	if (getHubType() != _Hubitat)	getChildDevice(getRoom())?.updateAdjMotionInd(0);
}

def	roomButtonPushedEventHandler(evt)	{
	ifDebug("roomButtonPushedEventHandler: $evt.name | $evt.value", 'info')
	if (!checkPauseModesAndDoW())	return;
	if (getHubType() == _SmartThings)	{
		if (!evt.data)	return;
		def eD = new groovy.json.JsonSlurper().parseText(evt.data)
		assert eD instanceof Map
		if (!eD || !eD['buttonNumber'])	return;
		def buttonForRoom = false
		for (def i = 1; i <= maxButtons; i++)	{
//            ifDebug(settings["roomButton$i"])
//            ifDebug(settings["roomButtonNumber$i"])
//            ifDebug(evt.deviceId)
//            ifDebug(settings["roomButton$i"]?.id)
//            ifDebug("${settings["roomButton$i"]} | ${settings["roomButtonNumber$i"]} | ${eD['buttonNumber']} | $evt.deviceId | ${settings["roomButton$i"]?.id}")
			if (settings["roomButton$i"] && settings["roomButtonNumber$i"] && evt.deviceId == settings["roomButton$i"].id && eD['buttonNumber'] == settings["roomButtonNumber$i"] as Integer)	{
				buttonForRoom = true
				break
			}
		}
		ifDebug("buttonForRoom: $buttonForRoom")
//        if (!eD || (buttonForRoom && eD['buttonNumber'] && eD['buttonNumber'] != buttonForRoom as Integer))	return;
		if (!buttonForRoom)	return;
	}
	def child = getChildDevice(getRoom())
	def rSt = child?.currentValue(occupancy)
	def nRSt = engaged
	def nextState = false
	for (def btn : roomButtonStates)	{
		if (nextState)	{
			nRSt = btn
			nextState = false
		}
		if (btn == rSt)		nextState = true;
	}
	"$nRSt"()
}

def	buttonPushedOccupiedEventHandler(evt)	{
	ifDebug("buttonPushedOccupiedEventHandler", 'info')
	if (!checkPauseModesAndDoW())	return;
	if (getHubType() == _SmartThings)	{
		if (!evt.data)	return;
		def eD = new groovy.json.JsonSlurper().parseText(evt.data)
		assert eD instanceof Map
		if (!eD || (buttonIsOccupied && eD['buttonNumber'] && eD['buttonNumber'] != buttonIsOccupied as Integer))	return;
	}
	def child = getChildDevice(getRoom())
	def rSt = child?.currentValue(occupancy)
	if (rSt == occupied)	{
		if (!buttonOnlySetsOccupied)
///			child.checking()
			checking()
	}
	else
///		child.occupied()
		occupied()
}

def occupiedSwitchOnEventHandler(evt)	{
	ifDebug("occupiedSwitchOnEventHandler", 'info')
	def child = getChildDevice(getRoom())
	if (getHubType() != _Hubitat)		child.updateOSwitchInd(isAnyOSwitchOn());
	def rSt = child?.currentValue(occupancy)
	if (!proceedWithOccupied(rSt))		return;
	if (!['vacant','occupied','checking', 'engaged'].contains(rSt))		return;
	if (['vacant', 'checking'].contains(rSt))
		"${(isRoomEngaged() ? engaged : occupied)}"()
	else if (rSt == occupied && state.noMotion)	{
		if (motionSensors && whichNoMotion == lastMotionInactive && motionSensors.currentMotion.contains(active))
			unscheduleAll("occupiedSwitchOnEventHandler")
		else
			refreshOccupiedTimer(occupied)
	}
}

def occupiedSwitchOffEventHandler(evt)	{
	ifDebug("occupiedSwitchOffEventHandler", 'info')
	def child = getChildDevice(getRoom())
	if (getHubType() != _Hubitat)		child.updateOSwitchInd(isAnyOSwitchOn());
	def rSt = child?.currentValue(occupancy)
	if (!proceedWithOccupied(rSt))		return;
	if (rSt == occupied && !occSwitches.currentSwitch.contains(on) && (!motionSensors || whichNoMotion != lastMotionInactive || !motionSensors.currentMotion.contains(active)))
		roomVacant();
///		child."${(motionSensors && motionSensors?.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : checking)}"()
//		"${(motionSensors && motionSensors?.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : (state.dimTimer ? checking : vacant))}"()
}

private proceedWithOccupied(rSt)	{
	if (!checkPauseModesAndDoW())				return false;
	if (rSt == locked && lockedOverrides)		return false;
	if (rSt == asleep && asleepOverrides)		return false;
	if (rSt == engaged && engagedOverrides)		return false;
	return true
}

def	switchOnEventHandler(evt)	{
	ifDebug("switchOnEventHandler", 'info')
	if (getHubType() != _Hubitat)	getChildDevice(getRoom()).updateSwitchInd(isAnySwitchOn());
//	if (!checkPauseModesAndDoW())	return;
}

def	switchOffEventHandler(evt)	{
	ifDebug("switchOffEventHandler", 'info')
	if (getHubType() != _Hubitat)	getChildDevice(getRoom()).updateSwitchInd(isAnySwitchOn());
//	if (!checkPauseModesAndDoW())	return;
}

def	buttonPushedEventHandler(evt)	{
	ifDebug("buttonPushedEventHandler", 'info')
	if (!checkPauseModesAndDoW())	return;
	if (getHubType() == _SmartThings)	{
		if (!evt.data)	return;
		def eD = new groovy.json.JsonSlurper().parseText(evt.data)
		assert eD instanceof Map
		if (!eD || (buttonIs && eD['buttonNumber'] != buttonIs as Integer))	return;
	}
	def child = getChildDevice(getRoom())
	def rSt = child?.currentValue(occupancy)
/*    if (child?.currentValue(occupancy) == engaged)
		child.generateEvent((resetEngagedDirectly ? vacant : checking))
	else
		child.engaged()
*/
//    ifDebug("buttonPushedEventHandler: ${child.currentValue(occupancy)}")
//    child.generateEvent((child?.currentValue(occupancy) != engaged ? engaged : (resetEngagedDirectly ? vacant : checking)))
	if (rSt == engaged)	{
		if (!buttonOnlySetsEngaged)
///			child."${(resetEngagedDirectly ? vacant : checking)}"();
//			"${(resetEngagedDirectly ? vacant : checking)}"()
			roomVacant(true)
	}
	else	engaged();
//        child.generateEvent(motionSensors?.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : checking)
///		child.engaged()
}

def	buttonPushedVacantEventHandler(evt)	{
	ifDebug("buttonPushedVacantEventHandler", 'info')
	if (!checkPauseModesAndDoW())	return;
	if (getHubType() == _SmartThings)	{
		if (!evt.data)	return;
		def eD = new groovy.json.JsonSlurper().parseText(evt.data)
		assert eD instanceof Map
		if (!eD || (buttonIsVacant && eD['buttonNumber'] && eD['buttonNumber'] != buttonIsVacant as Integer))	return;
	}
	def child = getChildDevice(getRoom())
//    ifDebug("buttonPushedVacantEventHandler: ${child.currentValue(occupancy)}")
	def rSt = child?.currentValue(occupancy)
//    if (['engaged', 'occupied', 'checking'].contains(rSt))
/*    if (rSt != vacant)
		child.vacant()
	else if (buttonToggleWithVacant && rSt == vacant)
		child.occupied()
*/
	if (rSt != vacant)
///		child.vacant()
		vacant()
//    child.generateEvent(rSt != vacant ? vacant : checking)
// added 18-01-30: if room is already vacant or another state dont do anything
/*    else    {
		if (rSt == vacant)
			child.generateEvent('checking')
	}*/
}

def	vacantSwitchOffEventHandler(evt)	{
	ifDebug("vacantSwitchOffEventHandler", 'info')
	if (!checkPauseModesAndDoW())	return;
	def child = getChildDevice(getRoom())
	def rSt = child?.currentValue(occupancy)
//    if (rSt == locked && lockedOverrides)	return;
	if (['engaged', 'occupied', 'checking'].contains(rSt))		vacant();
}

def	buttonPushedAsleepEventHandler(evt)		{
	ifDebug("buttonPushedAsleepEventHandler", 'info')
	if (!checkPauseModesAndDoW())	return;
	if (getHubType() == _SmartThings)	{
		if (!evt.data)	return;
		def eD = new groovy.json.JsonSlurper().parseText(evt.data)
		assert eD instanceof Map
		if (!eD || (buttonIsAsleep && eD['buttonNumber'] != buttonIsAsleep as Integer))	return;
	}
	def child = getChildDevice(getRoom())
	def rSt = child?.currentValue(occupancy)
	if (rSt == asleep)	{
		if (!buttonOnlySetsAsleep)
///			child."${(resetAsleepDirectly ? vacant : checking)}"();
//			"${(resetAsleepDirectly ? vacant : checking)}"()
			roomAwake()
	}
	else
///		child.asleep()
		asleep()
}

def	anotherRoomEngagedButtonPushedEventHandler(evt)		{
	ifDebug("anotherRoomEngagedButtonPushedEventHandler", 'info')
	if (!checkPauseModesAndDoW())	return;
	if (personsPresence && presenceActionContinuous && personsPresence.currentPresence.contains(present))	return;
	if (getHubType() == _SmartThings)	{
		if (!evt.data)	return;
		def eD = new groovy.json.JsonSlurper().parseText(evt.data)
		assert eD instanceof Map
		if (!eD || eD['buttonNumber'] != 9)	return;
	}
	def child = getChildDevice(getRoom())
	if (child?.currentValue(occupancy) == engaged)		roomVacant();
///		child."${(resetEngagedDirectly ? vacant : (motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : checking))}"()
//		"${(resetEngagedDirectly ? vacant : (motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : checking))}"()
}

def	presencePresentEventHandler(evt)	{
	ifDebug("presencePresentEventHandler", 'info')
	def child = getChildDevice(getRoom())
	if (getHubType() != _Hubitat)		child.updatePresenceInd(1);
	if (!checkPauseModesAndDoW())	return;
	def rSt = child?.currentValue(occupancy)
	if (rSt == locked && lockedOverrides)	return;
	if (rSt == asleep && asleepOverrides)	return;
	if (presenceActionArrival() && ['occupied', 'checking', 'vacant'].contains(rSt))		engaged();
///		child.engaged();
	processCoolHeat()
}

def	presenceNotPresentEventHandler(evt)		{
	ifDebug("presenceNotPresentEventHandler", 'info')
	if (personsPresence.currentPresence.contains(present))	return;
	def child = getChildDevice(getRoom())
	if (getHubType() != _Hubitat)		child.updatePresenceInd(0);
	if (!checkPauseModesAndDoW())	return;
	def rSt = child?.currentValue(occupancy)
	if (rSt == locked && lockedOverrides)	return;
	if (rSt == asleep && asleepOverrides)	return;
	if (presenceActionDeparture() && ['asleep', 'engaged', 'occupied'].contains(rSt))		roomVacant();
///		child."${(resetEngagedDirectly ? vacant : (motionsSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : checking))}"()
//		"${(resetEngagedDirectly ? vacant : (motionsSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : (state.dimTimer ? checking : vacant)))}"()
	state.thermoOverride = false
	processCoolHeat()
}

def	engagedSwitchOnEventHandler(evt)	{
	ifDebug("engagedSwitchOnEventHandler", 'info')
	def child = getChildDevice(getRoom())
	if (getHubType() != _Hubitat)		child.updateESwitchInd(isAnyESwitchOn());
	if (!checkPauseModesAndDoW())	return;
	def rSt = child?.currentValue(occupancy)
	if (rSt == locked && lockedOverrides)	return;
	if (rSt == asleep && asleepOverrides)	return;
	(rSt != engaged ? engaged() : refreshEngagedTimer(engaged))
}

def	engagedSwitchOffEventHandler(evt)	{
	ifDebug("engagedSwitchOffEventHandler", 'info')
	def child = getChildDevice(getRoom())
	if (getHubType() != _Hubitat)		child.updateESwitchInd(isAnyESwitchOn());
	if (!checkPauseModesAndDoW())	return;
	def rSt = child?.currentValue(occupancy)
	if (rSt == locked && lockedOverrides)	return;
	if (rSt == asleep && asleepOverrides)	return;
	if (isRoomEngaged())	return;
	if (rSt == engaged)		roomVacant();
///		child."${(resetEngagedDirectly ? vacant : (motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : checking))}"()
//		"${(resetEngagedDirectly ? vacant : (motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : checking))}"()
}

private isRoomEngaged(skipPresence = false, skipMusic = false, skipPower = false, skipSwitch = false, skipContact = false)	{
	if (!skipPresence && personsPresence && presenceActionContinuous && personsPresence.currentPresence.contains(present))	return true;
	if (!skipMusic && musicDevice && musicEngaged && musicDevice.currentStatus == 'playing')	return true;
	if (!skipPower && powerDevice && powerValueEngaged && getIntfromStr((String) powerDevice.currentPower) >= powerValueEngaged)	return true;
	if (!skipSwitch && engagedSwitch && engagedSwitch.currentSwitch.contains('on'))	return true;
	if (!skipContact && contactSensor)	{
		def cV = contactSensor.currentContact.contains(open)
		if ((!contactSensorOutsideDoor && !cV) || (contactSensorOutsideDoor && cV))	return true;
	}
	return false
}

private refreshOccupiedTimer(rSt, returnTimer = false)	{
//	if (!rSt)		rSt = getChildDevice(getRoom())?.currentValue(occupancy);
	def timer = null
	if (rSt == occupied && state.noMotion)	{
		(returnTimer ? timer = state.noMotion : updateChildTimer(state.noMotion))
//		if (!returnTimer)	updateChildTimer(state.noMotion)
//		else				timer = state.noMotion
		runIn(state.noMotion, roomVacant)
	}
	return timer
}

private refreshEngagedTimer(rSt, returnTimer = false)	{
//	if (!rSt)		rSt = getChildDevice(getRoom())?.currentValue(occupancy);
	def timer = null
	if (rSt == engaged && state.noMotionEngaged)	{
		(returnTimer ? timer = state.noMotionEngaged : updateChildTimer(state.noMotionEngaged))
//		if (!returnTimer)	updateChildTimer(state.noMotionEngaged)
//		else				timer = state.noMotionEngaged
		runIn(state.noMotionEngaged, roomVacant)
	}
	return timer
}

def	contactOpenEventHandler(evt)	{
	ifDebug("contactOpenEventHandler", 'info')
	def cV = (contactSensor ? contactSensor.currentContact : '')
	if (contactSensorOutsideDoor)	{
		if (announceDoor)	{
			if (announceDoorColor)	{
				state.colorNotificationColor = convertRGBToHueSaturation(colorsRGB[announceDoorColor][1])
				setupColorNotification()
			}
			if (announceDoorSpeaker)		speakIt(evt.device.displayName + ' closed. ');
		}
		if (announceContact && !cV.contains(open))		unschedule("contactStaysOpen");
	}
	else if (announceContact)	{
		def aC = announceContact as Integer
		runIn(aC * 60, contactStaysOpen)
	}
	def child = getChildDevice(getRoom())
	if (getHubType() != _Hubitat)		child.updateContactInd(contactSensorOutsideDoor ? (cV.contains(open) ? 0 : 1) : 0);
	if (!checkPauseModesAndDoW())	return;
	def rSt = child?.currentValue(occupancy)
	if (rSt == locked && lockedOverrides)	return;
	if (rSt == asleep && asleepOverrides)	return;
	if (![occupied, checking, vacant, engaged, asleep].contains(rSt))		return;
	if (rSt == engaged && isRoomEngaged())		{
		if (personsPresence && presenceActionContinuous && personsPresence.currentPresence.contains(present))
			unscheduleAll("contact closed handler")
		else if (resetEngagedWithContact)	{
			updateChildTimer((resetEngagedWithContact as Integer) * 60)
			runIn((resetEngagedWithContact as Integer) * 60, resetEngaged)
		}
		else
			refreshEngagedTimer(engaged)
		return
	}
//	if (resetEngagedWithContact && rSt == engaged && powerValueEngaged && getIntfromStr((String) powerDevice.currentPower) >= powerValueEngaged)	{
//		updateChildTimer((resetEngagedWithContact as Integer) * 60)
//		runIn((resetEngagedWithContact as Integer) * 60, resetEngaged)
//		return
//	}
	if (resetAsleepWithContact && rSt == asleep)	{
		updateChildTimer((resetAsleepWithContact as Integer) * 60)
		runIn((resetAsleepWithContact as Integer) * 60, resetAsleep)
		return
	}
//	if (rSt == engaged && isRoomEngaged())	{
//		if (personsPresence && presenceActionContinuous && personsPresence.currentPresence.contains(present))
//			unscheduleAll("contact open handler")
//		else
//			refreshEngagedTimer(engaged)
//		return
//	}
	if (contactSensorOutsideDoor)	{
		if (rSt == vacant && hasOccupiedDevice())
///			child."${(motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : checking)}"()
//			"${(motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : checking)}"()
			roomVacant()
		else if (rSt == occupied)	{
			if (!motionSensors || whichNoMotion != lastMotionInactive || !motionSensors.currentMotion.contains(active))
//				child.checking()
				checking()
		}
		else if (rSt == engaged && !contactSensorNotTriggersEngaged && !cV.contains(open))
///			child."${(resetEngagedDirectly ? vacant : (motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : checking))}"()
//			"${(resetEngagedDirectly ? vacant : (motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : checking))}"()
			roomVacant(true)
	}
	else		{
		if (rSt == vacant && hasOccupiedDevice())
///			child."${(motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : checking)}"()
//			"${(motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : checking)}"()
			roomVacant()
		else if (rSt == occupied)	{
			if (!motionSensors || whichNoMotion != lastMotionInactive || !motionSensors.currentMotion.contains(active))
///				child.checking()
				checking()
		}
		else if (rSt == engaged && !contactSensorNotTriggersEngaged)	{
///			child."${(resetEngagedDirectly ? vacant : (motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : checking))}"()
//			"${(resetEngagedDirectly ? vacant : (motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : checking))}"()
			roomVacant(true)
		}
	}
}

def	contactClosedEventHandler(evt)	{
	ifDebug("contactClosedEventHandler", 'info')
	def cV = contactSensor.currentContact
	if (contactSensorOutsideDoor)	{
		if (announceDoor)	{
			if (announceDoorColor)	{
				state.colorNotificationColor = convertRGBToHueSaturation(colorsRGB[announceDoorColor][1])
				setupColorNotification()
			}
			if (announceDoorSpeaker)		speakIt(evt.device.displayName + ' opened. ');
		}
		if (announceContact)	{
			def aC = announceContact as Integer
			runIn(aC * 60, contactStaysOpen)
		}
	}
	else if (announceContact && !cV.contains(open))
		unschedule("contactStaysOpen")
	def child = getChildDevice(getRoom())
	if (getHubType() != _Hubitat)		child.updateContactInd(contactSensorOutsideDoor ? 0 : (cV.contains(open) ? 0 : 1));
	if (!checkPauseModesAndDoW())	return;
	def rSt = child?.currentValue(occupancy)
	if (rSt == locked && lockedOverrides)	return;
	if (rSt == asleep && asleepOverrides)	return;
	if (![occupied, checking, vacant, engaged, asleep].contains(rSt))	return;
	if (rSt == engaged && isRoomEngaged())	{
		if (personsPresence && presenceActionContinuous && personsPresence.currentPresence.contains(present))
			unscheduleAll("contact closed handler")
		else if (resetEngagedWithContact)	{
			unschedule('resetEngaged')
			restoreTimer(engaged)
		}
		else
			refreshEngagedTimer(engaged)
		return
	}
	if (resetAsleepWithContact && !cV.contains(open) && rSt == asleep)	{
		unschedule('resetAsleep')
		restoreTimer(asleep)
	}

//	if (rSt == engaged && isRoomEngaged())	{
//		if (personsPresence && presenceActionContinuous && personsPresence.currentPresence.contains(present))
//			unscheduleAll("contact closed handler")
//		else
//			refreshEngagedTimer(engaged)
//		return
//	}
	if (contactSensorOutsideDoor)	{
		if (['checking', 'vacant'].contains(rSt))	{
			if (hasOccupiedDevice())
///				child."${(motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : checking)}"()
//				"${(motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : checking)}"()
				roomVacant()
			else if (cV.contains(open))
///				child.engaged()
				engaged()
		}
		else if (rSt == occupied && !contactSensorNotTriggersEngaged)
///			child.engaged()
			engaged()
		else if (rSt == engaged && !contactSensorNotTriggersEngaged)
			refreshEngagedTimer(engaged)
	}
	else		{
		if (['checking', 'vacant'].contains(rSt))	{
			if (hasOccupiedDevice())
///				child."${(motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : checking)}"()
				// cant turn off need to stay in checking if not motion
				"${(motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : checking)}"()
//				roomVacant()
			else if (!cV.contains(open))
///				child.engaged()
				engaged()
		}
		else if (rSt == occupied && !contactSensorNotTriggersEngaged && !cV.contains(open))
///			child.engaged()
			engaged()
		else if (rSt == engaged && !contactSensorNotTriggersEngaged)
			refreshEngagedTimer(engaged)
	}
}

def contactStaysOpen()	{
	ifDebug("contactStaysOpen", 'info')
	if (!contactSensor.currentContact.contains(open))	return;
	def cO = '', cOCount = 0
//	contactSensor.each	{
	for (def sen : contactSensor)	{
		if (sen.currentContact == open)		{
			cO = (cO.size() > 0 ? ', ' : '') + sen.displayName
			cOCount = cOCount + 1
		}
	}
	if (cOCount > 0 && announceContact)	{
		cO = addAnd(cO)
		if (announceContactColor)	{
			state.colorNotificationColor = convertRGBToHueSaturation(colorsRGB[announceContactColor][1])
			setupColorNotification()
		}
		if (announceContactSpeaker)		speakIt('Contacts ' + cO + " ${(cOCount == 1 ? 'is' : 'are')} open. ");
	}
	runIn(announceContact.toInteger() * 60, contactStaysOpen)
}

private addAnd(str)	{
	if (!str)	return '';
	def lio = str.lastIndexOf(',')
	return (lio == -1 ? str : (str.substring(0, lio) + " and " + str.substring(lio + 1)))
}

private restoreTimer(rS)	{
	def rSt = getChildDevice(getRoom())?.currentValue(occupancy)
	def childTimer = 0
	if (rSt == rS)		childTimer = calculateTimerLeft(rS);
	updateChildTimer(childTimer)
}

private calculateTimerLeft(rs)	{
	if (state.previousState.state != rs)	return 0;
	def eM = ((now() - state.previousState.date) / 1000l).toInteger()
//    ifDebug("rS: $rS | eM: $eM | $state.previousState.date")
	switch(rs)	{
		case occupied:	eM = (state.noMotion ? state.noMotion - eM : 0);				break
		case engaged:	eM = (state.noMotionEngaged ? state.noMotionEngaged - eM : 0);	break
		case asleep:	eM = (state.noAsleep ? state.noAsleep - eM : 0);				break
		case locked:	eM = (state.unLocked ? state.unLocked - eM : 0);				break
		default:		break
	}
	return eM
}

def resetEngaged()	{
	def child = getChildDevice(getRoom())
	def rSt = child?.currentValue(occupancy)
	if (rSt == engaged)
///		child."${(resetEngagedDirectly ? vacant : (motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : checking))}"()
//		"${(resetEngagedDirectly ? vacant : (motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : (state.dimTimer ? checking : vacant)))}"()
		roomVacant()
}

def resetAsleep()	{
	def child = getChildDevice(getRoom())
	def rSt = child?.currentValue(occupancy)
	if (rSt == asleep)	{
		unschedule('roomAwake')
///		child."${(resetAsleepDirectly ? vacant : (motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : checking))}"()
//		"${(resetAsleepDirectly ? vacant : (motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : checking))}"()
		roomAwake()
	}
}

def	contactsRTOpenEventHandler(evt)	{
	ifDebug("contactsRTOpenEventHandler", 'info')
	def child = getChildDevice(getRoom())
	if (getHubType() != _Hubitat)		child.updateContactRTIndC(contactSensorsRT.currentContact.contains(open) ? 0 : 1);
	if (announceContactRT)	{
		if (announceContactRTColor)	{
			state.colorNotificationColor = convertRGBToHueSaturation(colorsRGB[announceContactRTColor][1])
			setupColorNotification()
		}
		if (announceContactRTSpeaker)	speakIt(evt.device.displayName + ' opened.');
	}
	if (!checkPauseModesAndDoW())	return;
	processCoolHeat()
}

def	contactsRTClosedEventHandler(evt)	{
	ifDebug("contactsRTClosedEventHandler", 'info')
	def child = getChildDevice(getRoom())
	if (getHubType() != _Hubitat)		child.updateContactRTIndC(contactSensorsRT.currentContact.contains(open) ? 0 : 1);
	if (announceContactRT)	{
		if (announceContactRTColor)	{
			state.colorNotificationColor = convertRGBToHueSaturation(colorsRGB[announceContactRTColor][1])
			setupColorNotification()
		}
		if (announceContactRTSpeaker)	speakIt(evt.device.displayName + ' closed.');
	}
	if (!checkPauseModesAndDoW())	return;
	processCoolHeat()
}

def musicPlayingEventHandler(evt)	{
	ifDebug("musicPlayingEventHandler", 'info')
//    ifDebug("evt.name: $evt.name | evt.value: $evt.value")
	def child = getChildDevice(getRoom())
	if (!checkPauseModesAndDoW())	return;
//    if (isRoomEngaged(,true,,,))	return;
	def rSt = child?.currentValue(occupancy)
//    if (['occupied', 'checking'].contains(rSt) || (!motionSensors && rSt == 'vacant'))
	if (rSt == locked && lockedOverrides)	return;
	if (rSt == asleep && asleepOverrides)	return;
	if (rSt == occupied || (!hasOccupiedDevice() && rSt == vacant))
///		child.engaged()
		engaged()
	else if (hasOccupiedDevice() && rSt == vacant)
///		child."${(motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : checking)}"()
		"${(motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : (state.dimTimer ? checking : vacant))}"()
//        child.checking()
	else if (rSt == engaged)
		refreshEngagedTimer(engaged)
}

def musicStoppedEventHandler(evt)	{
	ifDebug("musicStoppedEventHandler", 'info')
//    ifDebug("evt.name: $evt.name | evt.value: $evt.value")
	def child = getChildDevice(getRoom())
	if (!checkPauseModesAndDoW())	return;
	if (isRoomEngaged(false,false,false,false,false))	return;
	def rSt = child?.currentValue(occupancy)
	if (rSt == locked && lockedOverrides)	return;
	if (rSt == asleep && asleepOverrides)	return;
	if (resetEngagedDirectly && rSt == engaged)
		child.vacant()
	else if (['engaged', 'occupied'].contains(rSt))
///		child."${(resetEngagedDirectly ? vacant : (motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : checking))})"()
//		"${(resetEngagedDirectly ? vacant : (motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : checking))})"()
		roomVacant()
}

def temperatureEventHandler(evt)	{
	def child = getChildDevice(getRoom())
	def temperature = getAvgTemperature()
	boolean isFarenheit = (location.temperatureScale == 'F' ? true : false)
//    ifDebug("temperatureEventHandler: $temperature")
	if (getHubType() != _Hubitat)		child.updateTemperatureInd(temperature);
//    if (!personsPresence)	return;
	if (!checkPauseModesAndDoW())	return;
	processCoolHeat(null, true)
}

def	asleepSwitchOnEventHandler(evt)	{
	ifDebug("asleepSwitchOnEventHandler", 'info')
	def child = getChildDevice(getRoom())
	if (getHubType() != _Hubitat)		child.updateASwitchInd(isAnyASwitchOn());
	if (!checkPauseModesAndDoW())	return;
	def rSt = child?.currentValue(occupancy)
	if (rSt == locked && lockedOverrides)	return;
	asleep()
}

def	asleepSwitchOffEventHandler(evt)	{
	ifDebug("asleepSwitchOffEventHandler", 'info')
	def child = getChildDevice(getRoom())
	if (getHubType() != _Hubitat)		child.updateASwitchInd(isAnyASwitchOn());
	if (!checkPauseModesAndDoW())	return;
	def rSt = child?.currentValue(occupancy)
	if (rSt == locked && lockedOverrides)	return;
	if (asleepSwitch.currentSwitch.contains('on'))	return;
	if (rSt == asleep)
///		child."${(motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : checking)}"()
		"${(isRoomEngaged() ? engaged : (motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : (state.dimTimer ? checking : vacant)))}"()
}

def	lockedSwitchOnEventHandler(evt)	{
	ifDebug("lockedSwitchOnEventHandler", 'info')
	def child = getChildDevice(getRoom())
	if (getHubType() != _Hubitat)		child.updateLSwitchInd(isAnyLSwitchOn());
	if (!checkPauseModesAndDoW())	return;
	if (!lockedSwitchCmd && lockedSwitch.currentSwitch.contains('on'))	return;
	if (child?.currentValue(occupancy) != locked)
		locked()
}

def	lockedSwitchOffEventHandler(evt)	{
	ifDebug("lockedSwitchOffEventHandler", 'info')
	def child = getChildDevice(getRoom())
	if (getHubType() != _Hubitat)	child.updateLSwitchInd(isAnyLSwitchOn());
	if (!checkPauseModesAndDoW())	return;
	if (lockedSwitchCmd && lockedSwitch.currentSwitch.contains('on'))	return;
	if (child?.currentValue(occupancy) == locked)
///		child."${(motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : checking)}"()
		"${(isRoomEngaged() ? engaged : (motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : (state.dimTimer ? checking : vacant)))}"()
}

def	lockedContactOpenEventHandler(evt)	{
	ifDebug("lockedContactOpenEventHandler", 'info')
	def child = getChildDevice(getRoom())
	if (!checkPauseModesAndDoW())	return;
	if (child?.currentValue(occupancy) == locked)
///		child."${(motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : checking)}"()
		"${(isRoomEngaged() ? engaged : (motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : (state.dimTimer ? checking : vacant)))}"()
}

def	lockedContactClosedEventHandler(evt)	{
	ifDebug("lockedContactClosedEventHandler", 'info')
	def child = getChildDevice(getRoom())
	if (!checkPauseModesAndDoW())	return;
	if (child?.currentValue(occupancy) != locked)
		locked()
}

def checkSwitchEventHandler(evt)	{
	def rSt = getChildDevice(getRoom())?.currentValue(occupancy)
	if (!onlyOnStateChange || (butNotInStates && butNotInStates.contains(rSt)))		switchesOnOrOff();
}

def roomThermostatEventHandler(evt)	{
	if (!state.roomThermoTurnedOn)	{
		state.thermoOverride = true
		runIn(thermoOverride * 60, thermoUnOverride)
	}
	else
		state.roomThermoTurnedOn = false
}

def roomCoolSwitchOnEventHandler(evt)	{
	if (!state.roomCoolTurnedOn)	{
		state.thermoOverride = true
		runIn(thermoOverride * 60, thermoUnOverride)
	}
	else
		state.roomCoolTurnedOn = false
}

def roomCoolSwitchOffEventHandler(evt)	{
	unschedule('thermoUnOverride')
	state.thermoOverride = false
}

def roomHeatSwitchOnEventHandler(evt)	{
	if (!state.roomHeatTurnedOn)	{
		state.thermoOverride = true
		runIn(thermoOverride * 60, thermoUnOverride)
	}
	else
		state.roomHeatTurnedOn = false
}

def roomHeatSwitchOffEventHandler(evt)	{
	unschedule('thermoUnOverride')
	state.thermoOverride = false
}

def thermoUnOverride()	{
	state.thermoOverride = false
	processCoolHeat()
}

def processCoolHeat(rSt = null, canSkip = false)	{
	ifDebug("processCoolHeat", 'info')
	if (!state.maintainRoomTemp || state.thermoOverride)	return;
	def nowTime = now()
	if (canSkip && ((nowTime - (state.lastProcessedCH ?: 0L)) < _ProcessCHEvery))	return;
	state.lastProcessedCH = nowTime
	def hT = getHubType()
	def child = getChildDevice(getRoom())
	if (!rSt)	rSt = child?.currentValue(occupancy);
//    ifDebug("rSt: $rSt")
	def temperature = getAvgTemperature()
//	def updateMaintainInd = true
	def turnOn = null
	def thisRule = [:]
	if (['engaged', 'occupied', 'asleep', 'vacant'].contains(rSt) && (!checkPresence || (personsPresence ? personsPresence.currentPresence.contains(present) : false)))	{
		turnOn = checkForRules(_TRule, rSt)
		if (turnOn)		thisRule = getRule(turnOn, _TRule);
	}

	ifDebug("processCoolHeat: rule: $turnOn")

	if (turnOn && maintainRoomTemp != '4' && (!contactSensorsRT || !contactSensorsRT.currentContact.contains(open)))	{
		if (['1', '3', '5'].contains(maintainRoomTemp) && ((useThermostat && roomThermostat) || (!useThermostat && roomCoolSwitch)))
			coolIt(thisRule, temperature)
		if (['2', '3', '5'].contains(maintainRoomTemp) && ((useThermostat && roomThermostat) || (!useThermostat && roomHeatSwitch)))
			heatIt(thisRule, temperature)
	}
	else    {
		state.roomThermoTurnedOn = false
		state.roomCoolTurnedOn = false
		state.roomHeatTurnedOn = false
		if (useThermostat)	{
			if (['1', '2', '3'].contains(maintainRoomTemp))		{
				if (roomThermostat)	{  roomThermostat.auto(); pauseIt  }
				if (roomVents)		delayedVentOff();
			}
		}
		else		{
			if (roomCoolSwitch)	{  roomCoolSwitch.off(); pauseIt  }
			if (roomHeatSwitch)	{  roomHeatSwitch.off(); pauseIt  }
		}
	}
	if (roomFanSwitch)	{
		if  (turnOn && thisRule.fanOnTemp)	{
			def fanLowTemp      = (thisRule.fanOnTemp + 0f).round(1)
			def fanMediumTemp   = (thisRule.fanOnTemp + (thisRule.fanSpeedIncTemp * 1f)).round(1)
			def fanHighTemp     = (thisRule.fanOnTemp + (thisRule.fanSpeedIncTemp * 2f)).round(1)
//            ifDebug("temperature: $temperature | fanOnTemp: $thisRule.fanOnTemp | fanLowTemp: $fanLowTemp | fanMediumTemp: $fanMediumTemp | fanHighTemp: $fanHighTemp")
			def fL = roomFanSwitch.hasCommand("setLevel")
			if (temperature >= fanLowTemp)	{
				roomFanSwitch.on()
				if (fL)	{
					pauseIt()
					roomFanSwitch.setLevel((temperature >= fanHighTemp ? fanHigh : (temperature >= fanMediumTemp ? fanMedium : fanLow)))
				}
			}
			else
				roomFanSwitch.off()
		}
		else
			roomFanSwitch.off()
		pauseIt()
	}
	updateCoolHeatInd(temperature, turnOn, thisRule);
//log.debug "perf processCoolHeat: ${now() - nowTime} ms"
} // processCoolHeat

private coolIt(thisRule, temperature)	{
	def coolHigh = thisRule.coolTemp + (thisRule.tempRange / 2f).round(1)
	def coolLow = thisRule.coolTemp - (thisRule.tempRange / 2f).round(1)
	if (outTempSensor && autoAdjustWithOutdoor)	{
		def outTemp = outTempSensor.currentTemperature
		boolean isFarenheit = (location.temperatureScale == 'F' ? true : false)
		if (outTemp > (isFarenheit ? 90 : 26.7))	{
			coolHigh = coolHigh - (isFarenheit ? 0.5 : 0.28)
			coolLow = coolLow - (isFarenheit ? 0.5 : 0.28)
		}
	}
	if (temperature >= coolHigh)	{
		if (useThermostat && maintainRoomTemp != '5')	{
			state.roomThermoTurnedOn = true
			roomThermostat.setCoolingSetpoint(thisRule.coolTemp - thermoToTempSensor); pauseIt()
			roomThermostat.fanAuto(); pauseIt()
			roomThermostat.cool(); pauseIt()
		}
		else if (roomCoolSwitch?.currentSwitch == off)	{
			state.roomCoolTurnedOn = true
			roomCoolSwitch.on(); pauseIt()
		}
	}
	else if (temperature <= coolLow && ((useThermostat && maintainRoomTemp != '5') || roomCoolSwitch))	{
		state.roomThermoTurnedOn = false
		state.roomCoolTurnedOn = false
		(useThermostat ? roomThermostat.auto() : roomCoolSwitch.off()); pauseIt()
	}
	if (useThermostat && roomVents)	{
		if (roomThermostat.currentThermostatOperatingState == 'cooling')	{
			def ventLevel = (((temperature - coolLow) * 100) / (coolHigh - coolLow)).round(0)
			ventLevel = (ventLevel > 100 ? 100 : (ventLevel > 0 ?: 0))
			ventsOn(ventLevel)
		}
		else
			delayedVentOff()
	}
}

private heatIt(thisRule, temperature)	{
	def heatHigh = thisRule.heatTemp + (thisRule.tempRange / 2f).round(1)
	def heatLow = thisRule.heatTemp - (thisRule.tempRange / 2f).round(1)
	if (outTempSensor && autoAdjustWithOutdoor)	{
		def outTemp = outTempSensor.currentTemperature
		boolean isFarenheit = (location.temperatureScale == 'F' ? true : false)
		if (outTemp < (isFarenheit ? 32 : 0))	{
			heatHigh = heatHigh + (isFarenheit ? 0.5 : 0.28)
			heatLow = heatLow + (isFarenheit ? 0.5 : 0.28)
		}
	}
	if (temperature >= heatHigh && ((useThermostat && maintainRoomTemp != '5') || roomHeatSwitch))	{
		state.roomThermoTurnedOn = false
		state.roomHeatTurnedOn = false
		(useThermostat ? roomThermostat.auto() : roomHeatSwitch.off()); pauseIt()
	}
	else if (temperature <= heatLow)	{
		if (useThermostat && maintainRoomTemp != '5')	{
			state.roomThermoTurnedOn = true
			roomThermostat.setHeatingSetpoint(thisRule.heatTemp - thermoToTempSensor); pauseIt()
			roomThermostat.fanAuto(); pauseIt()
			roomThermostat.heat(); pauseIt()
		}
		else if (roomHeatSwitch?.currentSwitch == off)	{
			state.roomHeatTurnedOn = true
			roomHeatSwitch.on(); pauseIt()
		}
	}
	if (useThermostat && roomVents)	{
		if (roomThermostat.currentThermostatOperatingState == 'heating')	{
			def ventLevel = (((temperature - heatLow) * 100 ) / (heatHigh - heatLow)).round(0)
			ventLevel = (ventLevel > 100 ? 100 : (ventLevel > 0 ?: 0))
			ventsOn(ventLevel)
		}
		else
			delayedVentOff()
	}
}

private updateCoolHeatInd(temperature, turnOn, thisRule)		{
	if (getHubType() == _Hubitat)	return;
	runIn(1, updateThermostatIndP)
//	if (updateMaintainInd)	{
	def child = getChildDevice(getRoom())
	if (turnOn)	{
		boolean isFarenheit = (location.temperatureScale == 'F' ? true : false)
		def coolTemp = thisRule.coolTemp
		def heatTemp = thisRule.heatTemp
		if (outTempSensor && autoAdjustWithOutdoor)	{
			def outTemp = outTempSensor.currentTemperature
			if (outTemp > (isFarenheit ? 90 : 26.7))	coolTemp = coolTemp - (isFarenheit ? 0.5 : 0.28);
			if (outTemp < (isFarenheit ? 32 : 0))		heatTemp = heatTemp + (isFarenheit ? 0.5 : 0.28);
		}
		if (maintainRoomTemp == '1')
			child.updateMaintainIndC(coolTemp)
		else if (maintainRoomTemp == '2')
			child.updateMaintainIndC(heatTemp)
		else if (maintainRoomTemp == '3' || maintainRoomTemp == '5')	{
			def x = Math.abs(temperature - coolTemp)
			def y = Math.abs(temperature - heatTemp)
			if (x >= y)
				child.updateMaintainIndC(heatTemp)
			else
				child.updateMaintainIndC(coolTemp)
		}
	}
	else
		child.updateMaintainIndC(-1)
//	}
}

def updateThermostatIndP()	{
	if (getHubType() == _Hubitat)	return;
	def thermo = 9
	def isHere = (personsPresence ? personsPresence.currentPresence.contains(present) : false)
	if ((useThermostat && roomThermostat?.currentThermostatOperatingState == 'cooling') ||
		(!useThermostat && roomCoolSwitch?.currentSwitch == on))
		thermo = 4
	else if ((useThermostat && roomThermostat?.currentThermostatOperatingState == 'heating') ||
			 (!useThermostat && roomHeatSwitch?.currentSwitch == on))
		thermo = 5
	else if (!isHere && ['1', '2', '3'].contains(maintainRoomTemp))
		thermo = 0
	else if (maintainRoomTemp == '3')
		thermo = 1
	else if (maintainRoomTemp == '1')
		thermo = 2
	else if (maintainRoomTemp == '2')
		thermo = 3
	ifDebug("updateThermostatIndP: thermo: $thermo")
	def child = getChildDevice(getRoom())
	if (child)		child.updateThermostatIndC(thermo)
}

def updateFanIndP(evt)	{
	if (getHubType() == _Hubitat)	return;
	def child = getChildDevice(getRoom())
	if (child)	{
		def cL = roomFanSwitch?.currentLevel
		child.updateFanIndC((!roomFanSwitch ? -1 : (roomFanSwitch.currentSwitch == 'off' ? 0 : (cL <= fanLow ? 1 : (cL <= fanMedium ? 2 : 3)))))
	}
}

def updateVentIndP(evt)	{
	if (getHubType() == _Hubitat)	return;
	def child = getChildDevice(getRoom())
	if (child)		child.updateVentIndC(!roomVents ? -1 : (roomVents.currentSwitch.contains(on) ? 1 : 0));
}

private delayedVentOff()	{
	if (roomVents)
		if (delayedVentOffBy)	runIn(delayedVentOffBy, ventsOff)
		else					ventsOff()
}

def ventsOff()		{  roomVents.off(); pauseIt()  }

private ventsOn(lvl)	{  unschedule('ventsOff'); roomVents.setLevel(lvl); pauseIt()  }

private checkForRules(ruleType, rSt = null)		{
	def turnOn = null
	def turnOnRules = [:]
	def thisRule = [:]
	def child = getChildDevice(getRoom())
	if (!rSt)	rSt = child?.currentValue(occupancy);
//    ifDebug("$state.rules")
	if (state.rules)	{
		def nowTime	= now() + 1000
		def nowDate = new Date(nowTime)
//		def sunriseTime, sunsetTime
		def sunRiseAndSet = sunRiseAndSet()
		if (!sunRiseAndSet)		return;
//		sunriseTime = sunRiseAndSet.rise
//		sunsetTime = sunRiseAndSet.set
		def timedRulesOnly = false
//		def sunriseTimeWithOff, sunsetTimeWithOff
		for (def i = 1; i <= maxRules; i++)	{
			def ruleHasTime = false
			def ruleNo = String.valueOf(i)
			if (turnOnRules[(ruleNo)])		thisRule = turnOnRules[(ruleNo)];
			else							thisRule = getNextRule(ruleNo, ruleType, true, true);
			if (thisRule.ruleNo == 'EOR')	break;
			if (!turnOnRules[(thisRule.ruleNo)])		turnOnRules << [(thisRule.ruleNo):thisRule];
			i = thisRule.ruleNo as Integer
			if (thisRule.mode && !thisRule.mode.contains(location.currentMode.toString()))		continue;
			if (thisRule.state && !thisRule.state.contains(rSt))		continue;
			if (thisRule.dayOfWeek && !(checkRunDay(thisRule.dayOfWeek)))	continue;
			if (state.timeCheck && (thisRule.fromTimeType && (thisRule.fromTimeType != _timeTime || thisRule.fromTime)) &&
				(thisRule.toTimeType && (thisRule.toTimeType != _timeTime || thisRule.toTime)))		{
//				def fTime, tTime
//				if (thisRule.fromTimeType == _timeSunrise)
//					fTime = (thisRule.fromTimeOffset ? new Date(sunriseTime.getTime() + (thisRule.fromTimeOffset * 60000L)) : sunriseTime)
//				else if (thisRule.fromTimeType == _timeSunset)
//					fTime = (thisRule.fromTimeOffset ? new Date(sunsetTime.getTime() + (thisRule.fromTimeOffset * 60000L)) : sunsetTime)
//				else
//					fTime = timeToday(thisRule.fromTime, location.timeZone)
//				if (thisRule.toTimeType == _timeSunrise)
//					tTime = (thisRule.toTimeOffset ? new Date(sunriseTime.getTime() + (thisRule.toTimeOffset * 60000L)) : sunriseTime)
//				else if (thisRule.toTimeType == _timeSunset)
//					tTime = (thisRule.toTimeOffset ? new Date(sunsetTime.getTime() + (thisRule.toTimeOffset * 60000L)) : sunsetTime)
//				else
//					tTime = timeToday(thisRule.toTime, location.timeZone)
//				while (fTime > tTime)		tTime = tTime.plus(1);
				def x = compareRuleTime(sunRiseAndSet, thisRule)
				if (!x.inBetween)		continue;
				if (!timedRulesOnly)	{
					turnOn = null
					timedRulesOnly = true
					i = 0
					continue
				}
				ruleHasTime = true
			}
//            ifDebug("ruleNo: $thisRule.ruleNo | thisRule.luxThreshold: $thisRule.luxThreshold | turnOn: $turnOn | previousRuleLux: $previousRuleLux")
//            ifDebug("timedRulesOnly: $timedRulesOnly | ruleHasTime: $ruleHasTime")
			if (timedRulesOnly && !ruleHasTime)		continue;
			turnOn = thisRule.ruleNo
		}
	}
	return turnOn
}

def humidityEventHandler(evt)	{
	ifDebug('humidityEventHandler', 'info')
	def nowTime = now()
	def child = getChildDevice(getRoom())
	def avgHumidity = getAvgHumidity()
	if (getHubType() != _Hubitat)		child.updateHumidityInd(avgHumidity);
	if (!state.avgHumidity)		state.avgHumidity = [:]
	def thisDay = (new Date(now())).getDay()
	def intCurrentHH = (new Date(now())).format("HH", location.timeZone) as Integer
	def mapKey = String.format("%03d", ((thisDay.toInteger() * 100) + intCurrentHH))
	state.avgHumidity[mapKey] = (state.avgHumidity[mapKey] ? ((state.avgHumidity[mapKey] + avgHumidity) / 2f).round(1) : avgHumidity)
	if (state.avgHumidity.size() > 2)	{
//        def rAvgHumidity = (state.avgHumidity.findAll { key, value -> !(key.contains('rollingAvgHumidity')) }.values().sum()) / (state.avgHumidity.size() - (state.avgHumidity.containsKey('rollingAvgHumidity') ? 1 : 0))
		def rAvgHumidity
		def y = state.avgHumidity?.findAll { !it.key.startsWith('rAH') }?.collect { it.value }
		if (y)	{
			rAvgHumidity = ((y.sum() / y.size()) * 1f).round(1)
			state.avgHumidity['rAH'] = rAvgHumidity
		}
		if (state.humidity.hour != intCurrentHH)	{
			for (def i = 0; i < 7; i++)	{
				def j = i * 100
				def k = state.avgHumidity.findAll { it.key.isInteger() && it.key.toInteger() >= j && it.key.toInteger() <= (j + 23) }
				if (k && k.size() > 2)	{
					rAvgHumidity = ((k.collect { it.value }.sum() / k.size()) * 1f).round(1)
					mapKey = String.format("%02dd", i)
					state.avgHumidity["rAH$mapKey"] = rAvgHumidity
				}
			}
			for (def i = 0; i < 24; i++)	{
				def k = []
				for (def j = 0; j < 7; j++)	{
					mapKey = String.format("%03d", ((j * 100) + i))
					if (state.avgHumidity[mapKey])		k << state.avgHumidity[mapKey];
				}
				if (k && k.size() > 2)	{
					rAvgHumidity = ((k.sum() / k.size()) * 1f).round(1)
					mapKey = String.format("%02dh", i)
					state.avgHumidity["rAH$mapKey"] = rAvgHumidity
				}
			}
			state.humidity.hour = intCurrentHH
		}
	}
	if (!checkPauseModesAndDoW())	return;
	def rSt = child?.currentValue(occupancy)
//    if (rSt == locked && lockedOverrides)	return;
	if (!onlyOnStateChange || (butNotInStates && butNotInStates.contains(rSt)))		switchesOnOrOff();
	processHumidity()
//log.debug "perf humidityEventHandler: ${now() - nowTime} ms"
}

def processHumidity(rSt = null)	{
	ifDebug("processHumidity", 'info')

	def nowTime = now()
	def child = getChildDevice(getRoom())
	if (!rSt)		rSt = child?.currentValue(occupancy);
	if (rSt == checking)	return;

	if (!state.maintainRoomHumi || state.humidity.override || state.humidity.offIn)		return;
	def humidity = getAvgHumidity()
	if (state.humidity.offAt)	{
		def humiOffAt
		if (!(state.humidity.offAt instanceof String))
			humiOffAt = state.humidity.offAt
		else		{
			if (state.humidity.offAt == 'HAV')
				mapKey = String.format("%02dh", intCurrentHH)
			else if (state.humidity.offAt == 'DAV')
				mapKey = String.format("%02dd", thisDay)
			else if (state.humidity.offAt == 'WAV')
				mapKey = ''
			humiOffAt = (state.avgHumidity["rAH$mapKey"] ?: (state.avgHumidity["rAH"] ?: humidity))
		}
		if ((roomDehumidifierSwitch && roomDehumidifierSwitch.currentSwitch == on && humidity <= humiOffAt) ||
			(roomHumidifierSwitch && roomHumidifierSwitch.currentSwitch == on && humidity >= humiOffAt))
			state.humidity.offAt = false
		if (state.humidity.offAt ? true : false)	return;
	}

	def thisRule = [:]
	def turnOn = null
	def postProcessing = false

	if (['engaged', 'occupied', 'asleep', 'vacant'].contains(rSt))		turnOn = checkForRules(_HRule, rSt);
	if (turnOn)		thisRule = getRule(turnOn, _HRule);
	if (turnOn && thisRule.state)	{
		if (thisRule.state.contains(engaged) || thisRule.state.contains(asleep) || (thisRule.state.contains(occupied) && state.humidity.lastState == vacant))
			state.humidity.lastRule = null
	}
// 	ifDebug("$state.humidity.lastRule | $state.humidity.lastState | $rSt | $state.humidity.minsInState")

	if (state.humidity.lastRule && state.humidity.lastState != rSt)		{
		def getOldRule = true
		if (state.humidity.minsInState > 0)		{
			def tDiff = (((now() - (state.humidity.lastStateAt ?: 0)) % 86400000f) / 60000f).trunc(0)
//			ifDebug("state.humidity.minsInState: $state.humidity.minsInState | tDiff: $tDiff")
			if (tDiff < state.humidity.minsInState)		getOldRule = false;
		}
		if (getOldRule)		{
			turnOn = state.humidity.lastRule
			thisRule = getRule(turnOn, _HRule)
			if (thisRule.humiCmp || thisRule.humiMinRun || thisRule.humiMaxRun)			postProcessing = true;
		}
	}

	state.humidity.lastRule = turnOn
	state.humidity.lastState = rSt
	state.humidity.lastStateAt = now()
	state.humidity.minsInState = (thisRule.humiMins ?: 0)
	if (!turnOn)	{
		turnOffHumiSwitches()
//log.debug "perf processHumidity 1: ${now() - nowTime} ms"
		return
	}

//	ifDebug("processHumidity: rule: $turnOn | $thisRule.humiMinRun | $thisRule.humiMaxRun | $rSt")

	if (!postProcessing)
		if (thisRule.deHumiOn)		{
			turnOnRoomDehumidifier()
//            updateLastState(rSt)
//log.debug "perf processHumidity 2: ${now() - nowTime} ms"
			return
		}
		else if (thisRule.humiOn)	{
			turnOnRoomHumidifier()
//            updateLastState(rSt)
//log.debug "perf processHumidity 3: ${now() - nowTime} ms"
			return
		}

	def thisDay = (new Date(now())).getDay()
	def intCurrentHH = (new Date(now())).format("HH", location.timeZone) as Integer
	def cmpHumidity = getHumiCmp(thisRule)

	ifDebug("cmpHumidity: $cmpHumidity | $rSt | $state.humidity.lastState")

	def turnOff = true
	state.humidity.forceOffIn = false
	if (thisRule.humiMinRun)	state.humidity.offIn = thisRule.humiMinRun;
	if (thisRule.humiMaxRun)	state.humidity.forceOffIn = thisRule.humiMaxRun;
	if (cmpHumidity)	{
		def humiPercented = ((cmpHumidity * thisRule.humiPercent) / 100f).round(1)
		def humiIncrease = cmpHumidity + humiPercented
		def humiDecrease = cmpHumidity - humiPercented

		if (humidity >= humiIncrease && roomDehumidifierSwitch)		{
			turnOnRoomDehumidifier()
//
			state.humidity.offAt = cmpHumidity as float
			turnOff = false
		}
		else if (humidity <= humiDecrease && roomHumidifierSwitch)	{
			turnOnRoomHumidifier()
//            state.humidity.offIn = thisRule.humiMinRun
			state.humidity.offAt = cmpHumidity as float
			turnOff == false
		}
//        humidityTimer()
	}
	ifDebug("$state.humidity.offIn | $state.humidity.forceOffIn")
	if (turnOff)	turnOffHumiSwitches();
	if (state.humidity.offIn || state.humidity.forceOffIn)		humidityTimer();

//log.debug "perf processHumidity: ${now() - nowTime} ms"
}

private getHumiCmp(thisRule)	{
	ifDebug("getHumiCmp", 'info')
	def cmpHumidity = false
	switch(thisRule.humiCmp)	{
		case '0':
			cmpHumidity = thisRule.humiValue as float
			break
		case '1':
			cmpHumidity = (state.humidity.previous ?: (state.avgHumidity["rAH"] ?: false))
			state.humidity.previous = false
			break
		case '2':
			cmpHumidity = (state.avgHumidity["rAH${String.format("%02dh", intCurrentHH)}"] ?: (state.avgHumidity["rAH"] ?: (state.humidity.previous ?: false)))
			break;
		case '3':
			cmpHumidity = (state.avgHumidity["rAH${String.format("%02dd", thisDay)}"] ?: (state.avgHumidity["rAH"] ?: (state.humidity.previous ?: false)))
			break;
		case '4':
			cmpHumidity = (state.avgHumidity["rAH"] ?: (state.humidity.previous ?: false))
			break;
		default:
			break
	}
	return cmpHumidity
}

private turnOnRoomDehumidifier()	{
	ifDebug("turnOnRoomDehumidifier", 'info')
	if (roomHumidifierSwitch && roomHumidifierSwitch.currentSwitch == on)		roomHumidifierSwitch.off();
	if (roomDehumidifierSwitch && roomDehumidifierSwitch.currentSwitch == off)	{
		state.humidity.dehumidifierTurnedOn = true
		roomDehumidifierSwitch.on()
	}
}

private turnOnRoomHumidifier()		{
	ifDebug("turnOnRoomHumidifier", 'info')
	if (roomDehumidifierSwitch && roomDehumidifierSwitch.currentSwitch == on)	roomDehumidifierSwitch.off();
	if (roomHumidifierSwitch && roomHumidifierSwitch.currentSwitch == off)	{
		state.humidity.humidifierTurnedOn = true
		roomHumidifierSwitch.on()
	}
}

private turnOffHumiSwitches()		{
	ifDebug("turnOffHumiSwitches", 'info')
	if (roomDehumidifierSwitch && roomDehumidifierSwitch.currentSwitch == on)		roomDehumidifierSwitch.off();
	if (roomHumidifierSwitch && roomHumidifierSwitch.currentSwitch == on)			roomHumidifierSwitch.off();
	unschedule('humidityTimer2')
	unschedule('humidityTimer3')
	state.humidity.offIn = false
	state.humidity.forceOffIn = false
	state.humidity.offAt = false
	state.humidity.lastRule = null
}

private humidityTimer()		{
	ifDebug("humidityTimer", 'info')
	unschedule('humidityTimer2')
	unschedule('humidityTimer3')
	if (roomDehumidifierSwitch?.currentSwitch == on || roomHumidifierSwitch?.currentSwitch == on)	{
		if (state.humidity.offIn)
			runIn(state.humidity.offIn * 60, humidityTimer2)
		else	{
			if (state.humidity.forceOffIn)		humidityTimer2();
			else if (!state.humidity.offAt)		turnOffHumiSwitches();
		}
	}
	else
		turnOffHumiSwitches()
}

def humidityTimer2()	{
	ifDebug("humidityTimer2", 'info')
	unschedule('humidityTimer3')
	if (roomDehumidifierSwitch?.currentSwitch == on || roomHumidifierSwitch?.currentSwitch == on)	{
		def offIn = state.humidity.offIn
		state.humidity.offIn = false
		state.humidity.lastRule = null
		processHumidity()
		if (state.humidity.forceOffIn && (roomDehumidifierSwitch?.currentSwitch == on || roomHumidifierSwitch?.currentSwitch == on))
			runIn((state.humidity.forceOffIn -  (offIn ?: 0)) * 60, humidityTimer3)
	}
	else	{
		state.humidity.offIn = false
		state.humidity.offAt = false
	}
	state.humidity.forceOffIn = false
}

def humidityTimer3()	{
	ifDebug("humidityTimer3", 'info')
	turnOffHumiSwitches()
}

def roomDehumidifierSwitchOnEventHandler(evt)	{
	ifDebug("roomDehumidifierSwitchOnEventHandler $state.humidity.dehumidifierTurnedOn", 'info')
	if (!state.humidity.dehumidifierTurnedOn)
		resetHumiAuto()
	else	{
		state.humidity.dehumidifierTurnedOn = false
		state.humidity.override = false
	}
}

def roomDehumidifierSwitchOffEventHandler(evt)		{
	ifDebug("roomDehumidifierSwitchOffEventHandler", 'info')
	unschedule('humiUnOverride')
	state.humidity.override = false
}

def roomHumidifierSwitchOnEventHandler(evt)		{
	ifDebug("roomHumidifierSwitchOnEventHandler $state.roomHumidifierTurnedOn", 'info')
	if (!state.humidity.humidifierTurnedOn)
		resetHumiAuto()
	else	{
		state.humidity.humidifierTurnedOn = false
		state.humidity.override = false
	}
}

def roomHumidifierSwitchOffEventHandler(evt)	{
	ifDebug("roomHumidifierSwitchOffEventHandler", 'info')
	unschedule('humiUnOverride')
	state.humidity.override = false
}

private resetHumiAuto()		{
	state.humidity.override = true
	unschedule('humidityTimer2')
	unschedule('humidityTimer3')
	state.humidity.offIn = false
	state.humidity.forceOffIn = false
//		humidityTimer()
	state.humidity.offAt = false
	state.humidity.lastRule = null
	state.humidity.minsInState = 0
	state.humidity.lastStateAt = 0
	runIn(humiOverride * 60, humiUnOverride)
}

def humiUnOverride()	{
	ifDebug("humiUnOverride", 'info')
	state.humidity.override = false
	processHumidity()
}

def luxEventHandler(evt)	{
	ifDebug("luxEventHandler", 'info')
	def child = getChildDevice(getRoom())
	int currentLux = getIntfromStr((String) evt.value)
	if (getHubType() != _Hubitat)		child.updateLuxInd(currentLux);
	if (!checkPauseModesAndDoW())	return;
	def rSt = child?.currentValue(occupancy)
//    if (rSt == locked && lockedOverrides)	return;
	if (!onlyOnStateChange || (butNotInStates && butNotInStates.contains(rSt)))		switchesOnOrOff();
}

private getIntfromStr(String str)	{
	int intValue
	if (!str)							intValue = 0;
	else if (str.indexOf('.') >= 0)		intValue = str.substring(0, str.indexOf('.')) as Integer;
	else								intValue = str.toInteger();
	return intValue
}

def powerEventHandler(evt)		{
	def nowTime = now()
	def child = getChildDevice(getRoom())
	def currentPower = getIntfromStr((String) evt.value)
	if (getHubType() != _Hubitat)		child.updatePowerInd(currentPower);
	if (!checkPauseModesAndDoW())	return;
	if (state.previousPower == currentPower)	return;
	def rSt = child?.currentValue(occupancy)
	if ((rSt == locked && lockedOverrides && !powerValueLocked) || (rSt == asleep && asleepOverrides && !powerValueAsleep) ||
		(rSt == engaged && engagedOverrides && !powerValueEngaged))
		return
	def timeOK = (powerValueEngaged || powerValueAsleep || powerValueLocked ? true : false)
	if (timeOK && (powerFromTimeType && (powerFromTimeType != _timeTime || powerFromTime)) &&
				  (powerToTimeType && (powerToTimeType != _timeTime || powerToTime)))				{
//		def nowDate = new Date(nowTime + 1000)
//		def sunriseTime, sunsetTime
		def sunRiseAndSet = sunRiseAndSet()
		if (!sunRiseAndSet)		return;
//		sunriseTime = sunRiseAndSet.rise
//		sunsetTime = sunRiseAndSet.set
//		def sunriseTimeWithOff, sunsetTimeWithOff
//		def fTime, tTime
//		if (powerFromTimeType == _timeSunrise)
//			fTime = (powerFromTimeOffset ? new Date(sunriseTime.getTime() + (powerFromTimeOffset * 60000L)) : sunriseTime)
//		else if (powerFromTimeType == _timeSunset)
//			fTime = (powerFromTimeOffset ? new Date(sunsetTime.getTime() + (powerFromTimeOffset * 60000L)) : sunsetTime)
//		else
//			fTime = timeToday(powerFromTime, location.timeZone)
//		if (powerToTimeType == _timeSunrise)
//			tTime = (powerToTimeOffset ? new Date(sunriseTime.getTime() + (powerToTimeOffset * 60000L)) : sunriseTime)
//		else if (powerToTimeType == _timeSunset)
//			tTime = (powerToTimeOffset ? new Date(sunsetTime.getTime() + (powerToTimeOffset * 60000L)) : sunsetTime)
//		else
//			tTime = timeToday(powerToTime, location.timeZone)
//		while (fTime > tTime)		tTime = tTime.plus(1);
//		if (!(timeOfDayIsBetween(fTime, tTime, nowDate, location.timeZone)))		timeOK = false;
		def x = compareRuleTime(sunRiseAndSet, [ruleNo:'power', fromTimeType:powerFromTimeType, fromTime:powerFromTime, fromTimeOffset:powerFromTimeOffset, toTimeType:powerToTimeType, toTime:powerToTime, toTimeOffset:powerToTimeOffset])
		if (!x.inBetween)		timeOK = false;
	}
	if (timeOK)
		if (powerValueEngaged)		{
			if (currentPower >= powerValueEngaged && state.previousPower < powerValueEngaged &&
				['occupied', 'checking', 'vacant'].contains(rSt) && (powerTriggerFromVacant || rSt != vacant))	{
				unschedule('powerStaysBelowEngaged')
				engaged()
			}
			else if (currentPower < powerValueEngaged && state.previousPower >= powerValueEngaged && rSt == engaged)
				runIn(powerStays, powerStaysBelowEngaged)
		}
		else if (powerValueAsleep)		{
			if (currentPower >= powerValueAsleep && state.previousPower < powerValueAsleep &&
				['engaged', 'occupied', 'checking', 'vacant'].contains(rSt) && (powerTriggerFromVacant || rSt != vacant))	{
				unschedule('powerStaysBelowAsleep')
				asleep()
			}
			else if (currentPower < powerValueAsleep && state.previousPower >= powerValueAsleep && rSt == asleep)
				runIn(powerStays, powerStaysBelowAsleep)
		}
		else if (powerValueLocked)		{
			if (currentPower >= powerValueLocked && state.previousPower < powerValueLocked &&
				['engaged', 'occupied', 'checking', 'vacant'].contains(rSt) && (powerTriggerFromVacant || rSt != vacant))	{
				unschedule('powerStaysBelowLocked')
				locked()
			}
			else if (currentPower < powerValueLocked && state.previousPower >= powerValueLocked && rSt == locked)
				runIn(powerStays, powerStaysBelowLocked)
		}
	if (state.powerCheck && (!onlyOnStateChange || (butNotInStates && butNotInStates.contains(rSt))))		switchesOnOrOff();
	state.previousPower = currentPower
//log.debug "powerEventHandler perf: ${now() - nowTime} ms"
}

def powerStaysBelowEngaged()	{
	if (getChildDevice(getRoom())?.currentValue(occupancy) == engaged && !isRoomEngaged())
//		"${(resetEngagedDirectly ? vacant : (motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : checking))}"()
		roomVacant()
}

def powerStaysBelowAsleep()		{
	if (getChildDevice(getRoom())?.currentValue(occupancy) == asleep)
//		"${(resetAsleepDirectly ? vacant : (motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : checking))}"()
		roomAwake()
}

def powerStaysBelowLocked()		{
	if (getChildDevice(getRoom())?.currentValue(occupancy) == locked)
//		"${(motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : checking)}"()
		roomvacant()
}

def speechEventHandler(evt)		{
	ifDebug("speechEventHandler", 'info')
	ifDebug("evt.name: $evt.name | evt.value: $evt.value")
}

def roomVacant(forceVacant = false)		{
	ifDebug("roomVacant", 'info')
	def rSt = getChildDevice(getRoom())?.currentValue(occupancy)
	if (!forceVacant && motionSensors && ['engaged', 'occupied', 'checking', 'vacant'].contains(rSt) && whichNoMotion == lastMotionInactive &&
		motionSensors.currentMotion.contains(active))		{
		"${(isRoomEngaged() ? engaged : occupied)}"()
		return
	}
	def newState = null
	if (rSt == engaged && resetEngagedDirectly)			newState = vacant;
	else if (rSt == checking)							newState = vacant;
//	else if (['engaged', 'occupied'].contains(rSt))		newState = (state.dimTimer ? checking : vacant);
	else												newState = (state.dimTimer ? checking : vacant);
	if (newState)		"$newState"();
}

def roomAwake()		{
	ifDebug("roomAwake", 'info')
//	def child = getChildDevice(getRoom())
	if (getChildDevice(getRoom())?.currentValue(occupancy) == asleep)
//		"${(!resetAsleepDirectly && state.dimTimer ? checking : vacant)}"();
		"${(resetAsleepDirectly ? vacant : (motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : (state.dimTimer ? checking : vacant)))}"()

}

def roomUnlocked()		{
	ifDebug("roomUnlocked", 'info')
//	def child = getChildDevice(getRoom())
	if (getChildDevice(getRoom())?.currentValue(occupancy) == locked)
//		"${(state.dimTimer ? checking : vacant)}"();
		roomVacant()
}

def handleSwitches(oldState, newState, returnTimer = false)	{
	ifDebug("${app.label} handleSwitches: room state - old: ${oldState} new: ${newState}", 'info')
	def nowTime = now()
	if (debugLogging && state.debugOffTime < nowTime)		app.updateSetting('debugLogging', [type: 'bool', value: false]);
	if (oldState == newState)	return false;
	state.previousState = ['state':newState, 'date':nowTime]
	previousStateStack(state.previousState)
	state.motiontraffic = 0
	if (!checkPauseModesAndDoW())	return;
	def child = getChildDevice(getRoom())
	if (oldState == vacant)		{
		if (nightSwitches && nightTurnOn.contains('3'))		unschedule('nightSwitchesOff');
		if (state.maintainRoomHumi && !state.humidity.previous)		state.humidity.previous = getAvgHumidity();
	}
	def timer = null
	if (oldState == asleep)		{
		unschedule('roomAwake')
		unschedule('resetAsleep')
		unschedule('nightSwitchesOff')
		if (returnTimer)	timer = 0;
		else 				updateChildTimer(0);
		if (nightSwitches && !nightTurnOn.contains('3'))	nightSwitchesOff();		// state changed away from asleep and night switches to turn off
	}
	else if (oldState == locked)
		unschedule('roomUnlocked')
	else		{
		unscheduleAll("handle switches")
		if (oldState == checking)		unDimLights(newState);
	}
	if (['engaged', 'occupied', 'asleep', 'vacant'].contains(newState))		{
		if (newState != vacant || state.vacant)		// not vacant or has vacant rule
			processRules(newState)
		else		{
			switches2Off()
			if (musicDevice && turnOffMusic && musicDevice.currentStatus == 'playing')		musicDevice.pause();
		}
	}
	if (newState == asleep)		{
		if (nightSwitches)	{
			if ((motionSensors && motionSensors.currentMotion.contains(active)) || nightTurnOn.contains('2'))		{
				dimNightLights()
				if (state.noMotionAsleep && ((motionSensors && !motionSensors.currentMotion.contains(active)) || whichNoMotion != lastMotionInactive))		{
					if (returnTimer)	timer = state.noMotionAsleep;
					else				updateChildTimer(state.noMotionAsleep)
					runIn(state.noMotionAsleep, nightSwitchesOff)
				}
			}
			else
				nightSwitchesOff()
		}
		if (state.noAsleep)		{
			if (returnTimer)	timer = state.noAsleep;
			else				updateChildTimer(state.noAsleep)
			runIn(state.noAsleep, roomAwake)
		}
		if (resetAsleepWithContact)		{
			def cV = (contactSensor ? contactSensor.currentContact : '')
			if (cV.contains(open))	{
				if (returnTimer)	timer = (resetAsleepWithContact as Integer) * 60;
				else				updateChildTimer((resetAsleepWithContact as Integer) * 60)
				runIn((resetAsleepWithContact as Integer) * 60, resetAsleep)
			}
		}
	}
	else if (newState == engaged)	{
		if (state.noMotionEngaged)		timer = refreshEngagedTimer(engaged, returnTimer);
	}
	else if (newState == occupied)		{
		state.motionTraffic = 1
		if (state.noMotion && (!motionSensors || whichNoMotion == lastMotionActive || (whichNoMotion == lastMotionInactive && !motionSensors.currentMotion.contains(active))))
			timer = refreshOccupiedTimer(occupied, returnTimer)
	}
	else if (newState == checking)		{
		dimLights()
//		def dT = (state.dimTimer ?: 1)
		if (state.dimTimer > 5)
			if (returnTimer)	timer = state.dimTimer;
			else				updateChildTimer(state.dimTimer);
		runIn(state.dimTimer, roomVacant)
	}
	else if (newState == locked)		{
		if (lockedTurnOff)		switches2Off();
		if (state.unLocked)		runIn(state.unLocked, roomUnlocked);
	}
	if (oldState == asleep && newState == vacant && nightSwitches && nightTurnOn.contains('3'))	{
		dimNightLights()
		if (state.noMotionAsleep)		{
			if (returnTimer)	timer = state.noMotionAsleep;
			else				updateChildTimer(state.noMotionAsleep)
			runIn(state.noMotionAsleep, nightSwitchesOff)
		}
	}
	if (['engaged', 'occupied', 'asleep', 'vacant'].contains(newState))	{
		processCoolHeat(newState)
		if (newState != vacant)		processHumidity(newState);
	}
	if (!state.processChild)	scheduleFromToTimes();
	if (returnTimer)	return timer;
//log.debug "perf handleSwitches: ${now() - nowTime} ms"
}

def switchesOnOrOff(switchesOnly = false)	{
	ifDebug("switchesOnOrOff", 'info')
//	state.holidayLights = false
//	def child = getChildDevice(getRoom())
	def rSt = getChildDevice(getRoom())?.currentValue(occupancy)
	if (rSt && ['engaged', 'occupied', 'asleep', 'vacant'].contains(rSt))	{
		def turnedOn = processRules(rSt, switchesOnly)
//		ifDebug("switchesOnOrOff: ${(!turnedOn)} | $allSwitchesOff")
		if (!turnedOn && allSwitchesOff)	{
			switches2Off()
			if (musicDevice && turnOffMusic && musicDevice.currentStatus == 'playing')		musicDevice.pause();
		}
	}
}

private processRules(pRSt = null, switchesOnly = false)	{
	ifDebug("processRules", 'info')
	if (!state.execute || !state.rules)		return false;
	def nowTime	= now() + 1000
	def nowDate = new Date(nowTime)
	def child = getChildDevice(getRoom())
	def turnOn = []
	def turnOnRules = [:]
	def previousRule = []
	def previousRuleLux = null
	def previousRulePower = false
	state.lastRule = null
	def thisRule = [:]
	state.noMotion = ((noMotionOccupied && noMotionOccupied >= 5) ? noMotionOccupied as Integer : null)
	state.noMotionEngaged = ((noMotionEngaged && noMotionEngaged >= 5) ? noMotionEngaged as Integer : null)
	state.dimTimer = ((dimTimer && dimTimer >= 5) ? dimTimer as Integer : 5) // forces minimum of 5 seconds to allow for checking state
	state.noMotionAsleep = ((noMotionAsleep && noMotionAsleep >= 5) ? noMotionAsleep as Integer : null)
	updateTimers()
	def rSt = (pRSt ?: child?.currentValue(occupancy))
//	def sunriseTime, sunsetTime
	def sunRiseAndSet = sunRiseAndSet()
	if (!sunRiseAndSet)		return;
//	sunriseTime = sunRiseAndSet.rise
//	sunsetTime = sunRiseAndSet.set
	def timedRulesOnly = false
//	def sunriseTimeWithOff, sunsetTimeWithOff
	for (def i = 1; i <= maxRules; i++)	{
		def ruleHasTime = false
		def ruleNo = String.valueOf(i)
		if (turnOnRules[(ruleNo)])		thisRule = turnOnRules[(ruleNo)];
		else							thisRule = getNextRule(ruleNo, _ERule, true, false);
		if (thisRule.ruleNo == 'EOR')	break;
		if (!turnOnRules[(thisRule.ruleNo)])		turnOnRules << [(thisRule.ruleNo):thisRule];
		i = thisRule.ruleNo as Integer
		if (thisRule.mode && !thisRule.mode.contains(location.currentMode.toString()))		continue;
		if (thisRule.state && !thisRule.state.contains(rSt))			continue;
		if (thisRule.dayOfWeek && !(checkRunDay(thisRule.dayOfWeek)))	continue;
		if (thisRule.fromDate && thisRule.toDate)	{
			if (nowDate.before(new Date().parse("yyyy-MM-dd'T'HH:mm:ssZ", thisRule.fromDate)))		continue;
			if (nowDate.after(new Date().parse("yyyy-MM-dd'T'HH:mm:ssZ", thisRule.toDate)))			continue;
		}
		if (thisRule.luxThreshold != null && getAvgLux() > thisRule.luxThreshold)		continue;
		if (state.powerCheck && ((previousRulePower && !thisRule.powerThreshold) ||
			(thisRule.powerThreshold && getIntfromStr((String) powerDevice.currentPower) < thisRule.powerThreshold)))
			continue;
		if (thisRule.presence && !personsPresence.findAll { thisRule.presence.contains(it.id) && it.currentPresence == present })
			continue;
		if (thisRule.fromHumidity && thisRule.toHumidity)	{
			def humidity = getAvgHumidity()
			if (humidity < thisRule.fromHumidity || humidity > thisRule.toHumidity)		continue;
		}
		if (thisRule.checkOn && thisRule.checkOn.currentSwitch.contains(off))		continue;
		if (thisRule.checkOff && thisRule.checkOff.currentSwitch.contains(on))		continue;
		if (state.timeCheck && (thisRule.fromTimeType && (thisRule.fromTimeType != _timeTime || thisRule.fromTime)) &&
			(thisRule.toTimeType && (thisRule.toTimeType != _timeTime || thisRule.toTime)))		{
//			def fTime, tTime
//			if (thisRule.fromTimeType == _timeSunrise)
//				fTime = (thisRule.fromTimeOffset ? new Date(sunriseTime.getTime() + (thisRule.fromTimeOffset * 60000L)) : sunriseTime)
//			else if (thisRule.fromTimeType == _timeSunset)
//				fTime = (thisRule.fromTimeOffset ? new Date(sunsetTime.getTime() + (thisRule.fromTimeOffset * 60000L)) : sunsetTime)
//			else
//				fTime = timeToday(thisRule.fromTime, location.timeZone)
//			if (thisRule.toTimeType == _timeSunrise)
//				tTime = (thisRule.toTimeOffset ? new Date(sunriseTime.getTime() + (thisRule.toTimeOffset * 60000L)) : sunriseTime)
//			else if (thisRule.toTimeType == _timeSunset)
//				tTime = (thisRule.toTimeOffset ? new Date(sunsetTime.getTime() + (thisRule.toTimeOffset * 60000L)) : sunsetTime)
//			else
//				tTime = timeToday(thisRule.toTime, location.timeZone)
//			while (fTime > tTime)		tTime = tTime.plus(1);
//			if (!(timeOfDayIsBetween(fTime, tTime, nowDate, location.timeZone)))	continue;

			def x = compareRuleTime(sunRiseAndSet, thisRule)
			if (!x.inBetween)		continue;
			if (!timedRulesOnly)	{
				turnOn = []
//				turnOnRules = [:]
				previousRule = []
				previousRuleLux = null
				timedRulesOnly = true
				i = 0
				continue
			}
			ruleHasTime = true
		}
		if (timedRulesOnly && !ruleHasTime)		continue;
		if (state.powerCheck && thisRule.powerThreshold && !previousRulePower)		{
			previousRulePower = true
			turnOn = []
//				turnOnRules = [:]
			previousRule = []
			previousRuleLux = null
			i = 0
			continue
		}
		if (thisRule.luxThreshold != null)	{
			if (previousRuleLux == thisRule.luxThreshold)	{
				turnOn << thisRule.ruleNo
				previousRule << thisRule.ruleNo
			}
			else if (!previousRuleLux || thisRule.luxThreshold < previousRuleLux)	{
				for (def prv : previousRule)		turnOn.remove(prv);
				turnOn << thisRule.ruleNo
				previousRule << thisRule.ruleNo
				previousRuleLux = thisRule.luxThreshold
			}
		}
		else
			turnOn << thisRule.ruleNo
	}

	ifDebug("processRules: rules to execute: $turnOn")
//log.debug "perf processRules after rules: ${now() - nowTime + 1000} ms"
	def ret
	if (state.holidayLights)
	 	if (state.holiRuleNo && (!turnOn || !turnOn.contains(state.holiRuleNo)))	{
			if (settings["switchesOn$state.holiRuleNo"] && settings["switchesOn$state.holiRuleNo"].currentSwitch.contains(on))
				settings["switchesOn$state.holiRuleNo"].off()
			state.holidayLights = false
		}
	if (turnOn)		{
		state.switchesPreventToggle = []
		for (def trn : turnOn)	{
			thisRule = turnOnRules[(trn)]
			for (def tR : thisRule.switchesOn)		{
				def itID = tR.getId()
				if (!state.switchesPreventToggle.contains(itID))	state.switchesPreventToggle << itID;
			}
		}
//log.debug "perf processRules pre execute: ${now() - nowTime + 1000} ms"
		for (def trn : turnOn)	{
			executeRule(turnOnRules[(trn)], switchesOnly)
		}
		state.switchesPreventToggle = []
		ret = true
	}
	else
	{
		if (getHubType() != _Hubitat)	child.updateLastRuleInd(-1);
		ret = false
	}
//log.debug "perf processRules: ${now() - nowTime + 1000} ms"
	return ret
}

private compareRuleTime(sunRiseAndSet, thisRule)		{
	def nowDate = new Date(now())
	def fTime, tTime
	if (thisRule.fromTimeType == _timeSunrise)
		fTime = (thisRule.fromTimeOffset ? new Date(sunRiseAndSet.rise.getTime() + (thisRule.fromTimeOffset * 60000L)) : sunRiseAndSet.rise)
	else if (thisRule.fromTimeType == _timeSunset)
		fTime = (thisRule.fromTimeOffset ? new Date(sunRiseAndSet.set.getTime() + (thisRule.fromTimeOffset * 60000L)) : sunRiseAndSet.set)
	else
		fTime = timeToday(thisRule.fromTime, location.timeZone)
	if (thisRule.toTimeType == _timeSunrise)
		tTime = (thisRule.toTimeOffset ? new Date(sunRiseAndSet.rise.getTime() + (thisRule.toTimeOffset * 60000L)) : sunRiseAndSet.rise)
	else if (thisRule.toTimeType == _timeSunset)
		tTime = (thisRule.toTimeOffset ? new Date(sunRiseAndSet.set.getTime() + (thisRule.toTimeOffset * 60000L)) : sunRiseAndSet.set)
	else
		tTime = timeToday(thisRule.toTime, location.timeZone)
	while (fTime > tTime)		tTime = tTime.plus(1);
	while (tTime.getTime() - nowDate.getTime() > 86400000L)		{
		fTime = fTime.minus(1)
		tTime = tTime.minus(1)
	}
//log.debug "ruleNo: $thisRule.ruleNo | fromTime: $thisRule.fromTime | fTime: $fTime | toTime: $thisRule.toTime | tTime: $tTime | nowDate: $nowDate | timeOfDayIsBetween: ${timeOfDayIsBetween(fTime, tTime, nowDate, location.timeZone)}"
	def x = timeOfDayIsBetween(fTime, tTime, nowDate, location.timeZone)
	return [inBetween:x, fTime:fTime, tTime:tTime]
}

private executeRule(thisRule, switchesOnly = false)	{
//    ifDebug("${app.label} executed rule no: $thisRule.ruleNo", 'info')
	def nowTime = now()
	turnSwitchesOnAndOff(thisRule)
	if (!switchesOnly)	{
		runDeviceCmds(thisRule)
		runActions(thisRule)
		executePiston(thisRule)
		musicAction(thisRule)
		setShade(thisRule)
	}
	if (thisRule.noMotion && thisRule.noMotion >= 5)					state.noMotion = thisRule.noMotion as Integer;
	if (thisRule.noMotionEngaged && thisRule.noMotionEngaged >= 5)		state.noMotionEngaged = thisRule.noMotionEngaged as Integer
	if (thisRule.dimTimer && thisRule.dimTimer >= 5)					state.dimTimer = thisRule.dimTimer as Integer;
	if (thisRule.noMotionAsleep && thisRule.noMotionAsleep >= 5)		state.noMotionAsleep = thisRule.noMotionAsleep as Integer;
	updateTimers()
//log.debug "perf executeRule: ${now() - nowTime} ms"
}

private turnSwitchesOnAndOff(thisRule)	{
	def nowTime = now()
	ifDebug("turnSwitchesOnAndOff", 'info')
	def hT = getHubType()
	state.lastRule = (state.lastRule ? state.lastRule + ',' : '') + thisRule.ruleNo
	if (hT != _Hubitat)		getChildDevice(getRoom()).updateLastRuleInd(state.lastRule);
	if (thisRule.switchesOn)	{
		if (thisRule.level?.startsWith('HL'))	{
			if (!state.holidayLights || state.holiRuleNo != thisRule.ruleNo)	{
				state.holiRuleNo = thisRule.ruleNo
				def i = thisRule.level.substring(2)
				state.holiHues = state.holidays[i].hues
				state.holiStyle = state.holidays[i].style
				state.holiSeconds = state.holidays[i].seconds
				state.holiLevel = state.holidays[i].level
				state.holiColorCount = state.holidays[i].count
				state.holiColorIndex = -1
				state.holiLastTW = [:]
				state.holidayLights = true
				holidayLights()
			}
		}
		else
			turnSwitchesOn(thisRule)
	}
//log.debug "perf turnSwitchesOnAndOff before off: ${now() - nowTime} ms"
	for (def swt : thisRule.switchesOff)	{
		def itID = swt.getId()
		if (!state.switchesPreventToggle.contains(itID))	{
			if (dimOver && state.switchesHasLevel[itID])
				(hT == _Hubitat ? swt.setLevel(0, dimOver.toInteger()) : swt.setLevel(0))
			else
				swt.off()
			pauseIt()
		}
	}
//log.debug "perf turnSwitchesOnAndOff: ${now() - nowTime} ms"
}

private	turnSwitchesOn(thisRule)	{
	def nowTime = now()
	def hT = getHubType()
	def uniform = true
	int hasLevel = 0, hasColor = 0, hasColorTemperature = 0
//log.debug "perf turnSwitchesOn pre uniform check: ${now() - nowTime} ms | thisRule.ruleNo: $thisRule.ruleNo"
	if (state.rules)	{
		if (state.rules[(thisRule.ruleNo)]?.containsKey('uniform'))		{
			uniform = state.rules[(thisRule.ruleNo)].uniform
			hasLevel = state.rules[(thisRule.ruleNo)].hasLevel
			hasColor = state.rules[(thisRule.ruleNo)].hasColor
			hasColorTemperature = state.rules[(thisRule.ruleNo)].hasColorTemperature
		}
		else	{
			for (def swt : thisRule.switchesOn)		{
				def itID = swt.getId()
				if (thisRule.color)
					if (state.switchesHasColor[itID])	{
						if (hasColor == 0)	hasColor = 1		else if (hasColor != 1)		{ uniform = false; break }
					}
					else	{
						if (hasColor == 0)	hasColor = -1		else if (hasColor != -1)	{ uniform = false; break }
					}
				if (thisRule.colorTemperature || (thisRule.level == 'AL' && autoColorTemperature))
					if (state.switchesHasColorTemperature[itID])	{
						if (hasColorTemperature == 0)	hasColorTemperature = 1		else if (hasColorTemperature != 1)		{ uniform = false; break }
					}
					else	{
						if (hasColorTemperature == 0)	hasColorTemperature = -1	else if (hasColorTemperature != -1)		{ uniform = false; break }
					}
				if (thisRule.level)
					if (state.switchesHasLevel[itID])	{
						if (hasLevel == 0)	hasLevel = 1		else if (hasLevel != 1)		{ uniform = false; break }
					}
					else	{
						if (hasLevel == 0)	hasLevel = -1		else if (hasLevel != -1)	{ uniform = false; break }
					}
			}
			state.rules[(thisRule.ruleNo)] << [uniform:uniform, hasLevel:hasLevel, hasColor:hasColor, hasColorTemperature:hasColorTemperature]
//			state.rules[(thisRule.ruleNo)] << [hasLevel:hasLevel]
//			state.rules[(thisRule.ruleNo)] << [hasColor:hasColor]
//			state.rules[(thisRule.ruleNo)] << [hasColorTemperature:hasColorTemperature]
		}
	}
//log.debug "perf turnSwitchesOn post uniform check: ${now() - nowTime} ms"
	def colorTemperature = null
	def level = null
	if (uniform)	{
		def turnOn = true
		if (hasColor == 1)	{
			thisRule.switchesOn.setColor(thisRule.hue)
			turnOn = false
		}
		else if (hasColorTemperature == 1)	{
			colorTemperature = (thisRule.level == 'AL' ? calculateLevelOrKelvin(true) : thisRule.colorTemperature) as Integer
			if (colorTemperature)	{
				thisRule.switchesOn.setColorTemperature(colorTemperature)
				turnOn = false
			}
		}
		if (hasLevel == 1)	{
			level = (thisRule.level == 'AL' ? calculateLevelOrKelvin(false) : thisRule.level) as Integer;
			if (level)	{
				(hT == _Hubitat && dimOver ? thisRule.switchesOn.setLevel(level, dimOver.toInteger()) : thisRule.switchesOn.setLevel(level))
				turnOn = false
			}
		}
		if (turnOn)		thisRule.switchesOn.on()
		ifDebug("uniform: $uniform | level: $level | colorTemperature: $colorTemperature")
//log.debug "perf turnSwitchesOn uniform exit: ${now() - nowTime} ms"
		return
	}
	for (def swt : thisRule.switchesOn)		{
		def turnOn = true
//		def setCTorColor = false
		def itID = swt.getId()
		if (thisRule.color && state.switchesHasColor[itID])	{
			swt.setColor(thisRule.hue)
//			setCTorColor = true
			turnOn = false
		}
		else if ((thisRule.colorTemperature || (thisRule.level == 'AL' && autoColorTemperature)) && state.switchesHasColorTemperature[itID])	{
			if (!colorTemperature)
				colorTemperature = (thisRule.level == 'AL' ? calculateLevelOrKelvin(true) : thisRule.colorTemperature) as Integer
			if (colorTemperature)	{
				swt.setColorTemperature(colorTemperature)
//				setCTorColor = true
				turnOn = false
			}
		}
		if (thisRule.level && state.switchesHasLevel[itID])	{
			if (!level)		level = (thisRule.level == 'AL' ? calculateLevelOrKelvin(false) : thisRule.level) as Integer;
			if (level)	{
//				if (setCTorColor)	pauseIt(true);
				(hT == _Hubitat && dimOver ? swt.setLevel(level, dimOver.toInteger()) : swt.setLevel(level))
				turnOn = false
			}
		}
		if (turnOn)		swt.on()
	}
	ifDebug("not uniform | level: $level | colorTemperature: $colorTemperature")
//log.debug "perf turnSwitchesOn not uniform exit: ${now() - nowTime} ms"
}

def holidayLights()	{
	def nowTime = now()
	ifDebug('holidayLights', 'info')
	if (!state.holidayLights)	return;
	def switchesOn = settings["switchesOn$state.holiRuleNo"]
	if (state.holiStyle == 'RO')	{
		state.holiColorIndex = (state.holiColorIndex < (state.holiColorCount -1) ? state.holiColorIndex + 1 : 0)
		holidayLightsRotate(switchesOn)
	}
	else if (state.holiStyle == 'TW')	{
		state.holiLastTW = (state.holiTW ?: [:])
		holidayLightsTwinkle(switchesOn)
	}
	runIn(state.holiSeconds, holidayLights)
//log.debug "perf holidayLights: ${now() - nowTime} ms"
}

private holidayLightsRotate(switchesOn)	{
	ifDebug('holidayLightsRotate', 'info')
	def cI = state.holiColorIndex
	for (def swt : switchesOn)		{
		def holiColor = state.holiHues."$cI"
		if (swt.currentSwitch == off)	swt.on();
		swt.setColor(holiColor); pauseIt()
		if (state.holiLevel)		swt.setLevel(state.holiLevel);
		cI = (cI < (state.holiColorCount -1) ? cI + 1 : 0)
	}
}

private holidayLightsTwinkle(switchesOn)	{
	ifDebug('holidayLightsTwinkle', 'info')
	def hT = getHubType()
	(hT == _Hubitat ? switchesOn.setLevel(0,0) : switchesOn.setLevel(0))
	def noSwitches = switchesOn.size()
	def noColors = state.holiHues.size()
	int cI
	def randomFound
	Random rand = new Random()
	state.holiTW = [:]
	for (def i = 0; i < noSwitches; i++)	{
		randomFound = false
		for (def j = 0; j < (noColors * 3); j++)	{
			cI = rand.nextInt(noColors)
			if (state.holiLastTW."$i" != cI)	{
				state.holiTW."$i" = cI
				randomFound = true
				break
			}
		}
		if (!randomFound)		state.holiTW."$i" = cI;
	}
	cI = 0
	for (def swt : switchesOn)		{
		def tw = state.holiTW."$cI"
		swt.setColor(state.holiHues."$tw"); pauseIt()
		if (state.holiLevel)		(hT == _Hubitat ? swt.setLevel(state.holiLevel, 1) : swt.setLevel(state.holiLevel));
		cI = cI + 1
	}
}

private runDeviceCmds(thisRule)		{
	if (thisRule.device && thisRule.commands)		for (def cmds : thisRule.commands)		{  thisRule.device."$cmds"(); pauseIt()  }
}

private runActions(thisRule)	{
	for (def act : thisRule.actions)		{  location.helloHome?.execute(act); pauseIt()  }
}

private executePiston(thisRule)	{
	if (thisRule.piston)	{ webCoRE_execute(thisRule.piston); pauseIt() }
}

private musicAction(thisRule)	{
	if (musicDevice && thisRule.musicAction)	{
		if (thisRule.musicAction == '1')	{  musicDevice.play(); pauseIt()  }
// to unmute or not?            musicDevice.unmute()
		else if (thisRule.musicAction == '2')	{  musicDevice.pause(); pauseIt()  }
	}
}

private setShade(thisRule)	{
	if (thisRule.shade)	{
		switch(thisRule.shade)	{
			case '0':       windowShades.close();           break;
			case '10':      windowShades.setLevel(10);      break;
			case '20':      windowShades.setLevel(20);      break;
			case '30':      windowShades.setLevel(30);      break;
			case '40':      windowShades.setLevel(40);      break;
			case '50':      windowShades.setLevel(50);      break;
			case '60':      windowShades.setLevel(60);      break;
			case '70':      windowShades.setLevel(70);      break;
			case '80':      windowShades.setLevel(80);      break;
			case '90':      windowShades.setLevel(90);      break;
			case '100':     windowShades.open();            break;
			case 'P1':      windowShades.presetPosition(1); break;
			case 'P2':      windowShades.presetPosition(2); break;
			case 'P3':      windowShades.presetPosition(3); break;
			default:        break;
		}
		pauseIt()
	}
}

private calculateLevelOrKelvin(kelvin = false)	{
	// only calculate level and kelvin every 10 mins
	def nowTime = now()
	int x
	if (!state.calculateLK)		state.calculateLK = [:];
	if (kelvin)		{
		if (state.calculateLK && state.calculateLK['kelvin'] && (nowTime - state.calculateLK.kelvin.time) < 180000)
			x = state.calculateLK.kelvin.value
		else	{
			x = calculateLK(minKelvin, maxKelvin, fadeCTWake, fadeKWakeBefore, fadeKWakeAfter, fadeCTSleep, fadeKSleepBefore, fadeKSleepAfter)
			state.calculateLK['kelvin'] = [time:nowTime, value:x]
		}
	}
	else	{
		if (state.calculateLK && state.calculateLK['level'] && (nowTime - state.calculateLK.level.time) < 180000)
			x = state.calculateLK.level.value
		else	{
			x = calculateLK(minLevel, maxLevel, fadeLevelWake, fadeWakeBefore, fadeWakeAfter, fadeLevelSleep, fadeSleepBefore, fadeSleepAfter)
			state.calculateLK['level'] = [time:nowTime, value:x]
		}
	}
	ifDebug("${(kelvin ? 'kelvin' : 'level')}: $x")
	return x
}

private calculateLK(min, max, fadeW, fadeWB, fadeWA, fadeS, fadeSB, fadeSA)	{
	long timeNow = now()
	def dateNow = new Date(timeNow)

	def useInput = (wakeupTime && sleepTime ? true : false)

	def wTime, sTime

	def hT = getHubType()
//log.debug "wakeupTime: $wakeupTime | sleepTime: $sleepTime"
	//wTime = timeToday((useInput ? (hT == _Hubitat ? wakeupTime.replace("+0000", "-0800") : wakeupTime) : "7:00"), location.timeZone)
	//sTime = timeToday((useInput ? (hT == _Hubitat ? sleepTime.replace("+0000", "-0800") : sleepTime) : "23:00"), location.timeZone)
	wTime = timeToday((useInput ? wakeupTime : "7:00"), location.timeZone)
	sTime = timeToday((useInput ? sleepTime : "23:00"), location.timeZone)
	while (wTime > sTime)		sTime = sTime.plus(1);

	if (dateNow > wTime && dateNow > sTime)	{
		wTime = timeTodayA(dateNow, wTime, location.timeZone)
		sTime = timeTodayA(dateNow, sTime, location.timeZone)
	}

	long maxMinDiff = max - min

	if (fadeW)	{
		def wTimeBefore = new Date((wTime.getTime() - (fadeWB * 3600000L)))
		def wTimeAfter = new Date((wTime.getTime() + (fadeWA * 3600000L)))

		if (timeOfDayIsBetween(wTimeBefore, wTimeAfter, dateNow, location.timeZone))	{
			double cDD = ((dateNow.getTime() - wTimeBefore.getTime()) / (wTimeAfter.getTime() - wTimeBefore.getTime()))
			cDD = cDD * maxMinDiff
			int cD = cDD + min
			return cD
		}
	}

	if (fadeS)	{
		def sTimeBefore = new Date((sTime.getTime() - (fadeSB * 3600000L)))
		def sTimeAfter = new Date((sTime.getTime() + (fadeSA * 3600000L)))

		if (timeOfDayIsBetween(sTimeBefore, sTimeAfter, dateNow, location.timeZone))	{
			double cDD = ((sTimeAfter.getTime() - dateNow.getTime()) / (sTimeAfter.getTime() - sTimeBefore.getTime()))
			cDD = cDD * maxMinDiff
			int cD = cDD + min
			return cD
		}
	}

	def x = (timeOfDayIsBetween(wTime, sTime, dateNow, location.timeZone) ? (fadeW ? max : null) : (fadeS ? min : null))
	return x
}

// since hubitat does not support timeTodayAfter(...) 2018-04-08
private timeTodayA(whichDate, thisDate, timeZone)	{
	return (thisDate.before(whichDate) ? thisDate.plus(((whichDate.getTime() - thisDate.getTime()) / 86400000L).intValue() + 1) : thisDate)
}

private whichSwitchesAreOn(returnAllSwitches = false)	{
	def switchesThatAreOn = []
	def switchesThatAreOnID = []
	for (def i = 1; i <= maxRules; i++)	{
		def ruleNo = String.valueOf(i)
		def thisRule = getNextRule(ruleNo, _ERule)
		if (thisRule.ruleNo == 'EOR')	break;
		i = thisRule.ruleNo as Integer
		for (def swt : thisRule.switchesOn)		{
			def itID = swt.getId()
			if ((returnAllSwitches || swt.currentSwitch == on) && !switchesThatAreOnID.contains(itID))	{
				switchesThatAreOn << swt
				switchesThatAreOnID << itID
			}
		}
	}
	return switchesThatAreOn
}

def dimLights()		{
	ifDebug("dimLights", 'info')
	def hT = getHubType()
	state.preDimLevel = [:]
	if (!state.dimTimer || (!state.dimByLevel && !state.dimToLevel))	return;
	def switchesThatAreOn = whichSwitchesAreOn()
	if (switchesThatAreOn && state.dimByLevel)		{
		for (def swt : switchesThatAreOn)	{
			if (swt.currentSwitch == 'on')	{
				def itID = swt.getId()
				if (state.switchesHasLevel[itID])	{
					def currentLevel = swt.currentLevel
					state.preDimLevel << [(swt.getId()):currentLevel]
					def dimByLevel = state.dimByLevel.toInteger()
					def newLevel = (currentLevel > dimByLevel ? currentLevel - dimByLevel : 1)
					(hT == _Hubitat && dimOver ? swt.setLevel(newLevel, dimOver.toInteger()) : swt.setLevel(newLevel))
					pauseIt()
				}
			}
		}
	}
	else		{
		int lux
		if (luxCheckingDimTo && luxSensor)		lux = getAvgLux();
		if (!luxCheckingDimTo || lux <= luxCheckingDimTo)	{
			def allSwitches = whichSwitchesAreOn(true)
			if (allSwitches && state.dimToLevel)	{
				for (def swt : allSwitches)		{
					def itID = swt.getId()
					if (state.switchesHasLevel[itID])	{
						state.preDimLevel << [(swt.getId()):swt.currentLevel]
						(hT == _Hubitat && dimOver ? swt.setLevel(state.dimToLevel, dimOver.toInteger()) : swt.setLevel(state.dimToLevel))
						pauseIt()
					}
				}
			}
		}
	}
}

private unDimLights(rSt, returnTimer = false)	{
	ifDebug("unDimLights", 'info')
	if (!state.dimTimer || (!state.dimByLevel && !state.dimToLevel) || !state.preDimLevel)	return;
	if (!notRestoreLL || rSt != vacant)		{
//		whichSwitchesAreOn().each	{
		def hT = getHubType()
		for (def swt : whichSwitchesAreOn())	{
			def itID = swt.getId()
			if (swt.currentSwitch == 'on' && state.switchesHasLevel[itID])
				def newLevel = state.preDimLevel[itID]
				if (newLevel > 0)	{ (hT == _Hubitat && dimOver ? swt.setLevel(newLevel, dimOver.toInteger()) : swt.setLevel(newLevel)); pauseIt() }
			}
	}
	updateChildTimer(0)
	state.preDimLevel = [:]
}

def switches2Off()	{
	if (state.holidayLights)		return;
//	state.holidayLights = false
	def switches = whichSwitchesAreOn(true)
	for (def swt : switches)	{
		if (swt.currentSwitch == on && swt.currentLevel != 0)
			(dimOver && getHubType() == _Hubitat && state.switchesHasLevel[(swt.getId())] ? swt.setLevel(0, dimOver.toInteger()) : swt.off())
	}
}

private previousStateStack(previousState)	{
	ifDebug("previousStateStack", 'info')
	def i
	def timeIs = now()
	def factor = (state.busyCheck ?: 10)
	def removeHowOld = (state.noMotion ? (((state.noMotion as Integer) + (state.dimTimer as Integer)) * factor) : (180 * factor))
	def howMany
	int gapBetween

	turnOffIsBusy()
	if (state.stateStack)
		for (i = 9; i > 0; i--)	{
			def s = String.valueOf(i)
			if (state.stateStack[s])	{
				gapBetween = ((timeIs - (state.stateStack[s])['date']) / 1000)
				if (gapBetween > removeHowOld)
					state.stateStack.remove(s)
				else
					break
			}
		}
	if (state.stateStack)
		for (i = 9; i > 0; i--)	{
			if (state.stateStack[String.valueOf(i-1)])
				state.stateStack[String.valueOf(i)] = state.stateStack[String.valueOf(i-1)]
		}
	else
		state.stateStack = [:]
	state.stateStack << ['0':previousState]

	if (state.busyCheck)	{
		howMany = 0
		gapBetween = 0
		for (i = 9; i > 0; i--)	{
			def s = String.valueOf(i)
			def sM = String.valueOf(i-1)
			if (state.stateStack[s] && ['occupied', 'checking', 'vacant'].contains((state.stateStack[s])['state']) &&
									   ['occupied', 'checking', 'vacant'].contains((state.stateStack[sM])['state']))		{
				howMany++
				gapBetween += (((state.stateStack[sM])['date'] - (state.stateStack[s])['date']) / 1000)
			}
		}
		if (howMany >= state.busyCheck)	{
			ifDebug("busy on")
			state.isBusy = true
			state.stateStack = [:]
			runIn(removeHowOld, turnOffIsBusy)
		}
	}
}

def turnOffIsBusy()		{  state.motionTraffic = 0; state.isBusy = false  }

def spawnChildDevice(roomName)	{
	ifDebug("spawnChildDevice")
	app.updateLabel(app.label)
	if (!childCreated())
		addChildDevice("bangali", "rooms occupancy", getRoom(), null, [name: getRoom(), label: roomName, completedSetup: true])
}

private childCreated()		{  return (getChildDevice(getRoom()) ? true : false)  }

private getRoom()	{  return "rm_${app.id}"  }

def uninstalled()	{
	ifDebug("uninstalled")
//    parent.unsubscribeChild(app.id)
//    parent.unsubscribeChildDevice(app)
	unschedule()
	unsubscribe()
	getAllChildDevices().each	{
		ifDebug("deleting $app.label virtual device $it.label")
		parent.unsubscribeChildRoomDevice(it)
		pauseIt()
		deleteChildDevice(it.deviceNetworkId)
	}
}

def childUninstalled()		{  ifDebug("uninstalled room device ${app.label}")  }

private convertRGBToHueSaturation(setColorTo)	{
	def str = setColorTo.replaceAll("\\s","").toLowerCase()
	def rgb = (colorsRGB[str][0] ?: colorsRGB['white'][0])
//    ifDebug("convertRGBToHueSaturation: $str | $rgb")
	float r = rgb[0] / 255
	float g = rgb[1] / 255
	float b = rgb[2] / 255
	float max = Math.max(Math.max(r, g), b)
	float min = Math.min(Math.min(r, g), b)
	float h, s, l = (max + min) / 2

	if (max == min)
		h = s = 0 // achromatic
	else    {
		float d = max - min
		s = l > 0.5 ? d / (2 - max - min) : d / (max + min)
		switch (max)	{
			case r:    h = (g - b) / d + (g < b ? 6 : 0);  break
			case g:    h = (b - r) / d + 2;                break
			case b:    h = (r - g) / d + 4;                break
		}
		h /= 6
	}
	return [hue: Math.round(h * 100), saturation: Math.round(s * 100), level: Math.round(l * 100)]
}

private unscheduleAll(classNameCalledFrom)		{
	ifDebug("${app.label} unschedule calling class: $classNameCalledFrom")
	unschedule('roomVacant')
	if (powerDevice)
		if (powerValueEngaged)		unschedule('powerStaysBelowEngaged');
		else if (powerValueAsleep)	unschedule('powerStaysBelowAsleep');
		else if (powerValueLocked)	unschedule('powerStaysBelowLocked');
	updateChildTimer(0)
}

private updateChildTimer(timer = 0)		{  (getChildDevice(getRoom())).setupTimer((int) timer)  }

/*
def updateSunRiseAndSet()	{
	sunRiseAndSet(true)
	scheduleFromToTimes()
}
*/

def scheduleFromToTimes(evt = [:])	{
	def nowTime = now()
	def nowDate = new Date(now())
	def cHH = nowDate.format("HH", location.timeZone).toInteger()
	def cMM = nowDate.format("mm", location.timeZone).toInteger()
	def hT = getHubType()
	def rSt = getChildDevice(getRoom())?.currentValue(occupancy)
	if (!state.processChild || !(evt instanceof Map))	{
		unschedule('timeHandler')
		state.processChild = (((new Date(now())).format("mm", location.timeZone).toInteger() + new Random().nextInt(60)) % 60)
	}
	else	{
		if (evt?.option?.contains('process') && (!onlyOnStateChange || (butNotInStates && butNotInStates.contains(rSt))))	{
			checkAndTurnOnOffSwitchesC()
//			state.processChild = ((cMM + 5) % 60).intValue()
		}
		if (evt?.option?.contains('time'))	{
//log.debug "new timeHandler"
//			if (checkPauseModesAndDoW())
			processRules()
		}
	}
//	def sunriseAndSunset = getSunriseAndSunset()
//	if (!sunriseAndSunset.sunrise || !sunriseAndSunset.sunset)	{
//		ifDebug("Please set location for the hub for rules to be processed.", "error")
//		return
//	}
//	state.sunriseTime = new Date(sunriseAndSunset.sunrise.getTime()).format("yyyy-MM-dd'T'HH:mm:ssZ")
//	state.sunsetTime  = new Date(sunriseAndSunset.sunset.getTime()).format("yyyy-MM-dd'T'HH:mm:ssZ")
	def x = ((cMM < state.processChild ? Math.abs(cMM - state.processChild) : Math.abs(state.processChild - cMM)) % 5)
	if (x == 0)    x = 5;
	cMM = cMM + x
	if (cMM > 59)    {
	    cMM = cMM % 60
	    cHH = (cHH > 22 ? 0 : cHH + 1)
	}
//	def pTime = timeTodayA(nowDate, timeToday(String.format("%02d:%02d", (state.processChild > cMM ? cHH : (cHH == 23 ? 0 : cHH + 1)), state.processChild), location.timeZone), location.timeZone)
	def pTime = timeTodayA(nowDate, timeToday(String.format("%02d:%02d", cHH, cMM), location.timeZone), location.timeZone)

	def process = true
	def time = false
	def runTime = pTime
	if (state.rules && state.timeCheck)		{
		def fromTime = scheduleFromTime()
		if (fromTime)	while (fromTime.getTime() < nowTime)	fromTime = fromTime.plus(1);
		def toTime = scheduleToTime()
		if (toTime)		while (toTime.getTime() < nowTime)		toTime = toTime.plus(1);
//log.debug "pTime: $pTime | fromTime: $fromTime | toTime: $toTime"
		if (process)	{
			if ((fromTime && !toTime) || (fromTime && fromTime <= toTime))	{
				if (pTime.equals(fromTime))			time = true
				else if (pTime.after(fromTime))		{  process = false; time = true; runTime = fromTime  }
			}
			else if ((toTime && !fromTime) || (toTime && toTime < fromTime))	{
				if (pTime.equals(toTime))		time = true
				else if (pTime.after(toTime))	{  process = false; time = true; runTime = toTime  }
			}
		}
		else	{
			if (fromTime && toTime)		{  time = true; runTime = (fromTime.before(toTime) ? fromTime : toTime)  }
			else if (fromTime)			{  time = true; runTime = fromTime  }
			else if (toTime)			{  time = true; runTime = toTime  }
		}
	}

//	if (process || time)
//		runOnce((time ? runTime : new Date(runTime.getTime() + Math.abs(new Random().nextInt(4000) % 4000) + 1000)), scheduleFromToTimes, [data: [option: "[${(process && time ? "process, time" : (process ? "process" : "time"))}]"]]);
	runOnce(runTime, scheduleFromToTimes, [data: [option: "[${(process && time ? "process, time" : (process ? "process" : "time"))}]"]])
//log.debug "perf scheduleFromToTimes: ${(now() - nowTime)} ms"
}

private scheduleFromTime()	{
	ifDebug("scheduleFromTime", 'info')
	if (!state.rules || !state.timeCheck)	return false;
	def nextTime = scheduleTime(true)
	if (nextTime)	{
		state.fTime = nextTime
		updateTimeFromToInd()
//		schedule(nextTime, timeFromHandler)
	}
	return nextTime
}

private scheduleToTime()	{
	ifDebug("scheduleToTime", 'info')
	if (!state.rules || !state.timeCheck)	return false;
	def nextTime = scheduleTime(false)
	if (nextTime)	{
		state.tTime = nextTime
		updateTimeFromToInd()
//		schedule(nextTime, timeToHandler)
	}
	return nextTime
}

private scheduleTime(fromTime)	{
	def nowTime	= now()
	def nowDate = new Date(nowTime)
//	def sunriseTime, sunsetTime
	def sunRiseAndSet = sunRiseAndSet()
	if (!sunRiseAndSet)		return;
//	sunriseTime = sunRiseAndSet.rise
//	sunsetTime = sunRiseAndSet.set
	def nextTime = null
//	def sunriseTimeWithOff, sunsetTimeWithOff
	for (def i = 1; i <= maxRules; i++)	{
		def ruleNo = String.valueOf(i)
		def thisRule = getNextRule(ruleNo, _ERule, true, true)
//log.debug thisRule
		if (thisRule.ruleNo == 'EOR')	break;
		i = thisRule.ruleNo as Integer
		if (thisRule.fromDate && thisRule.toDate)	{
			def fTime = new Date().parse("yyyy-MM-dd'T'HH:mm:ssZ", thisRule.fromDate)
			def tTime = new Date().parse("yyyy-MM-dd'T'HH:mm:ssZ", thisRule.toDate)
			if (nowDate > tTime)		continue;
			if ((!nextTime && nowDate >= fTime && nowDate <= tTime) || (nextTime && (fromTime ? fTime : tTime) >= nowDate && (fromTime ? fTime : tTime) < nextTime))
				nextTime = (fromTime ? fTime : tTime)
		}
//log.debug "$thisRule.fromTimeType | $thisRule.fromTime | $thisRule.toTimeType | $thisRule.toTime"
		if (!state.timeCheck || (!thisRule.fromTimeType || (thisRule.fromTimeType == _timeTime && !thisRule.fromTime)) ||
			(!thisRule.toTimeType || (thisRule.toTimeType == _timeTime && !thisRule.toTime)))
			continue;

		def cTime
		if ((fromTime ? thisRule.fromTimeType : thisRule.toTimeType) == _timeSunrise)
			cTime = ((fromTime ? thisRule.fromTimeOffset : thisRule.toTimeOffset) ? new Date(sunRiseAndSet.rise.getTime() + ((fromTime ? thisRule.fromTimeOffset : thisRule.toTimeOffset) * 60000L)) : sunRiseAndSet.rise)
		else if ((fromTime ? thisRule.fromTimeType : thisRule.toTimeType) == _timeSunset)
			cTime = ((fromTime ? thisRule.fromTimeOffset : thisRule.toTimeOffset) ? new Date(sunRiseAndSet.set.getTime() + ((fromTime ? thisRule.fromTimeOffset : thisRule.toTimeOffset) * 60000L)) : sunRiseAndSet.set)
		else
			cTime = timeToday((fromTime ? thisRule.fromTime : thisRule.toTime), location.timeZone)

		cTime = timeTodayA(nowDate, cTime, location.timeZone)

		if (!nextTime || timeOfDayIsBetween(nowDate, nextTime, cTime, location.timeZone))
			nextTime = cTime;
	}
	return nextTime
}

def timeHandler()		{
	ifDebug("timeHandler", 'info')
	def rSt = getChildDevice(getRoom())?.currentValue(occupancy)
	if (checkPauseModesAndDoW() && (!onlyOnStateChange || (butNotInStates && butNotInStates.contains(rSt))))		switchesOnOrOff();
	scheduleFromToTimes()
}

def timeFromHandler(evt = null)	{
	ifDebug("timeFromHandler", 'info')
	def rSt = getChildDevice(getRoom())?.currentValue(occupancy)
	if (checkPauseModesAndDoW() && (!onlyOnStateChange || (butNotInStates && butNotInStates.contains(rSt))))		switchesOnOrOff();
	scheduleFromToTimes()
}

def timeToHandler(evt = null)	{
	ifDebug("timeToHandler", 'info')
	def rSt = getChildDevice(getRoom())?.currentValue(occupancy)
	if (checkPauseModesAndDoW() && (!onlyOnStateChange || (butNotInStates && butNotInStates.contains(rSt))))		switchesOnOrOff();
	scheduleFromToTimes()
}

private sunRiseAndSet()		{
	def sunriseAndSunset = getSunriseAndSunset()
	if (!sunriseAndSunset.sunrise || !sunriseAndSunset.sunset)	{
		ifDebug("Please set location for the hub for rules to be processed.", "error")
		return false
	}
//	def sunriseTime = sunriseAndSunset.sunrise
//	def sunsetTime  = sunriseAndSunset.sunset
	return [rise:sunriseAndSunset.sunrise, set:sunriseAndSunset.sunset]
}

private updateTimeFromToInd()	{
	if (state.fTime && state.tTime)	{
		state.timeFromTo =  format24hrTime(state.fTime) + " "
		state.timeFromTo = state.timeFromTo + format24hrTime(state.tTime)
		if (getHubType() != _Hubitat)	{
			def child = getChildDevice(getRoom())
			child.updateTimeInd(state.timeFromTo);
		}
	}
}

private updateTimers()	{
	if (getHubType() != _Hubitat)
		getChildDevice(getRoom()).updateTimersInd(state.noMotion, state.dimTimer, state.noMotionEngaged, state.noMotionAsleep)
}

private format24hrTime(timeToFormat = new Date(now()), format = "HH:mm")	{  return timeToFormat.format("HH:mm", location.timeZone)  }

def getAdjMotionSensors()	{  return motionSensors  }

def getAdjRoomsSetting()	{  return adjRooms  }

def getLastStateChild()	{
	def addRoom = state.previousState
	addRoom << ['room':app.label]
//log.debug addRoom
	return addRoom
}

def getChildRoomOccupancyDevice()	{  return getChildDevice(getRoom())  }

def getChildRoomThermostat()	{  return (useThermostat && roomThermostat ? [name: app.label, thermostat: roomThermostat] : null)  }

def setupAlarmP(alarmDisabled, alarmTime, alarmVolume, alarmSound, alarmRepeat, alarmDayOfWeek)	{
	if (alarmDisabled || !alarmTime || !musicDevice)	{
		unschedule('ringAlarm')
		return
	}
	setSoundURI(alarmSound)
	state.alarm << [volume:alarmVolume]
	state.alarm << [repeat:alarmRepeat]
	if (alarmDayOfWeek)	{
		state.alarm << [dayOfWeek:[]]
		switch(alarmDayOfWeek)	{
			case 'ADW1':   case 'ADW2':   case 'ADW3':   case 'ADW4':   case 'ADW5':   case 'ADW6':   case 'ADW7':
							state.alarm << [dayOfWeek:(dayOfWeek)];							break
			case 'ADW8':	state.alarm.dayOfWeek = state.alarm.dayOfWeek + [1,2,3,4,5];	break
			case 'ADW9':	state.alarm.dayOfWeek = state.alarm.dayOfWeek + [6,7];			break
			default:		state.alarm.dayOfWeek = null;									break
		}
	}
	else
		state.alarm.dayOfWeek = null
	schedule(alarmTime, ringAlarm)
}

def setSoundURI(alarmSound)		{
	switch (alarmSound)	{
		case '0':	state.alarm = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/bell1.mp3", duration: "10"];		break
		case '1':	state.alarm = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/bell2.mp3", duration: "10"];		break
		case '2':	state.alarm = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/dogs.mp3", duration: "10"];		break
		case '3':	state.alarm = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/alarm.mp3", duration: "17"];		break
		case '4':	state.alarm = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/piano2.mp3", duration: "10"];		break
		case '5':	state.alarm = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/lightsaber.mp3", duration: "10"];	break
		default:	state.alarm = [uri: "", duration: "0"];																break
	}
}

def ringAlarm(turnOff = false)		{
	ifDebug("ringAlarm")
	if (turnOff)	{
		ifDebug("ringAlarm: turn alarm off")
		musicDevice.nextTrack()
		state.alarmRepeat = (state.alarm.repeat ?: 999) + 1
		unschedule('repeatAlarm')
		def child = getChildDevice(getRoom())
		child.alarmOff(true)
		return
	}
	if (!musicDevice)	return;
	if (state.alarm.dayOfWeek)	{
		def thisDay = (new Date(now())).getDay() + 1
	 	if (!state.alarm.dayOfWeek.contains(thisDay))		return;
	}
	state.alarmRepeat = 0
	repeatAlarm()
}

def repeatAlarm()	{
	def child = getChildDevice(getRoom())
	child.alarmOn()
	musicDevice.playTrackAndResume(state.alarm.uri, state.alarm.duration, state.alarm.volume)
	state.alarmRepeat = state.alarmRepeat + 1
	if (state.alarmRepeat <= state.alarm.repeat)	{
		def secs = state.alarm.duration as Integer
		runIn((secs + 5), repeatAlarm)
	}
	else
		child.alarmOff(true)
}

private checkAndTurnOnOffSwitchesC()	{
	def rSt = getChildDevice(getRoom())?.currentValue(occupancy)
//log.debug "onlyOnStateChange: $onlyOnStateChange | butNotInStates: $butNotInStates | rSt: $rSt"
	if ((onlyOnStateChange && (!butNotInStates || !butNotInStates.contains(rSt))) ||
		(awayModes && awayModes.contains(location.currentMode)) || !checkPauseModesAndDoW())
		return false;
//log.debug state.stateStack
	if (state.stateStack && ((now() - state.stateStack['0'].date) / 1000f) < 150)		return false;
//log.debug 'switchesOnOrOff'
	switchesOnOrOff(true)
	return true
}

private checkPauseModesAndDoW()	{
	if (pauseModes && pauseModes.contains(location.currentMode))	return false;
	if (state.dayOfWeek && !(checkRunDay()))	return false;
	return true
}

private checkRunDay(dayOfWeek = null)	{
	long timestamp = now()
	def thisDay = ((new Date(timestamp + location.timeZone.getOffset(timestamp))).day ?: 7)
	return (dayOfWeek ?: state.dayOfWeek).contains(thisDay)
}

def windowShadeEventHandler(evt)		{
	ifDebug("windowShadeEventHandler")
	def child = getChildDevice(getRoom())
	def wSSInd = -1
	if (windowShades)	{
		def wSS = windowShades.currentWindowShade
		if (wSS.contains('partially'))	wSSInd = 'Partially\nopen'
		else if (wSS.contains(open))	wSSInd = 'Open'
		else							wSSInd = 'Closed'
	}
	if (getHubType() != _Hubitat)	child.updateWSSInd(wSSInd);
}

private speakIt(str)	{
	if (announceInModes && !announceInModes.contains(location.currentMode))	return false;
	def nowDate = new Date(now())
	def intCurrentHH = nowDate.format("HH", location.timeZone) as Integer
	def intCurrentMM = nowDate.format("mm", location.timeZone) as Integer
	if (intCurrentHH < startHH || (intCurrentHH > endHH || (intCurrentHH == endHH && intCurrentMM != 0)))	return;
	def hT = getHubType()
	def vol = speakerVolume
	if (useVariableVolume)	{
		int x = startHH + ((endHH - startHH) / 4)
		int y = endHH - ((endHH - startHH) / 3)
		if (intCurrentHH <= x || intCurrentHH >= y)		vol = (speakerVolume - (speakerVolume / 3)).toInteger();
	}
	if (speakerDevices)	{
		def currentVolume = speakerDevices.currentLevel
		def isMuteOn = speakerDevices.currentMute.contains("muted")
		if (isMuteOn)		speakerDevices.unmute();
		speakerDevices.playTextAndResume(str, vol);
		if (currentVolume != vol)		speakerDevices.setLevel(currentVolume);
		if (isMuteOn)		musicPlayers.mute();
	}
	if (speechDevices)		speechDevices.speak(str);
	if (musicPlayers)	{
		def currentVolume = musicPlayers.currentLevel
		def isMuteOn = musicPlayers.currentMute.contains("muted")
		if (isMuteOn)		musicPlayers.unmute();
		musicPlayers.playTrackAndResume(str, vol)
		if (currentVolume != vol)		musicPlayers.setLevel(currentVolume)
		if (isMuteOn)		musicPlayers.mute();
	}
	if (hT == _SmartThings && listOfMQs)
		sendLocationEvent(name: "AskAlexaMsgQueue", value: "Rooms Occupancy", isStateChange: true,  descriptionText: "$str", data:[queues:listOfMQs, expires: 30, notifyOnly: true, suppressTimeDate: true])
	if (echoAccessCode)	{
		def uri = "https://api.notifymyecho.com/v1/NotifyMe?notification=${URLEncoder.encode(str, 'UTF-8')}&accessCode=${decrypt(echoAccessCode)}"
		httpPost([uri: uri, requestContentType: 'application/json'])	{ response ->
			if (response?.status != 200)		ifDebug("echo notify response $response");
		}
	}
}

def setupColorNotification()	{
	ifDebug("setupColorNotification", 'info')
	if (announceInModes && !(announceInModes.contains(location.currentMode.toString())))		return false;
	def nowDate = new Date(now())
	def intCurrentHH = nowDate.format("HH", location.timeZone) as Integer
	def intCurrentMM = nowDate.format("mm", location.timeZone) as Integer
	if (intCurrentHH < startHHColor || (intCurrentHH > endHHColor || (intCurrentHH == endHHColor && intCurrentMM != 0)))
		return
	if (!state.colorsRotating)	{
		state.colorNotifyTimes = 9
		state.colorSwitchSave = []
		state.colorColorSave = []
		state.colorColorTemperatureSave = []
		state.colorColorTemperatureTrueSave = []
		for (def swt : announceSwitches)	{
			state.colorSwitchSave << swt.currentSwitch
			state.colorColorSave << [hue: swt.currentHue, saturation: swt.currentSaturation, level: swt.currentLevel]
			def evts = swt.events(max: 250)
			def foundValue = false
			def keepSearching = true
			for (def evt : evts)	{
				if (!foundValue && keepSearching)	{
					if (evt.value == 'setColorTemperature')
						foundValue = true
					else if (['hue', 'saturation'].contains(evt.name))
						keepSearching = false
				}
				else
					break
			}
//			if (!foundValue)		state.colorColorTemperatureTrueSave << false;
			state.colorColorTemperatureTrueSave << (foundValue ? true : false)
			state.colorColorTemperatureSave << swt.currentColorTemperature
		}
		notifyWithColor()
	}
	else
		runIn(10, setupColorNotification)
}

def notifyWithColor()	{
//	ifDebug("notifyWithColor", 'info')
	if (state.colorNotifyTimes % 2)	{
		announceSwitches.on(); pauseIt()
		announceSwitches.setColor(state.colorNotificationColor)
	}
	else
		announceSwitches.off()
	pauseIt()

	state.colorNotifyTimes = state.colorNotifyTimes - 1
	if (state.colorNotifyTimes >= 0)
		runIn(1, notifyWithColor)
	else		{
		def i = 0
		for (def swt : announceSwitches)	{
			ifDebug("$swt | ${state.colorColorSave[i]} | ${state.colorSwitchSave[(i)]} | ${state.colorColorTemperatureTrueSave[i]} | ${state.colorColorTemperatureSave[i]}")
			if (state.colorColorTemperatureTrueSave[(i)] == true)	{
			 	swt.setColorTemperature(state.colorColorTemperatureSave[(i)])
				pauseIt(true)
			}
			else
				swt.setColor(state.colorColorSave[(i)])
			pauseIt(true)
			swt.setLevel(state.colorColorSave[(i)].level)
			i = i + 1
		}
		if (getHubType() == _Hubitat)	{
			def nowTime = now()
			def tillTime = nowTime + 250
			while (nowTime < tillTime)		nowTime = now();
		}
		i = 0
		for (def swt : announceSwitches)	{
			swt."${(state.colorSwitchSave[(i)] == off ? off : on)}"(); pauseIt()
			i = i + 1
		}
	}
}

private presenceActionArrival()		{  return (presenceAction == '1' || presenceAction == '3')  }
private presenceActionDeparture()	{  return (presenceAction == '2' || presenceAction == '3')  }

private ifDebug(msg = null, level = null)	{  if (msg && (isDebug() || level == 'error'))	log."${level ?: 'debug'}" " $app.label: " + msg  }

private	hasOccupiedDevice()		{ return (motionSensors || occupiedButton || occSwitches) }

// only called from device handler
def turnSwitchesAllOnOrOff(turnOn)	{
	def switches = getAllSwitches()
	if (switches)	{
		def action = (turnOn ? on : off)
		for (def swt : switches)		if (swt.currentSwitch != action)	{ swt."$action"(); pauseIt() };
	}
}

def roomDeviceSwitchOnP()	{ return roomDeviceSwitchOn }

private getAllSwitches()	{
	def switches = []
	def switchesID = []
	for (def i = 1; i <= maxRules; i++)		{
		def ruleNo = String.valueOf(i)
		def thisRule = getNextRule(ruleNo, _ERule)
		if (thisRule.ruleNo == 'EOR')	break;
		i = thisRule.ruleNo as Integer
		for (def swt : thisRule.switchesOn)	{
			def itID = swt.getId()
			if (!switchesID.contains(itID))		{
				switches << swt
				switchesID << itID
			}
		}
	}
	return switches
}

//------------------------------------------------------Night option------------------------------------------------------//
def	nightButtonPushedEventHandler(evt)	{
	ifDebug("nightButtonPushedEventHandler", 'info')
	if (!checkPauseModesAndDoW())	return;
	if (getHubType() == _SmartThings)	{
		if (!evt.data)	return;
		def nM = new groovy.json.JsonSlurper().parseText(evt.data)
		assert nM instanceof Map
		if (!nM || (nightButtonIs && nM['buttonNumber'] != nightButtonIs as Integer))	return;
	}
	def rSt = getChildDevice(getRoom())?.currentValue(occupancy)
	if (nightSwitches && rSt == 'asleep')	{
		unscheduleAll("night button pushed handler")
		def nS = nightSwitches.currentSwitch
		if (nightButtonAction == '1' || (nightButtonAction == '3' && !ns.contains(on)))			dimNightLights();
		else if ((nightButtonAction == "2" || nightButtonAction == '3') && nS.contains(on))		nightSwitchesOff();
	}
}

def dimNightLights()	{
	unschedule('dimNightLights')
	if (nightSwitches)	{
		if (!state.rules)		state.rules = [:];
		state.rules << ['NL':[:]]
		turnSwitchesOn([ruleNo:'NL', type:'e', name:'night lights', disabled:false, switchesOn:nightSwitches, level:state.nightSetLevelTo, color:state.nightSetColorTo, hue:state.nightSetHueTo, colorTemperature:state.nightSetCT])
		state.rules.remove('NL')
		if (getHubType() != _Hubitat)		getChildDevice(getRoom()).updateNSwitchInd(1);
	}
}

def nightSwitchesOff()	{
	unschedule('nightSwitchesOff')
	if (nightSwitches)	{
		nightSwitches.off(); pauseIt()
		if (getHubType() != _Hubitat)		getChildDevice(getRoom()).updateNSwitchInd(0);
	}
	if (resetAsleepWithContact && (contactSensor ? contactSensor.currentContact : '').contains(open))		{
		updateChildTimer((resetAsleepWithContact as Integer) * 60)
//		runIn((resetAsleepWithContact as Integer) * 60, resetAsleep)
	}
	else
		restoreTimer(asleep)
}

def sleepEventHandler(evt)		{
	ifDebug("sleepEventHandler: ${asleepSensor} - ${evt.value}", 'info')
	if (!checkPauseModesAndDoW())	return;
	def child = getChildDevice(getRoom())
	if (evt.value == "not sleeping")
//		"${(motionSensors && motionSensors.currentMotion.contains(active) && whichNoMotion != lastMotionActive ? occupied : checking)}"()
		roomAwake()
	else if (evt.value == "sleeping")
		asleep()
}

private pauseIt(longOne = false)	{
//	def hT = getHubType()
//	if (hT == _SmartThings)		pause(pauseMSecST);
//	else if (hT == _Hubitat)	pauseExecution(pauseMSecHU * (longOne ? 10 : 1));
}

//------------------------------------------------------------------------------------------------------------------------//

/*************************************************************************/
/* webCoRE Connector v0.2                                                */
/*************************************************************************/
/*  Copyright 2016 Adrian Caramaliu <ady624(at)gmail.com>                */
/*                                                                       */
/*  This program is free software: you can redistribute it and/or modify */
/*  it under the terms of the GNU General Public License as published by */
/*  the Free Software Foundation, either version 3 of the License, or    */
/*  (at your option) any later version.                                  */
/*                                                                       */
/*  This program is distributed in the hope that it will be useful,      */
/*  but WITHOUT ANY WARRANTY; without even the implied warranty of       */
/*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the         */
/*  GNU General Public License for more details.                         */
/*                                                                       */
/*  You should have received a copy of the GNU General Public License    */
/*  along with this program.  If not, see <http://www.gnu.org/licenses/>.*/
/*************************************************************************/
private webCoRE_handle()	{ return 'webCoRE' }
private webCoRE_init(pistonExecutedCbk)	{
	ifDebug("webCoRE_init", 'info')
	state.webCoRE = (state.webCoRE instanceof Map ? state.webCoRE:[:]) + (pistonExecutedCbk ? [cbk:pistonExecutedCbk] : [:])
	subscribe(location, "${webCoRE_handle()}.pistonList", webCoRE_handler)
	if (pistonExecutedCbk)	subscribe(location, "${webCoRE_handle()}.pistonExecuted", webCoRE_handler);
//    webCoRE_poll()
	sendLocationEvent([name: webCoRE_handle(), value:'poll', isStateChange:true, displayed:false])
}
/*
private webCoRE_poll()	{
	ifDebug("webCoRE_poll")
	sendLocationEvent([name: webCoRE_handle(), value:'poll', isStateChange:true, displayed:false])
}
*/
public  webCoRE_execute(pistonIdOrName, Map data=[:])	{
	ifDebug("webCoRE_execute", 'info')
	def i = (state.webCoRE?.pistons ?: []).find{(it.name == pistonIdOrName) || (it.id == pistonIdOrName)}?.id;
	if (i)	sendLocationEvent([name:i, value:app.label, isStateChange:true, displayed:false, data:data]);
}
public  webCoRE_list(mode)	{
	ifDebug("webCoRE_list", 'info')
	def p = state.webCoRE?.pistons;
	if (p)
		p.collect{mode == 'id' ? it.id : (mode == 'name' ? it.name : [id:it.id, name:it.name])
		// log.debug "Reading piston: ${it}"
	}
	return p
}
public  webCoRE_handler(evt)	{
	ifDebug("webCoRE_handler", 'info')
	switch(evt.value)	{
		case 'pistonList':
			List p = state.webCoRE?.pistons ?: []
			Map d = evt.jsonData ?: [:]
			if (d.id && d.pistons && (d.pistons instanceof List))	{
				p.removeAll{it.iid == d.id}
				p += d.pistons.collect{[iid:d.id]+it}.sort{it.name}
				state.webCoRE = [updated:now(), pistons:p]
			}
			break
		case 'pistonExecuted':
			def cbk = state.webCoRE?.cbk
			if (cbk && evt.jsonData)    "$cbk"(evt.jsonData);
			break
	}
}

//------------------------------------------------------------------------------------------------------------------------//

@Field final Map    colorsRGB = [
	aliceblue: [[240, 248, 255], 'Alice Blue'],
	antiquewhite: [[250, 235, 215], 'Antique White'],
	aqua: [[0, 255, 255], 'Aqua'],
	aquamarine: [[127, 255, 212], 'Aquamarine'],
	azure: [[240, 255, 255], 'Azure'],
	beige: [[245, 245, 220], 'Beige'],
	bisque: [[255, 228, 196], 'Bisque'],
	black: [[0, 0, 0], 'Black'],
	blanchedalmond: [[255, 235, 205], 'Blanched Almond'],
	blue: [[0, 0, 255], 'Blue'],
	blueviolet: [[138, 43, 226], 'Blue Violet'],
	brown: [[165, 42, 42], 'Brown'],
	burlywood: [[222, 184, 135], 'Burly Wood'],
	cadetblue: [[95, 158, 160], 'Cadet Blue'],
	chartreuse: [[127, 255, 0], 'Chartreuse'],
	chocolate: [[210, 105, 30], 'Chocolate'],
	coral: [[255, 127, 80], 'Coral'],
	cornflowerblue: [[100, 149, 237], 'Corn Flower Blue'],
	cornsilk: [[255, 248, 220], 'Corn Silk'],
	crimson: [[220, 20, 60], 'Crimson'],
	cyan: [[0, 255, 255], 'Cyan'],
	darkblue: [[0, 0, 139], 'Dark Blue'],
	darkcyan: [[0, 139, 139], 'Dark Cyan'],
	darkgoldenrod: [[184, 134, 11], 'Dark Golden Rod'],
	darkgray: [[169, 169, 169], 'Dark Gray'],
	darkgreen: [[0, 100, 0], 'Dark Green'],
	darkgrey: [[169, 169, 169], 'Dark Grey'],
	darkkhaki: [[189, 183, 107], 'Dark Khaki'],
	darkmagenta: [[139, 0, 139],  'Dark Magenta'],
	darkolivegreen: [[85, 107, 47], 'Dark Olive Green'],
	darkorange: [[255, 140, 0], 'Dark Orange'],
	darkorchid: [[153, 50, 204], 'Dark Orchid'],
	darkred: [[139, 0, 0], 'Dark Red'],
	darksalmon: [[233, 150, 122], 'Dark Salmon'],
	darkseagreen: [[143, 188, 143], 'Dark Sea Green'],
	darkslateblue: [[72, 61, 139], 'Dark Slate Blue'],
	darkslategray: [[47, 79, 79], 'Dark Slate Gray'],
	darkslategrey: [[47, 79, 79], 'Dark Slate Grey'],
	darkturquoise: [[0, 206, 209], 'Dark Turquoise'],
	darkviolet: [[148, 0, 211], 'Dark Violet'],
	deeppink: [[255, 20, 147], 'Deep Pink'],
	deepskyblue: [[0, 191, 255], 'Deep Sky Blue'],
	dimgray: [[105, 105, 105], 'Dim Gray'],
	dimgrey: [[105, 105, 105], 'Dim Grey'],
	dodgerblue: [[30, 144, 255], 'Dodger Blue'],
	firebrick: [[178, 34, 34], 'Fire Brick'],
	floralwhite: [[255, 250, 240], 'Floral White'],
	forestgreen: [[34, 139, 34], 'Forest Green'],
	fuchsia: [[255, 0, 255], 'Fuchsia'],
	gainsboro: [[220, 220, 220], 'Gainsboro'],
	ghostwhite: [[248, 248, 255], 'Ghost White'],
	gold: [[255, 215, 0], 'Gold'],
	goldenrod: [[218, 165, 32], 'Golden Rod'],
	gray: [[128, 128, 128], 'Gray'],
	green: [[0, 128, 0], 'Green'],
	greenyellow: [[173, 255, 47], 'Green Yellow'],
	grey: [[128, 128, 128], 'Grey'],
	honeydew: [[240, 255, 240], 'Honey Dew'],
	hotpink: [[255, 105, 180], 'Hot Pink'],
	indianred: [[205, 92, 92], 'Indian Red'],
	indigo: [[75, 0, 130], 'Indigo'],
	ivory: [[255, 255, 240], 'Ivory'],
	khaki: [[240, 230, 140], 'Khaki'],
	lavender: [[230, 230, 250], 'Lavender'],
	lavenderblush: [[255, 240, 245], 'Lavender Blush'],
	lawngreen: [[124, 252, 0], 'Lawn Green'],
	lemonchiffon: [[255, 250, 205], 'Lemon Chiffon'],
	lightblue: [[173, 216, 230], 'Light Blue'],
	lightcoral: [[240, 128, 128], 'Light Coral'],
	lightcyan: [[224, 255, 255], 'Light Cyan'],
	lightgoldenrodyellow: [[250, 250, 210], 'Light Golden Rod Yellow'],
	lightgray: [[211, 211, 211], 'Light Gray'],
	lightgreen: [[144, 238, 144], 'Light Green'],
	lightgrey: [[211, 211, 211], 'Light Grey'],
	lightpink: [[255, 182, 193], 'Light Pink'],
	lightsalmon: [[255, 160, 122], 'Light Salmon'],
	lightseagreen: [[32, 178, 170], 'Light Sea Green'],
	lightskyblue: [[135, 206, 250], 'Light Sky Blue'],
	lightslategray: [[119, 136, 153], 'Light Slate Gray'],
	lightslategrey: [[119, 136, 153], 'Light Slate Grey'],
	lightsteelblue: [[176, 196, 222], 'Ligth Steel Blue'],
	lightyellow: [[255, 255, 224], 'Light Yellow'],
	lime: [[0, 255, 0], 'Lime'],
	limegreen: [[50, 205, 50], 'Lime Green'],
	linen: [[250, 240, 230], 'Linen'],
	magenta: [[255, 0, 255], 'Magenta'],
	maroon: [[128, 0, 0], 'Maroon'],
	mediumaquamarine: [[102, 205, 170], 'Medium Aquamarine'],
	mediumblue: [[0, 0, 205], 'Medium Blue'],
	mediumorchid: [[186, 85, 211], 'Medium Orchid'],
	mediumpurple: [[147, 112, 219], 'Medium Purple'],
	mediumseagreen: [[60, 179, 113], 'Medium Sea Green'],
	mediumslateblue: [[123, 104, 238], 'Medium Slate Blue'],
	mediumspringgreen: [[0, 250, 154], 'Medium Spring Green'],
	mediumturquoise: [[72, 209, 204], 'Medium Turquoise'],
	mediumvioletred: [[199, 21, 133], 'Medium Violet Red'],
	midnightblue: [[25, 25, 112], 'Medium Blue'],
	mintcream: [[245, 255, 250], 'Mint Cream'],
	mistyrose: [[255, 228, 225], 'Misty Rose'],
	moccasin: [[255, 228, 181], 'Moccasin'],
	navajowhite: [[255, 222, 173], 'Navajo White'],
	navy: [[0, 0, 128], 'Navy'],
	oldlace: [[253, 245, 230], 'Old Lace'],
	olive: [[128, 128, 0], 'Olive'],
	olivedrab: [[107, 142, 35], 'Olive Drab'],
	orange: [[255, 165, 0], 'Orange'],
	orangered: [[255, 69, 0], 'Orange Red'],
	orchid: [[218, 112, 214], 'Orchid'],
	palegoldenrod: [[238, 232, 170], 'Pale Golden Rod'],
	palegreen: [[152, 251, 152], 'Pale Green'],
	paleturquoise: [[175, 238, 238], 'Pale Turquoise'],
	palevioletred: [[219, 112, 147], 'Pale Violet Red'],
	papayawhip: [[255, 239, 213], 'Papaya Whip'],
	peachpuff: [[255, 218, 185], 'Peach Cuff'],
	peru: [[205, 133, 63], 'Peru'],
	pink: [[255, 192, 203], 'Pink'],
	plum: [[221, 160, 221], 'Plum'],
	powderblue: [[176, 224, 230], 'Powder Blue'],
	purple: [[128, 0, 128], 'Purple'],
	rebeccapurple: [[102, 51, 153], 'Rebecca Purple'],
	red: [[255, 0, 0], 'Red'],
	rosybrown: [[188, 143, 143], 'Rosy Brown'],
	royalblue: [[65, 105, 225], 'Royal Blue'],
	saddlebrown: [[139, 69, 19], 'Saddle Brown'],
	salmon: [[250, 128, 114], 'Salmon'],
	sandybrown: [[244, 164, 96], 'Sandy Brown'],
	seagreen: [[46, 139, 87], 'Sea Green'],
	seashell: [[255, 245, 238], 'Sea Shell'],
	sienna: [[160, 82, 45], 'Sienna'],
	silver: [[192, 192, 192], 'Silver'],
	skyblue: [[135, 206, 235], 'Sky Blue'],
	slateblue: [[106, 90, 205], 'Slate Blue'],
	slategray: [[112, 128, 144], 'Slate Gray'],
	slategrey: [[112, 128, 144], 'Slate Grey'],
	snow: [[255, 250, 250], 'Snow'],
	springgreen: [[0, 255, 127], 'Spring Green'],
	steelblue: [[70, 130, 180], 'Steel Blue'],
	tan: [[210, 180, 140], 'Tan'],
	teal: [[0, 128, 128], 'Teal'],
	thistle: [[216, 191, 216], 'Thistle'],
	tomato: [[255, 99, 71], 'Tomato'],
	turquoise: [[64, 224, 208], 'Turquoise'],
	violet: [[238, 130, 238], 'Violet'],
	wheat: [[245, 222, 179], 'Wheat'],
	white: [[255, 255, 255], 'White'],
	whitesmoke: [[245, 245, 245], 'White Smoke'],
	yellow: [[255, 255, 0], 'Yellow'],
	yellowgreen: [[154, 205, 50], 'Yellow Green']
]

@Field final List    allSettingsVar = [
	["Room occupancy device:"],
	[["room child device", "Room child device:", null, false], "varRoomChild"],
	["room sensor settings:"],
	["motionSensors", "Motion sensors:", null, true],
	["triggerMotionSensors", "Trigger motion:", null, true, "or", "motionSensors"],
	[["whichNoMotion", "Which no motion:", null, false, "or", "motionSensors"], "varWhichNoMotion"],
	[["room buttons", "Room buttons:", null, true], "varRoomButtons"],
//	["roomButtonType", "Room button type:", null, false, "or", "roomButton"],
//	["roomButton", "Room button:", null, true],
//	["buttonForRoom", "Button number:", null, false, "or", "roomButton"],
	["roomButtonStates", "Rotate thru states:", null, false, "or", "roomButton"],
	["personsPresence", "Presence sensors:", null, true],
	["luxSensor", "Lux sensor:", null, true],
	["powerDevice", "Power device:", null, true],
	["humiditySensor", "Humidity sensor:", null, true],
	["musicDevice", "Music player:", null, true],
	["windowShades", "Window shades:", null, true],

	["occupied settings:"],
	["occupiedButtonType", "Occupied button type:", null, false, "or", "occupiedButton"],
	["occupiedButton", "Button device:", null, true],
	["buttonIsOccupied", "Button number:", null, false, "or", "occupiedButton"],
	["buttonOnlySetsOccupied", "Only sets:", null, false, "or", "occupiedButton"],
	["occSwitches", "Switches:", null, true],
	["noMotionOccupied", "Motion timeout:", " seconds", false, "or", "motionSensors", "occupiedButton", "occSwitches"],

	["engaged settings:"],
	[["busyCheck", "Busy check:", null, false, "or", "motionSensors"], "varBusyCheck"],
	["repeatedMotion", "Busy with motion:", null, false, "and", "motionSensors", "busyCheck"],
	["engagedButtonType", "Engaged button type:", null, false, "or", "engagedButton"],
	["engagedButton", "Button device:", null, true],
	["buttonIs", "Button number:", null, false, "or", "engagedButton"],
	["buttonOnlySetsEngaged", "Only sets:", null, false, "or", "engagedButton"],
	[["presenceAction", "Presence action:", null, false, "or", "personsPresence"], "varPresenceAction"],
	["presenceActionContinuous", "Presence continuous:", null, false, "or", "personsPresence"],
	["musicEngaged", "Engaged with music:", null, false, "or", "musicDevice"],
	["engagedSwitch", "Switches:", null, true],
	["powerValueEngaged", "Engaged power:", " watts", false, "or", "powerDevice"],
	["powerFromTimeType", "From time type:", null, false, "and", "powerDevice", "powerValueEngaged"],
	["powerFromTime", "Time from:", null, false, "and", "powerDevice", "powerValueEngaged", "powerFromTimeType"],
	["powerFromTimeOffset", "Time from offset:", null, false, "and", "powerDevice", "powerValueEngaged", "powerFromTimeType", "powerFromTime"],
	["powerToTimeType", "To time type:", null, false, "and", "powerDevice", "powerValueEngaged"],
	["powerToTime", "Time to:", null, false, "and", "powerDevice", "powerValueEngaged", "powerToTimeType"],
	["powerToTimeOffset", "Time to offset:", null, false, "and", "powerDevice", "powerValueEngaged", "powerToTimeType", "powerToTime"],
	["powerTriggerFromVacant", "From vacant:", null, false, "and", "powerDevice", "powerValueEngaged"],
	["powerStays", "Stays below:", " seconds", false, "and", "powerDevice", "powerValueEngaged"],
	["resetEngagedWithContact", "Reset engaged state:", " minutes", false, "and", "contactSensor", "powerValueEngaged"],
	["contactSensor", "Contact sensors:", null, true],
	["contactSensorOutsideDoor", "Outside door:", null, false, "or", "contactSensor"],
	["contactSensorNotTriggersEngaged", "Only sets:", null, false, "or", "contactSensor"],
	["noMotionEngaged", "Require motion:", " seconds"],
	["anotherRoomEngaged", "Switches:", null, true],
	["resetEngagedDirectly", "Reset no checking:"],
	["engagedOverrides", "Engaged overrides:", null, false],

	["checked settings:"],
	["dimTimer", "Dim timer:", " seconds"],
	["dimByLevel", "By level:", "%", false, "or", "dimTimer"],
	["dimToLevel", "To level:", "%", false, "or", "dimTimer"],
	["luxCheckingDimTo", "Below lux:", " lux", false, "and", "dimTimer", "dimToLevel", "luxSensor"],
	["notRestoreLL", "No restore:"],

	["vacant settings:"],
	["vacantButtonType", "Vacant button type:", null, false, "or", "vacantButton"],
	["vacantButton", "Button device:", null, true],
	["buttonIsVacant", "Button number:", null, false, "or", "vacantButton"],
	["vacantSwitches", "Switches:", null, true],
	["turnOffMusic", "Pause speaker:", null, false, "or", "musicDevice"],

	["asleep settings:"],
	["asleepSensor", "Asleep sensor:", null, true],
	["asleepButtonType", "Asleep button type:", null, false, "or", "asleepButton"],
	["asleepButton", "Button device:", null, true],
	["buttonIsAsleep", "Button number:", null, false, "or", "asleepButton"],
	["buttonOnlySetsAsleep", "Only sets:", null, false, "or", "asleepButton"],
	["asleepSwitch", "Switches:", null, true],
	["powerValueAsleep", "Asleep power:", " watts", false, "or", "powerDevice"],
	["powerFromTimeType", "From time type:", null, false, "and", "powerDevice", "powerValueAsleep"],
	["powerFromTime", "Time from:", null, false, "and", "powerDevice", "powerValueAsleep", "powerFromTimeType"],
	["powerFromTimeOffset", "Time from offset:", null, false, "and", "powerDevice", "powerValueAsleep", "powerFromTimeType", "powerFromTime"],
	["powerToTimeType", "To time type:", null, false, "and", "powerDevice", "powerValueAsleep"],
	["powerToTime", "Time to:", null, false, "and", "powerDevice", "powerValueAsleep", "powerToTimeType"],
	["powerToTimeOffset", "Time to offset:", null, false, "and", "powerDevice", "powerValueAsleep", "powerToTimeType", "powerToTime"],
	["powerTriggerFromVacant", "From vacant:", null, false, "and", "powerDevice", "powerValueAsleep"],
	["powerStays", "Stays below:", " seconds", false, "and", "powerDevice", "powerValueAsleep"],
	["noAsleep", "Asleep timeout:"],
	["resetAsleepDirectly", "Reset no checking:"],
	["resetAsleepWithContact", "Reset sleep state:", " minutes", false, "or", "contactSensor"],
	["asleepOverrides", "Asleep overrides:", null, false],
	["", ""],
	["nightSwitches", "Night switches:", null, true, "or", "motionSensors"],
	["nightSetLevelTo", "Set level:", "%", false, "and", "motionSensors", "nightSwitches"],
	["nightSetCT", "Set color temp:", " kelvin", false, "and", "motionSensors", "nightSwitches"],
	["nightMotionSensors", "Night motion sensor:", null, true, "and", "motionSensors", "nightSwitches"],
	["noMotionAsleep", "Motion timeout:", " seconds", false, "and", "motionSensors", "nightSwitches"],
	[["nightTurnOn", "Light on when:", null, false, "and", "motionSensors", "nightSwitches"], "varNightLightOn"],
	["nightButtonType", "Night button type:", null, false, "and", "motionSensors", "nightSwitches", "nightButton"],
	["nightButton", "Button device:", null, true, "and", "motionSensors", "nightSwitches"],
	["nightButtonIs", "Button number:", null, false, "and", "motionSensors", "nightSwitches", "nightButton"],
	[["nightButtonAction", "Button actions:", null, false, "and", "motionSensors", "nightSwitches", "nightButton"], "varNightButton"],

	["locked settings:"],
	["lockedSwitch", "Switches:", null, true],
	["lockedSwitchCmd", "When switch turns on:", null, false, "and", "lockedSwitch"],
	["powerValueLocked", "Locked power:", " watts", , false, "or", "powerDevice"],
	["powerFromTimeType", "From time type:", null, false, "and", "powerDevice", "powerValueLocked"],
	["powerFromTime", "Time from:", null, false, "and", "powerDevice", "powerValueLocked", "powerFromTimeType"],
	["powerFromTimeOffset", "Time from offset:", null, false, "and", "powerDevice", "powerValueLocked", "powerFromTimeType", "powerFromTime"],
	["powerToTimeType", "To time type:", null, false, "and", "powerDevice", "powerValueLocked"],
	["powerToTime", "Time to:", null, false, "and", "powerDevice", "powerValueLocked", "powerToTimeType"],
	["powerToTimeOffset", "Time to offset:", null, false, "and", "powerDevice", "powerValueLocked", "powerToTimeType", "powerToTime"],
	["powerTriggerFromVacant", "From vacant:", null, false, "and", "powerDevice", "powerValueLocked"],
	["powerStays", "Stays below:", " seconds", false, "and", "powerDevice", "powerValueLocked"],
	["lockedContact", "Locked contact:", null, true],
	["lockedContactCmd", "Contact closes:", null, false, "and", "lockedContact"],
	["lockedTurnOff", "Turn off switches:", null, false],
	["unLocked", "Locked timeout:", " hours", false],
	["lockedOverrides", "Locked overrides:", null, false],

	["auto level settings:"],
	["minLevel", "Min level:", "%"],
	["maxLevel", "Max level:", "%"],
	[["wakeupTime", "Wake time:", null, false, "or", "minLevel", "maxLevel"], "varDate"],
	[["sleepTime", "Sleep time:", null, false, "or", "minLevel", "maxLevel"], "varDate"],
	["fadeLevelWake", "Fade up wake time:", null, false, "and", "minLevel", "maxLevel"],
	["fadeWakeBefore", "Fade up wake before:", " hours", false, "and", "minLevel", "maxLevel", "fadeLevelWake"],
	["fadeWakeAfter", "Fade up wake after:", " hours", false, "and", "minLevel", "maxLevel", "fadeLevelWake"],
	["fadeLevelSleep", "Fade dn sleep time:", null, false, "and", "minLevel", "maxLevel"],
	["fadeSleepBefore", "Fade dn sleep before:", " hours", false, "and", "minLevel", "maxLevel", "fadeLevelSleep"],
	["fadeSleepAfter", "Fade dn wake after:", " hours", false, "and", "minLevel", "maxLevel", "fadeLevelSleep"],
	["autoColorTemperature", "Auto color temp:", null, false, "or", "minLevel", "maxLevel"],
	["minKelvin", "Min kelvin:", " kelvin", false, "or", "autoColorTemperature"],
	["maxKelvin", "Max kelvin:", " kelvin", false, "or", "autoColorTemperature"],
	["fadeCTWake", "Fade up wake time:", null, false, "and", "minKelvin", "minKelvin"],
	["fadeKWakeBefore", "Fade up wake before:", " hours", false, "and", "minKelvin", "minKelvin", "fadeCTWake"],
	["fadeKWakeAfter", "Fade up wake after:", " hours", false, "and", "minKelvin", "minKelvin", "fadeCTWake"],
	["fadeCTSleep", "Fade dn sleep time:", null, false, "and", "minKelvin", "minKelvin"],
	["fadeKSleepBefore", "Fade dn sleep before:", " hours", false, "and", "minKelvin", "minKelvin", "fadeCTSleep"],
	["fadeKSleepAfter", "Fade dn wake after:", " hours", false, "and", "minKelvin", "minKelvin", "fadeCTSleep"],

	["temperature settings:"],
	["tempSensors", "Temperature sensor:", null, true],
	[["maintainRoomTemp", "Manage temperature:", null, false, "and", "tempSensors"], "varMaintainTemp"],
	["useThermostat", "Use thermostat:", null, false, "and", "maintainRoomTemp"],
	["roomThermostat", "Room thermostat:", null, true, "and", "maintainRoomTemp", "useThermostat"],
	["thermoToTempSensor", "Temperature delta:", null, false, "and", "maintainRoomTemp", "useThermostat"],
	["roomCoolSwitch", "AC switch:", null, true, "and", "maintainRoomTemp", "!useThermostat"],
	["roomHeatSwitch", "Heater switch:", null, true, "and", "maintainRoomTemp", "!useThermostat"],
	["checkPresence", "Check presence:", null, false, "and", "maintainRoomTemp", "personsPresence"],
	["contactSensorsRT", "Contacts closed:", null, true, "and", "maintainRoomTemp", "personsPresence"],
	["thermoOverride", "Thermostat override:", null, true, "and", "maintainRoomTemp"],
	["outTempSensor", "Outdoor Temperature:", null, true],
	["autoAdjustWithOutdoor", "Adjust with outdoor:", null, false, "and", "outTempSensor"],
	["roomVents", "Room vents:", null, true, "and", "useThermostat"],
	["delayedVentOff", "Vents delayed off:", null, false, "and", "useThermostat", "roomVents"],
	["roomFanSwitch", "Fan switch:", null, true, "and", "tempSensors"],

	["holiday light settings:"],
	[["holi", "Holiday lights:"], "varHoli"],

	["rule settings:"],
	[["rule", "Rules:"], "varRule"],

	["adjacent settings:"],
	["adjRooms", "Adjacent rooms:", null, true],
	["adjRoomsMotion", "Motion check:", null, false, "and", "adjRooms"],
	["adjRoomsPathway", "Pathway lighting:", null, false, "and", "adjRooms"],

	["announcement settings:"],
	["speakerDevices", "Speakers:", null, true],
	["speechDevices", "Speech devices:", null, true],
	["musicPlayers", "Media players:", null, true],
	["listOfMQs", "Ask Alexa queues:", null, true],
	["speakerVolume", "Volume:", null, false, "or", "speakerDevices", "speechDevices", "musicPlayers", "listOfMQs"],
	["useVariableVolume", "Variable volume:", null, false, "or", "speakerDevices", "speechDevices", "musicPlayers", "listOfMQs"],
	["startHH", "From hour:", null, false, "or", "speakerDevices", "speechDevices", "musicPlayers", "listOfMQs"],
	["endHH", "To hour:", null, false, "or", "speakerDevices", "speechDevices", "musicPlayers", "listOfMQs"],
	["announceSwitches", "Speakers:", null, true],
	["startHHColor", "From hour:", null, false, "or", "announceSwitches"],
	["endHHColor", "To hour:", null, false, "or", "announceSwitches"],
	["announceDoor", "Announce door:", null, false],
	["announceDoorSpeaker", "With speaker:", null, false, "or", "announceDoor"],
	["announceDoorColor", "With color:", null, false, "or", "announceDoor"],
	["announceContact", "Announce door stays:", null, false],
	["announceContactSpeaker", "With speaker:", null, false, "or", "announceContact"],
	["announceContactColor", "With color:", null, false, "or", "announceContact"],
	["announceContactRT", "Announce window:", null, false],
	["announceContactRTSpeaker", "With speaker:", null, false, "or", "announceContactRT"],
	["announceContactRTColor", "With color:", null, false, "or", "announceContactRT"],
	["announceInModes", "In modes:", null, true, "or",  "or", "speakerDevices", "speechDevices", "musicPlayers", "listOfMQs", "announceSwitches"],

	["general settings:"],
	["awayModes", "Away modes:", null, true],
	["pauseModes", "Pause modes:", null, true],
	["dayOfWeek", "Days of week:", null, false],
	["useCelsius", "Use celsius:", null, false],
	["allSwitchesOff", "All switches off:", null, false],
	["dimOver", "Dim switches to off:", " seconds", false],
	["onlyOnStateChange", "Execute on state:", null, false],
	["butNotInStates", "But not in states:", null, false],
	["roomDeviceSwitchOn", "Which room state:", null, false],
	["iconURL", "Icon URL:", null, false]
]

@Field final String _RIimage = 'https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomOccupancySettings.png'
@Field final String _OPimage = 'https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsOnePage.png'
@Field final String _REimage = 'https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsEasy.png'
@Field final String _HAimage = 'https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsHideAdvanced.png'
@Field final String _ODimage = 'https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsOtherDevices.png'
@Field final String _OCimage = 'https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsOccupied.png'
@Field final String _ENimage = 'https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsEngaged.png'
@Field final String _CHimage = 'https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsChecking.png'
@Field final String _VAimage = 'https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsVacant.png'
@Field final String _ASimage = 'https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsAsleep.png'
@Field final String _LOimage = 'https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsLocked.png'
@Field final String _ALimage = 'https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsLightLevel.png'
@Field final String _HLimage = 'https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsHolidayLights3.png'
@Field final String _RTimage = 'https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsTemperature.png'
@Field final String _RHimage = 'https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsHumidity.png'
@Field final String _RUimage = 'https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsRules.png'
@Field final String _ARimage = 'https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsAdjacent5.png'
@Field final String _GEimage = 'https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsSettings.png'
@Field final String _ANimage = 'https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsAnnouncement.png'
@Field final String _VIimage = 'https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsViewAll.png'
@Field final String _GHimage = 'https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomOccupancySettings.png'
@Field final String _gitREADME = 'https://github.com/adey/bangali/blob/master/README.md'
