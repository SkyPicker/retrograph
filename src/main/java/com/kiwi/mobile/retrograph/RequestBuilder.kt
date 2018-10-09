package com.kiwi.mobile.retrograph

import com.google.gson.*
import com.kiwi.mobile.retrograph.model.*

/**
 * Builder for creating GraphQL request.
 *
 * example:
 *
 * val request = .operation(Operation.Type.QUERY)
 *    .objectField("get_flights")
 *       .arguments()
 *         .argument("providers", Provider.KIWI)
 *         .objectArgument("parameters")
 *           .value("flyFrom", "DEN")
 *           .value("to", "FRA")
 *           .value("dateFrom", "20/04/2018")
 *           .value("dateTo", "20/05/2018")
 *         .finish()
 *         .objectArgument("pagination")
 *           .value("offset", 0)
 *           .value("limit", 5)
 *         .finish()
 *       .finish()
 *       .objectField("data")
 *         .field("id")
 *         .field("price")
 *         .finish()
 *       .finish()
 *     .finish()
 *   .build()
 *
 * TODO: DSL definition of query for Kotlin.
 */
open class RequestBuilder {

  // region Protected Properties

  protected var document: Document = Document(this)

  // endregion Protected Properties

  // region Private Properties

  private var variables = mutableMapOf<String, Any?>()

  private val gson by lazy {
    GsonBuilder()
      .create()
  }

  // endregion Private Properties

  // region Public Methods

  /**
   * Returns the current document operation.
   */
  fun operation() = document.operation()

  /**
   * Creates and returns a new document operation. The old one will be dropped.
   */
  fun operation(type: Operation.Type, name: String = "") = document.operation(type, name)

  /**
   * Adds variable to the request.
   */
  fun variable(name: String, value: Any?) =
    apply { variables[name] = value }

  /**
   * Builds GraphQL request to be sent to server.
   */
  fun build() =
    Request(
      buildQueryString(),
      buildVariables()
    )

  // endregion Public Methods

  // region Private Methods

  /**
   * Builds a string from document.
   */
  private fun buildQueryString() = "$document"

  /**
   * Build a string from variables.
   */
  private fun buildVariables() = variables
    .map {
      it.key to gson.toJson(it.value)
    }
    .toMap()

  // endregion Private Methods
}
