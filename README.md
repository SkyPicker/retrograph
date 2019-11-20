# Retrograph

Retrofit extension for declarative generation of GrapQL requests.

[![Kotlin](https://img.shields.io/badge/Kotlin-1.3.50-blue.svg)](https://kotlinlang.org/)
[![Retrofit](https://img.shields.io/badge/Retrofit-2.6.1-blue.svg)](https://square.github.io/retrofit/)
[![RxJava](https://img.shields.io/badge/RxJava-2.2.12-blue.svg)](https://github.com/ReactiveX/RxJava)

## Overview

Retrograph is simple yet powerful extension - a call adapter - for a well-known networking library 
[Retrofit](https://square.github.io/retrofit/). It's main purpose is to convert provided plain
(if possible) request/response model data classes and plain request parameters data classes to 
a GraphQL-compliant
[request body](https://graphql.github.io/graphql-spec/June2018/#sec-Language) and then unwrap 
the obtained response data. The actual deserialization can be peformed by any converter factory
attached to the Retrofit and thus the deserialization is not scope of this library.

## Features

 * Simple, lightweight and easy to use extension for Retrofit.
 * Automatic request building using reflection and annotations. 
 * Manual request building using builder pattern.
 * Code coverage of the library is over 75%.

## Planned Features

 * Implicit Request building using Retrofit parameter annotations.
 * Kotlin reflection support.
 * Annotation processor support.
 * Manual request building using Kotlin DSL.
 * Heterogeneous fragment support.
 * Better error response handling.

## Documentation

All the features of the library are describe in form of 
[unit tests](src/test/java/com/kiwi/mobile/retrograph), here we will show some examples of usage.

### Example Model

```kotlin
// Response model classes.
data class Query(
  val search: SearchResultItemConnection
)
data class SearchResultItemConnection(
  @field:InlineFragment
  val nodes: List<Repository>
)
data class Repository(
  val id: String,

  @field:Alias("name")
  val repositoryName: String
)
enum class Type {
  REPOSITORY
}

// Request arguments model classes.
data class QueryArguments(
  val search: SearchArguments
)
data class SearchArguments(
  val query: String,
  val type: Type,
  val first: Int
)
```

### Retrofit Request Service Setup

```kotlin
// Retrofit GitHub request service definition.
interface GitHubRequestService {
  @POST("graphql")
  @GraphQL
  @Headers("Authorization: bearer $GIT_HUB_TOKEN")
  fun query(@Body request: Request): Single<Query>
}

// Retrofit GitHub request service instance.
val service = Retrofit.Builder()
  .baseUrl("https://api.github.com/")
  .addConverterFactory(GsonConverterFactory.create())
  .addCallAdapterFactory(GraphQLCallAdapterFactory.create())
  .build()
  .create<GitHubRequestService>()
```

### Automatic Request Building using Reflection

```kotlin
// GraphQL request construction.
val request = RequestBuilder()
  .operation()
  .fieldsOf<Query>(
    QueryArguments(
      search = SearchArguments(
        query = "Test",
        type = Type.REPOSITORY,
        first = 10
      )
    )
  )
  .finish()
  .build()

// Performing the request to obtain response data.
val response = service.query(request).blockingGet()
```

### Manual Request Building using Builder

```kotlin
// GraphQL request construction.
val request = RequestBuilder()
  .operation()
  .objectField("search")
    .arguments()
      .argument("query", "Test")
      .argument("type", Type.REPOSITORY)
      .argument("first", 10)
      .finish()
    .objectField("nodes")
      .inlineFragment("Repository")
        .field("id")
        .field("name", "repositoryName")
        .finish()
      .finish()
    .finish()
  .finish()
  .build()

// Performing the request to obtain response data.
val response = service.query(request).blockingGet()
```

## Authors

- [Radek Bartoň](https://github.com/Blackhex)
