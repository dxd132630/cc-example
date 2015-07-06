hadoop dfs -mkdir tweet_input
hadoop dfs -mkdir tweet_output
hadoop dfs -put tweet.txt tweet_input/
hadoop jar TweetAnalysis tweet_input tweet_output/ft1.txt tweet_output/ft2.txt
