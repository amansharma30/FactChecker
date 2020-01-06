Steps to execute the code
	
	1. Run as maven install to download all the required jars and dependencies.
	2. Use a compatible JDK and JRE to run the code (in case of execution errors because of mismatch in JDK and JRE such as JNIEXception).
	3. Set Text file encoding of the IDE to 'UTF-8' under Preference > Workspace > Text file encoding
	4. Set the variables 'inputfilepath' and 'outputfilepath' in the configuration file  named 'configurationFile.txt' kept in the classpath. These are the input file and output file paths.
	5. Run the java class named 'UnSupervisedChecker.java' kept in the package org.util.
	6. After the execution gets finished (a message will appear as "file written at {outputfilepath}" ), a result file is generated under the path 'outputfilepath'.
	
The whole SNLP2019_test.tsv file takes close to 30 minutes.