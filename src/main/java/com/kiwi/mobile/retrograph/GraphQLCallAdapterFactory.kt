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
      return GraphQLCallAdapter<Any>(
        ResponseBody::class.java, scheduler, false, true, false, RxType.COMPLETABLE
      )
    }

    if (!rawType.isRxType) {
      return null
    }

    var isResult = false
    var isBody = false
    var isGraphQLResponse = false
    val responseType: Type
    if (!returnType.isParameterized || returnType.isWildcardGeneric) {
      throwRxTypeMustBeParametrized(rawType.rxType)
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

    return GraphQLCallAdapter<Any>(
      responseType, scheduler, isResult, isBody, isGraphQLResponse, rawType.rxType
    )
  }

  // endregion Public Methods

  // region Private Methods

  private fun isGraphQLRequest(annotations: Array<Annotation>) = annotations
    .find { it is GraphQL } != null

  private fun throwRxTypeMustBeParametrized(type: RxType) {
    throw IllegalStateException(
      "${type.id} return type must be parameterized as ${type.id}<Foo> or ${type.id}<? extends Foo>"
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
