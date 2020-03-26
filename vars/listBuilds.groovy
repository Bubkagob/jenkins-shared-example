import java.text.SimpleDateFormat
def numHoursBack = 24
def dateFormat = new SimpleDateFormat("HH:mm")
def buildNameWidth = 30


def cutOfTime = System.currentTimeMillis() - numHoursBack * 3600 * 1000

SortedMap res = new TreeMap();

for (job in Jenkins.instance.getAllItems(BuildableItem.class)) {
  for (build in job.getBuilds()) {
    if (build.getTimeInMillis() < cutOfTime) {
      break;
    }
    res.put(build.getTimeInMillis(), build)
  }
}

def format = "%-10s%-${buildNameWidth}s%-10s"

println(String.format(format, "status", "build", "Time"))
for (entry in res.descendingMap().entrySet()) {
  def build = entry.getValue()
  println(String.format(format, build.getResult(), build.getFullDisplayName(), dateFormat.format(build.getTime())))
}