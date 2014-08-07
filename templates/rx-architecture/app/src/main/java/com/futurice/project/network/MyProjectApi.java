package com.futurice.project.network;

import com.futurice.project.models.pojo.SearchEngineResults;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.client.OkClient;
import rx.Observable;

/**
 * Entry point for all requests to **My Project** API.
 * Uses Retrofit library to abstract the actual REST API into a service.
 */
public class MyProjectApi {

    private static MyProjectApi instance;
    private SearchEngineService searchService;

    /**
     * Returns the instance of this singleton.
     */
    public static MyProjectApi getInstance() {
        if (instance == null) {
            instance = new MyProjectApi();
        }
        return instance;
    }

    /**
     * Private singleton constructor.
     */
    private MyProjectApi() {
        RestAdapter restAdapter = buildRestAdapter();
        this.searchService = restAdapter.create(SearchEngineService.class);
    }

    /**
     * Creates the RestAdapter by setting custom HttpClient.
     */
    private RestAdapter buildRestAdapter() {
        return new RestAdapter.Builder()
            .setEndpoint(ApiConstants.BASE_URL)
            // Out-comment the following line if you want to use the default converter Gson.
            .setConverter(new JacksonConverter())
            .setClient(getHttpClient())
            .build();
    }

    /**
     * Custom Http Client to define connection timeouts.
     */
    private Client getHttpClient() {
        OkHttpClient httpClient = new OkHttpClient();
        httpClient.setConnectTimeout(ApiConstants.HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
        httpClient.setReadTimeout(ApiConstants.HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS);
        return new OkClient(httpClient);
    }

    /**
     * Does a text search for a given query, and returns an Observable of the results.
     *
     * @param query the query string
     * @return an Observable with the results
     */
    public Observable<SearchEngineResults> getSearchEngineResults(String query) {
        return this.searchService.search(query);
    }
}
