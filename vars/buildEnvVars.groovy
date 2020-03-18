def call() {
    JOB_NAME = currentBuild.fullDisplayName
}

@NonCPS
def printParams(message) {
  env.getEnvironment().each { name, value -> println "Name: $name -> Value $value" }
}
