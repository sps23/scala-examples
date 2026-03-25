package io.github.sps23.handler

import zio.ZIO

trait GreetingService {

  def greet(name: String, title: String): ZIO[Any, Nothing, String] = ZIO.succeed(s"Hello, $title $name!")
}

object GreetingServiceImpl extends GreetingService with AuthProviderImpl
