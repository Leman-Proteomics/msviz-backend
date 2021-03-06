package ch.isbsib.proteomics.mzviz.matches.models

/**
 * @author Roman Mylonas, Trinidad Martin & Alexandre Masselot
 * copyright 2014-2015, SIB Swiss Institute of Bioinformatics
 */



case class ProteinMatch (proteinRef: ProteinRef,
                         previousAA: Option[String],
                         nextAA: Option[String],
                         startPos: Int,
                         endPos: Int,
                         isDecoy: Option[Boolean])
