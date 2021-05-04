package com.runtimetitans.dv2;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {

    private TextInputLayout number;
    private LottieAnimationView verified;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private TextView reason;
    private TextView owner,vehicle_name,vehicle_class,fuel,reg_auth,insurance_expiry,vehicle_expiry,pending_fine,theft;
    private boolean clean;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        number = findViewById(R.id.number);
        verified = findViewById(R.id.verified);
        reason = findViewById(R.id.reason);
        owner = findViewById(R.id.owner);
        vehicle_name = findViewById(R.id.vehicle_name);
        vehicle_class = findViewById(R.id.vehicle_class);
        fuel = findViewById(R.id.fuel);
        reg_auth = findViewById(R.id.reg_auth);
        insurance_expiry = findViewById(R.id.ins_exp);
        vehicle_expiry = findViewById(R.id.vehicle_exp);
        pending_fine = findViewById(R.id.fine);
        theft = findViewById(R.id.theft);

        number.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                firestore.collection("vehicles").document(number.getEditText().getText().toString()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        HashMap<String,Object> map = (HashMap<String, Object>) documentSnapshot.getData();
                        if (map!=null){
                            setData(map);
                            clean = true;
                            verified.setAnimation(R.raw.accepted);
                            if (isInsuranceExpired(map)){
                                reason.setText("Insurance expired");
                                reason.setTextColor(Color.parseColor("#ff0000"));
                                clean = false;
                                verified.setAnimation(R.raw.rejected);
                            }
                            if (isValidityExpired(map)) {
                                reason.setText("Validity Expired");
                                reason.setTextColor(Color.parseColor("#ff0000"));
                                clean = false;
                                verified.setAnimation(R.raw.rejected);
                            }
                            if ((boolean) map.get("isTheft")){
                                reason.setText("Theft Vehicle");
                                reason.setTextColor(Color.parseColor("#ff0000"));
                                clean = false;
                                verified.setAnimation(R.raw.rejected);
                            }
                            if ((long) map.get("pending_fine")>0){
                                reason.setText("Fine Pending");
                                reason.setTextColor(Color.parseColor("#ff0000"));
                                clean = false;
                                verified.setAnimation(R.raw.rejected);
                            }
                            verified.playAnimation();
                            if (clean) {
                                reason.setText("Verified");
                                reason.setTextColor(getResources().getColor(R.color.green));
                            }
                        }
                        else
                            number.setError("Vehicle not found");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        number.setError("Vehicle not found");
                    }
                });

            }
        });

        number.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                number.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });




    }



    private void setData(HashMap<String, Object> map) {
        owner.setText(map.get("owner").toString());
        vehicle_name.setText(map.get("vehicle_name").toString().toUpperCase());
        vehicle_class.setText(map.get("class").toString());
        fuel.setText(map.get("fuel").toString().toUpperCase());
        reg_auth.setText(map.get("reg_auth").toString());
        pending_fine.setText(map.get("pending_fine").toString());

        if ((boolean) map.get("isTheft")) {
            theft.setText("Yes");
            theft.setTextColor(Color.parseColor("#ff0000"));
        }
        else {
            theft.setText("No");
            theft.setTextColor(getResources().getColor(R.color.green));
        }
        Timestamp ins_exp = (Timestamp) map.get("insurance_exp");
        Timestamp vehicle_exp = (Timestamp) map.get("validity");


        insurance_expiry.setText(ins_exp.toDate().toString());
        vehicle_expiry.setText(vehicle_exp.toDate().toString());

    }

    private boolean isValidityExpired(HashMap<String, Object> map) {
        Timestamp expiry = (Timestamp) map.get("validity");
        if (expiry.compareTo(Timestamp.now())<0)
            return true;



        return false;
    }

    private boolean isInsuranceExpired(HashMap<String, Object> map) {
        Timestamp ins_exp = (Timestamp) map.get("insurance_exp");
        if (ins_exp.compareTo(Timestamp.now())<0)
            return true;

        return false;
    }
}
