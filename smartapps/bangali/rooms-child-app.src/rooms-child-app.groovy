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
            paragraph "The following settings are all optional. The corresponding actions will be skipped when any of the settings are left blank. When settings are specified they work in\
                            combination with other settings if specified.\n(scroll down for more settings ...)"
        }
        section("Change Room State to 'VACANT' On Away Mode?")		{
            input "awayModes", "mode", title: "Away Mode(s)?", required: false, multiple: true
        }
        section("Change Room State To 'OCCUPIED' On Motion?")		{
            input "motionSensors", "capability.motionSensor", title: "Motion Sensor(s)?", required: false, multiple: true
        }
        section("Turn ON Which Switches When Room Changes to 'OCCUPIED' OR Lux Falls Below Threshold?")		{
            input "switches", "capability.switch", title: "Switch(es)?", required: false, multiple: true
            input "setLevelTo", "enum", title: "Set Level When Turning On?", required: false, multiple: false, defaultValue: null,
                                                    options: [[1:"1%"],[10:"10%"],[20:"20%"],[30:"30%"],[40:"40%"],[50:"50%"],[60:"60%"],[70:"70%"],[80:"80%"],[90:"90%"],[100:"100%"]]
            input "setColorTo", "enum", title: "Hue Color?", required: false, multiple:false, defaultValue: null, options: [
                        			                                             ["Soft White":"Soft White - Default"],
                        					                                     ["White":"White - Concentrate"],
                        					                                     ["Daylight":"Daylight - Energize"],
                        					                                     ["Warm White":"Warm White - Relax"],
                        					                                      "Red","Green","Blue","Yellow","Orange","Purple","Pink"]
            input "setColorTemperatureTo", "number", title: "Set Level When Turning On?", required: false, multiple: false, defaultValue: null, range: "1500..6500"
        }
        section("Change Room to 'VACANT' After How Many Seconds Of No Motion?")		{
            input "noMotion", "number", title: "After How Many Seconds?", required: false, multiple: false, defaultValue: null, range: "5..99999"
        }
        section("Turn OFF Which Switches When Room Changes to 'VACANT' OR Lux Rises Above Threshold?\n(these can be different from the switches to turn on above.)")		{
            input "switches2", "capability.switch", title: "Which Switch(es)?", required: false, multiple: true
            input "dimTimer", "number", title: "Dim Lights For How Many Seconds Before Turning Off?", required: false, multiple: false, defaultValue: null, range: "5..99999"
            input "dimByLevel", "enum", title: "Dim Lights By What Level Before Turning Off?", required: false, multiple: false, defaultValue: null,
                                                                    options: [[10:"10%"],[20:"20%"],[30:"30%"],[40:"40%"],[50:"50%"],[60:"60%"],[70:"70%"],[80:"80%"],[90:"90%"]]
        }
        section("Use Lux Threshold To Turn On And Off Respective Switch(es) When Room IS 'OCCUPIED'?")		{
            input "luxDevice", "capability.illuminanceMeasurement", title: "Which Lux Sensor?", required: false, multiple: false
            input "luxThreshold", "number", title: "What Lux Value?", required: false, multiple: false, defaultValue: null, range: "0..*"
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
    state.noMotion = ((noMotion && noMotion >= 5) ? noMotion : 0)
	if (motionSensors)	{
    	subscribe(motionSensors, "motion.active", motionActiveEventHandler)
//    	subscribe(motionSensors, "motion.inactive", motionInactiveEventHandler)
	}
    if (switches)   {
        state.switchesHasLevel = [:]
        state.switchesHasColor = [:]
        state.switchesHasColorTemperature = [:]
        switches.each      {
            if (it.hasCommand("setLevel"))      {
                state.switchesHasLevel << [(it.getId()):true]
            }
            if (it.hasCommand("setColor"))   {
                state.switchesHasColor << [(it.getId()):true]
            }
            if (it.hasCommand("setColorTemperature"))   {
                state.switchesHasColorTemperature << [(it.getId()):true]
            }
        }
    }
    if (switches2)      {
        state.switches2HasLevel = [:]
        switches2.each      {
            if (it.hasCommand("setLevel"))      {
                state.switches2HasLevel << [(it.getId()):true]
            }
        }
        if (state.noMotion)      {
            subscribe(switches2, "switch.on", switchOnEventHandler)
//        	subscribe(switches2, "switch.off", switchOffEventHandler)
        }
    }
    state.setLevelTo = (setLevelTo ? setLevelTo as Integer : 0)
    saveHueToState()
    state.setColorTemperatureTo = (setColorTemperatureTo ?: 0)
    state.dimTimer = ((dimTimer && dimTimer >= 5) ? dimTimer : 0)
    state.dimByLevel = ((state.dimTimer && dimByLevel) ? dimByLevel as Integer : 0)
    if (luxDevice && luxThreshold)      {
        subscribe(luxDevice, "illuminance", luxEventHandler)
        state.luxEnabled = true
        state.previousLux = luxDevice.currentValue("illuminance")
    }
    else    {
        state.luxEnabled = false
        state.previousLux = null
    }
}

def	initialize()	{}

def	modeEventHandler(evt)	{
	if (awayModes && awayModes.contains(evt.value))
    	roomVacant()
}

def	motionActiveEventHandler(evt)	{
    unschedule()
	def child = getChildDevice(getRoom())
	def roomState = child.getRoomState()
    if (['checking', 'vacant'].contains(roomState))     {
		child.generateEvent('occupied')
	}
}

def	motionInactiveEventHandler(evt)     {
//    if (state.noMotion)     {
//        unschedule()
//    	runIn(state.noMotion, roomVacant)
//    }
}

def	switchOnEventHandler(evt)	{
	def child = getChildDevice(getRoom())
	def roomState = child.getRoomState()
    if (roomState == 'vacant')      {
        if (state.noMotion)
        	runIn(state.noMotion, dimLights)
    }
}

def	switchOffEventHandler(evt)  {
//    if (!('on' in switches2.currentValue("switch")))
//        unschedule()
}

def luxEventHandler(evt)    {
    def currentLux = evt.value.toInteger()
    def child = getChildDevice(getRoom())
    def roomState = child.getRoomState()
    if (['occupied', 'checking'].contains(roomState))   {
        if (luxFell(currentLux))
            switchesOn()
        else
            if (luxRose(currentLux))
                dimLights()
    }
    state.previousLux = currentLux
}

private luxFell(currentLux)   {   return (currentLux <= luxThreshold && state.previousLux > luxThreshold)  }

private luxRose(currentLux)   {   return (currentLux > luxThreshold && state.previousLux <= luxThreshold)  }

def roomVacant()	{
	def child = getChildDevice(getRoom())
	def roomState = child.getRoomState()
	if (['occupied', 'checking'].contains(roomState))
		child.generateEvent('vacant')
}

def handleSwitches(oldState = null, newState = null)	{
log.debug "room state old: ${oldState} new: ${newState}"
	if (newState && oldState != newState)      {
        if (['occupied', 'checking'].contains(newState))	{
            if (newState == 'occupied')     {
                if (!state.luxEnabled || luxDevice.currentValue("illuminance") <= luxThreshold)
                    switchesOn()
            }
            if (state.noMotion)
				runIn(state.noMotion, roomVacant)
        }
		else
			if (newState == 'vacant')    {
                unschedule()
				dimLights()
            }
		return true
	}
    else
    	return false
}

private switchesOn()	{
	if (switches)
        switches.each      {
            it.on()
            if (state.setColorTo && state.switchesHasColor[it.getId()])
                it.setColor(state.setColorTo)
            else    {
                if (state.setColorTemperatureTo && state.switchesHasColorTemperature[it.getId()])
                    it.setColorTemperature(state.setColorTemperatureTo)
            }
            if (state.setLevelTo && state.switchesHasLevel[it.getId()])
                it.setLevel(state.setLevelTo)
        }
}

def dimLights()     {
    if (state.dimTimer && state.dimByLevel)       {
        switches2.each      {
            if (it.currentValue("switch") == 'on')       {
                if (state.switches2HasLevel[it.getId()])     {
                    def currentLevel = it.currentValue("level")
                    def newLevel = (currentLevel > state.dimByLevel ? currentLevel - state.dimByLevel : 1)
                    it.setLevel(newLevel)
                }
            }
        }
        runIn(state.dimTimer, switchesOff)
    }
    else
        switchesOff()
}

def switchesOff()   {
	if (switches2)
        switches2.off()
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

def uninstalled() {
	unsubscribe()
	getChildDevices().each	{
		deleteChildDevice(it.deviceNetworkId)
	}
}

def childUninstalled()  {
log.debug "uninstalled room ${app.label}"
}

private saveHueToState()        {
    if (setColorTo)     {
        def hueColor = 0
        def saturation = 100
        switch(setColorTo)       {
            case "White":       hueColor = 52;  saturation = 19;    break;
            case "Daylight":    hueColor = 53;  saturation = 91;    break;
            case "Soft White":  hueColor = 23;  saturation = 56;    break;
            case "Warm White":  hueColor = 20;  saturation = 80;    break;
            case "Blue":        hueColor = 70;                      break;
            case "Green":       hueColor = 39;                      break;
            case "Yellow":      hueColor = 25;                      break;
            case "Orange":      hueColor = 10;                      break;
            case "Purple":      hueColor = 75;                      break;
            case "Pink":        hueColor = 83;                      break;
            case "Red":         hueColor = 100;                     break;
        }
        state.setColorTo = [hue: hueColor, saturation: saturation]
    }
    else
        state.setColorTo = null
}
