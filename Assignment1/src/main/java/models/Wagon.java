package models;

import java.text.MessageFormat;

public abstract class Wagon {
    protected int id;               // some unique ID of a Wagon
    private Wagon nextWagon = null;        // another wagon that is appended at the tail of this wagon
                                    // a.k.a. the successor of this wagon in a sequence
                                    // set to null if no successor is connected
    private Wagon previousWagon = null;    // another wagon that is prepended at the front of this wagon
                                    // a.k.a. the predecessor of this wagon in a sequence
                                    // set to null if no predecessor is connected


    // representation invariant propositions:
    // tail-connection-invariant:   wagon.nextWagon == null or wagon == wagon.nextWagon.previousWagon
    // front-connection-invariant:  wagon.previousWagon == null or wagon = wagon.previousWagon.nextWagon

    public Wagon (int wagonId) {
        this.id = wagonId;
    }

    public int getId() {
        return id;
    }

    public Wagon getNextWagon() {
        return nextWagon;
    }

    public Wagon getPreviousWagon() {
        return previousWagon;
    }

    public Wagon setPreviousWagon(Wagon wagon) {
        return previousWagon = wagon;
    }
    /**
     * @return  whether this wagon has a wagon appended at the tail
     */
    public boolean hasNextWagon() {
        return nextWagon != null;
    }

    /**
     * @return  whether this wagon has a wagon prepended at the front
     */
    public boolean hasPreviousWagon() {
        return previousWagon != null;
    }

    /**
     * Returns the last wagon attached to it,
     * if there are no wagons attached to it then this wagon is the last wagon.
     * @return  the last wagon
     */
    public Wagon getLastWagonAttached() {
        // TODO find the last wagon in the sequence
        Wagon next = this;
        while(next.hasNextWagon()){
            next = next.getNextWagon();
        }
        return next;
    }

    /**
     * @return  the length of the sequence of wagons towards the end of its tail
     * including this wagon itself.
     */
    public int getSequenceLength() {
        // TODO traverse the sequence and find its length
        int length = 1;
        Wagon next = this;
        while (next.hasNextWagon()){
            length++;
            next = this.getNextWagon();
        }
        return length;
    }

    /**
     * Attaches the tail wagon and its connected successors behind this wagon,
     * if and only if this wagon has no wagon attached at its tail
     * and if the tail wagon has no wagon attached in front of it.
     * @param tail the wagon to attach behind this wagon.
     * @throws IllegalStateException if this wagon already has a wagon appended to it.
     * @throws IllegalStateException if tail is already attached to a wagon in front of it.
     *          The exception should include a message that reports the conflicting connection,
     *          e.g.: "%s is already pulling %s"
     *          or:   "%s has already been attached to %s"
     */
    public void attachTail(Wagon tail) {

        //This wagon has previous wagon, so tail is already attached to a wagon in front of it
        if(this.hasNextWagon()){
            MessageFormat message = new MessageFormat("{0} has already been attached to {1}");
            throw new IllegalStateException(message.format(new Object[]{this, this.getNextWagon()}));
        }

        //Check tail has no previous wagon
        if(tail.hasPreviousWagon()){
            MessageFormat message = new MessageFormat("{0} is already pulling {1}");
            throw new IllegalStateException(message.format(new Object[]{tail, tail.hasPreviousWagon()}));
        }

        this.nextWagon = tail;
        tail.previousWagon = this;
    }

    /**
     * Detaches the tail from this wagon and returns the first wagon of this tail.
     * @return the first wagon of the tail that has been detached
     *          or <code>null</code> if it had no wagons attached to its tail.
     */
    public Wagon detachTail() {
        Wagon first = this.nextWagon;
        this.nextWagon = null;
        return first;
    }

    /**
     * Detaches this wagon from the wagon in front of it.
     * No action if this wagon has no previous wagon attached.
     * @return  the former previousWagon that has been detached from,
     *          or <code>null</code> if it had no previousWagon.
     */
    public Wagon detachFront() {
        // TODO detach this wagon from its predecessor (sustaining the invariant propositions).
        //   and return that predecessor
        Wagon previous = this.previousWagon;
        if(previous != null){
            previous.nextWagon = null;
            this.previousWagon = null;
        }
        return previous;
    }

    /**
     * Replaces the tail of the <code>front</code> wagon by this wagon and its connected successors
     * Before such reconfiguration can be made,
     * the method first disconnects this wagon form its predecessor,
     * and the <code>front</code> wagon from its current tail.
     * @param front the wagon to which this wagon must be attached to.
     */
    public void reAttachTo(Wagon front) {
        // TODO detach any existing connections that will be rearranged
        Wagon previous = this.getPreviousWagon();
        // TODO attach this wagon to its new predecessor front (sustaining the invariant propositions).
    }

    /**
     * Removes this wagon from the sequence that it is part of,
     * and reconnects its tail to the wagon in front of it, if any.
     */
    public void removeFromSequence() {
        // TODO
    }


    /**
     * Reverses the order in the sequence of wagons from this Wagon until its final successor.
     * The reversed sequence is attached again to the wagon in front of this Wagon, if any.
     * No action if this Wagon has no succeeding next wagon attached.
     * @return the new start Wagon of the reversed sequence (with is the former last Wagon of the original sequence)
     */
    public Wagon reverseSequence() {
        // TODO provide an iterative implementation,
        //   using attach- and detach methods of this class

        return null;
    }

    @Override
    public String toString() {
        return  String.format("[Wagon-%s]", this.id);
    }
}
