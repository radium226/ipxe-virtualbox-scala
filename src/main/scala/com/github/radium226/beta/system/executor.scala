package com.github.radium226.beta.system

import cats.{Applicative, Functor}
import cats.mtl.ApplicativeAsk
import cats.implicits._

case class ExecutorConfig()

case class Command()

trait ExecutorModule[F[_]] {

  def execute(command: Command): F[Unit]

}

object ExecutorModule {

  def apply[F[_]: ExecutorModule]: ExecutorModule[F] = implicitly

}

trait ExecutorInstances {

  implicit def executorInstance[F[_]](implicit A: ApplicativeAsk[F, ExecutorConfig]): ExecutorModule[F] = new ExecutorModule[F] {

    override def execute(command: Command): F[Unit] = {
      ??? //().pure[F]
    }

  }

}

trait ExecutorSyntax
