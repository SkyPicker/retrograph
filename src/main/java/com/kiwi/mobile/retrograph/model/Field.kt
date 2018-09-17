package com.kiwi.mobile.retrograph.model

import kotlin.reflect.*

/**
 * Class representing selection field.
 */
class Field<TSelectionSetParent>(
  val parent: SelectionSet<TSelectionSetParent>,
  val name: String = "",
  val alias: String = ""
) {

  // region Private Properties

  private val arguments = Arguments(this)
  private val fields = SelectionSet(this)

  // endregion Private Properties

  // region Public Methods

  fun arguments() = arguments

  fun argumentsOf(instance: Any?) =
    arguments.argumentsOf(instance)
      .finish()

  fun fields() = fields

  fun field(name: String, alias: String = "") =
    apply {
      fields.field(name, alias)
    }

  fun objectField(name: String, alias: String = "") =
    fields.objectField(name, alias)

  fun fieldsOf(clazz: Class<*>): Field<TSelectionSetParent> =
    fields.fieldsOf(clazz)
      .finish()

  fun fieldsOf(clazz: KClass<*>): Field<TSelectionSetParent> =
    fields.fieldsOf(clazz)
      .finish()

  /**
   * NOTE: Until multiple selections in [SelectionSet] is supported there is no need to continue
   * with with [SelectionSet]
   */
  fun finish() = parent.finish()

  override fun toString() =
    "${buildAliasString()}${buildNameString()}${buildArgumentsString()}${buildSelectionSetString()}"

  // endregion Public Methods

  // region Private Methods

  private fun buildAliasString() = if (alias.isNotEmpty()) "$alias: " else ""

  private fun buildNameString() = name

  private fun buildSelectionSetString() = if (fields.isNotEmpty) " { $fields }" else ""

  private fun buildArgumentsString() = if (arguments.isNotEmpty) "( $arguments )" else ""

  // endregion Private Methods
}
