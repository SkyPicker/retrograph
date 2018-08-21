package com.kiwi.mobile.retrograph

import com.kiwi.mobile.retrograph.annotation.*
import com.kiwi.mobile.retrograph.extension.*
import com.kiwi.mobile.retrograph.model.*

import io.reactivex.*

import okhttp3.*

import retrofit2.*
import retrofit2.adapter.rxjava2.*

import java.lang.reflect.*

import com.kiwi.mobile.retrograph.model.Response as GraphQLResponse

import retrofit2.Response as RetrofitResponse

class GraphQLCallAdapterFactory private constructor(
  private val scheduler: Scheduler?
):
  CallAdapter.Factory() {

  // region Public Types

  companion object {

    /**
     * Returns an instance which creates synchronous observables that do not operate on any
     * scheduler by default.
     */
    @JvmStatic
    fun create() = GraphQLCallAdapterFactory(null)

    /**
     * Returns an instance which creates synchronous observables that
     * [subscribe on][Observable.subscribeOn] `scheduler` by default.
     */
    @JvmStatic
    fun createWithScheduler(scheduler: Scheduler) = GraphQLCallAdapterFactory(scheduler)
  }

  // endregion Public Types

  // region Public Methods

  override fun get(
    returnType: Type,
    annotations: Array<Annotation>,
    retrofit: Retrofit
  ): CallAdapter<*, *>? {
    if (!isGraphQLRequest(annotations)) {
      return null
    }

    val rawType = returnType.rawType
    if (rawType == Completable::class.java) {
      // Completable is not parameterized (which is what the rest of this method deals with) so it
      // can only be created with a single configuration.
      return GraphQLCallAdapter<GraphQLResponse<Any>, Any>(
        ResponseBody::class.java, scheduler, false, true, false, false, false, false, true
      )
    }

    val isObservable = rawType == Observable::class.java
    val isFlowable = rawType == Flowable::class.java
    val isSingle = rawType == Single::class.java
    val isMaybe = rawType == Maybe::class.java
    if (!isObservable && !isFlowable && !isSingle && !isMaybe) {
      return null
    }

    var isResult = false
    var isBody = false
    var isGraphQLResponse = false
    val responseType: Type
    if (!returnType.isParameterized || returnType.isWildcardGeneric) {
      val name = when {
        isFlowable -> "Flowable"
        isSingle -> "Single"
        isMaybe -> "Maybe"
        isObservable -> "Observable"
        else -> "Unknown"
      }
      throwRxMustBeParametrized(name)
    }

    val observableType = returnType.parameterUpperBound!!
    val rawObservableType = observableType.rawType
    when (rawObservableType) {
      RetrofitResponse::class.java -> {
        if (!observableType.isParameterized || observableType.isWildcardGeneric) {
          throwResponseMustBeParametrized()
        }

        val retrofitResponseType = observableType.parameterUpperBound!!
        val rawResponseType = retrofitResponseType.rawType
        if (rawResponseType == GraphQLResponse::class.java) {
          if (!retrofitResponseType.isParameterized || retrofitResponseType.isWildcardGeneric) {
            throwResponseResponseMustBeParametrized()
          }

          responseType = retrofitResponseType
          isGraphQLResponse = true
        } else {
          responseType = ResponseType(retrofitResponseType)
        }
      }
      Result::class.java -> {
        if (!observableType.isParameterized || observableType.isWildcardGeneric) {
          throwResultMustBeParametrized()
        }

        val resultType = observableType.parameterUpperBound!!
        isResult = true

        val rawResponseType = resultType.rawType
        if (rawResponseType == GraphQLResponse::class.java) {
          if (!resultType.isParameterized || resultType.isWildcardGeneric) {
            throwResultResponseMustBeParametrized()
          }

          responseType = resultType
          isGraphQLResponse = true
        } else {
          responseType = ResponseType(resultType)
        }
      }
      GraphQLResponse::class.java -> {
        if (!observableType.isParameterized || observableType.isWildcardGeneric) {
          throwResponseMustBeParametrized()
        }

        responseType = observableType
        isGraphQLResponse = true
        isBody = true
      }
      else -> {
        responseType = ResponseType(observableType)
        isBody = true
      }
    }

    return GraphQLCallAdapter<GraphQLResponse<Any>, Any>(
      responseType, scheduler, isResult, isBody, isGraphQLResponse, isFlowable, isSingle, isMaybe,
      false
    )
  }

  // endregion Public Methods

  // region Private Methods

  private fun isGraphQLRequest(annotations: Array<Annotation>) = annotations
    .find { it is GraphQL } != null

  private fun throwRxMustBeParametrized(name: String) {
    throw IllegalStateException(
      "$name return type must be parameterized as $name<Foo> or $name<? extends Foo>"
    )
  }

  private fun throwResponseMustBeParametrized() {
    throw IllegalStateException(
      "Response must be parameterized as Response<Foo> or Response<? extends Foo>"
    )
  }

  private fun throwResponseResponseMustBeParametrized() {
    throw IllegalStateException(
      "Response must be parameterized as Response<Response<Foo>> or Response<Response<? extends Foo>>"
    )
  }

  private fun throwResultMustBeParametrized() {
    throw IllegalStateException(
      "Result must be parameterized as Result<Foo> or Result<? extends Foo>"
    )
  }

  private fun throwResultResponseMustBeParametrized() {
    throw IllegalStateException(
      "Result must be parameterized as Result<Response<Foo>> or Result<Response<? extends Foo>>"
    )
  }

  // endregion Private Methods
}
