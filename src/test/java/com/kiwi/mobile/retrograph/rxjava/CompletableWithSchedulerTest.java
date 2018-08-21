package com.kiwi.mobile.retrograph.rxjava;

import org.junit.*;

import io.reactivex.*;
import io.reactivex.schedulers.*;

import retrofit2.*;
import retrofit2.http.*;

import com.kiwi.mobile.retrograph.*;
import com.kiwi.mobile.retrograph.util.*;
import okhttp3.mockwebserver.*;

public final class CompletableWithSchedulerTest {
  @Rule
  public final MockWebServer server = new MockWebServer();
  @Rule
  public final RecordingCompletableObserver.Rule observerRule =
    new RecordingCompletableObserver.Rule();

  interface Service {
    @GET("/")
    Completable completable();
  }

  private final TestScheduler scheduler = new TestScheduler();
  private Service service;

  @Before
  public void setUp() {
    Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(server.url("/"))
      .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(scheduler))
      .build();
    service = retrofit.create(Service.class);
  }

  @Test
  public void completableUsesScheduler() {
    server.enqueue(new MockResponse());

    RecordingCompletableObserver observer = observerRule.create();
    service.completable().subscribe(observer);
    observer.assertNoEvents();

    scheduler.triggerActions();
    observer.assertComplete();
  }
}
