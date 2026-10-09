package edu.cmu.cs214.scheduling.pricing;

import edu.cmu.cs214.scheduling.domain.Booking;
import edu.cmu.cs214.scheduling.domain.Member;
import edu.cmu.cs214.scheduling.domain.MembershipTier;

import java.time.DayOfWeek;
import java.util.List;

/**
 * Prices one slot.
 *
 * <p>The published rules, in the order they apply: an hourly base rate, a
 * weekend surcharge, a discount for long bookings, and the member's tier
 * discount. Prices are in dollars, rounded to the cent at the end.
 */
public class PriceCalculator {

    private static final double BASE_RATE_PER_HOUR = 40.0;
    private static final double WEEKEND_SURCHARGE = 0.25;
    private static final double LONG_BOOKING_DISCOUNT = 0.10;
    private static final long LONG_BOOKING_MINUTES = 180;

    /** Price for one booking held by one member. A null member pays no tier discount. */
    public double price(Booking booking, Member member) {
        if (booking == null) {
            throw new IllegalArgumentException("booking must not be null");
        }

        long minutes = booking.getSlot().minutes();
        double price = BASE_RATE_PER_HOUR * (minutes / 60.0);

        DayOfWeek day = booking.getStart().getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            price = price + price * WEEKEND_SURCHARGE;
        }

        if (minutes >= LONG_BOOKING_MINUTES) {
            price = price - price * LONG_BOOKING_DISCOUNT;
        }

        MembershipTier tier = member == null ? MembershipTier.BASIC : member.getTier();
        if (tier.getDiscountRate() > 0.0) {
            price = price - price * tier.getDiscountRate();
        }

        return round(price);
    }

    /** The advertised hourly rate before any rule applies. */
    public double baseRatePerHour() {
        return BASE_RATE_PER_HOUR;
    }

    /** The sum of {@link #price(Booking, Member)} over every booking, cancelled ones skipped. */
    public double totalPrice(List<Booking> bookings, Member member) {
        double total = 0.0;
        for (Booking booking : bookings) {
            if (booking.isCancelled()) {
                continue;
            }
            total += price(booking, member);
        }
        return total;
    }

    private static double round(double amount) {
        return Math.round(amount * 100.0) / 100.0;
    }
}
