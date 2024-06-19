package code.org.easychat.model;

import com.google.firebase.Timestamp;

import java.util.List;

public class ChatRoomModel {
    private String chatRoomId;
    private List<String> userId;
    private String lastMessageSenderId;
    private Timestamp lastMessageTimestamp;
    private String lastMessage;

    public ChatRoomModel() {
    }

    public ChatRoomModel(String chatRoomId, List<String> userId, String lastMessageSenderId, Timestamp lastMessageTimestamp) {
        this.chatRoomId = chatRoomId;
        this.userId = userId;
        this.lastMessageSenderId = lastMessageSenderId;
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public List<String> getUserId() {
        return userId;
    }

    public void setUserId(List<String> userId) {
        this.userId = userId;
    }

    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }

    public Timestamp getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(Timestamp lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
