package org.kurento.tutorial.groupcall.behappy.utils;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BehappyUtil {

  private static final Logger log = LoggerFactory.getLogger(BehappyUtil.class);

  public static void createFolder(String folderPath) {
    String createFolderCmd = "[ -e " + folderPath + " ] || mkdir -p -m 777 "
        + folderPath;
    try {
      CommandExecutor.execCommand("/bin/sh", "-c", createFolderCmd);
      log.info("creating folder " + folderPath);
    } catch (IOException | InterruptedException e) {
      log.error(e.getMessage());
      ;
    }
  }
}
