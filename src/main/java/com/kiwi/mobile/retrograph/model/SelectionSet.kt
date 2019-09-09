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
      `class`.declaredFields
        .filter(isInstanceField)
        .filter(isNotDelegate)
        .map { javaField ->
          run { resolveField(javaField) }
            .run { resolveArguments(javaField, arguments) }
        }
    }

  fun fieldsOf(`class`: KClass<*>, arguments: Any? = null) =
    fieldsOf(`class`.java, arguments)

  inline fun <reified T> fieldsOf(arguments: Any? = null) =
    fieldsOf(T::class, arguments)

  fun finish() = parent

  override fun toString() = fields.joinToString(separator = ", ")

  // endregion Public Methods

  // region Private Methods

  private val isInstanceField = { javaField: JavaField ->
    !javaField.isTransient && !javaField.isStatic
  }

  private val isNotDelegate = { javaField: JavaField ->
    !javaField.name.endsWith("\$delegate")
  }

  private fun <T> SelectionSet<T>.resolveField(javaField: JavaField) =
    when {
      javaField.hasInlineFragment && javaField.type.isList ->
        objectField(javaField.aliasOrName, javaField.nameOrEmpty)
          .inlineFragment(javaField.parameterUpperBound.simpleName)
          .fieldsOf(javaField.parameterUpperBound)
      javaField.hasInlineFragment && javaField.type.isArray -> {
        objectField(javaField.aliasOrName, javaField.nameOrEmpty)
          .inlineFragment(javaField.parameterUpperBound.simpleName)
          .fieldsOf(javaField.parameterUpperBound)
      }
      javaField.hasInlineFragment ->
        objectField(javaField.aliasOrName, javaField.nameOrEmpty)
          .inlineFragment(javaField.type.simpleName)
          .fieldsOf(javaField.type)
      javaField.type.isPrimitiveOrWrapper ->
        field(javaField.aliasOrName, javaField.nameOrEmpty)
      javaField.type.isEnum ->
        field(javaField.aliasOrName, javaField.nameOrEmpty)
      javaField.type.isArray -> {
        val componentType = javaField.parameterUpperBound
        if (componentType.isPrimitiveOrWrapper || componentType.isEnum) {
          field(javaField.aliasOrName, javaField.nameOrEmpty)
        } else {
          objectField(javaField.aliasOrName, javaField.nameOrEmpty)
            .fieldsOf(componentType)
        }
      }
      javaField.type.isList -> {
        val componentType = javaField.parameterUpperBound
        if (componentType.isPrimitiveOrWrapper || componentType.isEnum) {
          field(javaField.aliasOrName, javaField.nameOrEmpty)
        } else {
          objectField(javaField.aliasOrName, javaField.nameOrEmpty)
            .fieldsOf(componentType)
        }
      }
      else ->
        objectField(javaField.aliasOrName, javaField.nameOrEmpty)
          .fieldsOf(javaField.type)
    }

  private fun <T> Field<T>.resolveArguments(javaField: JavaField, arguments: Any?) =
    if (arguments.fields.containsKey(javaField.name)) {
      argumentsOf(arguments.fields[javaField.name]?.get(arguments))
    } else {
      this
    }

  // endregion Private Methods
}
