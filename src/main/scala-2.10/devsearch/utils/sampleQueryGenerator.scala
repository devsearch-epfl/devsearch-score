package devsearch.utils

import org.apache.spark.{rdd, SparkConf, SparkContext}
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import devsearch.{CodeFileMetadata, BlobInputFormat, HeaderParser}
import org.apache.hadoop.fs.{Path, FileSystem}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.io.Text
import devsearch.features.CodeFileLocation


/*
My plan:
-----------------------------------------------------------------------------
Join codeFiles with their rank. Choose best ranked. Then create samples...
 */

/**
 * The rank of a certain repo.
 * @param owner
 * @param repo
 * @param rank
 */
case class Rank(owner: String, repo: String, rank: Double) extends java.io.Serializable

/**
 * A file from which we can extract sample queries
 * @param location
 * @param code
 */
case class SampleQuerySourceFile(location: CodeFileLocation, code: String) extends java.io.Serializable




/**
 * Created by hubi on 4/26/15.
 */
object sampleQueryGenerator {
  val repoRankPath = "/home/hubi/Documents/BigData/DevSearch/testRanking/*"
  val outputDir = "/home/hubi/Documents/BigData/DevSearch/rankedRepos"
  val blobPath = "/home/hubi/Documents/BigData/DevSearch/testrepos"



  /**
   * Transforms input String of form "owner/repo,rank" to case class Rank.
   * @param line
   * @return Rank
   */
  private def toRank (line: String): ((String, String), Double) = line.split(',') match {
    case Array(ownerRepo, rank, _*) => ((ownerRepo.split('/')(0), ownerRepo.split('/')(1)), rank.toDouble)
  }


  /**
   * Goes through RepoRank and returns the nbRepos best scored repos...
   * @return
   */
  private def getRanking(implicit sc: SparkContext): RDD[((String, String), Double)] = {
    val lines = sc.textFile(repoRankPath)
    lines map toRank
  }


  /**
   * takes a (header, content) tuple and returns it as SampleQuerySourceFile
   * @param headerContent
   * @return
   */
  private def toSampleSourceFile(headerContent: (Text, Text)): ((String, String), String) = headerContent match {
    case (header, content) => {
      val result = HeaderParser.parse(HeaderParser.parseBlobHeader, header.toString)
      if (result.isEmpty) null
      else {
        val metadata = result.get
        ((metadata.location.user, metadata.location.repoName), content.toString)
      }
    }
  }


  /**
   * Reads blobs and extracts sampleSourceFiles.
   * @param sc
   * @return
   */
  private def getSampleSourceFiles(implicit sc: SparkContext): RDD[((String, String), String)] = {
    // Go through each language directory and list all the contained blobs
    val fs = FileSystem.get(new java.net.URI(blobPath + "/*"), new Configuration())
    val blobPathList = fs.listStatus(new Path(blobPath))
      // Language directories
      .map(_.getPath)
      // Files in the language directories
      .flatMap(p => fs.listStatus(p))
      .map(_.getPath.toString)

    // Use custom input format to get header/snippet pairs from part files
    val headerSnippetPairs = sc.union(
      blobPathList.map(path =>
        sc.newAPIHadoopFile(path, classOf[BlobInputFormat], classOf[Text], classOf[Text])
      )
    )

    headerSnippetPairs map toSampleSourceFile
  }


  def main(args: Array[String]) {
    val sparkConf = new SparkConf().setAppName("Score").setMaster("local[4]")
    implicit val sc = new SparkContext(sparkConf)

    val ranking = getRanking

    val sampleQuerySourceFiles = getSampleSourceFiles


    //joined is of the form ((String, String), (Double, String))
    val joined = ranking.join(sampleQuerySourceFiles)











    /********************* TEST *********************/
    val best = joined sortBy(_._2._1, false) take(20)


    println("\n\n\n\n\n\n\n\n\n")
    for (x <- best)
      x match{
        case ((owner, repo),(rank, code)) => println(owner + ", " + repo + ": " + rank)
      }
  }

}
