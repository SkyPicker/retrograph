package com.kiwi.mobile.retrograph.rule

import io.reactivex.plugins.*

import org.junit.rules.*
import org.junit.runner.*
import org.junit.runners.model.*

class RxJavaPluginsResetRule:
  TestRule {

  override fun apply(base: Statement, description: Description) =
    object: Statement() {

      @Throws(Throwable::class)
      override fun evaluate() {
        RxJavaPlugins.reset()
        try {
          base.evaluate()
        } finally {
          RxJavaPlugins.reset()
        }
      }
    }
}
