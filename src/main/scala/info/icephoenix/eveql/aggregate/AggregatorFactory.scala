package info.icephoenix.eveql.aggregate

import org.streum.configrity.Configuration

object AggregatorFactory {
  def createAggregator(config: Configuration): Aggregator = {
    config[String]("type") match {
      case "avg" => new AvgAggregator(config)
      case "minmax" => new MinMaxAggregator(config)
      case unknown => throw new IllegalArgumentException(
        "Unknown aggregator type '%s'".format(unknown))
    }
  }
}
