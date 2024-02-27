import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

// Sends requests to a website to test the response it gives.
// Example: www.google.ca would be a 200 response but www.google.ca/404 would be a 404.
// Useful for bruteforcing website directories.

/**
 * README.txt
 *
 * Usage: MiaFuzzer www.url.com [PAYLOAD].txt blockedresponse,blockedresponse,[...]
 * www.url.com is the website you wish to fuzz. The url should have FUZZ somewhere within.
 * Example: www.url.com/FUZZ, or www.url.com/link.FUZZ, etc
 *
 * [PAYLOAD].txt is the directory of the list of payloads, each separated by a new line.
 * Example: If your URL is www.url.com/FUZZ and your .txt file says:
 * index
 * test
 * 123
 * the URLs www.url.com/index, www.url.com/test, and www.url.com/123 will be tested.
 * The location is relative to the .class file.
 *
 * blockedresponse,blockedresponse,[...] are the different HTTP responses you want to ignore.
 * Ones commonly useless are 404, 403, etc.
 * This argument is passed as 404,403,402 and you can add as many as needed.
 *
 * Example arguments:
 * java MiaFuzzer www.url.com/FUZZ.php special_payloads.txt 404,403,302,301
 */
public class MiaFuzzer
{
    public static final String logo = "#     # ###    #    #######\n##   ##  #    # #   #       #    # ###### ###### ###### #####\n# # # #  #   #   #  #       #    #     #      #  #      #    #\n#  #  #  #  #     # #####   #    #    #      #   #####  #    #\n#     #  #  ####### #       #    #   #      #    #      #####\n#     #  #  #     # #       #    #  #      #     #      #   #\n#     # ### #     # #        ####  ###### ###### ###### #    #\n";
    public static void main(String[] args)
    {
        String URL;
        if(args.length != 3)
        {
            System.out.println("Usage: java MiaFuzzer www.url.com [PAYLOAD].txt blockedresponse,blockedresponse,[...]");
        }
        else
        {
            URL = args[0];
            System.out.println(logo);
            // prevents an error from the URL being passed as www.example.com without an http
            if(!URL.contains("http"))
            {
                URL = "http://" + URL;
            }
            // for the script to function properly the URL needs to contain 'FUZZ'
            if(!URL.contains("FUZZ"))
            {
                URL = URL + "FUZZ";
                System.err.println("URL does not contain 'FUZZ'. URL automatically set to " + URL);
            }
            new MiaFuzzer(URL, args[1], separateResponseArg(args[2]));
            System.out.println(Arrays.toString(separateResponseArg(args[2])));
        }
    }

    // initializer method, controls all the major functions
    public MiaFuzzer(String URL, String filePath, int[] blockedResponses)
    {
        int totalRequests = 0;
        int blockedRequests = 0;
        int allowedRequests = 0;
        String[] payloads = new String[0];
        try
        {
            // opens [PAYLOAD].txt
            payloads = readPayloads(filePath);
        }
        catch (IOException e)
        {
            // not specifically a file not found error but I think that's the only possible one
            System.err.println("File not found: " + filePath);
        }
        for (String payload : payloads)
        {
            // replaces 'FUZZ' with something else - i.e. /index.html or something
            String temp = URL.replace("FUZZ", payload);
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
        System.err.println("Blocked requests: " + blockedRequests);
    }

    int getStatus(String URL)
    {
        int statusCode;
        try
        {
            URL testURL = new URL(URL);
            HttpURLConnection connection = (HttpURLConnection) testURL.openConnection();
            statusCode = connection.getResponseCode();
        }
        catch (IOException e)
        {
            // makes it look much better when there are no errors. HttpURLConnection returns an error sometimes
            // where it would just be 404 anyway.
            return 404;
        }
        return statusCode;
    }

    // reads the payloads from [PAYLOAD].txt and converts to a String[]
    String[] readPayloads(String filePath) throws IOException
    {
        ArrayList<String> payloadList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath)))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                payloadList.add(line);
            }
        }

        return payloadList.toArray(new String[0]);
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
    static int[] separateResponseArg(String arg)
    {
        String[] splitString = arg.split(",");
        int[] responses = new int[splitString.length];
        for (int i = 0; i < splitString.length; i++)
        {
            responses[i] = Integer.parseInt(splitString[i].trim());
        }
        return responses;
    }
}
