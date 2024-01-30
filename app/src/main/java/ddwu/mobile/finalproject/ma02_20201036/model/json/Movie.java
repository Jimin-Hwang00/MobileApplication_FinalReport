package ddwu.mobile.finalproject.ma02_20201036.model.json;

import java.io.Serializable;

public class Movie implements Serializable {
    private String title;
    private String image;
    private String director;
    private String actor;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    @Override
    public String toString() {
        return "movie{" +
                "title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", director='" + director + '\'' +
                ", actor='" + actor + '\'' +
                '}';
    }
}
