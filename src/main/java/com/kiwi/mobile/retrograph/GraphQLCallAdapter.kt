package com.kiwi.mobile.retrograph

import com.kiwi.mobile.retrograph.model.*
import com.kiwi.mobile.retrograph.rxjava.*

import io.reactivex.*
import io.reactivex.plugins.*

import retrofit2.*

import java.lang.reflect.*

import com.kiwi.mobile.retrograph.model.Response as GraphQLResponse

import retrofit2.Response as RetrofitResponse

typealias GraphQLObservable = Observable<RetrofitResponse<GraphQLResponse<Any>>>

@Suppress("UNCHECKED_CAST")
internal class GraphQLCallAdapter<R>(
  private val responseType: Type,
  private val scheduler: Scheduler?,
  private val isResult: Boolean,
  private val isBody: Boolean,
  private val isGraphQLResponse: Boolean,
  private val rxType: RxType
):
  CallAdapter<R, Any> {

  // region Public Methods

  override fun responseType() = responseType

  override fun adapt(call: Call<R>) =
    GraphQLCallExecuteObservable(call)
      .adapt()
      .applyScheduler()
      .toTarget()

  // endregion Public Methods

  // region Private Methods

  @SuppressWarnings("unchecked")
  private fun Observable<RetrofitResponse<R>>.adapt() =
    when {
      isGraphQLResponse && isBody -> BodyObservable(this)
      isGraphQLResponse && isResult -> ResultObservable(this)
      isGraphQLResponse -> this
      isBody -> GraphQLBodyObservable(this as GraphQLObservable)
      isResult -> GraphQLResultObservable(this as GraphQLObservable)
      else -> GraphQLRetrofitResponseObservable(this as GraphQLObservable)
    }

  private fun Observable<*>.applyScheduler() =
    if (scheduler != null) {
      subscribeOn(scheduler)
    } else {
      this
    }

  private fun Observable<*>.toTarget(): Any =
    when (rxType) {
      RxType.FLOWABLE -> toFlowable(BackpressureStrategy.LATEST)
      RxType.SINGLE -> singleOrError()
      RxType.MAYBE -> singleElement()
      RxType.COMPLETABLE -> ignoreElements()
      else -> RxJavaPlugins.onAssembly(this)
    }

  // endregion Private Methods
}
