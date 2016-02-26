package ch.isbsib.proteomics.mzviz.qc.models

import ch.isbsib.proteomics.mzviz.qc
import ch.isbsib.proteomics.mzviz.qc._

import scala.util.{Failure, Success, Try}


/**
 * Created by qjolliet on 28/07/15.
 */
//case class QcSummaryEntry(rawfileInfomation:RawfileInfomation,MS:Int,MMS:Int,MmsIdentify:Int,PeptideSeq:Int,MMSIdentifyPtg:Double,PkRepSeqPtg:Double)

case class QcSummaryEntry(rawfileInfomation:RawfileInfomation,MS:Int,MMS:Int,MmsIdentify:Int,PeptideSeq:Int,MMSIdentifyPtg:Double,PkRepSeqPtg:Double,MassStdDev:Double,Cmt:String)

//case class RawfileInfomation(proteinName:ProteinName,pQuantity:ProteinQuantity,machineName:MachineName,columnType:ColumnType,Date:QcDate,Index:QcIndex)
case class RawfileInfomation(proteinName:ProteinName,pQuantity:ProteinQuantity,machineName:MachineName,columnType:ColumnType,Date:QcDate,Index:QcIndex)


case class UpdateInfo(rawfileInfomation:RawfileInfomation,Cmt:String)

