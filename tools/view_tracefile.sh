#!/bin/sh
if [[ -z $ADB ]]; then ADB=adb; fi
if [[ -z $1 ]]; then
  echo "No file selected";
else
  $ADB pull /storage/sdcard0/$1
  traceview $1
fi
