package com.example.dropshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton button;
    public static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<Modal> modalList;
    private Adapter adapter;
    private List<Modal> fetchList;
    private ImageView dustbin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadJsonFromFileExplorer();
            }
        });


    }

    private void init() {

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        button = findViewById(R.id.button);
        modalList = new ArrayList<>();
        fetchList = new ArrayList<>();
        dustbin = findViewById(R.id.dustbin);

        adapter = new Adapter(fetchList);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(adapter);

    }

    private void selectFile() {

        Intent chooseFile;
        Intent intent;
        chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.addCategory(Intent.CATEGORY_DEFAULT);
        chooseFile.setType("application/*");
        intent = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(intent, 101);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            Toast.makeText(getApplicationContext(), "Result code not okay", Toast.LENGTH_SHORT).show();
            return;
        }
        String path = "";
        String fileName = "test.json";
        if (requestCode == 101) {

            if (data != null) {
                Uri uri = data.getData();
                path = uri.getPath();

                if (path.indexOf(fileName) != -1) {

                    String jsonData = getJsonDataFromUri(uri);
                    parseJson(jsonData);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Invalid File", Toast.LENGTH_SHORT).show();
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.asset:
                //import file from local asset
                loadJsonFromAsset();
                Toast.makeText(getApplicationContext(), "Importing from asset folder", Toast.LENGTH_SHORT).show();
                break;
            case R.id.sort_asc:
                Collections.sort(fetchList);
                Toast.makeText(getApplicationContext(), "Sorted in Ascending Order", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
                break;
            case R.id.sort_dsc:
                Collections.reverse(fetchList);
                Toast.makeText(getApplicationContext(), "Sorted in Descending Order", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
                break;
            default:
        }
        return true;
    }


    private void loadJsonFromAsset() {

        String jsonData = getJsonDataFromAsset();
        parseJson(jsonData);

    }

    private void loadJsonFromFileExplorer() {

        selectFile();
    }

    //Lister for realtime updates in firestore
    private void fetchData() {

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("dropShop")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }
                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    dustbin.setVisibility(View.GONE);
                                    Modal modal = dc.getDocument().toObject(Modal.class);
                                    if (!fetchList.contains(modal))
                                        fetchList.add(modal);
                                    adapter.notifyDataSetChanged();
                                    break;
                            }
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        fetchData();

        if (fetchList.isEmpty())
            dustbin.setVisibility(View.VISIBLE);
        else
            dustbin.setVisibility(View.GONE);
    }

    String getJsonDataFromUri(Uri uri) {
        String json = "";
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }

    String getJsonDataFromAsset() {
        String json = "";

        try {
            InputStream is = this.getAssets().open("test.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return json;
    }

    private void parseJson(String jsonData) {

        List<Modal> modalList = new ArrayList<>();

        try {

            JSONObject jsonObject = new JSONObject(jsonData);
            for (int i = 0; i < jsonData.length(); i++) {

                int j = i + 1;
                JSONObject data = jsonObject.getJSONObject("product_" + (j));

                String productId = data.getString("productId");
                String customerId = data.getString("customerId");
                String brandCode = data.getString("brandCode");
                String brandName = data.getString("brandName");
                int productCode = data.getInt("productCode");
                String productDesc = data.getString("productDesc");
                int mrp = data.getInt("mrp");
                int expiry = (int) data.get("expiry");

                Modal modal = new Modal(productId, customerId, brandCode, brandName, productCode, productDesc, mrp, expiry);
                modalList.add(modal);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        runBackgroundTask(modalList);
    }

    private void runBackgroundTask(List<Modal> modals) {

        if (!modals.isEmpty()) {
            PushData pushData = new PushData(modals, this);
            pushData.execute();
        }
    }
}