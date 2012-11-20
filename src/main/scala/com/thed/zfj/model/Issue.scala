package com.thed.zfj.model;

import scala.reflect.BeanInfo
import scala.reflect.BeanProperty
import scala.annotation.target._
import sjson.json._

@BeanInfo case class Project(@(JSONProperty @getter)(ignoreIfNull = true)id: String, @(JSONProperty @getter)(ignoreIfNull = true)key: String, 
															@(JSONProperty @getter)(ignoreIfNull = true)name:String, @(JSONProperty @getter)(ignoreIfNull = true)avatarUrls:Map[String, String],
															@(JSONProperty @getter)(ignoreIfNull = true) @(JSONTypeHint @field)(value = classOf[IssueType])issuetypes:List[IssueType]) {
	def this(id:String) = this(id, null, "Projects", null, null)
	def this() = this(null)
}

@BeanInfo case class IssueType( @JSONProperty(ignoreIfNull = true)id: String, @JSONProperty(ignoreIfNull = true)name:String, @JSONProperty(ignoreIfNull = true)description:String,
                                @(JSONProperty @getter)(ignoreIfNull = true)fields: Map[String, AnyRef]) {
	def this(id:String) = this(id, "Test", "", null)
	def this() = this(null)
}

@BeanInfo case class User( name: String) {
}

@BeanInfo case class Priority( @(JSONProperty @getter)(ignoreIfNull = true) id:String, @(JSONProperty @getter)(ignoreIfNull = true) name:String = null) {
}

@BeanInfo case class Version(  @(JSONProperty @getter)(ignoreIfNull = true)name: String) {
}

@BeanInfo case class Component( @(JSONProperty @getter)(ignoreIfNull = true)name: String) {
}


@BeanInfo case class Issue( @JSONTypeHint(classOf[Project]) var project:Project, @BeanProperty var summary:String, @JSONTypeHint(classOf[IssueType]) var issuetype:IssueType,
														@(JSONProperty @getter)(ignoreIfNull = true) @JSONTypeHint(classOf[User]) var assignee:User, 
														@(JSONProperty @getter)(ignoreIfNull = true) @JSONTypeHint(classOf[User]) var reporter:User,
														@(JSONProperty @getter)(ignoreIfNull = true) var description:String,
														@(JSONProperty @getter)(ignoreIfNull = true) var environment:String,
														@(JSONProperty @getter)(ignoreIfNull = true) var labels:Array[String],
														@(JSONProperty @getter)(ignoreIfNull = true) @(JSONTypeHint @field)(classOf[Priority]) var priority:Priority,
														@(JSONProperty @getter)(ignoreIfNull = true) @(JSONTypeHint @field)(value = classOf[Version]) var fixVersions:List[Version],
														@(JSONProperty @getter)(ignoreIfNull = true) @(JSONTypeHint @field)(value = classOf[Component]) var components:List[Component],
                            @(JSONProperty @getter)(ignoreIfNull = true) @(JSONTypeHint @field)(value = classOf[String]) var custom:Map[String, String]) {
	def this() = this(null, "", new IssueType(), null, null, "", "", null, null, null, null, null)
}

@BeanInfo case class TestStep( step: String, data:String, result:String) {
}