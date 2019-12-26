package com.github.radium226.ipxe

import scala.language.postfixOps

import squants.information.InformationConversions._

import cats.effect.{ExitCode => CatsExitCode, IO, IOApp}
import cats.implicits._

import instances.virtualbox._
import syntax.virtualbox._

import scala.concurrent.duration._


object Main extends IOApp {

  override def run(arguments: List[String]): IO[CatsExitCode] = {
    Executor
      .resource[IO]
      .use({ implicit executor =>
        val machine = Machine("Test", Linux, 1.gb, List(HardDrive(5 gb)))
        VirtualBox[IO]
          .machineResource(machine)
          .use({ _ =>
            for {
              _ <- IO(println("Before"))
              _ <- machine.start
              _ <- IO(println("After"))
            } yield ()
          })
      })
      .as(CatsExitCode.Success)

  }

}
