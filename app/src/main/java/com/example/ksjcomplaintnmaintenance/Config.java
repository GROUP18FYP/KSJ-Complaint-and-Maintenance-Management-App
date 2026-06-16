package com.example.ksjcomplaintnmaintenance;

public class Config {
    // CHANGE ONLY THIS LINE WHEN YOUR WI-FI CHANGES
    public static final String BASE_URL = "http://10.20.98.237/ksj_api/";
    
    // Helper to get full URLs
    public static String getUrl(String phpFile) {
        return BASE_URL + phpFile;
    }
}
