package bpr.service.backend.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

class DateTimeTest {

    DateTime dateTime;

    @BeforeEach
    public void beforeEach() {
        dateTime = new DateTime();
    }

    @Test
    public void getEpocSeconds() {
        var time = dateTime.getEpochMs();

        Assertions.assertTrue(time > 1000);
    }

    @Test
    public void canConvert() {
        var time = dateTime.getEpochMs();
        var expected = Instant.ofEpochMilli(time);

        var converted = dateTime.convertToDate(time);

        Assertions.assertEquals(expected, converted);
    }

}