package chatBackup;
public final class Client
{
    private Client()
    {
        //utility class only
    }

    public static final String CLIENT_ID = "";//your client id
    public static final String CLIENT_SECRET = "";//your client secret
    public static final String CODE  = "";//your code value
    public static final String SCOPE  =
            "ZohoCliq.Channels.ALL,ZohoCliq.Users.ALL,ZohoCliq.Profile.ALL,ZohoCliq.Departments.ALL,ZohoCliq.Designations.ALL,ZohoCliq.Chats.ALL,ZohoCliq.Teams.ALL,ZohoCliq.Messages.ALL,ZohoCliq.messageactions.ALL,ZohoCliq.Attachments.READ,ZohoCliq.Reminders.ALL,ZohoCliq.StorageData.ALL,ZohoCliq.Organisation.ALL,ZohoCliq.Attachments.READ,ZohoCliq.Webhooks.CREATE";

    public static String getAccessTokenURL()
    {
        return "https://accounts.zoho.com/oauth/v2/token?code="+
                CODE+"&grant_type=authorization_code&scope="+
                SCOPE+"&client_id="+
                CLIENT_ID+"&client_secret="+
                CLIENT_SECRET+"&redirect_uri=http://application_name.com/";

    }

    public static String getApiURL()
    {
        return "https://cliq.zoho.com/api/v2";
    }
    public static String getChats()
    {
        return getApiURL()+"/chats";
    }
    public static String getAuthHeader(String token)
    {
        return "Zoho-oauthtoken "+token;
    }

    public static class Cliq
    {
        private String accessToken;
        Cliq(String accessToken)
        {
            this.accessToken = accessToken;

        }
    }
}