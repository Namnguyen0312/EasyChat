package code.org.easychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.auth.User;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

import code.org.easychat.adapter.ChatRecViewAdapter;
import code.org.easychat.adapter.SearchUserRecViewAdapter;
import code.org.easychat.model.ChatMessageModel;
import code.org.easychat.model.ChatRoomModel;
import code.org.easychat.model.UserModel;
import code.org.easychat.utils.AndroidUtil;
import code.org.easychat.utils.FirebaseUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {
    private String chatroomId;
    private ChatRoomModel chatRoomModel;
    private UserModel otherUser;
    private TextView usernameChat;
    private ImageButton messageSendBtn, backBtn;
    private EditText chatInput;
    private RecyclerView chatRecView;
    private ChatRecViewAdapter adapter;

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initView();

        /**
         * Get User Model
         */
        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());

        chatroomId = FirebaseUtil.getChatRoomId(FirebaseUtil.currentUserId(), otherUser.getUserId());

        backBtn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        });

        usernameChat.setText(otherUser.getUsername().toString());

        FirebaseUtil.getOtherProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if(t.isSuccessful()){
                        Uri uri = t.getResult();
                        AndroidUtil.setProfilePic(this, uri, imageView);
                    }
                });

        messageSendBtn.setOnClickListener(v -> {
            String message = chatInput.getText().toString().trim();
            if(message.isEmpty()){
            }
            sendMessageToUser(message);
        });

        getOrCreateChatRoomModel();

        setupChatRecView();
    }

    private void setupChatRecView(){
        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();

        adapter = new ChatRecViewAdapter(options, getApplicationContext());

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);

        chatRecView.setLayoutManager(manager);
        chatRecView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                chatRecView.smoothScrollToPosition(0);
            }
        });
    }

    private void sendMessageToUser(String message){

        chatRoomModel.setLastMessageTimestamp(Timestamp.now());
        chatRoomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatRoomModel.setLastMessage(message);
        FirebaseUtil.getChatRoomReference(chatroomId).set(chatRoomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtil.currentUserId(), Timestamp.now());
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            chatInput.setText("");
                            sendNotification(message);
                        }
                    }
                });
    }

    private void getOrCreateChatRoomModel(){
        FirebaseUtil.getChatRoomReference(chatroomId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                chatRoomModel = task.getResult().toObject(ChatRoomModel.class);
                if(chatRoomModel == null){
                    /**
                     * First time chat
                     */
                    chatRoomModel = new ChatRoomModel(
                            chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserId(), otherUser.getUserId()),
                            "",
                            Timestamp.now()
                    );
                    FirebaseUtil.getChatRoomReference(chatroomId).set(chatRoomModel);
                }
            }
        });
    }

    private void sendNotification(String message){
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                UserModel currentUser = task.getResult().toObject(UserModel.class);
                try {

                    JSONObject jsonObject = new JSONObject();

                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title", currentUser.getUsername());
                    notificationObj.put("body", message);

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("userId", currentUser.getUserId());

                    jsonObject.put("notification", notificationObj);
                    jsonObject.put("data", dataObj);
                    jsonObject.put("to", otherUser.getFcmToken());

                    callApi(jsonObject);

                }catch (Exception e){

                }
            }
        });
    }

    private void callApi(JSONObject jsonObject){
        MediaType JSON = MediaType.get("application/json");

        OkHttpClient client = new OkHttpClient();

        String url = "https://fcm.googleapis.com/fcm/send";

        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer AAAAhG0ZeGA:APA91bH56KJyAhS_r_6dqJianbr_wN3CoucGSCaWK_10xw0uA8khsuettaesqXTxU-F6Ru4cZaXIGRrW_o7aiJpPDfGu91bZg8UnTEOPKQ8hMd2pmVpjaNbmEROxiBp7epVC9ERyTLp6")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });
    }

    private void initView(){
        usernameChat = findViewById(R.id.chat_user_name);
        messageSendBtn = findViewById(R.id.message_send_btn);
        chatInput = findViewById(R.id.chat_input);
        chatRecView = findViewById(R.id.chat_rec_view);
        backBtn = findViewById(R.id.chat_back_btn);
        imageView = findViewById(R.id.profile_pic_image_view);
    }
}