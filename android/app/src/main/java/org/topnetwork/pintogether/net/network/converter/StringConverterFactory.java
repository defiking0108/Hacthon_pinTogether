package org.topnetwork.pintogether.net.network.converter;

import androidx.annotation.Nullable;

import com.topnetwork.net.constant.ConstantKt;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by lgc on 2019/6/8.
 */

public class StringConverterFactory extends Converter.Factory {

    public static StringConverterFactory create() {
        return new StringConverterFactory();
    }

    @Nullable
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (!String.class.equals(type)) {
            return null;
        }
        return new Converter<ResponseBody, String>() {
            @Override
            public String convert(ResponseBody value) throws IOException {
                return value.string();
            }
        };
    }

    @Nullable
    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[]
            methodAnnotations, Retrofit retrofit) {
        if (!String.class.equals(type)) {
            return null;
        }
        return new Converter<String, RequestBody>() {
            @Override
            public RequestBody convert(String value) {
                return RequestBody.create(ConstantKt.MEDIA_TYPE_JSON, value);
            }
        };
    }
}
