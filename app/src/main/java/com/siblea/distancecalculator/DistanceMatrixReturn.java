package com.siblea.distancecalculator;

import java.text.DecimalFormat;
import java.util.List;

public class DistanceMatrixReturn {

    private List<String> destination_addresses;
    private List<String> origin_addresses;
    private List<Row> rows;

    public String getDistanceString() {
        return rows.get(0).getElements().get(0).getDistance().getText();
    }

    public String getDurationString() {
        double durationInMinutes = rows.get(0).getElements().get(0).getDuration().getValue();
        int hour = (int) durationInMinutes / 3600;
        int minute = (int) Math.round((durationInMinutes / 3600 - hour) * 100);

        DecimalFormat df = new DecimalFormat("00");
        return df.format(hour) + ":" + df.format(minute);
    }
}
