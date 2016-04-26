package catarinatouch.lfspersson.com.projetope;

import android.os.Handler;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.TextView;

public class Sucesso extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sucesso);
        getWindow().setFlags(AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT, AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT);

        TextView tvMsg = (TextView) findViewById(R.id.tvMsg);
        String nome = getIntent().getExtras().getString("nome");
        String sucess_pin = getResources().getString(R.string.sucess_pin);
        String msg = nome + ", " + sucess_pin;
        tvMsg.setText(msg);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Sucesso.this.finish();
            }
        }, 4000);
    }
}
