package com.example.spending_management.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spending_management.R;
import com.example.spending_management.databinding.RowTransactionBinding;
import com.example.spending_management.models.Category;
import com.example.spending_management.models.Transaction;
import com.example.spending_management.utils.Constants;
import com.example.spending_management.utils.Helper;

import java.util.ArrayList;

import io.realm.RealmResults;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {


    Context context;
    RealmResults<Transaction> transactions;

    public TransactionAdapter(Context context, RealmResults<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TransactionViewHolder(LayoutInflater.from(context).inflate(R.layout.row_transaction, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.binding.transactionAmount.setText(String.valueOf(transaction.getAmount()));
        holder.binding.accountLbl.setText(transaction.getAccount());

        holder.binding.transactionDate.setText(Helper.formatDate(transaction.getDate()));
        holder.binding.transactionCategory.setText(transaction.getCategory());

        Category transactionCategory = Constants.getCategoryDetails(transaction.getCategory());

        holder.binding.categoryIcon.setImageResource(transactionCategory.getCategory_image());
        holder.binding.categoryIcon.setBackgroundTintList(context.getColorStateList(transactionCategory.getCategory_color()));

        holder.binding.accountLbl.setBackgroundTintList(context.getColorStateList(Constants.getAccountColor(transaction.getAccount())));

        if(transaction.getType().equals(Constants.INCOME)){
            holder.binding.transactionAmount.setTextColor(context.getColor(R.color.greenColor));
        }else if (transaction.getType().equals(Constants.EXPENSE)){
            holder.binding.transactionAmount.setTextColor(context.getColor(R.color.redColor));
        }



    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public class TransactionViewHolder extends RecyclerView.ViewHolder{
        RowTransactionBinding binding;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowTransactionBinding.bind(itemView);
        }
    }
}
