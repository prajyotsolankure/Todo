package com.example.todo;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Todo> taskList;
    SharedPreferences sharedPreferences;
    String customUID;



    public TaskAdapter(List<Todo> taskList, Context context) {
        this.taskList = taskList;
        this.sharedPreferences = context.getSharedPreferences("firstTime", Context.MODE_PRIVATE);


    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new TaskViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Todo task = taskList.get(position);
        holder.due.setText(task.getDate1());
        holder.titleTextView.setText(task.getTitle());
        boolean c = task.getisCompleted();

        if (c) {

            holder.checkBox.setChecked(true);
            holder.due.setPaintFlags(holder.due.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.titleTextView.setPaintFlags(holder.titleTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.note.setPaintFlags(holder.note.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        } else {

            holder.checkBox.setChecked(false);
            holder.due.setPaintFlags(holder.due.getPaintFlags() & ~ Paint.STRIKE_THRU_TEXT_FLAG);
            holder.note.setPaintFlags(holder.note.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            holder.titleTextView.setPaintFlags(holder.titleTextView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String customUID = sharedPreferences.getString("uuid", "");
            if (isChecked) {

                u(true, customUID, task.getTaskID());
                holder.titleTextView.setPaintFlags(holder.titleTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.due.setPaintFlags(holder.due.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.note.setPaintFlags(holder.note.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                holder.due.setPaintFlags(holder.due.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                holder.titleTextView.setPaintFlags(holder.titleTextView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                holder.note.setPaintFlags(holder.note.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                u(false, customUID, task.getTaskID());
            }
        });

        holder.note.setText(task.getNote());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), update_task.class);
                intent.putExtra("title", task.getTitle());
                intent.putExtra("note", task.getNote());
                intent.putExtra("id", task.getTaskID());
                intent.putExtra("createdAt", task.getCreatedAt());
                intent.putExtra("date1",task.getDate1());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    // ViewHolder class
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, note,due;
        CheckBox checkBox;

        public TaskViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title);
            note = itemView.findViewById(R.id.note);
            checkBox = itemView.findViewById(R.id.c);
            due = itemView.findViewById(R.id.due);
        }
    }

    public void u(boolean c, String uid, String taskID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("isCompleted", c);
//        String customUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users")
                .document(uid)
                .collection("tasks_user")
                .document(taskID)
                .update(data)
                .addOnSuccessListener(aVoid -> {

                })
                .addOnFailureListener(e -> {

                });


    }
}
