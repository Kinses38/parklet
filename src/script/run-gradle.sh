#!bin/sh

export GRADLE_USER_HOME="$CACHE/gradle"
pwd
exec sh gradlew --no-daemon "$@"