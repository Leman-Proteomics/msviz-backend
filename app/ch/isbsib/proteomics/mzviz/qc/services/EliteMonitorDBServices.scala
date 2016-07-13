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
    collection.find(Json.obj()).cursor[EliteMonitorInfo].collect[List]()
  }

  /**
   * update elite monitor Information by a given date and index
   * @param monitorDate, monitorIndex, multiplier1, multiplier2,comment
   * @return
   */
  def updateEliteMonitorInfo(monitorDate:String,monitorIndex:String,multiplier1: String,multiplier2:String,comment:String): Future[Boolean] = {
    val condition = Json.obj("MonitorDate" ->monitorDate,"MonitorIndex" ->monitorIndex)
    val command = Json.obj("Multiplier1" ->multiplier1,"Multiplier2" ->multiplier2,"Comment" -> comment)
    collection.update(condition,command).map{
      case e: LastError if e.inError => throw MongoNotFoundException(e.errMsg.get)
      case _ => true
    }

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
