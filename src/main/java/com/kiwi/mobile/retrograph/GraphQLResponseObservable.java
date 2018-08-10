package com.kiwi.mobile.retrograph;

import io.reactivex.*;
import io.reactivex.disposables.*;
import io.reactivex.exceptions.*;
import io.reactivex.plugins.*;

import retrofit2.*;

import com.kiwi.mobile.retrograph.exception.*;

public class GraphQLResponseObservable<T> extends Observable<Response<T>> {
  private final Observable<Response<GraphQLResponse<T>>> upstream;

  GraphQLResponseObservable(Observable<Response<GraphQLResponse<T>>> upstream) {
    this.upstream = upstream;
  }

  @Override
  protected void subscribeActual(Observer<? super Response<T>> observer) {
    upstream.subscribe(new GraphQLResponseObserver<T>(observer));
  }

  private static class GraphQLResponseObserver<R>
    implements Observer<Response<GraphQLResponse<R>>> {
    private final Observer<? super Response<R>> observer;

    GraphQLResponseObserver(Observer<? super Response<R>> observer) {
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
          throwException(new HttpException(response));
        } else if (response.body().getData() == null) {
          throwException(new GraphQLException(response.body()));
        } else {
          observer.onNext(Response.success(response.body().getData().getBody(), response.raw()));
        }
      } else {
        observer.onNext(Response.<R>error(response.errorBody(), response.raw()));
      }
    }

    @Override
    public void onError(Throwable throwable) {
      throwException(throwable);
    }

    @Override
    public void onComplete() {
      observer.onComplete();
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
