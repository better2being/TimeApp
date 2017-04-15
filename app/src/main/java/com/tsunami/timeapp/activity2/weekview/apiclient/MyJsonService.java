package com.tsunami.timeapp.activity2.weekview.apiclient;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * @author wangshujie
 */
public interface MyJsonService {

    @GET("/1kpjf")
    void listEvents(Callback<List<Event>> eventsCallback);

}
