package com.kiwi.mobile.retrograph;

import java.io.*;

import org.junit.*;

import retrofit2.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public final class ResultTest {
  @Test
  public void response() {
    Response<String> response = Response.success("Hi");
    Result<String> result = Result.response(response);
    assertThat(result.isError()).isFalse();
    assertThat(result.error()).isNull();
    assertThat(result.response()).isSameAs(response);
  }

  @Test
  public void nullResponseThrows() {
    try {
      Result.response(null);
      fail();
    } catch (NullPointerException e) {
      assertThat(e).hasMessage("response == null");
    }
  }

  @Test
  public void error() {
    Throwable error = new IOException();
    Result<Object> result = Result.error(error);
    assertThat(result.isError()).isTrue();
    assertThat(result.error()).isSameAs(error);
    assertThat(result.response()).isNull();
  }

  @Test
  public void nullErrorThrows() {
    try {
      Result.error(null);
      fail();
    } catch (NullPointerException e) {
      assertThat(e).hasMessage("error == null");
    }
  }
}
