/***********************************************************************************************************************
*  Copyright 2018 bangali
*
*  Contributors:
*       https://github.com/jebbett      code for new weather icons based on weather condition data
*       https://www.deviantart.com/vclouds/art/VClouds-Weather-Icons-179152045     new weather icons courtesy of VClouds
*		https://github.com/arnbme		code for mytile
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
*  ApiXU Weather Driver
*
*  Author: bangali
*
*  Date: 2018-05-27
*
*  attribution: weather data courtesy: https://www.apixu.com/
*
*  attribution: sunrise and sunset courtesy: https://sunrise-sunset.org/
*
* for use with HUBITAT so no tiles
*
* features:
* - supports global weather data with free api key from apixu.com
* - provides calculated illuminance data based on time of day and weather condition code.
* - no local server setup needed
* - no personal weather station needed
*
***********************************************************************************************************************/

public static String version()      {  return "v5.1.0"  }

/***********************************************************************************************************************
*
* Version: 5.1.0
*	5/18/2019: added precipication forecast data from day - 2 to day + 2
*
* Version: 5.0.5
*	5/4/2019: fixed typos for feelsLike* and added condition code for day plus 1 forecasted data
*
* Version: 5.0.2
*	4/20/2019: allow selection for publishing feelsLike and wind attribuets
*
* Version: 5.0.1
*	3/24/2019: revert typo
*
* Version: 5.0.0
*	3/10/2019: allow selection of which attributes to publish
*	3/10/2019: restore localSunrise and localSunset attributes
*   3/10/2019: added option for lux polling interval
*   3/10/2019: added expanded weather polling interval
*
* Version: 4.3.1
*   1/20/2019: change icon size for mytile attribute
*
* Version: 4.3.0
*   12/30/2018: removed isStateChange:true based on testing done by @nh.schottfam on hubitat format
*
* Version: 4.2.0
*   12/30/2018: deprecated localSunrise and localSunset attributes instead use local_sunrise and local_sunset respectively
*
* Version: 4.1.0
*   12/29/2018: merged mytile code
*
* Version: 4.0.3
*   12/09/2018: added wind speed in MPS (meters per second)
*
* Version: 4.0.2
*   10/28/2018: continue publishing lux even if apixu api call fails.
*
* Version: 4.0.1
*   10/14/2018: removed logging of weather data.
*
* Version: 4.0.0
*   8/16/2018: added optional weather undergroud mappings.
*   8/16/2018: added forecast icon, high and low temperature for next day.
*
* Version: 3.5.0
*   8/10/2018: added temperature, pressure and humidity capabilities.
*
* Version: 3.0.0
*   7/25/2018: added code contribution from https://github.com/jebbett for new cooler weather icons with icons courtesy
*                 of https://www.deviantart.com/vclouds/art/VClouds-Weather-Icons-179152045.
*
* Version: 2.5.0
*   5/23/2018: update condition_icon to contain image for use on dashboard and moved icon url to condition_icon_url.
*
* Version: 2.0.0
*   5/29/2018: updated lux calculation with factor from condition code.
*
* Version: 1.0.0
*   5/27/2018: initial release.
*
*/

import groovy.transform.Field

metadata    {
    definition (name: "ApiXU Weather Driver", namespace: "bangali", author: "bangali")  {
        capability "Actuator"
        capability "Sensor"
        capability "Polling"
        capability "Illuminance Measurement"
        capability "Temperature Measurement"
        capability "Relative Humidity Measurement"
        capability "Pressure Measurement"
        capability "Ultraviolet Index"
//        capability "Switch"

        attribute "name", "string"
        attribute "region", "string"
        attribute "country", "string"
        attribute "lat", "string"
        attribute "lon", "string"
        attribute "tz_id", "string"
        attribute "localtime_epoch", "string"
        attribute "local_time", "string"
        attribute "local_date", "string"
        attribute "last_updated_epoch", "string"
        attribute "last_updated", "string"
//        attribute "temp_c", "string"
//        attribute "temp_f", "string"
        attribute "is_day", "string"
        attribute "condition_text", "string"
        attribute "condition_icon", "string"
        attribute "condition_icon_url", "string"
        attribute "condition_code", "string"
        attribute "visual", "string"
        attribute "visualWithText", "string"
        attribute "wind_mph", "string"
        attribute "wind_kph", "string"
		attribute "wind_mps", "string"
        attribute "wind_degree", "string"
        attribute "wind_dir", "string"
//        attribute "pressure_mb", "string"
//        attribute "pressure_in", "string"
        attribute "precip_mm", "string"
        attribute "precip_in", "string"

		attribute "precipDayMinus2", "string"
		attribute "precipDayMinus1", "string"
		attribute "precipDay0", "string"
		attribute "precipDayPlus1", "string"
		attribute "precipDayPlus2", "string"

        attribute "cloud", "string"
        attribute "feelsLike_c", "string"
        attribute "feelsLike_f", "string"
        attribute "vis_km", "string"
        attribute "vis_miles", "string"

        attribute "location", "string"
        attribute "city", "string"
        attribute "local_sunrise", "string"
        attribute "local_sunset", "string"
        attribute "twilight_begin", "string"
        attribute "twilight_end", "string"
        attribute "illuminated", "string"
        attribute "cCF", "string"
        attribute "lastXUupdate", "string"

        attribute "weather", "string"
        attribute "forecastIcon", "string"
        attribute "feelsLike", "string"
        attribute "wind", "string"
        attribute "percentPrecip", "string"

        attribute "localSunrise", "string"
        attribute "localSunset", "string"

		attribute "condition_codeDayPlus1", "string"
        attribute "visualDayPlus1", "string"
        attribute "visualDayPlus1WithText", "string"
        attribute "temperatureLowDayPlus1", "string"
        attribute "temperatureHighDayPlus1", "string"
        attribute "wind_mytile", "string"
        attribute "mytile", "string"

        command "refresh"
    }

    preferences     {
        input "zipCode", "text", title:"Zip code or city name or latitude,longitude?", required:true
        input "apixuKey", "text", title:"ApiXU key?", required:true
        input "cityName", "text", title: "Override default city name?", required:false, defaultValue:null
        input "isFahrenheit", "bool", title:"Use Imperial units?", required:true, defaultValue:true
//        input "publishWU", "bool", title: "Publish WU mappings?", required: true, defaultValue: false
        input "dashClock", "bool", title:"Flash time ':' every 2 seconds?", required:true, defaultValue:false
        input "pollEvery", "enum", title:"Poll ApiXU how frequently?\nrecommended setting 30 minutes.\nilluminance is always updated every 5 minutes.", required:true, defaultValue:30, options:[5:"5 minutes",10:"10 minutes",15:"15 minutes",30:"30 minutes"]
		input "luxEvery", "enum", title:"Publish illuminance how frequently?", required:true, defaultValue:5, options:[5:"5 minutes",10:"10 minutes",15:"15 minutes",30:"30 minutes"]
//		input "publishAttrs", "enum", title:"Publish which attributes?", required:true, multiple:true, options:attributesMap
		for (def attr : attributesMap)
			input "${attr.key}Publish", "bool", title: "$attr.value", required: true, defaultValue: true
    }

}

def updated()   {
	unschedule()
    state.tz_id = null
	state.localDate = null
	state.forecastPrecip = [date: null, precipDayMinus2:[in:999.9, mm:999.9], precipDayMinus1:[in:999.9, mm:999.9], precipDay0:[in:999.9, mm:999.9], precipDayPlus1:[in:999.9, mm:999.9], precipDayPlus2:[in:999.9, mm:999.9]]
    state.clockSeconds = true
    poll()
    "runEvery${pollEvery}Minutes"(poll)
    "runEvery${luxEvery}Minutes"(updateLux)
//    schedule("0 * * * * ?", updateClock)
//    schedule("0/2 0 0 ? * * *", updateClock)
    if (dashClock)  updateClock();
}

def poll()      {
    log.debug ">>>>> apixu: Executing 'poll', location: $zipCode"

    def obs = getXUdata()
    if (!obs)   {
        log.warn "No response from ApiXU API"
        return
    }
//log.debug obs

    def now = new Date().format('yyyy-MM-dd HH:mm', location.timeZone)
    sendEvent(name: "lastXUupdate", value: now, displayed: true)

    def tZ = TimeZone.getTimeZone(obs.location.tz_id)
    state.tz_id = obs.location.tz_id

    def localTime = new Date().parse("yyyy-MM-dd HH:mm", obs.location.localtime, tZ)
    def localDate = localTime.format("yyyy-MM-dd", tZ)
    def localTimeOnly = localTime.format("HH:mm", tZ)

    def sunriseAndSunset = getSunriseAndSunset(obs.location.lat, obs.location.lon, localDate)
    def sunriseTime = new Date().parse("yyyy-MM-dd'T'HH:mm:ssXXX", sunriseAndSunset.results.sunrise, tZ)
    def sunsetTime = new Date().parse("yyyy-MM-dd'T'HH:mm:ssXXX", sunriseAndSunset.results.sunset, tZ)
    def noonTime = new Date().parse("yyyy-MM-dd'T'HH:mm:ssXXX", sunriseAndSunset.results.solar_noon, tZ)
    def twilight_begin = new Date().parse("yyyy-MM-dd'T'HH:mm:ssXXX", sunriseAndSunset.results.civil_twilight_begin, tZ)
    def twilight_end = new Date().parse("yyyy-MM-dd'T'HH:mm:ssXXX", sunriseAndSunset.results.civil_twilight_end, tZ)

    def localSunrise = sunriseTime.format("HH:mm", tZ)
    sendEventPublish(name: "local_sunrise", value: localSunrise, descriptionText: "Sunrise today is at $localSunrise", displayed: true)
    def localSunset = sunsetTime.format("HH:mm", tZ)
    sendEventPublish(name: "local_sunset", value: localSunset, descriptionText: "Sunset today at is $localSunset", displayed: true)
    def tB = twilight_begin.format("HH:mm", tZ)
    sendEventPublish(name: "twilight_begin", value: tB, descriptionText: "Twilight begins today at $tB", displayed: true)
    def tE = twilight_end.format("HH:mm", tZ)
    sendEventPublish(name: "twilight_end", value: tE, descriptionText: "Twilight ends today at $tE", displayed: true)

    state.sunriseTime = sunriseTime.format("yyyy-MM-dd'T'HH:mm:ssXXX", tZ)
    state.sunsetTime = sunsetTime.format("yyyy-MM-dd'T'HH:mm:ssXXX", tZ)
    state.noonTime = noonTime.format("yyyy-MM-dd'T'HH:mm:ssXXX", tZ)
    state.twilight_begin = twilight_begin.format("yyyy-MM-dd'T'HH:mm:ssXXX", tZ)
    state.twilight_end = twilight_end.format("yyyy-MM-dd'T'HH:mm:ssXXX", tZ)

    sendEventPublish(name: "name", value: obs.location.name, displayed: true)
    sendEventPublish(name: "region", value: obs.location.region, displayed: true)
    sendEventPublish(name: "country", value: obs.location.country, displayed: true)
    sendEventPublish(name: "lat", value: obs.location.lat, displayed: true)
    sendEventPublish(name: "lon", value: obs.location.lon, displayed: true)
    sendEventPublish(name: "tz_id", value: obs.location.tz_id, displayed: true)
    sendEventPublish(name: "localtime_epoch", value: obs.location.localtime_epoch, displayed: true)
    sendEventPublish(name: "local_time", value: localTimeOnly, displayed: true)
    sendEventPublish(name: "local_date", value: localDate, displayed: true)
    sendEventPublish(name: "last_updated_epoch", value: obs.current.last_updated_epoch, displayed: true)
    sendEventPublish(name: "last_updated", value: obs.current.last_updated, displayed: true)
//    sendEventPublish(name: "temp_c", value: obs.current.temp_c, unit: "C")
//    sendEventPublish(name: "temp_f", value: obs.current.temp_f, unit: "F")
    sendEventPublish(name: "temperature", value: (isFahrenheit ? obs.current.temp_f : obs.current.temp_c), unit: "${(isFahrenheit ? 'F' : 'C')}", displayed: true)
    sendEventPublish(name: "is_day", value: obs.current.is_day, displayed: true)
    sendEventPublish(name: "condition_text", value: obs.current.condition.text, displayed: true)
    sendEventPublish(name: "condition_icon", value: '<img src=https:' + obs.current.condition.icon + '>', displayed: true)
    sendEventPublish(name: "condition_icon_url", value: 'https:' + obs.current.condition.icon, displayed: true)
    sendEventPublish(name: "condition_code", value: obs.current.condition.code, displayed: true)
    def imgName = getImgName(obs.current.condition.code, obs.current.is_day)
    sendEventPublish(name: "visual", value: '<img src=' + imgName + '>', displayed: true)
    sendEventPublish(name: "visualWithText", value: '<img src=' + imgName + '><br>' + obs.current.condition.text, displayed: true)
	if (isFahrenheit)	{
	    sendEventPublish(name: "wind_mph", value: obs.current.wind_mph, unit: "MPH", displayed: true)
		sendEventPublish(name: "wind_mps", value: ((obs.current.wind_kph / 3.6f).round(1)), unit: "MPS", displayed: true)
	}
	else
    	sendEventPublish(name: "wind_kph", value: obs.current.wind_kph, unit: "KPH", displayed: true)
    sendEventPublish(name: "wind_degree", value: obs.current.wind_degree, unit: "DEGREE", displayed: true)
    sendEventPublish(name: "wind_dir", value: obs.current.wind_dir, displayed: true)
//    sendEventPublish(name: "pressure_mb", value: obs.current.pressure_mb, unit: "MBAR")
//    sendEventPublish(name: "pressure_in", value: obs.current.pressure_in, unit: "IN")
    sendEventPublish(name: "pressure", value: (isFahrenheit ? obs.current.pressure_in : obs.current.pressure_mb), unit: "${(isFahrenheit ? 'IN' : 'MBAR')}", displayed: true)
	if (isFahrenheit)	{
	    sendEventPublish(name: "precip_in", value: obs.current.precip_in, unit: "IN", displayed: true)
		sendEventPublish(name: "feelsLike_f", value: obs.current.feelslike_f, unit: "F", displayed: true)
		sendEventPublish(name: "vis_miles", value: obs.current.vis_miles, unit: "MILES", displayed: true)
	}
	else	{
		sendEventPublish(name: "precip_mm", value: obs.current.precip_mm, unit: "MM", displayed: true)
		sendEventPublish(name: "feelsLike_c", value: obs.current.feelslike_c, unit: "C", displayed: true)
	    sendEventPublish(name: "vis_km", value: obs.current.vis_km, unit: "KM", displayed: true)
	}
    sendEventPublish(name: "humidity", value: obs.current.humidity, unit: "%", displayed: true)
    sendEventPublish(name: "cloud", value: obs.current.cloud, unit: "%", displayed: true)

    sendEventPublish(name: "condition_icon_only", value: obs.current.condition.icon.split("/")[-1], displayed: true)
    sendEventPublish(name: "location", value: obs.location.name + ', ' + obs.location.region, displayed: true)
    state.condition_code = obs.current.condition.code
    state.cloud = obs.current.cloud
    updateLux()

//    if (publishWU)      {
        sendEventPublish(name: "city", value: (cityName ?: obs.location.name), displayed: true)
        sendEventPublish(name: "weather", value: obs.current.condition.text, displayed: true)
        sendEventPublish(name: "forecastIcon", value: getWUIconName(obs.current.condition.code, obs.current.is_day), displayed: true)
        sendEventPublish(name: "feelsLike", value: (isFahrenheit ? obs.current.feelslike_f : obs.current.feelslike_c), unit: "${(isFahrenheit ? 'F' : 'C')}", displayed: true)
        sendEventPublish(name: "wind", value: (isFahrenheit ? obs.current.wind_mph : obs.current.wind_kph), unit: "${(isFahrenheit ? 'MPH' : 'KPH')}", displayed: true)
        sendEventPublish(name: "percentPrecip", value: (isFahrenheit ? obs.current.precip_in : obs.current.precip_mm), unit: "${(isFahrenheit ? 'IN' : 'MM')}", displayed: true)
        sendEventPublish(name: "localSunrise", value: localSunrise, displayed: true)
        sendEventPublish(name: "localSunset", value: localSunset, displayed: true)
//    }

	def wind_mytile=(isFahrenheit ? "${Math.round(obs.current.wind_mph)}" + " mph " : "${Math.round(obs.current.wind_kph)}" + " kph ")
	sendEventPublish(name: "wind_mytile", value: wind_mytile, displayed: true)

	sendEventPublish(name: "condition_codeDayPlus1", value: obs.forecast.forecastday[0].day.condition.code, displayed: true)
    def imgNamePlus1 = getImgName(obs.forecast.forecastday[0].day.condition.code, 1)
    sendEventPublish(name: "visualDayPlus1", value: '<img src=' + imgNamePlus1 + '>', displayed: true)
    sendEventPublish(name: "visualDayPlus1WithText", value: '<img src=' + imgNamePlus1 + '><br>' + obs.forecast.forecastday[0].day.condition.text, displayed: true)
    sendEventPublish(name: "temperatureHighDayPlus1", value: (isFahrenheit ? obs.forecast.forecastday[0].day.maxtemp_f :
                            obs.forecast.forecastday[0].day.maxtemp_c), unit: "${(isFahrenheit ? 'F' : 'C')}", displayed: true)
    sendEventPublish(name: "temperatureLowDayPlus1", value: (isFahrenheit ? obs.forecast.forecastday[0].day.mintemp_f :
                            obs.forecast.forecastday[0].day.mintemp_c), unit: "${(isFahrenheit ? 'F' : 'C')}", displayed: true)

	forecastPrecip(obs.forecast)

	def mytext = obs.location.name + ', ' + obs.location.region
//	if (isFahrenheit)	{
//		mytext += '<br>' + "${Math.round(obs.current.temp_f)}" + '&deg;F ' + obs.current.humidity + '%'
//		mytext += '<br>' + localSunrise + ' <img style="height:1em" src=https:' + obs.current.condition.icon + '> ' + localSunset
//		mytext += (wind_mytile == "0 mph " ? '<br> Wind is calm' : '<br>' + obs.current.wind_dir + ' ' + wind_mytile)
//		if (wind_mytile == "0 mph ")
//			mytext+='<br> Wind is calm'
//		else
//			mytext+='<br>' + obs.current.wind_dir + ' ' + wind_mytile
//		mytext += '<br>' + obs.current.condition.text
//	}
//	else	{
//		mytext += '<br>' + obs.current.temp_c + '&deg;C ' + obs.current.humidity + '%'
//		mytext += '<br>' + localSunrise + ' <img style="height:1.5em" src=https:' + obs.current.condition.icon + '> ' + localSunset
//		mytext += (wind_mytile == "0 kph " ? '<br> Wind is calm' : '<br>' + obs.current.wind_dir + ' ' + wind_mytile)
//		if (wind_mytile == "0 kph ")
//			mytext+='<br> Wind is calm'
//		else
//			mytext+='<br>' + obs.current.wind_dir + ' ' + wind_mytile
//		mytext += '<br>' + obs.current.condition.text
//	}
	mytext += '<br>' + (isFahrenheit ? "${Math.round(obs.current.temp_f)}" + '&deg;F ' : obs.current.temp_c + '&deg;C ') + obs.current.humidity + '%'
	mytext += '<br>' + localSunrise + ' <img style="height:2em" src=' + imgName + '> ' + localSunset
	mytext += (wind_mytile == (isFahrenheit ? "0 mph " : "0 kph ") ? '<br> Wind is calm' : '<br>' + obs.current.wind_dir + ' ' + wind_mytile)
	mytext += '<br>' + obs.current.condition.text

    sendEventPublish(name: "mytile", value: mytext, displayed: true)
    return
}

private forecastPrecip(forecast)	{
	if (!state.tz_id)       return;
    def nowTime = new Date()
    def tZ = TimeZone.getTimeZone(state.tz_id)
    def localDate = nowTime.format("yyyy-MM-dd", tZ)
    if (localDate == state.forecastPrecip.date)		return;

	state.forecastPrecip.date = localDate
	state.forecastPrecip.precipDayMinus2 = state.forecastPrecip.precipDayMinus1
	state.forecastPrecip.precipDayMinus1 = state.forecastPrecip.precipDay0
	state.forecastPrecip.precipDay0 = state.forecastPrecip.precipDayPlus1
	state.forecastPrecip.precipDayPlus1.mm = forecast.forecastday[0].day.totalprecip_mm
	state.forecastPrecip.precipDayPlus1.in = forecast.forecastday[0].day.totalprecip_in
	state.forecastPrecip.precipDayPlus2.mm = forecast.forecastday[1].day.totalprecip_mm
	state.forecastPrecip.precipDayPlus2.in = forecast.forecastday[1].day.totalprecip_in

	sendEventPublish(name: "precipDayMinus2", value: (isFahrenheit ? state.forecastPrecip.precipDayMinus2.in : state.forecastPrecip.precipDayMinus2.mm), unit: "${(isFahrenheit ? 'IN' : 'MM')}", displayed: true)
	sendEventPublish(name: "precipDayMinus1", value: (isFahrenheit ? state.forecastPrecip.precipDayMinus1.in : state.forecastPrecip.precipDayMinus1.mm), unit: "${(isFahrenheit ? 'IN' : 'MM')}", displayed: true)
	sendEventPublish(name: "precipDay0", value: (isFahrenheit ? state.forecastPrecip.precipDay0.in : state.forecastPrecip.precipDay0.mm), unit: "${(isFahrenheit ? 'IN' : 'MM')}", displayed: true)
	sendEventPublish(name: "precipDayPlus1", value: (isFahrenheit ? state.forecastPrecip.precipDayPlus1.in : state.forecastPrecip.precipDayPlus1.mm), unit: "${(isFahrenheit ? 'IN' : 'MM')}", displayed: true)
	sendEventPublish(name: "precipDayPlus2", value: (isFahrenheit ? state.forecastPrecip.precipDayPlus2.in : state.forecastPrecip.precipDayPlus2.mm), unit: "${(isFahrenheit ? 'IN' : 'MM')}", displayed: true)
}

def refresh()       { poll() }

def configure()     { poll() }

private getXUdata()   {
    def obs = [:]
    def params = [ uri: "https://api.apixu.com/v1/forecast.json?key=$apixuKey&q=$zipCode&days=3" ]
    try {
        httpGet(params)		{ resp ->
            if (resp?.data)     obs << resp.data;
            else                log.error "http call for ApiXU weather api did not return data: $resp";
        }
    } catch (e) { log.error "http call failed for ApiXU weather api: $e" }
//    log.debug "$obs"
    return obs
}

private getSunriseAndSunset(latitude, longitude, forDate)	{
    def params = [ uri: "https://api.sunrise-sunset.org/json?lat=$latitude&lng=$longitude&date=$forDate&formatted=0" ]
    def sunRiseAndSet = [:]
    try {
        httpGet(params)		{ resp -> sunRiseAndSet = resp.data }
    } catch (e) { log.error "http call failed for sunrise and sunset api: $e" }

    return sunRiseAndSet
}

def updateLux()     {
    if (!state.sunriseTime || !state.sunsetTime || !state.noonTime || !state.twilight_begin || !state.twilight_end || !state.tz_id)
        return

    def tZ = TimeZone.getTimeZone(state.tz_id)
    def lT = new Date().format("yyyy-MM-dd'T'HH:mm:ssXXX", tZ)
    def localTime = new Date().parse("yyyy-MM-dd'T'HH:mm:ssXXX", lT, tZ)
    def sunriseTime = new Date().parse("yyyy-MM-dd'T'HH:mm:ssXXX", state.sunriseTime, tZ)
    def sunsetTime = new Date().parse("yyyy-MM-dd'T'HH:mm:ssXXX", state.sunsetTime, tZ)
    def noonTime = new Date().parse("yyyy-MM-dd'T'HH:mm:ssXXX", state.noonTime, tZ)
    def twilight_begin = new Date().parse("yyyy-MM-dd'T'HH:mm:ssXXX", state.twilight_begin, tZ)
    def twilight_end = new Date().parse("yyyy-MM-dd'T'HH:mm:ssXXX", state.twilight_end, tZ)
    def lux = estimateLux(localTime, sunriseTime, sunsetTime, noonTime, twilight_begin, twilight_end, state.condition_code, state.cloud, state.tz_id)
    sendEventPublish(name: "illuminance", value: lux, unit: "lux", displayed: true)
    sendEventPublish(name: "illuminated", value: String.format("%,d lux", lux), displayed: true)
}

private estimateLux(localTime, sunriseTime, sunsetTime, noonTime, twilight_begin, twilight_end, condition_code, cloud, tz_id)     {
//    log.debug "condition_code: $condition_code | cloud: $cloud"
//    log.debug "twilight_begin: $twilight_begin | twilight_end: $twilight_end | tz_id: $tz_id"
//    log.debug "localTime: $localTime | sunriseTime: $sunriseTime | noonTime: $noonTime | sunsetTime: $sunsetTime"

    def tZ = TimeZone.getTimeZone(tz_id)
    def lux = 0l
    def aFCC = true
    def l

    if (timeOfDayIsBetween(sunriseTime, noonTime, localTime, tZ))      {
//        log.debug "between sunrise and noon"
        l = (((localTime.getTime() - sunriseTime.getTime()) * 10000f) / (noonTime.getTime() - sunriseTime.getTime()))
        lux = (l < 50f ? 50l : l.trunc(0) as long)
    }
    else if (timeOfDayIsBetween(noonTime, sunsetTime, localTime, tZ))      {
//        log.debug "between noon and sunset"
        l = (((sunsetTime.getTime() - localTime.getTime()) * 10000f) / (sunsetTime.getTime() - noonTime.getTime()))
        lux = (l < 50f ? 50l : l.trunc(0) as long)
    }
    else if (timeOfDayIsBetween(twilight_begin, sunriseTime, localTime, tZ))      {
//        log.debug "between sunrise and twilight"
        l = (((localTime.getTime() - twilight_begin.getTime()) * 50f) / (sunriseTime.getTime() - twilight_begin.getTime()))
        lux = (l < 10f ? 10l : l.trunc(0) as long)
    }
    else if (timeOfDayIsBetween(sunsetTime, twilight_end, localTime, tZ))      {
//        log.debug "between sunset and twilight"
        l = (((twilight_end.getTime() - localTime.getTime()) * 50f) / (twilight_end.getTime() - sunsetTime.getTime()))
        lux = (l < 10f ? 10l : l.trunc(0) as long)
    }
    else if (!timeOfDayIsBetween(twilight_begin, twilight_end, localTime, tZ))      {
//        log.debug "between non-twilight"
        lux = 5l
        aFCC = false
    }

    def cC = condition_code.toInteger()
    def cCT = ''
    def cCF
    if (aFCC)
        if (conditionFactor[cC])    {
            cCF = conditionFactor[cC][1]
            cCT = conditionFactor[cC][0]
        }
        else    {
            cCF = ((100 - (cloud.toInteger() / 3d)) / 100).round(1)
            cCT = 'using cloud cover'
        }
    else    {
        cCF = 1.0
        cCT = 'night time now'
    }

    lux = (lux * cCF) as long
//    log.debug "condition: $cC | condition text: $cCT | condition factor: $cCF | lux: $lux"
    sendEventPublish(name: "cCF", value: cCF, displayed: true)

    return lux
}

private timeOfDayIsBetween(fromDate, toDate, checkDate, timeZone)     {
    return (!checkDate.before(fromDate) && !checkDate.after(toDate))
}

private sendEventPublish(evt)	{
	def var = "${evt.name + 'Publish'}"
//	log.debug var
	def pub = this[var]
	if (pub)		sendEvent(name: evt.name, value: evt.value, descriptionText: evt.descriptionText, unit: evt.unit, displayed: evt.displayed);
//	log.debug pub
}

def updateClock()       {
    runIn(2, updateClock)
    if (!state.tz_id)       return;
    if (!tz_id)       return;
    def nowTime = new Date()
    def tZ = TimeZone.getTimeZone(state.tz_id)
    sendEventPublish(name: "local_time", value: nowTime.format((state.clockSeconds ? "HH:mm" : "HH mm"), tZ), displayed: true)
    def localDate = nowTime.format("yyyy-MM-dd", tZ)
    if (localDate != state.localDate)
    {   state.localDate = localDate
        sendEventPublish(name: "local_date", value: localDate, displayed: true)
    }
    state.clockSeconds = (state.clockSeconds ? false : true)
}

def getWUIconName(condition_code, is_day)     {
    def cC = condition_code.toInteger()
    def wuIcon = (conditionFactor[cC] ? conditionFactor[cC][2] : '')
    if (is_day != 1 && wuIcon)    wuIcon = 'nt_' + wuIcon;
    return wuIcon
}

@Field final Map    conditionFactor = [
        1000: ['Sunny', 1, 'sunny'],                                        1003: ['Partly cloudy', 0.8, 'partlycloudy'],
        1006: ['Cloudy', 0.6, 'cloudy'],                                    1009: ['Overcast', 0.5, 'cloudy'],
        1030: ['Mist', 0.5, 'fog'],                                         1063: ['Patchy rain possible', 0.8, 'chancerain'],
        1066: ['Patchy snow possible', 0.6, 'chancesnow'],                  1069: ['Patchy sleet possible', 0.6, 'chancesleet'],
        1072: ['Patchy freezing drizzle possible', 0.4, 'chancesleet'],     1087: ['Thundery outbreaks possible', 0.2, 'chancetstorms'],
        1114: ['Blowing snow', 0.3, 'snow'],                                1117: ['Blizzard', 0.1, 'snow'],
        1135: ['Fog', 0.2, 'fog'],                                          1147: ['Freezing fog', 0.1, 'fog'],
        1150: ['Patchy light drizzle', 0.8, 'rain'],                        1153: ['Light drizzle', 0.7, 'rain'],
        1168: ['Freezing drizzle', 0.5, 'sleet'],                           1171: ['Heavy freezing drizzle', 0.2, 'sleet'],
        1180: ['Patchy light rain', 0.8, 'rain'],                           1183: ['Light rain', 0.7, 'rain'],
        1186: ['Moderate rain at times', 0.5, 'rain'],                      1189: ['Moderate rain', 0.4, 'rain'],
        1192: ['Heavy rain at times', 0.3, 'rain'],                         1195: ['Heavy rain', 0.2, 'rain'],
        1198: ['Light freezing rain', 0.7, 'sleet'],                        1201: ['Moderate or heavy freezing rain', 0.3, 'sleet'],
        1204: ['Light sleet', 0.5, 'sleet'],                                1207: ['Moderate or heavy sleet', 0.3, 'sleet'],
        1210: ['Patchy light snow', 0.8, 'flurries'],                       1213: ['Light snow', 0.7, 'snow'],
        1216: ['Patchy moderate snow', 0.6, 'snow'],                        1219: ['Moderate snow', 0.5, 'snow'],
        1222: ['Patchy heavy snow', 0.4, 'snow'],                           1225: ['Heavy snow', 0.3, 'snow'],
        1237: ['Ice pellets', 0.5, 'sleet'],                                1240: ['Light rain shower', 0.8, 'rain'],
        1243: ['Moderate or heavy rain shower', 0.3, 'rain'],               1246: ['Torrential rain shower', 0.1, 'rain'],
        1249: ['Light sleet showers', 0.7, 'sleet'],                        1252: ['Moderate or heavy sleet showers', 0.5, 'sleet'],
        1255: ['Light snow showers', 0.7, 'snow'],                          1258: ['Moderate or heavy snow showers', 0.5, 'snow'],
        1261: ['Light showers of ice pellets', 0.7, 'sleet'],               1264: ['Moderate or heavy showers of ice pellets',0.3, 'sleet'],
        1273: ['Patchy light rain with thunder', 0.5, 'tstorms'],           1276: ['Moderate or heavy rain with thunder', 0.3, 'tstorms'],
        1279: ['Patchy light snow with thunder', 0.5, 'tstorms'],           1282: ['Moderate or heavy snow with thunder', 0.3, 'tstorms']
    ]

private getImgName(wCode, is_day)       {
    def url = "https://cdn.rawgit.com/adey/bangali/master/resources/icons/weather/"
    def imgItem = imgNames.find{ it.code == wCode && it.day == is_day }
    return (url + (imgItem ? imgItem.img : 'na.png'))
}

@Field final List    imgNames =     [
        [code: 1000, day: 1, img: '32.png', ],	// DAY - Sunny
        [code: 1003, day: 1, img: '30.png', ],	// DAY - Partly cloudy
        [code: 1006, day: 1, img: '28.png', ],	// DAY - Cloudy
        [code: 1009, day: 1, img: '26.png', ],	// DAY - Overcast
        [code: 1030, day: 1, img: '20.png', ],	// DAY - Mist
        [code: 1063, day: 1, img: '39.png', ],	// DAY - Patchy rain possible
        [code: 1066, day: 1, img: '41.png', ],	// DAY - Patchy snow possible
        [code: 1069, day: 1, img: '41.png', ],	// DAY - Patchy sleet possible
        [code: 1072, day: 1, img: '39.png', ],	// DAY - Patchy freezing drizzle possible
        [code: 1087, day: 1, img: '38.png', ],	// DAY - Thundery outbreaks possible
        [code: 1114, day: 1, img: '15.png', ],	// DAY - Blowing snow
        [code: 1117, day: 1, img: '16.png', ],	// DAY - Blizzard
        [code: 1135, day: 1, img: '21.png', ],	// DAY - Fog
        [code: 1147, day: 1, img: '21.png', ],	// DAY - Freezing fog
        [code: 1150, day: 1, img: '39.png', ],	// DAY - Patchy light drizzle
        [code: 1153, day: 1, img: '11.png', ],	// DAY - Light drizzle
        [code: 1168, day: 1, img: '8.png', ],	// DAY - Freezing drizzle
        [code: 1171, day: 1, img: '10.png', ],	// DAY - Heavy freezing drizzle
        [code: 1180, day: 1, img: '39.png', ],	// DAY - Patchy light rain
        [code: 1183, day: 1, img: '11.png', ],	// DAY - Light rain
        [code: 1186, day: 1, img: '39.png', ],	// DAY - Moderate rain at times
        [code: 1189, day: 1, img: '12.png', ],	// DAY - Moderate rain
        [code: 1192, day: 1, img: '39.png', ],	// DAY - Heavy rain at times
        [code: 1195, day: 1, img: '12.png', ],	// DAY - Heavy rain
        [code: 1198, day: 1, img: '8.png', ],	// DAY - Light freezing rain
        [code: 1201, day: 1, img: '10.png', ],	// DAY - Moderate or heavy freezing rain
        [code: 1204, day: 1, img: '5.png', ],	// DAY - Light sleet
        [code: 1207, day: 1, img: '6.png', ],	// DAY - Moderate or heavy sleet
        [code: 1210, day: 1, img: '41.png', ],	// DAY - Patchy light snow
        [code: 1213, day: 1, img: '18.png', ],	// DAY - Light snow
        [code: 1216, day: 1, img: '41.png', ],	// DAY - Patchy moderate snow
        [code: 1219, day: 1, img: '16.png', ],	// DAY - Moderate snow
        [code: 1222, day: 1, img: '41.png', ],	// DAY - Patchy heavy snow
        [code: 1225, day: 1, img: '16.png', ],	// DAY - Heavy snow
        [code: 1237, day: 1, img: '18.png', ],	// DAY - Ice pellets
        [code: 1240, day: 1, img: '11.png', ],	// DAY - Light rain shower
        [code: 1243, day: 1, img: '12.png', ],	// DAY - Moderate or heavy rain shower
        [code: 1246, day: 1, img: '12.png', ],	// DAY - Torrential rain shower
        [code: 1249, day: 1, img: '5.png', ],	// DAY - Light sleet showers
        [code: 1252, day: 1, img: '6.png', ],	// DAY - Moderate or heavy sleet showers
        [code: 1255, day: 1, img: '16.png', ],	// DAY - Light snow showers
        [code: 1258, day: 1, img: '16.png', ],	// DAY - Moderate or heavy snow showers
        [code: 1261, day: 1, img: '8.png', ],	// DAY - Light showers of ice pellets
        [code: 1264, day: 1, img: '10.png', ],	// DAY - Moderate or heavy showers of ice pellets
        [code: 1273, day: 1, img: '38.png', ],	// DAY - Patchy light rain with thunder
        [code: 1276, day: 1, img: '35.png', ],	// DAY - Moderate or heavy rain with thunder
        [code: 1279, day: 1, img: '41.png', ],	// DAY - Patchy light snow with thunder
        [code: 1282, day: 1, img: '18.png', ],	// DAY - Moderate or heavy snow with thunder
        [code: 1000, day: 0, img: '31.png', ],	// NIGHT - Clear
        [code: 1003, day: 0, img: '29.png', ],	// NIGHT - Partly cloudy
        [code: 1006, day: 0, img: '27.png', ],	// NIGHT - Cloudy
        [code: 1009, day: 0, img: '26.png', ],	// NIGHT - Overcast
        [code: 1030, day: 0, img: '20.png', ],	// NIGHT - Mist
        [code: 1063, day: 0, img: '45.png', ],	// NIGHT - Patchy rain possible
        [code: 1066, day: 0, img: '46.png', ],	// NIGHT - Patchy snow possible
        [code: 1069, day: 0, img: '46.png', ],	// NIGHT - Patchy sleet possible
        [code: 1072, day: 0, img: '45.png', ],	// NIGHT - Patchy freezing drizzle possible
        [code: 1087, day: 0, img: '47.png', ],	// NIGHT - Thundery outbreaks possible
        [code: 1114, day: 0, img: '15.png', ],	// NIGHT - Blowing snow
        [code: 1117, day: 0, img: '16.png', ],	// NIGHT - Blizzard
        [code: 1135, day: 0, img: '21.png', ],	// NIGHT - Fog
        [code: 1147, day: 0, img: '21.png', ],	// NIGHT - Freezing fog
        [code: 1150, day: 0, img: '45.png', ],	// NIGHT - Patchy light drizzle
        [code: 1153, day: 0, img: '11.png', ],	// NIGHT - Light drizzle
        [code: 1168, day: 0, img: '8.png', ],	// NIGHT - Freezing drizzle
        [code: 1171, day: 0, img: '10.png', ],	// NIGHT - Heavy freezing drizzle
        [code: 1180, day: 0, img: '45.png', ],	// NIGHT - Patchy light rain
        [code: 1183, day: 0, img: '11.png', ],	// NIGHT - Light rain
        [code: 1186, day: 0, img: '45.png', ],	// NIGHT - Moderate rain at times
        [code: 1189, day: 0, img: '12.png', ],	// NIGHT - Moderate rain
        [code: 1192, day: 0, img: '45.png', ],	// NIGHT - Heavy rain at times
        [code: 1195, day: 0, img: '12.png', ],	// NIGHT - Heavy rain
        [code: 1198, day: 0, img: '8.png', ],	// NIGHT - Light freezing rain
        [code: 1201, day: 0, img: '10.png', ],	// NIGHT - Moderate or heavy freezing rain
        [code: 1204, day: 0, img: '5.png', ],	// NIGHT - Light sleet
        [code: 1207, day: 0, img: '6.png', ],	// NIGHT - Moderate or heavy sleet
        [code: 1210, day: 0, img: '41.png', ],	// NIGHT - Patchy light snow
        [code: 1213, day: 0, img: '18.png', ],	// NIGHT - Light snow
        [code: 1216, day: 0, img: '41.png', ],	// NIGHT - Patchy moderate snow
        [code: 1219, day: 0, img: '16.png', ],	// NIGHT - Moderate snow
        [code: 1222, day: 0, img: '41.png', ],	// NIGHT - Patchy heavy snow
        [code: 1225, day: 0, img: '16.png', ],	// NIGHT - Heavy snow
        [code: 1237, day: 0, img: '18.png', ],	// NIGHT - Ice pellets
        [code: 1240, day: 0, img: '11.png', ],	// NIGHT - Light rain shower
        [code: 1243, day: 0, img: '12.png', ],	// NIGHT - Moderate or heavy rain shower
        [code: 1246, day: 0, img: '12.png', ],	// NIGHT - Torrential rain shower
        [code: 1249, day: 0, img: '5.png', ],	// NIGHT - Light sleet showers
        [code: 1252, day: 0, img: '6.png', ],	// NIGHT - Moderate or heavy sleet showers
        [code: 1255, day: 0, img: '16.png', ],	// NIGHT - Light snow showers
        [code: 1258, day: 0, img: '16.png', ],	// NIGHT - Moderate or heavy snow showers
        [code: 1261, day: 0, img: '8.png', ],	// NIGHT - Light showers of ice pellets
        [code: 1264, day: 0, img: '10.png', ],	// NIGHT - Moderate or heavy showers of ice pellets
        [code: 1273, day: 0, img: '47.png', ],	// NIGHT - Patchy light rain with thunder
        [code: 1276, day: 0, img: '35.png', ],	// NIGHT - Moderate or heavy rain with thunder
        [code: 1279, day: 0, img: '46.png', ],	// NIGHT - Patchy light snow with thunder
        [code: 1282, day: 0, img: '18.png', ]	// NIGHT - Moderate or heavy snow with thunder
]

@Field final Map	attributesMap = [
	city:				'City',
	cloud:				'Cloud',
	cCF:				'Cloud cover factor',
	condition_code:		'Condition code',
	condition_icon:		'Condition icon',
	condition_icon_only:'Condition icon only',
	condition_icon_url:	'Condition icon URL',
	condition_text:		'Condition text',
	weather:			'Condition text',
	country:			'Country',
	feelsLike:			'Feels like (in default unit)',
	feelsLike_c:		'Feels like °C',
	feelsLike_f:		'Feels like °F',
	forecastIcon:		'Forecast icon',
	humidity:			'Humidity',
	illuminance:		'Illuminance',
	illuminated:		'Dashboard illuminance',
	is_day:				'Is daytime',
	localtime_epoch:	'Localtime epoch',
	local_date:			'Local date',
	localSunrise:		'Local sunrise',
	local_sunrise:		'Local sunrise',
	localSunset:		'Local sunset',
	local_sunset:		'Local sunset',
	twilight_begin:		'Twilight begin',
	twilight_end:		'Twilight end',
	local_time:			'Local time',
	tz_id:				'Timezone ID',
	name:				'Location name',
	region:				'Region',
	location:			'Location name with region',
	lon:				'Longitude',
	lat:				'Latitude',
	last_updated:		'Last updated',
	last_updated_epoch:	'Last updated epoch',
	mytile:				'Mytile for dashboard',
	precip_mm:			'Precipitation MM',
	precip_in:			'Precipitation Inches',
	precipDayMinus2:	'Precipitation Day - 2',
	precipDayMinus1:	'Precipitation Day - 1',
	precipDay0:			'Precipitation Day - 0',
	precipDayPlus1:		'Precipitation Day + 1',
	precipDayPlus2:		'Precipitation Day + 2',
	percentPrecip:		'Percent precipitation',
	pressure:			'Pressure',
	temperature:		'Temperature',
	temperatureHighDayPlus1:'Temperature high day +1',
	temperatureLowDayPlus1:	'Temperature low day +1',
	vis_km:				'Visibility KM',
	vis_miles:			'Visibility miles',
	visual:				'Visual weather',
	condition_codeDayPlus1:	'Condition code day +1',
	visualDayPlus1:			'Visual weather day +1',
	visualDayPlus1WithText:	'Visual weather day +1 with text',
	visualWithText:		'Visual weather with text',
	wind:				'Wind (in default unit)',
	wind_degree:		'Wind Degree',
	wind_kph:			'Wind KPH',
	wind_mph:			'Wind MPH',
	wind_mps:			'Wind MPS',
	wind_degree:		'Wind degree',
	wind_dir:			'Wind direction',
	wind_mytile:		'Wind mytile'
]

//**********************************************************************************************************************
