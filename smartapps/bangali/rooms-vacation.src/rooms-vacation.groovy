/***********************************************************************************************************************
*
*  A SmartThings child smartapp which creates the "room" device using the rooms occupancy DTH and allows executing
*   various rules based on occupancy state. this alllows lights and other devices to be turned on and off based on
*   occupancy. it also allows many other actions like executing a routine or piston, turning on/off music and much
*   more. see the wiki for more details. (note wiki is still in progress. ok there is really no content in the wiki.
*   yet. but this is to reinforce my intention of putting the wiki together. ;-) will update with link once in place.)
*
*  Copyright (C) 2018 bangali
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
*  Name: Rooms Vacation
*   Source: https://github.com/adey/bangali/blob/master/smartapps/bangali/rooms-vacation.src/rooms-vacation.groovy
*
***********************************************************************************************************************/

public static String version()		{  return "v0.96.0"  }
boolean isDebug()					{  return false  }

definition	(
	name: "rooms vacation",
	namespace: "bangali",
	parent: "bangali:rooms manager",
	author: "bangali",
	description: "DO NOT INSTALL DIRECTLY. Rooms manager will create a new vacation manager app from this code.",
	category: "My Apps",
	iconUrl: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomOccupancy.png",
	iconX2Url: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomOccupancy@2x.png",
	iconX3Url: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomOccupancy@3x.png"
)

preferences		{  page(name: "mainPage", content: "mainPage")  }

def mainPage()	{
	if (state.vacaDisabled == null)		state.vacaDisabled = true;
	app.updateLabel('# ' + app.name + (!state.vacaDisabled ? ' is <FONT COLOR="33cc33">enabled</FONT>' : ''))
	def roomNames = parent.getRoomNames(app.id)
	def hT = getHubType()
	dynamicPage(name:"mainPage", title:"Vacation Settings", install:true, uninstall:false, submitOnChange:true)	{
		section {
			if (hT == 'HE')
				if (state.vacaDisabled)
	            	input "enableVaca", "button", title:"Enable Vacation Mode"
				else
					input "disableVaca", "button", title:"Disable Vacation Mode"
			else
				input 'vacaState', 'bool', title:'Vacation mode enabled?', required:true
			input 'vacaRooms', 'enum', title:'Replay state for which rooms?', required:false, multiple:true, options:roomNames
			input 'vacaStates', 'enum', title:'Replay which states?', required:true, multiple:true, defaultValue:['occupied', 'engaged'], options:['asleep', 'occupied', 'engaged']
			input 'replayAsIs', 'bool', title:'Replay as is?', required:true
			paragraph "VACATION MODE IS STILL WIP. WHILE SETTINGS AND ROOMS STATE DATA FOR THE SELECTED ROOMS WILL BE SAVED, THE APP ITSELF IS NONFUNCTIONAL."
		}
	}
}

private subHeaders(str)		{
	if (str.size() > 50)	str = str.substring(0, 50);
	return "<div style='text-align:center;background-color:#bbbbbb;color:#ffffff;'>${str.toUpperCase().center(50)}</div>"
}

def installed()		{ initialize() }

def updated()		{
	def nowTime = now()
	ifDebug("updated", 'info')
	initialize()
	def hT = getHubType()
	if (hT == 'ST')		state.vacaDisabled = (vacaState ? false : true);
	state.vacaRoomDevices = (vacaRooms ? parent.getRoomDevices(vacaRooms) : [:])
	if (!state.vacaDisabled)	{
		state.lastRun = now() - 1980000L
		for (def vRD : state.vacaRoomDevices)
			for (def i = 1; i <= 7; i++)
				if (state.rSH[vRD.key]?."$i")		state.rSH[vRD.key]."$i" = state.rSH[vRD.key]."$i".sort{ it.key }
		replayRecover()
		runEvery30Minutes(replayRecover)
	}
	parent.triggerSubscribeToVaca()
log.debug "perf updated: ${now() - nowTime} ms"
}

def initialize()	{
	ifDebug("initialize", 'info')
	unsubscribe()
	unschedule()
	state.replayRandom = false
}

def subscribeToRooms()	{
//	if (!state.vacaDisabled)		return;
	unsubscribe()
	def nowTime = now()
	def rDOs = parent.getChildRoomOccupancyDeviceObjects()
	ifDebug("there are ${rDOs.size()} devices.", 'info')
log.debug "there are ${rDOs.size()} devices."
	for (def rD : rDOs)		{
		ifDebug("initialize: room device: ${rD.label} id: ${rD.id}", 'info')
		subscribe(rD, "occupancy", roomStateHistory)
	}
log.debug "perf subscribeToRooms: ${now() - nowTime} ms"
}

// since hubitat does not support timeTodayAfter(...) 2018-04-08
private timeTodayA(whichDate, thisDate, timeZone)	{
	def newDate
	if (thisDate.before(whichDate))	{
		newDate = thisDate.plus(((whichDate.getTime() - thisDate.getTime()) / 86400000L).intValue() + 1)
	}
	else
		newDate = thisDate
	return newDate
}

private getHubType()	{
	if (!state.hubId)	state.hubId = location.hubs[0].id.toString();
	return (state.hubId.length() > 5 ? 'ST' : 'HE')
}

def unsubscribeChildRoomDevice(appChildDevice)	{
	ifDebug("unsubcribe: room: ${appChildDevice.label} id: ${appChildDevice.id}", 'info')
	if (getHubType() == 'HE')
		ifDebug("Hubitat does not yet support unsubscribing to a single device so removing a room requires a manual step.\n\
                 From Hubitat portal please go to devices and find the corresponding rooms occupancy device and remove it.\n\
                 Once the device is removed, from rooms manager app remove the room to complete uninstallation of the room.")
	else
		unsubscribe(appChildDevice)
}


def roomStateHistory(evt)	{
log.debug "evt.value: $evt.value | evt.device.name: $evt.device.name"
	if (state.vacaDisabled)		return;
	if (!(['asleep', 'engaged', 'occupied', 'vacant'].contains(evt.value)))		return;
	def nowTime = now()
	def dNI = evt.device.deviceNetworkId.toString()
//	state.remove('rSH');
	if (state.vacaRoomDevices.containsKey(dNI))		{
		def dow = getDoW().toString()
		def nowDate = new Date(nowTime)
		int hhMM = ((nowDate.format("HH", location.timeZone).toInteger() * 60) + nowDate.format("mm", location.timeZone).toInteger())
	    if (!state.rSH)		{  state.rSH = [:];	  ifDebug("rSH initialized")  }

		if (!state.rSH[dNI])			{  state.rSH[dNI] = [:];  log.debug "dNI initialized";  }
		if (!state.rSH[dNI]."$dow")		{  state.rSH[dNI]."$dow" = [:];  log.debug "dow initialized";  }
		state.rSH[dNI]."$dow" << [(hhMM.toString()):(evt.value[0..0])]
//		def deleteTime = (long) ((nowTime - 86400000000) / 1000)
//		while (state.rSH["$dNI"][0] && state.rSH["$dNI"][0].t < deleteTime)		{ state.rSH["$dNI"].remove(0); }
	}
	else
		state.rSH.remove(dNI)
log.debug "perf roomStateHistory: ${now() - nowTime} ms"
}

def replayRecover()		{
	def nowTime = now()

	if (!state.vacaDisabled)	{
		def prvRSt = null
		def removeHHmm
		for (def vRD : state.vacaRoomDevices)	{
			removeHHmm = []
			for (def i = 1; i <= 7; i++)		{
				for (def rSt : state.rSH[vRD.key]?."$i")	{
					if (rSt.value == 'v' && !['a', 'e', 'o'].contains(prvRSt))		removeHHmm << [(i.toString()), (rSt.key)];
					prvRSt = rSt.value
				}
			}
log.debug removeHHmm
			for (def rmv : removeHHmm)		state.rSH[vRD.key]."${rmv[0]}".remove(rmv[1]);
		}
	}

	if ((nowTime - state.lastRun) >= 1980000L)
		if (replayAsIs)		runEvery1Minute(replaySchedule);
		else				runIn(30, replayRandom);
log.debug "perf replayRecover: ${now() - nowTime} ms"
}

def replaySchedule()	{
	def nowTime = now()
	state.lastRun = nowTime
	def dow = getDoW()
	def nowDate = new Date(nowTime)
	int hhMM = ((nowDate.format("HH", location.timeZone).toInteger() * 60) + nowDate.format("mm", location.timeZone).toInteger())
	for (def vRD : state.vacaRoomDevices)	{
		if (state.rSH[vRD.key]?."$dow")	{
			def sRSt = state.rSH[vRD.key]."$dow"[(hhMM)]
			if (!sRST)		continue;
log.debug sRST
			def roomState = null
			switch(sRSt) {
				case 'a':	roomState = 'asleep';		break
				case 'e':	roomState = 'engaged';		break
				case 'o':	roomState = 'occupied';		break
				case 'v':	roomState = 'vacant';		break
			}
			if (roomState)		parent.setRoomState(state.vacaRoomDevices[vRD.key].id, stateMethod);
		}
	}
log.debug "perf replaySchedule: ${now() - nowTime} ms"
}

def replayRandom()		{
	def nowTime = now()
	state.lastRun = nowTime
	if (state.replayRandom)		return;
	def dow = getDoW().toString()
	def nowDate = new Date(nowTime)
	int hhMM = ((nowDate.format("HH", location.timeZone).toInteger() * 60) + nowDate.format("mm", location.timeZone).toInteger())
log.debug "perf replayRandom: ${now() - nowTime} ms"
}

private getDoW()	{
	long timestamp = now()
	return ((new Date(timestamp + location.timeZone.getOffset(timestamp))).day ?: 7)
}

private ifDebug(msg = null, level = null)	{  if (msg && (isDebug() || level == 'error'))  log."${level ?: 'debug'}" " $app.label: " + msg  }

def appButtonHandler(btn)	{  if (btn == 'enableVaca') state.vacaDisabled = false;		else if (btn == 'disableVaca') state.vacaDisabled = true;  }

//--------------------------------------------------------------------------------------------------------------------------------------------------//
