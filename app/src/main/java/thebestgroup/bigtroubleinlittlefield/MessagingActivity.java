package thebestgroup.bigtroubleinlittlefield;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MessagingActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "MyPrefsFile";
    private static final String TAG = "MessagingActivity";

    PuzzleManager pm;
    ArrayList<String> smsMessagesList = new ArrayList<>();
    ListView messages;
    ArrayAdapter arrayAdapter;
    EditText input;
    int puzzleIndex;
    Puzzle pz;
    Button mSend;
    boolean closeToQuestion;
    String incomimg;

    private Timer myTimer;
    private Runnable mRunnable;
    private Handler mPauseHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        mPauseHandler = new Handler();
        pm = new PuzzleManager(getApplicationContext());
        messages = (ListView) findViewById(R.id.messages);
        input = (EditText) findViewById(R.id.input);
        restoreList();
        messages.setAdapter(arrayAdapter);
        closeToQuestion = false;
        scrollToBottom();
        incomimg = "";

        mSend = (Button) findViewById(R.id.send);
        mSend.setOnClickListener(mClickListener);
    }


    private View.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            String playerInput = input.getText().toString().trim();
            if (playerInput.length() != 0) {
                input.setText("");
                addMessage(playerInput);
                startComputerDelay(playerInput);
            }
        }
    };

    private void checkPlayerInput(String playerInput) {
        if ((playerInput.equals(pz.getAnswer()) && closeToQuestion)) {
            if (puzzleIndex == 8) {
                addMessage("Good job, you've done it!");
                addMessage("Congratulations! You have solved all the puzzles! Check back later for added puzzles in future updates.");
            } else {
                pm.getPuzzle(puzzleIndex).setSolved(true);
                puzzleIndex++;
                pz = pm.getPuzzle(puzzleIndex);
                closeToQuestion = false;
                addMessage("Good job, here's the next one!");
                addMessage(pz.toString());
            }
        } else if ((playerInput.equals(pz.getAnswer()) && !closeToQuestion)) {
            addMessage("Check your map to make sure you are close enough!");
        } else {
            addMessage("Sorry, that's incorrect. Try again.");
        }
    }

    private void startComputerDelay(String str) {
        // Log.d(TAG, "Starting computer delay");
        final String s = str;
        mRunnable = createRunnable(s);
        mPauseHandler.postDelayed(mRunnable, 750); // Pause for three quarters of a second
    }

    private Runnable createRunnable(final String str) {
        return new Runnable() {
            public void run() {

                // Done thinking, time to move.
                Log.d(TAG, "delay over making move.");
                checkPlayerInput(str);
            }//end run override method
        };//end of new Runnable()
    }//end of createRunnable method



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_game, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager fm = getFragmentManager();
        switch (item.getItemId()) {
            case R.id.new_game:
                //startNewGame();
                mPauseHandler.removeCallbacks(mRunnable);
                startNewGame();
                arrayAdapter.notifyDataSetChanged();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(MessagingActivity.this, About.class));
                return true;
            case R.id.map:
                startMapActivity(mSend);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startNewGame() {
        puzzleIndex = 0;
        smsMessagesList.clear();
        for (int i = 0; i < pm.puzzles.size(); i++) {
            Puzzle cur = pm.getPuzzle(i);
            cur.setSolved(false);
        }
        pz = pm.getPuzzle(puzzleIndex);
        addMessage(pz.toString());
    }

    private void scrollToBottom() {
        messages.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                messages.setSelection(arrayAdapter.getCount() - 1);
            }
        });
    }

    private void addMessage(String str) {
        smsMessagesList.add(str);
        arrayAdapter.notifyDataSetChanged();
        scrollToBottom();
    }


    public void startMapActivity(View view) {
        Intent intent = new Intent(MessagingActivity.this, MapActivity.class);

        intent.putExtra("Size", pm.puzzles.size());
        for (int i = 0; i < pm.puzzles.size(); i++) {
            Puzzle cur = pm.getPuzzle(i);

            Log.d(TAG, "Lat " + cur.getLat());
            intent.putExtra("Lat" + i, cur.getLat());
            Log.d(TAG, "Long " + cur.getLng());
            intent.putExtra("Long" + i, cur.getLng());
            Log.d(TAG, "isSolved " + cur.isSolved());
            intent.putExtra("solved" + i, cur.isSolved());
            intent.putExtra("puzzle_index", puzzleIndex);

        }
        intent.putExtra("puzzleIndex", puzzleIndex);

        startActivityForResult(intent, 7);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "entering onActivityForResult");
        Log.d(TAG, "requestCode: " + requestCode);
        Log.d(TAG, "resultCode: " + resultCode);

        if (resultCode == RESULT_OK) {
            Log.d(TAG, "inside resultCode");
            if (requestCode == 7) {
                closeToQuestion = data.getBooleanExtra("closeToQuestion", false);
                Log.d(TAG, "Result of closeToQuestion at end of activity: " + closeToQuestion);
            }
        }

    }
    @Override
    protected void onPause() {
        super.onPause();
        // Log.d(TAG, "in onPause");
//        if (mSounds != null) {
//            mSounds.release();
//            mSounds = null;
//        }
        // since we are pausing, we want to stop the computer delay,
        // but restart it when we resume
        mPauseHandler.removeCallbacks(mRunnable);
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt("messageCount", smsMessagesList.size());
        editor.putInt("puzzleIndex", puzzleIndex);
        System.out.println("message count size: " + smsMessagesList.size());

        System.out.println("Saving List");

        for (int k = 0; k < pm.puzzles.size(); k++) {
            Puzzle p = pm.getPuzzle(k);
            editor.putBoolean("isSolved" + k, p.isSolved());
        }


        for (int i = 0; i < smsMessagesList.size(); i++) {
            editor.putString(String.valueOf(i), smsMessagesList.get(i));
            System.out.println(smsMessagesList.get(i));

        }

        // Commit the edits!
        editor.apply();
        editor.commit();
    }

    private void restoreList() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int messageCount = settings.getInt("messageCount", 0);
        puzzleIndex = settings.getInt("puzzleIndex", 0);
        pz = pm.getPuzzle(puzzleIndex);

        for (int k = 0; k < pm.puzzles.size(); k++) {
            Puzzle p = pm.getPuzzle(k);
            p.setSolved(settings.getBoolean("isSolved" + k, false));
        }

        if (settings.getInt("messageCount", 0) != 0) {
            int i = 0;
            System.out.println("Restoring List");
            while (i < messageCount) {
                smsMessagesList.add(settings.getString("" + i, "message"));
                System.out.println("reading");
                System.out.println(smsMessagesList.get(i));
                i++;
            }
            arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, smsMessagesList);
            System.out.println("done");

        } else {
            smsMessagesList = new ArrayList<>();
            arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, smsMessagesList);
            smsMessagesList.add(pz.toString());
        }
    }
}
