/***********************************************************************************************************************
*
*  A SmartThings device handler to allow handling rooms as devices which have states for occupancy.
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
*  Name: Rooms Occupancy
*  Source: https://github.com/adey/bangali/blob/master/devicetypes/bangali/rooms-occupancy.src/rooms-occupancy.groovy
*
***********************************************************************************************************************/

public static String version()		{  return "v0.95.0"  }
private static boolean isDebug()	{  return false  }

final String _SmartThings()	{ return 'ST' }
final String _Hubitat()		{ return 'HU' }

metadata {
	definition (
		name: "rooms occupancy",
		namespace: "bangali",
		author: "bangali")		{
		capability "Actuator"
		capability "PushableButton"		// hubitat changed `Button` to `PushableButton`  2018-04-20
		capability "Sensor"
		capability "Switch"
		capability "Beacon"
		capability "Health Check"
		attribute "occupancy", "enum", ['occupied', 'checking', 'vacant', 'locked', 'reserved', 'kaput', 'donotdisturb', 'asleep', 'engaged']
// for hubitat uncomment the next few lines ONLY if you want to use the icons on dashboard
//		attribute "occupancyIconS", "String"
//		attribute "occupancyIconM", "String"
//		attribute "occupancyIconL", "String"
//		attribute "occupancyIconXL", "String"
//		attribute "occupancyIconXXL", "String"
		attribute "occupancyIconURL", "String"
		attribute "alarmEnabled", "boolean"
		attribute "alarmTime", "String"
		attribute "alarmDayOfWeek", "String"
		attribute "alarmRepeat", "number"
		attribute "alarmSound", "String"
		attribute "countdown", "String"
		command "occupied"
		command "checking"
		command "vacant"
		command "locked"
		command "reserved"
		command "kaput"
		command "donotdisturb"
		command "asleep"
		command "engaged"
		command "push"		// for use with hubitat useful with dashbooard 2018-04-24
		command "turnOnAndOffSwitches"
		command "turnSwitchesAllOn"
		command "turnSwitchesAllOff"
		command "turnNightSwitchesAllOn"
		command "turnNightSwitchesAllOff"
		command "alarmOffAction"
	}

	simulator	{
	}

	preferences		{
		section("Alarm settings:")		{
			input "alarmDisabled", "bool", title: "Disable alarm?", required: true, defaultValue: true
			input "alarmTime", "time", title: "Alarm Time?", required: false
			input "alarmVolume", "number", title: "Volume?", description: "0-100%", required: (alarmTime ? true : false), range: "1..100"
			input "alarmSound", "enum", title:"Sound?", required: (alarmTime ? true : false), multiple: false, defaultValue: null,
								options: ["0":"Bell 1", "1":"Bell 2", "2":"Dogs Barking", "3":"Fire Alarm", "4":"Piano", "5":"Lightsaber"]
			input "alarmRepeat", "number", title: "Repeat?", description: "1-999", required: (alarmTime ? true : false), range: "1..999"
			input "alarmDayOfWeek", "enum", title: "Which days of the week?", required: false, multiple: false, defaultValue: null,
								options: ["ADW0":"All Days of Week","ADW8":"Monday to Friday","ADW9":"Saturday & Sunday","ADW2":"Monday", \
										  "ADW3":"Tuesday","ADW4":"Wednesday","ADW5":"Thursday","ADW6":"Friday","ADW7":"Saturday","ADW1":"Sunday"]
		}
	}
}

def parse(String description)	{  ifDebug("parse: $description")  }

def installed()		{  initialize()  }

def updated()		{  initialize()  }

def	initialize()	{
	unschedule()
	sendEvent(name: "numberOfButtons", value: 9, descriptionText: "set number of buttons to 9.", isStateChange: true, displayed: true)
	state.timer = 0
	setupAlarmC()
	sendEvent(name: "countdown", value: '0s', descriptionText: "countdown timer: 0s", isStateChange: true, displayed: true)
	if (getHubType() == _SmartThings)		{
		sendEvent(name: "DeviceWatch-DeviceStatus", value: "online")
		sendEvent(name: "healthStatus", value: "online")
		sendEvent(name: "DeviceWatch-Enroll", value: [protocol: "cloud", scheme:"untracked"].encodeAsJson(), displayed: false)
	}
}

def getHubType()	{
	if (!state.hubId)	state.hubId = location.hubs[0].id.toString()
	return (state.hubId.length() > 5 ? _SmartThings() : _Hubitat())
}

def setupAlarmC()	{
	if (parent)		parent.setupAlarmP(alarmDisabled, alarmTime, alarmVolume, alarmSound, alarmRepeat, alarmDayOfWeek);
	if (alarmDayOfWeek != 'ADW0')	{
		state.alarmDayOfWeek = []
		switch(alarmDayOfWeek)	{
			case 'ADW1':	state.alarmDayOfWeek << 'Mon';		break
			case 'ADW2':	state.alarmDayOfWeek << 'Tue';		break
			case 'ADW3':	state.alarmDayOfWeek << 'Wed';		break
			case 'ADW4':	state.alarmDayOfWeek << 'Thu';		break
			case 'ADW5':	state.alarmDayOfWeek << 'Fri';		break
			case 'ADW6':	state.alarmDayOfWeek << 'Sat';		break
			case 'ADW7':	state.alarmDayOfWeek << 'Sun';		break
			case 'ADW8':   	state.alarmDayOfWeek = state.alarmDayOfWeek + ['Mon','Tue','Wed','Thu','Fri'];	break
			case 'ADW9':   	state.alarmDayOfWeek = state.alarmDayOfWeek + ['Sat','Sun'];					break
			default:  		state.alarmDayOfWeek = null;		break
		}
	}
	else
		state.alarmDayOfWeek = ''
	state.alarmSound = (alarmSound ? ["Bell 1", "Bell 2", "Dogs Barking", "Fire Alarm", "Piano", "Lightsaber"][alarmSound as Integer] : '')
	sendEvent(name: "alarmEnabled", value: ((alarmDisabled || !alarmTime) ? 'No' : 'Yes'), descriptionText: "alarm enabled is ${(!alarmDisabled)}", isStateChange: true, displayed: true)
	sendEvent(name: "alarmTime", value: "${(alarmTime ? timeToday(alarmTime, location.timeZone).format("HH:mm", location.timeZone) : '')}", descriptionText: "alarm time is ${alarmTime}", isStateChange: true, displayed: true)
	sendEvent(name: "alarmDayOfWeek", value: "$state.alarmDayOfWeek", descriptionText: "alarm days of week is $state.alarmDayOfWeek", isStateChange: true, displayed: true)
	sendEvent(name: "alarmSound", value: "$state.alarmSound", descriptionText: "alarm sound is $state.alarmSound", isStateChange: true, displayed: true)
	sendEvent(name: "alarmRepeat", value: alarmRepeat, descriptionText: "alarm sounds is repeated $alarmRepeat times", isStateChange: true, displayed: true)
}

def on()	{
	if (!state.onState)		state.onState = parent?.roomDeviceSwitchOnP().toString();
	switch(state.onState ?: 'occupied')		{
		case 'occupied':	occupied();		break
		case 'engaged':		engaged();		break
		case 'locked':		locked();		break
		case 'asleep':		asleep();		break
		default:							break
	}
	sendEvent(name: "switch", value: "on", descriptionText: "$device.displayName is on", isStateChange: true, displayed: true)
}

def setOnStateC(e)		{  state.onState = (e ? e.toString() : 'occupied')  }

def	off()		{
	vacant()
	sendEvent(name: "switch", value: "off", descriptionText: "$device.displayName is off", isStateChange: true, displayed: true)
}

def push(buton)		{
	ifDebug("$buton")
	def hT = getHubType()
	switch(buton)		{
		case 1:		occupied();		break
		case 3:		vacant();		break
		case 4:		locked();		break
		case 8:		asleep();		break
		case 9:		engaged();		break
		default:
			if (hT != _Hubitat())
				sendEvent(name: "button", value: "pushed", data: [buttonNumber: "$buton"], descriptionText: "$device.displayName button $buton was pushed", isStateChange: true, displayed: true)
			else
				sendEvent(name: "pushableButton", value: buton, descriptionText: "$device.displayName button $buton was pushed", isStateChange: true, displayed: true)
			break
	}
}

def lock()		{  locked() }

def unlock()	{  vacant()  }

def occupied(handleSwitches = true)			{ runIn(0, stateUpdate, [data: [newState:'occupied', handleSwitches:handleSwitches]]) }

def checking(handleSwitches = true)			{ runIn(0, stateUpdate, [data: [newState:'checking', handleSwitches:handleSwitches]]) }

def vacant(handleSwitches = true)			{ runIn(0, stateUpdate, [data: [newState:'vacant', handleSwitches:handleSwitches]]) }

def donotdisturb(handleSwitches = true)		{ runIn(0, stateUpdate, [data: [newState:'donotdisturb', handleSwitches:handleSwitches]]) }

def reserved(handleSwitches = true)			{ runIn(0, stateUpdate, [data: [newState:'reserved', handleSwitches:handleSwitches]]) }

def asleep(handleSwitches = true)			{ runIn(0, stateUpdate, [data: [newState:'asleep', handleSwitches:handleSwitches]]) }

def locked(handleSwitches = true)			{ runIn(0, stateUpdate, [data: [newState:'locked', handleSwitches:handleSwitches]]) }

def engaged(handleSwitches = true)			{ runIn(0, stateUpdate, [data: [newState:'engaged', handleSwitches:handleSwitches]]) }

def kaput(handleSwitches = true)			{ runIn(0, stateUpdate, [data: [newState:'kaput', handleSwitches:handleSwitches]]) }

def	stateUpdate(data)		{
	if (!data)		return;
	def newState = data.newState
	def handleSwitches = data.handleSwitches
	if (state.oldState != newState)		{
        if (handleSwitches && parent)
			setupTimer((int) (parent.handleSwitches(state.oldState, newState, true) ?: 0))
		updateOccupancy(state.oldState, newState)
		state.oldState = newState
	}
	resetTile(newState)
}

def updateOccupancy(oldOcc, newOcc) 	{
	newOcc = newOcc?.toLowerCase()
	def hT = getHubType()
	def buttonMap = ['occupied':1, 'locked':4, 'vacant':3, 'reserved':5, 'checking':2, 'kaput':6, 'donotdisturb':7, 'asleep':8, 'engaged':9]
	if (!newOcc || !(buttonMap.containsKey(newOcc)))	{
		ifDebug("Missing or invalid parameter room occupancy: $newOcc")
		return
	}
	sendEvent(name: "occupancy", value: newOcc, descriptionText: "$device.displayName changed to $newOcc", isStateChange: true, displayed: true)
	if (hT == _Hubitat())		{
		def img = "https://cdn.rawgit.com/adey/bangali/master/resources/icons/rooms${newOcc?.capitalize()}State.png"
		sendEvent(name: "occupancyIconS", value: "<img src=$img height=25 width=25>", descriptionText: "$device.displayName $newOcc icon small", isStateChange: true, displayed: true)
		sendEvent(name: "occupancyIconM", value: "<img src=$img height=50 width=50>", descriptionText: "$device.displayName $newOcc icon medium", isStateChange: true, displayed: true)
		sendEvent(name: "occupancyIconL", value: "<img src=$img height=75 width=75>", descriptionText: "$device.displayName $newOcc icon large", isStateChange: true, displayed: true)
		sendEvent(name: "occupancyIconXL", value: "<img src=$img height=100 width=100>", descriptionText: "$device.displayName $newOcc icon extra large", isStateChange: true, displayed: true)
		sendEvent(name: "occupancyIconXXL", value: "<img src=$img height=150 width=150>", descriptionText: "$device.displayName $newOcc icon extra extra large", isStateChange: true, displayed: true)
		sendEvent(name: "occupancyIconURL", value: img, descriptionText: "$device.displayName $newOcc icon URL", isStateChange: true, displayed: true)
	}
	def button = buttonMap[newOcc]
	if (hT == _SmartThings())
		sendEvent(name: "button", value: "pushed", data: [buttonNumber: button], descriptionText: "$device.displayName button $button was pushed.", isStateChange: true)
	else
		sendEvent(name:"pushed", value:button, descriptionText: "$device.displayName button $button was pushed.", isStateChange: true)

	updateRoomStatusMsg()
}

def alarmOn()	{
	sendEvent(name: "occupancy", value: 'alarm', descriptionText: "$device.displayName alarm is on", isStateChange: true, displayed: true)
	runIn(2, alarmOff)
}

def alarmOff(endLoop = false)	{
	if (device.currentValue('occupancy') == 'alarm' || endLoop)
		sendEvent(name: "occupancy", value: "$state.oldState", descriptionText: "$device.displayName alarm is off", isStateChange: true, displayed: true)
	(endLoop ? unschedule() : runIn(1, alarmOn))
}

def alarmOffAction()	{
	ifDebug("alarmOffAction")
	unschedule()
	if (parent)		parent.ringAlarm(true);
	alarmOff(true)
}

private updateRoomStatusMsg()		{
	def formatter = new java.text.SimpleDateFormat("EEE, MMM d yyyy @ h:mm:ss a z")
	formatter.setTimeZone(location.timeZone)
	state.statusMsg = formatter.format(now())
	sendEvent(name: "status", value: state.statusMsg, isStateChange: true, displayed: false)
}

private	resetTile(occupancy)	{
	sendEvent(name: occupancy, value: occupancy, descriptionText: "$device.displayName reset tile $occupancy", isStateChange: true, displayed: false)
}

def turnSwitchesAllOn()		{
	if (parent)		{
		parent.turnSwitchesAllOnOrOff(true)
		if (getHubType() != _Hubitat())		updateSwitchInd(1);
	}
}

def turnSwitchesAllOff()		{
	if (parent)		{
		parent.turnSwitchesAllOnOrOff(false)
		if (getHubType() != _Hubitat())		updateSwitchInd(0);
	}
}

def turnNightSwitchesAllOn()	{
 	ifDebug("turnNightSwitchesAllOn")
	if (parent)	{
		parent.dimNightLights()
		if (getHubType() != _Hubitat())		updateNSwitchInd(1)
	}
}

def turnNightSwitchesAllOff()	{
	ifDebug("turnNightSwitchesAllOff")
	if (parent)		{
		parent.nightSwitchesOff()
		if (getHubType() != _Hubitat())		updateNSwitchInd(0)
	}
}

def	turnOnAndOffSwitches()	{
	if (parent)		parent.switchesOnOrOff();
	setupTimer(-1)
}

def setupTimer(int timer)	{
	if (timer != -1)	state.timerLeft = timer;
	timerNext()
}

def timerNext()		{
	int timerUpdate = (state.timerLeft > 60 ? 60 : (state.timerLeft < 5 ? state.timerLeft : 5))
	def timerInd = (state.timerLeft > 3600 ? (state.timerLeft / 3600f).round(1) + 'h' : (state.timerLeft > 60 ? (state.timerLeft / 60f).round(1) + 'm' : state.timerLeft + 's')).replace(".0","")
	if (getHubType() != _Hubitat())
		sendEvent(name: "timer", value: (timerInd ?: '--'), isStateChange: true, displayed: false)
	else
		sendEvent(name: "countdown", value: timerInd, descriptionText: "countdown timer: $timerInd", isStateChange: true, displayed: true)
	state.timerLeft = state.timerLeft - timerUpdate
	(state.timerLeft > 0 ? runIn(timerUpdate, timerNext) : unschedule('timerNext'))
}

private ifDebug(msg = null, level = null)	{  if (msg && (isDebug() || level == 'error'))	log."${level ?: 'debug'}" " $device.displayName device: " + msg  }
