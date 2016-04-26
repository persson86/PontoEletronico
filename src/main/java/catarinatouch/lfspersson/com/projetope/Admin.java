package catarinatouch.lfspersson.com.projetope;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class Admin extends AppCompatActivity {

    ArrayList<String> listUser = new ArrayList<String>();
    ArrayList<String> listUserId = new ArrayList<String>();
    ListView listView;
    ProgressBar progressBar;
    String urlPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initialize();
        loadAsyncTask();
    }

    protected void onResume() {
        super.onResume();
        loadList();
    }

    public void initialize() {
        Firebase.setAndroidContext(this);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.urlPin = getResources().getString(R.string.firebase_url_pin);
        getWindow().setFlags(AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT, AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT);
    }

    public void getUser() {

        Firebase firebase = new Firebase(this.urlPin);
        urlPin = getResources().getString(R.string.firebase_url_pin);
        final Firebase pinRef;
        pinRef = new Firebase(urlPin);
        pinRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    for (DataSnapshot idSnapshot : snapshot.getChildren()) {
                        Colaborador colaborador = (Colaborador) idSnapshot.getValue(Colaborador.class);
                        String nome = colaborador.getNome();
                        String id = colaborador.getId();
                        String pin = colaborador.getPin();
                        listUser.add(nome + " e PIN: " + pin);
                        listUserId.add(id);
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

    public void loadList() {
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listUser);

        listView = (ListView) findViewById(R.id.listUser);
        listView.setAdapter(itemsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String line = listUserId.get(position).toString();
                Bundle bundle = new Bundle();
                bundle.putString("id", line);
                Intent it = new Intent(Admin.this.getApplicationContext(), AdminDetalhe.class);
                it.putExtras(bundle);
                startActivity(it);
            }
        });
    }
}
