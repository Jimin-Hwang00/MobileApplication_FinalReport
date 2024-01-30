package ddwu.mobile.finalproject.ma02_20201036.model.dto;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "review_table")
public class Review implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name="image")
    public String image;

    @ColumnInfo(name="title")
    public String title;

    @ColumnInfo(name="director")
    public String director;

    @ColumnInfo(name="actor")
    public String actor;

    @ColumnInfo(name="comment")
    public String comment;

    @ColumnInfo(name="lat")
    public Double lat;

    @ColumnInfo(name="lng")
    public Double lng;

    @ColumnInfo(name="theater")
    public String theater;

    public long getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getTheater() {
        return theater;
    }

    public void setTheater(String theater) {
        this.theater = theater;
    }

    public Review() {}

    public Review(String image, String title, String director, String actor, String comment, Double lat, Double lng, String theater) {
        this.image = image;
        this.title = title;
        this.director = director;
        this.actor = actor;
        this.comment = comment;
        this.lat = lat;
        this.lng = lng;
        this.theater = theater;
    }

    public Review(long id, String image, String title, String director, String actor, String comment, Double lat, Double lng, String theater) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.director = director;
        this.actor = actor;
        this.comment = comment;
        this.lat = lat;
        this.lng = lng;
        this.theater = theater;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", image='" + image + '\'' +
                ", title='" + title + '\'' +
                ", director='" + director + '\'' +
                ", actor='" + actor + '\'' +
                ", comment='" + comment + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
