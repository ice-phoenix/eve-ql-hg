package info.icephoenix.eveql.filter

import com.beimin.eveapi.shared.wallet.transactions.ApiWalletTransaction
import org.streum.configrity.Configuration

import info.icephoenix.eveql.Util

class DateIntervalFilter(val config: Configuration) extends Filter {

  val fType = config[String]("type")

  if (fType != "dateInterval") {
    throw new IllegalArgumentException(
      "Cannot create DateIntervalFilter from '%s' filter".format(fType))
  }

  val from = Util.SDF.parse(config[String]("from"))
  val to = Util.SDF.parse(config[String]("to"))

  override def filter(set: Set[ApiWalletTransaction]): Set[ApiWalletTransaction] = {
    set.filter(t => {
      val date = t.getTransactionDateTime
      date.before(to) && date.after(from)
    })
  }
}
