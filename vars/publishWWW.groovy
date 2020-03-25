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
            for (founded in findFiles(glob: "**/*dashboadrd.html")){
                def dirName = founded.path.minus(founded.name)
                env.coverageScore = sh(script: "cat ${dirName}dashboard.html |  grep 's8 cl rt' |  grep -o '[0-9][0-9].[0-99][0-9]' | head -1", returnStdout: true).trim()
                publishHTML(
                    target : [
                        allowMissing: false,
                        alwaysLinkToLastBuild: false,
                        keepAll: true,
                        reportDir: "${dirName}",
                        reportFiles: '',
                        reportName: 'Regression Coverage Report',
                        reportTitles: 'The Report'
                    ]
                )
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
            
            
        } 
    }
}