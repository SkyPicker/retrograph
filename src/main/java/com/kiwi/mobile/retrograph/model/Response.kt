package com.kiwi.mobile.retrograph.model

data class Response<TData>(
  val data: Map<String, TData> = mapOf(),
  val invalid: Boolean = false,
  val errors: List<Error> = listOf()
)
