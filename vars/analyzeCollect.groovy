def call(){
    stage("Analyze and Collect artifacts"){
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
    stage("Download artifacts from 110VM"){
            sh "scp -o StrictHostKeyChecking=no -P 64013 physdesign@192.168.1.110:/home/work/jenkins/workspace/${JOB_NAME}/artifacts.zip artifacts.zip"
            unzip(
                zipFile: 'artifacts.zip',
                dir: 'build'
            )
        
    }
}