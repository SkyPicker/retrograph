package com.kiwi.mobile.retrograph.model

import java.lang.reflect.*

internal class ResponseType(
  private val argumentType: Type
): ParameterizedType {

  // region Public Methods

  override fun getRawType() = Response::class.java

  override fun getOwnerType() = null

  override fun getActualTypeArguments() = arrayOf(argumentType)

  override fun equals(other: Any?): Boolean {
    if ((other == null) || (other !is ParameterizedType)) {
      return false
    }
    return rawType == other.rawType
      && actualTypeArguments.contentEquals(other.actualTypeArguments)
  }

  override fun hashCode() = 31 * rawType.hashCode() + actualTypeArguments.hashCode()

  // endregion Public Methods
}
