package com.github.radium226.virtualbox

import java.nio.file.Path

import squants.information.Information


object data {

  type Name = String

  case class Machine(name: Name, os: OS, storage: Storage, system: System)

  case class Storage(controllers: List[storage.Controller])

  object storage {

    sealed trait Norm
    case object SATA extends Norm
    case object IDE  extends Norm

    case class Controller(norm: Norm, media: List[Medium])

    object medium {

      sealed trait Format
      case object VDI extends Format

    }

    sealed trait Medium
    case class HardDrive(format: medium.Format, memory: Information, filePath: Path) extends Medium
    case class OpticalDrive(filePath: Path) extends Medium

  }

  case class System(baseMemory: Information, bootOrder: List[system.Boot], efi: Boolean)

  object system {

    sealed trait Boot
    case object Floppy  extends Boot
    case object DVD     extends Boot
    case object Network extends Boot

  }

  sealed trait Status
  case object Started extends Status
  case object Stopped extends Status

  sealed trait OS
  case object Linux extends OS

}
