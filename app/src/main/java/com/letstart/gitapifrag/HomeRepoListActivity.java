package com.letstart.gitapifrag;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.letstart.gitapifrag.adapter.ReposAdapter;
import com.letstart.gitapifrag.async.JsonParser;
import com.letstart.gitapifrag.async.NetworkConn;
import com.letstart.gitapifrag.model.RespGenericBean;
import com.letstart.gitapifrag.model.repos.Repos;
import com.letstart.gitapifrag.model.repos.ReposList;
import com.letstart.gitapifrag.utils.Const;
import com.letstart.gitapifrag.utils.RestUrlBuilder;
import com.letstart.gitapifrag.utils.Utils;

import java.io.IOException;

public class HomeRepoListActivity
        extends AppCompatActivity
    implements NetworkConn.IresponseFromAT
    , JsonParser.IresponseFromJsonParserAT
{

    private static final String TAG = HomeRepoListActivity.class.getSimpleName();
    ReposAdapter reposAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_repo_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //ListView lv = getListView();
        ListView lv = (ListView)findViewById(R.id.listrepo);

                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });




        reposAdapter = new ReposAdapter(this, 0);

        lv.setAdapter(reposAdapter);
        //setListAdapter(reposAdapter);


        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i(TAG, "");
                    Repos temp_rep = reposAdapter.getItem(i);
                    mShowDialog(temp_rep.getOwner().getHtmlUrl(), temp_rep.getHtmlUrl());
                    return true;
                }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i(TAG, "");
                    showHistory(reposAdapter.getItem(i).getName());


            }
        });

        if (Utils.isOnline(this)) {
            loadDataRetry();
        } else {

            try {
                String reposDataCached = (String) Utils.readReposData(this, "repos.data");
                responsse(reposDataCached);

                    Toast.makeText(this, "you are offline, cached data", Toast.LENGTH_LONG).show();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }


    }
    public void loadDataRetry() {


        String[][] query_string = {

        };
        try {
            String completeUri = new RestUrlBuilder().getUrl(getResources().getString(R.string.git_server),
                    new String[]{"/users/orlando-antonino/repos"},
                    query_string);

            new NetworkConn(this,

                    completeUri
            );
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void responsse(String jsonStr) {
        if (jsonStr != null && !jsonStr.equals("")) {

            new JsonParser(this, jsonStr, "repos");
                try {
                    Utils.cacheReposData(this, "repos.data", jsonStr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        } else {
                ProgressBar pb = (ProgressBar) findViewById(R.id.progress_repo);
                if (pb != null)
                    pb.setVisibility(View.GONE);

                Toast.makeText(this, "Error on network request", Toast.LENGTH_LONG).show();
            }
    }
    public void mShowDialog(final String link1, final String link2) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final Intent i = new Intent(Intent.ACTION_VIEW);

        builder.setMessage(getString(R.string.open_repos_string))
                .setPositiveButton(getString(R.string.open_repos_url), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        i.setData(Uri.parse(link1));
                        startBrowser(i);
                    }
                })
                .setNegativeButton(getString(R.string.open_owner_url), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        i.setData(Uri.parse(link2));
                        startBrowser(i);
                    }
                });

        builder.create().show();

    }


    public void startBrowser(Intent in) {
        startActivity(in);
    }


    public void showHistory(String repoName) {

            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, DetailRepoListActivity.class);
            detailIntent.putExtra(Const.ARG_ITEM_ID, repoName);
            startActivity(detailIntent);
    }


        @Override
        public void responsseJson(RespGenericBean resp) {
            if (resp != null) {
                reposAdapter.addAll(((ReposList) resp).getReposArray());

            } else {
                Toast.makeText(getApplicationContext(), "Error on network request", Toast.LENGTH_LONG).show();
            }
            ProgressBar pb = (ProgressBar) findViewById(R.id.progress_repo);
            if (pb != null)
                pb.setVisibility(View.GONE);


        }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

                loadDataRetry();


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
