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

            stage('Push To VM') {
                steps {
                    script {
                        pushToVm()
                    }
                }
            }
        }
    }
}