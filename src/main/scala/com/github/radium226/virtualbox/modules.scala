package com.github.radium226.virtualbox

import cats.Functor
import cats.effect.{Fiber, Resource}
import cats.implicits._
import com.github.radium226.virtualbox.data._
import com.github.radium226.virtualbox.data
import com.github.radium226.virtualbox.data.storage.Medium


object modules {

  trait MachineModule[F[_]] {

    def create(machine: Machine): F[Unit]

    def destroy(machine: Machine): F[Unit]

    def start(machine: Machine): F[Fiber[F, Unit]]

    def stop(machine: Machine): F[Unit]

    def resource(machine: Machine)(implicit F: Functor[F]): Resource[F, Machine] = {
      Resource.make(create(machine).as(machine))(destroy)
    }

  }

  object MachineModule {

    def apply[F[_]: MachineModule]: MachineModule[F] = implicitly

  }

}
