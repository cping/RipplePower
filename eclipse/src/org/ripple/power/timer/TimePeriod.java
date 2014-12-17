package org.ripple.power.timer;

import java.util.Date;

@SuppressWarnings("rawtypes")
public interface TimePeriod extends Comparable {

    public Date getStart();

    public Date getEnd();

}
