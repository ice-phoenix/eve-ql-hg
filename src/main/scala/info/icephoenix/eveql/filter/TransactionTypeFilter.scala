package info.icephoenix.eveql.filter

import com.beimin.eveapi.shared.wallet.transactions.ApiWalletTransaction
import org.streum.configrity.Configuration

class TransactionTypeFilter(val config: Configuration) extends Filter {

  val fType = config[String]("type")

  if (fType != "transactionType") {
    throw new IllegalArgumentException(
      "Cannot create TransactionTypeFilter from '%s' filter".format(fType))
  }

  val value = config[String]("value")

  override def filter(set: Set[ApiWalletTransaction]): Set[ApiWalletTransaction] = {
    set.filter(t => {
      val tType = t.getTransactionType
      tType == value
    })
  }
}
