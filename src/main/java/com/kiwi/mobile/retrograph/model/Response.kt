package com.kiwi.mobile.retrograph.model

import java.io.Serializable

data class Response<TData>(
  val data: TData? = null,
  val invalid: Boolean = false,
  val errors: List<String> = listOf()
): Serializable {

  // region Public Methods

  override fun toString() =
    "{ " +
      "\"data\": $data, " +
      "\"invalid\": $invalid, " +
      "\"errors\": $errors, " +
      "}"
  // endregion Public Methods
}
