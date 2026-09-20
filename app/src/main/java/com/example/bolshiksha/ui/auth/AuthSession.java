package com.example.bolshiksha.ui.auth;

import android.content.Context;
import android.os.CancellationSignal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.credentials.ClearCredentialStateRequest;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.exceptions.ClearCredentialException;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executors;

public final class AuthSession {

    private AuthSession() {
    }

    @Nullable
    public static FirebaseUser currentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static boolean isSignedIn() {
        return currentUser() != null;
    }

    public static void signOut(@NonNull Context context, @Nullable Runnable onComplete) {
        FirebaseAuth.getInstance().signOut();
        CredentialManager credentialManager = CredentialManager.create(context.getApplicationContext());
        credentialManager.clearCredentialStateAsync(
                new ClearCredentialStateRequest(),
                new CancellationSignal(),
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<Void, ClearCredentialException>() {
                    @Override
                    public void onResult(@NonNull Void unused) {
                        if (onComplete != null) {
                            onComplete.run();
                        }
                    }

                    @Override
                    public void onError(@NonNull ClearCredentialException e) {
                        if (onComplete != null) {
                            onComplete.run();
                        }
                    }
                });
    }

    @NonNull
    public static String displayName() {
        FirebaseUser user = currentUser();
        if (user == null) {
            return "";
        }
        if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            return user.getDisplayName();
        }
        return user.getEmail() != null ? user.getEmail() : "";
    }

    @NonNull
    public static String email() {
        FirebaseUser user = currentUser();
        return user != null && user.getEmail() != null ? user.getEmail() : "";
    }
}
