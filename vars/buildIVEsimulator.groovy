def call(config){
  if(config.buses){
    config.buses.each{
      bus ->
      stage("Build simulator bus=${bus}"){
        sh """
        #!/bin/bash -l
        export RISCV=${config.toolchain}
        export PATH=\$RISCV/bin:\$PATH
        cd encr/ive
        cd rtl_src
        make BUS=${bus} build_vcs
        """
        }
    }
  } else {
    stage("Build simulator "){
      sh """
      #!/bin/bash -l
      export RISCV=${config.toolchain}
      export PATH=\$RISCV/bin:\$PATH
      cd encr/ive
      cd rtl_src
      make build_vcs
      """
    }
  }
}
