package es.esy.gopiappdeveloper.snf;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import es.esy.gopiappdeveloper.snf.network.PostDataHelper;
import es.esy.gopiappdeveloper.snf.util.ProgressDlg;


public class CompanyInfo extends Activity {
    TextView cmpId, cmpName, cmpStart, cmpOwner, cmpDept, cmpDes;
    String c_id = "";
    String url = "http://www.gopiappdeveloper.esy.es/myjson.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.company_view);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null) {
            c_id= extras.getString("companyID");
        }
        initVars();
    }

    public void initVars(){
        cmpId = (TextView)findViewById(R.id.cmpId);
        cmpName = (TextView)findViewById(R.id.cmpName);
        cmpStart = (TextView)findViewById(R.id.cmpStart);
        cmpOwner = (TextView)findViewById(R.id.cmpOwner);
        cmpDept = (TextView)findViewById(R.id.cmpDept);
        cmpDes = (TextView)findViewById(R.id.cmpDes);
        new InfoTask().execute();
    }

    class InfoTask extends AsyncTask<String, String, String> {

        JSONObject json = null;
        String results = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressDlg.clearDialog();
            ProgressDlg.showProgressDialog(CompanyInfo.this, "Loading", "Please wait...");
        }

        @Override
        protected String doInBackground(String... search_url) {
            ArrayList<String> keys = new ArrayList<String>();
            keys.add("id");
            ArrayList<String> values = new ArrayList<String>();
            values.add(c_id);
            try {
                PostDataHelper pdata = new PostDataHelper(keys, values, getApplicationContext());
                if (isCancelled()) {
                    return null;
                }
                results = pdata.postData(url);
            } catch (Exception e) {
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
            if (!results.isEmpty() && results != null) {
                try {
                    JSONArray root = new JSONArray(results);
                    JSONObject jNode = null;
                    jNode = root.getJSONObject(0);
                    final JSONObject finalJNode = jNode;
                    CompanyInfo.this.runOnUiThread(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        public void run() {
                            try {
                                cmpId.setText("ID:- " + finalJNode.getString("companyID"));
                                cmpName.setText("Name:- "+ finalJNode.getString("companyName"));
                                cmpStart.setText("Start Date:- "+ finalJNode.getString("companyStartDate"));
                                cmpOwner.setText("Owner:- "+ finalJNode.getString("companyOwner"));
                                cmpDept.setText("Departments:- "+ finalJNode.getString("companyDepartments"));
                                cmpDes.setText("Description:- "+ finalJNode.getString("companyDescription"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
