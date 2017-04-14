package dog.snow.androidrecruittest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import dog.snow.androidrecruittest.fetching.ApiClient;
import dog.snow.androidrecruittest.fetching.ApiInterface;
import dog.snow.androidrecruittest.model.Item;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    public static String TAG = "RetroMain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         fetchData();
    }

    private void fetchData(){
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<List<Item>> call = apiInterface.getSnowDogItems();
        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if(response.isSuccessful()){
                    AppContainer.get(getApplicationContext()).clearDatabase();
                    List<Item> items = response.body();
                    for (Item item : items){
                        AppContainer.get(getApplicationContext()).addItem(item);
                    }
                    for (Item item : AppContainer.get(getApplicationContext()).getItems()){
                        Log.i(TAG,item.getId().toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Log.i(TAG,t.toString());
            }
        });
    }
}
