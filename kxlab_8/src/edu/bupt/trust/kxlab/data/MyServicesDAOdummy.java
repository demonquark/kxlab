package edu.bupt.trust.kxlab.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;

import android.os.AsyncTask;
import android.os.Environment;

public class MyServicesDAOdummy extends ServicesDAOabstract {

	private File cacheDir;
	private int counter;

	public MyServicesDAOdummy(OnServicesRawDataReceivedListener listener){
		cacheDir = new File(Environment.getExternalStorageDirectory(), 
				Gegevens.FILE_USERDIRSD + Gegevens.FILE_SEPARATOR + Gegevens.FILE_CACHE);
		this.listener = listener;
		this.counter = 0;
	}

	@Override protected void readServices(final String path) {
		
		new AsyncTask<Void, Integer, Void>  (){
			@Override protected Void doInBackground(Void... params) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override protected void onPostExecute(Void v) {
				String response = null;
				if(path != null){
					//String filename = (path.contains(".")) ? path : path + ServicesDAOlocal.REPLACE_extension; 
					//response = readFromFile(filename);
					String s = "[" +
							"{\"serviceid\":107,\"servicetype\":1,\"servicetitle\":\"1\",\"servicedetail\":\"1\",\"servicecreatetime\":1385365825000,\"servicelastedittime\":1385365825000,\"credibilityScore\":\"0\",\"servicephoto\":null,\"useremail\":\"3\",\"servicestatus\":1}," +
									"{\"serviceid\":90,\"servicetype\":1,\"servicetitle\":\"1\",\"servicedetail\":\"前天，在《咱们结婚吧扭，但也算是个能得不对了\",\"servicecreatetime\":1385114472000,\"servicelastedittime\":1385114472000,\"credibilityScore\":\"0\",\"servicephoto\":null,\"useremail\":\"3\",\"servicestatus\":1}," +
									"{\"serviceid\":87,\"servicetype\":1,\"servicetitle\":\"1\",\"servicedetail\":\"前天，在《咱们结婚吧》里头听到妞儿说的话，“结婚就是两个人搭伙过日子，你要谈到什么爱不爱的，那就不对了，跑题了。”当时虽然觉得这话有点凄凉、有点别扭，但也算是个能让自己生活好过一些的箴言。 昨天，我看了韩寒主编的《很高兴见到你》里头的一篇文章，突然就觉得不对了，西式婚礼的誓词是什么，“我愿意爱他、安慰他、尊重他、保护他，像你爱自己一样。" +
									"不论他生病或是健康、富有或贫穷，始终忠於他，直到离开世界！”这才是婚姻的真谛啊，爱是排在第一位的啊。 所以，我将帖子发在这里，只是想提醒广大征友者以及应征者，相亲也是为了在千万人中找到那个彼此说出“我愿意”的人，千万不要让婚姻变了味道。 发牢骚完毕\",\"servicecreatetime\":1385049600000,\"servicelastedittime\":1385085600000,\"credibilityScore\":\"0\",\"servicephoto\":null,\"useremail\":\"3\",\"servicestatus\":1}," +
									"{\"serviceid\":14,\"servicetype\":1,\"servicetitle\":\"13\",\"servicedetail\":\"13\",\"servicecreatetime\":1383840000000,\"servicelastedittime\":1383879600000,\"credibilityScore\":\"0\",\"servicephoto\":null,\"useremail\":\"3\",\"servicestatus\":1}]";
					response = s;
				}
				
				listener.onReadServices(new RawResponse(response, path));
			}
	
		}.execute();
		
	}

	@Override protected void readService(String path) {
		
	}

	@Override protected void updateServiceScore(String path) {
		
	}

	@Override protected void createServiceComment(String path) {
		
	}
	
	public String randomPic(){
		String filename = "service";

		counter ++;
		int i = counter % 8;
		filename += (i < 3) ? i + ".jpg" : (i == 4) ? "0.png" : (i == 6) ? "0.gif" : ".dat";
		
		return new File(cacheDir, filename).getAbsolutePath();
	}
	
	public String readFromFile(String filename) {
        File file = new File(cacheDir, filename);
        byte[] b  = new byte[(int)file.length()];
		int len = b.length;
        Loggen.v(this, "Reading from file: " + file.getName());

		InputStream in = null;
		try{
			in = new FileInputStream(file);
			int total = 0;
			while (total < len) {
				int result = in.read(b, total, len - total);
				if (result == -1) { break; }
				total += result;
			}
		} catch (IOException e) { e.printStackTrace(); 
		} finally {
			if (in != null) { 
				try { in.close(); } catch (IOException e) { e.printStackTrace(); } 
			} 
		}
		
		return Charset.forName("UTF-8").decode(ByteBuffer.wrap(b)).toString();
		
	}

	@Override
	protected void searchService(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void editService(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createService(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void deleteService(String path) {
		// TODO Auto-generated method stub
		
	}


}
