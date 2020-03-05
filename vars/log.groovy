@Grab('org.apache.commons:commons-math3:3.4.1')
import org.apache.commons.math3.primes.Primes
void parallelize(int count) {
  if (!Primes.isPrime(count)) {
    error "${count} was not prime"
  }else{
      echo "IT Is a Prime: ${count}"
  }
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

void rc_analyze(message){
    echo "Release Candidate: ${message}"
    
    int numFailed = 0
    int total = 0
    int ran = 0 //results_run_verilator.txt
    for (f in findFiles(glob: '**/*results*.txt')) {
      echo "${f}"
      File resFile = new File ("${f}")
      String segment = resFile.getPath().split("/")[-1];
      def data = readFile(file: "${f}")
      //println(data)
      //echo data
      //data.each {echo "hello"}
      data.each {String line -> 
          if (line.contains("ARCH_tmp")){
                    //.append(segment.padRight(30) + line.padRight(14))
                    //print(segment.padRight(30) + line.padRight(14));
          }
                if (line.contains("Summary:")){
                    resultList = line.findAll( /\d+/ )
                        total += Integer.parseInt(resultList[1])
                        ran += Integer.parseInt(resultList[0])
                        if (resultList[0] == resultList[1]){
                            
                            //reportFile.append(resultList[0].padLeft(4)+"/"+resultList[1].padRight(4)+"OK\n")
                            //printf("%s/%sOK\n", resultList[0].padLeft(4), resultList[1].padRight(4))
                        }
                        else{
                            numFailed ++
                            //reportFile.append(resultList[0].padLeft(4)+"/"+resultList[1].padRight(4)+"Failed\n")
                            //printf("%s/%sFailed\n", resultList[0].padLeft(4), resultList[1].padRight(4))
                        }
                }
            }
        def summary = String.format("Total: %s\nPassed: %s\nFailed: %s", total, ran, numFailed)
        //println(summary)
        echo summary
        //reportFile.append(String.format("Total: %s\nPassed: %s\nFailed: %s", total, ran, numFailed))
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