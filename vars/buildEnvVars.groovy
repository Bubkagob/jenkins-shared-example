@NonCPS
def call() {
    def BUILD_USER = currentBuild.rawBuild.getCause(Cause.UserIdCause).getUserId()
    JOB_NAME = currentBuild.fullDisplayName
    //env.FTP_DIR = new Date().format("yy_MM_dd_${BUILD_NUMBER}", TimeZone.getTimeZone('Europe/Moscow'))
}

