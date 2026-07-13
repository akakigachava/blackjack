# Locate a JDK for the Maven wrapper. Sourced by run.sh and test.sh.
#
# Order of preference:
#   1. an already-set JAVA_HOME (must actually contain bin/javac)
#   2. /usr/libexec/java_home on macOS
#   3. the javac on PATH, but only if it resolves to a real JDK home —
#      Apple's /usr/bin/javac shim must not turn into JAVA_HOME=/usr
#   4. a working javac on PATH with JAVA_HOME left unset
# Otherwise: stop with a clear setup error.

if [ -n "${JAVA_HOME:-}" ]; then
  if [ ! -x "$JAVA_HOME/bin/javac" ]; then
    echo "Error: JAVA_HOME is set to '$JAVA_HOME' but '$JAVA_HOME/bin/javac' does not exist." >&2
    echo "Point JAVA_HOME at a full JDK 17+ (not a JRE), or unset it." >&2
    exit 1
  fi
else
  if [ "$(uname -s)" = "Darwin" ] && [ -x /usr/libexec/java_home ]; then
    JAVA_HOME="$(/usr/libexec/java_home 2>/dev/null || true)"
  fi

  if [ -z "${JAVA_HOME:-}" ] && command -v javac >/dev/null 2>&1; then
    javac_path="$(readlink -f "$(command -v javac)" 2>/dev/null || command -v javac)"
    candidate="$(dirname "$(dirname "$javac_path")")"
    # A real JDK home ships bin/javac plus a release file; this rejects
    # pseudo-homes like /usr derived from launcher shims.
    if [ -x "$candidate/bin/javac" ] && [ -f "$candidate/release" ]; then
      JAVA_HOME="$candidate"
    fi
  fi

  if [ -n "${JAVA_HOME:-}" ]; then
    export JAVA_HOME
  elif ! javac -version >/dev/null 2>&1; then
    echo "Error: no usable JDK found. Install JDK 17+ (a JRE is not enough)" >&2
    echo "and/or set JAVA_HOME to its installation directory." >&2
    exit 1
  fi
  # If javac on PATH works but no JDK home was identified, leave JAVA_HOME
  # unset and let Maven resolve the toolchain from PATH.
fi
