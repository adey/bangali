/***********************************************************************************************************************
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
***********************************************************************************************************************/

public static String version()      {  return "v0.50.0"  }
private static boolean isDebug()    {  return true  }

/***********************************************************************************************************************
*
*  Version: 0.50.0
*
*   DONE:   6/30/2018
*   1) added icons to main settings page for a room in hubitat. ST already shows these icons on the settings page.
*   2) added option to hide advanced settings.
*   3) added setting to adjust cooling and heating temperature by 0.5ªF when outside temperature is respectively over 90ªF and below 32ªF.
*   4) rewrote temperature management to be more consistent.
*   5) cleaned up rooms manager settings.
*   6) added option for how often device health message should be announced.
*   7) fixed a bug here and there.
*
*  Version: 0.45.0
*
*   DONE:   6/12/2018
*   1) added support for executing any device any command to rules.
*   2) added option to reset ENGAGED if contact stays open but still engaged using another device like power.
*   3) added option for LOCKED to override other devices that trigger other states. this excludes buttons that activate another state because by pressing a button user is expressing explicit intent to switch to that state.
*   4) added option to view all settings page to show a non-anonymized version for user to view locally.
*   5) updated timer countdown to be more uniform. hopefully :-)
*   6) updated settings in rooms manager to be more uniform.
*   7) added device health check with option to notify via speaker and/or color. this checks if the device has communicated with the hub in X number of hours, where X is configured through settings.
*   8) for hubitat only added option to check additional devices for device health even if those devices are not used with rooms.
*
*  Version: 0.40.0
*
*   DONE:   6/1/2018
*   1) cleaned up the settings page for rooms manager.
*   2) updated rooms device settings to deal with ST change of json parser which broke settings.
*   3) for rooms device events added a little more descriptive text.
// TODO make time range display actual time range not just the time type.
*   4) overhauled the view all settings page which had fallen behind.
*   5) added link to help text on github in app.
*   6) added setting for how fast room changes to VACANT if currently ASLEEP and room contact sensor is left open.
*   7) added setting for optional time range to set room to ENGAGED, LOCKED or ASLEEP with power wattage.
*   8) for CHECKING state added a lux value above which light will not get turned on for CHECKING state.
*   9) seperated the setting for reset ENGAGED and reset ASLEEP wtihout transitioning through the CHECKING state.
*   10) added fix to handle time preference settings for hubitat which does not handle timezone correctly for these settings.
*   11) introduced motion active check for when room state is transitioning to CHECKING state.
*   12) cleaned up some small bugs here and there along with some code cleanup.
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
*   DONE:   4/13/2018
*   1) changed message separator to '/' and added support for &is and &has.
*   2) added save and restore sound level when playing announcements.
*   3) restored lock only capability instead of using lock capability.
*   4) added support for lock state contact sensor by @BamaRayne.
*   5) added support for lock state switch and contact sensor to lock either on on/off or open/close by @BamaRayne.
*   6) added missing dot to nightSetCT range.
*
*  Version: 0.20.1
*
*   DONE:   4/11/2018
*   1) handle pause for hubitat.
*   2) adapt timeTodayAfter for hubitat compatibility.
*
*  Version: 0.20.0
*
*   DONE:   4/4/2018
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
***********************************************************************************************************************/

import groovy.transform.Field

@Field final String msgSeparator   = '/'

@Field final String _SmartThings = 'ST'
@Field final String _Hubitat     = 'HU'

@Field final int    pauseMSecST = 10
@Field final int    pauseMSecHU = 50

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
    page(name: "pagePersonNameSettings", content: "pagePersonNameSettings")
    page(name: "pagePersonColorSettings", content: "pagePersonColorSettings")
    page(name: "pageAnnouncementSpeakerTimeSettings", content: "pageAnnouncementSpeakerTimeSettings")
    page(name: "pageAnnouncementColorTimeSettings", content: "pageAnnouncementColorTimeSettings")
    page(name: "pageArrivalDepartureSettings", content: "pageArrivalDepartureSettings")
    page(name: "pageAnnouncementTextHelp", content: "pageAnnouncementTextHelp")
    page(name: "pageSunAnnouncementSettings", content: "pageSunAnnouncementSettings")
    page(name: "pageBatteryAnnouncementSettings", content: "pageBatteryAnnouncementSettings")
    page(name: "pageDeviceHealthSettings", content: "pageDeviceHealthSettings")
}

def mainPage()  {
    def appChildren = app.getChildApps().sort { it.label }
    dynamicPage(name: "mainPage", title: "Installed Rooms", install: false, uninstall: true, submitOnChange: true, nextPage: "pageSpeakerSettings") {
/*        section     {
            for (aC in appChildren)     {
                app(appName: "$aC.label")
            }
        }
*/
		section {
            app(name: "rooms manager", appName: "rooms child app", namespace: "bangali", title: "New Room", multiple: true)
		}
	}
}

def pageSpeakerSettings()   {
    ifDebug("pageSpeakerSettings")
//    def i = (presenceSensors ? presenceSensors.size() : 0)
//    def str = (presenceNames ? presenceNames.split(msgSeparator) : [])
//    def j = str.size()
    def playerDevice = (speakerDevices || speechDevices || musicPlayers ? true : false)
//    if (i != j && getHubType() == _SmartThings)     sendNotification("Count of presense sensors and names do not match!", [method: "push"]);
    def colorsList = []
    colorsRGB.each  { k, v ->
        colorsList << ["$k":"${v[1]}"]
    }
//    ifDebug("$colorsList")
    def nameString = []
    def colorString = []
    presenceSensors.each        {
        if (it)     {
            nameString << (settings["${it.getId()}Name"] ?: '')
            colorString << (settings["${it.getId()}Color"] ? colorsRGB[settings["${it.getId()}Color"]][1] : '')
        }
    }
    dynamicPage(name: "pageSpeakerSettings", title: "Annoucement Settings", install: true, uninstall: true)     {
        section("")     {
            href "pageAnnouncementSpeakerTimeSettings", title: "Spoken announcement settings", description: (playerDevice ? "Tap to change existing settings" : "Tap to configure")
        }
        section("")     {
            href "pageAnnouncementColorTimeSettings", title: "Color announcement settings", description: (announceSwitches ? "Tap to change existing settings" : "Tap to configure")
        }
        section("")       {
            href "pageArrivalDepartureSettings", title: "Arrival and departure settings", description: (speakerAnnounce || speakerAnnounceColor ? "Tap to change existing settings" : "Tap to configure")
        }
        section("")     {
            if (playerDevice)
                input "timeAnnounce", "enum", title: "Announce time?", required: false, multiple: false,
                                options: [[1:"Every 15 minutes"], [2:"Every 30 minutes"], [3:"Every hour"], [4:"No"]]
            else
                paragraph "Announce time?\nselect speaker devices to set."
        }
        section("")       {
            href "pageSunAnnouncementSettings", title: "Sun announcement settings", description: (sunAnnounce ? "Tap to change existing settings" : "Tap to configure")
        }
        section("")       {
            href "pageBatteryAnnouncementSettings", title: "Battery announcement settings", description: (batteryTime ? "Tap to change existing settings" : "Tap to configure")
        }
        section("")       {
            href "pageDeviceHealthSettings", title: "Device health settings", description: (checkHealth ? "Tap to change existing settings" : "Tap to configure")
        }
        section("Process execution rule(s) only on state change? (global setting overrides setting at room level)", hideable: false)		{
            input "onlyOnStateChange", "bool", title: "Only on state change?", required: false, multiple: false, defaultValue: false
        }
        section("")     {
            href "", title: "Help text on Github", style: "external", url: "https://github.com/adey/bangali/blob/master/README.md", description: "Click link to open in browser", image: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomOccupancySettings.png", required: false
        }
	}
}

def pagePersonNameSettings()           {
    ifDebug("pagePersonNameSettings")
    def namesList = []
    dynamicPage(name: "pagePersonNameSettings", title: "Presence sensor names:", install: false, uninstall: false)     {
        presenceSensors.each        {
            def itID = it.getId()
            def itDN = it.getDisplayName()
            section("")      {
                input "${itID}Name", "text", title: "$itDN Name?", required: true, multiple: false
            }
        }
    }
}

def pagePersonColorSettings()           {
    ifDebug("pagePersonColorSettings")
    def colorsList = []
    colorsRGB.each  { k, v ->
        colorsList << ["$k":"${v[1]}"]
    }
    dynamicPage(name: "pagePersonColorSettings", title: "Choose notification color:", install: false, uninstall: false)     {
        presenceSensors.each        {
            def itID = it.getId()
            def itDN = it.getDisplayName()
            section("")      {
                input "${itID}Color", "enum", title: "$itDN Color?", required: true, multiple: false, options: colorsList
            }
        }
    }
}

def pageAnnouncementTextHelp()      {
    dynamicPage(name: "pageAnnouncementTextHelp", title: "Announcement text format:", install: false, uninstall: false)     {
        section()       {
            paragraph "For announcement text all occurances of '&' is replaced with persons name(s)."
            paragraph "If there are multiple '$msgSeparator' separated strings in each announcement text input, then a random string will be used from that list of strings when announcing each time."
            paragraph "Similarly, all occurances of '&is' will be replaced with persons name(s) + ' is' or ' are' and '&has' with persons name(s) + ' has' or ' have'."
            paragraph "Choice of 'is' or 'are' and 'has' or 'have' is based on the number of person name(s) in the list for that announcement."
        }
    }
}

def pageAnnouncementSpeakerTimeSettings()      {
    def playerDevice = (speakerDevices || speechDevices || musicPlayers ? true : false)
    dynamicPage(name: "pageAnnouncementSpeakerTimeSettings", title: "Speaker settings:", install: false, uninstall: false)     {
        section("Speakers for annoucement:")       {
            input "speakerDevices", "capability.audioNotification", title: "Which speakers?", required: false, multiple: true, submitOnChange: true
            input "speechDevices", "capability.speechSynthesis", title: "Which speech devices?", required: false, multiple: true, submitOnChange: true
            input "musicPlayers", "capability.musicPlayer", title: "Which media players?", required: false, multiple: true, submitOnChange: true
            if (playerDevice)
                input "speakerVolume", "number", title: "Speaker volume?", required: false, multiple: false, defaultValue: 33, range: "1..100"
            else
                paragraph "Speaker volume?\nselect any speaker to set."
        }
        section("Spoken announcement during hours:")       {
            if (playerDevice)        {
                input "startHH", "number", title: "From hour?", description: "0..${(endHH ?: 23)}", required: true, multiple: false, defaultValue: 7, range: "0..${(endHH ?: 23)}", submitOnChange: true
                input "endHH", "number", title: "To hour?", description: "${(startHH ?: 0)}..23", required: true, multiple: false, defaultValue: 23, range: "${(startHH ?: 0)}..23", submitOnChange: true
            }
            else        {
                paragraph "Announce from hour?\nselect either presence or time announcement to set"
                paragraph "Announce to hour?\nselect either presence or time announcement to set"
            }
        }
    }
}

def pageAnnouncementColorTimeSettings()      {
    dynamicPage(name: "pageAnnouncementColorTimeSettings", title: "Color settings:", install: false, uninstall: false)     {
        section("Lights for announcement with color:")   {
            input "announceSwitches", "capability.switch", title: "Which switches?", required: false, multiple: true, submitOnChange: true
        }
        section("Color announcement during hours:")       {
            if (announceSwitches)        {
                input "startHHColor", "number", title: "From hour?", description: "0..${(endHHColor ?: 23)}", required: true, multiple: false, defaultValue: 18, range: "0..${(endHH ?: 23)}", submitOnChange: true
                input "endHHColor", "number", title: "To hour?", description: "${(startHHColor ?: 0)}..23", required: true, multiple: false, defaultValue: 23, range: "${(startHH ?: 0)}..23", submitOnChange: true
            }
            else        {
                paragraph "Announce from hour?\nselect announce switches to set"
                paragraph "Announce to hour?"
            }
        }
    }
}

def pageArrivalDepartureSettings()      {
    def nameString = []
    def colorString = []
    presenceSensors.each        {
        if (it)     {
            nameString << (settings["${it.getId()}Name"] ?: '')
            colorString << (settings["${it.getId()}Color"] ? colorsRGB[settings["${it.getId()}Color"]][1] : '')
        }
    }
    def playerDevice = (speakerDevices || speechDevices || musicPlayers ? true : false)
    dynamicPage(name: "pageArrivalDepartureSettings", title: "Arrival and departure settings", install: false, uninstall: false)     {
        section("Arrival and departure announcement")      {
            if (playerDevice)
                input "speakerAnnounce", "bool", title: "Announce presence with speaker?", required: false, multiple: false, defaultValue: false, submitOnChange: true
            else
                paragraph "Announce presence with speaker?\nselect speaker to set."
            if (announceSwitches)
                input "speakerAnnounceColor", "bool", title: "Announce presence with color?", required: false, multiple: false, defaultValue: false, submitOnChange: true
            else
                paragraph "Announce presence with color??\nselect announce with color light to set."
            if (speakerAnnounce || speakerAnnounceColor)
                input "presenceSensors", "capability.presenceSensor", title: "Which presence sensors?", required: true, multiple: true, submitOnChange: true
            else
                paragraph "Which presence sensors?\nselect either announce with speaker or color to set"
            if (presenceSensors)
                href "pagePersonNameSettings", title: "Names for presence sensor(s)", description: "$nameString"
            else
                paragraph "Names for presence sensor(s)\nselect presence sensor(s) to set."
        }
		section("Arrival and departure announcement text:")        {
            if (playerDevice && speakerAnnounce)    {
                href "pageAnnouncementTextHelp", title: "Accouncement text format help:", description: "Click to read"
                input "welcomeHome", "text", title: "Welcome home greeting?", required: true, multiple: false, defaultValue: 'Welcome home &.'
                input "welcomeHomeCloser", "text", title: "Welcome home greeting closer?", required: false, multiple: false
                input "leftHome", "text", title: "Left home announcement?", required: true, multiple: false, defaultValue: '&has left home.'
                input "leftHomeCloser", "text", title: "Left home announcement closer?", required: false, multiple: false
            }
            else    {
                paragraph "Welcome home greeting?\nselect speaker announce to set."
                paragraph "Welcome home greeting closer?\nselect speaker announce to set."
                paragraph "Left home announcement?\nselect speaker announce to set."
                paragraph "Left home announcement closer?\nselect speaker announce to set."
            }
        }
        section("Arrival and departure announcement with color:")   {
            if (speakerAnnounceColor)
                href "pagePersonColorSettings", title: "Color(s) for presence sensor(s)", description: "$colorString"
            else
                paragraph "Color(s) for presence sensor(s)\nselect announce with color to set."
        }
        section("Trigger annoucement settings:")      {
            if (speakerAnnounce || speakerAnnounceColor)    {
                input "contactSensors", "capability.contactSensor", title: "Welcome home greeting when which contact sensor(s) close?",
                                                required: (!motionSensors ? true : false), multiple: true
                input "motionSensors", "capability.motionSensor", title: "Welcome home greeting with motion on which motion sensor(s)?",
                                                required: (!contactSensors ? true : false), multiple: true
                input "secondsAfter", "number", title: "Left home announcement how many seconds after?",
                                                required: true, multiple: false, defaultValue: 15, range: "5..100"
            }
            else    {
                paragraph "Welcome home greeting when which contact sensor(s) close?\nselect announce to set."
                paragraph "Welcome home greeting with motion on which motion sensor(s)?\nselect announce to set."
                paragraph "Left home announcement how many seconds after?\nselect announce to set."
            }
        }
    }
}

def pageSunAnnouncementSettings()      {
    def colorsList = []
    colorsRGB.each  { k, v ->
        colorsList << ["$k":"${v[1]}"]
    }
    dynamicPage(name: "pageSunAnnouncementSettings", title: "Sun announcements:", install: false, uninstall: false)     {
        section("")     {
            if (announceSwitches)
                input "sunAnnounce", "enum", title: "Sunrise/sunset announcement?", required: false, multiple: false, defaultValue: null, submitOnChange: true, options: [[null:"None"],[1:"Sunrise"],[2:"Sunset"],[3:"Both"]]
            else
                paragraph "Sunrise/sunset announcement?\nselect lights for announce by color to set"
            if (['1', '3'].contains(sunAnnounce))
                input "sunriseColor", "enum", title: "Sunrise color?", required: true, multiple: false, options: colorsList
            else
                paragraph "Sunrise color?\nset sunrise announcement to set"
            if (['2', '3'].contains(sunAnnounce))
                input "sunsetColor", "enum", title: "Sunset color?", required: true, multiple: false, options: colorsList
            else
                paragraph "Sunset color?\nset sunset announcement to set"
        }
    }
}

def pageBatteryAnnouncementSettings()      {
    def colorsList = []
    colorsRGB.each  { k, v ->
        colorsList << ["$k":"${v[1]}"]
    }
    def playerDevice = (speakerDevices || speechDevices || musicPlayers ? true : false)
    dynamicPage(name: "pageBatteryAnnouncementSettings", title: "Battery announcement:", install: false, uninstall: false)     {
        section("")     {
            if (playerDevice || announceSwitches)
                input "batteryTime", "time", title: "Annouce battery status when?", required: false, multiple: false, submitOnChange: true
            else
                paragraph "Annouce battery status when?\nselect speakers or switches to set"
            if (batteryTime)
                input "batteryLevel", "number", title: "Battery level below which to include in status?", required: true, multiple: false, defaultValue: 33, range: "1..100"
            else
                paragraph "Battery level to include in status?\nselect battery time to set."
            if (batteryTime && announceSwitches)        {
                input "batteryOkColor", "enum", title: "Battery all OK color?", required: false, multiple: false, options: colorsList
                input "batteryLowColor", "enum", title: "Battery low color?", required: true, multiple: false, options: colorsList
            }
            else        {
                paragraph "Battery all OK warning color?\nselect battery time to set."
                paragraph "Battery low warning color?\nselect battery time to set."
            }
        }
    }
}

def pageDeviceHealthSettings()      {
    def colorsList = []
    colorsRGB.each  { k, v ->
        colorsList << ["$k":"${v[1]}"]
    }
    def playerDevice = (speakerDevices || speechDevices || musicPlayers ? true : false)
    def hT = getHubType()
    dynamicPage(name: "pageDeviceHealthSettings", title: "Device health announcement:", install: false, uninstall: false)     {
        section("")		{
            input "checkHealth", "bool", title: "Check device health?", required: false, multiple: false, defaultValue: false, submitOnChange: true
            if (checkHealth)
                input "eventHours", "enum", title: "Device event within how many hours?", required: true, multiple: false, options: [12:"12 hours", 24:"24 hours", 48:"48 hours", 72:"72 hours"]
            else
                paragraph "Announce device health status when?\nselect speakers or switches to set"

        }
        section("Speak health announcement:")      {
            if (checkHealth && playerDevice)
                input "healthEvery", "enum", title: "Every how many hours?", required: true, multiple: false, defaultValue: 0, options: [0:"No spoken announcement", 1:"1 hour", 2:"2 hours", 3:"3 hours", 6:"6 hours", 12:"12 hours", 24:"24 hours"]
            else
                paragraph "Every how many hours?\nselect check health to set."
        }
        section("Announce with color:")      {
            if (checkHealth && announceSwitches)        {
                input "healthOkColor", "enum", title: "Device health OK color?", required: false, multiple: false, options: colorsList
                input "healthWarnColor", "enum", title: "Device health warning color?", required: true, multiple: false, options: colorsList
            }
            else        {
                paragraph "Device health OK color?\nselect check health to set."
                paragraph "Device health warning color?"
            }
        }
        if (hT == _Hubitat)
            section("Additional devices for health check")      {
                input "healthAddDevices", "capability.*", title: "Check these devices?", required: false, multiple: true
            }
    }
}

def installed()		{  initialize()  }

def updated()		{
    ifDebug("updated")
	initialize()
    def hT = getHubType()
    if (hT == _Hubitat)     {
        if (settings["presenceNames"])          app.removeSetting("presenceNames")
        if (settings["presenceColorString"])    app.removeSetting("presenceColorString")
    }
    announceSetup()
    if (!onlyOnStateChange)
        if (hT == _SmartThings)       runEvery5Minutes(processChildSwitches)
        else                          schedule("33 0/1 * * * ?", processChildSwitches)
    schedule("0 0/15 * 1/1 * ? *", tellTime)
    if (checkHealth)        {
        state.healthHours = 0
        runEvery1Hour(checkDeviceHealth)
        runIn(1, checkDeviceHealth)
    }
    if (batteryTime)        schedule(batteryTime, batteryCheck)
}

def initialize()	{
    ifDebug("initialize")
    unsubscribe()
    unschedule()
    state.colorsRotating = false
	ifDebug("there are ${childApps.size()} rooms.")
	childApps.each	{ child ->
        def childRoomDevice = getChildRoomDeviceObject(child.id)
        ifDebug("initialize: room: ${child.label} id: ${child.id} childRoomDevice: $childRoomDevice")
//        subscribe(childRoomDevice, "button.pushed", buttonPushedEventHandler)
        subscribe(childRoomDevice, "occupancy", roomStateHistory)
	}
    if (announceSwitches && ['1', '3'].contains(sunAnnounce))       { ifDebug("sunrise"); subscribe(location, "sunrise", sunriseEventHandler)  }
    if (announceSwitches && ['2', '3'].contains(sunAnnounce))       { ifDebug("sunset"); subscribe(location, "sunset", sunsetEventHandler)    }
    state.lastBatteryUpdate = ''
}

private getHubType()        {
    if (!state.hubId)   state.hubId = location.hubs[0].id.toString()
    if (state.hubId.length() > 5)   return _SmartThings;
    else                            return _Hubitat;
}

def unsubscribeChild(childID)   {  unsubscribe(getChildRoomDeviceObject(childID))  }

def roomStateHistory(evt)        {
//    log.debug "rooms manager handler: event: $evt | data: $evt.data | date: $evt.date | description: $evt.description | descriptionText: $evt.descriptionText | device: $evt.device | displayName: $evt.displayName | deviceId: $evt.deviceId | id: $evt.id | hubId: $evt.hubId | isoDate: $evt.isoDate | location: $evt.location | locationId: $evt.locationId | name: $evt.name | source: $evt.source | unit: $evt.unit | value: $evt.value | isDigital: ${evt.isDigital()} | isPhysical: ${evt.isPhysical()} |"
//  rooms manager handler: event: physicalgraph.app.EventWrapper@3405f9c6 | data: {"microDeviceTile":{"type":"standard","icon":"st.Health & Wellness.health9","backgroundColor":"#616969"}} |
//  date: Thu Apr 05 07:43:49 UTC 2018 | dateValue: null | description: | descriptionText: Living Room changed to checking | device: Living Room |
//  displayName: Living Room | deviceId: e1e67d09-efa3-426d-8915-c7127707118c | id: 14279da0-38a5-11e8-949a-0a9482059a2a | hubId: null |
//  installedSmartAppId: null | isoDate: 2018-04-05T07:43:49.370Z | location: Home | locationId: c9924c68-002a-4c2a-a556-cfc4b5dd2b99 |
//  name: occupancy | source: DEVICE | stringValue: checking | unit: null | value: checking | isDigital: false | isPhysical: false | isStateChange: true |

// rooms manager handler: event: com.hubitat.hub.domain.Event@5434b29a | data: null | description: null | descriptionText: Kitchen changed to vacant |
//  device: Kitchen | displayName: Kitchen | deviceId: 20 | id: 11240 | hubId: null | location: null | locationId: null |
//  name: occupancy | source: DEVICE | unit: null | value: vacant | isDigital: false | isPhysical: false |

    def rSH = [state: evt.value, time: new Date(now()).format("yyyy-MM-dd'T'HH:mm:ssZ", location.timeZone)]
    if (!state.rSH)     {
        state.rSH = [:]
        ifDebug("rSH initialized")
    }
    ifDebug("rSH $evt.displayName | $evt.deviceId | $evt.value")
//    if (state.rSH[(evt.deviceId)])      state.rSH[(evt.deviceId)] << rSH;
//    else                                state.rSH[(evt.deviceId)] = rSH;
    if (!state.rSH[(evt.deviceId)])      state.rSH[(evt.deviceId)] = []
    state.rSH[(evt.deviceId)] << rSH;
//    else                                state.rSH = [(evt.deviceId): rSH];
/*    state.rSH.each     { dID, dMap ->
        state.rSH[dID] = dMap.sort  { a, b -> b.value <=> a.value  }
    }
*/
}

private announceSetup()     {
    state.whoCameHome = [:]
    state.whoCameHome.personsIn = []
    state.whoCameHome.personsOut = []
    state.whoCameHome.personNames = [:]
    state.personsColors = [:]
    state.colorsToRotate = [:]
    if (!speakerAnnounce && !speakerAnnounceColor)   return;
        presenceSensors.each        {
            def itID = it.getId()
            state.whoCameHome.personNames << [(itID):settings["${itID}Name"]]
            if (speakerAnnounceColor)       {
                def hue = convertRGBToHueSaturation(settings["${itID}Color"])
                state.personsColors << [(itID):hue]
            }
        }
        if (presenceSensors)     {
            subscribe(presenceSensors, "presence.present", presencePresentEventHandler)
            subscribe(presenceSensors, "presence.not present", presenceNotPresentEventHandler)
        }
        if (contactSensors)     subscribe(contactSensors, "contact.closed", contactClosedEventHandler);
        if (motionSensors)      subscribe(motionSensors, "motion.active", contactClosedEventHandler);
    def str, i
    state.welcomeHome = [:]
    if (welcomeHome)        {
        str = welcomeHome.split(msgSeparator)
        i = 0
        str.each    {
            state.welcomeHome[i] = it
            i = i + 1
        }
    }
    state.welcomeHomeCloser = [:]
    if (welcomeHomeCloser)      {
        str = welcomeHomeCloser.split(msgSeparator)
        i = 0
        str.each    {
            state.welcomeHomeCloser[i] = it
            i = i + 1
        }
    }
    state.leftHome = [:]
    if (leftHome)       {
        str = leftHome.split(msgSeparator)
        i = 0
        str.each    {
            state.leftHome[i] = it
            i = i + 1
        }
    }
    state.leftHomeCloser = [:]
    if (leftHomeCloser)     {
        str = leftHomeCloser.split(msgSeparator)
        i = 0
        str.each    {
            state.leftHomeCloser[i] = it
            i = i + 1
        }
    }
}

/*
def buttonPushedEventHandler(evt)     {
    ifDebug("buttonPushedEventHandler")
}
*/

def getChildRoomDeviceObject(childID)     {
    def roomDeviceObject = null
    childApps.each	{ child ->
        if (childID == child.id)        roomDeviceObject = child.getChildRoomDevice();
	}
    ifDebug("getChildRoomDeviceObject: childID: $childID | roomDeviceObject: $roomDeviceObject")
    return roomDeviceObject
}

def checkThermostatValid(childID, checkThermostat)      {
    ifDebug("checkThermostatValid: $checkThermostat")
    if (!checkThermostat)   return null;
    def otherRoom = null
    childApps.each	{ child ->
        if (childID != child.id)   {
            def thermo = child.getChildRoomThermostat()
            if (thermo && checkThermostat.getId() == thermo.thermostat.getId())     {
//                ifDebug("getChildRoomThermostat: $thermo.name")
                otherRoom = thermo.name
            }
        }
	}
    return otherRoom
}

def	presencePresentEventHandler(evt)     {  whoCameHome(evt.device)  }

def	presenceNotPresentEventHandler(evt)     {  whoCameHome(evt.device, true)  }

/*
def contactClosedEventHandler(evt = null)     {
    if ((evt && !state.whoCameHome.personsIn) || (!evt && !state.whoCameHome.personsOut))     return;
    def str = (evt ? state.whoCameHome.personsIn : state.whoCameHome.personsOut)
    def i = str.size()
    def j = 1
    def rand = new Random()
    def k = (state.welcomeHome1 ? Math.abs(rand.nextInt() % state.welcomeHome1.size()) : 0) + ''
    def k2 = (state.welcomeHomeCloser ? Math.abs(rand.nextInt() % state.welcomeHomeCloser.size()) : 0) + ''
    def l = (state.leftHome1 ? Math.abs(rand.nextInt() % state.leftHome1.size()) : 0) + ''
    def l2 = (state.leftHomeCloser ? Math.abs(rand.nextInt() % state.leftHomeCloser.size()) : 0) + ''
//    ifDebug("k: $k ${state.welcomeHome1[(k)]} | l: $l ${state.leftHome1[(l)]}")
    def persons = (evt ? state.welcomeHome1[(k)] : state.leftHome1[(l)]) + ' '
    str.each      {
        persons = persons + (j != 1 ? (j == i ? ' and ' : '/ ') : '') + it
        j = j + 1
    }
    persons = persons + ' ' + (evt ? state.welcomeHome2[(k)] : state.leftHome2[(l)]) +
                        ' ' + (evt ? (welcomeHomeCloser ? state.welcomeHomeCloser[(k2)] : '') :
                                     (leftHomeCloser ? state.leftHomeCloser[(l2)] : '')) + '.'
//    ifDebug("k: $k ${state.welcomeHome2[(k)]} | l: $l ${state.leftHome2[(l)]}")
    ifDebug("message: $persons")
    speakIt(str, persons)
    if (evt)    state.whoCameHome.personsIn = [];
    else        state.whoCameHome.personsOut = [];
}
*/

def contactClosedEventHandler(evt = null)     {
    if ((evt && !state.whoCameHome.personsIn) || (!evt && !state.whoCameHome.personsOut))     return;
    def rand = new Random()
    def k = (state.welcomeHome ? Math.abs(rand.nextInt() % state.welcomeHome.size()) : 0) + ''
    def k2 = (state.welcomeHomeCloser ? Math.abs(rand.nextInt() % state.welcomeHomeCloser.size()) : 0) + ''
    def l = (state.leftHome ? Math.abs(rand.nextInt() % state.leftHome.size()) : 0) + ''
    def l2 = (state.leftHomeCloser ? Math.abs(rand.nextInt() % state.leftHomeCloser.size()) : 0) + ''
    def persons = ''
    def str = (evt ? state.whoCameHome.personsIn : state.whoCameHome.personsOut)
    def i = str.size()
    def multiple = (i > 1 ? true : false)
    def j = 1
    str.each      {
        persons = persons + (j != 1 ? (j == i ? ' and ' : ', ') : '') + it
        j = j + 1
    }
    str = (evt ? (state.welcomeHome[(k)] ?: '') : (state.leftHome[(l)] ?: '')) + ' ' +
          (evt ? (state.welcomeHomeCloser[(k2)] ?: '') : (state.leftHomeCloser[(l2)] ?: ''))
//    ifDebug("pre message: $str")
// TODO add more generic text replacement like @is is replaced with `is` when 1 person and `are` when multiple persons.
    for (special in ['&is', '&are', '&has', '&have', '&'])    {
        def str2 = str.split(special)
        str = ''
        for (i = 0; i < str2.size(); i++)       {
//            def trimmed = str2[i].replaceAll("\\s","")
            def replaceWith
            switch(special)     {
                case '&':       replaceWith = persons;      break
                case ['&is', '&are']:     replaceWith = persons + (multiple ? ' are' : ' is');        break
                case ['&has', '&have']:   replaceWith = persons + (multiple ? ' have' : ' has');      break
                default:        replaceWith = 'unknown';    break
            }
            str = str + str2[i] + (i != (str2.size() -1) ? ' ' + replaceWith : '')
        }
        if (!str)   str = str2;
    }
//    ifDebug("message: $str")
    speakIt(str)
    if (evt)    state.whoCameHome.personsIn = [];
    else        state.whoCameHome.personsOut = [];
    if (speakerAnnounceColor)       setupColorRotation();
}

private speakIt(str)     {
    def nowDate = new Date(now())
    def intCurrentHH = nowDate.format("HH", location.timeZone) as Integer
    def intCurrentMM = nowDate.format("mm", location.timeZone) as Integer
    if (intCurrentHH >= startHH && (intCurrentHH < endHH || (intCurrentHH == endHH && intCurrentMM == 0)))      {
        if (speakerDevices)     {
            def currentVolume = speakerDevices.currentLevel
            def isMuteOn = speakerDevices.currentMute.contains("muted")
            if (isMuteOn)     speakerDevices.unmute();
            speakerDevices.playTextAndResume(str, speakerVolume);
            if (currentVolume != speakerVolume)      speakerDevices.setLevel(currentVolume)
            if (isMuteOn)     musicPlayers.mute();
        }
        if (speechDevices)      speechDevices.speak(str);
		if (musicPlayers)     {
            def currentVolume = musicPlayers.currentLevel
            def isMuteOn = musicPlayers.currentMute.contains("muted")
            if (isMuteOn)     musicPlayers.unmute();
            musicPlayers.playTrackAndResume(str, speakerVolume)
            if (currentVolume != speakerVolume)      musicPlayers.setLevel(currentVolume)
            if (isMuteOn)     musicPlayers.mute();
        }
    }
}

private setupColorRotation()        {
    state.colorsRotateSeconds = state.colorsToRotate.size() * 30
    if (!state.colorsRotating)       {
        state.colorsRotating = true
        state.colorsIndex = 0
        announceSwitches.on()
//        announceSwitches.setLevel(99)
        rotateColors()
    }
}

def rotateColors()      {
    ifDebug("rotateColors")
    ifDebug("$state.colorsIndex | $state.colorsRotateSeconds | ${state.colorsToRotate."$state.colorsIndex"} | ")
    announceSwitches.setColor(state.colorsToRotate."$state.colorsIndex"); pauseIt()
//    announceSwitches.setHue(state.colorsToRotate."$state.colorsIndex".hue)
//    announceSwitches.setSaturation(state.colorsToRotate."$state.colorsIndex".saturation)
    state.colorsRotateSeconds = (state.colorsRotateSeconds >= 5 ? state.colorsRotateSeconds - 5 : 0)
    state.colorsIndex = (state.colorsIndex < (state.colorsToRotate.size() -1) ? state.colorsIndex + 1 : 0)
    if (state.colorsRotateSeconds > 0)
        runIn(5, rotateColors)
    else        {
        state.colorsRotating = false
        state.colorsToRotate = [:]
        pauseIt(true); announceSwitches.off(); pauseIt()
    }
}

private whoCameHome(presenceSensor, left = false)      {
    if (!presenceSensor)    return;
    def pID = presenceSensor.getId()
    def presenceName = state.whoCameHome.personNames[(pID)]
    if (!presenceName)      return;
    ifDebug("presenceName: $presenceName | left: $left")
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
    if (speakerAnnounceColor)
        state.colorsToRotate << [(state.colorsToRotate.size()):state.personsColors[(pID)]]
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
        if (adjRooms)
            childApps.each	{ child ->
                if (childID != child.id && adjRooms.contains(child.id))      {
                    def mS = child.getAdjMotionSensors()
                    if (mS)
                        mS.each      {
                            def motionSensorId = it.getId()
                            if (!adjMotionSensorsIds.contains(motionSensorId))   {
                                adjMotionSensors << it
                                adjMotionSensorsIds << motionSensorId
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
    if (childID)
        childApps.each	{ child ->
            if (childID == child.id)    {
                def lastStateDateChild = child.getLastStateChild()
                lastStateDate = lastStateDateChild
            }
        }
    return lastStateDate
}

def processChildSwitches()      {
    def time = now()
/*    childApps.each	{ child ->
        def modeAndDoW = child.checkRoomModesAndDoW()
//        ifDebug("processChildSwitches: modeAndDoW: $modeAndDoW | child: $child.label")
        if (modeAndDoW)     {
            child.switchesOnOrOff(true)
            if (hT == _SmartThings)     pause(10);
        }
    }
*/
//    def quarterHour = (((time % 3600000f) / 60000f).trunc(0) % 15) // quarter hour = 0
    childApps.each  { child ->
        ifDebug("processChildSwitches: $child.label")
        if (child.checkAndTurnOnOffSwitchesC())
            if (getHubType() == _SmartThings)     pause(10);
    }
    ifDebug("${now() - time} ms")
}

def batteryCheck()      {
    def allBatteries = []
    def allBatteriesID = []
    childApps.each  { child ->
        def batteries = child.batteryDevices()
        batteries.each      {
            def itID = it.id
            if (!allBatteriesID.contains(itID))     {
                allBatteries << it
                allBatteriesID << itID
            }
        }
    }
    def bat
    def batteryNames = ''
    def batteryLow = false
    allBatteries.each      {
        bat = it.currentValue("battery")
        if (bat < batteryLevel)         {
            batteryLow = true
            batteryNames = batteryNames + (it.displayName ?: it.name) + ', '
        }
    }
    if (announceSwitches && ((!batteryLow && batteryOkColor) || (batteryLow && batteryLowColor)))        {
        def color = convertRGBToHueSaturation((colorsRGB[(batteryLow ? batteryLowColor : batteryOkColor)][1]))
        state.colorNotificationColor = color
        setupColorNotification()
    }
    if (speakerDevices || speechDevices || musicPlayers)        {
        state.lastBatteryUpdate = ( batteryNames?.trim() ? "the following battery devices are below $batteryLevel percent $batteryNames." :
                                                       "no device battery below $batteryLevel percent.")
        speakIt(state.lastBatteryUpdate)
    }
}

def sunriseEventHandler()       {
    ifDebug("sunriseEventHandler")
    state.colorNotificationColor = convertRGBToHueSaturation((colorsRGB[sunriseColor][1]))
    setupColorNotification()
}

def sunsetEventHandler()       {
    ifDebug("sunsetEventHandler")
    state.colorNotificationColor = convertRGBToHueSaturation((colorsRGB[sunsetColor][1]))
    setupColorNotification()
}

def setupColorNotification()        {
    def nowDate = new Date(now())
    def intCurrentHH = nowDate.format("HH", location.timeZone) as Integer
    def intCurrentMM = nowDate.format("mm", location.timeZone) as Integer
    if (intCurrentHH < startHHColor || (intCurrentHH > endHHColor || (intCurrentHH == endHHColor && intCurrentMM != 0)))
        return
    if (!state.colorsRotating)       {
        state.colorNotifyTimes = 9
        state.colorSwitchSave = []
        state.colorColorSave = []
        state.colorColorTemperatureSave = []
        state.colorColorTemperatureTrueSave = []
        announceSwitches.each   {
            state.colorSwitchSave << it.currentSwitch
            state.colorColorSave << [hue: it.currentHue, saturation: it.currentSaturation, level: it.currentLevel]
            def evts = it.events(max: 250)
            def foundValue = false
            def keepSearching = true
            evts.each       {
                if (!foundValue && keepSearching)
                    if (it.value == 'setColorTemperature')     {
                        foundValue = true
                        state.colorColorTemperatureTrueSave << true
                    }
                    else if (['hue', 'saturation'].contains(it.name))
                        keepSearching = false
            }
            if (!foundValue)        state.colorColorTemperatureTrueSave << false
            state.colorColorTemperatureSave << it.currentColorTemperature
        }
        notifyWithColor()
    }
    else
        runIn(10, setupColorNotification)
}

def notifyWithColor()      {
    ifDebug("notifyWithColor")
    if ((state.colorNotifyTimes % 2f).trunc(0) == 1)    {
        announceSwitches.on(); pauseIt()
        announceSwitches.setColor(state.colorNotificationColor); pauseIt()
//        announceSwitches.setLevel(99)
//        announceSwitches.setHue(state.colorNotificationColor.hue)
//        announceSwitches.setSaturation(state.colorNotificationColor.saturation)
    }
    else
        announceSwitches.off(); pauseIt()
    state.colorNotifyTimes = state.colorNotifyTimes - 1
//    if (state.colorNotifyTimes >= 0)      runIn(state.colorNotifyTimes % 2f).trunc(0) + 1, notifyWithColor)
    if (state.colorNotifyTimes >= 0)
        runIn(1, notifyWithColor)
    else        {
        def i = 0
        announceSwitches.each       {
            ifDebug("$it | ${state.colorColorSave[i]} | ${state.colorSwitchSave[(i)]} | ${state.colorColorTemperatureTrueSave[i]} | ${state.colorColorTemperatureSave[i]}")
            (state.colorColorTemperatureTrueSave[(i)] == true ? it.setColorTemperature(state.colorColorTemperatureSave[(i)]) :
                                                                it.setColor(state.colorColorSave[(i)])); pauseIt(true)
            (state.colorSwitchSave[(i)] == 'off' ? it.off() : it.on()); pauseIt()
//            (state.colorSwitchSave[(i)] == 'off' ? it.off() : it.on()); pauseIt()
            i = i + 1
        }
    }
}

private pauseIt(longOne = false)       {
    def hT = getHubType()
    if (hT == _SmartThings)     pause(pauseMSecST);
    else if (hT == _Hubitat)    pauseExecution(pauseMSecHU * (longOne ? 10 : 1));
}

def tellTime()      {
    ifDebug("tellTime")
    def nowDate = new Date(now())
    def intCurrentMM = nowDate.format("mm", location.timeZone) as Integer
    def intCurrentHH = nowDate.format("HH", location.timeZone) as Integer
// TODO
    def timeString = 'time is ' + (intCurrentMM == 0 ? '' : intCurrentMM + ' minutes past ') +
                   intCurrentHH + (intCurrentHH < 12 ? ' oclock.' : ' hundred hours.')
    if ((timeAnnounce == '1' || (timeAnnounce == '2' && (intCurrentMM == 0 || intCurrentMM == 30)) ||
        (timeAnnounce == '3' && intCurrentMM == 0)))       {
// TODO
//        speakerDevices.playTrackAndResume("http://s3.amazonaws.com/smartapp-media/sonos/bell1.mp3",
//                                            (intCurrentMM == 0 ? 10 : (intCurrentMM == 15 ? 4 : (intCurrentMM == 30 ? 6 : 8))), speakerVolume)
//        pause(1000)
//        if (speakerDevices)      speakerDevices.playTextAndResume(timeString, speakerVolume);
//        if (speechDevices)       speechDevices.speak(timeString);
        speakIt(timeString)
    }
}

def checkDeviceHealth()     {
    ifDebug("${now()}")
    def cDT = new Date(now() - (eventHours.toInteger() * 3600000l))
    ifDebug("$cDT")
    def hT = getHubType()
    def tD = []
    def tDID = []
    def itID
    def str = ''
    childApps.each      { child ->
        def aD = child.allDevicesC()
//        ifDebug("$aD")
        aD.each      {
            itID = it.id
            if (!tDID.contains(itID))       {
                tD << it
                tDID << itID
            }
        }
    }
    def aD = allDevices()
    aD.each      {
        itID = it.id
        if (!tDID.contains(itID))       {
            tD << it
            tDID << itID
        }
    }
    def dHC = ''
    tD.each     {
        def lastEvent = it.events(max: 100)
        def first = true
        lastEvent.each      {
            if (first && ((hT == _SmartThings && it.eventSource == 'DEVICE') || (hT == _Hubitat && it.source == 'DEVICE')))        {
                if (it.date.before(cDT))
                    dHC = dHC + (dHC ? ', ' : '') + it.displayName
//                    ifDebug("$it.displayName | $it.eventSource | $it.date")
                first = false
            }
        }
    }
    state.lastDeviceHealthUpdate = (dHC ? "$dHC devices have not checked in last $eventHours hours." : "device health is ok.")
    if (announceSwitches && ((!dHC && healthOkColor) || (dHC && healthWarnColor)))      {
        def color = convertRGBToHueSaturation(colorsRGB[(dHC ?  healthWarnColor : healthOkColor)][1])
        state.colorNotificationColor = color
        setupColorNotification()
    }
    if (healthEvery)        {
        if (state.healthHours == 0 && dHC && (speakerDevices || speechDevices || musicPlayers))
            speakIt(state.lastDeviceHealthUpdate)
        state.healthHours = (state.healthHours == 0 ? healthEvery as Integer : state.healthHours - 1)
    }
    ifDebug("healthEvery: $healthEvery | $state.lastDeviceHealthUpdate")
    ifDebug("${now()}")
}

def allDevices()       {
    def aD = []
    def aDID = []
    def itID
    settings.each   {
        it.value.each       {
            itID = isADevice(it)
            if (itID && !aDID.contains(itID))       {
                aD << it
                aDID << itID
            }
        }
    }
    return aD
}

private isADevice(thisThing)      {
    def itID = false
    try { itID = thisThing.id }    catch (all) {};
//    if (itID)       ifDebug("$thisThing | $itID")
    return itID
}


private ifDebug(msg = null, level = null)     {  if (msg && (isDebug() || level))  log."${level ?: 'debug'}" 'rooms manager: ' + msg  }

private convertRGBToHueSaturation(setColorTo)      {
    def str = setColorTo.replaceAll("\\s","").toLowerCase()
	def rgb = (colorsRGB[str][0] ?: colorsRGB['white'][0])
    ifDebug("$str | $rgb")
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

//------------------------------------------------------------------------------------------------------------------------//

@Field final Map    colorsRGB = [
    aliceblue: [[240, 248, 255], 'Alice Blue'],             antiquewhite: [[250, 235, 215], 'Antique White'],
    aqua: [[0, 255, 255], 'Aqua'],                          aquamarine: [[127, 255, 212], 'Aquamarine'],
    azure: [[240, 255, 255], 'Azure'],                      beige: [[245, 245, 220], 'Beige'],
    bisque: [[255, 228, 196], 'Bisque'],                    black: [[0, 0, 0], 'Black'],
    blanchedalmond: [[255, 235, 205], 'Blanched Almond'],   blue: [[0, 0, 255], 'Blue'],
    blueviolet: [[138, 43, 226], 'Blue Violet'],            brown: [[165, 42, 42], 'Brown'],
    burlywood: [[222, 184, 135], 'Burly Wood'],             cadetblue: [[95, 158, 160], 'Cadet Blue'],
    chartreuse: [[127, 255, 0], 'Chartreuse'],              chocolate: [[210, 105, 30], 'Chocolate'],
    coral: [[255, 127, 80], 'Coral'],                       cornflowerblue: [[100, 149, 237], 'Corn Flower Blue'],
    cornsilk: [[255, 248, 220], 'Corn Silk'],               crimson: [[220, 20, 60], 'Crimson'],
    cyan: [[0, 255, 255], 'Cyan'],                          darkblue: [[0, 0, 139], 'Dark Blue'],
    darkcyan: [[0, 139, 139], 'Dark Cyan'],                 darkgoldenrod: [[184, 134, 11], 'Dark Golden Rod'],
    darkgray: [[169, 169, 169], 'Dark Gray'],               darkgreen: [[0, 100, 0], 'Dark Green'],
    darkgrey: [[169, 169, 169], 'Dark Grey'],               darkkhaki: [[189, 183, 107], 'Dark Khaki'],
    darkmagenta: [[139, 0, 139],  'Dark Magenta'],          darkolivegreen: [[85, 107, 47], 'Dark Olive Green'],
    darkorange: [[255, 140, 0], 'Dark Orange'],             darkorchid: [[153, 50, 204], 'Dark Orchid'],
    darkred: [[139, 0, 0], 'Dark Red'],                     darksalmon: [[233, 150, 122], 'Dark Salmon'],
    darkseagreen: [[143, 188, 143], 'Dark Sea Green'],      darkslateblue: [[72, 61, 139], 'Dark Slate Blue'],
    darkslategray: [[47, 79, 79], 'Dark Slate Gray'],       darkslategrey: [[47, 79, 79], 'Dark Slate Grey'],
    darkturquoise: [[0, 206, 209], 'Dark Turquoise'],       darkviolet: [[148, 0, 211], 'Dark Violet'],
    deeppink: [[255, 20, 147], 'Deep Pink'],                deepskyblue: [[0, 191, 255], 'Deep Sky Blue'],
    dimgray: [[105, 105, 105], 'Dim Gray'],                 dimgrey: [[105, 105, 105], 'Dim Grey'],
    dodgerblue: [[30, 144, 255], 'Dodger Blue'],            firebrick: [[178, 34, 34], 'Fire Brick'],
    floralwhite: [[255, 250, 240], 'Floral White'],         forestgreen: [[34, 139, 34], 'Forest Green'],
    fuchsia: [[255, 0, 255], 'Fuchsia'],                    gainsboro: [[220, 220, 220], 'Gainsboro'],
    ghostwhite: [[248, 248, 255], 'Ghost White'],           gold: [[255, 215, 0], 'Gold'],
    goldenrod: [[218, 165, 32], 'Golden Rod'],              gray: [[128, 128, 128], 'Gray'],
    green: [[0, 128, 0], 'Green'],                          greenyellow: [[173, 255, 47], 'Green Yellow'],
    grey: [[128, 128, 128], 'Grey'],                        honeydew: [[240, 255, 240], 'Honey Dew'],
    hotpink: [[255, 105, 180], 'Hot Pink'],                 indianred: [[205, 92, 92], 'Indian Red'],
    indigo: [[75, 0, 130], 'Indigo'],                       ivory: [[255, 255, 240], 'Ivory'],
    khaki: [[240, 230, 140], 'Khaki'],                      lavender: [[230, 230, 250], 'Lavender'],
    lavenderblush: [[255, 240, 245], 'Lavender Blush'],     lawngreen: [[124, 252, 0], 'Lawn Green'],
    lemonchiffon: [[255, 250, 205], 'Lemon Chiffon'],       lightblue: [[173, 216, 230], 'Light Blue'],
    lightcoral: [[240, 128, 128], 'Light Coral'],           lightcyan: [[224, 255, 255], 'Light Cyan'],
    lightgoldenrodyellow: [[250, 250, 210], 'Light Golden Rod Yellow'],
    lightgray: [[211, 211, 211], 'Light Gray'],             lightgreen: [[144, 238, 144], 'Light Green'],
    lightgrey: [[211, 211, 211], 'Light Grey'],             lightpink: [[255, 182, 193], 'Light Pink'],
    lightsalmon: [[255, 160, 122], 'Light Salmon'],         lightseagreen: [[32, 178, 170], 'Light Sea Green'],
    lightskyblue: [[135, 206, 250], 'Light Sky Blue'],      lightslategray: [[119, 136, 153], 'Light Slate Gray'],
    lightslategrey: [[119, 136, 153], 'Light Slate Grey'],  lightsteelblue: [[176, 196, 222], 'Ligth Steel Blue'],
    lightyellow: [[255, 255, 224], 'Light Yellow'],         lime: [[0, 255, 0], 'Lime'],
    limegreen: [[50, 205, 50], 'Lime Green'],               linen: [[250, 240, 230], 'Linen'],
    magenta: [[255, 0, 255], 'Magenta'],                    maroon: [[128, 0, 0], 'Maroon'],
    mediumaquamarine: [[102, 205, 170], 'Medium Aquamarine'],
    mediumblue: [[0, 0, 205], 'Medium Blue'],               mediumorchid: [[186, 85, 211], 'Medium Orchid'],
    mediumpurple: [[147, 112, 219], 'Medium Purple'],       mediumseagreen: [[60, 179, 113], 'Medium Sea Green'],
    mediumslateblue: [[123, 104, 238], 'Medium Slate Blue'],
    mediumspringgreen: [[0, 250, 154], 'Medium Spring Green'],
    mediumturquoise: [[72, 209, 204], 'Medium Turquoise'],  mediumvioletred: [[199, 21, 133], 'Medium Violet Red'],
    midnightblue: [[25, 25, 112], 'Medium Blue'],           mintcream: [[245, 255, 250], 'Mint Cream'],
    mistyrose: [[255, 228, 225], 'Misty Rose'],             moccasin: [[255, 228, 181], 'Moccasin'],
    navajowhite: [[255, 222, 173], 'Navajo White'],         navy: [[0, 0, 128], 'Navy'],
    oldlace: [[253, 245, 230], 'Old Lace'],                 olive: [[128, 128, 0], 'Olive'],
    olivedrab: [[107, 142, 35], 'Olive Drab'],              orange: [[255, 165, 0], 'Orange'],
    orangered: [[255, 69, 0], 'Orange Red'],                orchid: [[218, 112, 214], 'Orchid'],
    palegoldenrod: [[238, 232, 170], 'Pale Golden Rod'],    palegreen: [[152, 251, 152], 'Pale Green'],
    paleturquoise: [[175, 238, 238], 'Pale Turquoise'],     palevioletred: [[219, 112, 147], 'Pale Violet Red'],
    papayawhip: [[255, 239, 213], 'Papaya Whip'],           peachpuff: [[255, 218, 185], 'Peach Cuff'],
    peru: [[205, 133, 63], 'Peru'],                         pink: [[255, 192, 203], 'Pink'],
    plum: [[221, 160, 221], 'Plum'],                        powderblue: [[176, 224, 230], 'Powder Blue'],
    purple: [[128, 0, 128], 'Purple'],                      rebeccapurple: [[102, 51, 153], 'Rebecca Purple'],
    red: [[255, 0, 0], 'Red'],                              rosybrown: [[188, 143, 143], 'Rosy Brown'],
    royalblue: [[65, 105, 225], 'Royal Blue'],              saddlebrown: [[139, 69, 19], 'Saddle Brown'],
    salmon: [[250, 128, 114], 'Salmon'],                    sandybrown: [[244, 164, 96], 'Sandy Brown'],
    seagreen: [[46, 139, 87], 'Sea Green'],                 seashell: [[255, 245, 238], 'Sea Shell'],
    sienna: [[160, 82, 45], 'Sienna'],                      silver: [[192, 192, 192], 'Silver'],
    skyblue: [[135, 206, 235], 'Sky Blue'],                 slateblue: [[106, 90, 205], 'Slate Blue'],
    slategray: [[112, 128, 144], 'Slate Gray'],             slategrey: [[112, 128, 144], 'Slate Grey'],
    snow: [[255, 250, 250], 'Snow'],                        springgreen: [[0, 255, 127], 'Spring Green'],
    steelblue: [[70, 130, 180], 'Steel Blue'],              tan: [[210, 180, 140], 'Tan'],
    teal: [[0, 128, 128], 'Teal'],                          thistle: [[216, 191, 216], 'Thistle'],
    tomato: [[255, 99, 71], 'Tomato'],                      turquoise: [[64, 224, 208], 'Turquoise'],
    violet: [[238, 130, 238], 'Violet'],                    wheat: [[245, 222, 179], 'Wheat'],
    white: [[255, 255, 255], 'White'],                      whitesmoke: [[245, 245, 245], 'White Smoke'],
    yellow: [[255, 255, 0], 'Yellow'],                      yellowgreen: [[154, 205, 50], 'Yellow Green']
]
