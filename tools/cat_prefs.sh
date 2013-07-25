#!/bin/sh
adb shell su -c cat /data/data/de.elanev.studip.android.app/shared_prefs/prefs.xml | xmllint --format -
