package com.example.ecommerceapp.ui.search;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.util.Log;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.BackPressedListener;
import com.example.ecommerceapp.data.MySuggestionProvider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class SearchableActivity extends AppCompatActivity{

    private static final String TAG = "SearchableActivity";
    private NavController navController;
    private BackPressedListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_search);

        // Get the intent, verify the action and get the query
       handleIntent(getIntent());

    }

    private void handleIntent(Intent intent) {
        Log.d(TAG, "MERO: handle");
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);

            Bundle bundle = new Bundle();
            bundle.putString("query", query);
            navController.setGraph(navController.getGraph(), bundle);
        }
    }

    public void setOnBackPressedListener(BackPressedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBackPressed() {
        if (listener != null) {
            listener.OnBackPressed();
        }
        else {
            super.onBackPressed();
        }
    }
}
