package bpr.service.backend.util;

import java.time.Instant;

public interface IDateTime {

    long getEpochMillis();

    long convertStringToEpochMillis(String date);
    Instant convertToDate(long epochMillis);
}
