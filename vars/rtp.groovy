def call(){
    node {
        String text = "<h2>Regression test</h2><a href=>Hello</a>"
        rtp(
            nullAction: '1',
            parserName: 'HTML',
            stableText: text,
            abortedAsStable: true,
            failedAsStable: true,
            unstableAsStable: true
        )
    }
}