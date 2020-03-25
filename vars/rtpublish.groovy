def call(){
    currentBuild.result = "SUCCESS"
    String result = currentBuild.result?:"SUCCESS"
    String build_id = env.BUILD_ID?: "0"
    String job_base_name = env.JOB_BASE_NAME ?: "Base name"
    String build_url = env.BUILD_URL ?: "Build Url"
    String git_commit = env.GIT_COMMIT?: "no git commit"
    String git_url = env.GIT_URL ?: "no git url"
    String git_branch = env.GIT_BRANCH ?: "no git branch"
    String toolchain_id = env.TOOLCHAIN ?: "default toolchain"
    String stableText = ""
    stableText += "<br><img src='${build_url}badge/icon'>"
    stableText += "<br>Build URL: <font size='2' face='Helvetica' color='blue'><b>${build_url}</b></font>"
    stableText += "<br>Project URL: <font size='2' face='Helvetica' color='blue'><b>${git_url}</b></font>"
    stableText += "<br>Branch: <font size='2' face='Helvetica' color='blue'><b>${git_branch}</b></font>"
    stableText += "<br>Commit id: <font size='2' face='Helvetica' color='blue'><b>${git_commit}</b></font>"
    stableText += "<br>Toolchain used: <font size='2' face='Helvetica' color='blue'><b>${toolchain_id}</b></font></p>"
    //stableText += "<br><b>${job_base_name}</b>"
    //stableText += "<br>Job URL: <b>${build_url}</b>"
    rtp parserName: 'HTML', stableText: "${stableText}"
}