/*****************************************************************************************************************
*
*  A SmartThings device handler to allow handling rooms as devices which have states.
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
*	formatDuration(...) code by ady624 for webCoRE. adpated by me to work here. original code can be found at:
*		https://github.com/ady624/webCoRE/blob/master/smartapps/ady624/webcore-piston.src/webcore-piston.groovy
*
*  Name: Room Occupancy
*  Source: https://github.com/adey/bangali/blob/master/devicetypes/bangali/rooms-occupancy.src/rooms-occupancy.groovy
*
*  Version: 0.04.5
*
*   DONE:   11/10/2017
*   1) revamped device details screen. if users dont like it will revert back.
*   2) when swiches are turned off because lux rose or is outside of time window added settings to turn off both
*           group of switches instead of just switches off.
*   3) added option to change state directly from engaged to vacant without moving to checking state.
*	4) removed last event from status message.
*
*  Version: 0.04.3
*
*   DONE:   11/8/2017
*   1) added last event to status message.
*   2) added concept of adjacent rooms that you can select in room settings. setting does not do anything yet :-)
*
*  Version: 0.04.2
*
*   DONE:   11/6/2017
*   1) added setting option to allow timeout from last motion active or on motion inactive. if motion has a long timeout
*           this will allow the lights to turn off quicker. but be aware motion sensor may show motion due to long
*           timeout while room indicates its vacant.
*
*  Version: 0.04.1
*
*   DONE:   11/3/2017
*   1) added support for time window to turn on/off switches when between those times. this works with other settings
*           as well. like if lux is specified both the lux setting and the time setting have to be true for switches
*           to be turned on or off.
*
*  Version: 0.04
*
*   DONE:   11/3/2017
*   1) added support for presence sensor to change room state to engaged when present. when presence sensor is not
*           present the room automation should work normally.
*   2) added support for modes which when set cause all automation to be bypassed if location is any of those modes.
*
*  Version: 0.03.7
*
*   DONE:   11/1/2017
*   1) added support for contact sensor. when contact sensor changes to closed room will be set to checking state.
*           if there is no motion afterwards room will be set to vacant. if there is motion, room will be set to
*           engaged which stops room automation from kicking in till the contact is opened again.
*           when contact sensor changes to open room will be set to checking state so automation can resume again.
*           the only exception to this is home changing to away in which case room will be set to vacant.
*   2) when contact sensor is specified but no motion sensor is specified room will be changed to engaged when
*           contact sensor closes.
*   3) if there is a motion sensor specified but no motion timeout value then room will be changed to vacant when
*           motion sensor becomes inactive and room is in occupied or checking state.
*   4) added engaged switch which when turned on will mark the room as engaged to stop automation. this gets a
*           little tricky when both engaged switch and contact sensor is defined. the contact sensor changing to
*           open will reset the state back to checking. but if there is subsequent motion in the room within the
*           timeout period the room will be set to occupied. or if the door is closed again and there is subsequent
*           motion in the room within the timeout period the room will be set to engaged stopping automation.
*   5) added lights control with lux for engaged state.
*   6) added button push to toogle room state between engaged and checking when room state is already engaged.
*
*  Version: 0.03.5
*
*   DONE:   10/29/2017
*   1) added support for setting level and/or color temperature for turning on switches. these will be set for
*           those devices in the turn on switchs list that support it.
*   2) since motion inactive timeout can vary so widely amongst different brands of motion sensors chose not to
*           use motion inactive event and instead timeout on motion active event for predictable user experience.
*   3) added support for dimming before turning off light.
*   4) added support for color setting which takes preference over color temperature if the switch supports it.
*   5) fixed small bugs.
*
*  Version: 0.03.1
*
*   DONE:   10/27/2017
*   1) added support for lux sensor and lux value. if these values are specified:
*       a) if lux value falls <= that value and switches on are selected those switches will be turned on.
*       b) if lux value rises > that value and switches off are selected those switches will be turned off.
*       c) switches on with motion will be turned on only when lux value is <= that value.
*   2) fixed small bugs.
*
*  Version: 0.03
*
*   DONE:
*   1) added new states do not disturb and asleep, on user demand. these have button value of 7 and 8 respectively.
*	2) locked and kaput moved below the fold and replaced on-screen with do not disturb and asleep respectively.
*   3) cleaned up settings display.
*   4) changed roomOccupancy to occupancyStatus. sorry for the compatibility breaking change. by user demand.
*   5) updated some interstitial text.
*   6) if no motion sensor specified but there is a timeout value >= 5 and turn off switches specified, those
*            switches will be switched off after timeout seconds if room is vacant.
*	7) added new engaged state, on user demand. this button has a button value of 9 respectively.
*   8) if room state changes any pending actions are cancelled.
*
*  Version: 0.02
*
*   DONE:
*	0) Initial commit.
*   1) added support for multiple away modes. when home changes to any these modes room is set to vacant but
*            only if room is in occupied or checking state.
*   2) added subscription for motion devices so if room is vacant or checking move room state to occupied.
*   3) added support for switches to be turned on when room is changed to occupied.
*   4) added support for switches to be turned off when room is changed to vacant, different switches from #3.
*   5) added button push events to tile commands, where occupied = button 1, ..., kaput = button 6 so it is
*           supported by ST Smart Lighting smartapp.
*
*****************************************************************************************************************/

metadata {
	definition (
    	name: "rooms occupancy",
        namespace: "bangali",
        author: "bangali")		{
		capability "Actuator"
		capability "Button"
		capability "Sensor"
		attribute "occupancyStatus", "string"
		command "occupied"
        command "checking"
		command "vacant"
        command "locked"
		command "reserved"
		command "kaput"
		command "donotdisturb"
		command "asleep"
		command "engaged"
		command "updateOccupancyStatus", ["string"]
	}

	simulator	{
	}

	tiles(scale: 2)		{
// old style display
/*    	multiAttributeTile(name: "occupancyStatus", width: 2, height: 2, canChangeBackground: true)		{
			tileAttribute ("device.occupancyStatus", key: "PRIMARY_CONTROL")		{
				attributeState "occupied", label: 'Occupied', icon:"st.Health & Wellness.health12", backgroundColor:"#90af89"
				attributeState "checking", label: 'Checking', icon:"st.Health & Wellness.health9", backgroundColor:"#616969"
				attributeState "vacant", label: 'Vacant', icon:"st.Home.home18", backgroundColor:"#32b399"
				attributeState "donotdisturb", label: 'Do Not Disturb', icon:"st.Seasonal Winter.seasonal-winter-011", backgroundColor:"#009cb2"
				attributeState "reserved", label: 'Reserved', icon:"st.Office.office7", backgroundColor:"#ccac00"
				attributeState "asleep", label: 'Asleep', icon:"st.Bedroom.bedroom2", backgroundColor:"#6879af"
				attributeState "locked", label: 'Locked', icon:"st.locks.lock.locked", backgroundColor:"#c079a3"
				attributeState "engaged", label: 'Engaged', icon:"st.locks.lock.unlocked", backgroundColor:"#ff6666"
				attributeState "kaput", label: 'Kaput', icon:"st.Outdoor.outdoor18", backgroundColor:"#95623d"
            }
       		tileAttribute ("device.status", key: "SECONDARY_CONTROL")	{
				attributeState "default", label:'${currentValue}'
			}
        }
*/
// new style display
		standardTile("occupancyStatus", "device.occupancyStatus", width: 4, height: 4, inactiveLabel: true, canChangeBackground: true)		{
			state "occupied", label: 'Occupied', icon:"st.Health & Wellness.health12", backgroundColor:"#90af89"
			state "checking", label: 'Checking', icon:"st.Health & Wellness.health9", backgroundColor:"#616969"
			state "vacant", label: 'Vacant', icon:"st.Home.home18", backgroundColor:"#32b399"
			state "donotdisturb", label: 'Do Not Disturb', icon:"st.Seasonal Winter.seasonal-winter-011", backgroundColor:"#009cb2"
			state "reserved", label: 'Reserved', icon:"st.Office.office7", backgroundColor:"#ccac00"
			state "asleep", label: 'Asleep', icon:"st.Bedroom.bedroom2", backgroundColor:"#6879af"
			state "locked", label: 'Locked', icon:"st.locks.lock.locked", backgroundColor:"#c079a3"
			state "engaged", label: 'Engaged', icon:"st.locks.lock.unlocked", backgroundColor:"#ff6666"
			state "kaput", label: 'Kaput', icon:"st.Outdoor.outdoor18", backgroundColor:"#95623d"
        }
		valueTile("status", "device.status", inactiveLabel: false, width: 5, height: 1, decoration: "flat", wordWrap: false)	{
			state "status", label:'${currentValue}', backgroundColor:"#ffffff", defaultState: false
		}

		valueTile("deviceList1", "device.deviceList1", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true) {
			state "deviceList1", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList2", "device.deviceList2", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true) {
			state "deviceList2", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList3", "device.deviceList3", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true) {
			state "deviceList3", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList4", "device.deviceList4", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true) {
			state "deviceList4", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList5", "device.deviceList5", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true) {
			state "deviceList5", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList6", "device.deviceList6", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true) {
			state "deviceList6", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList7", "device.deviceList7", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true) {
			state "deviceList7", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList8", "device.deviceList8", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true) {
			state "deviceList8", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList9", "device.deviceList9", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true) {
			state "deviceList9", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList10", "device.deviceList10", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true) {
			state "deviceList10", label:'${currentValue}', backgroundColor:"#ffffff"
		}

		standardTile("engaged", "device.engaged", width: 2, height: 2, canChangeIcon: true) {
			state "engaged", label:"Engaged", icon: "st.locks.lock.unlocked", action: "engaged", backgroundColor:"#ffffff", nextState:"toEngaged"
			state "toEngaged", label:"Updating", icon: "st.locks.lock.unlocked", backgroundColor:"#ff6666"
		}
		standardTile("vacant", "device.vacant", width: 2, height: 2, canChangeIcon: true) {
			state "vacant", label:"Vacant", icon: "st.Home.home18", action: "vacant", backgroundColor:"#ffffff", nextState:"toVacant"
			state "toVacant", label:"Updating", icon: "st.Home.home18", backgroundColor:"#32b399"
		}
/*		standardTile("checking", "device.checking", width: 2, height: 2, canChangeIcon: true) {
			state "checking", label:"Checking", icon: "st.Health & Wellness.health9", action: "checking", backgroundColor:"#ffffff", nextState:"toChecking"
			state "toChecking", label:"Updating", icon: "st.Health & Wellness.health9", backgroundColor:"#616969"
		}*/
		standardTile("occupied", "device.occupied", width: 2, height: 2, canChangeIcon: true) {
			state "occupied", label:"Occupied", icon: "st.Health & Wellness.health12", action: "occupied", backgroundColor:"#ffffff", nextState:"toOccupied"
            state "toOccupied", label:"Updating", icon:"st.Health & Wellness.health12", backgroundColor:"#90af89"
		}
		standardTile("donotdisturb", "device.donotdisturb", width: 2, height: 2, canChangeIcon: true) {
			state "donotdisturb", label:"DnD", icon: "st.Seasonal Winter.seasonal-winter-011", action: "donotdisturb", backgroundColor:"#ffffff", nextState:"toDoNotDisturb"
			state "toDoNotDisturb", label:"Updating", icon: "st.Seasonal Winter.seasonal-winter-011", backgroundColor:"#009cb2"
		}
        standardTile("reserved", "device.reserved", width: 2, height: 2, canChangeIcon: true) {
			state "reserved", label:"Reserved", icon: "st.Office.office7", action: "reserved", backgroundColor:"#ffffff", nextState:"toReserved"
			state "toReserved", label:"Updating", icon: "st.Office.office7", backgroundColor:"#ccac00"
		}
		standardTile("asleep", "device.asleep", width: 2, height: 2, canChangeIcon: true) {
			state "asleep", label:"Asleep", icon: "st.Bedroom.bedroom2", action: "asleep", backgroundColor:"#ffffff", nextState:"toAsleep"
			state "toAsleep", label:"Updating", icon: "st.Bedroom.bedroom2", backgroundColor:"#6879af"
		}
		standardTile("locked", "device.locked", width: 2, height: 2, canChangeIcon: true) {
			state "locked", label:"Locked", icon: "st.locks.lock.locked", action: "locked", backgroundColor:"#ffffff", nextState:"toLocked"
			state "toLocked", label:"Updating", icon: "st.locks.lock.locked", backgroundColor:"#c079a3"
		}
        standardTile("kaput", "device.kaput", width: 2, height: 2, canChangeIcon: true) {
			state "kaput", label:"Kaput", icon: "st.Outdoor.outdoor18", action: "kaput", backgroundColor:"#ffffff", nextState:"toKaput"
			state "toKaput", label:"Updating", icon: "st.Outdoor.outdoor18", backgroundColor:"#95623d"
		}

		main (["occupancyStatus"])

		// display all tiles
		details (["occupancyStatus", "engaged", "vacant", "status", "deviceList1", "deviceList2", "deviceList3", "deviceList4", "deviceList5", "deviceList6", "deviceList7", "deviceList8", "deviceList9", "deviceList10", "occupied", "donotdisturb", "reserved", "asleep", "locked", "kaput"])
		// display main and other button tiles only
		// details (["occupancyStatus", "engaged", "vacant", "status", "occupied", "donotdisturb", "reserved", "asleep", "locked", "kaput"])
		// display main tiles and devices list only
		// details (["occupancyStatus", "engaged", "vacant", "status", "deviceList1", "deviceList2", "deviceList3", "deviceList4", "deviceList5", "deviceList6", "deviceList7", "deviceList8", "deviceList9", "deviceList10"])
		// display main tiles only
		// details (["occupancyStatus", "engaged", "vacant", "status"])

	}
}

def parse(String description)	{}

def installed()		{	initialize();	vacant()	}

def updated()	{	initialize()	}

def	initialize()	{	sendEvent(name: "numberOfButtons", value: 8)	}

def occupied()	{	stateUpdate('occupied')		}

def checking()	{	stateUpdate('checking')		}

def vacant()	{	stateUpdate('vacant')		}

def donotdisturb()	{	stateUpdate('donotdisturb')		}

def reserved()	{	stateUpdate('reserved')		}

def asleep()	{	stateUpdate('asleep')		}

def locked()	{	stateUpdate('locked')		}

def engaged()	{	stateUpdate('engaged')		}

def kaput()		{	stateUpdate('kaput')		}

private	stateUpdate(state)	{
	def oldState = device.currentValue('occupancyStatus')
	if (oldState != state)	{
		updateOccupancyStatus(state)
        if (parent)
        	parent.handleSwitches(oldState, state)
	}
	resetTile(state)
}

private updateOccupancyStatus(occupancyStatus = null) 	{
	occupancyStatus = occupancyStatus?.toLowerCase()
//	def msgTextMap = ['occupied':'Room is occupied: ', 'locked':'Room is locked: ', 'vacant':'Room is vacant: ', 'reserved':'Room is reserved: ', 'checking':'Checking room status: ', 'kaput':'Room not in service: ', 'donotdisturb':'Room is do not disturb: ', 'asleep':'Room is asleep: ']
//	def msgTextMap = ['occupied', 'locked', 'vacant', 'reserved', 'checking', 'kaput', 'donotdisturb', 'asleep']
	def buttonMap = ['occupied':1, 'locked':4, 'vacant':3, 'reserved':5, 'checking':2, 'kaput':6, 'donotdisturb':7, 'asleep':8, 'engaged':9]
//	if (!occupancyStatus || !(msgTextMap.containsKey(occupancyStatus))) {
	if (!occupancyStatus || !(buttonMap.containsKey(occupancyStatus))) {
    	log.debug "${device.displayName}: Missing or invalid parameter room occupancy: $occupancyStatus"
        return
    }
	sendEvent(name: "occupancyStatus", value: occupancyStatus, descriptionText: "${device.displayName} changed to ${occupancyStatus}", isStateChange: true, displayed: true)
    def button = buttonMap[occupancyStatus]
	sendEvent(name: "button", value: "pushed", data: [buttonNumber: button], descriptionText: "$device.displayName button $button was pushed.", isStateChange: true)
//	def statusMsg = msgTextMap[device.currentValue('occupancyStatus')] + formatLocalTime()
	state.statusMsg = "Since: " + formatLocalTime()
	updateRoomStatus()
}

private formatLocalTime(time = now(), format = "EEE, MMM d yyyy @ h:mm:ss a z")		{
	def formatter = new java.text.SimpleDateFormat(format)
	formatter.setTimeZone(location.timeZone)
	return formatter.format(time)
}

private updateRoomStatus()		{
	sendEvent(name: "status", value: state.statusMsg, isStateChange: true, displayed: false)
}

def deviceList(devicesMap)		{
	def devicesTitle = ['engagedButton':'Button', 'presence':'Presence Sensor', 'engagedSwitch':'Engaged Switch', 'contactSensor':'Contact Sensor',
						'motionSensors':'Motion Sensor', 'switchesOn':'Switch ON', 'switchesOff':'Switch OFF', 'luxSensor':'Lux Sensor',
						'awayModes':'Away Mode', 'pauseModes':'Pause Mode']
	def deviceCount = 10
	def i = 1
	devicesMap.each	{ k, v ->
		if (v)			{
			v.each	{
				if (i <= deviceCount)		{
					sendEvent(name: "deviceList" + i, value: (devicesTitle[k] + ":\n" + (it.hasProperty('displayName') ? it.displayName : it)), isStateChange: true, displayed: false)
					i = i +1
				}
			}
		}
	}
	for (; i < deviceCount; i++)
		sendEvent(name: "deviceList" + i, value: null, isStateChange: true, displayed: false)
}

private	resetTile(occupancyStatus)	{
    sendEvent(name: occupancyStatus, value: occupancyStatus, descriptionText: "reset tile ${occupancyStatus} to ${occupancyStatus}", isStateChange: true, displayed: false)
}

def generateEvent(state = null)		{
//	if	(state && device.currentValue('occupancyStatus') != state)
	if	(state)
		stateUpdate(state)
	return null
}

def getRoomState()	{	return device.currentValue('occupancyStatus')	}

/*
not using yet but have plans to ...

private formatduration(long value, boolean friendly = false, granularity = 's', boolean showAdverbs = false)		{
	int sign = (value >= 0) ? 1 : -1
    if (sign < 0) value = -value
	int ms = value % 1000
    value = Math.floor((value - ms) / 1000)
	int s = value % 60
    value = Math.floor((value - s) / 60)
	int m = value % 60
    value = Math.floor((value - m) / 60)
	int h = value % 24
    value = Math.floor((value - h) / 24)
	int d = value

    def parts = 0
    def partName = ''
    switch (granularity) {
    	case 'd': parts = 1; partName = 'day'; break;
    	case 'h': parts = 2; partName = 'hour'; break;
    	case 'm': parts = 3; partName = 'minute'; break;
    	case 'ms': parts = 5; partName = 'millisecond'; break;
    	default: parts = 4; partName = 'second'; break;
    }

    parts = friendly ? parts : (parts < 3 ? 3 : parts)
    def result = ''
    if (friendly) {
    	List p = []
        if (d) p.push("$d day" + (d > 1 ? 's' : ''))
        if ((parts > 1) && h) p.push("$h hour" + (h > 1 ? 's' : ''))
        if ((parts > 2) && m) p.push("$m minute" + (m > 1 ? 's' : ''))
        if ((parts > 3) && s) p.push("$s second" + (s > 1 ? 's' : ''))
        if ((parts > 4) && ms) p.push("$ms millisecond" + (ms > 1 ? 's' : ''))
        switch (p.size()) {
        	case 0:
            	result = showAdverbs ? 'now' : '0 ' + partName + 's'
                break
            case 1:
            	result = p[0]
                break
			default:
            	result = '';
                int sz = p.size()
                for (int i=0; i < sz; i++) {
                	result += (i ? (sz > 2 ? ', ' : ' ') : '') + (i == sz - 1 ? 'and ' : '') + p[i]
                }
                result = (showAdverbs && (sign > 0) ? 'in ' : '') + result + (showAdverbs && (sign < 0) ? ' ago' : '')
            	break
		}
    }
	else
    	result = (sign < 0 ? '-' : '') + (d > 0 ? sprintf("%dd ", d) : '') + sprintf("%02d:%02d", h, m) + (parts > 3 ? sprintf(":%02d", s) : '') + (parts > 4 ? sprintf(".%03d", ms) : '')

    return result
}
*/
