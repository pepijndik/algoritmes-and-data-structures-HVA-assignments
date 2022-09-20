package models;

public class Locomotive{
    private int locNumber;
    private int maxWagons;


    public Locomotive(int locNumber, int maxWagons) {
        this.locNumber = locNumber;
        this.maxWagons = maxWagons;
    }

    public int getMaxWagons() {
        return maxWagons > 0 ? maxWagons : 0;
    }


    @Override
    public String toString() {
        return  String.format("[Loc-%s]", this.locNumber);
    }

}
