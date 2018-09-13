package com.kiwi.mobile.retrograph.model

import com.kiwi.mobile.retrograph.extension.*

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
  private val selectionSet = SelectionSet(this)

  // endregion Private Properties

  // region Public Methods

  fun arguments() = arguments

  fun field(name: String) =
    apply {
      selectionSet.field(name)
    }

  fun objectField(name: String) = selectionSet.objectField(name)

  fun fieldsOf(clazz: KClass<*>): Field<TSelectionSetParent> =
    apply {
      clazz.members
        .filter { it is KProperty }
        .filter { it.visibility == KVisibility.PUBLIC }
        .map {
          when (it.returnType) {
            Boolean::class.java ->
              field(it.name)
          }
          parent
        }
    }

  fun fieldsOf(clazz: Class<*>): Field<TSelectionSetParent> =
    apply {
      clazz.declaredFields
        .filter { !it.isTransient }
        //.filter { it.isPublic }
        .map {
          when {
            it.type.isPrimitiveOrWrapper ->
              field(it.serializedName)
            it.type.isEnum ->
              field(it.serializedName)
            it.type.isArray -> {
              val componentType = it.type.componentType
              if (componentType.isPrimitiveOrWrapper || componentType.isEnum) {
                field(it.serializedName)
              } else {
                objectField(it.serializedName)
                  .fieldsOf(componentType)
              }
            }
            // TODO
            /*it.genericType.isList ->
              objectField(it.serializedName)
                .fieldsOf((it.genericType as ParameterizedType).actualTypeArguments[0].javaClass)*/
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
