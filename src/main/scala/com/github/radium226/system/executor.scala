package com.github.radium226.system

import cats.effect.{Blocker, ContextShift, Sync}
import cats.implicits._
import com.github.radium226.ApplicativeConfig

case class ExecutorConfig(blocker: Blocker)

case class Command(arguments: List[Argument])

trait ExecutorModule[F[_]] {

  def execute(command: Command): F[ExitCode]

}

object ExecutorModule {

  def apply[F[_]: ExecutorModule]: ExecutorModule[F] = implicitly

}

trait ExecutorInstances {

  implicit def executorInstance[F[_]](implicit AC: ApplicativeConfig[F], S: Sync[F], contextShift: ContextShift[F]): ExecutorModule[F] = new ExecutorModule[F] {

    override def execute(command: Command): F[ExitCode] = for {
      config <- AC.ask.map(_.executorConfig)
      exitCode <- config.blocker.delay[F, ExitCode]({
        println(command)
        val process = new ProcessBuilder()
          .command(command.arguments: _*)
          .inheritIO()
          .start()

        process.waitFor()
      })
    } yield exitCode
  }
}

trait ExecutorSyntax {

  implicit class ListOfStringOps(listOfString: List[String]) {

    def execute[F[_]](implicit executor: ExecutorModule[F]): F[ExitCode] = {
      executor.execute(Command(listOfString))
    }

  }

}
