package com.letstart.gitapifrag;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.letstart.gitapifrag.adapter.HistoryAdapter;
import com.letstart.gitapifrag.async.JsonParser;
import com.letstart.gitapifrag.async.NetworkConn;
import com.letstart.gitapifrag.model.RespGenericBean;
import com.letstart.gitapifrag.model.history.HistoryList;
import com.letstart.gitapifrag.utils.Const;
import com.letstart.gitapifrag.utils.RestUrlBuilder;

public class DetailRepoListActivity extends AppCompatActivity
        implements NetworkConn.IresponseFromAT
        , JsonParser.IresponseFromJsonParserAT {

    String repoName = "";
    HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ListView lv = (ListView) findViewById(R.id.listrepodetail);


        // Load the dummy content specified by the fragment
        // arguments. In a real-world scenario, use a Loader
        // to load content from a content provider.
        repoName = getIntent().getStringExtra(Const.ARG_ITEM_ID);

//            Log.i("", "");

        String[][] query_string = {

        };
        try {
            String completeUri = new RestUrlBuilder().getUrl(getResources().getString(R.string.git_server),
                    new String[]{"/repos/orlando-antonino", "/" + repoName, "/commits"},
                    query_string);
            new NetworkConn(this,
                    completeUri

            );
        } catch (Exception e) {
            e.printStackTrace();
        }


        historyAdapter = new HistoryAdapter(this, 0);

        lv.setAdapter(historyAdapter);


    }

    @Override
    public void responsse(String jsonStr) {
        if (jsonStr != null && !jsonStr.equals("")) {
            new JsonParser(this, jsonStr, "history");

        } else {
            ProgressBar pb = (ProgressBar) this.findViewById(R.id.progress_detail);
            if (pb != null)
                pb.setVisibility(View.GONE);

            Toast.makeText(this, "Error on network request", Toast.LENGTH_LONG).show();


        }
    }

    @Override
    public void responsseJson(RespGenericBean respObj) {
        if (respObj != null) {
            historyAdapter.addAll(((HistoryList) respObj).getReposArray());
            historyAdapter.notifyDataSetChanged();

        } else {
            Toast.makeText(this, "Error on network request", Toast.LENGTH_LONG).show();

        }
        ProgressBar pb = (ProgressBar) this.findViewById(R.id.progress_detail);
        if (pb != null)
            pb.setVisibility(View.GONE);


    }

}
