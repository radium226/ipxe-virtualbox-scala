package com.github.radium226.beta.cli

import cats.{Applicative, Monad}
import cats.effect.Resource
import com.github.radium226.beta.Program
import com.github.radium226.beta.cli.instances._
import cats.mtl.implicits._
import com.github.radium226.beta.system.{Command, ExecutorConfig}
import com.github.radium226.beta.virtualbox.{Machine, MachineConfig}
import cats.implicits._

object SayHello extends Program[Config] {

  override def configResource[F[_]](arguments: List[String])(implicit A: Applicative[F]): Resource[F, Config] = {
    Resource.pure[F, Config](Config(ExecutorConfig(), MachineConfig()))
  }

  override def program[F[_]](config: Config)(implicit M: Monad[F], modules: Modules[F]): F[Unit] = for {
    _ <- modules.executor.execute(Command())
  } yield ()
}
