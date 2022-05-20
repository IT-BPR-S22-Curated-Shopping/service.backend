package bpr.service.backend.util;

import java.time.Instant;

public interface IDateTime {

    long getEpochMs();
    Instant convertToDate(long epochSeconds);
}
