package ch.isbsib.proteomics.mzviz.qc.services

import java.text.{DateFormat, SimpleDateFormat}

import ch.isbsib.proteomics.mzviz.commons.services.{MongoNotFoundException, MongoDBService}
import ch.isbsib.proteomics.mzviz.qc.models.{RawfileInfomation, QcSummaryEntry}


import ch.isbsib.proteomics.mzviz.qc.services.JsonQCFormats._
import ch.isbsib.proteomics.mzviz.theoretical.SequenceSource
import org.joda.time.format.ISODateTimeFormat
import play.api.libs.iteratee.Enumerator
import play.api.libs.json.{JsValue, JsString, Json}
import play.api.mvc.Controller
import play.modules.reactivemongo.MongoController
import reactivemongo.api._
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson
import reactivemongo.bson.BSON
import reactivemongo.core.commands.{Count, LastError}
//import sun.plugin.javascript.JSObject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by qjolliet on 04/08/15.test from new PC
 */
class SummaryMongoDBServices(val db: DefaultDB) extends MongoDBService  {
  val collectionName = "qc.summary"
  val mainKeyName = "rawfileInfomation.Date"

  setIndexes(List(
    new Index(Seq("rawfileInfomation.Date"->IndexType.Ascending,"rawfileInfomation.Index"->IndexType.Ascending),name = Some("Date"),unique = false),
    new Index(
      Seq("rawfileInfomation" -> IndexType.Ascending), name = Some("RawfileInfomation"),unique = true))
  )

  /**
   * insert a list of Summary entries
   * @param   entries to be inserted
   * @return a Future of the number of entries loaded
   */

  def insert(entries: Seq[QcSummaryEntry]):Future[Int] ={
    val enumerator = Enumerator.enumerate(entries)
    collection.bulkInsert(enumerator)
  }

  /**
   * retieves all entries
   * @return
   */
  def listAll: Future[Seq[QcSummaryEntry]] = {
    collection.find(Json.obj()).sort(Json.obj("rawfileInfomation.Date" -> -1,"rawfileInfomation.Index"->1)).cursor[QcSummaryEntry].collect[List]()
  }
  /**
   * retieves all entries for a given date
   * @param d the date
   * @return
   */
  def findAllByDate(d: String): Future[Seq[QcSummaryEntry]] = {
    val query = Json.obj("rawfileInfomation.Date" -> d)
      collection.find(query).cursor[QcSummaryEntry].collect[List]()
  }
  /**
   * retieves all entries for a given date
   * @param d1,d2 the dates
   * @return
   */
  def findAllBtw2Date(d1: String,d2:String): Future[Seq[QcSummaryEntry]] = {
    val query = Json.obj("rawfileInfomation.Date" ->Json.obj("$gte"-> d1,"$lte" ->d2))
    collection.find(query).sort(Json.obj("rawfileInfomation.Date" -> -1,"rawfileInfomation.Index"->1)).cursor[QcSummaryEntry].collect[List]()
  }


  /**
   * retieves all entries for a given date
   * @param d1,d2 the dates
   * @return
   */
  def deleteAllBtw2Date(d1: String,d2:String): Future[Boolean] = {
    val query = Json.obj("rawfileInfomation.Date" ->Json.obj("$gte"-> d1,"$lte" ->d2))
    collection.remove(query).map{
      case e: LastError if e.inError => throw MongoNotFoundException(e.errMsg.get)
      case _ => true
    }
  }



  def updateCmtByRawfileInfo(idx:RawfileInfomation,cmt:String): Future[Boolean] = {
    collection.update(Json.obj("rawfileInfomation"->idx),Json.obj("$set"->Json.obj("Cmt"->cmt))).map{
      case e: LastError if e.inError => throw MongoNotFoundException(e.errMsg.get)
      case _ => true
    }
  }

  def updateSelByRawfileInfo(idx:RawfileInfomation,selFlg:Boolean): Future[Boolean] = {
    collection.update(Json.obj("rawfileInfomation"->idx),Json.obj("$set"->Json.obj("selFlg"->selFlg))).map{
      case e: LastError if e.inError => throw MongoNotFoundException(e.errMsg.get)
      case _ => true
    }
  }


  /**
   *
   * remove all entries from the mongodb
   * @param d the date
   * @return
   */
  def deleteAllByDate(d: String): Future[Boolean] = {
    val query = Json.obj("rawfileInfomation.Date" -> d)
    collection.remove(query).map {
      case e: LastError if e.inError => throw MongoNotFoundException(e.errMsg.get)
      case _ => true
    }
  }

  /**
   * count the number of Entries
   * @return
   */
  def countSummary: Future[Int] = {
    db.command(Count(collectionName))
  }
}



object SummaryMongoDBServices extends Controller with MongoController {
  val default = new SummaryMongoDBServices(db)

  /**
   * get the default database
   * @return
   */
  def apply() = default


}