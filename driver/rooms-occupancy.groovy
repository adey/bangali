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

public static String version()		{  return "v0.99.6"  }
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
	}

	simulator	{
	}

	preferences		{
	}
}

def parse(String description)	{  ifDebug("parse: $description")  }

def installed()		{  initialize()  }

def updated()		{  initialize()  }

def	initialize()	{
	unschedule()
	sendEvent(name: "numberOfButtons", value: 9, descriptionText: "set number of buttons to 9.", displayed: true)
	state.timer = 0
	sendEvent(name: "countdown", value: '0s', descriptionText: "countdown timer: 0s", displayed: true)
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

def on()	{
	if (!state.onState)		state.onState = parent?.roomDeviceSwitchOnP().toString();
	switch(state.onState ?: 'occupied')		{
		case 'occupied':	occupied();		break
		case 'engaged':		engaged();		break
		case 'locked':		locked();		break
		case 'asleep':		asleep();		break
		default:							break
	}
	switchOnOff(true)
//	sendEvent(name: "switch", value: "on", descriptionText: "$device.displayName is on", displayed: true)
}

def setOnStateC(e)		{  state.onState = (e ? e.toString() : 'occupied')  }

def	off()		{
	vacant()
	switchOnOff(false)
//	sendEvent(name: "switch", value: "off", descriptionText: "$device.displayName is off", displayed: true)
}

private switchOnOff(on)	{
	def switchState = (on ? 'on' : 'off')
	sendEvent(name: "switch", value: "$switchState", descriptionText: "$device.displayName is $switchState", displayed: true)
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
				sendEvent(name: "button", value: "pushed", data: [buttonNumber: "$buton"], descriptionText: "$device.displayName button $buton was pushed", displayed: true)
			else
				sendEvent(name: "pushableButton", value: buton, descriptionText: "$device.displayName button $buton was pushed", displayed: true)
			break
	}
}

def lock()		{  locked() }

def unlock()	{  vacant()  }

def occupied(hS = true, vM = false)			{  stateUpdateSetup('occupied', hS, vM); 		switchOnOff(true)  }

def checking(hS = true, vM = false)			{  stateUpdateSetup('checking', hS, vM)  }

def vacant(hS = true, vM = false)			{  stateUpdateSetup('vacant', hS, vM);			switchOnOff(false)  }

def donotdisturb(hS = true, vM = false)		{  stateUpdateSetup('donotdisturb', hS, vM);	switchOnOff(false)  }

def reserved(hS = true, vM = false)			{  stateUpdateSetup('reserved', hS, vM);		switchOnOff(false)  }

def asleep(hS = true, vM = false)			{  stateUpdateSetup('asleep', hS, vM);			switchOnOff(true)  }

def locked(hS = true, vM = false)			{  stateUpdateSetup('locked', hS, vM);			switchOnOff(false)  }

def engaged(hS = true, vM = false)			{  stateUpdateSetup('engaged', hS, vM);			switchOnOff(true)  }

def kaput(hS = true, vM = false)			{  stateUpdateSetup('kaput', hS, vM);			switchOnOff(false)  }

private stateUpdateSetup(rSt, hS, vM)	{
	runIn(0, stateUpdate, [data: [newState:rSt, handleSwitches:hS, vacationMode:vM]])
}

def	stateUpdate(data)		{
	if (!data)		return;
	if (state.oldState != data.newState)		{
        if (data.handleSwitches && parent)
			setupTimer((int) (parent.handleSwitches(state.oldState, data.newState, true, data.vacationMode) ?: 0))
		updateOccupancy(state.oldState, data.newState)
		state.oldState = data.newState
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
	sendEvent(name: "occupancy", value: newOcc, descriptionText: "$device.displayName changed to $newOcc", displayed: true)
	if (hT == _Hubitat())		{
		def img = "https://cdn.rawgit.com/adey/bangali/master/resources/icons/rooms${newOcc?.capitalize()}State.png"
		sendEvent(name: "occupancyIconS", value: "<img src=$img height=25 width=25>", descriptionText: "$device.displayName $newOcc icon small", displayed: true)
		sendEvent(name: "occupancyIconM", value: "<img src=$img height=50 width=50>", descriptionText: "$device.displayName $newOcc icon medium", displayed: true)
		sendEvent(name: "occupancyIconL", value: "<img src=$img height=75 width=75>", descriptionText: "$device.displayName $newOcc icon large", displayed: true)
		sendEvent(name: "occupancyIconXL", value: "<img src=$img height=100 width=100>", descriptionText: "$device.displayName $newOcc icon extra large", displayed: true)
		sendEvent(name: "occupancyIconXXL", value: "<img src=$img height=150 width=150>", descriptionText: "$device.displayName $newOcc icon extra extra large", displayed: true)
		sendEvent(name: "occupancyIconURL", value: img, descriptionText: "$device.displayName $newOcc icon URL", displayed: true)
	}
	def button = buttonMap[newOcc]
	if (hT == _SmartThings())
		sendEvent(name: "button", value: "pushed", data: [buttonNumber: button], descriptionText: "$device.displayName button $button was pushed.")
	else
		sendEvent(name:"pushed", value:button, descriptionText: "$device.displayName button $button was pushed.")

	updateRoomStatusMsg()
}

private updateRoomStatusMsg()		{
	def formatter = new java.text.SimpleDateFormat("EEE, MMM d yyyy @ h:mm:ss a z")
	formatter.setTimeZone(location.timeZone)
	state.statusMsg = formatter.format(now())
	sendEvent(name: "status", value: state.statusMsg, displayed: false)
}

private	resetTile(occupancy)	{
	sendEvent(name: occupancy, value: occupancy, descriptionText: "$device.displayName reset tile $occupancy", displayed: false)
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
	int timerUpdate = (state.timerLeft > 30 ? 30 : (state.timerLeft < 5 ? state.timerLeft : 5))
	def timerInd = (state.timerLeft > 3600 ? (state.timerLeft / 3600f).round(1) + 'h' : (state.timerLeft > 60 ? (state.timerLeft / 60f).round(1) + 'm' : state.timerLeft + 's')).replace(".0","")
	if (getHubType() != _Hubitat())
		sendEvent(name: "timer", value: (timerInd ?: '--'), displayed: false)
	else
		sendEvent(name: "countdown", value: timerInd, descriptionText: "countdown timer: $timerInd", displayed: true)
	state.timerLeft = state.timerLeft - timerUpdate
	(state.timerLeft > 0 ? runIn(timerUpdate, timerNext) : unschedule('timerNext'))
}

private ifDebug(msg = null, level = null)	{  if (msg && (isDebug() || level == 'error'))	log."${level ?: 'debug'}" " $device.displayName device: " + msg  }
