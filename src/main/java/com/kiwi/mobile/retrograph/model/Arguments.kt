package com.kiwi.mobile.retrograph.model

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

  fun argument(name: String, values: List<Any?>) =
    apply {
      arguments.add(Argument(name, values))
    }

  fun objectArgument(name: String) =
    ObjectArgument(this, name)
      .also {
        arguments.add(it)
      }

  fun argumentsOf(name: String, instance: Any) =
    apply {
      objectArgument(name)
        .valuesOf(instance)
    }

  fun finish() = parent

  override fun toString() = arguments.joinToString(separator = ", ")

  // endregion Public Methods
}
