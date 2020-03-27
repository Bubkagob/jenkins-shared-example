import static groovy.io.FileType.FILES
import groovy.json.JsonSlurper
import groovy.json.JsonParserType
import groovy.json.JsonOutput

class ReportResult {
     String failed
     String total
     String report
}

@NonCPS
def mergeJSON(pair){
    def jsonSlurper = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY)
    def bArr = jsonSlurper.parseText(pair[0])
    def rArr = jsonSlurper.parseText(pair[1])
    return bArr + rArr
}

@NonCPS
def getSummaryMap(build_dir){
    def jsonSlurper = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY)
    def resultMap = [:]
    new File(build_dir).eachFileRecurse(FILES) {
        if(it.name.endsWith('results.json')) {
            dir = it.getParentFile()
            def jsonFiles = new FileNameFinder().getFileNames("${dir}", '*.json')
            if (jsonFiles.size() != 2) {println "Bad"}
            def list = []
            jsonFiles.each{list.add(JsonOutput.toJson(jsonSlurper.parse(new File("${it}"))))}
            def mergedJson = mergeJSON(list) 
            def key = mergedJson.name + mergedJson._index
            if(!resultMap.containsKey(key)) resultMap.put(key, [])
            resultMap[key].add(mergedJson)
        }
    }
    return resultMap
}

@NonCPS
def getFailedReport(build_dir){
    def resultMap = getSummaryMap(build_dir)
    def failList = []
    def totalCounter = 0
    def failedReportMap = [:]
    resultMap.each{ configName, configObject ->
        def tempList = []
        configObject.tests.findResults{it -> it.each{
                test ->
                totalCounter += 1
                if(!test.passed) {
                    tempList.add(test.test_name)
                    failList.add(new Tuple2(configName, test.test_name))
                }
            }  
        }
        if(!failedReportMap.containsKey(configName)) failedReportMap.put(configName, tempList)
    }
    return [failedReportMap, totalCounter]
}

def generateTextReport(build_dir){
    def failedReport = getFailedReport(build_dir)
    def failedMap = failedReport[0]
    def total = failedReport[1]
    def total_failed = 0
    def testAlignFormat = "| %-30s|%60s\t|%n";
    def resultString = ""
    resultString = resultString.concat("+"+"-"*95+"+\n")
    resultString += String.format(testAlignFormat, "CONFIG", "Failed tests")
    resultString = resultString.concat("+"+"-"*95+"+\n")
    failedMap.each{
        configName, failedList ->
        resultString += String.format(testAlignFormat, configName, "");
        failedList.each{ test_name ->
            total_failed += 1
            resultString += String.format(testAlignFormat, "", test_name)
            //System.out.format(testAlignFormat, "", test_name);
        }
        resultString = resultString.concat("+"+"-"*95+"+\n")
    }
    return new ReportResult(failed: total_failed, total: total, report: resultString)
}


def getIVEReport(build_dir){
    echo build_dir
    // for (founded in findFiles(glob: "**/results*.txt")) {
    //     founded.each{
    //         resultFile ->
    //         println resultFile.path
    //     }
    // }
    def testAlignFormat = "| %-30s|%60s\t|%n";
    def resultString = ""
    resultString = resultString.concat("+"+"-"*95+"+\n")
    resultString += String.format(testAlignFormat, "", "Failed tests")
    resultString = resultString.concat("+"+"-"*95+"+\n")
    def passed = 0
    def failed = 0

    // new File(".").eachFileRecurse(FILES) {
    //     if(it.name.matches("results_*.txt")) {
    for (founded in findFiles(glob: "**/results*.txt")) {
        //founded.each{
        File resFile = new File ("${founded}")
        echo resFile.name
        resultString += String.format(testAlignFormat, resFile.name, "");
        println resFile.name
        int passed_counter = 0
        int failed_counter = 0
        final String content = readFile(file: "${founded}")
        final List myKeys = extractLines(content)
        myKeys.each {
            String line -> 
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
        //}
    }
    resultString += String.format(testAlignFormat, "Total:", passed + failed);
    resultString += String.format(testAlignFormat, "Total Passed:", passed);
    resultString += String.format(testAlignFormat, "Total Failed:", failed);
    resultString = resultString.concat("+"+"-"*95+"+\n")
    int total = passed+failed
    return new ReportResult(failed: failed, total: total, report: resultString)
}

@NonCPS
def generateHTMLreport(build_dir){
    def resultString = ""
    def failedReport = getFailedReport(build_dir)
    def failedMap = failedReport[0]
    def total = failedReport[1]
    def json = JsonOutput.toJson(failedMap)
    def InputJSON = new JsonSlurper().parseText(json)
    def writer = new StringWriter()  // html is written here by markup builder
    def markup = new groovy.xml.MarkupBuilder(writer)  // the builder

    markup.html{
      
            markup.style(
                type:"text/css", '''
                .header, .first, .row {
                    border: 1px solid;
                    margin: 30px;
                    padding: 10px;
                }
                .header {
                    color: white;
                    font-size: 18pt;
                    background-color: #aec4c7
                }
                .first {
                    background-color:  #ffffff
                }
                .row {
                    text-align:right;
                    background-color:  #e0f7fa
                }
            '''
            )
        
        markup.table(style: 'border:2px solid;padding: 2px;text-align:center;style: "border-collapse:collapse;"'){
            markup.tbody{
            def count = 0
            def fcount = 0
            markup.tr{
                markup.th(title:"Field #1", class:"header", "Config name")
                markup.th(title:"Field #2", class:"header", "Failed tests")
            } // tr
            markup.tr{
                failedMap.each {
                conf, flist ->
                    markup.tr {
                        markup.th(title:"Field #1", 'class':'first', conf)
                        markup.td(title:"Field #2", 'class':'row',{  
                            markup.ul{
                                fcount = 0
                                flist.each{
                                    test_name ->
                                        fcount += 1
                                        markup.li(align:"right", test_name)
                                    }
                                }
                            }
                        )
                    }
                    def tot_failed = fcount
                    count += tot_failed
                    markup.tr{
                        markup.th(title:"Field #1", 'class':'row', "Per config")
                        markup.td(title:"Field #2", 'class':'row', "${tot_failed}")
                    }
                }
            } // tr
            markup.tr{
                    markup.th(title:"Field #1", 'class':'header', "Passed")
                    markup.td(title:"Field #2", 'class':'header', "${total - count} / ${total}")
                }
            markup.tr{
                    markup.th(title:"Field #1", 'class':'header', "Failed")
                    markup.td(title:"Field #2", 'class':'header', "${count} / ${total}")
                }
            }
        }
    }
    resultString = resultString.concat(writer.toString())
    return resultString
}


