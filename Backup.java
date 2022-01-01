package chatBackup;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Scanner;

public class Backup
{
    Scanner sc = new Scanner(System.in);
    private final String accessToken;
    JSONParser parser = new JSONParser();
    Backup(String token)
    {
        accessToken = token;
    }

    private HttpURLConnection getConnection(String url,String method) throws IOException {
        URL u = new URL(Client.getApiURL()+url);
        System.out.println(Client.getApiURL()+url);
        HttpURLConnection connection = (HttpURLConnection) u.openConnection();
        connection.setRequestMethod(method);

        connection.setRequestProperty("Authorization",Client.getAuthHeader(accessToken));
        connection.setRequestProperty("Content-Type","application/json");
        return  connection;
    }

    void showChats() throws IOException, ParseException {
        HttpURLConnection connection = getConnection("/chats","GET");
        connection.setConnectTimeout(2000);
        connection.setReadTimeout(2000);
        connection.connect();
        System.out.println(connection.usingProxy());
        if(connection.getResponseCode() == 200)
        {
            InputStreamReader in = new InputStreamReader(connection.getInputStream());
            JSONObject response = (JSONObject) parser.parse(in);
            if(response.containsKey("chats"))
            {
                JSONArray chats = (JSONArray) response.get("chats");
                for(Object obj : chats)
                {
                    System.out.println("==================================");
                    System.out.println("Name : " +((JSONObject) obj).get("name"));
                    System.out.println("Chat ID : " +((JSONObject) obj).get("chat_id"));
                }
                System.out.println("==================================");


            }
            else
            {
                System.out.println("No response found");
            }

        }
        else
        {
            System.out.println("Invalid response with status code "+connection.getResponseCode());

        }

    }


    void backupChat() throws IOException, ParseException {

        System.out.print("Enter Chat Id");
        sc.nextLine();
        String chatId = sc.nextLine();
        System.out.print("\nApply limits ? (Y/N)");
        boolean isLimited = sc.next().toLowerCase().equals("y");
        int limit;
        HttpURLConnection connection;
        if(isLimited)
        {
            System.out.print("\nEnter Limit ");
            limit = sc.nextInt();
            connection = getConnection("/chats/"+chatId+"/messages?fromtime=631132200000&limit="+limit,"GET");
        }
        else
        {
            connection = getConnection("/chats/"+chatId+"/messages?fromtime=631132200000","GET");

        }
        System.out.println(connection);
        connection.connect();
        if(connection.getResponseCode() == 200)
        {
            InputStreamReader in = new InputStreamReader(connection.getInputStream());
            JSONObject response = (JSONObject) parser.parse(in);
            File backup = new File("backup");
            if(!backup.exists()) backup.mkdir();
            String filename = chatId+".txt";
            PrintWriter pr = new PrintWriter("backup/"+filename);
            if(response.containsKey("data"))
            {
                JSONArray chats = (JSONArray) response.get("data");
                for(Object obj : chats)
                {
                    JSONObject sender =  (JSONObject) ((JSONObject) obj).get("sender");
                    String senderName = (String) sender.get("name");

                    if(((JSONObject) obj).containsKey("content"))
                    {
                        JSONObject content =  (JSONObject) ((JSONObject) obj).get("content");
                        if(content.containsKey("text")) {
                            pr.append(senderName).append(" : ").append((String) content.get("text"));
                            pr.println();
                            System.out.println(senderName + " : " + (String) content.get("text"));
                        }

                    }

                }


            }
            else
            {
                System.out.println("No response found");
            }
            pr.flush();
            pr.close();

        }
        else
        {
            System.out.println("Invalid response with status code "+connection.getResponseCode());


        }

    }

    void sendMessage() throws IOException, ParseException {
        JSONObject body = new JSONObject();
        System.out.print("\nEnter Chat ID : ");
        sc.nextLine();
        String id = sc.nextLine();
        System.out.print("\nEnter Text to send : ");
        String text = sc.nextLine();
        body.put("text",text);
        String jsonString = body.toJSONString();
        HttpURLConnection connection = getConnection("/chats/"+id +"/message","POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept","application/json");
        byte[] input = jsonString.getBytes(StandardCharsets.UTF_8);
        connection.setFixedLengthStreamingMode(input.length);
        long before = Instant.now().getEpochSecond();
        try(OutputStream os = connection.getOutputStream())
        {
            os.write(input);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if(connection.getResponseCode() == 200)
        {

            InputStreamReader in = new InputStreamReader(connection.getInputStream());
            JSONObject response = (JSONObject) parser.parse(in);
            System.out.println(response.toString());


        }
        else if(connection.getResponseCode() >= 400)
        {
            System.out.println("Invalid response with status code "+connection.getResponseCode());


        }
        else
        {
            System.out.println("Message Sent Successfully");

        }
        long after = Instant.now().getEpochSecond();
        System.out.println("Time diff = "+(after-before));

    }


    void start() throws IOException, ParseException {

        int n = 0;
        do
        {
            System.out.println("1. Show Chats");
            System.out.println("2. Backup Chat");
            System.out.println("3. Send Message");
            System.out.println("4. Exit");
            n = sc.nextInt();
            switch (n)
            {
                case 1:
                    showChats();
                    break;
                case 2:
                    backupChat();
                    break;
                case 3:
                    sendMessage();
                    break;
                case 4:
                    break;
                default:
                    System.out.println("Invalid Choice....");

            }
        }
        while(n != 3);


    }
}