package thebestgroup.bigtroubleinlittlefield;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by RajanBose on 4/26/2017.
 */

public class PuzzleManager {
    ArrayList<Puzzle> puzzles;
    private final String TAG = "PUZZLE_MANAGER";

    public PuzzleManager(Context context) {
        this.puzzles = new ArrayList<Puzzle>();


        getPuzzles(context, R.raw.questions);


    }

    public Puzzle getPuzzle(int i) {
        return puzzles.get(i);
    }

    private void getPuzzles(Context context, int rid) {

        InputStream inputStream = context.getResources().openRawResource(rid);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        Log.d(TAG, "Attempting Populating list");
        try {
            while ((line = buffreader.readLine()) != null) {
                String[] curr = line.split(":");

                Log.d(TAG, "Populating list");
                Log.d(TAG, "Current puzzle " + Arrays.toString(curr));

                String question = curr[0];
                String answer = curr[1];
                float lat = Float.parseFloat(curr[2].trim());
                float lng = Float.parseFloat(curr[3].trim());

                Puzzle pz = new Puzzle(question, answer, lat, lng);
                puzzles.add(pz);
                Log.d(TAG, "puzzle length " + puzzles.size());

            }
        } catch (IOException e) {
            Log.d(TAG, "Nothing found ");

        }


    }


}
