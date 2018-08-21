package com.kiwi.mobile.retrograph;

import java.util.*;

import org.junit.*;

import io.reactivex.Observable;
import io.reactivex.disposables.*;

import retrofit2.*;
import retrofit2.http.*;

import com.kiwi.mobile.retrograph.converter.*;
import okhttp3.Call;
import okhttp3.*;
import okhttp3.mockwebserver.*;

import static org.junit.Assert.*;

public final class CancelDisposeTest {
  @Rule
  public final MockWebServer server = new MockWebServer();

  interface Service {
    @GET("/")
    Observable<String> go();
  }

  private final OkHttpClient client = new OkHttpClient();
  private Service service;

  @Before
  public void setUp() {
    Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(server.url("/"))
      .addConverterFactory(new StringConverterFactory())
      .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
      .callFactory(client)
      .build();
    service = retrofit.create(Service.class);
  }

  @Test
  public void disposeCancelsCall() {
    Disposable disposable = service.go().subscribe();
    List<Call> calls = client.dispatcher().runningCalls();
    assertEquals(1, calls.size());
    disposable.dispose();
    assertTrue(calls.get(0).isCanceled());
  }

  @Test
  public void cancelDoesNotDispose() {
    Disposable disposable = service.go().subscribe();
    List<Call> calls = client.dispatcher().runningCalls();
    assertEquals(1, calls.size());
    calls.get(0).cancel();
    assertFalse(disposable.isDisposed());
  }
}

