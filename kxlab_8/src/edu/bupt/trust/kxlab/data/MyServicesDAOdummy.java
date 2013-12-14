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
									"{\"serviceid\":90,\"servicetype\":1,\"servicetitle\":\"1\",\"servicedetail\":\"ǰ�죬�ڡ����ǽ���Ť����Ҳ���Ǹ��ܵò�����\",\"servicecreatetime\":1385114472000,\"servicelastedittime\":1385114472000,\"credibilityScore\":\"0\",\"servicephoto\":null,\"useremail\":\"3\",\"servicestatus\":1}," +
									"{\"serviceid\":87,\"servicetype\":1,\"servicetitle\":\"1\",\"servicedetail\":\"ǰ�죬�ڡ����ǽ��ɡ���ͷ����椶�˵�Ļ����������������˴������ӣ���Ҫ̸��ʲô�������ģ��ǾͲ����ˣ������ˡ�����ʱ��Ȼ�����⻰�е��������е��Ť����Ҳ���Ǹ������Լ�����ù�һЩ�����ԡ� ���죬�ҿ��˺�������ġ��ܸ��˼����㡷��ͷ��һƪ���£�ͻȻ�;��ò����ˣ���ʽ������Ĵ���ʲô������Ը�Ⱞ������ο�����������������������㰮�Լ�һ����" +
									"�������������ǽ��������л�ƶ�ʼ���������ֱ���뿪���磡������ǻ��������а����������ڵ�һλ�İ��� ���ԣ��ҽ����ӷ������ֻ�������ѹ���������Լ�Ӧ���ߣ�����Ҳ��Ϊ����ǧ�������ҵ��Ǹ��˴�˵������Ը�⡱���ˣ�ǧ��Ҫ�û�������ζ���� ����ɧ���\",\"servicecreatetime\":1385049600000,\"servicelastedittime\":1385085600000,\"credibilityScore\":\"0\",\"servicephoto\":null,\"useremail\":\"3\",\"servicestatus\":1}," +
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
