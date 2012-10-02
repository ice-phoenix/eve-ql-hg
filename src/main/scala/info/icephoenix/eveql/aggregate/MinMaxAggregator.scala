package info.icephoenix.eveql.aggregate

import com.beimin.eveapi.shared.wallet.transactions.ApiWalletTransaction
import org.streum.configrity.Configuration

class MinMaxAggregator(val config: Configuration) extends Aggregator {

  val fType = config[String]("type")

  if (fType != "minmax") {
    throw new IllegalArgumentException(
      "Cannot create MinMaxAggregator from '%s' aggregator".format(fType))
  }

  override def aggregate(set: Set[ApiWalletTransaction]): List[String] = {
    val min = set.minBy(_.getPrice).getPrice
    val max = set.maxBy(_.getPrice).getPrice
    List(
      "Max price: %d ISK".format(max.round),
      "Min price: %d ISK".format(min.round)
    )
  }
}
