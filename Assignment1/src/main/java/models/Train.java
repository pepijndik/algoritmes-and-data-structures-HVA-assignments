package models;
public class Train {
    private final String origin;
    private final String destination;
    private final Locomotive engine;
    private Wagon firstWagon;

    /* Representation invariants:
        firstWagon == null || firstWagon.previousWagon == null
        engine != null
     */

    public Train(Locomotive engine, String origin, String destination) {
        this.engine = engine;
        this.destination = destination;
        this.origin = origin;
    }

    /* three helper methods that are useful in other methods */
    public boolean hasWagons() {
        return firstWagon != null;
    }

    public boolean isPassengerTrain() {
        return getFirstWagon() instanceof PassengerWagon;
    }

    public boolean isFreightTrain() {
        return getFirstWagon() instanceof FreightWagon;
    }

    public Locomotive getEngine() {
        return engine;
    }

    public Wagon getFirstWagon() {
        return firstWagon != null ? firstWagon : null;
    }

    /**
     * Replaces the current sequence of wagons (if any) in the train
     * by the given new sequence of wagons (if any)
     * (sustaining all representation invariants)
     *
     * @param wagon the first wagon of a sequence of wagons to be attached
     *              (can be null)
     */
    public void setFirstWagon(Wagon wagon) {
        this.firstWagon = wagon;
    }

    /**
     * @return the number of Wagons connected to the train
     */
    public int getNumberOfWagons() {
        int count = 0;
        if (hasWagons()) {
            count = getFirstWagon().getSequenceLength();
        }
        return count;
    }

    /**
     * @return the last wagon attached to the train
     */
    public Wagon getLastWagonAttached() {
        if (hasWagons()) {
            Wagon next = getFirstWagon();
            while (next.hasNextWagon()) {
                next = next.getNextWagon();
            }
            return next;
        }
        return null;
    }

    /**
     * @return the total number of seats on a passenger train
     * (return 0 for a freight train)
     */
    public int getTotalNumberOfSeats() {
        if (isFreightTrain()) {
            return 0;
        }

        int totalSeats = 0;
        Wagon currentWagon = getFirstWagon();
        while (currentWagon != null) {
            totalSeats += ((PassengerWagon) currentWagon).getNumberOfSeats();
            currentWagon = currentWagon.getNextWagon();
        }
        return totalSeats;
    }

    /**
     * calculates the total maximum weight of a freight train
     *
     * @return the total maximum weight of a freight train
     * (return 0 for a passenger train)
     */
    public int getTotalMaxWeight() {
        if (isPassengerTrain()) {
            return 0;
        }
        int totalWeight = 0;
        Wagon currentWagon = getFirstWagon();
        while (currentWagon != null) {
            totalWeight += ((FreightWagon) currentWagon).getMaxWeight();
            currentWagon = currentWagon.getNextWagon();
        }
        return totalWeight;
    }

    /**
     * Finds the wagon at the given position (starting at 1 for the first wagon of the train)
     *
     * @param position
     * @return the wagon found at the given position
     * (return null if the position is not valid for this train)
     */
    public Wagon findWagonAtPosition(int position) {
        Wagon wagon = null;
        if (hasWagons()) {
            int wagonPos = 1;
            if (position > getNumberOfWagons() || position < wagonPos) return null;
            wagon = getFirstWagon();

            while (wagonPos < position) {
                wagon = wagon.getNextWagon();
                wagonPos++;
            }
        }
        return wagon;
    }

    /**
     * Finds the wagon with a given wagonId
     *
     * @param wagonId
     * @return the wagon found
     * (return null if no wagon was found with the given wagonId)
     */
    public Wagon findWagonById(int wagonId) {
        Wagon currentWagon = getFirstWagon();
        if (hasWagons()) {
            while (currentWagon != null) {
                if (currentWagon.equals(this.getLastWagonAttached()) && currentWagon.getId() != wagonId) {
                    currentWagon = null;
                    break;
                }
                if (currentWagon.getId() == wagonId) break;

                currentWagon = currentWagon.getNextWagon();
            }
        }
        return currentWagon;
    }

    /**
     * Determines if the given sequence of wagons can be attached to this train
     * Verifies if the type of wagons match the type of train (Passenger or Freight)
     * Verifies that the capacity of the engine is sufficient to also pull the additional wagons
     * Verifies that the wagon is not part of the train already
     * Ignores the predecessors before the head wagon, if any
     *
     * @param wagon the head wagon of a sequence of wagons to consider for attachment
     * @return whether type and capacity of this train can accommodate attachment of the sequence
     */
    public boolean canAttach(Wagon wagon) {
        if (wagon == null) return false;
        boolean EngineCanPull = wagon.getSequenceLength() <= (this.engine.getMaxWagons() - this.getNumberOfWagons()) && this.findWagonById(wagon.getId()) == null;
        if (!this.hasWagons() && EngineCanPull) return true;
        return wagon.getClass() == this.getFirstWagon().getClass() && EngineCanPull;
    }


    /**
     * Tries to attach the given sequence of wagons to the rear of the train
     * No change is made if the attachment cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity)
     * if attachment is possible, the head wagon is first detached from its predecessors, if any
     *
     * @param wagon the head wagon of a sequence of wagons to be attached
     * @return whether the attachment could be completed successfully
     */
    public boolean attachToRear(Wagon wagon) {
        if (!canAttach(wagon)) return false;

        if (wagon.hasPreviousWagon()) {
            wagon.detachFront();
        }

        if (!this.hasWagons()) {
            this.setFirstWagon(wagon);
            return true;
        }
        this.getLastWagonAttached().attachTail(wagon);

        return true;
    }


    /**
     * Tries to insert the given sequence of wagons at the front of the train
     * (the front is at position one, before the current first wagon, if any)
     * No change is made if the insertion cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity)
     * if insertion is possible, the head wagon is first detached from its predecessors, if any
     *
     * @param wagon the head wagon of a sequence of wagons to be inserted
     * @return whether the insertion could be completed successfully
     */
    public boolean insertAtFront(Wagon wagon) {
        if (!canAttach(wagon)) return false;

        if (wagon.hasPreviousWagon())
            wagon.detachFront();

        if (this.hasWagons())
            wagon.getLastWagonAttached().attachTail(this.getFirstWagon());

        this.setFirstWagon(wagon);

        return true;
    }

    /**
     * Tries to insert the given sequence of wagons at/before the given position in the train.
     * (The current wagon at given position including all its successors shall then be reattached
     * after the last wagon of the given sequence.)
     * No change is made if the insertion cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity
     * or the given position is not valid for insertion into this train)
     * if insertion is possible, the head wagon of the sequence is first detached from its predecessors, if any
     *
     * @param position the position where the head wagon and its successors shall be inserted
     *                 1 <= position <= numWagons + 1
     *                 (i.e. insertion immediately after the last wagon is also possible)
     * @param wagon    the head wagon of a sequence of wagons to be inserted
     * @return whether the insertion could be completed successfully
     */
    public boolean insertAtPosition(int position, Wagon wagon) {
        final int FIRST_POSITION = 1;
        final int POSITION_LIMIT = this.getNumberOfWagons() + 1;
        Wagon current;

        if(!this.canAttach(wagon)) return false;
        if (position <= 0 || (this.hasWagons() && position > POSITION_LIMIT)) return false;

        wagon.detachFront();
        if (!this.hasWagons() || position == FIRST_POSITION) {
            current = this.getFirstWagon();
            this.setFirstWagon(wagon);

            if (current != null)
                wagon.attachTail(current);

            return true;
        }

        Wagon positionWagon = findWagonAtPosition(position);
        if (positionWagon == null) return this.attachToRear(wagon);

        if (positionWagon.hasPreviousWagon()) {
            Wagon previous = positionWagon.getPreviousWagon();

            positionWagon.detachFront();
            previous.attachTail(wagon);
            positionWagon.reAttachTo(wagon.getLastWagonAttached());
        }

        return true;
    }

    /**
     * Tries to remove one Wagon with the given wagonId from this train
     * and attach it at the rear of the given toTrain
     * No change is made if the removal or attachment cannot be made
     * (when the wagon cannot be found, or the trains are not compatible
     * or the engine of toTrain has insufficient capacity)
     *
     * @param wagonId the id of the wagon to be removed
     * @param toTrain the train to which the wagon shall be attached
     *                toTrain shall be different from this train
     * @return whether the move could be completed successfully
     */
    public boolean moveOneWagon(int wagonId, Train toTrain) {
        Wagon wagon = this.findWagonById(wagonId);

        if (!toTrain.canAttach(wagon) || wagon == null) return false;

        if (this.getFirstWagon().equals(wagon))
            this.setFirstWagon(wagon.getNextWagon());

        wagon.removeFromSequence();
        toTrain.attachToRear(wagon);

        return true;
    }

    /**
     * Tries to split this train before the wagon at given position and move the complete sequence
     * of wagons from the given position to the rear of toTrain.
     * No change is made if the split or re-attachment cannot be made
     * (when the position is not valid for this train, or the trains are not compatible
     * or the engine of toTrain has insufficient capacity)
     *
     * @param position 1 <= position <= numWagons
     * @param toTrain  the train to which the split sequence shall be attached
     *                 toTrain shall be different from this train
     * @return whether the move could be completed successfully
     */
    public boolean splitAtPosition(int position, Train toTrain) {
        Wagon positionWagon = this.findWagonAtPosition(position);

        if (!toTrain.canAttach(positionWagon) || positionWagon == null) return false;

        if (positionWagon == this.getFirstWagon())
            this.setFirstWagon(null);

        if(positionWagon.hasPreviousWagon()){
            positionWagon.getPreviousWagon().detachTail();
            positionWagon.detachFront();
        }
        toTrain.attachToRear(positionWagon);

        return true;
    }

    /**
     * Reverses the sequence of wagons in this train (if any)
     * i.e. the last wagon becomes the first wagon
     * the previous wagon of the last wagon becomes the second wagon
     * etc.
     * (No change if the train has no wagons or only one wagon)
     */
    public void reverse() {
        if(this.hasWagons()){
            Wagon newFirst = this.getFirstWagon().reverseSequence();
            this.setFirstWagon(newFirst);
        }
    }

    @Override
    public String toString() {
        StringBuilder TrainString = new StringBuilder();
        TrainString.append(this.engine).append(" ");
        if (hasWagons()) {
            Wagon next = getFirstWagon();
            while (next != null) {
                TrainString.append(next).append(" ");
                next = next.getNextWagon();
            }
        }
        TrainString.append(String.format("with %d wagons from %s to %s", this.getNumberOfWagons(), origin, destination));
        return TrainString.toString();
    }
}
