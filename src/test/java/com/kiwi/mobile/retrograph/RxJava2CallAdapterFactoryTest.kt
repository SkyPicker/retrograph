package com.kiwi.mobile.retrograph

import com.google.common.reflect.*
import com.kiwi.mobile.retrograph.converter.*

import io.reactivex.*

import org.assertj.core.api.Assertions.*

import org.junit.*
import org.junit.Assert.*

import retrofit2.*
import retrofit2.adapter.rxjava2.*

class RxJava2CallAdapterFactoryTest {

  // region Private Types

  private companion object {

    private val NO_ANNOTATIONS = arrayOf<Annotation>()
  }

  // endregion Private Types

  // region Private Properties

  private val factory = RxJava2CallAdapterFactory.create()
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
  fun nonRxJavaTypeReturnsNull() {
    val adapter = factory.get(String::class.java, NO_ANNOTATIONS, retrofit)
    assertThat(adapter)
      .isNull()
  }

  @Test
  fun responseTypes() {
    val observableBodyClass = object: TypeToken<Observable<String>>() {}.type
    assertThat(factory.get(observableBodyClass, NO_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(String::class.java)

    val singleBodyClass = object: TypeToken<Single<String>>() {}.type
    assertThat(factory.get(singleBodyClass, NO_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(String::class.java)

    val maybeBodyClass = object: TypeToken<Maybe<String>>() {}.type
    assertThat(factory.get(maybeBodyClass, NO_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(String::class.java)

    val flowableBodyClass = object: TypeToken<Flowable<String>>() {}.type
    assertThat(factory.get(flowableBodyClass, NO_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(String::class.java)

    val observableBodyWildcard = object: TypeToken<Observable<out String>>() {}.type
    assertThat(factory.get(observableBodyWildcard, NO_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(String::class.java)

    val singleBodyWildcard = object: TypeToken<Single<out String>>() {}.type
    assertThat(factory.get(singleBodyWildcard, NO_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(String::class.java)

    val maybeBodyWildcard = object: TypeToken<Maybe<out String>>() {}.type
    assertThat(factory.get(maybeBodyWildcard, NO_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(String::class.java)

    val flowableBodyWildcard = object: TypeToken<Flowable<out String>>() {}.type
    assertThat(factory.get(flowableBodyWildcard, NO_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(String::class.java)

    val observableBodyGeneric = object: TypeToken<Observable<List<String>>>() {}.type
    assertThat(factory.get(observableBodyGeneric, NO_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(object: TypeToken<List<String>>() {}.type)

    val singleBodyGeneric = object: TypeToken<Single<List<String>>>() {}.type
    assertThat(factory.get(singleBodyGeneric, NO_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(object: TypeToken<List<String>>() {}.type)

    val maybeBodyGeneric = object: TypeToken<Maybe<List<String>>>() {}.type
    assertThat(factory.get(maybeBodyGeneric, NO_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(object: TypeToken<List<String>>() {}.type)

    val flowableBodyGeneric = object: TypeToken<Flowable<List<String>>>() {}.type
    assertThat(factory.get(flowableBodyGeneric, NO_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(object: TypeToken<List<String>>() {}.type)

    val oservableResponseClass = object: TypeToken<Observable<Response<String>>>() {}.type
    assertThat(factory.get(oservableResponseClass, NO_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(String::class.java)

    val singleResponseClass = object: TypeToken<Single<Response<String>>>() {}.type
    assertThat(factory.get(singleResponseClass, NO_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(String::class.java)

    val maybeResponseClass = object: TypeToken<Maybe<Response<String>>>() {}.type
    assertThat(factory.get(maybeResponseClass, NO_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(String::class.java)

    val flowableResponseClass = object: TypeToken<Flowable<Response<String>>>() {}.type
    assertThat(factory.get(flowableResponseClass, NO_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(String::class.java)

    val observableResponseWildcard = object: TypeToken<Observable<Response<out String>>>() {}.type
    assertThat(factory.get(observableResponseWildcard, NO_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(String::class.java)

    val singleResponseWildcard = object: TypeToken<Single<Response<out String>>>() {}.type
    assertThat(factory.get(singleResponseWildcard, NO_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(String::class.java)

    val maybeResponseWildcard = object: TypeToken<Maybe<Response<out String>>>() {}.type
    assertThat(factory.get(maybeResponseWildcard, NO_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(String::class.java)

    val flowableResponseWildcard = object: TypeToken<Flowable<Response<out String>>>() {}.type
    assertThat(factory.get(flowableResponseWildcard, NO_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(String::class.java)

    val observableResultClass = object: TypeToken<Observable<Result<String>>>() {}.type
    assertThat(factory.get(observableResultClass, NO_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(String::class.java)

    val singleResultClass = object: TypeToken<Single<Result<String>>>() {}.type
    assertThat(factory.get(singleResultClass, NO_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(String::class.java)

    val maybeResultClass = object: TypeToken<Maybe<Result<String>>>() {}.type
    assertThat(factory.get(maybeResultClass, NO_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(String::class.java)

    val flowableResultClass = object: TypeToken<Flowable<Result<String>>>() {}.type
    assertThat(factory.get(flowableResultClass, NO_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(String::class.java)

    val observableResultWildcard = object: TypeToken<Observable<Result<out String>>>() {}.type
    assertThat(factory.get(observableResultWildcard, NO_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(String::class.java)

    val singleResultWildcard = object: TypeToken<Single<Result<out String>>>() {}.type
    assertThat(factory.get(singleResultWildcard, NO_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(String::class.java)

    val maybeResultWildcard = object: TypeToken<Maybe<Result<out String>>>() {}.type
    assertThat(factory.get(maybeResultWildcard, NO_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(String::class.java)

    val flowableResultWildcard = object: TypeToken<Flowable<Result<out String>>>() {}.type
    assertThat(factory.get(flowableResultWildcard, NO_ANNOTATIONS, retrofit)!!.responseType())
      .isEqualTo(String::class.java)
  }

  @Test
  fun rawBodyTypeThrows() {
    val observableType = object: TypeToken<Observable<*>>() {}.type
    try {
      factory.get(observableType, NO_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage(
          "Observable return type must be parameterized as Observable<Foo> or Observable<? extends Foo>"
        )
    }

    val singleType = object: TypeToken<Single<*>>() {}.type
    try {
      factory.get(singleType, NO_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage(
          "Single return type must be parameterized as Single<Foo> or Single<? extends Foo>"
        )
    }

    val maybeType = object: TypeToken<Maybe<*>>() {}.type
    try {
      factory.get(maybeType, NO_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage(
          "Maybe return type must be parameterized as Maybe<Foo> or Maybe<? extends Foo>"
        )
    }

    val flowableType = object: TypeToken<Flowable<*>>() {}.type
    try {
      factory.get(flowableType, NO_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage(
          "Flowable return type must be parameterized as Flowable<Foo> or Flowable<? extends Foo>"
        )
    }
  }

  @Test
  fun rawResponseTypeThrows() {
    val observableType = object: TypeToken<Observable<Response<*>>>() {}.type
    try {
      factory.get(observableType, NO_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage("Response must be parameterized as Response<Foo> or Response<? extends Foo>")
    }

    val singleType = object: TypeToken<Single<Response<*>>>() {}.type
    try {
      factory.get(singleType, NO_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage("Response must be parameterized as Response<Foo> or Response<? extends Foo>")
    }

    val maybeType = object: TypeToken<Maybe<Response<*>>>() {}.type
    try {
      factory.get(maybeType, NO_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage("Response must be parameterized as Response<Foo> or Response<? extends Foo>")
    }

    val flowableType = object: TypeToken<Flowable<Response<*>>>() {}.type
    try {
      factory.get(flowableType, NO_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage("Response must be parameterized as Response<Foo> or Response<? extends Foo>")
    }
  }

  @Test
  fun rawResultTypeThrows() {
    val observableType = object: TypeToken<Observable<Result<*>>>() {}.type
    try {
      factory.get(observableType, NO_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage("Result must be parameterized as Result<Foo> or Result<? extends Foo>")
    }

    val singleType = object: TypeToken<Single<Result<*>>>() {}.type
    try {
      factory.get(singleType, NO_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage("Result must be parameterized as Result<Foo> or Result<? extends Foo>")
    }

    val maybeType = object: TypeToken<Maybe<Result<*>>>() {}.type
    try {
      factory.get(maybeType, NO_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage("Result must be parameterized as Result<Foo> or Result<? extends Foo>")
    }

    val flowableType = object: TypeToken<Flowable<Result<*>>>() {}.type
    try {
      factory.get(flowableType, NO_ANNOTATIONS, retrofit)
      fail()
    } catch (exception: IllegalStateException) {
      assertThat(exception)
        .hasMessage("Result must be parameterized as Result<Foo> or Result<? extends Foo>")
    }
  }

  // endregion Public Methods
}
