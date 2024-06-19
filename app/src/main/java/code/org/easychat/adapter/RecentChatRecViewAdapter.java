package code.org.easychat.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import code.org.easychat.ChatActivity;
import code.org.easychat.R;
import code.org.easychat.model.ChatRoomModel;
import code.org.easychat.model.UserModel;
import code.org.easychat.utils.AndroidUtil;
import code.org.easychat.utils.FirebaseUtil;

public class RecentChatRecViewAdapter extends FirestoreRecyclerAdapter<ChatRoomModel, RecentChatRecViewAdapter.ChatRoomModelViewHolder> {
    private Context context;
    public RecentChatRecViewAdapter(@NonNull FirestoreRecyclerOptions<ChatRoomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatRoomModelViewHolder holder, int position, @NonNull ChatRoomModel model) {
        FirebaseUtil.getOtherUserFromChatroom(model.getUserId())
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());

                        UserModel otherUserModel = task.getResult().toObject(UserModel.class);

                        FirebaseUtil.getOtherProfilePicStorageRef(otherUserModel.getUserId()).getDownloadUrl()
                                .addOnCompleteListener(t -> {
                                    if(t.isSuccessful()){
                                        Uri uri = t.getResult();
                                        AndroidUtil.setProfilePic(context, uri, holder.profileImage);
                                    }
                                });

                        holder.usernameText.setText(otherUserModel.getUsername());
                        if(lastMessageSentByMe)
                            holder.lastMessageText.setText("You : " + model.getLastMessage());
                        else
                            holder.lastMessageText.setText(model.getLastMessage());
                        holder.lastMessageTime.setText(FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));

                        holder.itemView.setOnClickListener(v -> {
                            // TODO: Navigate to chat activity
                            Intent intent = new Intent(context, ChatActivity.class);
                            AndroidUtil.passUserModelAsIntent(intent, otherUserModel);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        });
                    }
                });
    }

    @NonNull
    @Override
    public ChatRoomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_rec_row, parent, false);
        return new ChatRoomModelViewHolder(view);
    }

    public class ChatRoomModelViewHolder extends RecyclerView.ViewHolder{

        private TextView usernameText, lastMessageText, lastMessageTime;
        private ImageView profileImage;

        public ChatRoomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            profileImage = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}
