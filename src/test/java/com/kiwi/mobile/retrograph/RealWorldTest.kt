package com.kiwi.mobile.retrograph

import com.google.gson.*

import com.kiwi.mobile.retrograph.annotation.*
import com.kiwi.mobile.retrograph.model.Request
import com.kiwi.mobile.retrograph.model.Response

import io.reactivex.*
import io.reactivex.observers.*

import okhttp3.*
import okhttp3.logging.*

import org.assertj.core.api.Assertions.*

import org.junit.*

import retrofit2.*
import retrofit2.converter.gson.*
import retrofit2.http.*

import java.util.concurrent.*

class RealWorldTest {

  // region Private Types

  private companion object {
    const val QUERY = "{\n" +
      "  get_flights(parameters: {dateFrom: \"10/09/2018\", dateTo: \"10/10/2018\", flyFrom: \"london_gb\"}, providers: [KIWI]) {\n" +
      "    data {\n" +
      "      id\n" +
      "      booking_token\n" +
      "      flyFrom\n" +
      "      flyTo\n" +
      "      cityFrom\n" +
      "      cityTo\n" +
      "    }\n" +
      "    currency\n" +
      "    more_pending\n" +
      "  }\n" +
      "}\n"
    const val VARIABLES = "{}"
  }

  data class GetFlights(
    val data: List<Item>,
    val currency: String,
    @Alias("more_pending") val morePending: Boolean
  ) {

    data class Item(
      val id: String,
      @Alias("booking_token") val bookingToken: String,
      val flyFrom: String,
      val flyTo: String,
      val cityFrom: String,
      val cityTo: String
    )
  }

  interface UmbrellaRequestService {

    @POST("graphql")
    @GraphQL
    fun getFlightsWithWrapper(@Body request: Request): Observable<Response<GetFlights>>

    @POST("graphql")
    @GraphQL
    fun getFlightsWithoutWrapper(@Body request: Request): Observable<GetFlights>
  }

  // endregion Private Types

  // region Private Properties

  private lateinit var gson: Gson
  private lateinit var retrofit: Retrofit
  private lateinit var service: UmbrellaRequestService

  // endregion Private Properties

  // region Public Methods

  @Before
  fun setUp() {
    gson = GsonBuilder()
      //.registerTypeAdapter(GraphQLResponse::class.java, GraphQLAdapter())
      .create()
    retrofit = Retrofit.Builder()
      .baseUrl("https://r-dev-umbrella.skypicker.com/")
      .client(
        OkHttpClient.Builder()
          .connectTimeout(20, TimeUnit.SECONDS)
          .readTimeout(20, TimeUnit.SECONDS)
          .writeTimeout(20, TimeUnit.SECONDS)
          .addInterceptor(
            HttpLoggingInterceptor()
              .setLevel(HttpLoggingInterceptor.Level.BODY)
          )
          .build()
      )
      .addConverterFactory(GsonConverterFactory.create())
      .addCallAdapterFactory(GraphQLCallAdapterFactory.create())
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .build()
    service = retrofit.create(UmbrellaRequestService::class.java)
  }

  @Test
  fun whenGetFlightsWithWrapperIsRequested_thenSomeDataAreRetrieved() {
    // given
    val observer = TestObserver<Response<GetFlights>>()

    // when
    service.getFlightsWithWrapper(Request(QUERY, VARIABLES))
      .subscribe(observer)
    observer.awaitTerminalEvent()
    observer.dispose()

    // then

    observer.assertComplete()
    observer.assertNoErrors()

    val response = observer.values()[0]

    System.out.println(
      "whenGetFlightsWithWrapperIsRequested_thenSomeDataAreRetrieved(): response: ${gson.toJson(
        response)}"
    )

    assertThat(response.data).containsKey("get_flights")
    assertThat(response.data["get_flights"]).isNotNull
    assertThat(response.data["get_flights"]?.data)
      .isNotEmpty
      .allSatisfy {
        assertThat(it.id).isNotBlank()
        // TODO: assertThat(it.bookingToken).isNotBlank()
        assertThat(it.flyFrom).isNotBlank()
        assertThat(it.flyTo).isNotBlank()
        assertThat(it.cityFrom).isNotBlank()
        assertThat(it.cityTo).isNotBlank()
      }
  }

  @Test
  fun whenGetFlightsWithoutWrapperIsRequested_thenSomeDataAreRetrieved() {
    // given
    val observer = TestObserver<GetFlights>()

    // when
    service.getFlightsWithoutWrapper(Request(QUERY, VARIABLES))
      .subscribe(observer)
    observer.awaitTerminalEvent()
    observer.dispose()

    // then

    observer.assertComplete()
    observer.assertNoErrors()

    val response = observer.values()[0]

    System.out.println(
      "whenGetFlightsWithoutWrapperIsRequested_thenSomeDataAreRetrieved(): response: ${gson.toJson(
        response)}"
    )

    assertThat(response.data)
      .isNotEmpty
      .allSatisfy {
        assertThat(it.id).isNotBlank()
        // TODO: assertThat(it.bookingToken).isNotBlank()
        assertThat(it.flyFrom).isNotBlank()
        assertThat(it.flyTo).isNotBlank()
        assertThat(it.cityFrom).isNotBlank()
        assertThat(it.cityTo).isNotBlank()
      }
  }

  // endregion Public Methods
}
