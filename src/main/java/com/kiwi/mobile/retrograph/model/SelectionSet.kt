package com.kiwi.mobile.retrograph.model

import kotlin.reflect.*

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

  fun fieldsOf(name: String, clazz: Class<*>) =
    apply {
      objectField(name)
        .fieldsOf(clazz)
    }

  fun fieldsOf(name: String, clazz: KClass<*>) =
    apply {
      objectField(name)
        .fieldsOf(clazz)
    }

  fun finish() = parent

  override fun toString() = fields.joinToString(separator = ", ")

  // endregion Public Methods
}
