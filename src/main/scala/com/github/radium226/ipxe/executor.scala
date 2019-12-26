package com.github.radium226.ipxe

import cats.effect.{Blocker, ContextShift, Resource, Sync}

case class Command(arguments: List[String])

trait Executor[F[_]] {

  def execute(command: Command): F[ExitCode]

}

class DefaultExecutor[F[_]: Sync: ContextShift](blocker: Blocker) extends Executor[F] {

  override def execute(command: Command): F[ExitCode] = {
    blocker.delay[F, ExitCode]({
      println(command)
      val process = new ProcessBuilder()
        .command(command.arguments: _*)
        .inheritIO()
        .start()

      process.waitFor()
    })
  }

}

object Executor {

  def apply[F[_]: Executor]: Executor[F] = implicitly

  def resource[F[_]: Sync: ContextShift]: Resource[F, Executor[F]] = {
    Blocker.apply[F].map(new DefaultExecutor(_))
  }

}

trait ExecutorInstances {

}

trait ExecutorSyntax {

}