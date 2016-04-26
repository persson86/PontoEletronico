package catarinatouch.lfspersson.com.projetope;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import me.philio.pinentry.PinEntryView;

public class MainActivity extends AppCompatActivity {
    String currentDate,
            formatedDate,
            currentTime,
            id,
            postPush,
            urlIndex,
            urlPin,
            pin;
    Button btnReg;
    Intent it;
    Boolean ok;
    PinEntryView pinEntryView;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initialize();
    }

    public void initialize() {
        Firebase.setAndroidContext(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnReg = (Button) findViewById(R.id.btn_reg);
        urlPin = getResources().getString(R.string.firebase_url_pin);
        getWindow().setFlags(AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT, AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT);
    }

    public void btnReg_onClick(View view) {
        pinEntryView = (PinEntryView) findViewById(R.id.input_pin);
        pin = pinEntryView.getText().toString();

        if (pinEntryView == null) {
            Toast.makeText(getApplicationContext(), R.string.error_pin_empty, Toast.LENGTH_LONG).show();
        } else {
            loadAsyncTask();
        }
    }

    public void loadAsyncTask() {

        new AsyncTask<String, Integer, Integer>() {
            @Override
            protected Integer doInBackground(String... params) {
                checkPin();
                return 1;
            }

            @Override
            protected void onPreExecute() {
                btnReg.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                pinEntryView.clearText();
            }

        }.execute();
    }

    public void checkPin() {
        urlPin = getResources().getString(R.string.firebase_url_pin);
        Firebase pinRef;
        pinRef = new Firebase(urlPin);
        pinRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    Toast.makeText(MainActivity.this.getApplicationContext(), R.string.error_pin, Toast.LENGTH_LONG).show();
                    return;
                }
                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    Colaborador colaborador = (Colaborador) eventSnapshot.getValue(Colaborador.class);
                    if (colaborador.getPin().equals(pin)) {
                        id = colaborador.getId();
                        String nome = colaborador.getNome();
                        registraPonto(nome, id);
                        ok = true;
                    }

                }

                if (ok.equals(false)) {
                    progressBar.setVisibility(View.GONE);
                    btnReg.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this.getApplicationContext(), R.string.error_pin, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void registraPonto(String nome, String id) {
        getTime();
        Firebase newPontoRef = new Firebase(getResources().getString(R.string.firebase_url_ponto)).push();
        Map<String, String> pontoMap = new HashMap();
        pontoMap.put("nome", nome);
        pontoMap.put("hora", currentTime);
        pontoMap.put("data", formatedDate);
        pontoMap.put("id", id);
        newPontoRef.setValue(pontoMap);
        postPush = newPontoRef.getKey();
        if (postPush != null) {
            setIndexDB();
            progressBar.setVisibility(View.GONE);
            btnReg.setVisibility(View.VISIBLE);
            startSucesso(nome);
            return;
        }
        Toast.makeText(this, R.string.error_5, Toast.LENGTH_LONG).show();
    }

    public void getTime() {
        currentTime = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date(System.currentTimeMillis()));
        formatedDate = currentDate.replaceAll("-", "/");
    }

    public void setIndexDB() {
        urlIndex = getResources().getString(R.string.firebase_url_indexid);
        urlIndex = urlIndex.concat(id);
        Firebase newIndexIdRef = new Firebase(urlIndex).push();
        Map<String, String> indexIdMap = new HashMap();
        indexIdMap.put("hora", currentTime);
        indexIdMap.put("data", formatedDate);
        indexIdMap.put("node", postPush);
        newIndexIdRef.setValue(indexIdMap);
        if (newIndexIdRef.getKey() == null) {
            Toast.makeText(this, R.string.error_5, Toast.LENGTH_LONG).show();
        }
    }

    public void startSucesso(String nome) {
        Bundle bundle = new Bundle();
        bundle.putString("nome", nome);
        it = new Intent(this, Sucesso.class);
        it.putExtras(bundle);
        startActivity(it);
    }

    public void tvLink_onClick(View view) {
        this.it = new Intent(this, Admin.class);
        startActivity(it);
    }
}
