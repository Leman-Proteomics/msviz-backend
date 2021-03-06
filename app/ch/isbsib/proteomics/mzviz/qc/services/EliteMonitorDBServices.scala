package ch.isbsib.proteomics.mzviz.qc.services

import java.text.{DateFormat, SimpleDateFormat}

import ch.isbsib.proteomics.mzviz.commons.services.{MongoNotFoundException, MongoDBService}
import ch.isbsib.proteomics.mzviz.qc.models.{EliteMonitorInfo, QcDeviceInfo}
import ch.isbsib.proteomics.mzviz.qc.services.JsonQCFormats._

import play.api.libs.json.{JsValue, JsString, Json}
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
class EliteMonitorDBServices (val db: DefaultDB) extends MongoDBService{
  val collectionName = "Elite.Monitor"
  val mainKeyName = "date"
  setIndexes(List(
    new Index(Seq("monitorDate"->IndexType.Ascending,"monitorIndex"->IndexType.Ascending),name = Some("monitor.elite_info"),unique = true))
  )
  /**
   * insert an elite monitor Information in an array
   * @param  deviceInfo
   * @return a Future of the number of elite monitor Information loaded
   */

  def insert(deviceInfo: EliteMonitorInfo):Future[Boolean] = {
    collection.insert(deviceInfo).map{
      case e: LastError if e.inError => false
      case _ => true
    }

  }
  /**
   * remove elite monitor Information from the mongodb
   * @param monitorDate,monitorIndex
   * @return
   */
  def delete(monitorDate:String,monitorIndex:String): Future[Boolean] = {
    val query = Json.obj("monitorDate" -> monitorDate,"monitorIndex"->monitorIndex)
    collection.remove(query).map {
      case e: LastError if e.inError => throw MongoNotFoundException(e.errMsg.get)
      case _ => true
    }
  }

  /**
   * retrieves a list of elite monitor Information
   * @return
   */
  def list: Future[List[EliteMonitorInfo]] = {
    collection.find(Json.obj()).sort(Json.obj("monitorDate" -> -1,"monitorIndex" -> -1)).cursor[EliteMonitorInfo].collect[List]()
  }

  /**
   * update elite monitor Information by a given date and index
   * @param updateMonitorInfo
   * @return
   */
  def updateEliteMonitorInfo(updateMonitorInfo:EliteMonitorInfo): Future[Boolean] = {
    val condition = Json.obj("monitorDate" -> updateMonitorInfo.monitorDate, "monitorIndex" ->updateMonitorInfo.monitorIndex)
    print(condition)
    val command = Json.obj("$set" -> Json.obj("Multplier1" -> updateMonitorInfo.Multplier1,"Multplier2" -> updateMonitorInfo.Multplier2,"Comment" -> updateMonitorInfo.Comment))
    print(command)
    collection.update(condition,command).map{
      case e: LastError if e.inError => throw MongoNotFoundException(e.errMsg.get)
      case _ => true
    }

  }

  /**
   * retieves all devinfos between given dates
   * @param d
   * @return
   */
  def findEliteMonitorInfoByDate(d: String): Future[Seq[EliteMonitorInfo]] = {
    val query = Json.obj("monitorDate" -> d)
    collection.find(query).cursor[EliteMonitorInfo].collect[List]()
  }


  /**
   * retieves all entries for a given date
   * @param d1,d2 the dates
   * @return
   */
  def findEliteMonitorInfoBtw2Date(d1: String,d2:String): Future[Seq[EliteMonitorInfo]] = {
    val query = Json.obj("monitorDate" ->Json.obj("$gte"-> d1,"$lte" ->d2))
    collection.find(query).sort(Json.obj("monitorDate" -> -1,"monitorIndex" -> -1)).cursor[EliteMonitorInfo].collect[List]()
  }

}


object EliteMonitorDBServices extends Controller with MongoController {
  val default = new EliteMonitorDBServices(db)

  /**
   * get the default database
   * @return
   */
  def apply() = default


}
