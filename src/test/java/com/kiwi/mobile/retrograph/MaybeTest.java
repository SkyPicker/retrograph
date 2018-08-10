package com.kiwi.mobile.retrograph;

import java.io.*;

import org.junit.*;

import io.reactivex.*;

import retrofit2.*;
import retrofit2.http.*;

import okhttp3.mockwebserver.*;

import static okhttp3.mockwebserver.SocketPolicy.*;
import static org.assertj.core.api.Assertions.*;

public final class MaybeTest {
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

    RecordingMaybeObserver<String> observer = observerRule.create();
    service.body().subscribe(observer);
    observer.assertValue("Hi");
  }

  @Test
  public void bodySuccess404() {
    server.enqueue(new MockResponse().setResponseCode(404));

    RecordingMaybeObserver<String> observer = observerRule.create();
    service.body().subscribe(observer);
    // Required for backwards compatibility.
    observer.assertError(HttpException.class, "HTTP 404 Client Error");
  }

  @Test
  public void bodyFailure() {
    server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AFTER_REQUEST));

    RecordingMaybeObserver<String> observer = observerRule.create();
    service.body().subscribe(observer);
    observer.assertError(IOException.class);
  }

  @Test
  public void responseSuccess200() {
    server.enqueue(new MockResponse().setBody("Hi"));

    RecordingMaybeObserver<Response<String>> observer = observerRule.create();
    service.response().subscribe(observer);
    Response<String> response = observer.takeValue();
    assertThat(response.isSuccessful()).isTrue();
  }

  @Test
  public void responseSuccess404() {
    server.enqueue(new MockResponse().setResponseCode(404));

    RecordingMaybeObserver<Response<String>> observer = observerRule.create();
    service.response().subscribe(observer);
    assertThat(observer.takeValue().isSuccessful()).isFalse();
  }

  @Test
  public void responseFailure() {
    server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AFTER_REQUEST));

    RecordingMaybeObserver<Response<String>> observer = observerRule.create();
    service.response().subscribe(observer);
    observer.assertError(IOException.class);
  }

  @Test
  public void resultSuccess200() {
    server.enqueue(new MockResponse().setBody("Hi"));

    RecordingMaybeObserver<Result<String>> observer = observerRule.create();
    service.result().subscribe(observer);
    Result<String> result = observer.takeValue();
    assertThat(result.isError()).isFalse();
    assertThat(result.response().isSuccessful()).isTrue();
  }

  @Test
  public void resultSuccess404() {
    server.enqueue(new MockResponse().setResponseCode(404));

    RecordingMaybeObserver<Result<String>> observer = observerRule.create();
    service.result().subscribe(observer);
    Result<String> result = observer.takeValue();
    assertThat(result.isError()).isFalse();
    assertThat(result.response().isSuccessful()).isFalse();
  }

  @Test
  public void resultFailure() {
    server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AFTER_REQUEST));

    RecordingMaybeObserver<Result<String>> observer = observerRule.create();
    service.result().subscribe(observer);
    Result<String> result = observer.takeValue();
    assertThat(result.isError()).isTrue();
    assertThat(result.error()).isInstanceOf(IOException.class);
  }
}
