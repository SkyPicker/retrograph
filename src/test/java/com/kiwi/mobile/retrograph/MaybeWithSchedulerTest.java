package com.kiwi.mobile.retrograph;

import org.junit.*;

import io.reactivex.*;
import io.reactivex.schedulers.*;

import retrofit2.*;
import retrofit2.http.*;

import okhttp3.mockwebserver.*;

public final class MaybeWithSchedulerTest {
  @Rule
  public final MockWebServer server = new MockWebServer();
  @Rule
  public final RecordingMaybeObserver.Rule observerRule = new RecordingMaybeObserver.Rule();

  interface Service {
    @GET("/")
    Maybe<String> body();

    @GET("/")
    Maybe<Response<String>> response();

    @GET("/")
    Maybe<Result<String>> result();
  }

  private final TestScheduler scheduler = new TestScheduler();
  private Service service;

  @Before
  public void setUp() {
    Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(server.url("/"))
      .addConverterFactory(new StringConverterFactory())
      .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(scheduler))
      .build();
    service = retrofit.create(Service.class);
  }

  @Test
  public void bodyUsesScheduler() {
    server.enqueue(new MockResponse());

    RecordingMaybeObserver<Object> observer = observerRule.create();
    service.body().subscribe(observer);
    observer.assertNoEvents();

    scheduler.triggerActions();
    observer.assertAnyValue();
  }

  @Test
  public void responseUsesScheduler() {
    server.enqueue(new MockResponse());

    RecordingMaybeObserver<Object> observer = observerRule.create();
    service.response().subscribe(observer);
    observer.assertNoEvents();

    scheduler.triggerActions();
    observer.assertAnyValue();
  }

  @Test
  public void resultUsesScheduler() {
    server.enqueue(new MockResponse());

    RecordingMaybeObserver<Object> observer = observerRule.create();
    service.result().subscribe(observer);
    observer.assertNoEvents();

    scheduler.triggerActions();
    observer.assertAnyValue();
  }
}
