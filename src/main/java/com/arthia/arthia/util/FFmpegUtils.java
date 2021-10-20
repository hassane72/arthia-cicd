package com.arthia.arthia.util;

import com.google.gson.Gson;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.crypto.KeyGenerator;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


public class FFmpegUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FFmpegUtils.class);
	
	
	//
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");


	public static void transcodeMP4ToM3u8(String source, String destFolder, String fileName) throws IOException, InterruptedException {

		//
		if (!Files.exists(Paths.get(source))) {
			throw new IllegalArgumentException(" ：" + source);
		}

		//
		Path workDir = Paths.get(destFolder);
		// Files.createDirectories(workDir);
		LOGGER.info("file destination：{}", workDir);

		//
		List<String> commands = new ArrayList<>();
		commands.add("ffmpeg");
		commands.add("-i")						;commands.add(source);					//
		commands.add("-codec:")					;commands.add("copy");				//
		commands.add("-start_number")			;commands.add("0");					//
		commands.add("-hls_time")				;commands.add("10");		//
		commands.add("-hls_list_size")			;commands.add("0");	//
		commands.add("-f")		                ;commands.add("hls");					//
		commands.add("index.m3u8");					                //

		//
		Process process = new ProcessBuilder()
				.command(commands)
				.directory(workDir.toFile())
				.start();

		//
		new Thread(() -> {
			try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					LOGGER.info(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();

		//
		new Thread(() -> {
			try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					LOGGER.info(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();


		//
		if (process.waitFor() != 0) {
			throw new RuntimeException(" ");
		}

	}


	public static void transcodeMP3ToM3u8(String source, String destFolder, String fileName) throws IOException, InterruptedException {

		//
		if (!Files.exists(Paths.get(source))) {
			throw new IllegalArgumentException(" ：" + source);
		}

		//
		Path workDir = Paths.get(destFolder);
		// Files.createDirectories(workDir);
		LOGGER.info("file destination：{}", workDir);

		// ffmpeg -i gucci.mp3 -vn -ac 2 -acodec aac -f segment -segment_format mpegts -segment_time 10 -segment_list audio/playlist.m3u8 audio/playlist-%05d.ts
		List<String> commands = new ArrayList<>();
		commands.add("ffmpeg");
		commands.add("-i")						;commands.add(source);					//
		commands.add("-vn");
		commands.add("-ac")					;commands.add("2");				//
		commands.add("-acodec")			;commands.add("aac");					//
		commands.add("-f")				;commands.add("segment");		//
		commands.add("-segment_format")			;commands.add("mpegts");	//
		commands.add("-segment_time")		                ;commands.add("10");					//
		commands.add("-segment_list")		                ;commands.add("index.m3u8");					//
		commands.add("index-%05d.ts");					                //

		//
		Process process = new ProcessBuilder()
				.command(commands)
				.directory(workDir.toFile())
				.start();

		//
		new Thread(() -> {
			try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					LOGGER.info(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();

		//
		new Thread(() -> {
			try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					LOGGER.info(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();


		//
		if (process.waitFor() != 0) {
			throw new RuntimeException(" ");
		}

	}

	/**
	 * MEDIA INFO
	 * @param source
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static MediaInfo getMediaInfo(String source) throws IOException, InterruptedException {
		List<String> commands = new ArrayList<>();
		commands.add("ffprobe");	
		commands.add("-i")				;commands.add(source);
		commands.add("-show_format");
		commands.add("-show_streams");
		commands.add("-print_format")	;commands.add("json");
		
		Process process = new ProcessBuilder(commands)
				.start();
		 
		MediaInfo mediaInfo = null;
		
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
			mediaInfo = new Gson().fromJson(bufferedReader, MediaInfo.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (process.waitFor() != 0) {
			return null;
		}
		
		return mediaInfo;
	}
	
	/**
	 * Get cover of video
	 * @param source		sourcefile
	 * @param file			file
	 * @param time			time to cute HH:mm:ss.[SSS]
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static boolean screenShots(String source, String file, String time) throws IOException, InterruptedException {
		
		List<String> commands = new ArrayList<>();
		commands.add("ffmpeg");	
		commands.add("-i")				;commands.add(source);
		commands.add("-ss")				;commands.add(time);
		commands.add("-y");
		commands.add("-q:v")			;commands.add("1");
		commands.add("-frames:v")		;commands.add("1");
		commands.add("-f");				;commands.add("image2");
		commands.add(file);
		
		Process process = new ProcessBuilder(commands)
					.start();
		
		//
		new Thread(() -> {
			try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					LOGGER.info(line);
				}
			} catch (IOException e) {
			}
		}).start();
		
		//
		new Thread(() -> {
			try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					LOGGER.error(line);
				}
			} catch (IOException e) {
			}
		}).start();
		
		return process.waitFor() == 0;
	}
}

