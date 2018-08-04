/***********************************************************************************************************************
*
*  A SmartThings device handler to allow handling rooms as devices which have states for occupancy.
*
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
*  Attribution:
*	formatDuration(...) code by ady624 for webCoRE. adpated by me to work here. original code can be found at:
*		https://github.com/ady624/webCoRE/blob/master/smartapps/ady624/webcore-piston.src/webcore-piston.groovy
*
*  Name: Rooms Occupancy
*  Source: https://github.com/adey/bangali/blob/master/devicetypes/bangali/rooms-occupancy.src/rooms-occupancy.groovy
*
***********************************************************************************************************************/

public static String version()      {  return "v0.60.0"  }
private static boolean isDebug()    {  return true  }

/***********************************************************************************************************************
*
*  Version: 0.60.0
*
*   DONE:   8/1/2018
*	1) BREAKING CHANGE: added color options in rooms to support the same colors as supported in rooms manager already.
*       you will need to update any color settings for rules after this update.
*	2) added option to select between pushable, holdable and double tap buttons on Hubitat.
*	3) added support for multiple types of speaker devices for rooms child so those are available for announcements.
*	4) added support for light bulbs to support announcement in each via color.
*	5) added support for some announcements for doors and windows in rooms.
*	6) optimized code for processing adjacent rooms when updated from rooms settings.
*	7) collapsed option for sending command to any device instead of being able to support only the operational mode
*		for ge siwtches on SmartThings. on Hubitat this supports sending any commands to any device from rules. on
*		Smartthings, since ST does not supporting any device from input, it currently supprts any commands to any
*		device that is of type switch.
*	8) added more details for temperature rules when shown in view all settings.
*	9) added option to support fans that only support on/off but not level settings.
*	10) changed how time scheduling is handled to work around how infrequent Smartthings issue with sunrise and sunset.
*	11) added support for variable volums to announcements.
*	12) swatted a bug here and a bug there.
*   13) BREAKING CHANGE: added multiple different occupancy icon sizes for use with Hubitat dashboard.
*       you will need to update any dashaboards using occupancyIcon to use one of the new icons of different sizes.
*	14) added image URL for occupancy icon for use for when Hubitat supports state attributes for image URL in dashboard.
*	15) add a few more tiles for SmartThings for missing attributes like outside temperature, vents and couple of others.
*	16) updated readme for github.
*
*  Version: 0.55.0
*
*   DONE:   7/13/2018
*   1) added support for ask alexa message for spoken announcements. (was added in v0.52.5 just announcing now)
*   2) added support on SmartThings for ge dimmer switch and setting the operational mode of the switch. (was added in v0.52.5 just announcing now)
*	3) added room button so you can use a single button to rotate thru selected room states.
*	4) added support for mode in rooms manager in which to make announcements.
*   5) added option to remove devices from health check.
*   6) fixed a couple of bugs.
*
*  Version: 0.52.5
*
*   DONE:   7/9/2018
*   1) added occupany icon to rooms device state for use with Hubitat.
*	2) added icons to settings for rooms device for Hubitat
*	3) added icons to settings for rooms manager for Hubitat
*	4) optimized performance of device health check on Hubitat.
*	5) started cleaning up code for room state replay.
*	6) added option to announce sunset and sunrise with speaker along with the color option that was already there.
*	7) added more granular control for window shade level.
*	8) added processing for room cool or heat rules on room state change.
*	9) swatted a bug here and a bug there.
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
*   6) added speech synthesis device for using things for announcements.
*   7) added random closing string to welcome and left home announcements.
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
*   1) added support for battery check and announcement on low battery.
*
*  Version: 0.14.2
*
*   DONE:   2/25/2018
*   1) added setting for announcement volume.
*   2) added support for outside door open/close announcement.
*
*  Version: 0.14.0
*
*   DONE:   2/25/2018
*   1) update device tiles to be more verbose.
*
*  Version: 0.12.7
*
*   DONE:   2/22/2018
*   1) added lock capability to put room in locked state through voice command.
*
*  Version: 0.12.6
*
*   DONE:   2/14/2018
*   1) added setting to pick state to be set when 'room device' switch turned on.
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
*   1) added support for time announce function. straightforward announcement for now but likely to get fancier ;-)
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
*       by comma and one of them will be randomly picked when making the announcement.
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

@Field final String _SmartThings = 'ST'
@Field final String _Hubitat     = 'HU'

metadata {
	definition (
    	name: "rooms occupancy",
        namespace: "bangali",
        author: "bangali")		{
		capability "Actuator"
// for hubitat comment the next line and uncomment the one after that is currently commented
		capability "Button"
//		capability "PushableButton"		// hubitat changed `Button` to `PushableButton`  2018-04-20
		capability "Sensor"
		capability "Switch"
		capability "Beacon"
// for hubitat comment the next line and uncomment the one after that is currently commented
		capability "Lock Only"
//		capability "Lock"		// hubitat does not support `Lock Only` 2018-04-07
		attribute "occupancy", "enum", ['occupied', 'checking', 'vacant', 'locked', 'reserved', 'kaput', 'donotdisturb', 'asleep', 'engaged']
// for hubitat uncomment the next few lines
//		attribute "occupancyIconS", "String"
//		attribute "occupancyIconM", "String"
//		attribute "occupancyIconL", "String"
//		attribute "occupancyIconXL", "String"
//		attribute "occupancyIconXXL", "String"
		attribute "occupancyIconURL", "String"
		attribute "alarmEnabled", "boolean"
		attribute "alarmTime", "String"
		attribute "alarmDayOfWeek", "String"
		attribute "alarmRepeat", "number"
		attribute "alarmSound", "String"
		attribute "countdown", "String"
		command "occupied"
        command "checking"
		command "vacant"
        command "locked"
		command "reserved"
		command "kaput"
		command "donotdisturb"
		command "asleep"
		command "engaged"
// for hubitat uncomment the next line
//		command "push"		// for use with hubitat useful with dashbooard 2018-04-24
		command "turnOnAndOffSwitches"
		command "turnSwitchesAllOn"
		command "turnSwitchesAllOff"
		command "turnNightSwitchesAllOn"
		command "turnNightSwitchesAllOff"
		command "alarmOffAction"
		command "updateOccupancy", ["string"]
	}

	simulator	{
	}

	preferences		{
		section("Alarm settings:")		{
			input "alarmDisabled", "bool", title: "Disable alarm?", required: true, multiple: false, defaultValue: true
			input "alarmTime", "time", title: "Alarm Time?", required: false, multiple: false
			input "alarmVolume", "number", title: "Volume?", description: "0-100%", required: (alarmTime ? true : false), range: "1..100"
			input "alarmSound", "enum", title:"Sound?", required: (alarmTime ? true : false), multiple: false, defaultValue: null,
								options: ["0":"Bell 1", "1":"Bell 2", "2":"Dogs Barking", "3":"Fire Alarm", "4":"Piano", "5":"Lightsaber"]
			input "alarmRepeat", "number", title: "Repeat?", description: "1-999", required: (alarmTime ? true : false), range: "1..999"
			input "alarmDayOfWeek", "enum", title: "Which days of the week?", required: false, multiple: false, defaultValue: null,
								options: ["ADW0":"All Days of Week","ADW8":"Monday to Friday","ADW9":"Saturday & Sunday","ADW2":"Monday", \
										  "ADW3":"Tuesday","ADW4":"Wednesday","ADW5":"Thursday","ADW6":"Friday","ADW7":"Saturday","ADW1":"Sunday"]
		}
	}

	tiles(scale: 2)		{
// old style display
/*    	multiAttributeTile(name: "occupancy", width: 2, height: 2, canChangeBackground: true)		{
			tileAttribute ("device.occupancy", key: "PRIMARY_CONTROL")		{
				attributeState "occupied", label: 'Occupied', icon:"st.Health & Wellness.health12", backgroundColor:"#90af89"
				attributeState "checking", label: 'Checking', icon:"st.Health & Wellness.health9", backgroundColor:"#616969"
				attributeState "vacant", label: 'Vacant', icon:"st.Home.home18", backgroundColor:"#32b399"
				attributeState "donotdisturb", label: 'Do Not Disturb', icon:"st.Seasonal Winter.seasonal-winter-011", backgroundColor:"#009cb2"
				attributeState "reserved", label: 'Reserved', icon:"st.Office.office7", backgroundColor:"#ccac00"
				attributeState "asleep", label: 'Asleep', icon:"st.Bedroom.bedroom2", backgroundColor:"#6879af"
				attributeState "locked", label: 'Locked', icon:"st.locks.lock.locked", backgroundColor:"#c079a3"
				attributeState "engaged", label: 'Engaged', icon:"st.locks.lock.unlocked", backgroundColor:"#ff6666"
				attributeState "kaput", label: 'Kaput', icon:"st.Outdoor.outdoor18", backgroundColor:"#95623d"
            }
       		tileAttribute ("device.status", key: "SECONDARY_CONTROL")	{
				attributeState "default", label:'${currentValue}'
			}
        }
*/
// new style display
	//	standardTile("occupancy", "device.occupancy", width: 2, height: 2, inactiveLabel: true, canChangeBackground: true)		{
		standardTile("occupancy", "device.occupancy", width: 2, height: 2, canChangeBackground: true)		{
			state "alarm", label: 'Alarm!', icon:"st.alarm.beep.beep", action:"alarmOffAction", backgroundColor:"#ff8c00"
			state "occupied", label: 'Occupied', icon:"st.Health & Wellness.health12", backgroundColor:"#90af89"
			state "checking", label: 'Checking', icon:"st.Health & Wellness.health9", backgroundColor:"#616969"
			state "vacant", label: 'Vacant', icon:"st.Home.home18", backgroundColor:"#32b399"
			state "donotdisturb", label: 'DnD', icon:"st.Seasonal Winter.seasonal-winter-011", backgroundColor:"#009cb2"
			state "reserved", label: 'Reserved', icon:"st.Office.office7", backgroundColor:"#ccac00"
			state "asleep", label: 'Asleep', icon:"st.Bedroom.bedroom2", backgroundColor:"#6879af"
			state "locked", label: 'Locked', icon:"st.locks.lock.locked", backgroundColor:"#c079a3"
			state "engaged", label: 'Engaged', icon:"st.locks.lock.unlocked", backgroundColor:"#ff6666"
			state "kaput", label: 'Kaput', icon:"st.Outdoor.outdoor18", backgroundColor:"#95623d"
        }
		valueTile("status", "device.status", inactiveLabel: false, width: 4, height: 1, decoration: "flat", wordWrap: false)	{
			state "status", label:'${currentValue}', backgroundColor:"#ffffff", defaultState: false
		}
//		valueTile("statusFiller", "device.statusFiller", inactiveLabel: false, width: 1, height: 1, decoration: "flat", wordWrap: false)	{
//			state "statusFiller", label:'${currentValue}', backgroundColor:"#ffffff", defaultState: false
//		}
		valueTile("timer", "device.timer", inactiveLabel: false, width: 1, height: 1, decoration: "flat")	{
			state "timer", label:'${currentValue}', action: "turnOnAndOffSwitches", backgroundColor:"#ffffff"
		}
		valueTile("timeInd", "device.timeInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("timeFT", label:'${currentValue}', backgroundColor:"#ffffff")
		}
//
		standardTile("motionInd", "device.motionInd", width: 1, height: 1, canChangeIcon: true) {
			state("inactive", label:'${name}', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff")
			state("active", label:'${name}', icon:"st.motion.motion.active", backgroundColor:"#00A0DC")
			state("none", label:'${name}', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff")
		}
		valueTile("luxInd", "device.luxInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("lux", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		standardTile("contactInd", "device.contactInd", width: 1, height: 1, canChangeIcon: true) {
			state("closed", label:'${name}', icon:"st.contact.contact.closed", backgroundColor:"#00A0DC")
			state("open", label:'${name}', icon:"st.contact.contact.open", backgroundColor:"#e86d13")
			state("none", label:'${name}', icon:"st.contact.contact.open", backgroundColor:"#ffffff")
		}
		standardTile("switchInd", "device.switchInd", width: 1, height: 1, canChangeIcon: true) {
			state("off", label: '${name}', action: "turnSwitchesAllOn", icon: "st.switches.switch.off", backgroundColor: "#ffffff")
			state("on", label: '${name}', action: "turnSwitchesAllOff", icon: "st.switches.switch.on", backgroundColor: "#00A0DC")
			state("none", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		standardTile("presenceInd", "device.presenceInd", width: 1, height: 1, canChangeIcon: true) {
			state("absent", label:'${name}', icon:"st.presence.tile.not-present", backgroundColor:"#ffffff")
			state("present", label:'${name}', icon:"st.presence.tile.present", backgroundColor:"#00A0DC")
			state("none", label:'${name}', icon:"st.presence.tile.not-present", backgroundColor:"#ffffff")
		}
		valueTile("presenceActionInd", "device.presenceActionInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("presenceAction", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		standardTile("musicInd", "device.musicInd", width: 1, height: 1, canChangeIcon: true)	{
			state("none", label:'none', icon:"st.Electronics.electronics12", backgroundColor:"#ffffff")
			state("pause", action: "playMusic", icon: "st.sonos.play-btn", backgroundColor: "#ffffff")
			state("play", action: "pauseMusic", icon: "st.sonos.pause-btn", backgroundColor: "#00A0DC")
		}
		valueTile("dowInd", "device.dowInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("dow", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("powerInd", "device.powerInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("power", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("pauseInd", "device.pauseInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat", wordWrap: true)	{
			state("pause", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("temperatureInd", "device.temperatureInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("temperature", label:'${currentValue}', unit:'', backgroundColors: [
/*                														// Celsius Color Range
                														[value:  0, color: "#153591"],
                														[value:  7, color: "#1E9CBB"],
                														[value: 15, color: "#90D2A7"],
                														[value: 23, color: "#44B621"],
                														[value: 29, color: "#F1D801"],
                														[value: 33, color: "#D04E00"],
                														[value: 36, color: "#BC2323"],*/
                														// Fahrenheit Color Range
                														[value: 32, color: "#153591"],
                														[value: 45, color: "#1E9CBB"],
                														[value: 59, color: "#90D2A7"],
                														[value: 73, color: "#44B621"],
                														[value: 84, color: "#F1D801"],
                														[value: 91, color: "#D04E00"],
                														[value: 97, color: "#BC2323"]])
		}
		valueTile("maintainInd", "device.maintainInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("temperature", label:'${currentValue}', backgroundColor:"#ffffff")
/*                														// Celsius Color Range
                														[[value:  0, color: "#153591"],
                														[value:  7, color: "#1E9CBB"],
                														[value: 15, color: "#90D2A7"],
                														[value: 23, color: "#44B621"],
                														[value: 29, color: "#F1D801"],
                														[value: 33, color: "#D04E00"],
                														[value: 36, color: "#BC2323"],
                														// Fahrenheit Color Range
                														[[value: 32, color: "#153591"],
                														[value: 45, color: "#1E9CBB"],
                														[value: 59, color: "#90D2A7"],
                														[value: 73, color: "#44B621"],
                														[value: 84, color: "#F1D801"],
                														[value: 91, color: "#D04E00"],
                														[value: 97, color: "#BC2323"]])*/
		}
		valueTile("outTempInd", "device.outTempInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("temperature", label:'${currentValue}', unit:'', backgroundColors: [
/*                														// Celsius Color Range
                														[value:  0, color: "#153591"],
                														[value:  7, color: "#1E9CBB"],
                														[value: 15, color: "#90D2A7"],
                														[value: 23, color: "#44B621"],
                														[value: 29, color: "#F1D801"],
                														[value: 33, color: "#D04E00"],
                														[value: 36, color: "#BC2323"],*/
                														// Fahrenheit Color Range
                														[value: 32, color: "#153591"],
                														[value: 45, color: "#1E9CBB"],
                														[value: 59, color: "#90D2A7"],
                														[value: 73, color: "#44B621"],
                														[value: 84, color: "#F1D801"],
                														[value: 91, color: "#D04E00"],
                														[value: 97, color: "#BC2323"]])
		}
		standardTile("ventInd", "device.ventInd", width: 1, height: 1, canChangeIcon: true) {
			state("none", label:'${name}', icon:"st.vents.vent", backgroundColor:"#ffffff")
			state("open", label:'${currentValue}', icon:"st.vents.vent-open", backgroundColor:"#e86d13")
			state("closed", label:'${name}', icon:"st.vents.vent-closed", backgroundColor:"#00A0DC")
		}
		standardTile("thermostatInd", "device.thermostatInd", width:1, height:1, canChangeIcon: true)	{
			state("none", label:'${currentValue}', backgroundColor:"#ffffff")
			state("off", icon: "st.thermostat.heating-cooling-off", backgroundColor: "#ffffff")
			state("auto", icon: "st.thermostat.auto", backgroundColor: "#ffffff")
			state("autoCool", icon: "st.thermostat.auto-cool", backgroundColor: "#ffffff")
			state("autoHeat", icon: "st.thermostat.heat", backgroundColor: "#ffffff")
			state("cooling", icon: "st.thermostat.cooling", backgroundColor: "#1E9CBB")
			state("heating", icon: "st.thermostat.heating", backgroundColor: "#D04E00")
		}
		valueTile("thermoOverrideInd", "device.thermoOverrideInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("thermoOverride", label:'${currentValue}', backgroundColor: "#ffffff")
		}
		standardTile("fanInd", "device.fanInd", width:1, height:1, canChangeIcon: true)		{
			state("none", label:'${currentValue}', backgroundColor:"#ffffff")
			state("off", label:'${currentValue}', icon: "st.Lighting.light24", backgroundColor: "#ffffff")
			state("low", label:'${currentValue}', icon: "st.Lighting.light24", backgroundColor: "#90D2A7")
			state("medium", label:'${currentValue}', icon: "st.Lighting.light24", backgroundColor: "#F1D801")
			state("high", label:'${currentValue}', icon: "st.Lighting.light24", backgroundColor: "#D04E00")
		}
		standardTile("contactRTInd", "device.contactRTInd", width: 1, height: 1, canChangeIcon: true) {
			state("closed", label:'${name}', icon:"st.contact.contact.closed", backgroundColor:"#00A0DC")
			state("open", label:'${name}', icon:"st.contact.contact.open", backgroundColor:"#e86d13")
			state("none", label:'${name}', icon:"st.contact.contact.open", backgroundColor:"#ffffff")
		}
		valueTile("rulesInd", "device.rulesInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("rules", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("lastRuleInd", "device.lastRuleInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("lastRule", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		standardTile("eSwitchInd", "device.eSwitchInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat") {
			state("none", label:'${currentValue}', backgroundColor:"#ffffff")
			state("off", label: '${name}', icon: "st.switches.switch.off", backgroundColor: "#ffffff")
			state("on", label: '${name}', icon: "st.switches.switch.on", backgroundColor: "#00A0DC")
		}
		standardTile("oSwitchInd", "device.oSwitchInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat") {
			state("none", label:'${currentValue}', backgroundColor:"#ffffff")
			state("off", label: '${name}', icon: "st.switches.switch.off", backgroundColor: "#ffffff")
			state("on", label: '${name}', icon: "st.switches.switch.on", backgroundColor: "#00A0DC")
		}
		standardTile("aSwitchInd", "device.aSwitchInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat") {
			state("none", label:'${currentValue}', backgroundColor:"#ffffff")
			state("off", label: '${name}', icon: "st.switches.switch.off", backgroundColor: "#ffffff")
			state("on", label: '${name}', icon: "st.switches.switch.on", backgroundColor: "#00A0DC")
		}
		valueTile("aRoomInd", "device.aRoomInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat", wordWrap: true)	{
			state("rooms", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("presenceEngagedInd", "device.presenceEngagedInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("presenceEngaged", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("busyEngagedInd", "device.busyEngagedInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("busyEngaged", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		standardTile("lSwitchInd", "device.lSwitchInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat") {
			state("none", label:'${currentValue}', backgroundColor:"#ffffff")
			state("off", label: '${name}', icon: "st.switches.switch.off", backgroundColor: "#ffffff")
			state("on", label: '${name}', icon: "st.switches.switch.on", backgroundColor: "#00A0DC")
		}
		standardTile("nSwitchInd", "device.nSwitchInd", width: 1, height: 1, canChangeIcon: true) {
			state("none", label:'${currentValue}', backgroundColor:"#ffffff")
			state("off", label: '${name}', action: "turnNightSwitchesAllOn", icon: "st.switches.switch.off", backgroundColor: "#ffffff")
			state("on", label: '${name}', action: "turnNightSwitchesAllOff", icon: "st.switches.switch.on", backgroundColor: "#00A0DC")
		}
		valueTile("wSSInd", "device.wSSInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("wSS", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("noMotionInd", "device.noMotionInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("noMotion", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("dimTimerInd", "device.dimTimerInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("dimTimer", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("noMotionEngagedInd", "device.noMotionEngagedInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("noMotionEngaged", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("noMotionAsleepInd", "device.noMotionAsleepInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("noMotionAsleep", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("turnAllOffInd", "device.turnAllOffInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("turnAllOff", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("dimByLevelInd", "device.dimByLevelInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("dimByLevel", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("eWattsInd", "device.eWattsInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("eWatts", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("aWattsInd", "device.aWattsInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("aWatts", label:'${currentValue}', backgroundColor:"#ffffff")
		}
//		valueTile("aRoomInd", "device.aRoomInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat", wordWrap: true)	{
//			state("rooms", label:'${currentValue}', backgroundColor:"#ffffff")
//		}
		standardTile("aMotionInd", "device.aMotionInd", width: 1, height: 1, canChangeIcon: true) {
			state("none", label:'${name}', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff")
			state("inactive", label:'${name}', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff")
			state("active", label:'${name}', icon:"st.motion.motion.active", backgroundColor:"#00A0DC")
		}

		valueTile("deviceList1", "device.deviceList1", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true) {
			state "deviceList1", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList2", "device.deviceList2", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true) {
			state "deviceList2", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList3", "device.deviceList3", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true) {
			state "deviceList3", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList4", "device.deviceList4", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true) {
			state "deviceList4", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList5", "device.deviceList5", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true) {
			state "deviceList5", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList6", "device.deviceList6", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true) {
			state "deviceList6", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList7", "device.deviceList7", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true) {
			state "deviceList7", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList8", "device.deviceList8", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true) {
			state "deviceList8", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList9", "device.deviceList9", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true) {
			state "deviceList9", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList10", "device.deviceList10", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true) {
			state "deviceList10", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList11", "device.deviceList11", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true) {
			state "deviceList11", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList12", "device.deviceList12", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true) {
			state "deviceList12", label:'${currentValue}', backgroundColor:"#ffffff"
		}

		standardTile("engaged", "device.engaged", width: 2, height: 2, canChangeIcon: true) {
			state "engaged", label:"Engaged", icon: "st.locks.lock.unlocked", action: "engaged", backgroundColor:"#ffffff", nextState:"toEngaged"
			state "toEngaged", label:"Updating", icon: "st.locks.lock.unlocked", backgroundColor:"#ff6666"
		}
		standardTile("vacant", "device.vacant", width: 2, height: 2, canChangeIcon: true) {
			state "vacant", label:"Vacant", icon: "st.Home.home18", action: "vacant", backgroundColor:"#ffffff", nextState:"toVacant"
			state "toVacant", label:"Updating", icon: "st.Home.home18", backgroundColor:"#32b399"
		}
/*		standardTile("checking", "device.checking", width: 2, height: 2, canChangeIcon: true) {
			state "checking", label:"Checking", icon: "st.Health & Wellness.health9", action: "checking", backgroundColor:"#ffffff", nextState:"toChecking"
			state "toChecking", label:"Updating", icon: "st.Health & Wellness.health9", backgroundColor:"#616969"
		}*/
		standardTile("occupied", "device.occupied", width: 2, height: 2, canChangeIcon: true) {
			state "occupied", label:"Occupied", icon: "st.Health & Wellness.health12", action: "occupied", backgroundColor:"#ffffff", nextState:"toOccupied"
            state "toOccupied", label:"Updating", icon:"st.Health & Wellness.health12", backgroundColor:"#90af89"
		}
		standardTile("donotdisturb", "device.donotdisturb", width: 2, height: 2, canChangeIcon: true) {
			state "donotdisturb", label:"DnD", icon: "st.Seasonal Winter.seasonal-winter-011", action: "donotdisturb", backgroundColor:"#ffffff", nextState:"toDoNotDisturb"
			state "toDoNotDisturb", label:"Updating", icon: "st.Seasonal Winter.seasonal-winter-011", backgroundColor:"#009cb2"
		}
        standardTile("reserved", "device.reserved", width: 2, height: 2, canChangeIcon: true) {
			state "reserved", label:"Reserved", icon: "st.Office.office7", action: "reserved", backgroundColor:"#ffffff", nextState:"toReserved"
			state "toReserved", label:"Updating", icon: "st.Office.office7", backgroundColor:"#ccac00"
		}
		standardTile("asleep", "device.asleep", width: 2, height: 2, canChangeIcon: true) {
			state "asleep", label:"Asleep", icon: "st.Bedroom.bedroom2", action: "asleep", backgroundColor:"#ffffff", nextState:"toAsleep"
			state "toAsleep", label:"Updating", icon: "st.Bedroom.bedroom2", backgroundColor:"#6879af"
		}
		standardTile("locked", "device.locked", width: 2, height: 2, canChangeIcon: true) {
			state "locked", label:"Locked", icon: "st.locks.lock.locked", action: "locked", backgroundColor:"#ffffff", nextState:"toLocked"
			state "toLocked", label:"Updating", icon: "st.locks.lock.locked", backgroundColor:"#c079a3"
		}
        standardTile("kaput", "device.kaput", width: 2, height: 2, canChangeIcon: true) {
			state "kaput", label:"Kaput", icon: "st.Outdoor.outdoor18", action: "kaput", backgroundColor:"#ffffff", nextState:"toKaput"
			state "toKaput", label:"Updating", icon: "st.Outdoor.outdoor18", backgroundColor:"#95623d"
		}

		valueTile("blankL", "device.blankL", width: 1, height: 1, decoration: "flat")					{ state "blankL", label:'\n' }
		valueTile("timerL", "device.timerL", width: 1, height: 1, decoration: "flat")					{ state "timerL", label:'timer' }
		valueTile("roomMotionL", "device.roomMotionL", width: 1, height: 1, decoration: "flat")			{ state "roomMotionL", label:'room\nmotion' }
		valueTile("adjRoomMotionL", "device.adjRoomMotionL", width: 1, height: 1, decoration: "flat")	{ state "adjRoomMotionL", label:'adjacent\nroom\nmotion' }
		valueTile("luxL", "device.luxL", width: 1, height: 1, decoration: "flat")						{ state "luxL", label:'room\nlux' }
		valueTile("roomContactL", "device.roomContactL", width: 1, height: 1, decoration: "flat")		{ state "roomContactL", label:'room\ncontact' }
		valueTile("presenceL", "device.presenceL", width: 1, height: 1, decoration: "flat")				{ state "presenceL", label:'presence' }
		valueTile("presenceActionL", "device.presenceActionL", width: 1, height: 1, decoration: "flat")	{ state "presenceActionL", label:'presence\naction' }
		valueTile("musicL", "device.musicL", width: 1, height: 1, decoration: "flat")					{ state "musicL", label:'music' }
		valueTile("dowL", "device.dowL", width: 1, height: 1, decoration: "flat")						{ state "dowL", label:'day of\nweek' }
		valueTile("timeL", "device.timeL", width: 1, height: 1, decoration: "flat")						{ state "timeL", label:'time\nschedule' }
		valueTile("oSwitchL", "device.oSwitchL", width: 1, height: 1, decoration: "flat")				{ state "oSwitchL", label:'occupied\nswitches' }
		valueTile("eSwitchL", "device.eSwitchL", width: 1, height: 1, decoration: "flat")				{ state "eSwitchL", label:'engaged\nswitches' }
		valueTile("aSwitchL", "device.aSwitchL", width: 1, height: 1, decoration: "flat")				{ state "aSwitchL", label:'asleep\nswitches' }
		valueTile("presenceEngagedL", "device.presenceEngagedL", width: 1, height: 1, decoration: "flat")	{ state "presenceEngagedL", label:'presence\nengaged' }
		valueTile("engagedWithBusyL", "device.engagedWithBusyL", width: 1, height: 1, decoration: "flat")	{ state "engagedWithBusyL", label:'engaged\nwith busy' }
		valueTile("lSwitchL", "device.lSwitchL", width: 1, height: 1, decoration: "flat")				{ state "lSwitchL", label:'locked\nswitch' }
		valueTile("oTimerL", "device.oTimerL", width: 1, height: 1, decoration: "flat")					{ state "oTimerL", label:'occupied\ntimer' }
		valueTile("cTimerL", "device.cTimerL", width: 1, height: 1, decoration: "flat")					{ state "cTimerL", label:'checking\ntimer' }
		valueTile("eTimerL", "device.eTimerL", width: 1, height: 1, decoration: "flat")					{ state "eTimerL", label:'engaged\ntimer' }
		valueTile("aTimerL", "device.aTimerL", width: 1, height: 1, decoration: "flat")					{ state "aTimerL", label:'asleep\ntimer' }
		valueTile("turnAllOffL", "device.turnAllOffL", width: 1, height: 1, decoration: "flat")			{ state "turnAllOffL", label:'turn\nall off' }
		valueTile("dimByL", "device.dimByL", width: 1, height: 1, decoration: "flat")					{ state "dimByL", label:'dim\nby / to\nlevel' }
		valueTile("switchL", "device.switchL", width: 1, height: 1, decoration: "flat")					{ state "switchL", label:'room\nswitches' }
		valueTile("nSwitchL", "device.nSwitchL", width: 1, height: 1, decoration: "flat")				{ state "nSwitchL", label:'night\nswitches' }
		valueTile("shadeL", "device.shadeL", width: 1, height: 1, decoration: "flat")					{ state "shadeL", label:'window\nshades' }
		valueTile("powerL", "device.powerL", width: 1, height: 1, decoration: "flat")					{ state "powerL", label:'power\nwatts' }
		valueTile("eWattsL", "device.eWattsL", width: 1, height: 1, decoration: "flat")					{ state "eWattsL", label:'engaged\nwatts' }
		valueTile("aWattsL", "device.aWattsL", width: 1, height: 1, decoration: "flat")					{ state "aWattsL", label:'asleep\nwatts' }
		valueTile("temperatureL", "device.temperatureL", width: 1, height: 1, decoration: "flat")		{ state "temperatureL", label:'room\ntemp' }
		valueTile("thermostatL", "device.thermostatL", width: 1, height: 1, decoration: "flat")			{ state "thermostatL", label:'heat /\ncool' }
		valueTile("maintainL", "device.maintainL", width: 1, height: 1, decoration: "flat")				{ state "maintainL", label:'maintain\ntemp' }
		valueTile("outTempL", "device.outTempeL", width: 1, height: 1, decoration: "flat")				{ state "temperatureL", label:'outside\ntemp' }
		valueTile("ventL", "device.ventL", width: 1, height: 1, decoration: "flat")			{ state "thermostatL", label:'vent\nlevel' }
		valueTile("fanL", "device.fanL", width: 1, height: 1, decoration: "flat")						{ state "fanL", label:'fan\nspeed' }
		valueTile("roomWindowsL", "device.roomWindowsL", width: 1, height: 1, decoration: "flat")		{ state "roomWindowsL", label:'room\nwindow' }
		valueTile("thermoOverrideL", "device.thermoOverrideL", width: 1, height: 1, decoration: "flat")	{ state "thermoOverrideL", label:'thermo\noverride' }
		valueTile("reservedL", "device.reservedL", width: 2, height: 1, decoration: "flat")				{ state "reservedL", label:'reserved' }
		valueTile("rulesL", "device.rulesL", width: 1, height: 1, decoration: "flat")					{ state "rulesL", label:'# of\nrules' }
		valueTile("lastRuleL", "device.lastRuleL", width: 1, height: 1, decoration: "flat")				{ state "lastRuleL", label:'last\nrules' }
		valueTile("adjRoomsL", "device.adjRoomsL", width: 1, height: 1, decoration: "flat")				{ state "adjRoomsL", label:'adjacent\nrooms' }

		main (["occupancy"])

		// display all tiles
		details ([	"occupancy", "occupied", "engaged",
					"vacant", "asleep", "locked",
					"status", "timerL", "timer",
					"roomMotionL", "motionInd", "adjRoomMotionL", "aMotionInd", "luxL", "luxInd",
					"roomContactL", "contactInd", "presenceL", "presenceInd", "presenceActionL", "presenceActionInd",
					"musicL", "musicInd", "dowL", "dowInd", "timeL", "timeInd",
					"oSwitchL", "oSwitchInd", "eSwitchL", "eSwitchInd", "aSwitchL", "aSwitchInd",
					"presenceEngagedL", "presenceEngagedInd", "engagedWithBusyL", "busyEngagedInd",  "lSwitchL", "lSwitchInd",
					"oTimerL", "noMotionInd", "cTimerL", "dimTimerInd", "eTimerL", "noMotionEngagedInd",
					"turnAllOffL", "turnAllOffInd", "dimByL", "dimByLevelInd", "aTimerL", "noMotionAsleepInd",
					"switchL", "switchInd", "nSwitchL", "nSwitchInd", "shadeL", "wSSInd",
					"powerL", "powerInd", "eWattsL", "eWattsInd", "aWattsL", "aWattsInd",
					"temperatureL", "temperatureInd", "thermostatL", "thermostatInd", "maintainL", "maintainInd",
					"outTempL", "outTempInd", "ventL", "ventInd", "fanL", "fanInd",
					"roomWindowsL", "contactRTInd", "thermoOverrideL", "thermoOverrideInd", "reservedL",
					"rulesL", "rulesInd", "lastRuleL", "lastRuleInd", "adjRoomsL", "aRoomInd"])

//		details (["occupancy", "engaged", "vacant", "status", "timer", "timeInd", "motionInd", "luxInd", "contactInd", "presenceInd", "switchInd", "musicInd", "occupied", "asleep", "powerInd", "pauseInd", "temperatureInd", "maintinInd", "donotdisturb", "locked", "kaput"])
		// details (["occupancy", "engaged", "vacant", "statusFiller", "status", "deviceList1", "deviceList2", "deviceList3", "deviceList4", "deviceList5", "deviceList6", "deviceList7", "deviceList8", "deviceList9", "deviceList10", "deviceList11", "deviceList12", "occupied", "donotdisturb", "reserved", "asleep", "locked", "kaput"])
		// display main and other button tiles only
		// details (["occupancy", "engaged", "vacant", "status", "occupied", "donotdisturb", "reserved", "asleep", "locked", "kaput"])
		// display main tiles and devices list only
		// details (["occupancy", "engaged", "vacant", "status", "deviceList1", "deviceList2", "deviceList3", "deviceList4", "deviceList5", "deviceList6", "deviceList7", "deviceList8", "deviceList9", "deviceList10", "deviceList11", "deviceList12")
		// display main tiles only
		// details (["occupancy", "engaged", "vacant", "status"])

	}
}

def parse(String description)	{  ifDebug("parse: $description")  }

// def installed()		{  initialize();	vacant()  }

def installed()		{  initialize()  }

def updated()		{  initialize()  }

def	initialize()	{
	unschedule()
	state
	sendEvent(name: "numberOfButtons", value: 9, descriptionText: "set number of buttons to 9.", isStateChange: true, displayed: true)
	state.timer = 0
	setupAlarmC()
	sendEvent(name: "countdown", value: '0s', descriptionText: "countdown timer: 0s", isStateChange: true, displayed: true)
}

def getHubType()        {
    if (!state.hubId)   state.hubId = location.hubs[0].id.toString()
    if (state.hubId.length() > 5)   return _SmartThings;
    else                            return _Hubitat;
}

def setupAlarmC()	{
	if (parent)		parent.setupAlarmP(alarmDisabled, alarmTime, alarmVolume, alarmSound, alarmRepeat, alarmDayOfWeek);
	if (alarmDayOfWeek != 'ADW0')      {
        state.alarmDayOfWeek = []
        switch(alarmDayOfWeek)       {
            case 'ADW1':	state.alarmDayOfWeek << 'Mon';		break
			case 'ADW2':	state.alarmDayOfWeek << 'Tue';		break
			case 'ADW3':	state.alarmDayOfWeek << 'Wed';		break
			case 'ADW4':	state.alarmDayOfWeek << 'Thu';		break
			case 'ADW5':	state.alarmDayOfWeek << 'Fri';		break
			case 'ADW6':	state.alarmDayOfWeek << 'Sat';		break
			case 'ADW7':	state.alarmDayOfWeek << 'Sun';		break
            case 'ADW8':   	['Mon','Tue','Wed','Thu','Fri'].each		{ state.alarmDayOfWeek << it };    break
            case 'ADW9':   	['Sat','Sun'].each							{ state.alarmDayOfWeek << it };    break
            default:  		state.alarmDayOfWeek = null;		break
        }
    }
    else
        state.alarmDayOfWeek = ''
	if (alarmSound)
		state.alarmSound = ["Bell 1", "Bell 2", "Dogs Barking", "Fire Alarm", "Piano", "Lightsaber"][alarmSound as Integer]
	else
		state.alarmSound = ''
	sendEvent(name: "alarmEnabled", value: ((alarmDisabled || !alarmTime) ? 'No' : 'Yes'), descriptionText: "alarm enabled is ${(!alarmDisabled)}", isStateChange: true, displayed: true)
	sendEvent(name: "alarmTime", value: "${(alarmTime ? timeToday(alarmTime, location.timeZone).format("HH:mm", location.timeZone) : '')}", descriptionText: "alarm time is ${alarmTime}", isStateChange: true, displayed: true)
	sendEvent(name: "alarmDayOfWeek", value: "$state.alarmDayOfWeek", descriptionText: "alarm days of week is $state.alarmDayOfWeek", isStateChange: true, displayed: true)
	sendEvent(name: "alarmSound", value: "$state.alarmSound", descriptionText: "alarm sound is $state.alarmSound", isStateChange: true, displayed: true)
	sendEvent(name: "alarmRepeat", value: alarmRepeat, descriptionText: "alarm sounds is repeated $alarmRepeat times", isStateChange: true, displayed: true)
}

def on()	{
	def toState = parent?.roomDeviceSwitchOnP()
	toState = (toState ? toState as String : 'occupied')
//	ifDebug("on: $toState")
	switch(toState)		{
		case 'occupied':	occupied();		break
		case 'engaged':		engaged();		break
		case 'locked':		locked();		break
		case 'asleep':		asleep();		break
		default:							break
	}
}

def	off()		{  vacant()  }

def push(button)		{
	ifDebug("$button")
	switch(button)		{
		case 1:		occupied();		break
		case 3:		vacant();		break
		case 4:		locked();		break
		case 8:		asleep();		break
		case 9:		engaged();		break
		default:					break
	}
}

def lock()		{  locked() }

def unlock()	{  vacant()  }

def occupied()	{	stateUpdate('occupied')		}

def checking()	{	stateUpdate('checking')		}

def vacant()	{	stateUpdate('vacant')		}

def donotdisturb()	{	stateUpdate('donotdisturb')		}

def reserved()	{	stateUpdate('reserved')		}

def asleep()	{	stateUpdate('asleep')		}

def locked()	{	stateUpdate('locked')		}

def engaged()	{	stateUpdate('engaged')		}

def kaput()		{	stateUpdate('kaput')		}

private	stateUpdate(newState)		{
//	def oldState = device.currentValue('occupancy')
	def oldState = state.oldState
	state.oldState = newState
	if (oldState != newState)	{
		updateOccupancy(newState)
        if (parent)		{
			parent.runInHandleSwitches(oldState, newState);
//			runIn(0, parent.runInHandleSwitches, data: [oldState: oldState, newState: newState])
		}
	}
	resetTile(newState)
}

private updateOccupancy(occupancy = null) 	{
	occupancy = occupancy?.toLowerCase()
	def hT = getHubType()
	def buttonMap = ['occupied':1, 'locked':4, 'vacant':3, 'reserved':5, 'checking':2, 'kaput':6, 'donotdisturb':7, 'asleep':8, 'engaged':9]
	if (!occupancy || !(buttonMap.containsKey(occupancy))) {
    	ifDebug("Missing or invalid parameter room occupancy: $occupancy")
        return
    }
	sendEvent(name: "occupancy", value: occupancy, descriptionText: "$device.displayName changed to $occupancy", isStateChange: true, displayed: true)
	if (hT == _Hubitat)		{
		def img = "https://cdn.rawgit.com/adey/bangali/master/resources/icons/rooms${occupancy?.capitalize()}State.png"
		sendEvent(name: "occupancyIconS", value: "<img src=$img height=25 width=25>", descriptionText: "$device.displayName $occupancy icon small", isStateChange: true, displayed: true)
		sendEvent(name: "occupancyIconM", value: "<img src=$img height=50 width=50>", descriptionText: "$device.displayName $occupancy icon medium", isStateChange: true, displayed: true)
		sendEvent(name: "occupancyIconL", value: "<img src=$img height=75 width=75>", descriptionText: "$device.displayName $occupancy icon large", isStateChange: true, displayed: true)
		sendEvent(name: "occupancyIconXL", value: "<img src=$img height=100 width=100>", descriptionText: "$device.displayName $occupancy icon extra large", isStateChange: true, displayed: true)
		sendEvent(name: "occupancyIconXXL", value: "<img src=$img height=150 width=150>", descriptionText: "$device.displayName $occupancy icon extra extra large", isStateChange: true, displayed: true)
		sendEvent(name: "occupancyIconURL", value: img, descriptionText: "$device.displayName $occupancy icon URL", isStateChange: true, displayed: true)
	}
    def button = buttonMap[occupancy]
	if (hT == _SmartThings)
		sendEvent(name: "button", value: "pushed", data: [buttonNumber: button], descriptionText: "$device.displayName button $button was pushed.", isStateChange: true)
	else
		sendEvent(name:"pushed", value:button, descriptionText: "$device.displayName button $button was pushed.", isStateChange: true)

	updateRoomStatusMsg()
}

def alarmOn()	{
	sendEvent(name: "occupancy", value: 'alarm', descriptionText: "$device.displayName alarm is on", isStateChange: true, displayed: true)
	runIn(2, alarmOff)
}

def alarmOff(endLoop = false)	{
	if (device.currentValue('occupancy') == 'alarm' || endLoop)
		sendEvent(name: "occupancy", value: "$state.oldState", descriptionText: "$device.displayName alarm is off", isStateChange: true, displayed: true)
	if (endLoop)	unschedule();
	else			runIn(1, alarmOn);
}

def alarmOffAction()	{
	ifDebug("alarmOffAction")
	unschedule()
	if (parent)		parent.ringAlarm(true);
	alarmOff(true)
}

private updateRoomStatusMsg()		{
//	sendEvent(name: "statusFiller", value: "Since:", isStateChange: true, displayed: false)
	state.statusMsg = formatLocalTime()
	sendEvent(name: "status", value: state.statusMsg, isStateChange: true, displayed: false)
}

private formatLocalTime(time = now(), format = "EEE, MMM d yyyy @ h:mm:ss a z")		{
	def formatter = new java.text.SimpleDateFormat(format)
	formatter.setTimeZone(location.timeZone)
	return formatter.format(time)
}

def deviceList(devicesMap)		{
	def devicesTitle = ['busyCheck':'Busy Check', 'engagedButton':'Button', 'presence':'Presence Sensor', 'engagedSwitch':'Engaged Switch', 'contactSensor':'Contact Sensor',
						'motionSensors':'Motion Sensor', 'switchesOn':'Switch ON', 'switchesOff':'Switch OFF', 'luxSensor':'Lux Sensor', 'adjRoomNames':'Adjacent Room',
						'awayModes':'Away Mode', 'pauseModes':'Pause Mode', 'sleepSensor':'Sleep Sensor', 'nightButton':'Night Button', 'nightSwitches':'Night Switch']
	def deviceCount = 12
	def i = 1
	devicesMap.each	{ k, v ->
		if (v)			{
			v.each	{
				if (it && i <= deviceCount)		{
					sendEvent(name: "deviceList" + i, value: (devicesTitle[k] + ":\n" + (it.hasProperty('displayName') ? it.displayName : it)), isStateChange: true, displayed: false)
					i = i +1
				}
			}
		}
	}
	for (; i <= deviceCount; i++)
		sendEvent(name: "deviceList" + i, value: null, isStateChange: true, displayed: false)
}

private	resetTile(occupancy)	{
    sendEvent(name: occupancy, value: occupancy, descriptionText: "$device.displayName reset tile $occupancy", isStateChange: true, displayed: false)
}

def generateEvent(newState = null)		{
	if (newState)		stateUpdate(newState);
}

def updateMotionInd(motionOn)		{
	def vV = 'none'
	def dD = "indicate no motion sensor"
	switch(motionOn)	{
		case 1:		vV = 'active';		dD = "indicate motion active";		break
		case 0:		vV = 'inactive';	dD = "indicate motion inactive";	break
	}
	sendEvent(name: 'motionInd', value: vV, descriptionText: dD, isStateChange: true, displayed: false)
}

def updateLuxInd(lux)		{
	sendEvent(name: 'luxInd', value: (lux == -1 ? '--' : (lux <= 100 ? lux : formatNumber(lux))), descriptionText: (lux == -1 ? "indicate no lux sensor" : "indicate lux value"), isStateChange: true, displayed: false)
}

def updateContactInd(contactClosed)		{
	def vV = 'none'
	def dD = "indicate no contact sensor"
	switch(contactClosed)	{
		case 1:		vV = 'closed';	dD = "indicate contact closed";		break
		case 0:		vV = 'open';	dD = "indicate contact open";		break
	}
	sendEvent(name: 'contactInd', value: vV, descriptionText: dD, isStateChange: true, displayed: false)
}

def updateContactRTIndC(contactStatus)		{
	def vV = 'none'
	def dD = "indicate no contact sensor"
	switch(contactStatus)	{
		case 1:		vV = 'closed';	dD = "indicate contact closed";		break
		case 0:		vV = 'open';	dD = "indicate contact open";		break
	}
	sendEvent(name: 'contactRTInd', value: vV, descriptionText: dD, isStateChange: true, displayed: false)
}

def updateSwitchInd(switchOn)		{
	def vV = '--'
	def dD = "indicate no switches to turn on in room"
	switch(switchOn)	{
		case 1:		vV = 'on';	dD = "indicate at least one switch in room is on";	break
		case 0:		vV = 'off';	dD = "indicate all switches in room is off";		break
	}
	sendEvent(name: 'switchInd', value: vV, descriptionText: dD, isStateChange: true, displayed: false)
}

def updatePresenceInd(presencePresent)		{
	def vV = 'none'
	def dD = "indicate no presence sensor"
	switch(presencePresent)		{
		case 1:		vV = 'present';	dD = "indicate presence present";		break
		case 0:		vV = 'absent';	dD = "indicate presence not present";	break
	}
	sendEvent(name: 'presenceInd', value: vV, descriptionText: dD, isStateChange: true, displayed: false)
}

def updatePresenceActionInd(presenceAction)		{
	def vV = '--'
	def dD = "indicate no presence sensor"
	switch(presenceAction)	{
		case '1':		vV = 'Arrival';		dD = "indicate arrival action when present";						break
		case '2':		vV = 'Departure';	dD = "indicate departure action when not present";					break
		case '3':		vV = 'Both';		dD = "indicate both arrival and depature action with presence";		break
		case '4':		vV = 'Neither';		dD = "indicate no action with with present";						break
	}
	sendEvent(name: 'presenceActionInd', value: vV, descriptionText: dD, isStateChange: true, displayed: false)
}

def updatePresenceEngagedInd(presenceEngaged)		{
	sendEvent(name: 'presenceEngagedInd', value: (presenceEngaged == -1 ? '--' : presenceEngaged), descriptionText: (presenceEngaged == -1 ? "indicate no presence sensor" : "indicate if presence action continuous"), isStateChange: true, displayed: false)
}

def updateBusyEngagedInd(busyEngaged)		{
	sendEvent(name: 'busyEngagedInd', value: (busyEngaged == -1 ? '--' : "$busyEngaged\ntraffic"), descriptionText: (busyEngaged == -1 ? "indicate no presence sensor" : "indicate traffic check"), isStateChange: true, displayed: false)
}

def updateDoWInd(dow)		{
	def		vV
	switch(dow)	{
		case '1':	vV = 'Monday';		break
		case '2':	vV = 'Tuesday';		break
		case '3':	vV = 'Wednesday';	break
		case '4':	vV = 'Thursday';	break
		case '5':	vV = 'Friday';		break
		case '6':	vV = 'Saturday';	break
		case '7':	vV = 'Sunday';		break
		case '8':	vV = 'M - F';		break
		case '9':	vV = 'S & S';		break
		default:	vV = 'Everyday';	break
	}
	sendEvent(name: 'dowInd', value: vV, descriptionText: "indicate run on only these days of the week: $vV", isStateChange: true, displayed: false)
}

def updateTimeInd(timeFromTo)		{
	sendEvent(name: 'timeInd', value: timeFromTo, descriptionText: "indicate time from to", isStateChange: true, displayed: false)
}

def updateTemperatureInd(temp)		{
	def tS = (location.temperatureScale ?: 'F')
	sendEvent(name: 'temperatureInd', value: (temp == -1 ? '--' : temp + '°' + tS), unit: tS,
			descriptionText: (temp == -1 ? "indicate no temperature sensor" : "indicate temperature value"), isStateChange: true, displayed: false)
}

def updateOutTempIndC(temp)		{
	def tS = (location.temperatureScale ?: 'F')
	sendEvent(name: 'outTempInd', value: (temp == -1 ? '--' : temp + '°' + tS), unit: tS,
			descriptionText: (temp == -1 ? "indicate no temperature sensor" : "indicate temperature value"), isStateChange: true, displayed: false)
}

def updateVentIndC(vent)		{
	def vL, dD
	switch(vent)	{
		case -1:	vL = 'none';		dD = "indicate no vents";		break
		case 0:		vL = 'closed';		dD = "indicate vents closed";	break
		default:	vL = 'open';		dD = "indicate vent open";	break
	}
	sendEvent(name: 'ventInd', value: (vL == 'open' ? formatNumber(vent) : vL), descriptionText: dD, isStateChange: true, displayed: false)
}

def updateMaintainIndC(temp)		{
	def tS = '°' + (location.temperatureScale ?: 'F')
	sendEvent(name: 'maintainInd', value: (temp == -1 ? '--' : temp + '°' + tS), unit: tS, descriptionText:
				(temp == -1 ? "indicate no maintain temperature" : "indicate maintain temperature value"), isStateChange: true, displayed: false)
}

def updateThermostatIndC(thermo)		{
	def vV = '--'
	def dD = "indicate no thermostat setting"
	switch(thermo)	{
		case 0:		vV = 'off';			dD = "indicate thermostat not auto";	break
		case 1:		vV = 'auto';		dD = "indicate thermostat auto";		break
		case 2:		vV = 'autoCool';	dD = "indicate thermostat auto cool";	break
		case 3:		vV = 'autoHeat';	dD = "indicate thermostat auto heat";	break
		case 4:		vV = 'cooling';		dD = "indicate thermostat cooling";		break
		case 5:		vV = 'heating';		dD = "indicate thermostat heating";		break
	}
	sendEvent(name: 'thermostatInd', value: vV, descriptionText: dD, isStateChange: true, displayed: false)
}

def updateThermoOverrideIndC(thermoOverride)		{
	sendEvent(name: 'thermoOverrideInd', value: thermoOverride, descriptionText: "indicate thermo override minutes", isStateChange: true, displayed: false)
}

def updateFanIndC(fan)		{
	def vV = '--'
	def dD = "indicate no fan setting"
	switch(fan)	{
		case 0:		vV = 'off';		dD = "indicate fan off";				break
		case 1:		vV = 'low';		dD = "indicate fan on at low speed";	break
		case 2:		vV = 'medium';	dD = "indicate fan on at medium speed";	break
		case 3:		vV = 'high';	dD = "indicate fan on at high speed";	break
	}
	sendEvent(name: 'fanInd', value: vV, descriptionText: dD, isStateChange: true, displayed: false)
}

def updateRulesInd(rules)		{
	sendEvent(name: 'rulesInd', value: (rules == -1 ? '0' : rules), descriptionText: (rules == -1 ? "indicate no rules" : "indicate rules count"), isStateChange: true, displayed: false)
}

def updateLastRuleInd(rule)		{
	sendEvent(name: 'lastRuleInd', value: (rule == -1 ? '--' : rule),
			descriptionText: (rule == -1 ? "indicate no rule executed" : "indicate rule number last executed"), isStateChange: true, displayed: false)
}

def updatePauseInd(pMode)		{
	sendEvent(name: 'pauseInd', value: (pMode == -1 ? '--' : pMode),
					descriptionText: (pMode == -1 ? "indicate no pause modes" : "indicate pause modes"), isStateChange: true, displayed: false)
}

def updatePowerInd(power)		{
	sendEvent(name: 'powerInd', value: (power == -1 ? '--' : (power <= 100 ? power : formatNumber(power))),
				descriptionText: (power == -1 ? "indicate no power sensor" : "indicate power value"), isStateChange: true, displayed: false)
}

def updateEWattsInd(eWatts)		{
	sendEvent(name: 'eWattsInd', value: (eWatts == -1 ? '--' : (eWatts <= 100 ? eWatts : formatNumber(eWatts))),
			descriptionText: (eWatts == -1 ? "indicate no engaged watts" : "indicate engaged watts value"), isStateChange: true, displayed: false)
}

def updateAWattsInd(aWatts)		{
	sendEvent(name: 'aWattsInd', value: (aWatts == -1 ? '--' : (aWatts <= 100 ? aWatts : formatNumber(aWatts))),
				descriptionText: (aWatts == -1 ? "indicate no asleep watts" : "indicate asleep watts value"), isStateChange: true, displayed: false)
}

def updateESwitchInd(switchOn)		{
	def vV = '--'
	def dD = "indicate no engaged switch"
	switch(switchOn)	{
		case 1:		vV = 'on';	dD = "indicate engaged switch is on";	break
		case 0:		vV = 'off';	dD = "indicate engaged switch is off";	break
	}
	sendEvent(name: 'eSwitchInd', value: vV, descriptionText: dD, isStateChange: true, displayed: false)
}

def updateTimersInd(noMotion, dimTimer, noMotionEngaged, noMotionAsleep)		{
	sendEvent(name: 'noMotionInd', value: (noMotion ? formatNumber(noMotion) : '--'),
				descriptionText: (noMotion ? "indicate motion timer for occupied state" : "indicate no motion timer for occupied state"), isStateChange: true, displayed: false)
	sendEvent(name: 'dimTimerInd', value: (dimTimer ? formatNumber(dimTimer) : '--'),
				descriptionText: (dimTimer ? "indicate timer for checking state" : "indicate no timer for checking state"), isStateChange: true, displayed: false)
	sendEvent(name: 'noMotionEngagedInd', value: (noMotionEngaged ? formatNumber(noMotionEngaged) : '--'),
				descriptionText: (noMotionEngaged ? "indicate motion timer for engaged state" : "indicate no motion timer for engaged state"), isStateChange: true, displayed: false)
	sendEvent(name: 'noMotionAsleepInd', value: (noMotionAsleep ? formatNumber(noMotionAsleep) : '--'),
				descriptionText: (noMotionAsleep ? "indicate motion timer for asleep state" : "indicate no motion timer for asleep state"), isStateChange: true, displayed: false)
}

def updateOSwitchInd(switchOn)		{
	def vV = '--'
	def dD = "indicate no occupied switches"
	switch(switchOn)	{
		case 1:		vV = 'on';	dD = "indicate at least one occupied switch is on";		break
		case 0:		vV = 'off';	dD = "indicate all occupied switches is off";			break
	}
	sendEvent(name: 'oSwitchInd', value: vV, descriptionText: dD, isStateChange: true, displayed: false)
}

def updateASwitchInd(switchOn)		{
	def vV = '--'
	def dD = "indicate no asleep switches"
	switch(switchOn)	{
		case 1:		vV = 'on';	dD = "indicate at least one asleep switch is on";	break
		case 0:		vV = 'off';	dD = "indicate all asleep switches are off";		break
	}
	sendEvent(name: 'aSwitchInd', value: vV, descriptionText: dD, isStateChange: true, displayed: false)
}

def updateNSwitchInd(switchOn)		{
	def vV = '--'
	def dD = "indicate no night switches"
	switch(switchOn)	{
		case 1:		vV = 'on';	dD = "indicate at least one night switch is on";	break
		case 0:		vV = 'off';	dD = "indicate all night switches are off";			break
	}
	sendEvent(name: 'nSwitchInd', value: vV, descriptionText: dD, isStateChange: true, displayed: false)
}

def updateLSwitchInd(switchOn)		{
	def vV = '--'
	def dD = "indicate no locked switch"
	switch(switchOn)	{
		case 1:		vV = 'on';	dD = "indicate locked switch is on";	break
		case 0:		vV = 'off';	dD = "indicate locked switch is off";	break
	}
	sendEvent(name: 'lSwitchInd', value: vV, descriptionText: dD, isStateChange: true, displayed: false)
}

def updateTurnAllOffInd(turnOff)		{
	sendEvent(name: 'turnAllOffInd', value: turnOff, descriptionText: "indicate if all switches should be turned off when no rules match", isStateChange: true, displayed: false)
}

def updateDimByLevelInd(dimBy, dimTo)		{
	sendEvent(name: 'dimByLevelInd', value: (dimBy == -1 && dimTo == -1 ? '-- / --' : "${(dimBy == -1 ? '--' : dimBy + '%')} /\n${(dimTo == -1 ? '--' : dimTo + '% ')}"), descriptionText: (dimBy == -1 && dimTo == -1 ? "indicate no dimming" : "indicate dimming by / to level"), isStateChange: true, displayed: false)
}

def updateAdjRoomsInd(aRooms)		{
	sendEvent(name: 'aRoomInd', value: (aRooms == -1 ? '--' : aRooms + '\nrooms'),
		descriptionText: (aRooms == -1 ? "indicate no adjacent rooms" : "indicate how many adjacent rooms"), isStateChange: true, displayed: false)
}

def updateWSSInd(wSS)		{
	sendEvent(name: 'wSSInd', value: (wSS == -1 ? '--' : wSS),
				descriptionText: (wSS == -1 ? "indicate no window shades" : "indicate window shade position"), isStateChange: true, displayed: false)
}

def updateAdjMotionInd(motionOn)		{
	def vV = 'none'
	def dD = "indicate no adjacent motion sensor"
	switch(motionOn)	{
		case 1:		vV = 'active';		dD = "indicate adjacent motion active";		break
		case 0:		vV = 'inactive';	dD = "indicate adjacent motion inactive";	break
	}
	sendEvent(name: 'aMotionInd', value: vV, descriptionText: dD, isStateChange: true, displayed: false)
}

private formatNumber(number)	{
	int n = number as Integer
	return (n > 0 ? String.format("%,d", n) : '')
}

def turnSwitchesAllOn()		{
	if (parent)		{
		parent.turnSwitchesAllOnOrOff(true)
        updateSwitchInd(1)
	}
}

def turnSwitchesAllOff()		{
	if (parent)		{
		parent.turnSwitchesAllOnOrOff(false)
		updateSwitchInd(0)
	}
}

def turnNightSwitchesAllOn()	{
 	ifDebug("turnNightSwitchesAllOn")
	if (parent)	{
		parent.dimNightLights()
		updateNSwitchInd(1)
	}
}

def turnNightSwitchesAllOff()	{
	ifDebug("turnNightSwitchesAllOff")
	if (parent)		{
		parent.nightSwitchesOff()
		updateNSwitchInd(0)
	}
}

def	turnOnAndOffSwitches()	{
	updateTimer(-1)
	if (parent)		parent.switchesOnOrOff();
}

def updateTimer(timer = 0)		{
	if (timer == -1)	timer = state.timer;
	else				state.timer = timer;
	sendEvent(name: "timer", value: (timer ?: '--'), isStateChange: true, displayed: false)
	sendEvent(name: "countdown", value: timer, descriptionText: "countdown timer: $timer", isStateChange: true, displayed: true)
}

/*
not using yet but have plans to ...

private formatduration(long value, boolean friendly = false, granularity = 's', boolean showAdverbs = false)		{
	int sign = (value >= 0) ? 1 : -1
    if (sign < 0) value = -value
	int ms = value % 1000
    value = Math.floor((value - ms) / 1000)
	int s = value % 60
    value = Math.floor((value - s) / 60)
	int m = value % 60
    value = Math.floor((value - m) / 60)
	int h = value % 24
    value = Math.floor((value - h) / 24)
	int d = value

    def parts = 0
    def partName = ''
    switch (granularity) {
    	case 'd': parts = 1; partName = 'day'; break;
    	case 'h': parts = 2; partName = 'hour'; break;
    	case 'm': parts = 3; partName = 'minute'; break;
    	case 'ms': parts = 5; partName = 'millisecond'; break;
    	default: parts = 4; partName = 'second'; break;
    }

    parts = friendly ? parts : (parts < 3 ? 3 : parts)
    def result = ''
    if (friendly) {
    	List p = []
        if (d) p.push("$d day" + (d > 1 ? 's' : ''))
        if ((parts > 1) && h) p.push("$h hour" + (h > 1 ? 's' : ''))
        if ((parts > 2) && m) p.push("$m minute" + (m > 1 ? 's' : ''))
        if ((parts > 3) && s) p.push("$s second" + (s > 1 ? 's' : ''))
        if ((parts > 4) && ms) p.push("$ms millisecond" + (ms > 1 ? 's' : ''))
        switch (p.size()) {
        	case 0:
            	result = showAdverbs ? 'now' : '0 ' + partName + 's'
                break
            case 1:
            	result = p[0]
                break
			default:
            	result = '';
                int sz = p.size()
                for (int i=0; i < sz; i++) {
                	result += (i ? (sz > 2 ? ', ' : ' ') : '') + (i == sz - 1 ? 'and ' : '') + p[i]
                }
                result = (showAdverbs && (sign > 0) ? 'in ' : '') + result + (showAdverbs && (sign < 0) ? ' ago' : '')
            	break
		}
    }
	else
    	result = (sign < 0 ? '-' : '') + (d > 0 ? sprintf("%dd ", d) : '') + sprintf("%02d:%02d", h, m) + (parts > 3 ? sprintf(":%02d", s) : '') + (parts > 4 ? sprintf(".%03d", ms) : '')

    return result
}
*/

private ifDebug(msg = null, level = null)     {  if (msg && (isDebug() || level))  log."${level ?: 'debug'}" "$device.displayName device " + msg  }
