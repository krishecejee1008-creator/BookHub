package BookHub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

// --- CUSTOM EXCEPTIONS ---
class OutOfStockException extends Exception {
    public OutOfStockException(String message) { super(message); }
}

class ItemNotFoundException extends Exception {
    public ItemNotFoundException(String message) { super(message); }
}

class EmptyCartException extends Exception {
    public EmptyCartException(String message) { super(message); }
}

// --- CORE CLASSES ---
class User {
    private String username;
    private String password;
    private ArrayList<Book> cart = new ArrayList<>();

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public ArrayList<Book> getCart() { return cart; }

    public void addToCart(Book book) { cart.add(book); }
    public void removeFromCart(Book book) { cart.remove(book); }
    public void clearCart() { cart.clear(); }
    public void setPassword(String password) { this.password = password; }
}

class Book {
    String itemID;
    String title;
    String author;
    Double price;
    int stockCount;

    public Book(String itemID, String title, String author, Double price, int stockCount) {
        this.itemID = itemID;
        this.title = title;
        this.author = author;
        this.price = price;
        this.stockCount = stockCount;
    }
}

class Order {
    String orderID;
    String username;
    ArrayList<Book> items;
    Double totalAmount;

    public Order(String username, ArrayList<Book> items, Double totalAmount) {
        this.orderID = "ORD-" + ThreadLocalRandom.current().nextInt(10000, 99999);
        this.username = username;
        this.items = new ArrayList<>(items);
        this.totalAmount = totalAmount;
    }
}

// --- MANAGERS ---
class InventoryManager {
    ArrayList<Book> myBooks = new ArrayList<>();

    public InventoryManager() {
        myBooks.add(new Book("1", "The Java Guide", "Krishna Jee", 29.99, 5));
        myBooks.add(new Book("2", "Head First OOP", "Kathy Sierra", 45.50, 3));
    }

    public void displayAllBooks() {
        System.out.println("\n--- Available Books in BookHub ---");
        for (Book book : myBooks) {
            System.out.println("[" + book.itemID + "] " + book.title + " by " + book.author +
                    " | Price: $" + book.price + " | Stock: " + book.stockCount);
        }
        System.out.println("----------------------------------");
    }

    public Book findBook(String itemID) throws ItemNotFoundException {
        for (Book book : myBooks) {
            if (book.itemID.equals(itemID.trim())) {
                return book;
            }
        }
        throw new ItemNotFoundException("Error: Book ID [" + itemID + "] not found in inventory!");
    }

    public void verifyAndDecrementStock(Book book) throws OutOfStockException {
        if (book.stockCount <= 0) {
            throw new OutOfStockException("Error: '" + book.title + "' is currently out of stock.");
        }
        book.stockCount--;
    }
}

class UserManager {
    HashMap<String, User> eUser = new HashMap<>();

    User registerUser(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            System.out.println("Invalid Credentials! Fields cannot be empty.");
            return null;
        }
        if (eUser.containsKey(username)) {
            System.out.println("User already exists! Please login instead.");
            return null;
        }
        User newUser = new User(username, password);
        eUser.put(username, newUser);
        System.out.println("Registered Successfully!");
        return newUser;
    }

    User loginUser(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            System.out.println("Invalid Credentials! Fields cannot be empty.");
            return null;
        }

        User oldUser = eUser.get(username);
        if (oldUser == null) {
            System.out.println("User not found! Please register first.");
            return null;
        }

        Scanner scan = new Scanner(System.in);
        String currentPasswordAttempt = password;

        while (true) {
            if (currentPasswordAttempt.equals(oldUser.getPassword())) {
                System.out.println("Login successful! Welcome " + oldUser.getUsername());
                return oldUser;
            }

            System.out.println("Login Failed!");
            System.out.println("-----Send_OTP(S)------|------Try_Again(T)------");
            String choiceStr = scan.nextLine();
            if(choiceStr.isEmpty()) continue;
            char loginMethod = choiceStr.charAt(0);

            if (loginMethod == 'S' || loginMethod == 's') {
                int otp = ThreadLocalRandom.current().nextInt(1000, 10000);
                System.out.println("Simulated OTP sent: " + otp);
                System.out.print("Enter OTP : ");
                int enterOTP = scan.nextInt();
                scan.nextLine();

                if (enterOTP == otp) {
                    System.out.print("Enter new Password : ");
                    String resetPassword = scan.nextLine();
                    oldUser.setPassword(resetPassword);
                    System.out.println("Password has been reset.");
                    currentPasswordAttempt = resetPassword;
                } else {
                    System.out.println("Wrong OTP!");
                }
            } else {
                System.out.print("Enter your Password : ");
                currentPasswordAttempt = scan.nextLine();
            }
        }
    }
}

// --- MAIN APPLICATION ---
public class BookHub {
    private static ArrayList<Order> transactionHistory = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserManager uManager = new UserManager();
        InventoryManager iManager = new InventoryManager();
        User currentUser = null;

        System.out.println("--------Welcome To BookHub---------");

        while (true) {
            if (currentUser == null) {
                System.out.println("\n--- [GUEST MODE] ---");
                System.out.println("---Register(R)---|---Login(L)---|---Surf Books(G)---|---Exit(E)---");
                System.out.print("Choose an option: ");
                String opt = scanner.nextLine().trim();
                if (opt.isEmpty()) continue;
                char choice = opt.charAt(0);

                if (choice == 'R' || choice == 'r') {
                    System.out.print("Enter your Username : ");
                    String user = scanner.nextLine();
                    System.out.print("Enter your Password : ");
                    String pass = scanner.nextLine();
                    currentUser = uManager.registerUser(user, pass);

                } else if (choice == 'L' || choice == 'l') {
                    System.out.print("Enter your Username : ");
                    String user = scanner.nextLine();
                    System.out.print("Enter your Password : ");
                    String pass = scanner.nextLine();
                    currentUser = uManager.loginUser(user, pass);

                } else if (choice == 'G' || choice == 'g') {
                    iManager.displayAllBooks();
                    System.out.println("⚠️ Note: You are viewing as a Guest. Log in to add items to a cart.");
                    System.out.println("Press Enter to return...");
                    scanner.nextLine();

                } else if (choice == 'E' || choice == 'e') {
                    System.out.println("Thank you for visiting! Exiting...");
                    break;
                }
            } else {
                System.out.println("\n--- [MEMBER DASHBOARD - Welcome " + currentUser.getUsername() + "] ---");
                System.out.println("---Browse & Add(B)---|---View/Manage Cart(C)---|---History/Return(H)---|---Logout(O)---|---Exit(E)---");
                System.out.print("Choose an option: ");
                String opt = scanner.nextLine().trim();
                if (opt.isEmpty()) continue;
                char choice = opt.charAt(0);

                if (choice == 'B' || choice == 'b') {
                    iManager.displayAllBooks();
                    System.out.print("Enter Book ID to Add to Cart (or press Enter to go back): ");
                    String idInput = scanner.nextLine().trim();
                    if (!idInput.isEmpty()) {
                        try {
                            Book book = iManager.findBook(idInput);
                            iManager.verifyAndDecrementStock(book); // Updates available quantity
                            currentUser.addToCart(book);
                            System.out.println(" [" + book.title + "] added to cart!");
                        } catch (ItemNotFoundException | OutOfStockException e) {
                            System.out.println(e.getMessage());
                        }
                    }

                } else if (choice == 'C' || choice == 'c') {
                    manageCartMenu(currentUser, iManager, scanner);

                } else if (choice == 'H' || choice == 'h') {
                    manageReturnsMenu(currentUser, scanner);

                } else if (choice == 'O' || choice == 'o') {
                    currentUser = null;
                    System.out.println("Successfully Logged Out!");

                } else if (choice == 'E' || choice == 'e') {
                    System.out.println("Thank you for visiting! Exiting...");
                    break;
                }
            }
        }
        scanner.close();
    }

    private static void manageCartMenu(User user, InventoryManager iManager, Scanner scanner) {
        while (true) {
            System.out.println("\n--- Your Shopping Cart ---");
            ArrayList<Book> cart = user.getCart();
            if (cart.isEmpty()) {
                System.out.println("Your cart is empty.");
                System.out.println("Press Enter to return to main dashboard...");
                scanner.nextLine();
                return;
            }

            double total = 0;
            for (int i = 0; i < cart.size(); i++) {
                Book b = cart.get(i);
                System.out.println((i + 1) + ". [" + b.itemID + "] " + b.title + " - $" + b.price);
                total += b.price;
            }
            System.out.println("--------------------------");
            System.out.println("Total Amount: $" + total);
            System.out.println("Options: [P] Place Order / Checkout | [R] Remove Item | [B] Back");
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim().toLowerCase();

            if (choice.equals("p")) {
                // Place Order / Issue Book
                Order newOrder = new Order(user.getUsername(), cart, total);
                transactionHistory.add(newOrder);
                user.clearCart();
                System.out.println(" Order successfully placed! Order ID: " + newOrder.orderID);
                return;
            } else if (choice.equals("r")) {
                // Remove Item from Cart
                System.out.print("Enter Item ID to remove: ");
                String rmId = scanner.nextLine().trim();
                Book match = null;
                for (Book b : cart) {
                    if (b.itemID.equals(rmId)) {
                        match = b;
                        break;
                    }
                }
                if (match != null) {
                    user.removeFromCart(match);
                    match.stockCount++; // Dynamic update to available quantity
                    System.out.println(" Removed '" + match.title + "' from cart.");
                } else {
                    System.out.println(" Item ID not found in your cart.");
                }
            } else {
                return;
            }
        }
    }

    private static void manageReturnsMenu(User user, Scanner scanner) {
        System.out.println("\n--- Transaction History ---");
        ArrayList<Order> userOrders = new ArrayList<>();
        for (Order o : transactionHistory) {
            if (o.username.equals(user.getUsername())) {
                userOrders.add(o);
            }
        }

        if (userOrders.isEmpty()) {
            System.out.println("No past orders found.");
            return;
        }

        for (Order o : userOrders) {
            System.out.println("\nOrder ID: " + o.orderID + " | Total: $" + o.totalAmount);
            for (Book b : o.items) {
                System.out.println("  • [" + b.itemID + "] " + b.title);
            }
        }
        System.out.println("---------------------------------");
        System.out.print("To return a book, enter its Book ID (or press Enter to go back): ");
        String returnId = scanner.nextLine().trim();

        if (!returnId.isEmpty()) {
            boolean itemReturned = false;
            for (Order o : userOrders) {
                for (Book b : o.items) {
                    if (b.itemID.equals(returnId)) {
                        b.stockCount++; // Update Available Quantity on return
                        o.items.remove(b);
                        System.out.println(" Book '" + b.title + "' returned successfully and re-stocked!");
                        itemReturned = true;
                        break;
                    }
                }
                if (itemReturned) {
                    if (o.items.isEmpty()) transactionHistory.remove(o);
                    break;
                }
            }
            if (!itemReturned) {
                System.out.println(" You have not ordered or borrowed a book with that ID.");
            }
        }
    }
}