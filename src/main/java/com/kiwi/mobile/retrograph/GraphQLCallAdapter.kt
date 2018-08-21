package com.kiwi.mobile.retrograph

import com.kiwi.mobile.retrograph.rxjava.*

import io.reactivex.*
import io.reactivex.plugins.*

import retrofit2.*

import java.lang.reflect.*

import com.kiwi.mobile.retrograph.model.Response as GraphQLResponse

import retrofit2.Response as RetrofitResponse

typealias GraphQLObservable = Observable<RetrofitResponse<GraphQLResponse<Any>>>

internal class GraphQLCallAdapter<R: GraphQLResponse<T>, T>(
  private val responseType: Type?,
  private val scheduler: Scheduler?,
  private val isResult: Boolean,
  private val isBody: Boolean,
  private val isGraphQLResponse: Boolean,
  private val isFlowable: Boolean,
  private val isSingle: Boolean,
  private val isMaybe: Boolean,
  private val isCompletable: Boolean
):
  CallAdapter<R, Any> {

  // region Public Methods

  override fun responseType() = responseType

  override fun adapt(call: Call<R>): Any {
    val responseObservable = GraphQLCallExecuteObservable(call)

    var observable: Observable<*> = when {
      isGraphQLResponse && isBody -> BodyObservable(responseObservable)
      isGraphQLResponse && isResult -> ResultObservable(responseObservable)
      isGraphQLResponse -> responseObservable
      isBody -> GraphQLBodyObservable(responseObservable as GraphQLObservable)
      isResult -> GraphQLResultObservable(responseObservable as GraphQLObservable)
      else -> GraphQLRetrofitResponseObservable(responseObservable as GraphQLObservable)
    }

    if (scheduler != null) {
      observable = observable.subscribeOn(scheduler)
    }

    return when {
      isFlowable -> observable.toFlowable(BackpressureStrategy.LATEST)
      isSingle -> observable.singleOrError()
      isMaybe -> observable.singleElement()
      isCompletable -> observable.ignoreElements()
      else -> RxJavaPlugins.onAssembly(observable)
    }
  }

  // endregion Public Methods
}
