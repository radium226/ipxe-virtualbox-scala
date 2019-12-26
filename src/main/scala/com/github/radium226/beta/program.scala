package com.github.radium226.beta

import cats.{Applicative, Monad}
import cats.data.ReaderT
import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.implicits._
import com.github.radium226.beta.cli.Modules

abstract class Program[C](implicit modules: Modules[ReaderT[IO, C, *]]) extends IOApp {

  abstract def configResource[F[_]](arguments: List[String])(implicit A: Applicative[F]): Resource[F, C]

  abstract def program[F[_]](config: C)(implicit M: Monad[F], modules: Modules[F]): F[Unit]

  override def run(arguments: List[String]): IO[ExitCode] = {
    configResource(arguments)
      .use({ config =>
        program[ReaderT[IO, C, *]](config).run(config)
      })
      .as(ExitCode.Success)
  }
}
