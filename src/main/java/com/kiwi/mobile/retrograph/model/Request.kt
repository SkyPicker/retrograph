package com.kiwi.mobile.retrograph.model

import java.io.*

class Request(
  var query: String,
  var variables: String
):
  Serializable {

  // region Public Methods

  // TODO: Replace with JsonObject.
  override fun toString() =
    "{ " +
      "\"query\": $query, " +
      "\"variables\": $variables " +
      "}"

  // endregion Public Methods
}
