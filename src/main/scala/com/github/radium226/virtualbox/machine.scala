package com.github.radium226.virtualbox

import java.nio.file.Path

import cats._
import cats.effect.{Fiber, Resource}
import cats.implicits._
import com.github.radium226.ApplicativeConfig
import com.github.radium226.system.{Command, ExecutorModule}

case class Machine(name: MachineName, os: OS)

case class MachineConfig(folderPath: Path)

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

trait MachineInstances {

  implicit def machineInstance[F[_]](implicit AC: ApplicativeConfig[F], M: Monad[F], executor: ExecutorModule[F]): MachineModule[F] = new MachineModule[F] {

    override def create(machine: Machine): F[Unit] = {
      executor.execute(Command(List("VBoxManage", "createvm", s"--name=${machine.name}", s"--ostype=${machine.os}", "--register"))).void
    }

    override def destroy(machine: Machine): F[Unit] = {
      executor.execute(Command(List("VBoxManage", "unregistervm", machine.name, "--delete"))).void
    }

    override def start(machine: Machine): F[Fiber[F, Unit]] = {
      val join      = (executor.execute(Command(List("VBoxManage", "startvm", machine.name))).void *> M.iterateWhile(status(machine))(_ == Started)).void
      val cancel    = stop(machine)
      M.pure(Fiber[F, Unit](join, cancel))
    }

    override def stop(machine: Machine): F[Unit] = {
      executor.execute(Command(List("VBoxManage", "controlvm", machine.name, "poweroff"))).void
    }

    def status(machine: Machine): F[Status] = {
      executor
        .execute(Command(List("sh", "-c", s"VBoxManage showvminfo '${machine.name}' | grep -c 'running (since'")))
        .map({
          case 0 =>
            Started
          case _ =>
            Stopped
        })
    }

  }

}

trait MachineSyntax {

  implicit class MachineOps(machine: Machine) {

    def start[F[_]](implicit machineModule: MachineModule[F]): F[Fiber[F, Unit]] = {
      machineModule.start(machine)
    }

    def create[F[_]](implicit machineModule: MachineModule[F]): F[Unit] = {
      machineModule.create(machine)
    }

    def resource[F[_]](implicit machineModule: MachineModule[F], F: Functor[F]): Resource[F, Machine] = {
      machineModule.resource(machine)
    }

  }

}
