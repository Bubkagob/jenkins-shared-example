def call(){
    stage('Push Project to agent VM') {
        steps {
            echo "======== PUSH to 192.168.1.110 ========"
            sshPublisher(
                publishers: [
                    sshPublisherDesc(
                    configName: 'SSHPUB110',
                    transfers: [
                        sshTransfer(
                            cleanRemote: true,
                            excludes: '',
                            execCommand: '',
                            execTimeout: 120000,
                            flatten: false,
                            makeEmptyDirs: false,
                            noDefaultExcludes: false,
                            patternSeparator: '[, ]+',
                            remoteDirectory: "${JOB_NAME}",
                            remoteDirectorySDF: false,
                            removePrefix: '',
                            sourceFiles: '**'
                        )
                    ],
                    usePromotionTimestamp: false,
                    useWorkspaceInPromotion: false,
                    verbose: false
                    )
                ]
            )
        }
    }
}