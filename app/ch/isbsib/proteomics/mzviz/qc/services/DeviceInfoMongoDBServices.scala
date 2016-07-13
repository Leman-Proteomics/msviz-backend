package ch.isbsib.proteomics.mzviz.qc.services

import java.text.{DateFormat, SimpleDateFormat}

import ch.isbsib.proteomics.mzviz.commons.services.{MongoNotFoundException, MongoDBService}
import ch.isbsib.proteomics.mzviz.qc.models.{QcDeviceInfo, RawfileInfomation, QcSummaryEntry}
import ch.isbsib.proteomics.mzviz.qc.services.JsonQCFormats._

import play.api.libs.iteratee.Enumerator
import play.api.libs.json.{JsValue, JsString, Json}
import play.api.mvc.Controller
import play.modules.reactivemongo.MongoController
import reactivemongo.api._
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.core.commands.{Count, LastError}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
/**
 * Created by qjolliet on 06/01/16.
 */
class DeviceInfoMongoDBServices (val db: DefaultDB) extends MongoDBService{
  val collectionName = "qc.device"
  val mainKeyName = "date"
  setIndexes(List(
    new Index(Seq("devDate"->IndexType.Ascending,"devType"->IndexType.Ascending),name = Some("info"),unique = true))
  )
  /**
   * insert a deviceInformation in an array
   * @param deviceInfo inserted
   * @return a Future of the number of deviceInformation loaded
   */

  def insert(deviceInfo: QcDeviceInfo):Future[Boolean] = {
    collection.insert(deviceInfo).map{
      case e: LastError if e.inError => false
      case _ => true
    }

  }
  /**
   * remove deviceInformation from the mongodb
   * @param devDate,devType
   * @return
   */
  def delete(devDate:String,devType:String): Future[Boolean] = {
    val query = Json.obj("devDate" -> devDate,"devType"->devType)
    collection.remove(query).map {
      case e: LastError if e.inError => throw MongoNotFoundException(e.errMsg.get)
      case _ => true
    }
  }

  /**
   * retrieves a list of deviceInformation
   * @return
   */
  def list: Future[List[QcDeviceInfo]] = {
    collection.find(Json.obj()).cursor[QcDeviceInfo].collect[List]()
  }

  /**
   * retieves all devinfos between given dates
   * @param d1,d2 the dates
   * @return
   */
  def findDevInfoBtw2Date(d1: String,d2:String): Future[Seq[QcDeviceInfo]] = {
    val query = Json.obj("devDate" ->Json.obj("$gte"-> d1,"$lte" ->d2))
    collection.find(query).sort(Json.obj("devDate" -> 1)).cursor[QcDeviceInfo].collect[List]()
  }

}

object DeviceInfoMongoDBServices extends Controller with MongoController {
  val default = new DeviceInfoMongoDBServices(db)

  /**
   * get the default database
   * @return
   */
  def apply() = default


}
