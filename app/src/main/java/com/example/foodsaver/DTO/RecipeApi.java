package com.example.foodsaver.DTO;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecipeApi {
    // SEARCH
    @GET("findByIngredients")
    Call<List<RecipeApus>> searchRecipe(@Query("apiKey") String apiKey, @Query("ingredients") String ingredients, @Query("number") int number);
}
