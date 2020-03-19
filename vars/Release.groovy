#!/usr/bin/env groovy
def call(currentBuild) {
    def repo = "https://github.com/ar-sc/scr1"
    //def branch = "development_ia"
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
            choice(
                name: "branch",
                choices: ['development', 'development_ia', 'development_is'],
                description: 'Select branch'
            )
        }
        stages{
            stage('Checkout SCM') {
                steps {
                    script {
                        scmVars = scmCheckout(repo, branch)
                    }
                }
            }
            // stage('My Stage') {
            //     steps {
            //         script {
            //             def GIT_TAGS = sh (script: 'git tag -l', returnStdout:true).trim()
            //             inputResult = input(
            //                 message: "Select a git tag",
            //                 parameters: [choice(name: "git_tag", choices: "${GIT_TAGS}", description: "Git tag")]
            //             )
            //         }
            //     }
            // }
            // stage('Parallel In Sequential') {
            //     parallel {
            //         stage('In Parallel 1') {
            //             steps {
            //                 echo "In Parallel 1"
            //                 echo "${FTP_DIR}"
            //             }
            //         }
            //         stage('In Parallel 2') {
            //             steps {
            //                 echo "In Parallel 2"
            //                 echo "${JOB_NAME}"
            //             }
            //         }
            //         stage('In Parallel 3') {
            //             steps {
            //                 echo "In Parallel 3"
            //                 echo "${BUILD_USER}"
            //             }
            //         }
            //         stage('In Parallel 4') {
            //             steps {
            //                 echo "In Parallel 4"
            //                 echo "${BUILD_NUMBER}"
            //                 script{
            //                     scmVars = scmCheckout(repo, branch)
            //                 }
                            
            //             }
            //         }
            //     }
            // }
            stage('Push To VM') {
                steps {
                    script {
                        pushToVm()
                    }
                }
            }
            
            stage("Check variables"){
                steps {
                    echo "scmVars ${scmVars} "
                    
                    script{
                        def paramValue = "development_ia"
                        if (!branch=="development_ia")
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