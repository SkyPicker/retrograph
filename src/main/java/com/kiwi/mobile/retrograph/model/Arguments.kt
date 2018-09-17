package com.kiwi.mobile.retrograph.model

import com.kiwi.mobile.retrograph.extension.*

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

  fun argumentsOf(instance: Any?) =
    apply {
      instance.fields
        .filter { !it.value.isTransient }
        //.filter { it.value.isPublic }
        .forEach {
          val (name, field) = it
          val value = field.get(instance)
          when {
            value == null ->
              argument(name, null)
            field.type.isPrimitiveOrWrapper ->
              argument(name, value)
            field.type.isEnum ->
              argument(name, value)
            field.type.isArray -> {
              val array = value as Array<*>
              val componentType = field.parameterUpperBound!!
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
            field.type.isList -> {
              val array = (value as List<*>).toTypedArray()
              val componentType = field.parameterUpperBound!!
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
            else ->
              objectArgument(name)
                .valuesOf(value)
                .finish()
          }
        }
    }

  fun finish() = parent

  override fun toString() = arguments.joinToString(separator = ", ")

  // endregion Public Methods
}
