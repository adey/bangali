Version: 0.99.6
---------------

DONE:   5/5/2019

1) added mode trigger for asleep state

Version: 0.99.5
---------------

DONE:   2/10/2019

1) bug fix for battery and health check times

DONE:   1/17/2019

1) exposed command optimization setting in general settings
2) check switches on is now any on instead of all on
3) room vacant check bug fix

Version: 0.99.4
---------------

DONE:   1/05/2018

1) added support for multiple power meters
2) added command optimization at the app level
3) added setting room occupancy device on / off so it can be used in the rules
4) other small fixes

Version: 0.99.3
---------------

DONE:   12/22/2018

1) standardized color notification routine
2) added asleep from and to times in asleep settings
3) various bug fixes and optimizations
- **REQUIRES SAVING ROOMS MANAGER AND ALL ROOMS CHILD APP SETTINGS AFTER UPDATING CODE. REMEMBER TO UPDATE DTH/DRIVER CODE ALSO.**
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.99.1
---------------

DONE:   12/01/2018

1) added parent/child version check.
	- first version that checks when settings are saved
	- next version will also check at runtime
	- **in 99.9% of the cases if you see any error in the logs this is the cause. i.e. rooms code was only partially updated or settings for all apps/(DTH || driver) were not saved when updating the code resulting in these errors.**
2) added feature back for room engaged with music
	- if using this feature remember to save settings for this room if its already been setup
3) added recovery for recurring processing if those schedules get cancelled
4) changed countdown timer display to 30 second interval because some users are impatient when watching the countdown to make sure it works ;-)
5) fixed bug for contact sensor spoken announcement and hidden percentage change trigger for humidity rule value
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.99.0
---------------

DONE:   11/20/2018

1) performance optimizations for hubitat along with:
	- **REQUIRES SAVING ROOMS MANAGER, ROOMS CHILD APP AND ROOMS VACATION SETTINGS AFTER UPDATING CODE SINCE SUBSCRIPTIONS HAVE CHANGED.**
	- heave code reuse.
	- deprecated code for alarm settings in rooms occupancy device
	- deprecated code for setting room to engaged with music player
	- deprecated code for setting window shades with rules
	- moved settings view code to new rooms child settings app so its take it out of runtime
		- **REQUIRES SAVING ROOMS CHILD SETTINGS CODE AS A NEW APP.**
2) added option to set room to asleep at time of day
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.96.0
---------------

DONE:   10/31/2018

1) updated code for time handling to fix issue with midnight changes for sunrise and sunset rules.
2) moved rooms vacation mode code from rooms manager to its own child app.
	- **REQUIRES SAVING ROOMS VACATION CODE AS A NEW APP. REMEMBER TO ALSO SAVE SETTINGS FOR BOTH ROOMS MANAGER AND ROOMS VACATION.**
3) updated spoken announcements code in rooms manager because HE does not support all of the same commands that ST does.
	- **PROBABLY NEED TO DO THE SAME FOR ROOMS CHILD APP AS WELL. BUT WANT TO LET THIS CHANGE BAKE IN REAL WORLD A BIT BEFORE I DO THAT.**
4) added setting for exception states for only on state change settings.
5) added 15% and 25% to light level setting.
6) added option to select 24 hours for announcement.
7) cleaning up settings text etc.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.95.1
---------------

DONE:   10/22/2018

1) changed how sunrise and sunset in rules are checked for time match to avoid issue with sunrise and sunset subscription intermittently not working.
2) fixed subscription to newly added check on and check off switches in rules.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.95.0
---------------

DONE:   10/7/2018

1) added power value, switches on and switches off triggers to rules definition and processing.
2) optimized rooms processing and how timed rules are handled.
	- **REQUIRES SAVING ROOMS MANAGER & ROOMS CHILD SETTINGS AFTER UPDATING TO THIS VERSION. OPEN SETTINGS FOR EACH AND CLICK SAVE. THATS IT.**
3) start collecting rooms state history.
4) cleanup code here and there.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.90.4
---------------

DONE:   10/3/2018

1) added color option to night lights.
2) tweaked the holiday lights twinkle a bit for HE.
3) optimized switches processing.
4) started adding sub-headers on HE.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.90.2
---------------

DONE:   10/1/2018

1) fix for holiday lights. requires resaving holiday lights settings.
2) more code optimization for frequent functions.
3) other small fixes.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.90.0
---------------

DONE:   9/20/2018

1) more optimizations for speed:
	- optimized how events are processed.
	- moved interval processing of room switches to individual rooms instead of from rooms manager.
	- moved timer/countdown handling to the device driver from the rooms child app.
	- for battery and device connectivity monitoring devices are now specified only in rooms manager and not collected from individual rooms.
	- optimized how scheduling is handled and how many timers are used.
	- reduced code size further to ~263K.
	- various other code optimizations.
	- **REQUIRES SAVING ROOMS MANAGER SETTINGS AFTER UPDATING TO THIS VERSION. OPEN ROOMS MANAGER GO TO SETTINGS AND CLICK SAVE. THATS IT.**
2) switched to using single regularly scheduled timer for both ST and HE for different reasons:
	- on ST because when ST infrastructure is under stress random processing can take really long and quickly get over the 20 second timeout.
	- on HE because all timers are blocking and i dont want any individual timer running for more than 1 second like when device monitoring check.
	- this allows rescheduling these kind of tasks after 10 seconds and 1 second of processing on ST and HE respectively.
3) thermostat indicator fix for manage vents only mode.
4) removed the lock only capability on ST because it causes issue with Alexa turning on and off the rooms occupancy device.
4) few other tweaks here and there.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.85.0
---------------

DONE:   8/10/2018

1) created CHANGELOG.md on github and moved out revision history from individual source files.
2) performance optimization for various frequent functions.
3) stopped retrieval of devices from child for battery and device connectivity check. while this works fine
	on HE on ST it can cause timeouts specially anytime the ST platform is generally stressed.
4) updated how rules are processed due to #3 above. **REQUIRES SAVING ALL ROOMS AFTER UPDATING TO THIS VERSION. JUST OPEN EACH ROOM AND CLICK SAVE. THATS IT.**
5) updated rooms to use only 1 schedule timer for time trigger in rules. previously used 2 schedule timers.
6) cleaned up files:
| file | from | to | % change |
|:---|:---|:---|---:|
| ST DTH | 58K | 56K | -3% |
| HE driver | 58K | 14K | -76% |
| app rooms manager | 80K | 60K | -25% |
| app rooms child | 360K | 269K | -25% |
7) updated humidity management to BETA.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.80.0
---------------

DONE:   8/07/2018

1) turned off logging by defaults for both the apps and the driver.
2) added option in the child app to turn on debug selectively for individual instances of rooms.
3) changed recurring processing of child switches on hubitat to every 5 mins which is now same as smartthingd.
4) removed pauses from child switch processing which cut down the processing time by 25% - 75%.
5) added a driver for hubitat which is a copy of the smartthings DTH with only the parts supported by hubitat.
6) removed all calls to update tiles in hubitat.
7) removed tiles and event publishing from the driver in hubitat.
8) changed how adjacent rooms processing on smartthings is handled on save to avoud timeouts. not an issue on hubitat.
9) added ALPHA version of humidity settings and rules.
10) added delayed off for room vents.
11) small fixes here and there.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.72.5
---------------

DONE:   8/30/2018

1) added humidity indicator to device occupancy DTH.
2) moved couple more room devices input settings to room devices page.
3) changed humidity from integer to decimal. humidity rules coming in next version.
4) coded and commented room restore settings since HE does not support updating settings input by user.
5) all room devices with multiple devices now use average values for illuminance, temperature etc.
6) changed some formatting for rooms manager settings on HE.
7) added support for critical device connectivity failure notification with SMS.
8) process temperature changes at a minimum interval of 30 seconds.
9) fixed a bug where new rooms were not being updated when using adjacent rooms.
10) cleaned up a bug here and there and optimized
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.70.2
---------------

DONE:   8/26/2018

1) small fixes.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.70.1
---------------

DONE:   8/25/2018

1) fixed mode filtering in rooms manager announcements.
2) added 5 and 10 seconds option to dim over settings in rooms child.
3) added repeat option to github updated message.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.70.0
---------------

DONE:   8/18/2018

1) removed the `lock` capability which i had included for use on HE since HE does not support 'lock only' capability.
2) fixed send event for switch and button in rooms occupany device.
3) added support for multiple button devices in room button device.
4) added dimming lights to off over configurable number of seconds.
5) added sms settings with time window for when sms should be sent.
6) added selectable time for github update sms notification.
7) added sms support for low battery notification.
8) renamed device health check to device connectivity check so folks dont confuse it with ST device health check.
9) added option to not speak sunrise and sunset announcement and only do color notification.
10) added option in battery check to add battery devices that are not otherwise used in rooms to battery check.
11) on HE added support for notify my echo for spoken messages from the room but not rooms manager yet.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.65.0
---------------

DONE:   8/13/2018

1) fixed temperature colors on DTH for ST
2) added option to select which room motion sensors triggers occupancy from VACANT state.
3) added option to trigger busy check with repeated motion to the existing checks for state change trigger.
4) added option to override OCCUPIED state trigger devices when in ENGAGED state.
5) on HE added support for deleting rules.
6) added option to override OCCUPIED and ENGAGED state trigger devices when in ASLEEP state.
7) added option to override OCCUPIED, ENGAGED and ASLEEP state trigger devices when in LOCKED state.
8) fixed a bug on power time type selection when limiting power trigger during certain hours.
9) on HE started work on save and restore settings. currently only allows viewing settings to save.
10) fixed bug for only on state change where mode and lux change would still trigger rules evaluation.
11) added github update notification via sms.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.60.0
---------------

DONE:   8/1/2018

1) BREAKING CHANGE: added color options in rooms to support the same colors as supported in rooms manager already.
    you will need to update any color settings for rules after this update.
2) added option to select between pushable, holdable and double tap buttons on Hubitat.
3) added support for multiple types of speaker devices for rooms child so those are available for announcements.
4) added support for light bulbs to support announcement in each via color.
5) added support for some announcements for doors and windows in rooms.
6) optimized code for processing adjacent rooms when updated from rooms settings.
7) collapsed option for sending command to any device instead of being able to support only the operational mode
	for ge siwtches on SmartThings. on Hubitat this supports sending any commands to any device from rules. on
	Smartthings, since ST does not supporting any device from input, it currently supprts any commands to any
	device that is of type switch.
8) added more details for temperature rules when shown in view all settings.
9) added option to support fans that only support on/off but not level settings.
10) changed how time scheduling is handled to work around how infrequent Smartthings issue with sunrise and sunset.
11) added support for variable volums to announcements.
12) swatted a bug here and a bug there.
13) BREAKING CHANGE: added multiple different occupancy icon sizes for use with Hubitat dashboard.
    you will need to update any dashaboards using occupancyIcon to use one of the new icons of different sizes.
14) added image URL for occupancy icon for use for when Hubitat supports state attributes for image URL in dashboard.
15) add a few more tiles for SmartThings for missing attributes like outside temperature, vents and couple of others.
16) updated readme for github.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.55.0
---------------

DONE:   7/13/2018

1) added support for ask alexa message for spoken announcements. (was added in v0.52.5 just announcing now)
2) added support on SmartThings for ge dimmer switch and setting the operational mode of the switch. (was added in v0.52.5 just announcing now)
3) added room button so you can use a single button to rotate thru selected room states.
4) added support for mode in rooms manager in which to make announcements.
5) added option to remove devices from health check.
6) fixed a couple of bugs.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.52.5
---------------

DONE:   7/9/2018

1) added occupany icon to rooms device state for use with Hubitat.
2) added icons to settings for rooms device for Hubitat
3) added icons to settings for rooms manager for Hubitat
4) optimized performance of device health check on Hubitat.
5) started cleaning up code for room state replay.
6) added option to announce sunset and sunrise with speaker along with the color option that was already there.
7) added more granular control for window shade level.
8) added processing for room cool or heat rules on room state change.
9) swatted a bug here and a bug there.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.50.0
---------------

DONE:   6/30/2018

1) added icons to main settings page for a room in hubitat. ST already shows these icons on the settings page.
2) added option to hide advanced settings.
3) added setting to adjust cooling and heating temperature by 0.5ªF when outside temperature is respectively over 90ªF and below 32ªF.
4) rewrote temperature management to be more consistent.
5) cleaned up rooms manager settings.
6) added option for how often device health message should be announced.
7) fixed a bug here and there.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.45.0
---------------

DONE:   6/12/2018

1) for hubitat only added support for executing any device any command to rules.
2) added option to reset ENGAGED if contact stays open but still engaged using another device like power.
3) added option for LOCKED to override other devices that trigger other states. this excludes buttons that activate another state because by pressing a button user is expressing explicit intent to switch to that state.
4) added option to view all settings page to show a non-anonymized version for user to view locally.
5) updated timer countdown to be more uniform. hopefully :-)
6) updated settings in rooms manager to be more uniform.
7) added device health check with option to notify via speaker and/or color. this checks if the device has communicated with the hub in X number of hours, where X is configured through settings.
8) for hubitat only added option to check additional devices for device health even if those devices are not used with rooms.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.40.0
---------------

DONE:   6/1/2018

1) cleaned up the settings page for rooms manager.
2) updated rooms device settings to deal with ST change of json parser which broke settings.
3) for rooms device events added a little more descriptive text.
// TODO make time range display actual time range not just the time type.
4) overhauled the view all settings page which had fallen behind.
5) added link to help text on github in app.
6) added setting for how fast room changes to VACANT if currently ASLEEP and room contact sensor is left open.
7) added setting for optional time range to set room to ENGAGED, LOCKED or ASLEEP with power wattage.
8) for CHECKING state added a lux value above which light will not get turned on for CHECKING state.
9) seperated the setting for reset ENGAGED and reset ASLEEP wtihout transitioning through the CHECKING state.
10) added fix to handle time preference settings for hubitat which does not handle timezone correctly for these settings.
11) introduced motion active check for when room state is transitioning to CHECKING state.
12) cleaned up some small bugs here and there along with some code cleanup.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.35.0
---------------

DONE:   5/11/2018

1) added option for buttons to set a state but not toggle from that state.
2) added option to set locked state with power value.
3) added option for contact sensors to not trigger engaged for use with landing or hallway areas.
4) added option to use only selective room motion sensors for motion during asleep mode for night lights.
5) changed options for when to turn on night lights.
6) added option to only run `execution` rules when state changes. this means once the state changes and the lights have been set, if you change the light settings those will not be reset till the room changes away from the current state.
7) added timer display to rooms occupancy device for asleep state.
8) organized settings in rooms manager.
9) updated docs.
10) couple of bug fixes.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.30.0
---------------

DONE:   5/5/2018

1) more doc update. latest on github: https://github.com/adey/bangali
2) added section at bottom of docs for non-obvious rules, will add more here.
3) added support for vents to be controlled with theromstat and room temperature.
4) optimized code a bit so can run switches on / off checker every 1 minute on hubitat and keep runtime under 1 second.
5) updated text and input settings for rooms manager. some of this is a BREAKING CHANGE and you will need to specify names and colors again.
6) updated settings page for rooms manager to be a bit more organized.
7) added color notification for battery devices specified in individual rooms settings.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.27.5
---------------

DONE:   5/2/2018

1) significant updates to documentation. latest on github: https://github.com/adey/bangali
2) turned down the delay between commands on hubitat
3) rooms can now be renamed which will also rename the device for the room.
4) updated text on input settings.
5) added button for occupied settings.
6) all buttons now flip between state for that button and if in that state already to checking state.
7) added push button support for hubitat dashboard.
8) swatted a bug here and a bug there.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.26.0
---------------

DONE:   4/22/2018

1) added motion support for welcome home announcement.
2) added notification by color, this currently is not constrained by announce only hours settings.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.25.0
---------------

DONE:   4/20/2018

1) get buttons working on hubitat.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.21.0
---------------

DONE:   4/19/2018

1) mostly readme updates.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.20.9
---------------

DONE:   4/15/2018

1) time today change for hubitat compatibility.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.20.7
---------------

DONE:   4/14/2018

1) added a bunch of state variable for use with hubitat dashboard tiles.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.20.5
---------------

DONE:   7/13/2018

1) changed message separator to '/' and added support for &is and &has.
2) added save and restore sound level when playing announcements.
3) restored lock only capability instead of using lock capability.
4) added support for lock state contact sensor by @BamaRayne.
5) added support for lock state switch and contact sensor to lock either on on/off or open/close by @BamaRayne.
6) added missing dot to nightSetCT range.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.20.1
---------------

DONE:   7/11/2018

1) handle pause for hubitat.
2) adapt timeTodayAfter for hubitat compatibility.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.20.0
---------------

DONE:   7/4/2018

1) change lock only to lock because hubitat does not support lock only capability.
2) add option for cooling / heating override in minutes.
3) added option to check room windoes before turning on cooling / heating.
4) cleaning up text in settings as i go along.
5) added option to not restore light level from dimming if room changes to vacant.
6) changed how auto level works by exposing by exposing as variables everything that used to be constant in the code.
7) added support for celsius values.
8) refactored a bunch of code and may have squashed a bug or two in the process.
9) refactored a bunch for hubitat compatibility.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.17.4
---------------

DONE:   3/25/2018

1) removed option to selectively turn off night switches instead of turning off all when leaving ASLEEP state.
2) made fan control standalone from heating / cooling.
3) added option to turn night lights on when entering or exiting ASLEEP state.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.17.2
---------------

DONE:   3/25/2018       FROM: @TonyFleisher

1) added option to selectively turn off night switches instead of turning off all when leaving ASLEEP state.
2) fixed a bug i introduced by turning on night switches instead of turning them off.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.17.0
---------------

DONE:   3/24/2018

1) refactored engaged state check to be more consistent.
2) added fan support to temperature settings and rules.
3) changed how heating and cooling works in it that no longer turns off the thermostat only raises and lowers the
    temperature and sets to cooling or heating mode but never turns off the thermostat.
4) added support for named holiday light strings which can be used in automation rules.
5) restructred the rules page a bit so not everything is on one page.
6) added speech synthesis device for using things for announcements.
7) added random closing string to welcome and left home announcements.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.16.0
---------------

DONE:   3/15/2018

1) code refactoring for hubitat compatibility.
2) changed occupancy attribute to enum which allows for subscription to occupancy state while string dpes not.
        thanks @mark2k on ST community forum.
3) added default settings for wake and sleep time for level and kelvin calculation for 'AL' settings.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.15.2
---------------

DONE:   3/5/2018

1) added support for icon URL setting for icon to display for each room.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.15.0
---------------

DONE:   3/2/2018

1) added icons to main settings page for room.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.14.6
---------------

DONE:   2/28/2018

1) added support for humidity sensor in rules.
2) added contact stays open notification.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.14.4
---------------

DONE:   2/26/2018

1) added support for battery check and announcement on low battery.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.14.2
---------------

DONE:   2/25/2018

1) added setting for announcement volume.
2) added support for outside door open/close announcement.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.14.0
---------------

DONE:   2/25/2018

1) update device tiles to be more verbose.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.12.6
---------------

DONE:   2/14/2018

1) added setting to pick state to be set when 'room device switch' turned on.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.12.5
---------------

DONE:   2/11/2018

1) added setting for dim to level if no bulb is on in checking state.
2) added temperature offset between thermostat and room temperature sesnor.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.12.2
---------------

DONE:   2/10/2018

1) added setting to require occupancy before triggering engaged state with power.
2) couple of bug fixes.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.12.0
---------------

DONE:   2/8/2018

1) added alarm to rooms occupancy. tested somewhat. family kind of upset with me for random alarms going off :-(
2) sunrise & sunset now support offset in minutes. so if you always wanted sunrise -30 or sunset +30 now you can.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.11.5
---------------

DONE:   2/5/2018

1) added setting for locked state timeout setting.
2) on motion active added check for power value to set room to engaged instead of occupied.
3) on occupied switch check power value to set room to engaged instead of occupied.
4) on contact close check for both occupied and checking state to set room to engaged.
5) for motion inactive with multiple motion sensors check all sensors for active before setting timer.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.11.0
---------------

DONE:   2/1/2018

// TODO
1) added support for time announce function. straightforward announcement for now but likely to get fancier ;-)
2) added rule name to display in rules page.
3) added support for power value stays below a certain number of seconds before triggering engaged or asleep.
4) added support for vacant switch. except this sets room to vacant when turned OFF not ON.
5) changed speaker device to music player in the rooms setup.
6) added support in rules to control window shade.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.10.7
---------------

DONE:   1/26/2018

1) added support for switch to set room to locked.
2) added support for random welcome home and left home messages. multiple messages can be specified delimited
    by comma and one of them will be randomly picked when making the announcement.
3) added support for switch to set room to asleep.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.10.6
---------------

DONE:   1/24/2018

1) added support for power value to set room to asleep.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.10.5
---------------

DONE:   1/23/2018

1) added rules support for maintaining temperature.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.10.0
---------------

DONE:   1/18/2018

1) added one page easy settings for first time users.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.09.9
---------------

DONE:   1/14/2018

1) added variable years to date filter.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.09.8
---------------

MERGED:   1/12/2018

1) added switches for occupied state and corresponding settings by https://github.com/TonyFleisher.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.09.7
---------------

DONE:   1/11/2018

1) addeed night switches control from device tiles indicators
2) added setting to keep room in engaged state based on continuous presence and not just presence change.
3) refactored how another room engaged works and checks for continuous presence before reseting room state.
// TODO
4) added resetting of asleep state to engaged state reset. will probably make that an option later.
// TODO
5) started work on adding thermostate to maintain room temperature. going to change this to use rules
    which will require a significant change to how rules work so wanted to push everything else out before
    starting the work to change maintain room temperature to use rules.
6) added another optimization when getting rules to allow getting conditions only.
7) move is busy check to motion handler instead of downstream.
8) added multiple rule processing with the following evaluation logic:
    a) if matching rules have no lux and no time all of those rules will be executed.
    b) if matching rules has lux the rule with the lowest lux value < current lux value will be
        executed. if there are multiple matching rules with the same lux value all of them will be executed.
    c) if matching rules has time all rules that match that current time will be executed.
    d) if matching rules have lux and time the rule with the lowest lux value < current lux value and
        matching time will be executed. if there are multiple matching rules with the same lux
        value and matching time all of them will be executed.
9) timer indicator now uses minutes when time is over 60 seconds.
10) fixed a few small bugs here and there.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.09.4
---------------

DONE:   12/30/2017

1) updated device tiles layout and added a bunch of indicators.
2) added checking state to room busy check.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.09.2
---------------

DONE:   12/25/2017

1) added option to temporarily override motion timers with rules.
2) added support for button to set room to asleep.
3) added checks for interval processing of rules.
4) some optimizations and bug fix.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.09.0
---------------

DONE:   12/23/2017

1) added color coding for temperature indicator. since ST does not allow device handler display to be conditional
    for celcius color coding user will need to edit the DTH and uncomment the celcius section and comment the
    Fahrenheit values.
2) added support for room AC and heater support to maintain room temperature. support for thermostat is coming.
3) moved all stanalone devices to their own settings page.
4) added setting to indiciate if contact sensor is on inside door or outside. e.g. contact sesnor on garage door
    would be an outside door contact sesnor. this reverses the occupancy logic so when contact sensor is open
    the door is engaged or occupied instead of when the door is closed.
5) added support for button to set room to vacant.
6) moved webCoRE_init call to the bottom of the updated() method.
7) couple of bug fixes.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.08.6
---------------

DONE:   12/17/2017

1) added support for variable text for arrival and departure announcements.
2) added support for power level to set room to engaged.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.08.5
---------------

DONE:   12/16/2017

1) added support for arrival and departure announcement.
2) added support for speaker control through rules and use of speaker to set a room to engaged.
3) bug fix to stop truncating temperature to integer.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.08.3
---------------

DONE:   12/12/2017

1) added support for wake and sleep times to calculate level and color temperature.
2) added support to process rules every 15 minutes so switches state/level/color temperature is updated even
    when there is no motion in room but there are switches on.
3) fix for continuous motion with motion sensor.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.08.1
---------------

DONE:   12/10/2017

1) added support for auto level which automatically calculates light level and optionally color temperature to
    to be set based on local sunrise and sunset times. this does not yet use circadian rhytym based calculation.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.08.0
---------------

DONE:   12/8/2017

1) added support to reset room state from ENAGED or ASLEEP when another room changes to ENGAGED or ASLEEP
2) added support to reset room state when another room changes to ENGAGED or ASLEEP.
3) removed lux threshold support from main settings since this is now available under rules.
4) fixed presence indicator for device display.
5) added support for multiple engaged switches.
6) added undimming for lights.
7) added support for centigrade display.
8) added support for multiple presence sensors.
9) couple of bug fixes.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.07.5
---------------

DONE:   12/5/2017

1) added support to reset room state from ENAGED or ASLEEP when another room changes to ENGAGED or ASLEEP
2) added right temperature scale support
3) fixed couple of bugs
4) added support for date filtering in rules
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.07.3
---------------

DONE:   12/2/2017

1) added support for executing piston instead of just turning on a light
2) added view all settings
3) added room device indicators to the room device so they can be seen in one place
4) added timer to room which counts down in increments of 5
5) some bug fixes.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.07.1
---------------

DONE:   11/28/2017

1) Fixed removed code
2) Added ability to choose action for night button
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.07.0
---------------

DONE:   11/27/2017

1) instead of adding swtiches to individual settings created rules to allow switches to be turned on and off
    and routines to be executed via this rule. VACANT state automatically turns of the switches the last rule
    turned on unless user creates a rule for VACANT state in which case the automatic turning off of switches
    on VACANT state is skipped instead the rules are checked and executed for the VACANT state.
2) some bug fixes.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.05.9
---------------

DONE:   11/21/2017

1) changed name of 'occupancyStatus' to just 'occupancy' to be consistent with ST.
2) added switches to turn on and off when room chnages to asleep. switches set to turn on are also turned off
        when room changes away from asleep.
2) some bug fixes.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.05.8
---------------

DONE:   11/20/2017

1) Changed configuration pages
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.05.7
---------------

DONE:   11/20/2017

1) added support for room busy check and setting ENGAGED state based on how busy room is.
2) added support for arrival and/or departure action when using presence sensor.
3) some bug fixes.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.05.5
---------------

DONE:   11/19/2017

1) added sleepSensor feature and corresponding settings by https://github.com/Johnwillliam.
2) some bug fixes.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.05.2
---------------

DONE:   11/16/2017

1) changed from 10 to 12 device settings and added adjacent rooms to devices display.
2) some bug fixes.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.05.1
---------------

DONE:   11/15/2017

1) added setting to select which days of week this rooms automation should run.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.05.0
---------------

DONE:   11/13/2017

1) expanded the adjacent room settings. if you specify adjacent rooms you can choose 2 options:
    i) if there is motion in an adjacent room you can force the current room to check for motion and on no
        motion change room state to vacant.
   ii) if there is motion in an adjacent room you can turn on lights in this room if it is currently vacant.
        this allows for the adjacent rooms feature to be used as a light your pathway can kind of setup.
2) some bug fixes.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.04.6
---------------

DONE:   11/12/2017

1) bug fixes around contact sensors.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.04.5
---------------

DONE:   11/10/2017

1) revamped device details screen. if users dont like it will revert back.
2) when swiches are turned off because lux rose or is outside of time window added settings to turn off both
        group of switches instead of just switches off.
3) added option to change state directly from engaged to vacant without moving to checking state.
4) removed last event from status message.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.04.3
---------------

DONE:   11/8/2017

1) added last event to status message.
2) added concept of adjacent rooms that you can select in room settings. setting does not do anything yet :-)
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.04.2
---------------

DONE:   11/6/2017

1) added setting option to allow timeout from last motion active or on motion inactive. if motion has a long timeout
        this will allow the lights to turn off quicker. but be aware motion sensor may show motion due to long
        timeout while room indicates its vacant.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.04.1
---------------

DONE:   11/5/2017

1) added support for time window to turn on/off switches when between those times. this works with other settings
        as well. like if lux is specified both the lux setting and the time setting have to be true for switches
        to be turned on or off.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.04
-------------

DONE:   11/3/2017

1) added support for presence sensor to change room state to engaged when present. when presence sensor is not
        present the room automation should work normally.
2) added support for modes which when set cause all automation to be bypassed if location is any of those modes.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.03.7
---------------

DONE:   11/1/2017

1) added support for contact sensor. when contact sensor changes to closed room will be set to checking state.
        if there is no motion afterwards room will be set to vacant. if there is motion, room will be set to
        engaged which stops room automation from kicking in till the contact is opened again.
        when contact sensor changes to open room will be set to checking state so automation can resume again.
        the only exception to this is home changing to away in which case room will be set to vacant.
2) when contact sensor is specified but no motion sensor is specified room will be changed to engaged when
        contact sensor closes.
3) if there is a motion sensor specified but no motion timeout value then room will be changed to vacant when
        motion sensor becomes inactive and room is in occupied or checking state.
4) added engaged switch which when turned on will mark the room as engaged to stop automation. this gets a
        little tricky when both engaged switch and contact sensor is defined. the contact sensor changing to
        open will reset the state back to checking. but if there is subsequent motion in the room within the
        timeout period the room will be set to occupied. or if the door is closed again and there is subsequent
        motion in the room within the timeout period the room will be set to engaged stopping automation.
5) added lights control with lux for engaged state.
6) added button push to toogle room state between engaged and checking when room state is already engaged.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.03.5
---------------

DONE:   10/29/2017

1) added support for setting level and/or color temperature for turning on switches. these will be set for
        those devices in the turn on switchs list that support it.
2) since motion inactive timeout can vary so widely amongst different brands of motion sensors chose not to
        use motion inactive event and instead timeout on motion active event for predictable user experience.
3) added support for dimming before turning off light.
4) added support for color setting which takes preference over color temperature if the switch supports it.
5) fixed small bugs.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.03.1
---------------

DONE:   10/27/2017

1) added support for lux sensor and lux value. if these values are specified:
    a) if lux value falls <= that value and switches on are selected those switches will be turned on.
    b) if lux value rises > that value and switches off are selected those switches will be turned off.
    c) switches on with motion will be turned on only when lux value is <= that value.
2) fixed small bugs.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.03
-------------

DONE:

1) added new states do not disturb and asleep, on user demand. these have button value of 7 and 8 respectively.
2) locked and kaput moved below the fold and replaced on-screen with do not disturb and asleep respectively.
3) cleaned up settings display.
4) changed roomOccupancy to occupancyStatus. sorry for the compatibility breaking change. by user demand.
5) updated some interstitial text.
6) if no motion sensor specified but there is a timeout value >= 5 and turn off switches specified, those
         switches will be switched off after timeout seconds if room is vacant.
7) added new engaged state, on user demand. this button has a button value of 9 respectively.
8) if room state changes any pending actions are cancelled.
-----------------------------------------------------------------------------------------------------------------------------------------------------

Version: 0.02
-------------

DONE:

0) Initial commit.
1) added support for multiple away modes. when home changes to any these modes room is set to vacant but
         only if room is in occupied or checking state.
2) added subscription for motion devices so if room is vacant or checking move room state to occupied.
3) added support for switches to be turned on when room is changed to occupied.
4) added support for switches to be turned off when room is changed to vacant, different switches from #3.
5) added button push events to tile commands, where occupied = button 1, ..., kaput = button 6 so it is
        supported by ST Smart Lighting smartapp.
-----------------------------------------------------------------------------------------------------------------------------------------------------
