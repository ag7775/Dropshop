package com.example.dropshop;

import android.content.Context;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PushData extends AsyncTask<Void,Integer,Void> {

    private List<Modal> modalList;
    private Context context;
    public static final String TAG = PushData.class.getSimpleName();
    private FirebaseFirestore firebaseFirestore;
    private int temp = 0;

    public PushData(List<Modal> modalList, Context context) {
        this.modalList = modalList;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {

        firebaseFirestore = FirebaseFirestore.getInstance();
    }


    @Override
    protected Void doInBackground(Void... voids) {

        for(int i=0;i<modalList.size();i++){

            String documentId = "product_"+(i+1);
            firebaseFirestore.collection("dropShop").document(documentId)
                    .set(modalList.get(i))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                               Log.i(TAG,"Task Successful");
                            }
                        }
                    });

        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
    }
}
