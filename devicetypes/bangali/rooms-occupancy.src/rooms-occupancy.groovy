/*****************************************************************************************************************
*
*  A SmartThings device handler to allow handling rooms as devices which have states.
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
*  Name: Room Occupancy
*  Source: https://github.com/adey/bangali/blob/master/devicetypes/bangali/rooms-occupancy.src/rooms-occupancy.groovy
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
*****************************************************************************************************************/

metadata {
	definition (
    	name: "rooms occupancy",
        namespace: "bangali",
        author: "bangali")		{
		capability "Actuator"
		capability "Button"
		capability "Sensor"
		capability "Switch"
		attribute "occupancy", "string"
		command "occupied"
        command "checking"
		command "vacant"
        command "locked"
		command "reserved"
		command "kaput"
		command "donotdisturb"
		command "asleep"
		command "engaged"
		command "turnOnAndOffSwitches"
		command "turnSwitchesAllOn"
		command "turnSwitchesAllOff"
		command "turnAsleepSwitchesAllOn"
		command "turnAsleepSwitchesAllOff"
		command "updateOccupancy", ["string"]
	}

	simulator	{
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
		standardTile("occupancy", "device.occupancy", width: 2, height: 2, inactiveLabel: true, canChangeBackground: true)		{
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
			state("lux", label:'${currentValue}\nlux', backgroundColor:"#ffffff")
		}
		standardTile("contactInd", "device.contactInd", width: 1, height: 1, canChangeIcon: true) {
			state("closed", label:'${name}', icon:"st.contact.contact.closed", backgroundColor:"#00A0DC")
			state("open", label:'${name}', icon:"st.contact.contact.open", backgroundColor:"#e86d13")
			state("none", label:'${name}', icon:"st.contact.contact.open", backgroundColor:"#ffffff")
		}
		standardTile("switchInd", "device.switchInd", width: 1, height: 1, canChangeIcon: true) {
			state("off", label: '${name}', action: "turnSwitchesAllOn", icon: "st.switches.switch.off", backgroundColor: "#ffffff")
			state("on", label: '${name}', action: "turnSwitchesAllOff", icon: "st.switches.switch.on", backgroundColor: "#00A0DC")
			state("none", label:'${name}', icon:"st.switches.switch.off", backgroundColor:"#ffffff")
		}
		standardTile("presenceInd", "device.presenceInd", width: 1, height: 1, canChangeIcon: true) {
			state("absent", label:'${name}', icon:"st.presence.tile.not-present", backgroundColor:"#ffffff")
			state("present", label:'${name}', icon:"st.presence.tile.present", backgroundColor:"#00A0DC")
			state("none", label:'${name}', icon:"st.presence.tile.not-present", backgroundColor:"#ffffff")
		}
		standardTile("musicInd", "device.musicInd", width: 1, height: 1, canChangeIcon: true)	{
			state("none", label:'none', icon:"st.Electronics.electronics12", backgroundColor:"#ffffff")
			state("pause", action: "playMusic", icon: "st.sonos.play-btn", backgroundColor: "#ffffff")
			state("play", action: "pauseMusic", icon: "st.sonos.pause-btn", backgroundColor: "#00A0DC")
		}
		valueTile("powerInd", "device.powerInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("power", label:'${currentValue}\nwatts', backgroundColor:"#ffffff")
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
		standardTile("thermostatInd", "device.thermostatInd", width:1, height:1, canChangeIcon: true)	{
			state("off", icon: "st.thermostat.heating-cooling-off", backgroundColor: "#ffffff")
			state("auto", icon: "st.thermostat.auto", backgroundColor: "#ffffff")
			state("autoCool", icon: "st.thermostat.auto-cool", backgroundColor: "#ffffff")
			state("autoHeat", icon: "st.thermostat.heat", backgroundColor: "#ffffff")
			state("cooling", icon: "st.thermostat.cooling", backgroundColor: "#153591")
			state("heating", icon: "st.thermostat.heating", backgroundColor: "#BC2323")
			state("none", label:'none', icon:"st.thermostat.thermostat-down", backgroundColor:"#ffffff")
		}
		valueTile("rulesInd", "device.rulesInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("rules", label:'Rules:\n${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("lastRuleInd", "device.lastRuleInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("lastRule", label:'Last:\n${currentValue}', backgroundColor:"#ffffff")
		}
		standardTile("eSwitchInd", "device.eSwitchInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat") {
			state("off", label: '${name}', icon: "st.switches.switch.off", backgroundColor: "#ffffff")
			state("on", label: '${name}', icon: "st.switches.switch.on", backgroundColor: "#00A0DC")
			state("none", label:'${name}', icon:"st.switches.switch.off", backgroundColor:"#ffffff")
		}
		standardTile("cSwitchInd", "device.cSwitchInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat") {
			state("off", label: '${name}', icon: "st.switches.switch.off", backgroundColor: "#ffffff")
			state("on", label: '${name}', icon: "st.switches.switch.on", backgroundColor: "#00A0DC")
			state("none", label:'${name}', icon:"st.switches.switch.off", backgroundColor:"#ffffff")
		}
		valueTile("noMotionEInd", "device.noMotionEInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("noMotionE", label:'${currentValue}\nsecs', backgroundColor:"#ffffff")
		}
		standardTile("aSwitchInd", "device.aSwitchInd", width: 1, height: 1, canChangeIcon: true) {
			state("off", label: '${name}', action: "turnAsleepSwitchesAllOn", icon: "st.switches.switch.off", backgroundColor: "#ffffff")
			state("on", label: '${name}', action: "turnAsleepSwitchesAllOff", icon: "st.switches.switch.on", backgroundColor: "#00A0DC")
			state("none", label:'${name}', icon:"st.switches.switch.off", backgroundColor:"#ffffff")
		}
		valueTile("aRoomInd", "device.aRoomInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat", wordWrap: true)	{
			state("rooms", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		standardTile("aMotionInd", "device.aMotionInd", width: 1, height: 1, canChangeIcon: true) {
			state("inactive", label:'${name}', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff")
			state("active", label:'${name}', icon:"st.motion.motion.active", backgroundColor:"#00A0DC")
			state("none", label:'${name}', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff")
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

		main (["occupancy"])

		// display all tiles
		details (["occupancy", "occupied", "engaged", "vacant", "asleep", "locked", "status", "timer", "timeInd", "motionInd", "luxInd", "contactInd", "presenceInd", "switchInd", "musicInd", "rulesInd", "cSwitchInd", "aRoomInd", "aMotionInd", "aSwitchInd", "thermostatInd", "lastRuleInd", "eSwitchInd", "noMotionEInd", "powerInd", "temperatureInd", "maintainInd"])
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

def parse(String description)	{}

def installed()		{  initialize();	vacant()  }

def updated()	{  initialize()  }

def	initialize()	{
	sendEvent(name: "numberOfButtons", value: 9)
	state.timer = 0
}

def on()		{  occupied()  }
def	off()		{  vacant()  }

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
	def oldState = device.currentValue('occupancy')
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
	def buttonMap = ['occupied':1, 'locked':4, 'vacant':3, 'reserved':5, 'checking':2, 'kaput':6, 'donotdisturb':7, 'asleep':8, 'engaged':9]
	if (!occupancy || !(buttonMap.containsKey(occupancy))) {
    	log.debug "${device.displayName}: Missing or invalid parameter room occupancy: $occupancy"
        return
    }
	sendEvent(name: "occupancy", value: occupancy, descriptionText: "${device.displayName} changed to ${occupancy}", isStateChange: true, displayed: true)
    def button = buttonMap[occupancy]
	sendEvent(name: "button", value: "pushed", data: [buttonNumber: button], descriptionText: "$device.displayName button $button was pushed.", isStateChange: true)
	updateRoomStatusMsg()
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
    sendEvent(name: occupancy, value: occupancy, descriptionText: "reset tile ${occupancy} to ${occupancy}", isStateChange: true, displayed: false)
}

def generateEvent(newState = null)		{
//	if	(state && device.currentValue('occupancy') != state)
/*
	switch(state)		{
		case 'occupied':		runIn(0, occupied);		break;
		case 'vacant':			runIn(0, vacant);		break;
		case 'checking':		runIn(0, checking);		break;
		case 'engaged':			runIn(0, engaged);		break;
		case 'locked':			runIn(0, locked);		break;
		case 'reserved':		runIn(0, reserved);		break;
		case 'kaput':			runIn(0, kaput);		break;
		case 'donotdisturb':	runIn(0, donotdisturb);	break;
		case 'asleep':			runIn(0, asleep);		break;
		default:										break;
	}
*/
	if (newState)		stateUpdate(newState);
}

def updateMotionInd(motionOn)		{
	switch(motionOn)	{
		case 1:
			sendEvent(name: 'motionInd', value: 'active', descriptionText: "indicate motion active", isStateChange: true, displayed: false)
			break
		case 0:
			sendEvent(name: 'motionInd', value: 'inactive', descriptionText: "indicate motion inactive", isStateChange: true, displayed: false)
			break
		default:
			sendEvent(name: 'motionInd', value: 'none', descriptionText: "indicate no motion sensor", isStateChange: true, displayed: false)
			break
	}
}

def updateLuxInd(lux)		{
	if (lux == -1)
		sendEvent(name: 'luxInd', value: '--', descriptionText: "indicate no lux sensor", isStateChange: true, displayed: false)
	else
		sendEvent(name: 'luxInd', value: lux, descriptionText: "indicate lux value", isStateChange: true, displayed: false)
}

def updateContactInd(contactClosed)		{
	switch(contactClosed)	{
		case 1:
			sendEvent(name: 'contactInd', value: 'closed', descriptionText: "indicate contact closed", isStateChange: true, displayed: false)
			break
		case 0:
			sendEvent(name: 'contactInd', value: 'open', descriptionText: "indicate contact open", isStateChange: true, displayed: false)
			break
		default:
			sendEvent(name: 'contactInd', value: 'none', descriptionText: "indicate no contact sensor", isStateChange: true, displayed: false)
			break
	}
}

def updateSwitchInd(switchOn)		{
	switch(switchOn)	{
		case 1:
			sendEvent(name: 'switchInd', value: 'on', descriptionText: "indicate switch at least one switch in room is on", isStateChange: true, displayed: false)
			break
		case 0:
			sendEvent(name: 'switchInd', value: 'off', descriptionText: "indicate all switches in room is off", isStateChange: true, displayed: false)
			break
		default:
			sendEvent(name: 'switchInd', value: 'none', descriptionText: "indicate no switches to turn on in room", isStateChange: true, displayed: false)
			break
	}
}

def updatePresenceInd(presencePresent)		{
	switch(presencePresent)	{
		case 1:
			sendEvent(name: 'presenceInd', value: 'present', descriptionText: "indicate presence present", isStateChange: true, displayed: false)
			break
		case 0:
			sendEvent(name: 'presenceInd', value: 'absent', descriptionText: "indicate presence not present", isStateChange: true, displayed: false)
			break
		default:
			sendEvent(name: 'presenceInd', value: 'none', descriptionText: "indicate no presence sensor", isStateChange: true, displayed: false)
			break
	}
}

def updateTimeInd(timeFromTo)		{
	sendEvent(name: 'timeInd', value: timeFromTo, descriptionText: "indicate time from to", isStateChange: true, displayed: false)
}

def updateTemperatureInd(temp)		{
	def tS = '°' + (location.temperatureScale ?: 'F')
	if (temp == -1)
		sendEvent(name: 'temperatureInd', value: '--', unit: tS, descriptionText: "indicate no temperature sensor", isStateChange: true, displayed: false)
	else
		sendEvent(name: 'temperatureInd', value: temp, unit: tS, descriptionText: "indicate temperature value", isStateChange: true, displayed: false)
}

def updateMaintainIndC(temp)		{
	def tS = '°' + (location.temperatureScale ?: 'F')
	if (temp == -1)
		sendEvent(name: 'maintainInd', value: '--' + tS, descriptionText: "indicate no maintain temperature", isStateChange: true, displayed: false)
	else
		sendEvent(name: 'maintainInd', value: temp + tS, descriptionText: "indicate maintain temperature value", isStateChange: true, displayed: false)
}

def updateThermostatIndC(thermo)		{
	def vV = 'none'; 	def dD = "indicate no thermostat setting";
	switch(thermo)	{
		case 0:
			vV = 'off';			dD = "indicate thermostat not auto";
			break
		case 1:
			vV = 'auto';		dD = "indicate thermostat auto";
			break
		case 2:
			vV = 'autoCool';	dD = "indicate thermostat auto cool";
			break
		case 3:
			vV = 'autoHeat';	dD = "indicate thermostat auto heat";
			break
		case 4:
			vV = 'cooling';		dD = "indicate thermostat cooling";
			break
		case 5:
			vV = 'heating';		dD = "indicate thermostat heating";
			break
	}
	sendEvent(name: 'thermostatInd', value: vV, descriptionText: dD, isStateChange: true, displayed: false)
}

def updateRulesInd(rules)		{
	if (rules == -1)
		sendEvent(name: 'rulesInd', value: '0', descriptionText: "indicate no rules", isStateChange: true, displayed: false)
	else
		sendEvent(name: 'rulesInd', value: rules, descriptionText: "indicate rules count", isStateChange: true, displayed: false)
}

def updateLastRuleInd(rule)		{
	if (rule == -1)
		sendEvent(name: 'lastRuleInd', value: '--', descriptionText: "indicate no rule executed", isStateChange: true, displayed: false)
	else
		sendEvent(name: 'lastRuleInd', value: rule, descriptionText: "indicate rule number last executed", isStateChange: true, displayed: false)
}

def updatePauseInd(pMode)		{
	if (pMode == -1)
		sendEvent(name: 'pauseInd', value: '--', descriptionText: "indicate no pause modes", isStateChange: true, displayed: false)
	else
		sendEvent(name: 'pauseInd', value: pMode, descriptionText: "indicate pause modes", isStateChange: true, displayed: false)
}

def updatePowerInd(power)		{
	if (power == -1)
		sendEvent(name: 'powerInd', value: '--', descriptionText: "indicate no lux sensor", isStateChange: true, displayed: false)
	else
		sendEvent(name: 'powerInd', value: power, descriptionText: "indicate lux value", isStateChange: true, displayed: false)
}

def updateESwitchInd(switchOn)		{
	switch(switchOn)	{
		case 1:
			sendEvent(name: 'eSwitchInd', value: 'on', descriptionText: "indicate engaged switch is on", isStateChange: true, displayed: false)
			break
		case 0:
			sendEvent(name: 'eSwitchInd', value: 'off', descriptionText: "indicate engaged switch is off", isStateChange: true, displayed: false)
			break
		default:
			sendEvent(name: 'eSwitchInd', value: 'none', descriptionText: "indicate no engaged switch", isStateChange: true, displayed: false)
			break
	}
}

def updateNoMotionEInd(noMotionE)		{
	if (noMotionE == -1)
		sendEvent(name: 'noMotionEInd', value: '--', descriptionText: "indicate no motion timer for engaged state", isStateChange: true, displayed: false)
	else
		sendEvent(name: 'noMotionEInd', value: noMotionE, descriptionText: "indicate motion timer for engaged state", isStateChange: true, displayed: false)
}

def updateASwitchInd(switchOn)		{
	switch(switchOn)	{
		case 1:
			sendEvent(name: 'aSwitchInd', value: 'on', descriptionText: "indicate at least one asleep switch is on", isStateChange: true, displayed: false)
			break
		case 0:
			sendEvent(name: 'aSwitchInd', value: 'off', descriptionText: "indicate all asleep switches is off", isStateChange: true, displayed: false)
			break
		default:
			sendEvent(name: 'aSwitchInd', value: 'none', descriptionText: "indicate no asleep switches", isStateChange: true, displayed: false)
			break
	}
}

def updateAdjRoomsInd(aRoom)		{
	if (aRoom == -1)
		sendEvent(name: 'aRoomInd', value: 'no adjacent rooms', descriptionText: "indicate no adjacent rooms", isStateChange: true, displayed: false)
	else
		sendEvent(name: 'aRoomInd', value: pMode, descriptionText: "indicate adjacent rooms", isStateChange: true, displayed: false)
}

def updateAdjMotionInd(motionOn)		{
	switch(motionOn)	{
		case 1:
			sendEvent(name: 'aMotionInd', value: 'active', descriptionText: "indicate adjacent motion active", isStateChange: true, displayed: false)
			break
		case 0:
			sendEvent(name: 'aMotionInd', value: 'inactive', descriptionText: "indicate adjacent motion inactive", isStateChange: true, displayed: false)
			break
		default:
			sendEvent(name: 'aMotionInd', value: 'none', descriptionText: "indicate no adjacent motion sensor", isStateChange: true, displayed: false)
			break
	}
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

def turnAsleepSwitchesAllOn()	{
log.debug "turnAsleepSwitchesAllOn"
	if (parent)	{
		parent.dimNightLights()
		updateASwitchInd(1)
	}
}

def turnAsleepSwitchesAllOff()	{
log.debug "turnAsleepSwitchesAllOff"
	if (parent)		{
		parent.nightSwitchesOff()
		updateASwitchInd(0)
	}
}

def	turnOnAndOffSwitches()	{
	updateTimer(-1)
	if (parent)		parent.switchesOnOrOff();
}

def updateTimer(timer = 0)		{
	if (timer == -1)
		timer = state.timer
	else
		state.timer = timer
	sendEvent(name: "timer", value: (timer ?: '--'), isStateChange: true, displayed: false)
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
