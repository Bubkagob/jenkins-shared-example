import static groovy.io.FileType.FILES
import groovy.json.JsonOutput
import groovy.json.JsonParserType
import groovy.json.JsonSlurper

def call(build_dir){
    def jsonSlurper = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY)
    def resultMap = [:]
    for (f in findFiles(glob: "**/*.json")){
        //echo "${f.path}"
        if(f.name.endsWith('results.json')) {
            //echo "${f.name}"
            File file = new File(f.path)
            dir = file.getParentFile().absolutePath.substring(1)
            echo dir
            
            def jsonFiles = sh(returnStdout: true, script: "find ${dir} -name '*.json'").split("\n").collect{ it.trim().replace("*", "")}
            
            // def jsonFiles = new FileNameFinder().getFileNames("${dir}", '*.json')
            if (jsonFiles.size() != 2) {println "Bad"}
            def list = []
            jsonFiles.each{list.add(JsonOutput.toJson(jsonSlurper.parse(new File(f.path))))}
            // // //def mergedJson = mergeJSON(list)
            def mergedJson = jsonSlurper.parseText(list[0]) + jsonSlurper.parseText(list[1])
            def key = mergedJson.name + mergedJson._index
            if(!resultMap.containsKey(key)) resultMap.put(key, [])
            resultMap[key].add(mergedJson)
        }
    }
    // def jsonSlurper = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY)
    
   
    // new File(build_dir).eachFileRecurse(FILES) {
    //     if(it.name.endsWith('results.json')) {
    //         dir = it.getParentFile()
    //         def jsonFiles = new FileNameFinder().getFileNames("${dir}", '*.json')
    //         if (jsonFiles.size() != 2) {println "Bad"}
    //         def list = []
    //         jsonFiles.each{list.add(JsonOutput.toJson(jsonSlurper.parse(new File("${it}"))))}
    //         //def mergedJson = mergeJSON(list)
    //         def mergedJson = jsonSlurper.parseText(list[0]) + jsonSlurper.parseText(list[1])
    //         def key = mergedJson.name + mergedJson._index
    //         if(!resultMap.containsKey(key)) resultMap.put(key, [])
    //         resultMap[key].add(mergedJson)
    //     }
    // }
    // return resultMap
    // =========================================== resultMap here
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
    // return [failedReportMap, totalCounter]
    // failedReportMap, totalCounter here
    // ==============================================

    int total_failed = 0
    String resultString = ""
    String testAlignFormat = "| %-30s|%60s\t|%n";
    resultString = resultString.concat("+"+"-"*95+"+\n")
    resultString += String.format(testAlignFormat, "CONFIG", "Failed tests")
    resultString = resultString.concat("+"+"-"*95+"+\n")
    failedReportMap.each{
        configName, failedList ->
        resultString += String.format(testAlignFormat, configName, "")
        failedList.each{ test_name ->
            total_failed += 1
            resultString += String.format(testAlignFormat, "", test_name)
        }
        resultString = resultString.concat("+"+"-"*95+"+\n")
    }
    // return [total_failed, totalCounter, resultString]
    return resultString
}
// (failed, total, result) = getSummaryMap('build')