def BUILD_USER = currentBuild.rawBuild.getCause(Cause.UserIdCause).getUserId()
def jobName = currentBuild.fullDisplayName
FTP_DIR = new Date().format("yy_MM_dd_${BUILD_NUMBER}", TimeZone.getTimeZone('Europe/Moscow'))