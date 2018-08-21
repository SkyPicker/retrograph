package com.kiwi.mobile.retrograph.extension

import com.google.gson.annotations.*
import com.google.gson.reflect.*

import java.lang.reflect.*
import java.lang.reflect.Array

// TODO: Move to trinerdis-utils.

/**
 * Resolve whether (this if primitive or wrapper type.
 */
val Class<*>.isPrimitiveOrWrapper
  get() = isPrimitive
    || (this == Boolean::class.java)
    || (this == Char::class.java)
    || (this == String::class.java)
    || (this == Byte::class.java)
    || (this == Short::class.java)
    || (this == Int::class.java)
    || (this == Long::class.java)
    || (this == Float::class.java)
    || (this == Double::class.java)
    || (this == Void::class.java)

val Field.isFinal
  get() = Modifier.isFinal(modifiers)

val Field.serializedName
  get() = getAnnotation(SerializedName::class.java)?.value ?: name

val Type.rawType
  get(): Type =
    when (this) {
      is Class<*> -> {
        this
      }
      is ParameterizedType -> {
        // I'm not exactly sure why getRawType() returns Type instead of Class. Neal isn't either
        // but suspects some pathological case related to nested classes exists.
        rawType as? Class<*> ?: throw IllegalArgumentException()
      }
      is GenericArrayType -> {
        Array.newInstance(genericComponentType.rawType as Class<*>, 0).javaClass
      }
      is TypeVariable<*> -> {
        // We could use the variable's bounds, but that won't work if there are multiple. Having
        // a raw type that's more general than necessary is okay.
        Any::class.java
      }
      is WildcardType -> {
        upperBounds[0].rawType
      }
      else -> {
        throw IllegalArgumentException(
          "Expected a Class, ParameterizedType, or GenericArrayType, but <$this> is of type "
            + javaClass.name
        )
      }
    }

val ParameterizedType.parameterUpperBound
  get(): Type {
    val paramType = actualTypeArguments[0]
    return if (paramType is WildcardType) {
      paramType.upperBounds[0]
    } else {
      paramType
    }
  }

val Type.parameterUpperBound
  get() = (this as? ParameterizedType)?.run { parameterUpperBound }

val Type.isParameterized
  get() = this is ParameterizedType

val Type.isGenericArray
  get() = this is GenericArrayType

val Type.isTypeVariable
  get() = this is TypeVariable<*>

val Type.isWildcard
  get() = this is WildcardType

val Type.isWildcardGeneric
  get() = isParameterized && ((this as ParameterizedType).actualTypeArguments[0] is WildcardType)
    && (parameterUpperBound == object: TypeToken<Any>() {}.type)
