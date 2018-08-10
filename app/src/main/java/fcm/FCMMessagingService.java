package fcm;

import android.content.Intent;
import android.util.Log;

import com.clinic.myclinic.Activities.UserProfileActivity;
import com.clinic.myclinic.Utils.MyNotificationManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FCMMessagingService extends FirebaseMessagingService {

    private static final String TAG = "fcm";

    //Вариант какого-то чувака в инете. Обдумать
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        //onMessageReceived will be called when ever you receive new message from server.. (app in background and foreground )
//        Log.d("fcm", "From: " + remoteMessage.getFrom());
//
//        if(remoteMessage.getNotification()!=null){
//            Log.d("fcm", "Notification Message Body: " + remoteMessage.getNotification().getBody());
//        }
//
//        if(remoteMessage.getData().containsKey("post_id") && remoteMessage.getData().containsKey("post_title")){
//            Log.d("Post ID",remoteMessage.getData().get("post_id").toString());
//            Log.d("Post Title",remoteMessage.getData().get("post_title").toString());
//            // eg. Server Send Structure data:{"post_id":"12345","post_title":"A Blog Post"}
//        }
//    }

    //Вариант Google Firebase
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                notifyUser(remoteMessage.getFrom(), remoteMessage.getNotification().getBody());
            } else {
                // Handle message within 10 seconds
                //handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void notifyUser(String from, String notification) {

        MyNotificationManager notificationManager = new MyNotificationManager(getApplicationContext());
        notificationManager.showNotification(from, notification, new Intent(getApplicationContext(), UserProfileActivity.class));
    }

}
