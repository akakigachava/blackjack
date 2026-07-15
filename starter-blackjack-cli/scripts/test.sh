#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."

# Maven needs a full JDK; jdk-env.sh validates or discovers JAVA_HOME.
. scripts/jdk-env.sh

# Use the discovered JDK's tools for the legacy checks when we have one.
JAVAC=javac
JAVA=java
if [ -n "${JAVA_HOME:-}" ]; then
  JAVAC="$JAVA_HOME/bin/javac"
  JAVA="$JAVA_HOME/bin/java"
fi

# Maven-managed tests (JUnit).
./mvnw -q test

# Legacy baseline checks kept from the original starter. They exercise the
# domain classes only, so compile just the test against the classes Maven
# already built (recompiling all sources here would need the MyBatis/H2
# jars on the javac classpath).
mkdir -p out
"$JAVAC" -cp target/classes -d out tests/BlackjackBaselineTest.java
"$JAVA" -cp out:target/classes BlackjackBaselineTest
