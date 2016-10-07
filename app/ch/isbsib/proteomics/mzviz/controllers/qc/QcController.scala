package ch.isbsib.proteomics.mzviz.controllers.qc

import java.io.File
import java.text.SimpleDateFormat
import javax.ws.rs.PathParam

import ch.isbsib.proteomics.mzviz.controllers.JsonCommonsFormats._
import ch.isbsib.proteomics.mzviz.controllers.CommonController

import ch.isbsib.proteomics.mzviz.qc._
import ch.isbsib.proteomics.mzviz.qc.importer.LoadSummary
import ch.isbsib.proteomics.mzviz.qc.models._
import ch.isbsib.proteomics.mzviz.qc.services.SummaryMongoDBServices
import com.wordnik.swagger.annotations._
import play.api.Play.current
import play.api.cache.Cached
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import ch.isbsib.proteomics.mzviz.qc.services.JsonQCFormats._
import play.api.libs.json._
import com.wordnik.swagger.annotations._
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Action
import play.api.mvc.BodyParsers.parse
import play.mvc.BodyParser.AnyContent

import scala.concurrent.Future
import scala.util.{Failure, Success}
import play.api.Logger

/**
 * Created by qinfang Jolliet on 12/08/15.
 */


@Api(value = "/qc", description = "Qc data access")
object QcController extends CommonController {

  @ApiOperation(nickname = "loadSummary",
    value = "Loads QcSummary file",
    notes = """ analyse Summary column's value by dates""",
    response = classOf[String],
    httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = "QcSummary file", required = true, dataType = "text/plain", paramType = "body")
  ))
  def loadQcSummary() = Action.async(parse.temporaryFile) {
    request => val entries = LoadSummary(request.body.file).getSummaryEntry
      SummaryMongoDBServices().insert(entries).map{n => Ok(Json.obj("inserted" -> n))

      Ok("yo" + request.body.file.toString)
      }.recover {
        case e => BadRequest(Json.toJson(e))
    }
  }

  @ApiOperation(nickname = "QcSummaryEntryListAll",
    value = "List all SummaryEntry",
    notes = """Returns only the QcSummaryEntry information """,
    response = classOf[Seq[QcSummaryEntry]],
    httpMethod = "GET")
  def listAll=
    Action.async {
      SummaryMongoDBServices().listAll
        .map { qcSummaryEntry => Ok(Json.toJson(qcSummaryEntry))
      }.recover {
        case e => BadRequest(e.getMessage + e.getStackTrace.mkString("\n"))
      }
    }

  @ApiOperation(nickname = "findQcSummaryEntryByDate",
    value = "find SummaryEntry by date",
    notes = """Returns only the QcSummaryEntry information """,
    response = classOf[Seq[QcSummaryEntry]],
    httpMethod = "GET")
  def findQcSummaryEntryByDate(@ApiParam(value = """QcDate""", defaultValue = "") @PathParam("Date") qcDate: String) =
    Action.async {
      SummaryMongoDBServices().findAllByDate(qcDate)
        .map { qcSummaryEntry => Ok(Json.toJson(qcSummaryEntry))
      }.recover {
        case e => BadRequest(e.getMessage + e.getStackTrace.mkString("\n"))
      }
    }

  @ApiOperation(nickname = "findQcSummaryEntryBtw2Date",
    value = "find SummaryEntry between two dates",
    notes = """Returns only the QcSummaryEntry information """,
    response = classOf[Seq[QcSummaryEntry]],
    httpMethod = "GET")
  def findQcSummaryBtw2Date(
                             @ApiParam(value = """QcDate1""", defaultValue = "") @PathParam("Date") qcDate1: String,
                               @ApiParam(value = """QcDate2""", defaultValue = "") @PathParam("Date") qcDate2: String) =
    Action.async {
      SummaryMongoDBServices().findAllBtw2Date(qcDate1,qcDate2)
        .map { qcSummaryEntry => Ok(Json.toJson(qcSummaryEntry))
      }.recover {
        case e => BadRequest(e.getMessage + e.getStackTrace.mkString("\n"))
      }
    }

    @ApiOperation(nickname = "UpdateQcSummaryByRawfileInfo",
      value = "update SummaryEntry by rawfileInfomation",
      notes = """update QcSummaryEntry information """,
      response = classOf[Boolean],
      httpMethod = "POST")
    @ApiImplicitParams(Array(
      new ApiImplicitParam(name = "body", value = "updateInfo", required = true, dataType = "application/json", paramType = "body")
    ))
   def updateCmtByRawfileInfo=

      Action.async {
        request =>
          val updateInfo:UpdateInfo = request.body.asText match {

            case Some(s) => Json.parse(s).as[UpdateInfo]
            case None => throw new Exception("you have to provider rawfileInfomation!")
          }
          println("print upinfo"+ updateInfo.Cmt)
          SummaryMongoDBServices().updateCmtByRawfileInfo(updateInfo.rawfileInfomation,updateInfo.Cmt)
                    .map {
                    n => Ok(Json.obj("Updated" -> n))
                      Ok("update" + updateInfo.Cmt)

                  }.recover {
            case e => BadRequest(e.getMessage + e.getStackTrace.mkString("\n"))
          }

      }

  @ApiOperation(nickname = "UpdateQcSelByRawfileInfo",
    value = "update Summary Selectionby rawfileInfomation",
    notes = """update QcSummaryEntry Selection """,
    response = classOf[Boolean],
    httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = "updateInfo", required = true, dataType = "application/json", paramType = "body")
  ))
  def updateInfoByRawfileInfo(@ApiParam(name = "updateType", value = "update column type") @PathParam("updateType") updateType: String) =

            updateType match {
              case "sel" =>
                Action.async {
                request =>
                  val updateInfo: UpdateSel = request.body.asText match {
                    case Some(s) => Json.parse(s).as[UpdateSel]
                    case None => throw new Exception("you have to provider rawfileInfomation!")
                  }
                  SummaryMongoDBServices().updateSelByRawfileInfo(updateInfo.rawfileInfomation, updateInfo.selFlg)
                    .map {
                    n => Ok(Json.obj("Updated" -> n))
                      Ok("update" + updateInfo.selFlg)

                  }.recover {
                    case e => BadRequest(e.getMessage + e.getStackTrace.mkString("\n"))
                  }
                }
              case "cmt" =>
                Action.async {
                  request =>
                    val updateInfo: UpdateInfo = request.body.asText match {
                      case Some(s) => Json.parse(s).as[UpdateInfo]
                      case None => throw new Exception("you have to provider rawfileInfomation!")
                    }
                    SummaryMongoDBServices().updateCmtByRawfileInfo(updateInfo.rawfileInfomation, updateInfo.Cmt)
                      .map {
                      n => Ok(Json.obj("Updated" -> n))
                        Ok("update" + updateInfo.Cmt)

                    }.recover {
                      case e => BadRequest(e.getMessage + e.getStackTrace.mkString("\n"))
                    }
                }

            }






  @ApiOperation(nickname = "deleteQcSummaryEntryBtw2Date",
    value = "delete SummaryEntry between two dates",
    notes = """return success or fail """,
    response = classOf[Seq[QcSummaryEntry]],
    httpMethod = "DELETE")
  def deleteQcSummaryBtw2Date(
                             @ApiParam(value = """QcDate1""", defaultValue = "") @PathParam("Date") qcDate1: String,
                             @ApiParam(value = """QcDate2""", defaultValue = "") @PathParam("Date") qcDate2: String) =
    Action.async {
      SummaryMongoDBServices().deleteAllBtw2Date(qcDate1,qcDate2)
        .map { x => Ok(Json.obj("deleted" -> x))
      }.recover {
        case e => BadRequest(e.getMessage + e.getStackTrace.mkString("\n"))
      }
    }

  @ApiOperation(nickname = "optionsQcSummary",
    value = "fake SummaryEntry between two dates",
    notes = """return success or fail """,
    response = classOf[Int],
    httpMethod = "OPTIONS")
  def optionsDelSummary(@ApiParam(value = """QcVal1""", defaultValue = "") @PathParam("Val1") val1: String,
                       @ApiParam(value = """QcVal2""", defaultValue = "") @PathParam("Val2") val2: String) =
    Action.async {
      Future {
        Ok("Ok")
      }
    }

  @ApiOperation(nickname = "optionsUpdateCmt",
    value = "fake updateCmt ",
    notes = """return success or fail """,
    response = classOf[Int],
    httpMethod = "OPTIONS")
  def optionsQcSummary=
    Action.async {
      Future {
        Ok("Ok")
      }
    }

  @ApiOperation(nickname = "deleteQcSummaryByDate",
    value = "delete QcSummaryEntry data by Date",
    notes = """only can delete one day's data""",
    response = classOf[String],
    httpMethod = "DELETE")
  def deleteQcSummaryByDate(qcDate: String) = Action.async {
    SummaryMongoDBServices().deleteAllByDate(qcDate).map { x=> Ok(Json.obj("deleted" -> x))

    }
  }

}
