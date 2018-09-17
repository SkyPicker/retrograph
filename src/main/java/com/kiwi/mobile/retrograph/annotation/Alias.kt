package com.kiwi.mobile.retrograph.annotation

@Target(
  AnnotationTarget.PROPERTY,
  AnnotationTarget.FIELD
)
annotation class Alias(
  val name: String
)
