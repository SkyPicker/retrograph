package com.kiwi.mobile.retrograph.model

data class Response<TData>(
  val data: TData? = null,
  val invalid: Boolean = false,
  val errors: List<Error> = listOf()
)
