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

public final class FlowableWithSchedulerTest {
  @Rule
  public final MockWebServer server = new MockWebServer();
  @Rule
  public final RecordingSubscriber.Rule subscriberRule = new RecordingSubscriber.Rule();

  interface Service {
    @GET("/")
    Flowable<String> body();

    @GET("/")
    Flowable<Response<String>> response();

    @GET("/")
    Flowable<Result<String>> result();
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

    RecordingSubscriber<Object> subscriber = subscriberRule.create();
    service.body().subscribe(subscriber);
    subscriber.assertNoEvents();

    scheduler.triggerActions();
    subscriber.assertAnyValue().assertComplete();
  }

  @Test
  public void responseUsesScheduler() {
    server.enqueue(new MockResponse());

    RecordingSubscriber<Object> subscriber = subscriberRule.create();
    service.response().subscribe(subscriber);
    subscriber.assertNoEvents();

    scheduler.triggerActions();
    subscriber.assertAnyValue().assertComplete();
  }

  @Test
  public void resultUsesScheduler() {
    server.enqueue(new MockResponse());

    RecordingSubscriber<Object> subscriber = subscriberRule.create();
    service.result().subscribe(subscriber);
    subscriber.assertNoEvents();

    scheduler.triggerActions();
    subscriber.assertAnyValue().assertComplete();
  }
}
