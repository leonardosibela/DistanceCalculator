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
        int t = (int) rows.get(0).getElements().get(0).getDuration().getValue();
        int hours = t / 3600;
        int minutes = (t % 3600) / 60;

        DecimalFormat df = new DecimalFormat("00");
        return df.format(hours) + ":" + df.format(minutes);
    }
}
