package com.kiwi.mobile.retrograph;

import java.io.*;

import org.jetbrains.annotations.*;

public final class GraphQLResponse<T> implements Serializable {

  // region Private Attributes

  private Data data;
  private String[] errors;

  // endregion Private Attributes

  // region Public Methods

  // region Constructors

  public GraphQLResponse(@Nullable T dataBody, @Nullable String[] errors) {
    if (dataBody != null) {
      this.data = new Data(dataBody);
    } else {
      this.data = null;
    }
    this.errors = errors;
  }

  // endregion Constructors

  // region Getters/Setters

  public Data getData() {
    return data;
  }

  public void setData(Data data) {
    this.data = data;
  }

  public String[] getErrors() {
    return errors;
  }

  public void setErrors(String[] errors) {
    this.errors = errors;
  }

  // endregion Getters/Setters

  // endregion Public Methods

  // region Inner Classes

  public final class Data {

    // region Private Attributes

    private T body;

    // endregion Private Attributes

    // region Public Methods

    // region Constructors

    public Data(T body) {
      this.body = body;
    }

    // endregion Constructors

    // region Getters/Setters

    public T getBody() {
      return body;
    }

    public void setBody(T body) {
      this.body = body;
    }

    // endregion Getters/Setters

    // endregion Public Methods
  }

  // endregion Inner Classes
}
