package com.kiwi.mobile.retrograph.rxjava;

import java.io.*;

import org.junit.*;

import io.reactivex.*;

import retrofit2.HttpException;
import retrofit2.*;
import retrofit2.adapter.rxjava2.*;
import retrofit2.http.*;

import com.kiwi.mobile.retrograph.RxJava2CallAdapterFactory;
import com.kiwi.mobile.retrograph.converter.*;
import com.kiwi.mobile.retrograph.util.*;
import okhttp3.mockwebserver.*;

import static okhttp3.mockwebserver.SocketPolicy.*;
import static org.assertj.core.api.Assertions.*;

public final class FlowableTest {
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
  public void bodySuccess200() {
    server.enqueue(new MockResponse().setBody("Hi"));

    RecordingSubscriber<String> subscriber = subscriberRule.create();
    service.body().subscribe(subscriber);
    subscriber.assertValue("Hi").assertComplete();
  }

  @Test
  public void bodySuccess404() {
    server.enqueue(new MockResponse().setResponseCode(404));

    RecordingSubscriber<String> subscriber = subscriberRule.create();
    service.body().subscribe(subscriber);
    // Required for backwards compatibility.
    subscriber.assertError(HttpException.class, "HTTP 404 Client Error");
  }

  @Test
  public void bodyFailure() {
    server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AFTER_REQUEST));

    RecordingSubscriber<String> subscriber = subscriberRule.create();
    service.body().subscribe(subscriber);
    subscriber.assertError(IOException.class);
  }

  @Test
  @Ignore("This test is broken also in Retrofit repository when RxJava is updated.")
  public void bodyRespectsBackpressure() {
    server.enqueue(new MockResponse().setBody("Hi"));

    RecordingSubscriber<String> subscriber = subscriberRule.createWithInitialRequest(0);
    Flowable<String> body = service.body();

    body.subscribe(subscriber);
    assertThat(server.getRequestCount()).isEqualTo(1);
    subscriber.assertNoEvents();

    subscriber.request(1);
    subscriber.assertAnyValue().assertComplete();

    subscriber.request(Long.MAX_VALUE); // Subsequent requests do not trigger HTTP or notifications.
    assertThat(server.getRequestCount()).isEqualTo(1);
  }

  @Test
  public void responseSuccess200() {
    server.enqueue(new MockResponse());

    RecordingSubscriber<Response<String>> subscriber = subscriberRule.create();
    service.response().subscribe(subscriber);
    assertThat(subscriber.takeValue().isSuccessful()).isTrue();
    subscriber.assertComplete();
  }

  @Test
  public void responseSuccess404() {
    server.enqueue(new MockResponse().setResponseCode(404));

    RecordingSubscriber<Response<String>> subscriber = subscriberRule.create();
    service.response().subscribe(subscriber);
    assertThat(subscriber.takeValue().isSuccessful()).isFalse();
    subscriber.assertComplete();
  }

  @Test
  public void responseFailure() {
    server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AFTER_REQUEST));

    RecordingSubscriber<Response<String>> subscriber = subscriberRule.create();
    service.response().subscribe(subscriber);
    subscriber.assertError(IOException.class);
  }

  @Test
  @Ignore("This test is broken also in Retrofit repository when RxJava is updated.")
  public void responseRespectsBackpressure() {
    server.enqueue(new MockResponse().setBody("Hi"));

    RecordingSubscriber<Response<String>> subscriber = subscriberRule.createWithInitialRequest(0);
    Flowable<Response<String>> o = service.response();

    o.subscribe(subscriber);
    assertThat(server.getRequestCount()).isEqualTo(1);
    subscriber.assertNoEvents();

    subscriber.request(1);
    subscriber.assertAnyValue().assertComplete();

    subscriber.request(Long.MAX_VALUE); // Subsequent requests do not trigger HTTP or notifications.
    assertThat(server.getRequestCount()).isEqualTo(1);
  }

  @Test
  public void resultSuccess200() {
    server.enqueue(new MockResponse());

    RecordingSubscriber<Result<String>> subscriber = subscriberRule.create();
    service.result().subscribe(subscriber);
    Result<String> result = subscriber.takeValue();
    assertThat(result.isError()).isFalse();
    assertThat(result.response().isSuccessful()).isTrue();
    subscriber.assertComplete();
  }

  @Test
  public void resultSuccess404() {
    server.enqueue(new MockResponse().setResponseCode(404));

    RecordingSubscriber<Result<String>> subscriber = subscriberRule.create();
    service.result().subscribe(subscriber);
    Result<String> result = subscriber.takeValue();
    assertThat(result.isError()).isFalse();
    assertThat(result.response().isSuccessful()).isFalse();
    subscriber.assertComplete();
  }

  @Test
  public void resultFailure() {
    server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AFTER_REQUEST));

    RecordingSubscriber<Result<String>> subscriber = subscriberRule.create();
    service.result().subscribe(subscriber);
    Result<String> result = subscriber.takeValue();
    assertThat(result.isError()).isTrue();
    assertThat(result.error()).isInstanceOf(IOException.class);
    subscriber.assertComplete();
  }

  @Test
  @Ignore("This test is broken also in Retrofit repository when RxJava is updated.")
  public void resultRespectsBackpressure() {
    server.enqueue(new MockResponse().setBody("Hi"));

    RecordingSubscriber<Result<String>> subscriber = subscriberRule.createWithInitialRequest(0);
    Flowable<Result<String>> result = service.result();

    result.subscribe(subscriber);
    assertThat(server.getRequestCount()).isEqualTo(1);
    subscriber.assertNoEvents();

    subscriber.request(1);
    subscriber.assertAnyValue().assertComplete();

    subscriber.request(Long.MAX_VALUE); // Subsequent requests do not trigger HTTP or notifications.
    assertThat(server.getRequestCount()).isEqualTo(1);
  }
}
