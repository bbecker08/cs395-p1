package edu.haverford.cs.squirrelfacts;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.widget.ListView;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Downloads JSON, parses each squirrel, and adds it to the collection via a task.
 */
class GetNewSquirrelsTask extends AsyncTask<String, Void, SquirrelList> {


    private MainActivity mRef;
    public GetNewSquirrelsTask(MainActivity ac)
    {
        super();
        mRef = ac;
    }

    /**
     * Takes string and returns JSON interpretable string from webpage.
     * @param mUrl
     * @return
     */
    private String getData(String mUrl) //Sourced ideas from: http://www.androidhive.info/2012/01/android-json-parsing-tutorial/
    {
        String out = null;

        try
        {
            URL url = new URL(mUrl);
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("GET");

            InputStream in = new BufferedInputStream(connect.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            try{

              String next = null;

              while((next = reader.readLine())!=null){
                  if(out==null)out=next+"\n";
                  else out+=next+"\n";
              }

            }catch(Exception e){e.printStackTrace();}
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return out;
    }


    /**
     * TODO: Implement this method to download a list of squirrels and parse it
     * @param strings
     * @return
     */
    @Override
    protected SquirrelList doInBackground(String... strings) {

        SquirrelList out = new SquirrelList();

        String toParse = getData(strings[0]);
        try {

            //JSONObject ob = new JSONObject(toParse);
            JSONArray stuff = new JSONArray(toParse);//ob.getJSONArray("");
            for(int i=0; i<stuff.length();i++)
            {
                JSONObject sq = stuff.getJSONObject(i);

                Squirrel toAdd = new Squirrel(sq.getString("name"),sq.getString("location"),sq.getString("picture"));
                out.addToFront(toAdd);
            }

        }catch(Exception e){e.printStackTrace();}
        return out;
    }

    @Override
    protected void onPostExecute(SquirrelList squirrels) {
        super.onPostExecute(squirrels);

        mRef.aSyncDone(squirrels);
    }
}

public class MainActivity extends AppCompatActivity {


    private GetNewSquirrelsTask mTask;
    private SquirrelList mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = (ListView) findViewById(R.id.squirrel_list);
        Squirrel s = new Squirrel("Black Squirrel",
                "Haverford, PA",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0c/Black_Squirrel.jpg/220px-Black_Squirrel.jpg");
        mList = (new SquirrelList());
        for (int i = 0; i < 100; i++) {
            mList.addToFront(s);
        }

        try {
            mTask = new GetNewSquirrelsTask(this);//.execute("https://raw.githubusercontent.com/kmicinski/squirreldata/master/squirrels.json");
            mTask.execute("https://raw.githubusercontent.com/kmicinski/squirreldata/master/squirrels.json");

        }catch(Exception e){e.printStackTrace();}


        ArrayList<Squirrel> al = mList.toArrayList();
        //SquirrelArrayAdapter adapter = new SquirrelArrayAdapter(this, al);
        /**
         * TODO: Uncomment this and make sure you can use your adapter
         */
        SquirrelListAdapter adapter = new SquirrelListAdapter(this, mList);
        listView.setAdapter(adapter);
    }

    public void aSyncDone(SquirrelList toAdd)
    {
        mList.addAll(toAdd);
    }
}
