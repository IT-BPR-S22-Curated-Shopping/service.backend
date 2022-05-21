package bpr.service.backend.util;

import org.springframework.stereotype.Component;

import java.time.Instant;


/*
    Datetime class created to enable mocking of datetime.
 */
@Component("DateTime")
public class DateTime implements IDateTime{

    @Override
    public long getEpochMillis() {
        return Instant.now().toEpochMilli();
    }

    @Override
    public long convertStringToEpochMillis(String date) {
        return Instant.parse(date).toEpochMilli();
    }

    @Override
    public Instant convertToDate(long epochMs) {
        return Instant.ofEpochMilli(epochMs);
    }


}
