package code.org.easychat.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import code.org.easychat.ChatActivity;
import code.org.easychat.R;
import code.org.easychat.model.ChatMessageModel;
import code.org.easychat.utils.AndroidUtil;
import code.org.easychat.utils.FirebaseUtil;

public class ChatRecViewAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecViewAdapter.ChatModelViewHolder> {
    private Context context;
    public ChatRecViewAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {
        if(model.getSenderId().equals(FirebaseUtil.currentUserId())){
            holder.receiveLayout.setVisibility(View.GONE);
            holder.senderLayout.setVisibility(View.VISIBLE);
            holder.senderText.setText(model.getMessage());
        }else {
            holder.receiveLayout.setVisibility(View.VISIBLE);
            holder.senderLayout.setVisibility(View.GONE);
            holder.receiveText.setText(model.getMessage());
        }
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_rec_row, parent, false);
        return new ChatModelViewHolder(view);
    }

    public class ChatModelViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout senderLayout, receiveLayout;
        private TextView senderText, receiveText;

        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);

            senderLayout = itemView.findViewById(R.id.sender_layout);
            receiveLayout = itemView.findViewById(R.id.receive_layout);
            senderText = itemView.findViewById(R.id.sender_text);
            receiveText = itemView.findViewById(R.id.receive_text);

        }
    }
}
