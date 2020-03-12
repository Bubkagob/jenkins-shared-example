@Grapes([
        @Grab(group = 'com.taskadapter', module = 'redmine-java-api', version = '3.1.2'),
        @Grab(group = 'org.slf4j', module = 'slf4j-jdk14', version = '1.7.1'),
        @Grab(group = 'org.apache.httpcomponents', module = 'httpclient', version = '4.5.7'),
        @Grab(group = 'org.easytesting', module = 'fest-assert', version = '1.4'),
        @Grab(group = 'org.json', module = 'json', version = '20090211'),
        @Grab(group = 'org.slf4j', module = 'slf4j-api', version = '1.7.1'),
        @Grab(group = 'org.apache.httpcomponents', module = 'httpcore', version = '4.4.11'),
        @Grab(group = 'junit', module = 'junit', version = '4.10')
])

import com.taskadapter.redmineapi.RedmineManager
import com.taskadapter.redmineapi.RedmineManagerFactory
import com.taskadapter.redmineapi.bean.Project
import com.taskadapter.redmineapi.bean.Issue
import com.taskadapter.redmineapi.bean.Tracker
import com.taskadapter.redmineapi.bean.IssueFactory
import com.taskadapter.redmineapi.bean.Version
import com.taskadapter.redmineapi.bean.VersionFactory
import groovy.json.JsonSlurper
import org.apache.http.entity.ContentType;



/*
        configuration
*/
def createRedmineReport(){
        def uri = "http://192.168.1.100:8080/"
        def apiAccessKey = "0c86aa59eeed3aca23b0973554deec18ebbd4182"
        def projectKey = "verification"
        def parentId = 3699
        def authorId = 47

        // Create an issue
        RedmineManager manager = RedmineManagerFactory.createWithApiKey(uri, apiAccessKey);
        def projectManager = manager.getProjectManager();
        def attachmentManager = manager.getAttachmentManager();
        def issue_manager = manager.getIssueManager();
        Project project = projectManager.getProjectByKey(projectKey);
        def tracker = project.getTrackerByName("Task");
        
        Issue issue = new Issue()
        issue.setSubject("Weekend regression")
        issue.setDescription("weekend regression task")
        issue.setAssigneeId(authorId)
        issue.setParentId(parentId)
        issue.setTracker(tracker)
        issue.setProjectId(project.getId())
        Issue createdIssue = issue_manager.createIssue(issue);
        createdIssue.setDoneRatio(100)
        createdIssue.setSpentHours(2.3)
        createdIssue.setAuthorId(authorId)
        createdIssue.setStatusId(3)
        issue_manager.update(createdIssue)

        //File file = new File("log.json");
        //attachmentManager.addAttachmentToIssue(createdIssue.getId(), file, ContentType.TEXT_PLAIN.getMimeType());
}