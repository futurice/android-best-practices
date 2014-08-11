package com.futurice.project.network;

import com.futurice.project.models.pojo.SearchEngineResults;

import retrofit.http.GET;
import retrofit.http.Query;

import rx.Observable;

/**
 * Endpoint for the Search Engine REST API.
 * Using Retrofit annotations.
 */
public interface SearchEngineService {
    @GET("/?format=json")
    public Observable<SearchEngineResults> search(@Query("q") String query);
}
