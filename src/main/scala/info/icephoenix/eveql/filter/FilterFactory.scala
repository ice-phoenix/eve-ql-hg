package info.icephoenix.eveql.filter

import org.streum.configrity.Configuration

object FilterFactory {

  def createFilter(config: Configuration): Filter = {
    config[String]("type") match {
      case "dateInterval" => new DateIntervalFilter(config)
      case "transactionType" => new TransactionTypeFilter(config)
      case unknown => throw new IllegalArgumentException(
        "Unknown filter type '%s'".format(unknown))
    }
  }

}
