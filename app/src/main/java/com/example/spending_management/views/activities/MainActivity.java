package com.example.spending_management.views.activities;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.spending_management.adapters.TransactionAdapter;
import com.example.spending_management.models.Transaction;
import com.example.spending_management.utils.Constants;
import com.example.spending_management.utils.Helper;
import com.example.spending_management.views.fragments.AddTransactionFragment;
import com.example.spending_management.R;
import com.example.spending_management.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    Calendar calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolBar);
        getSupportActionBar().setTitle("Transactions");

        binding.floatingActionButton.setOnClickListener(c -> {
            new AddTransactionFragment().show(getSupportFragmentManager(), null);
        });

        ArrayList<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("Income", "Business", "Cash", "Some note here", new Date(), 500, 2));
        transactions.add(new Transaction("Expense", "Rent", "MB Bank", "Some note here", new Date(), 100, 3));
        transactions.add(new Transaction("Income", "Investment", "Viettel Money", "Some note here", new Date(), 900, 4));
        transactions.add(new Transaction("Expense", "Loan", "Bank", "Some note here", new Date(), 700, 5));
        transactions.add(new Transaction("Income", "Other", "Bank", "Some note here", new Date(), 200, 6));

        TransactionAdapter transactionAdapter = new TransactionAdapter(this, transactions);
        binding.transactionList.setLayoutManager(new LinearLayoutManager(this));
        binding.transactionList.setAdapter(transactionAdapter);


        Constants.setCategories();
        calendar = Calendar.getInstance();
        updateDate();

        binding.previousDateBtn.setOnClickListener(c -> {
            calendar.add(Calendar.DATE, -1);
            updateDate();
        });

        binding.nextDateBtn.setOnClickListener(c -> {
            calendar.add(Calendar.DATE, 1);
            updateDate();
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    void updateDate(){
        //SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM, yyyy");
        binding.currentDate.setText(Helper.formatDate(calendar.getTime()));
    }
}