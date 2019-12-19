package com.kiwi.mobile.retrograph

import com.google.gson.*

import com.kiwi.mobile.retrograph.annotation.*
import com.kiwi.mobile.retrograph.annotation.Arguments
import com.kiwi.mobile.retrograph.model.Request
import com.kiwi.mobile.retrograph.model.Response

import io.reactivex.Observable
import io.reactivex.observers.*

import okhttp3.*
import okhttp3.logging.*

import org.assertj.core.api.Assertions.*

import org.joda.time.*
import org.joda.time.format.*

import org.junit.*

import retrofit2.*
import retrofit2.converter.gson.*
import retrofit2.http.*

import java.util.concurrent.*

class RealWorldTest {

  // region Public Types

  enum class Provider {
    KIWI;

    override fun toString() = name
  }

  data class GetFlights(
    val get_flights: FlightsSource
  )

  data class FlightsSource(
    val data: Array<FlightInterface>,
    val currency: String,
    @field:Alias("more_pending") val morePending: Boolean
  ) {

    data class FlightInterface(
      val id: String,
      @field:Alias("booking_token") val bookingToken: String,
      val flyFrom: String,
      val flyTo: String,
      val cityFrom: String,
      val cityTo: String
    )
  }

  data class GetFlightsArguments(
    val get_flights: FlightsSourceArguments = FlightsSourceArguments()
  )

  data class FlightsSourceArguments(
    val parameters: Parameters = Parameters(),
    val pagination: Pagination = Pagination(),
    val providers: List<Provider> = listOf(Provider.KIWI)
  ) {

    data class Parameters(
      val dateFrom: String = DATE_FROM,
      val dateTo: String = DATE_TO,
      val flyFrom: String = FLY_FROM,
      val v: Int = 3,
      val partner: String = "skypicker"
    )

    data class Pagination(
      val limit: Int = LIMIT,
      val offset: Int = OFFSET
    )
  }

  // endregion Public Types

  // region Private Types

  private companion object {

    val DATE_TIME_FORMAT = DateTimeFormat.forPattern("dd/MM/YYYY")

    val DATE_FROM = DateTime()
      .plusDays(10)
      .toString(DATE_TIME_FORMAT)

    val DATE_TO = DateTime()
      .plusDays(20)
      .toString(DATE_TIME_FORMAT)

    const val FLY_FROM = "london_gb"
    const val LIMIT = 20
    const val OFFSET = 0
    val PROVIDER = Provider.KIWI

    // @formatter:off
    val QUERY = "query { " +
      "get_flights(" +
        "parameters: { " +
          "dateFrom: \"$DATE_FROM\", " +
          "dateTo: \"$DATE_TO\", " +
          "flyFrom: \"$FLY_FROM\", " +
          "v: 3, " +
          "partner: \"skypicker\" " +
        "}, " +
        "pagination: { " +
          "limit: $LIMIT, " +
          "offset: $OFFSET " +
        "}, " +
        "providers: [ $PROVIDER ]" +
      ") { " +
        "data { " +
          "id, " +
          "bookingToken: booking_token, " +
          "flyFrom, " +
          "flyTo, " +
          "cityFrom, " +
          "cityTo " +
        "}, " +
        "currency, " +
        "morePending: more_pending " +
      "} " +
    "}"
    // @formatter:on

    val VARIABLES = mapOf<String, String>()

    val STRING_REQUEST = Request(QUERY, VARIABLES)

    // @formatter:off
    val BUILD_REQUEST = RequestBuilder()
      .operation()
      .objectField("get_flights")
        .arguments()
          .objectArgument("parameters")
            .value("dateFrom", DATE_FROM)
            .value("dateTo", DATE_TO)
            .value("flyFrom", FLY_FROM)
            .value("v", 3)
            .value("partner", "skypicker")
            .finish()
          .objectArgument("pagination")
            .value("limit", LIMIT)
            .value("offset", OFFSET)
            .finish()
          .listArgument("providers")
            .value(PROVIDER)
            .finish()
          .finish()
        .objectField("data")
          .field("id")
            .finish()
          .field("booking_token", "bookingToken")
            .finish()
          .field("flyFrom")
            .finish()
          .field("flyTo")
            .finish()
          .field("cityFrom")
            .finish()
          .field("cityTo")
            .finish()
          .finish()
        .field("currency")
           .finish()
        .field("more_pending", "morePending")
           .finish()
        .finish()
      .finish()
      .build()
    // @formatter:on

    val GENERATED_REQUEST = RequestBuilder()
      .operation()
      .fieldsOf<GetFlights>(GetFlightsArguments())
      .finish()
      .build()
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

    @POST("graphql")
    @GraphQL
    fun getFlightsWithoutWrapper(@Arguments arguments: GetFlightsArguments): Observable<GetFlights>
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
      .baseUrl("https://api.skypicker.com/umbrella/")
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
  fun whenGetFlightsWithWrapperAndStringRequestIsRequested_thenSomeDataAreRetrieved() {
    // given

    val observer = TestObserver<Response<GetFlights>>()

    System.out.println(
      "whenSearchWithString_thenSomeDataAreRetrieved(): request: "
        + STRING_REQUEST
    )

    // when

    service.getFlightsWithWrapper(STRING_REQUEST)
      .subscribe(observer)
    observer.awaitTerminalEvent()
    observer.dispose()

    // then

    observer.assertComplete()
    observer.assertNoErrors()

    val response = observer.values()[0]

    assertResponseValid(response)
  }

  @Test
  fun whenGetFlightsWithWrapperAndBuiltRequestIsRequested_thenSomeDataAreRetrieved() {
    // given

    val observer = TestObserver<Response<GetFlights>>()

    System.out.println(
      "whenGetFlightsWithWrapperAndBuiltRequestIsRequested_thenSomeDataAreRetrieved(): request: "
        + BUILD_REQUEST
    )

    assertThat(BUILD_REQUEST.toString())
      .isEqualTo(STRING_REQUEST.toString())

    // when

    service.getFlightsWithWrapper(BUILD_REQUEST)
      .subscribe(observer)
    observer.awaitTerminalEvent()
    observer.dispose()

    // then

    observer.assertComplete()
    observer.assertNoErrors()

    val response = observer.values()[0]

    assertResponseValid(response)
  }

  @Test
  fun whenGetFlightsWithWrapperAndGeneratedRequestIsRequested_thenSomeDataAreRetrieved() {
    // given

    val observer = TestObserver<Response<GetFlights>>()

    System.out.println(
      "whenGetFlightsWithWrapperAndGeneratedRequestIsRequested_thenSomeDataAreRetrieved(): request: "
        + GENERATED_REQUEST
    )

    assertThat(GENERATED_REQUEST.toString())
      .isEqualTo(STRING_REQUEST.toString())
    assertThat(GENERATED_REQUEST.toString())
      .isEqualTo(BUILD_REQUEST.toString())

    // when

    service.getFlightsWithWrapper(GENERATED_REQUEST)
      .subscribe(observer)
    observer.awaitTerminalEvent()
    observer.dispose()

    // then

    observer.assertComplete()
    observer.assertNoErrors()

    val response = observer.values()[0]

    assertResponseValid(response)
  }

  @Test
  @Ignore
  fun whenGetFlightsWithWrapperWithoutRequestIsRequested_thenSomeDataAreRetrieved() {
    // given

    val observer = TestObserver<Response<GetFlights>>()

    // when

    service.getFlightsWithWrapper(GetFlightsArguments())
      .subscribe(observer)
    observer.awaitTerminalEvent()
    observer.dispose()

    // then

    observer.assertComplete()
    observer.assertNoErrors()

    val response = observer.values()[0]

    assertResponseValid(response)
  }

  @Test
  fun whenGetFlightsWithoutWrapperAndStringRequestIsRequested_thenSomeDataAreRetrieved() {
    // given

    val observer = TestObserver<GetFlights>()

    System.out.println(
      "whenGetFlightsWithoutWrapperAndStringRequestIsRequested_thenSomeDataAreRetrieved(): request: " +
        STRING_REQUEST
    )

    // when

    service.getFlightsWithoutWrapper(STRING_REQUEST)
      .subscribe(observer)
    observer.awaitTerminalEvent()
    observer.dispose()

    // then

    observer.assertComplete()
    observer.assertNoErrors()

    val response = observer.values()[0]

    assertGetFlightsValid(response)
  }

  @Test
  fun whenGetFlightsWithoutWrapperAndBuiltRequestIsRequested_thenSomeDataAreRetrieved() {
    // given

    val observer = TestObserver<GetFlights>()

    System.out.println(
      "whenGetFlightsWithoutWrapperAndBuiltRequestIsRequested_thenSomeDataAreRetrieved(): request: "
        + BUILD_REQUEST
    )

    assertThat(BUILD_REQUEST.toString())
      .isEqualTo(STRING_REQUEST.toString())

    // when

    service.getFlightsWithoutWrapper(BUILD_REQUEST)
      .subscribe(observer)
    observer.awaitTerminalEvent()
    observer.dispose()

    // then

    observer.assertComplete()
    observer.assertNoErrors()

    val response = observer.values()[0]

    assertGetFlightsValid(response)
  }

  @Test
  fun whenGetFlightsWithoutWrapperAndGeneratedRequestIsRequested_thenSomeDataAreRetrieved() {
    // given

    val observer = TestObserver<GetFlights>()

    System.out.println(
      "whenGetFlightsWithoutWrapperAndGeneratedRequestIsRequested_thenSomeDataAreRetrieved(): request: "
        + GENERATED_REQUEST
    )

    assertThat(GENERATED_REQUEST.toString())
      .isEqualTo(STRING_REQUEST.toString())
    assertThat(GENERATED_REQUEST.toString())
      .isEqualTo(BUILD_REQUEST.toString())

    // when

    service.getFlightsWithoutWrapper(GENERATED_REQUEST)
      .subscribe(observer)
    observer.awaitTerminalEvent()
    observer.dispose()

    // then

    observer.assertComplete()
    observer.assertNoErrors()

    val response = observer.values()[0]

    assertGetFlightsValid(response)
  }

  @Test
  @Ignore
  fun whenGetFlightsWithoutWrapperWithoutRequestIsRequested_thenSomeDataAreRetrieved() {
    // given

    val observer = TestObserver<GetFlights>()

    // when

    service.getFlightsWithoutWrapper(GetFlightsArguments())
      .subscribe(observer)
    observer.awaitTerminalEvent()
    observer.dispose()

    // then

    observer.assertComplete()
    observer.assertNoErrors()

    val response = observer.values()[0]

    assertGetFlightsValid(response)
  }

  // endregion Public Methods

  // region Private Methods

  private fun assertResponseValid(response: Response<GetFlights>) {
    assertThat(response.data)
      .isNotNull
    assertThat(response.invalid)
      .isFalse()
    assertThat(response.errors)
      .isEmpty()
    assertGetFlightsValid(response.data!!)
  }

  private fun assertGetFlightsValid(response: GetFlights) {
    System.out.println("assertGetFlightsValid(): response: $response")

    assertThat(response.get_flights)
      .isNotNull
    assertThat(response.get_flights.data)
      .isNotEmpty
      .allSatisfy {
        assertThat(it.id).isNotBlank()
        assertThat(it.bookingToken).isNotBlank()
        assertThat(it.flyFrom).isNotBlank()
        assertThat(it.flyTo).isNotBlank()
        assertThat(it.cityFrom).isNotBlank()
        assertThat(it.cityTo).isNotBlank()
      }
    assertThat(response.get_flights.currency)
      .isNotBlank()
  }

  // endregion Private Methods
}
