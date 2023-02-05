package com.ugahacks.BestRoute.processors;


import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class FlightCostProcessor {
    //This class will process the flight cost data from an API.
    String jsonString;
    HashMap<String, String> iataCodes = new HashMap<String, String>();
    
    public void process(String origin, String destination, int numPeople) {
        getJSON(getIATA(origin), getIATA(destination), numPeople);
    }

    private void getJSON(String origin, String destination, int numPeople) {
        String buildURL = "https://skyscanner50.p.rapidapi.com/api/v1/searchFlights?" +
            "origin=" + origin + 
            "&destination=" + destination + 
            "&date=" + getDate() + 
            "&adults=" + numPeople + 
            "&currency=USD&countryCode=US&market=en-US";
        try {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
	        .url(buildURL)
	        .get()
	        .addHeader("X-RapidAPI-Key", "f984583381msh198a1863bc1426ap1f4886jsn30306581a5fa")
	        .addHeader("X-RapidAPI-Host", "skyscanner50.p.rapidapi.com")
	        .build();        
            Response response = client.newCall(request).execute();
            jsonString = response.body().string();
        } catch (IOException e) {
            System.out.println("Invalid Input");
        }
    }

    public double getCost() {
        String temp = jsonString.substring(jsonString.indexOf("price"));
        String output = jsonString.substring(jsonString.indexOf("price"), temp.indexOf("}"));
        return Double.parseDouble(output.substring(17));
    }

    private String getDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        now = now.plusDays(7);
        return dtf.format(now);
    }

    private String getIATA(String input) {
        String fullJson = "";
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                .url("https://airlabs.co/api/v9/cities?country_code=US&api_key=8b6ca479-8c6c-456d-a986-2193c31a7bb4")
                .get()
                .build();        
                Response response = client.newCall(request).execute();
                fullJson = response.body().string();
            } catch (IOException e) {
                System.out.println("Invalid Input");
            }
        for (int i = 0; i < fullJson.length() - 4; i++) {
            if (fullJson.substring(i, i + 4).equals("name")) {
                String name = fullJson.substring(i + 7, fullJson.indexOf("\"", i + 8));
                String code = fullJson.substring(fullJson.indexOf("code", i) + 7, fullJson.indexOf("\"", fullJson.indexOf("code", i) + 7));
                iataCodes.put(name, code);
            }
        }
        return iataCodes.get(input.substring(0, input.indexOf(","))).trim();
    }

}
