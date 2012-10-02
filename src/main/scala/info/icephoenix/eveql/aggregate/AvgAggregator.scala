package info.icephoenix.eveql.aggregate

import com.beimin.eveapi.shared.wallet.transactions.ApiWalletTransaction
import org.streum.configrity.Configuration

class AvgAggregator(val config: Configuration) extends Aggregator {

  val fType = config[String]("type")

  if (fType != "avg") {
    throw new IllegalArgumentException(
      "Cannot create AvgAggregator from '%s' aggregator".format(fType))
  }

  override def aggregate(set: Set[ApiWalletTransaction]): List[String] = {
    val data = set.foldLeft((0.0, 0))((r, e) => {
      val (sum, count) = r
      (sum + e.getPrice * e.getQuantity, count + e.getQuantity)
    })
    List("Average price: %d ISK".format((data._1 / data._2).round))
  }
}
