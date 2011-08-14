package org.specs2
package reporter
import specification._

class StatisticsRepositorySpec extends Specification { def is = sequential ^
                                                                                                            p^
  "A statistics repository can"                                                                             ^
    "store / retrieve statistics per specification name"                                                    ! repo().e1^
    "store statistics with a new timestamp so that the retrieval gets the latest statistics"                ! repo().e2^
    "store / retrieve a list of results associated to a spec name"                                          ! repo().e3^
                                                                                                            end


  case class repo() extends DefaultStatisticsRepository with BeforeAfter {
    def before = {
      Seq(specName1, specName2) foreach { s => new java.io.File(specStatsPath(s)).delete }
    }
    def after = before
    def execute(fs: Fragments) = FragmentExecution.executeExamples(fs)

    val specName1 = SpecName("spec1")
    val specName2 = SpecName("spec2")
    val (stats1, stats2) = (Stats(failures = 3), Stats(failures = 4))

    def e1 = this {
      storeStatistics(specName1, stats1)
      storeStatistics(specName2, stats2)
      getStatistics(specName1) must_== Some(Stats(failures = 3))
    }

    def e2 = this {
      storeStatistics(specName1, stats1)
      storeStatistics(specName1, stats2)
      getStatistics(specName1) must_== Some(Stats(failures = 4))
    }

    def e3 = this {
      storeResults(specName1, execute("e1" ! failure: Fragments))
      pending
    }
  }
}