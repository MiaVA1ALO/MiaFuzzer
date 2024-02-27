import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MiaFuzzer
{
    // "MiaFuzzer" in ASCII art
    private static final String logo = "#     # ###    #    #######\n##   ##  #    # #   #       #    # ###### ###### ###### #####\n# # # #  #   #   #  #       #    #     #      #  #      #    #\n#  #  #  #  #     # #####   #    #    #      #   #####  #    #\n#     #  #  ####### #       #    #   #      #    #      #####\n#     #  #  #     # #       #    #  #      #     #      #   #\n#     # ### #     # #        ####  ###### ###### ###### #    #\n";
    public static void main(String[] args)
    {
        String url;
        if(args.length < 1)
        {
            System.out.println("Usage: java MiaFuzzer www.url.com [PAYLOAD].txt blockedresponse,blockedresponse,[...]\nor\nUsage: java MiaFuzzer www.url.com [PAYLOAD].txt");
        }
        else
        {
            // for if there are no blocked response arguments
            String temp;
            int[] blockedResponses;

            if(args.length == 2)
            {
                temp = "-1";
            }
            else
            {
                temp = args[2];
            }
            blockedResponses = separateArgs(temp);
            url = args[0];

            System.out.println(logo);

            // prevents an error from the URL being passed as www.example.com without an http
            if(!url.contains("http://"))
            {
                url = "http://" + url;
            }
            // this script uses http, however it probably works without this if statement.
            if(url.contains("https://"))
            {
                url = url.replace("https://", "http://");
            }
            // for the script to function properly the URL needs to contain 'FUZZ'
            if(!url.contains("FUZZ"))
            {
                url += "FUZZ";
                System.err.println("URL does not contain 'FUZZ'. URL automatically set to " + url);
            }
            new MiaFuzzer(url, args[1], blockedResponses);
            System.out.println(Arrays.toString(separateArgs(args[2])));
        }
    }

    // left public in case someone wants to write something better than my main() (hint hint...)
    public MiaFuzzer(String url, String file, int[] blockedResponses)
    {
        int totalRequests = 0;
        int blockedRequests = 0;
        int allowedRequests = 0;
        String[] payloads = new String[0];
        try
        {
            // opens [PAYLOAD].txt
            payloads = readPayload(file);
        }
        catch (IOException e)
        {
            // not specifically a file not found error, but I think that's the only possible one
            System.err.println("File not found: " + file);
        }
        for (String payload : payloads)
        {
            // replaces 'FUZZ' with the next payload from the file.
            String temp = url.replace("FUZZ", payload);
            int response = getStatus(temp);
            if (!isBlocked(response, blockedResponses))
            {
                totalRequests++;
                allowedRequests++;
                if (response == 404)
                {
                    System.err.println(response + " " + temp);
                }
                else
                {
                    System.out.println(response + " " + temp);
                }
            }
            else
            {
                totalRequests++;
                blockedRequests++;
            }
        }
        System.out.println("\nTotal requests: " + totalRequests + "\nAllowed requests: " + allowedRequests);
        System.err.println("Blocked requests: " + blockedRequests); // err.println is red text (not in cmd)
    }

    // gets a status (404, 300, 200 whatever)
    int getStatus(String url)
    {
        int status;
        try
        {
            URL testURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) testURL.openConnection();
            status = connection.getResponseCode();
        }
        catch (IOException e)
        {
            // makes it look much better when there are no errors. HttpURLConnection returns an error sometimes
            // where it would just be 404 anyway.
            return 404;
        }
        return status;
    }

    // reads the payloads from [PAYLOAD].txt and converts to a String[]
    String[] readPayload(String file) throws IOException
    {
        ArrayList<String> list = new ArrayList<>();

        try (BufferedReader bReader = new BufferedReader(new FileReader(file)))
        {
            String line;
            while ((line = bReader.readLine()) != null)
            {
                list.add(line);
            }
        }

        return list.toArray(new String[0]);
    }

    // tests if the request is in the user's list of blocked responses (i.e. 404)
    boolean isBlocked(int response, int[] responses)
    {
        for (int temp : responses)
        {
            if (temp == response)
            {
                return true;
            }
        }
        return false;
    }

    // separates args[2] into an int[] (i.e. 404,403,302 = [404, 403, 302])
    static int[] separateArgs(String arg)
    {
        String[] string = arg.split(",");
        int[] responses = new int[string.length];
        for (int i = 0; i < string.length; i++)
        {
            responses[i] = Integer.parseInt(string[i].trim());
        }
        return responses;
    }
}
