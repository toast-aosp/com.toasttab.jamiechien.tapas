#!/bin/bash


TOOL_VERSION="1.0.2"
KEYWORD=("ILITEK Driver probe failed" "i2c_geni a94000.i2c: GSI QC err")
CURR=`pwd`

echo "==============================="
adb connect $1
adb -s $1 wait-for-device
if [ $# -gt 0 ]; then
    if [ $(adb devices | grep $1 | wc -l) -gt 0 ]; then
        echo "Target device $1 connected"
        TARGET="-s $1"
        test -d $CURR/$1 || mkdir -p $CURR/$1
    else
        echo "[ERROR] Target device $1 not connected!!!"
        exit
    fi
else
    d=$(adb devices| sed -n 2p | awk '{print $1}')
    echo "Device $d connected"
    test -d $CURR/$d || mkdir -p $CURR/$d
fi

echo "==============================="
cd $CURR/$d
run=0
error_cnt=0
while [ $error_cnt -eq 0 ]; do
    echo "RUN-$run"
    adb $TARGET reboot
    while [ $(ping $1 -c 5 | grep -w "0% packet loss" | wc -l) != 1 ]; do
        sleep 5
        echo "    pinging..."
    done
    echo "device found"
    while [ $(adb connect $1 | grep "connected to $1:5555" | wc -l) != 1 ]; do
        sleep 5
        echo "    connecting..."
    done
    adb $TARGET wait-for-device
    adb $TARGET bugreport
    LAST_BUGREP=`ls -A1 . | grep bugreport | sort -r | head -1`
    unzip -oq $LAST_BUGREP "${LAST_BUGREP%.*}.txt"
    for (( k=0; k<${#KEYWORD[@]}; k++ )); do
        echo "    grep keyword-$k"
        error_cnt=`grep "${KEYWORD[$k]}" "${LAST_BUGREP%.*}.txt" | wc -l`
        if [ $error_cnt -gt 0 ]; then
            echo "Got you evil one! (${KEYWORD[$k]})"
            echo "Rename evil bugreport..."
            mv $LAST_BUGREP I_FOUND_THE_EVIL.zip
            exit
        fi
    done
    run=$(($run+1))
    if [ $(($run%100)) -eq 0 ]; then
        adb $TARGET shell rm /bugreports/bugreport-*.*
        rm bugreport-*.zip
        rm bugreport-*.txt
    fi
    sleep 10
done
cd $CURR

