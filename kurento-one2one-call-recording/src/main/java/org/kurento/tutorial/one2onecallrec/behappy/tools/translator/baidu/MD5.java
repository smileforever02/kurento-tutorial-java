package org.kurento.tutorial.one2onecallrec.behappy.tools.translator.baidu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
  // initial a char array to put each hex char
  private static final char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6',
      '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

  /**
   * 
   * get the MD5 value of a string
   */
  public static String md5(String input) {
    if (input == null)
      return null;
    try {
      // get a MD5 converter(if want SHA1, use "SHA1" as parameter）
      MessageDigest messageDigest = MessageDigest.getInstance("MD5");
      byte[] inputByteArray = input.getBytes("utf-8");
      messageDigest.update(inputByteArray);
      // convert to another byte array which contains 16 elements.
      byte[] resultByteArray = messageDigest.digest();
      // convert byte array to string
      return byteArrayToHex(resultByteArray);
    } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
      return null;
    }
  }

  /**
   * 
   * get MD5 value of a file
   */
  public static String md5(File file) {
    try {
      if (!file.isFile()) {
        System.err.println("file " + file.getAbsolutePath()
            + "doesn't exist or it's not a file");
        return null;
      }
      FileInputStream in = new FileInputStream(file);
      String result = md5(in);
      in.close();
      return result;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String md5(InputStream in) {
    try {
      MessageDigest messagedigest = MessageDigest.getInstance("MD5");
      byte[] buffer = new byte[1024];
      int read = 0;
      while ((read = in.read(buffer)) != -1) {
        messagedigest.update(buffer, 0, read);
      }
      in.close();
      String result = byteArrayToHex(messagedigest.digest());
      return result;
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private static String byteArrayToHex(byte[] byteArray) {
    // 1 byte = 8 bits = 2 hex, because 2^8=16^2
    char[] resultCharArray = new char[byteArray.length * 2];
    int index = 0;
    for (byte b : byteArray) {
      resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
      resultCharArray[index++] = hexDigits[b & 0xf];
    }
    return new String(resultCharArray);
  }
}