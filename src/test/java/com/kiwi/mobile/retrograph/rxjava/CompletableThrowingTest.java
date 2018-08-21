package com.kiwi.mobile.retrograph.rxjava;

import java.util.concurrent.atomic.*;

import org.junit.*;
import org.junit.rules.*;

import io.reactivex.*;
import io.reactivex.exceptions.*;
import io.reactivex.plugins.*;

import retrofit2.*;
import retrofit2.http.*;

import com.kiwi.mobile.retrograph.*;
import com.kiwi.mobile.retrograph.rule.*;
import com.kiwi.mobile.retrograph.util.*;
import okhttp3.mockwebserver.*;

import static org.assertj.core.api.Assertions.*;

public final class CompletableThrowingTest {

  @Rule
  public final MockWebServer server = new MockWebServer();

  @Rule
  public final TestRule resetRule = new RxJavaPluginsResetRule();

  @Rule
  public final RecordingCompletableObserver.Rule observerRule =
    new RecordingCompletableObserver.Rule();

  interface Service {

    @GET("/")
    Completable completable();
  }

  private Service service;

  @Before
  public void setUp() {
    Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(server.url("/"))
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .build();

    service = retrofit.create(Service.class);
  }

  @Test
  public void throwingInOnCompleteDeliveredToPlugin() {
    server.enqueue(new MockResponse());

    AtomicReference<Throwable> errorReference = new AtomicReference<>();
    RxJavaPlugins.setErrorHandler(throwable -> {
      if (!errorReference.compareAndSet(null, throwable)) {
        throw Exceptions.propagate(throwable); // Don't swallow secondary errors!
      }
    });

    RecordingCompletableObserver observer = observerRule.create();
    RuntimeException exception = new RuntimeException();
    service.completable()
      .subscribe(new ForwardingCompletableObserver(observer) {
        @Override
        public void onComplete() {
          throw exception;
        }
      });

    assertThat(errorReference.get())
      .isInstanceOf(UndeliverableException.class);

    assertThat(errorReference.get().getCause())
      .isSameAs(exception);
  }

  @Test
  public void bodyThrowingInOnErrorDeliveredToPlugin() {
    server.enqueue(
      new MockResponse()
        .setResponseCode(404)
    );

    AtomicReference<Throwable> pluginReference = new AtomicReference<>();
    RxJavaPlugins.setErrorHandler(throwable -> {
      if (!pluginReference.compareAndSet(null, throwable)) {
        throw Exceptions.propagate(throwable); // Don't swallow secondary errors!
      }
    });

    RecordingCompletableObserver observer = observerRule.create();
    RuntimeException exception = new RuntimeException();
    AtomicReference<Throwable> errorReference = new AtomicReference<>();

    service.completable()
      .subscribe(new ForwardingCompletableObserver(observer) {
        @Override
        public void onError(Throwable throwable) {
          errorReference.set(throwable);
          throw exception;
        }
      });

    CompositeException composite = (CompositeException) pluginReference.get();

    assertThat(composite.getExceptions())
      .containsExactly(errorReference.get(), exception);
  }
}
