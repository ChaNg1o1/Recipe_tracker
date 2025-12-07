package com.chang1o.dao;

import com.chang1o.model.Recipe;
import com.chang1o.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RecipeDao {
    public boolean addRecipe(Recipe recipe){
        String sql = "INSERT INTO recipe (name,instruction,category_id,user_id) VALUES(?,?,?,?)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try{
            conn = DBUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1,recipe.getName());
            stmt.setString(2,recipe.getInstructions());
            stmt.setInt(3,recipe.getCategoryId());
            stmt.setInt(4,recipe.getUserId());

            int rowAffected =  stmt.executeUpdate();

            if (rowAffected > 0){
                ResultSet key = stmt.getGeneratedKeys();
                if (key.next()){
                    recipe.setId(key.getInt(1));
                }
            }

            return rowAffected > 0;

        }catch (SQLException e){
            System.err.println("添加食谱失败：" + e.getMessage());
            return false;
        }finally {
            closeResources(stmt,null,conn);
        }

    }

    public Recipe getRecipeById(int recipeId){
        String sql = "SELECT id,name,instruction,category_id,user_id FROM recipes WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            conn = DBUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            if (rs.next()){
                Recipe recipe = new Recipe();
                recipe.setId(rs.getInt("id"));
                recipe.setName(rs.getString("name"));
                recipe.setInstructions(rs.getString("instruction"));
                recipe.setCategoryId(rs.getInt("category_id"));
                recipe.setUserId(rs.getInt("user_id"));
                return recipe;
            }
            return null;
        } catch (SQLException e) {
            System.err.println("根据ID查询食谱失败：" + e.getMessage());
            return null;
        }finally {
            closeResources(stmt,rs,conn);
        }
    }

    public Recipe getRecipeByName(String name){
        String sql = "SELECT id,name,instruction,category_id,user_id FROM recipes WHERE name = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            conn = DBUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            if (rs.next()){
                Recipe recipe = new Recipe();
                recipe.setUserId(rs.getInt("id"));
                recipe.setName(rs.getString("name"));
                recipe.setInstructions(rs.getString("instruction"));
                recipe.setCategoryId(rs.getInt("category_id"));
                recipe.setUserId(rs.getInt("user_id"));
                return recipe;
            }

            return null;

        } catch (SQLException e) {
            System.err.println("根据名称查询食谱失败：" + e.getMessage());
            return null;
        }finally {
            closeResources(stmt,rs,conn);
        }
    }

    public List<Recipe> getAllRecipe(){
        List<Recipe> recipes = new ArrayList<>();
        String sql = "SELECT id,name,instruction,category_id,user_id FROM recipes ORDER BY name";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            conn = DBUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()){
                Recipe recipe = new Recipe();
                recipe.setId(rs.getInt("id"));
                recipe.setName(rs.getString("name"));
                recipe.setInstructions(rs.getString("instruction"));
                recipe.setCategoryId(rs.getInt("category_id"));
                recipe.setUserId(rs.getInt("user_id"));
                recipes.add(recipe);
            }
            return null;
        } catch (SQLException e) {
            System.err.println("查询食谱失败：" + e.getMessage());
        }finally {
            closeResources(stmt,rs,conn);
        }
        return recipes;
    }

    public List<Recipe> getRecipesByCategoryId(int categoryId){
        List<Recipe> recipes = new ArrayList<>();
        String sql = "SELECT id,name,instruction,category_id,user_id FROM recipes WHERE category_id = ? ORDER BY name";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
           conn = DBUtil.getInstance().getConnection();
           stmt = conn.prepareStatement(sql);
           stmt.setInt(1,categoryId);
           rs = stmt.executeQuery();

            while (rs.next()){
                Recipe recipe = new Recipe();
                recipe.setId(rs.getInt("id"));
                recipe.setName(rs.getString("name"));
                recipe.setInstructions(rs.getString("instruction"));
                recipe.setCategoryId(rs.getInt("category_id"));
                recipe.setUserId(rs.getInt("user_id"));
                recipes.add(recipe);
            }
        }catch (SQLException e){
            System.err.println("根据分类查询食谱失败：" + e.getMessage());
        }finally {
            closeResources(stmt,rs,conn);
        }
        return recipes;
    }

    public List<Recipe> searchRecipes(String keyword){
        List<Recipe> recipes = new ArrayList<>();
        String sql = "SELECT id,name,instruction,category_id,user_id FROM recipes WHERE name LIKE ? ORDER BY name";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            conn = DBUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()){
                Recipe recipe = new Recipe();
                recipe.setId(rs.getInt("id"));
                recipe.setName(rs.getString("name"));
                recipe.setInstructions(rs.getString("instruction"));
                recipe.setCategoryId(rs.getInt("category_id"));
                recipe.setUserId(rs.getInt("user_id"));
                recipes.add(recipe);
            }

        }catch (SQLException e){
            System.err.println("根据关键词搜索食谱失败：" + e.getMessage());
        }finally {
            closeResources(stmt,rs,conn);
        }
        return recipes;
    }


    public List<Recipe> getRecipesByUser(int userId) {
        List<Recipe> recipes = new ArrayList<>();
        String sql = "SELECT id, name, instructions, category_id, user_id FROM recipes WHERE user_id = ? ORDER BY name";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Recipe recipe = new Recipe();
                    recipe.setId(rs.getInt("id"));
                    recipe.setName(rs.getString("name"));
                    recipe.setInstructions(rs.getString("instructions"));
                    recipe.setCategoryId(rs.getInt("category_id"));
                    recipe.setUserId(rs.getInt("user_id"));
                    recipes.add(recipe);
                }
            }

        } catch (SQLException e) {
            System.err.println("查询用户食谱失败：" + e.getMessage());
        }

        return recipes;
    }

    public boolean updateRecipe(Recipe recipe){
        String sql = "UPDATE recipes SET name = ? , instruction SET ? , category_id SET ? , user_id SET ? , WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try{
            conn = DBUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1,recipe.getName());
            stmt.setString(2,recipe.getInstructions());
            stmt.setInt(3,recipe.getCategoryId());
            stmt.setInt(4,recipe.getUserId());
            stmt.setInt(5,recipe.getId());

            int rowAffected = stmt.executeUpdate();
            return rowAffected > 0;
        }catch (SQLException e){
            System.err.println("更新食谱失败：" + e.getMessage());
            return false;
        }finally {
            closeResources(stmt,null,conn);
        }
    }

    public boolean deleteRecipe(int recipeId){
        String sql = "DELETE FROM recipes WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1,recipeId);

            int rowAffected = stmt.executeUpdate();
            return rowAffected > 0;

        } catch (SQLException e) {
            System.err.println("删除食谱失败：" + e.getMessage());
            return false;
        }finally {
            closeResources(stmt,null,conn);
        }
    }

        public boolean exists(int recipeId){
            return getRecipeById(recipeId) != null;
        }

    public void closeResources(PreparedStatement stmt,ResultSet rs,Connection conn){
        if (rs != null){
           try {
               rs.close();
           } catch (SQLException e) {
               System.err.println("关闭rs失败：" + e.getMessage());
           }
        }

        if (stmt != null){
            try{
                stmt.close();
            }catch (SQLException e){
                System.err.println("关闭stmt失败：" + e.getMessage());
            }
        }

        if(conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("关闭conn失败：" + e.getMessage());
            }
        }
    }
}
