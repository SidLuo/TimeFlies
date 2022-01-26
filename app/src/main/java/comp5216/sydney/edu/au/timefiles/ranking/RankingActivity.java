package comp5216.sydney.edu.au.timefiles.ranking;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import comp5216.sydney.edu.au.timefiles.MainActivity;
import comp5216.sydney.edu.au.timefiles.R;
import comp5216.sydney.edu.au.timefiles.utils.AppInMachine;


public class RankingActivity extends AppCompatActivity {
    private FirebaseFirestore mFirestore;
    private RecyclerView mRankingRecycler;
    private RankingListAdapter rankingListAdapter;
    private static final String TAG = "RankingActivity";
    private ArrayList<RankingModel> ranks;
    private TextView currentTime;
    private TextView todayView;
    private Button uploadBtn;
    private boolean ifMine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        mRankingRecycler = findViewById(R.id.recycler_ranking);
        currentTime = (TextView) findViewById(R.id.currentTime);
        todayView = (TextView) findViewById(R.id.today);
        uploadBtn = (Button) findViewById(R.id.upload_btn);
        ranks = new ArrayList<RankingModel>();
        ifMine = false;

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);
        mFirestore = FirebaseFirestore.getInstance();

        // Initialize Firestore and the main RecyclerView
        getDBData();
        initRecyclerView();
    }

    public long getToday(){
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String todayS = df.format(new Date());
        Log.e("today",todayS);
        return Long.valueOf(todayS);
    }

    public void onUploadMine(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(RankingActivity.this);
        builder.setTitle(R.string.privacy_title);
        builder.setMessage(R.string.privacy_content);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                long today = getToday();
                if(!ifMine){
                    //add our data to database
                    RankingModel item = new RankingModel();
                    int totalTime = AppInMachine.getTotalTime(RankingActivity.this);
                    item.setUserName(MainActivity.authentication.getUserEmailAddress());
                    item.setPlanned_time(MainActivity.settingsInfo.getTotalTime());
                    item.setDate(today);
                    item.setActual_time(totalTime);
                    item.setRatio((float) totalTime / MainActivity.settingsInfo.getTotalTime());
                    Log.e(TAG, item.toString());

                    CollectionReference database = mFirestore.collection("completion_data");
                    database.add(item).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            getDBData();
                            Log.e("success", "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("failed", "Error adding document", e);
                                }
                            });
                }else{
                    // update our usage data
                    CollectionReference database = mFirestore.collection("completion_data");
                    database.whereGreaterThan("date", today - 1)
                            .whereEqualTo("userName", MainActivity.authentication.getUserEmailAddress())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            database.document(document.getId())
                                                    .update("actual_time", AppInMachine.getTotalTime(RankingActivity.this),
                                                            "ratio", (float) AppInMachine.getTotalTime(RankingActivity.this) / MainActivity.settingsInfo.getTotalTime(),
                                                            "planned_time", MainActivity.settingsInfo.getTotalTime())
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    getDBData();
                                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                }
                                            })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error updating document", e);
                                                        }
                                                    });
                                        }
                                    } else {
                                        Log.e(TAG, "Error getting documents: ", task.getException());
                                    }
                                }
                            });

                }

            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }


    private void getDBData() {
        ranks.clear();

        mFirestore.collection("completion_data")
                .orderBy("date")
                .whereGreaterThan("date", getToday() - 1)
                .orderBy("ratio")
                .limit(100)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ifMine = false;
                    Date current = new Date();
                    SimpleDateFormat rankIn = new SimpleDateFormat("yyyy-MM-dd ");
                    SimpleDateFormat updateIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    int rankPosition = 1;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        try{
                            RankingModel items = new RankingModel();
                            items.setUserName(document.getData().get("userName").toString());
                            items.setPlanned_time(Long.parseLong(document.getData().get("planned_time").toString()));
                            items.setActual_time(Long.parseLong(document.getData().get("actual_time").toString()));
                            items.setDate(getToday());
                            items.setRank(rankPosition);
                            items.setRatio( 1 - Float.parseFloat(String.valueOf((float) items.getActual_time() / items.getPlanned_time())));
                            if(items.getUserName().equals(MainActivity.authentication.getUserEmailAddress())) ifMine = true;
                            ranks.add(items);
                            rankPosition++;
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    todayView.setText("Rank in: " + rankIn.format(current));
                    rankingListAdapter.notifyDataSetChanged();
                    currentTime.setText("Updated in: " + updateIn.format(current));
                    if(ifMine){
                        uploadBtn.setText("Update my data");
                    }else{
                        uploadBtn.setText("Upload my data");
                    }
                } else {
                    Log.e(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void initRecyclerView() {
        rankingListAdapter = new RankingListAdapter(this, ranks);
        mRankingRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRankingRecycler.setAdapter(rankingListAdapter);
    }
}
