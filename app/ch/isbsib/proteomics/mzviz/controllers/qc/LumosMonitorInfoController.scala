package ch.isbsib.proteomics.mzviz.controllers.qc

import javax.ws.rs.PathParam

import ch.isbsib.proteomics.mzviz.controllers.CommonController
import ch.isbsib.proteomics.mzviz.qc.models.LumosMonitorInfo
import ch.isbsib.proteomics.mzviz.qc.services.LumosMonitorDBServices
import ch.isbsib.proteomics.mzviz.qc.services.JsonQCFormats._
import com.wordnik.swagger.annotations._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc.Action

import scala.concurrent.Future

/**
 * Created by qjolliet on 06/01/16.
 */
@Api(value = "/qc/eliteMonitor", description = "Qc Lumos Monitor Information")
object LumosMonitorInfoController extends CommonController{

  @ApiOperation(nickname = "AddEliteMonitorInfo",
    value = "Add Elite Monitor information",
    notes = """ insert Elite Monitor information by a date """,
    response = classOf[Boolean],
    httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = "lumosMonitorInfo", required = true, dataType = "application/json", paramType = "body")
  ))
  def addLumosMonitorInfo=

    Action.async{
      request =>
        val lumosMonitorInfo:LumosMonitorInfo = request.body.asText match {

          case Some(s) => Json.parse(s).as[LumosMonitorInfo]
          case None => throw new Exception("you have to provider rawfileInfomation!")
        }
        LumosMonitorDBServices().insert(lumosMonitorInfo)
          .map {
          n => Ok(Json.obj("inserted" -> n))
        }.recover {
          case e => BadRequest(e.getMessage + e.getStackTrace.mkString("\n"))
        }

    }

  @ApiOperation(nickname = "LumosMonitorInformation",
    value = "List all added elite monitor Information",
    notes = """Returns only the elite monitor information """,
    response = classOf[Seq[LumosMonitorInfo]],
    httpMethod = "GET")
  def list=
    Action.async {
      LumosMonitorDBServices().list
        .map { qcDeviceInfo=> Ok(Json.toJson(qcDeviceInfo))
      }.recover {
        case e => BadRequest(e.getMessage + e.getStackTrace.mkString("\n"))
      }
    }

  @ApiOperation(nickname = "findLumosMonitorInformation",
    value = "find LumosMonitorInfo by date",
    notes = """Returns only the LumosMonitorInfo information """,
    response = classOf[Seq[LumosMonitorInfo]],
    httpMethod = "GET")
  def findLumoseMonitorInfoByDate(@ApiParam(value = """monitorDate""", defaultValue = "") @PathParam("Date") monitorDate: String) =
    Action.async {
      LumosMonitorDBServices().findLumosMonitorInfoByDate(monitorDate)
        .map { lumosMonitorInfo => Ok(Json.toJson(lumosMonitorInfo))
      }.recover {
        case e => BadRequest(e.getMessage + e.getStackTrace.mkString("\n"))
      }
    }

  @ApiOperation(nickname = "findlumosMonitorInformation",
    value = "find LumosMonitorInfo between 2 dates",
    notes = """Returns only the LumosMonitorInfo information """,
    response = classOf[Seq[LumosMonitorInfo]],
    httpMethod = "GET")
  def findLumosMonitorInfoBtw2Date(@ApiParam(value = """monitorDate1""", defaultValue = "") @PathParam("Date") monitorDate1: String,
  @ApiParam(value = """monitorDate1""", defaultValue = "") @PathParam("Date") monitorDate2: String) =
    Action.async {
      LumosMonitorDBServices().findLumosMonitorInfoBtw2Date(monitorDate1,monitorDate2)
        .map { lumosMonitorInfo => Ok(Json.toJson(lumosMonitorInfo))
      }.recover {
        case e => BadRequest(e.getMessage + e.getStackTrace.mkString("\n"))
      }
    }

  @ApiOperation(nickname = "deleteEliteMonitorInfo",
    value = "delete Elite Monitor Infomation by date",
    notes = """return success or fail """,
    response = classOf[LumosMonitorInfo],
    httpMethod = "DELETE")
  def delete(
              @ApiParam(value = """MonitorDate""", defaultValue = "") @PathParam("MonitorDate") monitorDate: String,
              @ApiParam(value = """MonitorIndex""", defaultValue = "") @PathParam("MonitorIndex") monitorIndex: String) =
    Action.async {
      LumosMonitorDBServices().delete(monitorDate,monitorIndex)
        .map { x => Ok(Json.obj("deleted" -> x))
      }.recover {
        case e => BadRequest(e.getMessage + e.getStackTrace.mkString("\n"))
      }
    }

  @ApiOperation(nickname = "updateLumosMonitorInfo",
    value = "update Lumos Monitor Infomation by a date",
    notes = """Returns new Lumos Monitor Infomation """,
    response = classOf[Boolean],
    httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = "updateInfo", required = true, dataType = "application/json", paramType = "body")
  ))
  def updateLumosMonitorInfo =
    Action.async {
      request =>
        val updateInfo:LumosMonitorInfo = request.body.asText match {

          case Some(s) => Json.parse(s).as[LumosMonitorInfo]
          case None => throw new Exception("you have to provider EliteMonitorInfo!")
        }
        println("print upinfo"+ updateInfo.Multplier + ":" + updateInfo.Transmission + ":" + updateInfo.Comment)
        LumosMonitorDBServices().updateLumosMonitorInfo(updateInfo)
          .map { n => Ok(Json.obj("Updated" -> n))
                  Ok("Updated" + updateInfo.Multplier + ":" + updateInfo.Transmission)
               }.recover {
                   case e => BadRequest(e.getMessage + e.getStackTrace.mkString("\n"))
               }
    }

  @ApiOperation(nickname = "optionLumosMonitorInfo",
    value = "fake Lumos Monitor information ",
    notes = """return success or fail """,
    response = classOf[Int],
    httpMethod = "OPTIONS")
  def optionEliteMonitorInfo =
    Action.async {
      Future {
        Ok("Ok")
      }
    }
  @ApiOperation(nickname = "optionDelLumosMonitorInfo",
    value = "fake delete Lumos Monitor information ",
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
