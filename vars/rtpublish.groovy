def call(currentBuild){
    String stableText = "Hello"

    rtp parserName: 'HTML', stableText: "${stableText}"
}