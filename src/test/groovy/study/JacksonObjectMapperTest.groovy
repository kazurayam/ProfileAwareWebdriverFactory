package study

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Before
import org.junit.Test
import static org.junit.Assert.assertEquals

/**
 * https://rennnosukesann.hatenablog.com/entry/2018/06/25/000000
 */
class JacksonObjectMapperTest {

    private str = '''
{
  "hoge": {
    "fuga": 1,
    "piyo": 2
  },
  "foo": ["bar", "bow"]
}
'''

    private JsonNode root;

    @Before
    public void setup() {
        ObjectMapper mapper = new ObjectMapper();
        root = mapper.readTree(str);
    }

    @Test
    void test_hoge() {
        println root.get("hoge").toString()
    }

    @Test
    void test_hoge_fuga() {
        assertEquals(1, root.get("hoge").get("fuga").asInt())
    }

    @Test
    void test_foo_0() {
        assertEquals("\"bar\"", root.get("foo").get(0).toString())
    }

    @Test
    void test_hoge_piyo() {
        assertEquals(2, root.get("hoge").get("piyo").asInt())
        println root.get("hoge").get("piyo").asDouble()   // 2.0
        assertEquals(true, root.get("hoge").get("piyo").asBoolean())

    }
}
