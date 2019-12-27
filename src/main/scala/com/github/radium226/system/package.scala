package com.github.radium226

package object system {

  type ExitCode = Int

  type Argument = String

  object instances extends com.github.radium226.system.AllInstances

  object syntax extends com.github.radium226.system.AllSyntax

  object implicits extends com.github.radium226.system.AllInstances with com.github.radium226.system.AllSyntax

}
