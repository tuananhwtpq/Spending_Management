package com.example.spending_management.utils;

import com.example.spending_management.R;
import com.example.spending_management.models.Category;

import java.util.ArrayList;

public class Constants {
    public static String INCOME = "INCOME";
    public static String EXPENSE = "EXPENSE";

    public static ArrayList<Category> categories;
    public static void setCategories(){

        categories = new ArrayList<>();

        categories.add(new Category("Salary", R.drawable.ic_salary, R.color.category1));
        categories.add(new Category("Business", R.drawable.ic_business, R.color.category2));
        categories.add(new Category("Investment", R.drawable.ic_investment, R.color.category3));
        categories.add(new Category("Loan", R.drawable.ic_loan, R.color.category4));
        categories.add(new Category("Rent", R.drawable.ic_rent, R.color.category5));
        categories.add(new Category("Other", R.drawable.ic_other, R.color.category6));
    }

    public static Category getCategoriesDetails(String categoryName){
        for (Category cat :
                categories) {
            if(cat.getCategory_name().equals(categoryName)){
                return cat;
            }
        }

        return null;
    }

    public static int getAccountColor(String accountName){
        switch (accountName){
            case "Bank":
                return R.color.Bank;

            case "Cash":
                return R.color.Cash;
            case "MB_Bank":
                return  R.color.MB_Bank;
            case "Viettel_Money":
                return R.color.Viettel_Money;
            case "Other":
                return R.color.Other;
            default:
                return R.color.redColor;
        }
    }
}
