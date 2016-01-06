package ch.isbsib.proteomics.mzviz.qc.services

import ch.isbsib.proteomics.mzviz.commons.TempMongoDBForSpecs

import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.concurrent.ScalaFutures
import ch.isbsib.proteomics.mzviz.qc.services.JsonQCFormats._

/**
 * Created by qjolliet on 06/01/16.
 */
class QcDeviceInfoMongoDBServicesSpecs {
  implicit val defaultPatience = PatienceConfig(timeout = Span(15, Seconds), interval = Span(5000, Millis))

  /**
   * extends the temp mongo database and add a exp service above it
   */
  trait TempMongoDBService extends TempMongoDBForSpecs {
    val service = new SummaryMongoDBServices(db)
  }


}
