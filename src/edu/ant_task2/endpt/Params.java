package edu.ant_task2.endpt;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by Dmitry on 30.03.2017.
 */
class Params {
    private String paramFolder;
    private int paramCount;

    Params(String paramFolder) {
        this.paramFolder = paramFolder;
        File folder = new File(paramFolder);
        if (!folder.exists())
            folder.mkdir();
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files)
                file.delete();
        }
    }

    String getParamStat(String param) {
        String fileName = paramFolder + "\\" + paramHashCode(param) + ".txt";
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            String line;
            String[] ss = null;
            while ((line = bufferedReader.readLine()) != null) {
                if ((ss = line.split(","))[0].equals(param)) {
                    break;
                }
            }
            bufferedReader.close();
            if (ss == null)
                return "no such parameter: " + param;
            else {
                return param + " - min:" + ss[2] + " ; max:" + ss[3] + " ; avg:" + ss[4];
            }
        } catch (IOException e) {
//            e.printStackTrace();
            return "no such parameter: " + param;
        }
    }

    int getParamCount() {
        return paramCount;
    }

    private boolean append(String fileName, String string){
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName, true))) {
            bufferedWriter.write(string);
            bufferedWriter.newLine();
            paramCount++;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean write(String fileName, ArrayList<String> lines) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName, false))){
            for (String line : lines) {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    boolean add(String param, BigDecimal value) {
        // csv: param,count,min,max,avg
        String fileName = paramFolder + "\\" + paramHashCode(param) + ".txt";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            ArrayList<String> lines = new ArrayList<>();
            boolean flag = false;
            String[] ss;
            while ((line = br.readLine()) != null) {
                if ((ss = line.split(","))[0].equals(param)) {
                    int count = Integer.parseInt(ss[1]);
                    BigDecimal min = new BigDecimal(ss[2]);
                    BigDecimal max = new BigDecimal(ss[3]);
                    BigDecimal avg = new BigDecimal(ss[4]);
                    avg = avg.multiply(new BigDecimal(count)).add(value).divide(new BigDecimal(count+1),2,BigDecimal.ROUND_HALF_EVEN);
                    line = ss[0] + "," + (count+1) + "," +
                            value.min(min).toString() + "," + value.max(max).toString() + "," + avg;
                    flag = true;
                }
                lines.add(line);
            }
            if (flag) {
                return write(fileName, lines);
            } else {
                return append(fileName, param + "," + 1 + "," + value + "," + value + "," + value);
            }
        } catch (IOException e) {
            return append(fileName,param + "," + 1 + "," + value + "," + value + "," + value);
        }
    }

    private int paramHashCode(String param) {
        return param.hashCode() % 1000;
    }
}
