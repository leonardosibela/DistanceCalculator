package com.siblea.distancecalculator;


import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class GoogleDistanceMatrixAPI {

    private static final String ENDPOINT_URL = "https://maps.googleapis.com/maps/api/distancematrix/json";

    private static GoogleDistanceMatrixService googleDistanceMatrixService;

    public interface GoogleDistanceMatrixService {

        @GET()
        Call<DistanceMatrixReturn> getUsers(@Query("origins") Place origin, @Query("destinations") Place destination);

    }

    public static GoogleDistanceMatrixService getInstance() {

        if (googleDistanceMatrixService == null) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT_URL)
                    .client(new OkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            googleDistanceMatrixService = retrofit.create(GoogleDistanceMatrixService.class);
        }

        return googleDistanceMatrixService;
    }

    public static class Place {

        private double latitude;
        private double longitude;

        public Place(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public String toString() {
            return longitude + "," + latitude;
        }
    }
}
