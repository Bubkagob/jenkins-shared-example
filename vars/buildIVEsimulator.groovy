def call(config){
  cd rtl_src make build_vcs
    cd rtl_src make BUS=axi build_vcs
    if(config.buses){
      config.buses.each{
        bus ->
        stage("Build bus ${bus}"){
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
