package code.org.easychat;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import code.org.easychat.adapter.SearchUserRecViewAdapter;
import code.org.easychat.model.UserModel;
import code.org.easychat.utils.FirebaseUtil;

public class SearchUserActivity extends AppCompatActivity {

    private EditText searchInput;
    private ImageButton backBtn;
    private ImageButton searchBtn;
    private RecyclerView searchUserRecView;

    private SearchUserRecViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        initView();

        searchInput.requestFocus();

        backBtn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        });

        searchBtn.setOnClickListener(v -> {
            String searchTerm = searchInput.getText().toString();
            if(searchTerm.isEmpty() || searchTerm.length()<3){
                searchInput.setError("Invalid Username");
            }
            else{
                setupSearchRecView(searchTerm);
            }
        });
    }

    private void setupSearchRecView(String searchTerm){
        Query query = FirebaseUtil.allUserCollectionReference()
                .whereGreaterThanOrEqualTo("username", searchTerm)
                .whereLessThanOrEqualTo("username", searchTerm + "\uf8ff");

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class).build();

        adapter = new SearchUserRecViewAdapter(options, getApplicationContext());
        searchUserRecView.setLayoutManager(new LinearLayoutManager(this));
        searchUserRecView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(adapter!=null){
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null){
            adapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null){
            adapter.startListening();
        }
    }

    private void initView(){
        searchInput = findViewById(R.id.search_user_input);
        backBtn = findViewById(R.id.search_back_btn);
        searchBtn = findViewById(R.id.search_user_btn);
        searchUserRecView = findViewById(R.id.search_user_rec_view);
    }
}