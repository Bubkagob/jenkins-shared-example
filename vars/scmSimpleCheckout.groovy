def call(String repo, String branch) {
    def project_name = repo.split("/")[-1].toUpperCase()
    stage("Checkout ${project_name}") {
        script{
            retry(5){
                scmVars = checkout([
                    $class: 'GitSCM',
                    branches: [[name: "*/${branch}"]],
                    doGenerateSubmoduleConfigurations: false,
                    extensions: [
                        [$class: 'CloneOption', timeout: 120],
                        [$class: 'CleanCheckout']
                    ],
                    submoduleCfg: [],
                    userRemoteConfigs: [[
                            credentialsId: 'IVAN_PASS',
                            name: 'origin',
                            refspec: "+refs/heads/${branch}:refs/remotes/origin/${branch}",
                             url: "${repo}"
                            ]
                        ]
                    ]
                )
                sh "sed -i 's+https://+ssh://git@+g' .gitmodules"
                sh "git submodule sync"
                sh "git submodule update --init tests"  
            }
        }
    }
    return scmVars
}