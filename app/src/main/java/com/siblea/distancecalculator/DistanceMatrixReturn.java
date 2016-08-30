package com.siblea.distancecalculator;

import java.text.DecimalFormat;
import java.util.List;

public class DistanceMatrixReturn {

    private List<String> destination_addresses;
    private List<String> origin_addresses;
    private List<Row> rows;

    public String getDistanceString() {
        double distanceInMeters = rows.get(0).getElements().get(0).getDistance().getValue();
        return distanceInMeters / 1000 + "km";
    }

    public String getDurationString() {
        double durationInMinutes = rows.get(0).getElements().get(0).getDistance().getValue();
        int hour = (int) durationInMinutes / 60;
        int minute = (int) durationInMinutes % 60;

        DecimalFormat df = new DecimalFormat("00");
        return df.format(hour) + ":" + df.format(minute);
    }
}
