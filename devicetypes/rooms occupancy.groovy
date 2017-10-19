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
*  Source: https://github.com/adey/bangali/blob/master/devicetypes/rooms%20occupancy.groovy
*
*****************************************************************************************************************/

metadata {
	definition (
    	name: "rooms occupancy", 
        namespace: "bangali", 
        author: "bangali")		{
		capability "Sensor"        
		attribute "roomOccupancy", "string"
		command "occupied"
        command "checking"
		command "vacant"
        command "locked"
		command "reserved"
		command "kaput"
		command "updateRoomOccupancy", ["string"]
	}
    
	simulator	{
	}
    
	tiles(scale: 2)		{
    	multiAttributeTile(name: "roomOccupancy", width: 2, height: 2, canChangeBackground: true)		{
			tileAttribute ("device.roomOccupancy", key: "PRIMARY_CONTROL")		{
				attributeState "occupied", label: 'Occupied', icon:"st.Health & Wellness.health12", backgroundColor:"#90af89"
				attributeState "checking", label: 'Checking', icon:"st.Health & Wellness.health9", backgroundColor:"#616969"
				attributeState "vacant", label: 'Vacant', icon:"st.Home.home18", backgroundColor:"#6879af"
				attributeState "locked", label: 'Locked', icon:"st.locks.lock.locked", backgroundColor:"#c079a3"
				attributeState "reserved", label: 'Reserved', icon:"st.Office.office7", backgroundColor:"#b29600"
				attributeState "kaput", label: 'Kaput', icon:"st.Outdoor.outdoor18", backgroundColor:"#8a5128"
            }
       		tileAttribute ("device.status", key: "SECONDARY_CONTROL")	{
				attributeState "default", label:'${currentValue}'
			}
        }
        standardTile("occupied", "device.occupied", width: 2, height: 2, canChangeIcon: true) {
			state "occupied", label:"Occupied", icon: "st.Health & Wellness.health12", action: "occupied", backgroundColor:"#ffffff", nextState:"toOccupied"
            state "toOccupied", label:"Occupied", icon:"st.Health & Wellness.health12", backgroundColor:"#90af89"
		}
		standardTile("checking", "device.checking", width: 2, height: 2, canChangeIcon: true) {
			state "checking", label:"Checking", icon: "st.Health & Wellness.health9", action: "checking", backgroundColor:"#ffffff", nextState:"toChecking"
			state "toChecking", label:"Checking", icon: "st.Health & Wellness.health9", backgroundColor:"#616969"
		}
        standardTile("vacant", "device.vacant", width: 2, height: 2, canChangeIcon: true) {
			state "vacant", label:"Vacant", icon: "st.Home.home18", action: "vacant", backgroundColor:"#ffffff", nextState:"toVacant"
			state "toVacant", label:"Vacant", icon: "st.Home.home18", backgroundColor:"#6879af"
		}
        standardTile("locked", "device.locked", width: 2, height: 2, canChangeIcon: true) {
			state "locked", label:"Locked", icon: "st.locks.lock.locked", action: "locked", backgroundColor:"#ffffff", nextState:"toLocked"
			state "toLocked", label:"Locked", icon: "st.locks.lock.locked", backgroundColor:"#c079a3"
		}
        standardTile("reserved", "device.reserved", width: 2, height: 2, canChangeIcon: true) {
			state "reserved", label:"Reserved", icon: "st.Office.office7", action: "reserved", backgroundColor:"#ffffff", nextState:"toReserved"
			state "toReserved", label:"Reserved", icon: "st.Office.office7", backgroundColor:"#b29600"
		}
        standardTile("kaput", "device.kaput", width: 2, height: 2, canChangeIcon: true) {
			state "kaoput", label:"Kaput", icon: "st.Outdoor.outdoor18", action: "kaput", backgroundColor:"#ffffff", nextState:"toKaput"
			state "toKaput", label:"Kaput", icon: "st.Outdoor.outdoor18", backgroundColor:"#8a5128"
		}
		main (["roomOccupancy"])
		details (["roomOccupancy", "occupied", "checking", "vacant", "locked", "reserved", "kaput"])
	}
}

def parse(String description)	{}

def updated()	{}

def occupied()	{	stateUpdate('occupied')		}

def checking()	{	stateUpdate('checking')		}

def vacant()	{	stateUpdate('vacant')		}

def locked()	{	stateUpdate('locked')		}

def reserved()	{	stateUpdate('reserved')		}

def kaput()		{	stateUpdate('kaput')		}

private	stateUpdate(state)	{
	if (device.currentValue('roomOccupancy') != state)
		updateRoomOccupancy(state)
	resetTile(state)
}

private updateRoomOccupancy(roomOccupancy = null) 	{
	roomOccupancy = roomOccupancy?.toLowerCase()
	def msgTextMap = ['occupied':'Room is occupied: ', 'locked':'Room is locked: ', 'vacant':'Room is vacant: ', 'reserved':'Room is reserved: ', 'checking':'Checking room status: ', 'kaput':'Room not in service: ']
	if (!roomOccupancy || !(msgTextMap.containsKey(roomOccupancy))) {
    	log.debug "${device.displayName}: Missing or invalid parameter room occupancy. Allowed values Occupied, Vacant, Locked, Reserved or Checking."
        return
    }
	sendEvent(name: "roomOccupancy", value: roomOccupancy, descriptionText: "${device.displayName} changed to ${roomOccupancy}", isStateChange: true, displayed: true)
    def statusMsg = msgTextMap[device.currentValue('roomOccupancy')] + formatLocalTime()
	sendEvent(name: "status", value: statusMsg, isStateChange: true, displayed: false)
}

private formatLocalTime(format = "EEE, MMM d yyyy @ h:mm:ss a z", time = now())		{
	def formatter = new java.text.SimpleDateFormat(format)
	formatter.setTimeZone(location.timeZone)
	return formatter.format(time)
}

private	resetTile(roomOccupancy)	{
    sendEvent(name: roomOccupancy, value: roomOccupancy, descriptionText: "reset tile ${roomOccupancy} to ${roomOccupancy}", isStateChange: true, displayed: false)
}

def generateEvent(state = null)		{
	if	(state && device.currentValue('roomOccupancy') != state)
		updateRoomOccupancy(state)
	return null
}
