package com.github.ldjmthgh.schedule;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class FileCleanSchedule {
    @Value("${project.path.temp}")
    private String path;

    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanFile() {
        File parentDir = new File(path);
        if (!parentDir.isDirectory()) {
            throw new RuntimeException("app.tmp.path需为路径");
        }
        if (!parentDir.exists()) {
            return;
        }
        File[] files = parentDir.listFiles();
        if (null == files || files.length < 1) {
            return;
        }
        for (File file : files) {
            long DELAY_TIME_MILLIS = 3 * 60 * 60 * 1000L;
            if (System.currentTimeMillis() - file.lastModified() > DELAY_TIME_MILLIS) {
                try {
                    if (file.isDirectory()) {
                        FileUtils.deleteDirectory(file);
                    } else {
                        FileUtils.forceDelete(file);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
