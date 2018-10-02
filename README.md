# DistributedSDN
These functional modules provide two key functions of UbiFlow: AP optimized allocation, and overlay structure. 
Among them, assigntoAP can be added to Floodlight code as a built-in module. The method of adding a custom module can refer to the official Floodlight documentation. 
https://floodlight.atlassian.net/wiki/spaces/floodlightcontroller/pages/1343513/How+to+Write+a+Module
The overlay works on the application layer of each controller node, directly calling the northbound RESTful API provided by Floodlight.
