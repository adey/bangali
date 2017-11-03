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
*   6) added button push to toogle room state to engaged or checking when room state is already engaged.
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
        section		{
            if (!childCreated())
				label title: "Room Name:", required: true
            else
                paragraph "Room Name:\n${app.label}"
		}
        section		{
            paragraph "The following settings are optional. The corresponding actions will be skipped when the setting is left blank. When settings are specified they work in\
                            combination with other specified settings.\n(scroll down for more settings.)"
        }
        section("Change Room State to 'OCCUPIED' on Motion?")		{
            input "motionSensors", "capability.motionSensor", title: "Which Motion Sensor(s)?", required: false, multiple: true
        }
        section("Turn ON Which Switches When Room Changes to 'ENGAGED' or 'OCCUPIED' or Lux Falls Below Threshold?")		{
            input "switches", "capability.switch", title: "Which Switch(es)?", required: false, multiple: true
            input "setLevelTo", "enum", title: "Set Level When Turning ON?", required: false, multiple: false, defaultValue: null,
                                                    options: [[1:"1%"],[10:"10%"],[20:"20%"],[30:"30%"],[40:"40%"],[50:"50%"],[60:"60%"],[70:"70%"],[80:"80%"],[90:"90%"],[100:"100%"]]
            input "setColorTo", "enum", title: "Set Color When Turning ON?", required: false, multiple:false, defaultValue: null, options: [
                        			                                                 ["Soft White":"Soft White - Default"],
                        					                                         ["White":"White - Concentrate"],
                        					                                         ["Daylight":"Daylight - Energize"],
                        					                                         ["Warm White":"Warm White - Relax"],
                        					                                         "Red","Green","Blue","Yellow","Orange","Purple","Pink"]
            input "setColorTemperatureTo", "number", title: "Set Color Temperature When Turning ON? (if light supports color and color is specified this setting will be skipped for those light(s).)", required: false, multiple: false, defaultValue: null, range: "1500..6500"
        }
        section("Change Room to 'VACANT' After How Many Seconds on No Motion?")		{
            input "noMotion", "number", title: "After How Many Seconds?", required: false, multiple: false, defaultValue: null, range: "5..99999"
        }
        section("Change Room to 'ENGAGED' State on?")		{
            input "contactSensor", "capability.contactSensor", title: "Which Contact Sensor?", required: false, multiple: false
            input "engagedSwitch", "capability.switch", title: "Which Switch?", required: false, multiple: false
            input "engagedButton", "capability.button", title: "Which Button?", required: false, multiple: false, submitOnChange: true
            if (engagedButton)      {
                input "buttonNumber", "enum", title: "Button Number?", required: true, multiple: false, defaultValue: null, submitOnChange: true,
                                                            options: [[1:"One"],[2:"Two"],[3:"Three"],[4:"Four"],[5:"Five"],[6:"Six"],[7:"Seven"],[8:"Eight"]]
            }
            else
                paragraph "Button Number?\nselect button to set"
            input "personPresence", "capability.presenceSensor", title: "Which Presence Sensor?", required: false, multiple: false
        }
        section("Require Motion Within How Many Seconds When Room State is 'ENGAGED'?")		{
            input "noMotionEngaged", "number", title: "After How Many Seconds?", required: false, multiple: false, defaultValue: null, range: "5..99999"
        }
        section("Turn OFF which Switches when Room changes to 'VACANT' or Lux Rises above Threshold?\n(these can be different from the switches to turn on above.)")		{
            input "switches2", "capability.switch", title: "Which Switch(es)?", required: false, multiple: true
            input "dimTimer", "number", title: "Dim Lights For How Many Seconds Before Turning Off?", required: false, multiple: false, defaultValue: null, range: "5..99999"
            input "dimByLevel", "enum", title: "Dim Lights By What Level Before Turning Off?", required: false, multiple: false, defaultValue: null,
                                                                    options: [[10:"10%"],[20:"20%"],[30:"30%"],[40:"40%"],[50:"50%"],[60:"60%"],[70:"70%"],[80:"80%"],[90:"90%"]]
        }
        section("Use Lux Threshold to turn ON and OFF Switch(es) when Room is 'OCCUPIED' or 'ENGAGED'?")		{
            input "luxDevice", "capability.illuminanceMeasurement", title: "Which Lux Sensor?", required: false, multiple: false
            input "luxThreshold", "number", title: "What Lux Value?", required: false, multiple: false, defaultValue: null, range: "0..*"
        }
        section("Change Room State to 'VACANT' on Away Mode?")		{
            input "awayModes", "mode", title: "Away Mode(s)?", required: false, multiple: true
        }
        section("Pause Automation when in these Mode(s)?")		{
            input "pauseModes", "mode", title: "Pause Mode(s)?", required: false, multiple: true
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
    state.noMotionEngaged = ((noMotionEngaged && noMotionEngaged >= 5) ? noMotionEngaged : 0)
	if (motionSensors)	{
    	subscribe(motionSensors, "motion.active", motionActiveEventHandler)
    	subscribe(motionSensors, "motion.inactive", motionInactiveEventHandler)
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
    if (engagedSwitch)      {
    	subscribe(engagedSwitch, "switch.on", engagedSwitchOnEventHandler)
    	subscribe(engagedSwitch, "switch.off", engagedSwitchOffEventHandler)
	}
    if (contactSensor)      {
    	subscribe(contactSensor, "contact.open", contactOpenEventHandler)
    	subscribe(contactSensor, "contact.closed", contactClosedEventHandler)
	}
    if (engagedButton)
        subscribe(engagedButton, "button.pushed", buttonPushedEventHandler)
    if (personPresence)     {
    	subscribe(personPresence, "presence.present", presencePresentEventHandler)
        subscribe(personPresence, "presence.not present", presenceNotPresentEventHandler)
    }
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
    else
        if (pauseModes && pauseModes.contains(evt.value))
            unschedule()
}

def	motionActiveEventHandler(evt)	{
    if (pauseModes && pauseModes.contains(location.mode))
    	return
    unschedule()
	def child = getChildDevice(getRoom())
	def roomState = child.getRoomState()
    if (contactSensor && contactSensor.currentValue("contact") == 'closed')     {
        if (roomState == 'checking')
            child.generateEvent('engaged')
    }
    else    {
        if (['checking', 'vacant'].contains(roomState))
		      child.generateEvent('occupied')
        else    {
            if (newState == 'engaged' && state.noMotionEngaged)
                runIn(state.noMotionEngaged, roomVacant)
        }
    }
}

def	motionInactiveEventHandler(evt)     {
    if (pauseModes && pauseModes.contains(location.mode))
    	return
    def child = getChildDevice(getRoom())
	def roomState = child.getRoomState()
    if (!(state.noMotion) && ['occupied', 'checking'].contains(roomState))
        runIn(1, roomVacant)
}

def	switchOnEventHandler(evt)	{
    if (pauseModes && pauseModes.contains(location.mode))
    	return
	def child = getChildDevice(getRoom())
	def roomState = child.getRoomState()
    if (roomState == 'vacant')      {
        if (state.noMotion)
        	runIn(state.noMotion, dimLights)
    }
}

def	switchOffEventHandler(evt)  {
    if (pauseModes && pauseModes.contains(location.mode))
    	return
//    if (!('on' in switches2.currentValue("switch")))
//        unschedule()
}

def	engagedSwitchOnEventHandler(evt)     {
    if (pauseModes && pauseModes.contains(location.mode))
    	return
    if (personPresence && personPresence.currentValue("presence") == 'present')
        return
    unschedule()
    def child = getChildDevice(getRoom())
	def roomState = child.getRoomState()
    if (['occupied', 'checking', 'vacant'].contains(roomState))
        child.generateEvent('engaged')
}

def	engagedSwitchOffEventHandler(evt)	{
    if (pauseModes && pauseModes.contains(location.mode))
    	return
    if (personPresence && personPresence.currentValue("presence") == 'present')
        return
//    unschedule()
	def child = getChildDevice(getRoom())
	def roomState = child.getRoomState()
    if (['engaged', 'occupied', 'vacant'].contains(roomState))
		child.generateEvent('checking')
}

def	contactOpenEventHandler(evt)	{
    if (pauseModes && pauseModes.contains(location.mode))
    	return
    if (personPresence && personPresence.currentValue("presence") == 'present')
        return
    unschedule()
	def child = getChildDevice(getRoom())
	def roomState = child.getRoomState()
    if (['engaged', 'occupied', 'vacant'].contains(roomState))
		child.generateEvent('checking')
}

def	contactClosedEventHandler(evt)     {
    if (pauseModes && pauseModes.contains(location.mode))
    	return
    if (personPresence && personPresence.currentValue("presence") == 'present')
        return
    unschedule()
    def child = getChildDevice(getRoom())
    def roomState = child.getRoomState()
    if (motionSensors)      {
        if (['occupied', 'vacant'].contains(roomState))
            child.generateEvent('checking')
    }
    else    {
        if (['occupied', 'checking', 'vacant'].contains(roomState))
            child.generateEvent('engaged')
    }
}

def	buttonPushedEventHandler(evt)     {
    if ((pauseModes && pauseModes.contains(location.mode)) || buttonNumber != evt.value)
    	return
    unschedule()
    def child = getChildDevice(getRoom())
    def roomState = child.getRoomState()
    if (roomState == 'engaged')
        child.generateEvent('checking')
    else
        child.generateEvent('engaged')
}

def	presencePresentEventHandler(evt)     {
    if (pauseModes && pauseModes.contains(location.mode))
    	return
    unschedule()
    def child = getChildDevice(getRoom())
    def roomState = child.getRoomState()
    if (['occupied', 'checking', 'vacant'].contains(roomState))
        child.generateEvent('engaged')
}

def	presenceNotPresentEventHandler(evt)     {
    if (pauseModes && pauseModes.contains(location.mode))
    	return
    def child = getChildDevice(getRoom())
    def roomState = child.getRoomState()
    if (['engaged', 'occupied', 'vacant'].contains(roomState))
    	child.generateEvent('checking')
}

def luxEventHandler(evt)    {
    if (pauseModes && pauseModes.contains(location.mode))
    	return
    def currentLux = evt.value.toInteger()
    def child = getChildDevice(getRoom())
    def roomState = child.getRoomState()
    if (['engaged', 'occupied', 'checking'].contains(roomState))   {
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
    if (pauseModes && pauseModes.contains(location.mode))
        return
	if (newState && oldState != newState)      {
//      unschedule()
        if (['engaged', 'occupied', 'checking'].contains(newState))     {
            if (['engaged', 'occupied'].contains(newState))     {
                if (!state.luxEnabled || luxDevice.currentValue("illuminance") <= luxThreshold)
                    switchesOn()
            }
            if (newState == 'engaged')      {
                if (state.noMotionEngaged)
                    runIn(state.noMotionEngaged, roomVacant)
            }
            else
                if (state.noMotion)
                    runIn(state.noMotion, roomVacant)
                else
                    if (newState == 'checking')
                        runIn(1, roomVacant)
//            }
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
