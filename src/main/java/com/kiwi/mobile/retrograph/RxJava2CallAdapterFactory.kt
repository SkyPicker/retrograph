package com.kiwi.mobile.retrograph

import com.kiwi.mobile.retrograph.extension.*

import io.reactivex.*

import okhttp3.*

import retrofit2.*
import retrofit2.adapter.rxjava2.*

import java.lang.reflect.*

import com.kiwi.mobile.retrograph.model.Response as GraphQLResponse

import retrofit2.Response as RetrofitResponse

class RxJava2CallAdapterFactory private constructor(
  private val scheduler: Scheduler?,
  private val isAsync: Boolean
):
  CallAdapter.Factory() {

  // region Public Types

  companion object {

    /**
     * Returns an instance which creates synchronous observables that do not operate on any
     * scheduler by default.
     */
    @JvmStatic
    fun create() = RxJava2CallAdapterFactory(null, false)

    /**
     * Returns an instance which creates asynchronous observables. Applying
     * [Observable.subscribeOn] has no effect on stream types created by this factory.
     */
    @JvmStatic
    fun createAsync() = RxJava2CallAdapterFactory(null, true)

    /**
     * Returns an instance which creates synchronous observables that
     * [subscribe on][Observable.subscribeOn] `scheduler` by default.
     */
    @JvmStatic
    fun createWithScheduler(scheduler: Scheduler) = RxJava2CallAdapterFactory(scheduler, false)
  }

  // endregion Public Types

  // region Public Methods

  override fun get(
    returnType: Type,
    annotations: Array<Annotation>,
    retrofit: Retrofit
  ): CallAdapter<*, *>? {
    val rawType = returnType.rawType
    if (rawType == Completable::class.java) {
      // Completable is not parameterized (which is what the rest of this method deals with) so it
      // can only be created with a single configuration.
      return RxJava2CallAdapter<Any>(
        ResponseBody::class.java, scheduler, isAsync, false, true, false, false, false, true
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
    val responseType: Type
    if (!returnType.isParameterized || returnType.isWildcardGeneric) {
      val name = when {
        isFlowable -> "Flowable"
        isSingle -> "Single"
        isMaybe -> "Maybe"
        isObservable -> "Observable"
        else -> "Unknown"
      }
      throw IllegalStateException(
        name + " return type must be parameterized as " + name + "<Foo> or " + name
          + "<? extends Foo>"
      )
    }

    val observableType = returnType.parameterUpperBound!!
    val rawObservableType = observableType.rawType
    when (rawObservableType) {
      RetrofitResponse::class.java -> {
        if (!observableType.isParameterized || observableType.isWildcardGeneric) {
          throw IllegalStateException(
            "Response must be parameterized as Response<Foo> or Response<? extends Foo>"
          )
        }
        responseType = observableType.parameterUpperBound!!
      }
      Result::class.java -> {
        if (!observableType.isParameterized || observableType.isWildcardGeneric) {
          throw IllegalStateException(
            "Result must be parameterized as Result<Foo> or Result<? extends Foo>"
          )
        }
        responseType = observableType.parameterUpperBound!!
        isResult = true
      }
      else -> {
        responseType = observableType
        isBody = true
      }
    }

    return RxJava2CallAdapter<Any>(
      responseType, scheduler, isAsync, isResult, isBody, isFlowable, isSingle, isMaybe, false
    )
  }

  // endregion Public Methods

  // region Private Methods

  // endregion Private Methods
}
