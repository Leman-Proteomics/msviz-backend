package ch.isbsib.proteomics.mzviz.controllers.qc

import javax.ws.rs.PathParam

import ch.isbsib.proteomics.mzviz.controllers.CommonController
import ch.isbsib.proteomics.mzviz.qc.models.{EliteMonitorInfo}

import ch.isbsib.proteomics.mzviz.qc.services.EliteMonitorDBServices
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import ch.isbsib.proteomics.mzviz.qc.services.JsonQCFormats._

import play.api.libs.json.{JsObject, Json}
import com.wordnik.swagger.annotations._
import play.api.mvc.Action

import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
 * Created by qjolliet on 06/01/16.
 */
@Api(value = "/qc/eliteMonitor", description = "Qc Elite Monitor Information")
object EliteMonitorInfoController extends CommonController{

  @ApiOperation(nickname = "AddEliteMonitorInfo",
    value = "Add Elite Monitor information",
    notes = """ insert Elite Monitor information by a date """,
    response = classOf[Boolean],
    httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = "eliteMonitorInfo", required = true, dataType = "application/json", paramType = "body")
  ))
  def addEliteMonitorInfo=

    Action.async{
      request =>
        val eliteMmonitorInfo:EliteMonitorInfo = request.body.asText match {

          case Some(s) => Json.parse(s).as[EliteMonitorInfo]
          case None => throw new Exception("you have to provider rawfileInfomation!")
        }
        EliteMonitorDBServices().insert(eliteMmonitorInfo)
          .map {
          n => Ok(Json.obj("inserted" -> n))
        }.recover {
          case e => BadRequest(e.getMessage + e.getStackTrace.mkString("\n"))
        }

    }

  @ApiOperation(nickname = "EliteMonitorInformation",
    value = "List all added elite monitor Information",
    notes = """Returns only the elite monitor information """,
    response = classOf[Seq[EliteMonitorInfo]],
    httpMethod = "GET")
  def list=
    Action.async {
      EliteMonitorDBServices().list
        .map { qcDeviceInfo=> Ok(Json.toJson(qcDeviceInfo))
      }.recover {
        case e => BadRequest(e.getMessage + e.getStackTrace.mkString("\n"))
      }
    }

  @ApiOperation(nickname = "findEliteMonitorInformation",
    value = "find EliteMonitorInfo by date",
    notes = """Returns only the EliteMonitorInfo information """,
    response = classOf[Seq[EliteMonitorInfo]],
    httpMethod = "GET")
  def findEliteMonitorInfoByDate(@ApiParam(value = """monitorDate""", defaultValue = "") @PathParam("Date") monitorDate: String) =
    Action.async {
      EliteMonitorDBServices().findEliteMonitorInfoByDate(monitorDate)
        .map { eliteMonitorInfo => Ok(Json.toJson(eliteMonitorInfo))
      }.recover {
        case e => BadRequest(e.getMessage + e.getStackTrace.mkString("\n"))
      }
    }

  @ApiOperation(nickname = "findEliteMonitorInformation",
    value = "find EliteMonitorInfo between 2 dates",
    notes = """Returns only the EliteMonitorInfo information """,
    response = classOf[Seq[EliteMonitorInfo]],
    httpMethod = "GET")
  def findEliteMonitorInfoBtw2Date(@ApiParam(value = """monitorDate1""", defaultValue = "") @PathParam("Date") monitorDate1: String,
  @ApiParam(value = """monitorDate1""", defaultValue = "") @PathParam("Date") monitorDate2: String) =
    Action.async {
      EliteMonitorDBServices().findEliteMonitorInfoBtw2Date(monitorDate1,monitorDate2)
        .map { eliteMonitorInfo => Ok(Json.toJson(eliteMonitorInfo))
      }.recover {
        case e => BadRequest(e.getMessage + e.getStackTrace.mkString("\n"))
      }
    }

  @ApiOperation(nickname = "deleteEliteMonitorInfo",
    value = "delete Elite Monitor Infomation by date",
    notes = """return success or fail """,
    response = classOf[EliteMonitorInfo],
    httpMethod = "DELETE")
  def delete(
              @ApiParam(value = """MonitorDate""", defaultValue = "") @PathParam("MonitorDate") monitorDate: String,
              @ApiParam(value = """MonitorIndex""", defaultValue = "") @PathParam("MonitorIndex") monitorIndex: String) =
    Action.async {
      EliteMonitorDBServices().delete(monitorDate,monitorIndex)
        .map { x => Ok(Json.obj("deleted" -> x))
      }.recover {
        case e => BadRequest(e.getMessage + e.getStackTrace.mkString("\n"))
      }
    }

  @ApiOperation(nickname = "updateEliteMonitorInfo",
    value = "update Elite Monitor Infomation by a date",
    notes = """Returns new Elite Monitor Infomation """,
    response = classOf[Boolean],
    httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = "updateInfo", required = true, dataType = "application/json", paramType = "body")
  ))
  def updateEliteMonitorInfo =
    Action.async {
      request =>
        val updateInfo:EliteMonitorInfo = request.body.asText match {

          case Some(s) => Json.parse(s).as[EliteMonitorInfo]
          case None => throw new Exception("you have to provider EliteMonitorInfo!")
        }
        println("print upinfo"+ updateInfo.Multplier1 + ":" + updateInfo.Multplier2 + ":" + updateInfo.Comment)
        EliteMonitorDBServices().updateEliteMonitorInfo(updateInfo)
          .map { n => Ok(Json.obj("Updated" -> n))
                  Ok("Updated" + updateInfo.Multplier1 + ":" + updateInfo.Multplier2)
               }.recover {
                   case e => BadRequest(e.getMessage + e.getStackTrace.mkString("\n"))
               }
    }

  @ApiOperation(nickname = "optionEliteMonitorInfo",
    value = "fake Elite Monitor information ",
    notes = """return success or fail """,
    response = classOf[Int],
    httpMethod = "OPTIONS")
  def optionEliteMonitorInfo =
    Action.async {
      Future {
        Ok("Ok")
      }
    }
  @ApiOperation(nickname = "optionDelEliteMonitorInfo",
    value = "fake delete Elite Monitor information ",
    notes = """return success or fail """,
    response = classOf[Int],
    httpMethod = "OPTIONS")
  def optionDelEliteMonitorInfo(@ApiParam(value = """MonitorDate""", defaultValue = "") @PathParam("MonitorDate") monitorDate: String,
                       @ApiParam(value = """MonitorIndex""", defaultValue = "") @PathParam("MonitorIndex") monitorIndex: String) =
    Action.async {
      Future {
        Ok("Ok")
      }
    }

}
