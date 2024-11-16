package com.example.todo;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
//import androidx.recyclerview.widget.ItemTouchHelper;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class update_task extends AppCompatActivity {


    ImageButton back;
    LinearLayout due;
    AppCompatButton saveTask;
    EditText note,title;
    TextView date1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_task);

        back = findViewById(R.id.back);
        due = findViewById(R.id.due);
        title = findViewById(R.id.title);
        note = findViewById(R.id.note);
        saveTask = findViewById(R.id.save);
        date1 = findViewById(R.id.d);


        String titl = getIntent().getStringExtra("title");
        String not  = getIntent().getStringExtra("note");
        String d = getIntent().getStringExtra("date1");


        title.setText(titl);
        note.setText(not);
        date1.setText(d);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        saveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.getText().toString().isEmpty()){
                    Toast.makeText(update_task.this, "Title field can't be empty", Toast.LENGTH_SHORT).show();
                }else{
                    vis(1);
                    update();
                }
            }
        });

        due.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(update_task.this);
                dialog.setContentView(R.layout.calendar);
                dialog.show();
                CalendarView c = dialog.findViewById(R.id.cal);
                c.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                        date1.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                        dialog.dismiss();
                    }
                });
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void update(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences("firstTime",MODE_PRIVATE);
        String customUID = sharedPreferences.getString("uuid","");
        String taskId = getIntent().getStringExtra("id");
        Timestamp createdAt = getIntent().getParcelableExtra("createdAt");
        String t = title.getText().toString();
        Map<String,Object> data = new HashMap<>();
        data.put("title",t);
        data.put("date1",date1.getText().toString());
        data.put("note",note.getText().toString());
        data.put("taskID",taskId);
//        data.put("isCompleted",false);
        data.put("createdAt", createdAt);

        db.collection("users")
                .document(customUID)
                .collection("tasks_user")
                .document(taskId)
                .update(data)
                .addOnSuccessListener(documentReference -> {
                        vis(0);
                        Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                })
                .addOnFailureListener(e ->{
                    vis(0);
                    Toast.makeText(this, e+"", Toast.LENGTH_SHORT).show();
                });
    }
    public void vis(int i){
        View bg  = findViewById(R.id.backround1);
        ConstraintLayout main = findViewById(R.id.main);
        ProgressBar pb = findViewById(R.id.progressbar);
        if(i==0){
            main.setEnabled(true);
            bg.setVisibility(View.GONE);
            pb.setVisibility(View.GONE);
        }else{
            main.setEnabled(false);
            bg.setVisibility(View.VISIBLE);
            pb.setVisibility(View.VISIBLE);
        }
    }
}