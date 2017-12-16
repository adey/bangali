/*****************************************************************************************************************
*
*  A SmartThings smartapp to create/view rooms created with rooms occupancy DTH.
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
*  Name: Room Manager
*  Source: https://github.com/adey/bangali/blob/master/smartapps/bangali/rooms-manager.src/rooms-manager.groovy
*
*  Version: 0.08.5
*
*   DONE:   12/16/2017
*   1) added support for arrival and departure announcement.
*   2) added support for speaker control through rules and use of speaker to set a room to engaged.
*   3) bug fix to stop truncating temperature to integer.
*
*  Version: 0.08.3
*
*   DONE:   12/12/2017
*   1) added support for wake and sleep times to calculate level and color temperature.
*   2) add support to process rules every 15 minutes so switches state/level/color temperature is updated.
*   3) fix for continuous motion with motion sensor.
*
*  Version: 0.08.1
*
*   DONE:   12/10/2017
*   1) added support for auto level which automatically calculates light level and optionally color temperature to
*       to be set based on local sunrise and sunset times. this does not yet use circadian rhytym based calculation.
*
*  Version: 0.08.0
*
*   DONE:   12/8/2017
*   1) added support to reset room state from ENAGED or ASLEEP when another room changes to ENGAGED or ASLEEP
*   2) added support to reset room state when another room changes to ENGAGED or ASLEEP.
*   3) removed lux threshold support from main settings since this is now available under rules.
*   4) fixed presence indicator for device display.
*   5) added support for multiple engaged switches.
*   6) added undimming for lights.
*	7) added support for centigrade display.
*   8) added support for multiple presence sensors.
*   9) couple of bug fixes.
*
*  Version: 0.07.5
*
*   DONE:   12/5/2017
*   1) added support to reset room state from ENGAGED or ASLEEP when another room changes to ENGAGED or ASLEEP
*   2) added right temperature scale support
*   3) fixed couple of bugs
*   4) added support for date filtering in rules
*
*  Version: 0.07.3
*
*   DONE:   12/2/2017
*   1) added support for executing piston instead of just turning on a light
*   2) added view all settings
*   3) added room device indicators to the room device so they can be seen in one place
*   4) added timer to room which counts down in increments of 5
*   5) some bug fixes.
*
*  Version: 0.07.0
*
*   DONE:   11/27/2017
*   1) instead of adding swtiches to individual settings created rules to allow switches to be turned on and off
*       and routines to be executed via this rule. VACANT state automatically turns of the switches the last rule
*       turned on unless user creates a rule for VACANT state in which case the automatic turning off of switches
*       on VACANT state is skipped instead the rules are checked and executed for the VACANT state.
*   2) some bug fixes.
*
*  Version: 0.05.9
*
*   DONE:   11/21/2017
*   1) changed name of 'occupancyStatus' to just 'occupancy' to be consistent with ST.
*   2) added switches to turn on and off when room chnages to asleep. switches set to turn on are also turned off
*           when room changes away from asleep.
*   2) some bug fixes.
*
*  Version: 0.05.8
*
*   DONE:   11/20/2017
*   1) Changed configuration pages
*
*  Version: 0.05.7
*
*   DONE:   11/20/2017
*   1) added support for room busy check and setting ENGAGED state based on how busy room is.
*   2) added support for arrival and/or departure action when using presence sensor.
*   3) some bug fixes.
*
*  Version: 0.05.5
*
*   DONE:   11/19/2017
*   1) added sleepSensor feature and corresponding settings by https://github.com/Johnwillliam.
*   2) some bug fixes.
*
*  Version: 0.05.2
*
*   DONE:   11/16/2017
*   1) changed from 10 to 12 device settings and added adjacent rooms to devices display.
*   2) some bug fixes.
*
*  Version: 0.05.1
*
*   DONE:   11/15/2017
*   1) added setting to select which days of week this rooms automation should run.
*
*  Version: 0.05.0
*
*   DONE:   11/13/2017
*   1) expanded the adjacent room settings. if you specify adjacent rooms you can choose 2 options:
*       i) if there is motion in an adjacent room you can force the current room to check for motion and on no
*           motion change room state to vacant.
*      ii) if there is motion in an adjacent room you can turn on lights in this room if it is currently vacant.
*           this allows for the adjacent rooms feature to be used as a light your pathway can kind of setup.
*   2) some bug fixes.
*
*  Version: 0.04.6
*
*   DONE:   11/12/2017
*   1) bug fixes around contact sensors..
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

private isDebug()   {  return true  }

private ifDebug(msg = null)     {  if (msg && isDebug()) log.debug msg  }

definition (
    name: "rooms manager",
    namespace: "bangali",
    author: "bangali",
    description: "Create rooms",
    category: "My Apps",
    singleInstance: true,
    iconUrl: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomOccupancy.png",
    iconX2Url: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomOccupancy@2x.png",
    iconX3Url: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomOccupancy@3x.png"
)

preferences	{
    page(name: "mainPage", content: "mainPage")
    page(name: "pageSpeakerSettings", content: "pageSpeakerSettings")
}

def mainPage()  {
    dynamicPage(name: "mainPage", title: "Installed Rooms", install: false, uninstall: true, submitOnChange: true, nextPage: "pageSpeakerSettings") {
		section {
            app(name: "Rooms Manager", appName: "rooms child app", namespace: "bangali", title: "New Room", multiple: true)
		}
	}
}

def pageSpeakerSettings()   {
    def i = (presenceSensors ? presenceSensors.size() : 0)
    def str = (presenceNames ? presenceNames.split(',') : [])
    def j = str.size()
    if (i != j)
    sendNotification("Count of presense sensors and names do not match!", [method: "push"])
    dynamicPage(name: "pageSpeakerSettings", title: "Speaker Settings", install: true, uninstall: true)     {
		section   {
            input "speakerAnnounce", "bool", title: "Announce when presence sensors return?", required: false, multiple: false, defaultValue: false, submitOnChange: true
            if (speakerAnnounce)    {
                input "presenceSensors", "capability.presenceSensor", title: "Which presence snesors?", required: true, multiple: true
                input "presenceNames", "text", title: "Comma delmited names? (in sequence of presence sensors)", required: true, multiple: false, submitOnChange: true
                input "speakerDevices", "capability.audioNotification", title: "Which speakers?", required: true, multiple: true
                input "speakerVolume", "number", title: "Speaker volume?", required: true, multiple: false, defaultValue: 33, range: "1..100"
                input "contactSensors", "capability.contactSensor", title: "Which contact sensors?", required: true, multiple: true
            }
            else    {
                paragraph "Which presence sensors?\nselect announce to set."
                paragraph "Comma delmited names?\nselect announce to set."
                paragraph "Which speakers?\nselect announce to set."
                paragraph "Speaker volume?\nselect announce to set."
                paragraph "Which contact sensors?\nselect announce to set."
            }
        }
	}
}

def installed()		{	initialize()	}

def updated()		{
	unsubscribe()
    unschedule()
	initialize()
    if (speakerAnnounce)    {
        def i = presenceSensors.size()
        def str = presenceNames.split(',')
        def j = str.size()
        ifDebug("i: $i | str: $str | j: $j")
        if (i == j)     {
            i = 0
            presenceSensors.each        {
                state.whoCameHome.personNames << [(it.getId()):(i <= j ? str[i].trim() : '')]
                i = i + 1
            }
            if (presenceSensors)     {
                subscribe(presenceSensors, "presence.present", presencePresentEventHandler)
                subscribe(presenceSensors, "presence.not present", presenceNotPresentEventHandler)
            }
            subscribe(contactSensors, "contact.closed", contactClosedEventHandler)
        }
    }
    runEvery15Minutes(processChildSwitches)
}

def initialize()	{
	log.info "rooms manager: there are ${childApps.size()} rooms."
	childApps.each	{ child ->
		log.info "rooms manager: room: ${child.label} id: ${child.id}"
	}
    state.whoCameHome = [:]
    state.whoCameHome.personsIn = []
    state.whoCameHome.personsOut = []
    state.whoCameHome.personNames = [:]
}

def	presencePresentEventHandler(evt)     {
    whoCameHome(evt.device)
}

def	presenceNotPresentEventHandler(evt)     {
    whoCameHome(evt.device, true)
}

def contactClosedEventHandler(evt = null)     {
    if ((evt && !state.whoCameHome.personsIn) || (!evt && !state.whoCameHome.personsOut))     return;
    def str = (evt ? state.whoCameHome.personsIn : state.whoCameHome.personsOut)
    def i = str.size()
    def j = 1
    def persons = (evt ? 'Welcome home ' : '')
    str.each      {
        persons = persons + (j != 1 ? (j == i ? ' and ' : ', ') : '') + it
        j = j + 1
    }
    persons = persons + (evt ? '' : ' left home')
    speakerDevices.playTextAndResume(persons, speakerVolume as Integer)
    if (evt)
        state.whoCameHome.personsIn = []
    else
        state.whoCameHome.personsOut = []
}

def whoCameHome(presenceSensor, left = false)      {
    if (!presenceSensor)      return;
    def presenceName = state.whoCameHome.personNames[(presenceSensor.getId())]
    if (!presenceName)      return;
    ifDebug("presenceName: $presenceName")
/*    if (left)       {
        if (state.whoCameHome.persons && state.whoCameHome.persons.contains(presenceName))
            state.whoCameHome.persons.remove(presenceName)
        return
    }*/
    long nowTime = now()
    long howLong
    if (!left)      {
        if (state.whoCameHome.personsIn)      {
            howLong = nowTime - state.whoCameHome.lastOne
            if (howLong > 300000L)
                state.whoCameHome.personsIn = []
        }
        state.whoCameHome.lastOne = nowTime
        if (!state.whoCameHome.personsIn || !(state.whoCameHome.personsIn.contains(presenceName)))
            state.whoCameHome.personsIn << presenceName
    }
    else    {
        if (!state.whoCameHome.personsOut || !(state.whoCameHome.personsOut.contains(presenceName)))
            state.whoCameHome.personsOut << presenceName
        runIn(30, contactClosedEventHandler)
    }
}

def subscribeChildrenToEngaged(childID,roomID)     {
    if (!state.onEngaged)
        state.onEngaged = [:]
    if (state.onEngaged[(roomID)])
        state.onEngaged.remove(roomID)
    state.onEngaged << [(roomID):(childID)]
}

def notifyAnotherRoomEngaged(roomID)   {
log.debug "notifyAnotherRoomEngaged: $roomID"
    def childID = state.onEngaged[(roomID)]
    if (childID)   {
        childApps.each	{ child ->
            if (childID == child.id)
                child.anotherRoomEngagedEventHandler()
        }
    }
}

def getRoomNames(childID)    {
    def roomNames = [:]
    childApps.each	{ child ->
        if (childID != child.id)
            roomNames << [(child.id):(child.label)]
	}
    return (roomNames.sort { it.value })
}

def getARoomName(childID)    {
    def roomName = null
    childApps.each	{ child ->
        if (childID == child.id)
            roomName = child.label
	}
    return roomName
}

def handleAdjRooms()    {
//  adjRoomDetails = ['childid':app.id, 'adjrooms':adjRooms]
    def skipAdjRoomsMotionCheck = true
    def adjRoomDetailsMap = [:]
    childApps.each	{ childAll ->
        def adjRoomDetails = childAll.getAdjRoomDetails()
        def childID = adjRoomDetails['childid']
        def adjRooms = adjRoomDetails['adjrooms']
        adjRoomDetailsMap << [(childID):(adjRooms)]
        if (adjRooms)
            skipAdjRoomsMotionCheck = false
    }
    if (skipAdjRoomsMotionCheck)
        return false
    childApps.each	{ childAll ->
//        def adjRoomDetails = childAll.getAdjRoomDetails()
        def childID = childAll.id
        def adjRooms = adjRoomDetailsMap[childID]
        def adjMotionSensors = []
        def adjMotionSensorsNames = []
        if (childID && adjRooms)    {
            childApps.each	{ child ->
                if (childID != child.id && adjRooms.contains(child.id))      {
                    def motionSensors = child.getAdjMotionSensors()
                    if (motionSensors)  {
                        motionSensors.each  {
                            def motionSensorName = it.getName()
                            if (!(adjMotionSensorsNames.contains(motionSensorName)))   {
                                adjMotionSensors << it
                                adjMotionSensorsNames << motionSensorName
                            }
                        }
                    }
                }
            }
        }
log.debug "rooms manager: updating room $childAll.label"
log.debug "$adjMotionSensors"
        childAll.updateRoom(adjMotionSensors)
    }
    return true
}

def getLastStateDate(childID)      {
    def nowDate = new Date()
    def lastStateDate = [:]
    if (childID)    {
        childApps.each	{ child ->
            if (childID == child.id)    {
                def lastStateDateChild = child.getLastStateChild()
                lastStateDate = lastStateDateChild
            }
        }
    }
    return lastStateDate
}

def processChildSwitches()      {
    int i = 1
    childApps.each	{ child ->
//        runIn(i, child.turnOnAndOffSwitches, [overwrite: false])
        child.turnOnAndOffSwitches()
        i = i + 1
    }
}
