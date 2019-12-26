package com.github.radium226

import com.github.radium226.virtualbox.modules.MachineModule

class Modules[F[_]](machine: MachineModule[F])

object Modules {

  def apply[F[_]: MachineModule]: MachineModule[F] = implicitly

}
