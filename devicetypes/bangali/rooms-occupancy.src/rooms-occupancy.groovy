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
*  Name: Room Occupancy
*  Source: https://github.com/adey/bangali/blob/master/devicetypes/bangali/rooms-occupancy.src/rooms-occupancy.groovy
*  Version: 0.03
*
*   DONE:
*   1) added new states do not disturb and asleep, on user demand. these have button value of 7 and 8 respectively.
*	2) locked and kaput moved below the fold and replaced on-screen with do not disturb and asleep respectively.
*   3) cleaned up settings display.
*   4) changed roomOccupancy to occupancyStatus. sorry for the compatibility breaking change. by user demand.
*   5) updated some interstitial text.
*   6) if no motion sensor specified but there is a timeout value > 5 and turn off switches specified, those
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
    	multiAttributeTile(name: "occupancyStatus", width: 2, height: 2, canChangeBackground: true)		{
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
        standardTile("occupied", "device.occupied", width: 2, height: 2, canChangeIcon: true) {
			state "occupied", label:"Occupied", icon: "st.Health & Wellness.health12", action: "occupied", backgroundColor:"#ffffff", nextState:"toOccupied"
            state "toOccupied", label:"Updating", icon:"st.Health & Wellness.health12", backgroundColor:"#90af89"
		}
		standardTile("checking", "device.checking", width: 2, height: 2, canChangeIcon: true) {
			state "checking", label:"Checking", icon: "st.Health & Wellness.health9", action: "checking", backgroundColor:"#ffffff", nextState:"toChecking"
			state "toChecking", label:"Updating", icon: "st.Health & Wellness.health9", backgroundColor:"#616969"
		}
        standardTile("vacant", "device.vacant", width: 2, height: 2, canChangeIcon: true) {
			state "vacant", label:"Vacant", icon: "st.Home.home18", action: "vacant", backgroundColor:"#ffffff", nextState:"toVacant"
			state "toVacant", label:"Updating", icon: "st.Home.home18", backgroundColor:"#32b399"
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
		standardTile("engaged", "device.engaged", width: 2, height: 2, canChangeIcon: true) {
			state "engaged", label:"Engaged", icon: "st.locks.lock.unlocked", action: "engaged", backgroundColor:"#ffffff", nextState:"toEngaged"
			state "toEngaged", label:"Updating", icon: "st.locks.lock.unlocked", backgroundColor:"#ff6666"
		}
        standardTile("kaput", "device.kaput", width: 2, height: 2, canChangeIcon: true) {
			state "kaput", label:"Kaput", icon: "st.Outdoor.outdoor18", action: "kaput", backgroundColor:"#ffffff", nextState:"toKaput"
			state "toKaput", label:"Updating", icon: "st.Outdoor.outdoor18", backgroundColor:"#95623d"
		}
		main (["occupancyStatus"])
		details (["occupancyStatus", "vacant", "checking", "occupied", "donotdisturb", "reserved", "asleep", "locked", "engaged", "kaput"])
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
    	log.debug "${device.displayName}: Missing or invalid parameter room occupancy. Allowed values Occupied, Vacant, Locked, Reserved or Checking."
        return
    }
	sendEvent(name: "occupancyStatus", value: occupancyStatus, descriptionText: "${device.displayName} changed to ${occupancyStatus}", isStateChange: true, displayed: true)
    def button = buttonMap[occupancyStatus]
	sendEvent(name: "button", value: "pushed", data: [buttonNumber: button], descriptionText: "$device.displayName button $button was pushed.", isStateChange: true)
//	def statusMsg = msgTextMap[device.currentValue('occupancyStatus')] + formatLocalTime()
	def statusMsg = "Since: " + formatLocalTime()
	sendEvent(name: "status", value: statusMsg, isStateChange: true, displayed: false)
}

private formatLocalTime(format = "EEE, MMM d yyyy @ h:mm:ss a z", time = now())		{
	def formatter = new java.text.SimpleDateFormat(format)
	formatter.setTimeZone(location.timeZone)
	return formatter.format(time)
}

private	resetTile(occupancyStatus)	{
    sendEvent(name: occupancyStatus, value: occupancyStatus, descriptionText: "reset tile ${occupancyStatus} to ${occupancyStatus}", isStateChange: true, displayed: false)
}

def generateEvent(state = null)		{
	if	(state && device.currentValue('occupancyStatus') != state)
		updateOccupancyStatus(state)
	return null
}

def getRoomState()	{	return device.currentValue('occupancyStatus')		}
