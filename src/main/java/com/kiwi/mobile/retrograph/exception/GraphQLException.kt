package com.kiwi.mobile.retrograph.exception

import com.kiwi.mobile.retrograph.model.*

class GraphQLException(response: Response<*>):
  Exception(
    if (response.errors.isEmpty()) {
      "No error message."
    } else {
      response.errors.toString()
    }
  )
