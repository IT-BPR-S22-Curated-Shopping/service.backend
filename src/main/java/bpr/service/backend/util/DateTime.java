package bpr.service.backend.util;

import org.springframework.stereotype.Component;

import java.time.Instant;


/*
    Datetime class created to enable mocking of datetime.
 */
@Component("DateTime")
public class DateTime implements IDateTime{

    @Override
    public long getEpochSeconds() {
        return Instant.now().getEpochSecond();
    }

    @Override
    public Instant convertToDate(long timestamp) {
        return Instant.ofEpochSecond(timestamp);
    }
}
