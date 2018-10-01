package com.kiwi.mobile.retrograph.extension

import com.google.gson.annotations.*
import com.google.gson.reflect.*

import com.kiwi.mobile.retrograph.annotation.*
import com.kiwi.mobile.retrograph.model.*

import io.reactivex.*

import java.lang.reflect.*
import java.lang.reflect.Array
import java.lang.reflect.Field

// TODO: Move to trinerdis-utils.

private val WRAPPER_TYPES = arrayOf(
  Boolean::class.java,
  java.lang.Boolean::class.java,
  Char::class.java,
  java.lang.Character::class.java,
  String::class.java,
  java.lang.String::class.java,
  Byte::class.java,
  java.lang.Byte::class.java,
  Short::class.java,
  java.lang.Short::class.java,
  Integer::class.java,
  java.lang.Integer::class.java,
  Long::class.java,
  java.lang.Long::class.java,
  Float::class.java,
  java.lang.Float::class.java,
  Double::class.java,
  java.lang.Double::class.java,
  Unit::class.java,
  java.lang.Void::class.java
)

/**
 * Resolve whether (this if primitive or wrapper type.
 */
val Class<*>.isPrimitiveOrWrapper
  get() = isPrimitive || WRAPPER_TYPES.contains(this)

val Class<*>.isList
  get() = List::class.java.isAssignableFrom(this)

val Class<*>.ignoreNulls
  get() = isAnnotationPresent(IgnoreNulls::class.java)

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

val Type.isParameterized
  get() = this is ParameterizedType

val Type.parameterUpperBound
  get() = (this as? ParameterizedType)?.run { parameterUpperBound }

val Type.isGenericArray
  get() = this is GenericArrayType

val Type.isTypeVariable
  get() = this is TypeVariable<*>

val Type.isWildcard
  get() = this is WildcardType

val ParameterizedType.parameterUpperBound
  get(): Type {
    val paramType = actualTypeArguments[0]
    return if (paramType is WildcardType) {
      paramType.upperBounds[0]
    } else {
      paramType
    }
  }

val Type.isWildcardGeneric
  get() = isParameterized && ((this as ParameterizedType).actualTypeArguments[0] is WildcardType)
    && (parameterUpperBound == object: TypeToken<Any>() {}.type)

val Type.isRxType
  get() = (this == Observable::class.java)
    || (this == Flowable::class.java)
    || (this == Single::class.java)
    || (this == Maybe::class.java)

val Type.rxType
  get() = when (this) {
    Observable::class.java -> RxType.OBSERVABLE
    Flowable::class.java -> RxType.FLOWABLE
    Single::class.java -> RxType.SINGLE
    Maybe::class.java -> RxType.MAYBE
    else -> RxType.UNKNOWN
  }

val Any?.fields: Map<String, Field>
  get() = if (this != null) {
    this.javaClass.declaredFields
      .map {
        it.isAccessible = true
        it.aliasedName to it
      }
      .toMap<String, Field>()
  } else {
    mapOf()
  }

val Field.isPublic
  get() = Modifier.isPublic(modifiers)

val Field.isPrivate
  get() = Modifier.isPrivate(modifiers)

val Field.isProtected
  get() = Modifier.isProtected(modifiers)

val Field.isStatic
  get() = Modifier.isStatic(modifiers)

val Field.isFinal
  get() = Modifier.isFinal(modifiers)

val Field.isSynchronized
  get() = Modifier.isSynchronized(modifiers)

val Field.isVolatile
  get() = Modifier.isVolatile(modifiers)

val Field.isTransient
  get() = Modifier.isTransient(modifiers)

val Field.isNative
  get() = Modifier.isNative(modifiers)

val Field.isInterface
  get() = Modifier.isInterface(modifiers)

val Field.isAbstract
  get() = Modifier.isAbstract(modifiers)

val Field.isStrict
  get() = Modifier.isStrict(modifiers)

val Field.parameterUpperBound
  get() = when {
    type.isArray -> type.componentType
    else -> genericType.parameterUpperBound as? Class<*>
  }

val Field.serializedName
  get() = getAnnotation(SerializedName::class.java)?.value

val Field.alias
  get() = getAnnotation(Alias::class.java)?.name

val Field.aliasedName
  get() = alias ?: name

val Field.aliasOrEmpty
  get() = if (getAnnotation(Alias::class.java) != null) name else ""
