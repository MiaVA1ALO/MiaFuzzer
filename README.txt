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