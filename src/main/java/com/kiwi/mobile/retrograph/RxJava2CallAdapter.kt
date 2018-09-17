package com.kiwi.mobile.retrograph

import com.kiwi.mobile.retrograph.model.*
import com.kiwi.mobile.retrograph.rxjava.*

import io.reactivex.*
import io.reactivex.plugins.*

import retrofit2.*
import retrofit2.Response

import java.lang.reflect.*

internal class RxJava2CallAdapter<R>(
  private val responseType: Type,
  private val scheduler: Scheduler?,
  private val isAsync: Boolean,
  private val isResult: Boolean,
  private val isBody: Boolean,
  private val rxType: RxType
):
  CallAdapter<R, Any> {

  // region Public Methods

  override fun responseType() = responseType

  override fun adapt(call: Call<R>) =
    if (isAsync) {
      CallEnqueueObservable(call)
    } else {
      CallExecuteObservable(call)
    }
      .adapt()
      .applyScheduler()
      .toTarget()

  // endregion Public Methods

  // region Private Methods

  private fun Observable<Response<R>>.adapt() =
    when {
      isResult -> ResultObservable(this)
      isBody -> BodyObservable(this)
      else -> this
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
