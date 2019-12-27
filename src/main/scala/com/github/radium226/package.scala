package com.github

import cats.mtl.ApplicativeAsk
import com.github.radium226.system.ExecutorConfig
import com.github.radium226.virtualbox.MachineConfig

package object radium226 {

  case class Config(executorConfig: ExecutorConfig, machineConfig: MachineConfig)

  type ApplicativeConfig[F[_]] = ApplicativeAsk[F, Config]

  trait AllInstances extends system.AllInstances with virtualbox.AllInstances

  trait AllSyntax extends system.AllSyntax with virtualbox.AllSyntax

  object instances extends AllInstances

  object syntax extends AllSyntax

  object implicits extends AllInstances with AllSyntax

}
