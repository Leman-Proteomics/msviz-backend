package ch.isbsib.proteomics.mzviz.matches.models

/**
 * @author Roman Mylonas, Trinidad Martin & Alexandre Masselot
 *         copyright 2014-2015, SIB Swiss Institute of Bioinformatics
 */


case class PepMatchInfo(
                         score: IdentScore,
                         numMissedCleavages: Option[Int],
                         massDiff: Option[Double],
                         rank: Option[Int],
                         totalNumIons: Option[Int],
                         chargeState: Option[Int],
                         isRejected: Option[Boolean]
                         )
