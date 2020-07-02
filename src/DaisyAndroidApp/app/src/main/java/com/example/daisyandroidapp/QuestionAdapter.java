package com.example.daisyandroidapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class QuestionAdapter extends FirestoreRecyclerAdapter<Question, QuestionAdapter.QuestionHolder> {


    public QuestionAdapter(@NonNull FirestoreRecyclerOptions<Question> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull QuestionHolder holder, int position, @NonNull Question model) {
        holder.answerLayout.removeAllViews();
        holder.textViewQuestionTitle.setText(model.getTitle());
        holder.textViewQuestionId.setText("ID: " +model.getId());
        for (String answer : model.getAnswers()){
            final TextView answerTextView = new TextView(holder.mainView.getContext());

            answerTextView.setText("◘ " + answer);
            holder.answerLayout.addView(answerTextView);
        }
    }

    @NonNull
    @Override
    public QuestionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item, parent, false);
        return new QuestionHolder(v);
    }

    class QuestionHolder extends RecyclerView.ViewHolder {
        TextView textViewQuestionTitle;
        TextView textViewQuestionId;
        LinearLayout answerLayout;
        View mainView;

        public QuestionHolder(@NonNull View itemView) {
            super(itemView);
            mainView = itemView;
            answerLayout = itemView.findViewById(R.id.card_answer_layout);
            textViewQuestionTitle = itemView.findViewById(R.id.text_question_title);
            textViewQuestionId = itemView.findViewById(R.id.text_question_id);
        }
    }
}
