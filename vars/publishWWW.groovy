def call(){
    stage("Publish html"){
        script {
            if (fileExists('report.html')) {
                publishHTML(
                    target : [allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: '',
                    reportFiles: 'report.html',
                    reportName: 'Regression Report',
                    reportTitles: 'The Report']
                )
            }
            fileList = findFiles(glob: 'index.html')
            fileDir = fileList[0].path.minus(fileList[0].name)
            echo "Here is Dir"
            echo fileDir
            
            publishHTML(
                target : [
                    allowMissing: false,
                    alwaysLinkToLastBuild: false,
                    keepAll: true,
                    reportDir: "build/axi_rvimc/coverage",
                    reportFiles: '',
                    reportName: 'Regression Coverage Report',
                    reportTitles: 'The Report'
                ]
            )
        } 
    }
}