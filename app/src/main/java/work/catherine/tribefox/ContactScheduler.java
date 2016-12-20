package work.catherine.tribefox;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class ContactScheduler extends JobService {

    private UpdateAppsAsyncTask updateTask = new UpdateAppsAsyncTask();

    private static final String TAG = "SyncService";

    @Override
    public boolean onStartJob(JobParameters params) {
        // Note: this is preformed on the main thread.
        Log.i(TAG, "on start job: " + params.getJobId());

        Notify.contactNotification(this);

        updateTask.execute(params);

        return true;
    }

    // Stopping jobs if our job requires change.

    @Override
    public boolean onStopJob(JobParameters params) {
        // Note: return true to reschedule this job.

        boolean shouldReschedule = updateTask.stopJob(params);

        return shouldReschedule;

    }

    private class UpdateAppsAsyncTask extends AsyncTask<JobParameters, Void, JobParameters[]> {


        @Override
        protected JobParameters[] doInBackground(JobParameters... params) {

            // Do updating and stopping logical here.
            return params;
        }

        @Override
        protected void onPostExecute(JobParameters[] result) {
            for (JobParameters params : result) {
                if (!hasJobBeenStopped(params)) {
                    jobFinished(params, false);
                }
            }
        }

        private boolean hasJobBeenStopped(JobParameters params) {
            // Logic for checking stop.
            return false;
        }

        public boolean stopJob(JobParameters params) {
            // Logic for stopping a job. return true if job should be rescheduled.
            return false;
        }

    }
}
