// @Grab('org.apache.commons:commons-math3:3.4.1')
// import org.apache.commons.math3.primes.Primes

// void parallelize(int count) {
//   if (!Primes.isPrime(count)) {
//     error "${count} was not prime"
//   }else{
//       echo "IT Is a Prime: ${count}"
//   }
// }
import groovy.io.*
class Result {
     String failed
     String total
     String report
}

class LaunchConf {
     String scenarioFile
     String buildDir
     String launcher
}

def helloMe(message) {
    echo "HELLO WORLD ${message}"
}
def info(message) {
    echo "INFO: ${message}"
}

def warning(message) {
    echo "WARNING: ${message}"
}

@NonCPS
List extractLines(final String content) {
    List myKeys = []
    content.eachLine { line -> 
        myKeys << line
    }
    return myKeys
}


def getFiles(subdir){
  scenarios_list = []
  for (f in findFiles(glob: "**/rename/"+subdir+"/**/regression_default_*.yaml")){
    echo "${f}"
    echo "pushing"
    scenarios_list << "${f}"
  }
}

def prepareBuildDir(){
  def scenariosMap = [:]
  for (f in findFiles(glob: "**/regression_default_*.yaml")){
    echo "${f.path}"
    String scenario = "${f.path}".split("/")[-1]
    String folder = "${f.path}".split("/")[1]
    String build_dir = "build/"+"${f.path}".split("/")[1]
    String launcherPath = "${f.path}".split(scenario)[-2]
    // rename
    echo scenario
    echo launcherPath
    echo "${build_dir}"
    echo folder
    sh "[ -d ${build_dir} ] && echo OK || mkdir -p ${build_dir}"
    scenariosMap.put(build_dir, "${f.path}")
  }
  return scenariosMap
}

def listMeOld(){
  for (f in findFiles(glob: "**/regression_default_*.yaml")){
    echo "${f.path}"
    String segment = "${f.path}".split("/")[-1]
    String segment2 = "${f.path}".split("/")[1]
    echo segment
    echo segment2
    node("beta") {
      stage("Even Stage ${segment2}"){
        echo "${segment2}"
        sh "[ -d ${segment2} ] || mkdir ${segment2}"
      }
    }
    
  }
}


@NonCPS
def readDir(project_path) {
  echo "Prject path"
  echo project_path
  dlist = []
	new File(project_path + "/rename").eachDir{dlist << it.name }
	dlist.sort()
  dlist.each{
    echo it
  }
}

// @NonCPS
// def readDir()
// {
//   new File("/var/jenkins_home/").eachDir() {dir -> echo dir}
// }

Result rc_analyze(message){
    //echo "Release Candidate: ${message}"
    String summary = ""
    int total_failed = 0
    int total_all = 0
    int total_ran = 0
    for (f in findFiles(glob: '**/*results*.txt')) {
      int failed = 0
      int all = 0
      int ran = 0
      //echo "${f}"
      summary += "${f}\n"
      File resFile = new File ("${f}")
      String segment = resFile.getPath().split("/")[-1]
      final String content = readFile(file: "${f}")
      final List myKeys = extractLines(content)
      myKeys.each {String line -> 
          if (line.contains("ARCH_tmp")){
                    //.append(segment.padRight(30) + line.padRight(14))
                    //print(segment.padRight(30) + line.padRight(14));
          }
                if (line.contains("Summary:")){
                    resultList = line.findAll( /\d+/ )
                        all += Integer.parseInt(resultList[1])
                        ran += Integer.parseInt(resultList[0])
                        if (resultList[0] == resultList[1]){
                            
                            //reportFile.append(resultList[0].padLeft(4)+"/"+resultList[1].padRight(4)+"OK\n")
                            //printf("%s/%sOK\n", resultList[0].padLeft(4), resultList[1].padRight(4))
                        }
                        else{
                            failed ++
                            //reportFile.append(resultList[0].padLeft(4)+"/"+resultList[1].padRight(4)+"Failed\n")
                            //printf("%s/%sFailed\n", resultList[0].padLeft(4), resultList[1].padRight(4))
                        }
                }
            }
        total_all += all
        total_failed += failed
        def result = String.format("Total: %s\nPassed: %s\nFailed: %s", all, ran, failed)
        //println(summary)
        summary += "${result}\n"
        //echo result
        //reportFile.append(String.format("Total: %s\nPassed: %s\nFailed: %s", total, ran, failed))
   }
   return new Result(failed: total_failed, total: total_all, report: summary)
}

def runMultiple(int numTests){
  numTests.times{
    echo "${it}"
    println "Hello World ${it}"
    node {
      stage("Even Stage ${it}"){
        echo "${it}"
      }
    }
  }
}

def call(int buildNumber) {
  if (buildNumber % 2 == 0) {
    pipeline {
      agent any
      stages {
        stage('Even Stage') {
          steps {
            echo "The build number is even"
          }
        }
      }
    }
  } else {
    pipeline {
      agent any
      stages {
        stage('Odd Stage') {
          steps {
            echo "The build number is odd"
          }
        }
      }
    }
  }
}