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

# Legacy baseline checks kept from the original starter.
mkdir -p out
"$JAVAC" -d out src/main/java/Main.java src/main/java/blackjack/*.java tests/BlackjackBaselineTest.java
"$JAVA" -cp out BlackjackBaselineTest
