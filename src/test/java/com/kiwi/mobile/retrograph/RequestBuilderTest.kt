package com.kiwi.mobile.retrograph

import com.kiwi.mobile.retrograph.model.*

import org.assertj.core.api.Assertions.*

import org.junit.*

class RequestBuilderTest {

  // region Private Types

  private enum class Provider {
    KIWI
  }

  // endregion Private Types

  // region Public Methods

  @Before
  fun setUp() {
  }

  @Test
  fun whenOperationIsQuery_thenSerialized() {
    val request = RequestBuilder()
      .operation(Operation.Type.QUERY)
      .finish()
      .build()

    assertThat(request.query)
      .isEqualTo("query  {  }")
    assertThat(request.variables)
      .isEqualTo("{}")
  }

  @Test
  fun whenOperationIsSubscription_thenSerialized() {
    val request = RequestBuilder()
      .operation(Operation.Type.SUBSCRIPTION)
      .finish()
      .build()

    assertThat(request.query)
      .isEqualTo("subscription  {  }")
    assertThat(request.variables)
      .isEqualTo("{}")
  }

  @Test
  fun whenOperationIsMutation_thenSerialized() {
    val request = RequestBuilder()
      .operation(Operation.Type.MUTATION)
      .finish()
      .build()

    assertThat(request.query)
      .isEqualTo("mutation  {  }")
    assertThat(request.variables)
      .isEqualTo("{}")
  }

  @Test
  fun whenOperationNotSpecified_thenDefaultSerialized() {
    val builder = RequestBuilder()
    val request = builder.build()

    assertThat(builder.operation())
      .isInstanceOf(Operation::class.java)
    assertThat(request.query)
      .isEqualTo("query  {  }")
    assertThat(request.variables)
      .isEqualTo("{}")
  }

  @Test
  fun whenOperationNameSpecified_thenSerialized() {
    val request = RequestBuilder()
      .operation(Operation.Type.QUERY, "test")
      .finish()
      .build()

    assertThat(request.query)
      .isEqualTo("query test {  }")
    assertThat(request.variables)
      .isEqualTo("{}")
  }

  @Test
  fun whenVariablesAdded_thenSerialized() {
    val request = RequestBuilder()
      .variable("int", 123456)
      .variable("long", 123456789123456789L)
      .variable("float", 2.5f)
      .variable("double", 33.3)
      .variable("boolean", true)
      .variable("object", Pair("a", "b"))
      .build()

    assertThat(request.query)
      .isEqualTo("query  {  }")
    assertThat(request.variables)
      .isEqualTo(
        """{"int":123456,"long":123456789123456789,"float":2.5,"double":33.3,"boolean":true,"object":{"first":"a","second":"b"}}"""
      )
  }

  @Test
  fun whenExampleQueryIsCreated_thenSerialized() {
    // @formatter:off

    val request = RequestBuilder()
      .operation(Operation.Type.QUERY)
        .objectField("get_flights")
          .arguments()
            .argument("providers", Provider.KIWI)
            .objectArgument("parameters")
               .value("flyFrom", "DEN")
               .value("to", "FRA")
               .value("dateFrom", "20/04/2018")
               .value("dateTo", "20/05/2018")
            .finish()
            .objectArgument("pagination")
              .value("offset", 0)
              .value("limit", 5)
            .finish()
          .finish()
          .objectField("data")
            .field("id")
            .field("price")
            .finish()
          .finish()
        .finish()
      .build()

    // @formatter:on

    assertThat(request.query)
      .isEqualTo(
        // @formatter:off
        "query  { "
          + "get_flights( "
            + "providers: KIWI, "
            + "parameters: { "
              + "flyFrom: \"DEN\", "
              + "to: \"FRA\", "
              + "dateFrom: \"20/04/2018\", "
              + "dateTo: \"20/05/2018\" "
            + "}, "
            + "pagination: { "
              + "offset: 0, "
              + "limit: 5 "
            + "} "
          + ") { "
            + "data { "
              + "id, "
              + "price "
            + "} "
          + "} "
        + "}"
        // @formatter:on
      )
    assertThat(request.variables)
      .isEqualTo("{}")
  }

  // endregion Public Methods

}
