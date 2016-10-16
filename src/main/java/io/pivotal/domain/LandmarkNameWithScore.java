package io.pivotal.Domain;

/**
 * Created by mgoddard on 10/12/16.
 */
public class LandmarkNameWithScore {

    private String name;
    private float score;

    public LandmarkNameWithScore(String name, float score) {
        this.name = name;
        this.score = score;
    }

    public LandmarkNameWithScore() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getScorePercent() {
        return String.format("%d%%", (int) (100.0 * score));
    }

    public String toString() {
        return name + " (" + getScorePercent() + ")";
    }
}
