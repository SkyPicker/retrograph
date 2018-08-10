package com.kiwi.mobile.retrograph;

import io.reactivex.*;
import io.reactivex.disposables.*;
import io.reactivex.exceptions.*;
import io.reactivex.plugins.*;

import retrofit2.*;

import com.kiwi.mobile.retrograph.exception.*;

final class GraphQLBodyObservable<T> extends Observable<T> {
  private final Observable<Response<GraphQLResponse<T>>> upstream;

  GraphQLBodyObservable(Observable<Response<GraphQLResponse<T>>> upstream) {
    this.upstream = upstream;
  }

  @Override
  protected void subscribeActual(Observer<? super T> observer) {
    upstream.subscribe(new GraphQLBodyObserver<T>(observer));
  }

  private static class GraphQLBodyObserver<R> implements Observer<Response<GraphQLResponse<R>>> {
    private final Observer<? super R> observer;
    private boolean terminated;

    GraphQLBodyObserver(Observer<? super R> observer) {
      this.observer = observer;
    }

    @Override
    public void onSubscribe(Disposable disposable) {
      observer.onSubscribe(disposable);
    }

    @Override
    public void onNext(Response<GraphQLResponse<R>> response) {
      if (response.isSuccessful()) {
        if (response.body() == null) {
          terminated = true;
          throwException(new HttpException(response));
        } else if (response.body().getData() == null) {
          terminated = true;
          throwException(new GraphQLException(response.body()));
        } else {
          observer.onNext(response.body().getData().getBody());
        }
      } else {
        terminated = true;
        throwException(new HttpException(response));
      }
    }

    @Override
    public void onComplete() {
      if (!terminated) {
        observer.onComplete();
      }
    }

    @Override
    public void onError(Throwable throwable) {
      if (!terminated) {
        observer.onError(throwable);
      } else {
        // This should never happen! onNext handles and forwards errors automatically.
        Throwable broken = new AssertionError(
          "This should never happen! Report as a bug with the full stacktrace.");
        //noinspection UnnecessaryInitCause Two-arg AssertionError constructor is 1.7+ only.
        broken.initCause(throwable);
        RxJavaPlugins.onError(broken);
      }
    }

    private void throwException(Throwable throwable) {
      try {
        observer.onError(throwable);
      } catch (Throwable inner) {
        Exceptions.throwIfFatal(inner);
        RxJavaPlugins.onError(new CompositeException(throwable, inner));
      }
    }
  }
}
