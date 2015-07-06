/*###############################################################################################################################################

 #							Tweet Analysis Application	        						#

 #							 AUTHOR: DEEPTI DESHPANDE 	          						#

 #				OBJECTIVE: Analyze unique word count and median of unique words per tweet	    			        #

 ###############################################################################################################################################*/

This application is implemented in hadoop. It can be integrated with spark tweeter API for live streaming of tweets.

1. Application is a scalable tool to read tweet data from tweet_input directory and analyze the count of unique words in the tweet.txt file.
  And also to find the median of the unique words in each line read so far.
2. For each line read from the file the line is tokenized into words and pair of <word, 1> is emitted by MapForWordCount at the end of shuffle phase 
  we have the key value pair of <word,[1,1..1]> where repeated words in the whole file are put together. RudeceForWordCount counts the number of ones 
  the value part of the maper output. This counts the number of times the word apeared in the file. Output of reduce is written to tweet_output folder
  with <word, count>
3. For each line read from the file the line is split based on space and put it in HashSet This helps to get the unique set of words in the line. For 
  each line read it counts the unique word and finds the median with existing median thus far.The mapper only emits the <median,null> pair.Output of 
  reduce is written to tweet_output folder

Required Libraries:	
1. hadoop-mapred-0.21.0.jar.zip
2. hadoop-core-0.20.2.jar
3. hadoop-common-0.21.0.jar.zip

Execution instruction:
1. Add the required libraries to the Java project with the TweetAnalysis.java class
2. Export the jar of the project onto the hadoop cluster with single node or multiple node
3. Create the tweet input and output directories:
	hadoop dfs -mkdir tweet_input
	hadoop dfs -mkdir tweet_output
4. Put the tweet.txt file onto HDFS
	hadoop dfs -put tweet.txt tweet_input/
5. Execute the hadoop map-reduce application
	hadoop jar TweetAnalysis tweet_input tweet_output/ft1.txt tweet_output/ft2.txt
