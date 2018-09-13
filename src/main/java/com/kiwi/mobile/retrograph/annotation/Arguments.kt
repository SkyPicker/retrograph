package com.kiwi.mobile.retrograph.annotation

import kotlin.reflect.*

annotation class Arguments(val value: KClass<*> = Any::class)