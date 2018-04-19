# bangali's code stuff for SmartThings and Hubitat.

<h2>Rooms Occupancy and Rooms Manager</h2>

<p>While ST has a concept of rooms it is essentially a grouping mechanism which does not enable automation. In contrast rooms occupancy considers the room as a meta device and automates common tasks associated with a “room” physical or virtual. <strong>What makes it really useful is not just the room's occupancy state but the ability to manage automation for rooms in a set of rules for the room based on the occupancy state of the room and data from various sensors.</strong> When creating a room device through the smartapp you are able to create these rules for the rooms making your rooms really smart.</p>

<p>What these rules enable is many common tasks around rooms which most users go through automating at some point. Usually through setting up a few rules or creating a few pistons. I have been there and done that myself. While those work to a degree, it does not enable the kind of comprehensive automation that should be possible for devices in a room based on sensor and device inputs. This smartapp makes that possible.</p>

<p>If there is one principle that these apps are built on, it is - that your home automation should work in the background in a repeatable and predictable manner without requiring periodic human intervention. In short - your automation should work for you and not the other way around. <i>But even more importantly perhaps, this app gets you the kind of WAF for your home automation that you have always dreamed about.</i> 🙂</p>

<p>Additionally, these room devices also have attributes, capabilities and commands which are useable in webCoRE or other smartapps like Smart Lighting in ST or Rule Machine in Hubitat. There is a range of other automations that webCoRE makes possible that could not otherwise be done without writing a custom smartapp for it. I use webCoRE for that and am I big fan of Adrian. So checkout webCoRE as well if you don't already use it.</p>

<h4>How does this app work?</h4>

<p><i>This app works by setting rooms occupancy to various states based on a set of sensors as specified by the user. It takes this state and attribute values from various sensors and evaluates a set of rules that you create to match the conditions. When all of the conditions for the rule matches, it executes actions specified on these matching rules.</i></p>

<p>As an example, you can specify motion sensors in Occupied settings to set a room state to Occupied when there is motion from any of those motion sensors. Then create a rule to turn on some lights. In this rule you could also set that these lights should only be turned on if a lux sensor is at a certain lux value or lower. Or you could specify that only turn on the lights during certain times. Or you could specify turn on the lights at a certain level during certain times and at another level during other times.</p>

<p>As a part of the Occupied settings you can also specify timeout values so the room does not indefinitely stay set to Occupied state and the lights turn off after a while when there is no motion. How quickly that happens is controlled by you through the timeout value you specify in the settings. By specifying settings in the Checking settings you are also able to dim the lights before the lights turn off completely so there is a visual cue to the room occupant that the lights will turn off because they have not moved in a while.</p>

<p>Off course you may be in the room while and not be moving for a while like reading a book or watching TV. That's when you use the Engaged settings to set the room to Engaged state. See below for additional details.</p>

<h4>Here are the common room occupancy states:</h4>

<ul>
    <li>
        <h5>Occupied:</h5>
    </li>
    <p>Occupied is you go to a room are in there for a few minutes then leave the room. Lights come on when you enter the room and turn off after a couple of minutes of your leaving the room. Think of Occupied as a transient state and Engaged below as a somewhat persistent state.</p>
    <li>
        <h5>Engaged:</h5>
    </li>
    <p>Engaged is when you stay in a room for an extended period of time and may be motionless for some or all of the time. since we cant depend on the motion event for engaged state there are different options to set the room to engaged for extended occupancy. these are all under engaged settings and there is more coming. but these help make sure the switches you set to on stay on even if there is no motion in the room. When in Engaged state you have a different and longer timeout state than the Occupied state. So there is still a motion requirement but a much higher time threshold than the Occupied state.</p>
    <li>
        <h5>Asleep:</h5>
    </li>
    <p>Asleep state is meant for use while the room should be 'asleep' as in not respond to most typical automation like motion automation. But it does allow for other automation like using a night light and using a button to turn on or off the night lights. You are still able to create rules for the Asleep state but it additionally support a little bit for Asleep state specific automation in the Asleep settings.</p>
    <li>
        <h5>Vacant:</h5>
    </li>
    <p>Vacant state is for when the room is vacant and you want everything to get turned off. It is possible to setup rules for Vacant settings as well but not required.</p>
    <li>
        <h5>Checking:</h5>
    </li>
    <p>Occupied state is used for transition between states and not user controlled. For example, when moving from Occupied to Vacant occupancy state the room will transition to Checking state. While the app does not allow creating rules for checking state there is some settings available to control dimming of the lights when in Checking state.</p>
    <li>
        <h5>Locked:</h5>
    </li>
    <p>Locked state disables all automations for the room and allows you to control lights and other devices in the room either manually or some other way.</p>
</ul>

<p>The states 'locked', 'reserved', 'kaput' and 'donotdisturb' are effectively all similar in that they all disable automation. That being the case there is some sensors allowed to set / unset rooms to / from Locked state but no other automation beyond that for these occupancy states. Here is a quick description of the various top level settings and how the app works. <i>At the heart of the app is the concept of room states and rules to automate devices based on these room's states and other sensor inputs.</i> (In the following description when I talk about sensors it refers to devices that have attributes which are used to drive decisions in the room's rules.)</p>

<p><i>Note many of these settings are optional but when specified will require other settings to be specified. Like specifying a motion sensor is optional. But if you do specify a motion sensor the motion event to trigger timeout countdown becomes required.</i></p>

<h4>Top level settings:</h4>

<ul>
    <li style="font-weight: bold; margin-top: 25px; margin-bottom: 15px;">Room Devices</li>
    <p>Room sensors used in checking rule conditions based occupancy state and data from these sensors.</p>
    <table class="wikitable" style="width:900px">
        <tr>
            <td style="width:15%">Motion sensor(s)</td>
            <td style="width:85%">Room motion sensor(s) for motion activated state change like Occupied or Engaged.</td>
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
            <td>Room humidity sensor to use with rules for lights and switches.</td>
        </tr>
        <tr>
            <td>Music player</td>
            <td>Room music player to use with rules for lights and switches.</td>
        </tr>
        <tr>
            <td>Power meter</td>
            <td>Room power sensor to use with rules for lights and switches.</td>
        </tr>
        <tr>
            <td>Window shade</td>
            <td>Room window shade to use in rules.</td>
        </tr>
    </table>
</ul>

<p style="font-weight: bold; margin-top: 25px;">The next 6 settings group are for how the room is set to each of those 6 occupancy states and settings specific to that occupancy state.</p>
<ul>
    <li style="font-weight: bold; margin-top: 25px; margin-bottom: 15px;">Occupied Settings</li>
    <p>Settings that specify how this occupancy state is set. Normally it is based on motion but there are also other ways of detecting Occupied state like a specific switch turning on. Available settings:</p>
    <table class="wikitable" style="width:900px">
        <tr>
            <td style="width:15%">Switch</td>
            <td style="width:85%">Switch which when turned on will set room occupancy state to Occupied.</td>
        </tr>
        <tr>
            <td>Timeout</td>
            <td>Value in seconds for room state timeout after last motion event.</td>
        </tr>
    </table>
    <li style="font-weight: bold; margin-top: 25px; margin-bottom: 15px;">Engaged Settings</li>
    <p>Settings that specify how this occupancy state is set. Normally it is based on motion but there are also other ways of detecting Engaged state like a button being pressed.</p>
    <table class="wikitable" style="width:900px">
        <tr>
            <td style="width:15%">When room is busy</td>
            <td style="width:85%">Set room to Engaged state if Occupied state is triggered frequently in a short period of time or the room is busy and the lights should stay on for longer than in Occupied state.</td>
        </tr>
        <tr>
            <td>Button</td>
            <td>Set room occupancy to Engaged state when button is pushed.</td>
        </tr>
        <tr>
            <td>Button number</td>
            <td>Button number of button selected above.</td>
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
            <td>Keeps room occupancy set to Engaged when presence sensor is present.</td>
        </tr>
        <tr>
            <td>When music playing</td>
            <td>Keeps room occupancy set to Engaged when music is playing.</td>
        </tr>
        <tr>
            <td>Switch</td>
            <td>Switch which when turned on will set room occupancy to Engaged.</td>
        </tr>
        <tr>
            <td>Power value</td>
            <td>Power value in watts which when reached will set room occupancy to Engaged.</td>
        </tr>
        <tr>
            <td>Power stays below</td>
            <td>Power value has to stay below power value above for this many seconds before room state timeout countdown will start. This is keep room state from changing frequently with power value fluctuating.</td>
        </tr>
        <tr>
            <td>Contact sensor</td>
            <td>Contact sensor(s) when closed will set room occupancy to Engaged with motion.</td>
        </tr>
        <tr>
            <td>Outside door</td>
            <td>For use with outside doors like garage doors where the Engaged state is triggered with motion if the door is open.</td>
        </tr>
        <tr>
            <td>Timeout</td>
            <td>Value in seconds for room occupancy timeout from Engaged to Vacant.</td>
        </tr>
        <tr>
            <td>Reset Engaged/Asleep</td>
            <td>Reset Engaged or Asleep state when another room changes to Engaged or Asleep.</td>
        </tr>
        <tr>
            <td>Reset Engaged directly</td>
            <td>Reset room occupancy to Vacant directly without transitioning through Checking state.</td>
        </tr>
    </table>
    <li style="font-weight: bold; margin-top: 25px; margin-bottom: 15px;">Checking Settings</li>
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
            <td>Do not restore</td>
            <td>When transitioning from Checking state to another state do not restore the light levels to their previous value if that state is Vacant.</td>
        </tr>
    </table>
    <li style="font-weight: bold; margin-top: 25px; margin-bottom: 15px;">Vacant Settings</li>
    <p>Settings that specify how this occupancy state is set. Normally it is based on motion but there are also other ways of detecting Occupied state like a specific switch turning off.</p>
    <table class="wikitable" style="width:900px">
        <tr>
            <td style="width:15%">Button</td>
            <td style="width:85%">Set room occupancy to Vacant state when button is pushed.</td>
        </tr>
        <tr>
            <td>Button number</td>
            <td>Button number of button selected above.</td>
        </tr>
        <tr>
            <td>Switch</td>
            <td>Switch which when turned off will set room occupancy to Vacant.</td>
        </tr>
        <tr>
            <td>Stop music</td>
            <td>Pause music player when room occupancy changes to Vacant.</td>
        </tr>
    </table>
    <li style="font-weight: bold; margin-top: 25px; margin-bottom: 15px;">Asleep Settings</li>
    <p>Settings that specify how this occupancy state is set. Asleep is tricky because there is no true commonly used physical asleep sensors. So, these settings allow other ways of setting Asleep occupancy state and specifying night light settings which are a little different from how lights work through the rules.</p>
    <table class="wikitable" style="width:900px">
        <tr>
            <td style="width:15%">Sleep sensor</td>
            <td style="width:85%">Sleep sensor to set room occupancy to Asleep. (not a lot of device option here that supports sleep state. some users use the webCoRE presence sensor which I had originally modified to support sleep state, subsequently Adrian added the sleep state code to main.)</td>
        </tr>
        <tr>
            <td>Button</td>
            <td style="width:75%">Set room occupancy to Asleep state when button is pushed.</td>
        </tr>
        <tr>
            <td>Button number</td>
            <td>Button number of button selected above.</td>
        </tr>
        <tr>
            <td>Switch</td>
            <td>Switch which when turned off will set room occupancy to Asleep.</td>
        </tr>
        <tr>
            <td>Power value</td>
            <td>Pause music player when room occupancy changes to Vacant.</td>
        </tr>
        <tr>
            <td>Trigger from vacant</td>
            <td>When false room will need to be in a state other than Vacant for the Asleep state to be triggered.</td>
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
            <td>Option to turn off Asleep state after certain number of hours in case you forget to turn it off in the morning.</td>
        </tr>
    </table>
    <ul>
        <li style="list-style-type:square">
            <h5>Night lights:</h5>
        </li>
        <table class="wikitable" style="width:875px">
            <tr>
                <td style="width:15%">Turn on</td>
                <td style="width:85%">Switches to turn on with motion while in Asleep state.</td>
            </tr>
            <tr>
                <td>Level</td>
                <td>Set level when turning on switches above.</td>
            </tr>
            <tr>
                <td>Color temperature</td>
                <td>Set color temperature when turning on switches above.</td>
            </tr>
            <tr>
                <td>Timeout</td>
                <td>Value in seconds for night light timeout.</td>
            </tr>
            <tr>
                <td>Button</td>
                <td>Button to control night lights only.</td>
            </tr>
            <tr>
                <td>Button number</td>
                <td>Button number of button selected above.</td>
            </tr>
            <tr>
                <td>Button action</td>
                <td>Either toggle or turn night lights on or off.</td>
            </tr>
        </table>
    </ul>
    <li style="font-weight: bold; margin-top: 25px; margin-bottom: 15px;">Locked Settings</li>
    <p>Settings that specify how this occupancy state is set. This state disables all automation for the room.</p>
    <table class="wikitable" style="width:900px">
        <tr>
            <td style="width:15%">Switch</td>
            <td style="width:85%">Switch to set room occupancy to Locked.</td>
        </tr>
        <tr>
            <td>On or off</td>
            <td>Set Locked state when switch turns on or turns off.</td>
        </tr>
        <tr>
            <td>Contact</td>
            <td>Contact to set room occupancy to Locked.</td>
        </tr>
        <tr>
            <td>Open or closed</td>
            <td>Set Locked state when contact is open or closed.</td>
        </tr>
        <tr>
            <td>Switches off</td>
            <td>When true turn off switches when room occupancy changes to Locked.</td>
        </tr>
        <tr>
            <td>Timeout</td>
            <td>Option to turn off Locked state after certain number of hours.</td>
        </tr>
    </table>
</ul>

<p style="font-weight: bold; margin-top: 25px;">These group of settings allow for light routine settings used in the rules.</p>

<ul>
    <li style="font-weight: bold; margin-top: 25px; margin-bottom: 15px;">Auto Level 'AL' Settings</li>
    <p>Settings to specify auto level and color temperature settings for the room which allows using 'AL' as a light level rule to automatically calculate and use these values based on time of day, wake and sleep time specified.</p>
    <ul>
        <li>Specify level and color temperature settings along with wake and sleep times. Also allows specifying hours before and after wake and sleep times the light level and color temperature should be dimmed for optimal light levels.</li>
    </ul>
    <li style="font-weight: bold; margin-top: 25px; margin-bottom: 15px;">Holiday Lights 'HL' Settings</li>
    <p>Settings to specify holiday light patterns for use in rules during various holiday seasons. Allows for rotating colors through or slow twinkling any set of lights specified in the rules.</p>
    <ul>
        <li>Specify groups of colors and name them so you can use them with rules.</li>
    </ul>
</ul>

<p style="font-weight: bold; margin-top: 25px;">Temperature settings is their own group.</p>

<ul>
    <li style="font-weight: bold; margin-top: 25px; margin-bottom: 15px;">Temperature Settings</li>
    <p>Manage temperature settings for the room in conjunction with thermostat or switch controlled room AC and/or heater. After adding temperature settings remember create temperature rules in maintain rules so the app can automate temperature control based on these rules.</p>
    <ul>
        <li>Specify temperature settings including specifying thermostat, AC and heater switch and fan for use in temperature rules.</li>
    </ul>
</ul>

<p style="font-weight: bold; margin-top: 25px;">Here are the rest of the settings starting with the heart of the app - Maintain Rules, which allows you to maintain automation rules for the room and turn them in to smart rooms.</p>

<ul>
    <li style="font-weight: bold; margin-top: 25px; margin-bottom: 15px;">Maintain Rules</li>
    <p>Here's where to create the rules that check room occupancy state, various sensor values and other variables to decide which lights and switches should be turned on or off. It also allows executing a piston or routine or even starting and stopping a music player based on the rules.</p>
    <ul>
        <li>Here is where you automate the room and create rules to drive which lights and/or switches turn on based on which condition(s) like occupancy state, mode, date, time and other sensor values.</li>
    </ul>
    <li style="font-weight: bold; margin-top: 25px; margin-bottom: 15px;">Adjacent Room Settings</li>
    <p>Adjacent rooms allow specifying which rooms are adjacent to that room so you can automatically turn on lights in the next room when moving through this room.</p>
    <ul>
        <li>Specify settings to light your path when you are moving through rooms.</li>
    </ul>
    <li style="font-weight: bold; margin-top: 25px; margin-bottom: 15px;">Mode and Other Settings</li>
    <p>Miscellaneous settings that don't fit any where else, like in which modes should all automation be disabled or what icon to use for the room in the rooms manager and a few other settings.</p>
    <ul>
        <li>Various miscellaneous settings that do not fit anywhere else.</li>
    </ul>
    <li style="font-weight: bold; margin-top: 25px; margin-bottom: 15px;">View All Settings</li>
    <p>What the name says.</p>
</ul>

<p style="font-style: oblique; margin-top: 25px;">This only describes a part of what's possible through this app. For more details please see each group of settings for a room in the app.</p>

<p style="font-style: oblique; margin-top: 15px;">For a github install from repo in ST use : owner: adey / name: bangali / branch: master. Install and publish the rooms occupancy DTH then install and publish the rooms manager and rooms child app smartapps.</p>

<p style="font-style: oblique; margin-top: 15px;">For a manual install here are the links, in order of DTHs and smartapps you should save and publish.</p>

<p style="font-weight: bold; margin-top: 5px;">rooms occupancy DTH:</p>
<https://raw.githubusercontent.com/adey/bangali/master/devicetypes/bangali/rooms-occupancy.src/rooms-occupancy.groovy>

<p style="font-weight: bold; margin-top: 15px;">rooms manager smartapp:</p>
<https://raw.githubusercontent.com/adey/bangali/master/smartapps/bangali/rooms-manager.src/rooms-manager.groovy>

<p style="font-weight: bold; margin-top: 15px;">rooms child smartapp:</p>
<https://raw.githubusercontent.com/adey/bangali/master/smartapps/bangali/rooms-child-app.src/rooms-child-app.groovy>

<p style="font-weight: bold; margin-top: 20px;">Then go to ST app -> Automation tab -> Add a Smartapp -> My apps in ST app and install rooms manager app then create your rooms within rooms manager.</p>

<h5>When creating a room first give the room a name and save the room then go back in to the room to add various settings to the room. This is because the app uses app state to manage the rules and in ST the app state is not consistent till the app has been saved once.</h5>

<i><p>For more details see wiki here:</p>
http://thingsthataresmart.wiki/index.php?title=Rooms_Occupancy
</i>

<p style="margin-top: 25px; margin-bottom: 50px;">Knowing that users are finding their app useful enough to support development of the app is always motivating for any dev. So here is the donation link. To be clear ... <i>no donation is required or expected to use rooms manager</i>. But if you do donate please also know that it is much appreciated, thank you. <a href="https://www.paypal.me/dey">Donate here.</a></p>

<div id="screenshots-table">
    <table>
        <tr>
            <td style="padding:5px">
                <img height="400" src="https://user-images.githubusercontent.com/319291/32026136-d367e612-b997-11e7-885f-de855d9e444e.png">
            </td>
            <td style="padding:15px">
                <img height="400" src="https://user-images.githubusercontent.com/319291/32026131-d23495ec-b997-11e7-8bb7-adc8aa1000b7.png">
            </td>
            <td style="padding:15px">
                <img height="400" src="https://user-images.githubusercontent.com/319291/32026137-d3829390-b997-11e7-9d07-1899a6cace35.png">
            </td>
        </tr>
        <tr>
            <td style="padding:15px">
                <img height="400" src="https://user-images.githubusercontent.com/319291/32026138-d39e1bf6-b997-11e7-86bd-22ba97467597.png">
            </td>
            <td style="padding:15px">
                <img height="400" src="https://user-images.githubusercontent.com/319291/32026133-d26e175e-b997-11e7-88bc-ebe103cba53a.png">
            </td>
            <td style="padding:15px">
                <img height="400" src="https://user-images.githubusercontent.com/319291/32026132-d251cc52-b997-11e7-9dcb-3bffab4094f0.png">
            </td>
        </tr>
        <tr>
            <td style="padding:15px">
                <img height="400" src="https://user-images.githubusercontent.com/319291/32026135-d34e0b2a-b997-11e7-950a-d2e88e7cc2f2.png">
            </td>
            <td style="padding:15px">
                <img height="400" src="https://user-images.githubusercontent.com/319291/32026139-d3b7aab2-b997-11e7-91b9-74340fdd5a1c.png">
            </td>
            <td style="padding:15px">
                <img height="400" src="https://user-images.githubusercontent.com/319291/32026134-d285d5b0-b997-11e7-9387-b679be8410b8.png">
            </td>
        </tr>
    </table>
</div>
