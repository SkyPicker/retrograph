package com.kiwi.mobile.retrograph;

import java.util.concurrent.atomic.*;

import org.junit.*;
import org.junit.rules.*;

import io.reactivex.*;
import io.reactivex.disposables.*;
import io.reactivex.exceptions.*;
import io.reactivex.functions.*;
import io.reactivex.plugins.*;

import retrofit2.*;
import retrofit2.http.*;

import okhttp3.mockwebserver.*;

import static okhttp3.mockwebserver.SocketPolicy.*;
import static org.assertj.core.api.Assertions.*;

public final class SingleThrowingTest {
  @Rule
  public final MockWebServer server = new MockWebServer();
  @Rule
  public final TestRule resetRule = new RxJavaPluginsResetRule();
  @Rule
  public final RecordingSingleObserver.Rule subscriberRule = new RecordingSingleObserver.Rule();

  interface Service {
    @GET("/")
    Single<String> body();

    @GET("/")
    Single<Response<String>> response();

    @GET("/")
    Single<Result<String>> result();
  }

  private Service service;

  @Before
  public void setUp() {
    Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(server.url("/"))
      .addConverterFactory(new StringConverterFactory())
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .build();
    service = retrofit.create(Service.class);
  }

  @Test
  public void bodyThrowingInOnSuccessDeliveredToPlugin() {
    server.enqueue(new MockResponse());

    final AtomicReference<Throwable> throwableRef = new AtomicReference<>();
    RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
      @Override
      public void accept(Throwable throwable) throws Exception {
        if (!throwableRef.compareAndSet(null, throwable)) {
          throw Exceptions.propagate(throwable);
        }
      }
    });

    RecordingSingleObserver<String> observer = subscriberRule.create();
    final RuntimeException e = new RuntimeException();
    service.body().subscribe(new ForwardingObserver<String>(observer) {
      @Override
      public void onSuccess(String value) {
        throw e;
      }
    });

    assertThat(throwableRef.get())
      .isInstanceOf(UndeliverableException.class);

    assertThat(throwableRef.get().getCause())
      .isSameAs(e);
  }

  @Test
  public void bodyThrowingInOnErrorDeliveredToPlugin() {
    server.enqueue(new MockResponse().setResponseCode(404));

    final AtomicReference<Throwable> throwableRef = new AtomicReference<>();
    RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
      @Override
      public void accept(Throwable throwable) throws Exception {
        if (!throwableRef.compareAndSet(null, throwable)) {
          throw Exceptions.propagate(throwable);
        }
      }
    });

    RecordingSingleObserver<String> observer = subscriberRule.create();
    final AtomicReference<Throwable> errorRef = new AtomicReference<>();
    final RuntimeException e = new RuntimeException();
    service.body().subscribe(new ForwardingObserver<String>(observer) {
      @Override
      public void onError(Throwable throwable) {
        if (!errorRef.compareAndSet(null, throwable)) {
          throw Exceptions.propagate(throwable);
        }
        throw e;
      }
    });

    //noinspection ThrowableResultOfMethodCallIgnored
    CompositeException composite = (CompositeException) throwableRef.get();
    assertThat(composite.getExceptions()).containsExactly(errorRef.get(), e);
  }

  @Test
  public void responseThrowingInOnSuccessDeliveredToPlugin() {
    server.enqueue(new MockResponse());

    final AtomicReference<Throwable> throwableRef = new AtomicReference<>();
    RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
      @Override
      public void accept(Throwable throwable) throws Exception {
        if (!throwableRef.compareAndSet(null, throwable)) {
          throw Exceptions.propagate(throwable);
        }
      }
    });

    RecordingSingleObserver<Response<String>> observer = subscriberRule.create();
    final RuntimeException e = new RuntimeException();
    service.response().subscribe(new ForwardingObserver<Response<String>>(observer) {
      @Override
      public void onSuccess(Response<String> value) {
        throw e;
      }
    });

    assertThat(throwableRef.get())
      .isInstanceOf(UndeliverableException.class);

    assertThat(throwableRef.get().getCause())
      .isSameAs(e);
  }

  @Test
  public void responseThrowingInOnErrorDeliveredToPlugin() {
    server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AFTER_REQUEST));

    final AtomicReference<Throwable> throwableRef = new AtomicReference<>();
    RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
      @Override
      public void accept(Throwable throwable) throws Exception {
        if (!throwableRef.compareAndSet(null, throwable)) {
          throw Exceptions.propagate(throwable);
        }
      }
    });

    RecordingSingleObserver<Response<String>> observer = subscriberRule.create();
    final AtomicReference<Throwable> errorRef = new AtomicReference<>();
    final RuntimeException e = new RuntimeException();
    service.response().subscribe(new ForwardingObserver<Response<String>>(observer) {
      @Override
      public void onError(Throwable throwable) {
        if (!errorRef.compareAndSet(null, throwable)) {
          throw Exceptions.propagate(throwable);
        }
        throw e;
      }
    });

    //noinspection ThrowableResultOfMethodCallIgnored
    CompositeException composite = (CompositeException) throwableRef.get();
    assertThat(composite.getExceptions()).containsExactly(errorRef.get(), e);
  }

  @Test
  public void resultThrowingInOnSuccessDeliveredToPlugin() {
    server.enqueue(new MockResponse());

    final AtomicReference<Throwable> throwableRef = new AtomicReference<>();
    RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
      @Override
      public void accept(Throwable throwable) throws Exception {
        if (!throwableRef.compareAndSet(null, throwable)) {
          throw Exceptions.propagate(throwable);
        }
      }
    });

    RecordingSingleObserver<Result<String>> observer = subscriberRule.create();
    final RuntimeException e = new RuntimeException();
    service.result().subscribe(new ForwardingObserver<Result<String>>(observer) {
      @Override
      public void onSuccess(Result<String> value) {
        throw e;
      }
    });

    assertThat(throwableRef.get())
      .isInstanceOf(UndeliverableException.class);

    assertThat(throwableRef.get().getCause())
      .isSameAs(e);
  }

  @Ignore("Single's contract is onNext|onError so we have no way of triggering this case")
  @Test
  public void resultThrowingInOnErrorDeliveredToPlugin() {
    server.enqueue(new MockResponse());

    final AtomicReference<Throwable> throwableRef = new AtomicReference<>();
    RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
      @Override
      public void accept(Throwable throwable) throws Exception {
        if (!throwableRef.compareAndSet(null, throwable)) {
          throw Exceptions.propagate(throwable);
        }
      }
    });

    RecordingSingleObserver<Result<String>> observer = subscriberRule.create();
    final RuntimeException first = new RuntimeException();
    final RuntimeException second = new RuntimeException();
    service.result().subscribe(new ForwardingObserver<Result<String>>(observer) {
      @Override
      public void onSuccess(Result<String> value) {
        // The only way to trigger onError for Result is if onSuccess throws.
        throw first;
      }

      @Override
      public void onError(Throwable throwable) {
        throw second;
      }
    });

    //noinspection ThrowableResultOfMethodCallIgnored
    CompositeException composite = (CompositeException) throwableRef.get();
    assertThat(composite.getExceptions()).containsExactly(first, second);
  }

  private static abstract class ForwardingObserver<T> implements SingleObserver<T> {
    private final SingleObserver<T> delegate;

    ForwardingObserver(SingleObserver<T> delegate) {
      this.delegate = delegate;
    }

    @Override
    public void onSubscribe(Disposable disposable) {
      delegate.onSubscribe(disposable);
    }

    @Override
    public void onSuccess(T value) {
      delegate.onSuccess(value);
    }

    @Override
    public void onError(Throwable throwable) {
      delegate.onError(throwable);
    }
  }
}
