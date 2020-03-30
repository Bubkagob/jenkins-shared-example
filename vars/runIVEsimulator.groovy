def call(config){
  def memories = [
    "tcm",
    "sram"
  ]
  memories.each{
    memo ->
    {
      if(config.buses){
        config.buses.each{
          bus ->
          stage("Build bus ${bus}"){
            sh """
            #!/bin/bash -l
            export RISCV=${config.toolchain}
            export PATH=\$RISCV/bin:\$PATH
            echo "BUSES"
            """
          }
        }
      }

      if(config.scenarios) {
        config.scenarios.each{
          scenario -> {
            stage("Run ${scenario}"){
              sh """
              #!/bin/bash -l
              export RISCV=${config.toolchain}
              export PATH=\$RISCV/bin:\$PATH
              cd encr/ive
              cd tests_src
              chmod +x build_rtl_sim.sh
              echo "SCENARIOS"
              ##\$(PLF_SCENARIO=${scenario} ./build_rtl_sim.sh > log_TCM.txt 2>&1)
              """
            }
          }
      }

      else {
        sh """
          #!/bin/bash -l
          export RISCV=${config.toolchain}
          export PATH=\$RISCV/bin:\$PATH
          echo "BUSES"
          """
      }
    }
  }  
}