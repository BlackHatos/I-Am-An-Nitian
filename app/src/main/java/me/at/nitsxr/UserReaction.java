package me.at.nitsxr;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.co.iamannitian.iamannitian.R;

public class UserReaction extends AsyncTask<String, String, String>
{
    private NewsGetterSetter getterSetter;
    private NewsAdapter.NewsViewHolder holder;
    private String userId;
    UserReaction(NewsGetterSetter getterSetter, NewsAdapter.NewsViewHolder holder, String userId)
    {
        this.getterSetter = getterSetter;
        this.holder = holder;
        this.userId = userId;
    }

    private  final String url  = "https://app.thenextsem.com/app/user_reaction.php";
    @Override
    protected String doInBackground(String...params) {

       try
        {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            HttpResponse response = null;

            try
            {
                List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("newsId",getterSetter.getNewsId()));
                nameValuePairs.add(new BasicNameValuePair("userId",userId));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                response = httpClient.execute(httpPost);
            }
            catch (ClientProtocolException ex){ex.printStackTrace();}
            catch (IOException ex){ex.printStackTrace();}

            HttpEntity httpEntity = response.getEntity();
            String jsonArray = EntityUtils.toString(httpEntity);
            return jsonArray;

        }catch(IOException ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        if(response!=null)
        {
            try
            {
                JSONArray jsonArray = new JSONArray(response);
                updateUserReaction(jsonArray);
            }catch(JSONException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    private void updateUserReaction(JSONArray jsonArray)
    {
        try
        {
            JSONObject object = jsonArray.getJSONObject(0);
            holder.reactionCount.setText(object.getString("count"));
            String status = object.getString("status");

            if(status.equals("1"))
            {
                holder.userReaction.setImageResource(R.drawable.ic_favorite_black_24dp);
            }
            else
            {
                holder.userReaction.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            }

        }catch(Exception ex){ex.printStackTrace();}
    }
}