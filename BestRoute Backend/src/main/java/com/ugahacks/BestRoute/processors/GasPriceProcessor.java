package com.ugahacks.BestRoute.processors;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GasPriceProcessor {
    //This class will process the gas price data from an API.
    //Make a constructor that will take in all the data this class needs to process.

    private double gasPriceOrigin;
    private double gasPriceDestination;

    private String fullJson;

    public void process(String origin, String destination) {
        gasPriceOrigin = getJSON(getState(origin));
        gasPriceDestination = getJSON(getState(destination));
    }

    private String getState(String location) {
        return location.substring(location.indexOf(",")).trim();
    }

    private double getJSON(String location) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                .url("https://api.collectapi.com/gasPrice/stateUsaPrice?state=" + location)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "apikey 4cz7SRjMnLC8ZZyHjiPfLb:56hZPkRFedWFD0TcTPjtNs")
                .get()
                .build();        
            Response response = client.newCall(request).execute();
            fullJson = response.body().string();
        } catch (IOException e) {
            throw new RuntimeException("Invalid Input");
        }
        return Double.parseDouble(fullJson.substring(fullJson.indexOf("gasoline") + 11, fullJson.indexOf("gasoline") + 16));   
    }

    public double getPPG() {
        return (gasPriceOrigin + gasPriceDestination) / 2;
    }
}
