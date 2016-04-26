package catarinatouch.lfspersson.com.projetope;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class AdminDetalhe extends AppCompatActivity {
    String id;
    ArrayList<String> listDateHour = new ArrayList<String>();
    ListView listView;
    ProgressBar progressBar;
    String urlIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_detalhe);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initialize();
        loadAsyncTask();
    }

    public void initialize() {
        Firebase.setAndroidContext(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        urlIndex = getResources().getString(R.string.firebase_url_indexid);
        getWindow().setFlags(AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT, AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT);
        id = getIntent().getExtras().getString("id");
    }

    public void getUser() {
        listDateHour.clear();
        urlIndex = urlIndex.concat(id);
        Firebase pinRef;
        pinRef = new Firebase(urlIndex);

        pinRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    for (DataSnapshot idSnapshot : snapshot.getChildren()) {
                        Index indexId = (Index) idSnapshot.getValue(Index.class);
                        listDateHour.add("Dia " + indexId.getData() + " e horário " + indexId.getHora());
                    }
                    loadList();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void loadList() {
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listDateHour);
        listView = (ListView) findViewById(R.id.listDate);
        listView.setAdapter(itemsAdapter);
    }

    public void loadAsyncTask() {

        new AsyncTask<String, Integer, Integer>() {
            @Override
            protected Integer doInBackground(String... params) {
                getUser();
                return 1;
            }

            @Override
            protected void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
            }

        }.execute();
    }

}
