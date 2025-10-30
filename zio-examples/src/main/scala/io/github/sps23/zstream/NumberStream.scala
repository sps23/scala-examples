package io.github.sps23.zstream

import zio._
import zio.stream._

import java.io.IOException

object NumberStream extends ZIOAppDefault {

  // Stream from StreamExample
  private val numberStream: ZStream[Any, Nothing, Int] =
    ZStream.fromIterable(1 to 100).forever

  // Stream fiber that runs the number stream
  val streamFiber: ZIO[Any, IOException, Unit] =
    ZIO.logInfo("Starting number stream...") *>
      numberStream
        .zipWithIndex
        .tap { case (n, index) =>
          Console.printLine(s"Number: $n (count: ${index + 1})") *>
            ZIO.sleep(100.millis) *>
            ZIO.when(index + 1 >= 300)(
              ZIO.fail(new IOException("Stream processed 1000 numbers - intentional failure!"))
            )
        }
        .map(_._1) // Keep only the number, discard the index
        .runDrain
        .onInterrupt(ZIO.logInfo("Number stream stopped!"))
        .onExit {
          case Exit.Success(_)     => ZIO.logInfo("Number stream completed successfully")
          case Exit.Failure(cause) => ZIO.logInfo(s"Number stream failed: ${cause.prettyPrint}")
        }

  override def run: ZIO[Any, Any, Unit] =
    numberStream
      .tap(n => Console.printLine(s"Number: $n"))
      .runDrain
}
