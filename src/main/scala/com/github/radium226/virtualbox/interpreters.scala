package com.github.radium226.virtualbox

import com.github.radium226.system.executor.modules._
import com.github.radium226.virtualbox.modules._
import com.github.radium226.virtualbox.data._
import cats.Monad
import cats.effect.{Async, CancelToken, Concurrent, Fiber}
import cats.implicits._
import cats.mtl.ApplicativeAsk


object interpreters {

  implicit def machineModuleInterpreter[F[_]](implicit M: Monad[F], executor: ExecutorModule[F]): MachineModule[F] = new MachineModule[F] {

    override def create(machine: Machine): F[Unit] = for {
      _        <- executor.execute("VBoxManage", "createvm", s"--name=${machine.name}", s"--ostype=${machine.os}", "--register").void
    } yield ()

    override def destroy(machine: Machine): F[Unit] = for {
      _        <- executor.execute("VBoxManage", "unregistervm", machine.name, "--delete").void
    } yield ()

    def status(machine: Machine): F[Status] = {
      executor
        .execute("sh", "-c", s"VBoxManage showvminfo '${machine.name}' | grep -c 'running (since'")
        .map({
          case 0 =>
            Started
          case _ =>
            Stopped
        })
    }

    override def start(machine: Machine): F[Fiber[F, Unit]] = {
      val join      = (executor.execute("VBoxManage", "startvm", machine.name).void *> M.iterateWhile(status(machine))(_ == Started)).void
      val cancel    = stop(machine)
      M.pure(Fiber[F, Unit](join, cancel))
    }

    override def stop(machine: Machine): F[Unit] = for {
      _        <- executor.execute("VBoxManage", "controlvm", machine.name, "poweroff").void
    } yield ()
  }

}
