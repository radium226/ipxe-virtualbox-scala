package com.github.radium226.system.executor

import cats.data.ReaderT
import cats.effect.{Blocker, ContextShift, Sync}
import cats.mtl.ApplicativeAsk
import com.github.radium226.system.executor.data.{Argument, ExitCode}
import com.github.radium226.system.executor.modules.ExecutorModule

import cats.implicits._

object interpreters {

  implicit def executorModuleInterpreter[F[_]](implicit S: Sync[F], contextShift: ContextShift[F], AA: ApplicativeAsk[F, Blocker]): ExecutorModule[F] = new ExecutorModule[F] {

    override def execute(command: data.Command): F[ExitCode] = for {
      blocker  <- AA.ask
      exitCode <- blocker.delay[F, ExitCode]({
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
