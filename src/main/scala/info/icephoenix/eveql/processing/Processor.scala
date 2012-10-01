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
    res ::= "Results for %s:".format(tradeGood.what)

    val fSet = tradeGood.filters.foldLeft(set)((s, f) => {
      val ff = filters(f)
      ff.filter(s)
    })

    val tradeGoodId = fSet.find(_.getTypeName.matches(tradeGood.what)) match {
      case None => -1
      case Some(t) => t.getTypeID
    }

    if (tradeGoodId < 0) {
      res ::= "None"
    } else {
      val ffSet = fSet.filter(_.getTypeID == tradeGoodId)
      val r = tradeGood.aggregators.foldLeft(List.empty[String])((l, a) => {
        val aa = aggregators(a)
        aa.aggregate(ffSet) :: l
      })
      res :::= r
    }

    res.reverse
  }
}
