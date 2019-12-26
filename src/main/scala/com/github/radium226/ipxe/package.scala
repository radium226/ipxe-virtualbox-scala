package com.github.radium226

package object ipxe {

  type MachineName = String

  type ExitCode = Int

  sealed trait OS
  case object Linux extends OS

  object instances {

    object virtualbox extends VirtualBoxInstances

    object executor extends ExecutorInstances

  }

  object syntax {

    object executor extends ExecutorSyntax

    object virtualbox extends VirtualBoxSyntax

  }

}
