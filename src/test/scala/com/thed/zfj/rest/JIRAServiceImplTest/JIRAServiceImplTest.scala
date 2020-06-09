package com.thed.zfj.rest.JIRAServiceImplTest

import com.thed.zfj.rest.JiraService
import org.specs2.Specification
import org.specs2.specification.core.SpecStructure

/**
 * Created by smangal on 7/21/15.
 */
object JIRAServiceSpec extends Specification{ def is = s2""

  JiraService.url_base = "http://192.168.35.189:8080/"
  JiraService.userName = "admin"
  JiraService.passwd = "password"
  val issue = JiraService.getIssue("SSP-24");
  issue.key must not beNull
}
