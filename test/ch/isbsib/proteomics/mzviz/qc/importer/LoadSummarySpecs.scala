package ch.isbsib.proteomics.mzviz.qc.importer

import java.text.SimpleDateFormat

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.specs2.mutable.Specification

import scala.reflect.io.File

/**
 * Created by qjolliet on 30/07/15.
 */
class LoadSummarySpecs extends Specification with ScalaFutures{
  implicit val defaultPatience =
    PatienceConfig(timeout = Span(15, Seconds), interval = Span(5000, Millis))

  val filename="./test/resources/qc/summary.txt"
  "Loading Summary" should {
    val qcSummary = LoadSummary(filename)
    val summaryEntry = qcSummary.getSummaryEntry
    val n = summaryEntry.length

    "numberOfEntries should be 5" in {
      n must equalTo(5)
    }
    "rawfile Date should be yymmdd " in {
      summaryEntry(0).rawfileInfomation.Date.value must equalTo("150508")
    }
    "rawfile Index should be nn" in {
      summaryEntry(0).rawfileInfomation.Index.value must equalTo("00")
    }
    "rawfile MS value should be 2963 " in {
      summaryEntry(0).MS must equalTo(2963)
    }
    "rawfile MMS value should be 1494" in {
      summaryEntry(0).MMS must equalTo(1494)
    }
    "rawfile MMS identify value should be 134 " in {
      summaryEntry(0).MmsIdentify must equalTo(134)
    }
    "rawfile peptideSeq should be 57 " in {
      summaryEntry(0).PeptideSeq must equalTo(57)
    }
    "rawfile peptideSeq should be 6.56 " in {
      summaryEntry(0).MMSIdentifyPtg must equalTo(6.56)
    }
    "rawfile Peaks Repeatedly Sequenced should be 7.08 " in {
      summaryEntry(0).PkRepSeqPtg must equalTo(7.08)
    }
    "rawfile Cmt should be '' " in {
      summaryEntry(0).Cmt must equalTo("")
    }

    "get information from rawfile" should {
      val raw_file = "Hela_300ng_Qex_Dionex_00_150507_00"
      val info = qcSummary.getInfo(raw_file)

      "rawfile date should be '150507'" in {
        info(info.length - 2) must equalTo("150507")
      }
      "rawfile Index should be '00'" in {
        info(info.length - 1) must equalTo("00")
      }
    }

  }



}
