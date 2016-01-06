package ch.isbsib.proteomics.mzviz.qc.services

import java.text.{DateFormat, SimpleDateFormat}

import ch.isbsib.proteomics.mzviz.commons.services.{MongoNotFoundException, MongoDBService}
import ch.isbsib.proteomics.mzviz.qc.models.{RawfileInfomation, QcSummaryEntry}
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
    new Index(Seq("date"->IndexType.Ascending,"deviceType"->IndexType.Ascending),name = Some("info"),unique = true))
  )

}
object DeviceInfoMongoDBServices extends Controller with MongoController {
  val default = new SummaryMongoDBServices(db)

  /**
   * get the default database
   * @return
   */
  def apply() = default


}
