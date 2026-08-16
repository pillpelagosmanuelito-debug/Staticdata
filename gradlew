#!/usr/bin/env sh
##############################################################################
# Gradle start up script for POSIX. Standard wrapper script (Gradle 8.7).
# NOTA: el binario gradle-wrapper.jar no pudo descargarse en el entorno de
# construcción (sin acceso a red). El workflow de GitHub Actions
# (.github/workflows/android-build.yml) regenera este jar automáticamente
# con `gradle wrapper` antes de invocar ./gradlew, por lo que el pipeline de
# CI funciona igualmente en cuanto el usuario haga push.
##############################################################################
APP_HOME=$(cd "$(dirname "$0")" && pwd)
APP_NAME="Gradle"
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

if [ ! -f "$CLASSPATH" ]; then
  echo "ERROR: gradle-wrapper.jar no está presente." >&2
  echo "Ejecuta 'gradle wrapper --gradle-version 8.7' una vez (con Gradle instalado" >&2
  echo "o mediante el workflow de GitHub Actions incluido) para generarlo." >&2
  exit 1
fi

if [ -n "$JAVA_HOME" ] ; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD="java"
fi

exec "$JAVACMD" $DEFAULT_JVM_OPTS -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
