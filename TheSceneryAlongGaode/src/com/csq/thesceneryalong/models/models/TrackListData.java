package com.csq.thesceneryalong.models.models;

import com.csq.thesceneryalong.db.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by csq on 2014/6/23.
 */
public class TrackListData {
    /**
     * 所有轨迹
     */
    public List<Track> tracks;

    /**
     * 日期不同的组第一个的序号
     */
    public List<Integer> sectionIndices;

    /**
     * 分组日期
     */
    public List<String> headLetters;

    public static TrackListData createEmptyTrackListData(){
        TrackListData data = new TrackListData();
        data.tracks = new ArrayList<Track>();
        data.sectionIndices = new ArrayList<Integer>();
        data.headLetters = new ArrayList<String>();
        return data;
    }
}
