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
    public static void reportDuplicates(final String stageFileName, final ArrayList<Memory> matches)
    {
        PrintWriter out = null;
        String outputPath = "Z:" + File.separator + "Imports" + File.separator + "Flagged" + File.separator + stageFileName + "-matches.txt";
        final File test = new File(outputPath);
        if (test.exists())
        {
            outputPath = "Z:" + File.separator + "Imports" + File.separator + "Flagged" + File.separator + stageFileName + new SimpleDateFormat("HH.mm.ss").format(new Date()) +
                    "-matches.txt";
        }

        try (final FileWriter fw = new FileWriter(outputPath, true))
        {
            final BufferedWriter bw = new BufferedWriter(fw);
            out = new PrintWriter(bw);

            for (final Memory pic : matches)
            {
                out.println(pic.getPath());
            }

            out.flush();
            out.close();

        } catch (final Exception ex)
        {
            ex.printStackTrace();
        } finally
        {
            if (out != null)
            {
                out.close();
            }
        }
    }
}