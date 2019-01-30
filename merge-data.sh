#!/usr/bin/env bash

LOCAL="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

pushd $1
git checkout $2
popd

cp $1/java/libphonenumber/src/com/google/i18n/phonenumbers/data/* ${LOCAL}/libphoneval/src/main/assets/com/ajithvgiri/libphoneval/data
cp $1/java/libphonenumber/src/com/google/i18n/phonenumbers/data/* ${LOCAL}/libphoneval/src/test/resources/com/ajithvgiri/libphoneval/data
cp $1/java/libphonenumber/test/com/google/i18n/phonenumbers/data/* ${LOCAL}/libphoneval/src/test/resources/com/ajithvgiri/libphoneval/data
