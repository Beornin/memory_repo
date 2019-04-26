package io;

import obj.Memory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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

    public static void movePassed(final Memory memory)
    {
        try
        {
            if (memory.isImported() && !memory.isMatched())
            {
                if (!new File(passedDir + memory.getName()).exists())
                {
                    Files.move(Paths.get(memory.getPath()), Paths.get(passedDir + memory.getName()));
                }
                else
                {
                    int num = 0;
                    String save;
                    File saveFile = new File(passedDir + memory.getName());
                    while (saveFile.exists())
                    {
                        save = (num++) + memory.getName();
                        saveFile = new File(passedDir + save);
                    }
                    Files.move(Paths.get(memory.getPath()), Paths.get(saveFile.toURI()));
                }
            }
        } catch (final IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    public static void moveImportFileMatched(final Memory memory)
    {
        try
        {
            if (memory.isImported() && memory.isMatched() && new File(memory.getPath()).exists())
            {
                if (!new File(flaggedDir + memory.getName()).exists())
                {
                    Files.move(Paths.get(memory.getPath()), Paths.get(flaggedDir + memory.getName()));
                }
                else
                {
                    int num = 0;
                    String save;
                    File saveFile = new File(flaggedDir + memory.getName());
                    while (saveFile.exists())
                    {
                        save = (num++) + memory.getName();
                        saveFile = new File(flaggedDir + save);
                    }
                    Files.move(Paths.get(memory.getPath()), Paths.get(saveFile.toURI()));
                }
            }
        } catch (final Exception e)
        {
            e.printStackTrace();
        }
    }
}