# small-automation-tools/tp-probe-fail

## Tool Description
TG3 is found an issue of the touch panel no response after device reboots.
This tool attemps to reproduce this issue via continuously rebooting the device and obtain a bugreport to grep the error keywords.

## Usage
### device connected via usb cable
```
./tp_probe_failed.sh [serialno]
```

### device connected via wifi adb
```
./tp_probe_failed_wifi.sh [ip]
```
