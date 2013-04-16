package com.predictor.counter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;


public class AvgAgeCounter {
    private static final int MAX_RETRIES = 10;
    private static final int RETRY_DELAY_MSEC = 2000;
    private static final int AGE_OF_MAJORITY = 18;
    private static final String DEFAULT_CHARSET = "UTF-8";
    private final static Logger log = Logger.getLogger(AvgAgeCounter.class.getName());
    private static final String NEW_LINE = "\r\n";

    /**
     * Counts average age of adult females listed at csv file pointed by {@code location}
     * @param location http url pointing to csv file with persons
     * @return average age of adult females, 0 if no adult females found in list
     * @throws CounterException if age could not be calculated because of connection error or malformed csv
     */
    public static int count(String location) throws CounterException {
        long ageSum = 0;
        long personCounter = 0;
        int retries = MAX_RETRIES;
        boolean success = false;
        long downloadedBytes = 0;
        URL url;
        HttpURLConnection connection;
        try {
            url = new URL(location);

        } catch (MalformedURLException e) {
            throw new CounterException("Failed to parse location ["+location+"]. "+e.getMessage());
        }
        while (retries>0 && !success){
            try {
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestProperty("Range", "bytes="+downloadedBytes+"-");
                connection.setRequestProperty("Accept-Charset", DEFAULT_CHARSET);
                String charset = getCharset(connection);
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream(), charset));
                int responseCode = connection.getResponseCode();
                if(responseCode!=200 && responseCode!=206){
                    throw new CounterException("HTTP error: " + connection.getResponseMessage());
                }
                String inputLine;
                while ((inputLine = in.readLine()) != null){
                    if(inputLine.isEmpty())
                        continue;
                    Person person = CsvParser.parseLine(inputLine);
                    if(person.sex.equals(Person.Sex.FEMALE) && person.age >= AGE_OF_MAJORITY){
                        ageSum+=person.age;
                        personCounter++;
                        if(personCounter%100000==0)
                            log.info(personCounter+" females processed. Last processed female: " + person);
                    }
                    downloadedBytes+=inputLine.getBytes(charset).length+NEW_LINE.getBytes(charset).length;
                }
                in.close();
                success = true;
            } catch (Exception e) {
                log.warning("Error: "+e.getMessage());
                if (--retries==0){
                    log.severe(e.getMessage());
                    throw new CounterException("Failed to connect to location ["+location+"]. "+e.getMessage());
                }
                try {
                    Thread.sleep(RETRY_DELAY_MSEC);
                } catch (InterruptedException ignored) {
                }
                log.warning("Reconnecting...");
            }
        }
        return (personCounter>0)?(int) (ageSum / personCounter):0;
    }

    private static String getCharset(HttpURLConnection connection){
        String charset = DEFAULT_CHARSET;
        String contentType = connection.getContentType();
        if(contentType != null){
            String[] values = contentType.split(";");
            for (String value : values) {
                value = value.trim();
                if (value.toLowerCase().startsWith("charset=")) {
                    charset = value.substring("charset=".length());
                }
            }
        }
        return charset;
    }

    public static void main(String[] args) throws CounterException {
        int age = AvgAgeCounter.count("http://predictor-asus.dyndns.org/persons.csv");
        System.out.println(age);
    }
}
