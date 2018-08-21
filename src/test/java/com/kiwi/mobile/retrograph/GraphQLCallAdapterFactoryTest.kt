package com.kiwi.mobile.retrograph

import com.google.common.reflect.*

import com.kiwi.mobile.retrograph.annotation.*
import com.kiwi.mobile.retrograph.converter.*
import com.kiwi.mobile.retrograph.model.*

import io.reactivex.*

import org.assertj.core.api.Assertions.*

import org.junit.*
import org.junit.Assert.*

import retrofit2.*
import retrofit2.adapter.rxjava2.*

import kotlin.reflect.full.*

import com.kiwi.mobile.retrograph.model.Response as GraphQLResponse

import retrofit2.Response as RetrofitResponse

class GraphQLCallAdapterFactoryTest {

  // region Private Types

  private companion object {

    private val NO_ANNOTATIONS = arrayOf<Annotation>()

    private val GRAPH_QL_ANNOTATIONS = arrayOf<Annotation>(
      GraphQL::class.createInstance()
    )
  }

  // endregion Private Types

  // region Private Properties

  private val factory = GraphQLCallAdapterFactory.create()
  private lateinit var retrofit: Retrofit

  // endregion Private Properties

  // region Public Methods

  @Before
  fun setUp() {
    retrofit = Retrofit.Builder()
      .baseUrl("http://localhost:1")
      .addConverterFactory(StringConverterFactory())
      .addCallAdapterFactory(factory)
      .build()
  }

  @Test
  fun nonGraphQLAnnotationReturnsNull() {
    val observableType = object: TypeToken<Observable<String>>() {}.type
    val adapter = factory.get(observableType, NO_ANNOTATIONS, retrofit)
    assertThat(adapter)
      .isNull()
  }

  @Test
  fun nonRxJavaTypeReturnsNull() {
    val adapter = factory.get(String::class.java, GRAPH_QL_ANNOTATIONS, retrofit)
    assertThat(adapter)
      .isNull()
  }

  @Test
  fun bodyResponseTypes() {
    val observableClass = object: TypeToken<Observable<String>>() {}.type
    val adapter = factory.get(observableClass, GRAPH_QL_ANNOTATIONS, retrofit)!!
    assertThat(adapter.responseType())
      .isEqualTo(ResponseType(String::class.java))

    val singleClass = object: TypeToken<Single<String>>() {}.type
    assertThat(factory.get(singleClass, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(ResponseType(String::class.java))

    val maybeClass = object: TypeToken<Maybe<String>>() {}.type
    assertThat(factory.get(maybeClass, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(ResponseType(String::class.java))

    val flowableClass = object: TypeToken<Flowable<String>>() {}.type
    assertThat(factory.get(flowableClass, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(ResponseType(String::class.java))

    val observableWildcard = object: TypeToken<Observable<out String>>() {}.type
    assertThat(factory.get(observableWildcard, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(ResponseType(String::class.java))

    val singleWildcard = object: TypeToken<Single<out String>>() {}.type
    assertThat(factory.get(singleWildcard, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(ResponseType(String::class.java))

    val maybeWildcard = object: TypeToken<Maybe<out String>>() {}.type
    assertThat(factory.get(maybeWildcard, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(ResponseType(String::class.java))

    val flowableWildcard = object: TypeToken<Flowable<out String>>() {}.type
    assertThat(factory.get(flowableWildcard, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(ResponseType(String::class.java))

    val observableGeneric = object: TypeToken<Observable<List<String>>>() {}.type
    assertThat(factory.get(observableGeneric, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(ResponseType(object: TypeToken<List<String>>() {}.type))

    val singleGeneric = object: TypeToken<Single<List<String>>>() {}.type
    assertThat(factory.get(singleGeneric, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(ResponseType(object: TypeToken<List<String>>() {}.type))

    val maybeGeneric = object: TypeToken<Maybe<List<String>>>() {}.type
    assertThat(factory.get(maybeGeneric, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(ResponseType(object: TypeToken<List<String>>() {}.type))

    val flowableGeneric = object: TypeToken<Flowable<List<String>>>() {}.type
    assertThat(factory.get(flowableGeneric, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(ResponseType(object: TypeToken<List<String>>() {}.type))
  }

  @Test
  fun graphQLResponseResponseType() {
    val observableClass = object: TypeToken<Observable<GraphQLResponse<String>>>() {}.type
    assertThat(factory.get(observableClass, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(object: TypeToken<GraphQLResponse<String>>() {}.type)

    val singleClass = object: TypeToken<Single<GraphQLResponse<String>>>() {}.type
    assertThat(factory.get(singleClass, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(object: TypeToken<GraphQLResponse<String>>() {}.type)

    val maybeClass = object: TypeToken<Maybe<GraphQLResponse<String>>>() {}.type
    assertThat(factory.get(maybeClass, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(object: TypeToken<GraphQLResponse<String>>() {}.type)

    val flowableClass = object: TypeToken<Flowable<GraphQLResponse<String>>>() {}.type
    assertThat(factory.get(flowableClass, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(object: TypeToken<GraphQLResponse<String>>() {}.type)

    val observableWildcard = object: TypeToken<Observable<GraphQLResponse<out String>>>() {}.type
    assertThat(factory.get(observableWildcard, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(object: TypeToken<GraphQLResponse<out String>>() {}.type)

    val singleWildcard = object: TypeToken<Single<GraphQLResponse<out String>>>() {}.type
    assertThat(factory.get(singleWildcard, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(object: TypeToken<GraphQLResponse<out String>>() {}.type)

    val maybeGraphQLResponseWildcard = object: TypeToken<Maybe<GraphQLResponse<out String>>>() {}.type
    assertThat(
      factory.get(maybeGraphQLResponseWildcard, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(object: TypeToken<GraphQLResponse<out String>>() {}.type)

    val flowableWildcard = object: TypeToken<Flowable<GraphQLResponse<out String>>>() {}.type
    assertThat(factory.get(flowableWildcard, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(object: TypeToken<GraphQLResponse<out String>>() {}.type)

    val observableGeneric = object: TypeToken<Observable<GraphQLResponse<List<String>>>>() {}.type
    assertThat(factory.get(observableGeneric, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(object: TypeToken<GraphQLResponse<List<String>>>() {}.type)

    val singleGeneric = object: TypeToken<Single<GraphQLResponse<List<String>>>>() {}.type
    assertThat(factory.get(singleGeneric, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(object: TypeToken<GraphQLResponse<List<String>>>() {}.type)

    val maybeGeneric = object: TypeToken<Maybe<GraphQLResponse<List<String>>>>() {}.type
    assertThat(factory.get(maybeGeneric, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(object: TypeToken<GraphQLResponse<List<String>>>() {}.type)

    val flowableGeneric = object: TypeToken<Flowable<GraphQLResponse<List<String>>>>() {}.type
    assertThat(factory.get(flowableGeneric, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(object: TypeToken<GraphQLResponse<List<String>>>() {}.type)
  }

  @Test
  fun retrofitResponseResponseType() {
    val observableClass = object: TypeToken<Observable<RetrofitResponse<String>>>() {}.type
    assertThat(factory.get(observableClass, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(ResponseType(String::class.java))

    val singleClass = object: TypeToken<Single<RetrofitResponse<String>>>() {}.type
    assertThat(factory.get(singleClass, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(ResponseType(String::class.java))

    val maybeClass = object: TypeToken<Maybe<RetrofitResponse<String>>>() {}.type
    assertThat(factory.get(maybeClass, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(ResponseType(String::class.java))

    val flowableClass = object: TypeToken<Flowable<RetrofitResponse<String>>>() {}.type
    assertThat(factory.get(flowableClass, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(ResponseType(String::class.java))

    val observableWildcard = object: TypeToken<Observable<RetrofitResponse<out String>>>() {}.type
    assertThat(factory.get(observableWildcard, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(ResponseType(String::class.java))

    val singleWildcard = object: TypeToken<Single<RetrofitResponse<out String>>>() {}.type
    assertThat(factory.get(singleWildcard, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(ResponseType(String::class.java))

    val maybeWildcard = object: TypeToken<Maybe<RetrofitResponse<out String>>>() {}.type
    assertThat(factory.get(maybeWildcard, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(ResponseType(String::class.java))

    val flowableWildcard = object: TypeToken<Flowable<RetrofitResponse<out String>>>() {}.type
    assertThat(factory.get(flowableWildcard, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(ResponseType(String::class.java))
  }

  @Test
  fun retrofitResponseGraphQLResponseResponseType() {
    val observableClass = object: TypeToken<Observable<RetrofitResponse<GraphQLResponse<String>>>>() {}.type
    assertThat(factory.get(observableClass, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(object: TypeToken<GraphQLResponse<String>>() {}.type)

    val singleClass = object: TypeToken<Single<RetrofitResponse<GraphQLResponse<String>>>>() {}.type
    assertThat(factory.get(singleClass, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(object: TypeToken<GraphQLResponse<String>>() {}.type)

    val maybeClass = object: TypeToken<Maybe<RetrofitResponse<GraphQLResponse<String>>>>() {}.type
    assertThat(factory.get(maybeClass, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(object: TypeToken<GraphQLResponse<String>>() {}.type)

    val flowableClass = object: TypeToken<Flowable<RetrofitResponse<GraphQLResponse<String>>>>() {}.type
    assertThat(factory.get(flowableClass, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(object: TypeToken<GraphQLResponse<String>>() {}.type)

    val observableWildcard = object: TypeToken<Observable<RetrofitResponse<GraphQLResponse<out String>>>>() {}.type
    assertThat(factory.get(observableWildcard, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(object: TypeToken<GraphQLResponse<out String>>() {}.type)

    val singleWildcard = object: TypeToken<Single<RetrofitResponse<GraphQLResponse<out String>>>>() {}.type
    assertThat(factory.get(singleWildcard, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(object: TypeToken<GraphQLResponse<out String>>() {}.type)

    val maybeWildcard = object: TypeToken<Maybe<RetrofitResponse<GraphQLResponse<out String>>>>() {}.type
    assertThat(factory.get(maybeWildcard, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(object: TypeToken<GraphQLResponse<out String>>() {}.type)

    val flowableWildcard = object: TypeToken<Flowable<RetrofitResponse<GraphQLResponse<out String>>>>() {}.type
    assertThat(factory.get(flowableWildcard, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(object: TypeToken<GraphQLResponse<out String>>() {}.type)
  }

  @Test
  fun resultResponseTypes() {
    val observableClass = object: TypeToken<Observable<Result<String>>>() {}.type
    assertThat(factory.get(observableClass, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(ResponseType(String::class.java))

    val singleClass = object: TypeToken<Single<Result<String>>>() {}.type
    assertThat(factory.get(singleClass, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(ResponseType(String::class.java))

    val maybeClass = object: TypeToken<Maybe<Result<String>>>() {}.type
    assertThat(factory.get(maybeClass, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(ResponseType(String::class.java))

    val flowableClass = object: TypeToken<Flowable<Result<String>>>() {}.type
    assertThat(factory.get(flowableClass, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(ResponseType(String::class.java))

    val observableWildcard = object: TypeToken<Observable<Result<out String>>>() {}.type
    assertThat(factory.get(observableWildcard, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(ResponseType(String::class.java))

    val singleWildcard = object: TypeToken<Single<Result<out String>>>() {}.type
    assertThat(factory.get(singleWildcard, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(ResponseType(String::class.java))

    val maybeWildcard = object: TypeToken<Maybe<Result<out String>>>() {}.type
    assertThat(factory.get(maybeWildcard, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(ResponseType(String::class.java))

    val flowableWildcard = object: TypeToken<Flowable<Result<out String>>>() {}.type
    assertThat(factory.get(flowableWildcard, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(ResponseType(String::class.java))
  }

  @Test
  fun resultResponseGraphQLResponseTypes() {
    val observableClass = object: TypeToken<Observable<Result<GraphQLResponse<String>>>>() {}.type
    assertThat(factory.get(observableClass, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(object: TypeToken<GraphQLResponse<String>>() {}.type)

    val singleClass = object: TypeToken<Single<Result<GraphQLResponse<String>>>>() {}.type
    assertThat(factory.get(singleClass, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(object: TypeToken<GraphQLResponse<String>>() {}.type)

    val maybeClass = object: TypeToken<Maybe<Result<GraphQLResponse<String>>>>() {}.type
    assertThat(factory.get(maybeClass, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(object: TypeToken<GraphQLResponse<String>>() {}.type)

    val flowableClass = object: TypeToken<Flowable<Result<GraphQLResponse<String>>>>() {}.type
    assertThat(factory.get(flowableClass, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(object: TypeToken<GraphQLResponse<String>>() {}.type)

    val observableWildcard = object: TypeToken<Observable<Result<GraphQLResponse<out String>>>>() {}.type
    assertThat(factory.get(observableWildcard, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(object: TypeToken<GraphQLResponse<out String>>() {}.type)

    val singleWildcard = object: TypeToken<Single<Result<GraphQLResponse<out String>>>>() {}.type
    assertThat(factory.get(singleWildcard, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(object: TypeToken<GraphQLResponse<out String>>() {}.type)

    val maybeWildcard = object: TypeToken<Maybe<Result<GraphQLResponse<out String>>>>() {}.type
    assertThat(factory.get(maybeWildcard, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(object: TypeToken<GraphQLResponse<out String>>() {}.type)

    val flowableWildcard = object: TypeToken<Flowable<Result<GraphQLResponse<out String>>>>() {}.type
    assertThat(factory.get(flowableWildcard, GRAPH_QL_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(object: TypeToken<GraphQLResponse<out String>>() {}.type)
  }

  @Test
  fun rawBodyTypeThrows() {
    val observableType = object: TypeToken<Observable<*>>() {}.type
    try {
      factory.get(observableType, GRAPH_QL_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage(
          "Observable return type must be parameterized as Observable<Foo> or Observable<? extends Foo>"
        )
    }

    val singleType = object: TypeToken<Single<*>>() {}.type
    try {
      factory.get(singleType, GRAPH_QL_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage(
          "Single return type must be parameterized as Single<Foo> or Single<? extends Foo>"
        )
    }

    val maybeType = object: TypeToken<Maybe<*>>() {}.type
    try {
      factory.get(maybeType, GRAPH_QL_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage(
          "Maybe return type must be parameterized as Maybe<Foo> or Maybe<? extends Foo>"
        )
    }

    val flowableType = object: TypeToken<Flowable<*>>() {}.type
    try {
      factory.get(flowableType, GRAPH_QL_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage(
          "Flowable return type must be parameterized as Flowable<Foo> or Flowable<? extends Foo>"
        )
    }
  }

  @Test
  fun rawGraphQLResponseTypeThrows() {
    val observableType = object: TypeToken<Observable<GraphQLResponse<*>>>() {}.type
    try {
      factory.get(observableType, GRAPH_QL_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage(
          "Response must be parameterized as Response<Foo> or Response<? extends Foo>"
        )
    }

    val singleType = object: TypeToken<Single<GraphQLResponse<*>>>() {}.type
    try {
      factory.get(singleType, GRAPH_QL_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage(
          "Response must be parameterized as Response<Foo> or Response<? extends Foo>"
        )
    }

    val maybeType = object: TypeToken<Maybe<GraphQLResponse<*>>>() {}.type
    try {
      factory.get(maybeType, GRAPH_QL_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage(
          "Response must be parameterized as Response<Foo> or Response<? extends Foo>"
        )
    }

    val flowableType = object: TypeToken<Flowable<GraphQLResponse<*>>>() {}.type
    try {
      factory.get(flowableType, GRAPH_QL_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage(
          "Response must be parameterized as Response<Foo> or Response<? extends Foo>"
        )
    }
  }

  @Test
  fun rawRetrofitResponseTypeThrows() {
    val observableType = object: TypeToken<Observable<RetrofitResponse<*>>>() {}.type
    try {
      factory.get(observableType, GRAPH_QL_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage("Response must be parameterized as Response<Foo> or Response<? extends Foo>")
    }

    val singleType = object: TypeToken<Single<RetrofitResponse<*>>>() {}.type
    try {
      factory.get(singleType, GRAPH_QL_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage("Response must be parameterized as Response<Foo> or Response<? extends Foo>")
    }

    val maybeType = object: TypeToken<Maybe<RetrofitResponse<*>>>() {}.type
    try {
      factory.get(maybeType, GRAPH_QL_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage("Response must be parameterized as Response<Foo> or Response<? extends Foo>")
    }

    val flowableType = object: TypeToken<Flowable<RetrofitResponse<*>>>() {}.type
    try {
      factory.get(flowableType, GRAPH_QL_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage("Response must be parameterized as Response<Foo> or Response<? extends Foo>")
    }
  }

  @Test
  fun rawRetrofitResponseGraphQLResponseTypeThrows() {
    val observableType = object: TypeToken<Observable<RetrofitResponse<GraphQLResponse<*>>>>() {}.type
    try {
      factory.get(observableType, GRAPH_QL_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage(
          "Response must be parameterized as Response<Response<Foo>> or Response<Response<? extends Foo>>"
        )
    }

    val singleType = object: TypeToken<Single<RetrofitResponse<GraphQLResponse<*>>>>() {}.type
    try {
      factory.get(singleType, GRAPH_QL_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage(
          "Response must be parameterized as Response<Response<Foo>> or Response<Response<? extends Foo>>"
        )
    }

    val maybeType = object: TypeToken<Maybe<RetrofitResponse<GraphQLResponse<*>>>>() {}.type
    try {
      factory.get(maybeType, GRAPH_QL_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage(
          "Response must be parameterized as Response<Response<Foo>> or Response<Response<? extends Foo>>"
        )
    }

    val flowableType = object: TypeToken<Flowable<RetrofitResponse<GraphQLResponse<*>>>>() {}.type
    try {
      factory.get(flowableType, GRAPH_QL_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage(
          "Response must be parameterized as Response<Response<Foo>> or Response<Response<? extends Foo>>"
        )
    }
  }

  @Test
  fun rawResultTypeThrows() {
    val observableType = object: TypeToken<Observable<Result<*>>>() {}.type
    try {
      factory.get(observableType, GRAPH_QL_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage(
          "Result must be parameterized as Result<Foo> or Result<? extends Foo>"
        )
    }

    val singleType = object: TypeToken<Single<Result<*>>>() {}.type
    try {
      factory.get(singleType, GRAPH_QL_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage(
          "Result must be parameterized as Result<Foo> or Result<? extends Foo>"
        )
    }

    val maybeType = object: TypeToken<Maybe<Result<*>>>() {}.type
    try {
      factory.get(maybeType, GRAPH_QL_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage(
          "Result must be parameterized as Result<Foo> or Result<? extends Foo>"
        )
    }

    val flowableType = object: TypeToken<Flowable<Result<*>>>() {}.type
    try {
      factory.get(flowableType, GRAPH_QL_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage(
          "Result must be parameterized as Result<Foo> or Result<? extends Foo>"
        )
    }
  }

  @Test
  fun rawResultGraphQLResponseTypeThrows() {
    val observableType = object: TypeToken<Observable<Result<GraphQLResponse<*>>>>() {}.type
    try {
      factory.get(observableType, GRAPH_QL_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage(
          "Result must be parameterized as Result<Response<Foo>> or Result<Response<? extends Foo>>"
        )
    }

    val singleType = object: TypeToken<Single<Result<GraphQLResponse<*>>>>() {}.type
    try {
      factory.get(singleType, GRAPH_QL_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage(
          "Result must be parameterized as Result<Response<Foo>> or Result<Response<? extends Foo>>"
        )
    }

    val maybeType = object: TypeToken<Maybe<Result<GraphQLResponse<*>>>>() {}.type
    try {
      factory.get(maybeType, GRAPH_QL_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage(
          "Result must be parameterized as Result<Response<Foo>> or Result<Response<? extends Foo>>"
        )
    }

    val flowableType = object: TypeToken<Flowable<Result<GraphQLResponse<*>>>>() {}.type
    try {
      factory.get(flowableType, GRAPH_QL_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage(
          "Result must be parameterized as Result<Response<Foo>> or Result<Response<? extends Foo>>"
        )
    }
  }

  // endregion Public Methods
}
