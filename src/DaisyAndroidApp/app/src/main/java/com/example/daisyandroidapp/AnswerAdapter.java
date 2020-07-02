package com.example.daisyandroidapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class AnswerAdapter extends FirestoreRecyclerAdapter<Answer, AnswerAdapter.AnswerHolder> {

    public AnswerAdapter(@NonNull FirestoreRecyclerOptions<Answer> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AnswerHolder holder, int position, @NonNull Answer model) {
        holder.textViewUsername.setText(model.getUsername());
        holder.textViewQuestion.setText(model.getQuestion());
        holder.textViewAnswer.setText(model.getAnswer());
        holder.textViewTime.setText(model.getTimeAnswered());
    }

    @NonNull
    @Override
    public AnswerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_item, parent, false);
        return new AnswerHolder(v);
    }

    class AnswerHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername;
        TextView textViewQuestion;
        TextView textViewAnswer;
        TextView textViewTime;

        public AnswerHolder(@NonNull View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.text_view_user_questioned);
            textViewQuestion = itemView.findViewById(R.id.text_view_question);
            textViewAnswer = itemView.findViewById(R.id.text_view_answer);
            textViewTime = itemView.findViewById(R.id.text_view_time);
        }
    }
}
