package net.xxhong.rosclient.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import net.xxhong.rosclient.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PayActivity extends Activity {

    @Bind(R.id.tv_price)
    TextView tvPrice;
    @Bind(R.id.tv_ttmile)
    TextView tvMile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        ButterKnife.bind(this);

        // 显示总里程和价格
        showPrice();
    }

    private void showPrice() {
        double total_mile = 3.2*(double)(PathSelect.total_nodes-1);
        double total_price = 12;
        if (total_mile < 7){
        tvPrice.setText("￥ "+String.format("%.2f",total_price));
        tvMile.setText(String.format("%.2f", total_mile)+" km");
        }
        else
        {
            total_price = total_mile * 3.0;
            tvPrice.setText("￥ "+String.format("%.2f",total_price));
            tvMile.setText(String.format("%.2f",total_mile)+" km");
        }
    }

    @OnClick({R.id.btn_pay})
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_pay:

                StringBuilder result= new StringBuilder();

                result.append("Thank you!");

                new AlertDialog.Builder(PayActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle("Payment success")
                        .setMessage(result)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //finish();//Exit Activity
                                startActivity(new Intent(PayActivity.this, Call.class));
                            }
                        }).create().show();

                break;
            default:break;
        }
    }
}


