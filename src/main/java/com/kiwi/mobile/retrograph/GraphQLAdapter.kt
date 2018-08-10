package com.kiwi.mobile.retrograph

import com.google.gson.*

import java.lang.reflect.*

class GraphQLAdapter:
  JsonDeserializer<GraphQLResponse<*>> {

  @Throws(JsonParseException::class)
  override fun deserialize(
    json: JsonElement,
    typeOfT: Type,
    context: JsonDeserializationContext
  ) =
    if (json.isJsonObject) {
      val response = json.asJsonObject
      response.entrySet()
        .map { Pair(it.key, response.get(it.key)) }
        .filter { it.first != "data" || it.second.isJsonObject }
        .map {
          when (it.first) {
            "errors" -> GraphQLResponse<Any>(
              null, context.deserialize(it.second, Array<String>::class.java)
            )
            "data" -> {
              val entry = it.second.asJsonObject
                .entrySet()
                .iterator()
                .next()
              val typeArgument = (typeOfT as ParameterizedType).actualTypeArguments[0]

              GraphQLResponse(
                context.deserialize<Any>(entry.value, typeArgument), null
              )
            }
            else -> GraphQLResponse<Any>(null, null)
          }
        }
        .firstOrNull() ?: GraphQLResponse<Any>(null, null)
    } else {
      GraphQLResponse<Any>(null, null)
    }
}
