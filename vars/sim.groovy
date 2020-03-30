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
            stage('Checkout SCM') {
                steps {
                    script {
                        scmSimpleCheckout(repo, branch)
                    }
                }
            }
            pushToVm()
            // stage('Push To VM') {
            //     steps {
            //         script {
            //             pushToVm()
            //         }
            //     }
            // }
        }
    }
}