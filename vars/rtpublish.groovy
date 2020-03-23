def call(){
    currentBuild.result = "SUCCESS"
    String result = currentBuild.result?:"SUCCESS"
    String build_id = env.BUILD_ID?: "0"
    String job_base_name = env.JOB_BASE_NAME ?: "Base name"
    String build_url = env.BUILD_URL ?: "Build Url"
    String git_branch = env.scmVars.GIT_BRANCH?: "Cannot find branch name"
    String git_commit = env.scmVars.GIT_COMMIT?: "Cannot find git_commit name"
    //String scmVars = env.scmVars?: "no scm vars"
    
    
    

    // rtp parserName: 'HTML', stableText: "<a href='http://localhost:8081/view/Test/job/REPORT/ws/RPA_RAPORT_ROBOT_217.xlsx'>RAPORT</a>"
    // rtp parserName: 'HTML', stableText: "<a href='http://localhost:8081/view/Test/job/REPORT/ws/ROBOT_DATA_INPUT_217.xlsx'>DANE WEJSCIOWE</a>"


    // rtp parserName: 'HTML', stableText: currentBuild.result
    // rtp parserName: 'HTML', stableText: "${build_id}" 
   
    //String basetRow = ""
    String stableText = ""
    
    stableText += "<br><b>${env.scmVars}</b>"
    stableText += "<br><b>${build_id}</b>"
    stableText += "<br><b>${git_branch}</b>"
    stableText += "<br><b>${git_commit}</b>"
    stableText += "<br><b>${job_base_name}</b>"
    stableText += "<br><b>${build_url}</b>"
    stableText += "<br><a href='http://localhost:8081/view/Test/job/REPORT/ws/Logowanie1/Logowanie1.html'>Logowanie</a>"
    stableText += "<br><a href='http://localhost:8081/view/Test/job/REPORT/ws/Weryfikacja1/Weryfikacja1.html'>Weryfikacja kontrahenta</a>"
    stableText += "<br><a href='http://localhost:8081/view/Test/job/REPORT/ws/Zmiany1/Zmiany1.html'>Zmiany na koncie</a>"
    stableText += "<br><b>LINUX</b>"
    stableText += "<br><a href='http://localhost:8081/view/Test/job/REPORT/ws/Logowanie1/Logowanie1.html'>Logowanie SAP</a>"
    stableText += "<br><a href='http://localhost:8081/view/Test/job/REPORT/ws/Weryfikacja1/Weryfikacja1.html'>Ksiegowanie</a>"
    stableText += "<br><a href='http://localhost:8081/view/Test/job/REPORT/ws/Zmiany1/Zmiany1.html'>Zapisanie</a>"


    rtp parserName: 'HTML', stableText: "${stableText}"
}