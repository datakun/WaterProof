package com.kimjunu.waterproof;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.GoogleAuthProvider;

public class SettingsActivity extends BaseActivity {

    private static final String TAG = "Settings";

    private Button mBtnSign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mBtnSign = (Button) findViewById(R.id.btnSign);

        if (WaterProofApplication.mAuthUser == null) {
            signIn();

            mBtnSign.setText("Sign In");
        }
        else {
            mBtnSign.setText("Revoke");
        }

        mBtnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((Button) view).getText() == "Sign In") {
                    signIn();
                }
                else {
                    revokeAccess();
                }
            }
        });
    }

    public void onRevoke(View view) {
        revokeAccess();
    }

    @Override
    public void revokeAccess() {
        WaterProofApplication.mAuth.signOut();

        Auth.GoogleSignInApi.revokeAccess(WaterProofApplication.mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        hideProgressDialog();

                        mBtnSign.setText("Sign In");
                    }
                });
    }

    @Override
    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        WaterProofApplication.mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // 파이어 베이스에서 인증 실패한듯
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(SettingsActivity.this, "인증 실패", Toast.LENGTH_SHORT).show();

                            mBtnSign.setText("Sign In");
                        } else {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(SettingsActivity.this, "인증 성공.", Toast.LENGTH_SHORT).show();

                            mBtnSign.setText("Revoke");
                        }

                        hideProgressDialog();
                    }
                });
    }
}
