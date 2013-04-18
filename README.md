Stud.IP Android App
===================

This is a fork of the Stud.IP Android App by University Oldenburg. [uol-studip/StudIPAndroidApp][1]
We are trying to refactor it with modern libs, new features and backward compatibility down to 2.3.3.

This app is using the studip-rest.ip Rest-API Plugin. [studip/studip-rest.ip][2]

Feel free to fork and contribute to this project.

Setup
-----
* Make sure you've installed Eclipse with Maven and EGit.
* Install the Maven Android plugin as described here [http://code.google.com/p/maven-android-plugin/wiki/GettingStarted][3]
* In Eclipse just use File->Import->Check out existing Maven repository from scm
* Maven will take care of the rest
* For the time being you need to import the ActionBarSherlock and SlidingMenu manually without Maven.
For more information on how to do this, see [here][8] and [here][9]

    NOTE: To test it with your own installation of the studip-rest.ip plugin, 
    you need to create your own TempServerDeclares in the de.elanev.studip.android.app.util package
	and add them to the list in de.elanev.studip.android.app.backend.net.ChooseServerFragment.java.
    See the de.elanev.studip.android.app.util.TempServerDeclaresExample for more information.

Developed By
------------
* [ELAN e.V][10]

Contributors
------------

Contributing
------------
* Fork it
* Create branch
* Make your improvements and commit your changes
* Push the branch to your fork
* Open a Pull Request
* We will take care of the rest

Libs
---------
* [oauth-signpost][4]
* [Jackson JSON Processor][5]
* [ActionBarSherlock][6]
* [SlidingMenu][7]

License
-------
    Copyright (c) 2013 ELAN e.V.

	All rights reserved. This program and the accompanying materials
    are made available under the terms of the GNU Public License v3.0
    which accompanies this distribution, and is available at
    http://www.gnu.org/licenses/gpl.html

[1]: https://github.com/uol-studip/StudIPAndroidApp
[2]: https://github.com/studip/studip-rest.ip
[3]: http://code.google.com/p/maven-android-plugin/wiki/GettingStarted
[4]: http://code.google.com/p/oauth-signpost/
[5]: http://wiki.fasterxml.com/JacksonHome
[6]: http://actionbarsherlock.com/
[7]: https://github.com/jfeinstein10/SlidingMenu
[8]: http://actionbarsherlock.com/usage.html
[9]: https://github.com/jfeinstein10/SlidingMenu#setup-with-actionbarsherlock
[10]: http://www.elan-niedersachsen.de/startseite/
