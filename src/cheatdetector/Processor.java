package cheatdetector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/*
    This class is used to take some text file and do some simple operations on 
    it, like removing all white space or all C-style code comments.
*/
public class Processor {
    
    static enum Operation
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