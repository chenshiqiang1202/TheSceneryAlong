/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年4月28日 下午11:52:39   
 * @version 1.0   
 */
package com.csq.thesceneryalong.models.models.cluster_renderer;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MarkerOptions;
import com.csq.thesceneryalong.R;
import com.csq.thesceneryalong.app.App;
import com.csq.thesceneryalong.models.models.SceneryCluster;
import com.csq.thesceneryalong.utils.BitmapUtil;
import com.csq.thesceneryalong.utils.MultiDrawable;
import com.gaode.maps.android.clustering.Cluster;
import com.gaode.maps.android.clustering.ClusterManager;
import com.gaode.maps.android.clustering.view.DefaultClusterRenderer;
import com.gaode.maps.android.ui.IconGenerator;

public class SceneryRenderer extends DefaultClusterRenderer<SceneryCluster> {

	// ------------------------ Constants ------------------------

	// ------------------------- Fields --------------------------
	private final IconGenerator mIconGenerator = new IconGenerator(App.app);
    private final IconGenerator mClusterIconGenerator = new IconGenerator(App.app);
    private final ImageView mImageView;
    private final ImageView mClusterImageView;
    private final int mDimension;

	// ----------------------- Constructors ----------------------
	
	public SceneryRenderer(Context context, 
			AMap map,
			ClusterManager<SceneryCluster> clusterManager) 
	{
		super(context, map, clusterManager);
		// TODO Auto-generated constructor stub
		View multiProfile = LayoutInflater.from(context).inflate(R.layout.multi_profile, null);
        mClusterIconGenerator.setContentView(multiProfile);
        mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);
		
        int padding = (int) context.getResources().getDimension(R.dimen.padding_comm11);
		mDimension = (int) context.getResources().getDimension(R.dimen.scenery_icon_size);
        mImageView = new ImageView(App.app);
        mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
        mImageView.setPadding(padding, padding, padding, padding);
        mIconGenerator.setContentView(mImageView);
	}

	// -------- Methods for/from SuperClass/Interfaces -----------

	@Override
	protected void onBeforeClusterItemRendered(SceneryCluster item,
			MarkerOptions markerOptions) {
		mImageView.setImageBitmap(item.getPicture());
        Bitmap icon = mIconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
	}
	
	@Override
	protected void onBeforeClusterRendered(Cluster<SceneryCluster> cluster,
			MarkerOptions markerOptions) {
		List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
        int width = mDimension;
        int height = mDimension;

        for (SceneryCluster p : cluster.getItems()) {
            // Draw 4 at most.
            if (profilePhotos.size() == 4) break;
            Drawable drawable = BitmapUtil.toDrawable(App.app, p.getPicture());
            drawable.setBounds(0, 0, width, height);
            profilePhotos.add(drawable);
        }
        MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
        multiDrawable.setBounds(0, 0, width, height);

        mClusterImageView.setImageDrawable(multiDrawable);
        Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
	}
	
	@Override
	protected boolean shouldRenderAsCluster(Cluster<SceneryCluster> cluster) {
		// TODO Auto-generated method stub
		return cluster.getSize() > 1;
	}
	
	// --------------------- Methods public ----------------------

	// --------------------- Methods private ---------------------

	// --------------------- Getter & Setter ---------------------

	// --------------- Inner and Anonymous Classes ---------------
}
