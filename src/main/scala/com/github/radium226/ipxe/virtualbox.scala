package com.github.radium226.ipxe

import java.nio.file.{Path, Paths}

import cats.{Functor, Monad}
import cats.effect.{Resource, Timer}
import cats.implicits._
import squants.information.Information
import scala.concurrent.duration._


case class Machine(name: MachineName, os: OS, memorySize: Information, hardDrives: List[HardDrive])

case class HardDrive(size: Information)

trait VirtualBox[F[_]] {

  def createMachine(machine: Machine): F[Unit]

  def deleteMachine(machine: Machine): F[Unit]

  def addHardDrive(machine: Machine)(hardDrive: HardDrive, filePath: Path): F[Unit]

  def startMachine(machine: Machine): F[Unit]

  def isMachineStarted(machine: Machine): F[Boolean]

}

object VirtualBox {

  def apply[F[_]: VirtualBox]: VirtualBox[F] = implicitly

}

class VBoxManageVirtualBox[F[_]: Executor: Monad: Timer] extends VirtualBox[F] {

  override def addHardDrive(machine: Machine)(hardDrive: HardDrive, filePath: Path): F[Unit] = {
    for {
      _ <- Executor[F].execute(Command(List("VBoxManage", "createmedium", "disk", s"--filename=${filePath}", s"--size=${hardDrive.size.toMegabytes.toInt}")))
      _ <- Executor[F].execute(Command(List("VBoxManage", "storageattach", machine.name, "--storagectl=SATA", "--port=0", "--device=0", "--type=hdd", s"--medium=${filePath}")))
    } yield ()
  }

  override def createMachine(machine: Machine): F[Unit] = {
    for {
      _ <- Executor[F].execute(Command(List("VBoxManage", "createvm", s"--name=${machine.name}", s"--ostype=${machine.os}", "--register")))
      _ <- Executor[F].execute(Command(List("VBoxManage", "modifyvm", machine.name, s"--memory=${machine.memorySize.toMegabytes.toInt}")))
      _ <- Executor[F].execute(Command(List("VBoxManage", "modifyvm", machine.name, "--firmware=efi")))
      _ <- Executor[F].execute(Command(List("VBoxManage", "storagectl", machine.name, s"--name=SATA", "--add", "sata", "--controller", "IntelAhci")))
      _ <- machine.hardDrives.zipWithIndex.traverse({ case (hardDrive, index) => addHardDrive(machine)(hardDrive, Paths.get(s"disk-${index}.vdi")) })
    } yield ()
  }

  override def deleteMachine(machine: Machine): F[Unit] = {
    Executor[F].execute(Command(List("VBoxManage", "unregistervm", machine.name, "--delete"))).void
  }

  override def startMachine(machine: Machine): F[Unit] = {
    for {
      _ <- Executor[F].execute(Command(List("VBoxManage", "startvm", machine.name))).void
      _ <- Monad[F].iterateWhile(for {
        started <- isMachineStarted(machine)
        _       <- Timer[F].sleep(5 seconds)
        _        = println(s"started=${started}")
      } yield started)(identity)
    } yield ()
  }

  override def isMachineStarted(machine: Machine): F[Boolean] = {
    Executor[F].execute(Command(List("sh", "-c", s"VBoxManage showvminfo '${machine.name}' | grep -c 'running (since'"))).map(_ == 0)
  }

}

trait VirtualBoxInstances {

  implicit def virtualBoxVBoxManageInstance[F[_]: Executor: Monad: Timer]: VirtualBox[F] = new VBoxManageVirtualBox[F]

}

trait VirtualBoxSyntax {

  implicit class VirtualBoxOps[F[_]: Functor](virtualBox: VirtualBox[F]) {

    def machineResource(machine: Machine): Resource[F, Unit] = {
      Resource.make[F, Unit](virtualBox.createMachine(machine))({ _ => virtualBox.deleteMachine(machine) })
    }

  }

  implicit class MachineOps[F[_]: VirtualBox](machine: Machine) {

    def start: F[Unit] = VirtualBox[F].startMachine(machine)

  }

}