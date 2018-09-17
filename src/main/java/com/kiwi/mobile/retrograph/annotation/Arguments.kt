package com.kiwi.mobile.retrograph.annotation

import kotlin.reflect.*

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Arguments(val value: KClass<*> = Any::class)
