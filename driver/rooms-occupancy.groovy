/***********************************************************************************************************************
*
*  A Hubitat driver to allow handling rooms as devices which have states for occupancy.
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
*  Source: https://github.com/adey/bangali/blob/master/driver/rooms-occupancy.groovy
*
***********************************************************************************************************************/

public static String version()      {  return "v0.80.0"  }
private static boolean isDebug()    {  return false  }

import groovy.transform.Field

@Field final String _SmartThings = 'ST'
@Field final String _Hubitat     = 'HU'

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
		command "updateOccupancy", ["string"]
	}

	simulator	{
	}

	preferences		{
		section("Alarm settings:")		{
			input "alarmDisabled", "bool", title: "Disable alarm?", required: true, multiple: false, defaultValue: true
			input "alarmTime", "time", title: "Alarm Time?", required: false, multiple: false
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
	state
	sendEvent(name: "numberOfButtons", value: 9, descriptionText: "set number of buttons to 9.", isStateChange: true, displayed: true)
	state.timer = 0
	setupAlarmC()
	sendEvent(name: "countdown", value: '0s', descriptionText: "countdown timer: 0s", isStateChange: true, displayed: true)
}

def getHubType()        {
    if (!state.hubId)   state.hubId = location.hubs[0].id.toString()
    if (state.hubId.length() > 5)   return _SmartThings;
    else                            return _Hubitat;
}

def setupAlarmC()	{
	if (parent)		parent.setupAlarmP(alarmDisabled, alarmTime, alarmVolume, alarmSound, alarmRepeat, alarmDayOfWeek);
	if (alarmDayOfWeek != 'ADW0')      {
        state.alarmDayOfWeek = []
        switch(alarmDayOfWeek)       {
            case 'ADW1':	state.alarmDayOfWeek << 'Mon';		break
			case 'ADW2':	state.alarmDayOfWeek << 'Tue';		break
			case 'ADW3':	state.alarmDayOfWeek << 'Wed';		break
			case 'ADW4':	state.alarmDayOfWeek << 'Thu';		break
			case 'ADW5':	state.alarmDayOfWeek << 'Fri';		break
			case 'ADW6':	state.alarmDayOfWeek << 'Sat';		break
			case 'ADW7':	state.alarmDayOfWeek << 'Sun';		break
            case 'ADW8':   	['Mon','Tue','Wed','Thu','Fri'].each		{ state.alarmDayOfWeek << it };    break
            case 'ADW9':   	['Sat','Sun'].each							{ state.alarmDayOfWeek << it };    break
            default:  		state.alarmDayOfWeek = null;		break
        }
    }
    else
        state.alarmDayOfWeek = ''
	if (alarmSound)
		state.alarmSound = ["Bell 1", "Bell 2", "Dogs Barking", "Fire Alarm", "Piano", "Lightsaber"][alarmSound as Integer]
	else
		state.alarmSound = ''
	sendEvent(name: "alarmEnabled", value: ((alarmDisabled || !alarmTime) ? 'No' : 'Yes'), descriptionText: "alarm enabled is ${(!alarmDisabled)}", isStateChange: true, displayed: true)
	sendEvent(name: "alarmTime", value: "${(alarmTime ? timeToday(alarmTime, location.timeZone).format("HH:mm", location.timeZone) : '')}", descriptionText: "alarm time is ${alarmTime}", isStateChange: true, displayed: true)
	sendEvent(name: "alarmDayOfWeek", value: "$state.alarmDayOfWeek", descriptionText: "alarm days of week is $state.alarmDayOfWeek", isStateChange: true, displayed: true)
	sendEvent(name: "alarmSound", value: "$state.alarmSound", descriptionText: "alarm sound is $state.alarmSound", isStateChange: true, displayed: true)
	sendEvent(name: "alarmRepeat", value: alarmRepeat, descriptionText: "alarm sounds is repeated $alarmRepeat times", isStateChange: true, displayed: true)
}

def on()	{
	def toState = parent?.roomDeviceSwitchOnP()
	toState = (toState ? toState as String : 'occupied')
//	ifDebug("on: $toState")
	switch(toState)		{
		case 'occupied':	occupied();		break
		case 'engaged':		engaged();		break
		case 'locked':		locked();		break
		case 'asleep':		asleep();		break
		default:							break
	}
	sendEvent(name: "switch", value: "on", descriptionText: "$device.displayName is on", isStateChange: true, displayed: true)
}

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
			if (hT != _Hubitat)
				sendEvent(name: "button", value: "pushed", data: [buttonNumber: "$buton"], descriptionText: "$device.displayName button $buton was pushed", isStateChange: true, displayed: true)
			else
				sendEvent(name: "pushableButton", value: buton, descriptionText: "$device.displayName button $buton was pushed", isStateChange: true, displayed: true)
			break
	}
}

def lock()		{  locked() }

def unlock()	{  vacant()  }

def occupied()	{	stateUpdate('occupied')		}

def checking()	{	stateUpdate('checking')		}

def vacant()	{	stateUpdate('vacant')		}

def donotdisturb()	{	stateUpdate('donotdisturb')		}

def reserved()	{	stateUpdate('reserved')		}

def asleep()	{	stateUpdate('asleep')		}

def locked()	{	stateUpdate('locked')		}

def engaged()	{	stateUpdate('engaged')		}

def kaput()		{	stateUpdate('kaput')		}

private	stateUpdate(newState)		{
//	def oldState = device.currentValue('occupancy')
	def oldState = state.oldState
	state.oldState = newState
	if (oldState != newState)	{
		updateOccupancy(newState)
        if (parent)		{
			parent.runInHandleSwitches(oldState, newState);
//			runIn(0, parent.runInHandleSwitches, data: [oldState: oldState, newState: newState])
		}
	}
	resetTile(newState)
}

def updateOccupancy(occupancy = null) 	{
	occupancy = occupancy?.toLowerCase()
	def hT = getHubType()
	def buttonMap = ['occupied':1, 'locked':4, 'vacant':3, 'reserved':5, 'checking':2, 'kaput':6, 'donotdisturb':7, 'asleep':8, 'engaged':9]
	if (!occupancy || !(buttonMap.containsKey(occupancy))) {
    	ifDebug("Missing or invalid parameter room occupancy: $occupancy")
        return
    }
	sendEvent(name: "occupancy", value: occupancy, descriptionText: "$device.displayName changed to $occupancy", isStateChange: true, displayed: true)
	if (hT == _Hubitat)		{
		def img = "https://cdn.rawgit.com/adey/bangali/master/resources/icons/rooms${occupancy?.capitalize()}State.png"
		sendEvent(name: "occupancyIconS", value: "<img src=$img height=25 width=25>", descriptionText: "$device.displayName $occupancy icon small", isStateChange: true, displayed: true)
		sendEvent(name: "occupancyIconM", value: "<img src=$img height=50 width=50>", descriptionText: "$device.displayName $occupancy icon medium", isStateChange: true, displayed: true)
		sendEvent(name: "occupancyIconL", value: "<img src=$img height=75 width=75>", descriptionText: "$device.displayName $occupancy icon large", isStateChange: true, displayed: true)
		sendEvent(name: "occupancyIconXL", value: "<img src=$img height=100 width=100>", descriptionText: "$device.displayName $occupancy icon extra large", isStateChange: true, displayed: true)
		sendEvent(name: "occupancyIconXXL", value: "<img src=$img height=150 width=150>", descriptionText: "$device.displayName $occupancy icon extra extra large", isStateChange: true, displayed: true)
		sendEvent(name: "occupancyIconURL", value: img, descriptionText: "$device.displayName $occupancy icon URL", isStateChange: true, displayed: true)
	}
    def button = buttonMap[occupancy]
	if (hT == _SmartThings)
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
	if (endLoop)	unschedule();
	else			runIn(1, alarmOn);
}

def alarmOffAction()	{
	ifDebug("alarmOffAction")
	unschedule()
	if (parent)		parent.ringAlarm(true);
	alarmOff(true)
}

private updateRoomStatusMsg()		{
//	sendEvent(name: "statusFiller", value: "Since:", isStateChange: true, displayed: false)
	state.statusMsg = formatLocalTime()
	sendEvent(name: "status", value: state.statusMsg, isStateChange: true, displayed: false)
}

private formatLocalTime(time = now(), format = "EEE, MMM d yyyy @ h:mm:ss a z")		{
	def formatter = new java.text.SimpleDateFormat(format)
	formatter.setTimeZone(location.timeZone)
	return formatter.format(time)
}

def deviceList(devicesMap)		{
	def devicesTitle = ['busyCheck':'Busy Check', 'engagedButton':'Button', 'presence':'Presence Sensor', 'engagedSwitch':'Engaged Switch', 'contactSensor':'Contact Sensor',
						'motionSensors':'Motion Sensor', 'switchesOn':'Switch ON', 'switchesOff':'Switch OFF', 'luxSensor':'Lux Sensor', 'adjRoomNames':'Adjacent Room',
						'awayModes':'Away Mode', 'pauseModes':'Pause Mode', 'sleepSensor':'Sleep Sensor', 'nightButton':'Night Button', 'nightSwitches':'Night Switch']
	def deviceCount = 12
	def i = 1
	devicesMap.each	{ k, v ->
		if (v)			{
			v.each	{
				if (it && i <= deviceCount)		{
					sendEvent(name: "deviceList" + i, value: (devicesTitle[k] + ":\n" + (it.hasProperty('displayName') ? it.displayName : it)), isStateChange: true, displayed: false)
					i = i +1
				}
			}
		}
	}
	for (; i <= deviceCount; i++)
		sendEvent(name: "deviceList" + i, value: null, isStateChange: true, displayed: false)
}

private	resetTile(occupancy)	{
    sendEvent(name: occupancy, value: occupancy, descriptionText: "$device.displayName reset tile $occupancy", isStateChange: true, displayed: false)
}

def generateEvent(newState = null)		{
	if (newState)		stateUpdate(newState);
}

def turnSwitchesAllOn()		{
	if (parent)		{
		parent.turnSwitchesAllOnOrOff(true)
        	if (getHubType() != _Hubitat)	updateSwitchInd(1);
	}
}

def turnSwitchesAllOff()		{
	if (parent)		{
		parent.turnSwitchesAllOnOrOff(false)
		if (getHubType() != _Hubitat)	updateSwitchInd(0);
	}
}

def turnNightSwitchesAllOn()	{
 	ifDebug("turnNightSwitchesAllOn")
	if (parent)	{
		parent.dimNightLights()
		if (getHubType() != _Hubitat)	updateNSwitchInd(1);
	}
}

def turnNightSwitchesAllOff()	{
	ifDebug("turnNightSwitchesAllOff")
	if (parent)		{
		parent.nightSwitchesOff()
		if (getHubType() != _Hubitat)	updateNSwitchInd(0);
	}
}

def	turnOnAndOffSwitches()	{
	updateTimer(-1)
	if (parent)		parent.switchesOnOrOff();
}

def updateTimer(timer = 0)		{
	if (timer == -1)	timer = state.timer;
	else				state.timer = timer;
	sendEvent(name: "timer", value: (timer ?: '--'), isStateChange: true, displayed: false)
	sendEvent(name: "countdown", value: timer, descriptionText: "countdown timer: $timer", isStateChange: true, displayed: true)
}

private ifDebug(msg = null, level = null)     {  if (msg && (isDebug() || level == 'error'))  log."${level ?: 'debug'}" " $device.displayName device: " + msg  }
