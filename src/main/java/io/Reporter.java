package io;

import obj.TestObj;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Reporter
{
    public static void reportDuplicates(final String stageFileName, final ArrayList<TestObj> matches)
    {
        PrintWriter out = null;
        try (final FileWriter fw = new FileWriter("Z:" + File.separator + "Imports" + File.separator + "Flagged" + File.separator + stageFileName + "-matches.txt", true))
        {
            final BufferedWriter bw = new BufferedWriter(fw);
            out = new PrintWriter(bw);

            for (final TestObj pic : matches)
            {
                out.println(pic.getPath());
            }
            out.println("\n\n");

            out.flush();
            out.close();

        } catch (final Exception ignored)
        {

        } finally
        {
            if (out != null)
            {
                out.close();
            }
        }
    }
}