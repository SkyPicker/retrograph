package com.kiwi.mobile.retrograph.model

/**
 * Class representing argument primitive value.
 */
open class Value<TValue>(
  val name: String = "",
  val value: TValue
) {

  // region Public Methods

  override fun toString() = "${buildNameString()}${buildValueString()}"

  // endregion Public Methods

  // region Protected Methods

  protected open fun buildNameString() = if (name.isNotEmpty()) "$name: " else ""

  protected open fun buildValueString() =
    when (value) {
      is String -> "\"$value\""
      else -> "$value"
    }

  // endregion Protected Methods
}

/**
 * Class representing argument list value.
 */
class ListValue<TValuesParent>(
  val parent: Values<TValuesParent>,
  name: String = ""
):
  Value<Unit>(name, Unit) {

  // region Public Properties

  val isEmpty
    get() = values.isEmpty

  val isNotEmpty
    get() = !isEmpty

  // endregion Public Properties

  // region Private Properties

  private val values = Values(this)

  // endregion Private Properties

  // region Public Methods

  fun values() = values

  fun value(value: Any?) =
    apply {
      values.value("", value)
    }

  fun values(vararg values: Any?) =
    apply {
      values.forEach {
        this.values.value("", it)
      }
    }

  fun objectValue() =
    values.objectValue("")

  fun finish() = parent.finish()

  // endregion Public Methods

  // region Protected Methods

  override fun buildValueString() = "[ $values ]"

  // endregion Protected Methods
}

/**
 * Class representing argument object value.
 *
 * @param TValuesParent Values parent instance of [ObjectArgument] or [ObjectValue].
 */
class ObjectValue<TValuesParent>(
  val parent: Values<TValuesParent>,
  name: String = ""
):
  Value<Unit>(name, Unit) {

  // region Public Properties

  val isEmpty
    get() = values.isEmpty

  val isNotEmpty
    get() = !isEmpty

  // endregion Public Properties

  // region Private Properties

  private val values = Values(this)

  // endregion Private Properties

  // region Public Methods

  fun values() = values

  fun value(name: String, value: Any?) =
    apply {
      values.value(name, value)
    }

  fun listValue(name: String) =
    values.listValue(name)

  fun objectValue(name: String) =
    values.objectValue(name)

  fun valuesOf(instance: Any?): Values<ObjectValue<TValuesParent>> =
    values.valuesOf(instance)

  fun finish() = parent.finish()

  // endregion Public Methods

  // region Protected Methods

  override fun buildValueString() = "{ $values }"

  // endregion Protected Methods
}
