package com.example.bolshiksha.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialCancellationException;
import androidx.credentials.exceptions.GetCredentialException;

import com.example.bolshiksha.MainActivity;
import com.example.bolshiksha.R;
import com.example.bolshiksha.databinding.ActivityLoginBinding;
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private CredentialManager credentialManager;
    private CancellationSignal cancellationSignal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AuthSession.isSignedIn()) {
            openMain();
            return;
        }

        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.loginRoot, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        credentialManager = CredentialManager.create(this);
        binding.btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());
        binding.btnCreateAccount.setOnClickListener(v -> createAccount());
        binding.btnEmailSignIn.setOnClickListener(v -> signInWithUsernamePassword());
    }

    private void createAccount() {
        String username = UserProfileStore.normalizeUsername(textOf(binding.etUsername));
        String phone = UserProfileStore.normalizePhone(textOf(binding.etPhone));
        String password = textOf(binding.etPassword);
        if (!validateCredentials(username, password, phone, true)) {
            return;
        }

        setLoading(true);
        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(UserProfileStore.authEmailFor(username), password)
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()) {
                        setLoading(false);
                        Exception e = task.getException();
                        if (e instanceof FirebaseAuthUserCollisionException) {
                            toast(R.string.username_taken);
                            return;
                        }
                        toastError(R.string.signup_error, e);
                        return;
                    }

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user == null) {
                        setLoading(false);
                        toast(R.string.signup_error);
                        return;
                    }

                    UserProfileStore.updateDisplayName(user, username)
                            .continueWithTask(ignored ->
                                    UserProfileStore.claimUsernameAndSave(user, username, phone, "password"))
                            .addOnCompleteListener(this, saveTask -> {
                                if (saveTask.isSuccessful()) {
                                    Toast.makeText(this, R.string.signup_success, Toast.LENGTH_SHORT).show();
                                    openMain();
                                    return;
                                }
                                Exception saveError = saveTask.getException();
                                if (UserProfileStore.isUsernameTaken(saveError)) {
                                    user.delete().addOnCompleteListener(ignored -> {
                                        FirebaseAuth.getInstance().signOut();
                                        setLoading(false);
                                        toast(R.string.username_taken);
                                    });
                                    return;
                                }
                                // Auth user exists; still let them in and retry profile later.
                                toastError(R.string.signup_error, saveError);
                                openMain();
                            });
                });
    }

    private void signInWithUsernamePassword() {
        String username = UserProfileStore.normalizeUsername(textOf(binding.etUsername));
        String phone = UserProfileStore.normalizePhone(textOf(binding.etPhone));
        String password = textOf(binding.etPassword);
        if (!validateCredentials(username, password, phone, false)) {
            return;
        }

        setLoading(true);
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(UserProfileStore.authEmailFor(username), password)
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()) {
                        setLoading(false);
                        toastError(R.string.login_error, task.getException());
                        return;
                    }
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user == null) {
                        setLoading(false);
                        toast(R.string.login_error);
                        return;
                    }
                    UserProfileStore.saveExistingUser(user, username, phone, "password")
                            .addOnCompleteListener(ignored -> openMain());
                });
    }

    private boolean validateCredentials(
            @NonNull String username,
            @NonNull String password,
            @NonNull String phone,
            boolean requirePhone
    ) {
        binding.usernameLayout.setError(null);
        binding.phoneLayout.setError(null);
        binding.passwordLayout.setError(null);

        if (TextUtils.isEmpty(username)) {
            binding.usernameLayout.setError(getString(R.string.username_required));
            return false;
        }
        if (!UserProfileStore.isValidUsername(username)) {
            binding.usernameLayout.setError(getString(R.string.username_invalid));
            return false;
        }
        if (requirePhone && TextUtils.isEmpty(phone)) {
            binding.phoneLayout.setError(getString(R.string.phone_required));
            return false;
        }
        if (!TextUtils.isEmpty(phone) && !UserProfileStore.isValidPhone(phone)) {
            binding.phoneLayout.setError(getString(R.string.phone_invalid));
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            binding.passwordLayout.setError(getString(R.string.password_required));
            return false;
        }
        if (password.length() < 6) {
            binding.passwordLayout.setError(getString(R.string.password_too_short));
            return false;
        }
        return true;
    }

    private void signInWithGoogle() {
        setLoading(true);
        cancellationSignal = new CancellationSignal();

        GetSignInWithGoogleOption googleOption = new GetSignInWithGoogleOption.Builder(
                getString(R.string.google_web_client_id)
        ).build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleOption)
                .build();

        credentialManager.getCredentialAsync(
                this,
                request,
                cancellationSignal,
                ContextCompat.getMainExecutor(this),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(@NonNull GetCredentialResponse result) {
                        handleCredential(result.getCredential());
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        setLoading(false);
                        if (e instanceof GetCredentialCancellationException) {
                            Toast.makeText(LoginActivity.this, R.string.login_cancelled,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(LoginActivity.this,
                                getString(R.string.login_error) + ": " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void handleCredential(@NonNull Credential credential) {
        if (!(credential instanceof CustomCredential customCredential)
                || !GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                .equals(customCredential.getType())) {
            setLoading(false);
            Toast.makeText(this, R.string.login_error, Toast.LENGTH_SHORT).show();
            return;
        }

        GoogleIdTokenCredential googleIdTokenCredential =
                GoogleIdTokenCredential.createFrom(customCredential.getData());
        AuthCredential firebaseCredential =
                GoogleAuthProvider.getCredential(googleIdTokenCredential.getIdToken(), null);

        FirebaseAuth.getInstance().signInWithCredential(firebaseCredential)
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()) {
                        setLoading(false);
                        toastError(R.string.login_error, task.getException());
                        return;
                    }
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user == null) {
                        setLoading(false);
                        toast(R.string.login_error);
                        return;
                    }
                    UserProfileStore.saveExistingUser(user, null, null, "google")
                            .addOnCompleteListener(ignored -> openMain());
                });
    }

    @NonNull
    private static String textOf(@NonNull android.widget.EditText field) {
        CharSequence value = field.getText();
        return value == null ? "" : value.toString();
    }

    private void toast(int messageRes) {
        Toast.makeText(this, messageRes, Toast.LENGTH_LONG).show();
    }

    private void toastError(int prefixRes, Exception e) {
        String message = e != null && e.getMessage() != null
                ? getString(prefixRes) + ": " + e.getMessage()
                : getString(prefixRes);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void setLoading(boolean loading) {
        binding.progressSignIn.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnGoogleSignIn.setEnabled(!loading);
        binding.btnCreateAccount.setEnabled(!loading);
        binding.btnEmailSignIn.setEnabled(!loading);
        binding.etUsername.setEnabled(!loading);
        binding.etPhone.setEnabled(!loading);
        binding.etPassword.setEnabled(!loading);
    }

    private void openMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
        }
        super.onDestroy();
    }
}
