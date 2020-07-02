package com.example.daisyandroidapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class AskedQuestionAdapter extends FirestoreRecyclerAdapter<Question, AskedQuestionAdapter.QuestionHolder> {
    private onQuestionItemClickListener listener;

    public AskedQuestionAdapter(@NonNull FirestoreRecyclerOptions<Question> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull QuestionHolder holder, int position, @NonNull Question model) {
        holder.textViewTitle.setText(model.getTitle());
    }

    @NonNull
    @Override
    public QuestionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.asked_question_item, parent, false);
        return new QuestionHolder(v);
    }

    class QuestionHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;


        public QuestionHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.asked_question_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    public interface onQuestionItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(onQuestionItemClickListener listener) {
        this.listener = listener;
    }
}
