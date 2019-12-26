package com.github.radium226.system.executor

import com.github.radium226.system.executor.data._

object modules {

  trait ExecutorModule[F[_]] {

    def execute(command: Command): F[ExitCode]

    def execute(arguments: Argument*): F[ExitCode] = execute(Command(arguments.toList))

  }

  object ExecutorModule {

    def apply[F[_]: ExecutorModule]: ExecutorModule[F] = implicitly

  }

}
