package info.icephoenix.eveql.processing

import com.beimin.eveapi.shared.wallet.transactions.ApiWalletTransaction

import info.icephoenix.eveql.aggregate.Aggregator
import info.icephoenix.eveql.filter.Filter
import info.icephoenix.eveql.misc.TradeGood

class Processor(tradeGood: TradeGood,
                filters: Map[String, Filter],
                aggregators: Map[String, Aggregator]) {

  def process(set: Set[ApiWalletTransaction]): List[String] = {
    var res = List.empty[String]
    res ::= "==="
    res ::= "Results for '%s':".format(tradeGood.what)
    res ::= "==="

    val groupedBy = set.groupBy(_.getTypeID)
    val typeMap = groupedBy.map(e => (e._2.head.getTypeName, e._1)).toMap
    val matchingTypes = typeMap.filter(e => e._1.matches(tradeGood.what))

    matchingTypes.foreach(e => {
      res :::= process(groupedBy(e._2), e._1, e._2)
    })

    res.reverse
  }

  private def process(set: Set[ApiWalletTransaction],
                      name: String,
                      id: Int): List[String] = {

    var res = List.empty[String]

    val fSet = tradeGood.filters.foldLeft(set)((s, f) => {
      val ff = filters(f)
      ff.filter(s)
    })

    if (fSet.isEmpty) {
      return res
    }

    res ::= "Matched '%s'".format(name)
    res :::= tradeGood.aggregators.foldLeft(List.empty[String])((l, a) => {
      val aa = aggregators(a)
      aa.aggregate(fSet) ::: l
    })

    "***" :: res.map("  " + _)
  }
}
