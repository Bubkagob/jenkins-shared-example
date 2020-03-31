def call(config){
  script {
    def builds = [:]
    for (scenario in config.scenarios) {
      builds["${scenario}"] = {
        stage("Run ${scenario}") {
          sh "[ -d build ] && echo OK || mkdir -p build"
          sh "sed -i -- 's/mode: cli/mode: coverage/g' tests/_scenarios/${scenario_name}"
          sh "cd build; perl ../tests/common/framework/launcher/launch.pl --scenario ../tests/_scenarios/${scenario_name}"
        }
      }
    }
    parallel builds
  }
}
