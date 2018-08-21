package com.kiwi.mobile.retrograph.rxjava;

import org.junit.*;

import io.reactivex.*;
import io.reactivex.schedulers.*;

import retrofit2.*;
import retrofit2.adapter.rxjava2.*;
import retrofit2.http.*;

import com.kiwi.mobile.retrograph.RxJava2CallAdapterFactory;
import com.kiwi.mobile.retrograph.converter.*;
import com.kiwi.mobile.retrograph.util.*;
import okhttp3.mockwebserver.*;

public final class ObservableWithSchedulerTest {
  @Rule
  public final MockWebServer server = new MockWebServer();
  @Rule
  public final RecordingObserver.Rule observerRule = new RecordingObserver.Rule();

  interface Service {
    @GET("/")
    Observable<String> body();

    @GET("/")
    Observable<Response<String>> response();

    @GET("/")
    Observable<Result<String>> result();
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

    RecordingObserver<Object> observer = observerRule.create();
    service.body().subscribe(observer);
    observer.assertNoEvents();

    scheduler.triggerActions();
    observer.assertAnyValue().assertComplete();
  }

  @Test
  public void responseUsesScheduler() {
    server.enqueue(new MockResponse());

    RecordingObserver<Object> observer = observerRule.create();
    service.response().subscribe(observer);
    observer.assertNoEvents();

    scheduler.triggerActions();
    observer.assertAnyValue().assertComplete();
  }

  @Test
  public void resultUsesScheduler() {
    server.enqueue(new MockResponse());

    RecordingObserver<Object> observer = observerRule.create();
    service.result().subscribe(observer);
    observer.assertNoEvents();

    scheduler.triggerActions();
    observer.assertAnyValue().assertComplete();
  }
}
