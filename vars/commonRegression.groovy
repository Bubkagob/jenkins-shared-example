#!/usr/bin/env groovy
def call(currentBuild, scenarios, repo, branch) {
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
            extendedChoice(
                defaultValue: '', 
                description: 'Choose scenarios to run', 
                multiSelectDelimiter: ',', 
                name: 'CHOSEN_SCENARIOS', 
                quoteValue: false, 
                saveJSONParameterToFile: false, 
                type: 'PT_CHECKBOX', 
    def repo = "https://github.com/ar-sc/sc_riscv"
    def branch = "scr3_release_niime"
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
                                    sh "[ -d build ] && echo OK || mkdir -p build"
                                    sh "cd build; perl ../tests/common/framework/launcher/launch.pl --scenario ../tests/_scenarios/${scenario_name}"
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