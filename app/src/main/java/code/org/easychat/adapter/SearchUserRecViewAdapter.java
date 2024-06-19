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

import code.org.easychat.ChatActivity;
import code.org.easychat.R;
import code.org.easychat.model.UserModel;
import code.org.easychat.utils.AndroidUtil;
import code.org.easychat.utils.FirebaseUtil;

public class SearchUserRecViewAdapter extends FirestoreRecyclerAdapter<UserModel, SearchUserRecViewAdapter.UserModelViewHolder> {
    private Context context;
    public SearchUserRecViewAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull UserModel model) {
        holder.usernameText.setText(model.getUsername());
        holder.phoneText.setText(model.getPhone());

        if(model.getUserId().equals(FirebaseUtil.currentUserId())){
            holder.usernameText.setText(model.getUsername() + " (Me)");
        }

        FirebaseUtil.getOtherProfilePicStorageRef(model.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if(t.isSuccessful()){
                        Uri uri = t.getResult();
                        AndroidUtil.setProfilePic(context, uri, holder.profileImage);
                    }
                });

        holder.itemView.setOnClickListener(v -> {
            // TODO: Navigate to chat activity
            Intent intent = new Intent(context, ChatActivity.class);
            AndroidUtil.passUserModelAsIntent(intent, model);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_rec_row, parent, false);
        return new UserModelViewHolder(view);
    }

    public class UserModelViewHolder extends RecyclerView.ViewHolder{

        private TextView usernameText, phoneText;
        private ImageView profileImage;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            phoneText = itemView.findViewById(R.id.phone_text);
            profileImage = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}
