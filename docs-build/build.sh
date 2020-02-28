#!/bin/sh

# Requires kotlin to be installed:
# > brew update kotlin
# > brew install kotlin

WAVEBEANS_REPO=git@github.com:WaveBeans/wavebeans.git
VERSION=docs-layout

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
builder $(pwd)/wavebeans/docs/user/lib  $(pwd)/../docs/api/
builder $(pwd)/wavebeans/docs/user/cli  $(pwd)/../docs/cli/
builder $(pwd)/wavebeans/docs/user/exe  $(pwd)/../docs/exe/

# clean
rm builder.jar
rm -rf wavebeans