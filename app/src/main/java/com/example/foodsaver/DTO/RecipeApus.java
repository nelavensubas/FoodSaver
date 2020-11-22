package com.example.foodsaver.DTO;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecipeApus {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("imageType")
    @Expose
    private String imageType;
    @SerializedName("usedIngredientCount")
    @Expose
    private Integer usedIngredientCount;
    @SerializedName("missedIngredientCount")
    @Expose
    private Integer missedIngredientCount;
    @SerializedName("missedIngredients")
    @Expose
    private List<MissedIngredient> missedIngredients = null;
    @SerializedName("usedIngredients")
    @Expose
    private List<UsedIngredient> usedIngredients = null;
    @SerializedName("unusedIngredients")
    @Expose
    private List<UnusedIngredient> unusedIngredients = null;
    @SerializedName("likes")
    @Expose
    private Integer likes;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public Integer getUsedIngredientCount() {
        return usedIngredientCount;
    }

    public void setUsedIngredientCount(Integer usedIngredientCount) {
        this.usedIngredientCount = usedIngredientCount;
    }

    public Integer getMissedIngredientCount() {
        return missedIngredientCount;
    }

    public void setMissedIngredientCount(Integer missedIngredientCount) {
        this.missedIngredientCount = missedIngredientCount;
    }

    public List<MissedIngredient> getMissedIngredients() {
        return missedIngredients;
    }

    public void setMissedIngredients(List<MissedIngredient> missedIngredients) {
        this.missedIngredients = missedIngredients;
    }

    public List<UsedIngredient> getUsedIngredients() {
        return usedIngredients;
    }

    public void setUsedIngredients(List<UsedIngredient> usedIngredients) {
        this.usedIngredients = usedIngredients;
    }

    public List<UnusedIngredient> getUnusedIngredients() {
        return unusedIngredients;
    }

    public void setUnusedIngredients(List<UnusedIngredient> unusedIngredients) {
        this.unusedIngredients = unusedIngredients;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

}
