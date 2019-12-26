package com.github.radium226

import cats.Applicative
import cats.data.ReaderT
import cats.effect.{Blocker, ExitCode, IO, IOApp}
import com.github.radium226.system.executor.modules.ExecutorModule
import com.github.radium226.virtualbox.data._
import com.github.radium226.virtualbox.interpreters._
import com.github.radium226.virtualbox.modules._
import com.github.radium226.system.executor.data._
import com.github.radium226.system.executor.interpreters._
import com.github.radium226.system.executor.modules._
import cats.implicits._
import cats.mtl.ApplicativeAsk
import squants.information.InformationConversions._

import cats.implicits._

import cats.mtl.instances._
import cats.mtl.implicits._

object Main extends IOApp {

  case class Environment()

  def program[F[_]: MachineModule]: F[Unit] = {
    val storage = Storage(List.empty)
    val system = System(1 gb, List.empty, false)
    val machine = Machine("Test", Linux, storage, system)


    MachineModule[F].create(machine)
  }

  override def run(arguments: List[String]): IO[ExitCode] = {

    Blocker[IO].use({ blocker =>
      program[ReaderT[IO, Blocker, *]].run(blocker)
    }).as(ExitCode.Success)

  }

}
