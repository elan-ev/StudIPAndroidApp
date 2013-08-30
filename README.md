Stud.IP Android App
===================

A mobile Stud.IP client application for the Android plattform which utilizes the studip-rest.ip Rest-API Plugin [studip/studip-rest.ip][2]
for communication with a Stud.IP backend.

NOTE: This app is in alpha stadium and not intended to be used in an productive enviroment.

If you want to contribute to this project, feel free to do so. For more information on how to do this read the Contributing section below.

Setup
-----
* Make sure you've installed Eclipse with Maven and EGit.
* Install the Maven Android plugin as described here [http://code.google.com/p/maven-android-plugin/wiki/GettingStarted][3]
* In Eclipse just use `File->Import->Check out existing Maven repository from scm`
* Maven will take care of the rest
* All external library projects are now imported as git submodules.
	* You have to run ```git submodule init``` and ```git submodule update```, otherwise it will not compile.
    
NOTE: To test it with your own installation of the studip-rest.ip plugin, 
you need to create your own ```TempServerDeclares``` in the ```de.elanev.studip.android.app.backend.net.oauth```
package. See the ```TempServerDeclaresExample.java``` for more details.

Contributing
------------
* Improve the code
	* Fork it, make your changes, commit them and open a Pull Request. We will take care of the rest.
* Create issues for bugs, feature requests and other ideas
* Contribute to the wiki

Libs
---------
* [oauth-signpost][4]
* [Jackson JSON Processor][5]
* [ActionBarSherlock][6]
* [SlidingMenu][7]
* [Volley HTTP][8]

Developed By
------------
* [ELAN e.V][10]

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
[8]: https://android.googlesource.com/platform/frameworks/volley/
[10]: http://www.elan-niedersachsen.de/startseite/
