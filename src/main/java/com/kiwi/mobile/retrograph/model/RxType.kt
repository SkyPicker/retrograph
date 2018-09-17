package com.kiwi.mobile.retrograph.model

enum class RxType(
  val id: String
) {
  OBSERVABLE("Observable"),
  FLOWABLE("Flowable"),
  SINGLE("Single"),
  MAYBE("Maybe"),
  COMPLETABLE("Completable"),
  UNKNOWN("Unknown")
}
