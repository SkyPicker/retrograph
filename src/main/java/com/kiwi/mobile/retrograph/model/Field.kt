package com.kiwi.mobile.retrograph.model

import com.kiwi.mobile.retrograph.extension.*

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
  private val selectionSet = SelectionSet(this)

  // endregion Private Properties

  // region Public Methods

  fun arguments() = arguments

  fun field(name: String) =
    apply {
      selectionSet.field(name)
    }

  fun objectField(name: String) = selectionSet.objectField(name)

  fun fieldsOf(clazz: Class<*>): Field<TSelectionSetParent> =
    apply {
      clazz.declaredFields
        .filter { !it.isFinal }
        .map {
          when {
            it.type.isPrimitiveOrWrapper ->
              field(it.serializedName)
            it.type.isArray ->
              objectField(it.serializedName)
                .fieldsOf(it.type.componentType)
            else ->
              objectField(it.serializedName)
                .fieldsOf(it.type)
          }
          parent
        }
    }

  /**
   * NOTE: Until multiple selections in [SelectionSet] is supported there is no need to continue
   * with with [SelectionSet]
   */
  fun finish() = parent.finish()

  override fun toString() =
    "${buildAliasString()}$name${buildArgumentsString()}${buildSelectionSetString()}"

  // endregion Public Methods

  // region Private Methods

  private fun buildAliasString() = if (alias.isNotEmpty()) "$alias: " else ""

  private fun buildSelectionSetString() = if (selectionSet.isNotEmpty) " { $selectionSet }" else ""

  private fun buildArgumentsString() = if (arguments.isNotEmpty) "( $arguments )" else ""

  // endregion Private Methods
}
