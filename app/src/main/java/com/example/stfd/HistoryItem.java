package com.example.stfd;

import java.net.URI;
import java.util.List;

public class HistoryItem {
    private String numDoc;
    private String time;
    private List<URI> photos;

    public HistoryItem(String numDoc, String time, List<URI> photos) {
        this.numDoc = numDoc;
        this.time = time;
        this.photos = photos;
    }

    public String getNumDoc() {
        return numDoc;
    }

    public String getTime() {
        return time;
    }

    public List<URI> getPhotos() {
        return photos;
    }

    public void setNumDoc(String numDoc) {
        this.numDoc = numDoc;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setPhotos(List<URI> photos) {
        this.photos = photos;
    }
}
