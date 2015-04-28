package devsearch.Utils.Utils

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

/**
 * Created by hubi on 4/26/15.
 */
object testQueryGenerator {
  val repoRankPath = "/home/hubi/Documents/BigData/DevSearch/testRanking/part-00001"
  val outputDir = "/home/hubi/Documents/BigData/DevSearch/rankedRepos"


  /*
  My plan:
  -----------------------------------------------------------------------------
  list of CodeFiles out of the best ranked repos. Then create samples...
   */


  /**
   * Goes through RepoRank and returns the nbRepos best scored repos...
   * @param nbRepos
   * @return
   */
  private def getBestRepos(nbRepos: Int)(implicit sc: SparkContext): RDD[(String, Double)] = {
    var ranking = sc.textFile(repoRankPath).map((s: String) => (s.split(',')(0), s.split(',')(1).toDouble)).sortBy(_._2, true)

    ranking



  }


  def main(args: Array[String]) {
    implicit val sc = new SparkContext(new SparkConf().setAppName("Score").setMaster("local[4]"))

    getBestRepos(100).saveAsTextFile(outputDir)

  }

}
