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
*  2020-06-22   fixed enabling and disabling WATO (SmartThings BETA)
*  2020-06-22   updated to support SmartThings (SmartThings BETA)
*  2019-05-12   added support for contains
*  2019-05-11   added support for logging received event
*  2019-01-23   added support for executing RM rule action for both match and unmatched attribute
*  2018-12-30   added support to select attribute value being compared or attribute valye from another device as first
*					parameter for command on devices
*  2018-12-30   added support to check for any changes to device attribute value
*  2018-12-26   cleaned up settings bug
*  2018-12-24	added option to use attribute value as first parameter in device command
*  2018-10-30	display defaul room name in settings
*  2018-10-26	change value type when retrieving from state
*  2018-10-26	change device subscription for attribute
*  2018-10-22	added rises above and drops below check for numbers and decimals
*  2018-10-18	added option for case insensitive check when comparing text value
*  2018-10-18	added avg/max/min/sum when getting integer or deciaml attribute from multiple attribute devices
*  2018-10-18	added support for multiple attribute devices
*  2018-10-18	added support for multiple command on devices
*  2018-10-18	added support for enabling and disabling WATO
*  2018-10-18	added mode and time filtering
*  2018-10-18	added support for custom label for child app
*  2018-10-13	When any Attribute Then this command Otherwise that command
*
***********************************************************************************************************************/

public static String version()		{  return "v6.0.1"  }

// comment the next line for SmartThings
import hubitat.helper.RMUtils

import groovy.transform.Field

@Field final String _SmartThings = 'ST'
@Field final String _Hubitat     = 'HU'

definition		(
	name: "WATO child app",
	namespace: "bangali",
	parent: "bangali:WATO",
	author: "bangali",
	description: "DO NOT INSTALL DIRECTLY. WATO app will create new child instances using this app code.",
	category: "My Apps",
	iconUrl: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomOccupancy.png",
	iconX2Url: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomOccupancy@2x.png",
	iconX3Url: "https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomOccupancy@3x.png"
)

preferences		{  page(name: "wato", title: "WATO Settings")  }

def wato()		{
	def allAttrs = []
	def attrDevCount = 0
	getHubType()
	if (attrDev)	for (def aD : attrDev)		attrDevCount++;
	state.attrDevCount = attrDevCount
	if (attrDevCount == 1)
		allAttrs = attrDev.supportedAttributes.flatten().unique{ it.name }.collectEntries{ [(it):"${it.name.capitalize()}"] }
	else if (attrDevCount > 1)	{
		def fT = true
		for (def aD : attrDev)	{
			def nSs = aD.supportedAttributes.unique{ it.name }.collectEntries{ [(it):"${it.name.capitalize()}"] }
			if (!allAttrs && fT)	{
				allAttrs = nSs
//				xAttrs = nSs
				fT = false
			}
			else	{
//				def dSs = []
//				for (def aSs : allAttrs)		{
//					def match = false
//					for (def nS : nSs)		{
//						if (nS.value == aSs.value)		{
//							match = true
//							break
//						}
//					}
//					if (!match)		dSs << aSs.value;
//				}
//				if (dSs)	allAttrs = allAttrs.findAll{ !dSs.contains(it.value) }
				allAttrs = allAttrs.findAll{ nSs.containsValue(it.value) }
			}
		}
	}
//log.debug allAttrs

	def allCmds = []
	def sCs
	def devCount = 0
	if (dev)	for (def d : dev)	devCount++;
	if (devCount == 1)	{
		sCs = dev.supportedCommands.flatten()
		if (sCs)	allCmds = sCs.unique{ it.name }.collectEntries{ [(it):"${it.name.capitalize()}"] };
	}
	else if (devCount > 1)	{
		def fT = true
		for (def d : dev)	{
			def nCs = d.supportedCommands.unique{ it.name }.collectEntries{ [(it):"${it.name.capitalize()}"] }
			if (!allCmds && fT)		{
				sCs = d.supportedCommands
				allCmds = nCs
				fT = false
			}
			else	{
//				def dCs = []
//				for (def aCs : allCmds)		{
//					def match = false
//					for (def nC : nCs)		{
//						if (nC.value == aCs.value)		{
//							match = true;
//							break;
//						}
//					}
//					if (!match)		dCs << aCs.value;
//				}
//				if (dCs)	allCmds = allCmds.findAll{ !dCs.contains(it.value) }
				allCmds = allCmds.findAll{ nCs.containsValue(it.value) }
			}
		}
	}
//log.debug allCmds

	state.cParam = []
	state.unCParam = []
	if (dev)
		for (def sC : sCs)		{
			def sCA = sC.arguments
			if (devCmd && sC.name == devCmd && sCA)			state.cParam = sCA;
			if (devUnCmd && sC.name == devUnCmd && sCA)		state.unCParam = sCA;
		}

	if (state.hT == _Hubitat)		{
		if (attrTyp == 'Text')		app.removeSetting("attrMath");
		else						app.removeSetting("attrCase");
		if (attrOp && attrOp == '⬆︎⬇︎')		{
			app.removeSetting("attrTyp")
			app.removeSetting("attrCase")
			app.removeSetting("attrMath")
			app.removeSetting("attrVal")
		}
	}
	else	{
		if (attrTyp == 'Text')
			app.updateSetting("attrMath", [type: "enum", value: []])
		else
			app.updateSetting("attrCase", [type: "bool", value: false])
		if (attrOp && attrOp == '⬆︎⬇︎')		{
			app.updateSetting("attrTyp", [type: "enum", value: []])
			app.updateSetting("attrCase", [type: "bool", value: false])
			app.updateSetting("attrMath", [type: "enum", value: []])
			app.updateSetting("attrVal", [type: "text", value: ""])
		}
	}

	def rules = (state.hT == _Hubitat ? RMUtils.getRuleList() : null)

	def allCParamAttrs = [:], allUnCParamAttrs = [:]
	if (devCParamTyp1Dev)
		allCParamAttrs = devCParamTyp1Dev.supportedAttributes.unique{ it.name }.collectEntries{ [(it):"${it.name.capitalize()}"] }
	if (devUnCParamTyp1Dev)
		allUnCParamAttrs = devUnCParamTyp1Dev.supportedAttributes.unique{ it.name }.collectEntries{ [(it):"${it.name.capitalize()}"] }

	def lS = updLbl()

//	def optionCapaList = capaList.collect { [(it.key):it.value] }

	dynamicPage(name: "wato", title: "", install: true, uninstall: true)		{
		section("")		{
			if (state.watoDisabled)
            	input "enableWATO", "${(state.hT == _SmartThings ? 'bool' : 'button')}", title: "Enable this WATO?", required:false
			else
				input "disableWATO", "${(state.hT == _SmartThings ? 'bool' : 'button')}", title: "Disable this WATO?", required:false
			paragraph subHeaders('WHEN any ATTRIBUTE', true)
			if (state.hT == _SmartThings)
				input "attrDevCap", "enum", title: "Which capability?", required:true, multiple:false, submitOnChange:true, options:capaList
			input "attrDev", "capability.${(state.hT == _SmartThings ? attrDevCap : '*')}", title: "Attribute from devices?", required:true, multiple:true, submitOnChange:true
			if (attrDev)
				input "attr", "enum", title: "Attribute?", required:true, multiple:false, submitOnChange:true, options:allAttrs
			if (attr)
				input "attrOp", "enum", title: "Operator?", required:true, multiple:false, submitOnChange:true, options:[['<':"< (less than)"], ['<=':"<= (less than or equals to)"], ['=':"= (equals to)"], ['>=':">= {greater than or equals to}"], ['>':"> {greater than}"], ['!=':"!= {not equals to}"], ['⬆︎':"⬆︎ (rises above)"], ['⬇︎':"⬇︎ (falls below)"], ['⬆︎⬇︎':"⬆︎⬇︎ (changes)"], ['⊃':"⊃ (contains)"]]
			if (attrOp && attrOp != '⬆︎⬇︎')		{
				input "attrTyp", "enum", title: "Type?", required:true, multiple:false, submitOnChange:true, options:(['⬆︎', '⬇︎'].contains(attrOp) ? ["Number", "Decimal"] : ["Text", "Number", "Decimal"])
				if (attrTyp)	{
					if (attrTyp == 'Text')
						input "attrCase", "bool", title: "Case sensitive?", required:true, multiple:false, defaultValue:true
					else if (attrDevCount > 1)
						input "attrMath", "enum", title: "Math?", required:true, multiple:false, options:["Avg", "Max", "Min", "Sum"]
					input "attrVal", "${attrTyp.toLowerCase()}", title: "Value?", required:true
					if (attrDev && attr)		paragraph attrDev."current${attr.substring(0, 1).toUpperCase() + attr.substring(1)}".flatten().toString();
				}
			}
			if (attrTyp || (attrOp && attrOp == '⬆︎⬇︎'))	{
				paragraph subHeaders('THEN this command', true)
				if (state.hT == _Hubitat)
					input "cmdOrRM", "enum", title: "Execute command on device or run RM action?", required:true, multiple:false, submitOnChange:true, defaultValue:'', options:[['':"Execute command"], ['RM':'Run RM action']]
				if (state.hT == _SmartThings || !cmdOrRM)	{
					if (state.hT == _SmartThings)
						input "attrCmdCap", "enum", title: "Which capability?", required:true, multiple:false, submitOnChange:true, options:capaList
					input "dev", "capability.${(state.hT == _SmartThings ? attrCmdCap : '*')}", title: "Command on devices?", required:true, multiple:true, submitOnChange:true
					input "devCmd", "enum", title: "Command on match?", required:true, multiple:false, submitOnChange:true, options:allCmds
					if (state.cParam)		{
						def i = 1
						for (def cP : state.cParam)	{
							def pT = cP.toString().toLowerCase()
							if (i == 1)
								input "devCParamTyp1", "enum", title: "Command param type?", required:true, submitOnChange:true, defaultValue:'', options:[['':"Value"], ['A':"This attribute"], ['D':"Attribute from another device"]]
							if (i == 1 && devCParamTyp1)	{
								if (state.hT == _SmartThings)
									input "devCParamTyp1Cap", "enum", title: "Which capability?", required:true, multiple:false, submitOnChange:true, options:capaList
								if (devCParamTyp1 == 'D')	{
									input "devCParamTyp1Dev", "capability.${(state.hT == _SmartThings ? devCParamTyp1Cap : '*')}", title: "Attribute from which device?", required:true, submitOnChange:true
									if (devCParamTyp1Dev)
										input "devCParamTyp1Attr", "enum", title: "Attribute?", required:true, multiple:false, submitOnChange:true, options:allCParamAttrs

								}
	//							if (settings["devCParamAttrVal$i"])
	//								paragraph "Command param $i ($pT)?\nset use attribute value to false to set"
	//							else
	//								input "devCParam$i", "$pT", title: "Command param $i ($pT)?", required:(i == 1 ? true : false)
	//							input "devCParamAttrVal$i", "bool", title: "Use attribute value from device?", required:false, submitOnChange:true
							}
							else	{
								input "devCParam$i", "$pT", title: "Command param $i ($pT)?", required:(i == 1 ? true : false)
	//							app.removeSetting("devCParamAttrVal$i")
							}
							i = i + 1
						}
					}
					else
						paragraph "Command does not support param"
				}
				else	{
					if (state.hT == _Hubitat)		{
						app.removeSetting("dev")
						app.removeSetting("devCmd")
						input "devRule", "enum", title: "Which rule?", required:(devUnRule ? false : true), multiple:false, options:rules
					}
					else	{
						app.updateSetting("dev")
						app.updateSetting("devCmd", [type: "enum", value: []])
					}
				}
				paragraph subHeaders('OTHERWISE that command', true)
//				input "devUnWhich", "enum", title: "That command or RM action?", required:true, multiple:false, submitOnChange:true, defaultValue:null, options:[[null:"That command"], ['RM':'That RM action']]
				if (state.hT == _SmartThings || !cmdOrRM)	{
					input "devUnCmd", "enum", title: "Command on unmatch?", required:false, multiple:false, submitOnChange:true, options:allCmds
					if (state.unCParam)		{
						def i = 1
						for (def cP : state.unCParam)	{
							def pT = cP.toString().toLowerCase()
							if (i == 1)
								input "devUnCParamTyp1", "enum", title: "Command param type?", required:true, submitOnChange:true, defaultValue:'', options:[['':"Value"], ['A':"This attribute"], ['D':"Attribute from another device"]]
							if (i == 1 && devUnCParamTyp1)	{
								if (devUnCParamTyp1 == 'D')	{
									if (state.hT == _SmartThings)
										input "devUnCParamTyp1Cap", "enum", title: "Which capability?", required:true, multiple:false, submitOnChange:true, options:capaList
									input "devUnCParamTyp1Dev", "capability.${(state.hT == _SmartThings ? devUnCParamTyp1Cap : '*')}", title: "Attribute from which device?", required:true, submitOnChange:true
									if (devUnCParamTyp1Dev)
										input "devUnCParamTyp1Attr", "enum", title: "Attribute?", required:true, multiple:false, submitOnChange:true, options:allUnCParamAttrs

								}
	//							if (settings["devUnCParamAttrVal$i"])
	//								paragraph "Command param $i ($pT)?\nset use attribute value to false to set"
	//							else
	//								input "devUnCParam$i", "$pT", title: "Command param $i ($pT)?", required:(i == 1 ? true : false)
	//							input "devUnCParamAttrVal$i", "bool", title: "Use attribute value from device?", required:false, submitOnChange:true
							}
							else	{
								input "devUnCParam$i", "$pT", title: "Command param $i ($pT)?", required:(i == 1 ? true : false)
	//							app.removeSetting("devUnCParamAttrVal$i")
							}
							i = i + 1
						}
					}
					else
						paragraph "Command does not support param"
				}
				else	{
					if (state.hT == _Hubitat)		{
						app.removeSetting("dev")
						app.removeSetting("devUnCmd")
						input "devUnRule", "enum", title: "Which rule?", required:(devRule ? false : true), multiple:false, options:rules
					}
					else	{
						app.updateSetting("dev")
						app.updateSetting("devUnCmd", [type: "enum", value: []])
					}
				}
			}
		}
		section("")	{
			paragraph subHeaders('App Name (optional)', true, true)
			input "appName", "text", title: "WATO Name:", required:false, submitOnChange:true
			paragraph lS;
		}
		section("")		{
			paragraph subHeaders('Restrictions (optional)', true, true)
			input "inModes", "mode", title: "Only in modes?", required:false, multiple:true, submitOnChange:true
			input "fromTime", "time", title: "From Time?", required:(toTime ? true : false), defaultValue:null, submitOnChange:true
			input "toTime", "time", title: "To Time?", required:(fromTime ? true : false), defaultValue:null, submitOnChange:true
		}
		section("")		{
			paragraph subHeaders('Logging', true, true)
			input "log", "bool", title: "Log received event values?", required:false, defaultValue:false
		}
	}
}

private subHeaders(str, div = false, opt = false)	{
	if (str.size() > 50)	str = str.substring(0, 50);
	str = str.center(50)
	if (state.hT == _Hubitat)		{
		def divider = (div ? "<hr width='100%' size='10' noshade>" : '');
		if (opt)
			return "$divider<div style='text-align:center;background-color:#f9f2ec;color:#999999;'>$str</div>"
		else
			return "$divider<div style='text-align:center;background-color:#0066cc;color:#ffffff;'>$str</div>"
	}
	else
		return str
}

private getHubType()	{
	if (!state.hubId)	state.hubId = location.hubs[0].id.toString()
	state.hT = (state.hubId.length() > 5 ? _SmartThings : _Hubitat)
	return state.hT
}

def installed()		{  initialize()  }

def updated()		{
	initialize()
	if (state.hT == _SmartThings)
		if (enableWATO)
			state.watoDisabled = false
		else if (disableWATO)
			state.watoDisabled = true
	updLbl()
	if (!state.watoDisabled)	{
		for (def d : attrDev)		subscribe(d, "${attr.toString()}", checkAttr);
		state.prvAttrVal = checkVal()
	}
}

def initialize()	{  unsubscribe();	unschedule()  }

private updLbl()	{
	state.cParams = null
	state.unCParams = null
	if (state.hT == _SmartThings || !cmdOrRM)	{
		def cS = (state.cParam ? state.cParam.size() : 0)
		for (def i = 1; i <= 10; i++)		{
			if (settings["devCParam$i"] != null || (i == 1 && devCParamTyp1))
			 	if (i > cS)		{
					if (state.hT == _Hubitat)
						app.removeSetting("devCParam$i")
					else
						app.updateSetting("devCParam$i")
				}
				else
					state.cParams = i
			else
				if (state.cParams == null)		state.cParams = i - 1;
		}
		def uS = (state.unCParam ? state.unCParam.size() : 0)
		for (def i = 1; i <= 10; i++)		{
			if (settings["devUnCParam$i"] != null || (i == 1 && devUnCParamTyp1))
			 	if (i > uS)
					if (state.hT == _Hubitat)
						app.removeSetting("devUnCParam$i")
					else
						app.updateSetting("devUnCParam$i")
				else
					state.unCParams = i
			else
				if (state.unCParams == null)		state.unCParams = i - 1;
		}
	}
	def lS = lblStr()
	def l = (appName ?: lS)
	if (l)	app.updateLabel(l);
	return lS
}

private lblStr()	{
	def c = ''
	def u = ''
	if (state.hT == _SmartThings || !cmdOrRM)	{
		for (def i = 1; i <= (state.cParams ?: 0); i++)		{
			if (settings["devCParam$i"] != null || (i == 1 && devCParamTyp1))
				c = c + (c ? ', ' : '') + (i == 1 && devCParamTyp1 ? (devCParamTyp1 == 'D' ? '[' + devCParamTyp1Dev.toString() + ']' + ' : ' + devCParamTyp1Attr : '#') : settings["devCParam$i"])
			else
				break
		}
		for (def i = 1; i <= (state.unCParams ?: 0); i++)		{
			if (settings["devUnCParam$i"] != null || (i == 1 && devUnCParamTyp1))
				u = u + (u ? ', ' : '') + (i == 1 && devUnCParamTyp1 ? (devUnCParamTyp1 == 'D' ? '[' + devUnCParamTyp1Dev.toString() + ']' + ' : ' + devUnCParamTyp1Attr : '#') : settings["devUnCParam$i"])
			else
				break
		}
	}
	def l = null
	if (attrDev)	{
		if (devCmd || devUnCmd)		{
			l = ((state.watoDisabled ? '<FONT COLOR="ff0000">DISABLED: </FONT>' : '') + 'WHEN ' + attrDev.displayName + ' ATTRIBUTE ' + (state.attrDevCount > 1 && attrMath ? "${attrMath.toLowerCase()}($attr)" : attr) + ' ' + attrOp + ' ' + (attrOp == '⬆︎⬇︎' ? '' : (attrCase ? "${attrVal}.ignoreCase()" : attrVal)) + (devCmd ? ' THEN ' + dev + ' : ' + devCmd + (c ? '(' + c + ')' : '') : '') + (devUnCmd ? ' OTHERWISE ' + (devCmd ? '' : dev + ' : ') + devUnCmd + (u ? '(' + u + ')' : '') : '') + (inModes || (fromTime && toTime) ? ' {' : '') + (inModes ? 'modes: ' + inModes + (fromTime && toTime ? ' & ' : ' ') : '') + (fromTime && toTime ? 'time: ' + format24hrTime(new Date().parse("yyyy-MM-dd'T'HH:mm:ss.SSSZ", fromTime)) + ' - ' + format24hrTime(new Date().parse("yyyy-MM-dd'T'HH:mm:ss.SSSZ", toTime)) : '') + (inModes || (fromTime && toTime) ? '}' : ''))
		}
		else if (state.hT == _Hubitat && cmdOrRM)	{
			l = ((state.watoDisabled ? '<FONT COLOR="ff0000">DISABLED: </FONT>' : '') + 'WHEN ' + attrDev.displayName + ' ATTRIBUTE ' + (state.attrDevCount > 1 && attrMath ? "${attrMath.toLowerCase()}($attr)" : attr) + ' ' + attrOp + ' ' + (attrOp == '⬆︎⬇︎' ? '' : (attrCase ? "${attrVal}.ignoreCase()" : attrVal)) + (devRule ? ' THEN RM Action' : '') + (devUnRule ? ' OTHERWISE RM Action' : '') + (inModes || (fromTime && toTime) ? ' {' : '') + (inModes ? 'modes: ' + inModes + (fromTime && toTime ? ' & ' : ' ') : '') + (fromTime && toTime ? 'time: ' + format24hrTime(new Date().parse("yyyy-MM-dd'T'HH:mm:ss.SSSZ", fromTime)) + ' - ' + format24hrTime(new Date().parse("yyyy-MM-dd'T'HH:mm:ss.SSSZ", toTime)) : '') + (inModes || (fromTime && toTime) ? '}' : ''))

		}
		l = (l ? l.substring(0, (l.size() > 254 ? 254 : l.size())) : '')
	}

	return l
}

private format24hrTime(tTF = new Date(now()), fmt = 'HH:mm')	{  return tTF.format(fmt, location.timeZone)  }

def checkAttr(evt)	{
	if (log)		log.info "\tcheckAttr: name = $evt.name | value = $evt.value | source = $evt.source";
	if (state.watoDisabled)		return;
	if (inModes && !inModes.contains(location.currentMode.toString()))		return;
	if (fromTime && toTime)		{
		def fTime = timeToday(fromTime, location.timeZone)
		def tTime = timeToday(toTime, location.timeZone)
		while (fTime > tTime)		tTime = tTime.plus(1);
		def nowDate = new Date(now())
		if (!(timeOfDayIsBetween(fTime, tTime, nowDate, location.timeZone)))		return;
	}
//	eV = (state.attrDevCount == 1 ? evt.value : checkVal())
	def eV = checkVal()
//log.info "eV = $eV"
	if (eV == -99999)		return;
	def evtVal = setValType(eV)
//log.info "$evtVal | $state.prvAttrVal"
	if (evtVal == null)		return;
	def aV = (attrCase ? attrVal.toLowerCase() : attrVal)
	def match = false
	def noCmdRun = false
	switch(attrOp.toString()) {
		case "<":		if (evtVal < aV)		match = true;	break
		case "<=":		if (evtVal <= aV)		match = true;	break
		case "=":		if (evtVal == aV)		match = true;	break
		case ">=":		if (evtVal >= aV)		match = true;	break
		case ">":		if (evtVal > aV)		match = true;	break
		case "!=":		if (evtVal != aV)		match = true;	break
		case "⬆︎":
			def prvAttrVal = setValType(state.prvAttrVal)
			if (evtVal > aV)
				if (prvAttrVal <= aV)		match = true;
				else						noCmdRun = true;
			else
				if (prvAttrVal <= aV)		noCmdRun = true;
			break
		case "⬇︎":
			def prvAttrVal = setValType(state.prvAttrVal)
			if (evtVal < aV)
				if (prvAttrVal >= aV)		match = true;
				else						noCmdRun = true;
			else
				if (prvAttrVal >= aV)		noCmdRun = true;
			break
		case "⬆︎⬇︎":
			if (evtVal != state.prvAttrVal)		match = true;
			break
		case "⊃":
			if (evtVal.toString().contains(aV.toString()))		match = true;
			break
	}
//log.debug "$eV $attrOp $aV | $dev | $devCmd $state.cParams | $devUnCmd $state.unCParams | $match"
	state.prvAttrVal = evtVal
	if (noCmdRun)	return;

	if (state.hT == _SmartThings || (state.hT == _Hubitat && !cmdOrRM))		{
		def cmd = (match ? devCmd : devUnCmd)
		if (!cmd)		return;
		def paramCnt = (match ? (state.cParams ?: 0) : (state.unCParams ?: 0))
		def param1Val
		if (paramCnt > 0)
			param1Val = (match ? (devCParamTyp1 == 'A' ? evtVal : (devCParamTyp1 == 'D' ? devCParamTyp1Dev."current${devCParamTyp1Attr.substring(0, 1).toUpperCase() + devCParamTyp1Attr.substring(1)}" : devCParam1)) : (devUnCParamTyp1 == 'A' ? evtVal : (devUnCParamTyp1 == 'D' ? devUnCParamTyp1Dev."current${devUnCParamTyp1Attr.substring(0, 1).toUpperCase() + devUnCParamTyp1Attr.substring(1)}" : devUnCParam1)))
		switch(paramCnt)	{
			case 0:
				dev."$cmd"()
				break
			case 1:
				dev."$cmd"(toMap(param1Val))
				break
			case 2:
				dev."$cmd"(param1Val, (match ? devCParam2 : devUnCParam2))
				break
			case 3:
				dev."$cmd"(param1Val, (match ? devCParam2 : devUnCParam2), (match ? devCParam3 : devUnCParam3))
				break
			default:
				dev."$cmd"()
				break
		}
	}
	else if (state.hT == _Hubitat)		{
		def rule = []
		rule << (match ? devRule : devUnRule)
//log.info "execute rule: $rule"
		if (!rule)		return;
//		RMUtils.sendAction(rule, "setRuleBooleanTrue", app.label)
		RMUtils.sendAction(rule, "runRuleAct", app.label)
//		RMUtils.sendAction(rule, "setRuleBooleanFalse", app.label)
	}
}

private setValType(val)	{
	def value
	if (attrOp && attrOp == '⬆︎⬇︎')
		value = val
	else	{
		switch(attrTyp)		{
			case "Text":	value = (attrCase ? val.toString().toLowerCase() : val.toString());		break
			case "Number":	value = val as Integer;		break
			case "Decimal":	value = val as BigDecimal;	break
			default:		value = null;				break
		}
	}
	return value
}

private checkVal()	{
	def aDs = attrDev."current${attr.substring(0, 1).toUpperCase() + attr.substring(1)}"
	if (state.attrDevCount == 1)	return aDs[0];
	def eV = null
	switch(attrTyp) {
		case "Text":
			for (def aD : aDs)		{
				def aDC = (attrCase && aD ? aD.toLowerCase() : aD)
				if (!eV)
					eV = aDC
				else if (eV != aDC)	{
					eV = aDC + 'unMatched'
					break
				}
			}
			break
		case "Number":
		case "Decimal":
			switch(attrMath) {
				case 'Avg':		eV = aDs.sum() / aDs.size();	break
				case 'Max':		eV = aDs.max();					break
				case 'Min':		eV = aDs.min();					break
				case 'Sum':		eV = aDs.sum();					break
				default:		eV = null;						break
			}
			break
		default:
			eV = -99999
			break
	}
//log.info "aDs: $aDs | eV: $eV"
	return eV
}

private toMap(p)	{  return ((p =~ /^\[(([a-z]+:[0-9]+)[\s|,]*)+\]$/).matches() ? evaluate(p) : p)  }

def appButtonHandler(btn)	{  if (btn == 'enableWATO') state.watoDisabled = false;		else if (btn == 'disableWATO') state.watoDisabled = true;  }

@Field final List    capaList = [
	['activityLightingMode': "Activity Lighting Mode"],
	['airConditionerMode': "Air Conditioner Mode"],
	['alarm': "Alarm"],
	['audioMute': "Audio Mute"],
	['audioVolume': "Audio Volume"],
	['battery': "Battery"],
	['button': "Button"],
//	"Color Control"],
	['colorTemperature': "Color Temperature"],
	['contactSensor': "Contact Sensor"],
//	"Dishwasher Mode"],
	['doorControl': "Door Control"],
//	"Dryer Mode"],
	['fanSpeed': "Fan Speed"],
//	"Filter Status"],
	['garageDoorControl': "Garage Door Control"],
//	"Illuminance Measurement"],
//	"Infrared Level"],
	['lock': "Lock"],
//	"Media Input Source"],
//	"Media Playback Repeat"],
//	"Media Playback Shuffle"],
//	"Media Playback"],
//	"Motion Sensor"],
//	"Oven Mode"],
//	"Power Source"],
	['presenceSensor': "Presence Sensor"],
//	"Rapid Cooling"],
//	"Refrigeration Setpoint"],
//	"Relative Humidity Measurement"],
//	"Robot Cleaner Cleaning Mode"],
//	"Robot Cleaner Movement"],
//	"Robot Cleaner Turbo Mode"],
//	"Signal Strength"],
//	"Smoke Detector"],
//	"Sound Sensor"],
	['switchLevel': "Switch Level"],
	['switch': "Switch"],
//	"Thermostat Cooling Setpoint"],
//	"Thermostat Fan Mode"],
//	"Thermostat Heating Setpoint"],
//	"Thermostat Mode"],
//	"Thermostat Setpoint"],
//	"Tone"],
//	"Tv Channel"],
//	"Valve"],
//	"Washer Mode"],
	['waterSensor': "Water Sensor"]
//	"Window Shade"]
]
