package com.kiwi.mobile.retrograph;

import java.util.*;

import org.junit.rules.*;
import org.junit.runner.*;
import org.junit.runners.model.*;

import io.reactivex.*;
import io.reactivex.Observer;
import io.reactivex.disposables.*;

import static org.assertj.core.api.Assertions.*;

/**
 * A test {@link Observer} and JUnit rule which guarantees all events are asserted.
 */
final class RecordingSingleObserver<T> implements SingleObserver<T> {
  private final Deque<Notification<T>> events = new ArrayDeque<>();

  private RecordingSingleObserver() {
  }

  @Override
  public void onSubscribe(Disposable disposable) {
  }

  @Override
  public void onSuccess(T value) {
    events.add(Notification.createOnNext(value));
  }

  @Override
  public void onError(Throwable e) {
    events.add(Notification.<T>createOnError(e));
  }

  private Notification<T> takeNotification() {
    Notification<T> notification = events.pollFirst();
    if (notification == null) {
      throw new AssertionError("No event found!");
    }
    return notification;
  }

  public T takeValue() {
    Notification<T> notification = takeNotification();
    assertThat(notification.isOnNext())
      .as("Expected onNext event but was " + notification)
      .isTrue();
    return notification.getValue();
  }

  public Throwable takeError() {
    Notification<T> notification = takeNotification();
    assertThat(notification.isOnError())
      .as("Expected onError event but was " + notification)
      .isTrue();
    return notification.getError();
  }

  public RecordingSingleObserver<T> assertAnyValue() {
    takeValue();
    return this;
  }

  public RecordingSingleObserver<T> assertValue(T value) {
    assertThat(takeValue()).isEqualTo(value);
    return this;
  }

  public void assertError(Throwable throwable) {
    assertThat(takeError()).isEqualTo(throwable);
  }

  public void assertError(Class<? extends Throwable> errorClass) {
    assertError(errorClass, null);
  }

  public void assertError(Class<? extends Throwable> errorClass, String message) {
    Throwable throwable = takeError();
    assertThat(throwable).isInstanceOf(errorClass);
    if (message != null) {
      assertThat(throwable).hasMessage(message);
    }
    assertNoEvents();
  }

  public void assertNoEvents() {
    assertThat(events).as("Unconsumed events found!").isEmpty();
  }

  public static final class Rule implements TestRule {
    final List<RecordingSingleObserver<?>> subscribers = new ArrayList<>();

    public <T> RecordingSingleObserver<T> create() {
      RecordingSingleObserver<T> subscriber = new RecordingSingleObserver<>();
      subscribers.add(subscriber);
      return subscriber;
    }

    @Override
    public Statement apply(final Statement base, Description description) {
      return new Statement() {
        @Override
        public void evaluate() throws Throwable {
          base.evaluate();
          for (RecordingSingleObserver<?> subscriber : subscribers) {
            subscriber.assertNoEvents();
          }
        }
      };
    }
  }
}
