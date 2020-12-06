Backyard is an app for locating and reviewing campsites around the world (well, at least the Upper Connecticut River Valley).

The current development version is really coming together. A lot of the auxiliary functionality is currently disabled, but the meat of the project is the priority. The full stack is currently enabled, but I'm not running an actual server, so testing it may be a pain. Feel free to take my word for it, but if you want to test it, do this:

1) install MongoDB and run an instance
2) using the mongoimport tool, import all collections from the db folder on this repo
3) download the Backyard Server at github.com/xavierstone/backyard_server and run a Maven build with the goal "spring-boot:run"; this launches the server
4) download the develop branch of this repo, run in Android Studio

Please use the develop branch, as I have refrained from merging it into the master for the time being.

Things to Try:
 - search for "trail"
 - search for "gorge"
 - click on the markers that pop up on the map to see more information for a given site
 - check out the Quechee Gorge site, it has multiple images and thus demonstrates the photo gallery navigation
 - create a site of your own on the MongoDB back end and verify that it shows up in a search

The app automatically signs in a test user, but credentials are validated against the database and passwords are stored as a salt/hash combination.

Unresolved:
 - Pics are stored fairly small in the DB, so they may come out looking small depending on the screen resolution of your emulator.
 - Most buttons are currently disabled, while these features are nice, they are not essential to the app and I am prioritizing the essentials.

That's all for now,
-Xavier