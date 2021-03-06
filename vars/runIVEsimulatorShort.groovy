def call(config){
  def memories = [
    "tcm",
    "sram"
  ]
  def count = 0


  memories.each{
    memo ->
      stage("Run ${memo}") {
      if(config.buses){
        //stage("run ${memo}") {
          script {
            def builds = [:]
            for (bus in config.buses) {
              def new_bus = bus
              builds[bus] = {
                  //stage("Run ${bus}") {
                    count++
                    sleep count*10
                    sh """
                    #!/bin/bash -l
                    echo "${new_bus} ${memo} !!!!!!!!!"
                    export RISCV=${config.toolchain}
                    export PATH=\$RISCV/bin:\$PATH
                    cd encr/ive
                    cd rtl_src
                    #make run_vcs BUS=${new_bus} MEM=${memo} platform_dir=scr4
                    #make BUS=${new_bus} MEM=${memo} platform_dir=scr4
                    """
                  //}
              }
            }
            parallel builds
          }
        //}



        // config.buses.each{
        //   bus ->
        //     stage("Build bus ${bus} with ${memo}"){
        //       sh """
        //       #!/bin/bash -l
        //       export RISCV=${config.toolchain}
        //       export PATH=\$RISCV/bin:\$PATH
        //       echo "BUSES"
        //       """
        //     }
        // }
      }

      if(config.scenarios) {
        //stage("Run ${memo}") {
          script {
            def builds = [:]
            for (scenario in config.scenarios) {
              def new_conf = scenario
              builds[scenario] = {
                count++
                sleep count*10
                sh """
                #!/bin/bash -l
                export RISCV=${config.toolchain}
                export PATH=\$RISCV/bin:\$PATH
                cd encr/ive
                cd rtl_src
                echo "${new_conf} ${memo} !!!!!!!!!"
                # make PLF_SCENARIO=${scenario} run_vcs MEM=${memo}
                # make PLF_SCENARIO=${new_conf}  MEM=${memo}
                """
              }
            }
            parallel builds
          }
        //}

        // config.scenarios.each{
        //   scenario -> 
        //     stage("Run ${scenario} with ${memo}"){
        //       sh """
        //       #!/bin/bash -l
        //       export RISCV=${config.toolchain}
        //       export PATH=\$RISCV/bin:\$PATH
        //       cd encr/ive
        //       cd tests_src
        //       chmod +x build_rtl_sim.sh
        //       echo "SCENARIOS"
        //       ##\$(PLF_SCENARIO=${scenario} ./build_rtl_sim.sh > log_TCM.txt 2>&1)
        //       """
        //     }
        // }
      }

      else {
        sh """
          #!/bin/bash -l
          export RISCV=${config.toolchain}
          export PATH=\$RISCV/bin:\$PATH
          cd encr/ive
          cd rtl_src
          echo "platform_dir=scr4 ${memo} !!!!!!!!!"
          # make MEM=${memo} platform_dir=scr4
          # make run_vcs MEM=${memo} platform_dir=scr4
          """
      }
    }
  }
}
