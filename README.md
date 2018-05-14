# bangali's rooms automation

<img src="https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomOccupancySettings.png" width="175" style="float:left; width:110px; height:80px; padding-right: 30px;">
<h2 style="padding-top: 27px">Rooms Manager with Rooms Occupancy for Smartthings and Hubitat</h2>

<p style="padding-top: 35px">While ST has a concept of rooms it is essentially a grouping mechanism which does not enable automation. In contrast rooms occupancy considers the room as a meta device and automates common tasks associated with a “room” physical or virtual. <strong>What makes it really useful is not just the room's occupancy state but the ability to manage automation for rooms in a set of rules for the room based on the occupancy state of the room and data from various sensors.</strong> When creating a room device through the smartapp you are able to create these rules for the rooms making your rooms really smart.</p>

<p><i>You can continue reading here for the summarized version or read the more detailed and always the latest version on Github which also describes the individual settings:</i></p>
<p><a href="https://github.com/adey/bangali/blob/master/README.md">Rooms Manager and Rooms Occupancy readme on Github</a></p>

<p>What these rules enable is many common tasks around rooms which most users go through automating at some point. Usually through setting up a few rules or creating a few pistons. I have been there and done that myself. While those work to a degree, it does not enable the kind of comprehensive automation that should be possible for devices in a room based on sensor inputs. This smartapp makes that possible.</p>

<p>If there is one principle that these apps are built on, it is - that your home automation should work in the background in a repeatable and predictable manner without requiring periodic human intervention. In short - your automation should work for you and not the other way around. <i>But even more importantly perhaps, this app gets you the kind of WAF for your home automation that you have always dreamed about.</i> 🙂</p>

<p><i>You can continue reading here for the summarized version or read the more detailed and always the latest version on Github which also describes the individual settings:</i></p>
<p><a href="https://github.com/adey/bangali/blob/master/README.md">Rooms Manager and Rooms Occupancy readme on Github</a></p>

<p>Additionally, these room occupancy devices also have attributes, capabilities and commands which are useable in webCoRE or other smartapps like Smart Lighting in ST or Rule Machine in Hubitat. There is a range of other automations that webCoRE makes possible that could not otherwise be done without writing a custom smartapp for it. I use webCoRE for that and am I big fan of Adrian. So checkout webCoRE as well if you don't already use it.</p>

<h4>How does this app work?</h4>

<p><i>This app works by setting rooms occupancy to various states based on a set of sensors as specified by the user. It takes this state and attribute values from various sensors and evaluates a set of rules that you create to match the conditions. When all of the conditions for the rule matches, it executes actions specified on these matching rules.</i></p>

<p>As an example, you can specify motion sensors in Occupied settings to set a room state to Occupied when there is motion from any of those motion sensors. Then create a rule to turn on some lights. In this rule you could also set that these lights should only be turned on if a lux sensor is at a certain lux value or lower. Or you could specify that only turn on the lights during certain times. Or you could specify turn on the lights at a certain level during certain times and at another level during other times.</p>

<p>As a part of the Occupied settings you can also specify timeout values so the room does not indefinitely stay set to Occupied state and the lights turn off after a while when there is no motion. How quickly that happens is controlled by you through the timeout value you specify in the settings. By specifying settings in the Checking settings you are also able to dim the lights before the lights turn off completely so there is a visual cue to the room occupant that the lights will turn off because they have not moved in a while.</p>

<p>Off course you may be in the room while and not be moving for a while like reading a book or watching TV. That's when you use the Engaged settings to set the room to Engaged state. See below for additional details.</p>

<h4>Here are the common room occupancy states:</h4>

<ul>
    <img src="https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsOccupiedState.png" width="125" style="float:left; width:80px; height:50px; padding-right: 30px;">
        <h5>Occupied:</h5>
    <p>Occupied is you go to a room are in there for a few minutes then leave the room. Lights come on when you enter the room and turn off after a couple of minutes of your leaving the room. Think of Occupied as a transient state and Engaged below as a somewhat persistent state.</p>
    <img src="https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsEngagedState.png" width="125" style="float:left; width:80px; height:50px; padding-right: 30px;">
        <h5>Engaged:</h5>
    <p>Engaged is when you stay in a room for an extended period of time and may be motionless for some or all of the time. since we cant depend on the motion event for engaged state there are different options to set the room to engaged for extended occupancy. these are all under engaged settings and there is more coming. but these help make sure the switches you set to on stay on even if there is no motion in the room. When in Engaged state you have a different and longer timeout state than the Occupied state. So there is still a motion requirement but a much higher time threshold than the Occupied state.</p>
    <img src="https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsAsleepState.png" width="125" style="float:left; width:80px; height:50px; padding-right: 30px;">
        <h5>Asleep:</h5>
    <p>Asleep state is meant for use while the room should be 'asleep' as in not respond to most typical automation like motion automation. But it does allow for other automation like using a night light and using a button to turn on or off the night lights. You are still able to create rules for the Asleep state but it additionally support a little bit for Asleep state specific automation in the Asleep settings.</p>
    <img src="https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsVacantState.png" width="125" style="float:left; width:80px; height:50px; padding-right: 30px;">
        <h5>Vacant:</h5>
    <p>Vacant state is for when the room is vacant and you want everything to get turned off. It is possible to setup rules for Vacant settings as well but not required.</p>
    <img src="https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsCheckingState.png" width="125" style="float:left; width:80px; height:50px; padding-right: 30px;">
        <h5>Checking:</h5>
    <p>Occupied state is used for transition between states and not user controlled. For example, when moving from Occupied to Vacant occupancy state the room will transition to Checking state. While the app does not allow creating rules for checking state there is some settings available to control dimming of the lights when in Checking state.</p>
    <img src="https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsLockedState.png" width="125" style="float:left; width:80px; height:50px; padding-right: 30px;">
        <h5>Locked:</h5>
    <p>Locked state disables all automations for the room and allows you to control lights and other devices in the room either manually or some other way.</p>
</ul>

<p>The states 'locked', 'reserved', 'kaput' and 'donotdisturb' are effectively all similar in that they all disable automation. That being the case there is some sensors allowed to set / unset rooms to / from Locked state but no other automation beyond that for these occupancy states. Here is a quick description of the various top level settings and how the app works. <i>At the heart of the app is the concept of room states and rules to automate devices based on these room's states and other sensor inputs.</i> (In the following description when I talk about sensors it refers to devices that have attributes which are used to drive decisions in the room's rules.)</p>

<p><i>Note: Many of the following settings are optional but when specified will require other settings to be specified. Like specifying a motion sensor is optional. But if you do specify a motion sensor the motion event to trigger timeout countdown becomes required.</i></p>

<h4>Top level settings:</h4>

<ul>
    <img src="https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsOtherDevices.png" width="125" style="float:left; width:80px; height:50px; padding-right: 30px;">
    <h5>Room Devices</h5>
    <p>Room sensors used in checking rule conditions based occupancy state and data from these sensors.</p>
    <table class="wikitable" style="width:900px">
        <tr>
            <td style="width:15%">Motion sensor(s)</td>
            <td style="width:85%">Room motion sensor(s) for motion activated state change like Occupied or Engaged</td>
        </tr>
        <tr>
            <td>Motion event</td>
            <td>Motion event to use for timeout. Choose the motion active event to start the timeout if your motion sensor does not generate a motion inactive event following the motion active event.</td>
        </tr>
        <tr>
            <td>Presence sensor(s)</td>
            <td>Presence sensor to associate with the room. Helps control certain room actions based on presence state</td>
        </tr>
        <tr>
            <td>Lux sensor</td>
            <td>Room lux sensor to use with rules for lights and switches. For some rooms specifying an outdoor sensor might work better than a room lux sensor.</td>
        </tr>
        <tr>
            <td>Humidity sensor</td>
            <td>Room humidity sensor to use with rules for lights and switches</td>
        </tr>
        <tr>
            <td>Music player</td>
            <td>Room music player to use with rules for lights and switches</td>
        </tr>
        <tr>
            <td>Power meter</td>
            <td>Room power sensor to use with rules for lights and switches</td>
        </tr>
        <tr>
            <td>Window shade</td>
            <td>Room window shade to use in rules</td>
        </tr>
    </table>
</ul>

<p>&nbsp;</p>
<b>The next 6 settings group are for how the room is set to each of those 6 occupancy states and settings specific to that occupancy state.</b>
<ul>
    <p>&nbsp;</p>
    <img src="https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsOccupied.png" width="125" style="float:left; width:80px; height:50px; padding-right: 30px;">
    <h5>Occupied Settings</h5>
    <p>Settings that specify how this occupancy state is set. Normally it is based on motion but there are also other ways of detecting Occupied state like a specific switch turning on. Available settings:</p>
    <table class="wikitable" style="width:900px">
        <tr>
            <td style="width:15%">Button</td>
            <td style="width:85%">Set room occupancy to Occupied state when button is pushed</td>
        </tr>
        <tr>
            <td>Button number</td>
            <td>Button number of button selected above</td>
        </tr>
        <tr>
            <td>Only sets Occupied</td>
            <td>Option to turn off toggling between Occupied and Vacant state for this button. When set to true the button will only set Occupied state and no longer toggle. This helps if you are pressing a button with no visual feedback to set the room to Occupied but accidentally press it twice and don't want the room to toggle to Vacant state when it is already in Occupied state.</td>
        </tr>
        <tr>
            <td>Switch</td>
            <td>Switch which when turned on will set room occupancy state to Occupied</td>
        </tr>
        <tr>
            <td>Timeout</td>
            <td>Value in seconds for room state timeout after last motion event</td>
        </tr>
    </table>
    <p>&nbsp;</p>
    <img src="https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsEngaged.png" width="125" style="float:left; width:80px; height:50px; padding-right: 30px;">
    <h5>Engaged Settings</h5>
    <p>Settings that specify how this occupancy state is set. Normally it is based on motion but there are also other ways of detecting Engaged state like a button being pressed.</p>
    <table class="wikitable" style="width:900px">
        <tr>
            <td style="width:15%">When room is busy</td>
            <td style="width:85%">Set room to Engaged state if Occupied state is triggered frequently in a short period of time and the lights should stay on for longer than in Occupied state.
                    <br /><br />
                <p>Counts the number of time the occupancy state changes between Engaged <> Occupied <> Checking <> Vacant within ((Occupied no motion timer + Checking dim timer) * 10).</p>
                <ul>
                    <ul>
                        <li>Count = 5:&emsp;&emsp;Light traffic.</li>
                        <li>Count = 7:&emsp;&emsp;Medium traffic.</li>
                        <li>Count = 9:&emsp;&emsp;Heavy traffic.</li>
                    </ul>
                </ul>
                <br />
                <p>This is to automate Engaged state for when the room is flipping between Occupied and Vacant frequently in a short period of time.</p>
            </td>
        </tr>
        <tr>
            <td>Button</td>
            <td>Set room occupancy to Engaged state when button is pushed</td>
        </tr>
        <tr>
            <td>Button number</td>
            <td>Button number of button selected above</td>
        </tr>
        <tr>
            <td>Only sets Engaged</td>
            <td>Option to turn off toggling between Engaged and Vacant state for this button. When set to true the button will only set Engaged state and no longer toggle. This helps if you are pressing a button with no visual feedback to set the room to Engaged but accidentally press it twice and don't want the room to toggle to Vacant state when it is already in Engaged state.</td>
        </tr>
        <tr>
            <td>Presence sensor actions</td>
            <td>Choose if room occupancy state should switch with presence sensor:
                <ul>
                    <ul>
                        <li>Arrives:&emsp;&emsp;&ensp;Change room occupancy to Engaged.</li>
                        <li>Departs:&emsp;&ensp;&ensp;Change room occupancy to Vacant.</li>
                        <li>Both:&emsp;&emsp;&emsp;&ensp;Both of the actions.</li>
                        <li>Neither:&emsp;&emsp;None of the actions.</li>
                    </ul>
                </ul>
            </td>
        </tr>
        <tr>
            <td>Keep room engaged with presence</td>
            <td>Keeps room occupancy set to Engaged when presence sensor is present</td>
        </tr>
        <tr>
            <td>When music playing</td>
            <td>Keeps room occupancy set to Engaged when music is playing</td>
        </tr>
        <tr>
            <td>Switch</td>
            <td>Switch which when turned on will set room occupancy to Engaged and when turned off will set room occupancy to Vacant.</td>
        </tr>
        <tr>
            <td>Power value</td>
            <td>Power value in watts which when reached will set room occupancy to Engaged</td>
        </tr>
        <tr>
            <td>Trigger from vacant</td>
            <td>When false room will need to be in a state other than Vacant for the Asleep state to be triggered</td>
        </tr>
        <tr>
            <td>Power stays below</td>
            <td>Power value has to stay below power value above for this many seconds before room state timeout countdown will start. This is keep room state from changing frequently with power value fluctuating.</td>
        </tr>
        <tr>
            <td>Contact sensor</td>
            <td>Contact sensor(s) when closed will set room occupancy to Engaged with motion</td>
        </tr>
        <tr>
            <td>Outside door</td>
            <td>For use with outside doors like garage doors where the Engaged state is triggered with motion if the door is open</td>
        </tr>
        <tr>
            <td>Does not trigger Engaged</td>
            <td>For use with areas like landing or hallway leading to different rooms with contact sensor on those doors. You want any of the room doors opening to turn on the lights even before the landing/hallway motion sensor detects motion but you do not want these doors to set the landing/hallway to Engaged. Turn on this setting in that case.</td>
        </tr>
        <tr>
            <td>Timeout</td>
            <td>Value in seconds for room occupancy timeout from Engaged to Vacant</td>
        </tr>
        <tr>
            <td>Reset Engaged/Asleep</td>
            <td>Reset Engaged or Asleep state when another room changes to Engaged or Asleep</td>
        </tr>
        <tr>
            <td>Reset Engaged directly</td>
            <td>Reset room occupancy to Vacant directly without transitioning through Checking state</td>
        </tr>
    </table>
    <p>&nbsp;</p>
    <img src="https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsChecking.png" width="125" style="float:left; width:80px; height:50px; padding-right: 30px;">
    <h5>Checking Settings</h5>
    <p>Settings for timeout and light levels while in checking state.</p>
    <table class="wikitable" style="width:900px">
        <tr>
            <td style="width:15%">Dim timer</td>
            <td style="width:85%">Dim lights for how many seconds for visible notification of checking state and that lights and switches will be turned off.</td>
        </tr>
        <tr>
            <td>Dim level by</td>
            <td>If any lights are on dim them by this level.</td>
        </tr>
        <tr>
            <td>Dim level to</td>
            <td>If no lights are on turn them on at this level.</td>
        </tr>
        <tr>
            <td>Lux value?</td>
            <td>If no lights are on turn them on only when lux is at or below this lux value.</td>
        </tr>
        <tr>
            <td>Do not restore</td>
            <td>When transitioning from Checking state to another state do not restore the light levels to their previous value if that state is Vacant.</td>
        </tr>
    </table>
    <br />
    <img src="https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsVacant.png" width="125" style="float:left; width:80px; height:50px; padding-right: 30px;">
    <h5>Vacant Settings</h5>
    <p>Settings that specify how this occupancy state is set. Normally it is based on motion but there are also other ways of detecting Occupied state like a specific switch turning off.</p>
    <table class="wikitable" style="width:900px">
        <tr>
            <td style="width:15%">Button</td>
            <td style="width:85%">Set room occupancy to Vacant state when button is pushed</td>
        </tr>
        <tr>
            <td>Button number</td>
            <td>Button number of button selected above</td>
        </tr>
        <tr>
            <td>Switch</td>
            <td>Switch which when turned off will set room occupancy to Vacant</td>
        </tr>
        <tr>
            <td>Stop music</td>
            <td>Pause music player when room occupancy changes to Vacant</td>
        </tr>
    </table>
    <p>&nbsp;</p>
    <img src="https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsAsleep.png" width="125" style="float:left; width:80px; height:50px; padding-right: 30px;">
    <h5>Asleep Settings</h5>
    <p>Settings that specify how this occupancy state is set. Asleep is tricky because there is no true commonly used physical asleep sensors. So, these settings allow other ways of setting Asleep occupancy state and specifying night light settings which are a little different from how lights work through the rules.</p>
    <table class="wikitable" style="width:900px">
        <tr>
            <td style="width:15%">Sleep sensor</td>
            <td style="width:85%">Sleep sensor to set room occupancy to Asleep. (not a lot of device option here that supports sleep state. some users use the webCoRE presence sensor which I had originally modified to support sleep state, subsequently Adrian added the sleep state code to main.)</td>
        </tr>
        <tr>
            <td>Button</td>
            <td style="width:75%">Set room occupancy to Asleep state when button is pushed</td>
        </tr>
        <tr>
            <td>Button number</td>
            <td>Button number of button selected above</td>
        </tr>
        <tr>
            <td>Only sets Asleep</td>
            <td>Option to turn off toggling between Asleep and Vacant state for this button. When set to true the button will only set Asleep state and no longer toggle. This helps if you are pressing a button by the bedside to set the room to Asleep but accidentally press it twice and don't want the room to toggle to Vacant state when it is already in Asleep state.</td>
        </tr>
        <tr>
            <td>Switch</td>
            <td>Switch which when turned off will set room occupancy to Asleep</td>
        </tr>
        <tr>
            <td>Power value</td>
            <td>Power value in watts which when reached will set room occupancy to Asleep</td>
        </tr>
        <tr>
            <td>Trigger from vacant</td>
            <td>When false room will need to be in a state other than Vacant for the Asleep state to be triggered</td>
        </tr>
        <tr>
            <td>Power stays below</td>
            <td>Power value has to stay below power value above for this many seconds before room state timeout countdown will start. This is to keep room state from changing frequently with power value fluctuating.</td>
        </tr>
        <tr>
            <td>Timeout</td>
            <td>Option to turn off Asleep state after certain number of hours in case you forget to turn it off in the morning. Specially useful if you do not have a contact sensor on the bedroom doors.</td>
        </tr>
        <tr>
            <td>Reset with Contact</td>
            <td>Option to turn off Asleep state after certain number of hours in case you forget to turn it off in the morning</td>
        </tr>
    </table>
    <ul>
        <li style="list-style-type:square">
            <h5>Night lights:</h5>
            <p>Settings for night lights with motion while in Asleep state.</p>
        </li>
        <table class="wikitable" style="width:875px">
            <tr>
                <td style="width:15%">Turn on</td>
                <td style="width:85%">Switches to turn on with motion while in Asleep state</td>
            </tr>
            <tr>
                <td>Level</td>
                <td>Set level when turning on switches above</td>
            </tr>
            <tr>
                <td>Color temperature</td>
                <td>Set color temperature when turning on switches above</td>
            </tr>
            <tr>
                <td>Which motion sensors?</td>
                <td>Pick which room motion sensors will trigger night lights when Asleep. If you multiple motion sensors in the room and only want some of them trigger night lights but not others, you can pick the ones here that should trigger night lights.</td>
            </tr>
            <tr>
                <td>Timeout</td>
                <td>Value in seconds for night light timeout</td>
            </tr>
            <tr>
                <td>Button</td>
                <td>Button to control night lights only</td>
            </tr>
            <tr>
                <td>Button number</td>
                <td>Button number of button selected above</td>
            </tr>
            <tr>
                <td>Button action</td>
                <td>Either toggle or turn night lights on or off</td>
            </tr>
        </table>
    </ul>
    <p>&nbsp;</p>
    <img src="https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsLocked.png" width="125" style="float:left; width:80px; height:50px; padding-right: 30px;">
    <h5>Locked Settings</h5>
    <p>Settings that specify how this occupancy state is set. This state disables all automation for the room.</p>
    <table class="wikitable" style="width:900px">
        <tr>
            <td style="width:15%">Switch</td>
            <td style="width:85%">Switch to set room occupancy to Locked</td>
        </tr>
        <tr>
            <td>On or off</td>
            <td>Set Locked state when switch turns on or turns off</td>
        </tr>
        <tr>
            <td>Power value</td>
            <td>Power value in watts which when reached will set room occupancy to Locked</td>
        </tr>
        <tr>
            <td>Trigger from vacant</td>
            <td>When false room will need to be in a state other than Vacant for the Locked state to be triggered</td>
        </tr>
        <tr>
            <td>Power stays below</td>
            <td>Power value has to stay below power value above for this many seconds before room state timeout countdown will start. This is to keep room state from changing frequently with power value fluctuating.</td>
        </tr>
        <tr>
            <td>Contact</td>
            <td>Contact to set room occupancy to Locked</td>
        </tr>
        <tr>
            <td>Open or closed</td>
            <td>Set Locked state when contact is open or closed</td>
        </tr>
        <tr>
            <td>Switches off</td>
            <td>When true turn off switches when room occupancy changes to Locked</td>
        </tr>
        <tr>
            <td>Timeout</td>
            <td>Option to turn off Locked state after certain number of hours</td>
        </tr>
    </table>
</ul>

<p>&nbsp;</p>
<b>These group of settings allow for light routine settings used in the rules.</b>
<ul>
    <img src="https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsLightLevel.png" width="125" style="float:left; width:80px; height:50px; padding-right: 30px;">
    <h5>Auto Level 'AL' Settings</h5>
    <p>Settings to specify auto level and color temperature settings for the room which allows using 'AL' as a light level rule to automatically calculate and use these values based on time of day, wake and sleep time specified. Also allows specifying hours before and after wake and sleep times the light level and color temperature should be dimmed for optimal light levels.</p>
    <table class="wikitable" style="width:900px">
        <tr>
            <td style="width:15%">Minimum level</td>
            <td style="width:85%">Minimum light level</td>
        </tr>
        <tr>
            <td>Maximum level</td>
            <td>Maximum light level</td>
        </tr>
        <tr>
            <td>Wakeup time</td>
            <td>Wakeup time</td>
        </tr>
        <tr>
            <td>Sleep time</td>
            <td>Sleep time</td>
        </tr>
        <td>
        </td>
        <tr>
            <td>Fade up to wake time</td>
            <td>Fade light level up to wake time</td>
        </tr>
        <tr>
            <td>Hours before</td>
            <td>How many hours before wakeup time should light level start fading up</td>
        </tr>
        <tr>
            <td>Hours after</td>
            <td>How many hours after wakeup time should light level stop fading up</td>
        </tr>
        <tr>
            <td>Fade down to sleep time</td>
            <td>Fade light level down to sleep time</td>
        </tr>
        <tr>
            <td>Hours before</td>
            <td>How many hours before sleep time should light level start fading down</td>
        </tr>
        <tr>
            <td>Hours after</td>
            <td>How many hours after sleep time should light level stop fading down</td>
        </tr>
        <td>
        </td>
        <tr>
            <td>Auto color temperature</td>
            <td>Set color temperature along with level</td>
        </tr>
        <tr>
            <td>Minimum kelvin</td>
            <td>Minimum color temperature for light</td>
        </tr>
        <tr>
            <td>Maximum kelvin</td>
            <td>Maximum color temperature for light</td>
        </tr>
        <td>
        </td>
        <tr>
            <td>Fade up to wake time</td>
            <td>Fade color temperature up to wake time</td>
        </tr>
        <tr>
            <td>Hours before</td>
            <td>How many hours before wakeup time should color temperature start fading up</td>
        </tr>
        <tr>
            <td>Hours after</td>
            <td>How many hours after wakeup time should color temperature stop fading up</td>
        </tr>
        <tr>
            <td>Fade down to sleep time</td>
            <td>Fade color temperature down to sleep time</td>
        </tr>
        <tr>
            <td>Hours before</td>
            <td>How many hours before sleep time should color temperature start fading down</td>
        </tr>
        <tr>
            <td>Hours after</td>
            <td>How many hours after sleep time should color temperature stop fading down</td>
        </tr>
    </table>
    <p>&nbsp;</p>
    <img src="https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsHolidayLights2.png" width="125" style="float:left; width:80px; height:50px; padding-right: 30px;">
    <h5>Holiday Lights 'HL' Settings</h5>
    <p>Settings to specify holiday light patterns for use in rules during various holiday seasons. Allows for rotating colors through or slow twinkling any set of lights specified in the rules.</p>
    <ul>
        <table class="wikitable" style="width:900px">
            <tr>
                <td style="width:100%">List of previously defined holiday light patterns</td>
            </tr>
            <tr>
                <td>Option to create new holiday light pattern</td>
            </tr>
        </table>
        <ul>
            <li style="list-style-type:square">
                <h5>Holiday Light Pattern:</h5>
                <p>Create holiday light patterns to use on different holidays or other special occasion.</p>
            </li>
            <table class="wikitable" style="width:875px">
                <tr>
                    <td style="width:15%">Color string name</td>
                    <td style="width:85%">Name for this holiday light pattern for use in specifying with rules</td>
                </tr>
                <tr>
                    <td>Colors</td>
                    <td>Comma delimited list of colors</td>
                </tr>
                <tr>
                    <td>Light routine</td>
                    <td>Either Rotate or Twinkle where twinkle is a slow twinkle so the hub does not get saturated with frequent on and off commands</td>
                </tr>
                <tr>
                    <td>How many seconds</td>
                    <td>Either rotate colors or turn on and off after every how many seconds</td>
                </tr>
                <tr>
                    <td>Light level</td>
                    <td>Button to control night lights only</td>
                </tr>
            </table>
        </ul>
    </ul>
</ul>

<p>&nbsp;</p>
<b>Temperature settings is their own group.</b>

<ul>
    <img src="https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsTemperature.png" width="125" style="float:left; width:80px; height:50px; padding-right: 30px;">
    <h5>Temperature Settings</h5>
    <p>Manage temperature settings for the room in conjunction with thermostat or switch controlled room AC and/or heater. After adding temperature settings remember create temperature rules in maintain rules so the app can automate temperature control based on these rules.</p>
    <ul>
        <table class="wikitable" style="width:900px">
            <tr>
                <td style="width:15%">Temperature sensors</td>
                <td style="width:85%">Room temperature sensors</td>
            </tr>
            <tr>
                <td>Maintain temperature</td>
                <td>Maintain temperature:
                    <ul>
                        <ul>
                            <li>Cool:&emsp;&emsp;&ensp;&emsp;Cool room only</li>
                            <li>Heat:&emsp;&ensp;&emsp;&emsp;Heat room only</li>
                            <li>Both:&emsp;&emsp;&emsp;&ensp;Both cool and heat</li>
                            <li>Neither:&emsp;&emsp;Neither</li>
                        </ul>
                    </ul>
                </td>
            </tr>
            <tr>
                <td>Use thermostat</td>
                <td>Use thermostat to heat and cool or use in room AC and heater controlled by switches</td>
            </tr>
        </table>
        <ul>
            <li style="list-style-type:square">
                <h5>Use thermostat: ON</h5>
            </li>
            <table class="wikitable" style="width:875px">
                <tr>
                    <td style="width:15%">Thermostat</td>
                    <td style="width:85%">Thermostat</td>
                </tr>
                <tr>
                    <td>Delta temperature</td>
                    <td>Room temperature sensor <b>-</b> thermostat temperature reading</td>
                </tr>
            </table>
        </ul>
        <ul>
            <li style="list-style-type:square">
                <h5>Use thermostat: OFF</h5>
            </li>
            <table class="wikitable" style="width:875px">
                <tr>
                    <td style="width:15%">AC switch</td>
                    <td style="width:85%">Switch for in room AC</td>
                </tr>
                <tr>
                    <td>Heater switch</td>
                    <td>Switch for in room heather </td>
                </tr>
            </table>
        </ul>
        <table class="wikitable" style="width:900px">
            <tr>
                <td style="width:15%">Check presence</td>
                <td style="width:85%">Specify presence sensors to check for presence before maintaining temperature</td>
            </tr>
            <tr>
                <td>Check contact closed</td>
                <td>Specify window contact sensors to check for closed before maintaining temperature</td>
            </tr>
            <tr>
                <td>Switch override</td>
                <td>Allow thermostat or AC / heater switch to be manually overridden for how many minutes</td>
            </tr>
            <tr>
                <td>Outdoor temperature</td>
                <td>Outdoor temperature sensor that is not currently used but have plans to use</td>
            </tr>
            <tr>
                <td>Fan switch</td>
                <td>Fan switch to use in rules</td>
            </tr>
            <tr>
                <td>Room vents</td>
                <td>Room vents to automate with thermostat and room temperature.</td>
            </tr>
        </table>
    </ul>
</ul>

<p>&nbsp;</p>
<b>Here are the rest of the settings starting with the heart of the app - Maintain Rules, which allows you to maintain automation rules for the room and turn them in to smart rooms.</b>
<p>&nbsp;</p>

<ul>
    <img src="https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsRules.png" width="125" style="float:left; width:80px; height:50px; padding-right: 30px;">
    <h5>Maintain Rules</h5>
    <p>Here's where to create the rules that check room occupancy state, various sensor values and other variables to decide which lights and switches should be turned on or off. It also allows executing a piston or routine or even starting and stopping a music player based on the rules.</p>
    <ul>
        <table class="wikitable" style="width:900px">
            <tr>
                <td style="width:100%">List of defined rules</td>
            </tr>
            <tr>
                <td>Option to create new rule</td>
            </tr>
        </table>
        <ul>
            <li style="list-style-type:square">
                <h5>Edit Rule:</h5>
                <p>Add a new rule or edit existing rule settings.</p>
            </li>
            <table class="wikitable" style="width:875px">
                <tr>
                    <td style="width:15%">Rule number</td>
                    <td style="width:85%">For tracking rule settings, not editable</td>
                </tr>
                <tr>
                    <td>Rule name</td>
                    <td>User defined descriptive rule name</td>
                </tr>
                <tr>
                    <td>Rule disabled</td>
                    <td>Quick way to to turn off the rule instead of editing and removing each setting for that rule</td>
                </tr>
                <tr>
                    <td>Modes</td>
                    <td>Matches to location mode when evaluating rule. Available choices are based on modes defined for that hub location</td>
                </tr>
                <tr>
                    <td>Occupancy state</td>
                    <td>Matches to current occupancy state when evaluating rule. Available choices are:
                        <ul>
                            <ul>
                                <li>Asleep</li>
                                <li>Engaged</li>
                                <li>Occupied</li>
                                <li>Vacant:&emsp;&emsp;&emsp;Normally you don't need to create rules for the Vacant state. But if you want one of the lights to stay on during certain times in the evening even when the room is Vacant create a rule for the Vacant state with the right mode, times and switch(es)</li>
                            </ul>
                        </ul>
                    </td>
                </tr>
                <tr>
                    <td>Days of week</td>
                    <td>Matches to current day when evaluating rule</td>
                </tr>
                <tr>
                    <td>Rule type</td>
                    <td>Either Execution or Temperature depending on the rule type you are creating:
                        <ul>
                            <ul>
                                <li>Execution:&emsp;&emsp;&emsp;Allows turning on and off switches, executing routines and pistons, starting or stopping music player and setting window shade position</li>
                                <li>Temperature:&emsp;&emsp;&emsp;Allows setting room temperature to maintain and room fan on and off settings</li>
                            </ul>
                        </ul>
                    </td>
                </tr>
            </table>
            <ul>
                <li style="list-style-type:square">
                    <h5>Rule type: Execution</h5>
                </li>
                <table class="wikitable" style="width:875px">
                    <tr>
                        <td style="width:15%">Lux value</td>
                        <td style="width:85%">Matches to current lux value from sensor <= this value when evaluating rule.</td>
                    </tr>
                    <tr>
                        <td>Humidity range</td>
                        <td>Matches to current humidity reading from sensor</td>
                    </tr>
                    <tr>
                        <td>Date filter</td>
                        <td>Matches to current date when evaluating rule entered in <i>yyyy/mm/dd</i> format. Supports the following special values:
                            <ul>
                                <ul>
                                    <li><b>yyyy</b>:&emsp;&emsp;&emsp;Matches to current year when evaluating rule</li>
                                    <li><b>YYYY</b>:&emsp;&emsp;&ensp;Matches to next year when evaluating rule</li>
                                </ul>
                            </ul>
                        </td>
                    </tr>
                    <tr>
                        <td>Time trigger</td>
                        <td>Allows specifying <i>time range from - to</i> and matches to current time when evaluating rule. Supports the following values:
                            <ul>
                                <ul>
                                    <li>Sunrise:&emsp;&emsp;&emsp;Matches to local sunrise time with offset if specified</li>
                                    <li>Sunset:&emsp;&emsp;&emsp;&ensp;Matches to local sunset time with offset if specified</li>
                                    <li>Time:&emsp;&emsp;&emsp;&emsp;&ensp;Matches to specific time/li>
                                </ul>
                            </ul>
                        </td>
                    </tr>
                    <tr>
                        <td>Turn on which lights / switches</td>
                        <td>List of devices to turn on</td>
                    </tr>
                    <tr>
                        <td>Set level</td>
                        <td>Level value if the light or switch being turned on supports level. Also allows picking either Auto Level or one of the Holiday Light routines defined earlier</td>
                    </tr>
                    <tr>
                        <td>Set color</td>
                        <td>Color to set when turning on light if the light supports color</td>
                    </tr>
                    <tr>
                        <td>Set color temperature</td>
                        <td>Color temperature to set when turning on light if light supports color temperature</td>
                    </tr>
                    <tr>
                        <td>Turn off which lights / switches</td>
                        <td>List of devices to turn off</td>
                    </tr>
                </table>
                <ul>
                    <li style="list-style-type:square">
                        <h5>Routines/Pistons and more:</h5>
                        <p>Execute other actions.</p>
                    </li>
                    <table class="wikitable" style="width:875px">
                        <tr>
                            <td style="width:15%">Routines</td>
                            <td style="width:85%">Select routines to execute when rule evaluates as true</td>
                        </tr>
                        <tr>
                            <td>Piston</td>
                            <td>Select piston to execute when rule evaluates as true</td>
                        </tr>
                        <tr>
                            <td>Music player</td>
                            <td>Select if music player should be started or stopped when rule evaluates as true</td>
                        </tr>
                        <tr>
                            <td>Window shade</td>
                            <td>Set window shade to one of the preselected positions when rules evaluates as true</td>
                        </tr>
                    </table>
                </ul>
                <ul>
                    <li style="list-style-type:square">
                        <h5>Timer overrides:</h5>
                        <p>Override individual timer settings when this rule evaluates as true. For example normal timeout settings for Occupied state may be 180 seconds. But during Night mode the Occupied state timeout could be overridden here to be 30 seconds.</p>
                    </li>
                    <table class="wikitable" style="width:875px">
                        <tr>
                            <td style="width:15%">Occupied timeout</td>
                            <td style="width:85%">Timeout in seconds when rule evaluates as true</td>
                        </tr>
                        <tr>
                            <td>Engaged timeout</td>
                            <td>Timeout in seconds when rule evaluates as true</td>
                        </tr>
                        <tr>
                            <td>Checking timeout</td>
                            <td>Timeout in seconds when rule evaluates as true</td>
                        </tr>
                        <tr>
                            <td>Night light timeout</td>
                            <td>Timeout in seconds when rule evaluates as true</td>
                        </tr>
                    </table>
                </ul>
            </ul>
            <ul>
                <li style="list-style-type:square">
                    <h5>Rule type: Temperature</h5>
                </li>
                <table class="wikitable" style="width:875px">
                    <tr>
                        <td style="width:15%">Time trigger</td>
                        <td style="width:85%">Allows specifying <i>time range from - to</i> and matches to current time when evaluating rule. Supports the following values:
                            <ul>
                                <ul>
                                    <li>Sunrise:&emsp;&emsp;&emsp;Matches to local sunrise time with offset if specified</li>
                                    <li>Sunset:&emsp;&emsp;&emsp;&ensp;Matches to local sunset time with offset if specified</li>
                                    <li>Time:&emsp;&emsp;&emsp;&emsp;&ensp;Matches to specific time/li>
                                </ul>
                            </ul>
                        </td>
                    </tr>
                </table>
                <p>Manage room temperature settings:</p>
                <table class="wikitable" style="width:875px">
                    <tr>
                        <td style="width:15%">Cool temperature</td>
                        <td style="width:85%">Temperature to cool room to</td>
                    </tr>
                    <tr>
                        <td>Heat temperature</td>
                        <td>Temperature to heat room to</td>
                    </tr>
                    <tr>
                        <td>Temperature range</td>
                        <td>Select the temperature range within which the room temperature is maintained based on cool and heat temperature above.</td>
                    </tr>
                </table>
                <p>Fan control:</p>
                <table class="wikitable" style="width:875px">
                    <tr>
                        <td style="width:15%">Fan on temperature</td>
                        <td style="width:85%">Temperature at which to turn on fan.</td>
                    </tr>
                    <tr>
                        <td>Fan speed increments</td>
                        <td>Temperature increments with which to increment fan speed.</td>
                    </tr>
                </table>
                <p>Room vents control:</p>
                <table class="wikitable" style="width:875px">
                    <tr>
                        <td style="width:15%">Rooms vents</td>
                        <td style="width:85%">Room vents if specified are automatically controlled with thermostat and temperature.</td>
                    </tr>
                </table>
            </ul>
        </ul>
    </ul>
    <p>&nbsp;</p>
    <img src="https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsAdjacent5.png" width="125" style="float:left; width:80px; height:50px; padding-right: 30px;">
    <h5>Adjacent Room Settings</h5>
    <p>Adjacent rooms allow specifying which rooms are adjacent to that room so you can automatically turn on lights in the next room when moving through this room or force adjacent rooms to Checking state when there is motion in this room.</p>
    <ul>
        <table class="wikitable" style="width:900px">
            <tr>
                <td style="width:15%">Adjacent rooms</td>
                <td style="width:85%">Select the adjacent rooms to this room.</td>
            </tr>
            <tr>
                <td>Adjacent room motion</td>
                <td>If motion in adjacent room force motion check in this room to confirm someone is still in this room.</td>
            </tr>
            <tr>
                <td>Adjacent room lights</td>
                <td>If motion in this room move adjacent rooms to Checking state so lights come on dimmed and lights the pathway.</td>
            </tr>
        </table>
    </ul>
    <p>&nbsp;</p>
    <img src="https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsSettings.png" width="125" style="float:left; width:80px; height:50px; padding-right: 30px;">
    <h5>Mode and Other Settings</h5>
    <p>Miscellaneous settings that don't fit any where else, like in which modes should all automation be disabled or what icon to use for the room in the rooms manager and a few other settings.</p>
    <p>&nbsp;</p>
    <img src="https://cdn.rawgit.com/adey/bangali/master/resources/icons/roomsViewAll.png" width="125" style="float:left; width:80px; height:50px; padding-right: 30px;">
    <h5>View All Settings</h5>
    <p>What the name says.</p>
</ul>

<p>&nbsp;</p>
<p><i>For a github install from repo in ST use : owner: adey / name: bangali / branch: master. Install and publish the rooms occupancy DTH then install and publish the rooms manager and rooms child app smartapps.</i></p>

<p>For a manual install here are the links, in order of DTHs and smartapps you should save and publish.</p>

<p><b>rooms occupancy DTH:</b></p>
<a href="https://raw.githubusercontent.com/adey/bangali/master/devicetypes/bangali/rooms-occupancy.src/rooms-occupancy.groovy">https://raw.githubusercontent.com/adey/bangali/master/devicetypes/bangali/rooms-occupancy.src/rooms-occupancy.groovy</a>

<h5>when saving the driver it will generate an error on the first of each of these two lines:</h5>

<p>capability "Button"</p>
<p>//capability "PushableButton" // hubitat changed Button to PushableButton 2018-04-20</p>

<p>capability "Lock Only"</p>
<p>//capability "Lock" // hubitat does not support Lock Only 2018-04-07</p>

<i>Comment out the Button and Lock Only line and uncomment the PushableButton and Lock line, explanation in inline comment above.</i>

<p><b>rooms manager smartapp:</b></p>
<a href="https://raw.githubusercontent.com/adey/bangali/master/smartapps/bangali/rooms-manager.src/rooms-manager.groovy">https://raw.githubusercontent.com/adey/bangali/master/smartapps/bangali/rooms-manager.src/rooms-manager.groovy</a>

<p>&nbsp;</p>
<p><b>rooms child smartapp:</b></p>
<a href="https://raw.githubusercontent.com/adey/bangali/master/smartapps/bangali/rooms-child-app.src/rooms-child-app.groovy">https://raw.githubusercontent.com/adey/bangali/master/smartapps/bangali/rooms-child-app.src/rooms-child-app.groovy</a>

<p>&nbsp;</p>
<p>Then go to ST app -> Automation tab -> Add a Smartapp -> My apps in ST app and install rooms manager app then create your rooms within rooms manager.</p>

<h5>When creating a room first give the room a name and save the room then go back in to the room to add various settings to the room. This is because the app uses app state to manage the rules and in ST the app state is not consistent till the app has been saved once.</h5>

<p>Like the app? Like this post please. 😁</p>

<p>If you want to support development of the app here is the donation link. To be clear ... <i>no donation is required or expected to use rooms manager / occupancy.</i> But if you do donate please also know that it is much appreciated and thank you. Donate here: https://www.paypal.me/dey</p>

<p>&nbsp;</p>

<h5>Non-obvious rules:</h5>
<p>1. Outdoor lights with no motion sensor?
    <ul>
        Setup a room say `Outdoor` with the following settings specified for the rule:  
        <ul>
            <table class="wikitable" style="width:100%">
                <tr>
                    <td style="width:90%">Name of the rule.</td>
                </tr>
                <tr>
                    <td>Time from and to settings when the lights should turn on and off respectively. </td>
                </tr>
                <tr>
                    <td>Lights or other switches to turn off.</td>
                </tr>
                <tr>
                    <td>Optionally you can also specify the level, color and color temperature or use a Holiday Light rule that you have setup.</td>
                </tr>
            </table>
        </ul>
        The light will turn on at the time from time and turn off at the time to time. Off course you could create multiple such rules in this Outdoor room to turn off different lights at different times even on different days of the week.
    </ul>
</p>
<p>2. Turn off switches after X seconds with no motion sensor? (power saver)
    <ul>
        Setup a room with the following Engaged settings:  
        <ul>
            <table class="wikitable" style="width:100%">
                <tr>
                    <td style="width:90%">Set the switches you want turned off as engaged switches.</td>
                </tr>
                <tr>
                    <td>Specify the seconds after which you want the light turned off as the timeout setting.</td>
                </tr>
            </table>
        </ul>
        Then add a rule with the following settings:  
        <ul>
            <table class="wikitable" style="width:100%">
                <tr>
                    <td style="width:90%">Name of the rule.</td>
                </tr>
                <tr>
                    <td>From the state drop down pick state as Engaged.</td>
                </tr>
                <tr>
                    <td>All switches in Engaged settings as switches to turn on.</td>
                </tr>
            </table>
        </ul>
        Any of those lights when turned on will be turned off after that specified number of seconds.  
    </ul>
</p>

<p>&nbsp;</p>

<b>Here are a couple of screenshots of the device tiles for a Rooms Occupancy device from SmartThings. Note this is not applicable for Hubitat since Hubitat does not have device tiles, at least for now.</b>
<div id="screenshots-table">
    <table>
        <tr>
            <td style="padding:25px">
                <img height="500" src="https://raw.githubusercontent.com/adey/bangali/master/resources/screens/Rooms%20Occupancy%20Device%20Tiles%20-%20Screen%201.png">
            </td>
            <td style="padding:25px">
                <img height="500" src="https://raw.githubusercontent.com/adey/bangali/master/resources/screens/Rooms%20Occupancy%20Device%20Tiles%20-%20Screen%202.png">
            </td>
        </tr>
    </table>
</div>
