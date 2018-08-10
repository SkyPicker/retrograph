package com.kiwi.mobile.retrograph.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(
  AnnotationTarget.FUNCTION,
  AnnotationTarget.PROPERTY_GETTER,
  AnnotationTarget.PROPERTY_SETTER
)
annotation class GraphQL
