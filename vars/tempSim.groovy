#!/usr/bin/env groovy
def call(config) {
    String repo = config.repo
    String branch = config.branch
    env.TOOLCHAIN = config.toolchain
    pipeline {
        agent {
            label "beta"
        }
        environment {
            FTP_DIR = new Date().format("yy_MM_dd_${BUILD_NUMBER}", TimeZone.getTimeZone('Europe/Moscow'))
            
        }
        options {
            buildDiscarder(logRotator(numToKeepStr:'5'))
            disableConcurrentBuilds()
            timestamps()
        }
        triggers{
            pollSCM("H/5 * * * *")
        }
        stages{
            stage("Checkout SCM") {
                steps {
                    script {
                        scmSimpleCheckout(repo, branch)
                        
                    }
                }
            }
            // stage("Push To VM") {
            //     steps {
            //         script {
            //             pushToVm()
            //         }
            //     }
            // }

            stage("Build tests") {
                agent{
                    label "power"
                }
                steps {
                    script {
                        buildIVEtests(config)
                        buildIVEsimulator(config)
                        runIVEsimulator(config)
                    }
                }
            }

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
                        dir: 'encr/ive',
                        glob: '**/coverage/**, **/results*.txt, **/*results.txt, *failed.txt, **/*.xlsx, **/*.json, **/build.log'
                    )
                }
            }

            stage('Downloading') {
                steps {
                    script {
                        downloadArtifacts()
                        //publishWWW()
                    }
                }
            }

            stage("Generate reports"){
                steps{
                    script {
                        def ts = new Date()
                        def resultObject = reportlib.getIVEReport("${WORKSPACE}/encr/ive")
                        env.BUILD_URL = "${BUILD_URL}"
                        env.START_TIME = "${BUILD_TIMESTAMP}"
                        env.BUILD_DATE = ts.format("yyyy-MM-dd", TimeZone.getTimeZone('Europe/Moscow'))
                        env.COMPLETE_TIME = ts.format("EEE, MMMM dd, yyyy, HH:mm:ss '('zzz')'", TimeZone.getTimeZone('Europe/Moscow'))
                        env.REPORT = resultObject["report"]
                        env.TOTAL_TESTS = resultObject["total"]
                        env.FAILED_TESTS = resultObject["failed"]
                        writeFile(
                            file: "report.txt",
                            text: "${REPORT}" 
                        )
                        
                        archiveArtifacts(
                            artifacts: 'artifacts.zip, report.txt, report.html',
                            fingerprint: true
                        )
                    }
                }
            }
        }
        post{
            always {
                script{
                    //notificators.notifyGeneral(currentBuild.result)
                    rtpublish()
                }
            }
        }
    }
}