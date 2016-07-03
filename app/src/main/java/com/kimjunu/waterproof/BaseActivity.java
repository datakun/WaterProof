package com.kimjunu.waterproof;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.GoogleAuthProvider;

public class BaseActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "BaseActivity";

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 구글 인증 빌더 생성(허용 범위 설정 가능)
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        // enableAutoMange의 액티비티는 꼭 AppCompatActivity를 상속받아야한다
        WaterProofApplication.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 구글 인증 인텐드를 처리
        if (requestCode == WaterProofApplication.RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // 구글 인증을 성공하면 파이어베이스 인증 시작
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // 구글 인증 인텐드 취소한듯
                Toast.makeText(this, "인증 실패", Toast.LENGTH_SHORT).show();

                hideProgressDialog();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        WaterProofApplication.mAuth.addAuthStateListener(WaterProofApplication.mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (WaterProofApplication.mAuthListener != null) {
            WaterProofApplication.mAuth.removeAuthStateListener(WaterProofApplication.mAuthListener);
        }

        hideProgressDialog();
    }

    // 연결 실패 리스너
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Working harder...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    // 로그인
    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(WaterProofApplication.mGoogleApiClient);
        startActivityForResult(signInIntent, WaterProofApplication.RC_SIGN_IN);
    }

    // 로그아웃
    public void signOut() {
        // 파이어베이스 로그아웃
        WaterProofApplication.mAuth.signOut();

        // 구글 로그아웃
        if (WaterProofApplication.mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(WaterProofApplication.mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            hideProgressDialog();
                        }
                    });
        }
    }

    // 탈퇴
    public void revokeAccess() {
        WaterProofApplication.mAuth.signOut();

        Auth.GoogleSignInApi.revokeAccess(WaterProofApplication.mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        hideProgressDialog();
                    }
                });
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        WaterProofApplication.mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // 파이어 베이스에서 인증 실패한듯
                        if (!task.isSuccessful()) {
                            Toast.makeText(BaseActivity.this, "인증 실패", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BaseActivity.this, "인증 성공.", Toast.LENGTH_SHORT).show();
                        }

                        hideProgressDialog();
                    }
                });
    }
}
