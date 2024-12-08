package com.example.spending_management.views.activities;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
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
import com.example.spending_management.views.fragments.ClickInfor;
import com.example.spending_management.views.fragments.StatsFragment;
import com.example.spending_management.views.fragments.TransactionsFragment;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    Calendar calendar;
    public MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);



        setSupportActionBar(binding.toolBar);
        getSupportActionBar().setTitle("Transactions");


        Constants.setCategories();
        calendar = Calendar.getInstance();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, new TransactionsFragment());
        transaction.commit();

        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if(item.getItemId() == R.id.transaction){
                    getSupportFragmentManager().popBackStack();

                }else if (item.getItemId() == R.id.stats){
                    transaction.replace(R.id.content, new StatsFragment());
                    transaction.addToBackStack(null);
                } else if (item.getItemId() == R.id.more){
                    showMoreMenu();
                }
                transaction.commit();
                return true;
            }
        });
    }

    private void showMoreMenu(){
        PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.more));

        popupMenu.getMenuInflater().inflate(R.menu.more_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.export_excel) {
                exportExel();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void exportExel(){
        try {
            RealmResults<Transaction> transactions = viewModel.transactions.getValue();
            if (transactions != null && !transactions.isEmpty()) {
                List<Transaction> transactionList = new ArrayList<>(transactions);
                // Gọi hàm exportExcel từ Fragment hoặc tạo phương thức này ở MainActivity
                exportExcel(transactionList);
            } else {
                Toast.makeText(this, "No transactions to export", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Error exporting Excel file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private void exportExcel(List<Transaction> transactions) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Transactions");

        // Tạo tiêu đề cột
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Amount");
        headerRow.createCell(2).setCellValue("Category");
        headerRow.createCell(3).setCellValue("Date");

        // Thêm dữ liệu vào các dòng tiếp theo
        int rowNum = 1;
        for (Transaction transaction : transactions) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(transaction.getId());
            row.createCell(1).setCellValue(transaction.getAmount());
            row.createCell(2).setCellValue(transaction.getCategory());
            row.createCell(3).setCellValue(transaction.getDate());
        }

        // Lưu file
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Transactions.xlsx");
        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
            workbook.close();
            Toast.makeText(this, "Excel file exported to: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error exporting Excel file", Toast.LENGTH_LONG).show();
        }
    }

    public void getTransactions() {
        viewModel.getTransactions(calendar);
    }

    public MenuItem searchItem, thongBao;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu,menu);
        searchItem = menu.findItem(R.id.search);
        thongBao = menu.findItem(R.id.thongBao);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item == searchItem)
        {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            EditText editText = new EditText(this);
//            editText.setHint("Nhập từ khóa");
//            builder.setTitle("Tìm kiếm")
//                    .setView(editText)
//                    .setPositiveButton("OK", (dialog, which) -> {
//                        Toast.makeText(MainActivity.this, "Long", Toast.LENGTH_SHORT).show();
//                    })
//                    .setNegativeButton("Thoát", null)
//                    .show();
            Toast.makeText(this, "Vui lòng đăng ký Vip để tìm kiếm!", Toast.LENGTH_SHORT).show();
        }
        else if (item == thongBao)
        {
            Toast.makeText(this, "Không có thông báo!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }


}