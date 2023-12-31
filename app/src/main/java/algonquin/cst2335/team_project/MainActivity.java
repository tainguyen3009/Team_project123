package algonquin.cst2335.team_project;

import static android.database.sqlite.SQLiteDatabase.openDatabase;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.SimpleCursorAdapter;
import android.widget.SearchView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import algonquin.cst2335.nguy1041.R;

public class MainActivity extends AppCompatActivity {

    SearchView search;

    static DatabaseHelper myDbHelper;
    static boolean databaseOpened=false;

    SimpleCursorAdapter suggestionAdapter;

    ArrayList<History> historyList;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter historyAdapter;

    RelativeLayout emptyHistory;
    Cursor cursorHistory;



    private boolean doubleBackToExitPressedOnce = false;
    private MenuItem item;

    protected static void openDatabase()
    {
        try {
            myDbHelper.openDataBase();
            databaseOpened=true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void showAlertDialog()
    {
        search.setQuery("",false);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
        builder.setTitle("Word Not Found");
        builder.setMessage("Please search again");

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        search.clearFocus();
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();

    }
    protected void onResume() {
        MainActivity.super.onResume();
        fetch_history();
    }

    @SuppressLint("Range")
    private void fetch_history() {
        historyList = new ArrayList<>();
        historyAdapter = new RecyclerViewAdapterHistory((SearchView.OnSuggestionListener) MainActivity.this, historyList);
        recyclerView.setAdapter(historyAdapter);

        History h;

        if (databaseOpened) {
            Cursor cursorHistory = myDbHelper.getHistory();
            if (cursorHistory.moveToFirst()) {
                do {
                    h = new History(
                            cursorHistory.getString(cursorHistory.getColumnIndex("word")),
                            cursorHistory.getString(cursorHistory.getColumnIndex("en_definition"))
                    );
                    historyList.add(h);
                } while (cursorHistory.moveToNext());
            }

            historyAdapter.notifyDataSetChanged();
        }

        if (historyAdapter.getItemCount() == 0) {
            emptyHistory.setVisibility(View.VISIBLE);
        } else {
            emptyHistory.setVisibility(View.GONE);
        }
    }


    public boolean onQueryTextChange(final String s) {
        search.setIconifiedByDefault(false); //Give Suggestion list margins

        Pattern p = Pattern.compile("[A-Za-z \\-.]{1,25}");
        Matcher m = p.matcher(s);

        if(m.matches()) {
            Cursor cursorSuggestion=myDbHelper.getSuggestions(s);
            suggestionAdapter.changeCursor(cursorSuggestion);
        }

        return false;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        search =  (SearchView) findViewById(R.id.search_view);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                search.setIconified(false);

            }
        });


        myDbHelper = new DatabaseHelper(this);

        if(myDbHelper.checkDataBase())
        {
            openDatabase();

        }
        else
        {
            LoadDatabaseAsync task = new LoadDatabaseAsync(MainActivity.this);
            task.execute();
        }



        // setup SimpleCursorAdapter

        final String[] from = new String[] {"en_word"};
        final int[] to = new int[] {R.id.suggestion_text};

        suggestionAdapter = new SimpleCursorAdapter(MainActivity.this,
                R.layout.suggestion_row, null, from, to, 0){
            @Override
            public void changeCursor(Cursor cursor) {
                super.swapCursor(cursor);
            }

        };

        search.setSuggestionsAdapter(suggestionAdapter);


        search.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {

                // Add clicked text to search box
                CursorAdapter ca = search.getSuggestionsAdapter();
                Cursor cursor = ca.getCursor();
                cursor.moveToPosition(position);
                int columnIndex = cursor.getColumnIndex("en_word");
                if (columnIndex != -1) {
                    String clicked_word = cursor.getString(columnIndex);
                    search.setQuery(clicked_word, true);
                } else {

                    String clicked_word = cursor.getString(columnIndex);


                    search.clearFocus();
                    search.setFocusable(false);

                    Intent intent = new Intent(MainActivity.this, WordMeaningActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("en_word", clicked_word);
                    intent.putExtras(bundle);
                    startActivity(intent);

                    return true;
                }


                search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {

                        String text = search.getQuery().toString();

                        Pattern p = Pattern.compile("[A-Za-z \\-.]{1,25}");
                        Matcher m = p.matcher(text);

                        if (m.matches()) {
                            Cursor c = myDbHelper.getMeaning(text);

                            if (c.getCount() == 0) {
                                showAlertDialog();
                            } else {
                                //search.setQuery("",false);
                                search.clearFocus();
                                search.setFocusable(false);

                                Intent intent = new Intent(MainActivity.this, WordMeaningActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("en_word", text);
                                intent.putExtras(bundle);
                                startActivity(intent);

                            }

                        } else {
                            showAlertDialog();


                        }
                        return false;

                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }

                });


                emptyHistory = (RelativeLayout) findViewById(R.id.empty_history);

                //recycler View
                recyclerView = (RecyclerView) findViewById(R.id.recycler_view_history);
                layoutManager = new LinearLayoutManager(MainActivity.this);

                recyclerView.setLayoutManager(layoutManager);

                fetch_history();

                return false;
            }


            @SuppressLint("Range")
            private void fetch_history() {
                historyList = new ArrayList<>();
                historyAdapter = new RecyclerViewAdapterHistory(this, historyList);
                recyclerView.setAdapter(historyAdapter);

                History h;

                if (databaseOpened) {
                    cursorHistory = myDbHelper.getHistory();
                    if (cursorHistory.moveToFirst()) {
                        do {
                            h = new History(cursorHistory.getString(cursorHistory.getColumnIndex("word")), cursorHistory.getString(cursorHistory.getColumnIndex("en_definition")));
                            historyList.add(h);
                        }
                        while (cursorHistory.moveToNext());
                    }

                    historyAdapter.notifyDataSetChanged();
                }


                if (historyAdapter.getItemCount() == 0) {
                    emptyHistory.setVisibility(View.VISIBLE);
                } else {
                    emptyHistory.setVisibility(View.GONE);
                }
            }


            public boolean onCreateOptionsMenu(MenuItem menu) {
                // Inflate the menu_main; this adds items to the action bar if it is present.
                getMenuInflater().inflate(R.menu.class.getModifiers(), (Menu) menu);
                return true;
            }


            public boolean onOptionsItemSelected(MenuItem item) {
                // Handle action bar item clicks here. The action bar will
                // automatically handle clicks on the Home/Up button
                int id = item.getItemId();
                if (id == R.id.action_settings) {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    return true;
                }

                if (id == R.id.action_exit) {
                    finish();
                    return true;
                }
                return MainActivity.super.onOptionsItemSelected(item);
            }





            public void onBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    MainActivity.super.onResume();
                } else {
                    MainActivity.this.doubleBackToExitPressedOnce = true;
                    Toast.makeText(MainActivity.this, "Press Back again to exit", Toast.LENGTH_LONG).show();
                }
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }
        });
    };
}



