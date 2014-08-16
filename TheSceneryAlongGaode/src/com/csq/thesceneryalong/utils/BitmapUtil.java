package com.csq.thesceneryalong.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.List;

import com.csq.thesceneryalong.R;
import com.csq.thesceneryalong.app.App;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

public class BitmapUtil {

	// ***************************** 计算图片缩小倍数 *******************************

	private static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels, int orientation) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels, orientation);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels, int orientation) {
		double w, h;

		if (orientation == 0 || orientation == 180) {
			w = options.outWidth;
			h = options.outHeight;
		} else {
			w = options.outHeight;
			h = options.outWidth;
		}

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	// ***************************** 加载图片 *******************************
	/**
	 * 从uri加载图片 Try to load a {@link Bitmap} from the passed {@link Uri} ( a
	 * file, a content or an url )
	 * 
	 * @param uri
	 *            the image source
	 * @param minSideLength
	 * @param maxNumOfPixels
	 * @return null if load was unsuccesful
	 */
	public static Bitmap decodeBitmapFromUri(Context context, Uri uri, int minSideLength,
			int maxNumOfPixels) {
		InputStream stream = openInputStream(context,
				uri);
		if (null == stream) {
			return null;
		}

		int orientation = ExifUtils.getExifOrientation(
				context, uri);

		Bitmap bitmap = null;
		final BitmapFactory.Options decoded = decodeImageBounds(stream);
		IOUtils.closeSilently(stream);

		if (decoded != null) {
			int sampleSize;
			if (minSideLength < 0 || maxNumOfPixels < 0) {
				sampleSize = 1;
			} else {
				sampleSize = computeSampleSize(decoded, minSideLength,
						maxNumOfPixels, orientation);
			}

			BitmapFactory.Options options = getDefaultOptions();
			options.inSampleSize = sampleSize;

			bitmap = decodeBitmapFromUri(context, uri, options, minSideLength,
					maxNumOfPixels, orientation, 0);
		}

		return bitmap;
	}

	private static Bitmap decodeBitmapFromUri(Context context, Uri uri,
			BitmapFactory.Options options, int minSideLength,
			int maxNumOfPixels, int orientation, int loadTimes) {

		Bitmap bitmap = null;
		Bitmap newBitmap = null;

		if (loadTimes > 16) {
			return null;
		}

		InputStream stream = openInputStream(context,
				uri);
		if (null == stream)
			return null;

		try {
			// decode the bitmap via android BitmapFactory
			bitmap = BitmapFactory.decodeStream(stream, null, options);
			IOUtils.closeSilently(stream);

			if (bitmap != null) {
				if (minSideLength > 0 && maxNumOfPixels > 0) {
					newBitmap = rotateBitmap(bitmap, orientation);
					if (bitmap != newBitmap) {
						bitmap.recycle();
					}
					bitmap = newBitmap;
				}
			}

		} catch (OutOfMemoryError error) {
			IOUtils.closeSilently(stream);
			if (null != bitmap) {
				bitmap.recycle();
			}
			options.inSampleSize += 1;
			bitmap = decodeBitmapFromUri(context, uri, options, minSideLength,
					maxNumOfPixels, orientation, loadTimes + 1);
		}
		return bitmap;

	}

	/**
	 * 从文件路径加载图片
	 * 
	 * @param imageFile 图片文件路径
	 * @param minSideLength 最小边长
	 * @param maxNumOfPixels 最多像素点数
	 */
	public static Bitmap decodeBitmapFromFile(Context context, String imageFile,
			int minSideLength, int maxNumOfPixels) {
		return decodeBitmapFromUri(context, Uri.fromFile(new File(imageFile)),
				minSideLength, maxNumOfPixels);
	}

	/**
	 * 通过资源路径加载图片
	 * 
	 * @param context
	 * @param imageResId
	 * @param minSideLength
	 * @param maxNumOfPixels
	 * @return
	 */
	public static Bitmap decodeResource(Context context, int imageResId,
			int minSideLength, int maxNumOfPixels) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(context.getResources(), imageResId, opts);

		opts.inSampleSize = computeSampleSize(opts, minSideLength,
				maxNumOfPixels, 0);
		// 这里一定要将其设置回false，因为之前我们将其设置成了true
		opts.inJustDecodeBounds = false;

		return BitmapFactory.decodeResource(context.getResources(), imageResId,
				opts);
	}

	// ***************************** 图片处理 *******************************
	/**
	 * 获得圆角图片
	 * 
	 * @param bitmap
	 * @param roundPx
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx)
	{
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/**
	 * 旋转图片
	 * 
	 * @param bmp
	 * @param rotation 旋转角度
	 * @return
	 */
	public static Bitmap rotateBitmap(Bitmap bmp, float rotation) {
		if (rotation == 0) {
			return bmp;
		}

		Matrix matrix = new Matrix();
		matrix.postRotate(rotation);

		return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),
				matrix, true);
	}
	
	/**
	 * 将图片变为灰度图
	 * 
	 * @param old
	 * @return
	 */
	public static Bitmap getGreyImage(Bitmap old)
	{
		int width, height;
		height = old.getHeight();
		width = old.getWidth();
		Bitmap n = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(old.copy(Bitmap.Config.ARGB_8888, true));
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(n, 0, 0, paint);
		return n;
	}
	
	/**
	 * 缩放图片大小
	 * 
	 * @param bitmap
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height)
	{
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale((float) width / w, (float) height / h);
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
	}
	
	/**
	 * 获得带倒影的图片
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap getReflectionImageWithOrigin(Bitmap bitmap)
	{
		final int reflectionGap = 4;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);
		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
				width, height / 2, matrix, false);
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint defaultPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);
		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);
		return bitmapWithReflection;
	}
	
	/**
	 * 适配缩放图片大小
	 * 
	 * @param bitmap
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap zoomAdjustBitmap(Bitmap bitmap, int width, int height)
	{
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		int sw = w > width ? width : w;
		int sh = h > height ? height : h;
		if (w * h > width * height)
		{
			sw = width;
			sh = height;
		}
		Matrix matrix = new Matrix();
		matrix.postScale((float) sw / w, (float) sh / h);
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
	}

	/**
	 * 压缩图片
	 * @param fromFile
	 * @param toFile
	 * @param width
	 * @param height
	 * @param quality
	 * @return
	 */
	public static boolean transImage(String fromFile,String toFile,int width,int height,int quality){
		Bitmap bitmap = null;
		Bitmap resizeBitmap = null;
		FileOutputStream out = null;
		try
		{
    		BitmapFactory.Options opts = new BitmapFactory.Options();
    	    opts.inJustDecodeBounds = true;
    	    BitmapFactory.decodeFile(fromFile, opts);
    	    if(opts.outWidth * opts.outHeight < width * height)
            {
                 // 指定太大无法压缩，原图返回
                 return false;
            }
    	    bitmap = BitmapFactory.decodeFile(fromFile);
    	    resizeBitmap = zoomAdjustBitmap(bitmap,width,height);
    	    
    	    //saveFile
    	    File mycaptureFile = new File(toFile);
    	    out = new FileOutputStream(mycaptureFile);
    	    if (resizeBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out))
			{
				out.flush();
			}
			return true;
			
		}catch (Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if (bitmap != null && !bitmap.isRecycled())
			{
				// 记得释放资源，否则会内存溢出
				bitmap.recycle();
				bitmap = null;
			}
			if (resizeBitmap != null && !resizeBitmap.isRecycled())
			{
				resizeBitmap.recycle();
				resizeBitmap = null;
			}
			if(out != null)
			{
				try
				{
					out.close();
					out = null;
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	/**
	 * 上下拼接2个图片 bitmaps 要拼接的图片
	 */
	public static Bitmap spliceBitmapVertical(List<Bitmap> bitmaps,
			int backgroundColor) {
		if (bitmaps == null || bitmaps.isEmpty()) {
			return null;
		}

		int newWidth = 0;
		int newHeight = 0;
		for (Bitmap b : bitmaps) {
			if (b != null) {
				if (newWidth < b.getWidth()) {
					newWidth = b.getWidth();
				}
				newHeight += b.getHeight();
			}
		}

		if (newWidth == 0 || newHeight == 0) {
			return null;
		}

		Bitmap result = Bitmap.createBitmap(newWidth, newHeight,
				Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		canvas.drawColor(backgroundColor);

		int y = 0;
		for (Bitmap b : bitmaps) {
			if (b != null) {
				canvas.drawBitmap(b, 0, y, null);
				y += b.getHeight();
			}
		}

		return result;

	}
	
	private static Bitmap bMark;
	/**
	 * @description: 给图片添加视频水印
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param bitmap
	 */
	public static Bitmap addVideoWatermark(Bitmap bitmap){
		if(bitmap != null){
			Bitmap dest = bitmap.copy(Bitmap.Config.ARGB_8888, true);
			Canvas c = new Canvas(dest);
			int markSize = (int) (0.4f * Math.min(bitmap.getWidth(), bitmap.getHeight()));
			if(markSize > 150){
				markSize = 150;
			}
			if(bMark == null){
				bMark = BitmapFactory.decodeResource(App.getResources(), 
						R.drawable.ic_scenery_type_video);
			}
			c.drawBitmap(bMark, 
					new Rect(0, 0, bMark.getWidth(), bMark.getHeight()), 
					new Rect((bitmap.getWidth() - markSize)/2, 
							(bitmap.getHeight() - markSize)/2, 
							(bitmap.getWidth() + markSize)/2, 
							(bitmap.getHeight() + markSize)/2), 
					new Paint());
			return dest;
		}
		return null;
	}

	// ***************************** 图片保存 *******************************
	/**
	 * 保存图片
	 * 
	 * @param bm
	 * @param savePath
	 * @param format
	 * @param quality
	 * @return
	 */
	public static final boolean saveBitmap(Bitmap bm, String savePath,
			CompressFormat format, int quality) {
		BufferedOutputStream bos = null;
		try {
			File cf = new File(savePath);
			bos = new BufferedOutputStream(new FileOutputStream(cf));
			bm.compress(format, quality, bos);
			bos.flush();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("", "saveBitmap ===== " + e.toString());
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	// ***************************** 获取图片信息 *******************************
	/**
	 * 获取图片长宽等信息
	 * 
	 * @param stream
	 * @return
	 */
	public static BitmapFactory.Options decodeImageBounds(
			final InputStream stream) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(stream, null, options);
		if (options.outHeight > 0 && options.outWidth > 0) {
			return options;
		}
		return null;
	}

	static BitmapFactory.Options getDefaultOptions() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inDither = false;
		options.inJustDecodeBounds = false;
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inTempStorage = new byte[16 * 1024];
		return options;
	}

	// ***************************** 文件流 *******************************
	/**
	 * Return an {@link InputStream} from the given uri. ( can be a local
	 * content, a file path or an http url )
	 * 
	 * @param context
	 * @param uri
	 * @return the {@link InputStream} from the given uri, null if uri cannot be
	 *         opened
	 */
	public static InputStream openInputStream(Context context, Uri uri) {
		if (null == uri)
			return null;
		final String scheme = uri.getScheme();
		InputStream stream = null;
		if (scheme == null || ContentResolver.SCHEME_FILE.equals(scheme)) {
			// from file
			stream = openFileInputStream(uri.getPath());
		} else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
			// from content
			stream = openContentInputStream(context, uri);
		} else if ("http".equals(scheme) || "https".equals(scheme)) {
			// from remote uri
			stream = openRemoteInputStream(uri);
		}
		return stream;
	}

	/**
	 * Return a {@link FileInputStream} from the given path or null if file not
	 * found
	 * 
	 * @param path
	 *            the file path
	 * @return the {@link FileInputStream} of the given path, null if
	 *         {@link FileNotFoundException} is thrown
	 */
	static InputStream openFileInputStream(String path) {
		try {
			return new FileInputStream(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Return a {@link BufferedInputStream} from the given uri or null if an
	 * exception is thrown
	 * 
	 * @param context
	 * @param uri
	 * @return the {@link InputStream} of the given path. null if file is not
	 *         found
	 */
	static InputStream openContentInputStream(Context context, Uri uri) {
		try {
			return context.getContentResolver().openInputStream(uri);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Return an {@link InputStream} from the given url or null if failed to
	 * retrieve the content
	 * 
	 * @param uri
	 * @return
	 */
	public static InputStream openRemoteInputStream(Uri uri) {
		java.net.URL finalUrl;
		try {
			finalUrl = new java.net.URL(uri.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}

		HttpURLConnection connection;
		try {
			connection = (HttpURLConnection) finalUrl.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		connection.setInstanceFollowRedirects(false);
		int code;
		try {
			code = connection.getResponseCode();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		// permanent redirection
		if (code == HttpURLConnection.HTTP_MOVED_PERM
				|| code == HttpURLConnection.HTTP_MOVED_TEMP
				|| code == HttpURLConnection.HTTP_SEE_OTHER) {
			String newLocation = connection.getHeaderField("Location");
			return openRemoteInputStream(Uri.parse(newLocation));
		}

		try {
			return (InputStream) finalUrl.getContent();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	// ***************************** 转换 *******************************

	public static final Bitmap getFromDrawable(Drawable d) {
		if (d instanceof BitmapDrawable) {
			return ((BitmapDrawable) d).getBitmap();
		}
		return null;
	}
	
	/**
	 * 将Drawable转化为Bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable)
	{
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
				.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}

	public static final Drawable toDrawable(Context context, Bitmap b) {
		BitmapDrawable bitmapDrawable = new BitmapDrawable(
				context.getResources(), b);
		return (Drawable) bitmapDrawable;
	}
}
