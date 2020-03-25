#!/usr/bin/env groovy
def call(currentBuild, scenarios, repo, branch, mailRecipients) {
    def choices = scenarios.join(',')
    pipeline {
        agent {
            label "beta"
        }
        environment {
            //BUILD_USER = currentBuild.rawBuild.getCause(Cause.UserIdCause).getUserId()
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
                value: choices, 
                visibleItemCount: 10
            )
        }
        stages{
            stage('Checkout SCM') {
                steps {
                    script {
                        scmVars = scmSimpleCheckout(repo, branch)
                        sh "sed -i 's+https://+ssh://git@+g' .gitmodules"
                        sh "git submodule sync"
                        sh "git submodule update --init tests" 
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
                                    sh "sed -i -- 's/mode: cli/mode: coverage/g' tests/_scenarios/${scenario_name}"
                                    sh "cd build; perl ../tests/common/framework/launcher/launch.pl --scenario ../tests/_scenarios/${scenario_name}"
                                }
                        }
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
                        dir: 'build',
                        glob: '**/*results.txt, *failed.txt, **/*.xlsx, **/*.json, **/build.log'
                    )
                }
            }
            stage('Download From Artifacts from VM') {
                steps {
                    script {
                        downloadArtifacts()
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
                        env.TOTAL_TESTS = resultObject["total"]
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

            stage('Publish html') {
                steps {
                    script {
                        publishWWW()
                    }
                }
            }
            
            // stage("Check variables"){
            //     steps {
            //         echo "scmVars ${scmVars} "
                    
            //         script{
            //             def paramValue = "development_ia"
            //             echo "${branch}"
            //             if (branch != "development_ia")
            //             {
            //                 build(
            //                     job: 'new_release',
            //                     parameters: [
            //                         [
            //                             $class: 'StringParameterValue',
            //                             name: 'branch',
            //                             value: paramValue
            //                         ],
            //                     ]
            //                 )
            //             }
            //         } 
            //     }
            // }
        }

        post{
            always {
                script{
                    notificators.notifyGeneral(currentBuild.result)
                    rtpublish()
                }
            }
        }
    }
}