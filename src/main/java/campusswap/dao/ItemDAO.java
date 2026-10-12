package campusswap.dao;

import campusswap.db.DBConnection;
import campusswap.model.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

    public boolean addItem(Item item) {
        String sql = "INSERT INTO items (seller_id, name, category, item_condition, price, listing_type, status, description) " +
                     "VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, item.getSellerId());
            ps.setString(2, item.getName());
            ps.setString(3, item.getCategory());
            ps.setString(4, item.getCondition());
            ps.setDouble(5, item.getPrice());
            ps.setString(6, item.getListingType().name());
            ps.setString(7, Item.Status.AVAILABLE.name());
            ps.setString(8, item.getDescription());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Everything currently available on the marketplace (the Search/Home screens). */
    public List<Item> getAvailableItems() {
        return runQuery("SELECT * FROM items WHERE status = 'AVAILABLE' ORDER BY created_at DESC");
    }

    /** Filtered search - pass null/empty to skip a filter. */
    public List<Item> search(String keyword, String category) {
        StringBuilder sql = new StringBuilder("SELECT * FROM items WHERE status = 'AVAILABLE'");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND name LIKE ?");
            params.add("%" + keyword + "%");
        }
        if (category != null && !category.isBlank() && !category.equalsIgnoreCase("All")) {
            sql.append(" AND category = ?");
            params.add(category);
        }
        sql.append(" ORDER BY created_at DESC");

        List<Item> items = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) items.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public List<Item> getItemsBySeller(int sellerId) {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items WHERE seller_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) items.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    /** Called after a Buy/Rent is confirmed, so the item drops off the marketplace. */
    public boolean updateStatus(int itemId, Item.Status status) {
        String sql = "UPDATE items SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, itemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** For the admin dashboard: pull a listing entirely (e.g. reported item). */
    public boolean deleteItem(int itemId) {
        String sql = "DELETE FROM items WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private List<Item> runQuery(String sql) {
        List<Item> items = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) items.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    private Item mapRow(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setId(rs.getInt("id"));
        item.setSellerId(rs.getInt("seller_id"));
        item.setName(rs.getString("name"));
        item.setCategory(rs.getString("category"));
        item.setCondition(rs.getString("item_condition"));
        item.setPrice(rs.getDouble("price"));
        item.setListingType(Item.ListingType.valueOf(rs.getString("listing_type")));
        item.setStatus(Item.Status.valueOf(rs.getString("status")));
        item.setDescription(rs.getString("description"));
        return item;
    }
}
