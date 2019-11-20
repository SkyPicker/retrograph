package com.kiwi.mobile.retrograph.model

import com.kiwi.mobile.retrograph.extension.*

import java.lang.reflect.Field

/**
 * Class representing argument object value.
 *
 * @param TParent Parent instance of [ObjectArgument] or [Value].
 */
class Values<TParent>(
  val parent: TParent
) {

  // region Public Properties

  val isEmpty
    get() = values.isEmpty()

  val isNotEmpty
    get() = !isEmpty

  // endregion Public Properties

  // region Private Properties

  private val values = mutableListOf<Value<*>>()

  // endregion Private Properties

  // region Public Methods

  fun value(name: String, value: Any?) =
    Value(name, value)
      .also {
        values.add(it)
      }

  fun listValue(name: String) =
    ListValue(this, name)
      .also {
        values.add(it)
      }

  fun objectValue(name: String) =
    ObjectValue(this, name)
      .also {
        values.add(it)
      }

  // TODO: Not duplicate this algorithm in Arguments.
  fun valuesOf(instance: Any?) =
    apply {
      instance.fields
        .filter { !it.value.isTransient && !it.value.isStatic }
        .filter { !it.value.name.endsWith("\$delegate") }
        .forEach { valuesOf(instance, it.toPair()) }
    }

  fun finish() = parent

  override fun toString() = values.joinToString(separator = ", ")

  // endregion Public Methods

  // region Private Methods

  private fun valuesOf(instance: Any?, nameAndField: Pair<String, Field>) {
    val (name, field) = nameAndField
    val value = field.get(instance)
    when {
      value == null ->
        nullValue(instance, name)
      field.type.isPrimitiveOrWrapper ->
        value(name, value)
      field.type.isEnum ->
        value(name, value)
      field.type.isArray ->
        arrayValue(field, name, value as Array<*>)
      field.type.isList ->
        listValue(field, name, value as List<*>)
      else ->
        objectValue(name)
          .valuesOf(value)
          .finish()
    }
  }

  private fun nullValue(instance: Any?, name: String) {
    val ignoreNulls = instance?.javaClass?.hasIgnoreNulls ?: false
    if (!ignoreNulls) {
      value(name, null)
    }
  }

  private fun arrayValue(field: Field, name: String, value: Array<*>) {
    val componentType = field.type.componentType
    if (componentType.isPrimitiveOrWrapper || componentType.isEnum) {
      listValue(name)
        .values(*value)
        .finish()
    } else {
      listValue(name)
        .apply {
          value.forEach { item ->
            objectValue()
              .valuesOf(item)
              .finish()
          }
        }
        .finish()
    }
  }

  private fun listValue(field: Field, name: String, value: List<*>) {
    val array = value.toTypedArray()
    val componentType = field.parameterUpperBound
    if (componentType.isPrimitiveOrWrapper || componentType.isEnum) {
      listValue(name)
        .values(*array)
        .finish()
    } else {
      listValue(name)
        .apply {
          value.forEach { item ->
            objectValue()
              .valuesOf(item)
              .finish()
          }
        }
        .finish()
    }
  }

  // endregion Private Methods
}
