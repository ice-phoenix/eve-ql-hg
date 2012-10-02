package info.icephoenix.eveql.aggregate

import com.beimin.eveapi.shared.wallet.transactions.ApiWalletTransaction

trait Aggregator {

  def aggregate(set: Set[ApiWalletTransaction]): List[String]

}
