def call(config){
    
    if(config.scenarios){
        script {
          def builds = [:]
          for (scenario in config.scenarios) {
            echo "IN LOOP"
            echo scenario
            builds[scenario] = {
              // node {
              //   stage("Build ${scenario}") {
                  def new_conf = scenario
                  sh """
                  #!/bin/bash -l
                  echo "SCENARIO NEW! ${new_conf}"
                  export RISCV=${config.toolchain}
                  export PATH=\$RISCV/bin:\$PATH
                  cd encr/ive
                  cd tests_src
                  chmod +x build_rtl_sim.sh
                  # \$(PLF_SCENARIO=${scenario} ./build_rtl_sim.sh > log_${scenario}.txt 2>&1)
                  """
              //   }
              // }
            }
          }
          parallel builds
        }

      // config.scenarios.each{
      //   scenario ->
      //   stage("Build ${scenario}"){
      //     sh """
      //     #!/bin/bash -l
      //     export RISCV=${config.toolchain}
      //     export PATH=\$RISCV/bin:\$PATH
      //     cd encr/ive
      //     cd tests_src
      //     chmod +x build_rtl_sim.sh
      //     \$(PLF_SCENARIO=${scenario} ./build_rtl_sim.sh > log_TCM.txt 2>&1)
      //     """
      //   }
      // }


    } else {
      sh """
      #!/bin/bash -l
      export RISCV=${config.toolchain}
      export PATH=\$RISCV/bin:\$PATH
      cd encr/ive
      cd tests_src
      chmod +x build_rtl_sim.sh
      ./build_rtl_sim.sh
      """
    }
}
