import static groovy.io.FileType.FILES
import groovy.json.JsonSlurper
import groovy.json.JsonParserType
import groovy.json.JsonOutput

jsonSlurper = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY)

def mergeJSON(pair){
    def bArr = jsonSlurper.parseText(pair[0])
    def rArr = jsonSlurper.parseText(pair[1])
    return bArr + rArr
}

def getSummaryMap(build_dir){
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
def resultMap = getSummaryMap('build')
def json = JsonOutput.toJson(resultMap["scr1_cfg_rv32ic0"])
//println JsonOutput.prettyPrint(json)

def failedList = resultMap["scr1_cfg_rv32ic0"].findResults{it.tests.passed.value.contains('false')}
print(failedList)
def newFailed = resultMap.entrySet().stream().filter {it.key == "scr1_cfg_rv32ic0" && it.value.tests.passed.contains('false')}.findAny().isPresent()
println(newFailed)
// def x = mymap.find{ it.key == "likes" }?.value
// if(x)
//     println "x value: ${x}"
