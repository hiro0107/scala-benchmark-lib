package com.github.benchmark

trait Tag

case class LabeledTag[A](label: A) extends Tag {
  override def toString() = label.toString
}

case object EmptyTag extends Tag {
  override def toString() = ""
}
