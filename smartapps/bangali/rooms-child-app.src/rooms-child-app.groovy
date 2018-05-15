/***********************************************************************************************************************
*
*  A SmartThings child smartapp which creates the "room" device using the rooms occupancy DTH and allows executing
*   various rules based on occupancy state. this alllows lights and other devices to be turned on and off based on
*   occupancy. it also allows many other actions like executing a routine or piston, turning on/off music and much
*   more. see the wiki for more details. (note wiki is still in progress. ok there is really no content in the wiki.
*   yet. but this is to reinforce my intention of putting the wiki together. ;-) will update with link once in place.)
*  Copyright (C) 2017 bangali
*
*  Contributors:
*   https://github.com/Johnwillliam
*   https://github.com/TonyFleisher
*   https://github.com/BamaRayne
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
*   icons licensed and used with permission from: https://www.iconfinder.com/aha-soft
*   H letter icon resused from user: https://www.flickr.com/photos/lwr/ under CC BY-NC-SA 2.0
*
*	convertRGBToHueSaturation(...) adpated from code by ady624 for webCoRE. original code can be found at:
*		https://github.com/ady624/webCoRE/blob/master/smartapps/ady624/webcore-piston.src/webcore-piston.groovy
*	colorsRGB array color name and RGB values from code by ady624 for webCoRE.
*
*  Name: Rooms Child App
*   Source: https://github.com/adey/bangali/blob/master/smartapps/bangali/rooms-child-app.src/rooms-child-app.groovy
*
***********************************************************************************************************************/

public static String version()      {  return "v0.35.0"  }
private static boolean isDebug()    {  return true  }

/***********************************************************************************************************************
*
*  Version: 0.35.0
*
*   DONE:   5/11/2018
*   1) added option for buttons to set a state but not toggle from that state.
*   2) added option to set locked state with power value.
*   3) added option for contact sensors to not trigger engaged for use with landing or hallway areas.
*   4) added option to use only selective room motion sensors for motion during asleep mode for night lights.
*   5) changed options for when to turn on night lights.
*   6) added option to only run `execution` rules when state changes. this means once the state changes and the lights have been set, if you change the light settings those will not be reset till the room changes away from the current state.
*   7) added timer display to rooms occupancy device for asleep state.
*   8) organized settings in rooms manager.
*   9) updated docs.
*   10) couple of bug fixes.
*
*  Version: 0.30.0
*
*   DONE:   5/5/2018
*   1) more doc update. latest on github: https://github.com/adey/bangali
*   2) added section at bottom of docs for non-obvious rules, will add more here.
*   3) added support for vents to be controlled with theromstat and room temperature.
*   4) optimized code a bit so can run switches on / off checker every 1 minute on hubitat and keep runtime under 1 second.
*   5) updated text and input settings for rooms manager. some of this is a BREAKING CHANGE and you will need to specify names and colors again.
*   6) updated settings page for rooms manager to be a bit more organized.
*   7) added color notification for battery devices specified in individual rooms settings.
*
*  Version: 0.27.5
*
*   DONE:   5/2/2018
*   1) significant updates to documentation. latest on github: https://github.com/adey/bangali
*   2) turned down the delay between commands on hubitat
*   3) rooms can now be renamed which will also rename the device for the room.
*   4) updated text on input settings.
*   5) added button for occupied settings.
*   6) all buttons now flip between state for that button and if in that state already to checking state.
*   7) added push button support for hubitat dashboard.
*   8) swatted a bug here and a bug there.
*
*  Version: 0.26.0
*
*   DONE:   4/22/2018
*   1) added motion support for welcome home announcement.
*   2) added notification by color, this currently is not constrained by announce only hours settings.
*
*  Version: 0.25.0
*
*   DONE:   4/20/2018
*   1) get buttons working on hubitat.
*
*  Version: 0.21.0
*
*   DONE:   4/19/2018
*   1) mostly readme updates.
*
*  Version: 0.20.9
*
*   DONE:   4/15/2018
*   1) time today change for hubitat compatibility.
*
*  Version: 0.20.7
*
*   DONE:   4/14/2018
*   1) added a bunch of state variable for use with hubitat dashboard tiles.
*
*  Version: 0.20.5
*
*   DONE:   7/13/2018
*   1) changed message separator to '/' and added support for &is and &has.
*   2) added save and restore sound level when playing announcements.
*   3) restored lock only capability instead of using lock capability.
*   4) added support for lock state contact sensor by @BamaRayne.
*   5) added support for lock state switch and contact sensor to lock either on on/off or open/close by @BamaRayne.
*   6) added missing dot to nightSetCT range.
*
*  Version: 0.20.1
*
*   DONE:   7/11/2018
*   1) handle pause for hubitat.
*   2) adapt timeTodayAfter for hubitat compatibility.
*
*  Version: 0.20.0
*
*   DONE:   7/4/2018
*   1) change lock only to lock because hubitat does not support lock only capability.
*   2) add option for cooling / heating override in minutes.
*   3) added option to check room windoes before turning on cooling / heating.
*   4) cleaning up text in settings as i go along.
*   5) added option to not restore light level from dimming if room changes to vacant.
*   6) changed how auto level works by exposing by exposing as variables everything that used to be constant in the code.
*   7) added support for celsius values.
*   8) refactored a bunch of code and may have squashed a bug or two in the process.
*   9) refactored a bunch for hubitat compatibility.
*
*  Version: 0.17.4
*
*   DONE:   3/25/2018
*   1) removed option to selectively turn off night switches instead of turning off all when leaving ASLEEP state.
*   2) made fan control standalone from heating / cooling.
*   3) added option to turn night lights on when entering or exiting ASLEEP state.
*
*  Version: 0.17.2
*
*   DONE:   3/25/2018       FROM: @TonyFleisher
*   1) added option to selectively turn off night switches instead of turning off all when leaving ASLEEP state.
*   2) fixed a bug i introduced by turning on night switches instead of turning them off.
*
*  Version: 0.17.0
*
*   DONE:   3/24/2018
*   1) refactored engaged state check to be more consistent.
*   2) added fan support to temperature settings and rules.
*   3) changed how heating and cooling works in it that no longer turns off the thermostat only raises and lowers the
*       temperature and sets to cooling or heating mode but never turns off the thermostat.
*   4) added support for named holiday light strings which can be used in automation rules.
*   5) restructred the rules page a bit so not everything is on one page.
*   6) added speech synthesis device for using things for annoucements.
*   7) added random closing string to welcome and left home annoucements.
*
*  Version: 0.16.0
*
*   DONE:   3/15/2018
*   1) code refactoring for hubitat compatibility.
*   2) changed occupancy attribute to enum which allows for subscription to occupancy state while string dpes not.
        thanks @mark2k on ST community forum.
*   3) added default settings for wake and sleep time for level and kelvin calculation for 'AL' settings.
*
*  Version: 0.15.2
*
*   DONE:   3/5/2018
*   1) added support for icon URL setting for icon to display for each room.
*
*  Version: 0.15.0
*
*   DONE:   3/2/2018
*   1) added icons to main settings page for room.
*
*  Version: 0.14.6
*
*   DONE:   2/28/2018
*   1) added support for humidity sensor in rules.
*   2) added contact stays open notification.
*
*  Version: 0.14.4
*
*   DONE:   2/26/2018
*   1) added support for battery check and annoucement on low battery.
*
*  Version: 0.14.2
*
*   DONE:   2/25/2018
*   1) added setting for annoucement volume.
*   2) added support for outside door open/close announcement.
*
*  Version: 0.14.0
*
*   DONE:   2/25/2018
*   1) update device tiles to be more verbose.
*
*  Version: 0.12.6
*
*   DONE:   2/14/2018
*   1) added setting to pick state to be set when 'room device switch' turned on.
*
*  Version: 0.12.5
*
*   DONE:   2/11/2018
*   1) added setting for dim to level if no bulb is on in checking state.
*   2) added temperature offset between thermostat and room temperature sesnor.
*
*  Version: 0.12.2
*
*   DONE:   2/10/2018
*   1) added setting to require occupancy before triggering engaged state with power.
*   2) couple of bug fixes.
*
*  Version: 0.12.0
*
*   DONE:   2/8/2018
*   1) added alarm to rooms occupancy. tested somewhat. family kind of upset with me for random alarms going off :-(
*   2) sunrise & sunset now support offset in minutes. so if you always wanted sunrise -30 or sunset +30 now you can.
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
*   2) added support to process rules every 15 minutes so switches state/level/color temperature is updated even
*       when there is no motion in room but there are switches on.
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
*   1) added support to reset room state from ENAGED or ASLEEP when another room changes to ENGAGED or ASLEEP
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
*  Version: 0.07.1
*
*   DONE:   11/28/2017
*   1) Fixed removed code
*   2) Added ability to choose action for night button
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
***********************************************************************************************************************/

import groovy.transform.Field
import groovy.time.*

@Field final String lastMotionActive   = '1'
@Field final String lastMotionInactive = '2'

@Field final String occupancy  = 'occupancy'

@Field final String asleep   = 'asleep'
@Field final String engaged  = 'engaged'
@Field final String occupied = 'occupied'
@Field final String vacant   = 'vacant'
@Field final String checking = 'checking'
@Field final String locked   = 'locked'

@Field final String open        = 'open'
@Field final String closed      = 'closed'
@Field final String active      = 'active'
@Field final String inactive    = 'inactive'
@Field final String on          = 'on'
@Field final String off         = 'off'
@Field final String present     = 'present'
@Field final String notpresent  = 'not present'

// @Field final String noTraffic       = '0'
@Field final String lightTraffic   = '5'
@Field final String mediumTraffic  = '7'
@Field final String heavyTraffic   = '9'

@Field final int    pauseMSecST = 10
@Field final int    pauseMSecHU = 50

@Field final int    fanLow      = 33
@Field final int    fanMedium   = 67
@Field final int    fanHigh     = 99

@Field final String _SmartThings = 'ST'
@Field final String _Hubitat     = 'HU'

@Field final List   occupancyButtons =
        [[1:"occupied"],[2:"checking"],[3:"vacant"],[4:"locked"],[5:"reserved"],[6:"kaput"],[7:"donotdisturb"],[8:"asleep"],[9:"engaged"]]
@Field final List   genericButtons =
        [[1:"One"],[2:"Two"],[3:"Three"],[4:"Four"],[5:"Five"],[6:"Six"],[7:"Seven"],[8:"Eight"],[9:"Nine"],[10:"Ten"],[11:"Eleven"],[12:"Twelve"]]

definition	(
    name: "rooms child app",
    namespace: "bangali",
    parent: "bangali:rooms manager",
    author: "bangali",
    description: "DO NOT INSTALL DIRECTLY. Rooms child smartapp will create new rooms using 'rooms occupancy' DTH from the Rooms Manager smartapp.",
    category: "My Apps",
    iconUrl: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomOccupancy.png",
    iconX2Url: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomOccupancy@2x.png",
    iconX3Url: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomOccupancy@3x.png"
)

preferences {
	page(name: "roomName", title: "Room Name and Settings")
    page(name: "pageOnePager", title: "Easy Settings")
    page(name: "pageOccupiedSettings", title: "Occupied State Settings")
    page(name: "pageEngagedSettings", title: "Engaged State Settings")
    page(name: "pageCheckingSettings", title: "Checking State Settings")
    page(name: "pageVacantSettings", title: "Vacant State Settings")
    page(name: "pageOtherDevicesSettings", title: "Room Devices")
    page(name: "pageAutoLevelSettings", title: "Light Auto Level Settings")
    page(name: "pageHolidayLightPatterns", title: "Holiday Light Patterns")
    page(name: "pageHolidayLight", title: "Holiday Light Pattern")
    page(name: "pageRules", title: "Maintain Rules")
    page(name: "pageRule", title: "Edit Lighting Rule")
    page(name: "pageHumidity", title: "Edit Rule Humidity")
    page(name: "pageRuleDate", title: "Edit Rule Date")
    page(name: "pageRuleTime", title: "Edit Rule Time")
    page(name: "pageRuleOthers", title: "Edit Other Execution Rule Settings")
    page(name: "pageRuleTimer", title: "Edit Rule Timers")
    page(name: "pageAsleepSettings", title: "Asleep State Settings")
    page(name: "pageLockedSettings", title: "Locked State Settings")
    page(name: "pageAdjacentRooms", title: "Adjacent Rooms Settings")
    page(name: "pageRoomTemperature", title: "Room Temperature Settings")
    page(name: "pageGeneralSettings", title: "General Settings")
    page(name: "pageAllSettings", title: "All Settings")
}

def roomName()	{
    def roomNames = parent.getRoomNames(app.id)
    def luxAndTimeSettings = (luxSensor || timeSettings)
    def autoLevelSettings = (minLevel || maxLevel || state.ruleHasAL || autoColorTemperature)
    def holidayLightsSettings = false
    for (def i = 1; i <= 10; i++)
        if (settings["holiName$i"] || settings["holiColorString$i"])        {
            holidayLightsSettings = true
            break
        }
    def timeSettings = (fromTimeType || toTimeType)
    def adjRoomSettings = (adjRooms ? true : false)
    def miscSettings = (awayModes || pauseModes || dayOfWeek)
    def engagedSettings = (busyCheck || engagedButton || buttonIs || engagedSwitch || contactSensor || noMotionEngaged)
    def otherDevicesSettings = (personsPresence || luxAndTimeSettings || musicDevice || powerMeter)
//    def luxSettings = (luxSensor || luxThreshold)
//    def luxAndTimeSettings = (luxSettings || timeSettings)
    def asleepSettings = (asleepSensor || nightSwitches)
    state.passedOn = false
    state.holiPassedOn = false
    def roomIconURL
    if (iconURL)    roomIconURL = iconURL
    else            roomIconURL = "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomOccupancySettings.png"
	dynamicPage(name: "roomName", title: "Main Settings Page", install: true, uninstall: childCreated())		{
        section("Room name:")		{
            if (!childCreated())    {
                paragraph "ENTER ROOM NAME AND SAVE THE ROOM. THEN EDIT ROOM, TO ADD SETTINGS AND RULES. DO NOT ADD SETTINGS AND RULES WITHOUT FIRST SAVING THE ROOM ONCE."
				label title: "Room Name:", required: true, image: "$roomIconURL"
            }
            else
                label title: "Room Name:", required: true, image: "$roomIconURL"
//                paragraph "Room Name:\n${app.label}", image: "$roomIconURL"
		}
        section("One pager:")     {
            input "onePager", "bool", title: "Switch to easy settings?", required: false, multiple: false, defaultValue: false, submitOnChange: true,
                            image: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsOnePage.png"
        }
        if (onePager)   {
            section("One pager message:")     {
                paragraph "App is in easy settings mode. In this mode only a few settings are available for first time users to get started quickly. For more advanced settings please unset the easy settings toggle above. Any settings entered will be preserved."
    			href "pageOnePager", title: "EASY SETTINGS", description: (motionSensors ? "Tap to change existing settings" : "Tap to configure"),
                                image: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsEasy.png"
    		}
        }
        else    {
//            section		{
//                paragraph "Following settings are optional. Corresponding actions will be skipped when setting is blank."
//            }
            section("Room devices:")        {
                    href "pageOtherDevicesSettings", title: "ROOM DEVICES", description: (otherDevicesSettings ? "Tap to change existing settings" : "Tap to configure"),
                                image: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsOtherDevices.png"
            }
            section("Occupied settings:")       {
    				href "pageOccupiedSettings", title: "OCCUPIED SETTINGS", description: (motionSensors ? "Tap to change existing settings" : "Tap to configure"),
                                image: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsOccupied.png"

    		}
            section("Engaged settings:")        {
    				href "pageEngagedSettings", title: "ENGAGED SETTINGS", description: (engagedSettings ? "Tap to change existing settings" : "Tap to configure"),
                                image: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsEngaged.png"

    		}
            section("Checking settings:")       {
    				href "pageCheckingSettings", title: "CHECKING SETTINGS", description: ((dimTimer || dimByLevel) ? "Tap to change existing settings" : "Tap to configure"),
                                image: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsChecking.png"
    		}
            section("Vacant settings:")     {
    				href "pageVacantSettings", title: "VACANT SETTINGS", description: (turnOffMusic ? "Tap to change existing settings" : "Tap to configure"),
                                image: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsVacant.png"

    		}
            section("Asleep settings:")     {
    				href "pageAsleepSettings", title: "ASLEEP SETTINGS", description: (asleepSettings ? "Tap to change existing settings" : "Tap to configure"),
                                image: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsAsleep.png"
    		}
            section("Locked settings:")     {
    				href "pageLockedSettings", title: "LOCKED SETTINGS", description: (lockedSwitch ? "Tap to change existing settings" : "Tap to configure"),
                                image: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsLocked.png"
    		}
            section("'AL' settings:")       {
    				href "pageAutoLevelSettings", title: "AUTO LEVEL 'AL' SETTINGS", description: (autoLevelSettings ? "Tap to change existing settings" : "Tap to configure"),
                                image: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsLightLevel.png"
    		}
            section("'HL' settings:")       {
    				href "pageHolidayLightPatterns", title: "HOLIDAY LIGHTS 'HL' SETTINGS", description: (holidayLightsSettings ? "Tap to change existing settings" : "Tap to configure"),
                                image: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsHolidayLights2.png"
    		}
            section("Temperature settings:")        {
    				href "pageRoomTemperature", title: "TEMPERATURE SETTINGS", description: (tempSensors || maintainRoomTemp ? "Tap to change existing settings" : "Tap to configure"),
                                image: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsTemperature.png"
    		}
            section("Maintain rules:")      {
    				href "pageRules", title: "MAINTAIN RULES", description: "Create/Edit/Disable rules",
                                image: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsRules.png"
    		}
            section("Adjacent rooms settings:")     {
    				href "pageAdjacentRooms", title: "ADJACENT ROOMS SETTINGS", description: (adjRoomSettings ? "Tap to change existing settings" : "Tap to configure"),
                                image: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsAdjacent5.png"
    		}
            section("General settings:")        {
    				href "pageGeneralSettings", title: "MODE & OTHER SETTINGS", description: (miscSettings ? "Tap to change existing settings" : "Tap to configure"),
                                image: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsSettings.png"

    		}
        }
        section("View all settings:") {
				href "pageAllSettings", title: "VIEW ALL SETTINGS", description: "Tap to view all settings",
                            image: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsViewAll.png"
		}
        if (getHubType() == _SmartThings)
            remove("Remove Room", "Remove Room ${app.label}")
        else
            remove
	}
}

def getHubType()        {
//    if (!state.hubDomain)   state.hubDomain = location.getHubs().find{ it.getType().toString() == 'PHYSICAL' }
//    if (state.hubDomain.matches("Smartthings(.*)")) return _SmartThings;
//    else                                            return _Hubitat

    if (!state.hubId)   state.hubId = location.hubs[0].id.toString()
    if (state.hubId.length() > 5)   return _SmartThings;
    else                            return _Hubitat;
}

private pageOnePager()      {
	dynamicPage(name: "pageOnePager", title: "One Pager", install: false, uninstall: false)     {
        section("Motion sensor for OCCUPIED state:", hideable: false)        {
            input "motionSensors", "capability.motionSensor", title: "Which motion sensor(s)?", required: true, multiple: true, submitOnChange: true
        }
        section("Timeout configuration for OCCUPIED state:", hideable:fase) {
            if (motionSensors)
                input "noMotion", "number", title: "After how many seconds?", required: true, multiple: false, defaultValue: 300, range: "5..99999", submitOnChange: true
            else
                paragraph "After how many seconds?\nselect motion sensor(s) above to set"
        }
        section("Change room to ENGAGED with traffic?", hideable: false)		{
            if (motionSensors)
                input "busyCheck", "enum", title: "When room is busy?", required: false, multiple: false, defaultValue: 7,
                            options: [[null:"No auto engaged"],[5:"Light traffic"],[7:"Medium Traffic"],[9:"Heavy Traffic"]]
            else
                paragraph "When room is busy?\nselect motion sensor(s) above to set."
        }
        section("Timeout configuration for ENGAGED state:", hideable:false) {
            if (motionSensors)
                input "noMotionEngaged", "number", title: "After how many seconds?", required: false, multiple: false, defaultValue: 1800, range: "5..99999", submitOnChange: true
            else
                paragraph "After how many seconds?\nselect motion sensor(s) above to set"
        }
        section("Timeout configuration for CHECKING state:", hideable: false)		{
            input "dimTimer", "number", title: "After how many seconds?", required: true, multiple: false, defaultValue: 90, range: "5..99999", submitOnChange: true
            if (dimTimer)       {
                input "dimByLevel", "enum", title: "Dim lights that are on by what level?", required: false, multiple: false, defaultValue: null,
                                    options: [[10:"10%"],[20:"20%"],[30:"30%"],[40:"40%"],[50:"50%"],[60:"60%"],[70:"70%"],[80:"80%"],[90:"90%"]]
                input "dimToLevel", "enum", title: "If no light is on turn on at what level?", required: false, multiple: false, defaultValue: null,
                                    options: [[1:"1%"],[10:"10%"],[20:"20%"],[30:"30%"],[40:"40%"],[50:"50%"],[60:"60%"],[70:"70%"],[80:"80%"],[90:"90%"]]
            }
            else    {
                paragraph "If any light is on dim by what level?\nselect timer seconds above to set"
                paragraph "If no light is on turn on room lights and dim to what level?\nselect timer seconds above to set"
            }
        }
        section("States and switches:", hideable:false)     {
            input "state1", "enum", title: "Which state?", required: true, multiple: true, options: [occupied, engaged], defaultValue: [occupied, engaged]
            input "switchesOn1", "capability.switch", title: "Turn on which switches?", required: true, multiple: true
            input "setLevelTo1", "enum", title: "Set level when turning on?", required: false, multiple: false, defaultValue: null, submitOnChange: true,
                options: [[1:"1%"],[5:"5%"],[10:"10%"],[20:"20%"],[30:"30%"],[40:"40%"],[50:"50%"],[60:"60%"],[70:"70%"],[80:"80%"],[90:"90%"],[99:"99%"],[100:"100%"]]
        }
        section("Turn off all switches on no rule match?", hideable: false)		{
            input "allSwitchesOff", "bool", title: "Turn OFF all switches?", required: true, multiple: false, defaultValue: true
        }
	}
}

private pageOccupiedSettings()      {
    def buttonNames = genericButtons
    def occupiedButtonOptions = [:]
    if (occupiedButton)      {
        def occupiedButtonAttributes = occupiedButton.supportedAttributes
        def attributeNameFound = false
        occupiedButtonAttributes.each  { att ->
            if (att.name == occupancy)      buttonNames = occupancyButtons;
            if (att.name == 'numberOfButtons')      attributeNameFound = true;
        }
        def numberOfButtons = occupiedButton.currentNumberOfButtons
        if (attributeNameFound && numberOfButtons)      {
            def i = 0
            for (; i < numberOfButtons; i++)
                occupiedButtonOptions << buttonNames[i]
        }
        else
            occupiedButtonOptions << [null:"No buttons"]
    }
    def hT = getHubType()
    dynamicPage(name: "pageOccupiedSettings", title: "Occupied Settings", install: false, uninstall: false)     {
        section("Button for OCCUPIED state:", hideable: false)		{
            input "occupiedButton", "capability.${(hT == _SmartThings ? 'button' : 'pushableButton')}", title: "Button is pushed?", required: false, multiple: false, submitOnChange: true
            if (occupiedButton)       {
                input "buttonIsOccupied", "enum", title: "Button Number?", required: true, multiple: false, defaultValue: null, options: occupiedButtonOptions
                input "buttonOnlySetsOccupied", "bool", title: "Button only sets Occupied?", description: "if false will toggle occupied and vacant", required: false, multiple: false, defaultValue: false
//                input "buttonToggleWithOccupied", "bool", title: "If room state is OCCUPIED toggle to VACANT state?", required: true, multiple: false, defaultValue: false, options: occupiedButtonOptions
            }
            else        {
                paragraph "Button Number?\nselect button above to set"
                paragraph "Button only sets Occupied?\nselect button above to set"
//                paragraph "If room state is OCCUPIED toggle to VACANT state?\nselect button to set"
            }
        }
        section("Switch for OCCUPIED state:", hideable:false)	{
            input "occSwitches", "capability.switch", title: "Which switch turns ON?", required:false, multiple: true, submitOnChange: true
        }
        section("Timeout configuration for OCCUPIED state:", hideable:fase) {
            if (hasOccupiedDevice())
                input "noMotion", "number", title: "After how many seconds?", required: false, multiple: false, defaultValue: null, range: "5..99999", submitOnChange: true
            else
                paragraph "After how many seconds?\nselect occupancy device to set"
        }
	}
}

private pageEngagedSettings()       {
    def buttonNames = genericButtons
    def engagedButtonOptions = [:]
    if (engagedButton)      {
        def engagedButtonAttributes = engagedButton.supportedAttributes
        def attributeNameFound = false
        engagedButtonAttributes.each  { att ->
            if (att.name == occupancy)
                buttonNames = occupancyButtons
            if (att.name == 'numberOfButtons')
                attributeNameFound = true
        }
        def numberOfButtons = engagedButton.currentNumberOfButtons
        if (attributeNameFound && numberOfButtons)      {
            def i = 0
            for (; i < numberOfButtons; i++)
                engagedButtonOptions << buttonNames[i]
        }
        else
            engagedButtonOptions << [null:"No buttons"]
    }
    def roomDevices = parent.getRoomNames(app.id)
    def hT = getHubType()
	dynamicPage(name: "pageEngagedSettings", title: "Engaged Settings", install: false, uninstall: false)      {
		section("Change room to ENGAGED when?\n(if specified this will also reset room state to 'vacant' when the button is pushed again or presence sensor changes to not present etc.)", hideable: false)		{
            paragraph "Settings are in order of priority in which they are checked. For example, if there is both an engaged switch and contact sensor the engaged switch when ON will take priority over the contact sensor being OPEN."
            if (motionSensors)
                input "busyCheck", "enum", title: "When room is busy?", required: false, multiple: false, defaultValue: null,
                            options: [[null:"No auto engaged"],[5:"Light traffic"],[7:"Medium Traffic"],[9:"Heavy Traffic"]]
            else
                paragraph "When room is busy?\nselect motion sensor(s) above to set."
            input "engagedButton", "capability.${(hT == _SmartThings ? 'button' : 'pushableButton')}", title: "Button is pushed?", required: false, multiple: false, submitOnChange: true
            if (engagedButton)      {
                input "buttonIs", "enum", title: "Button number?", required: true, multiple: false, defaultValue: null, options: engagedButtonOptions
                input "buttonOnlySetsEngaged", "bool", title: "Button only sets Engaged?", description: "if false will toggle engaged and vacant", required: false, multiple: false, defaultValue: false
            }
            else        {
                paragraph "Button number?\nselect button above to set"
                paragraph "Button only sets Engaged?\nselect button above to set"
            }
            if (personsPresence)    {
                input "presenceAction", "enum", title: "Presence sensor actions?", required: true, multiple: false, defaultValue: 3,
                            options: [[1:"Set state to ENGAGED on Arrival"],[2:"Set state to VACANT on Departure"],[3:"Both actions"],[4:"Neither action"]]
                input "presenceActionContinuous", "bool", title: "Keep room engaged when presence sensor present?", required: false, multiple: false, defaultValue: false
            }
            else    {
                paragraph "Presence sensor actions?\nselect presence sensor(s) to set"
                paragraph "Keep room engaged when presence sensor present?\nselect presence sensor(s) to set"
            }
            if (musicDevice)
                input "musicEngaged", "bool", title: "Set room to engaged when music starts playing?", required: false, multiple: false, defaultValue: false
            else
                paragraph "Set room to engaged when music is playing?\nselect music device in speaker settings to set."
            input "engagedSwitch", "capability.switch", title: "Switch turns ON?", required: false, multiple: true
            if (powerDevice)    {
                if (!powerValueAsleep && !powerValueLocked)      {
                    input "powerValueEngaged", "number", title: "Power value to set room to ENGAGED state?", required: false, multiple: false, defaultValue: null, range: "0..99999", submitOnChange: true
                    input "powerTriggerFromVacant", "bool", title: "Power value triggers ENGAGED from VACANT state?", required: false, multiple: false, defaultValue: true
                    input "powerStays", "number", title: "Power stays below for how many seconds to reset ENGAGED state?", required: (powerValueEngaged ? true : false), multiple: false, defaultValue: 30, range: "30..999"
                }
                else        {
                    paragraph "Power value to set room to ENGAGED state?\npower already used to set room to ASLEEP."
                    paragraph "Power value triggers ENGAGED from VACANT state?"
                    paragraph "Power stays below for how many seconds to reset ENGAGED state?"
                }
            }
            else        {
                paragraph "Power value to set room to ENGAGED?\nselect power device in other devices to set."
                paragraph "Power value triggers ENGAGED from VACANT state?"
                paragraph "Power stays below for how many seconds to reset ENGAGED state?"
            }
            input "contactSensor", "capability.contactSensor", title: "Contact sensor closes?", required: false, multiple: true, submitOnChange: true
            if (contactSensor)      {
                input "contactSensorOutsideDoor", "bool", title: "Contact sensor on outside door?", required: false, multiple: false, defaultValue: false
                input "contactSensorNotTriggersEngaged", "bool", title: "Contact sensor does not trigger ENGAGED state?", required: false, multiple: false, defaultValue: false
            }
            else        {
                paragraph "Contact sensor on outside door?\nselect contact sensor above to set."
                paragraph "Contact sensor does not trigger ENGAGED state?\nselect contact sensor above to set."
            }
            input "noMotionEngaged", "number", title: "Require motion within how many seconds when room is ENGAGED?", required: false, multiple: false, defaultValue: null, range: "5..99999"
            input "anotherRoomEngaged", "enum", title: "Reset ENGAGED OR ASLEEP state when another room changes to ENGAGED OR ASLEEP? If yes, which room(s)?", required: false, multiple: true, defaultValue: null, options: roomDevices
            input "resetEngagedDirectly", "bool", title: "When resetting room from 'ENGAGED' directly move to 'VACANT' state?", required: false, multiple: false, defaultValue: false
        }
	}
}

/*
private buttonDetails(button)     {
    if (!button)    return;
    def supportedCaps = button.capabilities
    supportedCaps.each {cap ->
        ifDebug("This device supports the ${cap.name} capability")
    }
    def theAtts = button.supportedAttributes
    theAtts.each {att ->
        ifDebug("Supported Attribute: ${att.name}")
    }
}
*/

private pageCheckingSettings()      {
	dynamicPage(name: "pageCheckingSettings", title: "Checking Settings", install: false, uninstall: false)     {
        section("CHECKING state timer before room changes to VACANT:", hideable: false)		{
            input "dimTimer", "number", title: "For how many seconds? (this value should be higher than your motion sensor blind window. recommended value 2 x motion sensor blind window. this also doubles as the dim timer to dim lights for same number of seconds.)", required: false, multiple: false, defaultValue: 5, range: "5..99999", submitOnChange: true
            if (dimTimer)       {
                input "dimByLevel", "enum", title: "If any light is on dim by what level?", required: false, multiple: false, defaultValue: null,
                                    options: [[10:"10%"],[20:"20%"],[30:"30%"],[40:"40%"],[50:"50%"],[60:"60%"],[70:"70%"],[80:"80%"],[90:"90%"]]
                input "dimToLevel", "enum", title: "If no light is on turn on and dim to what level?", required: false, multiple: false, defaultValue: null, submitOnChange: true, options: [[1:"1%"],[10:"10%"],[20:"20%"],[30:"30%"],[40:"40%"],[50:"50%"],[60:"60%"],[70:"70%"],[80:"80%"],[90:"90%"]]
            }
            else    {
                paragraph "If any light is on dim by what level?\nselect timer seconds above to set"
                paragraph "If no light is on turn on and dim to what level?\nselect timer seconds above to set"
            }
            if (dimTimer && dimToLevel && luxSensor)
                input "luxCheckingDimTo", "number", title: "What lux value?", description: "if no light on only turn on when <= lux.", required: false, multiple: false, defaultValue: null, range: "0..*"
            else
                paragraph "What lux value?\nset dim timer, dim to level and lux sensor to select."
        }
        section("Light level", hideable: false)		{
            input "notRestoreLL", "bool", title: "Do not restore light level after dimming during CHECKING if VACANT now?", required: false, multiple: false, defaultValue: false
        }
	}
}

private pageVacantSettings()      {
    def buttonNames = genericButtons
    def vacantButtonOptions = [:]
    if (vacantButton)      {
        def vacantButtonAttributes = vacantButton.supportedAttributes
        def attributeNameFound = false
        vacantButtonAttributes.each  { att ->
            if (att.name == occupancy)      buttonNames = occupancyButtons;
            if (att.name == 'numberOfButtons')      attributeNameFound = true;
        }
        def numberOfButtons = vacantButton.currentNumberOfButtons
        if (attributeNameFound && numberOfButtons)      {
            def i = 0
            for (; i < numberOfButtons; i++)
                vacantButtonOptions << buttonNames[i]
        }
        else
            vacantButtonOptions << [null:"No buttons"]
    }
    def hT = getHubType()
	dynamicPage(name: "pageVacantSettings", title: "Vacant Settings", install: false, uninstall: false)     {
        section("Button for VACANT state:", hideable: false)		{
            input "vacantButton", "capability.${(hT == _SmartThings ? 'button' : 'pushableButton')}", title: "Button is pushed?", required: false, multiple: false, submitOnChange: true
            if (vacantButton)       {
                input "buttonIsVacant", "enum", title: "Button Number?", required: true, multiple: false, defaultValue: null, options: vacantButtonOptions
//                input "buttonToggleWithVacant", "bool", title: "If room state is VACANT toggle to OCCUPIED state?", required: true, multiple: false, defaultValue: false, options: vacantButtonOptions
            }
            else        {
                paragraph "Button Number?\nselect button to set"
//                paragraph "If room state is VACANT toggle to OCCUPIED state?\nselect button to set"
            }
        }
        section("Switch for VACANT state:", hideable:false)	{
            input "vacantSwitches", "capability.switch", title: "Which switch turns OFF?", required: false, multiple: true
        }
        section("Pause music on VACANT state:", hideable:false)	{
            if (musicDevice)
                input "turnOffMusic", "bool", title: "Pause speaker when room changes to VACANT?", required: false, multiple: false, defaultValue: false
            else
                paragraph "Stop speaker when room changes to VACANT?\nselect music player in speaker settings to set"
        }
	}
}


private pageOtherDevicesSettings()       {
	dynamicPage(name: "pageOtherDevicesSettings", title: "Room Sensors", install: false, uninstall: false)      {
        section("MOTION SENSOR(s):", hideable: false)        {
            input "motionSensors", "capability.motionSensor", title: "Which motion sensor?", required: false, multiple: true, submitOnChange: true
            if (motionSensors)
                input "whichNoMotion", "enum", title: "Use which motion event for timeout?", required: true, multiple: false, defaultValue: 2, submitOnChange: true, options: [[1:"Last Motion Active"],[2:"Last Motion Inactive"]]
            else
                paragraph "Use which motion event for timeout?\nselect motion sensor above to set"
        }
		section("PRESENCE SENSOR(s):", hideable: false)      {
            input "personsPresence", "capability.presenceSensor", title: "Presence sensors?", required: false, multiple: true, submitOnChange: true
        }
        section("LUX SENSOR:", hideable: false)      {
            input "luxSensor", "capability.illuminanceMeasurement", title: "Which lux sensor?", required: false, multiple: false
        }
        section("HUMIDITY SENSOR:", hideable: false)      {
            input "humiditySensor", "capability.relativeHumidityMeasurement", title: "Which humidity sensor?", required: false, multiple: false
        }
        section("MUSIC PLAYER:", hideable: false)      {
            input "musicDevice", "capability.musicPlayer", title: "Which music player?", required: false, multiple: false
        }
        section("POWER METER:", hideable: false)      {
            input "powerDevice", "capability.powerMeter", title: "Which power meter?", required: false, multiple: false
        }
        section("WINDOW SHADE:", hideable: false)      {
            input "windowShades", "capability.windowShade", title: "Which window shade?", required: false, multiple: true
        }
//        section("SPEECH RECOGNITION:", hideable: false)      {
//            input "speechDevice", "capability.speechRecognition", title: "Which speech device?", required: false, multiple: false
//        }
	}
}

/*
private pageAutoLevelSettings()     {
    ifDebug("pageAutoLevelSettings")
    def wTime
    def sTime
    def hT = getHubType()
    if (state.ruleHasAL || wakeupTime || sleepTime)     {
        if (!wakeupTime || !sleepTime)      {
            if (hT == 'ST')      sendNotification("Invalid time range!", [method: "push"]);
        }
        else        {
            wTime = timeToday(wakeupTime, location.timeZone)
            sTime = timeToday(sleepTime, location.timeZone)
            if (wTime > sTime || ((sTime.getTime() - wTime.getTime()) < 18000L))        {
                if (hT == 'ST')     sendNotification("Invalid time range!", [method: "push"]);
            }
        }
    }
    updateRulesToState()
    def levelRequired = (autoColorTemperature || state.ruleHasAL || minLevel || maxLevel ? true : false)
    wTime = timeToday("07:00", location.timeZone).format("yyyy-MM-dd'T'HH:mm:ss.SSSZ", location.timeZone)
    sTime = timeToday("22:00", location.timeZone).format("yyyy-MM-dd'T'HH:mm:ss.SSSZ", location.timeZone)
	dynamicPage(name: "pageAutoLevelSettings", title: "'AL' Level Settings", install: false, uninstall: false)    {
        section("Auto light level min/max:", hideable: false)		{
                input "minLevel", "number", title: "Minimum level?", required: levelRequired, multiple: false, defaultValue: (levelRequired ? 1 : null), range: "1..${maxLevel ?: 100}", submitOnChange: true
                input "maxLevel", "number", title: "Maximum level?", required: levelRequired, multiple: false, defaultValue: (levelRequired ? 100 : null), range: "${minLevel ?: 1}..100", submitOnChange: true
        }
        section("Fade level:", hideable: false)		{
            input "fadeLevelOnlyBeforeSleep", "bool", title: "Fade level only before sleep?", required: true, multiple: false, defaultValue: false, submitOnChange: true
        }
        section("Wake and sleep time:", hideable: false)		{
            input "wakeupTime", "time", title: "Wakeup Time?", required: levelRequired, multiple: false, defaultValue: wTime, submitOnChange: true
            input "sleepTime", "time", title: "Sleep Time?", required: levelRequired, multiple: false, defaultValue: sTime, submitOnChange: true
        }
        section("Auto color temperature:", hideable: false)		{
            input "autoColorTemperature", "bool", title: "Auto set color temperature?", required: false, multiple: false, defaultValue: false, submitOnChange: true
            if (autoColorTemperature)       {
                input "minKelvin", "number", title: "Minimum kelvin?", required: true, multiple: false, defaultValue: 1900, range: "1500..${maxKelvin?:9000}", submitOnChange: true
                input "maxKelvin", "number", title: "Maximum kelvin?", required: true, multiple: false, defaultValue: 6500, range: "$minKelvin..9000", submitOnChange: true
            }
            else    {
//                paragraph "Wakeup time?\nenable auto color temperature above to set"
//                paragraph "Sleep time?\nenable auto color temperature above to set"
                paragraph "Minimum kelvin?\nenable auto color temperature above to set"
                paragraph "Maximum kelvin?\nenable auto color temperature above to set"
            }
        }
        section("Simple 'AL':", hideable: false)		{
            input "simpleAL", "bool", title: "Use simple 'AL'?\nmax during wake hours and min at the other times.", required: levelRequired, multiple: false, defaultValue: false
        }
    }
}
*/

private pageAutoLevelSettings()     {
    ifDebug("pageAutoLevelSettings")
    def wTime
    def sTime
    def hT = getHubType()
    if (state.ruleHasAL || wakeupTime || sleepTime)
        if (!wakeupTime || !sleepTime)
            if (hT == _SmartThings)      sendNotification("Invalid time range!", [method: "push"]);
    updateRulesToState()
    def levelRequired = (autoColorTemperature || state.ruleHasAL || minLevel || maxLevel ? true : false)
//    wTime = timeToday("07:00", location.timeZone).format("yyyy-MM-dd'T'HH:mm:ss.SSSZ", location.timeZone)
//    sTime = timeToday("22:00", location.timeZone).format("yyyy-MM-dd'T'HH:mm:ss.SSSZ", location.timeZone)
	dynamicPage(name: "pageAutoLevelSettings", title: "'AL' Level Settings", install: false, uninstall: false)    {
        section("Auto light level min/max:", hideable: false)		{
                input "minLevel", "number", title: "Minimum level?", required: levelRequired, multiple: false, defaultValue: (levelRequired ? 1 : null), range: "1..${maxLevel ?: 100}", submitOnChange: true
                input "maxLevel", "number", title: "Maximum level?", required: levelRequired, multiple: false, defaultValue: (levelRequired ? 100 : null), range: "${minLevel ?: 1}..100", submitOnChange: true
        }
        if (levelRequired)      {
            section("Wake and sleep time:", hideable: false)		{
                input "wakeupTime", "time", title: "Wakeup Time?", required: levelRequired, multiple: false, defaultValue: "07:00", submitOnChange: true
                input "sleepTime", "time", title: "Sleep Time?", required: levelRequired, multiple: false, defaultValue: "23:00", submitOnChange: true
            }
            section("Fade level up:", hideable: false)		{
                input "fadeLevelWake", "bool", title: "Fade up to wake time?", required: true, multiple: false, defaultValue: false, submitOnChange: true
                if (fadeLevelWake)      {
                    input "fadeWakeBefore", "number", title: "Starting how many hours before?",
                                        required: true, multiple: false, defaultValue: 1, range: "0..10", submitOnChange: true
                    input "fadeWakeAfter", "number", title: "Ending how many hours after?",
                                        required: true, multiple: false, defaultValue: 0, range: "0..10", submitOnChange: true
                }
                else        {
                    paragraph "Starting how many hours before?\nset fade level up to set."
                    paragraph "Ending how many hours after?\nset fade level up to set."
                }
            }
            section("Fade level down:", hideable: false)		{
                input "fadeLevelSleep", "bool", title: "Fade down to sleep time?", required: true, multiple: false, defaultValue: false, submitOnChange: true
                if (fadeLevelSleep)      {
                    input "fadeSleepBefore", "number", title: "Starting how many hours before?",
                                        required: true, multiple: false, defaultValue: 2, range: "0..10", submitOnChange: true
                    input "fadeSleepAfter", "number", title: "Ending how many hours after?",
                                        required: true, multiple: false, defaultValue: 0, range: "0..10", submitOnChange: true
                }
                else        {
                    paragraph "Starting how many hours before?\nset fade level down to set."
                    paragraph "Ending how many hours after?\nset fade level down to set."
                }
            }
            section("Auto color temperature:", hideable: false)		{
                input "autoColorTemperature", "bool", title: "Auto set color temperature?", required: false, multiple: false, defaultValue: false, submitOnChange: true
                if (autoColorTemperature)       {
                    input "minKelvin", "number", title: "Minimum kelvin?", required: true, multiple: false,
                                        defaultValue: 1900, range: "1500..${maxKelvin?:9000}", submitOnChange: true
                    input "maxKelvin", "number", title: "Maximum kelvin?", required: true, multiple: false,
                                        defaultValue: 6500, range: "$minKelvin..9000", submitOnChange: true
                }
                else    {
                    paragraph "Minimum kelvin?\nenable auto color temperature above to set"
                    paragraph "Maximum kelvin?\nenable auto color temperature above to set"
                }
            }
            section("Fade color temperature up:", hideable: false)		{
                input "fadeCTWake", "bool", title: "Fade up to wake time?", required: true, multiple: false, defaultValue: false, submitOnChange: true
                if (autoColorTemperature && fadeCTWake)       {
                    input "fadeKWakeBefore", "number", title: "Starting how many hours before?",
                                        required: true, multiple: false, defaultValue: 1, range: "0..10", submitOnChange: true
                    input "fadeKWakeAfter", "number", title: "Ending how many hours after?",
                                        required: true, multiple: false, defaultValue: 0, range: "0..10", submitOnChange: true
                }
                else    {
                    paragraph "Starting how many hours before?\nset fade auto color temperature up to set."
                    paragraph "Ending how many hours after?\nset fade auto color temperature up to set."
                }
            }
            section("Fade color temperature down:", hideable: false)		{
                input "fadeCTSleep", "bool", title: "Fade down to sleep time?", required: true, multiple: false, defaultValue: false, submitOnChange: true
                if (autoColorTemperature && fadeCTSleep)       {
                    input "fadeKSleepBefore", "number", title: "Starting how many hours before?",
                                        required: true, multiple: false, defaultValue: 5, range: "0..10", submitOnChange: true
                    input "fadeKSleepAfter", "number", title: "Ending how many hours after?",
                                        required: true, multiple: false, defaultValue: 0, range: "0..10", submitOnChange: true
                }
                else    {
                    paragraph "Starting how many hours before?\nset fade color temperature down to set."
                    paragraph "Ending how many hours after?\nset fade color temperature down to set."
                }
            }
        }
    }
}

private pageHolidayLightPatterns()     {
    ifDebug("pageHolidayLightPatterns")
    state.holiPassedOn = false
    updateRulesToState()
//    def hLR = (holiEvery || holiTwinkle || )
	dynamicPage(name: "pageHolidayLightPatterns", title: "'HL' Settings", install: false, uninstall: false)    {
        section("", hideable: false)         {
            def eCS = 99
            for (def i = 1; i <= 10; i++)    {
                if (settings["holiName$i"] || settings["holiColorString$i"])        {
                    href "pageHolidayLight", title: (settings["holiName$i"] ?: settings["holiColorString$i"]), params: [holiLightNo: "$i"], required: false
                    if (!(settings["holiName$i"] && settings["holiColorString$i"]))     eCS = 88;
                }
            }
            if (eCS == 99)
                for (def i = 1; i <= 10; i++)
                    if (!(settings["holiName$i"] && settings["holiColorString$i"]))     {
                        eCS = i;
                        break;
                    }
            if (eCS < 11)
                href "pageHolidayLight", title: "Create new holiday light pattern", params: [holiLightNo: "$eCS"], required: false
        }
    }
}

private pageHolidayLight(params)   {
    if (!state.holiPassedOn && params)      {
        state.holiPassedOn = true
        state.holiPassedParams = params
    }
    if (params.holiLightNo)
        state.pageHoliLightNo = params.holiLightNo
    else if (state.holiPassedParams)
        state.pageHoliLightNo = state.holiPassedParams.holiLightNo
    def holiLightNo = state.pageHoliLightNo
//    ifDebug("pageHolidayLight: holiLightNo: $holiLightNo")
    dynamicPage(name: "pageHolidayLight", title: "Holiday Light Pattern", install: false, uninstall: false)   {
        section()     {
            input "holiName$holiLightNo", "text", title: "Color string name?", required: true,
                                multiple: false, submitOnChange: true
            input "holiColorString$holiLightNo", "text", title: "Comma delimited colors?", required: true,
                                multiple: false, submitOnChange: true
            input "holiStyle$holiLightNo", "enum", title: "Light routine?", required: true, multiple: false, defaultValue: null,
                                options: [[RO:"Rotate"],[TW:"Twinkle"]], submitOnChange: true
            if (settings["holiStyle$holiLightNo"])        {
                input "holiSeconds$holiLightNo", "number", title: "${(settings["holiStyle$holiLightNo"] == 'RO' ? 'Rotate' : 'Twinkle')} every how many seconds?",
                                required: true, multiple: false, defaultValue: (settings["holiStyle$holiLightNo"] == 'RO' ? 15 : 3),
                                range: (settings["holiStyle$holiLightNo"] == 'RO' ? "5..300" : "3..10"), submitOnChange: true
                input "holiLevel$holiLightNo", "enum", title: "Set light level to?", required: true, multiple: false, defaultValue: null,
                                options: [[1:"1%"],[10:"10%"],[20:"20%"],[30:"30%"],[40:"40%"],[50:"50%"],[60:"60%"],[70:"70%"],[80:"80%"],[90:"90%"],[99:"99%"],[100:"100%"]]
            }
            else        {
                paragraph "${(settings["holiStyle$holiLightNo"] == 'RO' ? 'Rotate' : 'Twinkle')} every how many seconds?\nSelect holiday light style to set."
                paragraph "Set light level to?\nSelect holiday light style to set."
            }
        }
    }
}


private pageRules()     {
    webCoRE_init()
    updateRulesToState()
    state.passedOn = false
    state.pList = webCoRE_list()
    state.pEnum = [:]
    state.pList.each  { pL -> state.pEnum << [(pL.id):(pL.name)] }
//    if (state.pEnum)    state.pList = state.pEnum.sort{ it.value };
    state.pList = []
    state.pEnum.each  { k, v -> state.pList << [(k):v] }
    state.pEnum = [:]
	dynamicPage(name: "pageRules", title: "Maintain Rules", install: false, uninstall: false)    {
//        state.rules = [1:[ruleNo:1, name:'Rule 1', mode:location.currentMode, state:null, level:50, ct:2700, color:[saturation:80,hue:20]]]
        section()   {
            def emptyRule = null
            if (!state.rules)
                emptyRule = 1
            else    {
                def i = 1
                for (; i < 11; i++)     {
                    def ruleNo = String.valueOf(i)
                    def thisRule = getRule(ruleNo, '*', false)
                    if (thisRule)   {
                        def ruleDesc = "$ruleNo: $thisRule.name -"
                        ruleDesc = (thisRule.mode ? "$ruleDesc Mode=$thisRule.mode" : "$ruleDesc")
                        ruleDesc = (thisRule.state ? "$ruleDesc State=$thisRule.state" : "$ruleDesc")
//                        ruleDesc = (thisRule.luxThreshold != null ? "$ruleDesc Lux=$thisRule.luxThreshold" : (luxThreshold ? "$ruleDesc Lux=$luxThreshold" : "$ruleDesc"))
                        if (thisRule.type != 't')   {
                            ruleDesc = (thisRule.luxThreshold != null ? "$ruleDesc Lux=$thisRule.luxThreshold" : "$ruleDesc")
                            ruleDesc = (thisRule.piston ? "$ruleDesc Piston=$thisRule.piston" : "$ruleDesc")
                            ruleDesc = (thisRule.actions ? "$ruleDesc Routines=$thisRule.actions" : "$ruleDesc")
                        }
                        if (thisRule.fromTimeType && thisRule.toTimeType)        {
                            def ruleFromTimeHHmm = (thisRule.fromTime ? format24hrTime(timeToday(thisRule.fromTime, location.timeZone)) : '')
                            def ruleToTimeHHmm = (thisRule.toTime ? format24hrTime(timeToday(thisRule.toTime, location.timeZone)) : '')
                            ruleDesc = (thisRule.fromTimeType == timeTime() ? "$ruleDesc From=$ruleFromTimeHHmm" : (thisRule.fromTimeType == timeSunrise() ? "$ruleDesc From=Sunrise" : "$ruleDesc From=Sunset"))
                            ruleDesc = (thisRule.toTimeType == timeTime() ? "$ruleDesc To=$ruleToTimeHHmm" : (thisRule.toTimeType == timeSunrise() ? "$ruleDesc To=Sunrise" : "$ruleDesc To=Sunset"))
                        }
                        if (thisRule.type == 't')   {
                            ruleDesc = (thisRule.coolTemp ? "$ruleDesc Cool=$thisRule.coolTemp" : "$ruleDesc")
                            ruleDesc = (thisRule.heatTemp ? "$ruleDesc Heat=$thisRule.heatTemp" : "$ruleDesc")
                        }
                        else    {
                            ruleDesc = (thisRule.switchesOn ? "$ruleDesc ON=$thisRule.switchesOn" : "$ruleDesc")
                            ruleDesc = (thisRule.switchesOff ? "$ruleDesc OFF=$thisRule.switchesOff" : "$ruleDesc")
                            ruleDesc = (thisRule.disabled ? "$ruleDesc Disabled=$thisRule.disabled" : "$ruleDesc")
                        }
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
        state.pageRuleNo = params.ruleNo
    else if (state.passedParams)
        state.pageRuleNo = state.passedParams.ruleNo
    def ruleNo = state.pageRuleNo
    def ruleFromTimeType = settings["fromTimeType$ruleNo"]
    def ruleToTimeType = settings["toTimeType$ruleNo"]
    def ruleFromTimeHHmm = (settings["fromTime$ruleNo"] ? format24hrTime(timeToday(settings["fromTime$ruleNo"], location.timeZone)) : '')
    def ruleToTimeHHmm = (settings["toTime$ruleNo"] ? format24hrTime(timeToday(settings["toTime$ruleNo"], location.timeZone)) : '')
    def ruleFromTimeOffset = settings["fromTimeOffset$ruleNo"]
    def ruleToTimeOffset = settings["toTimeOffset$ruleNo"]
    def ruleTimerOverride = (settings["noMotion$ruleNo"] || settings["noMotionEngaged$ruleNo"] || settings["dimTimer$ruleNo"] || settings["noMotionAsleep$ruleNo"])
    def ruleType = settings["type$ruleNo"]
    def levelOptions = []
    levelOptions << [AL:"Auto Level (and color temperature)"]
    for (def i = 1; i < 11; i++)
        if (settings["holiName$i"] && settings["holiColorString$i"])       levelOptions << ["HL$i": settings["holiName$i"]];
    [[1:"1%"],[5:"5%"],[10:"10%"],[20:"20%"],[30:"30%"],[40:"40%"],[50:"50%"],[60:"60%"],[70:"70%"],[80:"80%"],[90:"90%"],[99:"99%"],[100:"100%"]].each    {
        levelOptions << it
    }
//    ifDebug("levelOptions: $levelOptions")
    boolean isFarenheit = (location.temperatureScale == 'F' ? true : false)
    dynamicPage(name: "pageRule", title: "Edit Rule", install: false, uninstall: false)   {
        section()     {
            ifDebug("rule number page ${ruleNo}")
            paragraph "$ruleNo"
			input "name$ruleNo", "text", title: "Rule name?", required:false, multiple: false, capitalization: "none"
            input "disabled$ruleNo", "bool", title: "Rule disabled?", required: false, multiple: false, defaultValue: false
			input "mode$ruleNo", "mode", title: "Which mode?", required: false, multiple: true
            input "state$ruleNo", "enum", title: "Which state?", required: false, multiple: true, options: [asleep, engaged, occupied, vacant]
            input "dayOfWeek$ruleNo", "enum", title: "Which days of the week? (condition)", required: false, multiple: false, defaultValue: null,
                    options: [[null:"All Days of Week"],[8:"Monday to Friday"],[9:"Saturday & Sunday"],[2:"Monday"],\
                                                          [3:"Tuesday"],[4:"Wednesday"],[5:"Thursday"],[6:"Friday"],[7:"Saturday"],[1:"Sunday"]]
//            if (['1', '2', '3'].contains(maintainRoomTemp))
//                input "type$ruleNo", "enum", title: "Rule type?", required: true, multiple: false, defaultValue: null, options: [[null:"Execution Rule"],[t:"Temperature Rule"]], submitOnChange: true
//            else
                input "type$ruleNo", "enum", title: "Rule type?", required: true, multiple: false, defaultValue: null, options: [[e:"Execution Rule"],[t:"Temperature Rule"]], submitOnChange: true
            if (ruleType != 't')      {
                if (luxSensor)
                    input "luxThreshold$ruleNo", "number", title: "What lux value?", required: false, multiple: false, defaultValue: null, range: "0..*"
                else
                    paragraph "What lux value?\nset lux sensor in main settings to select."
            }
        }

        if (ruleType != 't')      {
            if (humiditySensor)     {
                section("") {
                    href "pageHumidity", title: "Humidity range? (condition)", description: "${(settings["fromHumidity$ruleNo"] || settings["toHumidity$ruleNo"] ? settings["fromHumidity$ruleNo"] + ' - ' + settings["toHumidity$ruleNo"] : 'Add humidity range')}", params: [ruleNo: "$ruleNo"]
                }
            }
            else
                section("")     {
                    paragraph "What humidity range?\nset humidity sensor in main settings to select."
                }
        }

        if (ruleType != 't')      {
            section("") {
        	   href "pageRuleDate", title: "Date filter? (condition)", description: "${(settings["fromDate$ruleNo"] || settings["toDate$ruleNo"] ? settings["fromDate$ruleNo"] + ' - ' + settings["toDate$ruleNo"] : 'Add date filtering')}", params: [ruleNo: "$ruleNo"]
            }
        }

        section("") {
            href "pageRuleTime", title: "Time trigger?", description: "${(ruleFromTimeType || ruleToTimeType ? (ruleFromTimeType == timeTime() ? "$ruleFromTimeHHmm" : (ruleFromTimeType == timeSunrise() ? "Sunrise" : "Sunset") + (ruleFromTimeOffset ? " $ruleFromTimeOffset" : "")) + ' : ' + (ruleToTimeType == timeTime() ? "$ruleToTimeHHmm" : (ruleToTimeType == timeSunrise() ? "Sunrise" : "Sunset") + (ruleToTimeOffset ? " $ruleToTimeOffset" : "")) : 'Add time trigger')}", params: [ruleNo: "$ruleNo"]
        }

        if (ruleType != 't')      {
            section("Lights and switches to turn ON:", hideable: false)     {
                input "switchesOn$ruleNo", "capability.switch", title: "Turn ON which switches?", required: false, multiple: true
                input "setLevelTo$ruleNo", "enum", title: "Set level when Turning ON?", required: false, multiple: false, defaultValue: null, submitOnChange: true,
                        options: levelOptions
//                        options: [[AL:"Auto Level (and color temperature)"],[HL:"Holiday Lights"],
//                                  [1:"1%"],[5:"5%"],[10:"10%"],[20:"20%"],[30:"30%"],[40:"40%"],[50:"50%"],[60:"60%"],[70:"70%"],[80:"80%"],[90:"90%"],[100:"100%"]]
                input "setColorTo$ruleNo", "enum", title: "Set color when turning ON?", required: false, multiple:false, defaultValue: null,
                                                                                   options: [["Soft White":"Soft White - Default"],
                                					                                         ["White":"White - Concentrate"],
                                					                                         ["Daylight":"Daylight - Energize"],
                                					                                         ["Warm White":"Warm White - Relax"],
                                					                                         "Red","Green","Blue","Yellow","Orange","Purple","Pink"]
                if (settings["setLevelTo$ruleNo"] == 'AL' && autoColorTemperature)
                    paragraph "Set color temperature when turning ON? (if light supports color and color is specified this setting will be ignored.)\ncannot set when level is set to 'AL'."
                else
                    input "setColorTemperatureTo$ruleNo", "number", title: "Set color temperature when turning ON? (if light supports color and color is specified this setting will be ignored.)",
                                                                                            required: false, multiple: false, defaultValue: null, range: "1500..6500"
                input "switchesOff$ruleNo", "capability.switch", title: "Turn OFF which switches?", required: false, multiple: true
            }
            section("")     {
            	href "pageRuleOthers", title: "Routines/pistons and more",
                    description: "${(settings["actions$ruleNo"] || settings["piston$ruleNo"] || settings["musicAction$ruleNo"]  || settings["shadePosition$ruleNo"] ? "Tap to change existing settings" : "Tap to configure")}", params: [ruleNo: "$ruleNo"]
            }
            section("")     {
            	href "pageRuleTimer", title: "Timer overrides", description: "${(ruleTimerOverride ? (settings["noMotion$ruleNo"] ?: '') + ', ' + (settings["noMotionEngaged$ruleNo"] ?: '') + ', ' + (settings["dimTimer$ruleNo"] ?: '')  + ', ' + (settings["noMotionAsleep$ruleNo"] ?: '') : 'Add timer overrides')}", params: [ruleNo: "$ruleNo"]
            }
        }
        else        {
            section("Maintain room temperature?", hideable: false)		{
                if (['1', '3'].contains(maintainRoomTemp) && ((useThermostat && roomThermostat) || (!useThermostat && roomCoolSwitch)))
                    input "coolTemp$ruleNo", "decimal", title: "Cool to what temperature?", required: (!settings["fanOnTemp$ruleNo"] ? true : false), multiple: false, range: "${(isFarenheit ? '32..99' : '0..38')}", submitOnChange: true
                else
                    paragraph "Cool to what temperature?\nset thermostat or cool switch to set."
                if (['2', '3'].contains(maintainRoomTemp) && ((useThermostat && roomThermostat) || (!useThermostat && roomHeatSwitch)))
                    input "heatTemp$ruleNo", "decimal", title: "Heat to what temperature?", required: true, multiple: false, range: "${(isFarenheit ? '32..99' : '0..38')}"
                else
                    paragraph "Heat to what temperature?\nset thermostat or heat switch to set."
                if (['1', '2', '3'].contains(maintainRoomTemp) && ((useThermostat && roomThermostat) || (!useThermostat && (roomCoolSwitch || roomHeatSwitch))))
                    input "tempRange$ruleNo", "decimal", title: "Within temperature range?", required: true, multiple: false, range: "1..3"
                else
                    paragraph "Within temperature range?\nset thermostat or cool/heat switch to set."
            }
            section("Fan control?", hideable: false)		{
                if (roomFanSwitch)      {
                    input "fanOnTemp$ruleNo", "decimal", title: "Fan on at temperature?",
                                            required: false, multiple: false, defaultValue: null, range: "${(isFarenheit ? '32..99' : '0..38')}", submitOnChange: true
                    input "fanSpeedIncTemp$ruleNo", "decimal", title: "Fan speed with what temperature increments?",
                                            required: (settings["fanOnTemp$ruleNo"]), multiple: false, range: "1..5"
                }
                else        {
                    paragraph "Fan on at temperature?\nset fan switch to set."
                    paragraph "Fan speed with what temperature increments?\nset fan switch to set."
                }
            }
            section("Vent control?", hideable: false)		{
                if (useThermostat && roomVents)
                    paragraph "Rooms vents will be automatically controlled with thermostat and room temperature."
                else
                    paragraph "Enabled when using thermostat and room vents is set."
            }
        }
    }
}

private pageHumidity(params)   {
    if (params.ruleNo)
        state.pageRuleNo = params.ruleNo
    else if (state.passedParams)
        state.pageRuleNo = state.passedParams.ruleNo
    def ruleNo = state.pageRuleNo
    def fHum = settings["fromHumidity$ruleNo"]
    def tHum = settings["toHumidity$ruleNo"]
    dynamicPage(name: "pageHumidity", title: "Edit Rule Humidity", install: false, uninstall: false)   {
        section()     {
            input "fromHumidity$ruleNo", "number", title: "From humidity?", required: (tHum ? true : false),
                                multiple: false, defaultValue: null, range: "0..$tHum", submitOnChange: true
            input "toHumidity$ruleNo", "number", title: "To humidity?", required: (fHum ? true : false),
                                multiple: false, defaultValue: null, range: "$fHum..100", submitOnChange: true
        }
    }
}

private pageRuleDate(params)   {
    if (params.ruleNo)
        state.pageRuleNo = params.ruleNo
    else if (state.passedParams)
        state.pageRuleNo = state.passedParams.ruleNo
    def ruleNo = state.pageRuleNo
    def ruleFromDate = settings["fromDate$ruleNo"]
    def ruleToDate = settings["toDate$ruleNo"]
//    if ((ruleFromDate && ruleToDate) && (!dateInputValid(ruleFromDate, true) || !dateInputValid(ruleToDate, false)))
    if (ruleFromDate && ruleToDate)     {
//        def fTime = dateInputValid(ruleFromDate, true)
//        def tTime = dateInputValid(ruleToDate, false)
        def rD = dateInputValid(ruleFromDate, ruleToDate)
        def fTime = rD[0]
        def tTime = rD[1]
        def fTime2
        def tTime2
        if (fTime && tTime)     {
            fTime2 = new Date().parse("yyyy-MM-dd'T'HH:mm:ssZ", fTime)
            tTime2 = new Date().parse("yyyy-MM-dd'T'HH:mm:ssZ", tTime)
        }
        if ((fTime && !tTime) || (!fTime && tTime) || (fTime && tTime && tTime2 < fTime2))
            if (getHubType() == _SmartThings)       sendNotification("Invalid date range!", [method: "push"]);
    }
    dynamicPage(name: "pageRuleDate", title: "Edit Rule Date Filter", install: false, uninstall: false)   {
        section     {
            paragraph 'NO WAY TO VALIDATE DATE FORMAT ON INPUT. If invalid date checking for date will be skipped.'
            paragraph 'Date formats below support following special values for year to enable dynamic date ranges:\n"yyyy" = this year\n"YYYY" = next year'
            input "fromDate$ruleNo", "text", title: "From date? (yyyy/MM/dd format)", required: (ruleToDate ? true : false), multiple: false, defaultValue: null, submitOnChange: true
            input "toDate$ruleNo", "text", title: "To date? (yyyy/MM/dd format)", required: (ruleFromDate ? true : false), multiple: false, defaultValue: null, submitOnChange: true
        }
    }
}

private pageRuleTime(params)   {
    if (params.ruleNo)
        state.pageRuleNo = params.ruleNo
    else if (state.passedParams)
        state.pageRuleNo = state.passedParams.ruleNo
    def ruleNo = state.pageRuleNo
    def ruleFromTimeType = settings["fromTimeType$ruleNo"]
    def ruleToTimeType = settings["toTimeType$ruleNo"]
    dynamicPage(name: "pageRuleTime", title: "Edit Rule Time Trigger", install: false, uninstall: false)   {
        section()     {
            if (ruleToTimeType)
                input "fromTimeType$ruleNo", "enum", title: "Choose from time type?", required: true, multiple: false, defaultValue: null, submitOnChange: true, options: [[1:"Sunrise"],[2:"Sunset"],[3:"Time"]]
            else
                input "fromTimeType$ruleNo", "enum", title: "Choose from time type?", required: false, multiple: false, defaultValue: null, submitOnChange: true, options: [[1:"Sunrise"],[2:"Sunset"],[3:"Time"]]
            if (ruleFromTimeType == '3')
                input "fromTime$ruleNo", "time", title: "From time?", required: true, multiple: false, defaultValue: null
            else if (ruleFromTimeType)
                input "fromTimeOffset$ruleNo", "number", title: "Time offset?", required: false, multiple: false, defaultValue: 0, range: "-600..600"
            else
                paragraph "Choose from time type to select offset or time"
            if (ruleFromTimeType)
                input "toTimeType$ruleNo", "enum", title: "Choose to time type?", required: true, multiple: false, defaultValue: null, submitOnChange: true, options: [[1:"Sunrise"],[2:"Sunset"],[3:"Time"]]
            else
                input "toTimeType$ruleNo", "enum", title: "Choose to time type?", required: false, multiple: false, defaultValue: null, submitOnChange: true, options: [[1:"Sunrise"],[2:"Sunset"],[3:"Time"]]
            if (ruleToTimeType == '3')
                input "toTime$ruleNo", "time", title: "To time?", required: true, multiple: false, defaultValue: null
            else if (ruleToTimeType)
                input "toTimeOffset$ruleNo", "number", title: "Time offset?", required: false, multiple: false, defaultValue: 0, range: "-600..600"
            else
                paragraph "Choose to time type to select offset or time"
        }
    }
}

private pageRuleTimer(params)   {
    if (params.ruleNo)
        state.pageRuleNo = params.ruleNo
    else if (state.passedParams)
        state.pageRuleNo = state.passedParams.ruleNo
    def ruleNo = state.pageRuleNo
    dynamicPage(name: "pageRuleTimer", title: "Edit Rule Timer Overrides", install: false, uninstall: false)   {
        section()     {
            paragraph "These settings will temporarily replace the global settings when this rule is executed and reset back to the global settings when this rule no longer matches."
            if (hasOccupiedDevice())
                input "noMotion$ruleNo", "number", title: "Timeout after how many seconds when OCCUPIED?", required: false, multiple: false, defaultValue: null, range: "5..99999", submitOnChange: true
            else
                paragraph "Timeout after how many seconds when OCCUPIED?\nselect occupancy device in OCCUPIED settings to set"
            input "noMotionEngaged$ruleNo", "number", title: "Require motion within how many seconds when ENGAGED?", required: false, multiple: false, defaultValue: null, range: "5..99999"
            input "dimTimer$ruleNo", "number", title: "CHECKING state timer for how many seconds?", required: false, multiple: false, defaultValue: null, range: "5..99999", submitOnChange: true
            input "noMotionAsleep$ruleNo", "number", title: "Motion timeout for night switches when ASLEEP?", required: false, multiple: false, defaultValue: null, range: "5..99999"
        }
    }
}

private pageRuleOthers(params)   {
    if (params.ruleNo)
        state.pageRuleNo = params.ruleNo
    else if (state.passedParams)
        state.pageRuleNo = state.passedParams.ruleNo
    def ruleNo = state.pageRuleNo
    def allActions = location.helloHome?.getPhrases()*.label
    if (allActions)     allActions.sort();
    dynamicPage(name: "pageRuleOthers", title: "Edit Rule Execute", install: false, uninstall: false)   {
        section("Routines/pistons and more:", hideable: false)     {
            input "actions$ruleNo", "enum", title: "Routines to execute?", required: false, multiple: true, defaultValue: null, options: allActions
            input "piston$ruleNo", "enum", title: "Piston to execute?", required: false, multiple: false, defaultValue: null, options: state.pList
            if (musicDevice)
                input "musicAction$ruleNo", "enum", title: "Start or stop music player?", required: false, multiple: false, defaultValue: null,
                                                                                options: [[1:"Start music player"], [2:"Pause music player"], [3:"Neither"]]
            else
                paragraph "Start or stop music player?\nset music player in speaker settings to set."
            if (windowShades)
                input "shadePosition$ruleNo", "enum", title: "Shade position?", required: false, multiple: false, defaultValue: 99,
                        options: [[99:"Leave it alone"],[0:"Open shade"],[1:"Close shade"],[P1:"Preset position 1"],\
                                                          [P2:"Preset position 2"],[P3:"Preset position 3"]]
            else
                paragraph "Set window shade position?\nspick window shades in other devices to set."
        }
    }
}

/*
private dateInputValid(dateInput, isStartDate)      {
    if (!dateInput || dateInput.size() < 8 || dateInput.size() > 10)    return null;
    def dI = Date.parse("yyyy/M/d HH:mm:ss z", dateInput + (isStartDate ? ' 00:00:00 ' : ' 23:59:59 ') + location.timeZone.getDisplayName())
//    ifDebug("$dI")
    def dP = dI.format("yyyy-MM-dd'T'HH:mm:ssZ")
    if (!dP)    return null;
//    ifDebug("$dP")
    return dP
}
*/

private dateInputValid(dateInputStart, dateInputEnd)       {
//    ifDebug("dateInputValid")
    def returnDates = [null, null]
    if ((!dateInputStart || dateInputStart.size() < 8 || dateInputStart.size() > 10) ||
        (!dateInputEnd || dateInputEnd.size() < 8 || dateInputEnd.size() > 10))
        return returnDates
    if (dateInputStart.toLowerCase().substring(0, 5) == 'yyyy/' || dateInputEnd.toLowerCase().substring(0, 5) == 'yyyy/')     {
        def dateIS = yearTranslate(dateInputStart)
        def dIS = Date.parse("yyyy/M/d HH:mm:ss z", dateIS + ' 00:00:00 ' + location.timeZone.getDisplayName())
        def dateIE = yearTranslate(dateInputEnd)
        def dIE = Date.parse("yyyy/M/d HH:mm:ss z", dateIE + ' 23:59:59 ' + location.timeZone.getDisplayName())
        def cDate = new Date(now())
        if (cDate > dIE)
            use(TimeCategory)   {
                dIS = dIS + 1.year
                dIE = dIE + 1.year
            }
        def dPS = dIS.format("yyyy-MM-dd'T'HH:mm:ssZ")
        def dPE = dIE.format("yyyy-MM-dd'T'HH:mm:ssZ")
        if (!dPS || !dPE)    return returnDates;
        returnDates = [dPS, dPE]
    }
    else    {
        def dIS = Date.parse("yyyy/M/d HH:mm:ss z", dateInputStart + ' 00:00:00 ' + location.timeZone.getDisplayName())
//    ifDebug("$dI")
        def dPS = dIS.format("yyyy-MM-dd'T'HH:mm:ssZ")
        def dIE = Date.parse("yyyy/M/d HH:mm:ss z", dateInputEnd + ' 23:59:59 ' + location.timeZone.getDisplayName())
//    ifDebug("$dI")
        def dPE = dIE.format("yyyy-MM-dd'T'HH:mm:ssZ")
//    ifDebug("$dP")
        if (!dPS || !dPE)    return returnDates;
        returnDates = [dPS, dPE]
    }
    ifDebug("returnDates: $returnDates")
    return returnDates
}

private yearTranslate(dateP)        {
//    ifDebug("yearTranslate")
    def returnDate
    def cDate = new Date(now())
    def thisYear = cDate.getAt(Calendar.YEAR)
    def nextYear = thisYear + 1
    if (dateP.substring(0,5) == 'yyyy/')
        returnDate = thisYear + dateP.substring(4)
    else if (dateP.substring(0,5) == 'YYYY/')
        returnDate = nextYear + dateP.substring(4)
    else
        returnDate = dateP
    ifDebug("yearTranslate: returnDate: $returnDate")
    return returnDate
}

private pageAsleepSettings() {
    def buttonNames = genericButtons
    def asleepButtonOptions = [:]
    if (asleepButton)      {
        def buttonAttributes = asleepButton.supportedAttributes
        def attributeNameFound = false
        buttonAttributes.each  { att ->
            if (att.name == occupancy)      buttonNames = occupancyButtons;
            if (att.name == 'numberOfButtons')      attributeNameFound = true;
        }
        def numberOfButtons = asleepButton.currentNumberOfButtons
        if (attributeNameFound && numberOfButtons)      {
            for (def i = 0; i < numberOfButtons; i++)
                asleepButtonOptions << buttonNames[i]
        }
        else
            asleepButtonOptions << [null:"No buttons"]
    }
    //buttonNames = [[1:"One"],[2:"Two"],[3:"Three"],[4:"Four"],[5:"Five"],[6:"Six"],[7:"Seven"],[8:"Eight"],[9:"Nine"],[10:"Ten"],[11:"Eleven"],[12:"Twelve"]]
    def nightButtonOptions = [:]
    if (nightButton)      {
        def nightButtonAttributes = nightButton.supportedAttributes
        def attributeNameFound = false
        nightButtonAttributes.each  { att ->
            if (att.name == occupancy)      buttonNames = occupancyButtons;
            if (att.name == 'numberOfButtons')      attributeNameFound = true;
        }
        def numberOfButtons = nightButton.currentNumberOfButtons
        if (attributeNameFound && numberOfButtons)      {
            for (def i = 0; i < numberOfButtons; i++)
                nightButtonOptions << buttonNames[i]
        }
        else
            nightButtonOptions << [null:"No buttons"]
    }
    def hT = getHubType()
    def roomMotionSensors = motionSensors.collect{ [(it.id): "${it.displayName}"] }
	dynamicPage(name: "pageAsleepSettings", title: "Asleep Settings", install: false, uninstall: false) {
        section("ASLEEP state settings:", hideable: false)		{
	    	input "asleepSensor", "capability.sleepSensor", title: "Sleep sensor to set room to ASLEEP?", required: false, multiple: false
            input "asleepButton", "capability.${(hT == _SmartThings ? 'button' : 'pushableButton')}", title: "Button to toggle ASLEEP state?", required: false, multiple: false, submitOnChange: true
            if (asleepButton)       {
                input "buttonIsAsleep", "enum", title: "Button Number?", required: true, multiple: false, defaultValue: null, options: asleepButtonOptions
                input "buttonOnlySetsAsleep", "bool", title: "Button only sets Asleep?", description: "if false will toggle asleep and vacant", required: false, multiple: false, defaultValue: false
            }
            else        {
                paragraph "Button Number?\nselect button above to set"
                paragraph "Button only sets Asleep?\nselect button above to set"
            }
            input "asleepSwitch", "capability.switch", title: "Which switch turns ON?", required:false, multiple: true
            if (powerDevice)    {
                if (!powerValueEngaged && !powerValueLocked)      {
                    input "powerValueAsleep", "number", title: "Power value to set room to ASLEEP state?", required: false, multiple: false, defaultValue: null, range: "0..99999", submitOnChange: true
                    input "powerTriggerFromVacant", "bool", title: "Power value triggers ASLEEP from VACANT state?", required: false, multiple: false, defaultValue: true
                    input "powerStays", "number", title: "Power stays below for how many seconds to reset ASLEEP state?", required: (powerValueAsleep ? true : false), multiple: false, defaultValue: 30, range: "30..999"
                }
                else        {
                    paragraph "Power value to set room to ASLEEP state?\npower already used to set room to ENGAGED."
                    paragraph "Power value triggers ASLEEP from VACANT state?"
                    paragraph "Power stays below for how many seconds to reset ASLEEP state?"
                }
            }
            else        {
                paragraph "Power value to set room to ASLEEP?\nselect power device in other devices to set."
                paragraph "Power value triggers ASLEEP from VACANT state?"
                paragraph "Power stays below for how many seconds to reset ASLEEP state?"
            }
            input "noAsleep", "number", title: "Timeout ASLEEP state after how many hours?", required: false, multiple: false, defaultValue: null, range: "1..99"
            if (contactSensor)
                input "resetAsleepWithContact", "bool", title: "Reset ASLEEP state if contact sensor is open for more than 30 minutes?", required: false, multiple: false, defaultValue: false
            else
                paragraph "Reset ASLEEP state if contact sensor is open for more than 30 minutes?\nselect contact sensor in engaged setttings to set."
        }
        section("Night Lights:", hideable: false)		{
            if (motionSensors)
                input "nightSwitches", "capability.switch", title: "Turn ON which Switches in ASLEEP state with motion?", required: false, multiple: true, submitOnChange: true
            else
                paragraph "Turn ON which Switches in ASLEEP state with motion?\nselect motion sensor(s) above to set."
            if (nightSwitches)      {
                input "nightSetLevelTo", "enum", title: "Set Level When Turning ON?", required: false, multiple: false, defaultValue: null,
                                        options: [[1:"1%"],[5:"5%"],[10:"10%"],[20:"20%"],[30:"30%"],[40:"40%"],[50:"50%"],[60:"60%"],[70:"70%"],[80:"80%"],[90:"90%"],[99:"99%"],[100:"100%"]]
                input "nightSetCT", "number", title: "Set color temperature when turning ON?", required: false, multiple: false, defaultValue: null, range: "1500..7500"
                input "nightMotionSensors", "enum", title: "Use which room motion sensor(s)?", description: "All room motion sensor(s)", required: false, multiple: true, defaultValue: null, options: roomMotionSensors
                input "noMotionAsleep", "number", title: "Timeout seconds for night lights?", required: false, multiple: false, defaultValue: null, range: "5..99999"
                input "nightTurnOn", "enum", title: "Turn on night lights when?", required: true, multiple: true,
                                        options: [[1:"Motion in ASLEEP state"],[2:"State changes to ASLEEP"],[3:"State changes away from ASLEEP"]]
                input "nightButton", "capability.${(hT == _SmartThings ? 'button' : 'pushableButton')}", title: "Button to toggle night lights?", required: false, multiple: false, submitOnChange: true
                if (nightButton)        {
                    input "nightButtonIs", "enum", title: "Button Number?", required: true, multiple: false, defaultValue: null, options: nightButtonOptions
                    input "nightButtonAction", "enum", title: "Button Action?", required: true, multiple: false, defaultValue: null,
                                                        submitOnChange: true, options: [[1:"Turn on"],[2:"Turn off"],[3:"Toggle"]]
                }
                else        {
                    paragraph "Button Number?\nselect button above to set"
                    paragraph "Button Action?\nselect action for the button above to set"
                }

/*                input "noAsleepSwitchesOverride", "bool", title: "Selectively turn off sleep switches when leaving ASLEEP?\n(default: all)", required: false, multiple: false, defaultValue: false, submitOnChange: true
                if (noAsleepSwitchesOverride)       {
//                    def noAsleepSwitchesOptions = []
//                    noAsleepSwitchesOptions += nightSwitches.collect{ [(it.id): "${it.displayName}"] }
                    def noAsleepSwitchesOptions = nightSwitches.collect{ [(it.id): "${it.displayName}"] }
                    input "noAsleepSwitchesOff", "enum", title: "Switches to turn OFF when leaving ASLEEP?", required: false, multiple: true, defaultValue:null, submitOnChange: true,
                        options: noAsleepSwitchesOptions
                }
                else
                    paragraph "Switches to turn OFF when leaving ASLEEP?\nselect selectively turn off sleep switches to set."*/
            }
            else        {
                paragraph "Set level when turning ON?\nselect switches above to set"
                paragraph "Set color temperature when turning ON?\nselect switches above to set"
                paragraph "Use which room motion sensor(s)?\nselect switches above to set"
                paragraph "Timeout seconds for night lights?\nselect switches above to set"
                paragraph "Turn on night lights when?\nselect switches above to set"
                paragraph "Button to toggle night lights?\nselect switches rooms above to set"
                paragraph "Button Number?\nselect button above to set"
                paragraph "Button Action?\nselect action for the button above to set"
            }
        }
	}
}

private pageLockedSettings()      {
	dynamicPage(name: "pageLockedSettings", title: "Locked Settings", install: false, uninstall: false)     {
        section("Switch configuration for LOCKED state:", hideable:false)	{
            input "lockedSwitch", "capability.switch", title: "Which switch for LOCKED state?", required: false, multiple: false, submitOnChange: true
            if (lockedSwitch)
            	input "lockedSwitchCmd", "bool", title: "When switch turns ON? (otherwise when switch turns off)", required: true, defaultValue: true
            else
                paragraph "When switch turns ON?\nselect locked switch above to set"
            if (powerDevice)    {
                if (!powerValueEngaged && !powerValueAsleep)      {
                    input "powerValueLocked", "number", title: "Power value to set room to LOCKED state?", required: false, multiple: false, defaultValue: null, range: "0..99999", submitOnChange: true
                    input "powerTriggerFromVacant", "bool", title: "Power value triggers LOCKED from VACANT state?", required: false, multiple: false, defaultValue: true
                    input "powerStays", "number", title: "Power stays below for how many seconds to reset LOCKED state?", required: (powerValueLocked ? true : false), multiple: false, defaultValue: 30, range: "30..999"
                }
                else        {
                    paragraph "Power value to set room to LOCKED state?\npower already used to set room to ASLEEP."
                    paragraph "Power value triggers LOCKED from VACANT state?"
                    paragraph "Power stays below for how many seconds to reset LOCKED state?"
                }
            }
            else        {
                paragraph "Power value to set room to LOCKED?\nselect power device in other devices to set."
                paragraph "Power value triggers LOCKED from VACANT state?"
                paragraph "Power stays below for how many seconds to reset LOCKED state?"
            }
            input "lockedContact", "capability.contactSensor", title: "Which contact for LOCKED state?", required:false, multiple: false, submitOnChange: true
            if (lockedContact)
            	input "lockedContactCmd", "bool", title: "When contact closes? (otherwise when contact opens)", required: true, defaultValue: true
            else
                paragraph "When contact closes?\nselect locked contact above to set"
            input "lockedTurnOff", "bool", title: "Turn off switches when room changes to LOCKED?", required: false, multiple: false, defaultValue: false
            input "unLocked", "number", title: "Timeout LOCKED state after how many hours?", required: false, multiple: false, defaultValue: null, range: "1..99"
        }
	}
}

private pageRoomTemperature()       {
    def validThermostat = true
    def otherRoom
    if (useThermostat && roomThermostat)    {
        otherRoom = parent.checkThermostatValid(app.id, roomThermostat)
        ifDebug("otherRoom: $otherRoom")
        if (otherRoom)      validThermostat = false;
    }
    boolean isFarenheit = (location.temperatureScale == 'F' ? true : false)
	dynamicPage(name: "pageRoomTemperature", title: "Temperature Settings", install: false, uninstall: false)    {
        if (validThermostat)       {
            section("Maintain room temperature:", hideable: false)		{
                input "tempSensors", "capability.temperatureMeasurement", title: "Room temperature sensor?", required: (['1', '2', '3'].contains(maintainRoomTemp)), multiple: true, submitOnChange: true
                input "maintainRoomTemp", "enum", title: "Maintain room temperature?", required: false, multiple: false, defaultValue: 4,
                                                                                options: [[1:"Cool"], [2:"Heat"], [3:"Both"], [4:"Neither"]], submitOnChange: true
                if (['1', '2', '3'].contains(maintainRoomTemp))     {
                    input "useThermostat", "bool", title: "Use thermostat? (otherwise uses room ac and/or heater)", required: true, multiple: false, defaultValue: false, submitOnChange: true
                    if (useThermostat)      {
                        input "roomThermostat", "capability.thermostat", title: "Which thermostat?", required: true, multiple: false, submitOnChange: true
                        input "thermoToTempSensor", "number", title: "Delta (room - thermostat) temperature?",
                                        description: "if room sensor reads 2° lower than thermostat set this to -2 and so on.",
                                        required: true, multiple: false, defaultValue: 0, range: "${(isFarenheit ? '-15..15' : '-9..9')}"
                    }
                }
                if (!useThermostat && ['1', '3'].contains(maintainRoomTemp))      {
                    input "roomCoolSwitch", "capability.switch", title: "AC switch?", required: true, multiple: false, submitOnChange: true
    //                input "roomCoolTemp", "decimal", title: "What temperature?", required: true, multiple: false, range: "32..99"
    /*
                    input "nightModes", "mode", title: "Night mode?", required: false, multiple: true, submitOnChange: true
                    if (nightModes)
                        input "roomCoolTempNight", "decimal", title: "What temperature?", required: true, multiple: false, range: "32..99"
                    else
                        paragraph "What temperature from night mode?\nselect night modes above to set"
    */
                }
                if (!useThermostat && ['2', '3'].contains(maintainRoomTemp))      {
                    input "roomHeatSwitch", "capability.switch", title: "Heater switch?", required: true, multiple: false
    //                input "roomHeatTemp", "decimal", title: "What temperature?", required: true, multiple: false, range: "32..99"
                }
                if (['1', '2', '3'].contains(maintainRoomTemp))     {
                    if (personsPresence)
                        input "checkPresence", "bool", title: "Check presence before maintaining temperature?", required: true, multiple: false, defaultValue: false
                    else
                        paragraph "Check presence before maintaining temperature?\nselect presence sensor(s) to set"
                    input "contactSensorsRT", "capability.contactSensor", title: "Check contact sensor(s) closed?", required: false, multiple: true
                    input "thermoOverride", "number", title: "Allow thermostat or switch override for how many minutes?",
                                                    required: true, multiple: false, defaultValue: 0, range: "1..15"
                    input "outTempSensor", "capability.temperatureMeasurement", title: "Outdoor temperature sensor?", required: false, multiple: false
                    paragraph "Remember to setup temperature rule(s) for room cooling and/or heating."
                }
            }
            section("Room fan:", hideable: false)		{
                input "roomFanSwitch", "capability.switch", title: "Fan switch?", required: false, multiple: false
            }
            section("Room vents:", hideable: false)		{
                if (useThermostat)
                    input "roomVents", "capability.switch", title: "Vents?", required: false, multiple: true
                else
                    paragraph "Vents?\nenabled when using thermostat."
            }
        }
        else        {
            section("Warn About Thermostat", hideable: false)		{
                paragraph "This thermostat has already been selected in room: $otherRoom. Please select another thermostat or clear the theromstat setting.\n\nSAVING THESE SETTINGS WITHOUT CHANGING OR CLEARING THE THERMOSTAT SELECTION WILL HAVE UNPREDICTABLE RESULTS."
                input "roomThermostat", "capability.thermostat", title: "Which thermostat?", required: true, multiple: false, submitOnChange: true
            }
        }
    }
}

private pageAdjacentRooms()     {
	def roomNames = parent.getRoomNames(app.id)
	dynamicPage(name: "pageAdjacentRooms", title: "Adjacent Rooms Settings", install: false, uninstall: false)    {
		section("Action when there is motion in ADJACENT rooms:", hideable: false)		{
            input "adjRooms", "enum", title: "Adjacent Rooms?", required: false, multiple: true, defaultValue: null, options: roomNames, submitOnChange: true
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

private pageGeneralSettings()       {
    boolean isFarenheit = (location.temperatureScale == 'F' ? true : false)
	dynamicPage(name: "pageGeneralSettings", title: "General Settings", install: false, uninstall: false) {
		section("Mode settings for AWAY and PAUSE modes?", hideable: false)		{
            input "awayModes", "mode", title: "Away modes to set Room to VACANT?", required: false, multiple: true
            input "pauseModes", "mode", title: "Modes in which to pause automation?", required: false, multiple: true
        }
        section("Run on which days of the week?\n(when blank runs on all days.)", hideable: false)		{
            input "dayOfWeek", "enum", title: "Which days of the week?", required: false, multiple: false, defaultValue: null, options: [[null:"All Days of Week"],[8:"Monday to Friday"],[9:"Saturday & Sunday"],[2:"Monday"],\
                                                     [3:"Tuesday"],[4:"Wednesday"],[5:"Thursday"],[6:"Friday"],[7:"Saturday"],[1:"Sunday"]]
		}
        section("Temperature Scale", hideable: false)		{
            input "useCelsius", "bool", title: "Use Celsius?", required: false, multiple: false, defaultValue: "${(!isFarenheit)}"
        }
        section("Icon URL to use for this room?\nfor best results please use image of type 'png' and size 1024x1024 pixels. image url needs to be publicly accessible for ST to access.", hideable: false)		{
            input "iconURL", "text", title: "Icon URL?", required: false, multiple: false, defaultValue: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomOccupancySettings.png"
        }
        section("Turn off all switches on no rule match?", hideable: false)		{
            input "allSwitchesOff", "bool", title: "Turn OFF?", required: false, multiple: false, defaultValue: true
        }
        section("Process execution rule(s) only on state change?", hideable: false)		{
            input "onlyOnStateChange", "bool", title: "Only on state change?", required: false, multiple: false, defaultValue: false
        }
        section("Annoucement volume?", hideable: false)		{
            if (musicDevice)
                input "announceVolume", "number", title: "Annoucement volume?", required: false, multiple: false, defaultValue: 50, range: "1..100"
            else
                paragraph "Annoucement volume?\nselect music device to set."
            if (contactSensor && musicDevice)
                input "announceContact", "enum", title: "Announce when conact stays open?", required: false, multiple: false, defaultValue: null,
                                            options: [[1:"Every 1 minute"],[2:"Every 2 minutes"],[3:"Every 3 minutes"],[5:"Every 5 minutes"],\
                                                                [10:"Every 10 minutes"],[15:"Every 15 minutes"],[30:"Every 30 minutes"]]
            else
                paragraph "Announce when conact stays open?\ncan only be enabled when contact sensor and music device is selected."
            if (contactSensor && contactSensorOutsideDoor && musicDevice)
                input "announceDoor", "bool", title: "Announce when door opened or closed?", required: false, multiple: false, defaultValue: false
            else
                paragraph "Announce when door open or closed?\ncan only be enabled when contact sensor is on outside door and music device is selected."
        }
        section("When room device switch capability turned on programatically (rooms_device.on()) set room to?\n(note: when room device switch is tuned off room state is set to VACANT.)", hideable: false)	{
            input "roomDeviceSwitchOn", "enum", title: "Which state?", required: false, multiple: false, defaultValue: ['occupied'],
                                        options: ['occupied', 'engaged', 'locked', 'asleep']
        }
	}
}

private pageAllSettings() {
    ifDebug("pageAllSettings")
    def dOW = [[null:"All Days of Week"],[8:"Monday to Friday"],[9:"Saturday & Sunday"],[2:"Monday"],[3:"Tuesday"],[4:"Wednesday"],[5:"Thursday"],[6:"Friday"],[7:"Saturday"],[1:"Sunday"]]
    boolean isFarenheit = (location.temperatureScale == 'F' ? true : false)
	dynamicPage(name: "pageAllSettings", title: "View All Settings", install: false, uninstall: false)    {
		section("", hideable: false)		{
            paragraph "Motion sensor:\t${(motionSensors ? true : '')}\nOccupancy timeout:\t${(hasOccupiedDevice() ? (noMotion ?: '') : '')}\nMotion event:\t\t${(motionSensors ? (whichNoMotion == 1 ? 'Last Motion Active' : 'Last Motion Inactive') : '')}\nOccupied switches:\t${ (occSwitches ? true : '')}"
            paragraph "Room busy check:\t${(!busyCheck ? 'No traffic check' : (busyCheck == lightTraffic ? 'Light traffic' : (busyCheck == mediumTraffic ? 'Medium traffic' : 'Heavy traffic')))}\n\nEngaged button:\t\t${(engagedButton ? true : '')}\nButton number:\t\t${(engagedButton && buttonIs ? buttonIs : '')}\nPerson presence:\t\t${(personsPresence ? personsPresence.size() : '')}\nPresence action:\t\t${(personsPresence ? (presenceAction == '1' ? 'Engaged on arrival' : (presenceAction == '2' ? 'Vacant on Departure' : (presenceAction == 3 ? 'Both' : 'Neither'))) : '')}\nPresence continuous:\t\t${(presenceActionContinuous ?: '')}\nPower meter:\t\t\t${(powerDevice ?: '')}\nPower value:\t\t\t${(powerDevice ? powerValueEngaged : '')}\nEngaged on music:\t\t${(musicDevice && musicEngaged ? true : '')}\nEngaged switches:\t\t${(engagedSwitch ? engagedSwitch.size() : '')}\nContact sensor:\t\t${(contactSensor ? contactSensor.size() : '')}\nOutside door:\t\t${(contactSensor && contactSensorOutsideDoor? true : '')}\nEngaged timeout:\t${(noMotionEngaged ?: '')}\nDirect reset:\t\t\t${(resetEngagedDirectly ? true : false)}"
            paragraph "Checking timer:\t\t${(dimTimer ?: '')}\nDim level:\t\t${(dimByLevel ?: '')}"
//            paragraph "Lux sensor:\t\t\t\t${(luxSensor ? true : '')}\nLux threshold:\t\t\t${(luxThreshold ?: '')}\nTurn off last switches:\t$allSwitchesOff"
            paragraph "Music player:\t\t${(musicDevice ? true : '')}"
            paragraph "Temperature sensors:\t\t${(tempSensors ? true : '')}\nMaintain room temperature:\t\t${(!maintainRoomTemp || maintainRoomTemp == '4' ? 'No' : (maintainRoomTemp == '1' ? 'Cool' : (maintainRoomTemp == '2' ? 'Heat' : 'Both')))}\nOutdoor temperature sensor:\t${(['1', '2', '3'].contains(maintainRoomTemp) && outTempSensor ? true : '')}"
            paragraph "Lux sensor:\t\t${(luxSensor ? true : '')}"
            paragraph "Power meter:\t\t${(powerDevice ? true : '')}"
            paragraph "Min level:\t\t\t${(minLevel ?: '')}\nMax level:\t\t\t${(maxLevel ?: '')}\nSet kelvin also?\t${(autoColorTemperature ? true : '')}\nWakeup time:\t\t${(autoColorTemperature ? format24hrTime(timeToday(wakeupTime, location.timeZone)) : '')}\nSleep time:\t\t${(autoColorTemperature ? format24hrTime(timeToday(sleepTime, location.timeZone)) : '')}\nMin kelvin:\t\t\t${(autoColorTemperature ? minKelvin : '')}\nMax kelvin:\t\t${(autoColorTemperature ? maxKelvin : '')}"
            paragraph "Asleep sensor:\t${(asleepSensor ? true : '')}\nAsleep button:\t\t${(alseepButton ? true : '')}\nButton number:\t${(!asleepButton ? '' : (buttonIsAsleep ?: ''))}\nAsleep timeout:\t${(noAsleep ? noAsleep + ' hours' : '')}\n\nAsleep switches:\t${(nightSwitches ? true : '')}\nNight level:\t\t${(nightSetLevelTo ?: '')}\nNight button:\t\t${(nightButton ? true : '')}\nButton number:\t${(!nightButton ? '' : (nightButtonIs ?: ''))}"
            paragraph "Away modes:\t\t\t${(awayModes ? awayModes.size() : '')}\nPause modes:\t\t\t${(pauseModes ? pauseModes.size() : '')}\nTurn off all switches:\t\t${(allSwitchesOff ? true : '')}\nDay of week:\t\t\t\t${(dayOfWeek ? dOW[dayOfWeek] : 'All days')}"
            def i = 1
            for (; i < 11; i++)     {
                def ruleNo = String.valueOf(i)
                def thisRule = getRule(ruleNo, '*', false)
                if (!thisRule)      continue;
                def ruleDesc = "$ruleNo:"
                ruleDesc = (thisRule.disabled ? "$ruleDesc Disabled=$thisRule.disabled" : "$ruleDesc")
                ruleDesc = (thisRule.mode ? "$ruleDesc Mode=$thisRule.mode" : "$ruleDesc")
                ruleDesc = (thisRule.state ? "$ruleDesc State=$thisRule.state" : "$ruleDesc")
                ruleDesc = (thisRule.dayOfWeek ? "$ruleDesc Days of Week=$thisRule.dayOfWeek" : "$ruleDesc")
                ruleDesc = (thisRule.luxThreshold != null ? "$ruleDesc Lux=$thisRule.luxThreshold" : "$ruleDesc")
                if (thisRule.fromDate && thisRule.toDate)        {
//                    def ruleFromDate = dateInputValid(settings["fromDate$ruleNo"], true)
//                    def ruleToDate = dateInputValid(settings["toDate$ruleNo"], false)
                    def rD = dateInputValid(settings["fromDate$ruleNo"], settings["toDate$ruleNo"])
                    def ruleFromDate = rD[0]
                    def ruleToDate = rD[1]
                    if (ruleFromDate && ruleToDate)     {
                        ruleDesc = (thisRule.fromDate ? "$ruleDesc From=${settings["fromDate$ruleNo"]}" : "$ruleDesc")
                        ruleDesc = (thisRule.toDate ? "$ruleDesc To=${settings["toDate$ruleNo"]}" : "$ruleDesc")
                    }
                }
                if (thisRule.fromTimeType && thisRule.toTimeType)        {
                    def ruleFromTimeHHmm = (thisRule.fromTime ? format24hrTime(timeToday(thisRule.fromTime, location.timeZone)) : '')
                    def ruleToTimeHHmm = (thisRule.toTime ? format24hrTime(timeToday(thisRule.toTime, location.timeZone)) : '')
                    ruleDesc = (thisRule.fromTimeType == timeTime() ? "$ruleDesc From=$ruleFromTimeHHmm" : (thisRule.fromTimeType == timeSunrise() ? "$ruleDesc From=Sunrise" : "$ruleDesc From=Sunset"))
                    ruleDesc = (thisRule.toTimeType == timeTime() ? "$ruleDesc To=$ruleToTimeHHmm" : (thisRule.toTimeType == timeSunrise() ? "$ruleDesc To=Sunrise" : "$ruleDesc To=Sunset"))
                }
                ruleDesc = (thisRule.piston ? "$ruleDesc Piston=true" : "$ruleDesc")
                ruleDesc = (thisRule.actions ? "$ruleDesc Routines=true" : "$ruleDesc")
                ruleDesc = (thisRule.musicAction ? "$ruleDesc Music=${(thisRule.musicAction == '1' ? 'Start' : (thisRule.musicAction == '2' ? 'Pause' : 'Neither'))}" : "$ruleDesc")
                if (thisRule.switchesOn)    {
                    ruleDesc = (thisRule.switchesOn ? "$ruleDesc ON=${thisRule.switchesOn.size()}" : "$ruleDesc")
                    ruleDesc = (thisRule.level ? "$ruleDesc Level=$thisRule.level" : "$ruleDesc")
                    ruleDesc = (thisRule.color ? "$ruleDesc Color=$thisRule.color" : "$ruleDesc")
                    ruleDesc = (thisRule.colorTemperature ? "$ruleDesc Kelvin=$thisRule.colorTemperature" : "$ruleDesc")
                }
                ruleDesc = (thisRule.switchesOff ? "$ruleDesc OFF=${thisRule.switchesOff.size()}" : "$ruleDesc")
                ruleDesc = (thisRule.noMotion ? "$ruleDesc Occupied Timer=${thisRule.noMotion}" : "$ruleDesc")
                ruleDesc = (thisRule.noMotionEngaged ? "$ruleDesc Engaged Timer=${thisRule.noMotionEngaged}" : "$ruleDesc")
                ruleDesc = (thisRule.dimTimer ? "$ruleDesc Checking Timer=${thisRule.dimTimer}" : "$ruleDesc")
                ruleDesc = (thisRule.noMotionAsleep ? "$ruleDesc Asleep Timer=${thisRule.noMotionAsleep}" : "$ruleDesc")
                paragraph "$ruleDesc"
            }
        }
    }
}

def installed()		{}

def updated()	{
    ifDebug("updated")
    if (!childCreated())    spawnChildDevice(app.label);
    if (!parent || !parent.handleAdjRooms())     {
        ifDebug("no adjacent rooms")
        def adjMotionSensors = []
        updateRoom(adjMotionSensors)
    }
    def adjRoomNames = []
    adjRooms.each  {  adjRoomNames << parent.getARoomName(it)  }
    def busyCheckDisplay = (busyCheck == lightTraffic ? ['Light traffic'] : (busyCheck == mediumTraffic ? ['Medium traffic'] : (busyCheck == heavyTraffic ? ['Heavy traffic'] : [])))
    def devicesMap = ['busyCheck':busyCheckDisplay, 'engagedButton':engagedButton, 'presence':personsPresence, 'engagedSwitch':engagedSwitch, 'contactSensor':contactSensor,
                      'motionSensors':motionSensors, 'luxSensor':luxSensor, 'adjRoomNames':adjRoomNames,
                      'sleepSensor':asleepSensor, 'nightButton':nightButton, 'nightSwitches':nightSwitches, 'awayModes':awayModes, 'pauseModes':pauseModes]
    def child = getChildDevice(getRoom())
    child.deviceList(devicesMap)
    def childLabel = child.getLabel()
    ifDebug("room label: $app.label | device label: $childLabel")
    if (childLabel != app.label)        child.setLabel(app.label);
    child.vacant()
}

def updateRoom(adjMotionSensors)     {
    ifDebug("updateRoom")
	initialize()
    def hT = getHubType()
    boolean isFarenheit = (location.temperatureScale == 'F' ? true : false)
//    def child = getChildDevice(getRoom())
	subscribe(location, "mode", modeEventHandler)
    state.noMotion = ((noMotion && noMotion >= 5) ? noMotion : null)
    state.noMotionEngaged = ((noMotionEngaged && noMotionEngaged >= 5) ? noMotionEngaged : null)
	if (motionSensors)	{
    	subscribe(motionSensors, "motion.active", motionActiveEventHandler)
    	subscribe(motionSensors, "motion.inactive", motionInactiveEventHandler)
	}
    if (adjMotionSensors && (adjRoomsMotion || adjRoomsPathway))   {
        subscribe(adjMotionSensors, "motion.active", adjMotionActiveEventHandler)
        subscribe(adjMotionSensors, "motion.inactive", adjMotionInactiveEventHandler)
    }
    if (occupiedButton)
        if (hT == _SmartThings)
            subscribe(occupiedButton, "button", buttonPushedOccupiedEventHandler)
        else
            subscribe(occupiedButton, "pushed.$buttonIsOccupied", buttonPushedOccupiedEventHandler)
    if (occSwitches) {
    	subscribe(occSwitches, "switch.on", occupiedSwitchOnEventHandler)
    	subscribe(occSwitches, "switch.off", occupiedSwitchOffEventHandler)
    }
//    def ind = -1
/*    if (adjMotionSensors)      {
        devValue = adjMotionSensors.currentMotion
        if (devValue.contains('active'))    ind = 1;
        else                                ind = 0;
    }*/
//    child.updateAdjMotionInd(ind)
    ifDebug("updateRoom 2")
    state.switchesHasLevel = [:]
    state.switchesHasColor = [:]
    state.switchesHasColorTemperature = [:]
    state.dimTimer = ((dimTimer && dimTimer >= 5) ? dimTimer : 5) // forces minimum of 5 seconds to allow for checking state
    state.dimByLevel = ((state.dimTimer && dimByLevel) ? dimByLevel as Integer : null)
    state.dimToLevel = ((state.dimTimer && dimToLevel) ? dimToLevel as Integer : null)
    if (engagedSwitch)      {
    	subscribe(engagedSwitch, "switch.on", engagedSwitchOnEventHandler)
    	subscribe(engagedSwitch, "switch.off", engagedSwitchOffEventHandler)
	}
    if (contactSensor)      {
    	subscribe(contactSensor, (contactSensorOutsideDoor ? "contact.closed" : "contact.open"), contactOpenEventHandler)
    	subscribe(contactSensor, (contactSensorOutsideDoor ? "contact.open" : "contact.closed"), contactClosedEventHandler)
	}
    if (musicDevice && musicEngaged)       {
        subscribe(musicDevice, "status.playing", musicPlayingEventHandler)
        subscribe(musicDevice, "status.paused", musicStoppedEventHandler)
        subscribe(musicDevice, "status.stopped", musicStoppedEventHandler)
    }
    state.busyCheck = (busyCheck ? busyCheck as Integer : null)
//    if (engagedButton)  subscribe(engagedButton, "button.pushed", buttonPushedEventHandler)
    if (engagedButton)
        if (hT == _SmartThings)
            subscribe(engagedButton, "button", buttonPushedEventHandler)
        else
            subscribe(engagedButton, "pushed.$buttonIs", buttonPushedEventHandler)
    if (personsPresence)     {
    	subscribe(personsPresence, "presence.present", presencePresentEventHandler)
        subscribe(personsPresence, "presence.not present", presenceNotPresentEventHandler)
    }
    state.holidays = [:]
    for (def i = 1; i < 11; i++)        {
        if (!settings["holiName$i"] || !settings["holiColorString$i"])     continue;
        def holiColors = []
        def holiHues = [:]
        def holiColorCount = 0
        def str = settings["holiColorString$i"].split(',')
        str.each    {
            holiColors << it
            def hue = convertRGBToHueSaturation(it)
            holiHues << [(holiColorCount):hue]
            holiColorCount = holiColorCount + 1
        }
        state.holidays << [(i):[name: settings["holiName$i"], count: holiColorCount, hues: holiHues,
                                colors: holiColors, style: settings["holiStyle$i"], seconds: settings["holiSeconds$i"], level: settings["holiLevel$i"]]]
    }
    if (anotherRoomEngaged)     {
//        parent.subscribeChildrenToEngaged(app.id, anotherRoomEngaged)
//        subsribe(anotherRoomEngaged, "occupancy", anotherRoomEventHandler)
        anotherRoomEngaged.each     {
            def roomDeviceObject = parent.getChildRoomDeviceObject(it)
            if (roomDeviceObject)
                if (hT == _SmartThings)
                    subscribe(roomDeviceObject, "button.pushed", anotherRoomEngagedButtonPushedEventHandler);
                else    {
                    subscribe(roomDeviceObject, "pushed.8", anotherRoomEngagedButtonPushedEventHandler);        // asleep
                    subscribe(roomDeviceObject, "pushed.9", anotherRoomEngagedButtonPushedEventHandler);        // engaged
                }
        }
    }
//    if (vacantButton)   subscribe(vacantButton, "button.pushed", buttonPushedVacantEventHandler);
    if (vacantButton)
        if (hT == _SmartThings)
            subscribe(vacantButton, "button", buttonPushedVacantEventHandler)
        else
            subscribe(vacantButton, "pushed.$buttonIsVacant", buttonPushedVacantEventHandler)
    if (vacantSwitches)   subscribe(vacantSwitches, "switch.off", vacantSwitchOffEventHandler);
    ifDebug("updateRoom 3")
    if (luxSensor)      {
        subscribe(luxSensor, "illuminance", luxEventHandler)
        state.previousLux = getIntfromStr((String) luxSensor.currentIlluminance)
    }
    else
        state.previousLux = null
    if (humiditySensor)     subscribe(humiditySensor, "relativeHumidityMeasurement", humidityEventHandler);
    if (powerDevice)    {
        subscribe(powerDevice, "power", powerEventHandler)
        state.previousPower = getIntfromStr((String) powerDevice.currentPower)
    }
    else
        state.previousPower = null
    if (speechDevice)   subscribe(speechDevice, "phraseSpoken", speechEventHandler);
    if (asleepSensor)   subscribe(asleepSensor, "sleeping", sleepEventHandler);
//    if (asleepButton)   subscribe(asleepButton, "button.pushed", buttonPushedAsleepEventHandler);
    if (asleepButton)
        if (hT == _SmartThings)
            subscribe(asleepButton, "button", buttonPushedAsleepEventHandler)
        else
            subscribe(asleepButton, "pushed.$buttonIsAsleep", buttonPushedAsleepEventHandler)
    if (asleepSwitch)      {
    	subscribe(asleepSwitch, "switch.on", asleepSwitchOnEventHandler)
    	subscribe(asleepSwitch, "switch.off", asleepSwitchOffEventHandler)
	}
//    if (nightButton)    subscribe(nightButton, "button.pushed", nightButtonPushedEventHandler);
    if (nightButton)
        if (hT == _SmartThings)
            subscribe(nightButton, "button", nightButtonPushedEventHandler)
        else
            subscribe(nightButton, "pushed.$nightButtonIs", nightButtonPushedEventHandler)
    state.noMotionAsleep = ((noMotionAsleep && noMotionAsleep >= 5) ? noMotionAsleep : null)
    nightSwitches.each      {
        if (it.hasCommand("setLevel"))    state.switchesHasLevel << [(it.getId()):true];
        if (it.hasCommand("setColorTemperature"))       state.switchesHasColorTemperature << [(it.getId()):true];
    }
    state.nightSetLevelTo = (nightSetLevelTo ? nightSetLevelTo as Integer : null)
    state.nightSetCT = (nightSetCT ? nightSetCT as Integer : null)
    state.noAsleep = ((noAsleep && noAsleep >= 1) ? (noAsleep * 60 * 60) : null)
    ifDebug("updateRoom 4")

    if (lockedContact) {
/*        if (lockedContactCmd == "open")      {
        	subscribe(lockedContact, "contact.open", lockedContactOpenEventHandler)
    		subscribe(lockedContact, "contact.closed", lockedContactClosedEventHandler)
        }
        if (lockedContactCmd == "closed")      {
        	subscribe(lockedContact, "contact.open", lockedContactClosedEventHandler)
    		subscribe(lockedContact, "contact.closed", lockedContactOpenEventHandler)
    	}
*/
        subscribe(lockedContact, "contact.${(lockedContactCmd ? 'open' : 'closed')}", lockedContactOpenEventHandler)
        subscribe(lockedContact, "contact.${(lockedContactCmd ? 'closed' : 'open')}", lockedContactClosedEventHandler)
    }

    if (lockedSwitch) {
/*        if (lockedSwitchCmd)        {
            subscribe(lockedSwitch, "switch.on", lockedSwitchOnEventHandler)
            subscribe(lockedSwitch, "switch.off", lockedSwitchOffEventHandler)
        }
        else        {
            subscribe(lockedSwitch, "switch.off", lockedSwitchOnEventHandler)
            subscribe(lockedSwitch, "switch.on", lockedSwitchOffEventHandler)
		}
*/
        subscribe(lockedSwitch, "switch.${(lockedSwitchCmd ? 'on' : 'off')}", lockedSwitchOnEventHandler)
        subscribe(lockedSwitch, "switch.${(lockedSwitchCmd ? 'off' : 'on')}", lockedSwitchOffEventHandler)
    }

    if (windowShades)   subscribe(windowShades, "windowShade", windowShadeEventHandler);
    if (thermoOverride)     {
        if (roomThermostat)     subscribe(roomThermostat, "thermostat", roomThermostatEventHandler);
        if (roomCoolSwitch)     {
            subscribe(roomCoolSwitch, "switch.on", roomCoolSwitchOnEventHandler)
            subscribe(roomCoolSwitch, "switch.off", roomCoolSwitchOffEventHandler)
        }
        if (roomHeatSwitch)     {
            subscribe(roomHeatSwitch, "switch.on", roomHeatSwitchOnEventHandler)
            subscribe(roomHeatSwitch, "switch.off", roomHeatSwitchOffEventHandler)
        }
    }
    state.roomThermoTurnedOn = false
    state.roomCoolTurnedOn = false
    state.roomHeatTurnedOn = false
    state.thermoOverride = false
    if (roomFanSwitch)      {
        subscribe(roomFanSwitch, "switch", updateFanIndP)
        subscribe(roomFanSwitch, "level", updateFanIndP)
    }
    state.unLocked = ((unLocked && unLocked >= 1) ? (unLocked * 60 * 60) : null)
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
    if (contactSensorsRT)      {
    	subscribe(contactSensorsRT, "contact.open", contactsRTOpenEventHandler)
        subscribe(contactSensorsRT, "contact.closed", contactsRTClosedEventHandler)
    }
    if (tempSensors)        subscribe(tempSensors, "temperature", temperatureEventHandler);
    updateRulesToState()
    updateSwitchAttributesToStateAndSubscribe()
//    switchesOnOrOff()
    subscribe(location, "sunrise", scheduleFromToTimes)
    subscribe(location, "sunset", scheduleFromToTimes)
    ifDebug("updateRoom runIns")
    runIn(0, processCoolHeat)
    runIn(1, scheduleFromToTimes)
    runIn(3, updateIndicators)
}

def	initialize()	{ unsubscribe(); unschedule(); state.remove("pList") }

def updateIndicators()      {
    ifDebug("updateIndicators")
    def child = getChildDevice(getRoom())
    def ind
    boolean isFarenheit = (location.temperatureScale == 'F' ? true : false)
    child.updateMotionInd((motionSensors ? (motionSensors.currentMotion.contains('active') ? 1 : 0) : -1))
    child.updateLuxInd((luxSensor ? getIntfromStr((String) luxSensor.currentIlluminance) : -1))
    child.updateContactInd((contactSensor ? (contactSensor.currentContact.contains('closed') ? 1 : 0) : -1))
    child.updateSwitchInd(isAnySwitchOn())
    child.updatePresenceInd((personsPresence ? (personsPresence.currentPresence.contains('present') ? 1 : 0) : -1))
    child.updatePresenceActionInd((personsPresence ? presenceAction : -1))
    child.updateDoWInd((dayOfWeek ?: -1))
    child.updateTemperatureInd((tempSensors ? getAvgTemperature() : -1))
//    temp = -1
//    child.updateMaintainInd(temp)
    child.updateRulesInd((state.rules ? state.rules.size() : -1))
    child.updateLastRuleInd(-1)
    child.updatePowerInd((powerDevice ? powerDevice.currentPower : -1))
    if (pauseModes)     {
        ind = ''
        pauseModes.each     {  ind = ind + (ind.size() > 0 ? ', ' : '') + it  }
    }
    else
        ind = -1
    child.updatePauseInd(ind)
    child.updateESwitchInd(isAnyESwitchOn())
    child.updateOSwitchInd(isAnyOccupiedSwitchOn())
    child.updateASwitchInd(isAnyASwitchOn())
    child.updateNSwitchInd(isAnyNSwitchOn())
    child.updatePresenceEngagedInd((personsPresence ? (presenceActionContinuous ? 'Yes' : 'No') : -1))
    child.updateBusyEngagedInd((busyCheck ? (busyCheck == lightTraffic ? 'Light' : (busyCheck == mediumTraffic ? 'Medium' : 'Heavy')) : -1))
    child.updateLSwitchInd(isAnyLSwitchOn())
    updateTimers()
    child.updateTurnAllOffInd(allSwitchesOff ? 'Yes' : 'No')
    child.updateDimByLevelInd((state.dimByLevel ?: -1), (state.dimToLevel ?: -1))
    child.updateEWattsInd(powerValueEngaged ?: -1)
    child.updateAWattsInd(powerValueAsleep ?: -1)
//        def roomNames = parent.getRoomNames(app.id)
//        ind = ''
//        adjRooms.each     {  ind = ind + (ind.size() > 0 ? ', ' : '') + roomNames[it]  }
//    }
    child.updateContactRTInd((contactSensorsRT ? (contactSensorsRT.currentContact.contains('closed') ? 1 : 0) : -1))
    child.updateAdjRoomsInd((adjRooms ? adjRooms.size() : -1))
/*    if (adjMotionSensors)      {
        devValue = adjMotionSensors.currentMotion
        if (devValue.contains('active'))    ind = 1;
        else                                ind = 0;
    }*/
    child.updateAdjMotionInd(-1)
    updateThermostatIndP(isHere)
    updateFanIndP()
    child.setupAlarmC()
//    child.updateThermoOverrideIndC(thermoOverride)
}

private getAvgTemperature()     {
//    ifDebug("getAvgTemperature")
    boolean isFarenheit = (location.temperatureScale == 'F' ? true : false)
    int countTempSensors = (tempSensors ? tempSensors.size() : 0)
    if (countTempSensors < 1)       return -1;
    def temperatures = tempSensors.currentTemperature
//    ifDebug("countTempSensors: $countTempSensors | temperatures: $temperatures")
    def temperature = 0.0
    temperatures.each   {  temperature = temperature + it  }
//    ifDebug("to return: ${(temperature / countTempSensors)}")
    return (temperature / countTempSensors)
}

private isAnySwitchOn()   {
    ifDebug("isAnySwitchOn")
    def ind = -1
    for (def i = 1; i < 11; i++)      {
        def ruleNo = String.valueOf(i)
        def thisRule = getNextRule(ruleNo, null)
        if (thisRule.ruleNo == 'EOR')     break;
        i = thisRule.ruleNo as Integer
        if (thisRule.switchesOn)
            if (thisRule.switchesOn.currentSwitch.contains('on'))    {
                ind = 1
                break
            }
            else
                ind = 0
    }
    return ind
}

private isAnyOccupiedSwitchOn() {
    ifDebug("isAnyOccupiedSwitchOn")
//    def v = false
//    if (occSwitches)    v = occSwitches.currentSwitch.contains('on');
//    return v
    return (occSwitches ? (occSwitches.currentSwitch.contains('on') ? 1 : 0) : -1)
}

/*
// Returns true if there is a contactSensor and the current state of contactSensor matches engaged state
private isContactSensorEngaged() {
	ifDebug("isContactSensorEngaged")
//    return (contactSensor ? (contactSensor.currentContact.contains('open') ?
//                           (!contactSensorOutsideDoor ? false : true) : (contactSensorOutsideDoor ? true : false)) : false)
    def cV = contactSensor.currentContact.contains('open')
    return (contactSensor ? (contactSensorOutsideDoor ? (cV ? true : false) : (cV ? false : true)) : false)
}
*/

private isAnyESwitchOn()   {
    ifDebug("isAnyESwitchOn")
    return (engagedSwitch ? (engagedSwitch.currentSwitch.contains('on') ? 1 : 0) : -1)
}

private isAnyASwitchOn()   {
    ifDebug("isAnyASwitchOn")
    return (asleepSwitch ? (asleepSwitch.currentSwitch.contains('on') ? 1 : 0) : -1)
}

private isAnyNSwitchOn()   {
    ifDebug("isAnyNSwitchOn")
    return (nightSwitches ? (nightSwitches.currentSwitch.contains('on') ? 1 : 0) : -1)
}

private isAnyLSwitchOn()   {
    ifDebug("isAnyLSwitchOn")
    return (lockedSwitch ? (lockedSwitch.currentSwitch.contains('on') ? 1 : 0) : -1)
}

/*
private isAnyLSwitchOff()   {
    ifDebug("isAnyLSwitchOff")
    return (lockedSwitch ? (lockedSwitch.currentSwitch.contains('off') ? 1 : 0) : -1)
}
*/

/*
private isAnyLContactOpen()   {
    ifDebug("isAnyLContactOpen")
    return (lockedSwitch ? (lockedSwitch.currentSwitch.contains('on') ? 1 : 0) : -1)
}

private isAnyLContactClosed()   {
    ifDebug("isAnyLContactClosed")
    return (lockedSwitch ? (lockedSwitch.currentSwitch.contains('off') ? 1 : 0) : -1)
}
*/

def updateRulesToState()    {
    ifDebug("updateRulesToState")
    state.timeCheck = false
    state.ruleHasAL = false
    state.ruleHasHL = false
    state.vacant = false
    state.powerCheck = false
    state.execute = false
    state.maintainRoomTemp = false
//    state.previousRuleNo = null
    state.rules = false
    def i = 1
    for (; i < 11; i++)     {
        def ruleNo = String.valueOf(i)
        def thisRule = getRule(ruleNo, '*', false)
        if (thisRule && !thisRule.disabled)     {
//        if (thisRule && (thisRule.disabled || thisRule.mode || thisRule.state || thisRule.dayOfWeek ||
//                        thisRule.luxThreshold != null || thisRule.piston || thisRule.actions || thisRule.musicAction ||
//                        thisRule.fromDate || thisRule.toDate || thisRule.fromTimeType || thisRule.toTimeType ||
//                        thisRule.switchesOn || thisRule.setLevelTo || thisRule.setColorTo || thisRule.setColorTemperatureTo ||
//                        thisRule.switchesOff || thisRule.noMotion || thisRule.noMotionEngaged || thisRule.dimTimer))     {

            state.maxRuleNo = i   // cant use yet because for existing rooms value will not be populated
            if (!state.rules)   state.rules = [:];
            state.rules << ["$ruleNo":[isRule:true]]
            if (!thisRule.type || thisRule.type == 'e')     state.execute = true
            else if (thisRule.type == 't')      state.maintainRoomTemp = true
            if (thisRule.level == 'AL')         state.ruleHasAL = true
            else if (thisRule.level?.startsWith('HL'))    state.ruleHasHL = true
            if (thisRule.state && thisRule.state.contains('vacant'))    state.vacant = true
            if (thisRule.fromTimeType && thisRule.toTimeType)           state.timeCheck = true
        }
    }
}

def updateSwitchAttributesToStateAndSubscribe()    {
    ifDebug("updateSwitchAttributesToStateAndSubscribe")
    def switches = []
    def switchesID = []
    def i = 1
    for (; i < 11; i++)     {
        def ruleNo = String.valueOf(i)
        def thisRule = getRule(ruleNo, null, false)
        if (thisRule && !thisRule.disabled && thisRule.switchesOn)
            thisRule.switchesOn.each      {
                def itID = it.getId()
                if (!switchesID.contains(itID))      {
                    switches << it
                    switchesID << itID
                    if (it.hasCommand("setLevel"))      state.switchesHasLevel << ["$itID":true];
                    if (it.hasCommand("setColor"))      state.switchesHasColor << ["$itID":true];
                    if (it.hasCommand("setColorTemperature"))       state.switchesHasColorTemperature << ["$itID":true];
                }
            }
    }
    if (switches)       {
        subscribe(switches, "switch.on", switchOnEventHandler)
        subscribe(switches, "switch.off", switchOffEventHandler)
    }
}

private getNextRule(ruleNo, ruleType = '*', getConditionsOnly = false)     {
    for (def i = ruleNo as Integer; i < 11; i++)       {
        def nextRuleNo = String.valueOf(i)
        def thisRule = getRule(nextRuleNo, ruleType, true, getConditionsOnly)
        if (thisRule && !thisRule.disabled)     return thisRule;
    }
    return [ruleNo:'EOR']
}

private getRule(ruleNo, ruleTypeP = '*', checkState = true, getConditionsOnly = false)     {
    if (!ruleNo)        return null
    if (checkState && (!state.rules || !state.rules[ruleNo]))      return null;
    if (ruleTypeP == 'e')   ruleTypeP = null;
    if (checkState && ((!ruleTypeP && !state.execute) || (ruleTypeP == 't' && !state.maintainRoomTemp)))    return null;
    def ruleType = settings["type$ruleNo"]
    if (ruleType == 'e')    ruleType = null;
    if (ruleTypeP != '*' && ruleType != ruleTypeP)      return null;
    def ruleName = settings["name$ruleNo"]
    def ruleDisabled = settings["disabled$ruleNo"]
    def ruleMode = settings["mode$ruleNo"]
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
    def ruleFromHumidity = settings["fromHumidity$ruleNo"]
    def ruleToHumidity = settings["toHumidity$ruleNo"]
//    def ruleFromDate = dateInputValid(settings["fromDate$ruleNo"], true)
//    def ruleToDate = dateInputValid(settings["toDate$ruleNo"], false)
    def rD = dateInputValid(settings["fromDate$ruleNo"], settings["toDate$ruleNo"])
    def ruleFromDate = (ruleType != 't' ? rD[0] : null)
    def ruleToDate = (ruleType != 't' ? rD[1] : null)
    def ruleFromTimeType = settings["fromTimeType$ruleNo"]
    def ruleFromTimeOffset = settings["fromTimeOffset$ruleNo"]
    def ruleFromTime = settings["fromTime$ruleNo"]
    def ruleToTimeType = settings["toTimeType$ruleNo"]
    def ruleToTimeOffset = settings["toTimeOffset$ruleNo"]
    def ruleToTime = settings["toTime$ruleNo"]

    if (ruleType == 't')    {
        def ruleRoomCoolTemp = settings["coolTemp$ruleNo"]
        def ruleRoomHeatTemp = settings["heatTemp$ruleNo"]
        def ruleTempRange = settings["tempRange$ruleNo"]
        def ruleFanOnTemp = settings["fanOnTemp$ruleNo"]
        def ruleFanSpeedIncTemp = settings["fanSpeedIncTemp$ruleNo"]
//        ifDebug("$ruleRoomCoolTemp | $ruleRoomHeatTemp | $ruleTempRange | $ruleFanOnTemp | $ruleFanSpeedIncTemp")
        if (!(ruleName || ruleDisabled || ruleMode || ruleState || ruleDayOfWeek ||
                      ruleFromTimeType || ruleToTimeType || ruleRoomCoolTemp || ruleRoomHeatTemp || ruleFanOnTemp))
            return null
        else
            return [ruleNo:ruleNo, type:ruleType, name:ruleName, disabled:ruleDisabled, mode:ruleMode, state:ruleState, dayOfWeek:ruleDayOfWeek,
                    fromTimeType:ruleFromTimeType, fromTimeOffset:ruleFromTimeOffset, fromTime:ruleFromTime,
                    toTimeType:ruleToTimeType, toTimeOffset:ruleToTimeOffset, toTime:ruleToTime,
                    coolTemp:ruleRoomCoolTemp, heatTemp:ruleRoomHeatTemp, tempRange:ruleTempRange, fanOnTemp:ruleFanOnTemp, fanSpeedIncTemp:ruleFanSpeedIncTemp]
    }
    else    {
        if (getConditionsOnly)      {
            if (!(ruleName || ruleDisabled || ruleMode || ruleState || ruleDayOfWeek || ruleLuxThreshold != null ||
                          (ruleFromHumidity && ruleToHumidity) ||
                          ruleFromDate || ruleToDate || ruleFromTimeType || ruleToTimeType))
                return null
            else
                return [ruleNo:ruleNo, type:ruleType, name:ruleName, disabled:ruleDisabled, mode:ruleMode, state:ruleState, dayOfWeek:ruleDayOfWeek,
                        luxThreshold:ruleLuxThreshold, fromHumidity:ruleFromHumidity, toHumidity:ruleToHumidity,
                        fromDate:ruleFromDate, toDate:ruleToDate,
                        fromTimeType:ruleFromTimeType, fromTimeOffset:ruleFromTimeOffset, fromTime:ruleFromTime,
                        toTimeType:ruleToTimeType, toTimeOffset:ruleToTimeOffset, toTime:ruleToTime]
        }
        def rulePiston = settings["piston$ruleNo"]
        def ruleActions = settings["actions$ruleNo"]
        def ruleMusicAction = settings["musicAction$ruleNo"]
        def ruleShadePostion = settings["shadePosition$ruleNo"]
        def ruleSwitchesOn = settings["switchesOn$ruleNo"]
        def ruleSetLevelTo = settings["setLevelTo$ruleNo"]
        def ruleSetColorTo = settings["setColorTo$ruleNo"]
        def ruleSetHueTo = returnHueAndSaturation(ruleSetColorTo)
        def ruleSetColorTemperatureTo = settings["setColorTemperatureTo$ruleNo"]
        def ruleSwitchesOff = settings["switchesOff$ruleNo"]
        def ruleNoMotion = settings["noMotion$ruleNo"]
        def ruleNoMotionEngaged = settings["noMotionEngaged$ruleNo"]
        def ruleDimTimer = settings["dimTimer$ruleNo"]
        def ruleNoMotionAsleep = settings["noMotionAsleep$ruleNo"]
        if (!(ruleName || ruleDisabled || ruleMode || ruleState || ruleDayOfWeek || ruleLuxThreshold != null ||
                (ruleFromHumidity && ruleToHumidity) ||
                ruleFromDate || ruleToDate || ruleFromTimeType || ruleToTimeType ||
                rulePiston || ruleActions || ruleMusicAction || ruleShadePostion ||
                ruleSwitchesOn || ruleSetLevelTo || ruleSetColorTo || ruleSetColorTemperatureTo || ruleSwitchesOff ||
                ruleNoMotion || ruleNoMotionEngaged || ruleDimTimer || ruleNoMotionAsleep))
            return null
        else
            return [ruleNo:ruleNo, type:ruleType, name:ruleName, disabled:ruleDisabled, mode:ruleMode, state:ruleState, dayOfWeek:ruleDayOfWeek,
                    luxThreshold:ruleLuxThreshold, fromHumidity:ruleFromHumidity, toHumidity:ruleToHumidity,
                    fromDate:ruleFromDate, toDate:ruleToDate,
                    fromTimeType:ruleFromTimeType, fromTimeOffset:ruleFromTimeOffset, fromTime:ruleFromTime,
                    toTimeType:ruleToTimeType, toTimeOffset:ruleToTimeOffset, toTime:ruleToTime,
                    piston:rulePiston, actions:ruleActions, musicAction:ruleMusicAction, shade:ruleShadePostion,
                    switchesOn:ruleSwitchesOn, level:ruleSetLevelTo, color:ruleSetColorTo, hue:ruleSetHueTo, colorTemperature:ruleSetColorTemperatureTo,
                    switchesOff:ruleSwitchesOff,
                    noMotion:ruleNoMotion, noMotionEngaged:ruleNoMotionEngaged, dimTimer:ruleDimTimer, noMotionAsleep:ruleNoMotionAsleep]
    }
}

def	modeEventHandler(evt)	{
    ifDebug("modeEventHandler")
    if (state.dayOfWeek && !(checkRunDay()))    return;
	if (awayModes && awayModes.contains(evt.value))
    	roomVacant(true)
    else if (pauseModes && pauseModes.contains(evt.value))
        unscheduleAll("mode handler")
    else
        switchesOnOrOff()
}

def	motionActiveEventHandler(evt)	{
    ifDebug("motionActiveEventHandler")
    def child = getChildDevice(getRoom())
    child.updateMotionInd(1)
    if (!checkPauseModesAndDoW())    return;
	def roomState = child?.currentValue(occupancy)
    if (roomState == asleep)		{
        ifDebug("$nightMotionSensors | $evt.id | ${!nightMotionSensors.contains(evt.deviceId)}")
        if (nightMotionSensors && !nightMotionSensors.contains(evt.deviceId))     return;
        if (nightSwitches && nightTurnOn.contains('1'))      {
            dimNightLights()
            if (state.noMotionAsleep && whichNoMotion != lastMotionInactive)    {
                updateChildTimer(state.noMotionAsleep)
                runIn(state.noMotionAsleep, nightSwitchesOff)
            }
        }
		return
    }
    unscheduleAll("motion active handler")
    if (roomState == engaged)     {
//        if (whichNoMotion == lastMotionActive && state.noMotionEngaged && !isRoomEngaged(false,true,false,true,false))      {
/*        if (whichNoMotion == lastMotionActive && state.noMotionEngaged)      {
            updateChildTimer(state.noMotionEngaged)
            runIn(state.noMotionEngaged, roomVacant)
        }
*/
        if (whichNoMotion == lastMotionActive)      refreshEngagedTimer(engaged);
        return
    }
/*
    if (state.busyCheck && roomState == occupied && state.stateStack['0']?.state == occupied && state.noMotion)      {
        def gapBetween = ((now() - state.stateStack['0'].date) / 1000f)
        def howMany = ((gapBetween / state.noMotion) * lightTraffic.toInteger()).trunc(0)
        ifDebug("howMany: $howMany | gapBetween: $gapBetween | busyCheck: $state.busyCheck")
        if (howMany >= state.busyCheck)      {
            state.isBusy = true
            state.stateStack = [:]
        }
    }
*/
    if (state.isBusy && ['occupied', 'checking', 'vacant'].contains(roomState))       {
        turnOffIsBusy()
        child.generateEvent(engaged)
        return
    }
/*    def cVContact = contactSensor?.currentContact
    if (contactSensor && ((!cVContact.contains(open) && !contactSensorOutsideDoor) || (!cVContact.contains(closed) && contactSensorOutsideDoor)))      {
        if (['occupied', 'checking'].contains(roomState))
            child.generateEvent(engaged)
        else if (roomState == vacant)
            child.generateEvent(occupied)
    }*/
    if (['checking', 'vacant'].contains(roomState))     {
//        if (powerDevice && powerValueEngaged && powerDevice.currentPower >= powerValueEngaged &&
//            (powerTriggerFromVacant || roomState != vacant)) || isRoomEngaged())
        if (isRoomEngaged())
            child.generateEvent(engaged)
        else if (powerDevice && powerValueAsleep && powerDevice.currentPower >= powerValueAsleep && (powerTriggerFromVacant || roomState != vacant))
            child.generateEvent(asleep)
        else if (powerDevice && powerValueLocked && powerDevice.currentPower >= powerValueLocked && (powerTriggerFromVacant || roomState != vacant))
            child.generateEvent(locked)
        else
            child.generateEvent(occupied)
    }
    else if (roomState == occupied && whichNoMotion == lastMotionActive && state.noMotion)   {
//        updateChildTimer(state.noMotion)
//        runIn(state.noMotion, roomVacant)
        refreshOccupiedTimer(occupied)
    }
}

def	motionInactiveEventHandler(evt)     {
    ifDebug("motionInactiveEventHandler")
    def child = getChildDevice(getRoom())
    def motionActive = motionSensors.currentMotion.contains(active)
    child.updateMotionInd( motionActive ? 1 : 0)
    if (!checkPauseModesAndDoW())    return;
	def roomState = child?.currentValue(occupancy)
    if (roomState == engaged)     {
//        if (whichNoMotion == lastMotionInactive && state.noMotionEngaged && !isRoomEngaged(false,true,false,true,false))      {
        if (whichNoMotion == lastMotionInactive && state.noMotionEngaged && !isRoomEngaged(false,true,false,true,true))      {
//            updateChildTimer(state.noMotionEngaged)
//            runIn(state.noMotionEngaged, roomVacant)
            refreshEngagedTimer(engaged)
        }
    }
    else if (roomState == occupied)       {
        if (state.noMotion && whichNoMotion == lastMotionInactive && !motionActive)    {
//            updateChildTimer(state.noMotion)
//            runIn(state.noMotion, roomVacant)
            refreshOccupiedTimer(occupied)
        }
    }
    else if (roomState == asleep && nightSwitches && nightTurnOn.contains('1'))     {
        if (!nightMotionSensors || nightMotionSensors.contains(evt.deviceId))      {
            if (whichNoMotion == lastMotionInactive && !motionActive)        {
                if (state.noMotionAsleep)       updateChildTimer(state.noMotionAsleep);
                runIn((state.noMotionAsleep ?: 1), nightSwitchesOff)
            }
        }
    }
}

def adjMotionActiveEventHandler(evt)    {
    ifDebug("adjMotionActiveEventHandler")
    def child = getChildDevice(getRoom())
    child.updateAdjMotionInd(1)
    if (!checkPauseModesAndDoW())    return;
    def roomState = child?.currentValue(occupancy)
    if (adjRoomsMotion && roomState == occupied)      {
        def mV = motionSensors?.currentMotion.contains(active)
        def mD = motionSensors?.getLastActivity().max()
        if (!(mV && mD > evt.date))     child.generateEvent(checking);
        return
    }
    if (adjRoomsPathway && roomState == vacant)
        adjRooms.each       {
            def lastStateDate = parent.getLastStateDate(it)
            if (lastStateDate['state'])         {
                def evtDate = evt.date.getTime()
                def lsDate = lastStateDate['date']
                def dateDiff = (evtDate - lsDate) + 0
                if (lastStateDate['state'] == vacant)    {
//                    switchesOn()
                    processRules(occupied)
                    child.generateEvent(checking)
                    return
                }
            }
        }
}

def adjMotionInactiveEventHandler(evt)      {
    ifDebug("adjMotionInactiveEventHandler")
    def child = getChildDevice(getRoom())
    child.updateAdjMotionInd(0)
//    child.updateAdjMotionInd((adjMotionSensors.currentMotion.contains('active') ? 1 : 0))
}

def	buttonPushedOccupiedEventHandler(evt)     {
    ifDebug("buttonPushedOccupiedEventHandler: $evt.data")
    if (!checkPauseModesAndDoW())    return;
    if (getHubType() == _SmartThings)       {
        if (!evt.data)      return;
        def eD = new groovy.json.JsonSlurper().parseText(evt.data)
        assert eD instanceof Map
        if (!eD || (buttonIsOccupied && eD['buttonNumber'] && eD['buttonNumber'] != buttonIsOccupied as Integer))     return;
    }
    def child = getChildDevice(getRoom())
//    ifDebug("buttonPushedVacantEventHandler: ${child.currentValue(occupancy)}")
//    def roomState = child?.currentValue(occupancy)
/*    if (roomState != occupied)
        child.generateEvent(occupied)
    else if (buttonToggleWithOccupied && roomState == occupied)
        child.generateEvent(vacant)
*/
//    child.generateEvent(child?.currentValue(occupancy) != occupied ? occupied : checking)
    if (roomState == occupied)        {
        if (!buttonOnlySetsOccupied)      child.generateEvent(checking);
    }
    else
        child.generateEvent(occupied)
// added 18-01-30: if room is already vacant or another state dont do anything
/*    else    {
        if (roomState == vacant)
            child.generateEvent('checking')
    }*/
}

def occupiedSwitchOnEventHandler(evt)       {
    ifDebug("occupiedSwitchOnEventHandler")
    def child = getChildDevice(getRoom())
    child.updateOSwitchInd(isAnyOccupiedSwitchOn())
    if (!checkPauseModesAndDoW())    return;
    def roomState = child?.currentValue(occupancy)
    if (!['vacant','occupied','checking'].contains(roomState))      return;
/*    def newState = roomState
    if (powerDevice && powerValueEngaged && (powerTriggerFromVacant || roomState != 'vacant')
        && powerDevice.currentPower >= powerValueEngaged)
        newState = 'engaged'
    else if (roomState == 'vacant')
        newState = 'occupied'
    else if (roomState == 'checking')
        newState = (contactSensor && isContactSensorEngaged() ? 'engaged' : 'occupied')
    if (newState == roomState)    {
        if (state.noMotion && newState == 'occupied')    {
            // If state didn't change, reset the timer unless motion sensor inactive will handle it
            if (motionSensors && whichNoMotion == lastMotionInactive && motionSensors.currentMotion.contains('active'))
                unscheduleAll("occupiedSwitchOnEventHandler")
            else    {
                updateChildTimer(state.noMotion)
                runIn(state.noMotion, roomVacant)
            }
        }
    }
    else
        child.generateEvent(newState)
*/
//    if (roomState == vacant)
//        child.generateEvent(occupied)
//    else if (roomState == checking)
    if (['vacant', 'checking'].contains(roomState))
        child.generateEvent(isRoomEngaged() ? engaged : occupied)
    else if (state.noMotion)    {
        // If state didn't change, reset the timer unless motion sensor inactive will handle it
        if (motionSensors && whichNoMotion == lastMotionInactive && motionSensors.currentMotion.contains('active'))
            unscheduleAll("occupiedSwitchOnEventHandler")
        else    {
//            updateChildTimer(state.noMotion)
//            runIn(state.noMotion, roomVacant)
            refreshOccupiedTimer(occupied)
        }
    }
}

def occupiedSwitchOffEventHandler(evt) {
    ifDebug("occupiedSwitchOffEventHandler")
    // occupied Switch is turned off
    def child = getChildDevice(getRoom())
    child.updateOSwitchInd(isAnyOccupiedSwitchOn())
    if (!checkPauseModesAndDoW())    return;
    if (child?.currentValue(occupancy) == occupied && !occSwitches.currentSwitch.contains(on))
        child.generateEvent(checking)
}

def	switchOnEventHandler(evt)       {
    ifDebug("switchOnEventHandler")
    runIn(1, toUpdateSwitchInd)
    if (!checkPauseModesAndDoW())    return;
/*    def child = getChildDevice(getRoom())
    def roomState = child.getRoomState()
    if (roomState == 'vacant')      {
        if (state.noMotion)
            runIn(state.noMotion, dimLights)
    }*/
}

def	switchOffEventHandler(evt)      {
    ifDebug("switchOffEventHandler")
    runIn(1, toUpdateSwitchInd)
    if (!checkPauseModesAndDoW())    return;
//    if (!('on' in switches2.currentSwitch))
//        unschedule()
}

def toUpdateSwitchInd()     {  getChildDevice(getRoom()).updateSwitchInd(isAnySwitchOn())  }

def	buttonPushedEventHandler(evt)     {
    ifDebug("buttonPushedEventHandler: $evt.data")
    if (!checkPauseModesAndDoW())    return;
    if (getHubType() == _SmartThings)       {
        if (!evt.data)      return;
        def eD = new groovy.json.JsonSlurper().parseText(evt.data)
        assert eD instanceof Map
        if (!eD || (buttonIs && eD['buttonNumber'] != buttonIs as Integer))     return;
    }
    def child = getChildDevice(getRoom())
    def roomState = child?.currentValue(occupancy)
/*    if (child?.currentValue(occupancy) == engaged)
        child.generateEvent((resetEngagedDirectly ? vacant : checking))
    else
        child.generateEvent(engaged)
*/
//    ifDebug("buttonPushedEventHandler: ${child.currentValue(occupancy)}")
//    child.generateEvent((child?.currentValue(occupancy) != engaged ? engaged : (resetEngagedDirectly ? vacant : checking)))
    if (roomState == engaged)        {
        if (!buttonOnlySetsEngaged)      child.generateEvent((resetEngagedDirectly ? vacant : checking));
    }
    else
        child.generateEvent(engaged)

}

def	buttonPushedVacantEventHandler(evt)     {
    ifDebug("buttonPushedVacantEventHandler: $evt.data")
    if (!checkPauseModesAndDoW())    return;
    if (getHubType() == _SmartThings)       {
        if (!evt.data)      return;
        def eD = new groovy.json.JsonSlurper().parseText(evt.data)
        assert eD instanceof Map
        if (!eD || (buttonIsVacant && eD['buttonNumber'] && eD['buttonNumber'] != buttonIsVacant as Integer))     return;
    }
    def child = getChildDevice(getRoom())
//    ifDebug("buttonPushedVacantEventHandler: ${child.currentValue(occupancy)}")
    def roomState = child?.currentValue(occupancy)
//    if (['engaged', 'occupied', 'checking'].contains(roomState))
/*    if (roomState != vacant)
        child.generateEvent(vacant)
    else if (buttonToggleWithVacant && roomState == vacant)
        child.generateEvent(occupied)
*/
    child.generateEvent(roomState != vacant ? vacant : checking)
// added 18-01-30: if room is already vacant or another state dont do anything
/*    else    {
        if (roomState == vacant)
            child.generateEvent('checking')
    }*/
}

def	vacantSwitchOffEventHandler(evt)     {
    ifDebug("vacantSwitchOffEventHandler")
    if (!checkPauseModesAndDoW())    return;
    def child = getChildDevice(getRoom())
    if (['engaged', 'occupied', 'checking'].contains(child?.currentValue(occupancy)))
        child.generateEvent(vacant)
}

def	buttonPushedAsleepEventHandler(evt)     {
    ifDebug("buttonPushedAsleepEventHandler: $evt.data")
    if (!checkPauseModesAndDoW())    return;
    if (getHubType() == _SmartThings)       {
        if (!evt.data)      return;
        def eD = new groovy.json.JsonSlurper().parseText(evt.data)
        assert eD instanceof Map
        if (!eD || (buttonIsAsleep && eD['buttonNumber'] != buttonIsAsleep as Integer))     return;
    }
    def child = getChildDevice(getRoom())
    def roomState = child?.currentValue(occupancy)
//    child.generateEvent(roomState != asleep ? asleep : checking)
    if (roomState == asleep)        {
        if (!buttonOnlySetsAsleep)      child.generateEvent(checking);
    }
    else
        child.generateEvent(asleep)
}

/*
def	anotherRoomEngagedEventHandler()     {
    ifDebug("anotherRoomEngagedEventHandler")
    if (pauseModes && pauseModes.contains(location.currentMode))   return;
    if (state.dayOfWeek && !(checkRunDay()))    return;
    if (!evt.data)      return;
    def aRD = new groovy.json.JsonSlurper().parseText(evt.data)
    assert aRD instanceof Map
    if (!aRD || aRD['buttonNumber'] != 9)
    	return
    ifDebug("anotherRoomEngagedEventHandler button match")
    def child = getChildDevice(getRoom())
    def roomState = child.currentValue('occupancy')
    if (roomState == 'engaged')     {
        if (resetEngagedDirectly)
            child.generateEvent('vacant')
        else
            child.generateEvent('checking')
    }
}
*/

def	anotherRoomEngagedButtonPushedEventHandler(evt)     {
    ifDebug("anotherRoomEngagedButtonPushedEventHandler $evt.data")
    if (!checkPauseModesAndDoW())    return;
    if (personsPresence && presenceActionContinuous && personsPresence.currentPresence.contains('present'))     return;
    if (getHubType() == _SmartThings)       {
        if (!evt.data)      return;
        def eD = new groovy.json.JsonSlurper().parseText(evt.data)
        assert eD instanceof Map
        if (!eD || eD['buttonNumber'] != 9)     return;
    }
    def child = getChildDevice(getRoom())
    if (['engaged', 'asleep'].contains(child?.currentValue(occupancy)))
        child.generateEvent((resetEngagedDirectly ? vacant : checking))
}

def	presencePresentEventHandler(evt)     {
    ifDebug("presencePresentEventHandler")
    def child = getChildDevice(getRoom())
    child.updatePresenceInd(1)
    if (!checkPauseModesAndDoW())    return;
    if (presenceActionArrival())
        if (['occupied', 'checking', 'vacant'].contains(child?.currentValue(occupancy)))      child.generateEvent(engaged);
    processCoolHeat()
}

def	presenceNotPresentEventHandler(evt)     {
    ifDebug("presenceNotPresentEventHandler")
    if (personsPresence.currentPresence.contains('present'))     return;
    def child = getChildDevice(getRoom())
    child.updatePresenceInd(0)
    if (!checkPauseModesAndDoW())    return;
    if (presenceActionDeparture() && ['asleep', 'engaged', 'occupied'].contains(child?.currentValue(occupancy)))
        child.generateEvent((resetEngagedDirectly ? vacant : checking))
    state.thermoOverride = false
//    child.updateThermoOverrideIndC(thermoOverride)
    processCoolHeat()
}

def	engagedSwitchOnEventHandler(evt)     {
    ifDebug("engagedSwitchOnEventHandler")
    def child = getChildDevice(getRoom())
    child.updateESwitchInd(isAnyESwitchOn())
    if (!checkPauseModesAndDoW())    return;
//    if (personsPresence && presenceActionContinuous && personsPresence.currentPresence.contains('present'))     return;
//    if (powerDevice && powerValueEngaged && powerDevice.currentPower >= powerValueEngaged)     return;
    def roomState = child?.currentValue(occupancy)
//    if (['occupied', 'checking', 'vacant', 'asleep'].contains(roomState))
    if (roomState != engaged)
        child.generateEvent(engaged)
    else
        refreshEngagedTimer(engaged)
}

def	engagedSwitchOffEventHandler(evt)	{
    ifDebug("engagedSwitchOffEventHandler")
    def child = getChildDevice(getRoom())
    child.updateESwitchInd(isAnyESwitchOn())
    if (!checkPauseModesAndDoW())    return;
    if (isRoomEngaged())        return;
	def roomState = child?.currentValue(occupancy)
    if (resetEngagedDirectly && roomState == engaged)
        child.generateEvent(vacant)
//    else if (['engaged', 'occupied'].contains(roomState))
    else if (roomState == engaged)
        child.generateEvent(checking)
}

private isRoomEngaged(skipPresence = false, skipMusic = false, skipPower = false, skipSwitch = false, skipContact = false)      {
    if (!skipPresence && personsPresence && presenceActionContinuous && personsPresence.currentPresence.contains('present'))     return true;
//    ifDebug("not presence")
    if (!skipMusic && musicDevice && musicEngaged && musicDevice.currentStatus == 'playing')      return true;
//    ifDebug("not music")
    if (!skipPower && powerDevice && powerValueEngaged && powerDevice.currentPower >= powerValueEngaged)      return true;
//    ifDebug("not power")
    if (!skipSwitch && engagedSwitch && engagedSwitch.currentSwitch.contains('on'))     return true;
//    ifDebug("not switch")
    if (!skipContact && contactSensor)      {
        def cV = contactSensor.currentContact.contains(open)
        if ((!contactSensorOutsideDoor && !cV) || (contactSensorOutsideDoor && cV))     return true;
    }
//    ifDebug("not contact")
    return false
}

private refreshOccupiedTimer(roomState = null)   {
    def child
    if (!roomState)     {
        child = getChildDevice(getRoom())
        roomState = child?.currentValue(occupancy)
    }
    if (roomState == occupied && state.noMotion)      {
        updateChildTimer(state.noMotion)
        runIn(state.noMotion, roomVacant)
    }
}

private refreshEngagedTimer(roomState = null)   {
    def child
    if (!roomState)     {
        child = getChildDevice(getRoom())
        roomState = child?.currentValue(occupancy)
    }
    if (roomState == engaged && state.noMotionEngaged)      {
        updateChildTimer(state.noMotionEngaged)
        runIn(state.noMotionEngaged, roomVacant)
    }
}

def	contactOpenEventHandler(evt)	{
    ifDebug("contactOpenEventHandler")
    def cV = contactSensor.currentContact
    if (contactSensorOutsideDoor)       {
        if (contactSensor && musicDevice && announceDoor)   musicDevice.playTextAndResume(evt.device.displayName + ' closed.', announceVolume);
        if (announceContact && !cV.contains(open))          unschedule("contactStaysOpen");
    }
    else if (announceContact)        {
        def aC = announceContact as Integer
        runIn(aC * 60, contactStaysOpen)
    }
    def child = getChildDevice(getRoom())
    child.updateContactInd(contactSensorOutsideDoor ? (cV.contains(open) ? 0 : 1) : 0)
    if (!checkPauseModesAndDoW())    return;
    def roomState = child?.currentValue(occupancy)
    if (resetAsleepWithContact && roomState == asleep)    {
        updateChildTimer(25 * 60)
        runIn(25 * 60, resetAsleep)
        return
    }

/*    if (!contactSensorOutsideDoor && !contactSensorNotTriggersEngaged && isRoomEngaged(false,false,false,false,true))        return;
    if (((!contactSensorOutsideDoor && cV.contains(open)) || (contactSensorOutsideDoor && !cV.contains(open))) &&
        resetEngagedDirectly && roomState == engaged && !contactSensorNotTriggersEngaged)
        child.generateEvent(vacant)
    else if (['engaged', 'occupied', 'vacant'].contains(roomState))
        child.generateEvent(checking)
    else if (contactSensorOutsideDoor)
        refreshEngagedTimer(roomState)
*/

    if (roomState == engaged && isRoomEngaged(false,false,false,false,true))        {
        refreshEngagedTimer(engaged)
        return
    }
    if (contactSensorOutsideDoor)       {
        if (roomState == vacant && hasOccupiedDevice())
            child.generateEvent(checking)
        else if (roomState == occupied)     {
            if (!motionSensors || whichNoMotion != lastMotionInactive || !motionSensors.currentMotion.contains(active))
                child.generateEvent(checking)
        }
        else if (roomState == engaged && !contactSensorNotTriggersEngaged && !cV.contains(open))
            child.generateEvent(resetEngagedDirectly ? vacant : checking)
    }
    else        {
        if (roomState == vacant && hasOccupiedDevice())
            child.generateEvent(checking)
        else if (roomState == occupied)     {
            if (!motionSensors || whichNoMotion != lastMotionInactive || !motionSensors.currentMotion.contains(active))
                child.generateEvent(checking)
        }
        else if (roomState == engaged && !contactSensorNotTriggersEngaged)
            child.generateEvent(resetEngagedDirectly ? vacant : checking)
    }
}

def	contactClosedEventHandler(evt)     {
    ifDebug("contactClosedEventHandler")
    def cV = contactSensor.currentContact
    if (contactSensorOutsideDoor)       {
        if (contactSensor && musicDevice && announceDoor)   musicDevice.playTextAndResume(evt.device.displayName + ' opened.', announceVolume);
        if (announceContact)        {
            def aC = announceContact as Integer
            runIn(aC * 60, contactStaysOpen)
        }
    }
    else if (announceContact && !cV.contains(open))
        unschedule("contactStaysOpen")
    def child = getChildDevice(getRoom())
    child.updateContactInd(contactSensorOutsideDoor ? 0 : (cV.contains(open) ? 0 : 1))
    if (!checkPauseModesAndDoW())    return;
    def roomState = child?.currentValue(occupancy)
    if (resetAsleepWithContact && roomState == asleep)      {
        unschedule('resetAsleep')
        updateChildTimer(0)
    }

/*    if (contactSensorOutsideDoor && !contactSensorNotTriggersEngaged && isRoomEngaged(false,false,false,false,true))        return;
//    if (['occupied', 'checking'].contains(roomState) || (!motionSensors && roomState == 'vacant'))
    if (((!contactSensorOutsideDoor && !cV.contains(open)) || (contactSensorOutsideDoor && cV.contains(open))) &&
        (['occupied', 'checking'].contains(roomState) || (!hasOccupiedDevice() && roomState == vacant)) && !contactSensorNotTriggersEngaged)
        child.generateEvent(engaged)
    else if (hasOccupiedDevice() && roomState == vacant)
        child.generateEvent(checking)
    else if (!contactSensorOutsideDoor)
        refreshEngagedTimer(roomState)
*/

    if (roomState == engaged && isRoomEngaged(false,false,false,false,true))        {
        refreshEngagedTimer(engaged)
        return
    }
    if (contactSensorOutsideDoor)       {
        if (['checking', 'vacant'].contains(roomState))    {
            if (hasOccupiedDevice())
                child.generateEvent(checking)
            else if (cV.contains(open))
                child.generateEvent(engaged)
        }
        else if (roomState == occupied && !contactSensorNotTriggersEngaged)
            child.generateEvent(engaged)
        else if (roomState == engaged && !contactSensorNotTriggersEngaged)
            refreshEngagedTimer(engaged)
    }
    else        {
        if (['checking', 'vacant'].contains(roomState))    {
            if (hasOccupiedDevice())
                child.generateEvent(checking)
            else if (!cV.contains(open))
                child.generateEvent(engaged)
        }
        else if (roomState == occupied && !contactSensorNotTriggersEngaged && !cV.contains(open))
            child.generateEvent(engaged)
        else if (roomState == engaged && !contactSensorNotTriggersEngaged && !cV.contains(open))
            refreshEngagedTimer(engaged)
    }
}

def contactStaysOpen()      {
    ifDebug("contactStaysOpen")
    def cV = contactSensor.currentContact
    if (!cV.contains(open))    return;
    def cO = ''
    contactSensor.each      {
        if (it.currentContact == open)
            cO = (cO.size() > 0 ? ', ' : '') + it.displayName
    }
    musicDevice.playTextAndResume('Contacts open ' + cO + '.', announceVolume)
    def aC = announceContact as Integer
    runIn(aC * 60, contactStaysOpen)
}

def resetAsleep()     {
    def child = getChildDevice(getRoom())
    def roomState = child?.currentValue(occupancy)
    if (roomState == asleep)    {
        unschedule('roomAwake')
        child.generateEvent(checking)
    }
}

def	contactsRTOpenEventHandler(evt)     {
    ifDebug("contactsRTOpenEventHandler")
    def child = getChildDevice(getRoom())
    child.updateContactRTInd(contactSensorsRT.currentContact.contains(open) ? 0 : 1)
    if (!checkPauseModesAndDoW())    return;
    processCoolHeat()
}

def	contactsRTClosedEventHandler(evt)     {
    ifDebug("contactsRTClosedEventHandler")
    def child = getChildDevice(getRoom())
    child.updateContactRTInd(contactSensorsRT.currentContact.contains(open) ? 0 : 1)
    if (!checkPauseModesAndDoW())    return;
    processCoolHeat()
}

def musicPlayingEventHandler(evt)       {
    ifDebug("musicPlayingEventHandler")
//    ifDebug("evt.name: $evt.name | evt.value: $evt.value")
    def child = getChildDevice(getRoom())
    if (!checkPauseModesAndDoW())    return;
//    if (isRoomEngaged(,true,,,))    return;
    def roomState = child?.currentValue(occupancy)
//    if (['occupied', 'checking'].contains(roomState) || (!motionSensors && roomState == 'vacant'))
    if (roomState == occupied || (!hasOccupiedDevice() && roomState == vacant))
        child.generateEvent(engaged)
    else if (hasOccupiedDevice() && roomState == vacant)
        child.generateEvent(checking)
    else
        refreshEngagedTimer(engaged)
}

def musicStoppedEventHandler(evt)       {
    ifDebug("musicStoppedEventHandler")
//    ifDebug("evt.name: $evt.name | evt.value: $evt.value")
    def child = getChildDevice(getRoom())
    if (!checkPauseModesAndDoW())    return;
    if (isRoomEngaged(false,true,false,false,false))    return;
	def roomState = child?.currentValue(occupancy)
    if (resetEngagedDirectly && roomState == engaged)
        child.generateEvent(vacant)
    else if (['engaged', 'occupied', 'vacant'].contains(roomState))
        child.generateEvent(checking)
}

def temperatureEventHandler(evt)    {
    def child = getChildDevice(getRoom())
    def temperature = getAvgTemperature()
    boolean isFarenheit = (location.temperatureScale == 'F' ? true : false)
//    ifDebug("temperatureEventHandler: $temperature")
    child.updateTemperatureInd(temperature)
//    if (!personsPresence)       return;
    if (!checkPauseModesAndDoW())    return;
    processCoolHeat()
}

def	asleepSwitchOnEventHandler(evt)     {
    ifDebug("asleepSwitchOnEventHandler")
    def child = getChildDevice(getRoom())
    child.updateASwitchInd(isAnyASwitchOn())
    if (!checkPauseModesAndDoW())    return;
    child.generateEvent(asleep)
}

def	asleepSwitchOffEventHandler(evt)	{
    ifDebug("asleepSwitchOffEventHandler")
    def child = getChildDevice(getRoom())
    child.updateASwitchInd(isAnyASwitchOn())
    if (!checkPauseModesAndDoW())    return;
    if (asleepSwitch.currentSwitch.contains('on'))      return;
    if (child?.currentValue(occupancy) == asleep)       child.generateEvent(checking);
}

def	lockedSwitchOnEventHandler(evt)     {
// log.info "lockedSwitchOnEventHandler has been called = On"
    ifDebug("lockedSwitchOnEventHandler")
    def child = getChildDevice(getRoom())
    child.updateLSwitchInd(isAnyLSwitchOn())
/*    if (lockedSwitchCmd)
        child.updateLSwitchInd(isAnyLSwitchOn())
    else
        child.updateLSwitchInd(isAnyLSwitchOff())
*/
    if (!checkPauseModesAndDoW())    return;
    if (child?.currentValue(occupancy) != locked)       child.generateEvent(locked)
}

def	lockedSwitchOffEventHandler(evt)	{
// log.info "lockedSwtichOffEventHandler has been called = Off"
    ifDebug("lockedSwitchOffEventHandler")
    def child = getChildDevice(getRoom())
    child.updateLSwitchInd(isAnyLSwitchOn())
/*    if (lockedSwitchCmd == "on") {
    child.updateLSwitchInd(isAnyLSwitchOn())
    }
    if (lockedSwitchCmd == "off") {
    child.updateLSwitchInd(isAnyLSwitchOff())
    }
*/
    if (!checkPauseModesAndDoW())    return;
    if (child?.currentValue(occupancy) == locked)       child.generateEvent(checking);
}

def	lockedContactOpenEventHandler(evt)     {
// log.info "lockedContactOpenEventHandler has been called = Open"
    ifDebug("lockedContactOpenEventHandler")
    def child = getChildDevice(getRoom())
/*    if (lockedContactCmd == "open") {
    child.updateLContactInd(isAnyLContactOpen())
    }
    if (lockedContactCmd == "closed") {
    child.updateLContactInd(isAnyLContactClosed())
    }
*/
    if (!checkPauseModesAndDoW())    return;
    if (child?.currentValue(occupancy) == locked)       child.generateEvent(checking);
}

def	lockedContactClosedEventHandler(evt)	{
// log.info "lockedContactClosedEventHandler has been called = Closed"
    ifDebug("lockedContactClosedEventHandler")
    def child = getChildDevice(getRoom())
/*    if (lockedContactCmd == "open") {
    child.updateLContactInd(isAnyLContactOpen())
    }
    if (lockedContactCmd == "closed") {
    child.updateLContactInd(isAnyLContactClosed())
    }
*/
    if (!checkPauseModesAndDoW())    return;
    if (child?.currentValue(occupancy) != locked)       child.generateEvent(locked);
}

/*
def processCoolHeat()       {
    ifDebug("processCoolHeat")
    def temp = -1
    def child = getChildDevice(getRoom())
    def isHere = (personsPresence ? personsPresence.currentPresence.contains('present') : false)
    if ((checkPresence && !isHere) || maintainRoomTemp == '4')    {
        updateMaintainIndP(temp)
        updateThermostatIndP(isHere)
        return
    }
    def roomState = child?.currentValue('occupancy')
    def temperature = getAvgTemperature()
    def updateMaintainIndicator = true
    if (['1', '3'].contains(maintainRoomTemp))      {
//        rmCoolTemp = (((nightModes && nightModes.contains(location.currentMode)) || roomState == 'asleep') ? roomCoolTempNight : roomCoolTemp)
        def coolHigh = roomCoolTemp + 0.5
        def coolLow = roomCoolTemp - 0.5
        if (temperature >= coolHigh && (!checkPresence || (checkPresence && isHere)))     {
            if (roomCoolSwitch?.currentSwitch == 'off')     {
                roomCoolSwitch.on()
                updateMaintainIndP(roomCoolTemp)
                updateMaintainIndicator = false
            }
        }
        else        {
            if (temperature <= coolLow && (!checkPresence || (checkPresence && !isHere)))         {
                if (roomCoolSwitch?.currentSwitch == 'on')  {
                    roomCoolSwitch.off()
                }
            }
        }
    }
    if (['2', '3'].contains(maintainRoomTemp))      {
        def heatHigh = roomHeatTemp + 0.5
        def heatLow = roomHeatTemp - 0.5
        if (temperature >= heatHigh && (!checkPresence || (checkPresence && !isHere)))     {
            if (roomHeatSwitch?.currentSwitch == 'on')      {
                roomHeatSwitch.off()
            }
        }
        else        {
            if (temperature <= heatLow && (!checkPresence || (checkPresence && isHere)))        {
                if (roomHeatSwitch?.currentSwitch == 'off') {
                    roomHeatSwitch.on()
                    updateMaintainIndP(roomHeatTemp)
                    updateMaintainIndicator = false
                }
            }
        }
    }
    updateThermostatIndP(isHere)
    if (updateMaintainIndicator)    {
        if (maintainRoomTemp == '1')
            updateMaintainIndP(roomCoolTemp)
        else if (maintainRoomTemp == '2')
            updateMaintainIndP(roomHeatTemp)
        else if (maintainRoomTemp == '3')       {
            def x = Math.abs(temperature - roomCoolTemp)
            def y = Math.abs(temperature - roomHeatTemp)
            if (x >= y)
                updateMaintainIndP(roomHeatTemp)
            else
                updateMaintainIndP(roomCoolTemp)
        }
    }
}
*/

def roomThermostatEventHandler(evt)       {
    if (!state.roomThermoTurnedOn)     {
        state.thermoOverride = true
        runIn(thermoOverride * 60, thermoUnOverride)
//        child.updateThermoOverrideIndC(thermoOverride + 16)
    }
    else
        state.roomThermoTurnedOn = false
}

def roomCoolSwitchOnEventHandler(evt)       {
    if (!state.roomCoolTurnedOn)     {
        state.thermoOverride = true
        runIn(thermoOverride * 60, thermoUnOverride)
//        child.updateThermoOverrideIndC(thermoOverride + 16)
    }
    else
        state.roomCoolTurnedOn = false
}

def roomCoolSwitchOffEventHandler(evt)       {
    unschedule('thermoUnOverride')
    state.thermoOverride = false
//    child.updateThermoOverrideIndC(thermoOverride)
}

def roomHeatSwitchOnEventHandler(evt)       {
    if (!state.roomHeatTurnedOn)     {
        state.thermoOverride = true
        runIn(thermoOverride * 60, thermoUnOverride)
//        child.updateThermoOverrideIndC(thermoOverride + 16)
    }
    else
        state.roomHeatTurnedOn = false
}

def roomHeatSwitchOffEventHandler(evt)       {
    unschedule('thermoUnOverride')
    state.thermoOverride = false
//    child.updateThermoOverrideIndC(thermoOverride)
}

def thermoUnOverride()      {
    state.thermoOverride = false
    processCoolHeat()
//    child.updateThermoOverrideIndC(thermoOverride)
}

def processCoolHeat()       {
    ifDebug("processCoolHeat")
    if (state.thermoOverride)       return;
    def temp = -1
    def child = getChildDevice(getRoom())
    def isHere = (personsPresence ? personsPresence.currentPresence.contains(present) : false)
    boolean isFarenheit = (location.temperatureScale == 'F' ? true : false)
//    def hT = getHubType()
    if ((checkPresence && !isHere) || maintainRoomTemp == '4' && !roomFanSwitch)    {
        if (checkPresence && !isHere)       {
            if (['1', '3'].contains(maintainRoomTemp))      {
                state.roomThermoTurnedOn = false
                state.roomCoolTurnedOn = false
                (useThermostat ? roomThermostat.auto() : roomCoolSwitch.off()); pauseIt()
            }
            if (['2', '3'].contains(maintainRoomTemp))      {
                state.roomThermoTurnedOn = false
                state.roomHeatTurnedOn = false
                (useThermostat ? roomThermostat.auto() : roomHeatSwitch.off()); pauseIt()
            }
            if (roomFanSwitch)      {
                roomFanSwitch.off(); pauseIt()
            }
            if (useThermostat && roomVents && ['1', '2', '3'].contains(maintainRoomTemp))        {
                roomVents.off(); pauseIt()
            }
        }
        updateMaintainIndP(temp)
        updateThermostatIndP(isHere)
//        updateFanIndP()
        return
    }
    def roomState = child?.currentValue(occupancy)
    def temperature = getAvgTemperature()
    def updateMaintainIndicator = true
    def turnOn = null
    def thisRule = [:]
//    ifDebug("$state.rules")
    if (state.rules)    {
        def currentMode = String.valueOf(location.currentMode)
        def nowTime	= now() + 1000
        def nowDate = new Date(nowTime)
        def sunriseAndSunset = getSunriseAndSunset()
        def sunriseTime = new Date(sunriseAndSunset.sunrise.getTime())
        def sunsetTime = new Date(sunriseAndSunset.sunset.getTime())
        def timedRulesOnly = false
        def sunriseTimeWithOff, sunsetTimeWithOff
        for (def i = 1; i < 11; i++)      {
            def ruleHasTime = false
            def ruleNo = String.valueOf(i)
            thisRule = getNextRule(ruleNo, 't', true)
            if (thisRule.ruleNo == 'EOR')     break;
            i = thisRule.ruleNo as Integer
            if (thisRule.mode && !thisRule.mode.contains(currentMode))      continue;
            if (thisRule.state && !thisRule.state.contains(roomState))      continue;
            if (thisRule.dayOfWeek && !(checkRunDay(thisRule.dayOfWeek)))   continue;
// saved old time comparison while adding offset to sunrise / sunset
/*            if ((thisRule.fromTimeType && (thisRule.fromTimeType != timeTime() || thisRule.fromTime)) &&
                (thisRule.toTimeType && (thisRule.toTimeType != timeTime() || thisRule.toTime)))    {
                def fTime = ( thisRule.fromTimeType == timeSunrise() ? sunriseTime : ( thisRule.fromTimeType == timeSunset() ? sunsetTime : timeToday(thisRule.fromTime, location.timeZone)))
                def tTime = ( thisRule.toTimeType == timeSunrise() ? sunriseTime : ( thisRule.toTimeType == timeSunset() ? sunsetTime : timeToday(thisRule.toTime, location.timeZone)))
//                ifDebug("ruleNo: $ruleNo | fTime: $fTime | tTime: $tTime | nowDate: $nowDate | timeOfDayIsBetween: ${timeOfDayIsBetween(fTime, tTime, nowDate, location.timeZone)}")
                if (!(timeOfDayIsBetween(fTime, tTime, nowDate, location.timeZone)))    continue;
                if (!timedRulesOnly)    {
                    turnOn = null
                    timedRulesOnly = true
                    i = 0
                    continue
                }
                ruleHasTime = true
            }*/
            if ((thisRule.fromTimeType && (thisRule.fromTimeType != timeTime() || thisRule.fromTime)) &&
                (thisRule.toTimeType && (thisRule.toTimeType != timeTime() || thisRule.toTime)))    {
                if (thisRule.fromTimeType == timeSunrise())
                    sunriseTimeWithOff = (thisRule.fromTimeOffset ? new Date(sunriseTime.getTime() + (thisRule.fromTimeOffset * 60000L)) : sunriseTime)
                else if (thisRule.fromTimeType == timeSunset())
                    sunsetTimeWithOff = (thisRule.fromTimeOffset ? new Date(sunsetTime.getTime() + (thisRule.fromTimeOffset * 60000L)) : sunsetTime)
                def fTime = ( thisRule.fromTimeType == timeSunrise() ? sunriseTimeWithOff : ( thisRule.fromTimeType == timeSunset() ? sunsetTimeWithOff : timeToday(thisRule.fromTime, location.timeZone)))
                if (thisRule.toTimeType == timeSunrise())
                    sunriseTimeWithOff = (thisRule.toTimeOffset ? new Date(sunriseTime.getTime() + (thisRule.toTimeOffset * 60000L)) : sunriseTime)
                else if (thisRule.toTimeType == timeSunset())
                    sunsetTimeWithOff = (thisRule.toTimeOffset ? new Date(sunsetTime.getTime() + (thisRule.toTimeOffset * 60000L)) : sunsetTime)
                def tTime = ( thisRule.toTimeType == timeSunrise() ? sunriseTimeWithOff : ( thisRule.toTimeType == timeSunset() ? sunsetTimeWithOff : timeToday(thisRule.toTime, location.timeZone)))
//                ifDebug("ruleNo: $ruleNo | fTime: $fTime | tTime: $tTime | nowDate: $nowDate | timeOfDayIsBetween: ${timeOfDayIsBetween(fTime, tTime, nowDate, location.timeZone)}")
                if (!(timeOfDayIsB(fTime, tTime, nowDate, location.timeZone)))    continue;
                if (!timedRulesOnly)    {
                    turnOn = null
                    timedRulesOnly = true
                    i = 0
                    continue
                }
                ruleHasTime = true
            }
//            ifDebug("ruleNo: $thisRule.ruleNo | thisRule.luxThreshold: $thisRule.luxThreshold | turnOn: $turnOn | previousRuleLux: $previousRuleLux")
//            ifDebug("timedRulesOnly: $timedRulesOnly | ruleHasTime: $ruleHasTime")
            if (timedRulesOnly && !ruleHasTime)     continue;
            turnOn = thisRule.ruleNo
        }
    }
    ifDebug("processCoolHeat: rule: $turnOn")

    if (!turnOn)        return;

    thisRule = getRule(turnOn, 't')
    if (!contactSensorsRT || !contactSensorsRT.currentContact.contains(open))     {
        def tempRange = thisRule.tempRange
        if (['1', '3'].contains(maintainRoomTemp) && ((useThermostat && roomThermostat) || (!useThermostat && roomCoolSwitch)))      {
            def coolHigh = thisRule.coolTemp + (tempRange / 2f).round(1)
            def coolLow = thisRule.coolTemp - (tempRange / 2f).round(1)
            if (temperature >= coolHigh)     {
                if (useThermostat)      {
                    state.roomThermoTurnedOn = true
                    roomThermostat.setCoolingSetpoint(thisRule.coolTemp - thermoToTempSensor); pauseIt()
                    roomThermostat.fanAuto(); pauseIt()
                    roomThermostat.cool(); pauseIt()
                }
                else if (roomCoolSwitch.currentSwitch == off)     {
                    state.roomCoolTurnedOn = true
                    roomCoolSwitch.on(); pauseIt()
                    updateMaintainIndP(roomCoolTemp)
                    updateMaintainIndicator = false
                }
            }
            else if (temperature <= coolLow)    {
                state.roomThermoTurnedOn = false
                state.roomCoolTurnedOn = false
                (useThermostat ? roomThermostat.auto() : roomCoolSwitch.off()); pauseIt()
            }
            if (useThermostat && roomVents)
                if (roomThermostat.currentThermostatOperatingState == 'cooling')      {
                    def ventLevel = (((temperature - coolLow) * 100) / (coolHigh - coolLow)).round(0)
                    ventLevel = (ventLevel > 100 ? 100 : (ventLevel > 0 ?: 0))
                    roomVents.setLevel(ventLevel); pauseIt()
                }
                else
                    roomVents.off(); pauseIt()
        }
        if (['2', '3'].contains(maintainRoomTemp) && ((useThermostat && roomThermostat) || (!useThermostat && roomHeatSwitch)))      {
            def heatHigh = thisRule.heatTemp + (tempRange / 2f).round(1)
            def heatLow = thisRule.heatTemp - (tempRange / 2f).round(1)
            if (temperature >= heatHigh)    {
                state.roomThermoTurnedOn = false
                state.roomHeatTurnedOn = false
                (useThermostat ? roomThermostat.auto() : roomHeatSwitch.off()); pauseIt()
            }
            else if (temperature <= heatLow)        {
                if (useThermostat)      {
                    state.roomThermoTurnedOn = true
                    roomThermostat.setHeatingSetpoint(thisRule.heatTemp - thermoToTempSensor); pauseIt()
                    roomThermostat.fanAuto(); pauseIt()
                    roomThermostat.heat(); pauseIt()
                }
                else if (roomHeatSwitch.currentSwitch == off)     {
                    state.roomHeatTurnedOn = true
                    roomHeatSwitch.on(); pauseIt()
                    updateMaintainIndP(roomHeatTemp)
                    updateMaintainIndicator = false
                }
            }
            if (useThermostat && roomVents)
                if (roomThermostat.currentThermostatOperatingState == 'heating')      {
                    def ventLevel = (((temperature - heatLow) * 100 ) / (heatHigh - heatLow)).round(0)
                    ventLevel = (ventLevel > 100 ? 100 : (ventLevel > 0 ?: 0))
                    roomVents.setLevel(ventLevel); pauseIt()
                }
                else
                    roomVents.off(); pauseIt()
        }
    }
    else if (contactSensorsRT && !contactSensorsRT.currentContact.contains(open))     {
        if (['1', '3'].contains(maintainRoomTemp))      {
            state.roomThermoTurnedOn = false
            state.roomCoolTurnedOn = false
            (useThermostat ? roomThermostat.auto() : roomCoolSwitch.off()); pauseIt()
        }
        if (['2', '3'].contains(maintainRoomTemp))      {
            state.roomThermoTurnedOn = false
            state.roomHeatTurnedOn = false
            (useThermostat ? roomThermostat.auto() : roomHeatSwitch.off()); pauseIt()
        }
        if (useThermostat && roomVents && ['1', '2', '3'].contains(maintainRoomTemp))        {
            roomVents.off(); pauseIt()
        }
    }
    if (roomFanSwitch)        {
        if  (thisRule.fanOnTemp)        {
            def fanLowTemp      = (thisRule.fanOnTemp + 0f).round(1)
            def fanMediumTemp   = (thisRule.fanOnTemp + thisRule.fanSpeedIncTemp + 0f).round(1)
            def fanHighTemp     = (thisRule.fanOnTemp + (thisRule.fanSpeedIncTemp * 2f)).round(1)
//            ifDebug("temperature: $temperature | fanOnTemp: $thisRule.fanOnTemp | fanLowTemp: $fanLowTemp | fanMediumTemp: $fanMediumTemp | fanHighTemp: $fanHighTemp")
            if (temperature >= fanHighTemp)         { roomFanSwitch.on(); pauseIt(); roomFanSwitch.setLevel(fanHigh); }
            else if (temperature >= fanMediumTemp)  { roomFanSwitch.on(); pauseIt(); roomFanSwitch.setLevel(fanMedium); }
            else if (temperature >= fanLowTemp)     { roomFanSwitch.on(); pauseIt(); roomFanSwitch.setLevel(fanLow); }
            else                                    roomFanSwitch.off();
        }
        else
            roomFanSwitch.off()
        pauseIt()
    }
    updateThermostatIndP(isHere)
    if (updateMaintainIndicator)    {
        if (maintainRoomTemp == '1')
            updateMaintainIndP(thisRule.coolTemp)
        else if (maintainRoomTemp == '2')
            updateMaintainIndP(thisRule.heatTemp)
        else if (maintainRoomTemp == '3')       {
            def x = Math.abs(temperature - thisRule.coolTemp)
            def y = Math.abs(temperature - thisRule.heatTemp)
            if (x >= y)
                updateMaintainIndP(thisRule.heatTemp)
            else
                updateMaintainIndP(thisRule.coolTemp)
        }
    }
//    updateFanIndP()
}

private timeOfDayIsB(fromDate, toDate, checkDate, timeZone)     {
/*    if (timeOfDayIsBetween(fromDate, toDate, checkDate, location.timeZone) != (!checkDate.before(fromDate) && !checkDate.after(toDate))     {
        ifDebug("call bangali", error)
        ifDebug("timeOfDayIsB: ST: ${timeOfDayIsBetween(fromDate, toDate, checkDate, location.timeZone)} | Groovy: ${(!checkDate.before(fromDate) && !checkDate.after(toDate))}")
    }
*/
    return ( getHubType() == _SmartThings ? timeOfDayIsBetween(fromDate, toDate, checkDate, timeZone) :
                                            (!checkDate.before(fromDate) && !checkDate.after(toDate)))
}

private updateMaintainIndP(temp)   {
    ifDebug("updateMaintainIndP: temp: $temp")
    def child = getChildDevice(getRoom())
    if (child)  child.updateMaintainIndC(temp);
}

private updateThermostatIndP(isHere)   {
    ifDebug("updateThermostatIndP")
    def thermo = 9
    if ((useThermostat && roomThermostat && roomThermostat.currentThermostatOperatingState == 'cooling') ||
        (!useThermostat && roomCoolSwitch && roomCoolSwitch.currentSwitch == 'on'))
        thermo = 4
    else if ((useThermostat && roomThermostat && roomThermostat.currentThermostatOperatingState == 'heating') ||
             (!useThermostat && roomHeatSwitch && roomHeatSwitch.currentSwitch == 'on'))
        thermo = 5
    else if (!isHere && ['1', '2', '3'].contains(maintainRoomTemp))
        thermo = 0
    else if (maintainRoomTemp == '3')
        thermo = 1
    else if (maintainRoomTemp == '1')
        thermo = 2
    else if (maintainRoomTemp == '2')
        thermo = 3
    ifDebug("updateTheromstatInd: thermo: $thermo")
    def child = getChildDevice(getRoom())
    if (child)  child.updateThermostatIndC(thermo)
}

def updateFanIndP(evt)   {
    ifDebug("updateFanIndP")
    def child = getChildDevice(getRoom())
    if (child)      {
        def cL = roomFanSwitch?.currentLevel
        child.updateFanIndC((!roomFanSwitch ? -1 : (roomFanSwitch.currentSwitch == 'off' ? 0 : (cL <= fanLow ? 1 : (cL <= fanMedium ? 2 : 3)))))
    }
}

def luxEventHandler(evt)    {
    ifDebug("luxEventHandler")
    def child = getChildDevice(getRoom())
    int currentLux = getIntfromStr((String) evt.value)
    child.updateLuxInd(currentLux)
    if (!checkPauseModesAndDoW())    return;
    switchesOnOrOff()
    state.previousLux = currentLux
}

def humidityEventHandler(evt)    {
    ifDebug("humidityEventHandler")
    def child = getChildDevice(getRoom())
    if (!checkPauseModesAndDoW())    return;
    switchesOnOrOff()
}

private getIntfromStr(String mayOrMayNotBeDecimal)     {
//    ifDebug("getIntfromStr")
    int intValue
    if (mayOrMayNotBeDecimal.indexOf('.') >= 0)     {
        def str = mayOrMayNotBeDecimal.substring(0, mayOrMayNotBeDecimal.indexOf('.'))
        intValue = str as Integer
    }
    else
        intValue = mayOrMayNotBeDecimal.toInteger()
//    ifDebug("intValue: $intValue")
    return intValue
}

def powerEventHandler(evt)    {
    ifDebug("powerEventHandler")
    def child = getChildDevice(getRoom())
    def currentPower = getIntfromStr((String) evt.value)
    child.updatePowerInd(currentPower)
    if (!checkPauseModesAndDoW())    return;
    def roomState = child?.currentValue(occupancy)
    if (powerValueEngaged)     {
        if (currentPower >= powerValueEngaged && state.previousPower < powerValueEngaged &&
            ['occupied', 'checking', 'vacant'].contains(roomState) && (powerTriggerFromVacant || roomState != vacant))     {
            unschedule('powerStaysBelowEngaged')
            child.generateEvent(engaged)
        }
        else if (currentPower < powerValueEngaged && state.previousPower >= powerValueEngaged && roomState == engaged)
            runIn(powerStays, powerStaysBelowEngaged)
    }
    else if (powerValueAsleep)      {
        if (currentPower >= powerValueAsleep && state.previousPower < powerValueAsleep &&
            ['engaged', 'occupied', 'checking', 'vacant'].contains(roomState) && (powerTriggerFromVacant || roomState != vacant))    {
            unschedule('powerStaysBelowAsleep')
            child.generateEvent(asleep)
        }
        else if (currentPower < powerValueAsleep && state.previousPower >= powerValueAsleep && roomState == asleep)
            runIn(powerStays, powerStaysBelowAsleep)
    }
    else if (powerValueLocked)      {
        if (currentPower >= powerValueLocked && state.previousPower < powerValueLocked &&
            ['engaged', 'occupied', 'checking', 'vacant'].contains(roomState) && (powerTriggerFromVacant || roomState != vacant))    {
            unschedule('powerStaysBelowLocked')
            child.generateEvent(locked)
        }
        else if (currentPower < powerValueLocked && state.previousPower >= powerValueLocked && roomState == locked)
            runIn(powerStays, powerStaysBelowLocked)
    }
    state.previousPower = currentPower
}

def powerStaysBelowEngaged()   {
    def child = getChildDevice(getRoom())
    def roomState = child?.currentValue(occupancy)
    if (roomState == engaged)     {
//        def cV = contactSensor?.currentContact
        if (!isRoomEngaged())      child.generateEvent((resetEngagedDirectly ? vacant : checking));
    }
}

def powerStaysBelowAsleep()   {
    def child = getChildDevice(getRoom())
    def roomState = child?.currentValue(occupancy)
    if (roomState == asleep)      child.generateEvent(checking);
}

def powerStaysBelowLocked()   {
    def child = getChildDevice(getRoom())
    def roomState = child?.currentValue(occupancy)
    if (roomState == locked)      child.generateEvent(checking);
}

def speechEventHandler(evt)       {
    ifDebug("speechEventHandler")
    ifDebug("evt.name: $evt.name | evt.value: $evt.value")
}

//private luxFell(currentLux, luxThreshold)   {   return (currentLux <= luxThreshold && state.previousLux > luxThreshold)  }

//private luxRose(currentLux, luxThreshold)   {   return (currentLux > luxThreshold && state.previousLux <= luxThreshold)  }

// pass in child and roomState???
def roomVacant(forceVacant = false)	  {
    ifDebug("roomVacant")
    def child = getChildDevice(getRoom())
	def roomState = child?.currentValue(occupancy)
    if (!forceVacant && motionSensors && ['engaged', 'occupied', 'checking'].contains(roomState) && whichNoMotion == lastMotionInactive &&
        motionSensors.currentMotion.contains(active))     {
        motionActiveEventHandler(null)
        return
    }
    def newState = null
    if (['engaged', 'occupied'].contains(roomState))    newState = (state.dimTimer ? checking : vacant);
    else if (roomState == checking)                     newState = vacant;
    if (newState)   {
        child.generateEvent(newState)
//        sendLocationEvent(name: "occupancy", value: newState, descriptionText: "${child.displayName} changed to ${newState}", isStateChange: true, displayed: true, device: "$child.id", source: "DEVICE")
    }
}

def roomAwake()	  {
    ifDebug("roomAwake")
	def child = getChildDevice(getRoom())
	def roomState = child?.currentValue(occupancy)
    if (roomState == asleep)      child.generateEvent((state.dimTimer ? checking : vacant));
}

def roomUnlocked()	  {
    ifDebug("roomUnlocked")
	def child = getChildDevice(getRoom())
	def roomState = child?.currentValue(occupancy)
    if (roomState == locked)      child.generateEvent((state.dimTimer ? checking : vacant));
}

def runInHandleSwitches(oldState = null, newState = null)     {
    ifDebug("runInHandleSwitches")
    if (!oldState && !newState)
        ifDebug("runInHandleSwitches: child did not pass old and new state params in call!", 'error')
    else
        runIn(0, handleSwitches, [data: [oldState: oldState, newState: newState]])
}

//def handleSwitches(oldState = null, newState = null)	{
def handleSwitches(data)	{
    def oldState = data.oldState
    def newState = data.newState
    ifDebug("${app.label} room state - old: ${oldState} new: ${newState}")
//    state.roomState = newState
//      "yyyy-MM-dd'T'HH:mm:ssZ" = 2017-11-13T23:32:45+0000
    if (oldState == newState)      return false;
    def nowDate = now()
    state.previousState = ['state':newState, 'date':nowDate]
    previousStateStack(state.previousState)
    if (!checkPauseModesAndDoW())    return;
    def child = getChildDevice(getRoom())
    if (oldState == vacant && nightSwitches && nightTurnOn.contains('3'))       unschedule('nightSwitchesOff');
    if (oldState == asleep)       {
        unschedule('roomAwake')
        unschedule('resetAsleep')
        unschedule('nightSwitchesOff')
        updateChildTimer(0)
/*        if (noAsleepSwitchesOverride)       {
            if (nightSwitches && noAsleepSwitchesOff)       {
                def child = getChildDevice(getRoom())
                def theOffSwitches = nightSwitches.find { noAsleepSwitchesOff.contains(it.id) }
                ifDebug("Turning off devices in noAsleepSwitchesOff: ${ theOffSwitches}")
                theOffSwitches?.each { it.off(); pause(pauseMSec) }
                if (theOffSwitches)     {
                    theOffSwitches.each { it.off(); pause(pauseMSec) }
                    child.updateNSwitchInd(isAnyNSwitchOn())
                }
            }
            ifDebug("noAsleepSwitchesOverride: no switches to turn off")
        }
        else
*/
        if (nightSwitches)
            if (!nightTurnOn.contains('3'))
/*                dimNightLights()
                if (state.noMotionAsleep)        {
                    updateChildTimer(state.noMotionAsleep)
                    runIn(state.noMotionAsleep, nightSwitchesOff)
                }
            }
            else
*/
                nightSwitchesOff()
    }
    else if (oldState == locked)
        unschedule('roomUnlocked')
    else    {
        unscheduleAll("handle switches")
//        if (newState == checking && oldState == vacant)     processRules(occupied, true);
        if (oldState == checking)                      unDimLights(newState);
    }
    if (['engaged', 'occupied', 'asleep', 'vacant'].contains(newState))     {
        if (newState != vacant || state.vacant)   // not vacant or has vacant rule
            processRules()
        else        {
            switches2Off()
//            ifDebug("turnOffMusic: $turnOffMusic | musicDevice.currentStatus: $musicDevice.currentStatus")
            if (musicDevice && turnOffMusic && musicDevice.currentStatus == 'playing')
                musicDevice.pause()
        }
    }
//        if (['engaged', 'asleep'].contains(newState))       {
//            ifDebug("calling parent.notifyAnotherRoomEngaged: $app.id")
//            parent.notifyAnotherRoomEngaged(app.id)
    if (newState == asleep)     {
        if (nightSwitches)
            if (motionSensors.currentMotion.contains(active) || nightTurnOn.contains('2'))    {
                dimNightLights()
                if (state.noMotionAsleep && (!motionSensors.currentMotion.contains(active) || whichNoMotion != lastMotionInactive))        {
                    updateChildTimer(state.noMotionAsleep)
                    runIn(state.noMotionAsleep, nightSwitchesOff)
                }
            }
            else
                nightSwitchesOff()
        if (state.noAsleep)     {
            updateChildTimer(state.noAsleep)
            runIn(state.noAsleep, roomAwake)
        }
//                processCoolHeat()
    }
    else if (newState == engaged)       {
        if (state.noMotionEngaged && !isRoomEngaged(false,true,false,true,true))      {
//            updateChildTimer(state.noMotionEngaged)
//            runIn(state.noMotionEngaged, roomVacant)
            refreshEngagedTimer(engaged)
        }
    }
    else if (newState == occupied)     {
        if (state.noMotion && (!motionSensors || whichNoMotion == lastMotionActive ||
                                                (whichNoMotion == lastMotionInactive && !motionSensors.currentMotion.contains(active))))      {
//            updateChildTimer(state.noMotion)
//            runIn(state.noMotion, roomVacant)
            refreshOccupiedTimer(occupied)
        }
/*        if (state.noMotion)     {
            if (motionSensors)      {
                if (whichNoMotion == lastMotionActive ||
                    (whichNoMotion == lastMotionInactive && !motionSensors.currentMotion.contains('active')))      {
                    updateChildTimer(state.noMotion)
                    runIn(state.noMotion, roomVacant)
                }
            }
            else    {
//                    if (state.noMotion) {
                        // If there are no motion sensors, we start the timer when we change to occupied.
                updateChildTimer(state.noMotion)
                runIn(state.noMotion, roomVacant)
//                    }
            }
        }
*/
    }
//    }
    else if (newState == checking)     {
        dimLights()
        def dT = state.dimTimer ?: 1
        if (dT > 5)     updateChildTimer(dT);
        runIn(dT, roomVacant)
    }
    else if (newState == locked)      {
        if (lockedTurnOff)      switches2Off();
        if (state.unLocked)     runIn(state.unLocked, roomUnlocked);
    }
    if (oldState == asleep && newState == vacant && nightSwitches && nightTurnOn.contains('3'))       {
        dimNightLights()
        if (state.noMotionAsleep)        {
            updateChildTimer(state.noMotionAsleep)
            runIn(state.noMotionAsleep, nightSwitchesOff)
        }
    }
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
    if (state.luxEnabled && luxSensor.currentIlluminance > luxThreshold)
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
        processRules()
}
*/

def switchesOnOrOff(switchesOnly = false)      {
    ifDebug("switchesOnOrOff")
    state.holidayLights = false
    def child = getChildDevice(getRoom())
    def roomState = child?.currentValue(occupancy)
    if (roomState && ['engaged', 'occupied', 'asleep', 'vacant'].contains(roomState))      {
        def turnedOn = processRules(roomState, switchesOnly)
        ifDebug("switchesOnOrOff: ${(!turnedOn)} | $allSwitchesOff")
        if (!turnedOn && allSwitchesOff)        {
            switches2Off()
            if (musicDevice && turnOffMusic && musicDevice.currentStatus == 'playing')      musicDevice.pause();
        }
    }
}

private processRules(passedRoomState = null, switchesOnly = false)     {
    ifDebug("processRules")
/*    if (luxThreshold)     {
        def lux = luxSensor.currentIlluminance
        if (lux > luxThreshold)     return false;
    }*/
    state.holidayLights = false
    def child = getChildDevice(getRoom())
    def turnOn = []
    def previousRule = []
    def previousRuleLux = null
    state.lastRule = null
    def thisRule = [:]
    state.noMotion = ((noMotion && noMotion >= 5) ? noMotion : null)
    state.noMotionEngaged = ((noMotionEngaged && noMotionEngaged >= 5) ? noMotionEngaged : null)
    state.dimTimer = ((dimTimer && dimTimer >= 5) ? dimTimer : 5) // forces minimum of 5 seconds to allow for checking state
    state.noMotionAsleep = ((noMotionAsleep && noMotionAsleep >= 5) ? noMotionAsleep : null)
    updateTimers()
    if (state.rules)    {
        def currentMode = String.valueOf(location.currentMode)
        def roomState = (passedRoomState ?: child?.currentValue(occupancy))
        def nowTime	= now() + 1000
        def nowDate = new Date(nowTime)
        def sunriseAndSunset = getSunriseAndSunset()
        def sunriseTime = new Date(sunriseAndSunset.sunrise.getTime())
        def sunsetTime = new Date(sunriseAndSunset.sunset.getTime())
        def timedRulesOnly = false
        def sunriseTimeWithOff, sunsetTimeWithOff
        def i = 1
        for (; i < 11; i++)      {
//        for (def rule in state.rules.sort{ it.key })    {
            def ruleHasTime = false
            def ruleNo = String.valueOf(i)
            thisRule = getNextRule(ruleNo, null, true)
            if (thisRule.ruleNo == 'EOR')     break;
            i = thisRule.ruleNo as Integer
//            if (!thisRule || thisRule.disabled)      continue;
//            if (!thisRule.mode && !thisRule.state && !thisRule.dayOfWeek && thisRule.luxThreshold == null && !thisRule.actions &&
//                !thisRule.fromTimeType && !thisRule.toTimeType && !thisRule.switchesOn && !thisRule.switchesOff && !thisRule.piston)
//                continue
            if (thisRule.mode && !thisRule.mode.contains(currentMode))      continue;
            if (thisRule.state && !thisRule.state.contains(roomState))      continue;
            if (thisRule.dayOfWeek && !(checkRunDay(thisRule.dayOfWeek)))   continue;
            if (thisRule.fromDate && thisRule.toDate)   {
                def fTime = new Date().parse("yyyy-MM-dd'T'HH:mm:ssZ", thisRule.fromDate)
                def tTime = new Date().parse("yyyy-MM-dd'T'HH:mm:ssZ", thisRule.toDate)
//                ifDebug("fTime: $fTime | tTime: $tTime | nowDate: $nowDate | is time of day: ${(nowDate < fTime || nowDate > tTime)}")
                if (nowDate < fTime || nowDate > tTime)    continue;
            }
            if (thisRule.luxThreshold != null)   {
                int lux = getIntfromStr((String) luxSensor.currentIlluminance)
//                ifDebug("lux from device: $lux | rule lux threshold: $thisRule.luxThreshold")
                if (lux > thisRule.luxThreshold)    continue;
            }
            if (thisRule.fromHumidity && thisRule.toHumidity)   {
                int humidity = getIntfromStr((String) humiditySensor.currentRelativeHumidityMeasurement)
//                ifDebug("humidity from device: $humidity | rule lux range: $thisRule.fromHumidity - $thisRule.toHumidity")
                if (humidity < thisRule.fromHumidity || humidity > thisRule.toHumidity)    continue;
            }
            if ((thisRule.fromTimeType && (thisRule.fromTimeType != timeTime() || thisRule.fromTime)) &&
                (thisRule.toTimeType && (thisRule.toTimeType != timeTime() || thisRule.toTime)))    {
                if (thisRule.fromTimeType == timeSunrise())
                    sunriseTimeWithOff = (thisRule.fromTimeOffset ? new Date(sunriseTime.getTime() + (thisRule.fromTimeOffset * 60000L)) : sunriseTime)
                else if (thisRule.fromTimeType == timeSunset())
                    sunsetTimeWithOff = (thisRule.fromTimeOffset ? new Date(sunsetTime.getTime() + (thisRule.fromTimeOffset * 60000L)) : sunsetTime)
                def fTime = ( thisRule.fromTimeType == timeSunrise() ? sunriseTimeWithOff : ( thisRule.fromTimeType == timeSunset() ? sunsetTimeWithOff : timeToday(thisRule.fromTime, location.timeZone)))
                if (thisRule.toTimeType == timeSunrise())
                    sunriseTimeWithOff = (thisRule.toTimeOffset ? new Date(sunriseTime.getTime() + (thisRule.toTimeOffset * 60000L)) : sunriseTime)
                else if (thisRule.toTimeType == timeSunset())
                    sunsetTimeWithOff = (thisRule.toTimeOffset ? new Date(sunsetTime.getTime() + (thisRule.toTimeOffset * 60000L)) : sunsetTime)
                def tTime = ( thisRule.toTimeType == timeSunrise() ? sunriseTimeWithOff : ( thisRule.toTimeType == timeSunset() ? sunsetTimeWithOff : timeToday(thisRule.toTime, location.timeZone)))
//                ifDebug("ruleNo: $ruleNo | fTime: $fTime | tTime: $tTime | nowDate: $nowDate | timeOfDayIsBetween: ${timeOfDayIsBetween(fTime, tTime, nowDate, location.timeZone)}")
                if (!(timeOfDayIsB(fTime, tTime, nowDate, location.timeZone)))    continue;
                if (!timedRulesOnly)    {
                    turnOn = []
                    previousRule = []
                    previousRuleLux = null
                    timedRulesOnly = true
                    i = 0
                    continue
                }
                ruleHasTime = true
            }
//            ifDebug("ruleNo: $thisRule.ruleNo | thisRule.luxThreshold: $thisRule.luxThreshold | turnOn: $turnOn | previousRuleLux: $previousRuleLux")
//            ifDebug("timedRulesOnly: $timedRulesOnly | ruleHasTime: $ruleHasTime")
            if (timedRulesOnly && !ruleHasTime)     continue;
            if (thisRule.luxThreshold != null)      {
                if (previousRuleLux == thisRule.luxThreshold)   {
                    turnOn << thisRule.ruleNo
                    previousRule << thisRule.ruleNo
                }
                else if (!previousRuleLux || thisRule.luxThreshold < previousRuleLux)    {
                    previousRule.each   { turnOn.remove(it) }
                    turnOn << thisRule.ruleNo
                    previousRule << thisRule.ruleNo
                    previousRuleLux = thisRule.luxThreshold
                }
            }
            else
                turnOn << thisRule.ruleNo

/*            if (thisRule.luxThreshold != null)      {
                if (!turnOn || !previousRuleLux || thisRule.luxThreshold < previousRuleLux)    {
                    turnOn = thisRule.ruleNo
                    previousRuleLux = thisRule.luxThreshold
                }
            }
            else    {
//                if (!turnOn)
//                    turnOn = thisRule.ruleNo
                executeRule(thisRule)
                executedRule = true
            }*/
        }
    }
    ifDebug("processRules: rules to execute: $turnOn")
    if (turnOn)     {
        state.switchesPreventToggle = []
        def rules = []
        turnOn.each     {
            thisRule = getRule(it, null)
            thisRule.switchesOn.each    {
                def itID = it.getId()
                if (!state.switchesPreventToggle.contains(itID))    state.switchesPreventToggle << itID;
            }
            rules << thisRule
        }
/*        turnOn.each     {
            thisRule = getRule(it, null)
            executeRule(thisRule, switchesOnly)
        }*/
        rules.each      {  executeRule(it, switchesOnly)  }
        state.switchesPreventToggle = []
        return true
    }
    else
    {
        child.updateLastRuleInd(-1)
        return false
    }
}

private executeRule(thisRule, switchesOnly = false)   {
    ifDebug("${app.label} executed rule no: $thisRule.ruleNo")
    turnSwitchesOnAndOff(thisRule)
    if (!switchesOnly)  {
        runActions(thisRule)
        executePiston(thisRule)
        musicAction(thisRule)
    }
    setShade(thisRule)
    if (thisRule.noMotion && thisRule.noMotion >= 5)      state.noMotion = thisRule.noMotion as Integer;
    if (thisRule.noMotionEngaged && thisRule.noMotionEngaged >= 5)      state.noMotionEngaged = thisRule.noMotionEngaged as Integer
    if (thisRule.dimTimer && thisRule.dimTimer >= 5)      state.dimTimer = thisRule.dimTimer as Integer;
    if (thisRule.noMotionAsleep && thisRule.noMotionAsleep >= 5)        state.noMotionAsleep = thisRule.noMotionAsleep as Integer;
    updateTimers()
}

private turnSwitchesOnAndOff(thisRule)       {
    ifDebug("turnSwitchesOnAndOff")
//    if (thisRule && (thisRule.switchesOn || thisRule.switchesOff))
//        state.previousRuleNo = thisRule.ruleNo
    state.lastRule = (state.lastRule ? state.lastRule + ',' : '') + thisRule.ruleNo
    getChildDevice(getRoom()).updateLastRuleInd(state.lastRule)
//    def hT = getHubType()
    if (thisRule.switchesOn)    {
        if (thisRule.level?.startsWith('HL'))     {
            state.holiRuleNo = thisRule.ruleNo
            def i = thisRule.level.substring(2)
            state.holiHues = state.holidays[i].hues
            state.holiStyle = state.holidays[i].style
            state.holiSeconds = state.holidays[i].seconds
            state.holiLevel = state.holidays[i].level
            state.holiColorCount = state.holidays[i].count
            state.holiColorIndex = -1
            state.holiLastTW = [:]
            state.holidayLights = true
            holidayLights()
        }
        else        {
            def colorTemperature = null
            def level = null
            thisRule.switchesOn.each      {
//                if (it.currentSwitch != on)     {
                    it.on(); pauseIt()
//                }
                def itID = it.getId()
                if (thisRule.color && state.switchesHasColor[itID])     {
    //                if (it.currentColor != thisRule.hue)
                        it.setColor(thisRule.hue); pauseIt()
                }
                else if ((thisRule.colorTemperature || (thisRule.level == 'AL' && autoColorTemperature)) && state.switchesHasColorTemperature[itID])       {
                    if (!colorTemperature)      {
                        if (thisRule.level == 'AL' && autoColorTemperature)
                            colorTemperature = calculateLevelOrKelvin(true) as Integer
                        else
                            colorTemperature = thisRule.colorTemperature as Integer
                    }
    //                    if (it.currentColorTemperature != colorTemperature)
                    if (colorTemperature)
                        it.setColorTemperature(colorTemperature); pauseIt()
                }
                if (thisRule.level && state.switchesHasLevel[itID])     {
                    if (!level)     {
                        if (thisRule.level == 'AL')
                            level = calculateLevelOrKelvin(false) as Integer
                        else
                            level = thisRule.level as Integer
                    }
    //                if (it.currentLevel != level)
                    if (level)
                        it.setLevel(level); pauseIt()
                }
            }
        }
//        def child = getChildDevice(getRoom())
//        child.updateSwitchInd(1)
    }
//    if (thisRule.switchesOff && thisRule.switchesOff.currentSwitch.contains('on'))
//    if (thisRule.switchesOff)       {thisRule.switchesOff.off();
    if (thisRule.switchesOff)
        thisRule.switchesOff.each       {
            if (!state.switchesPreventToggle.contains(it.getId()))    {  it.off(); pauseIt()  }
        }
}

def holidayLights()    {
    if (!state.holidayLights)       return;
    def thisRule = getRule(state.holiRuleNo, null)
    if (state.holiStyle == 'RO')        {
        state.holiColorIndex = (state.holiColorIndex < (state.holiColorCount -1) ? state.holiColorIndex + 1 : 0)
        holidayLightsRotate(thisRule)
    }
    else if (state.holiStyle == 'TW')       {
        state.holiLastTW = (state.holiTW ?: [:])
        holidayLightsTwinkle(thisRule)
    }
    runIn(state.holiSeconds, holidayLights)
}

private holidayLightsRotate(thisRule)       {
    def cI = state.holiColorIndex
    thisRule.switchesOn.each    {
        def holiColor = state.holiHues."$cI"
        it.setColor(holiColor); pauseIt()
//        it.setLevel(state.holiLevel); pauseIt()
        cI = (cI < (state.holiColorCount -1) ? cI + 1 : 0)
    }
}

private holidayLightsTwinkle(thisRule)       {
    thisRule.switchesOn.setLevel(1)
    def noSwitches = thisRule.switchesOn.size()
    def noColors = state.holiHues.size()
    int cI
    def randomFound
    Random rand = new Random()
    state.holiTW = [:]
    for (def i = 0; i < noSwitches; i++)        {
        randomFound = false
        for (def j = 0; j < (noColors * 3); j++)     {
//            cI = Math.abs(new Random().nextInt() % noColors)
            cI = rand.nextInt(noColors)
            if (state.holiLastTW."$i" != cI)        {
                state.holiTW."$i" = cI
                randomFound = true
                break
            }
        }
        if (!randomFound)     state.holiTW."$i" = cI;
    }
    cI = 0
    thisRule.switchesOn.each    {
        def tw = state.holiTW."$cI"
        it.setColor(state.holiHues."$tw"); pauseIt()
//        it.setLevel(state.holiLevel); pauseIt()
        cI = cI + 1
    }
}

private runActions(thisRule)    {
    if (thisRule.actions)   {  thisRule.actions.each  {  location.helloHome?.execute(it); pauseIt()  }  }
}

private executePiston(thisRule)    {
    if (thisRule.piston)  { webCoRE_execute(thisRule.piston); pauseIt() }
}

private musicAction(thisRule)       {
    if (musicDevice && thisRule.musicAction)        {
        if (thisRule.musicAction == '1')    {
            musicDevice.play(); ; pauseIt()
// to unmute or not?            musicDevice.unmute()
        }
        else if (thisRule.musicAction == '2')   { musicDevice.pause(); pauseIt() }
    }
}

private setShade(thisRule)      {
    switch(thisRule.shade)      {
        case '0':       windowShades.open();  pauseIt();            break;
        case '1':       windowShades.close(); pauseIt();            break;
        case 'P1':      windowShades.presetPosition(1); pauseIt();  break;
        case 'P2':      windowShades.presetPosition(2); pauseIt();  break;
        case 'P3':      windowShades.presetPosition(3); pauseIt();  break;
        default:        break;
    }
}

/*
private calculateLevelOrKelvin(kelvin = false)       {
    ifDebug("calculateLevelOrKelvin")
    long timeNow = now()
    def dateNow = new Date(timeNow)

    def wTime, sTime

    if (wakeupTime && sleepTime)    {
        wTime = timeTodayAfter(sleepTime, wakeupTime, location.timeZone)
        sTime = timeToday(sleepTime, location.timeZone)
    }
    else        {
        wTime = timeTodayAfter("22:00", "7:00", location.timeZone)
        sTime = timeToday("22:00", location.timeZone)
    }
//    if (wTime > sTime)      return maxKelvin;

    if (simpleAL)
        return (timeOfDayIsBetween(sTime, wTime, dateNow, location.timeZone) ? (kelvin ? minKelvin : minLevel) :
                                                                               (kelvin ? maxKelvin : maxLevel))

    // dont recalculate kelvin or level if it was calculated in the last 10 mins
/*
    if (kelvin)     {
        if (state.kelvin && state.kelvin.kelvin && dateNow.getTime() < state.kelvin.time && wTime == state.kelvin.wTime && sTime == state.kelvin.sTime &&
           minKelvin == state.kelvin.minKelvin && maxKelvin == state.kelvin.maxKelvin)
           return state.kelvin.kelvin
        else
            state.kelvin = [time: (dateNow.getTime() + 600000L), wTime: wTime, sTime: sTime, minKelvin: minKelvin, maxKelvin: maxKelvin]
    }
    else if (state.level && state.level.level && dateNow.getTime() < state.level.time && wTime == state.level.wTime && sTime == state.level.sTime &&
        minLevel == state.level.minLevel && maxLevel == state.level.maxLevel)
        return state.level.level
    else
        state.level = [time: (dateNow.getTime() + 600000L), wTime: wTime, sTime: sTime, minLevel: minLevel, maxLevel: maxLevel]
//*\/

    def wTimeMinus1hr = new Date((wTime.getTime() - 3600000L))
    def sTimeMinus2hr = new Date((sTime.getTime() - 7200000L))

//    ifDebug("now: $dateNow | wTimeMinus1hr: $wTimeMinus1hr | sTimeMinus2hr: $sTimeMinus2hr")

    long maxMinDiff = (kelvin ? (maxKelvin - minKelvin) : (maxLevel - minLevel))

    if (!kelvin && fadeLevelOnlyBeforeSleep)     {
        if (timeOfDayIsBetween(sTimeMinus2hr, sTime, dateNow, location.timeZone))       {
            double cDD = ((sTime.getTime() - dateNow.getTime()) / (sTime.getTime() - sTimeMinus2hr.getTime()))
            cDD = cDD * maxMinDiff
            int cD = cDD + minLevel
            state.level << [level: cD]
            ifDebug("fadeLevelOnlyBeforeSleep: $cD")
            return cD
        }
    }

    if (timeOfDayIsBetween(sTimeMinus2hr, wTimeMinus1hr, dateNow, location.timeZone))     {
//        ifDebug("in sleep hours")
        if (kelvin)     {
            state.kelvin << [kelvin: minKelvin]
            return minKelvin
        }
        else        {
            state.level << [level: minLevel]
            return minLevel
        }
    }

    wTime = timeToday((wakeupTime && sleepTime ? wakeupTime : "7:00"), location.timeZone)
    wTimeMinus1hr = new Date((wTime.getTime() - 3600000L))

/*    def sunriseAndSunset = getSunriseAndSunset()
//    ifDebug("${new Date(sunriseAndSunset.sunrise.getTime())} | ${new Date(sunriseAndSunset.sunset.getTime())}")
    def sunriseTime = sunriseAndSunset.sunrise.getTime()
    def sunsetTime = sunriseAndSunset.sunset.getTime()
    long sunDiff = ((sunsetTime - sunriseTime) / 2L)
    def d = new Date(sunriseTime + sunDiff) //*\/

//    ifDebug("now: $dateNow | wTimeMinus1hr: $wTimeMinus1hr | sTimeMinus2hr: $sTimeMinus2hr")

    long sunDiff = (sTimeMinus2hr.getTime() - wTimeMinus1hr.getTime()) / 2L
    def d = new Date(wTimeMinus1hr.getTime() + sunDiff)

    def peakMinus1hr = new Date((d.getTime() - 3600000L))
    def peakPlus1hr = new Date((d.getTime() + 3600000L))

//    ifDebug("now: $dateNow | peakMinus1hr: $peakMinus1hr | peakPlus1hr: $peakPlus1hr")

    if (timeOfDayIsBetween(peakMinus1hr, peakPlus1hr, dateNow, location.timeZone))     {
        ifDebug("in peak hours")
        if (kelvin)     {
            state.kelvin << [kelvin: maxKelvin]
            return maxKelvin
        }
        else        {
            state.level << [level: maxLevel]
            return maxLevel
        }
    }

    double cDD
    int cD
    long timeStart
    long timeEnd
/*
    def z1 = dateNow.format("HH", location.timeZone) as Integer
    def z2 = dateNow.format("mm", location.timeZone) as Integer
    long timeIs = (z1 * 3600L) + (z2 * 60L)
//*\/
    long timeIs = dateNow.getTime()

    if (dateNow < peakMinus1hr)     {
/*
        z1 = wTimeMinus1hr.format("HH", location.timeZone) as Integer
        z2 = wTimeMinus1hr.format("mm", location.timeZone) as Integer
        timeStart = (z1 * 3600L) + (z2 * 60L)
//*\/
        timeStart = wTimeMinus1hr.getTime()
/*
        z1 = peakMinus1hr.format("HH", location.timeZone) as Integer
        z2 = peakMinus1hr.format("mm", location.timeZone) as Integer
        timeEnd = (z1 * 3600L) + (z2 * 60L)
//*\/
        timeEnd =

        cDD = (timeIs - timeStart) / (timeEnd - timeStart)
        cDD = cDD * maxMinDiff
    }
    else        {
        z1 = peakPlus1hr.format("HH", location.timeZone) as Integer
        z2 = peakPlus1hr.format("mm", location.timeZone) as Integer
        timeStart = (z1 * 3600L) + (z2 * 60L)

        z1 = sTimeMinus2hr.format("HH", location.timeZone) as Integer
        z2 = sTimeMinus2hr.format("mm", location.timeZone) as Integer
        timeEnd = (z1 * 3600L) + (z2 * 60L)

        cDD = (timeIs - timeStart) / (timeEnd - timeStart)
        cDD = cDD * maxMinDiff
        cDD = maxMinDiff - cDD
    }

//    ifDebug("timeStart: $timeStart | timeEnd: $timeEnd | timeIs: $timeIs")

    if (kelvin)     {
        cD = cDD + minKelvin
        state.kelvin << [kelvin: cD]
    }
    else        {
        cD = cDD + minLevel
        state.level << [level: cD]
    }
    ifDebug("circadian Daylight ${(kelvin ? 'kelvin' : 'level')}: $cD")
    return cD
}
*/

/*
private calculateLevelOrKelvin(kelvin = false)       {
    ifDebug("calculateLevelOrKelvin")
    long timeNow = now()
    def dateNow = new Date(timeNow)
    def dateNowISO = dateNow.format("yyyy-MM-dd'T'HH:mm:ssZ", location.timeZone)

    def useInput = (wakeupTime && sleepTime ? true : false)

    def wTime, sTime

    wTime = timeToday((useInput ? wakeupTime : "7:00"), location.timeZone)
    sTime = timeToday((useInput ? sleepTime : "23:00"), location.timeZone)

    if (dateNow > wTime && dateNow > sTime)     {
        wTime = timeTodayAfter(dateNow, wTime.format("yyyy-MM-dd'T'HH:mm:ssZ", location.timeZone), location.timeZone)
        sTime = timeTodayAfter(dateNow, sTime.format("yyyy-MM-dd'T'HH:mm:ssZ", location.timeZone), location.timeZone)
    }

    long maxMinDiff = (kelvin ? (maxKelvin - minKelvin) : (maxLevel - minLevel))

    if (fadeLevelWake)      {
        def wTimeBefore = new Date((wTime.getTime() - (fadeWakeBefore * 3600000L)))
        def wTimeAfter = new Date((wTime.getTime() + (fadeWakeAfter * 3600000L)))

        if (timeOfDayIsBetween(wTimeBefore, wTimeAfter, dateNow, location.timeZone))     {
            double cDD = ((dateNow.getTime() - wTimeBefore.getTime()) / (wTimeAfter.getTime() - wTimeBefore.getTime()))
            cDD = cDD * maxMinDiff
            int cD = cDD + (kelvin ? minKelvin : minLevel)
            ifDebug("calculateLevelOrKelvin: kelvin: $kelvin | value: $cD")
            return cD
        }
    }

    if (fadeLevelSleep)     {
        def sTimeBefore = new Date((sTime.getTime() - (fadeSleepBefore * 3600000L)))
        def sTimeAfter = new Date((sTime.getTime() + (fadeSleepAfter * 3600000L)))

        if (timeOfDayIsBetween(sTimeBefore, sTimeAfter, dateNow, location.timeZone))     {
            double cDD = ((sTimeAfter.getTime() - dateNow.getTime()) / (sTimeAfter.getTime() - sTimeBefore.getTime()))
            cDD = cDD * maxMinDiff
            int cD = cDD + (kelvin ? minKelvin : minLevel)
            ifDebug("calculateLevelOrKelvin: kelvin: $kelvin | value: $cD")
            return cD
        }
    }

    if (timeOfDayIsBetween(wTime, sTime, dateNow, location.timeZone))       {
        ifDebug("calculateLevelOrKelvin: kelvin: $kelvin | value: ${(kelvin ? maxKelvin : maxLevel)}")
        return ((kelvin ? maxKelvin : maxLevel))
    }
    else        {
        ifDebug("calculateLevelOrKelvin: kelvin: $kelvin | value: ${(kelvin ? minKelvin : minLevel)}")
        return ((kelvin ? minKelvin : minLevel))
    }
}
*/

private calculateLevelOrKelvin(kelvin = false)       {
    ifDebug("calculateLevelOrKelvin")
    if (kelvin)
        return calculateLK(minKelvin, maxKelvin, fadeCTWake, fadeKWakeBefore, fadeKWakeAfter, fadeCTSleep, fadeKSleepBefore, fadeKSleepAfter)
    else
        return calculateLK(minLevel, maxLevel, fadeLevelWake, fadeWakeBefore, fadeWakeAfter, fadeLevelSleep, fadeSleepBefore, fadeSleepAfter)

}

private calculateLK(min, max, fadeW, fadeWB, fadeWA, fadeS, fadeSB, fadeSA)       {
    long timeNow = now()
    def dateNow = new Date(timeNow)

    def useInput = (wakeupTime && sleepTime ? true : false)

    def wTime, sTime

    wTime = timeToday((useInput ? wakeupTime : "7:00"), location.timeZone)
    sTime = timeToday((useInput ? sleepTime : "23:00"), location.timeZone)

//    ifDebug("$dateNow | $wTime | $sTime | $location.timeZone")
    if (dateNow > wTime && dateNow > sTime)     {
        // hubitat does not support timeTodayAfter() method 2018-04-07
        wTime = timeTodayA(dateNow, wTime, location.timeZone)
        sTime = timeTodayA(dateNow, sTime, location.timeZone)
    }

    long maxMinDiff = max - min

    if (fadeW)      {
        def wTimeBefore = new Date((wTime.getTime() - (fadeWB * 3600000L)))
        def wTimeAfter = new Date((wTime.getTime() + (fadeWA * 3600000L)))

        if (timeOfDayIsB(wTimeBefore, wTimeAfter, dateNow, location.timeZone))     {
            double cDD = ((dateNow.getTime() - wTimeBefore.getTime()) / (wTimeAfter.getTime() - wTimeBefore.getTime()))
            cDD = cDD * maxMinDiff
            int cD = cDD + min
            ifDebug("calculateLK: value: $cD")
            return cD
        }
    }

    if (fadeS)     {
        def sTimeBefore = new Date((sTime.getTime() - (fadeSB * 3600000L)))
        def sTimeAfter = new Date((sTime.getTime() + (fadeSA * 3600000L)))

        if (timeOfDayIsB(sTimeBefore, sTimeAfter, dateNow, location.timeZone))     {
            double cDD = ((sTimeAfter.getTime() - dateNow.getTime()) / (sTimeAfter.getTime() - sTimeBefore.getTime()))
            cDD = cDD * maxMinDiff
            int cD = cDD + min
            ifDebug("calculateLK: value: $cD")
            return cD
        }
    }

    if (timeOfDayIsB(wTime, sTime, dateNow, location.timeZone))       {
        if (fadeW)      {
            ifDebug("calculateLK: $max")
            return max
        }
        else
            return null
    }
    else        {
        if (fadeS)      {
            ifDebug("calculateLK: $min")
            return min
        }
        else
            return null
    }
}

// since hubitat does not support timeTodayAfter(...) 2018-04-08
private timeTodayA(whichDate, thisDate, timeZone)      {
    def newDate
    if (thisDate.before(whichDate))     {
        int dura = (int) ((whichDate.getTime() - thisDate.getTime()) / 86400000L)
//        ifDebug("$whichDate | $thisDate | $duration")
        use (TimeCategory)   {
//            newDate = thisDate.plus(duration.days + 1)
            newDate = thisDate + dura.day + 1.day
        }
//        ifDebug("$newDate")
    }
    else
        newDate = thisDate
    return newDate
}

/*
private calculateLightLevel()       {
    ifDebug("calculateLightLevel")
    if (autoColorTemperature)   return calculateLevelOrKelvin(false);

    long timeNow = now()
    def d = new Date(timeNow)
    def z11 = d.format("HH", location.timeZone) as Integer
    def z12 = d.format("mm", location.timeZone) as Integer
    long timeIs = (z11 * 3600L) + (z12 * 60L)
    def sunriseAndSunset = getSunriseAndSunset()
//    ifDebug("${new Date(sunriseAndSunset.sunrise.getTime())} | ${new Date(sunriseAndSunset.sunset.getTime())}")
    def sunriseTime = sunriseAndSunset.sunrise.getTime()
    def sunsetTime = sunriseAndSunset.sunset.getTime()
    long sunDiff = ((sunsetTime - sunriseTime) / 2L)
    d = new Date(sunriseTime + sunDiff)
//    ifDebug("peakTime: $d")
    def z21 = d.format("HH", location.timeZone) as Integer
    def z22 = d.format("mm", location.timeZone) as Integer
    long time1500 = (z21 * 3600L) + (z22 * 60L)
    d = timeToday("23:59", location.timeZone)
    def z31 = d.format("HH", location.timeZone) as Integer
    def z32 = d.format("mm", location.timeZone) as Integer
    long time2359 = (z31 * 3600L) + (z32 * 60L)
//    ifDebug("$z11, $z12, $z21, $z22, $z31, $z32")

    long levelDiff = (maxLevel - minLevel)
    double lLD
    int lD
    if (timeIs < time1500)  {
        lLD = timeIs / time1500
        lLD = lLD * levelDiff
    }
    else    {
        lLD = timeIs / time2359
        lLD = lLD * levelDiff
        lLD = levelDiff - lLD
    }
    lD = lLD + minLevel
    ifDebug("Daylight level: $lD")
    return lD
}
*/

private whichSwitchesAreOn(returnAllSwitches = false)   {
//    ifDebug("whichSwitchesAreOn")
    def switchesThatAreOn = []
    def switchesThatAreOnID = []
    for (def i = 1; i < 11; i++)      {
        def ruleNo = String.valueOf(i)
        def thisRule = getNextRule(ruleNo, null)
        if (thisRule.ruleNo == 'EOR')     break;
        i = thisRule.ruleNo as Integer
        thisRule.switchesOn.each        {
            def itID = it.getId()
// TODO temporarily added toTurnOff to remove light on check because of ongoing issues with ST firmware release 0.20.12
            if ((returnAllSwitches || it.currentSwitch == 'on') && !switchesThatAreOnID.contains(itID))    {
                switchesThatAreOn << it
                switchesThatAreOnID << itID
            }
        }
    }
    ifDebug("whichSwitchesAreOn: $switchesThatAreOn")
    return switchesThatAreOn
}

def dimLights()     {
    ifDebug("dimLights")
    state.preDimLevel = [:]
    if (!state.dimTimer || (!state.dimByLevel && !state.dimToLevel))       return;
    def switchesThatAreOn = whichSwitchesAreOn()
    if (switchesThatAreOn && state.dimByLevel)
        switchesThatAreOn.each      {
            if (it.currentSwitch == 'on')      {
                if (it.hasCommand("setLevel"))     {
                    def currentLevel = it.currentLevel
                    state.preDimLevel << [(it.getId()):currentLevel]
                    it.setLevel((currentLevel > state.dimByLevel ? currentLevel - state.dimByLevel : 1)); pauseIt()
                }
            }
        }
    else        {
        int lux
        if (luxCheckingDimTo && luxSensor)      lux = getIntfromStr((String) luxSensor.currentIlluminance);
        if (!luxCheckingDimTo || lux <= luxCheckingDimTo)        {
            def allSwitches = whichSwitchesAreOn(true)
            if (allSwitches && state.dimToLevel)
                allSwitches.each      {
                    if (it.hasCommand("setLevel"))     {
                        it.on(); pauseIt()
                        state.preDimLevel << [(it.getId()):it.currentLevel]
                        it.setLevel(state.dimToLevel); pauseIt()
                    }
                }
        }
    }
}

private unDimLights(roomState)       {
    ifDebug("unDimLights")
//    ifDebug("state.preDimLevel: $state.preDimLevel")
    if (!state.dimTimer || (!state.dimByLevel && !state.dimToLevel) || !state.preDimLevel)      return;
    if (!notRestoreLL || roomState != vacant)      {
        def switchesThatAreOn = whichSwitchesAreOn()
        switchesThatAreOn.each      {
            if (it.currentSwitch == 'on' && it.hasCommand("setLevel"))
                def newLevel = state.preDimLevel[(it.getId())]
                ifDebug("newLevel: it: $it | $newLevel")
                if (newLevel > 0)   { it.setLevel(newLevel); pauseIt() }
            }
    }
    updateChildTimer(0)
    state.preDimLevel = [:]
}

def switches2Off()       {
    ifDebug("switches2Off")
    state.holidayLights = false
    def switchesThatAreOn = whichSwitchesAreOn(true)
    switchesThatAreOn.each  {
        it.off(); pauseIt();
    }
}

private previousStateStack(previousState)    {
    ifDebug("previousStateStack")
    def i
    def timeIs = now()
    def factor = (state.busyCheck ?: 10)
    def removeHowOld = (state.noMotion ? (((state.noMotion as Integer) + (state.dimTimer as Integer)) * factor) : (180 * factor))
    def howMany
    int gapBetween

    turnOffIsBusy()
    if (state.stateStack)
        for (i = 9; i > 0; i--)     {
            def s = String.valueOf(i)
            if (state.stateStack[s])        {
                gapBetween = ((timeIs - (state.stateStack[s])['date']) / 1000)
                if (gapBetween > removeHowOld)
                    state.stateStack.remove(s)
                else
                    break
            }
        }
    if (state.stateStack)
        for (i = 9; i > 0; i--)     {
            if (state.stateStack[String.valueOf(i-1)])
                state.stateStack[String.valueOf(i)] = state.stateStack[String.valueOf(i-1)]
        }
    else
        state.stateStack = [:]
    state.stateStack << ['0':previousState]

    if (state.busyCheck)      {
        howMany = 0
        gapBetween = 0
        for (i = 9; i > 0; i--)     {
            def s = String.valueOf(i)
            def sM = String.valueOf(i-1)
            if (state.stateStack[s] && ['occupied', 'checking', 'vacant'].contains((state.stateStack[s])['state']) &&
                                       ['occupied', 'checking', 'vacant'].contains((state.stateStack[sM])['state']))         {
                howMany++
                gapBetween += (((state.stateStack[sM])['date'] - (state.stateStack[s])['date']) / 1000)
            }
        }
        ifDebug("howMany: $howMany | gapBetween: $gapBetween | busyCheck: $state.busyCheck | isBusy: $state.isBusy | newState: $previousState")
        if (howMany >= state.busyCheck)   {
            ifDebug("busy on")
            state.isBusy = true
            state.stateStack = [:]
            runIn(removeHowOld, turnOffIsBusy)
        }
    }
}

def turnOffIsBusy()     {
    unschedule('turnOffIsBusy')
    state.isBusy = false
}

def spawnChildDevice(roomName)	{
    ifDebug("spawnChildDevice")
	app.updateLabel(app.label)
	if (!childCreated())
		def child = addChildDevice("bangali", "rooms occupancy", getRoom(), null, [name: getRoom(), label: roomName, completedSetup: true])
}

private childCreated()		{
//    ifDebug("childCreated")
//	if (getChildDevice(getRoom()))     return true;
//	else                               return false;
    return (getChildDevice(getRoom()) ? true : false)
}

private getRoom()	{  return "rm_${app.id}"  }

def uninstalled() {
    ifDebug("uninstalled")
    parent.unsubscribeChild(app.id)
	getChildDevices(true).each     {  deleteChildDevice(it.deviceNetworkId)  }
}

def childUninstalled()	{  ifDebug("uninstalled room ${app.label}")  }

private returnHueAndSaturation(setColorTo)        {
    def rHAS = null
    if (setColorTo)     {
        def hueColor = 0
        def saturation = 100
        switch(setColorTo)       {
            case "White":       hueColor = 52;  saturation = 19;    break
            case "Daylight":    hueColor = 53;  saturation = 91;    break
            case "Soft White":  hueColor = 23;  saturation = 56;    break
            case "Warm White":  hueColor = 20;  saturation = 80;    break
            case "Blue":        hueColor = 66;                      break
            case "Green":       hueColor = 39;                      break
            case "Yellow":      hueColor = 25;                      break
            case "Orange":      hueColor = 10;                      break
            case "Purple":      hueColor = 75;                      break
            case "Pink":        hueColor = 83;                      break
            case "Red":         hueColor = 0;                       break
        }
        rHAS = [hue: hueColor, saturation: saturation]
    }
    return rHAS
}

private convertRGBToHueSaturation(setColorTo)      {
    def str = setColorTo.replaceAll("\\s","").toLowerCase()
	def rgb = (colorsRGB[str] ?: colorsRGB['white'])
    float r = rgb[0] / 255
    float g = rgb[1] / 255
    float b = rgb[2] / 255
    float max = Math.max(Math.max(r, g), b)
    float min = Math.min(Math.min(r, g), b)
    float h, s, l = (max + min) / 2

    if (max == min)
        h = s = 0 // achromatic
    else    {
        float d = max - min
        s = l > 0.5 ? d / (2 - max - min) : d / (max + min)
        switch (max)    {
            case r:    h = (g - b) / d + (g < b ? 6 : 0);  break
            case g:    h = (b - r) / d + 2;                break
            case b:    h = (r - g) / d + 4;                break
        }
        h /= 6
    }
    return [hue: Math.round(h * 100), saturation: Math.round(s * 100), level: Math.round(l * 100)]
}

private unscheduleAll(classNameCalledFrom)		{
    ifDebug("${app.label} unschedule calling class: $classNameCalledFrom")
    unschedule('roomVacant')
//    unschedule('setToEngaged')
    unschedule('powerStaysBelowEngaged')
    unschedule('powerStaysBelowAsleep')
    unschedule('powerStaysBelowLocked')
    updateChildTimer(0)
//    unschedule("dimLights")
//    unschedule("switches2Off")
}

private updateChildTimer(timer = 0)     {
    ifDebug("updateChildTimer")
//	state.timer = (timer ? timer + 5 : 0)
    state.timer = timer as Integer
	timerNext()
}

/*
private updateAsleepChildTimer(timer = 0)   {
    ifDebug("updateChildTimer")
    state.timer = timer as Integer
	timerNext()
}
*/

def timerNext()		{
//    state.timer = (state.timer >= 5 ? state.timer - 5 : 0)
    int timerUpdate = (state.timer > 600 ? 300 : (state.timer > 60 ? 60 : (state.timer < 5 ? state.timer : 5)))
    def timerInd = (state.timer > 3600 ? (state.timer / 3600f).round(1) + 'h' : (state.timer > 60 ? (state.timer / 60f).round(1) + 'm' : state.timer + 's')).replace(".0","")
    getChildDevice(getRoom()).updateTimer(timerInd)
    state.timer = state.timer - timerUpdate
//	(state.timer > 0 ? runIn(timerUpdate, timerNext) : unschedule('timerNext'))
    (state.timer > 0 ? runIn(timerUpdate, timerNext) : unschedule('timerNext'))
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

def scheduleFromToTimes(evt = null)       {
    if (!state.rules || !state.timeCheck)       return;
    ifDebug("scheduleFromToTimes")
//    def sunriseFromSubscribed = false
//    def sunriseToSubscribed = false
//    def sunsetFromSubscribed = false
//    def sunsetToSubscribed = false
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
/*    def i = 1
    for (; i < 11; i++)     {
        def ruleNo = String.valueOf(i)
        def thisRule = getNextRule(ruleNo, null, true)
        if (thisRule.ruleNo == 'EOR')     break;
        i = thisRule.ruleNo as Integer
//        def thisRule = getRule(ruleNo)
//        if (!thisRule || thisRule.disabled)      continue;
        if (!thisRule.fromTimeType || !thisRule.toTimeType)     continue;
        if (thisRule.fromTimeType == timeSunrise() && !sunriseFromSubscribed)   {
            subscribe(location, "sunrise", timeFromHandler)
            sunriseFromSubscribed = true
        }
        else    {
            if (thisRule.fromTimeType == timeSunset() && !sunsetFromSubscribed)   {
                subscribe(location, "sunset", timeFromHandler)
                sunsetFromSubscribed = true
            }
        }
        if (thisRule.toTimeType == timeSunrise() && !sunriseToSubscribed)   {
            subscribe(location, "sunrise", timeToHandler)
            sunriseToSubscribed = true
        }
        else    {
            if (thisRule.toTimeType == timeSunset() && !sunsetToSubscribed)   {
                subscribe(location, "sunset", timeToHandler)
                sunsetToSubscribed = true
            }
        }
    }*/
    scheduleFromTime()
    scheduleToTime()
}

private scheduleFromTime()      {
    ifDebug("scheduleFromTime")
    if (!state.rules || !state.timeCheck)       return;
    def nowTime	= now()
    def nowDate = new Date(nowTime)
    def sunriseAndSunset = getSunriseAndSunset()
    def sunriseTime = new Date(sunriseAndSunset.sunrise.getTime())
    def sunsetTime = new Date(sunriseAndSunset.sunset.getTime())
    def nextTimeType = null
    def nextTime = null
    def sunriseTimeWithOff, sunsetTimeWithOff
    def i = 1
    for (; i < 11; i++)     {
        def ruleNo = String.valueOf(i)
        def thisRule = getNextRule(ruleNo, null, true)
        if (thisRule.ruleNo == 'EOR')     break;
        i = thisRule.ruleNo as Integer
        if (thisRule.fromDate && thisRule.toDate)       {
//            ifDebug("scheduleFromTime: thisRule.fromDate: $thisRule.fromDate | thisRule.toDate: $thisRule.toDate")
            def fTime = new Date().parse("yyyy-MM-dd'T'HH:mm:ssZ", thisRule.fromDate)
            def tTime = new Date().parse("yyyy-MM-dd'T'HH:mm:ssZ", thisRule.toDate)
//            ifDebug("scheduleFromTime: nowDate: $nowDate | nextTime: $nextTime | fTime: $fTime | tTime: $tTime")
            if (nowDate > tTime)        continue;
//            if ((!nextTime && nowDate < fTime) || (nextTime && timeOfDayIsBetween(nowDate, nextTime, fTime, location.timeZone)))   {
            if ((!nextTime && nowDate >= fTime && nowDate <= tTime) || (nextTime && fTime >= nowDate && fTime < nextTime))   {
                nextTime = fTime
                nextTimeType = timeTime()
            }
        }
        if ((!thisRule.fromTimeType || (thisRule.fromTimeType == timeTime() && !thisRule.fromTime)) ||
            (!thisRule.toTimeType || (thisRule.toTimeType == timeTime() && !thisRule.toTime)))
            continue
//        def fromTime = timeTodayAfter(nowDate, thisRule.fromTime, location.timeZone)
        if (thisRule.fromTimeType == timeSunrise())
            sunriseTimeWithOff = (thisRule.fromTimeOffset ? new Date(sunriseTime.getTime() + (thisRule.fromTimeOffset * 60000L)) : sunriseTime)
        else if (thisRule.fromTimeType == timeSunset())
            sunsetTimeWithOff = (thisRule.fromTimeOffset ? new Date(sunsetTime.getTime() + (thisRule.fromTimeOffset * 60000L)) : sunsetTime)
        def fTime = ( thisRule.fromTimeType == timeSunrise() ? sunriseTimeWithOff : ( thisRule.fromTimeType == timeSunset() ? sunsetTimeWithOff : timeToday(thisRule.fromTime, location.timeZone)))
//        ifDebug("nowDate: $nowDate | nextTime: $nextTime | fTime: $fTime")
        if (!nextTime || timeOfDayIsB(nowDate, nextTime, fTime, location.timeZone))      {
            nextTimeType = thisRule.fromTimeType
            nextTime = fTime
        }
    }
    if (nextTime)   {
        state.fTime = nextTime
        updateTimeFromToInd()
//        if (nextTimeType == timeTime())     schedule(nextTime, timeFromHandler);
        schedule(nextTime, timeFromHandler);
    }
}

private scheduleToTime()      {
    ifDebug("scheduleToTime")
    if (!state.rules || !state.timeCheck)       return;
    def nowTime	= now()
    def nowDate = new Date(nowTime)
    def sunriseAndSunset = getSunriseAndSunset()
    def sunriseTime = new Date(sunriseAndSunset.sunrise.getTime())
    def sunsetTime = new Date(sunriseAndSunset.sunset.getTime())
    def nextTimeType = null
    def nextTime = null
    def sunriseTimeWithOff, sunsetTimeWithOff
    def i = 1
    for (; i < 11; i++)     {
        def ruleNo = String.valueOf(i)
        def thisRule = getNextRule(ruleNo, null, true)
        if (thisRule.ruleNo == 'EOR')     break;
        i = thisRule.ruleNo as Integer
        if (thisRule.fromDate && thisRule.toDate)       {
//            ifDebug("scheduleToTime: thisRule.fromDate: $thisRule.fromDate | thisRule.toDate: $thisRule.toDate")
            def fTime = new Date().parse("yyyy-MM-dd'T'HH:mm:ssZ", thisRule.fromDate)
            def tTime = new Date().parse("yyyy-MM-dd'T'HH:mm:ssZ", thisRule.toDate)
//            ifDebug("scheduleToTime: nowDate: $nowDate | nextTime: $nextTime | fTime: $fTime | tTime: $tTime")
            if (nowDate > tTime)        continue;
//            if ((!nextTime && nowDate < tTime) || (nextTime && timeOfDayIsBetween(nowDate, nextTime, tTime, location.timeZone)))   {
            if ((!nextTime && nowDate >= fTime && nowDate <= tTime) || (nextTime && tTime >= nowDate && tTime < nextTime))   {
                nextTime = tTime
                nextTimeType = timeTime()
            }
        }
        if ((!thisRule.fromTimeType || (thisRule.fromTimeType == timeTime() && !thisRule.fromTime)) ||
            (!thisRule.toTimeType || (thisRule.toTimeType == timeTime() && !thisRule.toTime)))
            continue
//        def toTime = timeTodayAfter(nowDate, thisRule.toTime, location.timeZone)
        if (thisRule.toTimeType == timeSunrise())
            sunriseTimeWithOff = (thisRule.toTimeOffset ? new Date(sunriseTime.getTime() + (thisRule.toTimeOffset * 60000L)) : sunriseTime)
        else if (thisRule.toTimeType == timeSunset())
            sunsetTimeWithOff = (thisRule.toTimeOffset ? new Date(sunsetTime.getTime() + (thisRule.toTimeOffset * 60000L)) : sunsetTime)
        def tTime = ( thisRule.toTimeType == timeSunrise() ? sunriseTimeWithOff : ( thisRule.toTimeType == timeSunset() ? sunsetTimeWithOff : timeToday(thisRule.toTime, location.timeZone)))
//        ifDebug("nowDate: $nowDate | nextTime: $nextTime | tTime: $tTime")
        if (!nextTime || timeOfDayIsB(nowDate, nextTime, tTime, location.timeZone))      {
            nextTimeType = thisRule.toTimeType
            nextTime = tTime
        }
    }
    if (nextTime)   {
        state.tTime = nextTime
        updateTimeFromToInd()
//        if (nextTimeType == timeTime())     schedule(nextTime, timeToHandler);
        schedule(nextTime, timeToHandler);
    }
}

def timeFromHandler(evt = null)       {
    ifDebug("timeFromHandler")
    if (checkPauseModesAndDoW())    switchesOnOrOff();
//    def child = getChildDevice(getRoom())
//    def roomState = child.getRoomState()
//    if (['engaged', 'occupied', 'asleep', 'vacant'].contains(roomState))
//    switchesOnOrOff()
    scheduleFromToTimes()
}

def timeToHandler(evt = null)       {
    ifDebug("timeToHandler")
    if (checkPauseModesAndDoW())    switchesOnOrOff();
//    def child = getChildDevice(getRoom())
//    def roomState = child.getRoomState()
//    if (['engaged', 'occupied', 'asleep', 'vacant'].contains(roomState))
//    switchesOnOrOff()
    scheduleFromToTimes()
}

private updateTimeFromToInd()     {
    if (state.fTime && state.tTime)     {
        state.timeFromTo =  format24hrTime(state.fTime) + " "
        state.timeFromTo = state.timeFromTo + format24hrTime(state.tTime)
        def child = getChildDevice(getRoom())
        child.updateTimeInd(state.timeFromTo)
    }
}

private updateTimers()      {
    def child = getChildDevice(getRoom())
    child.updateTimersInd(state.noMotion, state.dimTimer, state.noMotionEngaged, state.noMotionAsleep)
}

private format24hrTime(timeToFormat = new Date(now()), format = "HH:mm")		{
    return timeToFormat.format("HH:mm", location.timeZone)
}

def getAdjMotionSensors()  {
    ifDebug("getAdjMotionSensors")
/*    if (motionSensors)   {
        def motionSensorsList = []
        motionSensors.each   {  motionSensorsList << it }
        return motionSensorsList
    }
    else
        return null*/
    return motionsensors
}

def getAdjRoomDetails()  {
    def adjRoomDetails = ['childid':app.id, 'adjrooms':adjRooms]
    ifDebug("getAdjRoomDetails: ${adjRoomDetails['childid']} | adjrooms: ${adjRoomDetails['adjrooms']}")
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

def getAdjRoomsSetting()  {
    ifDebug("getAdjRoomsSetting")
    return adjRooms
}

def getLastStateChild()     {
    def addRoom = state.previousState
    addRoom << ['room':app.label]
    return addRoom
}

def getChildRoomDevice()    {  return getChildDevice(getRoom())  }

def getChildRoomThermostat()    {  return (useThermostat && roomThermostat ? [name: app.label, thermostat: roomThermostat] : null)  }

def setupAlarmP(alarmDisabled, alarmTime, alarmVolume, alarmSound, alarmRepeat, alarmDayOfWeek)     {
    if (alarmDisabled || !alarmTime || !musicDevice)    {
        unschedule('ringAlarm')
        return
    }
    setSoundURI(alarmSound)
    state.alarm << [volume:alarmVolume]
    state.alarm << [repeat:alarmRepeat]
    if (alarmDayOfWeek)      {
        state.alarm << [dayOfWeek:[]]
        switch(alarmDayOfWeek)       {
            case '1':   case '2':   case '3':   case '4':   case '5':   case '6':   case '7':
                        state.alarm << [dayOfWeek:(dayOfWeek)];                 break;
            case '8':   [1,2,3,4,5].each    { state.alarm.dayOfWeek << it };    break;
            case '9':   [6,7].each          { state.alarm.dayOfWeek << it };    break;
            default:    state.alarm.dayOfWeek = null;                           break;
        }
    }
    else
        state.alarm.dayOfWeek = null
    schedule(alarmTime, ringAlarm)
}

def setSoundURI(alarmSound)		{
	switch (alarmSound) {
		case '1':	state.alarm = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/bell1.mp3", duration: "10"];		break
		case '2':	state.alarm = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/bell2.mp3", duration: "10"];		break
		case '3':	state.alarm = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/dogs.mp3", duration: "10"];		break
		case '4':	state.alarm = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/alarm.mp3", duration: "17"];		break
		case '5':	state.alarm = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/piano2.mp3", duration: "10"];		break
		case '6':	state.alarm = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/lightsaber.mp3", duration: "10"];	break
		default:	state.alarm = [uri: "", duration: "0"];																break
	}
}

def ringAlarm(turnOff = false)		{
	ifDebug("ringAlarm")
    if (turnOff)    {
        ifDebug("ringAlarm: turn alarm off")
        musicDevice.nextTrack()
        state.alarmRepeat = (state.alarm.repeat ?: 999) + 1
        unschedule('repeatAlarm')
        def child = getChildDevice(getRoom())
        child.alarmOff(true)
        return
    }
    if (!musicDevice)        return;
	if (state.alarm.dayOfWeek)	{
		def thisDay = (new Date(now())).getDay()
	 	if (!state.alarm.dayOfWeek.contains(thisDay))		return;
	}
	state.alarmRepeat = 0
    repeatAlarm()
}

def repeatAlarm()	{
    def child = getChildDevice(getRoom())
    child.alarmOn()
	musicDevice.playTrackAndResume(state.alarm.uri, state.alarm.duration, state.alarm.volume)
	state.alarmRepeat = state.alarmRepeat + 1
	if (state.alarmRepeat <= state.alarm.repeat)    {
        def secs = state.alarm.duration as Integer
        runIn((secs + 5), repeatAlarm)
    }
    else
        child.alarmOff(true)
}

def checkAndTurnOnOffSwitchesC()      {
    if (onlyOnStateChange)      return false;
    if (awayModes && awayModes.contains(location.currentMode))    return false;
    if (!checkPauseModesAndDoW())       return false;
    switchesOnOrOff(true)
//    if (engagedSwitch.currentSwitch.contains(on))       engagedSwitchOnEventHandler(null);
    return true
}

private checkPauseModesAndDoW()  {
    if (pauseModes && pauseModes.contains(location.currentMode))    return false;
    if (state.dayOfWeek && !(checkRunDay()))    return false;
    return true
}

private checkRunDay(dayOfWeek = null)   {
    def thisDay = (new Date(now())).getDay()
    return (dayOfWeek ? dayOfWeek : state.dayOfWeek).contains(thisDay)
//    if (dayOfWeek)  return (dayOfWeek.contains(thisDay))
//    else            return (state.dayOfWeek.contains(thisDay))
//    return ("${dayOfWeek ?: state.dayOfWeek}".contains(thisDay))
}

def windowShadeEventHandler(evt)		{
    ifDebug("windowShadeEventHandler")
	def child = getChildDevice(getRoom())
    def wSSInd = -1
    if (windowShades)       {
        def wSS = windowShades.currentWindowShade
        if (wSS.contains('partially'))  wSSInd = 'Partially\nopen'
        else if (wSS.contains(open))    wSSInd = 'Open'
        else                            wSSInd = 'Closed'
    }
    child.updateWSSInd(wSSInd)
}

// private lastMotionActive()      {  return '1'  }
// private lastMotionInactive()    {  return '2'  }

private timeSunrise()   {  return '1'  }
private timeSunset()    {  return '2'  }
private timeTime()      {  return '3'  }

private presenceActionArrival()       {  return (presenceAction == '1' || presenceAction == '3')  }
private presenceActionDeparture()     {  return (presenceAction == '2' || presenceAction == '3')  }

private ifDebug(msg = null, level = null)     {  if (msg && (isDebug() || level))  log."${level ?: 'debug'}" 'rooms child ' + msg  }

private	hasOccupiedDevice()		{ return (motionSensors || occupiedButton || occSwitches)}

// only called from device handler
def turnSwitchesAllOnOrOff(turnOn)     {
    ifDebug("turnSwitchesAllOnOrOff")
    def switches = getAllSwitches()
    if (switches)       {
        def action = (turnOn ? on : off)
        switches.each   {  if (it.currentSwitch != action)   { it."$action"(); pauseIt() }  }
    }
}

def roomDeviceSwitchOnP()    {  return roomDeviceSwitchOn  }

private getAllSwitches()    {
    def switches = []
    def switchesID = []
    def i = 1
    for (; i < 11; i++)     {
        def ruleNo = String.valueOf(i)
        def thisRule = getNextRule(ruleNo, null)
        if (thisRule.ruleNo == 'EOR')     break;
        i = thisRule.ruleNo as Integer
        thisRule.switchesOn.each        {
            def itID = it.getId()
            if (!switchesID.contains(itID))     {
                switches << it
                switchesID << itID
            }
        }
    }
//    ifDebug("getAllSwitches: $switches")
    return switches
}

//------------------------------------------------------Night option------------------------------------------------------//
def	nightButtonPushedEventHandler(evt)     {
    ifDebug("nightButtonPushedEventHandler: $evt.data")
    if (!checkPauseModesAndDoW())    return;
    if (getHubType() == _SmartThings)       {
        if (!evt.data)      return;
        def nM = new groovy.json.JsonSlurper().parseText(evt.data)
        assert nM instanceof Map
    // missing map values dont seem to return null in hubitat 2017-04-07
        if (!nM || (nightButtonIs && nM['buttonNumber'] != null && nM['buttonNumber'] != nightButtonIs as Integer))
            return;
    }
    def roomState = getChildDevice(getRoom())?.currentValue(occupancy)
    if (nightSwitches && roomState == 'asleep')         {
        unscheduleAll("night button pushed handler")
        def switchValue = nightSwitches.currentSwitch
        if (nightButtonAction == "1")
        	dimNightLights()
        else if (nightButtonAction == "2" && switchValue.contains(on))
        	nightSwitchesOff()
        else if (nightButtonAction == "3")
        	(switchValue.contains(on) ? nightSwitchesOff() : dimNightLights())
    }
}

def dimNightLights()     {
//    unschedule('dimNightLights')
    if (nightSwitches)     {
        nightSwitches.each      {
            it.on(); pauseIt()
            def itID = it.getId()
            if (state.nightSetLevelTo && state.switchesHasLevel[itID])
                { it.setLevel(state.nightSetLevelTo); pauseIt() }
            if (state.nightSetCT && state.switchesHasColorTemperature[itID])
                { it.setColorTemperature(state.nightSetCT); pauseIt() }
        }
        getChildDevice(getRoom()).updateNSwitchInd(1)
    }
}

def nightSwitchesOff()      {
    unschedule('nightSwitchesOff')
    if (nightSwitches)  {
        nightSwitches.each      { it.off(); pauseIt() }
        getChildDevice(getRoom()).updateNSwitchInd(0)
    }
}

def sleepEventHandler(evt)		{
    ifDebug("sleepEventHandler: ${asleepSensor} - ${evt.value}")
    if (!checkPauseModesAndDoW())    return;
	def child = getChildDevice(getRoom())
    def roomState = child?.currentValue(occupancy)
    if (evt.value == "not sleeping")
    	child.generateEvent(checking)
    else if (evt.value == "sleeping")
        child.generateEvent(asleep)
}

def batteryDevices()      {
    ifDebug("batteryCheck")
    def allBatteryDevices = []
    def allBatteryDeviceID = []
    def itID
    def itBattery
    settings.each       {
        it.value.each       {
            itID = null
            if (it)     try { itID = it.id }    catch (all) {};
            if (itID)   {
                itBattery = false
                try { itBattery = (it.hasCapability("Battery") ? true : false) }    catch (all) {};
                if (itBattery && !allBatteryDeviceID.contains(itID))      {
                    allBatteryDevices << it
                    allBatteryDeviceID << itID
                }
            }
        }
    }
    return allBatteryDevices
}

private pauseIt()       {
    def hT = getHubType()
    if (hT == _SmartThings)     pause(pauseMSecST);
    else if (hT == _Hubitat)    pauseExecution(pauseMSecHU);
}

//------------------------------------------------------------------------------------------------------------------------//

/*************************************************************************/
/* webCoRE Connector v0.2                                                */
/*************************************************************************/
/*  Copyright 2016 Adrian Caramaliu <ady624(at)gmail.com>                */
/*                                                                       */
/*  This program is free software: you can redistribute it and/or modify */
/*  it under the terms of the GNU General Public License as published by */
/*  the Free Software Foundation, either version 3 of the License, or    */
/*  (at your option) any later version.                                  */
/*                                                                       */
/*  This program is distributed in the hope that it will be useful,      */
/*  but WITHOUT ANY WARRANTY; without even the implied warranty of       */
/*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the         */
/*  GNU General Public License for more details.                         */
/*                                                                       */
/*  You should have received a copy of the GNU General Public License    */
/*  along with this program.  If not, see <http://www.gnu.org/licenses/>.*/
/*************************************************************************/
/*  Initialize the connector in your initialize() method using           */
/*     webCoRE_init()                                                    */
/*  Optionally, pass the string name of a method to call when a piston   */
/*  is executed:                                                         */
/*     webCoRE_init('pistonExecutedMethod')                              */
/*************************************************************************/
/*  List all available pistons by using one of the following:            */
/*     webCoRE_list() - returns the list of id/name pairs                */
/*     webCoRE_list('id') - returns the list of piston IDs               */
/*     webCoRE_list('name') - returns the list of piston names           */
/*************************************************************************/
/*  Execute a piston by using the following:                             */
/*     webCoRE_execute(pistonIdOrName)                                   */
/*  The execute method accepts either an id or the name of a             */
/*  piston, previously retrieved by webCoRE_list()                       */
/*************************************************************************/
private webCoRE_handle()    { return 'webCoRE' }
private webCoRE_init(pistonExecutedCbk)     {
    ifDebug("webCoRE_init")
    state.webCoRE = (state.webCoRE instanceof Map ? state.webCoRE:[:]) + (pistonExecutedCbk ? [cbk:pistonExecutedCbk] : [:])
    subscribe(location, "${webCoRE_handle()}.pistonList", webCoRE_handler)
    if (pistonExecutedCbk)    subscribe(location, "${webCoRE_handle()}.pistonExecuted", webCoRE_handler);
//    webCoRE_poll()
    sendLocationEvent([name: webCoRE_handle(), value:'poll', isStateChange:true, displayed:false])
}
/*
private webCoRE_poll()      {
    ifDebug("webCoRE_poll")
    sendLocationEvent([name: webCoRE_handle(), value:'poll', isStateChange:true, displayed:false])
}
*/
public  webCoRE_execute(pistonIdOrName, Map data=[:])    {
    ifDebug("webCoRE_execute")
    def i = (state.webCoRE?.pistons ?: []).find{(it.name == pistonIdOrName) || (it.id == pistonIdOrName)}?.id;
    if (i)    sendLocationEvent([name:i, value:app.label, isStateChange:true, displayed:false, data:data]);
}
public  webCoRE_list(mode)      {
    ifDebug("webCoRE_list")
	def p = state.webCoRE?.pistons;
    if (p)
        p.collect{mode == 'id' ? it.id : (mode == 'name' ? it.name : [id:it.id, name:it.name])
        // log.debug "Reading piston: ${it}"
	}
    return p
}
public  webCoRE_handler(evt)    {
    ifDebug("webCoRE_handler")
    switch(evt.value)   {
        case 'pistonList':
            List p = state.webCoRE?.pistons ?: []
            Map d = evt.jsonData ?: [:]
            if (d.id && d.pistons && (d.pistons instanceof List))       {
                p.removeAll{it.iid == d.id}
                p += d.pistons.collect{[iid:d.id]+it}.sort{it.name}
                state.webCoRE = [updated:now(), pistons:p]
            }
            break
        case 'pistonExecuted':
            def cbk = state.webCoRE?.cbk
            if (cbk && evt.jsonData)    "$cbk"(evt.jsonData);
            break
    }
}

//------------------------------------------------------------------------------------------------------------------------//

@Field final Map    colorsRGB = [
        aliceblue: [240, 248, 255],             antiquewhite: [250, 235, 215],          aqua: [0, 255, 255],
        aquamarine: [127, 255, 212],            azure: [240, 255, 255],                 beige: [245, 245, 220],
        bisque: [255, 228, 196],                black: [0, 0, 0],                       blanchedalmond: [255, 235, 205],
        blue: [0, 0, 255],                      blueviolet: [138, 43, 226],             brown: [165, 42, 42],
        burlywood: [222, 184, 135],             cadetblue: [95, 158, 160],              chartreuse: [127, 255, 0],
        chocolate: [210, 105, 30],              coral: [255, 127, 80],                  cornflowerblue: [100, 149, 237],
        cornsilk: [255, 248, 220],              crimson: [220, 20, 60],                 cyan: [0, 255, 255],
        darkblue: [0, 0, 139],                  darkcyan: [0, 139, 139],                darkgoldenrod: [184, 134, 11],
        darkgray: [169, 169, 169],              darkgreen: [0, 100, 0],                 darkgrey: [169, 169, 169],
        darkkhaki: [189, 183, 107],             darkmagenta: [139, 0, 139],             darkolivegreen: [85, 107, 47],
        darkorange: [255, 140, 0],              darkorchid: [153, 50, 204],             darkred: [139, 0, 0],
        darksalmon: [233, 150, 122],            darkseagreen: [143, 188, 143],          darkslateblue: [72, 61, 139],
        darkslategray: [47, 79, 79],            darkslategrey: [47, 79, 79],            darkturquoise: [0, 206, 209],
        darkviolet: [148, 0, 211],              deeppink: [255, 20, 147],               deepskyblue: [0, 191, 255],
        dimgray: [105, 105, 105],               dimgrey: [105, 105, 105],               dodgerblue: [30, 144, 255],
        firebrick: [178, 34, 34],               floralwhite: [255, 250, 240],           forestgreen: [34, 139, 34],
        fuchsia: [255, 0, 255],                 gainsboro: [220, 220, 220],             ghostwhite: [248, 248, 255],
        gold: [255, 215, 0],                    goldenrod: [218, 165, 32],              gray: [128, 128, 128],
        green: [0, 128, 0],                     greenyellow: [173, 255, 47],            grey: [128, 128, 128],
        honeydew: [240, 255, 240],              hotpink: [255, 105, 180],               indianred: [205, 92, 92],
        indigo: [75, 0, 130],                   ivory: [255, 255, 240],                 khaki: [240, 230, 140],
        lavender: [230, 230, 250],              lavenderblush: [255, 240, 245],         lawngreen: [124, 252, 0],
        lemonchiffon: [255, 250, 205],          lightblue: [173, 216, 230],             lightcoral: [240, 128, 128],
        lightcyan: [224, 255, 255],             lightgoldenrodyellow: [250, 250, 210],  lightgray: [211, 211, 211],
        lightgreen: [144, 238, 144],            lightgrey: [211, 211, 211],             lightpink: [255, 182, 193],
        lightsalmon: [255, 160, 122],           lightseagreen: [32, 178, 170],          lightskyblue: [135, 206, 250],
        lightslategray: [119, 136, 153],        lightslategrey: [119, 136, 153],        lightsteelblue: [176, 196, 222],
        lightyellow: [255, 255, 224],           lime: [0, 255, 0],                      limegreen: [50, 205, 50],
        linen: [250, 240, 230],                 magenta: [255, 0, 255],                 maroon: [128, 0, 0],
        mediumaquamarine: [102, 205, 170],      mediumblue: [0, 0, 205],                mediumorchid: [186, 85, 211],
        mediumpurple: [147, 112, 219],          mediumseagreen: [60, 179, 113],         mediumslateblue: [123, 104, 238],
        mediumspringgreen: [0, 250, 154],       mediumturquoise: [72, 209, 204],        mediumvioletred: [199, 21, 133],
        midnightblue: [25, 25, 112],            mintcream: [245, 255, 250],             mistyrose: [255, 228, 225],
        moccasin: [255, 228, 181],              navajowhite: [255, 222, 173],           navy: [0, 0, 128],
        oldlace: [253, 245, 230],               olive: [128, 128, 0],                   olivedrab: [107, 142, 35],
        orange: [255, 165, 0],                  orangered: [255, 69, 0],                orchid: [218, 112, 214],
        palegoldenrod: [238, 232, 170],         palegreen: [152, 251, 152],             paleturquoise: [175, 238, 238],
        palevioletred: [219, 112, 147],         papayawhip: [255, 239, 213],            peachpuff: [255, 218, 185],
        peru: [205, 133, 63],                   pink: [255, 192, 203],                  plum: [221, 160, 221],
        powderblue: [176, 224, 230],            purple: [128, 0, 128],                  rebeccapurple: [102, 51, 153],
        red: [255, 0, 0],                       rosybrown: [188, 143, 143],             royalblue: [65, 105, 225],
        saddlebrown: [139, 69, 19],             salmon: [250, 128, 114],                sandybrown: [244, 164, 96],
        seagreen: [46, 139, 87],                seashell: [255, 245, 238],              sienna: [160, 82, 45],
        silver: [192, 192, 192],                skyblue: [135, 206, 235],               slateblue: [106, 90, 205],
        slategray: [112, 128, 144],             slategrey: [112, 128, 144],             snow: [255, 250, 250],
        springgreen: [0, 255, 127],             steelblue: [70, 130, 180],              tan: [210, 180, 140],
        teal: [0, 128, 128],                    thistle: [216, 191, 216],               tomato: [255, 99, 71],
        turquoise: [64, 224, 208],              violet: [238, 130, 238],                wheat: [245, 222, 179],
        white: [255, 255, 255],                 whitesmoke: [245, 245, 245],            yellow: [255, 255, 0],
        yellowgreen: [154, 205, 50]
]
