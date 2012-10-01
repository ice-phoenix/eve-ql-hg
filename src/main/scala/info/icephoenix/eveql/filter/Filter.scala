package info.icephoenix.eveql.filter

import com.beimin.eveapi.shared.wallet.transactions.ApiWalletTransaction

trait Filter {

  def filter(set: Set[ApiWalletTransaction]): Set[ApiWalletTransaction]

}
