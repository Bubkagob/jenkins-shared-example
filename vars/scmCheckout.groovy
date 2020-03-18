def call(String repo, String branch) {
    def project_name = repo.split("/")[-1].toUpperCase()
    stage("Checkout ${project_name}") {
        script{
            retry(5){
                environment {
                    scmVars = checkout([
                        $class: 'GitSCM',
                        branches: [[name: "*/${branch}"]],
                        doGenerateSubmoduleConfigurations: false,
                        extensions: [
                            [$class: 'CloneOption', timeout: 120],
                            [$class: 'CleanCheckout'],
                            [$class: 'SubmoduleOption',
                            disableSubmodules: false,
                            parentCredentials: true,
                            recursiveSubmodules: true,
                            reference: '',
                            trackingSubmodules: false]
                            ],
                            submoduleCfg: [],
                            userRemoteConfigs: [
                                [
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
    }
    return scmVars
}

