package com.csq.thesceneryalong.io.db;

import java.io.IOException;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;


public class TsaDaoGenerator{

	private static final int dbVersion = 1;

	public static void main(String[] args) throws IOException, Exception {
		// TODO Auto-generated method stub
		Schema schema = new Schema(dbVersion, "com.csq.thesceneryalong.db");

		addTables(schema);

		schema.enableKeepSectionsByDefault();

        new DaoGenerator().generateAll(schema, "../TheSceneryAlong/src-gen");
	}

	public static void addTables(Schema schema){
		//------------------------- 轨迹 ----------------------------
		Entity track = schema.addEntity("Track");
		track.addIdProperty();							//id
		track.addStringProperty("uniqueMack").notNull();//轨迹唯一标志,手机uuid+beginTime，轨迹文件保存文件夹
		track.addIntProperty("version");				//版本数，每修改编辑一次+1

		track.addStringProperty("name").notNull();		//轨迹名
		track.addStringProperty("description");			//描述
		track.addLongProperty("beginTime").notNull();	//轨迹开始记录的时间
		track.addLongProperty("endTime");				//轨迹停止记录的时间

		track.addIntProperty("pointsNum");				//轨迹点数
		track.addLongProperty("firstPointTime");		//记录到的第一个轨迹点的时间
		track.addLongProperty("lastPointTime");			//记录到的最后一个轨迹点的时间
		track.addLongProperty("movingTime");			//轨迹记录移动的时间
		track.addDoubleProperty("movingDistance");			//轨迹记录移动的里程
		track.addLongProperty("simulateTime");			//模拟时间

		track.addIntProperty("sceneryNum");				//风景数，即图片和视频数
		track.addIntProperty("recordStatus");			//记录状态，0finished,1recording,2paused

		track.implementsSerializable();

		//------------------------- 轨迹点 ----------------------------
		Entity trackPoint = schema.addEntity("TrackPoint");
		trackPoint.addIdProperty();							//id
		Property trackIdProperty = trackPoint.addLongProperty("trackId").notNull().getProperty();	//轨迹id
			trackPoint.addToOne(track, trackIdProperty);	//一个轨迹点只对应一条轨迹
		Property timeProperty = trackPoint.addLongProperty("time").notNull().getProperty();					//时间
		trackPoint.addDoubleProperty("longitude").notNull();			//经度
		trackPoint.addDoubleProperty("latitude").notNull();			//纬度
		trackPoint.addDoubleProperty("altitude");			//海拔
		trackPoint.addFloatProperty("accuracy");			//精度
		trackPoint.addFloatProperty("speed");				//速度
		trackPoint.addFloatProperty("bearing");				//方向
		trackPoint.addStringProperty("provider");			//位置提供者
		trackPoint.addIntProperty("pointStatus").notNull();			//轨迹点状态，0正常记录，1暂停，2恢复，1-2位置无效
			//一条轨迹对应多个轨迹点
			ToMany trackToTrackPoints = track.addToMany(trackPoint, trackIdProperty);
			trackToTrackPoints.setName("trackPoints");
			trackToTrackPoints.orderAsc(timeProperty);

		trackPoint.implementsSerializable();

		//------------------------- 风景 ----------------------------
		Entity scenery = schema.addEntity("Scenery");
		scenery.addIdProperty();							//id
		scenery.addStringProperty("name").notNull();					//名称
		scenery.addStringProperty("description");			//描述
		scenery.addStringProperty("type").notNull();					//类型,image/video/none
		scenery.addStringProperty("uniqueMack").notNull();			//轨迹唯一标志,手机uuid+beginTime，轨迹文件保存文件夹
		Property trackIdProperty2 = scenery.addLongProperty("trackId").notNull().getProperty();	//轨迹id
		scenery.addToOne(track, trackIdProperty2);		//一个轨迹点只对应一条轨迹
		Property timeProperty2 = scenery.addLongProperty("time").notNull().getProperty();					//时间
		scenery.addDoubleProperty("longitude").notNull();			//经度
		scenery.addDoubleProperty("latitude").notNull();			//纬度
		scenery.addDoubleProperty("altitude");			//海拔
		scenery.addFloatProperty("accuracy");			//精度
		scenery.addFloatProperty("speed");				//速度
		scenery.addFloatProperty("bearing");			//方向
		scenery.addStringProperty("provider");			//位置提供者
		scenery.addStringProperty("address");			//地址
			//一条轨迹对应多个轨迹点
			ToMany sceneryToTrackPoints = track.addToMany(scenery, trackIdProperty2);
			sceneryToTrackPoints.setName("scenerys");
			sceneryToTrackPoints.orderAsc(timeProperty2);

		scenery.implementsSerializable();
	}

}
