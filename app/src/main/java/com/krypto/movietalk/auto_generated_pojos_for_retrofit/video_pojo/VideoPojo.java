package com.krypto.movietalk.auto_generated_pojos_for_retrofit.video_pojo;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class VideoPojo {

    @Expose
    private int id;
    @Expose
    private List<VideoResult> results = new ArrayList<>();

    /**
     *
     * @return
     * The id
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The results
     */
    public List<VideoResult> getResults() {
        return results;
    }

    /**
     *
     * @param results
     * The results
     */
    public void setResults(List<VideoResult> results) {
        this.results = results;
    }

}

