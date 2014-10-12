package app

import app.core._
import org.gmd.shell._

/**
 * Created by guillermo.mercadal on 28/09/14.
 */
object Launcher {
  def main(args: Array[String]) {
    val shellExecutor = new Executor
    println(shellExecutor("ls -l"))

    val consumer = new AppConsumer
    args.foreach(consumer.apply)
  }
}
