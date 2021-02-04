package me.at.nitsxr;
import android.os.AsyncTask;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserReaction extends AsyncTask<String, String, String>
{
    private NewsGetterSetter getterSetter;
    private String userId;
    private int  updated_status;

    public UserReaction(NewsGetterSetter getterSetter, String userId, int updated_status)
    {
        this.getterSetter = getterSetter;
        this.userId = userId;
        this.updated_status = updated_status;
    }

    private  final String url  = "https://app.thenextsem.com/app/update_reaction.php";
    @Override
    protected String doInBackground(String...params)
    {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

        try
        {
            List<NameValuePair> nameValuePairs = new ArrayList<>(2);

            nameValuePairs.add(new BasicNameValuePair("newsIdKey",getterSetter.getNewsId()));
            nameValuePairs.add(new BasicNameValuePair("userIdKey",userId));
            nameValuePairs.add(new BasicNameValuePair("statusKey",updated_status+""));

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httpClient.execute(httpPost);
        }
        catch (ClientProtocolException ex){ex.printStackTrace();}
        catch (IOException ex){ex.printStackTrace();}

        return null;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String response)
    {
        super.onPostExecute(response);
        // update GUI with response
    }
}