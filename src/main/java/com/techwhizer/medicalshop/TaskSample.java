package com.techwhizer.medicalshop;

import com.victorlaerte.asynctask.AsyncTask;

import java.util.HashMap;
import java.util.Map;

public class TaskSample {

    private void executeTask(){
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.setDaemon(false);
        myAsyncTask.execute();

        Map<String, Object> status = new HashMap<>();
//        map.put("message", message);
//        map.put("is_success", false);

    }
    private class MyAsyncTask extends AsyncTask<String, Integer, Boolean> {
        private String msg;

        @Override
        public void onPreExecute() {
            msg = "";

        }

        @Override
        public Boolean doInBackground(String... params) {
            /* Background Thread is running */

           /* Map<String, Object> status = login(input, password);
            boolean isSuccess = (boolean) status.get("is_success");
            msg = (String) status.get("message");
            return isSuccess;*/

            return false;

        }

        @Override
        public void onPostExecute(Boolean success) {
            if (success) {

            } else {
//                loginButton.setVisible(true);
//                hideElement(progressBar);
//                customDialog.showAlertBox("Login failed", msg);
            }
        }

        @Override
        public void progressCallback(Integer... params) {

        }
    }
}
