package com.kiwi.mobile.retrograph;

import java.lang.reflect.*;

import android.support.annotation.*;

import io.reactivex.*;
import io.reactivex.plugins.*;

import retrofit2.*;

final class RxJava2CallAdapter<R> implements CallAdapter<R, Object> {

  @Nullable
  private final Type responseType;
  private final Scheduler scheduler;
  private final boolean isAsync;
  private final boolean isResult;
  private final boolean isBody;
  private final boolean isGraphQLResponse;
  private final boolean isFlowable;
  private final boolean isSingle;
  private final boolean isMaybe;
  private final boolean isCompletable;
  private final boolean isGraphQLRequest;

  RxJava2CallAdapter(Type responseType, @Nullable Scheduler scheduler, boolean isAsync,
    boolean isResult, boolean isBody, boolean isGraphQLResponse, boolean isFlowable,
    boolean isSingle, boolean isMaybe, boolean isCompletable, boolean isGraphQLRequest) {
    this.responseType = responseType;
    this.scheduler = scheduler;
    this.isAsync = isAsync;
    this.isResult = isResult;
    this.isBody = isBody;
    this.isGraphQLResponse = isGraphQLResponse;
    this.isFlowable = isFlowable;
    this.isSingle = isSingle;
    this.isMaybe = isMaybe;
    this.isCompletable = isCompletable;
    this.isGraphQLRequest = isGraphQLRequest;
  }

  @Override
  public Type responseType() {
    return responseType;
  }

  @Override
  public Object adapt(Call<R> call) {

    Observable<?> observable;

    // In case when GraphQL API is called (so response contain GraphQLResponse) and user want to
    // obtain response without GraphQLResponse.
    if (isGraphQLRequest && !isGraphQLResponse) {
      Call<GraphQLResponse<R>> graphQLCall = (Call<GraphQLResponse<R>>) call;

      Observable<Response<GraphQLResponse<R>>> responseObservable = isAsync
        ? new CallEnqueueObservable<>(graphQLCall)
        : new CallExecuteObservable<>(graphQLCall);

      if (isResult) {
        observable = new GraphQLResultObservable<>(responseObservable);
      } else if (isBody) {
        observable = new GraphQLBodyObservable<>(responseObservable);
      } else {
        observable = new GraphQLResponseObservable<>(responseObservable);
      }
    } else {
      Observable<Response<R>> responseObservable = isAsync
        ? new CallEnqueueObservable<>(call)
        : new CallExecuteObservable<>(call);

      if (isResult) {
        observable = new ResultObservable<>(responseObservable);
      } else if (isBody) {
        observable = new BodyObservable<>(responseObservable);
      } else {
        observable = responseObservable;
      }
    }

    if (scheduler != null) {
      observable = observable.subscribeOn(scheduler);
    }

    if (isFlowable) {
      return observable.toFlowable(BackpressureStrategy.LATEST);
    }
    if (isSingle) {
      return observable.singleOrError();
    }
    if (isMaybe) {
      return observable.singleElement();
    }
    if (isCompletable) {
      return observable.ignoreElements();
    }
    return RxJavaPlugins.onAssembly(observable);
  }
}
