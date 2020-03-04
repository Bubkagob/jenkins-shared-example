import com.taskadapter.redmineapi.RedmineManager
import com.taskadapter.redmineapi.RedmineManagerFactory
import com.taskadapter.redmineapi.bean.Project
import groovy.json.JsonSlurper

@Grapes([
        @Grab(group = 'com.taskadapter', module = 'redmine-java-api', version = '2.2.0'),
        @Grab(group = 'org.slf4j', module = 'slf4j-jdk14', version = '1.7.1'),
        @Grab(group = 'org.apache.httpcomponents', module = 'httpclient', version = '4.2'),
        @Grab(group = 'org.easytesting', module = 'fest-assert', version = '1.4'),
        @Grab(group = 'org.json', module = 'json', version = '20090211'),
        @Grab(group = 'org.slf4j', module = 'slf4j-api', version = '1.7.1'),
        @Grab(group = 'org.apache.httpcomponents', module = 'httpcore', version = '4.2'),
        @Grab(group = 'junit', module = 'junit', version = '4.10')
])
def uri = "http://192.168.1.100:8080/"
def userName = 'ia-sc'
def password = 'Redmine665532!'
RedmineManager mgr = RedmineManagerFactory.createWithUserAuth(uri, userName, password)
List<Project> projectsWithHttpBasicAuth = mgr.getProjectManager().getProjects()
for (Project project : projectsWithHttpBasicAuth) {
    System.out.println(project.toString())
}
