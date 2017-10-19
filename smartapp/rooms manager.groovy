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
*  Name: Room Manager
*  Source: https://github.com/adey/bangali/blob/master/smartapp/rooms%20manager.groovy
*
*****************************************************************************************************************/

definition (
    name: "rooms manager",
    namespace: "bangali",
    author: "bangali",
    description: "Create rooms",
    category: "My Apps",
    singleInstance: true,
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
)

preferences	{
	page(name: "mainPage", title: "Installed Rooms", install: true, uninstall: true, submitOnChange: true) {
		section {
            app(name: "rooms manager", appName: "rooms child app", namespace: "bangali", title: "New Room", multiple: true)
		}
	}
}

def installed()		{
	initialize()
}

def updated()		{
	unsubscribe()
	initialize()
}

def initialize()	{
log.debug "rooms manager: there are ${childApps.size()} rooms."
	childApps.each	{ child ->
		log.debug "room manager: room: ${child.label}"
	}
}
