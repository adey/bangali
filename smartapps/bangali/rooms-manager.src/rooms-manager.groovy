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
*  Name: Rooms Manager
*  Source: https://github.com/adey/bangali/blob/master/smartapps/bangali/rooms-manager.src/rooms-manager.groovy
*
*****************************************************************************************************************/

public static String version()      {  return "v0.11.5"  }
private static boolean isDebug()    {  return true  }

/*****************************************************************************************************************
*
*  Version: 0.11.5
*
*   DONE:   2/5/2018
*   1) added setting for locked state timeout setting.
*   2) on motion active added check for power value to set room to engaged instead of occupied.
*   3) on occupied switch check power value to set room to engaged instead of occupied.
*   4) on contact close check for both occupied and checking state to set room to engaged.
*   5) for motion inactive with multiple motion sensors check all sensors for active before setting timer.
*
*  Version: 0.11.0
*
*   DONE:   2/1/2018
// TODO
*   1) added support for time announce function. straightforward annoucement for now but likely to get fancier ;-)
*   2) added rule name to display in rules page.
*   3) added support for power value stays below a certain number of seconds before triggering engaged or asleep.
*   4) added support for vacant switch. except this sets room to vacant when turned OFF not ON.
*   5) changed speaker device to music player in the rooms setup.
*   6) added support in rules to control window shade.
*
*  Version: 0.10.7
*
*   DONE:   1/26/2018
*   1) added support for switch to set room to locked.
*   2) added support for random welcome home and left home messages. multiple messages can be specified delimited
*       by comma and one of them will be randomly picked when making the annoucement.
*   3) added support for switch to set room to asleep.
*
*  Version: 0.10.6
*
*   DONE:   1/24/2018
*   1) added support for power value to set room to asleep.
*
*  Version: 0.10.5
*
*   DONE:   1/23/2018
*   1) added rules support for maintaining temperature.
*
*  Version: 0.10.0
*
*   DONE:   1/18/2018
*   1) added one page easy settings for first time users.
*
*  Version: 0.09.9
*
*   DONE:   1/14/2018
*   1) added variable years to date filter.
*
*  Version: 0.09.8
*
*   MERGED:   1/12/2018
*   1) added switches for occupied state and corresponding settings by https://github.com/TonyFleisher.
*
*  Version: 0.09.7
*
*   DONE:   1/11/2018
*   1) addeed night switches control from device tiles indicators
*   2) added setting to keep room in engaged state based on continuous presence and not just presence change.
*   3) refactored how another room engaged works and checks for continuous presence before reseting room state.
// TODO
*   4) added resetting of asleep state to engaged state reset. will probably make that an option later.
// TODO
*   5) started work on adding thermostate to maintain room temperature. going to change this to use rules
*       which will require a significant change to how rules work so wanted to push everything else out before
*       starting the work to change maintain room temperature to use rules.
*   6) added another optimization when getting rules to allow getting conditions only.
*   7) move is busy check to motion handler instead of downstream.
*   8) added multiple rule processing with the following evaluation logic:
*       a) if matching rules have no lux and no time all of those rules will be executed.
*       b) if matching rules has lux the rule with the lowest lux value < current lux value will be
*           executed. if there are multiple matching rules with the same lux value all of them will be executed.
*       c) if matching rules has time all rules that match that current time will be executed.
*       d) if matching rules have lux and time the rule with the lowest lux value < current lux value and
*           matching time will be executed. if there are multiple matching rules with the same lux
*           value and matching time all of them will be executed.
*   9) timer indicator now uses minutes when time is over 60 seconds.
*   10) fixed a few small bugs here and there.
*
*  Version: 0.09.4
*
*   DONE:   12/30/2017
*   1) updated device tiles layout and added a bunch of indicators.
*   2) added checking state to room busy check.
*
*  Version: 0.09.2
*
*   DONE:   12/25/2017
*   1) added option to temporarily override motion timers with rules.
*   2) added support for button to set room to asleep.
*   3) added checks for interval processing of rules.
*   4) some optimizations and bug fix.
*
*  Version: 0.09.0
*
*   DONE:   12/23/2017
*   1) added color coding for temperature indicator. since ST does not allow device handler display to be conditional
*       for celcius color coding user will need to edit the DTH and uncomment the celcius section and comment the
*       Fahrenheit values.
*   2) added support for room AC and heater support to maintain room temperature. support for thermostat is coming.
*   3) moved all stanalone devices to their own settings page.
*   4) added setting to indiciate if contact sensor is on inside door or outside. e.g. contact sesnor on garage door
*       would be an outside door contact sesnor. this reverses the occupancy logic so when contact sensor is open
*       the door is engaged or occupied instead of when the door is closed.
*   5) added support for button to set room to vacant.
*   6) moved webCoRE_init call to the bottom of the updated() method.
*   7) couple of bug fixes.
*
*  Version: 0.08.6
*
*   DONE:   12/17/2017
*   1) added support for variable text for arrival and departure announcements.
*   2) added support for power level to set room to engaged.
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
            app(name: "rooms manager", appName: "rooms child app", namespace: "bangali", title: "New Room", multiple: true)
		}
	}
}

def pageSpeakerSettings()   {
    def i = (presenceSensors ? presenceSensors.size() : 0)
    def str = (presenceNames ? presenceNames.split(',') : [])
    def j = str.size()
    if (presenceNames && i != j)     sendNotification("Count of presense sensors and names do not match!", [method: "push"]);
    dynamicPage(name: "pageSpeakerSettings", title: "Presence & Announcement Settings", install: true, uninstall: true)     {
		section   {
			input "presenceSensors", "capability.presenceSensor", title: "Which presence sensors?", required: true, multiple: true
			input "speakerDevices", "capability.audioNotification", title: "Which speakers?", required: false, multiple: true, submitOnChange: true
			input "speechSynthesizers", "capability.speechSynthesis", title: "Which synthesizers?", required: false, multiple: true, submitOnChange: true
			
            if (speakerDevices)     {
                input "speakerVolume", "number", title: "Speaker volume?", required: false, multiple: false, defaultValue: 33, range: "1..100"			
            }
            else        {
                paragraph "Speaker volume?\nselect speaker(s) to set."
            }
			if (speakerDevices || speechSynthesizers)
				input "speakerAnnounce", "bool", title: "Announce when presence sensors arrive or depart?", required: false, multiple: false, defaultValue: false, submitOnChange: true
			else
				paragraph "Announce when presence sensors arrive or depart?\nselect speaker/synths to set."

			if ((speakerDevices || speechSynthesizers) && speakerAnnounce)    {
                input "presenceSensors", "capability.presenceSensor", title: "Which presence snesors?", required: true, multiple: true
                input "presenceNames", "text", title: "Comma delmited names? (in sequence of presence sensors)", required: true, multiple: false, submitOnChange: true
                input "contactSensors", "capability.contactSensor", title: "Which contact sensors? (welcome home greeting is played after this contact sensor closes.)", required: true, multiple: true
                input "welcomeHome", "text", title: "Welcome home greeting? (& will be replaced with the names.)", required: true, multiple: false, defaultValue: 'Welcome home &'
                input "secondsAfter", "number", title: "Seconds after? (left home annoucement is made after this many seconds.)", required: true, multiple: false, defaultValue: 15, range: "5..100"
                input "leftHome", "text", title: "Left home announcement? (& will be replaced with the names.)", required: true, multiple: false, defaultValue: '& left home'
            }
            else    {
                paragraph "Which presence sensors?\nselect announce to set."
                paragraph "Comma delmited names?\nselect announce to set."
                paragraph "Which speakers?\nselect announce to set."
                paragraph "Speaker volume?\nselect announce to set."
                paragraph "Which contact sensors?\nselect announce to set."
                paragraph "Welcome home greeting?\nselect announce to set."
                paragraph "Seconds after?\nselect announce to set."
                paragraph "Left home announcement?\nselect announce to set."
            }
            if (speakerDevices || speechSynthesizers)
                input "timeAnnounce", "enum", title: "Announce time?", required: false, multiple: false, defaultValue: 4,
                                options: [[1:"Every 15 minutes"], [2:"Every 30 minutes"], [3:"Every hour"], [4:"No"]], submitOnChange: true
            else
                paragraph "Announce time?\nselect speakers/synths to set."
        }
        section     {
            if (speakerAnnounce || ['1', '2', '3'].contains(timeAnnounce))        {
                input "startHH", "number", title: "Annouce from hour?", required: true, multiple: false, defaultValue: 7, range: "1..${endHH ? endHH : 23}", submitOnChange: true
                input "endHH", "number", title: "Announce to hour?", required: true, multiple: false, defaultValue: 7, range: "${startHH ? startHH : 23}..23", submitOnChange: true
            }
            else        {
                paragraph "Announce from hour?\nselect either presence or time annoucement to set"
                paragraph "Announce to hour?\nselect either presence or time annoucement to set"
            }
        }
	}
}

def installed()		{  initialize()  }

def updated()		{
	unsubscribe()
    unschedule()
	initialize()
    announceSetup()
    runEvery15Minutes(processChildSwitches)
    if (timeAnnounce != '4')    schedule("0 0/15 * 1/1 * ? *", tellTime)
}

def initialize()	{
    unsubscribe()
	log.info "rooms manager: there are ${childApps.size()} rooms."
	childApps.each	{ child ->
		log.info "rooms manager: room: ${child.label} id: ${child.id}"
        def childRoomDevice = getChildRoomDeviceObject(child.id)
        ifDebug("initialize: childRoomDevice: $childRoomDevice")
        subscribe(childRoomDevice, "button.pushed", buttonPushedEventHandler)
	}
    state.whoCameHome = [:]
    state.whoCameHome.personsIn = []
    state.whoCameHome.personsOut = []
    state.whoCameHome.personNames = [:]
}

private announceSetup() {
    if (!speakerAnnounce)   return;
    def i = presenceSensors.size()
    def str = presenceNames.split(',')
    def j = str.size()
    if (i == j)     {
        i = 0
        presenceSensors.each        {
            state.whoCameHome.personNames << [(it.getId()):(i < j ? str[i].trim() : '')]
            i = i + 1
        }
        if (presenceSensors)     {
            subscribe(presenceSensors, "presence.present", presencePresentEventHandler)
            subscribe(presenceSensors, "presence.not present", presenceNotPresentEventHandler)
        }
        if (contactSensors)     subscribe(contactSensors, "contact.closed", contactClosedEventHandler);
    }
    state.welcomeHome1 = [:]
    state.welcomeHome2 = [:]
    str = welcomeHome.split(',')
    i = 0
    str.each    {
        def str2 = it.split('&')
        state.welcomeHome1[i] = str2[0]
        state.welcomeHome2[i] = (str2.size() > 1 ? str2[1] : '')
        i = i + 1
    }
    state.leftHome1 = [:]
    state.leftHome2 = [:]
    str = leftHome.split(',')
    i = 0
    str.each    {
        def str2 = it.split('&')
        state.leftHome1[i] = str2[0]
        state.leftHome2[i] = (str2.size() > 1 ? str2[1] : '')
        i = i + 1
    }
}

def buttonPushedEventHandler(evt)     {
    ifDebug("buttonPushedEventHandler")
}

def getChildRoomDeviceObject(childID)     {
    def roomDeviceObject = null
    childApps.each	{ child ->
        if (childID == child.id)        roomDeviceObject = child.getChildRoomDevice();
	}
    ifDebug("getChildRoomDeviceObject: childID: $childID | roomDeviceObject: $roomDeviceObject")
    return roomDeviceObject
}

def	presencePresentEventHandler(evt)     {  whoCameHome(evt.device)  }

def	presenceNotPresentEventHandler(evt)     {  whoCameHome(evt.device, true)  }

def contactClosedEventHandler(evt = null)     {
    if ((evt && !state.whoCameHome.personsIn) || (!evt && !state.whoCameHome.personsOut))     return;
    def str = (evt ? state.whoCameHome.personsIn : state.whoCameHome.personsOut)
    def i = str.size()
    def j = 1
    def k = (state.welcomeHome1 ? Math.abs(new Random().nextInt() % state.welcomeHome1.size()) : 0) + ''
    def l = (state.leftHome1 ? Math.abs(new Random().nextInt() % state.leftHome1.size()) : 0) + ''
//    ifDebug("k: $k ${state.welcomeHome1[(k)]} | l: $l ${state.leftHome1[(l)]}")
    def persons = (evt ? state.welcomeHome1[(k)] : state.leftHome1[(l)]) + ' '
    str.each      {
        persons = persons + (j != 1 ? (j == i ? ' and ' : ', ') : '') + it
        j = j + 1
    }
    persons = persons + ' ' + (evt ? state.welcomeHome2[(k)] : state.leftHome2[(l)])
//    ifDebug("k: $k ${state.welcomeHome2[(k)]} | l: $l ${state.leftHome2[(l)]}")
    ifDebug("message: $persons")
    def nowDate = new Date(now())
    def intCurrentHH = nowDate.format("HH", location.timeZone) as Integer
    if (intCurrentHH >= startHH && intCurrentHH <= endHH)
    {
    	if (speakerDevices)
        	speakerDevices.playTextAndResume(persons, speakerVolume)
        if (speechSynthesizers)
        	speechSynthesizers.speak(persons)
    }
    if (evt)    state.whoCameHome.personsIn = [];
    else        state.whoCameHome.personsOut = [];
}

def whoCameHome(presenceSensor, left = false)      {
    if (!presenceSensor)    return;
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
            if (howLong > 300000L)      state.whoCameHome.personsIn = [];
        }
        state.whoCameHome.lastOne = nowTime
        if (!state.whoCameHome.personsIn || !(state.whoCameHome.personsIn.contains(presenceName)))
            state.whoCameHome.personsIn << presenceName
    }
    else    {
        if (!state.whoCameHome.personsOut || !(state.whoCameHome.personsOut.contains(presenceName)))
            state.whoCameHome.personsOut << presenceName
        runIn(secondsAfter as Integer, contactClosedEventHandler)
    }
}

/*
def subscribeChildrenToEngaged(childID, roomID)     {
    ifDebug("subscribeChildrenToEngaged: childID: $childID | roomID: $roomID")
    if (!state.onEngaged)       state.onEngaged = [:];
    if (roomID)     {
        if (state.onEngaged[(roomID)])      state.onEngaged.remove(roomID);
        state.onEngaged << [(roomID):(childID)]
    }
}


def notifyAnotherRoomEngaged(roomID)   {
    ifDebug("notifyAnotherRoomEngaged: $roomID")
    def childID = state.onEngaged[(roomID)]
    if (childID)   {
        childApps.each	{ child ->
            if (childID == child.id)        child.anotherRoomEngagedEventHandler();
        }
    }
}
*/

def getRoomNames(childID)    {
    def roomNames = [:]
    childApps.each	{ child ->
        if (childID != child.id)        roomNames << [(child.id):(child.label)];
	}
    return (roomNames.sort { it.value })
}

def getARoomName(childID)    {
    def roomName = null
    childApps.each	{ child ->
        if (childID == child.id)        roomName = child.label;
	}
    return roomName
}

/*
def handleAdjRooms()    {
    ifDebug("handleAdjRooms")
//  adjRoomDetails = ['childid':app.id, 'adjrooms':adjRooms]
    def skipAdjRoomsMotionCheck = true
    def adjRoomDetailsMap = [:]
    childApps.each	{ childAll ->
        def adjRoomDetails = childAll.getAdjRoomDetails()
        def childID = adjRoomDetails['childid']
        def adjRooms = adjRoomDetails['adjrooms']
        adjRoomDetailsMap << [(childID):(adjRooms)]
        if (adjRooms)       skipAdjRoomsMotionCheck = false;
    }
    if (skipAdjRoomsMotionCheck)        return false;
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
        ifDebug("rooms manager: updating room $childAll.label")
        ifDebug("$adjMotionSensors")
        childAll.updateRoom(adjMotionSensors)
    }
    return true
}
*/

def handleAdjRooms()    {
    ifDebug("handleAdjRooms")
//  adjRoomDetails = ['childid':app.id, 'adjrooms':adjRooms]
    def skipAdjRoomsMotionCheck = true
    def adjRoomDetailsMap = [:]
    childApps.each	{ childAll ->
        def adjRooms = childAll.getAdjRoomsSetting()
        adjRoomDetailsMap << [(childAll.id):(adjRooms)]
        if (adjRooms)       skipAdjRoomsMotionCheck = false;
    }
    if (skipAdjRoomsMotionCheck)        return false;
    childApps.each	{ childAll ->
//        def adjRoomDetails = childAll.getAdjRoomDetails()
        def childID = childAll.id
        def adjRooms = adjRoomDetailsMap[childID]
        def adjMotionSensors = []
        def adjMotionSensorsIds = []
        if (adjRooms)    {
            childApps.each	{ child ->
                if (childID != child.id && adjRooms.contains(child.id))      {
                    def motionSensors = child.getAdjMotionSensors()
                    if (motionSensors)  {
                        motionSensors.each  {
                            def motionSensorId = it.getId()
                            if (!adjMotionSensorsIds.contains(motionSensorId))   {
                                adjMotionSensors << it
                                adjMotionSensorsIds << motionSensorId
                            }
                        }
                    }
                }
            }
        }
        ifDebug("rooms manager: updating room $childAll.label")
        ifDebug("$adjMotionSensors")
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
//        runIn(i, child.switchesOnOrOff, [overwrite: false])
        if (child.checkRoomModesAndDoW())       child.switchesOnOrOff(true);
        i = i + 1
    }
}

def tellTime()      {
    ifDebug("tellTime")
    def nowDate = new Date(now())
    def intCurrentMM = nowDate.format("mm", location.timeZone) as Integer
    def intCurrentHH = nowDate.format("HH", location.timeZone) as Integer
// TODO
    if ((timeAnnounce == '1' || (timeAnnounce == '2' && (intCurrentMM == 0 || intCurrentMM == 30)) ||
        (timeAnnounce == '3' && intCurrentMM == 0)) && intCurrentHH >= startHH && (intCurrentHH < endHH || (intCurrentHH == endHH && intCurrentMM == 0)))       {
        def timeString = 'time is ' + (intCurrentMM == 0 ? '' : intCurrentMM + ' minutes past ') +
                                                            intCurrentHH + (intCurrentHH < 12 ? ' oclock' : ' hundred hours')
// TODO
//        speakerDevices.playTrackAndResume("http://s3.amazonaws.com/smartapp-media/sonos/bell1.mp3",
//                                            (intCurrentMM == 0 ? 10 : (intCurrentMM == 15 ? 4 : (intCurrentMM == 30 ? 6 : 8))), speakerVolume)
//        pause(1000)
        if (speakerDevices)
        	speakerDevices.playTextAndResume(timeString, speakerVolume)
        if (speechSynthesizers)
        	speechSynthesizers.speak(timeString)
    }
}

private ifDebug(msg = null, level = null)     {  if (msg && (isDebug() || level))  log."${level ?: 'debug'}" msg  }
