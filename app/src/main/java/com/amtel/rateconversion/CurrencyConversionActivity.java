package com.amtel.rateconversion;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amtel.rateconversion.values.ConversionValue;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

/**
 * Created by pradeepbk on 3/31/14.
 */
public class CurrencyConversionActivity extends FragmentActivity implements AdapterView.OnItemSelectedListener{


    private EditText qnt;
    private TextView resultText;

    private ResultSetAdapter mAdapter;
    private String from_unit;
    private String to_unit;

    private final String URL = "http://rate-exchange.appspot.com/currency?";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_conversion);


        Spinner spinner = (Spinner) findViewById(R.id.from_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.conversion_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        Spinner to_spinner = (Spinner) findViewById(R.id.to_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        to_spinner.setAdapter(adapter);
        to_spinner.setOnItemSelectedListener(this);


        mAdapter = new ResultSetAdapter();

        if (savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().add(R.id.result_list, new ResultListFragment(mAdapter)).commit();

        }

        qnt = (EditText) findViewById(R.id.qty);
        resultText = (TextView) findViewById(R.id.result);


    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        int parent_id = parent.getId();
        switch (parent_id)
        {
            case R.id.from_spinner:
               from_unit = parent.getItemAtPosition(position).toString();  break;
            case R.id.to_spinner:
               to_unit = parent.getItemAtPosition(position).toString(); break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        from_unit = "INR";
        to_unit = "INR";
    }




    public void ConvertRate(View v) {

        if (validateInput()) {
            new Convert().execute();
        }


    }

    private boolean validateInput() {
         if (qnt.getText().toString() != null && "".equals(qnt.getText().toString())) {
            Toast.makeText(this, "Empty Quantity filed.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    private class Convert extends AsyncTask<Void, Void, String> {

        ProgressDialog dialog = null;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(CurrencyConversionActivity.this);
            dialog.setMessage("Converting....");
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            String jsonString = null;
            StringBuilder urlString = new StringBuilder();
            urlString.append(URL);
            urlString.append("from=");
            urlString.append(from_unit);
            urlString.append("&to=");
            urlString.append(to_unit);
            urlString.append("&q=");
            urlString.append(qnt.getText().toString());

            try {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(URI.create(urlString.toString()));

                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();

                InputStream is = httpEntity.getContent();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

                StringBuilder stringBuilder = new StringBuilder();

                String result = "";
                while ((result = bufferedReader.readLine()) != null) {

                    stringBuilder.append(result);

                }
                is.close();
                jsonString = stringBuilder.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }


            return jsonString;
        }


        @Override
        protected void onPostExecute(String jsonString) {
            dialog.dismiss();

            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                String result = jsonObject.getString("v");
                resultText.setText(result);
                ConversionValue.addItem(new ConversionValue.ConversionData(getHtmlString()));
                mAdapter.notifyDataSetChanged();


            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(CurrencyConversionActivity.this,
                        "Unable to convert for the rate for given input", Toast.LENGTH_SHORT).show();



            }
        }

    }


    private String getHtmlString() {
        StringBuilder builder = new StringBuilder();
        builder.append("<b>From:</b> ");
        builder.append(from_unit);
        builder.append("<br>");
        builder.append("<b>To: </b>");
        builder.append(to_unit);
        builder.append("<br>");
        builder.append("<b>Quantity: </b>");
        builder.append(qnt.getText().toString());
        builder.append("<br>");
        builder.append("<b>Result: </b>");
        builder.append(resultText.getText().toString());
        return builder.toString();
    }


}
