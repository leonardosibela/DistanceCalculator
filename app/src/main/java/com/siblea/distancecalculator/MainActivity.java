package com.siblea.distancecalculator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.distance)
    TextView distance;

    @BindView(R.id.duration)
    TextView duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.calculate)
    public void calculate() {
        GoogleDistanceMatrixAPI.GoogleDistanceMatrixService gdmAPI = GoogleDistanceMatrixAPI.getInstance();
        Call<DistanceMatrixReturn> paths = gdmAPI.get(
                new GoogleDistanceMatrixAPI.Place(-46.93717, -22.38263),
                new GoogleDistanceMatrixAPI.Place(-47.06361, -22.91079),
                "AIzaSyCHeuumo6FhrrBKoXqXAoqusqUFEHJYjHg");

        paths.enqueue(new Callback<DistanceMatrixReturn>() {

            @Override
            public void onResponse(Call<DistanceMatrixReturn> call, Response<DistanceMatrixReturn> response) {
                DistanceMatrixReturn distanceMatrix = response.body();
                duration.setText(distanceMatrix.getDurationString());
                distance.setText(distanceMatrix.getDistanceString());
            }

            @Override
            public void onFailure(Call<DistanceMatrixReturn> call, Throwable t) {
                Toast.makeText(getBaseContext(), "FUDEU", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
