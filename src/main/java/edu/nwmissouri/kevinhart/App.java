package edu.nwmissouri.kevinhart;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;
import java.util.Arrays;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Comparator;
import org.apache.commons.io.*;

/**
 * Hello world!
 */
public final class App {
    private App() {
    }

    private static void process(String fileName){
        SparkConf sparkConf = new SparkConf().setMaster("local").setAppName("Word Counter");

        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);

        JavaRDD<String> inputFile = sparkContext.textFile(fileName);

        JavaRDD<String> wordsFromFile = inputFile.flatMap(content -> Arrays.asList(content.split(" ")).iterator());

        JavaPairRDD<String, Integer> countData = wordsFromFile.mapToPair(t -> new Tuple2(t, 1)).reduceByKey((x, y) -> (int) x + (int) y);

        JavaPairRDD<Integer, String> ctData = countData.mapToPair(p -> new Tuple2(p._2, p._1)).sortByKey(Comparator.reverseOrder());
        
        String outputFolder = "output";

        Path path = FileSystems.getDefault().getPath(outputFolder);
        FileUtils.deleteQuietly(path.toFile());

        ctData.saveAsTextFile(outputFolder);
    }

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("No files provided.");
            System.exit(0);
        }
        
        process(args[0]);
    }
}
