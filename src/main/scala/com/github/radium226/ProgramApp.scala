package com.github.radium226

import cats._
import cats.data._
import cats.effect._
import com.github.radium226.virtualbox.modules._

/*abstract class ProgramApp {

  implicit def defaultInstance[F[_]: MachineModule]: Modules[F] = {
    new Modules(
      machine = MachineModule[F]
    )
  }

  abstract def program[F[_]: Modules]: F[Unit]

  override def run(arguments: List[String]): IO[ExitCode] = {

    Blocker[IO].use({ blocker =>
      MachineModule[ReaderT[IO, Blocker, *]]

      program[ReaderT[IO, Blocker, *]].run(blocker)
    }).as(ExitCode.Success)

  }

}*/
