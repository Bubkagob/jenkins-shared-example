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
        script {
          def builds = [:]
          for (bus in config.buses) {
            def new_bus = bus
            builds[bus] = {
              count++
              sleep count*10
              sh """
              #!/bin/bash -l
              export RISCV=${config.toolchain}
              export PATH=\$RISCV/bin:\$PATH
              cd encr/ive
              cd rtl_src
              #make run_vcs BUS=${new_bus} MEM=${memo} platform_dir=scr4
              make BUS=${new_bus} MEM=${memo} platform_dir=scr4
              """
            }
          }
          parallel builds
        }
      }

      if(config.scenarios) {
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
              # make PLF_SCENARIO=${scenario} run_vcs MEM=${memo}
              make PLF_SCENARIO=${new_conf}  MEM=${memo}
              """
            }
          }
          parallel builds
        }
      }

      else {
        sh """
        #!/bin/bash -l
        export RISCV=${config.toolchain}
        export PATH=\$RISCV/bin:\$PATH
        cd encr/ive
        cd rtl_src
        echo "platform_dir=scr4 ${memo} !!!!!!!!!"
        make MEM=${memo} platform_dir=scr4
        # make run_vcs MEM=${memo} platform_dir=scr4
        """
      }
    }
  }
}
