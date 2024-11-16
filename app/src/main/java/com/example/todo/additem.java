package com.example.todo;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class additem extends AppCompatActivity {

    ImageButton back;
    LinearLayout due;
    AppCompatButton saveTask;
    EditText note,title;
    TextView date1;
    int day,m,y;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_additem);

        back = findViewById(R.id.back);
        due = findViewById(R.id.due);
        title = findViewById(R.id.title);
        note = findViewById(R.id.note);
        saveTask = findViewById(R.id.save);



        due.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(additem.this, "Wait", Toast.LENGTH_SHORT).show();
                Dialog dialog = new Dialog(additem.this);
                dialog.setContentView(R.layout.calendar);
                dialog.show();
                CalendarView cal  = dialog.findViewById(R.id.cal);
                cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                        TextView date1 = findViewById(R.id.date1);
                        date1.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                        dialog.dismiss();
                    }
                });
            }
        });

        saveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.getText().toString().isEmpty()){
                    Toast.makeText(additem.this, "Title field can't be empty", Toast.LENGTH_SHORT).show();
                }else {
                    vis(1);
                    storeData();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onBackPressed() {
//        finishActivity();
        super.onBackPressed();
    }
    public void storeData(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences("firstTime",MODE_PRIVATE);

        String customUID = sharedPreferences.getString("uuid","");
        String taskId = UUID.randomUUID().toString();
        date1 = findViewById(R.id.date1);
        String date = date1.getText().toString();
        if(date.isEmpty()){
            date = "No due date";
        }
        Map<String,Object> data = new HashMap<>();
        data.put("date1",date);
        data.put("title",title.getText().toString());
        data.put("note",note.getText().toString());
        data.put("isCompleted",false);
        data.put("taskID",taskId);
        data.put("createdAt", FieldValue.serverTimestamp());

        db.collection("users")
                .document(customUID)
                .collection("tasks_user")
                .document(taskId)
                .set(data)
                .addOnSuccessListener(documentReference -> {
                    vis(0);
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