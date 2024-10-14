package gcfv2;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import java.io.IOException;

public class FireStoreService {
    private final Firestore firestore;

    public FireStoreService() throws IOException {
        FirestoreOptions options = FirestoreOptions.getDefaultInstance().toBuilder().build();
        this.firestore = options.getService();
    }

    public Firestore getFirestore() {
        return firestore;
    }
}
