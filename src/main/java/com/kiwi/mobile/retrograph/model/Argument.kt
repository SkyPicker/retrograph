package com.kiwi.mobile.retrograph.model

import com.kiwi.mobile.retrograph.extension.*

/**
 * Class representing argument.
 */
open class Argument<TValue>(
  val name: String = "",
  val value: TValue
) {

  // region Public Methods

  override fun toString() = "$name: ${buildValueString()}"

  // endregion Public Methods

  // region Private Methods

  private fun buildValueString() =
    when (value) {
      is String -> "\"$value\""
      is Array<*> -> "[" + value.joinToString(separator = ", ") + "]"
      else -> "$value"
    }

  // endregion Private Methods
}

/**
 * Class representing object argument.
 */
class ObjectArgument<TSelectionSetParent>(
  val parent: Arguments<TSelectionSetParent>,
  name: String = ""
):
  Argument<MutableList<Value<*>>>(name, mutableListOf()) {

  // region Public Properties

  val isEmpty
    get() = value.isEmpty()

  val isNotEmpty
    get() = !isEmpty

  // endregion Public Properties

  // region Public Methods

  fun value(name: String, value: Any?) =
    apply {
      this.value.add(Value(name, value))
    }

  fun value(name: String, values: List<Any?>) =
    apply {
      value.add(Value(name, values))
    }

  fun objectValue(name: String) =
    ObjectValue(this, name)
      .also {
        value.add(it)
      }

  fun valuesOf(instance: Any): ObjectArgument<TSelectionSetParent> =
    apply {
      instance.javaClass.declaredFields
        .filter { !it.isFinal }
        .map {
          when {
            it.type.isPrimitiveOrWrapper ->
              value(it.serializedName, it.get(instance))
            it.type.isArray ->
              value(it.serializedName, it.get(instance))
            else ->
              objectValue(it.serializedName)
                .valuesOf(it.get(instance))
          }
          parent
        }
    }

  fun finish() = parent

  override fun toString() = """$name: { ${value.joinToString(separator = ", ")} }"""

  // endregion Public Methods
}
