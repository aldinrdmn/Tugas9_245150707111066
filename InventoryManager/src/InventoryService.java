import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class InventoryService {

    public void initDataDirectory(Path dir) throws IOException {
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
    }

    public List<Product> loadProducts(Path csvPath) throws IOException {
        List<Product> products = new ArrayList<>();
        if (Files.exists(csvPath)) {
            try (BufferedReader reader = Files.newBufferedReader(csvPath)) {
                String line = reader.readLine(); // header
                while ((line = reader.readLine()) != null) {
                    products.add(Product.fromCsvLine(line));
                }
            }
        }
        return products;
    }

    public void saveProducts(List<Product> products, Path csvPath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(csvPath)) {
            writer.write("id,name,category,price,quantity\n");
            for (Product p : products) {
                writer.write(p.toCsvLine() + "\n");
            }
        }
    }

    public List<Product> searchProducts(List<Product> products, String keyword) {
        return products.stream()
            .filter(p -> p.getName().toLowerCase().contains(keyword.toLowerCase()))
            .collect(Collectors.toList());
    }

    public void sortProducts(List<Product> products, String criteria) {
        if (criteria.equals("price")) {
            products.sort(Comparator.comparingDouble(Product::getPrice));
        } else if (criteria.equals("quantity")) {
            products.sort(Comparator.comparingInt(Product::getQuantity));
        }
    }

    public List<Product> filterByPrice(List<Product> products, double min, double max) {
        return products.stream()
            .filter(p -> p.getPrice() >= min && p.getPrice() <= max)
            .collect(Collectors.toList());
    }

    public void runMenu(List<Product> products) throws IOException {
        Scanner sc = new Scanner(System.in);
        boolean running = true;
        while (running) {
            System.out.println("\n=== INVENTORY MANAGER ===");
            System.out.println("1. Lihat semua");
            System.out.println("2. Tambah produk");
            System.out.println("3. Update stok");
            System.out.println("4. Hapus produk");
            System.out.println("5. Cari produk");
            System.out.println("6. Sort produk");
            System.out.println("7. Filter harga");
            System.out.println("8. Simpan & Keluar");
            System.out.print("Pilih: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    viewAll(products);
                    break;
                case "2":
                    addProduct(products, sc);
                    break;
                case "3":
                    updateQuantity(products, sc);
                    break;
                case "4":
                    deleteProduct(products, sc);
                    break;
                case "5":
                    System.out.print("Keyword: ");
                    String keyword = sc.nextLine();
                    List<Product> found = searchProducts(products, keyword);
                    viewAll(found);
                    break;
                case "6":
                    System.out.print("Sort by (price/quantity): ");
                    String criteria = sc.nextLine();
                    sortProducts(products, criteria);
                    viewAll(products);
                    break;
                case "7":
                    System.out.print("Min price: ");
                    double min = Double.parseDouble(sc.nextLine());
                    System.out.print("Max price: ");
                    double max = Double.parseDouble(sc.nextLine());
                    List<Product> filtered = filterByPrice(products, min, max);
                    viewAll(filtered);
                    break;
                case "8":
                    running = false;
                    break;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
    }

    private void viewAll(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("Tidak ada produk.");
            return;
        }
        for (Product p : products) {
            System.out.printf("ID: %d | Nama: %s | Kategori: %s | Harga: %.2f | Stok: %d%n",
                    p.getId(), p.getName(), p.getCategory(), p.getPrice(), p.getQuantity());
        }
    }

    private void addProduct(List<Product> products, Scanner sc) {
        System.out.print("Nama: ");
        String name = sc.nextLine();
        System.out.print("Kategori: ");
        String category = sc.nextLine();
        System.out.print("Harga: ");
        double price = Double.parseDouble(sc.nextLine());
        System.out.print("Stok: ");
        int quantity = Integer.parseInt(sc.nextLine());

        int newId = products.stream().mapToInt(Product::getId).max().orElse(0) + 1;
        Product newProduct = new Product(newId, name, category, price, quantity);
        products.add(newProduct);
        System.out.println("Produk berhasil ditambahkan.");
    }

    private void updateQuantity(List<Product> products, Scanner sc) {
        System.out.print("ID produk: ");
        int id = Integer.parseInt(sc.nextLine());
        Optional<Product> opt = products.stream().filter(p -> p.getId() == id).findFirst();
        if (opt.isPresent()) {
            System.out.print("Stok baru: ");
            int qty = Integer.parseInt(sc.nextLine());
            opt.get().setQuantity(qty);
            System.out.println("Stok diperbarui.");
        } else {
            System.out.println("Produk tidak ditemukan.");
        }
    }

    private void deleteProduct(List<Product> products, Scanner sc) {
        System.out.print("ID produk yang dihapus: ");
        int id = Integer.parseInt(sc.nextLine());
        boolean removed = products.removeIf(p -> p.getId() == id);
        if (removed) {
            System.out.println("Produk berhasil dihapus.");
        } else {
            System.out.println("Produk tidak ditemukan.");
        }
    }
}
