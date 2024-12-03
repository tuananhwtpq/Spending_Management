package com.example.spending_management.views.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.spending_management.R;
import com.example.spending_management.adapters.AccountAdapter;
import com.example.spending_management.adapters.CategoryAdapter;
import com.example.spending_management.databinding.FragmentViewInforBinding;
import com.example.spending_management.databinding.ListDialogBinding;
import com.example.spending_management.models.Account;
import com.example.spending_management.models.Transaction;
import com.example.spending_management.utils.Constants;
import com.example.spending_management.utils.Helper;
import com.example.spending_management.viewmodels.MainViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;

public class ViewInforFragment extends BottomSheetDialogFragment {

    FragmentViewInforBinding binding;
    Transaction transaction;
    MainViewModel viewModel;

    public ViewInforFragment() {
        // Required empty public constructor
    }

    public static ViewInforFragment newInstance(Transaction transaction) {
        ViewInforFragment fragment = new ViewInforFragment();
        Bundle args = new Bundle();
        args.putSerializable("transaction", transaction);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentViewInforBinding.inflate(inflater);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        if (getArguments() != null) {
            transaction = (Transaction) getArguments().getSerializable("transaction");
            transaction = Realm.getDefaultInstance().copyFromRealm(transaction);
        }

        if (transaction == null) {
            Toast.makeText(getContext(), "Transaction data is missing.", Toast.LENGTH_SHORT).show();
            dismiss();  // Đóng BottomSheet nếu không có dữ liệu
            return binding.getRoot();
        }

        // Display current transaction details
        bindTransactionData();

        // Set Date picker
        binding.viewInfoDate.setOnClickListener(v -> showDatePicker());

        // Set Category picker
        binding.viewInfoCategory.setOnClickListener(v -> showCategoryPicker(inflater));

        // Set Account picker
        binding.viewInfoAccount.setOnClickListener(v -> showAccountPicker(inflater));

        // Edit transaction button click
        binding.editTransactionBtn.setOnClickListener(v -> editTransaction());

        // Delete transaction button click


        return binding.getRoot();
    }

    private void bindTransactionData() {
        binding.viewInfoAmount.setText(String.valueOf(transaction.getAmount()));
        binding.viewInfoNote.setText(transaction.getNote());
        binding.viewInfoDate.setText(Helper.formatDate(transaction.getDate()));
        binding.viewInfoCategory.setText(transaction.getCategory());
        binding.viewInfoAccount.setText(transaction.getAccount());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext());
        datePickerDialog.setOnDateSetListener((datePicker, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            String dateToShow = Helper.formatDate(calendar.getTime());
            binding.viewInfoDate.setText(dateToShow);
            updateTransactionDate(calendar.getTime());
        });
        datePickerDialog.show();
    }

    private void updateTransactionDate(Date date) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(realmInstance -> {
            transaction.setDate(date);
            transaction.setId(date.getTime());
        }, realm::close, error -> {
            Toast.makeText(getContext(), "Error updating date", Toast.LENGTH_SHORT).show();
            realm.close();
        });
    }

    private void showCategoryPicker(LayoutInflater inflater) {
        ListDialogBinding dialogBinding = ListDialogBinding.inflate(inflater);
        android.app.AlertDialog categoryDialog = new android.app.AlertDialog.Builder(getContext()).create();
        categoryDialog.setView(dialogBinding.getRoot());
        CategoryAdapter categoryAdapter = new CategoryAdapter(getContext(), Constants.categories, category -> {
            binding.viewInfoCategory.setText(category.getCategoryName());
            updateTransactionCategory(category.getCategoryName());
            categoryDialog.dismiss();
        });
        dialogBinding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        dialogBinding.recyclerView.setAdapter(categoryAdapter);
        categoryDialog.show();
    }

    private void updateTransactionCategory(String categoryName) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(realmInstance -> transaction.setCategory(categoryName), realm::close, error -> {
            Toast.makeText(getContext(), "Error updating category", Toast.LENGTH_SHORT).show();
            realm.close();
        });
    }

    private void showAccountPicker(LayoutInflater inflater) {
        ListDialogBinding dialogBinding = ListDialogBinding.inflate(inflater);
        android.app.AlertDialog accountsDialog = new android.app.AlertDialog.Builder(getContext()).create();
        accountsDialog.setView(dialogBinding.getRoot());

        ArrayList<Account> accounts = new ArrayList<>();
        accounts.add(new Account(0, "Cash"));
        accounts.add(new Account(0, "Bank"));
        accounts.add(new Account(0, "PayPal"));
        accounts.add(new Account(0, "Viettel Money"));
        accounts.add(new Account(0, "Other"));

        AccountAdapter adapter = new AccountAdapter(getContext(), accounts, account -> {
            binding.viewInfoAccount.setText(account.getAccount_name());
            updateTransactionAccount(account.getAccount_name());
            accountsDialog.dismiss();
        });
        dialogBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dialogBinding.recyclerView.setAdapter(adapter);
        accountsDialog.show();
    }

    private void updateTransactionAccount(String accountName) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(realmInstance -> transaction.setAccount(accountName), realm::close, error -> {
            Toast.makeText(getContext(), "Error updating account", Toast.LENGTH_SHORT).show();
            realm.close();
        });
    }

    private void editTransaction() {
        // Inflate dialog layout for editing transaction details
        final View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_transaction, null);

        // Initialize fields in the dialog to show current transaction data
        EditText amountEditText = dialogView.findViewById(R.id.view_info_amount);
        EditText noteEditText = dialogView.findViewById(R.id.view_info_note);
        amountEditText.setText(String.valueOf(transaction.getAmount()));
        noteEditText.setText(transaction.getNote());

        // Create and display AlertDialog
        new AlertDialog.Builder(getContext())
                .setTitle("Edit Transaction")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    // Get the edited data from dialog fields
                    String newAmountStr = amountEditText.getText().toString();
                    String newNote = noteEditText.getText().toString();

                    // Validate amount
                    if (!newAmountStr.isEmpty()) {
                        double newAmount = Double.parseDouble(newAmountStr);
                        updateTransactionDetails(newAmount, newNote);
                    } else {
                        Toast.makeText(getContext(), "Amount cannot be empty.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateTransactionDetails(double newAmount, String newNote) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(realmInstance -> {
            transaction.setAmount(newAmount);
            transaction.setNote(newNote);
        }, realm::close, error -> {
            Toast.makeText(getContext(), "Error updating transaction", Toast.LENGTH_SHORT).show();
            realm.close();
        });
    }


    private void showDeleteDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Delete", (dialog, which) -> deleteTransaction())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteTransaction() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(realmInstance -> {
            // Xóa giao dịch khỏi Realm
            transaction.deleteFromRealm();
        }, () -> {
            // Nếu xóa thành công
            Toast.makeText(getContext(), "Transaction deleted.", Toast.LENGTH_SHORT).show();
            dismiss();  // Đóng Fragment sau khi xóa
        }, error -> {
            // Nếu có lỗi xảy ra
            Toast.makeText(getContext(), "Error deleting transaction.", Toast.LENGTH_SHORT).show();
            realm.close();
        });
    }
}
