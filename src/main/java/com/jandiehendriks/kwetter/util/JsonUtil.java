package com.jandiehendriks.kwetter.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jandie
 */
public class JsonUtil {
    public String encode(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(JsonUtil.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public <T> T decode(String json, Class<T> type) {
        try {
            return (T) new ObjectMapper().readValue(json, type);
        } catch (IOException ex) {
            Logger.getLogger(JsonUtil.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
