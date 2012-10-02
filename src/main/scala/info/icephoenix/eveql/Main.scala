package info.icephoenix.eveql

import com.beimin.eveapi.account.apikeyinfo.ApiKeyInfoParser
import com.beimin.eveapi.account.characters.CharactersParser
import com.beimin.eveapi.character.wallet.transactions.{WalletTransactionsParser => CharTransactionsParser}
import com.beimin.eveapi.core.ApiAuthorization
import com.beimin.eveapi.corporation.sheet.CorpSheetParser
import com.beimin.eveapi.corporation.wallet.transactions.{WalletTransactionsParser => CorpTransactionsParser}
import org.streum.configrity.Configuration

import info.icephoenix.eveql.aggregate.AggregatorFactory
import info.icephoenix.eveql.download.FullWalletTransactionsDownloader
import info.icephoenix.eveql.filter.FilterFactory
import info.icephoenix.eveql.misc._
import info.icephoenix.eveql.processing.Processor
import info.icephoenix.eveql.Util._

import scala.collection.JavaConversions._

object Main {
  def main(args: Array[String]) {

    val configFileName = if (args.length > 0) {
      args(0)
    } else {
      "test.conf"
    }

    try {
      val config = Configuration.load(configFileName)

      val id = config[Int]("apiKeyInfo.id")
      val vCode = config[String]("apiKeyInfo.vCode")
      val auth = new ApiAuthorization(id, vCode)

      val downloader = new FullWalletTransactionsDownloader()

      val transactions = getApiKeyType(auth) match {
        case KeyType.Account => {
          val charName = config[String]("charInfo.name")
          CharactersParser.getInstance()
          .getResponse(auth)
          .getAll
          .find(_.getName == charName) match {
            case None => throw new IllegalArgumentException(
              "Cannot find character '%s'".format(charName))
            case Some(char) => {
              downloader.download(
                CharTransactionsParser.getInstance(),
                new ApiAuthorization(id, char.getCharacterID, vCode),
                1000
              )
            }
          }
        }

        case KeyType.Character => {
          downloader.download(
            CharTransactionsParser.getInstance(),
            new ApiAuthorization(id, vCode),
            1000
          )
        }

        case KeyType.Corporation => {
          val walletName = config[String]("corpName.walletName")
          CorpSheetParser.getInstance()
          .getResponse(auth)
          .getWalletDivisions
          .find(_._2 == walletName) match {
            case None => throw new IllegalArgumentException(
              "Cannot find wallet '%s'".format(walletName))
            case Some(wd) => {
              downloader.download(
                CorpTransactionsParser.getInstance(),
                new ApiAuthorization(id, vCode),
                wd._1
              )
            }
          }
        }
      }

      val goodsConfig = config.detach("tradeGoods")
      val goods = goodsConfig[List[TradeGood]]("list")

      val filtersConfig = config.detach("filters")
      val allFilters = filtersConfig[List[String]]("all")
      val filters = allFilters.map(e => {
        val fName = e
        val fConfig = filtersConfig.detach(fName)
        val filter = FilterFactory.createFilter(fConfig)
        (fName, filter)
      }).toMap

      val aggregatorsConfig = config.detach("aggregators")
      val allAggregators = aggregatorsConfig[List[String]]("all")
      val aggregators = allAggregators.map(e => {
        val aName = e
        val aConfig = aggregatorsConfig.detach(aName)
        val aggregator = AggregatorFactory.createAggregator(aConfig)
        (aName, aggregator)
      }).toMap

      val cpus = goods.map(new Processor(_, filters, aggregators))

      cpus.foreach(cpu => {
        cpu.process(transactions).foreach(println(_))
      })

    } catch {
      case ex: Exception => ex.printStackTrace()
    }
  }

  def getApiKeyType(auth: ApiAuthorization): Int = {
    val resp = ApiKeyInfoParser.getInstance().getResponse(auth);
    if (resp.isAccountKey) {
      return Util.KeyType.Account
    } else if (resp.isCharacterKey) {
      return Util.KeyType.Character
    } else if (resp.isCorporationKey) {
      return Util.KeyType.Corporation
    } else {
      throw new IllegalArgumentException(
        "Key %d:%s is of unknown type".format(auth.getKeyID, auth.getVCode))
    }
  }
}
