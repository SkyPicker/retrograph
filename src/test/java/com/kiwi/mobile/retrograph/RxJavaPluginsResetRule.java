package com.kiwi.mobile.retrograph;

import org.junit.rules.*;
import org.junit.runner.*;
import org.junit.runners.model.*;

import io.reactivex.plugins.*;

final class RxJavaPluginsResetRule implements TestRule {
  @Override
  public Statement apply(final Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        RxJavaPlugins.reset();
        try {
          base.evaluate();
        } finally {
          RxJavaPlugins.reset();
        }
      }
    };
  }
}
