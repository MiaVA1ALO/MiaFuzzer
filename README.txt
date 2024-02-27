MiaFuzzer is a script designed to 'fuzz' a website (bruteforce the file directory) made because I didn't particularily like the layout of other fuzzing programs.
Simply sends requests (specified by a text file) to a website to test the response it gives.
Example: www.google.ca would be a 200 response but www.google.ca/404 would be a 404 response.
USAGES
java MiaFuzzer sitetofuzz payload blockedresponses
or
java MiaFuzzer sitetofuzz payload
Pseudo: www.site.domain/FUZZ payload.txt xxx,xxx,[...]
Pseudo: site.domain/index.FUZZ payload.txt -1
Pseudo: protocol://www.site.domain/FUZZ/index.html xxx
Example: google.com/search?q=FUZZ searchlist.txt -1
Example: https://talkingelectronics.com/FUZZ.html list.txt 404,403
Example: 192.168.2.1
