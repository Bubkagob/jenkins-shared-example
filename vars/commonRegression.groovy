#!/usr/bin/env groovy
def call(currentBuild, scenarios) {
    def repo = "https://github.com/ar-sc/scr1"
    def branch = "development_ia"
    def choices = scenarios.join(',')
    pipeline {
        agent {
            label "beta"
        }
        environment {
            BUILD_USER = currentBuild.rawBuild.getCause(Cause.UserIdCause).getUserId()
            FTP_DIR = new Date().format("yy_MM_dd_${BUILD_NUMBER}", TimeZone.getTimeZone('Europe/Moscow'))
        }
        options {
            buildDiscarder(logRotator(numToKeepStr:'5'))
            disableConcurrentBuilds()
            timestamps()
        }
        triggers{
            pollSCM('H/50 * * * *')
        }
        parameters {
            // string(
            //     defaultValue: '',
            //     description: '',
            //     name: 'GIT_REPO_URL'
            //     )
            // string(
            //     defaultValue: '',
            //     description: '',
            //     name: 'COMMIT_ID'
            //     )
            // choice(
            //     name: "branch",
            //     choices: ['development', 'development_ia', 'development_is'],
            //     description: 'Select branch'
            // )

            extendedChoice(
                defaultValue: '', 
                description: 'Choose scenarios to run', 
                multiSelectDelimiter: ',', 
                name: 'CHOSEN_SCENARIOS', 
                quoteValue: false, 
                saveJSONParameterToFile: false, 
                type: 'PT_CHECKBOX', 
                value: choices, 
                visibleItemCount: 10
            )
        }
        stages{
            stage('Checkout SCM') {
                steps {
                    script {
                        scmVars = scmCheckout(repo, branch)
                        echo "${CHOSEN_SCENARIOS}"
                    }
                }
            }

            stage('Push To VM') {
                steps {
                    script {
                        pushToVm()
                    }
                }
            }

            stage('Make stages') {
                agent{
                    label "power"
                }
                steps {
                    script {
                        scenarios.each{
                            scenario_name -> 
                                stage("Run scenario ${scenario_name}"){
                                    echo "${scenario_name}"
                                    echo "========Run ${scenario_name}========"
                                    sh '''
                                    #!/bin/bash -l
                                    [ -d build ] && echo OK || mkdir -p build
                                    cd build
                                    echo ${scenario_name}
                                    perl ../tests/common/framework/launcher/launch.pl --scenario ../tests/_scenarios/${scenario_name}
                                    '''
                                }
                        }
                    }
                }
            }
            
            stage("Check variables"){
                steps {
                    echo "scmVars ${scmVars} "
                    
                    script{
                        def paramValue = "development_ia"
                        echo "${branch}"
                        if (branch != "development_ia")
                        {
                            build(
                                job: 'new_release',
                                parameters: [
                                    [
                                        $class: 'StringParameterValue',
                                        name: 'branch',
                                        value: paramValue
                                    ],
                                ]
                            )
                        }
                    }
                    
                }
            }
        }
    }
}