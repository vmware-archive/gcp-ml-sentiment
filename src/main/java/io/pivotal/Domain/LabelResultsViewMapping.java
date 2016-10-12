package io.pivotal.Domain;

/**
 * Created by mgoddard on 10/12/16.
 */
public class LabelResultsViewMapping {

    private String description;
    private float score;

    public LabelResultsViewMapping(String description, float score) {
        this.description = description;
        this.score = score;
    }

    public LabelResultsViewMapping() {
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getDescription() {
        return description;
    }

    public float getScore() {
        return score;
    }

    public String getScorePercent() {
        return String.format("%d%%", (int)(100.0 * getScore()));
    }

    public String toString () {
        return "LabelResultsViewMapping{" +
                "description='" + description + '\'' +
                ", score='" + score + '\'' +
                '}';
    }
}
