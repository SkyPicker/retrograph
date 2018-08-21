package com.kiwi.mobile.retrograph.util

import io.reactivex.*

abstract class ForwardingCompletableObserver(delegate: CompletableObserver):
  CompletableObserver by delegate
