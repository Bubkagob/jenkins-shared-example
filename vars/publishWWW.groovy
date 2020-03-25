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
            for (f in findFiles(glob: "**/*dashboard.html")){
               echo f.path 
            }
            // fileList = findFiles(glob: '**/*dashboard.html')
            // fileList.each{
            //     founded->
            //     coverageDir = founded.path.minus(founded.name).toString()
            //     echo "Here is Dir"
            //     echo founded
            // }
            // fileDir = fileList[0].path.minus(fileList[0].name)
            // echo "Here is Dir"
            // echo fileDir
            
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