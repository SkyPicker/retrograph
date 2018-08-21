package com.kiwi.mobile.retrograph.model

data class Error(
  val message: String = "",
  val locations: List<Location> = arrayListOf(),
  val path: List<Any> = arrayListOf()
) {

  data class Location(
    val line: Int = 0,
    val column: Int = 0
  )
}
