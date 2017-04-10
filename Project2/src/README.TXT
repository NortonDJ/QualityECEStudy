PROJECT TITLE: Implementing a Reliable Transport Protocol
DATE: April.9th.2017
AUTHORS: Darren Norton, Yizhong Chen

HOW TO START THIS PROJECT: 
	1. Open terminal. 
	2. Then Go to “/src” directory, compile the program by 
	   “javac ExperimentController.java”
	3. Alternatively, you can also run the program through “Network Simulator”:
	   “java NetworkSimulator …” with seven following parameters:
		@param args[0] file with messages
      		@param args[1] time between messages (int)
      		@param args[2] loss probability (float)
      		@param args[3] corruption probability (float)
      		@param args[4] window size (int)
      		@param args[5] protocol type (int)
      		@param args[6] debugging trace (int)

HOW TO GET DATA:
	If the program has not been compile, follow first and second step in “HOW TO START 	THIS PROJECT”

	Then, run the program, by “java ExperimentController”