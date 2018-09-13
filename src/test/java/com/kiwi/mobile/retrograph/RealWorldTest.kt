package com.kiwi.mobile.retrograph

import com.google.gson.*

import com.kiwi.mobile.retrograph.annotation.*
import com.kiwi.mobile.retrograph.annotation.Arguments
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

  // region Public Types

  enum class Provider {
    KIWI
  }

  data class GetFlights(
    @Arguments(FlightsSourceArguments::class)
    val get_flights: FlightsSource
  )

  data class FlightsSource(
    val data: List<FlightInterface>,
    val currency: String,
    @Alias("more_pending") val morePending: Boolean
  ) {

    data class FlightInterface(
      val id: String,
      @Alias("booking_token") val bookingToken: String,
      val flyFrom: String,
      val flyTo: String,
      val cityFrom: String,
      val cityTo: String
    )
  }

  data class GetFlightsArguments(
    val get_flights: FlightsSourceArguments
  )

  data class FlightsSourceArguments(
    val parameters: Parameters,
    val providers: Array<Provider>
  ) {
    data class Parameters(
      val dateFrom: String,
      val dateTo: String,
      val flyFrom: String
    )
  }

  // endregion Public Types

  // region Private Types

  private companion object {

    val GET_FLIGHT_ARGUMENTS = GetFlightsArguments(
      get_flights = FlightsSourceArguments(
        parameters = FlightsSourceArguments.Parameters(
          dateFrom = "10/09/2018",
          dateTo = "10/10/2018",
          flyFrom = "london_gb"
        ),
        providers = arrayOf(Provider.KIWI)
      )
    )

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

  interface UmbrellaRequestService {

    @POST("graphql")
    @GraphQL
    fun getFlightsWithWrapper(@Body request: Request): Observable<Response<GetFlights>>

    @POST("graphql")
    @GraphQL
    fun getFlightsWithWrapper(
      @Arguments arguments: GetFlightsArguments
    ): Observable<Response<GetFlights>>

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
              .setLevel(HttpLoggingInterceptor.Level.BASIC)
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
  fun whenGetFlightsWithWrapperAndRequestIsRequested_thenSomeDataAreRetrieved() {
    // given

    val observer = TestObserver<Response<GetFlights>>()
    val request = Request(QUERY, VARIABLES)

    System.out.println(
      "whenGetFlightsWithWrapperAndRequestIsRequested_thenSomeDataAreRetrieved(): request: "
        + gson.toJson(request)
    )

    // when

    service.getFlightsWithWrapper(request)
      .subscribe(observer)
    observer.awaitTerminalEvent()
    observer.dispose()

    // then

    observer.assertComplete()
    observer.assertNoErrors()

    val response = observer.values()[0]

    System.out.println(
      "whenGetFlightsWithWrapperAndRequestIsRequested_thenSomeDataAreRetrieved(): response: "
        + gson.toJson(response)
    )

    assertThat(response.data)
      .isNotNull
    assertThat(response.data?.get_flights)
      .isNotNull
    assertThat(response.data?.get_flights?.data)
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
  @Ignore
  fun whenGetFlightsWithWrapperWithoutRequestIsRequested_thenSomeDataAreRetrieved() {
    // given

    val observer = TestObserver<Response<GetFlights>>()

    // when

    service.getFlightsWithWrapper(GET_FLIGHT_ARGUMENTS)
      .subscribe(observer)
    observer.awaitTerminalEvent()
    observer.dispose()

    // then

    observer.assertComplete()
    observer.assertNoErrors()

    val response = observer.values()[0]

    System.out.println(
      "whenGetFlightsWithWrapperWithoutRequestIsRequested_thenSomeDataAreRetrieved(): response: "
        + gson.toJson(response)
    )

    assertThat(response.data)
      .isNotNull
    assertThat(response.data?.get_flights)
      .isNotNull
    assertThat(response.data?.get_flights?.data)
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
    val request = Request(QUERY, VARIABLES)

    System.out.println(
      "whenGetFlightsWithoutWrapperIsRequested_thenSomeDataAreRetrieved(): request: " +
        gson.toJson(request)
    )

    // when

    service.getFlightsWithoutWrapper(request)
      .subscribe(observer)
    observer.awaitTerminalEvent()
    observer.dispose()

    // then

    observer.assertComplete()
    observer.assertNoErrors()

    val response = observer.values()[0]

    System.out.println(
      "whenGetFlightsWithoutWrapperIsRequested_thenSomeDataAreRetrieved(): response: " +
        gson.toJson(response)
    )

    assertThat(response.get_flights)
      .isNotNull
    assertThat(response.get_flights.data)
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
