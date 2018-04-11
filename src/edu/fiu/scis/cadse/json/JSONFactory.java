package edu.fiu.scis.cadse.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

/**
 * @author <a href="mailto:dalam004@fiu.edu">Dewan Moksedul Alam</a>
 * @author last modified by $Author: $
 * @version $Revision: $ $Date: $
 */
public class JSONFactory {
  private static final JSONFactory sInstance = new JSONFactory();
  private ObjectMapper mObjectMapper;

  private JSONFactory() {

  }

  public static JSONFactory getsInstance() {
    return sInstance;
  }

  public ObjectMapper getObjectMapper() {
    if (mObjectMapper == null) {
      mObjectMapper = new ObjectMapper();
      mObjectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
      mObjectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }
    return mObjectMapper;
  }

  public <T> String objectToString(final T pObject) throws JsonProcessingException {
    return getObjectMapper().writeValueAsString(pObject);
  }

  public <T> T stringToObject(final String pString, final Class<T> pType) throws IOException {
    return getObjectMapper().readValue(pString, pType);
  }

  public <T> T readFromFile(final File pFile, final Class<T> pType) throws IOException {
    return getObjectMapper().readValue(pFile, pType);
  }

  public void writeToFile(final File pFile, final Object pType) throws IOException {
    getObjectMapper().writeValue(pFile, pType);
  }
}
