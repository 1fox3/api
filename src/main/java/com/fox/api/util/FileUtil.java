package com.fox.api.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件操作类
 * @author lusongsong
 * @date 2020/10/28 16:14
 */
public class FileUtil {
    public static final String FILE_ROOT_PATH = "/export/data/stock";

    /**
     * 覆盖写
     * @param file
     * @param content
     * @return
     */
    public static Boolean coverWrite(String file, String content) {
        return write(file, content, false);
    }

    /**
     * 添加写
     * @param file
     * @param content
     * @return
     */
    public static Boolean appendWrite(String file, String content) {
        return write(file, content, true);
    }

    /**
     * 写文件
     * @param filePath
     * @param content
     * @param append
     * @return
     */
    public static Boolean write(String filePath, String content, Boolean append) {
        if (null == filePath || filePath.isEmpty()) {
            return false;
        }
        content = null == content ? "" : content;
        append = null == append ? true : false;

        filePath = (FILE_ROOT_PATH + "/" + filePath)
                .replace("\\", "/")
                .replace("//", "/");
        try {
            File file = new File(filePath);
            File path = new File(file.getParent());
            //创建文件目录
            if (!path.exists()) {
                path.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file, append);
            fileWriter.write(content);
            fileWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 读取全部内容
     * @param filePath
     * @return
     */
    public static String read(String filePath) {
        filePath = (FILE_ROOT_PATH + "/" + filePath)
                .replace("\\", "/")
                .replace("//", "/");
        StringBuffer stringBuffer = new StringBuffer();
        InputStreamReader reader = null;
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        if (!file.canRead()) {
            return null;
        }
        try {
            //一次读多个字符
            char[] tempChars = new char[1024];
            int charRead;
            reader = new InputStreamReader(new FileInputStream(filePath));
            // 读入多个字符到字符数组中，charread为一次读取字符数
            while ((charRead = reader.read(tempChars)) != -1) {
                // 同样屏蔽掉\r不显示
                if ((charRead == tempChars.length)
                        && (tempChars[tempChars.length - 1] != '\r')) {
                    stringBuffer.append(tempChars);
                } else {
                    for (int i = 0; i < charRead; i++) {
                        stringBuffer.append(tempChars[i]);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return stringBuffer.toString();
        }
    }

    /**
     * 按行读取文件
     * @param filePath
     * @return
     */
    public static List<String> readByLines(String filePath) {
        BufferedReader reader = null;
        List<String> fileContent = new ArrayList<>();
        try {
            File file = new File(filePath);
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                fileContent.add(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
            return fileContent;
        }
    }
}
