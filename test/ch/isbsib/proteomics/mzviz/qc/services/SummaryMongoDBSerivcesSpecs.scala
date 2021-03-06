package ch.isbsib.proteomics.mzviz.qc.services

import ch.isbsib.proteomics.mzviz.commons.TempMongoDBForSpecs
import ch.isbsib.proteomics.mzviz.qc._
import ch.isbsib.proteomics.mzviz.qc.importer.LoadSummary
import ch.isbsib.proteomics.mzviz.qc.models.RawfileInfomation

import org.specs2.mutable.Specification
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.specs2.mutable.Specification
import ch.isbsib.proteomics.mzviz.qc.services.JsonQCFormats._
import play.api.libs.json.Json

import scala.util.parsing.json.JSON

/**
 * Created by qjolliet on 05/08/15.
 */
class SummaryMongoDBSerivcesSpecs extends Specification with ScalaFutures {
  implicit val defaultPatience =
    PatienceConfig(timeout = Span(15, Seconds), interval = Span(5000, Millis))

  /**
   * extends the temp mongo database and add a exp service above it
   */
  trait TempMongoDBService extends TempMongoDBForSpecs {
    val service = new SummaryMongoDBServices(db)
  }


  "insert" should {
    "insert  2" in new TempMongoDBService {
      val entries = LoadSummary("./test/resources/qc/summary.txt").getSummaryEntry
      val n: Int = service.insert(entries).futureValue
      n must equalTo(5)

      val qcSummaryEntry = service.findAllByDate("150508").futureValue
      Thread.sleep(200)
      qcSummaryEntry.size must equalTo(2)

      val idx = RawfileInfomation(ProteinName("Hela"),ProteinQuantity("300ng"),MachineName("Qex"),ColumnType("Dionex_00"),QcDate("150508"),QcIndex("00"))
      //val idx=Json.toJson("'proteinName':'Hela','pQuantity':'300ng','machineName':'Qex','columnType':'Dionex_00','Date':'150507','Index':'00'")
      val b: Boolean = service.updateCmtByRawfileInfo(idx,"hello").futureValue
      Thread.sleep(200)
      b must equalTo(true)

    }

  }

/*
  "Insert 13,delete 3 " should {
    "find 4" in new TempMongoDBService {
      val ins1 = service.insert(LoadSummary("./test/resources/qc/summary1.txt").getSummaryEntry).futureValue
      ins1 must equalTo(13)

      val n: Boolean = service.deleteAllByDate("150507").futureValue
      n must equalTo(true)
      val cnt = service.countSummary.futureValue
      cnt must equalTo(10)

      val qcSummaryEntry = service.findAllByDate("150520").futureValue
      Thread.sleep(200)
      qcSummaryEntry.size must equalTo(4)

      //val idx = RawfileInfomation(ProteinName("Hela"),ProteinQuantity("300ng"),MachineName("Qex"),ColumnType("Dionex_00"),QcDate("150507"),QcIndex("00"))
      val idx=Json.toJson("'proteinName':'Hela','pQuantity':'300ng','machineName':'Qex','columnType':'Dionex_00','Date':'150507','Index':'00'")
      val b: Boolean = service.updateCmtByRawfileInfo(idx,"hello").futureValue
      Thread.sleep(200)
      b must equalTo(true)


      //val upd=service.updateByRawfileInfo(idx,"hello").futureValue


    }

  }


  "Insert 13,find 13 " should {
    "find between date1 and date2" in new TempMongoDBService {
      val ins1 = service.insert(LoadSummary("./test/resources/qc/summary1.txt").getSummaryEntry).futureValue
      ins1 must equalTo(13)

      val qcSummaryEntry = service.findAllBtw2Date("150507", "150610").futureValue
      Thread.sleep(200)
      qcSummaryEntry.size must equalTo(10)
    }

  }

  "Insert 13,find 13 " should {
    "find all" in new TempMongoDBService {
      val ins1 = service.insert(LoadSummary("./test/resources/qc/summary1.txt").getSummaryEntry).futureValue
      ins1 must equalTo(13)

      val qcSummaryEntry = service.listAll.futureValue
      Thread.sleep(200)
      qcSummaryEntry.size must equalTo(13)
    }

  }*/

}