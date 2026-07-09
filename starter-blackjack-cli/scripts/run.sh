#!/usr/bin/env bash
set -euo pipefail

mkdir -p out
javac -d out src/Main.java
java -cp out Main

