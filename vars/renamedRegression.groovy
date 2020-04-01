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
            pollSCM('H/5 * * * *')
        }
        stages{
            stage('Checkout SCM') {
                steps {
                    script {
                        scmVars = scmSimpleCheckout(repo, branch)
                    }
                }
            }

            stage("Prepare build dirs"){
                steps{
                    script{
                        misc.readDir("${WORKSPACE}")
                        scenarios = misc.prepareBuildDir()
                    }
                    sh "ls -lat"
                    sh "ls -lat build" 
                }
            }

            stage('Push To VM') {
                steps {
                    script {
                        pushToVm()
                    }
                }
            }

            stage('Create stages ') {
                agent{
                    label "power"
                }
                steps {
                    script{
                        scenarios.each {
                            build_dir, conf ->           
                            stage("Run stage ${build_dir}"){
                                echo "${build_dir}"
                                echo "${conf}"
                                sh "[ -d ${build_dir} ] && echo OK || mkdir -p ${build_dir}"
                                sh "ls -lat ${WORKSPACE}"
                                sh "hostname"
                                sh "riscv64-unknown-elf-gcc -v"
                                sh "cd ${build_dir}; perl ${conf.launcher} --scenario ${conf.scenarioFile} "
                            }
                        }
                    }
                }
            }

            // stage("Analyze and Collect antifacts"){
            //     agent{
            //         label "power"
            //     }
            //     steps{
            //         echo "======== Generate failed.txt ========"
            //         script {
            //             if (fileExists('artifacts.zip')) {
            //                 sh "rm artifacts.zip"
            //             }
            //         }
            //         zip(
            //             archive: true,
            //             zipFile: 'artifacts.zip',
            //             dir: 'build',
            //             glob: '**/*results.txt, *failed.txt, **/*.xlsx, **/*.json, **/build.log'
            //         )
            //     }
            // }
            // stage('Download From Artifacts from VM') {
            //     steps {
            //         script {
            //             downloadArtifacts()
            //         }
            //     }
            // }
            // stage("Generate reports"){
            //     steps{
            //         echo "======== Generate reports with groovy scripts ========"
            //         script {
            //             def ts = new Date()
            //             def resultObject = reportlib.generateTextReport("${WORKSPACE}/build")
            //             env.BUILD_URL = "${BUILD_URL}"
            //             env.START_TIME = "${BUILD_TIMESTAMP}"
            //             env.BUILD_DATE = ts.format("yyyy-MM-dd", TimeZone.getTimeZone('Europe/Moscow'))
            //             env.COMPLETE_TIME = ts.format("EEE, MMMM dd, yyyy, HH:mm:ss '('zzz')'", TimeZone.getTimeZone('Europe/Moscow'))
            //             env.REPORT = resultObject["report"]
            //             env.TOTAL_TESTS = resultObject["total"]
            //             env.FAILED_TESTS = resultObject["failed"]
            //             writeFile(
            //                 file: "report.txt",
            //                 text: "${REPORT}" 
            //             )
            //             def HTML_REPORT = reportlib.generateHTMLreport("${WORKSPACE}/build")
            //             writeFile(
            //                 file: "report.html",
            //                 text: "${HTML_REPORT}" 
            //             )
            //             archiveArtifacts(
            //                 artifacts: 'artifacts.zip, report.txt, report.html',
            //                 fingerprint: true
            //             )
            //         }
            //     }
            // }

            // stage('Publish html') {
            //     steps {
            //         script {
            //             publishWWW()
            //         }
            //     }
            // }
        }

        // post{
        //     always {
        //         script{
        //             notificators.notifyGeneral(currentBuild.result)
        //             rtpublish()
        //         }
        //     }
        // }
    }
}