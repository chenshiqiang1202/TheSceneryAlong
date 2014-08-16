/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年4月29日 上午12:09:03   
 * @version 1.0   
 */
package com.csq.thesceneryalong.utils.dbmodel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import android.location.Location;

import com.csq.thesceneryalong.constant.PathConstants;
import com.csq.thesceneryalong.db.Scenery;
import com.csq.thesceneryalong.db.Track;
import com.csq.thesceneryalong.logic.manager.MyLocationManager;
import com.csq.thesceneryalong.logic.manager.TrackManager;
import com.csq.thesceneryalong.models.models.SceneryType;
import com.csq.thesceneryalong.utils.CaptureMediaUtil.MediaData;
import com.csq.thesceneryalong.utils.DateUtils;
import com.csq.thesceneryalong.utils.Dom4jUtil;
import com.csq.thesceneryalong.utils.StringUtils;

public class SceneryUtil {
	
	public static final String NODE_SCENERY = "Scenery";
	public static final String NODE_SCENERY_NAME = "name";
	public static final String NODE_SCENERY_DESCRIPTION = "description";
	public static final String NODE_SCENERY_TYPE = "type";
	public static final String NODE_SCENERY_UNIQUEMACK = "uniqueMack";
	public static final String NODE_SCENERY_TIME = "time";
	public static final String NODE_SCENERY_LONGITUDE = "longitude";
	public static final String NODE_SCENERY_LATITUDE = "latitude";
	public static final String NODE_SCENERY_ALTITUDE = "altitude";
	public static final String NODE_SCENERY_ACCURACY = "accuracy";
	public static final String NODE_SCENERY_SPEED = "speed";
	public static final String NODE_SCENERY_BEARING = "bearing";
	public static final String NODE_SCENERY_PROVIDER = "provider";
	public static final String NODE_SCENERY_ADDRESS = "address";
	
	
	/**
	 * @description: 根据media类型和文件路径，生成一个风景
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param media
	 * @return
	 */
	public static Scenery newSceneryOfCurTrack(MediaData media){
		Scenery r = null;
		Track track = TrackManager.getInstance().getCurTrack();
		Location loc = MyLocationManager.getInstance().getCurrentLocation();
		if(track != null && loc != null){
			String uniqueMack = track.getUniqueMack();
			String sceneryType = SceneryType.create(media).name();
			String sceneryName = DateUtils.getFormatedDateYMDHMSFile(System.currentTimeMillis());
			//风景文件名
			String sceneryPath = getSceneryFilePath(uniqueMack, sceneryType, sceneryName);
			
			if(new File(media.filePath).renameTo(new File(sceneryPath))){
				r = new Scenery();
				r.setAccuracy(loc.getAccuracy());
				r.setAltitude(loc.getAltitude());
				r.setBearing(loc.getBearing());
				r.setLatitude(loc.getLatitude());
				r.setLongitude(loc.getLongitude());
				r.setName(sceneryName);
				r.setProvider(loc.getProvider());
				r.setSpeed(loc.getSpeed());
				r.setTime(loc.getTime());
				r.setTrackId(track.getId());
				r.setType(sceneryType);
				r.setUniqueMack(uniqueMack);
			}
		}
		return r; 
	}

	/**
	 * @description: 获得风景文件的完整路径
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param scenery
	 * @return
	 */
	public static String getSceneryFilePath(Scenery scenery){
		return getSceneryFilePath(scenery.getUniqueMack(), 
				scenery.getType(), 
				scenery.getName());
	}
	
	public static String getSceneryFilePath(String uniqueMack, String sceneryType, String sceneryName){
		String suffix = "";	//后缀
		if(sceneryType.equals(SceneryType.image.name())){
			suffix = ".jpg";
		}else if(sceneryType.equals(SceneryType.video.name())){
			suffix = ".mp4";
		}
		
		String path = PathConstants.getTrackpath() 
				+ File.separator + uniqueMack
				+ File.separator + sceneryType;
		File f = new File(path);
		if(!f.exists()){
			f.mkdirs();
		}
		
		File nomedia = new File(path + File.separator + ".nomedia");
		if(!nomedia.exists()){
			try {
				nomedia.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return path	+ File.separator + sceneryName + suffix;
	}
	
	/**
	 * @description: 创建xml文件，并添加到轨迹scenerys节点下面
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param scenerys
	 * @param scenery
	 */
	public static void createXml(Element scenerys, Scenery scenery){
		Element sceneryElement = scenerys.addElement(NODE_SCENERY);
		
		Element name = sceneryElement.addElement(NODE_SCENERY_NAME);
		name.setText(StringUtils.avoidNull(scenery.getName(), ""));
		
		Element description = sceneryElement.addElement(NODE_SCENERY_DESCRIPTION);
		description.setText(StringUtils.avoidNull(scenery.getDescription(), ""));
		
		Element type = sceneryElement.addElement(NODE_SCENERY_TYPE);
		type.setText(StringUtils.avoidNull(scenery.getType(), ""));
		
		Element uniqueMack = sceneryElement.addElement(NODE_SCENERY_UNIQUEMACK);
		uniqueMack.setText(StringUtils.avoidNull(scenery.getUniqueMack(), ""));
		
		Element time = sceneryElement.addElement(NODE_SCENERY_TIME);
		time.setText(StringUtils.avoidNull(scenery.getTime(), ""));
		
		Element longitude = sceneryElement.addElement(NODE_SCENERY_LONGITUDE);
		longitude.setText(StringUtils.avoidNull(scenery.getLongitude(), ""));
		
		Element latitude = sceneryElement.addElement(NODE_SCENERY_LATITUDE);
		latitude.setText(StringUtils.avoidNull(scenery.getLatitude(), ""));
		
		Element altitude = sceneryElement.addElement(NODE_SCENERY_ALTITUDE);
		altitude.setText(StringUtils.avoidNull(scenery.getAltitude(), ""));
		
		Element accuracy = sceneryElement.addElement(NODE_SCENERY_ACCURACY);
		accuracy.setText(StringUtils.avoidNull(scenery.getAccuracy(), ""));
		
		Element speed = sceneryElement.addElement(NODE_SCENERY_SPEED);
		speed.setText(StringUtils.avoidNull(scenery.getSpeed(), ""));
		
		Element bearing = sceneryElement.addElement(NODE_SCENERY_BEARING);
		bearing.setText(StringUtils.avoidNull(scenery.getBearing(), ""));
		
		Element provider = sceneryElement.addElement(NODE_SCENERY_PROVIDER);
		provider.setText(StringUtils.avoidNull(scenery.getProvider(), ""));
		
		Element address = sceneryElement.addElement(NODE_SCENERY_ADDRESS);
		address.setText(StringUtils.avoidNull(scenery.getAddress(), ""));
	}
	
	public static List<Scenery> parseXml(Element scenerysElement){
		List<Scenery> ss = new ArrayList<Scenery>();
		if(scenerysElement != null){
			List<Element> ssEs = scenerysElement.elements(NODE_SCENERY);
			if(ssEs != null && !ssEs.isEmpty()){
				Scenery scenery = null;
				for(Element item : ssEs){
					scenery = new Scenery();
					
					scenery.setName(Dom4jUtil.parseString(item, 
							NODE_SCENERY_NAME, 
							""));
					
					scenery.setDescription(Dom4jUtil.parseString(item, 
							NODE_SCENERY_DESCRIPTION, 
							""));
					
					scenery.setType(Dom4jUtil.parseString(item, 
							NODE_SCENERY_TYPE, 
							""));
					
					scenery.setUniqueMack(Dom4jUtil.parseString(item, 
							NODE_SCENERY_UNIQUEMACK, 
							""));
					
					scenery.setTime(Dom4jUtil.parseLong(item, 
							NODE_SCENERY_TIME, 
							0));
					
					scenery.setLongitude(Dom4jUtil.parseDouble(item, 
							NODE_SCENERY_LONGITUDE, 
							0));
					
					scenery.setLatitude(Dom4jUtil.parseDouble(item, 
							NODE_SCENERY_LATITUDE, 
							0));
					
					scenery.setAltitude(Dom4jUtil.parseDouble(item, 
							NODE_SCENERY_ALTITUDE, 
							0));
					
					scenery.setAccuracy(Dom4jUtil.parseFloat(item, 
							NODE_SCENERY_ACCURACY, 
							0));
					
					scenery.setSpeed(Dom4jUtil.parseFloat(item, 
							NODE_SCENERY_SPEED, 
							0));
					
					scenery.setBearing(Dom4jUtil.parseFloat(item, 
							NODE_SCENERY_BEARING, 
							0));
					
					scenery.setProvider(Dom4jUtil.parseString(item, 
							NODE_SCENERY_PROVIDER, 
							""));
					
					scenery.setAddress(Dom4jUtil.parseString(item, 
							NODE_SCENERY_ADDRESS, 
							""));
					
					ss.add(scenery);
				}
			}
		}
		return ss;
	}
	
}
