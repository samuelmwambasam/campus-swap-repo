package campusswap.dao;

import campusswap.db.DBConnection;
import campusswap.model.Item;
import campusswap.model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    private final ItemDAO itemDAO = new ItemDAO();

    /**
     * Creates the transaction record AND flips the item's status so it
     * disappears from the marketplace. Both statements succeed or neither does.
     */
    public boolean createTransaction(Transaction tx) {
        String sql = "INSERT INTO transactions (item_id, buyer_id, seller_id, transaction_type, pickup_location, total, status) " +
                     "VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, tx.getItemId());
                ps.setInt(2, tx.getBuyerId());
                ps.setInt(3, tx.getSellerId());
                ps.setString(4, tx.getType().name());
                ps.setString(5, tx.getPickupLocation());
                ps.setDouble(6, tx.getTotal());
                ps.setString(7, Transaction.Status.CONFIRMED.name());
                ps.executeUpdate();
            }

            Item.Status newStatus = tx.getType() == Transaction.Type.RENT
                    ? Item.Status.RENTED
                    : Item.Status.SOLD;

            try (PreparedStatement ps2 = conn.prepareStatement("UPDATE items SET status = ? WHERE id = ?")) {
                ps2.setString(1, newStatus.name());
                ps2.setInt(2, tx.getItemId());
                ps2.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Transaction> getTransactionsForUser(int userId) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE buyer_id = ? OR seller_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        Transaction tx = new Transaction();
        tx.setId(rs.getInt("id"));
        tx.setItemId(rs.getInt("item_id"));
        tx.setBuyerId(rs.getInt("buyer_id"));
        tx.setSellerId(rs.getInt("seller_id"));
        tx.setType(Transaction.Type.valueOf(rs.getString("transaction_type")));
        tx.setPickupLocation(rs.getString("pickup_location"));
        tx.setTotal(rs.getDouble("total"));
        tx.setStatus(Transaction.Status.valueOf(rs.getString("status")));
        return tx;
    }
}