package chatBackup;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;


class Auth
{
    private String accessToken;
    private boolean isAuthenticated = false;

    public void authorize(String url)
    {
        try
        {
            URL u = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("POST");
            connection.connect();
            int statusCode = connection.getResponseCode();
            if (statusCode == 200) {
                InputStreamReader ir = new InputStreamReader(connection.getInputStream());
                JSONParser parser = new JSONParser();
                JSONObject response = (JSONObject) parser.parse(ir);
                if (response.containsKey("error"))
                {
                    System.out.println("Error : " + response.get("error"));
                }
                else {

                    PrintWriter pr = new PrintWriter("auth.txt");
                    if(response.containsKey("access_token"))
                    {
                        accessToken = (String) response.get("access_token");
                        pr.write(accessToken);

                    }
                    pr.flush();
                    isAuthenticated = true;
                    pr.close();
                    ir.close();

                }
            } else {
                System.out.println("Invalid statuscode : " + statusCode);
            }


        }
        catch(IOException | ParseException e)
        {
            e.printStackTrace();
        }
    }

    public void authorizeUser(){

        try(FileReader fr = new FileReader("auth.txt"))
        {
            System.out.println("Getting Cached Data....");
            BufferedReader br = new BufferedReader(fr);
            String line;
            StringBuilder authToken = new StringBuilder();
            while((line = br.readLine()) != null)
            {
                authToken.append(line);
            }
            accessToken = authToken.toString();
            isAuthenticated = true;

        }
        catch (FileNotFoundException e)
        {
            System.out.println("No Access token found..");
            authorize(Client.getAccessTokenURL());


        }  catch(IOException e) {
            e.printStackTrace();
        }

    }
    public void start() throws IOException, ParseException {
        System.out.println("Authorizing user");
        authorizeUser();
        if(isAuthenticated) {

            new Backup(accessToken).start();
        }
        else System.out.println("Authentication Failed..Try again");
    }
}


public class Main
{
    public static void main(String[] args) throws IOException, ParseException {
        new Auth().start();

    }

}