#!bin/sh

export GRADLE_USER_HOME="$CACHE/gradle"
exec sh gradlew --no-daemon "$@"