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
            }
        
    }
}