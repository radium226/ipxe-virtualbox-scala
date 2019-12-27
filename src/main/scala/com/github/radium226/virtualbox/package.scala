package com.github.radium226

package object virtualbox {

  type MachineName = String

  sealed trait OS
  case object Linux extends OS

  sealed trait Status
  case object Started extends Status
  case object Stopped extends Status

  object instances extends com.github.radium226.virtualbox.AllInstances

  object syntax extends com.github.radium226.virtualbox.AllInstances

  object implicits extends com.github.radium226.virtualbox.AllInstances with com.github.radium226.virtualbox.AllSyntax

}
