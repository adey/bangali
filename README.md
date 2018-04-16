# bangali's random code stuff for SmartThings.

While ST has a concept of rooms it is essentially a grouping mechanism which does not enable automation. In contrast rooms occupancy considers the room as a meta device and automates common tasks associated with a “room” physical or virtual. What makes it really useful is not just the room's occupancy state but the ability to manage automation for rooms in a set of rules for the room based on the occupancy state of the room and data from various sensors. When creating a room device through the smartapp you are able to create these rules for the rooms making your rooms really smart.

What these rules enable is many common tasks around rooms which most users go through automating at some point. Usually through setting up a few rules or creating a few pistons. I have been there and done that myself. While those work to a degree, it does not enable the kind of comprehensive automation that should be possible for devices in a room based on sensor and device inputs. This smartapp makes that possible.

If there is one principle that these apps are built on, it is - that your home automation should work in the background in a repeatable and predictable manner without requiring periodic human intervention. In short - your automation should work for you and not the other way around. But even importantly perhaps, this app gets you the kind of WAF for your home automation that you have always dreamed about. 🙂

Additionally, these rooms devices also have attributes, capabilities and commands which are useable in webCoRE or other smartapps like Smart Lighting in ST or rule machine in Hubitat. There is a range of other automations that webCoRE makes possible that could not otherwise be done without writing a custom smartapp for it. I use webCoRE for that and am I big fan of Adrian. So checkout webCoRE as well if you don't already use it.

<h4>Here are the most common room occupancy states:</h4>

- <h4>Occupied:</h4><p style="margin-left: 40px">Occupied is you go to a room are in there for a few minutes then leave the room. Lights come on when you enter the room and turn off after a couple of minutes of your leaving the room. Think of Occupied as a transient state and Engaged below as a persistent state.</p>
- <h4>Engaged:</h4><p style="margin-left: 40px">Engaged is when you stay in a room for an extended period of time and may be motionless for some or all of the time. since we cant depend on the motion event for engaged state there are different options to set the room to engaged for extended occupancy. these are all under engaged settings and there is more coming. but these help make sure the switches you set to on stay on even if there is no motion in the room. When in Engaged state you have a different and longer timeout state than the Occupied state. So there is still a motion requirement but a much higher time threshold than the Occupied state.
- <h4>Asleep:</h4><p style="margin-left: 40px">Asleep state is meant for use while the room should be 'asleep' as in not respond to most typical automation like motion automation. But it does allow for other automation like using a night light and using a button to turn on or off the night lights. You are still able to create rules for the Asleep state but it additionally support a little bit for Asleep state specific automation in the Asleep settings.
- <h4>Vacant:</h4><p style="margin-left: 40px">Vacant state is for when the room is vacant and you want everything to get turned off. It is possible to setup rules for Vacant settings as well but not required.
- <h4>Checking:</h4><p style="margin-left: 5%">Occupied state is used for transition between states and not user controlled. For example, when moving from Occupied to Vacant occupancy state the room will transition to Checking state. While the app does not allow creating rules for checking state there is some settings available to control dimming of the lights when in Checking state.
- <h4>Locked:</h4><p style="margin-left: 5%">Locked state disable all automation for the room and allows you to control lights and other devices in the room either manually or some other way.

The states 'locked', 'reserved' and 'kaput' are effectively all similar in that they all disable automation. Since there is no real difference in automation between these states instead of names, there is some sensors allowed to set / unset rooms to / from Locked state but no other automation beyond that for these occupancy states.

Here is a quick description of the various top level settings and how the app works. At the heart of the app is the concept of room states and rules to automate devices based on these room's states and other sensor inputs. (In the following description when I talk about sensors it refers to devices that have attributes which are used to drive decisions in the room's rules.)

<h4>Here are the top level settings:</h4>

- Occupied Settings
- Engaged Settings
- Checking Settings
- Vacant Settings
- Asleep Settings
- Locked Settings
- Other Devices
- Auto Level 'AL' Settings
- Holiday Lights 'HL' Settings
- Temperature Settings
- Maintain Rules
- Adjacent Room Settings
- Mode and Other Settings
- View All Settings

This is only a part of what's possible through this app. please take a look at all settings for a room in the app to get a sense of what else is possible.

<h5>When creating a room first give the room a name and save the room then go back in to the room to add various settings to the room. This is because the app uses app state to create the rules and in ST the app state is not consistent till the app has been created first.</h5>

`For a github install from repo in ST use : owner: adey / name: bangali / branch: master. Install and publish the rooms occupancy DTH then install and publish the rooms manager and rooms child app smartapps.`

`For a manual install here are the links, in order of DTHs and smartapps you should save and publish.`

rooms occupancy DTH:
https://raw.githubusercontent.com/adey/bangali/master/devicetypes/bangali/rooms-occupancy.src/rooms-occupancy.groovy

rooms manager smartapp:
https://raw.githubusercontent.com/adey/bangali/master/smartapps/bangali/rooms-manager.src/rooms-manager.groovy

rooms child smartapp:
https://raw.githubusercontent.com/adey/bangali/master/smartapps/bangali/rooms-child-app.src/rooms-child-app.groovy

Knowing that users are finding their app useful enough to support development of the app is always motivating for any dev. So here is the donation link. To be clear ... _no donation is required or expected to use rooms manager_. But if you do donate please also know that it is much appreciated. With that, if you feel like it ... <a href="https://www.paypal.me/dey">please donate here</a>.

<br>

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
