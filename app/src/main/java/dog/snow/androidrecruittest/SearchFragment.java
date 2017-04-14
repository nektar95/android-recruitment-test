package dog.snow.androidrecruittest;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Aleksander on 14.04.2017.
 */

public class SearchFragment extends Fragment {
    private EditText mSearchEditText;
    private ImageButton mLoupeButton;
    private String mSearchQuery;
    private SearchInterface mCallbacks;

    public static SearchFragment newInstance(){
        return new SearchFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment, container, false);

        mSearchEditText = (EditText) view.findViewById(R.id.search_et);
        mLoupeButton = (ImageButton) view.findViewById(R.id.search_loupe);

        mLoupeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getActivity().getCurrentFocus();
                if(view != null){
                    InputMethodManager inputMethodManager=(InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
                }

                if(mSearchQuery.isEmpty()) {
                    Snackbar.make(v, R.string.empty_query, Snackbar.LENGTH_SHORT).show();
                    return;
                }
                mCallbacks.scrollView(mSearchQuery);

            }
        });
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSearchQuery = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    View view = getActivity().getCurrentFocus();
                    if(view != null){
                        InputMethodManager inputMethodManager=(InputMethodManager) getActivity()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
                    }

                    if(mSearchEditText.getText().toString().length()==0) {
                        Snackbar.make(v, R.string.empty_query, Snackbar.LENGTH_LONG).show();
                    }

                    mCallbacks.scrollView(mSearchQuery);
                    return true;
                }
                return false;
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallbacks = (SearchInterface) context;
        } catch (ClassCastException cce){
            throw new ClassCastException(context.toString()+"can not implement interface");
        }
    }
}
