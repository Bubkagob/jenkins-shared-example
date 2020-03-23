def call(){
    //currentBuild.result = "SUCCESS"
    rtp parserName: 'HTML', stableText: "<a href='http://localhost:8081/view/Test/job/REPORT/ws/RPA_RAPORT_ROBOT_217.xlsx'>RAPORT</a>"
    rtp parserName: 'HTML', stableText: "<a href='http://localhost:8081/view/Test/job/REPORT/ws/ROBOT_DATA_INPUT_217.xlsx'>DANE WEJSCIOWE</a>"

    rtp parserName: 'HTML', stableText: "<br><br>LOGS: "
    rtp parserName: 'HTML', stableText: currentBuild.result
    String basetRow = ""
    String stableText = ""
    
    stableText += "<br><br><b>ROW 1</b>"
    stableText += "<br><b>WINDOWS</b>"
    stableText += "<br><a href='http://localhost:8081/view/Test/job/REPORT/ws/Logowanie1/Logowanie1.html'>Logowanie</a>"
    stableText += "<br><a href='http://localhost:8081/view/Test/job/REPORT/ws/Weryfikacja1/Weryfikacja1.html'>Weryfikacja kontrahenta</a>"
    stableText += "<br><a href='http://localhost:8081/view/Test/job/REPORT/ws/Zmiany1/Zmiany1.html'>Zmiany na koncie</a>"
    stableText += "<br><b>LINUX</b>"
    stableText += "<br><a href='http://localhost:8081/view/Test/job/REPORT/ws/Logowanie1/Logowanie1.html'>Logowanie SAP</a>"
    stableText += "<br><a href='http://localhost:8081/view/Test/job/REPORT/ws/Weryfikacja1/Weryfikacja1.html'>Ksiegowanie</a>"
    stableText += "<br><a href='http://localhost:8081/view/Test/job/REPORT/ws/Zmiany1/Zmiany1.html'>Zapisanie</a>"
    
    stableText += "<br><br><b>ROW 2</b>"
    stableText += "<br><b>WINDOWS</b>"
    stableText += "<br><a href='http://localhost:8081/view/Test/job/REPORT/ws/Logowanie1/Logowanie1.html'>Logowanie</a>"
    stableText += "<br><a href='http://localhost:8081/view/Test/job/REPORT/ws/Weryfikacja1/Weryfikacja1.html'>Weryfikacja kontrahenta</a>"
    stableText += "<br><a href='http://localhost:8081/view/Test/job/REPORT/ws/Zmiany1/Zmiany1.html'>Zmiany na koncie</a>"
    stableText += "<br><b>LINUX</b>"
    stableText += "<br><a href='http://localhost:8081/view/Test/job/REPORT/ws/Logowanie1/Logowanie1.html'>Logowanie SAP</a>"
    stableText += "<br><a href='http://localhost:8081/view/Test/job/REPORT/ws/Weryfikacja1/Weryfikacja1.html'>Ksiegowanie</a>"
    stableText += "<br><a href='http://localhost:8081/view/Test/job/REPORT/ws/Zmiany1/Zmiany1.html'>Zapisanie</a>"
    
    stableText += "<br><br><b>ROW 3</b>"
    stableText += "<br><b>WINDOWS</b>"
    stableText += "<br><a href='http://localhost:8081/view/Test/job/REPORT/ws/Logowanie1/Logowanie1.html'>Logowanie</a>"
    stableText += "<br><a href='http://localhost:8081/view/Test/job/REPORT/ws/Weryfikacja1/Weryfikacja1.html'>Weryfikacja kontrahenta</a>"
    stableText += "<br><a href='http://localhost:8081/view/Test/job/REPORT/ws/Zmiany1/Zmiany1.html'>Zmiany na koncie</a>"
    stableText += "<br><b>LINUX</b>"
    stableText += "<br><a href='http://localhost:8081/view/Test/job/REPORT/ws/Logowanie1/Logowanie1.html'>Logowanie SAP</a>"
    stableText += "<br><a href='http://localhost:8081/view/Test/job/REPORT/ws/Weryfikacja1/Weryfikacja1.html'>Ksiegowanie</a>"
    stableText += "<br><a href='http://localhost:8081/view/Test/job/REPORT/ws/Zmiany1/Zmiany1.html'>Zapisanie</a>"
    

    rtp parserName: 'HTML', stableText: "${stableText}"
}