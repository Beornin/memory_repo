package io;

import obj.TestObj;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Mover
{
    private final static String stageDir = "Z:" + File.separator + "Imports" + File.separator + "Stage" + File.separator;
    private final static String passedDir = "Z:" + File.separator + "Imports" + File.separator + "Pass" + File.separator;
    private final static String flaggedDir = "Z:" + File.separator + "Imports" + File.separator + "Flagged" + File.separator;
    private final static String importDir = "C:" + File.separator + "Users" + File.separator + "Ben" + File.separator + "Google Drive" + File.separator + "Thomas Transfer";

    public static void moveToStaging()
    {
        final File[] fList = new File(importDir).listFiles();
        if (fList != null)
        {
            for (final File currentFile : fList)
            {
                try
                {
                    Files.move(currentFile.toPath(), Paths.get(stageDir + currentFile.getName()));
                    if (!new File(stageDir + currentFile.getName()).exists())
                    {
                        if (Files.deleteIfExists(currentFile.toPath()))
                        {
                            System.out.println("File not deleted from GDrive! :" + currentFile.getName());
                        }
                        else
                        {
                            System.out.println("File WAS deleted from GDrive! :" + currentFile.getName());
                        }
                    }
                    else
                    {
                        System.out.println("File not moved to staging! :" + currentFile.getName());
                    }
                } catch (final IOException ioe)
                {
                    ioe.printStackTrace();
                }
            }
        }
    }

    public static void movePassed(final TestObj stageFile)
    {
        try
        {
            if (stageFile.isImported() && !stageFile.isMatched() && !new File(passedDir + stageFile.getPath().replace(stageDir, "")).exists())
            {
                Files.move(Paths.get(stageFile.getPath()), Paths.get(passedDir + stageFile.getName()));
            }
            else
            {
                Files.move(Paths.get(stageFile.getPath()), Paths.get(passedDir + stageFile.getPath().replace(stageDir, "") + "(" + new SimpleDateFormat("HH.mm.ss").format(new Date()) + ")"));
            }
        } catch (final IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    public static void moveImportFileMatched(final TestObj test1)
    {
        try
        {
            if (test1.isImported() && new File(test1.getPath()).exists() && !new File(flaggedDir + test1.getPath().replace(stageDir, "")).exists())
            {
                Files.move(Paths.get(test1.getPath()), Paths.get(flaggedDir + test1.getName()));
            }
            else
            {
                Files.move(Paths.get(test1.getPath()), Paths.get(flaggedDir + test1.getPath().replace(stageDir, "") + "(" + new SimpleDateFormat("HH.mm.ss").format(new Date()) + ")"));
            }
        } catch (final Exception e)
        {
            e.printStackTrace();
        }
    }
}