package com.kiwi.mobile.retrograph.rxjava

import io.reactivex.*
import io.reactivex.disposables.*
import io.reactivex.exceptions.*
import io.reactivex.plugins.*

import retrofit2.*

class BodyObservable<T>(
  private val upstream: Observable<Response<T>>
):
  Observable<T>() {

  // region Private Types

  private class BodyObserver<R>(
    private val observer: Observer<in R>
  ):
    Observer<Response<R>> {

    // region Private Properties

    private var terminated: Boolean = false

    // endregion Private Properties

    // region Public Methods

    override fun onSubscribe(disposable: Disposable) {
      sendSubscribe(disposable)
    }

    override fun onNext(response: Response<R>) {
      if (!response.isSuccessful) {
        throwException(HttpException(response))
        return
      }

      sendResponse(response)
    }

    override fun onComplete() {
      sendComplete()
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

    // endregion Public Methods

    // region Private Methods

    private fun sendSubscribe(disposable: Disposable) {
      observer.onSubscribe(disposable)
    }

    private fun sendResponse(response: Response<R>) {
      val body = response.body()
      when (body) {
        null -> {
          throwException(HttpException(response))
        }
        else -> {
          observer.onNext(body)
        }
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

  override fun subscribeActual(observer: Observer<in T>) {
    upstream.subscribe(BodyObserver(observer))
  }

  // endregion Protected Methods
}
