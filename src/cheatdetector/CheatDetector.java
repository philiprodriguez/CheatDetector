package cheatdetector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Author Philip Rodriguez Summer 2017
 */
public class CheatDetector {
    public static void main(String[] args) throws IOException
    {
        if (args.length < 1)
        {
            printHelp();
        }
        else if (args[0].equals("-rawdistance") && args.length == 3)
        {
            Processor p1 = new Processor(new File(args[1]));
            Processor p2 = new Processor(new File(args[2]));
            
            System.out.println("Estimated computational operations required: " + (p1.getResult().length() * p2.getResult().length()));
            System.out.println("Computing edit ditsance...");
            
            int ed = editDistance(p1.getResult(), p2.getResult());
            double pc1 = ((ed/(double)p1.getResult().length())*100.0);
            double pc2 = ((ed/(double)p2.getResult().length())*100.0);
            double pc = Math.min(pc1, pc2);
            System.out.println("Edit distance is " + ed);
            System.out.println("Minimum percent difference between " + new File(args[1]).getName() + " and " + new File(args[2]).getName() + " is " + pc + "%");
        }
        else if (args[0].equals("-distance") && args.length == 3)
        {
            Processor p1 = new Processor(new File(args[1]));
            Processor p2 = new Processor(new File(args[2]));
            
            p1.applyOperation(Processor.Operation.REMOVE_COMMENTS);
            p1.applyOperation(Processor.Operation.REMOVE_WHITESPACE);
            p2.applyOperation(Processor.Operation.REMOVE_COMMENTS);
            p2.applyOperation(Processor.Operation.REMOVE_WHITESPACE);
            
            System.out.println("Estimated computational operations required: " + (p1.getResult().length() * p2.getResult().length()));
            System.out.println("Computing edit ditsance...");

            int ed = editDistance(p1.getResult(), p2.getResult());
            double pc1 = ((ed/(double)p1.getResult().length())*100.0);
            double pc2 = ((ed/(double)p2.getResult().length())*100.0);
            double pc = Math.min(pc1, pc2);
            System.out.println("Edit distance is " + ed);
            System.out.println("Minimum percent difference between " + new File(args[1]).getName() + " and " + new File(args[2]).getName() + " is " + pc + "%");
        }
        else if (args[0].equals("-compareall") && (args.length == 2 || args.length == 3))
        {
            double threshold = Double.parseDouble(args[1]);
            
            if (args.length == 3)
            {
                System.out.println("Output will be written to file " + args[2] + "");
            }
            else
            {
                System.out.println("No output file will be written at the end!");
            }
            
            File[] files = getAllFiles(null);
            
            System.out.println("Starting pairwise comparison of " + files.length + " files!");
            
            StringBuilder endResult = new StringBuilder();
            
            long startTime = System.currentTimeMillis();
            int num = 1;
            int last = (files.length*(files.length-1))/2;
            for(int i = 0; i < files.length; i++)
                for(int j = i+1; j < files.length; j++)
                {
                    System.out.print("Running comparison " + num + " of " + last + "...   ");
                    Processor p1 = new Processor(files[i]);
                    Processor p2 = new Processor(files[j]);

                    p1.applyOperation(Processor.Operation.REMOVE_COMMENTS);
                    p1.applyOperation(Processor.Operation.REMOVE_WHITESPACE);
                    p2.applyOperation(Processor.Operation.REMOVE_COMMENTS);
                    p2.applyOperation(Processor.Operation.REMOVE_WHITESPACE);
                    int ed = editDistance(p1.getResult(), p2.getResult());
                    double pc1 = ((ed/(double)p1.getResult().length())*100.0);
                    double pc2 = ((ed/(double)p2.getResult().length())*100.0);
                    double pc = Math.min(pc1, pc2);
                    
                    System.out.format("%3.2f", pc);
                    System.out.println("%\t" + getTimeRemaining(startTime, num, last));
                    if (pc < threshold)
                    {
                        System.out.println("Threshold breached by the following:");
                        System.out.println(files[i].getAbsolutePath());
                        System.out.println(files[j].getAbsolutePath());
                        System.out.println();
                        endResult.append("Minimum difference of " + pc + "% between " + files[i].getAbsolutePath() + " and " + files[j].getAbsolutePath() + System.lineSeparator() + System.lineSeparator());
                    }
                    num++;
                }
            System.out.println("Comparing complete!");
            System.out.println("End Results:");
            System.out.println(endResult.toString());
            if (args.length == 3)
            {
                System.out.println("Writing results to file " + args[2]);
                PrintWriter pw = new PrintWriter(new File(args[2]));
                pw.println(endResult.toString());
                pw.flush();
                pw.close();
                System.out.println("Done!");
            }
        }
        else if (args[0].equals("-comparedirs") && (args.length == 4 || args.length == 5))
        {
            double threshold = Double.parseDouble(args[1]);
            
            if (args.length == 5)
            {
                System.out.println("Output will be written to file " + args[4] + "");
            }
            else
            {
                System.out.println("No output file will be written at the end!");
            }
            
            File[] listOne = getAllFiles(args[2]);
            File[] listTwo = getAllFiles(args[3]);
            
            System.out.println("Starting comparison between " + listOne.length + " files and " + listTwo.length + " files!");
            
            StringBuilder endResult = new StringBuilder();
            
            long startTime = System.currentTimeMillis();
            int num = 1;
            int last = listOne.length*listTwo.length;
            for(int i = 0; i < listOne.length; i++)
                for(int j = 0; j < listTwo.length; j++)
                {
                    System.out.print("Running comparison " + num + " of " + last + "...   ");
                    Processor p1 = new Processor(listOne[i]);
                    Processor p2 = new Processor(listTwo[j]);

                    p1.applyOperation(Processor.Operation.REMOVE_COMMENTS);
                    p1.applyOperation(Processor.Operation.REMOVE_WHITESPACE);
                    p2.applyOperation(Processor.Operation.REMOVE_COMMENTS);
                    p2.applyOperation(Processor.Operation.REMOVE_WHITESPACE);
                    int ed = editDistance(p1.getResult(), p2.getResult());
                    double pc1 = ((ed/(double)p1.getResult().length())*100.0);
                    double pc2 = ((ed/(double)p2.getResult().length())*100.0);
                    double pc = Math.min(pc1, pc2);
                    
                    System.out.format("%3.2f", pc);
                    System.out.println("%\t" + getTimeRemaining(startTime, num, last));
                    if (pc < threshold)
                    {
                        System.out.println("Threshold breached by the following:");
                        System.out.println(listOne[i].getAbsolutePath());
                        System.out.println(listTwo[j].getAbsolutePath());
                        System.out.println();
                        endResult.append("Minimum difference of " + pc + "% between " + listOne[i].getAbsolutePath() + " and " + listTwo[j].getAbsolutePath() + System.lineSeparator() + System.lineSeparator());
                    }
                    num++;
                }
            System.out.println("Comparing complete!");
            System.out.println("End Results:");
            System.out.println(endResult.toString());
            if (args.length == 5)
            {
                System.out.println("Writing results to file " + args[4]);
                PrintWriter pw = new PrintWriter(new File(args[4]));
                pw.println(endResult.toString());
                pw.flush();
                pw.close();
                System.out.println("Done!");
            }
        }
        else if (args[0].equals("-rw") && args.length == 2)
        {
            Processor p1 = new Processor(new File(args[1]));
            p1.applyOperation(Processor.Operation.REMOVE_WHITESPACE);
            System.out.println(p1.getResult());
        }
        else if (args[0].equals("-rc") && args.length == 2)
        {
            Processor p1 = new Processor(new File(args[1]));
            p1.applyOperation(Processor.Operation.REMOVE_COMMENTS);
            System.out.println(p1.getResult());
        }
        else
        {
            printHelp();
        }
    }
    
    public static void printHelp()
    {
        System.out.println("~~~~~~~~~~Philip's Cheat Detection Tool~~~~~~~~~~");
        System.out.println("~                                               ~");
        System.out.println("~ This tool was designed for manipulating code  ~");
        System.out.println("~ files written in C or Java. It can help to    ~");
        System.out.println("~ determine if one source code file is the same ~");
        System.out.println("~ as another. It uses the edit distance to      ~");
        System.out.println("~ determine the similarity between two files.   ~");
        System.out.println("~ It also can be used to process text, such as  ~");
        System.out.println("~ removing whitespace or comments.              ~");
        System.out.println("~                                               ~");
        System.out.println("~             Philip Rodriguez 2017             ~");
        System.out.println("~                                               ~");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("java CheatDetection [operation]");
        System.out.println();
        System.out.println("Valid operations:");
        System.out.println("-rawdistance [file1] [file2]");
        System.out.println();
        System.out.println("Computes the edit distance between file1 and file2 with no additional processing, and prints that out along with a percentage that represents the edit distance over the number of characters in file 1.");
        System.out.println();
        System.out.println();
        System.out.println("-distance [file1] [file2]");
        System.out.println();
        System.out.println("Computes the edit distance between file1 and file2 after first removing whitespace and comments from both files' content.");
        System.out.println();
        System.out.println();
        System.out.println("-rw [file]");
        System.out.println();
        System.out.println("Removes the whitespace from file and prints the reuslt to the screen.");
        System.out.println();
        System.out.println();
        System.out.println("-rc [file]");
        System.out.println();
        System.out.println("Removes the comments from file and prints the result to the screen.");
        System.out.println();
        System.out.println();
        System.out.println("-compareall [threshold] [optional output file]");
        System.out.println();
        System.out.println("Performs the distance comparison pairwise between every two code files (*.c, *.h, *.java) in the working directory. Prints a message for any pair whose percent difference is under the percentage threshold, and also repeats at the end a list of pairs that fell below the threshold. If you give it an output file argument, this will print the end results to an output file of that name in the working directory.");
        System.out.println();
        System.out.println();
        System.out.println("-comparedirs [threshold] [path to first directory] [path to second directory] [optional output file]");
        System.out.println();
        System.out.println("Performs the distance comparison for each file in the first directory with each file in the second directory (*.c, *.h, *.java). Prints a message for any pair whose percent difference is under the percentage threshold, and also repeats at the end a list of pairs that fell below the threshold. If you give it an output file argument, this will print the end results to an output file of that name in the working directory.");
    }
    
    /*
        Given the start time in milliseconds and the number of comparisons
        already completed and the number of total comparisons required, this
        method returns a string of the format hh:mm:ss that represents the
        estimated time remaining for all comparisons to be finished.
    */
    public static String getTimeRemaining(long startTime, int complete, int total)
    {
        double secondsElapsed = (System.currentTimeMillis()-startTime)/1000.0;
        double avgSpeed = complete/secondsElapsed;
        int remainingComparisons = total-complete;
        long seconds = Math.round(remainingComparisons/avgSpeed);
        
        long hours = seconds/3600;
        seconds = seconds - (hours*3600);
        long minutes = seconds/60;
        seconds = seconds - (minutes*60);
        
        return String.format("(Time Remaining %d:%02d:%02d)", hours, minutes, seconds);
    }
    
    /*
        This method recursively searches for files in the directory located at
        the path represented by [dirPath] which have a code extention of .c,
        .java, or .h. Then, it returns all files it finds. Pass it the empty
        string if you want to use the current working directory as the parent
        directory to search in.
    */
    public static File[] getAllFiles(String dirPath)
    {
        File searchDirectory = null;
        if (dirPath != null && !dirPath.equals(""))
            searchDirectory = new File(dirPath);
        else
            searchDirectory = new File(Paths.get(".").toAbsolutePath().normalize().toString());
        
        ArrayList<File> results = new ArrayList<>();
        Queue<File> files = new LinkedList<File>();
        
        for(File f : searchDirectory.listFiles())
            files.add(f);
        
        while(!files.isEmpty())
        {
            File f = files.poll();
            if (f.isDirectory())
            {
                files.addAll(Arrays.asList(f.listFiles()));
            }
            else if (f.getName().endsWith(".c") || f.getName().endsWith(".java") || f.getName().endsWith(".h"))
            {
                results.add(f);
            }
        }
        return results.toArray(new File[]{});
    }
    
    /*
        This method returns the edit distance between [s1] and [s2]. It was ported
        from a memoized solution. I bet we can space-save this in the future, since
        it seems like it only looks one row down at most...
    */
    public static int editDistance(String s1, String s2) {
        //Pathological cases
        if (s1.length() == 0)
        {
            return s2.length();
        }
        if (s2.length() == 0)
        {
            return s1.length();
        }
        
        int[][] editDistance = new int[s1.length()][s2.length()];
        
        for(int index1 = s1.length()-1; index1 >= 0; index1--)
        {
            for(int index2 = s2.length()-1; index2 >= 0; index2--)
            {
                //We are going to assume we are making s1 become like s2.
                
                //If there is no conflict here, then just move along!
                if (s1.charAt(index1) == s2.charAt(index2))
                {
                    editDistance[index1][index2] = getEditDistance(editDistance, index1+1, index2+1);
                }
                else
                {
                    //There is a conflict at this point, so we must resolve it!
                    
                    //Suppose we insert the correct character into s1...
                    int insert = 1+getEditDistance(editDistance, index1, index2+1);

                    //Suppose we delete a char in s1...
                    int delete = 1+getEditDistance(editDistance, index1+1, index2);

                    //Suppose we change the char in s1 to be like s2...
                    int substitute = 1+getEditDistance(editDistance, index1+1, index2+1);

                    editDistance[index1][index2] = Math.min(insert, Math.min(delete, substitute));
                }
            }
        }
        
        return editDistance[0][0];
    }
    
    /*
        This is a helper method for accessing the editDistance array. It is
        primarily to easily handle going out of bounds, etc.
    */
    private static int getEditDistance(int[][] editDistance, int index1, int index2)
    {
        int s1Length = editDistance.length;
        int s2Length = editDistance[0].length;
        
        //If we are essentially out of bounds for s1, then we'll need to delete
        //any remaining characters of s2...
        if (index1 >= s1Length)
        {
            return s2Length-index2;
        }
        
        //Similarly, for being out of bounds in s2, delete all remaining s1
        //characters to bring it into spec...
        if (index2 >= s2Length)
        {
            return s1Length-index1;
        }
        
        //If we are in bounds, then just return what we already know!
        return editDistance[index1][index2];
    }
    /*
    private static int editDistanceDP(String s1, String s2)
    {
        memo = new int[s1.length()][s2.length()];
        for(int r = 0; r < memo.length; r++)
            Arrays.fill(memo[r], -1);
        return editDistanceDP(s1, s2, 0, 0);
    }
    
    private static int[][] memo;
    private static int editDistanceDP(String s1, String s2, int index1, int index2) {
        if (index1 >= s1.length()) {
            //we must insert everything left in s2 into s1 to finish it up!
            return s2.length() - index2;
        }
        if (index2 >= s2.length())
        {
            //we must delete everything left in s1
            return s1.length() - index1;
        }
        
        //Did we already solve this state?
        if (memo[index1][index2] != -1)
        {
            return memo[index1][index2];
        }

        //What if we have a match?
        if (s1.charAt(index1) == s2.charAt(index2)) 
        {
            return editDistanceDP(s1, s2, index1 + 1, index2 + 1);
        }
        else
        {
            //Suppose we insert, substitute, or delete char at index1...
            //insert
            int insert = 1 + editDistanceDP(s1, s2, index1, index2 + 1);
            
            //suppose we substitute
            int substitute = 1 + editDistanceDP(s1, s2, index1 + 1, index2 + 1);
            
            //suppose we delete
            int delete = 1 + editDistanceDP(s1, s2, index1 + 1, index2);
            
            return memo[index1][index2] = Math.min(insert, Math.min(substitute, delete));
        }
    }
    */
}

/*
    This class is used to take some text file and do some simple operations on 
    it, like removing all white space or all C-style code comments.
*/
class Processor {
    
    public static enum Operation
    {
        REMOVE_WHITESPACE, REMOVE_COMMENTS
    }
    
    private String result;
    
    public Processor(File file) throws FileNotFoundException, IOException
    {
        StringBuilder fileContent = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String line = null;
        while((line = br.readLine()) != null)
        {
            fileContent.append(line + System.lineSeparator());
        }
        result = fileContent.toString();
    }
    
    public String getResult()
    {
        return result;
    }
    
    private int place = 0;
    
    private char peekChar()
    {
        if (place >= result.length())
            return 0;
        return result.charAt(place);
    }
    
    private char nextChar()
    {
        place++;
        return peekChar();
    }
    
    public void applyOperation(Operation operation)
    {
        place = 0;
        StringBuilder output = new StringBuilder();
        while(peekChar() != 0)
        {
            if (peekChar() == '/' && operation == Operation.REMOVE_COMMENTS)
            {
                nextChar();
                if (peekChar() == '/')
                {
                    //We're in a comment!
                    while(nextChar() != '\n')
                    {
                        //Skip stuff until end of line...
                    }
                }
                else if (peekChar() == '*')
                {
                    //We're in a block comment!
                    nextChar();
                    while(true)
                    {
                        if (peekChar() == '*')
                        {
                            nextChar();
                            if (peekChar() == '/')
                            {
                                //We hit the end of the comment!
                                nextChar();
                                break;
                            }
                        }
                        else
                        {
                            nextChar();
                        }
                    }
                }
                else
                {
                    //Must have been a lone slash in code...
                    output.append('/');
                }
            }
            else if (isWhitespace(peekChar()) && operation == Operation.REMOVE_WHITESPACE)
            {
                //Eat whitespace...
                while(isWhitespace(peekChar()))
                {
                    nextChar();
                }
            }
            else
            {
                output.append(peekChar());
                nextChar();
            }
        }
        result = output.toString();
    }
    
    private boolean isWhitespace(char c)
    {
        return c == '\n' || c == ' ' || c == '\t' || c == '\r';
    }
}

