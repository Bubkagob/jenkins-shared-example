import static groovy.io.FileType.FILES
import groovy.json.JsonSlurper
import groovy.json.JsonParserType
import groovy.json.JsonOutput

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
    def failedMap = getFailedReport(build_dir)[0]
    def testAlignFormat = "| %-30s|%60s\t|%n";
    def resultString = ""
    resultString = resultString.concat("+"+"-"*95+"+\n")
    resultString += String.format(testAlignFormat, "CONFIG", "Failed tests")
    resultString = resultString.concat("+"+"-"*95+"+\n")
    failedMap.each{
        configName, failedList ->
        resultString += String.format(testAlignFormat, configName, "");
        failedList.each{ test_name ->
            resultString += String.format(testAlignFormat, "", test_name)
            //System.out.format(testAlignFormat, "", test_name);
        }
        resultString = resultString.concat("+"+"-"*95+"+\n")
    }
    return resultString
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

    // MAKE OF HTML
    // markup.html{
    //     head{
    //         style(type:"text/css", '''
    //             .header, .first, .row {
    //                 border: 1px solid;
    //                 margin: 30px;
    //                 padding: 10px;
    //             }
    //             .header {
    //                 color: white;
    //                 font-size: 18pt;
    //                 background-color: #aec4c7
    //             }
    //             .first {
    //                 background-color:  #ffffff
    //             }
    //             .row {
    //                 text-align:right;
    //                 background-color:  #e0f7fa
    //             }
    //         ''')
    // } 
    // markup.table(style: 'border:2px solid;padding: 2px;text-align:center;style: "border-collapse:collapse;"') {
    // markup.thead{
    //     markup.tr {
    //         markup.th(title:"Field #1", 'class':'header', "Config name")
    //         markup.th(title:"Field #2", 'class':'header', "Failed tests")
    //     } // tr
    // } // thead
    // markup.tbody{
    // def count = 0
    // markup.tr{failedMap.each {
    //     conf, flist ->
    //     markup.tr {
    //         markup.th(title:"Field #1", 'class':'first', conf)
    //         markup.td(title:"Field #2", 'class':'row',{
                
    //             markup.ul {
    //                 flist.each{test_name ->
    //                     markup.li(align:"right", test_name)
    //                 }
    //             }
    //         })
    //     }
    //     markup.tr{
    //         count += flist.size()
    //         markup.td(title:"Field #1", 'class':'row', "Total")
    //         markup.td(title:"Field #2", 'class':'row', flist.size())
    //     }
    // } // td
    // } // tr
    // markup.tr{
    //         markup.td(title:"Field #1", 'class':'header', "Passed")
    //         markup.td(title:"Field #2", 'class':'header', "${total - count} / ${total}")
    //     }
    // markup.tr{
    //         markup.td(title:"Field #1", 'class':'header', "Failed")
    //         markup.td(title:"Field #2", 'class':'header', "${count} / ${total}")
    //     }

    // } //tbody
    // } // table
    // }
    markup.html{
        markup.head(
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
        )
        markup.table(style: 'border:2px solid;padding: 2px;text-align:center;style: "border-collapse:collapse;"'){
            markup.thead(
                markup.tr{
                    markup.th(title:"Field #1", class:"header", "Config name")
                    markup.th(title:"Field #2", class:"header", "Failed tests")
                } // tr
            ) // thead
            markup.tbody{
            def count = 0
            def fcount = 0
            markup.tr(
                failedMap.each {
                (conf, flist) ->
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
                    // def tot_failed = fcount
                    // count += tot_failed
                    // markup.tr{
                    //     markup.th(title:"Field #1", 'class':'row', "Total")
                    //     markup.td(title:"Field #2", 'class':'row', "${tot_failed}")
                    // }
                }
            ) // tr
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



//generateTextReport('build')
//generateHTMLreport('build')



// failedCollection = getFailedReport('build')
// def total = 0
// failedCollection.each{conf, arr ->
//     println conf
//     total += arr.size()
//     println arr.size()
// }
// println "Total"
// println total

//def resultMap = getSummaryMap('build')
//def json = JsonOutput.toJson(resultMap)
//println JsonOutput.prettyPrint(json)
//def failedMap = getFailedReport('build')
//def json = JsonOutput.toJson(failedMap)
//def InputJSON = new JsonSlurper().parseText(json)
//println JsonOutput.prettyPrint(json)
//def json = JsonOutput.toJson(failed)
//println JsonOutput.prettyPrint(json)
//println(failed)
//println(failed.size())

//def failedList = resultMap['scr1_cfg_rv32ic0'].findResults{it -> it.tests.passed.value == false? it.tests.test_name: null}

// def failedList = resultMap['scr1_cfg_rv32ic0'].findResults{
//         it -> 
//         it.tests.findResults{it.passed == false? it.test_name:null}
//     }

//failedList.each{it -> println it.size()}
//failedList =  failedList.findAll { item -> !item.isEmpty() }
//println(failedList)


//def failedList = resultMap['scr1_cfg_rv32imc3'].findResults{it.tests.passed.value.contains('false')}
//def failedList = resultMap['scr1_cfg_rv32imc3'].findAll{it.tests.passed.value.contains('false')}
//def failedList = resultMap['scr1_cfg_rv32ic0'].findResults{it.tests.passed}

//failedList.each{println it.getClass()}
//def newFailed = resultMap.entrySet().stream().filter {it.key == "scr1_cfg_rv32ic0" && it.value.tests.passed.contains('false')}.findAny().isPresent()
//println(newFailed)
// def x = mymap.find{ it.key == "likes" }?.value
// if(x)
//     println "x value: ${x}"
//def c = [1, 2, 3, 4]

// def c = [["true"], ["false", "true", "false"]]
// def results  = c.findResults { it ->
//     it.contains("false") ?    // if this is true
//         it:    // return this
//         null        // otherwise skip this one
// }

// println(results)



