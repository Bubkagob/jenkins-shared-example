def call() {
    JOB_NAME = currentBuild.fullDisplayName
    //env.FTP_DIR = new Date().format("yy_MM_dd_${BUILD_NUMBER}", TimeZone.getTimeZone('Europe/Moscow'))
}

