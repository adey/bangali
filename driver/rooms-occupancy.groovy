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

public static String version()		{  return "v1.0.1"  }
private static boolean isDebug()	{  return false  }

final String _SmartThings()	{ return 'ST' }
final String _Hubitat()		{ return 'HU' }

metadata {
	definition (
		name: "rooms occupancy",
		namespace: "bangali",
		author: "bangali")		{
		capability "Actuator"
// for hubitat comment the next line and uncomment the one after that is currently commented
//		capability "Button"
		capability "PushableButton"		// hubitat changed `Button` to `PushableButton`  2018-04-20
		capability "Sensor"
		capability "Switch"
		capability "Beacon"
		capability "Health Check"
// for hubitat comment the next line since this capability is not supported
//		capability "Lock Only"
		attribute "occupancy", "enum", ['occupied', 'checking', 'vacant', 'locked', 'reserved', 'kaput', 'donotdisturb', 'asleep', 'engaged']
// for hubitat leave the next few lines uncommented ONLY if you want to use the icons on dashboard
		attribute "occupancyIconS", "String"
		attribute "occupancyIconM", "String"
		attribute "occupancyIconL", "String"
		attribute "occupancyIconXL", "String"
		attribute "occupancyIconXXL", "String"
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
// for hubitat uncomment the next line
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
}

def setOnStateC(e)		{  state.onState = (e ? e.toString() : 'occupied')  }

def	off()		{
	vacant()
	switchOnOff(false)
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

def occupied(vM = false)		{  parent.occupied(vM)  }

def checking(vM = false)		{  parent.checking(vM)  }

def vacant(vM = false)			{  parent.vacant(vM)  }

def donotdisturb(vM = false)	{  parent.donotdisturb(vM)  }

def reserved(vM = false)		{  parent.reserved(vM)  }

def asleep(vM = false)			{  parent.asleep(vM)  }

def locked(vM = false)			{  parent.locked(vM)  }

def engaged(vM = false)			{  parent.engaged(vM)  }

def kaput(vM = false)			{  parent.kaput(vM)  }

/*
private	resetTile(occupancy)	{
	sendEvent(name: occupancy, value: occupancy, descriptionText: "$device.displayName reset tile $occupancy", displayed: false)
}

*/

def turnSwitchesAllOn()		{
	if (parent)		parent.turnSwitchesAllOnOrOff(true);
//		if (getHubType() != _Hubitat())		updateSwitchInd(1);
}

def turnSwitchesAllOff()		{
	if (parent)		parent.turnSwitchesAllOnOrOff(false);
//		if (getHubType() != _Hubitat())		updateSwitchInd(0);
}

def turnNightSwitchesAllOn()	{
 	ifDebug("turnNightSwitchesAllOn")
	if (parent)		parent.dimNightLights();
//		if (getHubType() != _Hubitat())		updateNSwitchInd(1)
}

def turnNightSwitchesAllOff()	{
	ifDebug("turnNightSwitchesAllOff")
	if (parent)		parent.nightSwitchesOff();
//		if (getHubType() != _Hubitat())		updateNSwitchInd(0)
}

def	turnOnAndOffSwitches()	{
	if (parent)		parent.switchesOnOrOff();
//	setupTimer(-1)
}

private ifDebug(msg = null, level = null)	{  if (msg && (isDebug() || level == 'error'))	log."${level ?: 'debug'}" " $device.displayName device: " + msg  }
