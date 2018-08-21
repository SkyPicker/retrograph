package com.kiwi.mobile.retrograph.rxjava

import io.reactivex.*
import io.reactivex.disposables.*
import io.reactivex.exceptions.*
import io.reactivex.plugins.*

import retrofit2.*

internal class CallEnqueueObservable<T>(
  private val originalCall: Call<T>
):
  Observable<Response<T>>() {

  // region Private Types

  private class CallCallback<T>(
    private val call: Call<*>,
    private val observer: Observer<in Response<T>>
  ):
    Disposable, Callback<T> {

    // region Private Properties

    @Volatile
    private var disposed: Boolean = false

    private var terminated = false

    // endregion Private Properties

    override fun onResponse(call: Call<T>, response: Response<T>) {
      if (disposed) {
        return
      }

      try {
        observer.onNext(response)

        if (!disposed) {
          terminated = true
          observer.onComplete()
        }
      } catch (throwable: Throwable) {
        if (terminated) {
          RxJavaPlugins.onError(throwable)
        } else if (!disposed) {
          try {
            observer.onError(throwable)
          } catch (inner: Throwable) {
            Exceptions.throwIfFatal(inner)
            RxJavaPlugins.onError(CompositeException(throwable, inner))
          }
        }
      }
    }

    override fun onFailure(call: Call<T>, throwable: Throwable) {
      if (call.isCanceled) {
        return
      }

      try {
        observer.onError(throwable)
      } catch (innerThrowable: Throwable) {
        Exceptions.throwIfFatal(innerThrowable)
        RxJavaPlugins.onError(CompositeException(throwable, innerThrowable))
      }
    }

    override fun dispose() {
      disposed = true
      call.cancel()
    }

    override fun isDisposed() = disposed
  }

  // endregion Private Types

  // region Protected Methods

  override fun subscribeActual(observer: Observer<in Response<T>>) {
    // Since Call is a one-shot type, clone it for each new observer.
    val call = originalCall.clone()
    val callback = CallCallback(call, observer)
    observer.onSubscribe(callback)
    call.enqueue(callback)
  }

  // endregion Protected Methods
}
