package com.kiwi.mobile.retrograph.model

import com.kiwi.mobile.retrograph.extension.*

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

  fun valuesOf(instance: Any?) =
    apply {
      instance.fields
        .filter { !it.value.isTransient }
        //.filter { it.value.isPublic }
        .forEach {
          val (name, field) = it
          val value = field.get(instance)
          when {
            value == null ->
              value(name, null)
            field.type.isPrimitiveOrWrapper ->
              value(name, value)
            field.type.isEnum ->
              value(name, value)
            field.type.isArray -> {
              val array = value as Array<*>
              val componentType = field.type.componentType
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
            field.type.isList -> {
              val array = (value as List<*>).toTypedArray()
              val componentType = field.parameterUpperBound!!
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
            else ->
              objectValue(name)
                .valuesOf(value)
                .finish()
          }
        }
    }

  fun finish() = parent

  override fun toString() = values.joinToString(separator = ", ")

  // endregion Public Methods
}
