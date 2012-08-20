package simperf.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * �ļ���������
 * @author imbugs
 * @version $Id: FileOperateUtils.java,v 0.1 2010-6-29 ����11:41:15 imbugs Exp $
 */
public class FileOperateUtils {

    /** Logger */
    protected static Logger logger = LoggerFactory.getLogger(FileOperateUtils.class);

    /**
     * ���ļ���׷������
     * @param path
     * @param contents
     */
    public static void appendFile(String path, String contents) {
        try {
            FileWriter fw = new FileWriter(path, true);
            fw.write(contents);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ���ļ���д������,�Ḳ��ԭ�ļ�
     * @param path     ָ���ļ�·��
     * @param contents д������
     */
    public static void writeFile(String path, String contents) {
        writeFile(path, contents, true);
    }

    /**
     * 
     * @param path
     * @param contents
     * @param overwrite true: ����ԭ�ļ�  false: ��ָ���ļ��Ѿ������򲻽��в���
     */
    public static void writeFile(String path, String contents, boolean overwrite) {
        File file = new File(path);
        try {
            if (overwrite && file.exists()) {
                file.delete();
            }
            if (!file.exists()) {
                file.createNewFile();
                FileWriter fw = new FileWriter(path);
                fw.write(contents);
                fw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * �ݹ�����ļ�
     * @param findDir Ŀ¼
     * @param fileNameRegex �ļ���ƥ����ʽ, null:�������κ��ļ�
     * @param fileList �����ļ��б�
     */
    public static void findFileRecursive(final String findDir, final String fileNameRegex,
                                         List<File> fileList) {
        if (fileList == null) {
            return;
        }
        File file = new File(findDir);
        if (file.isFile()) {
            if (null == fileNameRegex || file.getName().matches(fileNameRegex)) {
                fileList.add(file);
            }
        } else if (file.isDirectory()) {
            File[] dirFiles = file.listFiles();
            for (File dirFile : dirFiles) {
                findFileRecursive(dirFile.getAbsolutePath(), fileNameRegex, fileList);
            }
        }
    }

    /**
     * �����ļ�
     * @param fromFile
     * @return
     */
    public static boolean backupFile(File fromFile) {

        if (!fromFile.exists()) {
            return false;
        }

        String bakFileName = fromFile.getName() + ".bak";

        return renameFile(fromFile, bakFileName);

    }

    /**
     * �����ļ�����ɾ��ԭ�ļ�
     * @param fromFile
     * @return
     */
    public static boolean backupFileToDel(File fromFile) {

        if (!fromFile.exists()) {
            return false;
        }

        return backupFile(fromFile) && fromFile.delete();

    }

    /**
     * �������ļ�
     * ע�⣺��newName�Ѵ��ڣ���ֱ��ɾ��
     * 
     * @param fromFile-ԭ�ļ�
     * @param toFile-Ŀ���ļ�
     * @return
     */
    public static boolean renameFile(File fromFile, String newName) {

        // ����������ĺϷ��ԣ���newName�ļ��Ѵ��ڣ�ɾ��
        String orgiFilePath = fromFile.getParent();
        File newFile = new File(orgiFilePath + "/" + newName);

        if (newFile.exists() && newFile.delete()) {
            logger.error(newFile.getAbsolutePath() + " �Ѵ��ڲ�ɾ���ɹ���");
        }

        // ��ʽ����ԭʼ�ļ�
        if (fromFile.renameTo(newFile)) {
            logger.error(fromFile.getName() + "������Ϊ" + newFile.getName() + "�ɹ���");
            return true;
        } else {
            logger.error(fromFile.getName() + " ������Ϊ" + newFile.getName() + "ʧ�ܣ�");
            return false;
        }

    }

    /**
     * �����ļ���������;Ŀ���ļ������ڣ�ֱ��ɾ��
     * @param fromFile-ԭ�ļ�
     * @param toFile-Ŀ���ļ�
     * @return
     * @throws IOException 
     */
    public static boolean copyFile(File fromFile, File toFile) throws IOException {

        if (toFile.exists() && toFile.delete()) {
            logger.error(toFile.getAbsolutePath() + " �Ѵ��ڲ�ɾ���ɹ���");
        }

        if (fromFile.exists()) {
            BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fromFile));
            BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(toFile));

            //����   
            int c;
            while ((c = bin.read()) != -1) {
                bout.write(c);

            }
            bin.close();
            bout.close();
            return true;
        } else {
            logger.error(fromFile.getAbsolutePath() + " �����ڣ�����ʧ�ܣ�");
            return false;
        }

    }
}
