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
*  Name: Rooms Occupancy
*  Source: https://github.com/adey/bangali/blob/master/devicetypes/bangali/rooms-occupancy.src/rooms-occupancy.groovy
*
***********************************************************************************************************************/

public static String version()		{  return "v1.0.0"  }
private static boolean isDebug()	{  return false  }

final String _SmartThings()	{ return 'ST' }
final String _Hubitat()		{ return 'HU' }

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
		capability "Health Check"
// for hubitat comment the next line since this capability is not supported
//		capability "Lock Only"
		attribute "occupancy", "enum", ['occupied', 'checking', 'vacant', 'locked', 'reserved', 'kaput', 'donotdisturb', 'asleep', 'engaged']
// for hubitat uncomment the next few lines ONLY if you want to use the icons on dashboard
//		attribute "occupancyIconS", "String"
//		attribute "occupancyIconM", "String"
//		attribute "occupancyIconL", "String"
//		attribute "occupancyIconXL", "String"
//		attribute "occupancyIconXXL", "String"
		attribute "occupancyIconURL", "String"
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
	}

	simulator	{
	}

	preferences		{
	}

	//
	// REMOVE THE FOLLOWING FOR HUBITAT		<<<<<
	//

	tiles(scale: 2)		{
		standardTile("occupancy", "device.occupancy", width: 2, height: 2, canChangeBackground: true)		{
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
		valueTile("timer", "device.timer", inactiveLabel: false, width: 1, height: 1, decoration: "flat")	{
			state "timer", label:'${currentValue}', action: "turnOnAndOffSwitches", backgroundColor:"#ffffff"
		}
		valueTile("timeInd", "device.timeInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("timeFT", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		standardTile("motionInd", "device.motionInd", width: 1, height: 1, canChangeIcon: true)	{
			state("inactive", label:'${name}', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff")
			state("active", label:'${name}', icon:"st.motion.motion.active", backgroundColor:"#00A0DC")
			state("none", label:'${name}', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff")
		}
		valueTile("luxInd", "device.luxInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("lux", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("humidityInd", "device.humidityInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("humidity", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		standardTile("contactInd", "device.contactInd", width: 1, height: 1, canChangeIcon: true)	{
			state("closed", label:'${name}', icon:"st.contact.contact.closed", backgroundColor:"#00A0DC")
			state("open", label:'${name}', icon:"st.contact.contact.open", backgroundColor:"#e86d13")
			state("none", label:'${name}', icon:"st.contact.contact.open", backgroundColor:"#ffffff")
		}
		standardTile("switchInd", "device.switchInd", width: 1, height: 1, canChangeIcon: true)	{
			state("off", label: '${name}', action: "turnSwitchesAllOn", icon: "st.switches.switch.off", backgroundColor: "#ffffff")
			state("on", label: '${name}', action: "turnSwitchesAllOff", icon: "st.switches.switch.on", backgroundColor: "#00A0DC")
			state("none", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		standardTile("presenceInd", "device.presenceInd", width: 1, height: 1, canChangeIcon: true)	{
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
			state("temperature", label:'${currentValue}', unit:'', backgroundColors: [
/*                														// Celsius Color Range
																		[[value:  0, color: "#153591"],
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
		standardTile("ventInd", "device.ventInd", width: 1, height: 1, canChangeIcon: true)	{
			state("none", label:'none', icon:"st.vents.vent", backgroundColor:"#ffffff")
			state("closed", label:'closed', icon:"st.vents.vent-closed", backgroundColor:"#00A0DC")
			state("open", label:'${currentValue}', icon:"st.vents.vent-open", backgroundColor:"#e86d13")
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
		standardTile("contactRTInd", "device.contactRTInd", width: 1, height: 1, canChangeIcon: true)	{
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
		standardTile("eSwitchInd", "device.eSwitchInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("none", label:'${currentValue}', backgroundColor:"#ffffff")
			state("off", label: '${name}', icon: "st.switches.switch.off", backgroundColor: "#ffffff")
			state("on", label: '${name}', icon: "st.switches.switch.on", backgroundColor: "#00A0DC")
		}
		standardTile("oSwitchInd", "device.oSwitchInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("none", label:'${currentValue}', backgroundColor:"#ffffff")
			state("off", label: '${name}', icon: "st.switches.switch.off", backgroundColor: "#ffffff")
			state("on", label: '${name}', icon: "st.switches.switch.on", backgroundColor: "#00A0DC")
		}
		standardTile("aSwitchInd", "device.aSwitchInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
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
		standardTile("lSwitchInd", "device.lSwitchInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("none", label:'${currentValue}', backgroundColor:"#ffffff")
			state("off", label: '${name}', icon: "st.switches.switch.off", backgroundColor: "#ffffff")
			state("on", label: '${name}', icon: "st.switches.switch.on", backgroundColor: "#00A0DC")
		}
		standardTile("nSwitchInd", "device.nSwitchInd", width: 1, height: 1, canChangeIcon: true)	{
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
		standardTile("aMotionInd", "device.aMotionInd", width: 1, height: 1, canChangeIcon: true)	{
			state("none", label:'${name}', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff")
			state("inactive", label:'${name}', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff")
			state("active", label:'${name}', icon:"st.motion.motion.active", backgroundColor:"#00A0DC")
		}
		valueTile("deviceList1", "device.deviceList1", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true)	{
			state "deviceList1", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList2", "device.deviceList2", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true)	{
			state "deviceList2", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList3", "device.deviceList3", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true)	{
			state "deviceList3", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList4", "device.deviceList4", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true)	{
			state "deviceList4", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList5", "device.deviceList5", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true)	{
			state "deviceList5", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList6", "device.deviceList6", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true)	{
			state "deviceList6", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList7", "device.deviceList7", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true)	{
			state "deviceList7", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList8", "device.deviceList8", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true)	{
			state "deviceList8", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList9", "device.deviceList9", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true)	{
			state "deviceList9", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList10", "device.deviceList10", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true)	{
			state "deviceList10", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList11", "device.deviceList11", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true)	{
			state "deviceList11", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList12", "device.deviceList12", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true)	{
			state "deviceList12", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		standardTile("engaged", "device.engaged", width: 2, height: 2, canChangeIcon: true)	{
			state "engaged", label:"Engaged", icon: "st.locks.lock.unlocked", action: "engaged", backgroundColor:"#ffffff", nextState:"toEngaged"
			state "toEngaged", label:"Updating", icon: "st.locks.lock.unlocked", backgroundColor:"#ff6666"
		}
		standardTile("vacant", "device.vacant", width: 2, height: 2, canChangeIcon: true)	{
			state "vacant", label:"Vacant", icon: "st.Home.home18", action: "vacant", backgroundColor:"#ffffff", nextState:"toVacant"
			state "toVacant", label:"Updating", icon: "st.Home.home18", backgroundColor:"#32b399"
		}
		standardTile("occupied", "device.occupied", width: 2, height: 2, canChangeIcon: true)	{
			state "occupied", label:"Occupied", icon: "st.Health & Wellness.health12", action: "occupied", backgroundColor:"#ffffff", nextState:"toOccupied"
			state "toOccupied", label:"Updating", icon:"st.Health & Wellness.health12", backgroundColor:"#90af89"
		}
		standardTile("donotdisturb", "device.donotdisturb", width: 2, height: 2, canChangeIcon: true)	{
			state "donotdisturb", label:"DnD", icon: "st.Seasonal Winter.seasonal-winter-011", action: "donotdisturb", backgroundColor:"#ffffff", nextState:"toDoNotDisturb"
			state "toDoNotDisturb", label:"Updating", icon: "st.Seasonal Winter.seasonal-winter-011", backgroundColor:"#009cb2"
		}
		standardTile("reserved", "device.reserved", width: 2, height: 2, canChangeIcon: true)	{
			state "reserved", label:"Reserved", icon: "st.Office.office7", action: "reserved", backgroundColor:"#ffffff", nextState:"toReserved"
			state "toReserved", label:"Updating", icon: "st.Office.office7", backgroundColor:"#ccac00"
		}
		standardTile("asleep", "device.asleep", width: 2, height: 2, canChangeIcon: true)	{
			state "asleep", label:"Asleep", icon: "st.Bedroom.bedroom2", action: "asleep", backgroundColor:"#ffffff", nextState:"toAsleep"
			state "toAsleep", label:"Updating", icon: "st.Bedroom.bedroom2", backgroundColor:"#6879af"
		}
		standardTile("locked", "device.locked", width: 2, height: 2, canChangeIcon: true)	{
			state "locked", label:"Locked", icon: "st.locks.lock.locked", action: "locked", backgroundColor:"#ffffff", nextState:"toLocked"
			state "toLocked", label:"Updating", icon: "st.locks.lock.locked", backgroundColor:"#c079a3"
		}
		standardTile("kaput", "device.kaput", width: 2, height: 2, canChangeIcon: true)	{
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
		valueTile("outTempL", "device.outTempeL", width: 1, height: 1, decoration: "flat")				{ state "outTempL", label:'outside\ntemp' }
		valueTile("ventL", "device.ventL", width: 1, height: 1, decoration: "flat")						{ state "ventL", label:'vent\nlevel' }
		valueTile("fanL", "device.fanL", width: 1, height: 1, decoration: "flat")						{ state "fanL", label:'fan\nspeed' }
		valueTile("roomWindowsL", "device.roomWindowsL", width: 1, height: 1, decoration: "flat")		{ state "roomWindowsL", label:'room\nwindow' }
		valueTile("thermoOverrideL", "device.thermoOverrideL", width: 1, height: 1, decoration: "flat")	{ state "thermoOverrideL", label:'thermo\noverride' }
		valueTile("humidityL", "device.humidityL", width: 1, height: 1, decoration: "flat")				{ state "humidityL", label:'humidity' }
		valueTile("reservedL", "device.reservedL", width: 2, height: 1, decoration: "flat")				{ state "reservedL", label:'reserved' }
		valueTile("rulesL", "device.rulesL", width: 1, height: 1, decoration: "flat")					{ state "rulesL", label:'# of\nrules' }
		valueTile("lastRuleL", "device.lastRuleL", width: 1, height: 1, decoration: "flat")				{ state "lastRuleL", label:'last\nrules' }
		valueTile("adjRoomsL", "device.adjRoomsL", width: 1, height: 1, decoration: "flat")				{ state "adjRoomsL", label:'adjacent\nrooms' }

		main (["occupancy"])

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
					"roomWindowsL", "contactRTInd", "thermoOverrideL", "thermoOverrideInd", "humidityL", "humidityInd",
					"rulesL", "rulesInd", "lastRuleL", "lastRuleInd", "adjRoomsL", "aRoomInd"])
	}

	// REMOVE TILL HERE FOR HUBITAT		<<<<<

}

def parse(String description)	{  ifDebug("parse: $description")  }

def installed()		{  initialize()  }

def updated()		{  initialize()  }

def	initialize()	{
	unschedule()
	sendEvent(name: "numberOfButtons", value: 9, descriptionText: "set number of buttons to 9.", displayed: true)
	state.timer = 0
	sendEvent(name: "countdown", value: '0s', descriptionText: "countdown timer: 0s", displayed: true)
	if (getHubType() == _SmartThings)		{
		sendEvent(name: "DeviceWatch-DeviceStatus", value: "online")
		sendEvent(name: "healthStatus", value: "online")
		sendEvent(name: "DeviceWatch-Enroll", value: [protocol: "cloud", scheme:"untracked"].encodeAsJson(), displayed: false)
	}
}

def getHubType()	{
	if (!state.hubId)	state.hubId = location.hubs[0].id.toString()
	return (state.hubId.length() > 5 ? _SmartThings() : _Hubitat())
}

def on()	{
	if (!state.onState)		state.onState = parent?.roomDeviceSwitchOnP().toString();
	switch(state.onState ?: 'occupied')		{
		case 'occupied':	occupied();		break
		case 'engaged':		engaged();		break
		case 'locked':		locked();		break
		case 'asleep':		asleep();		break
		default:							break
	}
	switchOnOff(true)
}

def setOnStateC(e)		{  state.onState = (e ? e.toString() : 'occupied')  }

def	off()		{
	vacant()
	switchOnOff(false)
}

private switchOnOff(on)	{
	def switchState = (on ? 'on' : 'off')
	sendEvent(name: "switch", value: "$switchState", descriptionText: "$device.displayName is $switchState", displayed: true)
}

def push(buton)		{
	ifDebug("$buton")
	def hT = getHubType()
	switch(buton)		{
		case 1:		occupied();		break
		case 3:		vacant();		break
		case 4:		locked();		break
		case 8:		asleep();		break
		case 9:		engaged();		break
		default:
			if (hT != _Hubitat())
				sendEvent(name: "button", value: "pushed", data: [buttonNumber: "$buton"], descriptionText: "$device.displayName button $buton was pushed", displayed: true)
			else
				sendEvent(name: "pushableButton", value: buton, descriptionText: "$device.displayName button $buton was pushed", displayed: true)
			break
	}
}

def lock()		{  locked() }

def unlock()	{  vacant()  }

def occupied(vM = false)		{  parent.occupied(vM)  }

def checking(vM = false)		{  parent.checking(vM)  }

def vacant(vM = false)			{  parent.vacant(vM)  }

def donotdisturb(vM = false)	{  parent.donotdisturb(vM)  }

def reserved(vM = false)		{  parent.reserved(vM)  }

def asleep(vM = false)			{  parent.asleep(vM)  }

def locked(vM = false)			{  parent.locked(vM)  }

def engaged(vM = false)			{  parent.engaged(vM)  }

def kaput(vM = false)			{  parent.kaput(vM)  }

/*
private	resetTile(occupancy)	{
	sendEvent(name: occupancy, value: occupancy, descriptionText: "$device.displayName reset tile $occupancy", displayed: false)
}

*/

def turnSwitchesAllOn()		{
	if (parent)		parent.turnSwitchesAllOnOrOff(true);
//		if (getHubType() != _Hubitat())		updateSwitchInd(1);
}

def turnSwitchesAllOff()		{
	if (parent)		parent.turnSwitchesAllOnOrOff(false);
//		if (getHubType() != _Hubitat())		updateSwitchInd(0);
}

def turnNightSwitchesAllOn()	{
 	ifDebug("turnNightSwitchesAllOn")
	if (parent)		parent.dimNightLights();
//		if (getHubType() != _Hubitat())		updateNSwitchInd(1)
}

def turnNightSwitchesAllOff()	{
	ifDebug("turnNightSwitchesAllOff")
	if (parent)		parent.nightSwitchesOff();
//		if (getHubType() != _Hubitat())		updateNSwitchInd(0)
}

def	turnOnAndOffSwitches()	{
	if (parent)		parent.switchesOnOrOff();
//	setupTimer(-1)
}

private ifDebug(msg = null, level = null)	{  if (msg && (isDebug() || level == 'error'))	log."${level ?: 'debug'}" " $device.displayName device: " + msg  }
