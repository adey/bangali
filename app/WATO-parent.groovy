/***********************************************************************************************************************
*  Copyright 2018 bangali
*
*  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License. You may obtain a copy of the License at:
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
*  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
*  for the specific language governing permissions and limitations under the License.
*
*  WATO (When any Attribute Then this command Otherwise that command) parent
*
*  Author: bangali
*
*  2018-10-18	added option for case insensitive check when comparing text value
*  2018-10-18	added avg/max/min/sum when getting attribute from multiple attribute devices
*  2018-10-18	added support for multiple attribute devices
*  2018-10-18	added support for multiple command on devices
*  2018-10-18	added support for enabling and disabling WATO
*  2018-10-18	added mode and time filtering
*  2018-10-18	added support for custom label for child app
*  2018-10-13	When any Attribute Then this command Otherwise that command
*
***********************************************************************************************************************/

public static String version()		{  return "v2.0.0"  }

definition		(
	name: "WATO",
	namespace: "bangali",
	author: "bangali",
	description: "When any Attribute Then command Otherwise command",
	category: "My Apps",
	singleInstance: true,
	iconUrl: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomOccupancy.png",
	iconX2Url: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomOccupancy@2x.png",
	iconX3Url: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomOccupancy@3x.png"
)

preferences		{  page(name: "mainPage", content: "mainPage")  }

def mainPage()	{
	def appChildren = app.getChildApps().sort { it.label }
	dynamicPage(name: "mainPage", title: "When any Attribute Then this command Otherwise that command", install: true, uninstall: true, submitOnChange: true)	{
		section {  app(name: "WATO", appName: "WATO child app", namespace: "bangali", title: "Define New WATO", multiple: true)  }
	}
}

def installed()		{  initialize()  }

def updated()		{  initialize()  }

def initialize()	{  unsubscribe();	unschedule()  }
