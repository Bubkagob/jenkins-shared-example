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
                   wrap([$class: 'BuildUser']) {
                       echo "BUILD_USER=${BUILD_USER}"
                       echo "BUILD_USER_FIRST_NAME=${BUILD_USER_FIRST_NAME}"
                       //echo "BUILD_USER_LAST_NAME=${BUILD_USER_LAST_NAME}"
                       echo "BUILD_USER_ID=${BUILD_USER_ID}"
                       echo "BUILD_USER_EMAIL=${BUILD_USER_EMAIL}"
                       echo "---"
                       echo "env.BUILD_USER=${env.BUILD_USER}"
                       echo "env.BUILD_USER_FIRST_NAME=${env.BUILD_USER_FIRST_NAME}"
                       echo "env.BUILD_USER_LAST_NAME=${env.BUILD_USER_LAST_NAME}"
                       echo "env.BUILD_USER_ID=${env.BUILD_USER_ID}"
                       echo "env.BUILD_USER_EMAIL=${env.BUILD_USER_EMAIL}"
                   }

                        scmVars = scmCheckout(repo, branch)
                    }

                    // script {
                    //     def user = env.BUILD_USER_ID
                    //     echo "${BUILD_USER}"
                    //     echo "${user}"
                    //     scmVars = scmCheckout(repo, branch)
                    // }
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
            stage("Generate reports"){
                steps{
                    echo "======== Generate reports with groovy scripts ========"
                    script {
                        def ts = new Date()
                        def resultObject = reportlib.generateTextReport("${WORKSPACE}/build")
                        env.BUILD_URL = "${BUILD_URL}"
                        env.START_TIME = "${BUILD_TIMESTAMP}"
                        env.BUILD_DATE = ts.format("yyyy-MM-dd", TimeZone.getTimeZone('Europe/Moscow'))
                        env.COMPLETE_TIME = ts.format("EEE, MMMM dd, yyyy, HH:mm:ss '('zzz')'", TimeZone.getTimeZone('Europe/Moscow'))
                        env.REPORT = resultObject["report"]
                        //env.TOTAL_TESTS = resultObject["total"]
                        env.FAILED_TESTS = resultObject["failed"]
                        writeFile(
                            file: "report.txt",
                            text: "${REPORT}" 
                        )
                        def HTML_REPORT = reportlib.generateHTMLreport("${WORKSPACE}/build")
                        writeFile(
                            file: "report.html",
                            text: "${HTML_REPORT}" 
                        )
                        archiveArtifacts(
                            artifacts: 'artifacts.zip, report.txt, report.html',
                            fingerprint: true
                        )
                    }
                }
            }
        }
        post {
            always {
                // slackSend(
                //     channel: "#ci",
                //     color: COLOR_MAP[currentBuild.currentResult],
                //     message: "*${currentBuild.currentResult}:* Job ${env.JOB_NAME} build ${env.BUILD_NUMBER} in time ${currentBuild.durationString.minus(' and counting')}\nMore info at: ${env.BUILD_URL}\n${REPORT}\n"
                // )
                script{
                    notificators.notifyGeneral(currentBuild.result)
                    rtpublish()
                }
                // emailext(
                //     attachmentsPattern: "report.txt, report.html",
                //     attachLog: true,
                //     compressLog: true,
                //     body: '''${SCRIPT, template="regression.template"}''',
                //     mimeType: 'text/html',
                //     subject: "${currentBuild.fullDisplayName} ${currentBuild.durationString.minus(' and counting')} ${currentBuild.currentResult}",
                //     to: "${mailRecipients}",
                //     replyTo: "${mailRecipients}"
                //     //recipientProviders: [[$class: 'DevelopersRecipientProvider']]
                //     //recipientProviders: [[$class: 'CulpritsRecipientProvider'], [$class: 'RequesterRecipientProvider']],
                //     //recipientProviders: [[$class: 'CulpritsRecipientProvider']]
                // )
            }
        }
    }
}