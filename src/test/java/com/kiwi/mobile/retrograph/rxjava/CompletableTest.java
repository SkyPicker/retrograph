package com.kiwi.mobile.retrograph.rxjava;

import java.io.*;

import org.junit.*;

import io.reactivex.*;

import retrofit2.*;
import retrofit2.http.*;

import com.kiwi.mobile.retrograph.*;
import com.kiwi.mobile.retrograph.util.*;
import okhttp3.mockwebserver.*;

import static okhttp3.mockwebserver.SocketPolicy.*;

public final class CompletableTest {
  @Rule
  public final MockWebServer server = new MockWebServer();
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
  public void completableSuccess200() {
    server.enqueue(new MockResponse().setBody("Hi"));

    RecordingCompletableObserver observer = observerRule.create();

    service.completable()
      .subscribe(observer);

    observer.assertComplete();
  }

  @Test
  public void completableSuccess404() {
    server.enqueue(new MockResponse().setResponseCode(404));

    RecordingCompletableObserver observer = observerRule.create();

    service.completable()
      .subscribe(observer);

    // Required for backwards compatibility.
    observer.assertError(HttpException.class, "HTTP 404 Client Error");
  }

  @Test
  public void completableFailure() {
    server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AFTER_REQUEST));

    RecordingCompletableObserver observer = observerRule.create();

    service.completable()
      .subscribe(observer);

    observer.assertError(IOException.class);
  }
}
