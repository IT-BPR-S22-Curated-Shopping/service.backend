package bpr.service.backend.util;

import java.time.Instant;

public interface IDateTime {

    long getEpochSeconds();
    Instant convertToDate(long timestamp);
}
