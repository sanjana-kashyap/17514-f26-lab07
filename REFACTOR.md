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

List every design pattern you can name in that package. For each one, the class
or classes that carry it.

### The problem each one solves

For each pattern you listed, what would have to be true about the requirements
for that pattern to be the right call? One sentence each, not in terms of
"flexibility".

### Which of those problems exist here

For each pattern, does the problem it solves exist in this codebase? Point at
the code that settles it.

### The simpler structure

**Your proposal.** What replaces `notify/`. Sketch the classes and the one
method that matters.

**What stays the same.** The tested behavior it must still produce, named
precisely enough that a reader can check it against the shipped tests.

**What you would keep, if anything.** If you would keep one interface, say which
and why. "None of it" is a fine answer if you can defend it.

### What would bring each layer back

For at least two of the layers you would remove, what requirement, if it arrived
next sprint, would make that layer the right structure? Be specific about the
requirement, not about the pattern.

**Misuse or anti-pattern?** Say which this is and why the distinction matters.

---

## Milestone 3: The missing pattern

Read `pricing/`. Not coded, one sentence.

**The pattern.** Which one fits `PriceCalculator`, and the problem that makes it
fit. Name the problem.

**Would you apply it today?** Yes or no, one line, with the reason.
