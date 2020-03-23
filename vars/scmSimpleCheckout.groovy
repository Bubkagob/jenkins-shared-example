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
            }
        }
    }
    env.GIT_COMMIT = scmVars.GIT_COMMIT
    env.GIT_URL = scmVars.GIT_URL
    env.GIT_BRANCH = scmVars.GIT_BRANCH
    return scmVars
}