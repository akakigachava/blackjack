#!/usr/bin/env bash
set -euo pipefail

mkdir -p out
javac -d out src/Main.java tests/BlackjackBaselineTest.java
java -cp out BlackjackBaselineTest

