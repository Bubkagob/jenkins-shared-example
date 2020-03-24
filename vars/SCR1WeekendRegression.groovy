#!/usr/bin/env groovy
def call(currentBuild, repo, branch, mailRecipients) {
    pipeline {
        agent {
            label "beta"
        }
        environment {
            BUILD_USER = currentBuild.rawBuild.getCause(Cause.UserIdCause).getUserId()
            FTP_DIR = new Date().format("yy_MM_dd_${BUILD_NUMBER}", TimeZone.getTimeZone('Europe/Moscow'))
        }
        options {
            buildDiscarder(
                logRotator(
                    artifactDaysToKeepStr: '',
                    artifactNumToKeepStr: '',
                    daysToKeepStr: '10',
                    numToKeepStr: '20'
                )
                )
            disableConcurrentBuilds()
            timestamps()
        }
        triggers{
            cron('H 19 * * 6-7')
        }
        stages{
            stage('Checkout SCM') {
                steps {
                    script {
                        scmVars = scmCheckout(repo, branch)
                    }
                }
            }

            // stage('Push To VM') {
            //     steps {
            //         script {
            //             pushToVm()
            //         }
            //     }
            // }

            // stage("Run single test"){
            //     agent{
            //         label "power"
            //     }
            //     steps{
            //         echo "========Run DRY test========" 
            //         sh '''
            //             #!/bin/bash -l
            //             hostname
            //             ls -la
            //             cd scripts
            //             chmod +x run_all_configs.sh
            //             sed -i -- 's/axi ahb/axi/g' run_all_configs.sh
            //             sed -i -- 's/rvimc rvimc_min rvic rvec/rvimc/g' run_all_configs.sh
            //             sed '35,50 {s/^/#/}' ../tests/_scenarios/scr1/regression_*
            //             ls -la ../tests/_scenarios/scr1/
            //             sed -i -- 's/mode: cli/mode: coverage/g' ../tests/_scenarios/scr1/regression_*
            //             #cat ../tests/common/tools/reporters/analyzer.py 
            //             ./run_all_configs.sh
            //         '''
            //     }
            // }
            // stage("Run ALL CONFIGS"){
            //     agent{
            //         label "power"
            //     }
            //     steps{
            //         echo "========Run All Configs ========" 
            //         sh '''
            //             #!/bin/bash -l
            //             cd scripts
            //             sed -i -- 's/axi/axi ahb/g' run_all_configs.sh
            //             sed -i -- 's/rvimc/rvimc rvimc_min rvic rvec/g' run_all_configs.sh
            //             sed '35,50 {s/^//}' ../tests/_scenarios/scr1/regression_*
            //             #./run_all_configs.sh
            //         '''
            //     }
            // }
            stage("Analyze and Collect antifacts"){
                agent{
                    label "power"
                }
                steps{
                    echo "======== Generate failed.txt ========"
                    script {
                        if (fileExists('artifacts.zip')) {
                            sh "rm artifacts.zip"
                        }
                    }
                    zip(
                        archive: true,
                        zipFile: 'artifacts.zip',
                        dir: 'build',
                        glob: '**/coverage/**, **/*results.txt, *failed.txt, **/*.xlsx, **/*.json, **/build.log'
                    )
                }
            }
            stage('Analyse/Collect') {
                steps {
                    script {
                        downloadArtifacts()
                        publishWWW()
                    }
                }
            }
        }
    }
}