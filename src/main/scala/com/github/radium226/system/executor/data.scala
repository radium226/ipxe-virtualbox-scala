package com.github.radium226.system.executor

object data {

  type ExitCode = Int

  type Argument = String

  case class Command(arguments: List[Argument])

}
