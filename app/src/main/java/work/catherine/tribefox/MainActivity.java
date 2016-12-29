package work.catherine.tribefox;

import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static int kJobId = 0;
    public static final String TribePrefs = "TribePrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // retrieve data from shared preferences
        SharedPreferences prefs = getSharedPreferences(TribePrefs, MODE_PRIVATE);
        int howMany = prefs.getAll().size();
        if (howMany > 0) {
            LinearLayout list = (LinearLayout) findViewById(R.id.tribe_list);
            assert list != null;
            list.removeAllViews();
            for (int index = 0; index < howMany; index++){
                View row = getLayoutInflater().inflate(R.layout.content_main, list, false);
                row.setId(index);
                list.addView(row);
                Spinner spinner = (Spinner) row.findViewById(R.id.spinner);
                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MainActivity.this,
                        R.array.frequency_array, android.R.layout.simple_spinner_item);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                spinner.setAdapter(adapter);
                spinner.setSelection(prefs.getInt(String.valueOf(index),0));
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout list = (LinearLayout) findViewById(R.id.tribe_list);
                View row = getLayoutInflater().inflate(R.layout.content_main, list, false);

                // get child view count
                assert list != null;
                final int TribeNum = (list).getChildCount();

                row.setId(TribeNum);
                list.addView(row);

                Spinner spinner = (Spinner) row.findViewById(R.id.spinner);
                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MainActivity.this,
                        R.array.frequency_array, android.R.layout.simple_spinner_item);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                spinner.setAdapter(adapter);

                // schedule job when spinner item selected
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        String[] frequency_array = getResources().getStringArray(R.array.frequency_array_values);
                        double frequencyDouble = 23 + Double.parseDouble(frequency_array[position]);
                        long frequencyLong = (long) frequencyDouble;

                        // schedule job
                        if (0 < position) {
                            ComponentName serviceName = new ComponentName(MainActivity.this, ContactScheduler.class);

                            JobInfo jobInfo = new JobInfo.Builder(kJobId++, serviceName)
                                    .setRequiresDeviceIdle(false)
                                    .setRequiresCharging(false)
                                            // 24 hours = 86400000
                                    .setPeriodic(frequencyLong)
                                    .build();

                            JobScheduler scheduler = (JobScheduler) MainActivity.this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                            scheduler.schedule(jobInfo);
                        }

                        // write to shared preferences
                        // spinnerIndex is 0 indexed
                        SharedPreferences.Editor editor = getSharedPreferences(TribePrefs, MODE_PRIVATE).edit();
                        editor.putInt(String.valueOf(TribeNum), position);
                        editor.apply();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // your code here
                    }

                });

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


//    @Override
//    public class SpinnerActivity extends Activity implements OnItemSelectedListener {
//
//        Spinner spinner = (Spinner) findViewById(R.id.spinner);
//        spinner.setOnItemSelectedListener(this);
//
//        public void onItemSelected(AdapterView<?> parent, View view,
//                                   int pos, long id) {
//            // An item was selected. You can retrieve the selected item using
//            // parent.getItemAtPosition(pos)
//        }
//
//        public void onNothingSelected(AdapterView<?> parent) {
//            // Another interface callback
//        }
//    }
}
