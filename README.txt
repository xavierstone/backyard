Backyard is an app for locating and reviewing campsites around the world (well, at least the couple sample ones I added in the Upper Connecticut River Valley).

Some of the auxiliary functionality is currently disabled, but the meat of the project is beefy and delicious. The full stack is currently enabled, but I'm not running an actual server, so testing it will require some setup:

1) install MongoDB and run an instance
2) using the mongoimport tool, import all collections from the db folder on this repo
3) download the Backyard Server at github.com/xavierstone/backyard_server and run a Maven build with the goal "spring-boot:run"; this launches the server
4) download the develop branch of this repo, run in Android Studio

Things to Try:
 - search for "trail"
 - search for "gorge"
 - click on the markers that pop up on the map to see more information for a given site
 - check out the Quechee Gorge site, it has multiple images and thus demonstrates the photo gallery navigation
 - create a site of your own on the MongoDB back end and verify that it shows up in a search (in-app "add site" functionality is currently disabled)
 - sign in using the test user with email "test" and password "test" or create an account of your own!
 - try signing out and switching users as well

Please be gentle with my baby,
-Xavier