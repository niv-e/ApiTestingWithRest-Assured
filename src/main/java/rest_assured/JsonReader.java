package rest_assured;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class JsonReader{
    ObjectMapper mapper = new ObjectMapper();
    public List<Map<?, ?>> readList(String pathToInputFile) {
        List<Map<?, ?>> testInput;

        try {
            testInput = mapper.readValue(Paths.get(pathToInputFile).toFile(), List.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return testInput;
    }
    public Map<?, ?> read(String pathToInputFile) {
        Map<?, ?> testInput;

        try {
            testInput = mapper.readValue(Paths.get(pathToInputFile)
                    .toFile(), Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return testInput;
    }
    
    // to get BookingDates from json
    public <T> T parseObjectFromJson (String pathString, Class<T> classType)
    {
    	T obj = null; 
    	try {
			obj = mapper.readValue(Paths.get(pathString)
                    .toFile(),classType);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return obj;
    }
}