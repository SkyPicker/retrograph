package com.kiwi.mobile.retrograph.rxjava

import io.reactivex.*
import io.reactivex.disposables.*
import io.reactivex.exceptions.*
import io.reactivex.plugins.*

import com.kiwi.mobile.retrograph.model.Response as GraphQLResponse

import retrofit2.Response as RetrofitResponse

class GraphQLRetrofitResponseObservable<T>(
  private val upstream: Observable<RetrofitResponse<GraphQLResponse<T>>>
):
  Observable<RetrofitResponse<T>>() {

  // region Private Types

  private class GraphQLResponseObserver<R>(
    private val observer: Observer<in RetrofitResponse<R>>
  ):
    Observer<RetrofitResponse<GraphQLResponse<R>>> {

    // region Private Properties

    private var terminated: Boolean = false

    // endregion Private Properties

    // region Public Methods

    override fun onSubscribe(disposable: Disposable) {
      sendSubscribe(disposable)
    }

    override fun onNext(response: RetrofitResponse<GraphQLResponse<R>>) {
      sendResponse(response)
    }

    override fun onError(throwable: Throwable) {
      if (!terminated) {
        throwException(throwable)
      } else {
        // This should never happen! onNext handles and forwards errors automatically.
        val broken = AssertionError(
          "This should never happen! Report as a bug with the full stacktrace."
        )

        broken.initCause(throwable)
        RxJavaPlugins.onError(broken)
      }
    }

    override fun onComplete() {
      sendComplete()
    }

    // endregion Public Methods

    // region Private Methods

    private fun sendSubscribe(disposable: Disposable) {
      observer.onSubscribe(disposable)
    }

    private fun sendResponse(response: RetrofitResponse<GraphQLResponse<R>>) {
      val body = response.body()
      when {
        body?.data == null ->
          observer.onNext(RetrofitResponse.error(response.errorBody()!!, response.raw()))
        else ->
          observer.onNext(RetrofitResponse.success(body.data))
      }
    }

    private fun sendComplete() {
      if (!terminated) {
        terminated = true
        observer.onComplete()
      }
    }

    private fun throwException(throwable: Throwable) {
      terminated = true
      try {
        observer.onError(throwable)
      } catch (innerThrowable: Throwable) {
        Exceptions.throwIfFatal(innerThrowable)
        RxJavaPlugins.onError(CompositeException(throwable, innerThrowable))
      }
    }

    // endregion Private Methods
  }

  // endregion Private Types

  // region Protected Methods

  override fun subscribeActual(observer: Observer<in RetrofitResponse<T>>) {
    upstream.subscribe(GraphQLResponseObserver(observer))
  }

  // endregion Protected Methods
}
