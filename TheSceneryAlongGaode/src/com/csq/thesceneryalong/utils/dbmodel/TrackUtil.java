/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月1日 下午10:53:20   
 * @version 1.0   
 */
package com.csq.thesceneryalong.utils.dbmodel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.zeroturnaround.zip.ZipUtil;

import android.text.TextUtils;

import com.csq.thesceneryalong.constant.PathConstants;
import com.csq.thesceneryalong.db.Scenery;
import com.csq.thesceneryalong.db.Track;
import com.csq.thesceneryalong.db.TrackPoint;
import com.csq.thesceneryalong.io.db.SceneryDb;
import com.csq.thesceneryalong.io.db.TrackDb;
import com.csq.thesceneryalong.io.db.TrackPointDb;
import com.csq.thesceneryalong.models.models.RecordStatus;
import com.csq.thesceneryalong.utils.DateUtils;
import com.csq.thesceneryalong.utils.DeviceUtil;
import com.csq.thesceneryalong.utils.Dom4jUtil;
import com.csq.thesceneryalong.utils.StringUtils;

public class TrackUtil {
	
	public static final String NODE_TRACK = "Track";
	public static final String NODE_TRACK_UNIQUEMACK = "uniqueMack";
	public static final String NODE_TRACK_VERSION = "version";
	public static final String NODE_TRACK_NAME = "name";
	public static final String NODE_TRACK_DESCRIPTION = "description";
	public static final String NODE_TRACK_BEGINTIME = "beginTime";
	public static final String NODE_TRACK_ENDTIME = "endTime";
	public static final String NODE_TRACK_POINTSNUM = "pointsNum";
	public static final String NODE_TRACK_FIRSTPOINTTIME = "firstPointTime";
	public static final String NODE_TRACK_LASTPOINTTIME = "lastPointTime";
	public static final String NODE_TRACK_MOVINGTIME = "movingTime";
	public static final String NODE_TRACK_MOVINGDISTANCE = "movingDistance";
	public static final String NODE_TRACK_SCENERYNUM = "sceneryNum";
	public static final String NODE_TRACK_RECORDSTATUS = "recordStatus";
	public static final String NODE_TRACK_TRACKPOINTS = "trackPoints";
	public static final String NODE_TRACK_SCENERYS = "scenerys";

	/**
	 * @description: 创建一个正在记录的轨迹
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @return
	 */
	public static Track newRecordingTrack(){
		Track track = new Track();
		long curTime = System.currentTimeMillis();
		
		track.setBeginTime(curTime);
		track.setDescription("");
		track.setEndTime(curTime);
		track.setFirstPointTime(curTime);
		track.setLastPointTime(curTime);
		track.setMovingDistance(0.0);
		track.setMovingTime(0l);
		track.setName(DateUtils.getFormatedDateYMDHM(curTime));
		track.setPointsNum(0);
		track.setRecordStatus(RecordStatus.recording.getValue());
		track.setSceneryNum(0);
		track.setUniqueMack(DeviceUtil.getTrackUniqueMark());
		track.setVersion(0);
		
		return track;
	}
	
	/**
	 * @description: 获得列表日期分隔标识yyyy-MM-dd
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param track
	 * @return
	 */
	public static String getListSelection(Track track){
		return DateUtils.getFormatedDateYMD(track.getBeginTime());
	}
	
	/**
	 * @description: 获得xml保存路径，在轨迹目录/xml/v1.xml
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param track
	 * @return
	 */
	public static String getTrackXmlPath(Track track){
		return getTrackXmlPath(track.getUniqueMack());
	}
	
	public static String getTrackXmlPath(String uniqueMack){
		String path = PathConstants.getTrackpath() 
				+ File.separator + uniqueMack
				+ File.separator + "xml";
		File f = new File(path);
		if(!f.exists()){
			f.mkdirs();
		}
		
		return path	+ File.separator + uniqueMack + ".xml";
	}
	
	
	/**
	 * @description: 获取轨迹路径
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param track
	 * @return
	 */
	public static String getTrackPath(Track track){
		String path = PathConstants.getTrackpath() 
				+ File.separator + track.getUniqueMack();
		File f = new File(path);
		if(!f.exists()){
			f.mkdirs();
		}
		return path;
	}
	
	
	/**
	 * @description: 生成xml文件，路径getTrackXmlPath(Track track)即轨迹目录/xml/v1.xml
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param track
	 * @return
	 */
	public static String createXml(Track track){
		
		try {
			//DocumentHelper提供了创建Document对象的方法  
	        Document document = DocumentHelper.createDocument();
	        document.setXMLEncoding("UTF-8");
	        //1.添加Track节点
	        Element trackElement = document.addElement(NODE_TRACK);
	        //1.1 uniqueMack
	        Element uniqueMack = trackElement.addElement(NODE_TRACK_UNIQUEMACK);
	        uniqueMack.setText(StringUtils.avoidNull(track.getUniqueMack(), ""));
	        //1.2 version
	        Element version = trackElement.addElement(NODE_TRACK_VERSION);
	        version.setText(StringUtils.avoidNull(track.getVersion(), ""));
	        //1.3 name
	        Element name = trackElement.addElement(NODE_TRACK_NAME);
	        name.setText(StringUtils.avoidNull(track.getName(), ""));
	        //1.4 description
	        Element description = trackElement.addElement(NODE_TRACK_DESCRIPTION);
	        description.setText(StringUtils.avoidNull(track.getDescription(), ""));
	        //1.5 beginTime
	        Element beginTime = trackElement.addElement(NODE_TRACK_BEGINTIME);
	        beginTime.setText(StringUtils.avoidNull(track.getBeginTime(), ""));
	        //1.6 endTime
	        Element endTime = trackElement.addElement(NODE_TRACK_ENDTIME);
	        endTime.setText(StringUtils.avoidNull(track.getEndTime(), ""));
	        //1.7 pointsNum
	        Element pointsNum = trackElement.addElement(NODE_TRACK_POINTSNUM);
	        pointsNum.setText(StringUtils.avoidNull(track.getPointsNum(), ""));
	        //1.8 firstPointTime
	        Element firstPointTime = trackElement.addElement(NODE_TRACK_FIRSTPOINTTIME);
	        firstPointTime.setText(StringUtils.avoidNull(track.getFirstPointTime(), ""));
	        //1.9 lastPointTime
	        Element lastPointTime = trackElement.addElement(NODE_TRACK_LASTPOINTTIME);
	        lastPointTime.setText(StringUtils.avoidNull(track.getLastPointTime(), ""));
	        //1.10 movingTime
	        Element movingTime = trackElement.addElement(NODE_TRACK_MOVINGTIME);
	        movingTime.setText(StringUtils.avoidNull(track.getMovingTime(), ""));
	        //1.11 movingDistance
	        Element movingDistance = trackElement.addElement(NODE_TRACK_MOVINGDISTANCE);
	        movingDistance.setText(StringUtils.avoidNull(track.getMovingDistance(), ""));
	        //1.12 sceneryNum
	        Element sceneryNum = trackElement.addElement(NODE_TRACK_SCENERYNUM);
	        sceneryNum.setText(StringUtils.avoidNull(track.getSceneryNum(), ""));
	        //1.13 recordStatus
	        Element recordStatus = trackElement.addElement(NODE_TRACK_RECORDSTATUS);
	        recordStatus.setText(StringUtils.avoidNull(track.getRecordStatus(), ""));
	        
	        //1.14 trackPoints
	        Element trackPoints = trackElement.addElement(NODE_TRACK_TRACKPOINTS);
	        List<TrackPoint> tps = track.getTrackPoints();
	        track.resetTrackPoints();
	        
		        if(tps != null && !tps.isEmpty()){
		        	for(TrackPoint point : tps){
		        		TrackPointUtil.createXml(trackPoints, point);
		        	}
		        }
	        
	        //1.15 Scenery
	        Element scenerys = trackElement.addElement(NODE_TRACK_SCENERYS);
	        List<Scenery> ss = track.getScenerys();
	        track.resetScenerys();
	        
		        if(ss != null && !ss.isEmpty()){
		        	for(Scenery point : ss){
		        		SceneryUtil.createXml(scenerys, point);
		        	}
		        }
		        
		    String xmlPath = getTrackXmlPath(track);
	        Writer fileWriter = new FileWriter(xmlPath);  
	        //换行
	        OutputFormat format = new OutputFormat();  
	        format.setEncoding("UTF-8");  
	        format.setNewlines(true); 
	        //dom4j提供了专门写入文件的对象XMLWriter  
	        XMLWriter xmlWriter = new XMLWriter(fileWriter, format);  
	        xmlWriter.write(document);  
	        xmlWriter.flush();  
	        xmlWriter.close();
	        
	        return xmlPath;
	        
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
        
		return null;
	}
	
	/**
	 * @description: 解析xml文件，生成轨迹,并保存到数据库
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param xmlPath
	 * @return
	 */
	public static Track parseXmlAndSave(String xmlPath) {
		try {
			// 将src下面的xml转换为输入流
			InputStream inputStream = new FileInputStream(xmlPath);
			// 创建SAXReader读取器，专门用于读取xml
			SAXReader saxReader = new SAXReader();
			// 根据saxReader的read重写方法可知，既可以通过inputStream输入流来读取，也可以通过file对象来读取
			Document document = saxReader.read(inputStream);
			
			Track track = new Track();
			
			Element trackElement = document.getRootElement();
			
			track.setUniqueMack(Dom4jUtil.parseString(trackElement, 
					NODE_TRACK_UNIQUEMACK, 
					""));
			
			track.setVersion(Dom4jUtil.parseInterger(trackElement, 
					NODE_TRACK_VERSION, 
					0));
			
			track.setName(Dom4jUtil.parseString(trackElement, 
					NODE_TRACK_NAME, 
					""));
			
			track.setDescription(Dom4jUtil.parseString(trackElement, 
					NODE_TRACK_DESCRIPTION, 
					""));
			
			track.setBeginTime(Dom4jUtil.parseLong(trackElement, 
					NODE_TRACK_BEGINTIME, 
					0));
			
			track.setEndTime(Dom4jUtil.parseLong(trackElement, 
					NODE_TRACK_ENDTIME, 
					0));
			
			track.setFirstPointTime(Dom4jUtil.parseLong(trackElement, 
					NODE_TRACK_FIRSTPOINTTIME, 
					0));
			
			track.setLastPointTime(Dom4jUtil.parseLong(trackElement, 
					NODE_TRACK_LASTPOINTTIME, 
					0));
			
			track.setMovingTime(Dom4jUtil.parseLong(trackElement, 
					NODE_TRACK_MOVINGTIME, 
					0));
			
			track.setMovingDistance(Dom4jUtil.parseDouble(trackElement, 
					NODE_TRACK_MOVINGDISTANCE, 
					0));
			
			track.setRecordStatus(Dom4jUtil.parseInterger(trackElement, 
					NODE_TRACK_RECORDSTATUS, 
					0));
			
			List<TrackPoint> tps = TrackPointUtil.parseXml(
					trackElement.element(NODE_TRACK_TRACKPOINTS));
			track.setPointsNum(tps.size());	//轨迹点以解析到的为准
			
			List<Scenery> ss = SceneryUtil.parseXml(
					trackElement.element(NODE_TRACK_SCENERYS));
			track.setSceneryNum(ss.size());	//风景数以解析到的为准
			
			//保存轨迹
			long trackId = TrackDb.getInstance().add(track);
			if(trackId < 1){
				//轨迹保存失败，返回
				return null;
			}
			track.setId(trackId);
			
			//保存轨迹点
			if(!tps.isEmpty()){
				for(TrackPoint tp : tps){
					tp.setTrackId(trackId);
				}
				TrackPointDb.getInstance().addSome(tps);
			}
			
			//保存风景
			if(!ss.isEmpty()){
				for(Scenery s : ss){
					s.setTrackId(trackId);
				}
				SceneryDb.getInstance().addSome(ss);
			}
			
			return track;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/**
	 * @description: 压缩并返回zip路径
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param track
	 * @return
	 */
	public static String zipPack(Track track){
		if(createXml(track) != null){
			String trackPath = PathConstants.getTrackpath()
					+ File.separator + track.getUniqueMack();
			String zipPath = PathConstants.getExportpath() 
					+ File.separator + StringUtils.filterIllegalWords(track.getName()) 
					+ ".tsa";
			ZipUtil.pack(new File(trackPath), new File(zipPath), true);
			return zipPath;
		}
		return null;
	}
	
	/**
	 * @description: 获取需要导入的轨迹文件列表
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @return
	 */
	public static List<String> getImportTasFiles(){
		List<String> tsas = new ArrayList<String>();
		File root = new File(PathConstants.getImportpath());
		if(root.exists()){
			File[] fs = root.listFiles();
			if(fs != null && fs.length > 0){
				for(File f : fs){
					if(f.isFile() && f.getName().endsWith(".tsa")){
						tsas.add(f.getAbsolutePath());
					}
				}
			}
		}
		return tsas;
	}
	
	/**
	 * @description: 导入一条轨迹
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param zipPath
	 * @return
	 */
	public static Track importATrack(String tsaPath){
		//先获取轨迹唯一标识
		String uniqueMack = getUniqueMackFromZip(tsaPath);
		
		if(!TextUtils.isEmpty(uniqueMack)){
			Track track = TrackDb.getInstance().queryByUniqueMack(uniqueMack);
			if(track != null){
				//导入的轨迹已存在
				//导入成功，删除tsa文件
				new File(tsaPath).delete();
				return track;
			}else{
				//不存在，先解压，再解析保存到数据库
				//解压
				ZipUtil.unpack(new File(tsaPath), 
						new File(PathConstants.getTrackpath()));
				//解析并保存轨迹到数据库
				String xmlPath = getTrackXmlPath(uniqueMack);
				track = parseXmlAndSave(xmlPath);
				if(track != null){
					//导入成功，删除tsa文件
					new File(tsaPath).delete();
				}
				return track;
			}
		}
		return null;
	}
	
	
	/**
	 * @description: 从zip文件获取轨迹唯一标识
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param zipPath
	 * @return
	 */
	public static String getUniqueMackFromZip(String zipPath){
		String uniqueMack = null;
		
		ZipInputStream inZip = null;
		try {
			inZip = new ZipInputStream(new java.io.FileInputStream(zipPath));

			ZipEntry zipEntry;
			String entryName;
			while ((zipEntry = inZip.getNextEntry()) != null) {
				entryName = zipEntry.getName();
				if (zipEntry.isDirectory() && entryName.contains(File.separator + "xml")) {
					uniqueMack = entryName.substring(0, 
							entryName.indexOf(File.separator + "xml"));
					break;
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(inZip != null){
				try {
					inZip.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return uniqueMack;
	}
	
	
}
