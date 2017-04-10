PROJECT TITLE: Implementing a Reliable Transport Protocol
DATE: April.9th.2017
AUTHORS: Darren Norton, Yizhong Chen

Fair warning:
    This project was developed in Intellij IDEA, and uses a build structure such
    that the build directory is Project2/production. If the program does not accept
    your input files, we have left a built in working directory detector. Please
    adjust using that.

    The unit tests provided in the /tests folder are run using Junit5, and will
    likely not run on BlueJ. We don't know how to get the library for this, but
    all 42 unit tests ran as of 04/09/2017.

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
	If the program has not yet been compiled, follow the first and second steps
	in “HOW TO START THIS PROJECT”

	Then, run the experiments, through the command “java ExperimentController”