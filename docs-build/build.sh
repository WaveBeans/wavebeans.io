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
builder --base=$(pwd)/wavebeans/docs/user/api --output=$(pwd)/../docs/api/ --nav-order=3 --support-extensions=png
builder --base=$(pwd)/wavebeans/docs/user/exe --output=$(pwd)/../docs/exe/ --nav-order=4 --support-extensions=png
builder --base=$(pwd)/wavebeans/docs/user/cli --output=$(pwd)/../docs/cli/ --nav-order=5 --support-extensions=png
builder --base=$(pwd)/wavebeans/docs/user/http --output=$(pwd)/../docs/http/ --nav-order=6 --support-extensions=png

### blog

# fresh build
git clone $WAVE_BLOG_REPO wave-blog
cd wave-blog/
git checkout $BLOG_VERSION
cd ../

# build blog 
rm -rf wave-blog/podcast-use-case # temporary remove it, do not publish yet
builder --base=$(pwd)/wave-blog --output=$(pwd)/../blog/ --nav-order=2 --support-extensions=png

### clean
rm builder.jar
rm -rf wavebeans
rm -rf wave-blog
