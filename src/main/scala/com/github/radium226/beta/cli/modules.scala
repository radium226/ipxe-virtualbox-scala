package com.github.radium226.beta.cli

import com.github.radium226.beta.system.ExecutorModule
import com.github.radium226.beta.virtualbox.MachineModule

case class Modules[F[_]](
  executor: ExecutorModule[F],
  machine: MachineModule[F]
)

object Modules {

  implicit def default[F[_]](implicit
    executor: ExecutorModule[F],
    machine: MachineModule[F]
  ): Modules[F] = Modules(
    executor,
    machine
  )

  def apply[F[_]: Modules]: Modules[F] = implicitly

}
