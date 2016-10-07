package ch.isbsib.proteomics.mzviz.controllers.qc

import javax.ws.rs.PathParam

import ch.isbsib.proteomics.mzviz.controllers.CommonController
import ch.isbsib.proteomics.mzviz.qc.models.QexTransInfo
import ch.isbsib.proteomics.mzviz.qc.services.JsonQCFormats._
import ch.isbsib.proteomics.mzviz.qc.services.{QexHFTransInfoDBServices}
import com.wordnik.swagger.annotations._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc.Action

import scala.concurrent.Future

/**
 * Created by qjolliet on 25/07/16.
 */
@Api(value = "/qc/qexHFTransInfo", description = "Qc Qex Transmission Information")
object QexHFTransInfoController extends CommonController{

  @ApiOperation(nickname = "AddQexTransInfo",
    value = "Add QexHF Transmission information",
    notes = """ insert Qex Transmission information by a date """,
    response = classOf[Boolean],
    httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = "qexHFTransInfo", required = true, dataType = "application/json", paramType = "body")
  ))
  def addQexTransInfo=

    Action.async{
      request =>
        val qexHFTransInfo:QexTransInfo = request.body.asText match {

          case Some(s) => Json.parse(s).as[QexTransInfo]
          case None => throw new Exception("you have to provider rawfileInfomation!")
        }
        QexHFTransInfoDBServices().insert(qexHFTransInfo)
          .map {
          n => Ok(Json.obj("inserted" -> n))
        }.recover {
          case e => BadRequest(e.getMessage + e.getStackTrace.mkString("\n"))
        }

    }

  @ApiOperation(nickname = "QexHFTransInformation",
    value = "List all added QexHF transmission Information",
    notes = """Returns only the QexHF transmission information """,
    response = classOf[Seq[QexTransInfo]],
    httpMethod = "GET")
  def list=
    Action.async {
      QexHFTransInfoDBServices().list
        .map { qcDeviceInfo=> Ok(Json.toJson(qcDeviceInfo))
      }.recover {
        case e => BadRequest(e.getMessage + e.getStackTrace.mkString("\n"))
      }
    }

  @ApiOperation(nickname = "findQexHFTransInformation",
    value = "find QexHFTransInfo by date",
    notes = """Returns only the QexHF Transmission information """,
    response = classOf[Seq[QexTransInfo]],
    httpMethod = "GET")
  def findQexTransInfoByDate(@ApiParam(value = """transDate""", defaultValue = "") @PathParam("Date") transDate: String) =
    Action.async {
      QexHFTransInfoDBServices().findQexTransInfoByDate(transDate)
        .map { qexTransInfo => Ok(Json.toJson(qexTransInfo))
      }.recover {
        case e => BadRequest(e.getMessage + e.getStackTrace.mkString("\n"))
      }
    }

  @ApiOperation(nickname = "findQexHFTransInformation",
    value = "find findQexHFTransInformation between 2 dates",
    notes = """Returns only the QexHFTransInfo information """,
    response = classOf[Seq[QexTransInfo]],
    httpMethod = "GET")
  def findQexTransInfoBtw2Date(@ApiParam(value = """transDate1""", defaultValue = "") @PathParam("Date1") transDate1: String,
                                   @ApiParam(value = """transDate2""", defaultValue = "") @PathParam("Date2") transDate2: String) =
    Action.async {
      QexHFTransInfoDBServices().findQexTransInfoBtw2Date(transDate1,transDate2)
        .map { qexTransInfo => Ok(Json.toJson(qexTransInfo))
      }.recover {
        case e => BadRequest(e.getMessage + e.getStackTrace.mkString("\n"))
      }
    }

  @ApiOperation(nickname = "deleteQexHFTransInfo",
    value = "delete QexHF Transmission Infomation by date",
    notes = """return success or fail """,
    response = classOf[QexTransInfo],
    httpMethod = "DELETE")
  def delete(
              @ApiParam(value = """TransDate""", defaultValue = "") @PathParam("TransDate") transDate: String,
              @ApiParam(value = """TransIndex""", defaultValue = "") @PathParam("TransIndex") transIndex: String) =
    Action.async {
      QexHFTransInfoDBServices().delete(transDate,transIndex)
        .map { x => Ok(Json.obj("deleted" -> x))
      }.recover {
        case e => BadRequest(e.getMessage + e.getStackTrace.mkString("\n"))
      }
    }

  @ApiOperation(nickname = "updateQexHFTransInfo",
    value = "update QexHF Transmission Infomation by a date",
    notes = """Returns new Elite Monitor Infomation """,
    response = classOf[Boolean],
    httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = "updateInfo", required = true, dataType = "application/json", paramType = "body")
  ))
  def updateQexTransInfo =
    Action.async {
      request =>
        val updateInfo:QexTransInfo = request.body.asText match {

          case Some(s) => Json.parse(s).as[QexTransInfo]
          case None => throw new Exception("you have to provider QexHFTransInfo!")
        }
        println("print upinfo"+ updateInfo.TransResult + ":" + updateInfo.Comment)
        QexHFTransInfoDBServices().updateQexTransInfo(updateInfo)
          .map { n => Ok(Json.obj("Updated" -> n))
          Ok("Updated" + updateInfo.TransResult )
        }.recover {
          case e => BadRequest(e.getMessage + e.getStackTrace.mkString("\n"))
        }
    }

  @ApiOperation(nickname = "optionQexHFTransInfo",
    value = "fake QexHF Transmission information ",
    notes = """return success or fail """,
    response = classOf[Int],
    httpMethod = "OPTIONS")
  def optionQexTransInfo =
    Action.async {
      Future {
        Ok("Ok")
      }
    }
  @ApiOperation(nickname = "optionDelQexHFTransInfo",
    value = "fake delete QexHF Transmission information ",
    notes = """return success or fail """,
    response = classOf[Int],
    httpMethod = "OPTIONS")
  def optionDelQexTransInfo(@ApiParam(value = """TransDate""", defaultValue = "") @PathParam("TransDate") TransDate: String,
                                @ApiParam(value = """TransIndex""", defaultValue = "") @PathParam("MonitorIndex") TransIndex: String) =
    Action.async {
      Future {
        Ok("Ok")
      }
    }

}
