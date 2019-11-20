package com.kiwi.mobile.retrograph.model

import com.kiwi.mobile.retrograph.*

/**
 * Class representing a query document.
 *
 * TODO: Multiple Definitions?
 * TODO: FragmentDefinition?
 */
class Document(
  val parent: RequestBuilder
) {

  // region Private Properties

  private var operation = Operation(this)

  // endregion Private Properties

  // region Public Methods

  fun operation() = this.operation

  /**
   * Tell GraphQL server which operation to execute.
   */
  fun operation(type: Operation.Type, name: String = "") =
    Operation(this, type, name)
      .also { operation = it }

  fun finish() = parent

  override fun toString() = "$operation"

  // endregion Public Methods
}
