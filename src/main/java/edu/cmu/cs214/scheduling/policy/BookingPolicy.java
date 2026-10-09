package edu.cmu.cs214.scheduling.policy;

import edu.cmu.cs214.scheduling.domain.Booking;
import edu.cmu.cs214.scheduling.domain.BookingOutcome;
import edu.cmu.cs214.scheduling.domain.BookingRequest;
import edu.cmu.cs214.scheduling.domain.BookingStore;
import edu.cmu.cs214.scheduling.domain.Member;
import edu.cmu.cs214.scheduling.domain.Room;

/**
 * The rules one {@link edu.cmu.cs214.scheduling.domain.BookingType} applies to
 * submission, cancellation, pricing, and description.
 *
 * <p>{@link BookingPolicyFactory} looks up the policy for a given type.
 */
public interface BookingPolicy {

    /** Where a notification goes when there is no member to tell. */
    String FACILITIES_CONTACT = "facilities@rooms.example.edu";

    /**
     * Validates the request against this type's rules, writes what it can, and
     * publishes whatever notifications that produces.
     *
     * @param room the room the request named, already confirmed to exist
     */
    BookingOutcome submit(BookingRequest request, Room room);

    /**
     * Releases a booking already confirmed to exist and not yet cancelled.
     *
     * @param adminOverride set by callers acting with facilities authority
     * @return true when something was released
     */
    boolean cancel(Booking booking, boolean adminOverride);

    /** What the holder owes for a booking, in dollars. */
    double priceOf(Booking booking);

    /** A one-line summary for schedules and confirmation screens. */
    String describe(Booking booking);

    /** The room's display name, or its id when the room no longer exists. */
    static String roomNameOrId(BookingStore store, Booking booking) {
        Room room = store.findRoom(booking.getRoomId());
        return room == null ? booking.getRoomId() : room.getName();
    }

    /** The member's email, or the facilities contact when there is no member. */
    static String recipientFor(Member member) {
        return member == null ? FACILITIES_CONTACT : member.getEmail();
    }
}
