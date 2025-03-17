package nl.inholland.endassignment.endproject.utils;

import nl.inholland.endassignment.endproject.models.Sale;
import nl.inholland.endassignment.endproject.models.Showing;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Database implements Serializable {
    private static final long serialVersionUID = 1L; // Explicit serialVersionUID
    private List<Showing> showings;
    private List<Sale> salesHistory;

    public Database() {
        // Try loading from a serialized file, or seed data if the file does not exist
        Database loadedDatabase = Serializer.deserialize(Database.class);
        if (loadedDatabase != null) {
            this.showings = loadedDatabase.showings;
            this.salesHistory = loadedDatabase.salesHistory;

            // Debugging check
            for (Sale sale : salesHistory) {
                if (sale.getSaleDate() == null) {
                    System.err.println("Found sale with null saleDate for customer: " + sale.getCustomerName());
                }
            }

        } else {
            this.showings = new ArrayList<>();
            this.salesHistory = new ArrayList<>();
            seedData();
        }


    }

    private void seedData() {
        if (showings.isEmpty()) {  // Seed data only if there are no existing showings
            showings.add(new Showing("Avengers: Endgame", LocalDateTime.of(2025, 1, 20, 14, 0), LocalDateTime.of(2025, 1, 20, 16, 30), 72));
            showings.add(new Showing("Inception", LocalDateTime.of(2025, 1, 21, 19, 0), LocalDateTime.of(2025, 1, 21, 21, 30), 72));
        }
        if (salesHistory.isEmpty()) {  // Seed sales history only if it's empty
        }
    }


    public List<Showing> getShowings() {
        return showings;
    }

    public void addShowing(Showing showing) {
        showings.add(showing);
    }

    public void removeShowing(Showing showing) {
        showings.remove(showing);
    }

    public void addSale(Sale sale) {
        salesHistory.add(sale);
    }

    public List<Sale> getSalesHistory() {
        return salesHistory;
    }

    public void save() {
        Serializer.serialize(this);
    }
}
