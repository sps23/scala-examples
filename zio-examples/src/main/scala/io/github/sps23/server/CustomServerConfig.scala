package io.github.sps23.server

import zio._
import zio.http._
import zio.http.netty.NettyConfig.LeakDetectionLevel
import zio.http.netty.{ChannelType, NettyConfig}

object CustomServerConfig extends ZIOAppDefault {

  private val port = 8081

  private val routes: Routes[Any, Response] =
    Routes(
      Method.GET / "healthcheck" -> handler(Response.text("OK")),
      Method.GET / "server-info" -> handler(Response.text("Custom server config is active")),
    )

  private val app = routes @@ Middleware.requestLogging()

  private val responseCompressionConfig =
    Server.Config.ResponseCompressionConfig(
      // Compress everything from this threshold upwards (bytes).
      contentThreshold = 1024,
      options = IndexedSeq(
        // Common fast web compression codecs.
        Server.Config.CompressionOptions.gzip(),
        Server.Config.CompressionOptions.deflate(),
      ),
    )

  private val webSocketConfig =
    WebSocketConfig.default
      // Max time allowed to finish websocket upgrade handshake.
      .handshakeTimeout(10.seconds)
      // How long server waits before force-closing a half-closed websocket.
      .forceCloseTimeout(5.seconds)
      // Keep default ping/pong behavior unless your app needs raw control frames.
      .forwardPongFrames(forward = false)
      .forwardCloseFrames(forward = false)

  private val serverConfig =
    Server.Config.default
      // Bind host + port used by the HTTP server.
      .binding("0.0.0.0", port)
      // Enable 100-Continue support for clients that send Expect: 100-continue.
      .acceptContinue(enable = true)
      // Keep persistent HTTP/1.1 connections alive.
      .keepAlive(enable = true)
      // Decompress gzipped/deflated incoming requests (strict mode for safety).
      .requestDecompression(isStrict = true)
      // Compress responses to reduce bandwidth usage.
      .responseCompression(responseCompressionConfig)
      // Stream request bodies and avoid fully buffering large payloads.
      .enableRequestStreaming
      // Maximum HTTP request line size (method + URI + version).
      .maxInitialLineLength(8192)
      // Maximum aggregate header size accepted from clients.
      .maxHeaderSize(16384)
      // Emit warning logs when a fatal server-side response write failure happens.
      .logWarningOnFatalError(enable = true)
      // Grace period used during shutdown for in-flight requests to complete.
      .gracefulShutdownTimeout(30.seconds)
      // Close idle keep-alive connections after timeout.
      .idleTimeout(60.seconds)
      // Keep execution on ZIO executor to avoid event-loop starvation risks.
      .avoidContextSwitching(value = false)
      // Max pending TCP connections waiting in the accept queue.
      .soBacklog(value = 1024)
      // Disable Nagle's algorithm to reduce latency for small responses.
      .tcpNoDelay(value = true)
      // Apply websocket-specific protocol and timeout settings.
      .webSocketConfig(webSocketConfig)

  private val nettyConfig =
    NettyConfig.default
      // Netty leak detection level. ADVANCED gives good diagnostics at moderate overhead.
      .leakDetection(LeakDetectionLevel.ADVANCED)
      // Pick channel implementation suitable for current platform.
      .channelType(ChannelType.AUTO)
      // Worker event-loop thread count for handling accepted connections.
      .maxThreads(java.lang.Runtime.getRuntime.availableProcessors() * 2)
      // Configure boss group (acceptor threads) separately from worker group.
      .bossGroup(
        NettyConfig.BossGroup(
          channelType = ChannelType.AUTO,
          nThreads = 1,
          // Quiet period waits for new tasks before finalizing shutdown.
          shutdownQuietPeriodDuration = 2.seconds,
          // Hard timeout for boss event-loop shutdown.
          shutdownTimeOutDuration = 15.seconds,
        )
      )
      // Quiet period for worker group shutdown.
      .copy(shutdownQuietPeriodDuration = 2.seconds)
      // Hard timeout for worker group shutdown.
      .copy(shutdownTimeoutDuration = 20.seconds)

  private val serverLayer: ZLayer[Any, Throwable, Server] =
    ZLayer.succeed(serverConfig) ++ ZLayer.succeed(nettyConfig) >>> Server.customized

  override def run: ZIO[Any, Throwable, Unit] =
    (for {
      _ <- ZIO.logInfo(s"Starting custom server on port $port")
      _ <- ZIO.logInfo(s"Healthcheck endpoint: http://localhost:$port/healthcheck")
      _ <- Server.serve(app)
    } yield ())
      .provide(serverLayer)
      .onInterrupt(ZIO.logInfo("Custom server interrupted, shutting down..."))
}
