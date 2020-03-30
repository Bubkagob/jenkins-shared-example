def call(scenarios){
    
    if(scenarios){
      scenarios.each{
        scenario ->
        stage("Build ${scenario}"){
          sh """
          #!/bin/bash -l
          export RISCV=${config.toolchain}
          export PATH=\$RISCV/bin:\$PATH
          cd encr/ive
          cd tests_src
          chmod +x build_rtl_sim.sh
          \$(PLF_SCENARIO=${scenario} ./build_rtl_sim.sh > log_TCM.txt 2>&1)
          """
        }
      }
    } else {
      sh """
      #!/bin/bash -l
      export RISCV=${toolchain}
      export PATH=\$RISCV/bin:\$PATH
      cd encr/ive
      cd tests_src
      chmod +x build_rtl_sim.sh
      ./build_rtl_sim.sh
      """
    }
}
