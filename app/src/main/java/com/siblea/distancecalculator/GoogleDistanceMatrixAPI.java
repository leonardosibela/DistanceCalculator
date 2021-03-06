package com.siblea.distancecalculator;


import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class GoogleDistanceMatrixAPI {

    private static final String ENDPOINT_URL = "https://maps.googleapis.com/";

    private static GoogleDistanceMatrixService googleDistanceMatrixService;

    public interface GoogleDistanceMatrixService {

        @GET("maps/api/distancematrix/json")
        Call<DistanceMatrixReturn> get(@Query("origins") Place origin, @Query("destinations") Place destination, @Query("key") String key);
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

        public Place(double longitude, double latitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public String toString() {
            return latitude + "," + longitude;
        }
    }
}
