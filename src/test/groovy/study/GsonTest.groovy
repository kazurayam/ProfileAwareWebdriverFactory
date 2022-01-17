package study

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.Test

import static org.junit.Assert.*

class GsonTest {

    @Test
    void test_prettyPrinting() {
        String jsonInString = """{"foo":{"bar":{"baz":"bee"}},"profile":"zoo"}"""
        Gson gson = new Gson()
        Map<String, Object> m = gson.fromJson(jsonInString, Map.class)
        String value = m.get("profile")
        assertEquals("zoo", value)
    }
}
