/*****************************************************************************************************************
*
*  A SmartThings child smartapp which creates the "room" device using the rooms occupancy DTH.
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
*  Name: Room Child App
*  Source: https://github.com/adey/bangali/blob/master/smartapp/rooms%20child%20app.groovy
*  Version: 0.01
*
*****************************************************************************************************************/

definition	(
    name: "rooms child app",
    namespace: "bangali",
    parent: "bangali:rooms manager",
    author: "bangali",
    description: "DO NOT INSTALL DIRECTLY OR PUBLISH. Rooms child smartapp to create new rooms using 'rooms occupancy' DTH from Rooms Manager smartapp.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
)

preferences {
	page(name: "roomName")
}

def roomName()	{
	dynamicPage(name: "roomName", title: "Room Name", install: true, uninstall: childCreated()) {
		if (!childCreated())	{
			section		{
				label title: "Room Name:", required: true
			}
			section("Update Room State On Away Mode?")		{
 				input "awayMode", "mode", title: "Away Mode", required: false, multiple: false
			}
    	} else {
			section		{
            	paragraph "Room Name:\n${app.label}"
			}
			section("Update Room State On Away Mode?")		{
 				input "awayMode", "mode", title: "Away Mode", required: false, multiple: false
			}
		}
	}
}

def installed()		{}

def updated()	{
	unsubscribe()
	initialize()
	if (!childCreated())	{
		spawnChildDevice(app.label)
	}
	if (awayMode)	{
		subscribe(location, modeEventHandler)
	}
}

def	initialize()	{}

def	modeEventHandler(evt)	{
	if (awayMode && evt.value == awayMode)		{
		def child = getChildDevice(getRoom())
		child.generateEvent('vacant')
	}
}

def uninstalled() {
	getChildDevices().each	{
		deleteChildDevice(it.deviceNetworkId)
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
