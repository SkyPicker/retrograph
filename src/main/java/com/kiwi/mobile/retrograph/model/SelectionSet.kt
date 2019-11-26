package com.kiwi.mobile.retrograph.model

import com.kiwi.mobile.retrograph.extension.*

import kotlin.reflect.*

import java.lang.reflect.Field as JavaField

/**
 * Class representing selection set.
 *
 * TODO: FragmentSpread
 * TODO: InlineFragment
 */
class SelectionSet<TParent>(
  val parent: TParent
) {

  // region Public Properties

  val isEmpty
    get() = fields.isEmpty()

  val isNotEmpty
    get() = !isEmpty

  // endregion Public Properties

  // region Private Properties

  private val fields = mutableListOf<Field<TParent>>()

  // endregion Private Properties

  // region Public Methods

  fun field(name: String, alias: String = "") =
    Field(this, name, alias)
      .also {
        fields.add(it)
      }

  fun objectField(name: String, alias: String = "") =
    Field(this, name, alias)
      .also {
        fields.add(it)
      }

  fun inlineFragment(name: String) =
    Field(this, "... on $name")
      .also {
        fields.add(it)
      }

  fun fieldsOf(`class`: Class<*>, arguments: Any? = null) =
    apply {
      val argumentsFields = arguments.serializableFields
      `class`.serializableFields
        .forEach { resolveField(it.value, argumentsFields[it.key]?.get(arguments)) }
    }

  fun fieldsOf(`class`: KClass<*>, arguments: Any? = null) =
    fieldsOf(`class`.java, arguments)

  inline fun <reified T> fieldsOf(arguments: Any? = null) =
    fieldsOf(T::class, arguments)

  fun finish() = parent

  override fun toString() = fields.joinToString(separator = ", ")

  // endregion Public Methods

  // region Private Methods

  private fun resolveField(javaField: JavaField, arguments: Any?) =
    when {
      javaField.hasInlineFragment && javaField.type.isList ->
        objectFragmentField(javaField, javaField.parameterUpperBound, arguments)
      javaField.hasInlineFragment && javaField.type.isArray ->
        objectFragmentField(javaField, javaField.parameterUpperBound, arguments)
      javaField.hasInlineFragment ->
        objectFragmentField(javaField, javaField.type, arguments)
      javaField.type.isPrimitiveOrWrapper ->
        primitiveField(javaField, arguments)
      javaField.type.isEnum ->
        primitiveField(javaField, arguments)
      javaField.type.isList || javaField.type.isArray -> {
        val componentType = javaField.parameterUpperBound
        if (componentType.isPrimitiveOrWrapper || componentType.isEnum) {
          primitiveField(javaField, arguments)
        } else {
          objectField(javaField, componentType, arguments)
        }
      }
      else ->
        objectField(javaField, javaField.type, arguments)
    }

  private fun primitiveField(javaField: JavaField, arguments: Any?) =
    field(javaField.aliasOrName, javaField.nameOrEmpty)
      .argumentsOf(arguments)

  private fun objectField(javaField: JavaField, type: Class<*>, arguments: Any?) =
    objectField(javaField.aliasOrName, javaField.nameOrEmpty)
      .fieldsOf(type, arguments)
      .argumentsOf(arguments, arguments.remainingArguments(type))

  private fun objectFragmentField(javaField: JavaField, type: Class<*>, arguments: Any?) =
    objectField(javaField.aliasOrName, javaField.nameOrEmpty)
      .inlineFragment(type.simpleName)
      .fieldsOf(type, arguments)
      .argumentsOf(arguments, arguments.remainingArguments(type))

  private fun Any?.remainingArguments(type: Class<*>): Map<String, JavaField> {
    val typeFields = type.serializableFields
    return serializableFields.filter { !typeFields.containsKey(it.key) }
  }

  // endregion Private Methods
}
