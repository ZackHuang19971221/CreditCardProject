package com.example.testcreditcard;

import java.util.ArrayList;

public class Event {

    private static ArrayList<OnMessageSendListener> mOnMessageSendListeners;

    public static void Invoke(String message){
        for (OnMessageSendListener item:mOnMessageSendListeners)
        {
            item.onMessageSend(message);
        }
    }

    public static void addOnMessageSendListener(OnMessageSendListener listener) {
        if (mOnMessageSendListeners == null) {
            mOnMessageSendListeners = new ArrayList<>();
        }
        if(listener == null){
            return;
        }
        if (!mOnMessageSendListeners.contains(listener)) {
            mOnMessageSendListeners.add(listener);
        }
    }

    public interface OnMessageSendListener {
        void onMessageSend(String message);
    }
}

