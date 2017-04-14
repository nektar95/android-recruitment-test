package dog.snow.androidrecruittest.fetching;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by pc on 14.04.2017.
 */

public class ApiClient {
    private static final String BASE_URL = "http://10.0.2.2:8080";
    private static Retrofit mRetrofit = null;

    public static Retrofit getClient() {
        if (mRetrofit==null) {
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return mRetrofit;
    }
}
