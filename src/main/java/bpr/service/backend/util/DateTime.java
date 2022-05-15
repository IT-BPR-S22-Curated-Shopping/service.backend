package bpr.service.backend.util;

import org.springframework.stereotype.Component;

import java.util.Date;

@Component("DateTime")
public class DateTime implements IDateTime{

    @Override
    public long getEpochSeconds() {
        return new Date().getTime();
    }
}
