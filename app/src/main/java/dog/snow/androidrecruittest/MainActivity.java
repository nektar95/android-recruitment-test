package dog.snow.androidrecruittest;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import dog.snow.androidrecruittest.fetching.ApiClient;
import dog.snow.androidrecruittest.fetching.ApiInterface;
import dog.snow.androidrecruittest.model.Item;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    public static String TAG = "RetroMain";
    private RecyclerView mItemRecyclerView;
    private ItemsAdapter mItemsAdapter;
    private List<Item> mItemsResults;
    private EditText mSearchEditText;
    private TextView mEmptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mItemRecyclerView = (RecyclerView) findViewById(R.id.items_rv);
        mEmptyTextView = (TextView) findViewById(R.id.empty_list_tv);
        mSearchEditText = (EditText) findViewById(R.id.search_et);

        mItemRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        fetchData();
    }

    private void updateUI(){
        mEmptyTextView.setVisibility(View.GONE);
        mItemsResults = AppContainer.get(getApplicationContext()).getItems();
        mItemsAdapter = new ItemsAdapter(mItemsResults);
        mItemRecyclerView.setAdapter(mItemsAdapter);
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
                    updateUI();
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Toast.makeText(getApplicationContext(),getString(R.string.loading_error),Toast.LENGTH_LONG);
                Log.i(TAG,t.toString());
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
            Picasso.with(getApplicationContext()).load(item.getUrl()+"/icon.png")
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
