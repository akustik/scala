package app

import app.core._

/**
 * Created by guillermo.mercadal on 28/09/14.
 */
object Launcher {
  def main(args: Array[String]) {
    val consumer = new AppConsumer
    args.foreach(consumer.apply)
  }
}
