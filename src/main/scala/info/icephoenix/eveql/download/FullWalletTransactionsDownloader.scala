package info.icephoenix.eveql.download

import com.beimin.eveapi.core.ApiAuthorization
import com.beimin.eveapi.shared.wallet.transactions.{ApiWalletTransaction, AbstractWalletTransactionsParser}

import scala.collection.JavaConversions._
import scala.collection.mutable.{Set => MSet}

class FullWalletTransactionsDownloader {
  def download(wtp: AbstractWalletTransactionsParser,
                auth: ApiAuthorization,
                accountId: Int): Set[ApiWalletTransaction] = {
    val res = MSet.empty[ApiWalletTransaction]

    val data = wtp.getResponse(auth, accountId).getAll.toSet
    if (data.isEmpty) return res.toSet

    val beforeId = data.minBy(_.getTransactionID).getTransactionID
    data.foreach(res.add(_))

    download(wtp, auth, accountId, beforeId, res)

    return res.toSet
  }

  private def download(wtp: AbstractWalletTransactionsParser,
                        auth: ApiAuthorization,
                        accountId: Int,
                        beforeId: Long,
                        acc: MSet[ApiWalletTransaction]) {
    val data = wtp.getResponse(auth, accountId, beforeId).getAll
    if (data.isEmpty) return

    val newBeforeId = data.minBy(_.getTransactionID).getTransactionID
    if (newBeforeId == beforeId) return

    data.foreach(acc.add(_))

    download(wtp, auth, accountId, newBeforeId, acc)
  }
}
