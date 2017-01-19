package es.espinr.gijonair.alerts;

import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by martin on 16/01/2017.
 */

public class FirebaseService extends FirebaseInstanceIdService {

    private static final String TAG = "FirebaseService";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
/*        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
*/
    }

    private void sendRegistrationToServer(String refreshedToken) {
    }


}
