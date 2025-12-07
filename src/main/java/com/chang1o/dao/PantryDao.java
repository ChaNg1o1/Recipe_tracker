package com.chang1o.dao;

import com.chang1o.model.PantryItem;
import com.chang1o.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PantryDao {
    public boolean addPantryItem(PantryItem item){
        String sql = "INSERT INTO pantry(user_id,ingredient_id,quantity,expiry_date)VALUES(?,?,?,?)";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);

            stmt.setInt(1,item.getUserId());
            stmt.setInt(2,item.getIngredientId());
            stmt.setString(3,item.getQuantity());

            if (item.getExpiryDate() != null){
                stmt.setDate(4,Date.valueOf(item.getExpiryDate()));
            }else{
                stmt.setNull(4,Types.DATE); //DATE类型的Null
            }

            int rowAffected = stmt.executeUpdate();

            //同步ID
            if (rowAffected > 0){
                ResultSet key = stmt.getGeneratedKeys();
                if (key.next()){
                    item.setId(key.getInt(1));
                }
            }

            return rowAffected > 0;

        }catch (SQLException e){
            System.err.println("添加库存物品失败：" + e.getMessage());
            return false;
        }finally {
            closeResources(stmt,null,conn);
        }
    }

    public PantryItem getPantryItemById(int itemId){
        String sql = "SELECT id,user_id,ingredient_id,quantity,expiry_date FROM pantry WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1,itemId);
            rs = stmt.executeQuery();

            if (rs.next()){
                PantryItem item = new PantryItem();
                item.setId(rs.getInt("id"));
                item.setUserId(rs.getInt("user_id"));
                item.setIngredientId(rs.getInt("ingredient_id"));
                item.setQuantity(rs.getString("quantity"));

                Date expiryDate = rs.getDate("expiry_date");
                if (expiryDate != null){
                    item.setExpiryDate(expiryDate.toLocalDate());
                }
                return item;
            }
            return null;
        }catch (SQLException e){
            System.err.println("根据ID查询库存物品失败：" + e.getMessage());
            return null;
        }finally {
            closeResources(stmt,rs,conn);
        }
    }

    public List<PantryItem> getPantryItemsByUser(int userId){
        List<PantryItem> items = new ArrayList<>();
        String sql = "SELECT id,user_id,ingredient_id,quantity,expiry_date FROM pantry WHERE user_id = ? ORDER BY expiry_date";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1,userId);
            rs = stmt.executeQuery();

            while (rs.next()){
                PantryItem item = new PantryItem();
                item.setId(rs.getInt("id"));
                item.setUserId(rs.getInt("user_id"));
                item.setIngredientId(rs.getInt("ingredient_id"));
                item.setQuantity(rs.getString("quantity"));
                Date expiry_date = rs.getDate("expiry_date");
                if (expiry_date != null){
                    item.setExpiryDate(expiry_date.toLocalDate());
                }
                items.add(item);
            }
        } catch (SQLException e) {
            System.err.println("获取用户库存失败：" + e.getMessage());
        }finally {
            closeResources(stmt,rs,conn);
        }
        return items;
    }

    public List<PantryItem> getExpiringItems(int userId,int days){
        List<PantryItem> items = new ArrayList<>();
        String sql =
                "SELECT id,user_id,ingredient_id,quantity,expiry_date FROM pantry" +
                "WHERE user_id = ? AND expiry_date IS NOT NULL" +
                "AND expiry_date <= DATE_ADD(CURDATE(),INTERVAL ? DAY)" +
                "ORDER BY expiry_date";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()){
                PantryItem item = new PantryItem();
                item.setId(rs.getInt("id"));
                item.setUserId(rs.getInt("user_id"));
                item.setIngredientId(rs.getInt("ingredient_id"));
                item.setQuantity(rs.getString("quantity"));

                Date expirDate = rs.getDate("expiry_date");
                if (expirDate != null){
                    item.setExpiryDate(expirDate.toLocalDate());
                }
                items.add(item);
            }
        } catch (SQLException e) {
           System.err.println("获取即将过期物品失败：" + e.getMessage());
        }finally {
            closeResources(stmt,rs,conn);
        }
        return items;
    }

    public List<PantryItem> getExpiredItems(int userId){
        List<PantryItem> items = new ArrayList<>();
        String sql =
                "SELECT id,user_id,ingredient_id,quantity,expiry_date FROM pantry" +
                "WHERE user_id = ? AND expiry_date IS NOT NULL" +
                "AND expiry_date < CURDATE()" +
                "ORDER BY expiry_date";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()){
                PantryItem item = new PantryItem();
                item.setId(rs.getInt("id"));
                item.setUserId(rs.getInt("user_id"));
                item.setIngredientId(rs.getInt("ingredient_id"));
                item.setQuantity(rs.getString("quantity"));

                Date expiryDate = rs.getDate("expiry_date");
                if (expiryDate != null){
                    item.setExpiryDate(expiryDate.toLocalDate());
                }
                items.add(item);
            }
        }catch(SQLException e){
            System.err.println("获取已过期物品失败：" + e.getMessage());
        }finally {
            closeResources(stmt,rs,conn);
        }
        return items;
    }

    public boolean updatePantryItem(PantryItem item){
        String sql = "UPDATE Pantry SET quantity = ?,expiry_date = ? WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1,item.getQuantity());

            if (item.getExpiryDate() != null){
                stmt.setDate(2,Date.valueOf(item.getExpiryDate()));
            }else {
                stmt.setNull(2, Types.DATE);
            }

            stmt.setInt(3,item.getId());

            int rowAffected = stmt.executeUpdate();
            return rowAffected > 0; //return true
        } catch (SQLException e) {
            System.err.println("更新库存物品失败：" + e.getMessage());
            return false;
        }finally {
            closeResources(stmt,null,conn);
        }
    }

    public boolean deletePantryItem(int itemId){
        String sql = "DELETE FROM pantry WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1,itemId);

            int rowAffected = stmt.executeUpdate();
            return rowAffected > 0;
        } catch (SQLException e) {
            System.err.println("删除库存物品失败：" + e.getMessage());
            return false;
        }finally {
            closeResources(stmt,null,conn);
        }
    }

    private void closeResources(PreparedStatement stmt,ResultSet rs,Connection conn){
        if (rs != null){
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("关闭rs失败" + e.getMessage());
            }
        }

        if (stmt != null){
            try {
                stmt.close();
            }catch (SQLException e){
                System.err.println("关闭stmts失败" + e.getMessage());
            }
        }

        if (conn != null){
            DBUtil.closeConnection(conn);
        }
    }
}
