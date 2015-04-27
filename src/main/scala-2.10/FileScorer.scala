import scala.collection.immutable.ListMap

/**
 * Created by julien on 24.04.15.
 * Still a test. For now, I will try to implement a function computing
 * a code file's score based on given attributes weights
 */
object FileScorer {
  def score(searchOutput : Map<CodeFileData, List[Int]): CodeFileData = {
    val temp = searchOutput mapValues (x => x zip weights map (elem => elem._1*elem._2) foldleft(0) (_+_) )
    ListMap(temp.toSeq.sortWith(_._2 > _._2):_*)

  }
}
