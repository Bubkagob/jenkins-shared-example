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