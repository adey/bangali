/*****************************************************************************************************************
*
*  A SmartThings child smartapp which creates the "room" device using the rooms occupancy DTH.
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
*  Name: Room Child App
*  Source: https://github.com/adey/bangali/blob/master/smartapp/rooms%20child%20app.groovy
*  Version: 0.02
*
*   DONE:
*   1) added support for multiple away modes. when home changes to any these modes room is set to vacant but
*            only if room is in occupied or checking state.
*   2) added subscription for motion devices so if room is vacant or checking move room state to occupied.
*   3) added support for switches to be turned on when room is changed to occupied.
*   4) added support for switches to be turned off when room is changed to vacant, different switches from #3.
*   5) added button push events to tile commands, where occupied = button 1, ..., kaput = button 6 so it is 
*            supported by ST Smart Lighting smartapp.
*
*****************************************************************************************************************/

definition	(
    name: "rooms child app",
    namespace: "bangali",
    parent: "bangali:rooms manager",
    author: "bangali",
    description: "DO NOT INSTALL DIRECTLY OR PUBLISH. Rooms child smartapp to create new rooms using 'rooms occupancy' DTH from Rooms Manager smartapp.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
)

preferences {
	page(name: "roomName")
}

def roomName()	{
	dynamicPage(name: "roomName", title: "Room Name", install: true, uninstall: childCreated())		{
		if (!childCreated())	{
			section		{
				label title: "Room Name:", required: true
			}
			section		{
				paragraph "The following settings are all optional. The corresponding actions will be skipped when any of the settings are left blank. (scroll down for more settings ...)"
			}
			section("Update Room State On Away Mode?")		{
 				input "awayModes", "mode", title: "Away Mode(s)?", required: false, multiple: true
			}
			section("Change Room State To 'OCCUPIED' On Motion?")		{
 				input "motionSensors", "capability.motionSensor", title: "Which Motion Sensor(s)?", required: false, multiple: true
			}
			section("Turn On Switches When Room Changes to 'OCCUPIED'?")		{
 				input "switches", "capability.switch", title: "Which Switch(es)?", required: false, multiple: true
			}
			section("Change Room to 'VACANT' When No Motion?")		{
 				input "noMotion", "number", title: "After How Many Seconds?", required: false, multiple: true, defaultValue: 90, range: "5..*"
			}
			section("Turn Off Switches When Room Changes to 'VACANT'?")		{
 				input "switches2", "capability.switch", title: "Which Switch(es)?", required: false, multiple: true
			}
    	} else {
			section		{
				paragraph "Room Name:\n${app.label}"
			}
			section		{
				paragraph "The following settings are all optional. The corresponding actions will be skipped when any of the settings are left blank. (scroll down for more settings ...)"
			}
			section("Update Room State On Away Mode?")		{
 				input "awayModes", "mode", title: "Away Mode(s)?", required: false, multiple: true
			}
			section("Change Room State To 'OCCUPIED' On Motion?")		{
 				input "motionSensors", "capability.motionSensor", title: "Motion Sensor(s)?", required: false, multiple: true
			}
			section("Turn On Switches When Room Changes to 'OCCUPIED'?")		{
 				input "switches", "capability.switch", title: "Switch(es)?", required: false, multiple: true
			}
			section("Change Room to 'VACANT' When No Motion?")		{
 				input "noMotion", "number", title: "After How Many Seconds?", required: false, multiple: true, defaultValue: 90, range: "5..*"
			}
			section("Turn Off Switches When Room Changes to 'VACANT'?")		{
 				input "switches2", "capability.switch", title: "Which Switch(es)?", required: false, multiple: true
			}
		}
	}
}

def installed()		{}

def updated()	{
	unsubscribe()
	initialize()
	if (!childCreated())	{
		spawnChildDevice(app.label)
	}
	if (awayModes)	{
		subscribe(location, modeEventHandler)
	}
	if (motionSensors)	{
    	subscribe(motionSensors, "motion.active", motionActiveEventHandler)
    	subscribe(motionSensors, "motion.inactive", motionInactiveEventHandler)
	}
}

def	initialize()	{}

def	modeEventHandler(evt)	{
	if (awayModes && awayModes.contains(evt.value))
    	roomVacant()
}

def	motionActiveEventHandler(evt)	{
	def child = getChildDevice(getRoom())
	def state = child.getRoomState()
    if (['checking', 'vacant'].contains(state))	{
		child.generateEvent('occupied')
		if (state == 'vacant')
			switchesOn()
	}
    if (noMotion)
    	unschedule()
}

def	motionInactiveEventHandler(evt)	{
	def nMI = noMotion[0].toInteger()
    if (noMotion && nMI > 5)
    	runIn(nMI, roomVacant)
}

def roomVacant()	{
	def child = getChildDevice(getRoom())
	def state = child.getRoomState()
	if (['occupied', 'checking'].contains(state))	{
		child.generateEvent('vacant')
		switchesOff()
	}
}

def uninstalled() {
	unsubscribe()
	getChildDevices().each	{
		deleteChildDevice(it.deviceNetworkId)
	}
}

def spawnChildDevice(roomName)	{
	app.updateLabel(app.label)
	if (!childCreated())
		def child = addChildDevice("bangali", "rooms occupancy", getRoom(), null, [name: getRoom(), label: roomName, completedSetup: true])
}

private childCreated()		{
	if (getChildDevice(getRoom()))
		return true
	else
		return false
}

private getRoom()	{	return "rm_${app.id}"	}

def handleSwitches(oldState = null, state = null)	{
	if (state && oldState != state)	{
		if (state == 'occupied')
			switchesOn()
		else
			if (state == 'vacant')
				switchesOff()
		return true
	}
    else
    	return false
}

private switchesOn()	{
	if (switches)
		switches.on()
}

private switchesOff()	{
	if (switches2)
		switches2.off()
}
