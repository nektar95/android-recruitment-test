package dog.snow.androidrecruittest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import dog.snow.androidrecruittest.fetching.ApiClient;
import dog.snow.androidrecruittest.fetching.ApiError;
import dog.snow.androidrecruittest.fetching.ApiInterface;
import dog.snow.androidrecruittest.model.Item;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SearchInterface {
    public static String TAG = "RetroMain";
    private RecyclerView mItemRecyclerView;
    private ItemsAdapter mItemsAdapter;
    private List<Item> mItemsResults;
    private TextView mEmptyTextView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String mSearchQuery;
    private LinearLayoutManager mLinearLayoutManager;

    public void scrollView(String queary){
        mSearchQuery = queary;
        scrollToPosition();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSearchQuery = "";
        mItemsResults = new ArrayList<>();
        if(savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.fragment_container, SearchFragment.newInstance());
            transaction.commit();
        }

        mItemRecyclerView = (RecyclerView) findViewById(R.id.items_rv);
        mEmptyTextView = (TextView) findViewById(R.id.empty_list_tv);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchData();
            }
        });

        mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mItemRecyclerView.setLayoutManager(mLinearLayoutManager);
        mItemsAdapter = new ItemsAdapter(mItemsResults);
        mItemRecyclerView.setAdapter(mItemsAdapter);

        if(AppContainer.get(getApplicationContext()).getItems().size()==0){
            fetchData();
        } else {
            updateUI();
        }
    }

    private void scrollToPosition(){
        String name,desc;
        for (Item item : mItemsResults){
            name = item.getName().toLowerCase();
            desc = item.getDescription().toLowerCase();
            if(name.contains(mSearchQuery) || desc.contains(mSearchQuery)){
                mLinearLayoutManager.scrollToPositionWithOffset(item.getId(),0);
                break;
            }
        }
    }

    private void updateUI(){
        mSwipeRefreshLayout.setRefreshing(false);
        mItemsResults.clear();
        mItemsAdapter.notifyDataSetChanged();
        mItemsResults.addAll(AppContainer.get(getApplicationContext()).getItems());
        if(mItemsResults.size()>0){
            mEmptyTextView.setVisibility(View.GONE);
            mItemsAdapter.notifyDataSetChanged();
        } else {
            mEmptyTextView.setVisibility(View.VISIBLE);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void fetchData(){
        if(!isNetworkAvailable()){
            Toast.makeText(getApplicationContext(),getString(R.string.internet_connection),Toast.LENGTH_LONG).show();
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<List<Item>> call = apiInterface.getSnowDogItems();
        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if(response.isSuccessful()){
                    AppContainer.get(getApplicationContext()).clearDatabase();
                    PicassoTools.clearCache(Picasso.with(getApplicationContext()));
                    List<Item> items = response.body();
                    for (Item item : items){
                        AppContainer.get(getApplicationContext()).addItem(item);
                    }
                    updateUI();
                } else {
                    Converter<ResponseBody, ApiError> converter = ApiClient.getClient()
                            .responseBodyConverter(ApiError.class, new Annotation[0]);

                    ApiError error;

                    try {
                        error = converter.convert(response.errorBody());
                    } catch (IOException e) {
                        error = new ApiError();
                    }

                    Toast.makeText(getApplicationContext(),getString(R.string.loading_error),Toast.LENGTH_LONG).show();
                    Log.i(TAG,error.toString());
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Toast.makeText(getApplicationContext(),getString(R.string.loading_error),Toast.LENGTH_LONG).show();
                Log.i(TAG,t.toString());
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private class ItemHolder extends RecyclerView.ViewHolder{
        private ImageView mImageView;
        private TextView mName;
        private TextView mDescription;
        private ConstraintLayout mConstraintLayout;

        public void clearAnimation() {
            mConstraintLayout.clearAnimation();
        }

        public ItemHolder(View itemView){
            super(itemView);
            mConstraintLayout = (ConstraintLayout) itemView.findViewById(R.id.constraintLayout);
            mName = (TextView) itemView.findViewById(R.id.name_tv);
            mDescription = (TextView) itemView.findViewById(R.id.description_tv);
            mImageView = (ImageView) itemView.findViewById(R.id.icon_ic);
        }

        public void bindResult(final Item item){
            mName.setText(item.getName());
            mDescription.setText(item.getDescription());
            Picasso.with(getApplicationContext()).load(item.getUrl())
                    .resize(64,64)
                    .centerCrop()
                    .into(mImageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Log.i(TAG,"Can't load picture: "+item.getId());
                        }

                    });
        }
    }

    private class ItemsAdapter extends RecyclerView.Adapter<ItemHolder> {
        private List<Item> mItems;
        private int lastPosition = -1;

        public ItemsAdapter(List<Item> items) {
            mItems = items;
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            View view = layoutInflater.inflate(R.layout.item, parent, false);
            return new ItemHolder(view);
        }
        @Override
        public void onViewDetachedFromWindow(final ItemHolder holder) {
            super.onViewDetachedFromWindow(holder);
            holder.clearAnimation();
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            Item item = mItems.get(position);
            holder.bindResult(item);
            if(position> lastPosition) {
                AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
                animation.setDuration(250);
                animation.setFillAfter(true);
                holder.mConstraintLayout.startAnimation(animation);
                lastPosition = position;
            }
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }
    }
}
