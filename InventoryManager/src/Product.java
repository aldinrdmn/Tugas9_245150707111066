public class Product {
    private int id;
    private String name;
    private String category;
    private double price;
    private int quantity;

    public Product(int id, String name, String category, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String toCsvLine() {
        return id + "," + name + "," + category + "," + price + "," + quantity;
    }

    public static Product fromCsvLine(String line) {
        String[] parts = line.split(",", 5);
        int id = Integer.parseInt(parts[0]);
        String name = parts[1];
        String category = parts[2];
        double price = Double.parseDouble(parts[3]);
        int quantity = Integer.parseInt(parts[4]);
        return new Product(id, name, category, price, quantity);
    }
}
