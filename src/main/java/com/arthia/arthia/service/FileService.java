package com.arthia.arthia.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.arthia.arthia.util.FFmpegUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileService.class);


    @Value("${upload.path}")
    private String uploadPath;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadPath));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload folder!");
        }
    }

    public void saveAndConvertM3u8(MultipartFile file){

      //  this.save(file);

        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
        Path tempFile = tempDir.resolve(file.getOriginalFilename());


        String title = file.getOriginalFilename();
        title = title.substring(0, title.lastIndexOf("."));

        String today = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
        String m3u8 = "m3u8";

        try {
            file.transferTo(tempFile);
            Path targetFolder = Files.createDirectories(Paths.get(uploadPath, m3u8, today, title.trim()));
            LOGGER.info("file destination：{}", targetFolder);
            Files.createDirectories(targetFolder);
            LOGGER.info("file destination：{}", targetFolder);
            String[] exts = file.getOriginalFilename().split(Pattern.quote("."));
            String ext = exts[exts.length - 1];
            if(ext.equals("mp3")){
                FFmpegUtils.transcodeMP3ToM3u8(tempFile.toString(), targetFolder.toString(), file.getOriginalFilename());
            }else{

                FFmpegUtils.transcodeMP4ToM3u8(tempFile.toString(), targetFolder.toString(), file.getOriginalFilename());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void save(MultipartFile file) {
        try {
            Path root = Paths.get(uploadPath);
            if (!Files.exists(root)) {
                init();
            }
            Files.copy(file.getInputStream(), root.resolve(file.getOriginalFilename()));
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    public Resource load(String filename) {
        try {
            Path file = Paths.get(uploadPath)
                    .resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    public void deleteAll() {
        FileSystemUtils.deleteRecursively(Paths.get(uploadPath)
                .toFile());
    }

    public List<Path> loadAll() {
        try {
            Path root = Paths.get(uploadPath);
            if (Files.exists(root)) {
                return Files.walk(root, 1)
                        .filter(path -> !path.equals(root))
                        .collect(Collectors.toList());
            }

            return Collections.emptyList();
        } catch (IOException e) {
            throw new RuntimeException("Could not list the files!");
        }
    }
}
