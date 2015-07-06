package tweetAnalysis;
/*###############################################################################################################################################

 #							Tweet Analysis Application	        						#

 #							 AUTHOR: DEEPTI DESHPANDE 	          						#

 #				OBJECTIVE: Analyze unique word count and median of unique words per tweet	    			        #

 ###############################################################################################################################################*/



import java.io.IOException;

import java.util.*;



import org.apache.hadoop.fs.Path;

import org.apache.hadoop.io.*;

import org.apache.hadoop.mapred.*;



public class TweetAnalysisApp {

/*

* Reads the input file from the HDFS path passed in args[0] and tokenizes

* each line of the input file and then emits <word, 1>

*/



public static class MapForWordCount extends MapReduceBase implements

Mapper<LongWritable, Text, Text, IntWritable> {

private final static IntWritable one = new IntWritable(1);

private Text word = new Text();



public void map(LongWritable key, Text value,

OutputCollector<Text, IntWritable> output, Reporter reporter)

throws IOException {

String line = value.toString();

StringTokenizer tokenizer = new StringTokenizer(line);

while (tokenizer.hasMoreTokens()) {

word.set(tokenizer.nextToken());

output.collect(word, one);

}

}

}



/*

* Reads the input file from the HDFS path passed in args[1] and splits the

* line based on space as delimiter and puts it in the hash set. This helps

* to get the unique set of words in the line. For each line read it counts

* the unique word count and finds the median with existing median thus far.

* The mapper only emits the <median,null> pair

*/

public static class MapForMedian extends MapReduceBase implements

Mapper<LongWritable, Text, DoubleWritable, NullWritable> {

private double runningMedian = 0;

private int count = 0;



public void map(LongWritable key, Text value,

OutputCollector<DoubleWritable, NullWritable> output,

Reporter reporter) throws IOException {

String line = value.toString();

Set<String> st = new HashSet<String>(Arrays.asList(line.split(" ")));

count++;

runningMedian += st.size();

double median = (double) (runningMedian / (double) count);

output.collect(new DoubleWritable(median), NullWritable.get());

}

}



/*

* This reducer is to find the total count of each uniqie words in the

* tweets.txt file for each <word,[1,1,1,..]> pair from the mapper, the

* reducer iterates over the key values and finds the number of occurance of

* the work in the file It emmits <word, count> in the output file

*/

public static class ReduceForWordCount extends MapReduceBase implements

Reducer<Text, IntWritable, Text, IntWritable> {

public void reduce(Text key, Iterator<IntWritable> values,

OutputCollector<Text, IntWritable> output, Reporter reporter)

throws IOException {

int count = 0;

while (values.hasNext()) {

count += values.next().get();

}

output.collect(key, new IntWritable(count));

}

}



/*

* DRIVER FUNCTION

*/

public static void main(String[] args) throws Exception {

// job file instantiation

JobConf conf = new JobConf(TweetAnalysisApp.class);

conf.setJobName("wordcount");

conf.setMapperClass(MapForWordCount.class);

conf.setReducerClass(ReduceForWordCount.class);

conf.setInputFormat(TextInputFormat.class);

conf.setOutputFormat(TextOutputFormat.class);

conf.setOutputKeyClass(Text.class);

conf.setOutputValueClass(IntWritable.class);

FileInputFormat.setInputPaths(conf, new Path(args[0]));// input

// directory in

// dfs

FileOutputFormat.setOutputPath(conf, new Path(args[1]));// output

// directory in

// dfs



JobClient.runJob(conf);



// job file instantiation

JobConf conf1 = new JobConf(TweetAnalysisApp.class);

conf1.setJobName("median");

conf1.setMapperClass(MapForMedian.class);

conf1.setInputFormat(TextInputFormat.class);

conf1.setOutputFormat(TextOutputFormat.class);

conf1.setOutputKeyClass(DoubleWritable.class);

conf1.setOutputValueClass(NullWritable.class);

FileInputFormat.setInputPaths(conf1, new Path(args[0]));// input

// directory in

// dfs

FileOutputFormat.setOutputPath(conf1, new Path(args[2]));// output

// directory

// in dfs



JobClient.runJob(conf1);

}

}
