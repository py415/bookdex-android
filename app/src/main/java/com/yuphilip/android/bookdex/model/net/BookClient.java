package com.yuphilip.android.bookdex.model.net;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class BookClient {

    //region Properties

    private static final String API_BASE_URL = "https://openlibrary.org/";
    private final AsyncHttpClient client;

    //endregion

    public BookClient() {
        this.client = new AsyncHttpClient();
    }

    private String getApiUrl() {
        return API_BASE_URL + "search.json?q=";
    }

    // Method for accessing the search API
    public void getBooks(final String query, JsonHttpResponseHandler handler) {

        try {
            String url = getApiUrl();
            client.get(url + URLEncoder.encode(query, "utf-8"), handler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

}
