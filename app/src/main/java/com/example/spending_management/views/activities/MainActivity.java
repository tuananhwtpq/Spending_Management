package com.example.spending_management.views.activities;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.spending_management.adapters.TransactionAdapter;
import com.example.spending_management.models.Transaction;
import com.example.spending_management.utils.Constants;
import com.example.spending_management.utils.Helper;
import com.example.spending_management.viewmodels.MainViewModel;
import com.example.spending_management.views.fragments.AddTransactionFragment;
import com.example.spending_management.R;
import com.example.spending_management.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    Calendar calendar;

    Realm realm ;

    MainViewModel viewModel ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        setSupportActionBar(binding.toolBar);
        getSupportActionBar().setTitle("Transactions");

        binding.floatingActionButton.setOnClickListener(c -> {
            new AddTransactionFragment().show(getSupportFragmentManager(), null);
        });








        binding.transactionList.setLayoutManager(new LinearLayoutManager(this));
        viewModel.transactions.observe(this, new Observer<RealmResults<Transaction>>() {
            @Override
            public void onChanged(RealmResults<Transaction> transactions) {
                TransactionAdapter transactionAdapter = new TransactionAdapter(MainActivity.this, transactions);

                binding.transactionList.setAdapter(transactionAdapter);
            }
        });




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

    public  void setupDataBase(){
        Realm.init(this);
        realm =  Realm.getDefaultInstance();
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