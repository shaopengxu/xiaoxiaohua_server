set GRADLE_OPTS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005
gradle clean jettyRun -Penv=dev-xsp