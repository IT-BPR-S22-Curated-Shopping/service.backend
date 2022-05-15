package bpr.service.backend.util;

import java.util.Date;

public interface IDateTime {

    long getEpochSeconds();
    Date convertToDate(long timestamp);
}
