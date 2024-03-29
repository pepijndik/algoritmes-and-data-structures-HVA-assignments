package models;
public abstract class Wagon {
    protected int id;                       // some unique ID of a Wagon
    private Wagon nextWagon = null;        // another wagon that is appended at the tail of this wagon
    // a.k.a. the successor of this wagon in a sequence
    // set to null if no successor is connected
    private Wagon previousWagon = null;    // another wagon that is prepended at the front of this wagon
    // a.k.a. the predecessor of this wagon in a sequence
    // set to null if no predecessor is connected


    // representation invariant propositions:
    // tail-connection-invariant:   wagon.nextWagon == null or wagon == wagon.nextWagon.previousWagon
    // front-connection-invariant:  wagon.previousWagon == null or wagon = wagon.previousWagon.nextWagon

    public Wagon(int wagonId) {
        this.id = wagonId;
    }

    public int getId() {
        return id;
    }

    public Wagon getNextWagon() {
        return this.hasNextWagon() ? nextWagon : null;
    }

    public Wagon getPreviousWagon() {
        return this.hasPreviousWagon() ? previousWagon : null;
    }

    public Wagon setPreviousWagon(Wagon wagon) {
        return previousWagon = wagon;
    }

    public Wagon setNextWagon(Wagon wagon) {
        return nextWagon = wagon;
    }

    /**
     * @return whether this wagon has a wagon appended at the tail
     */
    public boolean hasNextWagon() {
        return nextWagon != null;
    }

    /**
     * @return whether this wagon has a wagon prepended at the front
     */
    public boolean hasPreviousWagon() {
        return previousWagon != null;
    }

    /**
     * Returns the last wagon attached to it,
     * if there are no wagons attached to it then this wagon is the last wagon.
     *
     * @return the last wagon
     */
    public Wagon getLastWagonAttached() {
        Wagon next = this;
        while (next.hasNextWagon()) {
            next = next.getNextWagon();
        }
        return next;
    }

    /**
     * @return the length of the sequence of wagons towards the end of its tail
     * including this wagon itself.
     */
    public int getSequenceLength() {
        int length = 1;
        Wagon next = this;
        while (next.hasNextWagon()) {
            length++;
            next = next.getNextWagon();
        }
        return length;
    }

    /**
     * Attaches the tail wagon and its connected successors behind this wagon,
     * if and only if this wagon has no wagon attached at its tail
     * and if the tail wagon has no wagon attached in front of it.
     *
     * @param tail the wagon to attach behind this wagon.
     * @throws IllegalStateException if this wagon already has a wagon appended to it.
     * @throws IllegalStateException if tail is already attached to a wagon in front of it.
     *                               The exception should include a message that reports the conflicting connection,
     *                               e.g.: "%s is already pulling %s"
     *                               or:   "%s has already been attached to %s"
     */
    public void attachTail(Wagon tail) {

        //This wagon has next wagon, so tail is already attached to a wagon in front of it
        if (this.hasNextWagon())
            throw new IllegalStateException(String.format("%s has already been attached to %s", this.getNextWagon(), this));

        //Check tail has previous wagon
        if (tail.hasPreviousWagon())
            throw new IllegalStateException(String.format("%s is already pulling %s", tail.getPreviousWagon(), tail));

        this.setNextWagon(tail);
        tail.setPreviousWagon(this);
    }

    /**
     * Detaches the tail from this wagon and returns the first wagon of this tail.
     *
     * @return the first wagon of the tail that has been detached
     * or <code>null</code> if it had no wagons attached to its tail.
     */
    public Wagon detachTail() {
        Wagon first = this.getNextWagon();
        if (first != null) {
            this.setNextWagon(null);
            first.setPreviousWagon(null);
        }
        return first;
    }

    /**
     * Detaches this wagon from the wagon in front of it.
     * No action if this wagon has no previous wagon attached.
     *
     * @return the former previousWagon that has been detached from,
     * or <code>null</code> if it had no previousWagon.
     */
    public Wagon detachFront() {
        Wagon previous = this.getPreviousWagon();
        if (previous != null) {
            previous.setNextWagon(null);
            this.setPreviousWagon(null);
        }
        return previous;
    }

    /**
     * Replaces the tail of the <code>front</code> wagon by this wagon and its connected successors
     * Before such reconfiguration can be made,
     * the method first disconnects this wagon form its predecessor,
     * and the <code>front</code> wagon from its current tail.
     *
     * @param front the wagon to which this wagon must be attached to.
     */
    public void reAttachTo(Wagon front) {
        front.detachTail();
        this.detachFront();
        front.attachTail(this);
    }

    /**
     * Removes this wagon from the sequence that it is part of,
     * and reconnects its tail to the wagon in front of it, if any.
     */
    public void removeFromSequence() {
        Wagon prev = this.getPreviousWagon();
        Wagon next = this.getNextWagon();

        this.detachTail();
        this.detachFront();

        if (prev != null) {
            prev.detachTail();
            if (next != null) prev.attachTail(next);
        }
    }
    
    /**
     * Reverses the order in the sequence of wagons from this Wagon until its final successor.
     * The reversed sequence is attached again to the wagon in front of this Wagon, if any.
     * No action if this Wagon has no succeeding next wagon attached.
     *
     * @return the new start Wagon of the reversed sequence (which is the former last Wagon of the original sequence)
     */
    public Wagon reverseSequence() {
        Wagon previous = null;
        Wagon current = this;
        Wagon last = this.getLastWagonAttached();
        Wagon temp;

        if (current.hasPreviousWagon()) {
            previous = current.getPreviousWagon();
            current.detachFront();
            previous.detachTail();
        }

        while (current != null) {
            temp = current.getNextWagon();
            current.setNextWagon(current.getPreviousWagon());
            current.setPreviousWagon(temp);
            current = current.getPreviousWagon();
        }
        current = last;

        if (previous != null) current.reAttachTo(previous);

        return current;
    }

    @Override
    public String toString() {
        return String.format("[Wagon-%s]", this.id);
    }
}
