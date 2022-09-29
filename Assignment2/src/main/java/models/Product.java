package models;

public class Product {
    private final long barcode;
    private String title;
    private double price;

    public Product(long barcode) {
        this.barcode = barcode;
    }
    public Product(long barcode, String title, double price) {
        this(barcode);
        this.title = title;
        this.price = price;
    }

    /**
     * parses product information from a textLine with format: barcode, title, price
     * @param textLine
     * @return  a new Product instance with the provided information
     *          or null if the textLine is corrupt or incomplete
     */
    public static Product fromLine(String textLine) {
        final int PRODUCT_INFO_LIMIT = 3;
        Product newProduct = null;

        if (textLine != null) {
            String[] pInfoSplitArray = textLine.split(", ");

            if (pInfoSplitArray.length < PRODUCT_INFO_LIMIT ||
                    !productValidation(pInfoSplitArray[0],pInfoSplitArray[2]))
                return null;

            newProduct = new Product(Long.parseLong(pInfoSplitArray[0]), pInfoSplitArray[1],
                    Double.parseDouble(pInfoSplitArray[2]));
        }

        return newProduct;
    }

    public static Boolean productValidation(String barcode, String price) {
        try {
            Long.parseLong(barcode);
        } catch (NumberFormatException e) {
            return false;
        }

        try {
            Double.parseDouble(price);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public long getBarcode() {
        return barcode;
    }

    public String getTitle() {
        return title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Product)) return false;
        return this.getBarcode() == ((Product)other).getBarcode();
    }

    @Override
    public String toString() {
        StringBuilder product = new StringBuilder();

        product.append(this.barcode).append("/");
        product.append(this.title).append("/");
        product.append(this.price);

        return product.toString();
    }


    // TODO add public and private methods as per your requirements
}
