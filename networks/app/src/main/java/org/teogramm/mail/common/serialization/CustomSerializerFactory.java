package org.teogramm.mail.common.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;

/**
 * Creates gson objects that utilize the custom serializers
 * and deserializers in this package
 */
public class CustomSerializerFactory {

    /**
     * Creates a Gson object that utilizes all the custom
     * serializers
     */
    public static Gson createWithAll(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class,new LocalDateTimeSerializer());
        return gsonBuilder.create();
    }
}
