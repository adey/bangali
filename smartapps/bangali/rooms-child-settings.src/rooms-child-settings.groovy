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
*
*	convertRGBToHueSaturation(...) adpated from code by ady624 for webCoRE. original code can be found at:
*		https://github.com/ady624/webCoRE/blob/master/smartapps/ady624/webcore-piston.src/webcore-piston.groovy
*	colorsRGB array color name and RGB values from code by ady624 for webCoRE.
*
*  Name: Rooms Child Settings
*   Source: https://github.com/adey/bangali/blob/master/smartapps/bangali/rooms-child-settings.src/rooms-child-settings.groovy
*
***********************************************************************************************************************/

public static String version()		{  return "v0.99.5"  }

import groovy.transform.Field

@Field final String lastMotionActive   = '1'
@Field final String lastMotionInactive = '2'

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

@Field final String _SmartThings = 'ST'
@Field final String _Hubitat     = 'HU'

@Field final String padChar = '･'

@Field final String pushAButton = 'pushableButton'
@Field final String holdAButton = 'holdableButton'
@Field final String doubleTapAButton = 'doubleTapableButton'

@Field final Map    heCapToAttrMap = [(pushAButton):'pushed', (holdAButton):'held', (doubleTapAButton):'doubleTapped']

@Field final int    maxRules    = 10
@Field final int    maxHolis    = 10
@Field final int    maxButtons  = 5

@Field final String _timeSunrise = '1'
@Field final String _timeSunset	 = '2'
@Field final String _timeTime	 = '3'

@Field final String _ERule      = 'e'
@Field final String _TRule      = 't'
@Field final String _HRule      = 'h'

definition	(
	name: "rooms child settings",
	namespace: "bangali",
	parent: "bangali:rooms manager",
	author: "bangali",
	description: "DO NOT INSTALL DIRECTLY. Rooms Manager app uses this to for the view all settings screen.",
	category: "My Apps",
	iconUrl: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomOccupancy.png",
	iconX2Url: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomOccupancy@2x.png",
	iconX3Url: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomOccupancy@3x.png"
)

preferences {
	page(name: "mainPage", content: "mainPage")
}

def mainPage()	{
	dynamicPage(name: "mainPage", title: "No settings", install: false, uninstall: false)	{
		section() {
		}
	}
}

def installed()		{  initialize()  }

def updated()		{  initialize()  }

def initialize()	{  unsubscribe(); unschedule()  }

def allSettings(setings, allRules, childCreated, onlyHas, anonIt)	{
//    ifDebug("pageAllSettings")
	def hT = getHubType()
	def pVer = parent.version()
	def ver = version()
	if (pVer != ver)		{
		def errMsg = "Rooms Child Settings app verion does not match Rooms Manager app version. Parent version is $pVer and this app version is ${ver}. Please update app code and save${(hT == _SmartThings ? '/publish' : '')} before trying again."
		log.error errMsg
		return errMsg
	}
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
			if (para)	{
//log.debug para
				para = para + (hT == _Hubitat ? '</pre>' : '\n');
			}
			para = para + (hT == _Hubitat ? '\n<pre>' : '\n') + varAllS[0].toUpperCase()
		}
		else    {
			def var = (setings[varAllS[0]] || varTranslate ? (varTranslate ? "$varTranslate"(setings[varAllS[0]], setings, allRules, childCreated, onlyHas, anonIt) : setings[varAllS[0]]) : '')
			if (var && var.toString().isInteger())	{
				def vN = var.toInteger()
				if (vN > 0)		var = String.format("%,d", vN);
			}
			if (vitCount == 2)	{
				if (!onlyHas || var)
					para = para + "\n${(hT == _Hubitat ? '&ensp;' : '')}${var ? (setings[varAllS[0]] ? padViewString(varAllS[1]) : varAllS[1]) : varAllS[1]}${var ?: ''}"
			}
			else if (vitCount == 3)	{
				if (!onlyHas || var)
					para = para + "\n${(hT == _Hubitat ? '&ensp;' : '')}${var ? padViewString(varAllS[1]) : varAllS[1]}${var ?: ''}" + (var && varAllS[2] ? varAllS[2] : '')
			}
			else if (vitCount == 4)	{
				sum = varAllS[3]
				s = (sum && anonIt ? (var instanceof List ? var.size() : (var ? 1 : '')) : (var ?: ''))
//                        ifDebug(">>>>> 3 >>> $itCount | $varAllS | ${settings[varAllS[0]]} | $var | $sum | $check | ${['or', 'and'].contains(check)} | $varTranslate | $s")
				if (!onlyHas || s)
					para = para + "\n${(hT == _Hubitat ? '&ensp;' : '')}${s ? padViewString(varAllS[1]) : varAllS[1]}$s" + (s && varAllS[2] ? varAllS[2] : '')
			}
			else if (vitCount > 4)	{
				sum = varAllS[3]
				def check = varAllS[4]?.trim().toLowerCase()
				s = (sum && anonIt ? (var instanceof List ? var.size() : (var ? 1 : '')) : (var ?: ''))
//                        ifDebug(">>>>> 4 >>> $vitCount | $check | ${['or', 'and'].contains(check)} | $s")
				if (vitCount > 5 && ['or', 'and'].contains(check))	{
					def pas = (check == 'and' ? true : false)
					for (def i = 5; i < vitCount; i++)	{
						def v = setings[varAllS[i]]
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
					para = para + "\n${(hT == _Hubitat ? '&ensp;' : '')}${s ? padViewString(varAllS[1]) : varAllS[1]}$s" + (s && varAllS[2] ? varAllS[2] : '')
			}
		}
	}
	if (para)	para = para + (hT == _Hubitat ? '</pre>' : '\n');
//		}
//	}
	return para
}

private getHubType()	{
	if (!state.hubId)	state.hubId = location.hubs[0].id.toString()
	if (state.hubId.length() > 5)	return _SmartThings;
	else							return _Hubitat;
}

private padViewString(str, chars = 20)	{
	def padSz = ((chars ?: 20) * (getHubType() == _Hubitat ? 1.5 : 1)) as Integer
	def sz = (str ? str.size() : 0)
	def s = (sz > 0 ? str.substring(0, (sz > padSz ? (padSz - 1) : (sz - 1))) : '')
	s = s + ((' ' + padChar + ' ') * ((padSz - s.size() + 5f) / 3).round(0))
	return s.substring(0, padSz + 3) + ' '
}

private varRoomChild(x, setings, allRules, childCreated, onlyHas, anonIt)		{
	return (childCreated ? "Child device OK" : "Child device NOT created")
}

private varRoomButtons(x, setings, allRules, childCreated, onlyHas, anonIt)		{
	def hT = getHubType()
	def bS = ''
	def bN = 'roomButton'
//	def bT = "Room buttons:"
	def bC = 0
	for (def i = 1; i <= maxButtons; i++)	{
		if (setings["$bN$i"] || setings["${bN}Number$i"])
			if (anonIt)		bC = bC + 1;
			else			bS = bS + (bS ? "\n${(hT == _Hubitat ? '&ensp;' : '')}" + padViewString('') : '') +
								"${setings["$bN$i"]} : ${(hT == _Hubitat ? heCapToAttrMap[setings["roomButtonType$i"]] + ' : ' : '')}${setings["${bN}Number$i"]}";
	}
	if (anonIt && bC > 0)		bS = "$bC buttons";
	return bS
}

private varWhichNoMotion(wM, setings, allRules, childCreated, onlyHas, anonIt)		{
	return (setings.motionSensors ? (wM == lastMotionActive ? "Last Motion Active" : "Last Motion Inactive") : '')
}

private varBusyCheck(bC, setings, allRules, childCreated, onlyHas, anonIt)		{
	def bCS
	switch(bC)	{
		case lightTraffic:   bCS = 'Light traffic';      break
		case mediumTraffic:  bCS = 'Medium traffic';     break
		case heavyTraffic:   bCS = 'Heavy traffic';      break
		default:             bCS = 'No traffic check';   break
	}
	return bCS
}

private varDate(dT, setings, allRules, childCreated, onlyHas, anonIt)		{
	def dateX = /^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3}-[0-9]{4}\z/
	def matcher
	return ((matcher = dT =~ dateX) ? dT.substring(11,16) : dT)
}

private varHoli(x, setings, allRules, childCreated, onlyHas, anonIt)		{
	def hT = getHubType()
	def hS = ''
	for (def i = 1; i <= maxHolis; i++)	{
		if (setings["holiName$i"] || setings["holiColorString$i"])
			hS = hS + "\n${(hT == _Hubitat ? '&emsp;&emsp;' : '')}" + padViewString(setings["holiName$i"].trim() + ":", 15) + setings["holiColorString$i"]
	}
	return holiString
}

def ruleDelete(allRules)	{
	def rulesList = []
	for (def thisRule : allRules)	{
		if (!thisRule)		continue;
		rulesList << [(thisRule.ruleNo):"${varRuleString(thisRule, false)}"]
	}
	return rulesList
}

private varRule(x, setings, allRules, childCreated, onlyHas, anonIt)		{
	def hT = getHubType()
	def rS = ''
	for (def thisRule : allRules)		rS = rS + "\n${(hT == _Hubitat ? '&emsp;&emsp;' : '')}" + varRuleString(thisRule, anonIt);
	return rS
}

private varRuleString(thisRule, anonIt)	{
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
		rD = "$rD From＝$thisRule.fromDate"
		rD = "$rD To＝$thisRule.toDate"
	}
	if (thisRule.fromTimeType && thisRule.toTimeType)	{
		def rFT = (thisRule.fromTime ? format24hrTime(timeToday(thisRule.fromTime, location.timeZone)) : '')
		def rTT = (thisRule.toTime ? format24hrTime(timeToday(thisRule.toTime, location.timeZone)) : '')
		rD = (thisRule.fromTimeType == _timeTime ? "$rD From＝$rFT" :
					(thisRule.fromTimeType == _timeSunrise ? "$rD From＝Sunrise" + varRuleTimeOffset(thisRule.fromTimeOffset) : "$rD From＝Sunset" + varRuleTimeOffset(thisRule.fromTimeOffset)))
		rD = (thisRule.toTimeType == _timeTime ? "$rD To＝$rTT" :
					(thisRule.toTimeType == _timeSunrise ? "$rD To＝Sunrise" + varRuleTimeOffset(thisRule.toTimeOffset) : "$rD To＝Sunset" + varRuleTimeOffset(thisRule.toTimeOffset)))
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

private varRuleTimeOffset(offset)		{  return String.format( " %+d", offset.toInteger())  }

private varNightLightOn(nLO, setings, allRules, childCreated, onlyHas, anonIt)		{
	if (!nLO)	return ''
	def hT = getHubType()
	def nLOS = ''
	if (nLO.contains('1'))		nLOS = "With motion"
	if (nLO.contains('2'))		nLOS = (nLOS ? nLOS + "\n${(hT == _Hubitat ? '&ensp;' : '')}" + padViewString('') : '') + "Changes to asleep"
	if (nLO.contains('3'))		nLOS = (nLOS ? nLOS + "\n${(hT == _Hubitat ? '&ensp;' : '')}" + padViewString('') : '') + "Changes from asleep"
	return nLOS + ' '
}

private varNightButton(nBA, setings, allRules, childCreated, onlyHas, anonIt)		{  return (nBA ? [1:"Turn on",2:"Turn off",3:"Toggle"][nBA] : '')  }

private varMaintainTemp(mRT, setings, allRules, childCreated, onlyHas, anonIt)		{
	return (mRT ? [1:"Cool room",2:"Heat room",3:"Cool & heat room",4:"No"][mRT] : '')
}

private varPresenceAction(pAct, setings, allRules, childCreated, onlyHas, anonIt)		{
	return (pAct ? ['1':"ENGAGED on Arrival",'2':"VACANT on Departure",'3':"Both actions",'4':"Neither action"][pAct] : '')
}

private format24hrTime(timeToFormat = new Date(now()), format = "HH:mm")	{
	return timeToFormat.format("HH:mm", location.timeZone)
}

@Field final List    allSettingsVar = [
	["Room occupancy device:"],
	[["room child device", "Room child device:", null, false], "varRoomChild"],
	["room sensor settings:"],
	["motionSensors", "Motion sensors:", null, true],
	["triggerMotionSensors", "Trigger motion:", null, true, "or", "motionSensors"],
	[["whichNoMotion", "Which no motion:", null, false, "or", "motionSensors"], "varWhichNoMotion"],
	["accelSensors", "Acceleration sensors:", null, true],
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
//	["musicEngaged", "Engaged with music:", null, false, "or", "musicDevice"],
	["engagedSwitch", "Switches:", null, true],
	["powerValueEngaged", "Engaged power:", " watts", false, "or", "powerDevice"],
	["powerFromTimeType", "From time type:", null, false, "and", "powerDevice", "powerValueEngaged"],
	["powerFromTime", "Time from:", null, false, "and", "powerDevice", "powerValueEngaged", "powerFromTimeType"],
	["powerFromTimeOffset", "Time from offset:", null, false, "and", "powerDevice", "powerValueEngaged", "powerFromTimeType", "powerFromTime"],
	["powerToTimeType", "To time type:", null, false, "and", "powerDevice", "powerValueEngaged"],
	["powerToTime", "Time to:", null, false, "and", "powerDevice", "powerValueEngaged", "powerToTimeType"],
	["powerToTimeOffset", "Time to offset:", null, false, "and", "powerDevice", "powerValueEngaged", "powerToTimeType", "powerToTime"],
	["powerTriggerFromVacant", "From vacant:", null, false, "and", "powerDevice", "powerValueEngaged"],
	["powerTriggerFromOccupied", "From occupied:", null, false, "and", "powerDevice", "powerValueEngaged"],
	["powerStays", "Stays below:", " seconds", false, "and", "powerDevice", "powerValueEngaged"],
	["resetEngagedWithContact", "Reset engaged state:", " minutes", false, "and", "contactSensor", "powerValueEngaged"],
	["contactSensor", "Contact sensors:", null, true],
	["contactSensorOutsideDoor", "Outside door:", null, false, "or", "contactSensor"],
	["contactSensorNotTriggersEngaged", "Only sets:", null, false, "or", "contactSensor"],
	["noMotionEngaged", "Require motion:", " seconds"],
	["anotherRoomEngaged", "Switches:", null, true],
	["resetEngagedDirectly", "Reset no checking:"],
	["engagedOverrides", "Engaged overrides:", null, false],

	["checking settings:"],
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
	[["asleepFromTime", "Asleep from time:", null, false], "varDate"],
	[["asleepToTime", "Asleep to time:", null, false], "varDate"],
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
	["asleepMode", "Asleep mode:", null, false],
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
