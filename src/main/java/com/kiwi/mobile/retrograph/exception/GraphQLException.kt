package com.kiwi.mobile.retrograph.exception

import com.kiwi.mobile.retrograph.*

class GraphQLException(response: GraphQLResponse<*>):
  Exception(
    if ((response.errors == null) || response.errors.isEmpty()) {
      "GraphQLException: No error message."
    } else {
      response.errors[0]
    }
  )
