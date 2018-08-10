package com.kiwi.mobile.retrograph;

import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.*;

import android.support.annotation.*;

import com.google.gson.reflect.*;

import io.reactivex.*;

import retrofit2.*;
import retrofit2.CallAdapter.*;

import com.kiwi.mobile.retrograph.annotation.*;

/**
 * A {@linkplain CallAdapter.Factory call adapter} which uses RxJava 2 for creating observables.
 * <p>
 * Adding this class to {@link Retrofit} allows you to return an {@link Observable},
 * {@link Flowable}, {@link Single}, {@link Completable} or {@link Maybe} from service methods.
 * <pre><code>
 * interface MyService {
 *   &#64;GET("user/me")
 *   Observable&lt;User&gt; getUser()
 * }
 * </code></pre>
 * There are three configurations supported for the {@code Observable}, {@code Flowable},
 * {@code Single}, {@link Completable} and {@code Maybe} type parameter:
 * <ul>
 * <li>Direct body (e.g., {@code Observable<User>}) calls {@code onNext} with the deserialized body
 * for 2XX responses and calls {@code onError} with {@link HttpException} for non-2XX responses and
 * {@link IOException} for network errors.</li>
 * <li>Response wrapped body (e.g., {@code Observable<Response<User>>}) calls {@code onNext}
 * with a {@link Response} object for all HTTP responses and calls {@code onError} with
 * {@link IOException} for network errors</li>
 * <li>Result wrapped body (e.g., {@code Observable<Result<User>>}) calls {@code onNext} with a
 * {@link Result} object for all HTTP responses and errors.</li>
 * </ul>
 * <br />
 * Nowadays GraphQL API is supported. Use &#64;GraphQL annotation to the service method.
 * <pre><code>
 * interface MyService {
 *   &#64;GraphQL
 *   &#64;GET("user/me")
 *   Observable&lt;User&gt; getUser()
 * }
 * </code></pre>
 * In case of that (additionally to the old ones) three more configurations are supported:
 * <ul>
 * <li>{@code Observable<GraphQLResponse<User>>}</li>
 * <li>{@code Observable<Response<GraphQLResponse<User>>>}</li>
 * <li>{@code Observable<Result<GraphQLResponse<User>>>}</li>
 * </ul>
 */
public final class RxJava2CallAdapterFactory extends CallAdapter.Factory {

  /**
   * Returns an instance which creates synchronous observables that do not operate on any scheduler
   * by default.
   */
  public static RxJava2CallAdapterFactory create() {
    return new RxJava2CallAdapterFactory(null, false);
  }

  /**
   * Returns an instance which creates asynchronous observables. Applying
   * {@link Observable#subscribeOn} has no effect on stream types created by this factory.
   */
  public static RxJava2CallAdapterFactory createAsync() {
    return new RxJava2CallAdapterFactory(null, true);
  }

  /**
   * Returns an instance which creates synchronous observables that
   * {@linkplain Observable#subscribeOn(Scheduler) subscribe on} {@code scheduler} by default.
   */
  @SuppressWarnings("ConstantConditions") // Guarding public API nullability.
  public static RxJava2CallAdapterFactory createWithScheduler(Scheduler scheduler) {
    if (scheduler == null) {
      throw new NullPointerException("scheduler == null");
    }
    return new RxJava2CallAdapterFactory(scheduler, false);
  }

  @Nullable
  private final Scheduler scheduler;

  private final boolean isAsync;

  private RxJava2CallAdapterFactory(@Nullable Scheduler scheduler, boolean isAsync) {
    this.scheduler = scheduler;
    this.isAsync = isAsync;
  }

  @Override
  public CallAdapter<?, ?> get(
    @NonNull Type returnType,
    @NonNull Annotation[] annotations,
    @NonNull Retrofit retrofit
  ) {
    Class<?> rawType = getRawType(returnType);

    if (rawType == Completable.class) {
      // Completable is not parameterized (which is what the rest of this method deals with) so it
      // can only be created with a single configuration.
      return new RxJava2CallAdapter(
        Void.class, scheduler, isAsync, false, true, false, false, false, false, true, false
      );
    }

    boolean isFlowable = rawType == Flowable.class;
    boolean isSingle = rawType == Single.class;
    boolean isMaybe = rawType == Maybe.class;
    if ((rawType != Observable.class) && !isFlowable && !isSingle && !isMaybe) {
      return null;
    }

    boolean isResult = false;
    boolean isBody = false;
    boolean isGraphQLResponse = false;
    Type responseType;
    if (!(returnType instanceof ParameterizedType)) {
      String name = isFlowable ? "Flowable"
        : isSingle ? "Single"
        : isMaybe ? "Maybe" : "Observable";
      throw new IllegalStateException(
        name + " return type must be parameterized as " + name + "<Foo> or " + name
          + "<? extends Foo>"
      );
    }

    boolean isGraphQLRequest = false;
    /*for (Annotation annotation : annotations) {
      if (annotation.annotationType().equals(GraphQL.class)) {
        isGraphQLRequest = true;
      }
    }*/

    Type observableType = getParameterUpperBound(0, (ParameterizedType) returnType);
    Class<?> rawObservableType = getRawType(observableType);
    if (rawObservableType == Response.class) {
      if (!(observableType instanceof ParameterizedType)) {
        throw new IllegalStateException(
          "Response must be parameterized as Response<Foo> or Response<? extends Foo>"
        );
      }
      responseType = getParameterUpperBound(0, (ParameterizedType) observableType);

      /*if (responseType instanceof ParameterizedType) {
        Class<?> rawResponseType = getRawType(responseType);
        if (rawResponseType == GraphQLResponse.class) {
          isGraphQLResponse = true;
        }
      }*/
    } else if (rawObservableType == Result.class) {
      if (!(observableType instanceof ParameterizedType)) {
        throw new IllegalStateException(
          "Result must be parameterized as Result<Foo> or Result<? extends Foo>"
        );
      }
      responseType = getParameterUpperBound(0, (ParameterizedType) observableType);

      /*if (responseType instanceof ParameterizedType) {
        Class<?> rawResponseType = getRawType(responseType);
        if (rawResponseType == GraphQLResponse.class) {
          isGraphQLResponse = true;
        }
      }*/

      isResult = true;
    } else {
      /*if (rawObservableType == GraphQLResponse.class) {
        if (!(observableType instanceof ParameterizedType)) {
          throw new IllegalStateException(
            "GraphQLResponse must be parameterized as GraphQLResponse<Foo> or GraphQLResponse<? extends Foo>"
          );
        }
        isGraphQLResponse = true;
      }*/

      responseType = observableType;
      isBody = true;
    }

    /*if (isGraphQLRequest && !isGraphQLResponse) {
      responseType = TypeToken.getParameterized(GraphQLResponse.class, responseType).getType();
    }*/

    return new RxJava2CallAdapter(
      responseType, scheduler, isAsync, isResult, isBody, isGraphQLResponse, isFlowable, isSingle,
      isMaybe, false, isGraphQLRequest
    );
  }
}
