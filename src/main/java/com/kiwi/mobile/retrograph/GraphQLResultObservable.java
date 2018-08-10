package com.kiwi.mobile.retrograph;

import io.reactivex.*;
import io.reactivex.disposables.*;
import io.reactivex.exceptions.*;
import io.reactivex.plugins.*;

import retrofit2.*;

import com.kiwi.mobile.retrograph.exception.*;

public class GraphQLResultObservable<T> extends Observable<Result<T>> {
  private final Observable<Response<GraphQLResponse<T>>> upstream;

  GraphQLResultObservable(Observable<Response<GraphQLResponse<T>>> upstream) {
    this.upstream = upstream;
  }

  @Override
  protected void subscribeActual(Observer<? super Result<T>> observer) {
    upstream.subscribe(new GraphQLResultObserver<T>(observer));
  }

  private static class GraphQLResultObserver<R> implements Observer<Response<GraphQLResponse<R>>> {
    private final Observer<? super Result<R>> observer;

    GraphQLResultObserver(Observer<? super Result<R>> observer) {
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
          Response<R> resultResponse =
            Response.success(response.body().getData().getBody(), response.raw());
          observer.onNext(Result.response(resultResponse));
        }
      } else {
        Response<R> resultResponse = Response.error(response.errorBody(), response.raw());
        observer.onNext(Result.response(resultResponse));
      }
    }

    @Override
    public void onError(Throwable throwable) {
      try {
        observer.onNext(Result.<R>error(throwable));
      } catch (Throwable t) {
        throwException(t);
        return;
      }
      observer.onComplete();
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