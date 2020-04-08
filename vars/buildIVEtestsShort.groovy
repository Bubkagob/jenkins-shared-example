def call(config){
    if(config.scenarios){
        script {
          def builds = [:]
          for (scenario in config.scenarios) {
            def new_conf = scenario
            builds[scenario] = {
              sh """
              #!/bin/bash -l
              export RISCV=${config.toolchain}
              export PATH=\$RISCV/bin:\$PATH
              cd encr/ive
              cd tests_src
              chmod +x build_rtl_sim.sh
              echo "${new_conf} !!!!"
              # \$(PLF_SCENARIO=${new_conf} ./build_rtl_sim.sh > log_${new_conf}.txt 2>&1)
              """
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
      # ./build_rtl_sim.sh
      """
    }
}
