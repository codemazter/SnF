package es.esy.gopiappdeveloper.snf.network;

import android.content.Context;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class PostDataHelper
{
    ArrayList<String>	keys	= new ArrayList<String>();
    ArrayList<String>	values	= new ArrayList<String>();
    Context				_context;

    public PostDataHelper(ArrayList<String> _keysad, ArrayList<String> _valuesad, Context ctx)
    {
        _context = ctx;
        keys.add("Platform");
        values.add("Android");
        keys.addAll(_keysad);
        values.addAll(_valuesad);
    }

    public String postData(String url)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(url);
        sb.append("?");
        for (int i = 0; i < keys.size(); i++)
        {
            if (i != 0)
                sb.append("&");
            sb.append(keys.get(i));
            sb.append("=");
            sb.append(values.get(i));
        }
        HttpPost httppost = new HttpPost(url);
        try
        {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(keys.size());
            for (int i = 0; i < keys.size(); i++)
            {
                nameValuePairs.add(new BasicNameValuePair(keys.get(i), values.get(i)));
            }
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            HttpParams httpParameters = new BasicHttpParams();
            // Set the timeout in milliseconds until a connection is
            // established.
            int timeoutConnection = 30000;

            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            // Set the default socket timeout (SO_TIMEOUT)
            // in milliseconds which is the timeout for waiting for data.
            int timeoutSocket = 30000;

            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpConnectionParams.setTcpNoDelay(httpParameters, true);

            DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
            BasicHttpResponse httpResponse = (BasicHttpResponse) httpClient.execute(httppost);
            StatusLine statusLine = httpResponse.getStatusLine();
            if (statusLine.getStatusCode() >= 300)
            {
                return null;
            }
            HttpEntity entity = httpResponse.getEntity();
            if (entity == null)
            {
            }
            return entity == null ? null : EntityUtils.toString(entity);
        }
        catch (ClientProtocolException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
