#!/bin/sh

# Requires kotlin to be installed:
# > brew update kotlin
# > brew install kotlin

WAVEBEANS_REPO=git@github.com:WaveBeans/wavebeans.git
VERSION=master
WAVE_BLOG_REPO=git@github.com:WaveBeans/wave-blog.git
BLOG_VERSION=master

### prepare

# clean
rm -rf wavebeans
rm -rf wave-blog

# compile doc-builder
kotlinc builder.kt -include-runtime -d builder.jar
alias builder="java -jar builder.jar"

### documentation

# fresh build
git clone $WAVEBEANS_REPO wavebeans
cd wavebeans/
git checkout $VERSION
cd ../

# build docs from wavebeans sources
builder $(pwd)/wavebeans/docs/user/lib  $(pwd)/../docs/api/ 3
builder $(pwd)/wavebeans/docs/user/exe  $(pwd)/../docs/exe/ 4
builder $(pwd)/wavebeans/docs/user/cli  $(pwd)/../docs/cli/ 5
builder $(pwd)/wavebeans/docs/user/http  $(pwd)/../docs/http/ 6

### blog

# fresh build
git clone $WAVE_BLOG_REPO wave-blog
cd wave-blog/
git checkout $BLOG_VERSION

rm -rf podcast-use-case # temporary remove it, do not publish yet
cd ../

# build articles 
builder $(pwd)/wave-blog  $(pwd)/../blog/ 2

### clean
rm builder.jar
rm -rf wavebeans
rm -rf wave-blog
