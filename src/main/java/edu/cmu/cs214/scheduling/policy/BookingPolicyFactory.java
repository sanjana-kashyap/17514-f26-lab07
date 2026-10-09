package edu.cmu.cs214.scheduling.policy;

import edu.cmu.cs214.scheduling.domain.BookingStore;
import edu.cmu.cs214.scheduling.domain.BookingType;
import edu.cmu.cs214.scheduling.notify.NotificationHub;
import edu.cmu.cs214.scheduling.pricing.PriceCalculator;

import java.util.EnumMap;
import java.util.Map;

/** Builds and caches the one {@link BookingPolicy} for each {@link BookingType}. */
public final class BookingPolicyFactory {

    private final Map<BookingType, BookingPolicy> policies = new EnumMap<>(BookingType.class);

    public BookingPolicyFactory(BookingStore store, PriceCalculator calculator,
                                NotificationHub hub) {
        policies.put(BookingType.REGULAR, new RegularPolicy(store, calculator, hub));
        policies.put(BookingType.RECURRING, new RecurringPolicy(store, calculator, hub));
        policies.put(BookingType.BLOCKED, new BlockedPolicy(store, hub));
    }

    /** The policy that owns the rules for one booking type. */
    public BookingPolicy forType(BookingType type) {
        BookingPolicy policy = policies.get(type);
        if (policy == null) {
            throw new IllegalArgumentException("unsupported booking type " + type);
        }
        return policy;
    }
}
