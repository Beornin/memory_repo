package io;

import obj.Memory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Reporter
{
    /**
     * This creates a text file to show what new memory matched a current memory
     *
     * @param stageFileName The memory checked for duplicate name
     * @param matches       The matches found
     */
    public static void reportDuplicates(final String stageFileName, final ArrayList<Memory> matches)
    {
        final PrintWriter out;
        String outputPath = "Z:" + File.separator + "Imports" + File.separator + "Flagged" + File.separator + stageFileName + "-matches.txt";
        final File test = new File(outputPath);
        if (test.exists())
        {
            outputPath = "Z:" + File.separator + "Imports" + File.separator + "Flagged" + File.separator + stageFileName + new SimpleDateFormat("HH.mm.ss").format(new Date()) +
                    "-matches.txt";
        }

        try
        {
            final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputPath, true));
            out = new PrintWriter(bufferedWriter);

            for (final Memory pic : matches)
            {
                out.println(pic.getPath());
            }

            out.flush();
            out.close();

        } catch (final Exception ex)
        {
            ex.printStackTrace();
        }
    }
}