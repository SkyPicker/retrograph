package com.kiwi.mobile.retrograph.model

import com.kiwi.mobile.retrograph.extension.*

import java.lang.reflect.Field as JavaField

class Arguments<TSelectionSetParent>(
  val parent: Field<TSelectionSetParent>
) {

  // region Public Properties

  val isEmpty
    get() = arguments.isEmpty()

  val isNotEmpty
    get() = !isEmpty

  // endregion Public Properties

  // region Private Properties

  private val arguments = mutableListOf<Argument<*>>()

  // endregion Private Properties

  // region Public Methods

  fun argument(name: String, value: Any?) =
    apply {
      arguments.add(Argument(name, value))
    }

  fun listArgument(name: String) =
    ListArgument(this, name)
      .also {
        arguments.add(it)
      }

  fun objectArgument(name: String) =
    ObjectArgument(this, name)
      .also {
        arguments.add(it)
      }

  // TODO: Not duplicate this algorithm in Values.
  fun argumentsOf(instance: Any?) =
    apply {
      instance.fields
        .filter { !it.value.isTransient && !it.value.isStatic }
        .filter { !it.value.name.endsWith("\$delegate") }
        .forEach { argumentsOf(instance, it.toPair()) }
    }

  fun finish() = parent

  override fun toString() = arguments.joinToString(separator = ", ")

  // endregion Public Methods

  // region Private Methods

  private fun argumentsOf(instance: Any?, nameAndField: Pair<String, JavaField>) {
    val (name, field) = nameAndField
    val value = field.get(instance)
    when {
      value == null ->
        nullArgument(instance, name)
      field.type.isPrimitiveOrWrapper ->
        argument(name, value)
      field.type.isEnum ->
        argument(name, value)
      field.type.isArray ->
        arrayArgument(field, name, value as Array<*>)
      field.type.isList ->
        listArgument(field, name, value as List<*>)
      else ->
        objectArgument(name, value)
    }
  }

  private fun nullArgument(instance: Any?, name: String) {
    val ignoreNulls = instance?.javaClass?.hasIgnoreNulls ?: false
    if (!ignoreNulls) {
      argument(name, null)
    }
  }

  private fun arrayArgument(field: JavaField, name: String, value: Array<*>) {
    val componentType = field.parameterUpperBound
    if (componentType.isPrimitiveOrWrapper || componentType.isEnum) {
      listArgument(name)
        .values(*value)
        .finish()
    } else {
      listArgument(name)
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

  private fun listArgument(field: JavaField, name: String, value: List<*>) {
    val array = value.toTypedArray()
    val componentType = field.parameterUpperBound
    if (componentType.isPrimitiveOrWrapper || componentType.isEnum) {
      listArgument(name)
        .values(*array)
        .finish()
    } else {
      listArgument(name)
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

  private fun objectArgument(name: String, value: Any) {
    objectArgument(name)
      .valuesOf(value)
      .finish()
  }

  // endregion Private Methods
}
