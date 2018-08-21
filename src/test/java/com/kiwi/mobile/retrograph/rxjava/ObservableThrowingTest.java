package com.kiwi.mobile.retrograph.rxjava;

import java.util.concurrent.atomic.*;

import org.junit.*;
import org.junit.rules.*;

import io.reactivex.*;
import io.reactivex.disposables.*;
import io.reactivex.exceptions.*;
import io.reactivex.plugins.*;

import retrofit2.*;
import retrofit2.adapter.rxjava2.*;
import retrofit2.http.*;

import com.kiwi.mobile.retrograph.RxJava2CallAdapterFactory;
import com.kiwi.mobile.retrograph.converter.*;
import com.kiwi.mobile.retrograph.rule.*;
import com.kiwi.mobile.retrograph.util.*;
import okhttp3.mockwebserver.*;

import static okhttp3.mockwebserver.SocketPolicy.*;
import static org.assertj.core.api.Assertions.*;

public final class ObservableThrowingTest {
  @Rule
  public final MockWebServer server = new MockWebServer();
  @Rule
  public final TestRule resetRule = new RxJavaPluginsResetRule();
  @Rule
  public final RecordingObserver.Rule subscriberRule = new RecordingObserver.Rule();

  interface Service {
    @GET("/")
    Observable<String> body();

    @GET("/")
    Observable<Response<String>> response();

    @GET("/")
    Observable<Result<String>> result();
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
  public void bodyThrowingInOnNextDeliveredToError() {
    server.enqueue(new MockResponse());

    RecordingObserver<String> observer = subscriberRule.create();
    final RuntimeException e = new RuntimeException();
    service.body().subscribe(new ForwardingObserver<String>(observer) {
      @Override
      public void onNext(String value) {
        throw e;
      }
    });

    observer.assertError(e);
  }

  @Test
  public void bodyThrowingInOnCompleteDeliveredToPlugin() {
    server.enqueue(new MockResponse());

    final AtomicReference<Throwable> throwableRef = new AtomicReference<>();
    RxJavaPlugins.setErrorHandler(throwable -> {
      if (!throwableRef.compareAndSet(null, throwable)) {
        throw Exceptions.propagate(throwable);
      }
    });

    RecordingObserver<String> observer = subscriberRule.create();
    final RuntimeException e = new RuntimeException();
    service.body().subscribe(new ForwardingObserver<String>(observer) {
      @Override
      public void onComplete() {
        throw e;
      }
    });

    observer.assertAnyValue();

    assertThat(throwableRef.get())
      .isInstanceOf(UndeliverableException.class);

    assertThat(throwableRef.get().getCause())
      .isSameAs(e);
  }

  @Test
  public void bodyThrowingInOnErrorDeliveredToPlugin() {
    server.enqueue(new MockResponse().setResponseCode(404));

    final AtomicReference<Throwable> throwableRef = new AtomicReference<>();
    RxJavaPlugins.setErrorHandler(throwable -> {
      if (!throwableRef.compareAndSet(null, throwable)) {
        throw Exceptions.propagate(throwable);
      }
    });

    RecordingObserver<String> observer = subscriberRule.create();
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
  public void responseThrowingInOnNextDeliveredToError() {
    server.enqueue(new MockResponse());

    RecordingObserver<Response<String>> observer = subscriberRule.create();
    final RuntimeException e = new RuntimeException();
    service.response().subscribe(new ForwardingObserver<Response<String>>(observer) {
      @Override
      public void onNext(Response<String> value) {
        throw e;
      }
    });

    observer.assertError(e);
  }

  @Test
  public void responseThrowingInOnCompleteDeliveredToPlugin() {
    server.enqueue(new MockResponse());

    final AtomicReference<Throwable> throwableRef = new AtomicReference<>();
    RxJavaPlugins.setErrorHandler(throwable -> {
      if (!throwableRef.compareAndSet(null, throwable)) {
        throw Exceptions.propagate(throwable);
      }
    });

    RecordingObserver<Response<String>> observer = subscriberRule.create();
    final RuntimeException e = new RuntimeException();
    service.response().subscribe(new ForwardingObserver<Response<String>>(observer) {
      @Override
      public void onComplete() {
        throw e;
      }
    });

    observer.assertAnyValue();

    assertThat(throwableRef.get())
      .isInstanceOf(UndeliverableException.class);

    assertThat(throwableRef.get().getCause())
      .isSameAs(e);
  }

  @Test
  public void responseThrowingInOnErrorDeliveredToPlugin() {
    server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AFTER_REQUEST));

    final AtomicReference<Throwable> throwableRef = new AtomicReference<>();
    RxJavaPlugins.setErrorHandler(throwable -> {
      if (!throwableRef.compareAndSet(null, throwable)) {
        throw Exceptions.propagate(throwable);
      }
    });

    RecordingObserver<Response<String>> observer = subscriberRule.create();
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
  public void resultThrowingInOnNextDeliveredToError() {
    server.enqueue(new MockResponse());

    RecordingObserver<Result<String>> observer = subscriberRule.create();
    final RuntimeException e = new RuntimeException();
    service.result().subscribe(new ForwardingObserver<Result<String>>(observer) {
      @Override
      public void onNext(Result<String> value) {
        throw e;
      }
    });

    observer.assertError(e);
  }

  @Test
  public void resultThrowingInOnCompletedDeliveredToPlugin() {
    server.enqueue(new MockResponse());

    final AtomicReference<Throwable> throwableRef = new AtomicReference<>();
    RxJavaPlugins.setErrorHandler(throwable -> {
      if (!throwableRef.compareAndSet(null, throwable)) {
        throw Exceptions.propagate(throwable);
      }
    });

    RecordingObserver<Result<String>> observer = subscriberRule.create();
    final RuntimeException e = new RuntimeException();
    service.result().subscribe(new ForwardingObserver<Result<String>>(observer) {
      @Override
      public void onComplete() {
        throw e;
      }
    });

    observer.assertAnyValue();

    assertThat(throwableRef.get())
      .isInstanceOf(UndeliverableException.class);

    assertThat(throwableRef.get().getCause())
      .isSameAs(e);
  }

  @Test
  public void resultThrowingInOnErrorDeliveredToPlugin() {
    server.enqueue(new MockResponse());

    final AtomicReference<Throwable> throwableRef = new AtomicReference<>();
    RxJavaPlugins.setErrorHandler(throwable -> {
      if (!throwableRef.compareAndSet(null, throwable)) {
        throw Exceptions.propagate(throwable);
      }
    });

    RecordingObserver<Result<String>> observer = subscriberRule.create();
    final RuntimeException first = new RuntimeException();
    final RuntimeException second = new RuntimeException();
    service.result().subscribe(new ForwardingObserver<Result<String>>(observer) {
      @Override
      public void onNext(Result<String> value) {
        // The only way to trigger onError for a result is if onNext throws.
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

  private static abstract class ForwardingObserver<T> implements Observer<T> {
    private final Observer<T> delegate;

    ForwardingObserver(Observer<T> delegate) {
      this.delegate = delegate;
    }

    @Override
    public void onSubscribe(Disposable disposable) {
      delegate.onSubscribe(disposable);
    }

    @Override
    public void onNext(T value) {
      delegate.onNext(value);
    }

    @Override
    public void onError(Throwable throwable) {
      delegate.onError(throwable);
    }

    @Override
    public void onComplete() {
      delegate.onComplete();
    }
  }
}
