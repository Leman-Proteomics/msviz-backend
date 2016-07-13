package ch.isbsib.proteomics.mzviz.qc.services

import ch.isbsib.proteomics.mzviz.commons.TempMongoDBForSpecs
import ch.isbsib.proteomics.mzviz.qc.models.{EliteMonitorInfo, QcDeviceInfo}

import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.concurrent.ScalaFutures
import ch.isbsib.proteomics.mzviz.qc.services.JsonQCFormats._
import org.specs2.mutable.Specification

/**
 * Created by qjolliet on 13/07/16.
 */
class EliteMonitorInfoMongoDBServicesSpecs extends Specification with ScalaFutures{
  implicit val defaultPatience = PatienceConfig(timeout = Span(15, Seconds), interval = Span(5000, Millis))

  /**
   * extends the temp mongo database and add a exp service above it
   */
  trait TempMongoDBService extends TempMongoDBForSpecs {
    val service = new EliteMonitorDBServices(db)
  }

  "insert" should {
    "insert  1" in new TempMongoDBService {
      val eliteMonitorInfo =EliteMonitorInfo("20150506","1","1320.31","1265.94","")
      val inserted= service.insert(eliteMonitorInfo).futureValue
      inserted mustEqual(true)
    }
  }

  "list" should {
    "all" in new TempMongoDBService {
      val eliteMonitorInfo =EliteMonitorInfo("20150506","1","1320.31","1265.94","")
      service.insert(eliteMonitorInfo).futureValue

      val eliteMonitorInfoList = service.list.futureValue
      Thread.sleep(200)
      eliteMonitorInfoList.size must equalTo(1)
      eliteMonitorInfoList(0).monitorDate mustEqual ("20150506")
      eliteMonitorInfoList(0).monitorIndex mustEqual ("1")
      eliteMonitorInfoList(0).Multplier1 mustEqual ("1320.31")
      eliteMonitorInfoList(0).Multplier2 mustEqual ("1265.94")
      eliteMonitorInfoList(0).Comment mustEqual ("")
    }
  }


  "delete" should {
    "remove 1 entry" in new TempMongoDBService {
      val eliteDevInfo =EliteMonitorInfo("20150506","1","1320.31","1265.94","")
      val inserted= service.insert(eliteDevInfo).futureValue
      inserted mustEqual(true)

      //remove
      val n: Boolean = service.delete("20150506","1").futureValue
      n must equalTo(true)
      val eliteMonitorInfo = service.list.futureValue
      Thread.sleep(200)
      eliteMonitorInfo.size must equalTo(0)

    }
  }

  "Update" should {
    "Update 1 entry" in new TempMongoDBService {
      val eliteDevInfo =EliteMonitorInfo("20150506","1","1320.31","1265.94","")
      val inserted= service.insert(eliteDevInfo).futureValue
      inserted mustEqual(true)

      //update
      val n: Boolean = service.updateEliteMonitorInfo("20150506","1","1320","1265","cleanning 1").futureValue
      n must equalTo(true)
      val eliteMonitorInfo = service.list.futureValue
      Thread.sleep(200)
      eliteMonitorInfo.size must equalTo(1)
      eliteMonitorInfo(0).monitorDate mustEqual ("20150506")
      eliteMonitorInfo(0).monitorIndex mustEqual ("1")
      eliteMonitorInfo(0).Multplier1 mustEqual ("1320")
      eliteMonitorInfo(0).Multplier2 mustEqual ("1265")
      eliteMonitorInfo(0).Comment mustEqual ("cleanning 1")

    }
  }

}
