def call(config){
  def count = 0
  stage("Build simulator "){
    if(config.buses){
      script{
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
            make BUS=${new_bus} build_vcs
            """
          }
        }
        parallel builds
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
}