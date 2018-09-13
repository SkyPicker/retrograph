package com.kiwi.mobile.retrograph.rxjava

import io.reactivex.*
import io.reactivex.disposables.*
import io.reactivex.exceptions.*
import io.reactivex.plugins.*

import retrofit2.adapter.rxjava2.*

import com.kiwi.mobile.retrograph.model.Response as GraphQLResponse

import retrofit2.Response as RetrofitResponse

class GraphQLResultObservable<T>(
  private val upstream: Observable<RetrofitResponse<GraphQLResponse<T>>>
):
  Observable<Result<T>>() {

  // region Private Types

  private class GraphQLResultObserver<R>(
    private val observer: Observer<in Result<R>>
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
      sendError(throwable)
      sendComplete()
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
          observer.onNext(
            Result.response(RetrofitResponse.error(response.errorBody()!!, response.raw()))
          )
        else ->
          observer.onNext(Result.response(RetrofitResponse.success(body.data)))
      }
    }

    private fun sendError(throwable: Throwable) {
      try {
        observer.onNext(Result.error(throwable))
      } catch (nextThrowable: Throwable) {
        throwException(nextThrowable)
        return
      }
    }

    private fun sendComplete() {
      if (!terminated) {
        terminated = true
        observer.onComplete()
      }
    }

    private fun throwException(throwable: Throwable) {
      try {
        terminated = true
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

  override fun subscribeActual(observer: Observer<in Result<T>>) {
    upstream.subscribe(GraphQLResultObserver(observer))
  }

  // endregion Protected Methods
}