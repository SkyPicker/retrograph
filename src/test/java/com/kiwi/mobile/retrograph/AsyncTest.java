package com.kiwi.mobile.retrograph;

import java.io.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import org.junit.*;

import io.reactivex.*;
import io.reactivex.exceptions.*;
import io.reactivex.functions.*;
import io.reactivex.observers.*;
import io.reactivex.plugins.*;

import retrofit2.*;
import retrofit2.http.*;

import com.kiwi.mobile.retrograph.CompletableThrowingTest.*;
import okhttp3.mockwebserver.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public final class AsyncTest {

  @Rule
  public final MockWebServer server = new MockWebServer();

  interface Service {
    @GET("/")
    Completable completable();
  }

  private Service service;

  @Before
  public void setUp() {
    Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(server.url("/"))
      .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
      .build();
    service = retrofit.create(Service.class);
  }

  @Test
  public void success() throws InterruptedException {
    TestObserver<Void> observer = new TestObserver<>();
    service.completable().subscribe(observer);
    assertFalse(observer.await(1, TimeUnit.SECONDS));

    server.enqueue(new MockResponse());
    observer.awaitTerminalEvent(1, TimeUnit.SECONDS);
    observer.assertComplete();
  }

  @Test
  public void failure() throws InterruptedException {
    TestObserver<Void> observer = new TestObserver<>();
    service.completable().subscribe(observer);
    assertFalse(observer.await(1, TimeUnit.SECONDS));

    server.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST));
    observer.awaitTerminalEvent(1, TimeUnit.SECONDS);
    observer.assertError(IOException.class);
  }

  @Test
  public void throwingInOnCompleteDeliveredToPlugin() throws InterruptedException {
    server.enqueue(new MockResponse());

    CountDownLatch latch = new CountDownLatch(1);
    AtomicReference<Throwable> errorReference = new AtomicReference<>();

    RxJavaPlugins.setErrorHandler(throwable -> {
      if (!errorReference.compareAndSet(null, throwable)) {
        throw Exceptions.propagate(throwable); // Don't swallow secondary errors!
      }
      latch.countDown();
    });

    TestObserver<Void> observer = new TestObserver<>();
    RuntimeException exception = new RuntimeException();
    service.completable()
      .subscribe(new ForwardingCompletableObserver(observer) {
        @Override
        public void onComplete() {
          throw exception;
        }
      });

    latch.await(1, TimeUnit.SECONDS);

    assertThat(errorReference.get())
      .isInstanceOf(UndeliverableException.class);

    assertThat(errorReference.get().getCause())
      .isSameAs(exception);
  }

  @Test
  public void bodyThrowingInOnErrorDeliveredToPlugin() throws InterruptedException {
    server.enqueue(new MockResponse().setResponseCode(404));

    final CountDownLatch latch = new CountDownLatch(1);
    final AtomicReference<Throwable> pluginRef = new AtomicReference<>();
    RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
      @Override
      public void accept(Throwable throwable) throws Exception {
        if (!pluginRef.compareAndSet(null, throwable)) {
          throw Exceptions.propagate(throwable); // Don't swallow secondary errors!
        }
        latch.countDown();
      }
    });

    TestObserver<Void> observer = new TestObserver<>();
    final RuntimeException e = new RuntimeException();
    final AtomicReference<Throwable> errorRef = new AtomicReference<>();
    service.completable().subscribe(new ForwardingCompletableObserver(observer) {
      @Override
      public void onError(Throwable throwable) {
        errorRef.set(throwable);
        throw e;
      }
    });

    latch.await(1, TimeUnit.SECONDS);

    //noinspection ThrowableResultOfMethodCallIgnored
    CompositeException composite = (CompositeException) pluginRef.get();

    assertThat(composite.getExceptions())
      .containsExactly(errorRef.get(), e);
  }
}
