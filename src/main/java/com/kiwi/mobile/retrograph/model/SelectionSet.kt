package com.kiwi.mobile.retrograph.model

import com.kiwi.mobile.retrograph.extension.*

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

  fun fieldsOf(clazz: Class<*>, arguments: Any? = null) =
    apply {
      val argumentsFields = arguments.fields

      clazz.declaredFields
        .filter { !it.isTransient && !it.isStatic }
        .filter { !it.name.endsWith("\$delegate")}
        .map {
          val field = when {
            it.type.isPrimitiveOrWrapper ->
              field(it.aliasedName, it.aliasOrEmpty)
            it.type.isEnum ->
              field(it.aliasedName, it.aliasOrEmpty)
            it.type.isArray -> {
              val componentType = it.parameterUpperBound!!
              if (componentType.isPrimitiveOrWrapper || componentType.isEnum) {
                field(it.aliasedName, it.aliasOrEmpty)
              } else {
                objectField(it.aliasedName, it.aliasOrEmpty)
                  .fieldsOf(componentType)
              }
            }
            it.type.isList -> {
              val componentType = it.parameterUpperBound!!
              if (componentType.isPrimitiveOrWrapper || componentType.isEnum) {
                field(it.aliasedName, it.aliasOrEmpty)
              } else {
                objectField(it.aliasedName, it.aliasOrEmpty)
                  .fieldsOf(componentType)
              }
            }
            else ->
              objectField(it.aliasedName, it.aliasOrEmpty)
                .fieldsOf(it.type)
          }

          if (argumentsFields.containsKey(it.name)) {
            field.argumentsOf(argumentsFields[it.name]?.get(arguments))
          }

          parent
        }
    }

  fun fieldsOf(clazz: KClass<*>, arguments: Any? = null) =
    apply {
      clazz.members
        .filter { it is KProperty }
        .filter { it.visibility == KVisibility.PUBLIC }
        .map {
          // TODO: Generate rquest using Kotlin reflection.
          when (it.returnType) {
            Boolean::class ->
              field(it.name)
          }
          parent
        }
    }

  fun finish() = parent

  override fun toString() = fields.joinToString(separator = ", ")

  // endregion Public Methods
}
