package com.alx.findphone.claptofind.flashalert.consent_dialog.dialog;


import static com.alx.findphone.claptofind.flashalert.App.PRODUCT_LIFETIME;
import static com.alx.findphone.claptofind.flashalert.App.PRODUCT_SUBS;
import static com.alx.findphone.claptofind.flashalert.utils.constant.Constants.KEY_CANCEL;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.alx.findphone.claptofind.flashalert.R;
import com.alx.findphone.claptofind.flashalert.databinding.DialogBillingBinding;
import com.alx.findphone.claptofind.flashalert.main.activity.PermissionActivity;
import com.common.control.interfaces.PurchaseCallback;
import com.common.control.manager.PurchaseManager;
import com.common.control.manager.PurchaseManagerInApp;

public class BillingDialog extends BaseDialog<DialogBillingBinding> implements PurchaseCallback {
    private static BillingDialog INSTANCE;
    private boolean isBillingSub = true;

    public static BillingDialog getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BillingDialog();
        }
        return INSTANCE;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.dialog_billing;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    protected void initView() {
        PurchaseManager.getInstance().setCallback(this);
        setCancelable(false);
        setSize();
    }

    private void setSize() {
        int w = Resources.getSystem().getDisplayMetrics().widthPixels;
        binding.tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, 20 * w / 360f);
        binding.tvContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, 15 * w / 360f);
        binding.tvBenefit1.setTextSize(TypedValue.COMPLEX_UNIT_PX, 13 * w / 360f);
        binding.tvBenefit2.setTextSize(TypedValue.COMPLEX_UNIT_PX, 13 * w / 360f);
        binding.tvBenefit3.setTextSize(TypedValue.COMPLEX_UNIT_PX, 13 * w / 360f);
        binding.tvBenefit4.setTextSize(TypedValue.COMPLEX_UNIT_PX, 13 * w / 360f);

        binding.tvDateSub.setTextSize(TypedValue.COMPLEX_UNIT_PX, 12 * w / 360f);
        binding.tvForever.setTextSize(TypedValue.COMPLEX_UNIT_PX, 12 * w / 360f);

        binding.tvContinue.setTextSize(TypedValue.COMPLEX_UNIT_PX, 14 * w / 360f);
        binding.tvPolicy.setTextSize(TypedValue.COMPLEX_UNIT_PX, 12 * w / 360f);
        binding.tvPolicy.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void addEvent() {
        binding.tvPrice.setText(PurchaseManager.getInstance().getPriceSub(PRODUCT_SUBS));
        if (PurchaseManager.getInstance().getBillingPeriod(PRODUCT_SUBS).isBlank()) {
            binding.tvDateSub.setText(getString(R.string._7_days, "7"));
        } else binding.tvDateSub.setText(getString(R.string._7_days, PurchaseManager.getInstance().getBillingPeriod(PRODUCT_SUBS)));

        binding.tvPriceForever.setText(PurchaseManagerInApp.getInstance().getPriceInApp(PRODUCT_LIFETIME));

        binding.ivClose.setOnClickListener(v -> {
            callback.callback(KEY_CANCEL, null);
        });

        binding.tvBasic.setSelected(true);
        binding.layoutTextBasic.setSelected(true);
        binding.tvPremium.setSelected(false);
        binding.layoutTextPremium.setSelected(false);
        binding.cvPremium.setStrokeColor(Color.parseColor("#0263D1"));
        binding.cvIncludeAds.setStrokeColor(Color.TRANSPARENT);

        binding.cvPremium.setOnClickListener(v -> {
            isBillingSub = true;
            binding.tvBasic.setSelected(true);
            binding.layoutTextBasic.setSelected(true);
            binding.tvPremium.setSelected(false);
            binding.layoutTextPremium.setSelected(false);

            binding.cvPremium.setStrokeColor(Color.parseColor("#0263D1"));
            binding.cvIncludeAds.setStrokeColor(Color.TRANSPARENT);
        });
        binding.cvIncludeAds.setOnClickListener(v -> {
            isBillingSub = false;
            binding.tvBasic.setSelected(false);
            binding.layoutTextBasic.setSelected(false);
            binding.tvPremium.setSelected(true);
            binding.layoutTextPremium.setSelected(true);
            binding.cvIncludeAds.setStrokeColor(Color.parseColor("#0263D1"));
            binding.cvPremium.setStrokeColor(Color.TRANSPARENT);
        });
        binding.tvContinue.setOnClickListener(v -> {
            if (isBillingSub) {
                PurchaseManager.getInstance().launchPurchase(requireActivity(), PRODUCT_SUBS);
            } else {
//                callback.callback(Const.KEY_CANCEL, null);
                PurchaseManagerInApp.getInstance().launchPurchase(requireActivity(), PRODUCT_LIFETIME);
            }
        });
        binding.tvPolicy.setOnClickListener(view ->
                callback.callback(KEY_CANCEL, null)
        );
    }

    @Override
    public void dismiss() {
        super.dismiss();
        INSTANCE = null;
    }

    @Override
    public void purchaseSuccess() {
        ProgressDialog progressBar = new ProgressDialog(requireContext());
        progressBar.setTitle(getString(R.string.please_wait));
        progressBar.setMessage(getString(R.string.processing_your_pack));
        progressBar.show();

        new Handler().postDelayed(() -> {
            requireActivity().finishAffinity();
            startActivity(new Intent(requireContext(), PermissionActivity.class));
        }, 2000);
    }

    @Override
    public void purchaseFail() {

    }

    public void show(AppCompatActivity activity) {
        try {
            show(activity.getSupportFragmentManager(), getClass().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
