package com.example.todo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {


     FloatingActionButton add;
     FirebaseFirestore db;

     RecyclerView re;
     List<Todo> tasklist;
     TaskAdapter taskAdapter;
     String customUID;


    int firstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        add = findViewById(R.id.add);
        re = findViewById(R.id.re);
        re.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sharedPreferences = getSharedPreferences("firstTime",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        firstTime = sharedPreferences.getInt("f",0);

        // ------- Checking app is opened first time or not --------//
        if(firstTime == 0){
            customUID = UUID.randomUUID().toString();
            editor.putInt("f",1);
            editor.putString("uuid",customUID);
            editor.apply();
        }else {
            customUID = sharedPreferences.getString("uuid", "");
        }


        db = FirebaseFirestore.getInstance();


        // ------- Task List --------//
        tasklist = new ArrayList<>();
        taskAdapter = new TaskAdapter(tasklist,this);
        re.setAdapter(taskAdapter);

        // ------- Calling load task function --------//
        loadTasks();
        setVis();

        // ------- Add new Task --------//
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this,additem.class);
                startActivity(intent);
            }
        });

        // ------- Delete Task --------//
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                String id = tasklist.get(viewHolder.getAbsoluteAdapterPosition()).getTaskID();
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                int position = viewHolder.getAdapterPosition();



                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("\nDelete Task")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tasklist.remove(position);
                                taskAdapter.notifyItemRemoved(position);
                                setVis();
                                firestore.collection("users")
                                        .document(customUID)
                                        .collection("tasks_user")
                                        .document(id)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                taskAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                                                Toast.makeText(MainActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                taskAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(re);




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    // ------- Function to load tasks --------//
    public void loadTasks() {
        db.collection("users").document(customUID).collection("tasks_user")
                .orderBy("createdAt",Query.Direction.ASCENDING)
                .addSnapshotListener((snapshot,e) ->{
                    if(e!=null){
                        setVis();
                        Log.e("Firestore","Error getting",e);
                        return;
                    }
                    if(snapshot != null && !snapshot.isEmpty()){
                        tasklist.clear();
                        for (QueryDocumentSnapshot document : snapshot) {

                            Todo taskItem = document.toObject(Todo.class);
                            tasklist.add(taskItem);

                        }
                        setVis();
                        taskAdapter.notifyDataSetChanged();
                    }
                });
    }

    // ------- Function to set visibility of img ---------//
    public void setVis(){
        ImageView img = findViewById(R.id.img);
        if (taskAdapter != null) {
            if(taskAdapter.getItemCount()==0){
                img.setVisibility(View.VISIBLE);
            }else{
                img.setVisibility(View.GONE);
            }
        }
    }
}