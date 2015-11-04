package pe.jota.nfcsample;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NfcAdapter.CreateNdefMessageCallback{
    private static final String LOG_TAG = AppCompatActivity.class.getSimpleName();
    NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Check for available NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Snackbar.make(findViewById(R.id.toolbar), "NFC is not available", Snackbar.LENGTH_LONG).show();
            finish();
            return;
        }
        // Register callback
        mNfcAdapter.setNdefPushMessageCallback(this, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();

        NdefMessage[] msgs = null;

        // Just like in the documentation example, let's get the contents
        // when the activity has been started because of reading an NFC Tag:
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
        }

        // Print the received messages to the console
        if (msgs != null) {
            for(int i = 0; i < msgs.length; i++) {
                Log.d(LOG_TAG, "Message " + i + " received: " + msgs[i]);
            }
        }
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String text = ("Hello NFC!");
        NdefMessage msg = new NdefMessage(
                new NdefRecord[] {
                        NdefRecord.createMime(
                                getString(R.string.mime_type), text.getBytes()),
        // TODO: replace this test app record for the actual one ("pe.jota.nfcsample")
                        NdefRecord.createApplicationRecord("com.applica.sarcasm")
                });
        Log.d(LOG_TAG, "sending NFC...");
        return msg;
    }
}
