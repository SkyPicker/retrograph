package com.kiwi.mobile.retrograph.rxjava

import io.reactivex.*
import io.reactivex.disposables.*
import io.reactivex.exceptions.*
import io.reactivex.plugins.*

import retrofit2.*

import com.kiwi.mobile.retrograph.model.Response as GraphQLResponse

import retrofit2.Response as RetrofitResponse

internal class GraphQLCallExecuteObservable<R>(
  private val originalCall: Call<R>
):
  Observable<RetrofitResponse<R>>() {

  // region Private Types

  private class CallDisposable<S>(
    private val call: Call<S>
  ):
    Disposable {

    // region Private Properties

    @Volatile
    private var disposed: Boolean = false

    // endregion Private Properties

    override fun dispose() {
      disposed = true
      call.cancel()
    }

    override fun isDisposed(): Boolean {
      return disposed
    }
  }

  // endregion Private Types

  // region Protected Methods

  override fun subscribeActual(observer: Observer<in RetrofitResponse<R>>) {
    // Since Call is a one-shot type, clone it for each new observer.
    val call = originalCall.clone()
    val disposable = CallDisposable(call)
    observer.onSubscribe(disposable)

    var terminated = false
    try {
      val response = call.execute()
      if (!disposable.isDisposed) {
        observer.onNext(response)
      }
      if (!disposable.isDisposed) {
        terminated = true
        observer.onComplete()
      }
    } catch (throwable: Throwable) {
      Exceptions.throwIfFatal(throwable)
      if (terminated) {
        RxJavaPlugins.onError(throwable)
      } else if (!disposable.isDisposed) {
        try {
          observer.onError(throwable)
        } catch (inner: Throwable) {
          Exceptions.throwIfFatal(inner)
          RxJavaPlugins.onError(CompositeException(throwable, inner))
        }
      } else {
        // nothing
      }
    }
  }

  // endregion Protected Methods
}
