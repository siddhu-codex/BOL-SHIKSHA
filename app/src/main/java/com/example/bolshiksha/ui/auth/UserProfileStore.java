package com.example.bolshiksha.ui.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Saves public profile fields in Firestore. Passwords stay in Firebase Authentication.
 */
public final class UserProfileStore {

    static final String COLLECTION_USERS = "users";
    static final String COLLECTION_USERNAMES = "usernames";
    static final String AUTH_EMAIL_DOMAIN = "bolshiksha.app";

    public static final String ERROR_USERNAME_TAKEN = "USERNAME_TAKEN";

    private UserProfileStore() {
    }

    @NonNull
    public static String normalizeUsername(@Nullable String raw) {
        return raw == null ? "" : raw.trim().toLowerCase(Locale.US);
    }

    public static boolean isValidUsername(@NonNull String username) {
        return username.matches("^[a-z0-9._]{3,20}$");
    }

    @NonNull
    public static String normalizePhone(@Nullable String raw) {
        if (raw == null) {
            return "";
        }
        String digits = raw.replaceAll("\\D", "");
        if (digits.startsWith("91") && digits.length() == 12) {
            digits = digits.substring(2);
        }
        return digits;
    }

    public static boolean isValidPhone(@NonNull String tenDigitPhone) {
        return tenDigitPhone.matches("^[6-9]\\d{9}$");
    }

    @NonNull
    public static String e164Phone(@NonNull String tenDigitPhone) {
        return "+91" + tenDigitPhone;
    }

    @NonNull
    public static String authEmailFor(@NonNull String username) {
        return username + "@" + AUTH_EMAIL_DOMAIN;
    }

    @NonNull
    public static Task<Void> claimUsernameAndSave(
            @NonNull FirebaseUser user,
            @NonNull String username,
            @Nullable String phone,
            @NonNull String provider
    ) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference usernameRef = db.collection(COLLECTION_USERNAMES).document(username);
        DocumentReference userRef = db.collection(COLLECTION_USERS).document(user.getUid());

        return db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot usernameSnap = transaction.get(usernameRef);
            if (usernameSnap.exists()) {
                String existingUid = usernameSnap.getString("uid");
                if (existingUid == null || !existingUid.equals(user.getUid())) {
                    throw new UsernameTakenException();
                }
            }

            Map<String, Object> usernameDoc = new HashMap<>();
            usernameDoc.put("uid", user.getUid());
            transaction.set(usernameRef, usernameDoc);

            Map<String, Object> userDoc = profileMap(user, username, phone, provider);
            userDoc.put("createdAt", FieldValue.serverTimestamp());
            transaction.set(userRef, userDoc, SetOptions.merge());
            return null;
        }).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                Exception e = task.getException();
                if (isUsernameTaken(e)) {
                    return Tasks.forException(new UsernameTakenException());
                }
                return Tasks.forException(e != null ? e : new Exception("Could not save profile"));
            }
            return Tasks.forResult(null);
        });
    }

    public static boolean isUsernameTaken(@Nullable Exception error) {
        Throwable current = error;
        while (current != null) {
            if (current instanceof UsernameTakenException
                    || ERROR_USERNAME_TAKEN.equals(current.getMessage())) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    @NonNull
    public static Task<Void> saveExistingUser(
            @NonNull FirebaseUser user,
            @Nullable String username,
            @Nullable String phone,
            @NonNull String provider
    ) {
        String resolved = username;
        if (resolved == null || resolved.isEmpty()) {
            resolved = fallbackUsername(user);
        }
        Map<String, Object> userDoc = profileMap(user, resolved, phone, provider);
        return FirebaseFirestore.getInstance()
                .collection(COLLECTION_USERS)
                .document(user.getUid())
                .set(userDoc, SetOptions.merge());
    }

    @NonNull
    public static Task<Void> updateDisplayName(@NonNull FirebaseUser user, @NonNull String username) {
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();
        return user.updateProfile(request);
    }

    @NonNull
    private static Map<String, Object> profileMap(
            @NonNull FirebaseUser user,
            @NonNull String username,
            @Nullable String phone,
            @NonNull String provider
    ) {
        Map<String, Object> userDoc = new HashMap<>();
        userDoc.put("uid", user.getUid());
        userDoc.put("username", username);
        userDoc.put("email", user.getEmail());
        userDoc.put("displayName", user.getDisplayName() != null ? user.getDisplayName() : username);
        userDoc.put("provider", provider);
        if (phone != null && !phone.isEmpty()) {
            userDoc.put("phone", e164Phone(phone));
        }
        userDoc.put("updatedAt", FieldValue.serverTimestamp());
        return userDoc;
    }

    @NonNull
    private static String fallbackUsername(@NonNull FirebaseUser user) {
        if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            return normalizeUsername(user.getDisplayName().replace(" ", "."));
        }
        String email = user.getEmail();
        if (email != null && email.contains("@")) {
            return normalizeUsername(email.substring(0, email.indexOf('@')));
        }
        return user.getUid();
    }

    public static final class UsernameTakenException extends RuntimeException {
        public UsernameTakenException() {
            super(ERROR_USERNAME_TAKEN);
        }
    }
}
