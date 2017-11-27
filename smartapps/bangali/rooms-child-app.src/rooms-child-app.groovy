/*****************************************************************************************************************
*
*  A SmartThings child smartapp which creates the "room" device using the rooms occupancy DTH.
*  Copyright (C) 2017 bangali
*
*  Contributors:
*   https://github.com/Johnwillliam
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
*   1) bug fixes around contact sensors.
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
*   DONE:   11/5/2017
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

definition	(
    name: "rooms child app",
    namespace: "bangali",
    parent: "bangali:rooms manager",
    author: "bangali",
    description: "DO NOT INSTALL DIRECTLY. Rooms child smartapp to create new rooms using 'rooms occupancy' DTH from Rooms Manager smartapp.",
    category: "My Apps",
    iconUrl: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomOccupancy.png",
    iconX2Url: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomOccupancy@2x.png",
    iconX3Url: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomOccupancy@3x.png"
)

preferences {
	page(name: "roomName", title: "Room Name and Settings")
    page(name: "pageOccupiedSettings", title: "Occupied Mode Settings")
    page(name: "pageLuxTimeSettings", title: "Lux & Time Settings")
    page(name: "pageRules", title: "Maintain Lighting Rules")
    page(name: "pageRule", title: "Edit Lighting Rule")
    page(name: "pageEngagedSettings", title: "Engaged Mode Settings")
    page(name: "pageNightMode", title: "Night Mode Settings")
    page(name: "pageAdjacentRooms", title: "Adjacent Rooms Settings")
    page(name: "pageGeneralSettings", title: "General Settings")
}

def roomName()	{
    def roomNames = parent.getRoomNames(app.id)
    def timeSettings = (fromTimeType || toTimeType)
    def adjRoomSettings = (adjRooms ? true : false)
    def miscSettings = (awayModes || pauseModes || dayOfWeek)
    def engagedSettings = (busyCheck || engagedButton || buttonIs || personPresence || engagedSwitch || contactSensor || noMotionEngaged)
    def luxSettings = (luxSensor || luxThreshold)
    def luxAndTimeSettings = (luxSettings || timeSettings)
    def asleepSettings = (asleepSensor || nightSwitches)
    state.passedOn = false
	dynamicPage(name: "roomName", title: "Room Name", install: true, uninstall: childCreated())		{
        section		{
            if (!childCreated())
				label title: "Room Name:", required: true
            else
                paragraph "Room Name:\n${app.label}"
		}
        section		{
            paragraph "FOLLOWING SETTINGS ARE ALL OPTIONAL. CORRESPONDING ACTIONS WILL BE SKIPPED WHEN SETTING IS BLANK. WHEN SPECIFIED SETTINGS WORK IN COMBINATION WHEN THAT MAKES SENSE."
        }
        section("MOTION SENSOR CONFIGURATION", hideable: true, hidden: (!motionSensors))		{
            input "motionSensors", "capability.motionSensor", title: "Which motion sensor?", required: false, multiple: true, submitOnChange: true
            input "noMotion", "number", title: "Motion timeout after how many seconds?", required: false, multiple: false, defaultValue: null, range: "5..99999", submitOnChange: true
            if (noMotion)
                input "whichNoMotion", "enum", title: "Use which motion event?", required: true, multiple: false, defaultValue: 2, submitOnChange: true,
                                                                                        options: [[1:"Last Motion Active"],[2:"Last Motion Inactive"]]
            else
                paragraph "Use which motion event?\nselect number of seconds above to set"
        }
//        section("TURN ON SWITCHES WHEN ROOM CHANGES TO 'ENGAGED' OR 'OCCUPIED'?\n(works with rules below.)", hideable: true, hidden: (!switches))		{
//            input "switches", "capability.switch", title: "Which switches?", required: false, multiple: true
/*            input "setLevelTo", "enum", title: "Set level when Turning ON?", required: false, multiple: false, defaultValue: null,
                                                    options: [[1:"1%"],[10:"10%"],[20:"20%"],[30:"30%"],[40:"40%"],[50:"50%"],[60:"60%"],[70:"70%"],[80:"80%"],[90:"90%"],[100:"100%"]]
            input "setColorTo", "enum", title: "Set color when turning ON?", required: false, multiple:false, defaultValue: null, options: [
                        			                                                 ["Soft White":"Soft White - Default"],
                        					                                         ["White":"White - Concentrate"],
                        					                                         ["Daylight":"Daylight - Energize"],
                        					                                         ["Warm White":"Warm White - Relax"],
                        					                                         "Red","Green","Blue","Yellow","Orange","Purple","Pink"]
            input "setColorTemperatureTo", "number", title: "Set color temperature when turning ON? (if light supports color and color is specified this setting will be ignored.)",
                                                                                    required: false, multiple: false, defaultValue: null, range: "1500..6500"*/
//        }
        section("Dimming settings before room changes to VACANT.", hideable: true, hidden: (!dimTimer))		{
//            input "switches2", "capability.switch", title: "Which switches?", required: false, multiple: true
            input "dimTimer", "number", title: "Dim lights for how many seconds?", required: false, multiple: false, defaultValue: null, range: "5..99999", submitOnChange: true
            if (dimTimer)
                input "dimByLevel", "enum", title: "Dim lights by what level?", required: false, multiple: false, defaultValue: null,
                                                                    options: [[10:"10%"],[20:"20%"],[30:"30%"],[40:"40%"],[50:"50%"],[60:"60%"],[70:"70%"],[80:"80%"],[90:"90%"]]
            else
                paragraph "Dim lights by what level?\nselect dim timer above to set"
        }
        section("") {
				href "pageLuxTimeSettings", title: "LUX & TIME SETTINGS", description: (luxAndTimeSettings ? "Tap to change existing settings" : "Tap to configure")
		}
        section("") {
				href "pageRules", title: "LIGHTING RULES", description: "Maintain rules"
		}
        section("") {
				href "pageEngagedSettings", title: "ENGAGED SETTINGS", description: (engagedSettings ? "Tap to change existing settings" : "Tap to configure")
		}
        section("") {
				href "pageNightMode", title: "ASLEEP STATE SETTINGS", description: (asleepSettings ? "Tap to change existing settings" : "Tap to configure")
		}
        section("") {
				href "pageAdjacentRooms", title: "ADJACENT ROOMS SETTINGS", description: (adjRoomSettings ? "Tap to change existing settings" : "Tap to configure")
		}
        section("") {
				href "pageGeneralSettings", title: "MODE AND DAY OF WEEK SETTINGS", description: (miscSettings ? "Tap to change existing settings" : "Tap to configure")
		}
        remove("Remove Room", "Remove Room ${app.label}")
	}
}

private pageRules()     {
    updateRulesToState()
    state.passedOn = false
	dynamicPage(name: "pageRules", title: "", install: false, uninstall: false)    {
//        state.rules = [1:[ruleNo:1, name:'Rule 1', mode:location.currentMode, state:null, level:50, ct:2700, color:[saturation:80,hue:20]]]
        section()   {
            def emptyRule = null
            if (!state.rules)   {
                emptyRule = 1
//                state.rules = [:]
            }
            else    {
                def i = 1
                for (; i < 11; i++)     {
                    def ruleNo = String.valueOf(i)
                    def thisRule = getRule(ruleNo, false)
                    if (thisRule)   {
                        def ruleDesc = "$ruleNo:"
                        ruleDesc = (thisRule.mode ? "$ruleDesc Mode=$thisRule.mode" : "$ruleDesc")
                        ruleDesc = (thisRule.state ? "$ruleDesc State=$thisRule.state" : "$ruleDesc")
                        ruleDesc = (thisRule.luxThreshold ? "$ruleDesc Rule Lux=$thisRule.luxThreshold" : (luxThreshold ? "$ruleDesc Lux=$luxThreshold" : "$ruleDesc"))
                        ruleDesc = (thisRule.actions ? "$ruleDesc Routines=$thisRule.actions" : "$ruleDesc")
                        if (thisRule.fromTimeType && thisRule.toTimeType)        {
                            def ruleFromTimeHHmm = (thisRule.fromTime ? timeToday(thisRule.fromTime).format('HH:mm', location.timeZone) : '')
                            def ruleToTimeHHmm = (thisRule.toTime ? timeToday(thisRule.toTime).format('HH:mm', location.timeZone) : '')
                            ruleDesc = (thisRule.fromTimeType == timeTime() ? "$ruleDesc From Time=$ruleFromTimeHHmm" : (thisRule.fromTimeType == timeSunrise() ? "$ruleDesc From Time=Sunrise" : "$ruleDesc From Time=Sunset"))
                            ruleDesc = (thisRule.toTimeType == timeTime() ? "$ruleDesc To Time=$ruleToTimeHHmm" : (thisRule.toTimeType == timeSunrise() ? "$ruleDesc To Time=Sunrise" : "$ruleDesc To Time=Sunset"))
                        }
                        ruleDesc = (thisRule.switchesOn ? "$ruleDesc ON=$thisRule.switchesOn" : "$ruleDesc")
                        ruleDesc = (thisRule.switchesOff ? "$ruleDesc OFF=$thisRule.switchesOff" : "$ruleDesc")
                        ruleDesc = (thisRule.disabled ? "$ruleDesc Disabled=$thisRule.disabled" : "$ruleDesc")
                        href "pageRule", title: "$ruleDesc", params: [ruleNo: "$ruleNo"], required: false
//                        href "pageRule", description: "$thisRule.name", title: "$ruleDesc", params: [ruleNo: "$ruleNo"], required: false
                    }
                    else
                        if (!emptyRule)
                            emptyRule = i
                }
/*                for (def rule in state.rules.sort{ it.key })    {
                    def thisRule = rule.value
                    ifDebug("$thisRule")
                    href "pageRule", description: "${thisRule.name}", title: "${thisRule.ruleNo}", params: [ruleNo: "${thisRule.ruleNo}"], required: false
                    i++
                }*/
            }
            if (emptyRule)
                href "pageRule", title: "Create new rule", params: [ruleNo: emptyRule], required: false
            else
                paragraph "At max number of rules: 10"
        }
	}
}

private pageRule(params)   {
    if (!state.passedOn && params)      {
        state.passedOn = true
        state.passedParams = params
    }
    if (params.ruleNo)
        state.pageruleNo = params.ruleNo
    else if (state.passedParams)
        state.pageruleNo = state.passedParams.ruleNo
    def ruleNo = state.pageruleNo
    def ruleToTimeType = settings["toTimeType$ruleNo"]
    def ruleFromTimeType = settings["fromTimeType$ruleNo"]
    def allActions = location.helloHome?.getPhrases()*.label
    if (allActions)    allActions.sort();
//    def ruleLuxSensor = settings["luxSensor$ruleNo"]
    dynamicPage(name: "pageRule", title: "", install: false, uninstall: false)   {
        section()     {
            ifDebug("rule number page ${ruleNo}")
            paragraph "$ruleNo"
			input "name$ruleNo", "text", title: "Rule name?", required:false, multiple: false, capitalization: "none"
            input "disabled$ruleNo", "bool", title: "Rule disabled?", required: false, multiple: false, defaultValue: false
			input "mode$ruleNo", "mode", title: "Which mode?", required: false, multiple: true
            input "state$ruleNo", "enum", title: "Which state?", required: false, multiple: true,
                    options: ['asleep', 'engaged', 'occupied', 'vacant']
            input "dayOfWeek$ruleNo", "enum", title: "Which days of the week?", required: false, multiple: false, defaultValue: null,
                                                options: [[null:"All Days of Week"],[8:"Monday to Friday"],[9:"Saturday & Sunday"],[2:"Monday"],\
                                                          [3:"Tuesday"],[4:"Wednesday"],[5:"Thursday"],[6:"Friday"],[7:"Saturday"],[1:"Sunday"]]
//            input "luxSensor$ruleNo", "capability.illuminanceMeasurement", title: "Which lux sensor?", required: false, multiple: false, submitOnChange: true
            if (luxSensor && luxThreshold)
                input "luxThreshold$ruleNo", "number", title: "What lux value?", required: false, multiple: false, defaultValue: luxThreshold, range: "0..$luxThreshold"
            else
                paragraph "What lux value?\nset lux sensor and lux threshold in main settings to select."
            if (ruleToTimeType)
                input "fromTimeType$ruleNo", "enum", title: "Choose from time type?", required: true, multiple: false, defaultValue: null, submitOnChange: true, options: [[1:"Sunrise"],[2:"Sunset"],[3:"Time"]]
            else
                input "fromTimeType$ruleNo", "enum", title: "Choose from time type?", required: false, multiple: false, defaultValue: null, submitOnChange: true, options: [[1:"Sunrise"],[2:"Sunset"],[3:"Time"]]
            if (ruleFromTimeType == '3')
                input "fromTime$ruleNo", "time", title: "From time?", required: true, multiple: false, defaultValue: null
            else
                paragraph "From time?\nchange from time type to time to select"
            if (ruleFromTimeType)
                input "toTimeType$ruleNo", "enum", title: "Choose to time type?", required: true, multiple: false, defaultValue: null, submitOnChange: true, options: [[1:"Sunrise"],[2:"Sunset"],[3:"Time"]]
            else
                input "toTimeType$ruleNo", "enum", title: "Choose to time type?", required: false, multiple: false, defaultValue: null, submitOnChange: true, options: [[1:"Sunrise"],[2:"Sunset"],[3:"Time"]]
            if (ruleToTimeType == '3')
                input "toTime$ruleNo", "time", title: "To time?", required: true, multiple: false, defaultValue: null
            else
                paragraph "To time?\nchange to time type to time to select"
            input "actions$ruleNo", "enum", title: "Routines to execute?", required: false, multiple: true, defaultValue: null, options: allActions
            input "switchesOn$ruleNo", "capability.switch", title: "Turn ON which switches?", required: false, multiple: true
/*            input "setLevelTo", "enum", title: "Set level when Turning ON?", required: false, multiple: false, defaultValue: null,
                                        options: [[1:"1%"],[10:"10%"],[20:"20%"],[30:"30%"],[40:"40%"],[50:"50%"],[60:"60%"],[70:"70%"],[80:"80%"],[90:"90%"],[100:"100%"]]
input "setColorTo", "enum", title: "Set color when turning ON?", required: false, multiple:false, defaultValue: null, options: [
                                                                         ["Soft White":"Soft White - Default"],
                                                                         ["White":"White - Concentrate"],
                                                                         ["Daylight":"Daylight - Energize"],
                                                                         ["Warm White":"Warm White - Relax"],
                                                                         "Red","Green","Blue","Yellow","Orange","Purple","Pink"]
input "setColorTemperatureTo", "number", title: "Set color temperature when turning ON? (if light supports color and color is specified this setting will be ignored.)",
                                                                        required: false, multiple: false, defaultValue: null, range: "1500..6500"*/
            input "setLevelTo$ruleNo", "enum", title: "Set level when Turning ON?", required: false, multiple: false, defaultValue: null,
                    options: [[1:"1%"],[10:"10%"],[20:"20%"],[30:"30%"],[40:"40%"],[50:"50%"],[60:"60%"],[70:"70%"],[80:"80%"],[90:"90%"],[100:"100%"]]
            input "setColorTo$ruleNo", "enum", title: "Set color when turning ON?", required: false, multiple:false, defaultValue: null, options: [
                            			                                                 ["Soft White":"Soft White - Default"],
                            					                                         ["White":"White - Concentrate"],
                            					                                         ["Daylight":"Daylight - Energize"],
                            					                                         ["Warm White":"Warm White - Relax"],
                            					                                         "Red","Green","Blue","Yellow","Orange","Purple","Pink"]
            input "setColorTemperatureTo$ruleNo", "number", title: "Set color temperature when turning ON? (if light supports color and color is specified this setting will be ignored.)",
                                                                                        required: false, multiple: false, defaultValue: null, range: "1500..6500"
            input "switchesOff$ruleNo", "capability.switch", title: "Turn OFF which switches?", required: false, multiple: true
        }
    }
}

private pageEngagedSettings() {
    def buttonNames = [[1:"One"],[2:"Two"],[3:"Three"],[4:"Four"],[5:"Five"],[6:"Six"],[7:"Seven"],[8:"Eight"],[9:"Nine"],[10:"Ten"],[11:"Eleven"],[12:"Twelve"]]
    def engagedButtonOptions = [:]
    if (engagedButton)      {
        def engagedButtonAttributes = engagedButton.supportedAttributes
        def attributeNameFound = false
        engagedButtonAttributes.each  { att ->
            if (att.name == 'numberOfButtons')
                attributeNameFound = true
        }
        def numberOfButtons = engagedButton.currentValue("numberOfButtons")
        if (attributeNameFound && numberOfButtons)      {
            def i = 0
            for (; i < numberOfButtons; i++)
                engagedButtonOptions << buttonNames[i]
        }
        else
            engagedButtonOptions << [null:"No buttons"]
    }

	dynamicPage(name: "pageEngagedSettings", title: "", install: false, uninstall: false) {
		section("CHANGE ROOM TO 'ENGAGED' WHEN?\n(if specified this will also reset room state to 'vacant' when the button is pushed again or presence sensor changes to not present etc.)", hideable: false)		{
            if (motionSensors)
                input "busyCheck", "enum", title: "When room is busy?", required: false, multiple: false, defaultValue: null,
                                                                            options: [[null:"No auto engaged"],[3:"Light traffic"],[5:"Medium Traffic"],[7:"Heavy Traffic"]]
            else
                paragraph "When room is busy?\nselect motion sensor(s) above to set."
            input "engagedButton", "capability.button", title: "Button is pushed?", required: false, multiple: false, submitOnChange: true
            if (engagedButton)
                input "buttonIs", "enum", title: "Button Number?", required: true, multiple: false, defaultValue: null, options: engagedButtonOptions
            else
                paragraph "Button Number?\nselect button to set"
            input "personPresence", "capability.presenceSensor", title: "Presence sensor?", required: false, multiple: false, submitOnChange: true
            if (personPresence)
                input "presenceAction", "enum", title: "Arrival or Departure or Both?", required: true, multiple: false, defaultValue: 3,
                                                    options: [[1:"Set state to ENGAGED on Arrival"],[2:"Set state to VACANT on Departure"],[3:"Both actions"]]
            else
                paragraph "Arrival or Departure or Both??\nselect presence sensor above to set"
            input "engagedSwitch", "capability.switch", title: "Switch turns ON?", required: false, multiple: false
            input "contactSensor", "capability.contactSensor", title: "Contact sensor closes?", required: false, multiple: false
            input "noMotionEngaged", "number", title: "Require motion within how many seconds when room is 'ENGAGED'?", required: false, multiple: false, defaultValue: null, range: "5..99999"
            input "resetEngagedDirectly", "bool", title: "When resetting room from 'ENGAGED' directly move to 'VACANT' state?", required: false, multiple: false, defaultValue: false
        }
	}
}

private pageLuxTimeSettings() {
	dynamicPage(name: "pageLuxTimeSettings", title: "", install: false, uninstall: false) {
		section("LUX SENSOR TO TURN ON AND OFF SWITCHES WHEN ROOM IS 'OCCUPIED' OR 'ENGAGED'?", hideable: false)		{
            input "luxSensor", "capability.illuminanceMeasurement", title: "Which lux sensor?", required: false, multiple: false, submitOnChange: true
            if (luxSensor)
                input "luxThreshold", "number", title: "What lux value?", required: false, multiple: false, defaultValue: null, range: "0..*"
            else
                paragraph "What lux value?\nset lux sensor to select"
        }
/*        section("TIME RANGE TO TURN ON AND OFF SWITCHES WHEN ROOM IS 'OCCUPIED' OR 'ENGAGED'?", hideable: false)      {
            if (toTimeType)
                input "fromTimeType", "enum", title: "Choose from time type?", required: true, multiple: false, defaultValue: null, submitOnChange: true, options: [[1:"Sunrise"],[2:"Sunset"],[3:"Time"]]
            else
                input "fromTimeType", "enum", title: "Choose from time type?", required: false, multiple: false, defaultValue: null, submitOnChange: true, options: [[1:"Sunrise"],[2:"Sunset"],[3:"Time"]]
            if (fromTimeType == '3')
                input "fromTime", "time", title: "From time?", required: true, multiple: false, defaultValue: null, submitOnChange: true
            else
                paragraph "From time?\nchange from time type to time to select"
            if (fromTimeType)
                input "toTimeType", "enum", title: "Choose to time type?", required: true, multiple: false, defaultValue: null, submitOnChange: true, options: [[1:"Sunrise"],[2:"Sunset"],[3:"Time"]]
            else
                input "toTimeType", "enum", title: "Choose to time type?", required: false, multiple: false, defaultValue: null, submitOnChange: true, options: [[1:"Sunrise"],[2:"Sunset"],[3:"Time"]]
            if (toTimeType == '3')
                input "toTime", "time", title: "To time?", required: true, multiple: false, defaultValue: null, submitOnChange: true
            else
                paragraph "To time?\nchange to time type to time to select"
        }*/
        section("TURN OFF LAST SWITCHES ON IN ROOM WHEN LUX VALUE RISES OR TIME IS OUTSIDE OF DEFINED RULES?", hideable: false)		{
            input "allSwitchesOff", "bool", title: "Turn OFF last switches?", required: false, multiple: false, defaultValue: false
        }
	}
}

private pageNightMode() {
    def buttonNames = [[1:"One"],[2:"Two"],[3:"Three"],[4:"Four"],[5:"Five"],[6:"Six"],[7:"Seven"],[8:"Eight"],[9:"Nine"],[10:"Ten"],[11:"Eleven"],[12:"Twelve"]]
    def nightButtonOptions = [:]
    if (nightButton)      {
        def nightButtonAttributes = nightButton.supportedAttributes
        def attributeNameFound = false
        nightButtonAttributes.each  { att ->
            if (att.name == 'numberOfButtons')
                attributeNameFound = true
        }
        def numberOfButtons = nightButton.currentValue("numberOfButtons")
        if (attributeNameFound && numberOfButtons)      {
            def i = 0
            for (; i < numberOfButtons; i++)
                nightButtonOptions << buttonNames[i]
        }
        else
            nightButtonOptions << [null:"No buttons"]
    }
	dynamicPage(name: "pageNightMode", title: "", install: false, uninstall: false) {
        section("SETTINGS FOR 'ASLEEP' STATE INCLUDING SWITCHES TO TURN ON AND OFF, MOTION DETECTED NIGHT LIGHTS AND BUTTON TO TURN ON AND OFF NIGHT LIGHTS.", hideable: false)		{
//            input "asleepOnSwitches", "capability.switch", title: "Turn ON which Switches when room changes to ASLEEP?", required: false, multiple: true
//            input "asleepOffSwitches", "capability.switch", title: "Turn OFF which Switches when room changes to ASLEEP?", required: false, multiple: true
	    	input "asleepSensor", "capability.sleepSensor", title: "Sleep sensor to change room state to ASLEEP?", required: false, multiple: false
            input "noAsleep", "number", title: "Timeout ASLEEP state after how many hours?", required: false, multiple: false, defaultValue: null, range: "1..99"
            if (motionSensors)
                input "nightSwitches", "capability.switch", title: "Turn ON which Switches when room state is ASLEEP and there is Motion?", required: false, multiple: true, submitOnChange: true
            else
                paragraph "Turn ON which Switches when room state is ASLEEP and there is Motion?\nselect motion sensor(s) above to set."
            if (nightSwitches)      {
//                input "nightSetLevelTo", "enum", title: "Set Level When Turning ON?", required: false, multiple: false, defaultValue: null,
//                                                    options: [[1:"1%"],[10:"10%"],[20:"20%"],[30:"30%"],[40:"40%"],[50:"50%"],[60:"60%"],[70:"70%"],[80:"80%"],[90:"90%"],[100:"100%"]]
                input "nightButton", "capability.button", title: "Button to toggle ASLEEP state Switches?", required: false, multiple: false, submitOnChange: true
                if (nightButton)
                    input "nightButtonIs", "enum", title: "Button Number?", required: true, multiple: false, defaultValue: null, options: nightButtonOptions
                else
                    paragraph "Button Number?\nselect button above to set"
            }
            else        {
//                paragraph "Set Level When Turning ON?\nselect switches above to set"
                paragraph "Button to toggle Night Switches?\nselect switches rooms above to set"
                paragraph "Button Number?\nselect button above to set"
            }
        }
	}
}

private pageAdjacentRooms() {
	def roomNames = parent.getRoomNames(app.id)
	dynamicPage(name: "pageAdjacentRooms", title: "", install: false, uninstall: false) {
		section("ADJACENT ROOMS ALLOWS FOR ACTION WHEN THERE IS MOTION IN ADJACENT ROOMS.", hideable: false)		{
            input "adjRooms", "enum", title: "Adjacent Rooms?", required: false, multiple: true, options: roomNames, submitOnChange: true
            if (adjRooms)   {
                input "adjRoomsMotion", "bool", title: "If motion in adjacent room check if person is still in this room?", required: false, multiple: false, defaultValue: false
                input "adjRoomsPathway", "bool", title: "If moving through room turn on switches in adjacent rooms?", required: false, multiple: false, defaultValue: false
            }
            else    {
                paragraph "If motion in adjacent room check if person still in this room?\nselect adjacent rooms above to set"
                paragraph "If moving through room turn on switches in adjacent rooms?\nselect adjacent rooms above to set"
            }
        }
	}
}

private pageGeneralSettings() {
	dynamicPage(name: "pageGeneralSettings", title: "", install: false0, uninstall: false) {
		section("MODE SETTINGS FOR AWAY AND PAUSE MODES?", hideable: false)		{
            input "awayModes", "mode", title: "Away modes to set Room to 'VACANT'?", required: false, multiple: true
            input "pauseModes", "mode", title: "Modes in which to pause automation?", required: false, multiple: true
        }
        section("RUN ROOMS AUTOMATION ON WHICH DAYS OF THE WEEK.\n(WHEN BLANK RUNS ON ALL DAYS.)", hideable: false)		{
            	input "dayOfWeek", "enum", title: "Which days of the week?", required: false, multiple: false, defaultValue: null,
						                                                    options: [[null:"All Days of Week"],[8:"Monday to Friday"],[9:"Saturday & Sunday"],[2:"Monday"],\
                                                                                      [3:"Tuesday"],[4:"Wednesday"],[5:"Thursday"],[6:"Friday"],[7:"Saturday"],[1:"Sunday"]]
		}
	}
}

def installed()		{}

def updated()	{
    if (!childCreated())	{
		spawnChildDevice(app.label)
	}
    def adjMotionSensors = parent.handleAdjRooms()
    ifDebug("adjusted adjacent rooms")
    def child = getChildDevice(getRoom())
    def adjRoomNames = []
    adjRooms.each  {  adjRoomNames << parent.getARoomName(it)  }
    def busyCheckDisplay = (state.busyCheck == 3 ? ['Light traffic'] : (state.busyCheck == 5 ? ['Medium traffic'] : (state.busyCheck == 7 ? ['Heavy traffic'] : [])))
    def devicesMap = ['busyCheck':busyCheckDisplay, 'engagedButton':engagedButton, 'presence':personPresence, 'engagedSwitch':engagedSwitch, 'contactSensor':contactSensor,
                      'motionSensors':motionSensors, 'luxSensor':luxSensor, 'adjRoomNames':adjRoomNames,
                      'sleepSensor':asleepSensor, 'nightButton':nightButton, 'nightSwitches':nightSwitches, 'awayModes':awayModes, 'pauseModes':pauseModes]

    child.deviceList(devicesMap)
//    child.deviceList(personPresence, engagedButton, engagedSwitch, contactSensor, motionSensors, switches, switches2, luxSensor)
}

def updateRoom(adjMotionSensors)     {
	unsubscribe()
    unschedule()
	initialize()
    state.clear()
	if (awayModes)	{
		subscribe(location, modeEventHandler)
	}
    state.noMotion = ((noMotion && noMotion >= 5) ? noMotion : 0)
    state.noMotionEngaged = ((noMotionEngaged && noMotionEngaged >= 5) ? noMotionEngaged : 0)
	if (motionSensors)	{
    	subscribe(motionSensors, "motion.active", motionActiveEventHandler)
    	subscribe(motionSensors, "motion.inactive", motionInactiveEventHandler)
	}
    if (adjMotionSensors && (adjRoomsMotion || adjRoomsPathway))   {
        subscribe(adjMotionSensors, "motion.active", adjMotionActiveEventHandler)
        subscribe(adjMotionSensors, "motion.inactive", adjMotionInactiveEventHandler)
    }
    state.switchesHasLevel = [:]
    state.switchesHasColor = [:]
    state.switchesHasColorTemperature = [:]
//    state.setLevelTo = (setLevelTo ? setLevelTo as Integer : 0)
//    saveHueToState()
//    state.setColorTemperatureTo = (setColorTemperatureTo ?: 0)
    state.dimTimer = ((dimTimer && dimTimer >= 5) ? dimTimer : 0)
    state.dimByLevel = ((state.dimTimer && dimByLevel) ? dimByLevel as Integer : null)
    if (engagedSwitch)      {
    	subscribe(engagedSwitch, "switch.on", engagedSwitchOnEventHandler)
    	subscribe(engagedSwitch, "switch.off", engagedSwitchOffEventHandler)
	}
    if (contactSensor)      {
    	subscribe(contactSensor, "contact.open", contactOpenEventHandler)
    	subscribe(contactSensor, "contact.closed", contactClosedEventHandler)
	}
    state.busyCheck = (busyCheck ? busyCheck as Integer : null)
    if (engagedButton)
        subscribe(engagedButton, "button.pushed", buttonPushedEventHandler)
    if (personPresence)     {
    	subscribe(personPresence, "presence.present", presencePresentEventHandler)
        subscribe(personPresence, "presence.not present", presenceNotPresentEventHandler)
    }
    if (luxSensor)      {
        subscribe(luxSensor, "illuminance", luxEventHandler)
//        state.luxEnabled = true
        state.previousLux = luxSensor.currentValue("illuminance")
    }
    else    {
//        state.luxEnabled = false
        state.previousLux = null
    }
    if (asleepSensor)
        subscribe(asleepSensor, "sleeping", sleepEventHandler)
    if (nightButton)
        subscribe(nightButton, "button.pushed", nightButtonPushedEventHandler)
    if (nightSwitches)   {
        nightSwitches.each      {
            if (it.hasCommand("setLevel"))
                state.switchesHasLevel << [(it.getId()):true]
        }
    }
    state.nightSetLevelTo = (nightSetLevelTo ? nightSetLevelTo as Integer : null)
    state.noAsleep = ((noAsleep && noAsleep >= 1) ? (noAsleep * 60 * 60) : 0)
    if (dayOfWeek)      {
        state.dayOfWeek = []
        switch(dayOfWeek)       {
            case '1':   case '2':   case '3':   case '4':   case '5':   case '6':   case '7':
                        state.dayOfWeek << dayOfWeek;                       break;
            case '8':   [1,2,3,4,5].each    { state.dayOfWeek << it };      break;
            case '9':   [6,7].each          { state.dayOfWeek << it };      break;
            default:    state.dayOfWeek = null;                             break;
        }
    }
    else
        state.dayOfWeek = null
    updateRulesToState()
    runIn(1, scheduleFromToTimes)
}

def updateRulesToState()    {
    def rulesMap = [:]
    state.timeCheck = false
    state.vacant = false
    state.previousRuleNo = null
    state.rules = false
//    state.rules = [1:[ruleNo:1, name:'Rule 1', mode:location.currentMode, state:null, level:50, ct:2700, color:[saturation:80,hue:20]]]
    def i = 1
    for (; i < 11; i++)     {
        def ruleNo = String.valueOf(i)
/*        def ruleName = settings["name$ruleNo"]
        def ruleDisabled = settings["disabled$ruleNo"]
        def ruleMode = settings["mode$ruleNo"]
//        def ruleModeID = (ruleMode ? String.valueOf(ruleMode) : null)
        def ruleState = settings["state$ruleNo"]
        def ruleDayOfWeek = []
        if (settings["dayOfWeek$ruleNo"])      {
            switch(settings["dayOfWeek$ruleNo"])       {
                case '1':   case '2':   case '3':   case '4':   case '5':   case '6':   case '7':
                        ruleDayOfWeek << settings["dayOfWeek$ruleNo"];        break;
                case '8':   [1,2,3,4,5].each    { ruleDayOfWeek << it };      break;
                case '9':   [6,7].each          { ruleDayOfWeek << it };      break;
                default:    ruleDayOfWeek = null;                             break;
            }
        }
        else
            ruleDayOfWeek = null
//        def ruleLuxSensorID = null
//        settings["luxSensor$ruleNo"].each { ruleLuxSensorID = it.getId() }
        def ruleLuxThreshold = settings["luxThreshold$ruleNo"]
        def ruleFromTimeType = settings["fromTimeType$ruleNo"]
        def ruleFromTime = settings["fromTime$ruleNo"]
        def ruleToTimeType = settings["toTimeType$ruleNo"]
        def ruleToTime = settings["toTime$ruleNo"]
        def ruleSwitchesOn = settings["switchesOn$ruleNo"]
        def ruleSetLevelTo = settings["setLevelTo$ruleNo"]
        def ruleSetColorTo = settings["setColorTo$ruleNo"]
        def ruleSetHueTo = returnHueAndSaturation(ruleSetColorTo)
        def ruleSetColorTemperatureTo = settings["setColorTemperatureTo$ruleNo"]
        def ruleSwitchesOff = settings["switchesOff$ruleNo"]
//        ifDebug("$ruleNo || $ruleName || $ruleMode || $ruleState || $ruleLuxSensor || $ruleFromTimeType || $ruleToTimeType")
//        ifDebug("$ruleNo || $ruleSetLevelTo || $ruleSetHueTo || $ruleSetColorTemperatureTo")
//        if ((ruleModeID || ruleState || ruleLuxSensorID || ruleFromTimeType || ruleToTimeType) && (ruleSetLevelTo || ruleSetHueTo || ruleSetColorTemperatureTo))       {
//            state.rules << ["$ruleNo":[ruleNo:i, name:ruleName, mode:ruleModeID, state:ruleState, luxSensor:ruleLuxSensorID, luxThreshold:ruleLuxThreshold,
//                               fromTimeType:ruleFromTimeType, fromTime:ruleFromTime, toTimeType:ruleToTimeType, toTime:ruleToTime,
//                               level:ruleSetLevelTo, color:ruleSetHueTo, colorTemperature:ruleSetColorTemperatureTo]]
        ifDebug("$ruleName || $ruleMode || $ruleState || $ruleLuxThreshold || $ruleFromTimeType || $ruleToTimeType")*/
        def thisRule = getRule(ruleNo, false)
        if (thisRule)     {
//            state.rules << ["$ruleNo":[ruleNo:i, name:ruleName, disabled:ruleDisabled, mode:ruleMode, state:ruleState, dayOfWeek:ruleDayOfWeek, luxThreshold:ruleLuxThreshold,
//                                       fromTimeType:ruleFromTimeType, fromTime:ruleFromTime, toTimeType:ruleToTimeType, toTime:ruleToTime,
//                                       level:ruleSetLevelTo, color:ruleSetColorTo, hue:ruleSetHueTo, colorTemperature:ruleSetColorTemperatureTo]]
//            if (!state.rules) state.rules = [:]
//            rulesMap << ["$ruleNo":[isRule:true]]
//            if (thisRule.luxThreshold)      state.luxCheck = true
            state.rules = true
            if (thisRule.state && thisRule.state.contains('vacant'))    state.vacant = true
            if (thisRule.fromTimeType && thisRule.toTimeType)           state.timeCheck = true
            thisRule.switchesOn.each      {
                def itID = it.getId()
                if (it.hasCommand("setLevel"))
                    state.switchesHasLevel << ["$itID":true]
                if (it.hasCommand("setColor"))
                    state.switchesHasColor << ["$itID":true]
                if (it.hasCommand("setColorTemperature"))
                    state.switchesHasColorTemperature << ["$itID":true]
            }
        }
    }
//    state.rules = rulesMap
}

private getRule(ruleNo, checkState = true)     {
    if (!ruleNo)        return null
/*    if (checkState && state.rules)      {
        def ruleThere = state.rules[ruleNo]
        if (!ruleThere)      return null
    }*/
    def ruleName = settings["name$ruleNo"]
    def ruleDisabled = settings["disabled$ruleNo"]
    def ruleMode = settings["mode$ruleNo"]
//        def ruleModeID = (ruleMode ? String.valueOf(ruleMode) : null)
    def ruleState = settings["state$ruleNo"]
    def ruleDayOfWeek = []
    if (settings["dayOfWeek$ruleNo"])      {
        switch(settings["dayOfWeek$ruleNo"])       {
            case '1':   case '2':   case '3':   case '4':   case '5':   case '6':   case '7':
                    ruleDayOfWeek << settings["dayOfWeek$ruleNo"];        break;
            case '8':   [1,2,3,4,5].each    { ruleDayOfWeek << it };      break;
            case '9':   [6,7].each          { ruleDayOfWeek << it };      break;
            default:    ruleDayOfWeek = null;                             break;
        }
    }
    else
        ruleDayOfWeek = null
    def ruleLuxThreshold = settings["luxThreshold$ruleNo"]
    if (checkState && ruleLuxThreshold)       {
        if (luxThreshold)   {
            if (Integer.valueOf(ruleLuxThreshold) > Integer.valueOf(luxThreshold))    ruleLuxThreshold = Integer.valueOf(luxThreshold)
        }
        else
            ruleLuxThreshold = null
    }
    def ruleFromTimeType = settings["fromTimeType$ruleNo"]
    def ruleFromTime = settings["fromTime$ruleNo"]
    def ruleToTimeType = settings["toTimeType$ruleNo"]
    def ruleToTime = settings["toTime$ruleNo"]
    def ruleActions = settings["actions$ruleNo"]
    def ruleSwitchesOn = settings["switchesOn$ruleNo"]
    def ruleSetLevelTo = settings["setLevelTo$ruleNo"]
    def ruleSetColorTo = settings["setColorTo$ruleNo"]
    def ruleSetHueTo = returnHueAndSaturation(ruleSetColorTo)
    def ruleSetColorTemperatureTo = settings["setColorTemperatureTo$ruleNo"]
    def ruleSwitchesOff = settings["switchesOff$ruleNo"]
    if (ruleName || ruleDisabled || ruleMode || ruleState || ruleDayOfWeek || ruleFromTimeType || ruleToTimeType || ruleLuxThreshold ||
                    ruleActions || ruleSwitchesOn || ruleSetLevelTo || ruleSetColorTo || ruleSetColorTemperatureTo || ruleSwitchesOff)
        return [ruleNo:ruleNo, name:ruleName, disabled:ruleDisabled, mode:ruleMode, state:ruleState, dayOfWeek:ruleDayOfWeek, luxThreshold:ruleLuxThreshold,
                               fromTimeType:ruleFromTimeType, fromTime:ruleFromTime, toTimeType:ruleToTimeType, toTime:ruleToTime, actions:ruleActions,
                               switchesOn:ruleSwitchesOn, level:ruleSetLevelTo, color:ruleSetColorTo, hue:ruleSetHueTo,
                               colorTemperature:ruleSetColorTemperatureTo, switchesOff:ruleSwitchesOff]
    else
        return null
}

def	initialize()	{}

def	modeEventHandler(evt)	{
    if (state.dayOfWeek && !(checkRunDay()))
        return
	if (awayModes && awayModes.contains(evt.value))    {
    	roomVacant()
        return
    }
    else
        if (pauseModes && pauseModes.contains(evt.value))   {
            unscheduleAll("mode handler")
            return
        }
    def child = getChildDevice(getRoom())
    def roomState = child.getRoomState()
    if (state.rules && ['engaged', 'occupied', 'asleep', 'vacant'].contains(roomState))
        turnOnAndSetSwitches()
}

def	motionActiveEventHandler(evt)	{
    if (pauseModes && pauseModes.contains(location.currentMode))
    	return
    if (state.dayOfWeek && !(checkRunDay()))
        return
	def child = getChildDevice(getRoom())
	def roomState = child.getRoomState()
    if (roomState == 'asleep')		{
        if (nightSwitches)      {
            dimNightLights()
            if (state.noMotion && whichNoMotion != lastMotionInactive())
                runIn(state.noMotion, nightSwitchesOff)
        }
		return
    }
    unscheduleAll("motion active handler")
    if (contactSensor && contactSensor.currentValue("contact") == 'closed')     {
        if (['occupied', 'checking'].contains(roomState))   {
            child.generateEvent('engaged')
        }
        else
            if (roomState == 'vacant')
                child.generateEvent('checking')
    }
    else    {
        if (['checking', 'vacant'].contains(roomState))
		      child.generateEvent('occupied')
        else    {
            if (roomState == 'occupied' && whichNoMotion == lastMotionActive() && state.noMotion)
                runIn(state.noMotion, roomVacant)
        }
    }
}

def	motionInactiveEventHandler(evt)     {
    if (pauseModes && pauseModes.contains(location.currentMode))
    	return
    if (state.dayOfWeek && !(checkRunDay()))
        return
    def child = getChildDevice(getRoom())
	def roomState = child.getRoomState()
    if (['occupied'].contains(roomState))       {
//        if (!(state.noMotion))
//            runIn(1, roomVacant)
//        else
            if (state.noMotion && whichNoMotion == lastMotionInactive())
                runIn(state.noMotion, roomVacant)
    }
    else    {
        if (roomState == 'asleep' && nightSwitches)
            if (whichNoMotion == lastMotionInactive())
                runIn((state.noMotion ?: 1), nightSwitchesOff)
    }
}

def adjMotionActiveEventHandler(evt)    {
    if (pauseModes && pauseModes.contains(location.currentMode))
    	return
    if (state.dayOfWeek && !(checkRunDay()))
        return
    def child = getChildDevice(getRoom())
    def roomState = child.getRoomState()
    if (adjRoomsMotion && roomState == 'occupied')      {
        def motionValue = motionSensors.currentValue("motion")
        def motionLastActivity = motionSensors.getLastActivity()
        def mV = motionValue.contains('active')
        def mD = motionLastActivity.max()
        if (mV && mD > evt.date)
            return
        child.generateEvent('checking')
        return
    }
    if (adjRoomsPathway && roomState == 'vacant')      {
        adjRooms.each   {
            def lastStateDate = parent.getLastStateDate(it)
            if (lastStateDate['state'])     {
                def evtDate = evt.date.getTime()
                def lsDate = lastStateDate['date']
                def dateDiff = (evtDate - lsDate) + 0
                if (lastStateDate['state'] == 'vacant')    {
//                    switchesOn()
                    turnOnAndSetSwitches('occupied')
                    child.generateEvent('checking')
                    return
                }
            }
        }
    }
}

def adjMotionInactiveEventHandler(evt)    {
}

def	switchOnEventHandler(evt)	{
    if (pauseModes && pauseModes.contains(location.currentMode))
    	return
    if (state.dayOfWeek && !(checkRunDay()))
        return
/*    def child = getChildDevice(getRoom())
    def roomState = child.getRoomState()
    if (roomState == 'vacant')      {
        if (state.noMotion)
            runIn(state.noMotion, dimLights)
    }*/
}

def	switchOffEventHandler(evt)  {
    if (pauseModes && pauseModes.contains(location.currentMode))
    	return
    if (state.dayOfWeek && !(checkRunDay()))
        return
//    if (!('on' in switches2.currentValue("switch")))
//        unschedule()
}

def	buttonPushedEventHandler(evt)     {
    if (pauseModes && pauseModes.contains(location.currentMode))   return;
    if (state.dayOfWeek && !(checkRunDay()))    return;
    if (!evt.data)      return;
    def eD = new groovy.json.JsonSlurper().parseText(evt.data)
    assert eD instanceof Map
    if (!eD || (buttonIs && eD['buttonNumber'] != buttonIs as Integer))
    	return
    def child = getChildDevice(getRoom())
    def roomState = child.getRoomState()
    if (roomState == 'engaged')     {
        if (resetEngagedDirectly)
            child.generateEvent('vacant')
        else
            child.generateEvent('checking')
    }
    else
        child.generateEvent('engaged')
}

def	presencePresentEventHandler(evt)     {
    if (pauseModes && pauseModes.contains(location.currentMode))   return;
    if (state.dayOfWeek && !(checkRunDay()))    return;
    if (presenceActionArrival())      {
        def child = getChildDevice(getRoom())
        def roomState = child.getRoomState()
        if (['occupied', 'checking', 'vacant'].contains(roomState))
            child.generateEvent('engaged')
    }
}

def	presenceNotPresentEventHandler(evt)     {
    if (pauseModes && pauseModes.contains(location.currentMode))   return;
    if (state.dayOfWeek && !(checkRunDay()))    return;
    if (presenceActionDeparture())      {
        def child = getChildDevice(getRoom())
        def roomState = child.getRoomState()
        if (['asleep', 'engaged', 'occupied'].contains(roomState))      {
            if (resetEngagedDirectly)
                child.generateEvent('vacant')
            else
                child.generateEvent('checking')
        }
    }
}

def	engagedSwitchOnEventHandler(evt)     {
    if (pauseModes && pauseModes.contains(location.currentMode))   return;
    if (state.dayOfWeek && !(checkRunDay()))    return;
    if (personPresence && personPresence.currentValue("presence") == 'present')     return;
    def child = getChildDevice(getRoom())
	def roomState = child.getRoomState()
    if (['occupied', 'checking', 'vacant'].contains(roomState))
        child.generateEvent('engaged')
}

def	engagedSwitchOffEventHandler(evt)	{
    if (pauseModes && pauseModes.contains(location.currentMode))   return;
    if (state.dayOfWeek && !(checkRunDay()))    return;
    if (personPresence && personPresence.currentValue("presence") == 'present')     return;
	def child = getChildDevice(getRoom())
	def roomState = child.getRoomState()
    if (resetEngagedDirectly && roomState == 'engaged')
        child.generateEvent('vacant')
    else
        if (['engaged', 'occupied'].contains(roomState))
            child.generateEvent('checking')
}

def	contactOpenEventHandler(evt)	{
    if (pauseModes && pauseModes.contains(location.currentMode))   return;
    if (state.dayOfWeek && !(checkRunDay()))    return;
    if (personPresence && personPresence.currentValue("presence") == 'present')     return;
    if (engagedSwitch && engagedSwitch.currentValue("switch") == 'on')  return;
	def child = getChildDevice(getRoom())
	def roomState = child.getRoomState()
    if (resetEngagedDirectly && roomState == 'engaged')
        child.generateEvent('vacant')
    else
        if (['engaged', 'occupied', 'vacant'].contains(roomState))
            child.generateEvent('checking')
}

def	contactClosedEventHandler(evt)     {
    if (pauseModes && pauseModes.contains(location.currentMode))   return;
    if (state.dayOfWeek && !(checkRunDay()))    return;
    if (personPresence && personPresence.currentValue("presence") == 'present')     return;
    if (engagedSwitch && engagedSwitch.currentValue("switch") == 'on')      return;
    def child = getChildDevice(getRoom())
    def roomState = child.getRoomState()
    if (['occupied', 'checking'].contains(roomState) || (!motionSensors && roomState == 'vacant'))
        child.generateEvent('engaged')
    else
        if (motionSensors && roomState == 'vacant')
            child.generateEvent('checking')
}

def luxEventHandler(evt)    {
    if (pauseModes && pauseModes.contains(location.currentMode))       return;
    if (state.dayOfWeek && !(checkRunDay()))    return;
    if (!luxThresholdssssss)        return;
    def currentLux = evt.value.toInteger()
/*    def currentMode = String.valueOf(location.currentMode)
    def child = getChildDevice(getRoom())
	def roomState = child.currentValue("occupancy")
    def nowTime	= now()
    def nowDate = new Date(nowTime)
    def sunriseAndSunset = getSunriseAndSunset()
    def sunriseTime = new Date(sunriseAndSunset.sunrise.getTime())
    def sunsetTime = new Date(sunriseAndSunset.sunset.getTime())
//    for (def rule in state.rules.sort{ it.key })    {
    def i = 1
    for (; i < 11; i++)     {
        def ruleNo = String.valueOf(i)
        def thisRule = getRule(ruleNo)
        if (!thisRule || thisRule.disabled)      continue;
        if (!thisRule.mode && !thisRule.state && !thisRule.dayOfWeek && !thisRule.luxThreshold && !thisRule.fromTimeType && !thisRule.toTimeType)
            continue
        if (thisRule.mode && !thisRule.mode.contains(currentMode))      continue;
        if (thisRule.state && !thisRule.state.contains(roomState))      continue;
        if (thisRule.dayOfWeek && !(checkRunDay(thisRule.dayOfWeek)))    return;
        if (!thisRule.luxThreshold)     continue;
        if ((thisRule.fromTimeType && (thisRule.fromTimeType != timeTime() || thisRule.fromTime)) &&
            (thisRule.toTimeType && (thisRule.toTimeType != timeTime() || thisRule.toTime)))    {
            def fTime = ( thisRule.fromTimeType == timeSunrise() ? sunriseTime : ( thisRule.fromTimeType == timeSunset() ? sunsetTime : timeToday(thisRule.fromTime, location.timeZone)))
            def tTime = ( thisRule.toTimeType == timeSunrise() ? sunriseTime : ( thisRule.toTimeType == timeSunset() ? sunsetTime : timeToday(thisRule.toTime, location.timeZone)))
            if (timeOfDayIsBetween(fTime, tTime, nowDate, location.timeZone))       continue;
        }
        break
    }
    if (i < 11)   {*/
        if (luxFell(currentLux, luxThreshold))    {
            if (['engaged', 'occupied', 'asleep', 'vacant'].contains(roomState))
                turnOnAndSetSwitches()
        }
        else
            if (luxRose(currentLux, luxThreshold))    {
                if (['engaged', 'occupied', 'asleep', 'vacant'].contains(roomState) && allSwitchesOff)
                        switches2Off()
//                    dimLights(true)
//                    if (state.dimTimer)     {
//                        runOnce(new Date(now() + (state.dimTimer * 1000)), forceSwitches2Off)
//                        unschedule("switches2Off")
//                    }
                }
//            }
//    }
    state.previousLux = currentLux
}

private luxFell(currentLux, luxThreshold)   {   return (currentLux <= luxThreshold && state.previousLux > luxThreshold)  }

private luxRose(currentLux, luxThreshold)   {   return (currentLux > luxThreshold && state.previousLux <= luxThreshold)  }

// pass in child and roomState???
def roomVacant()	  {
    ifDebug("roomVacant")
	def child = getChildDevice(getRoom())
	def roomState = child.getRoomState()
    def newState = null
    if (['engaged', 'occupied'].contains(roomState))    {
        if (state.dimTimer)
            newState = 'checking'
        else
            newState = 'vacant'
    }
    else
        if (roomState == 'checking')
            newState = 'vacant'
    if (newState)
        child.generateEvent(newState)
}

def roomAwake()	  {
    ifDebug("roomAwake")
	def child = getChildDevice(getRoom())
	def roomState = child.getRoomState()
    def newState = null
    if (roomState == 'asleep')
        if (state.dimTimer)
            newState = 'checking'
        else
            newState = 'vacant'
    if (newState)
        child.generateEvent(newState)
}

def handleSwitches(oldState = null, newState = null)	{
    ifDebug("${app.label} room state - old: ${oldState} new: ${newState}")
//    state.roomState = newState
//      "yyyy-MM-dd'T'HH:mm:ssZ" = 2017-11-13T23:32:45+0000
    if (!newState || oldState == newState)      return false;
    def nowDate = now()
    state.previousState = ['state':newState, 'date':nowDate]
    previousStateStack(state.previousState)
    if (pauseModes && pauseModes.contains(location.currentMode))       return false;
    if (state.dayOfWeek && !(checkRunDay()))                    return false;
    ifDebug("busyCheck: $state.busyCheck | isBusy: $state.isBusy | newState: $newState")
    def moveToEngaged = false
    if (state.busyCheck && state.isBusy && newState != 'engaged' && ['occupied', 'checking', 'vacant'].contains(newState))      {
        moveToEngaged = true
//        return true
    }
    if (oldState == 'asleep')       {
        unschedule("roomAwake")
        nightSwitchesOff()
    }
    else
        unscheduleAll("handle switches")
    if (['engaged', 'occupied', 'asleep', 'vacant'].contains(newState))     {
        if (newState != 'vacant' || state.vacant)
            turnOnAndSetSwitches()
        else
            switches2Off()
        if (newState == 'asleep')   {
            nightSwitchesOff()
            if (state.noAsleep)
                runIn(state.noAsleep, roomAwake)
        }
        if (newState == 'engaged')  {
            if (state.noMotionEngaged)
                runIn(state.noMotionEngaged, roomVacant)
        }
        if (newState == 'occupied')
            if (state.noMotion)     {
                def motionValue = motionSensors.currentValue("motion")
                def mV = motionValue.contains('active')
                if (whichNoMotion == lastMotionActive() || (whichNoMotion == lastMotionInactive() && !mV))
                    runIn(state.noMotion, roomVacant)
            }
    }
    else
        if (newState == 'checking')     {
            dimLights()
            runIn(state.dimTimer ?: 1, roomVacant)
        }
	return moveToEngaged
}

/*
private switchesOn()	{
    if (fromTimeType && toTimeType)     {
        def nowTime	= now()
        def nowDate = new Date(nowTime)
        def sunriseAndSunset = getSunriseAndSunset()
        def sunriseTime = new Date(sunriseAndSunset.sunrise.getTime())
        def sunsetTime = new Date(sunriseAndSunset.sunset.getTime())
        def fromDate = timeToday(fromTime, location.timeZone)
        def toDate = timeToday(toTime, location.timeZone)
        def fTime = (fromTimeType == timeSunrise() ? sunriseTime : ( fromTimeType == timeSunset() ? sunsetTime : fromDate))
        def tTime = (toTimeType == timeSunrise() ? sunriseTime : ( toTimeType == timeSunset() ? sunsetTime : toDate))
//log.debug "checking time from: $fTime | to: $tTime | date: $nowDate | ${timeOfDayIsBetween(fTime, tTime, nowDate, location.timeZone)}"
        if (!(timeOfDayIsBetween(fTime, tTime, nowDate, location.timeZone)))
            return
    }
    if (state.luxEnabled && luxSensor.currentValue("illuminance") > luxThreshold)
        return
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
    if (switches)
        turnOnAndSetSwitches()
}
*/

private turnOnAndSetSwitches(roomState = null)     {
    if (luxThreshold)     {
        def lux = luxSensor.currentValue("illuminance")
        if (lux > luxThreshold)     return false;
    }
    def turnOn = null
    def thisRule = [:]
    if (state.rules)    {
        def currentMode = String.valueOf(location.currentMode)
        if (!roomState)     {
            def child = getChildDevice(getRoom())
            roomState = child.currentValue("occupancy")
        }
        def nowTime	= now()
        def nowDate = new Date(nowTime)
        def sunriseAndSunset = getSunriseAndSunset()
        def sunriseTime = new Date(sunriseAndSunset.sunrise.getTime())
        def sunsetTime = new Date(sunriseAndSunset.sunset.getTime())
        def i = 1
        for (; i < 11; i++)      {
//        for (def rule in state.rules.sort{ it.key })    {
            def ruleNo = String.valueOf(i)
            thisRule = getRule(ruleNo)
            if (!thisRule || thisRule.disabled)      continue;
            if (!thisRule.mode && !thisRule.state && !thisRule.dayOfWeek && !thisRule.luxThreshold && !thisRule.actions &&
                !thisRule.fromTimeType && !thisRule.toTimeType && !thisRule.switchesOn && !thisRule.switchesOff)
                continue
            if (thisRule.mode && !thisRule.mode.contains(currentMode))      continue;
            if (thisRule.state && !thisRule.state.contains(roomState))      continue;
            if (thisRule.dayOfWeek && !(checkRunDay(thisRule.dayOfWeek)))   continue;
            if (thisRule.luxThreshold)   {
                def lux = luxSensor.currentValue("illuminance")
                if (lux > thisRule.luxThreshold)    continue;
            }
            if ((thisRule.fromTimeType && (thisRule.fromTimeType != timeTime() || thisRule.fromTime)) &&
                (thisRule.toTimeType && (thisRule.toTimeType != timeTime() || thisRule.toTime)))    {
                def fTime = ( thisRule.fromTimeType == timeSunrise() ? sunriseTime : ( thisRule.fromTimeType == timeSunset() ? sunsetTime : timeToday(thisRule.fromTime, location.timeZone)))
                def tTime = ( thisRule.toTimeType == timeSunrise() ? sunriseTime : ( thisRule.toTimeType == timeSunset() ? sunsetTime : timeToday(thisRule.toTime, location.timeZone)))
                if (!(timeOfDayIsBetween(fTime, tTime, nowDate, location.timeZone)))    continue;
            }
            turnOn = ruleNo
            break
        }
    }
    if (turnOn)     {
        switchesOnOff(thisRule)
        runActions(thisRule)
        return true
    }
    else
        return false
}

private switchesOnOff(thisRule)       {
    if (thisRule && (thisRule.switchesOn || thisRule.switchesOff))
        state.previousRuleNo = thisRule.ruleNo
    if (thisRule.switchesOn)
        thisRule.switchesOn.each      {
            it.on();
            def itID = it.getId()
            if (thisRule.color && state.switchesHasColor[itID])
                it.setColor(thisRule.hue)
            else    {
                if (thisRule.colorTemperature && state.switchesHasColorTemperature[itID])
                    it.setColorTemperature(thisRule.colorTemperature)
            }
            if (thisRule.level && state.switchesHasLevel[itID])
                it.setLevel(thisRule.level)
        }
    if (thisRule.switchesOff)
        thisRule.switchesOff.off()
}

private runActions(thisRule)    {
    if (thisRule.actions)
        thisRule.actions.each   {
            location.helloHome?.execute(it)
        }
}

def dimLights()     {
    ifDebug("dim lights")
    if (state.dimTimer && state.previousRuleNo)
        if (state.dimByLevel)      {
            def thisRule = getRule(state.previousRuleNo)
            thisRule.switchesOn.each      {
                if (it.currentValue("switch") == 'on')
                    if (it.hasCommand("setLevel"))     {
                        def currentLevel = it.currentValue("level")
                        def newLevel = (currentLevel > state.dimByLevel ? currentLevel - state.dimByLevel : 1)
                        it.setLevel(newLevel)
                    }
            }
        }

/*    if (state.dimTimer)       {
        if (state.dimByLevel)      {
            switches2.each      {
                if (it.currentValue("switch") == 'on')       {
                    if (state.switchesHasLevel[it.getId()])     {
                        def currentLevel = it.currentValue("level")
                        def newLevel = (currentLevel > state.dimByLevel ? currentLevel - state.dimByLevel : 1)
                        it.setLevel(newLevel)
                    }
                }
            }
            if (allSwitches && allSwitchesOff)  {
                switches.each   {
                    if (it.currentValue("switch") == 'on')       {
                        if (state.switchesHasLevel[it.getId()])     {
                            def currentLevel = it.currentValue("level")
                            def newLevel = (currentLevel > state.dimByLevel ? currentLevel - state.dimByLevel : 1)
                            it.setLevel(newLevel)
                        }
                    }
                }
            }
        }
    }
    runIn(state.dimTimer ?: 1, switches2Off, [data: [allSwitches: allSwitches]])*/
}

//def forceSwitches2Off()     {  switches2Off(allSwitchesOff)  }

def switches2Off()       {
    ifDebug("switches2Off")
    if (state.previousRuleNo)       {
        def thisRule = getRule(state.previousRuleNo)
        if (thisRule && thisRule.switchesOn)        thisRule.switchesOn.off();
    }
}

private previousStateStack(previousState)    {
    def i
    def timeIs = now()
    def removeHowOld = (state.noMotion ? ((state.noMotion + state.dimTimer) * 10) : (180 * 10))
    def howMany
    int gapBetween

    if (state.stateStack)   {
        for (i = 9; i > 0; i--)     {
            def s = String.valueOf(i)
            if (state.stateStack[s])        {
                gapBetween = ((timeIs - (state.stateStack[s])['date']) / 1000)
                if (gapBetween > removeHowOld)  {
                    state.stateStack.remove(s)
                }
                else
                    break
            }
        }
    }

    if (state.stateStack)   {
        for (i = 9; i > 0; i--)     {
            if (state.stateStack[String.valueOf(i-1)])
                state.stateStack[String.valueOf(i)] = state.stateStack[String.valueOf(i-1)]
        }
    }
    else
        state.stateStack = [:]
    state.stateStack << ['0':previousState]

    state.isBusy = false
    if (state.busyCheck)      {

/*        howMany = 0
        gapBetween = 0
        state.stateStack.each   { k, v ->
            if (['occupied', 'checking', 'vacant'].contains(v['state']))     {
                howMany++
                gapBetween += ((timeIs - v['date']) / 1000)
            }
        }
        ifDebug("howMany: $howMany | gapBetween: $gapBetween")*/

        howMany = 0
        gapBetween = 0
        for (i = 9; i > 0; i--)     {
            def s = String.valueOf(i)
            def sM = String.valueOf(i-1)
            if (state.stateStack[s] && ['occupied', 'vacant'].contains((state.stateStack[s])['state']) &&
                                       ['occupied', 'vacant'].contains((state.stateStack[sM])['state']))         {
                howMany++
                gapBetween += (((state.stateStack[sM])['date'] - (state.stateStack[s])['date']) / 1000)
            }
        }
        ifDebug("howMany: $howMany | gapBetween: $gapBetween | busyCheck: $state.busyCheck")

        if (howMany >= state.busyCheck)   {
            ifDebug("busy on")
            state.isBusy = true
            state.stateStack = [:]
        }
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

def uninstalled() {
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

private returnHueAndSaturation(setColorTo)        {
    def rHAS
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
        rHAS = [hue: hueColor, saturation: saturation]
    }
    else
        rHAS = null
    return rHAS
}

private unscheduleAll(classNameCalledFrom)		{
log.debug "${app.label} unschedule calling class: $classNameCalledFrom"
    unschedule("roomVacant")
//    unschedule("dimLights")
//    unschedule("switches2Off")
}

/*
private scheduleFromToTimes()       {
    if (!state.rules || !state.timeCheck)
        return
    def sunriseFromSubscribed = false
    def sunriseToSubscribed = false
    def sunsetFromSubscribed = false
    def sunsetToSubscribed = false
    for (def rule in state.rules.sort{ it.key })        {
        def thisRule = rule.value
        if (thisRule.disabled)      continue
        if (!thisRule.fromTimeType || !thisRule.toTimeType)     continue
        ifDebug("$thisRule")
        if (thisRule.fromTimeType == timeTime())
            schedule(thisRule.fromTime, timeFromHandler, [overwrite: false])
        else    {
            if (thisRule.fromTimeType == timeSunrise() && !sunriseFromSubscribed)   {
                subscribe(location, "sunrise", timeFromHandler)
                sunriseFromSubscribed = true
            }
            else
                if (!sunsetFromSubscribed)   {
                    subscribe(location, "sunset", timeFromHandler)
                    sunsetFromSubscribed = true
                }
        }
        if (thisRule.toTimeType == timeTime())
            schedule(thisRule.toTime, timeToHandler, [overwrite: false])
        else    {
            if (thisRule.toTimeType == timeSunrise() && !sunriseToSubscribed)   {
                subscribe(location, "sunrise", timeToHandler)
                sunriseToSubscribed = true
            }
            else
                if (!sunsetToSubscribed)   {
                    subscribe(location, "sunset", timeToHandler)
                    sunsetToSubscribed = true
                }
        }
    }
}
*/

def scheduleFromToTimes()       {
    if (!state.rules || !state.timeCheck)
        return
    ifDebug("scheduleFromToTimes")
    def sunriseFromSubscribed = false
    def sunriseToSubscribed = false
    def sunsetFromSubscribed = false
    def sunsetToSubscribed = false
/*    for (def rule in state.rules.sort{ it.key })        {
        def thisRule = rule.value
        if (thisRule.disabled)      continue
        if (!thisRule.fromTimeType || !thisRule.toTimeType)     continue
        ifDebug("$thisRule")
        if (thisRule.fromTimeType == timeSunrise() && !sunriseFromSubscribed)   {
            subscribe(location, "sunrise", timeFromHandler)
            sunriseFromSubscribed = true
        }
        else
            if (thisRule.fromTimeType == timeSunset() && !sunsetFromSubscribed)   {
                subscribe(location, "sunset", timeFromHandler)
                sunsetFromSubscribed = true
            }
        if (thisRule.toTimeType == timeSunrise() && !sunriseToSubscribed)   {
            subscribe(location, "sunrise", timeToHandler)
            sunriseToSubscribed = true
        }
        else
            if (thisRule.toTimeType == timeSunset() && !sunsetToSubscribed)   {
                subscribe(location, "sunset", timeToHandler)
                sunsetToSubscribed = true
            }
    }*/
    def i = 1
    for (; i < 11; i++)     {
        def ruleNo = String.valueOf(i)
        def thisRule = getRule(ruleNo)
        if (!thisRule || thisRule.disabled)      continue
        if (!thisRule.fromTimeType || !thisRule.toTimeType)     continue
        if (thisRule.fromTimeType == timeSunrise() && !sunriseFromSubscribed)   {
            subscribe(location, "sunrise", timeFromHandler)
            sunriseFromSubscribed = true
        }
        else
            if (thisRule.fromTimeType == timeSunset() && !sunsetFromSubscribed)   {
                subscribe(location, "sunset", timeFromHandler)
                sunsetFromSubscribed = true
            }
        if (thisRule.toTimeType == timeSunrise() && !sunriseToSubscribed)   {
            subscribe(location, "sunrise", timeToHandler)
            sunriseToSubscribed = true
        }
        else
            if (thisRule.toTimeType == timeSunset() && !sunsetToSubscribed)   {
                subscribe(location, "sunset", timeToHandler)
                sunsetToSubscribed = true
            }
    }
    scheduleFromTime()
    scheduleToTime()
}

private scheduleFromTime()      {
    if (!state.rules || !state.timeCheck)
        return
    ifDebug("scheduleFromTime")
    def nowTime	= now()
    def nowDate = new Date(nowTime)
    def nextTime = null
/*    for (def rule in state.rules.sort{ it.key })        {
        def thisRule = rule.value
        if (thisRule.disabled)      continue
        if (!thisRule.fromTimeType || thisRule.fromTimeType != timeTime() || !thisRule.fromTime || !thisRule.toTimeType)
            continue
        def fromTime = timeTodayAfter(nowDate, thisRule.fromTime, location.timeZone)
        if (!nextTime)      {
            nextTime = fromTime
            continue
        }
        ifDebug("nowDate: $nowDate || nextTime: $nextTime || fromTime: $fromTime")
        ifDebug("${timeOfDayIsBetween(nowDate, nextTime, fromTime, location.timeZone)}")
        if (timeOfDayIsBetween(nowDate, nextTime, fromTime, location.timeZone))
            nextTime = fromTime
    }*/
    def i = 1
    for (; i < 11; i++)     {
        def ruleNo = String.valueOf(i)
        def thisRule = getRule(ruleNo)
        if (!thisRule || thisRule.disabled)      continue
        if (!thisRule.fromTimeType || thisRule.fromTimeType != timeTime() || !thisRule.toTimeType)
            continue
        def fromTime = timeTodayAfter(nowDate, thisRule.fromTime, location.timeZone)
        if (!nextTime)      {
            nextTime = fromTime
            continue
        }
        if (timeOfDayIsBetween(nowDate, nextTime, fromTime, location.timeZone))
            nextTime = fromTime
    }
    if (nextTime)
        schedule(nextTime, timeFromHandler)
}

private scheduleToTime()      {
    if (!state.rules || !state.timeCheck)
        return
    ifDebug("scheduleToTime")
    def nowTime	= now()
    def nowDate = new Date(nowTime)
    def nextTime = null
/*    for (def rule in state.rules.sort{ it.key })        {
        def thisRule = rule.value
        if (thisRule.disabled)      continue
        if (!thisRule.toTimeType || thisRule.toTimeType != timeTime() || !thisRule.toTime || !thisRule.fromTimeType)
            continue
        def toTime = timeTodayAfter(nowDate, thisRule.toTime, location.timeZone)
        if (!nextTime)      {
            nextTime = toTime
            continue
        }
        ifDebug("nowDate: $nowDate || nextTime: $nextTime || toTime: $toTime")
        ifDebug("${timeOfDayIsBetween(nowDate, nextTime, toTime, location.timeZone)}")
        if (timeOfDayIsBetween(nowDate, nextTime, toTime, location.timeZone))
            nextTime = toTime
    }*/
    def i = 1
    for (; i < 11; i++)     {
        def ruleNo = String.valueOf(i)
        def thisRule = getRule(ruleNo)
        if (!thisRule || thisRule.disabled)      continue
        if (!thisRule.fromTimeType || !thisRule.toTimeType || thisRule.toTimeType != timeTime())
            continue
        def toTime = timeTodayAfter(nowDate, thisRule.toTime, location.timeZone)
        if (!nextTime)      {
            nextTime = toTime
            continue
        }
        if (timeOfDayIsBetween(nowDate, nextTime, toTime, location.timeZone))
            nextTime = toTime
    }
    if (nextTime)
        schedule(nextTime, timeToHandler)
}

def timeFromHandler(evt = null)       {
    ifDebug("timeFromHandler")
    if (pauseModes && pauseModes.contains(location.currentMode))       return;
    if (state.dayOfWeek && !(checkRunDay()))        return;
    def child = getChildDevice(getRoom())
    def roomState = child.getRoomState()
    if (['engaged', 'occupied', 'asleep', 'vacant'].contains(roomState))
        turnOnAndSetSwitches()
    scheduleFromTime()
}

def timeToHandler(evt = null)       {
    ifDebug("timeToHandler")
    if (pauseModes && pauseModes.contains(location.currentMode))       return;
    if (state.dayOfWeek && !(checkRunDay()))        return;
    def child = getChildDevice(getRoom())
    def roomState = child.getRoomState()
    if (['engaged', 'occupied', 'asleep', 'vacant'].contains(roomState) && state.previousRuleNo)      {
        def turnedOn = turnOnAndSetSwitches()
        ifDebug("turnedOn: $turnedOn | allSwitchesOff: $allSwitchesOff")
        if (!turnedOn && allSwitchesOff)
            switches2Off()
//        switches2Off(allSwitchesOff)
//        dimLights(true)
/*        if (state.dimTimer)   {
            runOnce(new Date(now() + (state.dimTimer * 1000)), forceSwitches2Off)
            unschedule("switches2Off")
        }*/
    }
    scheduleToTime()
}

def getAdjMotionSensors()  {
    if (motionSensors)   {
        def motionSensorsList = []
        motionSensors.each   {  motionSensorsList << it }
        return motionSensorsList
    }
    else
        return null
}

def getAdjRoomDetails()  {
    def adjRoomDetails = ['childid':app.id, 'adjrooms':adjRooms]
log.debug "childid: ${adjRoomDetails['childid']} | adjrooms: ${adjRoomDetails['adjrooms']}"
/*    if (motionSensors)   {
        def motionSensorsList = []
        def motionSensorsNameList = []
        motionSensors.each   {
            motionSensorsList << it
            motionsSensorName = it.getName()
        }
        adjRoomDetails << ['motionsensors':motionSensorsList]
        adjRoomDetails << ['motionsensorsnames':motionSensorsNamesList]
    }
    else
        adjRoomDetails << ['motionsensors':null,'motionsensorsnames':null]
*/
    return adjRoomDetails
}

def getLastStateChild()     {
    def addRoom = state.previousState
    addRoom << ['room':app.label]
    return addRoom
}

private checkRunDay(dayOfWeek = null)   {
    def thisDay = (new Date(now())).getDay()
    if (dayOfWeek)
        return (dayOfWeek.contains(thisDay))
    else
        return (state.dayOfWeek.contains(thisDay))
}

private lastMotionActive()      {  return '1'  }
private lastMotionInactive()    {  return '2'  }

private timeSunrise()   {  return '1'  }
private timeSunset()    {  return '2'  }
private timeTime()      {  return '3'  }

private presenceActionArrival()       {  return (presenceAction == '1' || presenceAction == '3')  }
private presenceActionDeparture()     {  return (presenceAction == '2' || presenceAction == '3')  }

private ifDebug(msg = null)     {
    if (msg && isDebug())
        log.debug msg
}

private isDebug()   {  return true  }

//------------------------------------------------------Night option------------------------------------------------------//
def	nightButtonPushedEventHandler(evt)     {
    if (!evt.data)
        return
    def nM = new groovy.json.JsonSlurper().parseText(evt.data)
    assert nM instanceof Map
    if (!nM || (nightButtonIs && nM['buttonNumber'] != nightButtonIs as Integer))
        return
    def child = getChildDevice(getRoom())
    def roomState = child.getRoomState()
    if (nightSwitches && roomState == 'asleep')     {
        unscheduleAll("night button pushed handler")
        def switchValue = nightSwitches.currentValue("switch")
        if (switchValue.contains('on'))
            nightSwitchesOff()
        else
            dimNightLights()
    }
}

def dimNightLights()     {
    nightSwitches.each      {
        it.on()
        if (state.nightSetLevelTo && state.switchesHasLevel[it.getId()])
            it.setLevel(state.nightSetLevelTo)
    }
}

def nightSwitchesOff()      {
    unscheduleAll("night switches off")
    if (nightSwitches)
        nightSwitches.off()
}

def sleepEventHandler(evt)		{
log.debug "sleepEventHandler: ${asleepSensor} - ${evt.value}"
	def child = getChildDevice(getRoom())
    def roomState = child.getRoomState()
    if (evt.value == "not sleeping")	{
    	child.generateEvent('checking')
    }
    else if (evt.value == "sleeping")	{
    	child.generateEvent('asleep')
    }
}
//------------------------------------------------------------------------------------------------------------------------//
