import scala.collection.immutable.ListMap


/**
 * Created by julien on 24.04.15.
 * Still a test. For now, I will try to implement a function computing
 * a code file's score based on given attributes weights
 */

object FileScorer {

  val weights = (1,2,3)

  /**
   * Input: A mapping between a file and the list of the values of all its superfeatures.
   * Output: A mapping between a file and its score, the Map is sorted by decreasing score
   */
  def score(searchOutput : Map<CodeFileData, List[Int]): ListMap<CodeFileData, List[Int] = {
    val temp = searchOutput mapValues (x => x zip weights map (elem => elem._1*elem._2)) mapValues (x => x.foldLeft(0)(_+_))
    ListMap(temp.toSeq.sortWith(_._2 > _._2):_*)

  }
}
