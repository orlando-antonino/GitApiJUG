package com.letstart.gitapifrag.async;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.letstart.gitapifrag.model.RespGenericBean;
import com.letstart.gitapifrag.model.history.CommitHistory;
import com.letstart.gitapifrag.model.history.HistoryList;
import com.letstart.gitapifrag.model.repos.Repos;
import com.letstart.gitapifrag.model.repos.ReposList;

import java.lang.ref.WeakReference;

/**
 * Created by orlando on 19/04/15.
 */
public class JsonParser {

    @SuppressWarnings("unused")
    private static final String TAG = "NetworkConn";

    String url;

    WeakReference<IresponseFromJsonParserAT> responseIntW;
    String type;




    public interface IresponseFromJsonParserAT{
        void responsseJson(RespGenericBean respObj);

    }


    public JsonParser(IresponseFromJsonParserAT response, String jsonStringInput, String type){

        this.url = jsonStringInput;
        this.type = type;

        responseIntW=  new WeakReference<>(response);
//        Log.i(TAG,url);
        new JSONParser().execute(url);


    }

    public class JSONParser extends AsyncTask<String,Void,RespGenericBean> {

        @Override
        protected RespGenericBean doInBackground(String... jsonInput) {


               String data = jsonInput[0];

                if (type.equals("repos")) {
                    Gson resp = new Gson();


                    Repos[] repos = resp.fromJson(data,Repos[].class);

                    ReposList reposList= new ReposList(repos);
                    return reposList   ;
                }
                else if(type.equals("history")){
                    Gson resp = new Gson();

                    CommitHistory[] commitArray = resp.fromJson(data,CommitHistory[].class);
                   HistoryList historyList= new HistoryList(commitArray);

                    return historyList   ;
                }
            else
                return null;



        }

        @Override
        protected void onPostExecute(RespGenericBean respGenericBean) {
            super.onPostExecute(respGenericBean);
            if(responseIntW.get()!=null) {
                responseIntW.get().responsseJson(respGenericBean);
            }
        }
    }



}
