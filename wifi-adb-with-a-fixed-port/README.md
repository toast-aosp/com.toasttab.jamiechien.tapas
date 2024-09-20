# tapas/wifi-adb-with-a-fixed-port

## Tool Description
Adb over wifi in Android 11+ uses a dynamic port, which limits the capability of automation.
This tool is to set it to a fixed one, thus adb connection could still be available after device reboots.

## Usage
This tool is for `userdebug` build firmware only.
1. Connect the device with usb cable
2. Obtain the serial number of the connected device
    - single device: `adb get-serialno`
    - multiple devices: `adb device`
3. Run the tool with the serial number
    ```
    ./fixedWifiAdb [-s serialno]
    ```
4. Once observing completed message in the terminal, you could unplug the usb cable and use wifi adb with a fixed port `5555`.
5. Use the following commands to connect the device via wifi adb.
    ```
    adb connect [IP]
    adb -s [IP] shell
    ```
