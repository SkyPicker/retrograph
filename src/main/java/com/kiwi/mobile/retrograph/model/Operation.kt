package com.kiwi.mobile.retrograph.model

import kotlin.reflect.*

/**
 * Class representing query operation.
 *
 * TODO: VariableDefinitions
 * TODO: Directives
 */
class Operation(
  val parent: Document,
  val type: Type = Type.QUERY,
  val name: String = ""
) {

  // region Public Types

  enum class Type(
    val value: String
  ) {
    QUERY("query"),
    MUTATION("mutation"),
    SUBSCRIPTION("subscription")
  }

  // endregion Public Types

  // region Private Properties

  private val selectionSet = SelectionSet(this)

  // endregion Private Properties

  // region Public Methods

  fun field(name: String, alias: String = "") =
    selectionSet.field(name, alias)
      .finish()

  fun objectField(name: String, alias: String = "") =
    selectionSet.objectField(name, alias)

  fun fieldsOf(clazz: Class<*>, arguments: Any? = null) =
    selectionSet.fieldsOf(clazz, arguments)
      .finish()

  fun fieldsOf(clazz: KClass<*>, arguments: Any? = null) =
    selectionSet.fieldsOf(clazz, arguments)
      .finish()

  // NOTE: For now, nothing can be done after [Operation] is finished so finish also [Document].
  fun finish() = parent.finish()

  override fun toString() = "${type.value} ${buildNameString()}{ $selectionSet }"

  // endregion Public Methods

  // region Private Methods

  private fun buildNameString() = if (name.isNotEmpty()) "$name " else ""

  // endregion Private Methods
}
