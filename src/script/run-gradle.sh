#!bin/sh

export GRADLE_USER_HOME="$CACHE/gradle"
cd src/
exec sh gradlew --no-daemon "$@"