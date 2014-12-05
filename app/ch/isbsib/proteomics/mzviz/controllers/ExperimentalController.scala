package ch.isbsib.proteomics.mzviz.controllers

import java.io.File
import javax.ws.rs.PathParam

import ch.isbsib.proteomics.mzviz.experimental.IdRun
import ch.isbsib.proteomics.mzviz.experimental.importer.LoaderMGF
import ch.isbsib.proteomics.mzviz.experimental.models.{RefSpectrum, ExpMSnSpectrum}
import ch.isbsib.proteomics.mzviz.experimental.services.ExpMongoDBService
import ch.isbsib.proteomics.mzviz.experimental.services.JsonExpFormats._
import com.wordnik.swagger.annotations._
import play.api.libs.Files
import play.api.mvc.{MultipartFormData, Request, Action, Controller}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.functional.syntax._
import play.api.libs.json._
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

// Reactive Mongo imports

import reactivemongo.api._

// Reactive Mongo plugin, including the JSON-specialized collection

import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection

/**
 * @author Alexandre Masselot
 */
@Api(value = "/exp", description = "experimental data access")
object ExperimentalController extends CommonController {

  def stats = Action.async {

    ExpMongoDBService().stats.map { st =>
      Ok(writesMap.writes(st))
    }
  }

  @ApiOperation(nickname = "listMSRunIds",
    value = "the list of run ids",
    notes = """from the parameter run-id at load time""",
    response = classOf[List[String]],
    httpMethod = "GET")
  def listMSRunIds = Action.async {
    ExpMongoDBService().listMsRunIds.map {
      ids => Ok(Json.obj("msRuns" -> ids.map(_.value)))
    }
  }

  @ApiOperation(nickname = "loadMSRun",
    value = "Loads an MGF peak list as a run",
    notes = """ run-id will be important to link with the mzid""",
    response = classOf[String],
    httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "mgf", value = "mgf peak list", required = true, dataType = "file", paramType = "body")
  ))
  def loadMSRun = Action.async(parse.multipartFormData) {
    request =>
      localFile("mgf", request)
        .flatMap {
        case (uploadedFile, filename) =>
          val idRun = request.body.dataParts.get("run-id").map(_.head)
          val msRun = LoaderMGF.load(uploadedFile.getAbsolutePath, idRun)
          ExpMongoDBService().insert(msRun)
      }
        .map { n => Ok(Json.obj("inserted" -> n))
      }.recover {
        case e => BadRequest(e.getMessage)
      }
  }

  @ApiOperation(nickname = "findExpSpectrum",
    value = "find a spectrum by run id and spectrum title",
    notes = """the tuple should be unique by indexing""",
    response = classOf[ExpMSnSpectrum],
    httpMethod = "GET")
  def findExpSpectrum(@ApiParam(value = """run id""", defaultValue = "") @PathParam("idRun") idRun: String,
                      @ApiParam(value = """spectrum title""", defaultValue = "") @PathParam("title") title: String) =
    Action.async {
      ExpMongoDBService().findSpectrumByRunIdAndTitle(IdRun(idRun), title)
        .map { case sp: ExpMSnSpectrum => Ok(Json.toJson(sp))}
        .recover {
        case e => BadRequest(e.getMessage + e.getStackTrace.mkString("\n"))
      }
    }

  @ApiOperation(nickname = "findAllRefSpectraByIdRun",
    value = "find all spectra for a given run id",
    notes = """Returns only the reference information (precursor & co)""",
    response = classOf[List[RefSpectrum]],
    httpMethod = "GET")
  def findAllRefSpectraByIdRun(@ApiParam(value = """run id""", defaultValue = "") @PathParam("idRun") idRun: String) =
    Action.async {
      ExpMongoDBService().findAllRefSpectraByIdRun(IdRun(idRun))
        .map { case sphList: List[JsObject] => Ok(Json.toJson(sphList))}
        .recover {
        case e => BadRequest(e.getMessage + e.getStackTrace.mkString("\n"))
      }
    }

  @ApiOperation(nickname = "deleteMSRun",
    value = "delete a run and all experimental data associated with it",
    notes = """No double check is done. Use with caution""",
    response = classOf[String],
    httpMethod = "DELETE")
  def deleteMSRun(idRun: String) = Action.async {
    ExpMongoDBService().delete(IdRun(idRun)).map { x =>
      Ok("OK")
    }
  }
}
