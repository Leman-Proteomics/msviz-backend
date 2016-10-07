package ch.isbsib.proteomics.mzviz.qc.services

/**
 * Created by qjolliet on 03/10/16.
 */
import ch.isbsib.proteomics.mzviz.commons.services.{MongoNotFoundException, MongoDBService}
import ch.isbsib.proteomics.mzviz.qc.models.{QexTransInfo}
import ch.isbsib.proteomics.mzviz.qc.services.JsonQCFormats._

import play.api.libs.json.{Json}
import play.api.mvc.Controller
import play.modules.reactivemongo.MongoController
import reactivemongo.api._
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.core.commands.{Count, LastError}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
/**
 * Created by qjolliet on 13/07/16.
 */
class QexHFTransInfoDBServices (val db: DefaultDB) extends MongoDBService{
  val collectionName = "QexHF.Transmisson"
  val mainKeyName = "date"
  setIndexes(List(
    new Index(Seq("transDate"->IndexType.Ascending,"transIndex"->IndexType.Ascending),name = Some("transmission.qexhf_info"),unique = true))
  )
  /**
   * insert an elite monitor Information in an array
   * @param  transInfo
   * @return a Future of the number of elite monitor Information loaded
   */

  def insert(transInfo: QexTransInfo):Future[Boolean] = {
    collection.insert(transInfo).map{
      case e: LastError if e.inError => false
      case _ => true
    }

  }
  /**
   * remove qex transmission Information from the mongodb
   * @param  transDate,transIndex
   * @return
   */
  def delete(transDate:String,transIndex:String): Future[Boolean] = {
    val query = Json.obj("transDate" -> transDate,"transIndex"->transIndex)
    collection.remove(query).map {
      case e: LastError if e.inError => throw MongoNotFoundException(e.errMsg.get)
      case _ => true
    }
  }

  /**
   * retrieves a list of qex transmission Information
   * @return
   */
  def list: Future[List[QexTransInfo]] = {
    collection.find(Json.obj()).sort(Json.obj("transDate" -> -1,"transIndex" -> -1)).cursor[QexTransInfo].collect[List]()
  }

  /**
   * update qex transmission Information by a given date and index
   * @param updateTransInfo
   * @return
   */
  def updateQexTransInfo(updateTransInfo:QexTransInfo): Future[Boolean] = {
    val condition = Json.obj("transDate" -> updateTransInfo.transDate, "transIndex" ->updateTransInfo.transIndex)
    print(condition)
    val command = Json.obj("$set" -> Json.obj("TransResult" -> updateTransInfo.TransResult,"Comment" -> updateTransInfo.Comment))
    print(command)
    collection.update(condition,command).map{
      case e: LastError if e.inError => throw MongoNotFoundException(e.errMsg.get)
      case _ => true
    }

  }

  /**
   * retieves all transmission infomation by given dates
   * @param d
   * @return
   */
  def findQexTransInfoByDate(d: String): Future[Seq[QexTransInfo]] = {
    val query = Json.obj("transDate" -> d)
    collection.find(query).cursor[QexTransInfo].collect[List]()
  }

  /**
   * retieves all entries for a given date
   * @param d1,d2 the dates
   * @return
   */
  def findQexTransInfoBtw2Date(d1: String,d2:String): Future[Seq[QexTransInfo]] = {
    val query = Json.obj("transDate" ->Json.obj("$gte"-> d1,"$lte" ->d2))
    collection.find(query).sort(Json.obj("transDate" -> -1,"transIndex" -> -1)).cursor[QexTransInfo].collect[List]()
  }

}

object QexHFTransInfoDBServices extends Controller with MongoController {
  val default = new QexHFTransInfoDBServices(db)

  /**
   * get the default database
   * @return
   */
  def apply() = default


}

