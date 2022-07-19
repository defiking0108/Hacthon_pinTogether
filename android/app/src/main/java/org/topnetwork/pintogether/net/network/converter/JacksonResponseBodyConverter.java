package org.topnetwork.pintogether.net.network.converter;

import com.fasterxml.jackson.databind.ObjectReader;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * JacksonResponseBodyConverter
 * @param <T>
 */
final class JacksonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
  private final ObjectReader adapter;

  JacksonResponseBodyConverter(ObjectReader adapter) {
    this.adapter = adapter;
  }

  @Override public T convert(ResponseBody value) throws IOException {
    try {
      T result = adapter.readValue(value.charStream());
      return result;
    } finally {
      value.close();
    }
  }
}