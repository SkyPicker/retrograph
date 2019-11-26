package com.kiwi.mobile.retrograph.model

import kotlin.reflect.*

import java.lang.reflect.Field as JavaField

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

  fun argumentsOf(instance: Any?, arguments: Map<String, JavaField>) =
    this.arguments.argumentsOf(instance, arguments)
      .finish()

  fun fields() = fields

  fun field(name: String, alias: String = "") =
    fields.field(name, alias)

  fun objectField(name: String, alias: String = "") =
    fields.objectField(name, alias)

  fun inlineFragment(name: String) =
    fields.inlineFragment(name)

  fun fieldsOf(`class`: Class<*>, arguments: Any? = null): Field<TSelectionSetParent> =
    fields.fieldsOf(`class`, arguments)
      .finish()

  fun fieldsOf(`class`: KClass<*>, arguments: Any? = null) =
    fieldsOf(`class`.java, arguments)

  inline fun <reified T> fieldsOf(arguments: Any? = null) =
    fieldsOf(T::class, arguments)

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

  private fun buildArgumentsString() = if (arguments.isNotEmpty) "($arguments)" else ""

  // endregion Private Methods
}
