package com.hackathon.management.dto;

import com.hackathon.management.entity.Track;

public class TrackDistributionDto {
    private Track track;
    private String label;
    private long count;

    public TrackDistributionDto() {
    }

    public TrackDistributionDto(Track track, String label, long count) {
        this.track = track;
        this.label = label;
        this.count = count;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
