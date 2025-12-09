package com.chang1o.model;
import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private int id;
    private String name;
    private String instructions;
    private int categoryId;
    private Category category;
    private int userId;

    private List<RecipeIngredient> ingredients;

    public Recipe(){}

    public Recipe(String name, String instructions, int categoryId, int userId) {
        this.name = name;
        this.instructions = instructions;
        this.categoryId = categoryId;
        this.userId = userId;
    }

    public Recipe(int id,String name,String instructions,int categoryId,Category category,int userId){
        this.id = id;
        this.name = name;
        this.instructions = instructions;
        this.categoryId = categoryId;
        this.category = category;
        this.userId = userId;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getInstructions(){
        return instructions;
    }

    public void setInstructions(String instructions){
        this.instructions = instructions;
    }

    public int getCategoryId(){
        return categoryId;
    }

    public void setCategoryId(int categoryId){
        this.categoryId = categoryId;
    }

    public Category getCategory(){
        return category;
    }

    public void setCategory(Category category){
        this.category = category;
        if (category != null){
            this.categoryId = category.getId();
        }
    }

    public int getUserId(){
        return userId;
    }

    public void  setUserId(int userId){
        this.userId = userId;
    }

    public List<RecipeIngredient> getIngredients(){
        return  ingredients;
    }

    public void setIngredients(List<RecipeIngredient> ingredients){
        this.ingredients = ingredients;
    }

    public void addIngredient(RecipeIngredient ingredient){
        if (this.ingredients == null){
            this.ingredients = new ArrayList<>();
        }
        this.ingredients.add(ingredient);
    }

    @Override
    public String toString(){
        return "Recipe{" + "id" + id + ",name='" + name + "'"
                + ",instructions=" + instructions + ",categoryId="
                + categoryId + ",userId=" + userId + ",category="
                + (category != null ? category.getName() : "null") + "}";
    }

    public void setNmae(int name) {

    }
}
