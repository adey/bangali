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

public static String version()		{  return "v3.2.0"  }

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

	if (attrTyp == 'Text')		app.removeSetting("attrMath");
	else						app.removeSetting("attrCase");
	updLbl()

	dynamicPage(name: "wato", title: "", install: true, uninstall: true)		{
		section("")		{
			if (state.watoDisabled)
            	input "enableWATO", "button", title: "Enable this WATO"
			else
				input "disableWATO", "button", title: "Disable this WATO"
			paragraph subHeaders('WHEN any ATTRIBUTE', true)
			input "attrDev", "capability.*", title: "Attribute from devices?", required:true, multiple:true, submitOnChange:true
			if (attrDev)
				input "attr", "enum", title: "Attribute?", required:true, multiple:false, submitOnChange:true, options:allAttrs
			if (attr)
				input "attrOp", "enum", title: "Operator?", required:true, multiple:false, submitOnChange:true, options:["<", "<=", "=", ">=", ">", "!=", "⬆︎", "⬇︎"]
			if (attrOp)
				input "attrTyp", "enum", title: "Type?", required:true, multiple:false, submitOnChange:true, options:(['⬆︎', '⬇︎'].contains(attrOp) ? ["Number", "Decimal"] : ["Text", "Number", "Decimal"])
			if (attrTyp == 'Text')
				input "attrCase", "bool", title: "Case sensitive?", required:true, multiple:false, defaultValue:true
			else if (attrDevCount > 1)
				input "attrMath", "enum", title: "Math?", required:true, multiple:false, options:["Avg", "Max", "Min", "Sum"]
			if (attrTyp)	{
				input "attrVal", "${attrTyp.toLowerCase()}", title: "Value?", required:true
				if (attrDev && attr)		paragraph attrDev."current${attr.substring(0, 1).toUpperCase() + attr.substring(1)}".flatten().toString();
				paragraph subHeaders('THEN this command', true)
				input "dev", "capability.*", title: "Command on devices?", required:true, multiple:true, submitOnChange:true
				input "devCmd", "enum", title: "Command on match?", required:true, multiple:false, submitOnChange:true, options:allCmds
				if (state.cParam)		{
					def i = 1
					for (def cP : state.cParam)	{
						def pT = cP.toString().toLowerCase()
						input "devCParam$i", "$pT", title: "Command param $i ($pT)?", required:(i == 1 ? true : false)
						i = i + 1
					}
				}
				else
					paragraph "Command does not support param"
				paragraph subHeaders('OTHERWISE that command', true)
				input "devUnCmd", "enum", title: "Command on unmatch?", required:false, multiple:false, submitOnChange:true, options:allCmds
				if (state.unCParam)		{
					def i = 1
					for (def cP : state.unCParam)	{
						def pT = cP.toString().toLowerCase()
						input "devUnCParam$i", "$pT", title: "Command param $i ($pT)?", required:(i == 1 ? true : false)
						i = i + 1
					}
				}
				else
					paragraph "Command does not support param"
			}
		}
		section("")	{
			paragraph subHeaders('App Name (optional)', true, true)
			input "appName", "text", title: "WATO Name:", required:false, submitOnChange:true
		}
		section("")		{
			paragraph subHeaders('Restrictions (optional)', true, true)
			input "inModes", "mode", title: "Only in modes?", required:false, multiple:true, submitOnChange:true
			input "fromTime", "time", title: "From Time?", required:(toTime ? true : false), defaultValue:null, submitOnChange:true
			input "toTime", "time", title: "To Time?", required:(fromTime ? true : false), defaultValue:null, submitOnChange:true
		}
	}
}

private subHeaders(str, div = false, opt = false)	{
	if (str.size() > 50)	str = str.substring(0, 50);
	str = str.center(50)
	def divider = (div ? "<hr width='75%' size='10' noshade>" : '');
	if (opt)
		return "$divider<div style='text-align:center;background-color:#f9f2ec;color:#999999;'>$str</div>"
	else
		return "$divider<div style='text-align:center;background-color:#0066cc;color:#ffffff;'>$str</div>"
}

def installed()		{  initialize()  }

def updated()		{
	initialize()
	updLbl()
	if (!state.watoDisabled)	{
		for (def d : attrDev)		subscribe(d, "${attr.toString()}", checkAttr);
		state.prvAttrVal = checkVal()
	}
}

private updLbl()	{
	state.cParams = null
	def cS = (state.cParam ? state.cParam.size() : 0)
	for (def i = 1; i <= 10; i++)		{
		if (settings["devCParam$i"] != null)
		 	if (i > cS)		app.removeSetting("devCParam$i");
			else			state.cParams = i;
		else
			if (state.cParams == null)		state.cParams = i - 1;
	}
	state.unCParams = null
	def uS = (state.unCParam ? state.unCParam.size() : 0)
	for (def i = 1; i <= 10; i++)		{
		if (settings["devUnCParam$i"] != null)
		 	if (i > uS)		app.removeSetting("devUnCParam$i");
			else			state.unCParams = i;
		else
			if (state.unCParams == null)		state.unCParams = i - 1;
	}
	def l = (appName ?: lblStr())
	if (l)	app.updateLabel(l);
}

private lblStr()	{
	def c = ''
	for (def i = 1; i <= (state.cParams ?: 0); i++)		{
		if (settings["devCParam$i"] != null)	c = c + (c ? ', ' : '') + settings["devCParam$i"]
		else		break;
	}
	def u = ''
	for (def i = 1; i <= (state.unCParams ?: 0); i++)		{
		if (settings["devUnCParam$i"] != null)		u = u + (u ? ', ' : '') + settings["devUnCParam$i"]
		else		break;
	}
	def l = null
	if (attrDev && (devCmd || devUnCmd))	{
		l = ((state.watoDisabled ? '<FONT COLOR="ff0000">DISABLED: </FONT>' : '') + 'WHEN ' + attrDev.displayName + ' ATTRIBUTE ' + (state.attrDevCount > 1 && attrMath ? "${attrMath.toLowerCase()}($attr)" : attr) + ' ' + attrOp + ' ' + (attrCase ? "${attrVal}.ignoreCase()" : attrVal) + (devCmd ? ' THEN ' + dev + ' : ' + devCmd + (c ? '(' + c + ')' : '') : '') + (devUnCmd ? ' OTHERWISE ' + (devCmd ? '' : dev + ' : ') + devUnCmd + (u ? '(' + u + ')' : '') : '') + (inModes || (fromTime && toTime) ? ' {' : '') + (inModes ? 'modes: ' + inModes + (fromTime && toTime ? ' & ' : ' ') : '') + (fromTime && toTime ? 'time: ' + format24hrTime(new Date().parse("yyyy-MM-dd'T'HH:mm:ss.SSSZ", fromTime)) + ' - ' + format24hrTime(new Date().parse("yyyy-MM-dd'T'HH:mm:ss.SSSZ", toTime)) : '') + (inModes || (fromTime && toTime) ? '}' : ''))
		l = l.substring(0, (l.size() > 254 ? 254 : l.size()))
	}
	return l
}

private format24hrTime(tTF = new Date(now()), fmt = 'HH:mm')	{  return tTF.format(fmt, location.timeZone)  }

def initialize()	{  unsubscribe();	unschedule()  }

def checkAttr(evt)	{
log.debug "\tcheckAttr: name = $evt.name | value = $evt.value"
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
	if (!eV)		return;
	def evtVal = setValType(eV)
	if (evtVal == null)		return;
	def aV = (attrCase ? attrVal.toLowerCase() : attrVal)
	def match = false
	def noCmdRun = false
	switch(attrOp) {
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
	}
//log.debug "$eV $attrOp $aV | $dev | $devCmd $state.cParams | $devUnCmd $state.unCParams | $match"
	state.prvAttrVal = evtVal
	if (noCmdRun)	return;
	def cmd = (match ? devCmd : devUnCmd)
	if (!cmd)		return;
	switch((match ? (state.cParams ?: 0) : (state.unCParams ?: 0)))	{
		case 0:
			dev."$cmd"();	break
		case 1:
			dev."$cmd"(toMap((match ? devCParam1 : devUnCParam1)));		break
		case 2:
			dev."$cmd"((match ? devCParam1 : devUnCParam1), (match ? devCParam2 : devUnCParam2));	break
		case 3:
			dev."$cmd"((match ? devCParam1 : devUnCParam1), (match ? devCParam2 : devUnCParam2), (match ? devCParam3 : devUnCParam3));		break
		default:
			dev."$cmd"();	break
	}
}

private setValType(val)	{
	def value
	switch(attrTyp)		{
		case "Text":	value = (attrCase ? val.toString().toLowerCase() : val.toString());		break
		case "Number":	value = val as Integer;		break
		case "Decimal":	value = val as BigDecimal;	break
		default:		value = null;				break
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
			evtVal = null
			break
	}
//log.debug "aDs: $aDs | eV: $eV"
	return eV
}

private toMap(p)	{  return ((p =~ /^\[(([a-z]+:[0-9]+)[\s|,]*)+\]$/).matches() ? evaluate(p) : p)  }

def appButtonHandler(btn)	{  if (btn == 'enableWATO') state.watoDisabled = false;		else if (btn == 'disableWATO') state.watoDisabled = true;  }
