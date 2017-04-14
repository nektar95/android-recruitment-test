package dog.snow.androidrecruittest.fetching;

import java.util.List;

import dog.snow.androidrecruittest.model.Item;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by pc on 14.04.2017.
 */

public interface ApiInterface {
    @GET("/api/items")
    Call<List<Item>> getSnowDogItems();

    @GET("/api/items/{id}")
    Call<Item> getSnowDogItem(@Path("id") int id);
}
