package com.gaode.maps.android.clustering;

import java.util.Collection;

import com.amap.api.maps.model.LatLng;

/**
 * A collection of ClusterItems that are nearby each other.
 */
public interface Cluster<T extends ClusterItem> {
    public LatLng getPosition();

    Collection<T> getItems();

    int getSize();
}