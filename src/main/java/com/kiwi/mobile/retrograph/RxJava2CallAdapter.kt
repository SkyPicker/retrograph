package com.kiwi.mobile.retrograph

import com.kiwi.mobile.retrograph.rxjava.*

import io.reactivex.*
import io.reactivex.plugins.*

import retrofit2.*

import java.lang.reflect.*

import com.kiwi.mobile.retrograph.model.Response as GraphQLResponse

import retrofit2.Response as RetrofitResponse

internal class RxJava2CallAdapter<R>(
  private val responseType: Type,
  private val scheduler: Scheduler?,
  private val isAsync: Boolean,
  private val isResult: Boolean,
  private val isBody: Boolean,
  private val isFlowable: Boolean,
  private val isSingle: Boolean,
  private val isMaybe: Boolean,
  private val isCompletable: Boolean
):
  CallAdapter<R, Any> {

  // region Public Methods

  override fun responseType() = responseType

  override fun adapt(call: Call<R>): Any {
    val responseObservable = if (isAsync) {
      CallEnqueueObservable(call)
    } else {
      CallExecuteObservable(call)
    }

    var observable: Observable<*> = when {
      isResult -> ResultObservable(responseObservable)
      isBody -> BodyObservable(responseObservable)
      else -> responseObservable
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
