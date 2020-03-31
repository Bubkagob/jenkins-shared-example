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
            stage("Push To VM") {
                steps {
                    script {
                        pushToVm()
                    }
                }
            }

            stage("Build tests") {
                agent{
                    label "power"
                }
                steps {
                    script {
                        buildIVEtests(config)
                    }
                }
            }

            stage("Build simulator") {
                agent{
                    label "power"
                }
                steps {
                    script {
                        buildIVEsimulator(config)
                    }
                }
            }

            stage("Run simulation") {
                parallel {
                agent{
                    label "power"
                }
                steps {
                    script {
                        runIVEsimulator(config)
                    }
                }
            }
            }

        }
    }
}