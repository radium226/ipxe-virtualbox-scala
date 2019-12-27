package com.github.radium226

import java.nio.file.Paths

import cats.Monad
import cats.data.ReaderT
import cats.effect.{Blocker, ExitCode, IO, IOApp}
import com.github.radium226.system.{ExecutorConfig, ExecutorModule}
import com.github.radium226.virtualbox.{MachineConfig, MachineModule}

import cats.implicits._
import cats.mtl.implicits._
import com.github.radium226.implicits._

object Main extends IOApp {

  def program[F[_]: ExecutorModule: MachineModule: Monad](config: Config): F[Unit] = {
    for {
      _ <- List("echo", "Starting! ").execute[F]
      _ <- List("echo", "Stopping!").execute[F]
    } yield ()
  }

  override def run(arguments: List[String]): IO[ExitCode] = {
    val configResource = for {
      executorBlocker <- Blocker[IO]
      folderPath       = Paths.get("/tmp/machines")
    } yield Config(ExecutorConfig(executorBlocker), MachineConfig(folderPath))

    configResource
      .use({ config =>
        program[ReaderT[IO, Config, *]](config).run(config)
      })
      .as(ExitCode.Success)
  }
}
