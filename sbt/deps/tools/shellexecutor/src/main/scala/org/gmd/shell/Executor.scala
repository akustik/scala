package org.gmd.shell

import java.io.ByteArrayOutputStream
import org.apache.commons.exec._

class Executor {
  def apply(cmd: String): String = {
    val output = new ByteArrayOutputStream
    val psh = new PumpStreamHandler(output)
    val command = new CommandLine("/bin/sh")
    command.addArgument("-c")
    command.addArgument(cmd, false)
    val executor = new DefaultExecutor()
    executor.setStreamHandler(psh)

    try {
      if (executor.execute(command) != 0) {
        throw new IllegalStateException(s"Command $cmd failed")
      } else {
        output.toString("UTF-8")
      }
    } catch {
      case e: ExecuteException => {
        println(output.toString("UTF-8"))
        throw e
      }
    }
  }
}
