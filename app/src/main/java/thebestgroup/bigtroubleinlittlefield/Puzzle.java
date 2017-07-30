package thebestgroup.bigtroubleinlittlefield;

import android.media.Image;

import java.util.ArrayList;

/**
 * Created by RajanBose on 4/4/2017.
 */

public class Puzzle {

    private String question;
    private String answer;
    private float lat;
    private float lng;
    private Image image;
    private boolean isSolved;
    private ArrayList<String> hints;

    public Puzzle(String question, String answer, float lat, float lng) {
        this.question = question;
        this.answer = answer;
        this.lat = lat;
        this.lng = lng;
//        this.image =
        isSolved = false;
        this.hints = new ArrayList<String>();
    }


    public String getAnswer() {
        return answer;
    }

    public float getLat() {
        return lat;
    }

    public float getLng() {
        return lng;
    }

    public void setSolved(boolean input){isSolved = input; }

    public boolean isSolved(){ return isSolved;}

    public void setSolutuion(String solutuion) {
        this.answer = solutuion;
    }

    public void setLat(long lat) {
        this.lat = lat;
    }

    public void setLongitude(long longitude) {
        this.lng = longitude;
    }
    @Override
    public String toString(){
        return question;
    }
}
