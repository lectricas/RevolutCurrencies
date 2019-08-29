### Task:

The app must download and update rates every 1 second using API
https://revolut.duckdns.org/latest?base=EUR

List all currencies you get from the endpoint (one per row). Each row has an input where you
can enter any amount of money. When you tap on currency row it should slide to top and its
input becomes first responder. When you’re changing the amount the app must simultaneously
update the corresponding value for other currencies.

Use any libraries and languages(java/kotlin) you want. Please, note that the solution should be production ready.

Video demo: https://youtu.be/omcS-6LeKoo 


### Possible issues:
1.  Not production ready.
2.  Keyboard unexpected behaviour. 
3.  Poor error handling.
4.  Unable to change the base currency when internet connection is lost.
5.  Most of the problems comes from poor understanding of Android UI thread execution order/understanding of how recyclerView works.
