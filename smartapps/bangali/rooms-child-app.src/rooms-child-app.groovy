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
*  Source: https://github.com/adey/bangali/blob/master/smartapps/bangali/rooms-child-app.src/rooms-child-app.groovy
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

definition	(
    name: "rooms child app",
    namespace: "bangali",
    parent: "bangali:rooms manager",
    author: "bangali",
    description: "DO NOT INSTALL DIRECTLY. Rooms child smartapp to create new rooms using 'rooms occupancy' DTH from Rooms Manager smartapp.",
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
    	} else {
			section		{
				paragraph "Room Name:\n${app.label}"
			}
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
            input "noMotion", "number", title: "After How Many Seconds?", required: false, multiple: true, defaultValue: null, range: "5..*"
        }
        section("Turn Off Switches When Room Changes to 'VACANT'?")		{
            input "switches2", "capability.switch", title: "Which Switch(es)?", required: false, multiple: true
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
    state.noMotion = (noMotion ? (noMotion[0].toInteger() >= 5 ? noMotion[0].toInteger() : 5) : 0)
	if (motionSensors)	{
    	subscribe(motionSensors, "motion.active", motionActiveEventHandler)
    	subscribe(motionSensors, "motion.inactive", motionInactiveEventHandler)
	}
    else    {
        if (state.noMotion && switches2)      {
            subscribe(switches2, "switch.on", switchOnEventHandler)
        	subscribe(switches2, "switch.off", switchOffEventHandler)
        }
    }
}

def	initialize()	{}

def uninstalled() {
	unsubscribe()
	getChildDevices().each	{
		deleteChildDevice(it.deviceNetworkId)
	}
}

def childUninstalled()  {
log.debug "uninstalled room ${app.label}"
}

def	modeEventHandler(evt)	{
	if (awayModes && awayModes.contains(evt.value))
    	roomVacant()
}

def	motionActiveEventHandler(evt)	{
	def child = getChildDevice(getRoom())
	def roomState = child.getRoomState()
    if (['checking', 'vacant'].contains(roomState))	{
		child.generateEvent('occupied')
		if (roomState == 'vacant')
			switchesOn()
	}
//    if (noMotion)
//    	unschedule()
}

def	motionInactiveEventHandler(evt)     {
//	def motionTimeout = noMotion[0].toInteger()
    if (state.noMotion)
    	runIn(state.noMotion, roomVacant)
}

def	switchOnEventHandler(evt)	{
	def child = getChildDevice(getRoom())
	def roomState = child.getRoomState()
    if (roomState == 'vacant')      {
//        def switchTimeout = noMotion[0].toInteger()
        if (state.noMotion)
        	runIn(state.noMotion, switchesOff)
    }
}

def	switchOffEventHandler(evt)	{
    if (!('on' in switches2.currentSwitch))
        unschedule()
}

def roomVacant()	{
	def child = getChildDevice(getRoom())
	def roomState = child.getRoomState()
	if (['occupied', 'checking'].contains(roomState))	{
		child.generateEvent('vacant')
		switchesOff()
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

def handleSwitches(oldState = null, newstate = null)	{
	if (newState && oldState != newState)	{
		if (newState == 'occupied')
			switchesOn()
		else
			if (newState == 'vacant')
				switchesOff()
        unschedule()
        if (['occupied', 'checking'].contains(newState))	{
//            def switchTimeout = noMotion[0].toInteger()
            if (state.noMotion)
                runIn(state.noMotion, switchesOff)
        }
		return true
	}
    else
    	return false
}

private switchesOn()	{
	if (switches)
		switches.on()
}

def switchesOff()   {
	if (switches2)
        switches2.off()
}
