#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."

# Maven needs a full JDK. If JAVA_HOME is unset, derive it from javac.
if [ -z "${JAVA_HOME:-}" ] && command -v javac >/dev/null; then
  JAVA_HOME="$(dirname "$(dirname "$(readlink -f "$(command -v javac)")")")"
  export JAVA_HOME
fi

# Maven-managed tests (JUnit).
./mvnw -q test

# Legacy baseline checks kept from the original starter.
mkdir -p out
javac -d out src/main/java/Main.java tests/BlackjackBaselineTest.java
java -cp out BlackjackBaselineTest
