#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."

# Maven needs a full JDK; jdk-env.sh validates or discovers JAVA_HOME.
. scripts/jdk-env.sh

./mvnw -q compile exec:java
