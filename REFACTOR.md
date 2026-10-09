# REFACTOR.md

One section per milestone. Fill each one in as you go, in order.

Milestone 1 is written in two sittings, the pin before the refactor and the rest
after. A pin written afterwards is worth nothing, and a TA will ask.

Keep it short and specific. Point at methods, call sites, and test names.

---

## Milestone 1: Direct a refactor, characterization first

### The pin (write this section before you direct the refactor)

**The pin.** `BookingWorkflow.java:priceOfRejectsAnUnknownBooking()` - verifies
that `priceOf()` rejects an unknown booking and throws an exception.

**Why that one, and does a shipped test already cover it?** We are not checking
against the unhappy path of `priceOf()` - an easy verification but gives us a
lot of robustness in our test suite.

**What a regeneration would do differently here.** Probably would have made the
same decision if it was specified that an unknown booking ID is rejected.
Otherwise, the regeneration could jsut return a negative price or zero,
depending on the model.

### The directive

**The refactor and the exact directive.** Conditional replaced by polymorphism.

> Help me break down a booking policy for the booking workflow: RegularPolicy,
> RecurringPolicy, BlockedPolicy. They could be looked up from an enum or a
> simple factory. Each would be implementing a BookingPolicy interface with
> submit(), cancel(), priceOf(), describe(). Try to keep your changes modular:
> maybe add a new file/directory for this class and compose it within the
> BookingWorkflow. domain, reporting, and notify are not in bounds since they
> are unrelated to the booking policy. Price calculator would benefit from this
> policy as well.

### The result

**The diff and the suite.** Commit hash:
3062a294c4ac01ca28c7c17f86e109ca9a1e2ad0

**What did NOT change: behavior and files.** Untouched are the notification,
report sumamry, and domain logic. Verified with code diff + running the same
unchanged tests as before. The new test I added for pinning an existing
behaviour also passes.

**One thing the agent changed that you had to look at twice.** I had to check
the factory again because of a possibly unmapped booking type. Accepted agent's
suggestion of throwing an exception.

### The closing explanation

**Refactor or regenerate?** Regenerating would not have been a better call. We
already have the code passing the behaviour check right now. Observable
behaviour hasn't changed, so a code refactoring is the better option than to
start from scratch again. The codebase is already being used by a client in
`/domain` and seems to be on the more mature side. We would b throwing away a
lot of good work for one internal restructuring.

**What would flip your answer.** If externable observable behaviour is changing
and we have no clients of our code yet, it might be worth regenerating. But at
that point, it would need to be closer to being a different product altogether.

---

## Milestone 2: The pattern critique

Read `notify/`. It works and the outbox tests pass.

### The patterns present

Observer: `NotificationHub` / `NotificationSubscriber` + `OutboxSubscriber`

Strategy: `NotificationStrategy` / `EmailNotificationStrategy`

Factory: `NotifierFactory`

### The problem each one solves

Observer: if the publisher needs to notify an unknown/large number of
independent consumers of the same event without knowing their concrete types

Strategy: the algorithm for turning a `NotificationMessage` into delivered text
needs to vary independently of who's publishing

Factory: shared logic or expensive resource must have exactly one instance that
callers coordinate through. The type of the object is dynamically decided.

### Which of those problems exist here

Observer: currently, we only have one subscriber, so the problem doesn't exist
today

Strategy: there is only one way of notifying customers - through email. We are
optimising for a problem that doesn't exist today, so it can be seen as clutter.

Factory: there is no real logic/state for the creation of the notifier, so the
problem doesn't exist here, so it's bloating the codebase right now.

### The simpler structure

**Your proposal.** A `Notifier` class replaces the whole sub-directory (note: we
also want to keep `NotificationMessage`) and its classes. It has one method
`publish()` that delivers a `NotificationMessage` to a recipient.

**What stays the same.** The behaviour that must still be produced is the fact
that it can send a specifically formatted message to the recipients. Also being
able to show us what and how many messages have been sent.

**What you would keep, if anything.** The one class I would keep could be
`Outbox` so we can compose it into the `Notifier`.

### What would bring each layer back

Observer: if we have multiple consumers of the same message (if we have a group
booking and all participants want to be notified), then would have to bring the
`NotificationHub` back.

Strategy: if we have more than one way of delivering the messages (if SMS is
supposed to be supported tomorrow). Would bring back the `NotificationStrategy`
layer.

**Misuse or anti-pattern?** Anti-pattern: speculative generality - building
something just in case when the need isn't here yet. The distinction is
important because these are valid patterns to apply for extending the codebase,
and there isn't a mismatch in the use cases.

---

## Milestone 3: The missing pattern

Read `pricing/`. Not coded, one sentence.

**The pattern.** Decorator pattern: calculating the price is a chain of
calculations that just builds on the previous result.

**Would you apply it today?** Currently, the rules are too few to implement a
whole decorator pattern.
