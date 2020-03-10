#!/bin/sh

# Requires kotlin to be installed:
# > brew update kotlin
# > brew install kotlin

WAVEBEANS_REPO=git@github.com:WaveBeans/wavebeans.git
VERSION=master

# clean
rm -rf wavebeans

# fresh build
git clone $WAVEBEANS_REPO wavebeans
cd wavebeans/
git checkout $VERSION
cd ../

# compile doc-builder
kotlinc builder.kt -include-runtime -d builder.jar
alias builder="java -jar builder.jar"

# build docs from wavebeans sources
builder $(pwd)/wavebeans/docs/user/lib  $(pwd)/../docs/api/ 3
builder $(pwd)/wavebeans/docs/user/exe  $(pwd)/../docs/exe/ 4
builder $(pwd)/wavebeans/docs/user/cli  $(pwd)/../docs/cli/ 5
builder $(pwd)/wavebeans/docs/user/http  $(pwd)/../docs/http/ 6

# clean
rm builder.jar
rm -rf wavebeans