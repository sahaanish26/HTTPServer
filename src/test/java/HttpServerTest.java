import com.example.httpserver.HttpServer;
import com.example.httpserver.application.FileApplication;
import com.example.httpserver.workers.HttpServerDelegate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HttpServerTest {



    private HttpURLConnection con ;
    protected static ExecutorService testthreadPoolOne ;



    @Before
    public void setUp() {
        try {

            HttpServerDelegate  delegate  = new HttpServerDelegate(8081, "web/");
            //server.setName("TestThread");
            testthreadPoolOne = Executors.newSingleThreadExecutor();
                testthreadPoolOne.execute(delegate);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    @Test
    public void testGetRequests() {

        //HttpURLConnection con = null;
        try {
            URL url = new URL("http://localhost:8081/index.html");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int status = con.getResponseCode();

            Assert.assertEquals(200, status);

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(con!=null) {
                con.disconnect();
            }
        }

    }

    @Test
    public void testGetCacheContentRequests() {


        try {
            URL url = new URL("http://localhost:8081/index.html");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();


            if (status == HttpURLConnection.HTTP_OK) {
                String etag = con.getHeaderField("Etag");
                System.out.println(etag);
                con.disconnect();
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("If-None-Match",etag);
                int statusNext = con.getResponseCode();
                Assert.assertEquals(304, statusNext);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(con!=null) {
                con.disconnect();
            }
        }

    }

    @Test
    public void testGetCacheTimeRequests() {

      //  HttpURLConnection con = null;
        try {
            URL url = new URL("http://localhost:8081/index.html");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();


            if (status == HttpURLConnection.HTTP_OK) {
                String lastModifiedTs = con.getHeaderField("Last-Modified");
                System.out.println(lastModifiedTs);
                con.disconnect();
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("If-Modified-Since",lastModifiedTs);
                int statusNext = con.getResponseCode();
                Assert.assertEquals(304, statusNext);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(con!=null) {
                con.disconnect();
            }
        }

    }


    @Test
    public void testGetNotFoundRequests() {


        try {
            URL url = new URL("http://localhost:8081/inde.html");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();
            Assert.assertEquals(404, status);


        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(con!=null) {
                con.disconnect();
            }
        }

    }
    @After
    public void tearDown() {


        testthreadPoolOne.shutdown();
        try {
            testthreadPoolOne.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

}
