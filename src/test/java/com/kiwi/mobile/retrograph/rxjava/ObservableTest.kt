package com.kiwi.mobile.retrograph.rxjava

import com.kiwi.mobile.retrograph.*
import com.kiwi.mobile.retrograph.RxJava2CallAdapterFactory
import com.kiwi.mobile.retrograph.annotation.*
import com.kiwi.mobile.retrograph.util.*

import io.reactivex.*

import okhttp3.mockwebserver.*
import okhttp3.mockwebserver.SocketPolicy.*

import org.assertj.core.api.Assertions.*
import org.junit.*

import retrofit2.*
import retrofit2.HttpException
import retrofit2.adapter.rxjava2.*
import retrofit2.converter.gson.*
import retrofit2.http.*

import java.io.*

import com.kiwi.mobile.retrograph.model.Response as GraphQLResponse

import retrofit2.Response as RetrofitResponse

class ObservableTest {

  // region Private Types

  private companion object {
    private const val BODY = """"Hi""""
    private const val GRAPH_QL_BODY = """{ "data": "Hi" }"""
  }

  internal interface Service {

    @GET("/")
    fun body(): Observable<String>

    @GraphQL
    @GET("/")
    fun graphQLBody(): Observable<String>

    @GraphQL
    @GET("/")
    fun graphQLGraphQLResponse(): Observable<GraphQLResponse<String>>

    @GET("/")
    fun retrofitResponse(): Observable<RetrofitResponse<String>>

    @GraphQL
    @GET("/")
    fun graphQLRetrofitResponse(): Observable<RetrofitResponse<String>>

    @GraphQL
    @GET("/")
    fun graphQLRetrofitResponseGraphQLResponse(): Observable<RetrofitResponse<GraphQLResponse<String>>>

    @GET("/")
    fun result(): Observable<Result<String>>

    @GraphQL
    @GET("/")
    fun graphQLResult(): Observable<Result<String>>

    @GraphQL
    @GET("/")
    fun graphQLResultGraphQLResponse(): Observable<Result<GraphQLResponse<String>>>
  }

  // endregion Private Types

  // region Public Properties

  @get:Rule
  val server = MockWebServer()

  @get:Rule
  val observerRule = RecordingObserver.Rule()

  // endregion Public Properties

  // region Private Properties

  private lateinit var service: Service

  // endregion Private Properties

  @Before
  fun setUp() {
    val retrofit = Retrofit.Builder()
      .baseUrl(server.url("/"))
      .addConverterFactory(GsonConverterFactory.create())
      .addCallAdapterFactory(GraphQLCallAdapterFactory.create())
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .build()
    service = retrofit.create(Service::class.java)
  }

  // region Body

  @Test
  fun bodySuccess200() {
    server.enqueue(MockResponse().setBody(BODY))

    val observer = observerRule.create<String>()
    service.body()
      .subscribe(observer)

    observer.assertValue("Hi")
      .assertComplete()
  }

  @Test
  fun bodySuccess404() {
    server.enqueue(MockResponse().setResponseCode(404))

    val observer = observerRule.create<String>()
    service.body()
      .subscribe(observer)

    // Required for backwards compatibility.
    observer.assertError(HttpException::class.java, "HTTP 404 Client Error")
  }

  @Test
  fun bodyFailure() {
    server.enqueue(MockResponse().setSocketPolicy(DISCONNECT_AFTER_REQUEST))

    val observer = observerRule.create<String>()
    service.body()
      .subscribe(observer)

    observer.assertError(IOException::class.java)
  }

  // endregion Body

  // region GraphQL Body

  @Test
  fun graphQLBodySuccess200() {
    server.enqueue(MockResponse().setBody(GRAPH_QL_BODY))

    val observer = observerRule.create<String>()
    service.graphQLBody()
      .subscribe(observer)

    observer.assertValue("Hi")
      .assertComplete()
  }

  @Test
  fun graphQLBodySuccess404() {
    server.enqueue(MockResponse().setResponseCode(404))

    val observer = observerRule.create<String>()
    service.graphQLBody()
      .subscribe(observer)

    // Required for backwards compatibility.
    observer.assertError(HttpException::class.java, "HTTP 404 Client Error")
  }

  @Test
  fun graphQLBodyFailure() {
    server.enqueue(MockResponse().setSocketPolicy(DISCONNECT_AFTER_REQUEST))

    val observer = observerRule.create<String>()
    service.graphQLBody()
      .subscribe(observer)

    observer.assertError(IOException::class.java)
  }

  // endregion GraphQL Body

  // region GraphQL GraphQL Response

  @Test
  fun graphQLGraphQLResponseSuccess200() {
    server.enqueue(MockResponse().setBody(GRAPH_QL_BODY))

    val observer = observerRule.create<GraphQLResponse<String>>()
    service.graphQLGraphQLResponse()
      .subscribe(observer)

    observer.assertValue(
      GraphQLResponse("Hi")
    )
      .assertComplete()
  }

  @Test
  fun graphQLGraphQLResponseSuccess404() {
    server.enqueue(MockResponse().setResponseCode(404))

    val observer = observerRule.create<GraphQLResponse<String>>()
    service.graphQLGraphQLResponse()
      .subscribe(observer)

    // Required for backwards compatibility.
    observer.assertError(HttpException::class.java, "HTTP 404 Client Error")
  }

  @Test
  fun graphQLGraphQLResponseFailure() {
    server.enqueue(MockResponse().setSocketPolicy(DISCONNECT_AFTER_REQUEST))

    val observer = observerRule.create<GraphQLResponse<String>>()
    service.graphQLGraphQLResponse()
      .subscribe(observer)

    observer.assertError(IOException::class.java)
  }

  // endregion GraphQL GraphQL Response

  // region Retrofit Response

  @Test
  fun retrofitResponseSuccess200() {
    server.enqueue(MockResponse().setBody(BODY))

    val observer = observerRule.create<RetrofitResponse<String>>()
    service.retrofitResponse()
      .subscribe(observer)

    val response = observer.takeValue()
    assertThat(response.isSuccessful)
      .isTrue()
    assertThat(response.body())
      .isEqualTo("Hi")
    observer.assertComplete()
  }

  @Test
  fun retrofitResponseSuccess404() {
    server.enqueue(MockResponse().setResponseCode(404))

    val observer = observerRule.create<RetrofitResponse<String>>()
    service.retrofitResponse()
      .subscribe(observer)

    val response = observer.takeValue()
    assertThat(response.isSuccessful)
      .isFalse()
    observer.assertComplete()
  }

  @Test
  fun retrofitResponseFailure() {
    server.enqueue(MockResponse().setSocketPolicy(DISCONNECT_AFTER_REQUEST))

    val observer = observerRule.create<RetrofitResponse<String>>()
    service.retrofitResponse()
      .subscribe(observer)

    observer.assertError(IOException::class.java)
  }

  // endregion Retrofit Response

  // region GraphQL Retrofit Response

  @Test
  fun graphQLRetrofitResponseSuccess200() {
    server.enqueue(MockResponse().setBody(GRAPH_QL_BODY))

    val observer = observerRule.create<RetrofitResponse<String>>()
    service.graphQLRetrofitResponse()
      .subscribe(observer)

    val response = observer.takeValue()
    assertThat(response.isSuccessful)
      .isTrue()
    assertThat(response.body())
      .isEqualTo("Hi")
    observer.assertComplete()
  }

  @Test
  fun graphQLRetrofitResponseSuccess404() {
    server.enqueue(MockResponse().setResponseCode(404))

    val observer = observerRule.create<RetrofitResponse<String>>()
    service.graphQLRetrofitResponse()
      .subscribe(observer)

    val response = observer.takeValue()
    assertThat(response.isSuccessful)
      .isFalse()
    observer.assertComplete()
  }

  @Test
  fun graphQLRetrofitResponseFailure() {
    server.enqueue(MockResponse().setSocketPolicy(DISCONNECT_AFTER_REQUEST))

    val observer = observerRule.create<RetrofitResponse<String>>()
    service.graphQLRetrofitResponse()
      .subscribe(observer)

    observer.assertError(IOException::class.java)
  }

  // endregion GraphQL Retrofit Response

  // region GraphQL Retrofit Response GraphQL Response

  @Test
  fun graphQLRetrofitResponseGraphQLResponseSuccess200() {
    server.enqueue(MockResponse().setBody(GRAPH_QL_BODY))

    val observer = observerRule.create<RetrofitResponse<GraphQLResponse<String>>>()
    service.graphQLRetrofitResponseGraphQLResponse()
      .subscribe(observer)

    val response = observer.takeValue()
    assertThat(response.isSuccessful)
      .isTrue()
    assertThat(response.body())
      .isEqualTo(GraphQLResponse("Hi"))
    observer.assertComplete()
  }

  @Test
  fun graphQLRetrofitResponseGraphQLResponseSuccess404() {
    server.enqueue(MockResponse().setResponseCode(404))

    val observer = observerRule.create<RetrofitResponse<GraphQLResponse<String>>>()
    service.graphQLRetrofitResponseGraphQLResponse()
      .subscribe(observer)

    val response = observer.takeValue()
    assertThat(response.isSuccessful)
      .isFalse()
    observer.assertComplete()
  }

  @Test
  fun graphQLRetrofitResponseGraphQLResponseFailure() {
    server.enqueue(MockResponse().setSocketPolicy(DISCONNECT_AFTER_REQUEST))

    val observer = observerRule.create<RetrofitResponse<GraphQLResponse<String>>>()
    service.graphQLRetrofitResponseGraphQLResponse()
      .subscribe(observer)

    observer.assertError(IOException::class.java)
  }

  // endregion GraphQL Retrofit Response GraphQL Response

  // region Result

  @Test
  fun resultSuccess200() {
    server.enqueue(MockResponse().setBody(BODY))

    val observer = observerRule.create<Result<String>>()
    service.result()
      .subscribe(observer)

    val result = observer.takeValue()
    assertThat(result.isError)
      .isFalse()
    val response = result.response()!!
    assertThat(response.isSuccessful)
      .isTrue()
    assertThat(response.body())
      .isEqualTo("Hi")
    observer.assertComplete()
  }

  @Test
  fun resultSuccess404() {
    server.enqueue(MockResponse().setResponseCode(404))

    val observer = observerRule.create<Result<String>>()
    service.result()
      .subscribe(observer)

    val result = observer.takeValue()
    assertThat(result.isError)
      .isFalse()
    val response = result.response()!!
    assertThat(response.isSuccessful)
      .isFalse()
    observer.assertComplete()
  }

  @Test
  fun resultFailure() {
    server.enqueue(MockResponse().setSocketPolicy(DISCONNECT_AFTER_REQUEST))

    val observer = observerRule.create<Result<String>>()
    service.result()
      .subscribe(observer)

    val result = observer.takeValue()
    assertThat(result.isError)
      .isTrue()
    assertThat(result.error())
      .isInstanceOf(IOException::class.java)
    observer.assertComplete()
  }

  // endregion Result

  // region GraphQL Result

  @Test
  fun graphQLResultSuccess200() {
    server.enqueue(MockResponse().setBody(GRAPH_QL_BODY))

    val observer = observerRule.create<Result<String>>()
    service.graphQLResult()
      .subscribe(observer)

    val result = observer.takeValue()
    assertThat(result.isError)
      .isFalse()
    val response = result.response()!!
    assertThat(response.isSuccessful)
      .isTrue()
    assertThat(response.body())
      .isEqualTo("Hi")
    observer.assertComplete()
  }

  @Test
  fun graphQLResultSuccess404() {
    server.enqueue(MockResponse().setResponseCode(404))

    val observer = observerRule.create<Result<String>>()
    service.graphQLResult()
      .subscribe(observer)

    val result = observer.takeValue()
    assertThat(result.isError)
      .isFalse()
    val response = result.response()!!
    assertThat(response.isSuccessful)
      .isFalse()
    observer.assertComplete()
  }

  @Test
  fun graphQLResultFailure() {
    server.enqueue(MockResponse().setSocketPolicy(DISCONNECT_AFTER_REQUEST))

    val observer = observerRule.create<Result<String>>()
    service.graphQLResult()
      .subscribe(observer)

    val result = observer.takeValue()
    assertThat(result.isError)
      .isTrue()
    assertThat(result.error())
      .isInstanceOf(IOException::class.java)
    observer.assertComplete()
  }

  // endregion GraphQL Result

  // region GraphQL Result GraphQL Response

  @Test
  fun graphQLResultGraphQLResponseSuccess200() {
    server.enqueue(MockResponse().setBody(GRAPH_QL_BODY))

    val observer = observerRule.create<Result<GraphQLResponse<String>>>()
    service.graphQLResultGraphQLResponse()
      .subscribe(observer)

    val result = observer.takeValue()
    assertThat(result.isError)
      .isFalse()
    val response = result.response()!!
    assertThat(response.isSuccessful)
      .isTrue()
    assertThat(response.body())
      .isEqualTo(GraphQLResponse("Hi"))
    observer.assertComplete()
  }

  @Test
  fun graphQLResultGraphQLResponseSuccess404() {
    server.enqueue(MockResponse().setResponseCode(404))

    val observer = observerRule.create<Result<GraphQLResponse<String>>>()
    service.graphQLResultGraphQLResponse()
      .subscribe(observer)

    val result = observer.takeValue()
    assertThat(result.isError)
      .isFalse()
    val response = result.response()!!
    assertThat(response.isSuccessful)
      .isFalse()
    observer.assertComplete()
  }

  @Test
  fun graphQLResultGraphQLResponseFailure() {
    server.enqueue(MockResponse().setSocketPolicy(DISCONNECT_AFTER_REQUEST))

    val observer = observerRule.create<Result<GraphQLResponse<String>>>()
    service.graphQLResultGraphQLResponse()
      .subscribe(observer)

    val result = observer.takeValue()
    assertThat(result.isError)
      .isTrue()
    assertThat(result.error())
      .isInstanceOf(IOException::class.java)
    observer.assertComplete()
  }

  // endregion GraphQL Result GraphQL Response
}
