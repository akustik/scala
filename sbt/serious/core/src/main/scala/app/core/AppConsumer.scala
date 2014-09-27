package app.core

import app.util._

class AppConsumer {
  val logger = new StdoutLog
  def apply(content: String) = {
    logger.info(s"$content consumed")  
  }
}
