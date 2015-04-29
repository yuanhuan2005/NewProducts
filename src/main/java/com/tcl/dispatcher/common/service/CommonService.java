package com.tcl.dispatcher.common.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import com.amazonaws.services.sqs.model.Message;
import com.tcl.dispatcher.common.domain.AudioVideoInfo;
import com.tcl.dispatcher.common.domain.CommandExecutedResult;
import com.tcl.dispatcher.common.domain.OSType;
import com.tcl.dispatcher.listener.domain.ServiceType;
import com.tcl.dispatcher.test.RkitHttpResponse;

public class CommonService
{
	final static private Log DEBUGGER = LogFactory.getLog(CommonService.class);

	final static private int TIME_OUT = 30000;

	// ��Ϣ����ӳ���ϵ
	private static Map<String, Integer> serviceTypeMap = new HashMap<String, Integer>();

	private static String outputMsg = "";

	private static String errorMsg = "";

	/**
	 * ��ʼ����Ϣ����ӳ���ϵ
	 */
	private static Map<String, Integer> getServiceTypeMap()
	{
		if (null == CommonService.serviceTypeMap)
		{
			CommonService.serviceTypeMap = new HashMap<String, Integer>();
		}
		CommonService.serviceTypeMap.clear();

		CommonService.serviceTypeMap.put("transcoding", ServiceType.TRANSCODING);
		CommonService.serviceTypeMap.put("clipping", ServiceType.CLIPPING);
		CommonService.serviceTypeMap.put("objectDetection", ServiceType.OBJECT_DETECTION);
		CommonService.serviceTypeMap.put("script", ServiceType.SCRIPT);
		CommonService.serviceTypeMap.put("faceDetection", ServiceType.FACE_DETECTION);
		CommonService.serviceTypeMap.put("faceRecognition", ServiceType.FACE_RECOGNITION);
		CommonService.serviceTypeMap.put("snapshots", ServiceType.SNAPSHOTS);
		CommonService.serviceTypeMap.put("insertion", ServiceType.INSERTION);
		CommonService.serviceTypeMap.put("logo", ServiceType.LOGO);
		CommonService.serviceTypeMap.put("pictureBook", ServiceType.PICTURE_BOOK);
		return CommonService.serviceTypeMap;
	}

	/**
	 * �������������ƻ�ȡ��Ӧ��ֵ
	 * 
	 * @param propertitesFile
	 *            property file
	 * @param confKey
	 *            ����������
	 * @return confValue ������ֵ
	 */
	public static String getBaseConfValue(String propertitesFile, String confKey)
	{
		String confValue = "";

		InputStream inputStream = new CommonService().getClass().getClassLoader().getResourceAsStream(propertitesFile);
		Properties p = new Properties();

		try
		{
			p.load(inputStream);
			confValue = p.getProperty(confKey);
		}
		catch (Exception e)
		{
			confValue = "";
		}
		finally
		{
			try
			{
				if (null != inputStream)
				{
					inputStream.close();
				}
			}
			catch (IOException e)
			{
				confValue = "";
			}
		}

		return confValue;
	}

	/**
	 * ��ͨJAVA��ȡ WEB��Ŀ�µ�WEB-INFĿ¼·��
	 * 
	 * @return WEB-INFĿ¼·��
	 */
	public static String getWebInfPath()
	{
		URL url = new CommonService().getClass().getProtectionDomain().getCodeSource().getLocation();
		String path = url.toString();
		int index = path.indexOf("WEB-INF");

		if (index == -1)
		{
			index = path.indexOf("classes");
		}

		if (index == -1)
		{
			index = path.indexOf("bin");
		}

		path = path.substring(0, index);

		if (path.startsWith("zip"))
		{
			// ��class�ļ���war��ʱ����ʱ����zip:D:/...������·��
			path = path.substring(4);
		}
		else if (path.startsWith("file"))
		{
			// ��class�ļ���class�ļ���ʱ����ʱ����file:/D:/...������·��
			path = path.substring(6);
		}
		else if (path.startsWith("jar"))
		{
			// ��class�ļ���jar�ļ�����ʱ����ʱ����jar:file:/D:/...������·��
			path = path.substring(10);
		}
		try
		{
			path = URLDecoder.decode(path, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			CommonService.DEBUGGER.error("Exception: " + e.toString());
		}

		return path;
	}

	/**
	 * �������������ƻ�ȡ��Ӧ��ֵ
	 * 
	 * @param confKey
	 *            ����������
	 * @return confValue ������ֵ
	 */
	public static String getDispatcherConfValue(String confKey)
	{
		return CommonService.getBaseConfValue("dispatcher.propertites", confKey);
	}

	/**
	 * ִ�б���Windows����Linux�������ȡ����ֵ��ִ�н����Ϣ
	 * 
	 * @param cmd
	 *            ��ִ�е�����
	 * @return �����ֵ
	 */
	public static CommandExecutedResult execLocalCommand(String cmd)
	{
		CommandExecutedResult commandExecutedResult = new CommandExecutedResult();
		int exitValue = -1;
		String resultMessage = "";
		commandExecutedResult.setExitValue(exitValue);
		commandExecutedResult.setResultMessage(resultMessage);
		Process process = null;

		try
		{
			process = Runtime.getRuntime().exec(cmd);

			// ��ȡCPU�ĺ���������ǵ���CPU�Ļ����Ͳ��������߳�ȥ�������ʹ������ˣ�ֻ��
			if (1 == Runtime.getRuntime().availableProcessors())
			{
				InputStream stderrStream = process.getErrorStream();
				InputStream stdoutStream = process.getInputStream();
				InputStreamReader errStreamReader = new InputStreamReader(stderrStream);
				InputStreamReader outStreamReader = new InputStreamReader(stdoutStream);
				BufferedReader errBufReader = new BufferedReader(errStreamReader);
				BufferedReader outBufReader = new BufferedReader(outStreamReader);
				String errLine = null;
				String outLine = null;
				while (null != (errLine = errBufReader.readLine()) || null != (outLine = outBufReader.readLine()))
				{
					if (null != errLine && !"".equals(errLine))
					{
						CommonService.DEBUGGER.debug(errLine);
						CommonService.errorMsg += errLine;
					}
					if (null != outLine && !"".equals(outLine))
					{
						CommonService.DEBUGGER.debug(outLine);
						CommonService.outputMsg += outLine;
					}
				}
			}
			else
			{
				// ��ȡ���̵ı�׼������
				final InputStream inputStream = process.getInputStream();

				// ��ȡ���̵Ĵ�����
				final InputStream errorStream = process.getErrorStream();

				// �����������ؽ���
				Thread outputStreamThread = new Thread()
				{
					@Override
					public void run()
					{
						BufferedReader br1 = new BufferedReader(new InputStreamReader(inputStream));
						try
						{
							String line1 = null;
							CommonService.outputMsg = "";
							while ((line1 = br1.readLine()) != null)
							{
								if (line1 != null)
								{
									CommonService.outputMsg += line1;
									CommonService.DEBUGGER.debug(line1);
								}
							}
						}
						catch (IOException e)
						{
							CommonService.DEBUGGER.error("Exception: " + e.toString());
						}
						finally
						{
							try
							{
								inputStream.close();
							}
							catch (IOException e)
							{
								CommonService.DEBUGGER.error("Exception: " + e.toString());
							}
						}
					}
				};
				outputStreamThread.start();

				// ������������ؽ���
				Thread errorStreamThread = new Thread()
				{
					@Override
					public void run()
					{
						BufferedReader br2 = new BufferedReader(new InputStreamReader(errorStream));
						try
						{
							String line2 = null;
							CommonService.errorMsg = "";
							while ((line2 = br2.readLine()) != null)
							{
								if (line2 != null)
								{
									CommonService.errorMsg += line2;
									CommonService.DEBUGGER.debug(line2);
								}
							}
						}
						catch (IOException e)
						{
							CommonService.DEBUGGER.error("Exception: " + e.toString());
						}
						finally
						{
							try
							{
								errorStream.close();
							}
							catch (IOException e)
							{
								CommonService.DEBUGGER.error("Exception: " + e.toString());
							}
						}
					}
				};
				errorStreamThread.start();

				// �ȴ�������ʹ�������ؽ��̽���
				while (outputStreamThread.isAlive() || errorStreamThread.isAlive())
				{
					// ֻ�ǵȴ���������������
					;
				}
			}

			// ��ȡ����ֵ
			exitValue = process.waitFor();
			if (0 == exitValue)
			{
				resultMessage = CommonService.outputMsg;
			}
			else
			{
				resultMessage = CommonService.errorMsg;
			}
		}
		catch (Exception e)
		{
			CommonService.DEBUGGER.error("Exception: " + e.toString());
		}
		finally
		{
			if (null != process)
			{
				process.destroy();
			}
		}

		commandExecutedResult.setExitValue(exitValue);
		commandExecutedResult.setResultMessage(resultMessage);
		return commandExecutedResult;
	}

	/**
	 * ��ȡ����ϵͳ����
	 * 
	 * @return ����ϵͳ����: 0��ʾwindows, 1��ʾLinux
	 */
	public static int getOperatingSystemType()
	{
		int osType = 0;
		String osName = System.getProperty("os.name");
		if (osName.toLowerCase().indexOf("windows") < 0)
		{
			osType = 1;
		}

		return osType;
	}

	/**
	 * �ж��ַ����Ƿ�Ϊ��
	 * 
	 * @param str
	 *            �ַ���
	 * @return true��ʾΪ�գ�false��ʾ�ǿ�
	 */
	public static boolean isStringNull(String str)
	{
		boolean result = false;

		if (null == str || "".equals(str))
		{
			return true;
		}

		return result;
	}

	/**
	 * �ж��ַ����Ƿ�Ϊ��
	 * 
	 * @param str
	 *            �ַ���
	 * @return true��ʾ��Ϊ�գ�false��ʾΪ��
	 */
	public static boolean isStringNotNull(String str)
	{
		return !CommonService.isStringNull(str);
	}

	/**
	 * �ж��ļ��Ƿ����
	 * 
	 * @param filePath
	 *            �ļ�·��
	 * @return true��ʾ���ڣ�false��ʾ������
	 */
	public static boolean isFileExisted(String filePath)
	{
		try
		{
			File file = new File(filePath);
			if (!file.exists())
			{
				return false;
			}
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}

	/**
	 * �ж�Ŀ¼�Ƿ��д
	 * 
	 * @param Ŀ¼
	 * @return true��ʾ��д��false��ʾ����д
	 */
	public static boolean isDirWritable(String dir)
	{
		File file = new File(dir);
		if (!file.exists())
		{
			CommonService.DEBUGGER.error(dir + " not found");
			return false;
		}

		if (!file.isDirectory())
		{
			CommonService.DEBUGGER.error(dir + " not a directory");
			return false;
		}

		if (!file.canWrite())
		{
			CommonService.DEBUGGER.error(dir + " cat not write");
			return false;
		}

		return true;
	}

	/**
	 * ɾ���ļ�
	 * 
	 * @param filePath
	 * @return true��ʾɾ���ɹ���false��ʾɾ��ʧ��
	 */
	public static boolean deleteFile(String filePath)
	{
		try
		{
			File file = new File(filePath);
			if (!file.exists())
			{
				return true;
			}

			return file.delete();
		}
		catch (Exception e)
		{
			CommonService.DEBUGGER.error("Exception: " + e.toString());
		}

		return false;
	}

	/**
	 * �����������Ƶ�ļ�·����ȡ��Ƶ�ļ�������Ƶ��Ϣ
	 * 
	 * @param inputVideoPath
	 *            �������Ƶ�ļ�·��
	 * @return ��Ƶ�ļ�������Ƶ��Ϣ
	 */
	public static AudioVideoInfo getInputAudioVideoInfo(String inputVideoPath)
	{
		AudioVideoInfo audioVideoInfo = null;
		CommonService.DEBUGGER.debug("inputVideoPath = " + inputVideoPath);

		// �������
		if (CommonService.isStringNull(inputVideoPath))
		{
			CommonService.DEBUGGER.error("inputVideoPath is null");
			return audioVideoInfo;
		}

		// ��ȡ�ļ�����
		String format = CommonService.getFileFormat(inputVideoPath);

		// ִ������
		String cmd = "ffprobe -v error -print_format json -show_streams " + inputVideoPath;
		CommonService.DEBUGGER.debug("ffprobe cmd = " + cmd);
		CommandExecutedResult commandExecutedResult = CommonService.execLocalCommand(cmd);
		if (null == commandExecutedResult || 0 != commandExecutedResult.getExitValue())
		{
			CommonService.DEBUGGER.error("failed to execute ffprobe cmd");
			return audioVideoInfo;
		}

		// ���������ת��ΪJava����
		String resultMessage = commandExecutedResult.getResultMessage();
		try
		{
			JSONObject resultMessageJsonObj = new JSONObject(resultMessage);
			JSONArray streamsJsonArr = (JSONArray) resultMessageJsonObj.get("streams");

			audioVideoInfo = new AudioVideoInfo();
			audioVideoInfo.setFileFormat(format);

			for (int i = 0; i < streamsJsonArr.length(); i++)
			{
				JSONObject streamsJsonObj = (JSONObject) streamsJsonArr.get(i);
				String codecType = streamsJsonObj.getString("codec_type");

				if ("video".equals(codecType))
				{
					audioVideoInfo.setVideoFrameSize(streamsJsonObj.getString("width") + "x"
					        + streamsJsonObj.getString("height"));
					audioVideoInfo.setVideoBitrate(Double.valueOf(streamsJsonObj.getString("bit_rate")) / 1024);

					float duration = Float.valueOf(streamsJsonObj.getString("duration"));
					float framesNum = Float.valueOf(streamsJsonObj.getString("nb_frames"));
					float tmpFramesFloat = framesNum / duration;
					BigDecimal bigDecimal = new BigDecimal(tmpFramesFloat);
					float framesFloat = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					Double frameRate = Double.valueOf(framesFloat);
					audioVideoInfo.setVideoFrameRate(frameRate);
				}

				if ("audio".equals(codecType))
				{
					audioVideoInfo.setAudioBitrate(Double.valueOf(streamsJsonObj.getString("bit_rate")) / 1024);
					audioVideoInfo.setAudioSamplingRate(streamsJsonObj.getDouble("sample_rate"));
				}
			}

		}
		catch (Exception e)
		{
			CommonService.DEBUGGER.error("Exception: " + e.toString());
		}

		return audioVideoInfo;
	}

	/**
	 * ��������ļ���ŵ�Ŀ¼·��
	 * 
	 * @param clipMessageReq
	 *            ����
	 * @return ����ļ���ŵ�Ŀ¼·��
	 */
	public static String getLocalTempOutputDir()
	{
		String localTempOutputPath = "";

		String defaultOutputFilePathInWindows = "C:/";
		String defaultOutputFilePathInLinux = "/tmp/";

		int osType = CommonService.getOperatingSystemType();
		switch (osType)
		{
			case OSType.WINDOWS:
				String tmpOutputFilePathInWindows = CommonService.getDispatcherConfValue("tmpOutputFilePathInWindows");
				if (!CommonService.isDirWritable(tmpOutputFilePathInWindows))
				{
					CommonService.DEBUGGER.error(tmpOutputFilePathInWindows
					        + " is not writable, change to default dir:" + defaultOutputFilePathInWindows);
					tmpOutputFilePathInWindows = defaultOutputFilePathInWindows;
					if (!CommonService.isDirWritable(tmpOutputFilePathInWindows))
					{
						CommonService.DEBUGGER.error("default dir " + defaultOutputFilePathInWindows
						        + " is not writable too, please check your settings");
						return null;
					}
				}

				if (!tmpOutputFilePathInWindows.endsWith("/"))
				{
					tmpOutputFilePathInWindows += "/";
				}
				localTempOutputPath = tmpOutputFilePathInWindows;
				break;
			case OSType.LINUX:

				String tmpOutputFilePathInLinux = CommonService.getDispatcherConfValue("tmpOutputFilePathInLinux");
				if (!CommonService.isDirWritable(tmpOutputFilePathInLinux))
				{
					CommonService.DEBUGGER.error(tmpOutputFilePathInLinux + " is not writable, change to default dir:"
					        + defaultOutputFilePathInLinux);
					tmpOutputFilePathInLinux = defaultOutputFilePathInLinux;
					if (!CommonService.isDirWritable(tmpOutputFilePathInLinux))
					{
						CommonService.DEBUGGER.error("default dir " + defaultOutputFilePathInLinux
						        + " is not writable too, please check your settings");
						return null;
					}
				}

				if (!tmpOutputFilePathInLinux.endsWith("/"))
				{
					tmpOutputFilePathInLinux += "/";
				}
				localTempOutputPath = tmpOutputFilePathInLinux;
				break;
			default:
				return null;
		}

		CommonService.DEBUGGER.debug("localTempOutputDir = " + localTempOutputPath);
		return localTempOutputPath;
	}

	/**
	 * ��������ļ�·��
	 * 
	 * @param clipMessageReq
	 *            ����
	 * @return ����ļ�·��
	 */
	public static String genLocalTempOutputPath(final String outputPath)
	{
		if (CommonService.isStringNull(outputPath))
		{
			return null;
		}

		String localTempOutputPath = CommonService.getLocalTempOutputDir();
		String outputFileName = outputPath.substring(outputPath.lastIndexOf("/") + 1);

		localTempOutputPath = localTempOutputPath + outputFileName;

		CommonService.DEBUGGER.debug("localTempOutputPath = " + localTempOutputPath);
		return localTempOutputPath;
	}

	/**
	 * ����Ϣ�����н�����Ϣ����
	 * 
	 * @param message
	 *            ��Ϣ����
	 * @return ��Ϣ����
	 */
	public static String parseServiceType(Message message)
	{
		String serviceType = null;
		try
		{
			JSONObject messageJson = new JSONObject(message.getBody());
			serviceType = messageJson.getString("serviceType");
		}
		catch (Exception e)
		{
			CommonService.DEBUGGER.error("Exception : " + e.toString());
		}
		return serviceType;
	}

	/**
	 * ����Ϣ���ʹ�Stringת��Ϊ��Ӧ��Int��ֵ
	 * 
	 * @param serviceType
	 *            ��Ϣ����
	 * @return ��Ϣ���Ͷ�Ӧ��Int��ֵ
	 */
	public static int convertServiceTypeToInt(String serviceType)
	{
		int serviceTypeToInt = -1;

		try
		{
			serviceTypeToInt = CommonService.getServiceTypeMap().get(serviceType);
		}
		catch (Exception e)
		{
			CommonService.DEBUGGER.error("Exception: " + e.toString());
		}

		return serviceTypeToInt;
	}

	/**
	 * ����Ϣ�����н�����Ϣ���͵�int��ֵ
	 * 
	 * @param message
	 *            ��Ϣ����
	 * @return ��Ϣ���͵�int��ֵ
	 */
	public static int parseServiceTypeToInt(Message message)
	{
		return CommonService.convertServiceTypeToInt(CommonService.parseServiceType(message));
	}

	/**
	 * ��ȡ��Ƶ�������������ı��ʣ� output / input
	 * 
	 * @param outputVideoFrameSize
	 * @param inputVideoFrameSize
	 * @return
	 */
	public static float getVideoParamRatio(String inputVideoFrameSize, String outputVideoFrameSize)
	{
		float result = 1;

		try
		{
			float inputWidth = Float.valueOf(inputVideoFrameSize.split("x")[0]);
			float inputHeight = Float.valueOf(inputVideoFrameSize.split("x")[1]);
			float outputWidth = Float.valueOf(outputVideoFrameSize.split("x")[0]);
			float outputHeight = Float.valueOf(outputVideoFrameSize.split("x")[1]);

			result = outputHeight * outputWidth / inputWidth / inputHeight;

		}
		catch (Exception e)
		{
			CommonService.DEBUGGER.error("Exception: " + e.toString());
		}

		return result;
	}

	/**
	 * ��Ŀ¼�ĺ�����ϱ�Ҫ��б��
	 * 
	 * @param dirPath
	 *            Ŀ¼·��
	 * @return ����б�ܵ�Ŀ¼·��
	 */
	public static String addSlashToDirPathIfNecessary(String dirPath)
	{
		if (CommonService.isStringNull(dirPath))
		{
			return dirPath;
		}

		String finalDirPath = dirPath;
		if (!dirPath.endsWith("/"))
		{
			finalDirPath = dirPath + "/";
		}

		return finalDirPath;
	}

	/**
	 * д���ļ�
	 * 
	 * @param filePath
	 *            �ļ�·��
	 * @param fileContent
	 *            �ļ�����
	 * @return true��ʾд��ɹ���false��ʾʧ��
	 */
	public static boolean writeFile(String filePath, String fileContent)
	{

		FileWriter fw = null;
		try
		{
			fw = new FileWriter(filePath, true);
			PrintWriter pw = new PrintWriter(fw);
			pw.println(fileContent);
			pw.close();
			fw.close();
			return true;
		}
		catch (IOException e)
		{
			CommonService.DEBUGGER.error("Exception : " + e.toString());
		}

		return false;
	}

	/**
	 * ���ݷֱ��ʵĿ���߸ߵ�float���͵���ֵ����ȡ��int������ֵ
	 * 
	 * @param widthHeightFloat
	 *            float���͵���ֵ
	 * @return widthHeightInt int������ֵ
	 */
	public static int getWidthHeightInt(float widthHeightFloat)
	{
		int widthHeightInt = 0;
		int newWidthHeightInt = 0;
		int origWidthHeightInt = Integer.valueOf(String.valueOf(widthHeightFloat).substring(0,
		        String.valueOf(widthHeightFloat).indexOf(".")));

		BigDecimal bigDecimal = new BigDecimal(widthHeightFloat);
		float finalValue = bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP).floatValue();

		widthHeightInt = Integer.valueOf(String.valueOf(finalValue).substring(0,
		        String.valueOf(finalValue).indexOf(".")));

		// ffmepgת���ʱ���Ⱥ͸߶ȱ�����ż�����˴���һ�´������»�������ȡ��ӽ����Ǹ�ż��
		if (widthHeightInt % 2 != 0)
		{
			newWidthHeightInt = widthHeightInt + 1;
			if (widthHeightFloat - Float.valueOf(origWidthHeightInt) < Float.valueOf(newWidthHeightInt)
			        - widthHeightFloat
			        && origWidthHeightInt % 2 == 0)
			{
				widthHeightInt = origWidthHeightInt;
			}
			else
			{
				widthHeightInt = newWidthHeightInt;
			}
		}

		return widthHeightInt;
	}

	/**
	 * �����·ֱ���
	 * 
	 * @param frameSize
	 *            �ֱ��ʣ�ȡֵ��w640��h320��1280x720��copy
	 * @param origWidthInt
	 *            ԭʼ�ֱ��ʵĿ��
	 * @param origHeightInt
	 *            ԭʼ�ֱ��ʵĸ߶�
	 * @return
	 */
	public static String calcNewFrameSize(String frameSize, int origWidthInt, int origHeightInt)
	{
		int outputWidthInt;
		int outputHeightInt;
		String newFrameSize = null;
		if (CommonService.isStringNull(frameSize) || "copy".equals(frameSize))
		{
			newFrameSize = origWidthInt + "x" + origHeightInt;
		}
		else if (frameSize.startsWith("w"))
		{
			// ��ȹ̶����߶Ȱ���������
			outputWidthInt = Integer.valueOf(frameSize.substring(1));
			float outputHeightFloat = Float.valueOf(outputWidthInt) * Float.valueOf(origHeightInt)
			        / Float.valueOf(origWidthInt);
			outputHeightInt = CommonService.getWidthHeightInt(outputHeightFloat);
			newFrameSize = outputWidthInt + "x" + outputHeightInt;
		}
		else if (frameSize.startsWith("h"))
		{
			// �߶ȹ̶�����Ȱ���������
			outputHeightInt = Integer.valueOf(frameSize.substring(1));
			float outputWidthFloat = Float.valueOf(outputHeightInt) * Float.valueOf(origWidthInt)
			        / Float.valueOf(origHeightInt);
			outputWidthInt = CommonService.getWidthHeightInt(outputWidthFloat);
			newFrameSize = outputWidthInt + "x" + outputHeightInt;
		}
		else if (frameSize.indexOf("x") > 1)
		{
			// �̶�����Ƶ�ߴ�
			newFrameSize = frameSize;
		}

		return newFrameSize;
	}

	/**
	 * �����������Ƶ�ļ�·����ȡ��Ƶ�ļ�������Ƶ��Ϣ
	 * 
	 * @param inputVideoPath
	 *            �������Ƶ�ļ�·��
	 * @return ��Ƶ�ļ�������Ƶ��Ϣ
	 */
	public static String getImageFrameSize(String inputImagePath)
	{
		String imageFrameSize = null;

		// �������
		if (CommonService.isStringNull(inputImagePath))
		{
			CommonService.DEBUGGER.error("inputImagePath is null");
			return imageFrameSize;
		}

		// ִ������
		String cmd = "ffprobe -v error -print_format json -show_streams " + inputImagePath;
		CommandExecutedResult commandExecutedResult = CommonService.execLocalCommand(cmd);
		if (null == commandExecutedResult || 0 != commandExecutedResult.getExitValue())
		{
			CommonService.DEBUGGER.error("failed to execute ffprobe cmd");
			return imageFrameSize;
		}

		// ���������ת��ΪJava����
		String resultMessage = commandExecutedResult.getResultMessage();
		try
		{
			JSONObject resultMessageJsonObj = new JSONObject(resultMessage);
			JSONArray streamsJsonArr = (JSONArray) resultMessageJsonObj.get("streams");

			for (int i = 0; i < streamsJsonArr.length(); i++)
			{
				JSONObject streamsJsonObj = (JSONObject) streamsJsonArr.get(i);
				String codecType = streamsJsonObj.getString("codec_type");
				if ("video".equals(codecType))
				{
					imageFrameSize = streamsJsonObj.getString("width") + "x" + streamsJsonObj.getString("height");
				}
			}

		}
		catch (Exception e)
		{
			CommonService.DEBUGGER.error("Exception: " + e.toString());
		}

		return imageFrameSize;
	}

	/**
	 * ��ȡ�ļ�����
	 * 
	 * @param filePath
	 *            �ļ�·��
	 * @return �ļ�����
	 */
	public static String getFileFormat(String filePath)
	{
		String fileFormat = "";

		// �����ǿռ��
		if (CommonService.isStringNull(filePath))
		{
			CommonService.DEBUGGER.error("filePath is null");
			return fileFormat;
		}

		// ����ļ��Ƿ�������
		if (filePath.indexOf(".") < 0)
		{
			CommonService.DEBUGGER.error("failed to get format from filePath");
			return fileFormat;
		}

		fileFormat = filePath.substring(filePath.lastIndexOf(".") + 1);

		return fileFormat;
	}

	/**
	 * �������ļ����������µ��ļ�·��
	 * 
	 * @param filePath
	 *            �ļ�·�������磺/tmp/test.mp4
	 * @param newFormat
	 *            �����ͣ����磺flv
	 * @return ���ļ�·�������磺/tmp/test.flv
	 */
	public static String genFilePathWithNewFormat(String filePath, String newFormat)
	{
		String newFilePath = "";

		// �����ǿռ��
		if (CommonService.isStringNull(filePath))
		{
			CommonService.DEBUGGER.error("filePath is null");
			return newFilePath;
		}

		// �����ǿռ��
		if (CommonService.isStringNull(filePath))
		{
			CommonService.DEBUGGER.error("filePath is null");
			newFilePath = filePath;
			return newFilePath;
		}

		// ����ļ��Ƿ�������
		if (filePath.indexOf(".") < 0)
		{
			newFilePath = filePath + "." + newFormat;
			return newFilePath;
		}

		String filePathWithoutFormat = filePath.substring(0, filePath.lastIndexOf(".") + 1);
		newFilePath = filePathWithoutFormat + newFormat;

		return newFilePath;
	}

	/**
	 * ɾ��Ŀ¼
	 * 
	 * @param dirPath
	 *            Ŀ¼·��
	 */
	public static void deleteFolder(String dirPath)
	{
		if (CommonService.isStringNull(dirPath))
		{
			return;
		}

		try
		{
			File folder = new File(dirPath);
			File[] files = folder.listFiles();
			if (null != files)
			{
				for (File f : files)
				{
					if (f.isDirectory())
					{
						CommonService.deleteFolder(f.getAbsolutePath());
					}
					else
					{
						f.delete();
					}
				}
			}
			folder.delete();
		}
		catch (Exception e)
		{
			CommonService.DEBUGGER.error("Exception: " + e.toString());
		}
	}

	public static void copyFile(String oldPath, String newPath)
	{
		InputStream inStream = null;
		FileOutputStream fs = null;

		try
		{
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists())
			{
				// �ļ�����ʱ
				inStream = new FileInputStream(oldPath); // ����ԭ�ļ�
				fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1)
				{
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				fs.close();
			}
		}
		catch (Exception e)
		{
			System.out.println("���Ƶ����ļ���������");
			e.printStackTrace();
		}
	}

	public static void delFile(String filePathAndName)
	{
		try
		{
			String filePath = filePathAndName;
			filePath = filePath.toString();
			java.io.File myDelFile = new java.io.File(filePath);
			myDelFile.delete();

		}
		catch (Exception e)
		{
			CommonService.DEBUGGER.error("Exception: " + e.toString());
		}
	}

	public static void moveFile(String oldPath, String newPath)
	{
		CommonService.copyFile(oldPath, newPath);
		CommonService.delFile(oldPath);
	}

	public static void newFolder(String folderPath)
	{
		try
		{
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			if (!myFilePath.exists())
			{
				myFilePath.mkdir();
			}
		}
		catch (Exception e)
		{
			CommonService.DEBUGGER.error("Exception: " + e.toString());
		}
	}

	public static void renameFile(String oldPath, String newPath)
	{
		try
		{
			File dest = null;
			if (!oldPath.equals(newPath))
			{
				File file = new File(oldPath);
				dest = new File(newPath);
				file.renameTo(dest);
			}
		}
		catch (Exception e)
		{
			CommonService.DEBUGGER.error("Exception: " + e.toString());
		}
	}

	/**
	 * ��ȡ������Ƶ�ļ�����Ϣ��json��ʽ
	 * 
	 * @param inputVideoPath
	 *            ������Ƶ�ļ�·��
	 * @return json��ʽ����Ƶ��Ϣ
	 */
	public static String getInputVideoInfoInJson(String inputVideoPath)
	{
		String resultMessage = "";
		System.out.println("inputVideoPath = " + inputVideoPath);

		if (CommonService.isStringNull(inputVideoPath))
		{
			System.out.println("inputVideoPath is null");
			return resultMessage;
		}

		String cmd = "ffprobe -v error -print_format json -show_streams " + inputVideoPath;
		System.out.println("ffprobe cmd = " + cmd);
		CommandExecutedResult commandExecutedResult = CommonService.execLocalCommand(cmd);
		if (null == commandExecutedResult || 0 != commandExecutedResult.getExitValue())
		{
			System.out.println("failed to execute ffprobe cmd");
			return resultMessage;
		}

		resultMessage = commandExecutedResult.getResultMessage();
		return resultMessage;
	}

	/**
	 * ��ȡ������Ƶ��֡�ߴ�
	 * 
	 * @param inputVideoPath
	 *            ������Ƶ�ļ�·��
	 * @return ��Ƶ֡�ߴ�
	 */
	public static String getInputVideoFrameSize(String inputVideoPath)
	{
		String videoFrameSize = "";
		String inputVideoInfoInJson = CommonService.getInputVideoInfoInJson(inputVideoPath);
		if (CommonService.isStringNull(inputVideoInfoInJson))
		{
			return videoFrameSize;
		}

		if (inputVideoInfoInJson.indexOf("width") < 0 || inputVideoInfoInJson.indexOf("height") < 0)
		{
			return videoFrameSize;
		}

		try
		{
			JSONObject resultMessageJsonObj = new JSONObject(inputVideoInfoInJson);
			JSONArray streamsJsonArr = (JSONArray) resultMessageJsonObj.get("streams");

			for (int i = 0; i < streamsJsonArr.length(); i++)
			{
				JSONObject streamsJsonObj = (JSONObject) streamsJsonArr.get(i);
				String codecType = streamsJsonObj.getString("codec_type");

				if ("video".equals(codecType))
				{
					videoFrameSize = streamsJsonObj.getString("width") + "x" + streamsJsonObj.getString("height");
					break;
				}

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			String widthString = inputVideoInfoInJson.substring(inputVideoInfoInJson.indexOf("width"));
			widthString = widthString.substring(widthString.indexOf(":") + 1, widthString.indexOf(",")).trim();
			String heightString = inputVideoInfoInJson.substring(inputVideoInfoInJson.indexOf("height"));
			heightString = heightString.substring(heightString.indexOf(":") + 1, heightString.indexOf(",")).trim();
			videoFrameSize = widthString + "x" + heightString;
		}

		return videoFrameSize;
	}

	/**
	 * ��������ļ���֡�ߴ�
	 * 
	 * @param reqAudioVideoInfo
	 *            ������Ƶ��Ϣ
	 * @param inputAudioVideoInfo
	 *            ������Ƶ��Ϣ
	 * @return ����ļ���֡�ߴ�
	 */
	public static String genOutputFrameSize(String reqVideoFrameSize, String inputVideoFrameSize)
	{
		if (CommonService.isStringNull(reqVideoFrameSize) || CommonService.isStringNull(inputVideoFrameSize))
		{
			return null;
		}

		int inputWidthInt = Integer.valueOf(inputVideoFrameSize.split("x")[0]);
		int inputHeightInt = Integer.valueOf(inputVideoFrameSize.split("x")[1]);
		String videoFrameSize = CommonService.calcNewFrameSize(reqVideoFrameSize, inputWidthInt, inputHeightInt);
		return videoFrameSize;
	}

	/**
	 * ����һ��HTTP��GET��POST����
	 * 
	 * @param httpUrl
	 *            http URL
	 * @param postData
	 *            POST����Ĳ�����GET����ʱ���ֶ�Ϊ��
	 * @return ��Ӧ��Ϣ
	 */
	@SuppressWarnings("unused")
	private static RkitHttpResponse sendHttpGetPostRequest(String httpUrl, String postData)
	{
		RkitHttpResponse rkitHttpResponse = new RkitHttpResponse();
		int httpStatusCode = 500;
		String responseMessage = "Failed to get response. ";
		rkitHttpResponse.setHttpStatusCode(httpStatusCode);
		rkitHttpResponse.setResponseMessage(responseMessage);

		try
		{
			// Send the request
			URL url = new URL(httpUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setReadTimeout(CommonService.TIME_OUT);
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

			// write POST parameters
			if (CommonService.isStringNotNull(postData))
			{
				writer.write(postData);
			}
			writer.flush();

			// Get the response
			StringBuffer answer = new StringBuffer();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null)
			{
				answer.append(line);
			}

			reader.close();
			writer.close();

			httpStatusCode = conn.getResponseCode();
			responseMessage = answer.toString();
		}
		catch (MalformedURLException e)
		{
			CommonService.DEBUGGER.error("Exception: " + e.toString());
			responseMessage += e.toString();
		}
		catch (IOException e)
		{
			CommonService.DEBUGGER.error("Exception: " + e.toString());
			responseMessage += e.toString();
		}

		rkitHttpResponse.setHttpStatusCode(httpStatusCode);
		rkitHttpResponse.setResponseMessage(responseMessage);
		return rkitHttpResponse;
	}

	public static RkitHttpResponse sendPostRequest(String postUrl, String data)
	{
		RkitHttpResponse rkitHttpResponse = new RkitHttpResponse();
		int httpStatusCode = 500;
		String responseMessage = "Failed to get response. ";
		rkitHttpResponse.setHttpStatusCode(httpStatusCode);
		rkitHttpResponse.setResponseMessage(responseMessage);

		try
		{
			URL url = new URL(postUrl);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json");
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

			if (CommonService.isStringNotNull(data))
			{
				writer.write(data);
			}
			writer.flush();

			StringBuffer answer = new StringBuffer();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null)
			{
				answer.append(line);
			}
			writer.close();
			reader.close();

			responseMessage = answer.toString();
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
			responseMessage = e.toString();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			responseMessage = e.toString();
		}

		rkitHttpResponse.setHttpStatusCode(200);
		rkitHttpResponse.setResponseMessage(responseMessage);
		return rkitHttpResponse;
	}

	public static RkitHttpResponse sendGetRequest(String getUrl)
	{
		RkitHttpResponse rkitHttpResponse = new RkitHttpResponse();
		int httpStatusCode = 500;
		String responseMessage = "Failed to get response. ";
		rkitHttpResponse.setHttpStatusCode(httpStatusCode);
		rkitHttpResponse.setResponseMessage(responseMessage);
		try
		{
			HttpGet httpGet = new HttpGet(getUrl);
			HttpResponse response = new DefaultHttpClient().execute(httpGet);
			rkitHttpResponse.setHttpStatusCode(response.getStatusLine().getStatusCode());
			if (200 == response.getStatusLine().getStatusCode())
			{
				responseMessage = EntityUtils.toString(response.getEntity());
			}
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
			responseMessage = e.toString();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			responseMessage = e.toString();
		}

		rkitHttpResponse.setResponseMessage(responseMessage);
		return rkitHttpResponse;
	}

	/**
	 * ȥ��html��ǩ
	 * 
	 * @param input
	 *            �����html�ַ���
	 * @return ��ȥtags��html�ı��ַ���
	 */
	public static String deleteHtmlTags(String input)
	{
		String newMsg = input;
		if (CommonService.isStringNull(input))
		{
			return newMsg;
		}

		newMsg = Jsoup.parse(newMsg).text();
		return newMsg;
	}

	public static String genErrorMessageInJson(String errorCode, String errorMsg)
	{
		return "{\"ErrorCode\": \"" + errorCode + "\", \"ErrorMessage\": \"" + errorMsg + "\"}";
	}

	public static String byteArrayToHex(byte[] bytes)
	{
		// �ַ����飬�������ʮ�������ַ�
		char[] hexReferChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		// һ���ֽ�ռ8λ��һ��ʮ�������ַ�ռ4λ��ʮ�������ַ�����ĳ���Ϊ�ֽ����鳤�ȵ�����
		char[] hexChars = new char[bytes.length * 2];
		int index = 0;
		for (byte b : bytes)
		{
			// ȡ�ֽڵĸ�4λ
			hexChars[index++] = hexReferChars[b >>> 4 & 0xf];
			// ȡ�ֽڵĵ�4λ
			hexChars[index++] = hexReferChars[b & 0xf];
		}
		return new String(hexChars);
	}

	public static String encodeStrByMd5(String str)
	{
		String md5Str = "";
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			// ʹ��ָ��byte[]����ժҪ
			md.update(str.getBytes());
			// ��ɼ��㣬���ؽ������
			byte[] b = md.digest();
			md5Str = CommonService.byteArrayToHex(b);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return md5Str;
	}

	/**
	 * ����������
	 * 
	 * @param seconds
	 *            ����
	 */
	public static void doSleep(long seconds)
	{
		try
		{
			Thread.sleep(seconds * 1000);
		}
		catch (InterruptedException e)
		{
			// ignore
		}
	}
}
