package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import scala.io.Source
import models.DateUtil
import java.net.URLEncoder

object Application extends Controller {

  val jiraQuery = "project = %s AND component = plan ORDER BY plannedStart"

  def index = Action {
    Redirect(routes.Application.gantt("UPDUSERREFACTOR"))
  }

  def gantt(projectId: String) = Action {
    Ok(views.html.main(projectId))
  }

  def ganttData(projectId: String) = Action {
    val username = Play.application.configuration.getString("jira.username").get
    val passwordCrypt = Play.application.configuration.getString("jira.password.crypt").get
    val passwordSeed = Play.application.configuration.getString("jira.password.seed").get

    import org.jasypt.util.text._
    val textEncryptor: BasicTextEncryptor = new BasicTextEncryptor
    textEncryptor.setPassword(passwordSeed)

    val password = textEncryptor.decrypt(passwordCrypt)

    // fetch issues in xml
    val requestURL = "http://issue.daumcorp.com/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?jqlQuery=" +
      URLEncoder.encode(jiraQuery.format(projectId), "UTF-8") +
      "&tempMax=1000" +
      "&os_username=" + username +
      "&os_password=" + password
    val jiraXml = scala.xml.XML.load(requestURL)

    // convert xml to jsgantt xml format
    val gantt =
      for (item <- jiraXml \ "channel" \ "item") yield {
        var plannedStart: String = DateUtil convertDateFormat (item \ "created" text)
        var plannedEnd: String = DateUtil convertDateFormat (item \ "updated" text)

        item \ "customfields" \ "customfield" foreach { cfnode =>
          if ((cfnode \ "customfieldname").text == "plannedStart")
            plannedStart = DateUtil convertDateFormat (cfnode \ "customfieldvalues" \ "customfieldvalue" text)
          else if ((cfnode \ "customfieldname").text == "plannedEnd")
            plannedEnd = DateUtil convertDateFormat (cfnode \ "customfieldvalues" \ "customfieldvalue" text)
        }

        var name: String = (item \ "title" text)
        name = name drop ((name indexOf { "]" }) + 2)

        val color: String = (item \ "status" text) match {
          case "Open" => "ADD8E6" // LightBlue
          case "In Progress" => "0000ff" // blue
          case "Resolved" => "00008b" // DarkBlue
          case "Reopened" => "0000ff" // blue
          case "Closed" => "000000" // black
          case _ => "808080" // gray
        }

        // if this issue has subtasks, mark as parent group.
        val isParentGroup = if ((item \ "subtasks" \ "subtask").size > 0) 1 else 0

        <task>
          <pID>{ item \ "key" \ "@id" text }</pID>
          <pName>{ name }</pName>
          <pStart>{ plannedStart }</pStart>
          <pEnd>{ plannedEnd }</pEnd>
          <pColor>{ color }</pColor>
          <pLink>{ item \ "link" text }</pLink>
          <pMile>0</pMile>
          <pRes>{ item \ "assignee" text }</pRes>
          <pComp>0</pComp>
          <pGroup>{ isParentGroup }</pGroup>
          <pParent>{ item \ "parent" \ "@id" text }</pParent>
          <pOpen/>
          <pDepend/>
        </task>
      }
    Ok(<project>{ gantt }</project>).as(XML)
  }
}