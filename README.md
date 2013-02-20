Stud.IP Android App
===================

This is a fork of the Stud.IP Android App by University Oldenburg.
[https://github.com/uol-studip/StudIPAndroidApp]
I'm trying to refactor it with modern libs and newer Android Features, 
but also with backward compatibility until 2.3.3.
This app is using the studip-rest.ip Rest-API Plugin.
[https://github.com/studip/studip-rest.ip]
Feel free to fork and contribute to this project.

Setup
-----
* Make sure you habe Ecclipse with maven and egit.
* Install the maven android plugin as described here [http://code.google.com/p/maven-android-plugin/wiki/GettingStarted]
* In eclipse just use File->Import->Check out existing maven repository from scm
* Maven will take care of the rest

    NOTE: To test it with an installation of the restIP.plugin, 
    you need to create your own TempServerDeclares in the studip.app.util package.
    See the studip.app.util.TempServerDeclaresExample for more information.
            

Developed By
------------
* ELAN e.V

Contributors
------------

Contributing
------------
*Fork it
*Create branch
*Make and commit your changes
*Push the branch to your fork
*Open a Pull Request

Used Libs
---------
*oauth-signpost [http://code.google.com/p/oauth-signpost/]
*Jackson JSON Processor [http://wiki.fasterxml.com/JacksonHome]

License
-------
    Copyright (c) 2013 ELAN e.V.
	All rights reserved. This program and the accompanying materials
    are made available under the terms of the GNU Public License v3.0
    which accompanies this distribution, and is available at
    [http://www.gnu.org/licenses/gpl.html]
