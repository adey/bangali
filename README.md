# bangali's random code stuff for SmartThings.

while ST has a concept of rooms it seems to be more of a grouping mechanism. in contrast rooms occupancy considers the room as a meta device and automates a few common tasks associated with a “room” physical or virtual. in keeping with that it has attributes, capabilities and commands which are useable in webcore or other smartapps like Smart Lighting.

however, what makes it useful for me is not just the room's state but the abiloty to manage automation for room in one set of settings for the room. when adding a room device through the smartapp you are able to configure settings for the room which allow the various devices in the room to be automated based on these settings.

for example in settings:

- if away modes are selected, rooms will change to ‘vacant’ state if they are in either the ‘occupied’ or ‘checking’ state when location changes to any of the away modes.
- if motion sensors are selected, rooms will change to ‘occupied’ state on motion if they were previously ‘vacant’.
- if switches are selected, when room changes to ‘occupied’ the switches will be turned on.
- if motion timeout in seconds is selected the room will be changed to ‘vacant’ after last motion inactive + motion timeout seconds.
- if 2nd group of switches are selected, when room changes to ‘vacant’ the switches will be turned off.

this is only a part of what's possible. please take a look at the settings for a room in the child-app that captures all of these settings to get a sense of what else is possible.

these are very common tasks around rooms which most users go through automating at some point, so also posting here in case others find this useful.

<br>
<img height="400" src="https://user-images.githubusercontent.com/319291/32026136-d367e612-b997-11e7-885f-de855d9e444e.png">
<img height="400" src="https://user-images.githubusercontent.com/319291/32026131-d23495ec-b997-11e7-8bb7-adc8aa1000b7.png">
<img height="400" src="https://user-images.githubusercontent.com/319291/32026137-d3829390-b997-11e7-9d07-1899a6cace35.png">
<img height="400" src="https://user-images.githubusercontent.com/319291/32026138-d39e1bf6-b997-11e7-86bd-22ba97467597.png">
<img height="400" src="https://user-images.githubusercontent.com/319291/32026133-d26e175e-b997-11e7-88bc-ebe103cba53a.png">
<img height="400" src="https://user-images.githubusercontent.com/319291/32026132-d251cc52-b997-11e7-9dcb-3bffab4094f0.png">
<img height="400" src="https://user-images.githubusercontent.com/319291/32026135-d34e0b2a-b997-11e7-950a-d2e88e7cc2f2.png">
<img height="400" src="https://user-images.githubusercontent.com/319291/32026139-d3b7aab2-b997-11e7-91b9-74340fdd5a1c.png">
<img height="400" src="https://user-images.githubusercontent.com/319291/32026134-d285d5b0-b997-11e7-9387-b679be8410b8.png">
<br>
