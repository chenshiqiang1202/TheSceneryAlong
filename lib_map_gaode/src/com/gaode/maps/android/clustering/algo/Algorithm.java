package com.gaode.maps.android.clustering.algo;

import java.util.Collection;
import java.util.Set;

import com.gaode.maps.android.clustering.Cluster;
import com.gaode.maps.android.clustering.ClusterItem;

/**
 * Logic for computing clusters
 */
public interface Algorithm<T extends ClusterItem> {
    void addItem(T item);

    void addItems(Collection<T> items);

    void clearItems();

    void removeItem(T item);

    Set<? extends Cluster<T>> getClusters(double zoom);

    Collection<T> getItems();
}