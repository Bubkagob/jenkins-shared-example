def call(){
    currentBuild.result = "SUCCESS"
    String result = currentBuild.result?:"SUCCESS"
    String build_id = env.BUILD_ID?: "0"
    String job_base_name = env.JOB_BASE_NAME ?: "Base name"
    String build_url = env.BUILD_URL ?: "Build Url"
    String git_commit = env.GIT_COMMIT?: "no git commit"
    String git_url = env.GIT_URL ?: "no git url"
    String git_branch = env.GIT_BRANCH ?: "no git branch"
    String stableText = ""
    
    stableText += "<br>Project URL: <b>${git_url}</b>"
    stableText += "<br>Branch: <b>${git_branch}</b>"
    stableText += "<br>Commit id: <b>${git_commit}</b>"
    //stableText += "<br><b>${job_base_name}</b>"
    //stableText += "<br>Job URL: <b>${build_url}</b>"
    
    rtp parserName: 'HTML', stableText: "${stableText}"
}