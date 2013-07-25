#!/bin/sh
if [[ -z $ADB ]]; then ADB=adb; fi
$ADB shell su -c cp /data/data/de.elanev.studip.android.app/databases/studip.db	/storage/sdcard0
$ADB pull /storage/sdcard0/studip.db
$ADB shell su -c rm /storage/sdcard0/studip.db	
echo "$@;" | sqlite3 -header -column studip.db