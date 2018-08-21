package com.kiwi.mobile.retrograph

import com.kiwi.mobile.retrograph.util.*
import io.reactivex.*
import io.reactivex.exceptions.*
import io.reactivex.observers.*
import io.reactivex.plugins.*
import okhttp3.mockwebserver.*
import org.assertj.core.api.Assertions.*
import org.junit.*
import org.junit.Assert.*
import retrofit2.*
import retrofit2.http.*
import java.io.*
import java.util.concurrent.*
import java.util.concurrent.atomic.*

class AsyncTest {

  // region Private Types

  internal interface Service {
    @GET("/")
    fun completable(): Completable
  }

  // endregion Private Types

  // region Public Properties

  @get:Rule
  val server = MockWebServer()

  // endregion Public Properties

  // region Private Properties

  private lateinit var service: Service

  // endregion Private Properties

  // region Public Methods

  @Before
  fun setUp() {
    val retrofit = Retrofit.Builder()
      .baseUrl(server.url("/"))
      .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
      .build()
    service = retrofit.create(Service::class.java)
  }

  @Test
  @Throws(InterruptedException::class)
  fun success() {
    val observer = TestObserver<Unit>()

    service.completable()
      .subscribe(observer)

    assertFalse(observer.await(1, TimeUnit.SECONDS))

    val response = MockResponse().setBody("Hi")

    server.enqueue(response)

    observer.awaitTerminalEvent(1, TimeUnit.SECONDS)
    observer.assertComplete()
  }

  @Test
  @Throws(InterruptedException::class)
  fun failure() {
    val observer = TestObserver<Any>()

    service.completable()
      .subscribe(observer)

    assertFalse(observer.await(1, TimeUnit.SECONDS))

    server.enqueue(MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST))

    observer.awaitTerminalEvent(1, TimeUnit.SECONDS)
    observer.assertError(IOException::class.java)
  }

  @Test
  @Throws(InterruptedException::class)
  fun throwingInOnCompleteDeliveredToPlugin() {
    server.enqueue(MockResponse().setBody("Hi"))

    val latch = CountDownLatch(1)
    val errorReference = AtomicReference<Throwable>()

    RxJavaPlugins.setErrorHandler { throwable ->
      if (!errorReference.compareAndSet(null, throwable)) {
        throw Exceptions.propagate(throwable) // Don't swallow secondary errors!
      }
      latch.countDown()
    }

    val observer = TestObserver<Void>()
    val exception = RuntimeException()
    service.completable()
      .subscribe(object: ForwardingCompletableObserver(observer) {
        override fun onComplete() {
          throw exception
        }
      })

    latch.await(1, TimeUnit.SECONDS)

    assertThat(errorReference.get())
      .isInstanceOf(UndeliverableException::class.java)
    assertThat(errorReference.get().cause)
      .isSameAs(exception)
  }

  @Test
  @Throws(InterruptedException::class)
  fun bodyThrowingInOnErrorDeliveredToPlugin() {
    server.enqueue(MockResponse().setResponseCode(404))

    val latch = CountDownLatch(1)
    val pluginReference = AtomicReference<Throwable>()

    RxJavaPlugins.setErrorHandler { throwable ->
      if (!pluginReference.compareAndSet(null, throwable)) {
        throw Exceptions.propagate(throwable) // Don't swallow secondary errors!
      }
      latch.countDown()
    }

    val observer = TestObserver<Any>()
    val exception = RuntimeException()
    val throwableReference = AtomicReference<Throwable>()
    service.completable()
      .subscribe(object: ForwardingCompletableObserver(observer) {
        override fun onError(throwable: Throwable) {
          throwableReference.set(throwable)
          throw exception
        }
      })

    latch.await(1, TimeUnit.SECONDS)

    val composite = pluginReference.get() as CompositeException

    assertThat(composite.exceptions)
      .containsExactly(throwableReference.get(), exception)
  }

  // endregion Public Methods
}
