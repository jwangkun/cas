package org.apereo.cas.ticket.support;

import org.apereo.cas.ticket.TicketState;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * Expiration policy that is based on a certain time period for a ticket to
 * exist.
 * <p>
 * The expiration policy defined by this class is one of inactivity.  If you are inactive for the specified
 * amount of time, the ticket will be expired.
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
public class TimeoutExpirationPolicy extends AbstractCasExpirationPolicy {

    /** Serialization support. */
    private static final long serialVersionUID = -7636642464326939536L;

    /** The time to kill in milliseconds. */
    private long timeToKillInMilliSeconds;


    /** No-arg constructor for serialization support. */
    public TimeoutExpirationPolicy() {
        this.timeToKillInMilliSeconds = 0;
    }

    /**
     * Instantiates a new timeout expiration policy.
     *
     * @param timeToKillInMilliSeconds the time to kill in milli seconds
     */
    public TimeoutExpirationPolicy(final long timeToKillInMilliSeconds) {
        this.timeToKillInMilliSeconds = timeToKillInMilliSeconds;
    }

    /**
     * Instantiates a new Timeout expiration policy.
     *
     * @param timeToKill the time to kill
     * @param timeUnit the time unit
     */
    public TimeoutExpirationPolicy(final long timeToKill, final TimeUnit timeUnit) {
        this.timeToKillInMilliSeconds = timeUnit.toMillis(timeToKill);
    }

    @Override
    public boolean isExpired(final TicketState ticketState) {
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        final ZonedDateTime expirationTime = now.plus(this.timeToKillInMilliSeconds, ChronoUnit.MILLIS);
        return ticketState == null || now.isAfter(expirationTime);
    }

    @Override
    public Long getTimeToLive() {
        return new Long(Integer.MAX_VALUE);
    }

    @Override
    public Long getTimeToIdle() {
        return this.timeToKillInMilliSeconds;
    }
}
