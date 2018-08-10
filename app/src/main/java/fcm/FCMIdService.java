package fcm;

import android.util.Log;

import com.clinic.myclinic.Utils.PersistantStorageUtils;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FCMIdService extends FirebaseInstanceIdService {

    private static final String TAG = "fcm";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        storeToken(refreshedToken);
    }

    private void storeToken(String refreshedToken) {
        PersistantStorageUtils.storeToken(refreshedToken, getApplicationContext());
    }
}
