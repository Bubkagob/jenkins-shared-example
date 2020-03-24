def notifyGeneral(String buildStatus = 'STARTED') {
  // build status of null means successful
  buildStatus =  buildStatus ?: 'SUCCESS'
  def mailRecipients = "ivan.alexandrov@syntacore.com"
  // Default values
  def colorName = 'RED'
  def colorCode = '#FF0000'
  def subject = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
  def summary = "${subject} (${env.BUILD_URL})"
  def details = """<p>STARTED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
    <p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>&QUOT;</p>"""

  // Override default values based on build status
  if (buildStatus == 'STARTED') {
    color = 'YELLOW'
    colorCode = '#FFFF00'
  } else if (buildStatus == 'SUCCESS') {
    color = 'GREEN'
    colorCode = '#00FF00'
  } else {
    color = 'RED'
    colorCode = '#FF0000'
  }

  // Send notifications
    slackSend(
        channel: "#ci",
        color: colorCode,
        message: summary
    )

    emailext(
      attachmentsPattern: "report.txt, report.html",
      attachLog: true,
      compressLog: true,
      body: '''${SCRIPT, template="regression.template"}''',
      mimeType: 'text/html',
      subject: "${currentBuild.fullDisplayName} ${currentBuild.durationString.minus(' and counting')} ${currentBuild.currentResult}",
      to: "${mailRecipients}",
      replyTo: "${mailRecipients}"
                    //recipientProviders: [[$class: 'DevelopersRecipientProvider']]
                    //recipientProviders: [[$class: 'CulpritsRecipientProvider'], [$class: 'RequesterRecipientProvider']],
                    //recipientProviders: [[$class: 'CulpritsRecipientProvider']]
    )

  //emailext(
  //    subject: subject,
  //    body: details,
  //    recipientProviders: [[$class: 'DevelopersRecipientProvider']]
  //  )
}
