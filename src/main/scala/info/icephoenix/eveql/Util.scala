package info.icephoenix.eveql

import java.text.{DecimalFormat, SimpleDateFormat}
import org.streum.configrity.converter.ValueConverter

import info.icephoenix.eveql.misc.TradeGood

object Util {

  object KeyType {
    val Account = 1
    val Character = 2
    val Corporation = 3
  }

  val SDF = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss")

  val DF = new DecimalFormat("###,###,###,###,###.## ISK")

  implicit object TradeGoodConverter extends ValueConverter[TradeGood] {
    val regex = """(.*)\s*\$\$\s*(.*)\s*\$\$\s*(.*)""".r

    def parse(s: String) = {
      val m = regex.findFirstMatchIn(s).getOrElse(
        throw new IllegalArgumentException(
          "Incorrect trade good format: '%s'".format(s))
      )

      val name = m.group(1).trim()
      val filters = m.group(2).split(',').map(_.trim).toSet
      val aggregators = m.group(3).split(',').map(_.trim).toSet

      new TradeGood(name, filters, aggregators)
    }
  }

}
