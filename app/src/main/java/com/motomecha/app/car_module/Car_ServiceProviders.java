package com.motomecha.app.car_module;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.gson.Gson;
import com.motomecha.app.Global_classes.GlobalUrlInit;
import com.motomecha.app.R;
import com.motomecha.app.Global_classes.carmechantlist;
import com.motomecha.app.dbhandler.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Car_ServiceProviders extends AppCompatActivity {
    PlaceAutocompleteFragment autocompleteFragment;
    ListView car_module_list;
    private  ProgressDialog dialog;
String slat,slng,servetype,vehicletype,myurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car__service_providers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading. Please wait...");
        TextView tv = (TextView) findViewById(R.id.text_view_toolb);
        setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        Typeface custom_font = Typeface.createFromAsset(getApplication().getAssets(), "fonts/rama.ttf");
        assert tv != null;
        tv.setTypeface(custom_font);
        car_module_list=(ListView) findViewById(R.id.car_module);
        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
        final HashMap<String, String> user = db.getUserDetails();
        slat = user.get("klati");
        slng = user.get("klongi");
        servetype = getIntent().getStringExtra("servicetype");
        vehicletype = getIntent().getStringExtra("vehicletype");
        String text = "<font color=#ff1545>SERVICE</font> <font color=#ffffff>PROVIDERS</font>";
        tv.setText(Html.fromHtml(text));
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        AutocompleteFilter filter = new AutocompleteFilter.Builder().setCountry("IN").build();
        autocompleteFragment.setFilter(filter);
        autocompleteFragment.setHint("AREA OR PINCODE");
if (vehicletype.contains(" ")) {
    String[] separated = vehicletype.split(" ");
    String vehicletype1 = separated[0]; // this will contain "Fruit"
    String vehicletype2 = separated[1];
    vehicletype = vehicletype1 + "%20" + vehicletype2;
}

myurl=GlobalUrlInit.CAR_MERCHANLIST+"?slat="+slat+"&slng="+slng+"&serve_type="+servetype+"&vehicletype="+vehicletype+"&vehiclemode=car";
        new JSONTask().execute(myurl);
    }


    public class MovieAdapter extends ArrayAdapter {

        private List<carmechantlist> movieModelList;
        private int resource;
        Context context;
        private LayoutInflater inflater;
        MovieAdapter(Context context, int resource, List<carmechantlist> objects) {
            super(context, resource, objects);
            movieModelList = objects;
            this.context =context;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        }
        @Override
        public int getViewTypeCount() {

            return 1;
        }

        @Override
        public int getItemViewType(int position) {

            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder holder  ;

            if(convertView == null){
                convertView = inflater.inflate(resource,null);
                holder = new ViewHolder();
                holder.display_na=(TextView) convertView.findViewById(R.id.textView10);
                holder.place=(TextView) convertView.findViewById(R.id.place_db);
                holder.cost=(TextView) convertView.findViewById(R.id.textView11);
                convertView.setTag(holder);

            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            carmechantlist categorieslist= movieModelList.get(position);
            holder.display_na.setText(categorieslist.getDisplay_name());
            holder.place.setText(categorieslist.getArea_location());
            holder.cost.setText(categorieslist.getPrice());
            return convertView;
        }

        class ViewHolder{

            private TextView display_na,place,cost;
        }



    }
    public class JSONTask extends AsyncTask<String,String, List<carmechantlist>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected List<carmechantlist> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder buffer = new StringBuilder();
                String line ="";
                while ((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                String finalJson = buffer.toString();

                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray parentArray = parentObject.getJSONArray("result");
                List<carmechantlist> movieModelList = new ArrayList<>();
                Gson gson = new Gson();
                for(int i=0; i<parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    carmechantlist categorieslist = gson.fromJson(finalObject.toString(), carmechantlist.class);
                    movieModelList.add(categorieslist);

                }

                return movieModelList;

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            } finally {
                if(connection != null) {
                    connection.disconnect();
                }
                try {
                    if(reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return  null;

        }

        @Override
        protected void onPostExecute(final List<carmechantlist> movieModelList) {
            super.onPostExecute(movieModelList);
            dialog.dismiss();

            if(movieModelList != null){
                MovieAdapter adapter = new MovieAdapter(getApplicationContext(), R.layout.row_car_merchant, movieModelList);
                car_module_list.setAdapter(adapter);
//                car_module_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        /*carmechantlist categorieslist = movieModelList.get(position);
//                        Intent intent = new Intent(ModelSelect.this, Plate_Regiestration.class);
//                        intent.putExtra("brand",categorieslist.getModel());
//                        intent.putExtra("brand_type",btype);
//                        startActivity(intent);*/
//
//                    }
//                });
            }
            else {
                Toast.makeText(getApplicationContext(),"Please check your internet connection!",Toast.LENGTH_SHORT).show();

            }


        }

    }

}