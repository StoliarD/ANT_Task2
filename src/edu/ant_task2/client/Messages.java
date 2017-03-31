package edu.ant_task2.client;

import java.io.*;

/**
 * Created by Dmitry on 31.03.2017.
 */
public class Messages {
    private String messagesFolder;

    public Messages(String messagesFolder) {
        this.messagesFolder = messagesFolder;
        File folder = new File(messagesFolder);
        if (!folder.exists())
            folder.mkdir();
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files)
                file.delete();
        }
    }

    boolean checkMessage(String message) {
        String fileName = messagesFolder + "\\" + messageHashCode(message) + ".txt";
        File file = new File(fileName);
        if (!file.exists()) {
            append(file,message);
            return true;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals(message))
                    return false;
            }
            append(file,message);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void append(File file, String message){
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true))) {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int messageHashCode(String message) {
        return message.hashCode() % 1000;
    }

}
