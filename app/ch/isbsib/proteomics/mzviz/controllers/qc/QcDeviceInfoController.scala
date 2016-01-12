package ch.isbsib.proteomics.mzviz.controllers.qc

import javax.ws.rs.PathParam

import ch.isbsib.proteomics.mzviz.controllers.JsonCommonsFormats._
import ch.isbsib.proteomics.mzviz.controllers.CommonController
import ch.isbsib.proteomics.mzviz.qc.models.QcDeviceInfo

import ch.isbsib.proteomics.mzviz.qc.services.DeviceInfoMongoDBServices
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import ch.isbsib.proteomics.mzviz.qc.services.JsonQCFormats._

import play.api.libs.json.{JsObject, Json}
import com.wordnik.swagger.annotations._
import play.api.mvc.Action
import play.api.mvc.BodyParsers.parse
import play.mvc.BodyParser.AnyContent
import scala.concurrent.Future
import scala.util.{Failure, Success}
import play.api.Logger

/**
 * Created by qjolliet on 06/01/16.
 */
@Api(value = "/qc/deviceInfo", description = "Qc device information")
object QcDeviceInfoController extends CommonController{

  @ApiOperation(nickname = "AddDeviceInfo",
    value = "Add Device information",
    notes = """ insert device information in one date """,
    response = classOf[Boolean],
    httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = "deviceInfo", required = true, dataType = "application/json", paramType = "body")
  ))
  def addDeviceInfo=

    Action.async{
      request =>
        val deviceInfo:QcDeviceInfo = request.body.asText match {

          case Some(s) => Json.parse(s).as[QcDeviceInfo]
          case None => throw new Exception("you have to provider rawfileInfomation!")
        }
        DeviceInfoMongoDBServices().insert(deviceInfo)
          .map {
          n => Ok(Json.obj("inserted" -> n))
        }.recover {
          case e => BadRequest(e.getMessage + e.getStackTrace.mkString("\n"))
        }

    }

  @ApiOperation(nickname = "QcDeviceInformation",
    value = "List all added device Information",
    notes = """Returns only the QcSummaryEntry information """,
    response = classOf[Seq[QcDeviceInfo]],
    httpMethod = "GET")
  def list=
    Action.async {
      DeviceInfoMongoDBServices().list
        .map { qcDeviceInfo=> Ok(Json.toJson(qcDeviceInfo))
      }.recover {
        case e => BadRequest(e.getMessage + e.getStackTrace.mkString("\n"))
      }
    }

  @ApiOperation(nickname = "deleteQcSummaryEntryBtw2Date",
    value = "delete SummaryEntry between two dates",
    notes = """return success or fail """,
    response = classOf[QcDeviceInfo],
    httpMethod = "DELETE")
  def delete(
                               @ApiParam(value = """DevDate""", defaultValue = "") @PathParam("DevDate") devDate: String,
                               @ApiParam(value = """DevType""", defaultValue = "") @PathParam("DevType") devType: String) =
    Action.async {
      DeviceInfoMongoDBServices().delete(devDate,devType)
        .map { x => Ok(Json.obj("deleted" -> x))
      }.recover {
        case e => BadRequest(e.getMessage + e.getStackTrace.mkString("\n"))
      }
    }

  @ApiOperation(nickname = "optionQcDevInfo",
    value = "fake qc device information ",
    notes = """return success or fail """,
    response = classOf[Int],
    httpMethod = "OPTIONS")
  def optionQcDevInfo =
    Action.async {
      Future {
        Ok("Ok")
      }
    }
  @ApiOperation(nickname = "optionsQcDeviceInfo",
    value = "fake delete device information ",
    notes = """return success or fail """,
    response = classOf[Int],
    httpMethod = "OPTIONS")
  def optionDelDevInfo(@ApiParam(value = """DevDate""", defaultValue = "") @PathParam("DevDate") devDate: String,
                       @ApiParam(value = """DevType""", defaultValue = "") @PathParam("DevType") devType: String) =
    Action.async {
      Future {
        Ok("Ok")
      }
    }

}
