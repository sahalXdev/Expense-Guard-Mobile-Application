package com.example.expenseguard;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.expenseguard.databinding.ActivityDashBoardBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DashBoardActivity extends AppCompatActivity {
    ActivityDashBoardBinding binding;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    ArrayList<TransactionModel> transactionModelArrayList;
    TransactionAdapter transactionAdapter;
    int sumExpense = 0;
    int sumIncome = 0;

    @Override
    public void onBackPressed() {

        AlertDialog.Builder exit = new AlertDialog.Builder(DashBoardActivity.this);
        exit.setTitle("Exit !");
        exit.setMessage("Do You Want To Exit ?");
        exit.setCancelable(false);
        exit.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        exit.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //finish();
                finishAffinity();

            }
        });
        exit.create().show();

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        transactionModelArrayList = new ArrayList<>();

        binding.historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.historyRecyclerView.setHasFixedSize(true);





        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser()==null){
                    startActivity(new Intent(DashBoardActivity.this,MainActivity.class));
                    finish();


                }
            }
        });

        binding.addFloatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startActivity(new Intent(DashBoardActivity.this,AddTransactionActivity.class));
                }catch (Exception e){

                }
            }
        });


        loadData();

        binding.refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startActivity(new Intent(DashBoardActivity.this,DashBoardActivity.class));
                    Toast.makeText(DashBoardActivity.this, "Refreshed", Toast.LENGTH_SHORT).show();
                }catch (Exception e){

                }

            }
        });
        binding.signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createSignOutDialog();
            }
        });

    }



    private void createSignOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DashBoardActivity.this);
        builder.setTitle("SignOut")
                .setMessage("Are You Sure Want To SignOut This ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        firebaseAuth.signOut();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        builder.create().show();
    }



    private void loadData(){
        firebaseFirestore.collection("Expenses").document(firebaseAuth.getUid()).collection("Notes")
                .orderBy("date", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for (DocumentSnapshot ds:task.getResult()){
                            TransactionModel model = new TransactionModel(
                                    ds.getString("id"),
                                    ds.getString("note"),
                                    ds.getString("amount"),
                                    ds.getString("type"),
                                    ds.getString("date"));
                            int amount = Integer.parseInt(ds.getString("amount"));
                            if (ds.getString("type").equals("Expense")){
                                sumExpense=sumExpense+amount;
                            }else {
                                sumIncome=sumIncome+amount;
                            }
                            transactionModelArrayList.add(model);
                        }
                        binding.totalIncome.setText(String.valueOf(sumIncome));
                        binding.totalExpense.setText(String.valueOf(sumExpense));
                        binding.totalBalance.setText(String.valueOf(sumIncome-sumExpense));

                        transactionAdapter = new TransactionAdapter(DashBoardActivity.this,transactionModelArrayList);
                        binding.historyRecyclerView.setAdapter(transactionAdapter);
                    }
                });
    }
}