package com.github.radium226.beta.cli

import cats.data.ReaderT
import com.github.radium226.beta.system.{Command, ExecutorConfig, ExecutorModule}
import com.github.radium226.beta.virtualbox.{Machine, MachineConfig, MachineModule}

trait InstancesForConfig {

  implicit def executorForConfig[F[_]](implicit executorModule: ExecutorModule[ReaderT[F, ExecutorConfig, *]]): ExecutorModule[ReaderT[F, Config, *]] = new ExecutorModule[ReaderT[F, Config, *]] {

    override def execute(command: Command): ReaderT[F, Config, Unit] = executorModule.execute(command).local(_.executorConfig)

  }

  implicit def machineForConfig[F[_]](implicit machineModule: MachineModule[ReaderT[F, MachineConfig, *]]): MachineModule[ReaderT[F, Config, *]] = new MachineModule[ReaderT[F, Config, *]] {

    override def create(machine: Machine): ReaderT[F, Config, Unit] = machineModule.create(machine).local(_.machineConfig)

  }

}

case class Config(
  executorConfig: ExecutorConfig,
  machineConfig: MachineConfig
)
