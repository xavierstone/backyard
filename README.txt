Backyard is an app for locating and reviewing campsites around the world.

The current development version reflects a desire for the integration of functionality into a single interface.
There is a lot of work left to go, but this is a good starting point.

Please use the develop branch, as I have refrained from merging it into the master for the time being.

Make sure you give it the permissions it asks for at the start, because I have had problems with the permission dialog in the past. While I am pretty sure the app can handle being denied permissions, I would rather you wait to check it out until I make a point of testing it myself.

The Android emulator requires you to manually set your location, and when you do, set it somewhere in the vicinity of 43.6396894,-72.3118208, which is around the same area most of the hardcoded campsites are.

Pics are stored fairly small in the DB, so they may come out looking small depending on the screen resolution of your emulator. This is on my radar.

Most buttons are currently disabled, while these features are nice, they are not essential to the app and I am prioritizing the essentials.

The database is still implemented as SQLite and some of the functionality is still messy and inefficient, but if you want to check out previous commits you can compare it and it's definitely getting much better. Not to mention, all that messy functionality is now hidden behind a very clean layer of classes!

Be sure to check out the data structures in the /.models package; these are all new and the only data structure before was /.db.DBData.java which you can check out but I don't really want to talk about.

That's all for now,
-Xavier