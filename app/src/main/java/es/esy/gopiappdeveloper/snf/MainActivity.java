package es.esy.gopiappdeveloper.snf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import es.esy.gopiappdeveloper.snf.adapter.LVAdapter;
import es.esy.gopiappdeveloper.snf.network.PostDataHelper;
import es.esy.gopiappdeveloper.snf.util.ProgressDlg;

public class MainActivity extends Activity implements View.OnClickListener{

    EditText key;
    Button lookup;
    ListView listView;
    Spinner cmp_filter;
    String url = "http://www.gopiappdeveloper.esy.es/myjson.php";
    String departments[] = {"All","Accounting", "Advertising", "Asset Management", "Customer Relations",
     "Customer Service", "Finances", "Human Resources", "Legal Department",
     "Media Relations", "Payroll", "Public Relations", "Quality Assurance",
     "Research and Development", "Sales and Marketing", "Tech Support"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        initVars();
    }

    public void initVars(){
        key = (EditText)findViewById(R.id.key);
        lookup = (Button)findViewById(R.id.lookup);
        lookup.setOnClickListener(this);
        listView = (ListView)findViewById(R.id.listView);
        cmp_filter = (Spinner)findViewById(R.id.cmp_filter);
        ArrayList<String> dep_li = new ArrayList<String>();
        for (int i = 0; i < departments.length; ++i) {
            dep_li.add(departments[i]);
        }
        ArrayAdapter<String> dep_adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, dep_li);
        cmp_filter.setAdapter(dep_adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lookup:
                String query = key.getText().toString();
                if(query.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter company name", Toast.LENGTH_SHORT).show();
                }else{
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    new LookupTask().execute(url,query);
                }
                break;
        }
    }

    class LookupTask extends AsyncTask<String, String, String> {

        JSONObject json = null;
        String results = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressDlg.clearDialog();
            ProgressDlg.showProgressDialog(MainActivity.this, "Loading", "Please wait...");
        }

        @Override
        protected String doInBackground(String... search_url) {
            ArrayList<String> keys = new ArrayList<String>();
            keys.add("company");
            ArrayList<String> values = new ArrayList<String>();
            values.add(search_url[1]);

            try {
                PostDataHelper pdata = new PostDataHelper(keys, values,getApplicationContext());
                if (isCancelled()) {
                    return null;
                }
                results = pdata.postData(search_url[0]);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... text) {
            // Things to be done while execution of long running operation is in
        }

        @Override
        protected void onPostExecute(String result) {
            ProgressDlg.dismissProgressDialog();
            if(!results.isEmpty() && results!=null){
                try {
                    JSONArray root = new JSONArray(results);
                    final LVAdapter lv = new LVAdapter(MainActivity.this,root);
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            listView.setAdapter(lv);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    TextView tv = (TextView) view.findViewById(R.id.cmp_id);
                                    String companyID = tv.getText().toString();
                                    Intent ci = new Intent(MainActivity.this,CompanyInfo.class);
                                    ci.putExtra("companyID",companyID);
                                    startActivity(ci);
                                }
                            });

                            cmp_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                    String key = cmp_filter.getSelectedItem().toString().trim();
                                    lv.getFilter().filter(key);
                                }
                                @Override
                                public void onNothingSelected(AdapterView<?> arg0) {
                                }
                            });
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        listView.setEmptyView(findViewById(R.id.emptyElement));
                        Toast.makeText(getApplicationContext(), "Sorry..! No results were found", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
