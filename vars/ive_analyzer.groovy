import static groovy.io.FileType.FILES
class ReportResult {
     String failed
     String total
     String report
}

def getIVEReport(){
    def testAlignFormat = "| %-30s|%60s\t|%n";
    def resultString = ""
    resultString = resultString.concat("+"+"-"*95+"+\n")
    resultString += String.format(testAlignFormat, "", "Failed tests")
    resultString = resultString.concat("+"+"-"*95+"+\n")
    def passed = 0
    def failed = 0

    new File(".").eachFileRecurse(FILES) {
        if(it.name.matches("results.*")) {
            resultString += String.format(testAlignFormat, it.name, "");
            println it.name
            int passed_counter = 0
            int failed_counter = 0

            it.eachLine { line ->
                if (line.contains("PASSED")){
                    passed_counter += 1
                }
                else if (line.contains("FAILED")){
                    failed_counter += 1
                    String result = line.substring(line.indexOf("/") + 1, line.indexOf("*"));
                    resultString += String.format(testAlignFormat, "", result.trim())
                }
                
            }
            passed += passed_counter
            failed += failed_counter
            resultString += String.format(testAlignFormat, "Passed:", passed_counter);
            resultString += String.format(testAlignFormat, "Failed:", failed_counter);
            resultString = resultString.concat("+"+"-"*95+"+\n")
        }
    }
    resultString += String.format(testAlignFormat, "Total:", passed + failed);
    resultString += String.format(testAlignFormat, "Total Passed:", passed);
    resultString += String.format(testAlignFormat, "Total Failed:", failed);
    resultString = resultString.concat("+"+"-"*95+"+\n")
    println resultString
}

getIVEReport()