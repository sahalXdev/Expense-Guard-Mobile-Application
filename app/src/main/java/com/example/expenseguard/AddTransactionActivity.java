package com.example.expenseguard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expenseguard.databinding.ActivityAddTransactionBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class AddTransactionActivity extends AppCompatActivity {
    ActivityAddTransactionBinding binding;
    FirebaseFirestore fStore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String type="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fStore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        binding.expenseCheckBoxAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type="Expense";
                binding.expenseCheckBoxAdd.setChecked(true);
                binding.incomeCheckBoxAdd.setChecked(false);
            }
        });
        binding.incomeCheckBoxAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type="Income";
                binding.expenseCheckBoxAdd.setChecked(false);
                binding.incomeCheckBoxAdd.setChecked(true);
            }
        });
        binding.btnAddTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount = binding.userAmountAdd.getText().toString().trim();
                String note = binding.userNoteAdd.getText().toString().trim();
                if (amount.length() <= 0) {
                    return;
                }
                if (type.length() <= 0) {
                    Toast.makeText(AddTransactionActivity.this, "Select Transaction Type", Toast.LENGTH_SHORT).show();
                }
                SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy HH:mm ss", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());


                 String id= UUID.randomUUID().toString();




                Map<String, Object> transaction = new HashMap<>();
                transaction.put("id",id);
                transaction.put("amount", amount);
                transaction.put("note", note);
                transaction.put("type", type);
                transaction.put("date", currentDateandTime);


                fStore.collection("Expenses").document(firebaseAuth.getUid()).collection("Notes")

                        .document(id)
                        .set(transaction)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(AddTransactionActivity.this, "Added", Toast.LENGTH_SHORT).show();
                                try {
                                    startActivity(new Intent(AddTransactionActivity.this, DashBoardActivity.class));
                                } catch (Exception e) {

                                }
                            }

                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddTransactionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
    public class SequentialUUIDGenerator{
        private long currentCounter;

        public String generateSequentialUUID(){
            currentCounter=currentCounter++;

            UUID uuid = new UUID(currentCounter,5L);

            return uuid.toString();
        }

    }
}