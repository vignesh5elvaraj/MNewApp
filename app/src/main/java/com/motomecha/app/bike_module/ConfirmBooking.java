package com.motomecha.app.bike_module;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.motomecha.app.R;
import com.motomecha.app.dbhandler.SQLiteHandler;

import java.util.Calendar;
import java.util.HashMap;

public class ConfirmBooking extends AppCompatActivity implements View.OnClickListener{
    ImageButton dateselect;
    private int mYear, mMonth, mDay;
    TextView Edat;
    EditText Eotherreq,Eaddress;
    ImageButton Iconfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_booking);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView tv = (TextView) findViewById(R.id.text_view_toolb);
        Typeface custom_font = Typeface.createFromAsset(getApplication().getAssets(), "fonts/rama.ttf");
        assert tv != null;
        tv.setTypeface(custom_font);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        String text = "<font color=#ff1545>CONFIRM</font> <font color=#ffffff>BOOKING</font>";
        tv.setText(Html.fromHtml(text));
        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
        final HashMap<String, String> user = db.getUserDetails();
        Eotherreq=(EditText) findViewById(R.id.textView6);
        Eaddress=(EditText) findViewById(R.id.address_booking);
        Iconfirm=(ImageButton) findViewById(R.id.imageButton5);

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
setTitle("");
        mMonth=mMonth+1;
        Edat=(TextView) findViewById(R.id.textView8);
        Edat.setText(mDay+"-"+mMonth+"-"+mYear);
         dateselect=(ImageButton) findViewById(R.id.imageButton4);
        dateselect.setOnClickListener(this);
Iconfirm.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent=new Intent(ConfirmBooking.this, LastPage.class);
        startActivity(intent);
    }
});
    }
        @Override
    public void onClick(View v) {

             DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        Edat.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

}
