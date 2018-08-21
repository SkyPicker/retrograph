package com.kiwi.mobile.retrograph.annotation

@Target(
  AnnotationTarget.PROPERTY
)
annotation class Alias(
  val name: String
)
