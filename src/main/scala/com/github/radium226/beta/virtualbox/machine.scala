package com.github.radium226.beta.virtualbox

import cats._
import cats.mtl._
import cats.implicits._
import cats.mtl.implicits._

case class Machine()

case class MachineConfig()

trait MachineModule[F[_]] {

  def create(machine: Machine): F[Unit]

}

object MachineModule {

  def apply[F[_]: MachineModule]: MachineModule[F] = implicitly

}

trait MachineInstances {

  implicit def machineInstance[F[_]](implicit AA: ApplicativeAsk[F, MachineConfig]): MachineModule[F] = new MachineModule[F] {

    override def create(machine: Machine): F[Unit] = {
      AA.applicative.unit
    }

  }

}

trait MachineSyntax
