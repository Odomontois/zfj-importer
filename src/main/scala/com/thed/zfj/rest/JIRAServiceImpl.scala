package com.thed.zfj.rest
// Sample usage of ...
// - Dispatch (http://dispatch.databinder.net)
// - SJSON (https://github.com/debasishg/sjson)

import java.io.File
import java.net.{URI, URL}
import java.util.HashMap

import com.atlassian.fugue
import com.thed.model._
import com.thed.service.impl.zie.AbstractImportManager.{ArrayMap, SingleValueMap}
import com.thed.service.zie.ImportManager
import com.thed.util._
import com.thed.zfj.model.{Component, Issue, IssueType, Priority, Project, TestStep, Version}
import com.thed.zfj.ui.ImportSwingApp.cbissueType
import dispatch.classic._
import dispatch.classic.mime.Mime._
import org.apache.commons.lang3.StringUtils
import org.apache.commons.logging.{Log, LogFactory}
import sjson.json.Serializer.SJSON
import sjson.json._

import scala.annotation.target._
import scala.collection.JavaConversions._
import collection.JavaConversions._
import scala.reflect.BeanInfo
import scala.swing.Dialog

// Model
@BeanInfo class LocalIssue(val id: String, val key: String) { def this() = this("10000", "") }
@BeanInfo class LocalUser(val accountId: String) { def this() = this("dummyAccountId") }
@BeanInfo class JiraMetaResponse(val expand:String, @(JSONTypeHint @field)(value = classOf[Project])val projects:List[Project]){ def this() = this("projects", null) }
object ZfjServerType extends Enumeration { val BTF, Cloud = Value}

//http://localhost:8080/rest/api/latest/issue/IC-1
// Http Request

object JiraService {
  private val log:Log = LogFactory.getLog(this.getClass);
	val http = new Http with HttpsLeniency
	var url_base = "http://localhost:2990/jira/rest"
	var userName = "admin"
	var passwd = "admin"
	var project:Project = _
	var issueType:IssueType = _
	var zConfig:ZConfig = _
	var serverType:ZfjServerType.Value = _
	def main(args: Array[String]):Unit = {
		//val json = http(url(url_base +"/api/latest/issue/SUM-1").as_!(userName, passwd) >~ { _.getLines.mkString } )
		// Deserialization
		//var inIssue = SJSON.in[LocalIssue](json)
//
//		val adminUser = new com.thed.zfj.model.User("admin")
//
    println(convertToMap(Array("one", "two")));
    println(new String(SJSON.out(Map("project" -> new Project("10001"), "customefield_10010" -> "Something", "customfield_10020" -> Array("first", "second")))))
		for(i <- 1 to 2){
			val issue = new Issue();
//			val issueId = saveTestcase(issue)
//			println(issueId)
			//saveTestStep(issueId, TestStep("StepX " + i, "dataX " + i, "resultX " +i));
		}
//		return;

			var data:String = "{\"expand\":\"projects\",\"projects\":[{\"self\":\"http://localhost:2990/jira/rest/api/2/project/DE\",\"id\":\"10300\",\"key\":\"DE\",\"name\":\"Deutsch_Projekt\",\"avatarUrls\":{\"16x16\":\"http://localhost:2990/jira/secure/projectavatar?size=small&pid=10300&avatarId=10000\",\"48x48\":\"http://localhost:2990/jira/secure/projectavatar?pid=10300&avatarId=10000\"},\"issuetypes\":[{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/1\",\"id\":\"1\",\"description\":\"A problem which impairs or prevents the functions of the product.\",\"iconUrl\":\"http://localhost:2990/jira/images/icons/bug.gif\",\"name\":\"Bug\",\"subtask\":false},{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/2\",\"id\":\"2\",\"description\":\"A new feature of the product, which has yet to be developed.\",\"iconUrl\":\"http://localhost:2990/jira/images/icons/newfeature.gif\",\"name\":\"New Feature\",\"subtask\":false},{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/3\",\"id\":\"3\",\"description\":\"A task that needs to be done.\",\"iconUrl\":\"http://localhost:2990/jira/images/icons/task.gif\",\"name\":\"Task\",\"subtask\":false},{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/4\",\"id\":\"4\",\"description\":\"An improvement or enhancement to an existing feature or task.\",\"iconUrl\":\"http://localhost:2990/jira/images/icons/improvement.gif\",\"name\":\"Improvement\",\"subtask\":false},{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/5\",\"id\":\"5\",\"description\":\"The sub-task of the issue\",\"iconUrl\":\"http://localhost:2990/jira/images/icons/issue_subtask.gif\",\"name\":\"Sub-task\",\"subtask\":true},{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/6\",\"id\":\"6\",\"description\":\"This Issue Type is used to create Zephyr Test within Jira.\",\"iconUrl\":\"http://localhost:2990/jira/download/resources/com.thed.zephyr.je/images/icons/ico_zephyr_issuetype.png\",\"name\":\"Test\",\"subtask\":false},{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/7\",\"id\":\"7\",\"description\":\"A big user story that needs to be broken down.\",\"iconUrl\":\"http://localhost:2990/jira/images/icons/ico_epic.png\",\"name\":\"Epic\",\"subtask\":false},{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/8\",\"id\":\"8\",\"description\":\"A user story\",\"iconUrl\":\"http://localhost:2990/jira/images/icons/ico_story.png\",\"name\":\"Story\",\"subtask\":false},{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/9\",\"id\":\"9\",\"description\":\"A technical task.\",\"iconUrl\":\"http://localhost:2990/jira/images/icons/ico_task.png\",\"name\":\"Technical task\",\"subtask\":true}]},{\"self\":\"http://localhost:2990/jira/rest/api/2/project/IC\",\"id\":\"10000\",\"key\":\"IC\",\"name\":\"IronClad_ENGLISH\",\"avatarUrls\":{\"16x16\":\"http://localhost:2990/jira/secure/projectavatar?size=small&pid=10000&avatarId=10005\",\"48x48\":\"http://localhost:2990/jira/secure/projectavatar?pid=10000&avatarId=10005\"},\"issuetypes\":[{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/1\",\"id\":\"1\",\"description\":\"A problem which impairs or prevents the functions of the product.\",\"iconUrl\":\"http://localhost:2990/jira/images/icons/bug.gif\",\"name\":\"Bug\",\"subtask\":false},{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/2\",\"id\":\"2\",\"description\":\"A new feature of the product, which has yet to be developed.\",\"iconUrl\":\"http://localhost:2990/jira/images/icons/newfeature.gif\",\"name\":\"New Feature\",\"subtask\":false},{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/3\",\"id\":\"3\",\"description\":\"A task that needs to be done.\",\"iconUrl\":\"http://localhost:2990/jira/images/icons/task.gif\",\"name\":\"Task\",\"subtask\":false},{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/4\",\"id\":\"4\",\"description\":\"An improvement or enhancement to an existing feature or task.\",\"iconUrl\":\"http://localhost:2990/jira/images/icons/improvement.gif\",\"name\":\"Improvement\",\"subtask\":false},{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/5\",\"id\":\"5\",\"description\":\"The sub-task of the issue\",\"iconUrl\":\"http://localhost:2990/jira/images/icons/issue_subtask.gif\",\"name\":\"Sub-task\",\"subtask\":true},{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/6\",\"id\":\"6\",\"description\":\"This Issue Type is used to create Zephyr Test within Jira.\",\"iconUrl\":\"http://localhost:2990/jira/download/resources/com.thed.zephyr.je/images/icons/ico_zephyr_issuetype.png\",\"name\":\"Test\",\"subtask\":false},{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/7\",\"id\":\"7\",\"description\":\"A big user story that needs to be broken down.\",\"iconUrl\":\"http://localhost:2990/jira/images/icons/ico_epic.png\",\"name\":\"Epic\",\"subtask\":false},{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/8\",\"id\":\"8\",\"description\":\"A user story\",\"iconUrl\":\"http://localhost:2990/jira/images/icons/ico_story.png\",\"name\":\"Story\",\"subtask\":false},{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/9\",\"id\":\"9\",\"description\":\"A technical task.\",\"iconUrl\":\"http://localhost:2990/jira/images/icons/ico_task.png\",\"name\":\"Technical task\",\"subtask\":true}]},{\"self\":\"http://localhost:2990/jira/rest/api/2/project/NW\",\"id\":\"10200\",\"key\":\"NW\",\"name\":\"new project\",\"avatarUrls\":{\"16x16\":\"http://localhost:2990/jira/secure/projectavatar?size=small&pid=10200&avatarId=10011\",\"48x48\":\"http://localhost:2990/jira/secure/projectavatar?pid=10200&avatarId=10011\"},\"issuetypes\":[{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/1\",\"id\":\"1\",\"description\":\"A problem which impairs or prevents the functions of the product.\",\"iconUrl\":\"http://localhost:2990/jira/images/icons/bug.gif\",\"name\":\"Bug\",\"subtask\":false},{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/2\",\"id\":\"2\",\"description\":\"A new feature of the product, which has yet to be developed.\",\"iconUrl\":\"http://localhost:2990/jira/images/icons/newfeature.gif\",\"name\":\"New Feature\",\"subtask\":false},{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/3\",\"id\":\"3\",\"description\":\"A task that needs to be done.\",\"iconUrl\":\"http://localhost:2990/jira/images/icons/task.gif\",\"name\":\"Task\",\"subtask\":false},{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/4\",\"id\":\"4\",\"description\":\"An improvement or enhancement to an existing feature or task.\",\"iconUrl\":\"http://localhost:2990/jira/images/icons/improvement.gif\",\"name\":\"Improvement\",\"subtask\":false},{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/5\",\"id\":\"5\",\"description\":\"The sub-task of the issue\",\"iconUrl\":\"http://localhost:2990/jira/images/icons/issue_subtask.gif\",\"name\":\"Sub-task\",\"subtask\":true},{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/6\",\"id\":\"6\",\"description\":\"This Issue Type is used to create Zephyr Test within Jira.\",\"iconUrl\":\"http://localhost:2990/jira/download/resources/com.thed.zephyr.je/images/icons/ico_zephyr_issuetype.png\",\"name\":\"Test\",\"subtask\":false},{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/7\",\"id\":\"7\",\"description\":\"A big user story that needs to be broken down.\",\"iconUrl\":\"http://localhost:2990/jira/images/icons/ico_epic.png\",\"name\":\"Epic\",\"subtask\":false},{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/8\",\"id\":\"8\",\"description\":\"A user story\",\"iconUrl\":\"http://localhost:2990/jira/images/icons/ico_story.png\",\"name\":\"Story\",\"subtask\":false},{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/9\",\"id\":\"9\",\"description\":\"A technical task.\",\"iconUrl\":\"http://localhost:2990/jira/images/icons/ico_task.png\",\"name\":\"Technical task\",\"subtask\":true}]},{\"self\":\"http://localhost:2990/jira/rest/api/2/project/WS\",\"id\":\"10100\",\"key\":\"WS\",\"name\":\"WindStorm\",\"avatarUrls\":{\"16x16\":\"http://localhost:2990/jira/secure/projectavatar?size=small&pid=10100&avatarId=10011\",\"48x48\":\"http://localhost:2990/jira/secure/projectavatar?pid=10100&avatarId=10011\"},\"issuetypes\":[{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/1\",\"id\":\"1\",\"description\":\"A problem which impairs or prevents the functions of the product.\",\"iconUrl\":\"http://localhost:2990/jira/images/icons/bug.gif\",\"name\":\"Bug\",\"subtask\":false},{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/2\",\"id\":\"2\",\"description\":\"A new feature of the product, which has yet to be developed.\",\"iconUrl\":\"http://localhost:2990/jira/images/icons/newfeature.gif\",\"name\":\"New Feature\",\"subtask\":false},{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/3\",\"id\":\"3\",\"description\":\"A task that needs to be done.\",\"iconUrl\":\"http://localhost:2990/jira/images/icons/task.gif\",\"name\":\"Task\",\"subtask\":false},{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/4\",\"id\":\"4\",\"description\":\"An improvement or enhancement to an existing feature or task.\",\"iconUrl\":\"http://localhost:2990/jira/images/icons/improvement.gif\",\"name\":\"Improvement\",\"subtask\":false},{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/5\",\"id\":\"5\",\"description\":\"The sub-task of the issue\",\"iconUrl\":\"http://localhost:2990/jira/images/icons/issue_subtask.gif\",\"name\":\"Sub-task\",\"subtask\":true},{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/6\",\"id\":\"6\",\"description\":\"This Issue Type is used to create Zephyr Test within Jira.\",\"iconUrl\":\"http://localhost:2990/jira/download/resources/com.thed.zephyr.je/images/icons/ico_zephyr_issuetype.png\",\"name\":\"Test\",\"subtask\":false},{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/7\",\"id\":\"7\",\"description\":\"A big user story that needs to be broken down.\",\"iconUrl\":\"http://localhost:2990/jira/images/icons/ico_epic.png\",\"name\":\"Epic\",\"subtask\":false},{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/8\",\"id\":\"8\",\"description\":\"A user story\",\"iconUrl\":\"http://localhost:2990/jira/images/icons/ico_story.png\",\"name\":\"Story\",\"subtask\":false},{\"self\":\"http://localhost:2990/jira/rest/api/latest/issuetype/9\",\"id\":\"9\",\"description\":\"A technical task.\",\"iconUrl\":\"http://localhost:2990/jira/images/icons/ico_task.png\",\"name\":\"Technical task\",\"subtask\":true}]}]}"
			//println(JSON.parseFull(data).get.asInstanceOf[Map[String, Any]].get("projects").get.asInstanceOf[List[Any]])
			var projects:List[Project] = (SJSON.in[JiraMetaResponse](data)).projects
			println(projects.get(0))
			projects.foreach(project => println(project.name))

			
//			var fieldMapDetails = new HashSet[FieldMapDetail]();
//			fieldMapDetails.add(new FieldMapDetail(ZephyrFieldEnum.EXTERNAL_ID, "F"))
//			fieldMapDetails.add(new FieldMapDetail(ZephyrFieldEnum.NAME, "E"))
//			fieldMapDetails.add(new FieldMapDetail(ZephyrFieldEnum.STEPS, "G"))
//			fieldMapDetails.add(new FieldMapDetail(ZephyrFieldEnum.RESULT, "H"))
//			fieldMapDetails.add(new FieldMapDetail(ZephyrFieldEnum.TESTDATA, "I")) 
//			val fieldMap = new FieldMap(1l, "First Map", "description", new Date(), ".csv", /*row number*/ 2, Constants.BY_ID_CHANGE, fieldMapDetails, "testcase")
//			val importJob = new ImportJob(1l, "Temp", "/Users/smangal/import/first", "csv", null, /*status*/ "1", null, fieldMap, new HashSet[JobHistory](), "testcase")
			
	}

  val dummyProject = new Project("-1", "", "Please select project...", null, null)
  val dummyIssueType = new IssueType("-1", "Please select issueType...", "Dummy", null)
	def getProjects():List[Project] = {
			val meta = http(getHttpRequest("/api/latest/project").as_!(userName, passwd)  >~ { _.getLines.mkString } )
			//val projects = JSON.parseFull(meta).get.asInstanceOf[Map[String, Any]].get("projects").get.asInstanceOf[List[Any]]
      var projects:List[Project] = SJSON.in[List[Project]](meta)
      val dummy =
      projects ::= dummyProject
      projects
	}

  def getMeta(projectId:String):List[Project] = {
			val meta = http(getHttpRequest("/api/latest/issue/createmeta?projectIds="+projectId+"&expand=projects.issuetypes.fields").as_!(userName, passwd)  >~ { _.getLines.mkString } )
			//val projects = JSON.parseFull(meta).get.asInstanceOf[Map[String, Any]].get("projects").get.asInstanceOf[List[Any]]
      var projects:List[Project] = (SJSON.in[JiraMetaResponse](meta)).projects
      projects
	}

	def getIssue(issueKey:String):LocalIssue = {
		val issueJson = http(getHttpRequest("/api/latest/issue/"+issueKey).as_!(userName, passwd)  >~ { _.getLines.mkString } )
		//val projects = JSON.parseFull(meta).get.asInstanceOf[Map[String, Any]].get("projects").get.asInstanceOf[List[Any]]
		var issue = SJSON.in[LocalIssue](issueJson)
		issue
	}
	
	def startImport(importJob:ImportJob, importManager:ImportManager):String = {
		importManager.importAllFiles(importJob, "", 1l)
		println(importJob.getHistory());
		importJob.getHistory().toString()
	}

  def saveTestcase(testcase:Testcase):String = {
		var issue = Map[String, AnyRef]();
		issue += ("project" -> project)
		issue += ("issuetype" -> issueType)
    //replace any linefeeds with single spaces
		issue += ("summary" -> {
          testcase.getName().replace("\n", " ")
      })
		if(testcase.getDescription() != null)
			issue += ("description" -> testcase.getDescription())
		if(testcase.getAssignee() != null)
			issue += ("assignee" -> new com.thed.zfj.model.User(testcase.getAssignee()))
		if(testcase.getCreator() != null)
			issue += ("reporter" -> new com.thed.zfj.model.User(testcase.getCreator()))
		if(testcase.getPriority() != null){
          issue += ("priority" -> {
            if (testcase.getPriority().matches("[+-]?\\d+"))
              new Priority(testcase.getPriority())
            else
              new Priority(null, testcase.getPriority())
          })
        }
		if(testcase.getFixVersions() != null){
			var versions = List[Version]()
			testcase.getFixVersions().split(",").foreach{verName => versions ::= new Version(verName)}
			issue += ("fixVersions" -> versions)
		}
		if(testcase.getTag() != null){
			issue += ("labels" -> testcase.getTag().split(","))
		}
		if(testcase.getDueDate() != null){
			issue += ("duedate" -> testcase.getDueDate());
		}
		if(testcase.getComments() != null){
			//TBI
		}
    if(testcase.environment != null)
		  issue += ("environment" -> testcase.environment)
		if(testcase.components != null){
			var comps = List[Component]()
			testcase.components.split(",").foreach{compName => comps ::= new Component(compName)}
			issue += ("components" -> comps)
		}

		/* see https://developer.atlassian.com/jiradev/api-reference/jira-rest-apis/jira-rest-api-tutorials/jira-rest-api-example-create-issue */
    testcase.getCustomProperties.foreach(f = entry => issue += (entry._1 -> {
				if(entry._2.isInstanceOf[ArrayMap]){
					//Convert java map into scala map
					convertArrayMapToMap(entry._2.asInstanceOf[ArrayMap]);
				}
				else if (entry._2.isInstanceOf[SingleValueMap]){
					//Convert java map into scala map
					val singleValueMap = entry._2.asInstanceOf[SingleValueMap]
					Map(singleValueMap.getMapKey -> singleValueMap.getValue)
				}
				else if (entry._2.isInstanceOf[Map[_,_]]){
						//Convert java map into scala map
						Map("value" -> entry._2.asInstanceOf[Map[String, String]].get("value"))
				}else (entry._2)
      }
    ))
		
		val issueId = saveTestcase(issue)
    if (testcase.getComments != null)
      updateComments(issueId, testcase.getComments)
    issueId
	}
  
  def saveAttachment(issueId:String, file:File) {
    
		val issueOutput = http(getHttpRequest("/api/latest/issue/" + issueId + "/attachments").as_!(userName, passwd)  
		    <:< Map("X-Atlassian-Token" -> "nocheck")
		    <<* ("file", file) 
		    >~ { _.getLines.mkString } )
		    
		
    log.debug(issueOutput)
		
  }

  private def convertToMap(values:Array[String]):Array[Map[String, String]] = {
    var valuesMap = Array[Map[String, String]]()
    values.foreach(strVal => valuesMap ++= Array(Map("value" -> strVal)))
    valuesMap
  }

	private def convertArrayMapToMap(valueArrayMap:ArrayMap):Array[Map[String, String]] = {
		var valuesMap = Array[Map[String, String]]()
		valueArrayMap.getValues.foreach(strVal => valuesMap ++= Array(Map(valueArrayMap.getMapKey -> strVal)))
		valuesMap
	}
	
	private def saveTestcase(issue:Map[String, AnyRef]):String = {
		var fields = new String( SJSON.out(Map("fields" -> issue)))
		log.debug(fields)
		val issueOutput = http(getHttpRequest("/api/latest/issue/").as_!(userName, passwd)  << (fields, "application/json") >~ { _.getLines.mkString } )
		val issueRes = SJSON.in[LocalIssue](issueOutput)
    log.debug(issueOutput)
		issueRes.id
	}

	def updateAccountId():String = {
		var accountId = ""
		try {
			val accountIdRes = http(getHttpRequest("/api/latest/myself").as_!(userName, passwd) >~ {
				_.getLines.mkString
			})
			println(accountIdRes);
			val userRes = SJSON.in[LocalUser](accountIdRes)
			 accountId = userRes.accountId;
			println(accountId);
			accountId
		 }
	}

  private def updateComments(issueId:String, comments:String):Unit = {
    var fields = new String( SJSON.out(Map("body" -> comments)))
    val issueOutput = http(getHttpRequest("/api/latest/issue/" + issueId + "/comment").as_!(userName, passwd)  << (fields, "application/json") >~ { _.getLines.mkString } )
    println(issueOutput);
  }

	def saveTestStep(issueId:String, step:TestStep):String = {
		serverType match{
			case ZfjServerType.BTF => {
				return saveTestStepBTF(issueId, step);
			}
			case ZfjServerType.Cloud => {
				return saveTestStepCloud(issueId, step);
			}
		}
	}

	private def saveTestStepBTF(issueId:String, step:TestStep):String = {
		var fields = new String( SJSON.out(step))
		println(fields + " \n IssueId is:" + issueId)
		var stepResponse = http(getHttpRequest("/zephyr/latest/teststep/" + issueId).as_!(userName, passwd) <:< Map("User-Agent" -> "ZFJImporter", "AO_7DEABF" -> java.util.UUID.randomUUID.toString, "AO-7DEABF" -> java.util.UUID.randomUUID.toString)  << (fields, "application/json") >~ { _.getLines.mkString } )
		println(stepResponse)
		stepResponse
	}

	private def saveTestStepCloud(issueId:String, step:TestStep):String = {
		var fields = new String( SJSON.out(step))
		println(fields + " \n IssueId is:" + issueId)
		var jwtToken = ZFJRestClientUtil.getJWTToken(new URI(zConfig.ZEPHYR_BASE_URL + "/public/rest/api/1.0/teststep/" + issueId + "?projectId=" + project.id), HttpMethod.POST, zConfig)
		var stepResponse = http(getHttpRequest("/api/1.0/teststep/" + issueId + "?projectId=" + project.id, zConfig.ZEPHYR_BASE_URL + "/public/rest" ) <:< Map(
			"User-Agent" -> "ZFJImporter",
			"Authorization" -> jwtToken,
			"zapiAccessKey" -> zConfig.ACCESS_KEY)  << (fields, "application/json") >~ { _.getLines.mkString } )
		println(stepResponse)
		stepResponse
	}



	private def getHttpRequest(urlFragment:String, urlBase:String = url_base):Request = {
		var origBaseUrl:URL = new URL(urlBase.replaceFirst("/$", ""));
		/* If url doesnt already have rest endPoint in it, lets append it. Both ZFJ BTF and Cloud need it.*/
		if(!StringUtils.endsWith(origBaseUrl.getFile(), "/rest")){
			origBaseUrl = new URL(origBaseUrl.getProtocol(), origBaseUrl.getHost(), origBaseUrl.getPort(), origBaseUrl.getFile() + "/rest");
		}
    return getHttpRequest(urlFragment, origBaseUrl)
  }

	private def getHttpRequest(urlFragment: String, origBaseUrl: URL): Request = {
		if (origBaseUrl.getProtocol().indexOf("https") > -1) {
			System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true")
			System.setProperty("sun.security.ssl.allowLegacyHelloMessages", "true")
			return url(origBaseUrl.toString() + urlFragment).secure
		}
		else
			return url(origBaseUrl.toString() + urlFragment)
	}
}