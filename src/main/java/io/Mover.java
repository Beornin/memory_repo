package io;

import obj.Memory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This class handles any file IO
 */
public class Mover
{
    private final static String STAGE_DIR = "Z:" + File.separator + "Imports" + File.separator + "Stage" + File.separator;
    private final static String PASSED_DIR = "Z:" + File.separator + "Imports" + File.separator + "Pass" + File.separator;
    private final static String FLAGGED_DIR = "Z:" + File.separator + "Imports" + File.separator + "Flagged" + File.separator;
    private final static String IMPORT_DIR = "C:" + File.separator + "Users" + File.separator + "Ben" + File.separator + "Google Drive" + File.separator + "Thomas Transfer";

    /**
     * This method moves files from an imported dir to the staging folder
     */
    public static void moveToStaging()
    {
        final File[] fList = new File(IMPORT_DIR).listFiles();
        if (fList != null)
        {
            for (final File currentFile : fList)
            {
                try
                {
                    Files.move(currentFile.toPath(), Paths.get(STAGE_DIR + currentFile.getName()));
                    if (!new File(STAGE_DIR + currentFile.getName()).exists())
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

    /**
     * This moves the memory passed in to the passed folder
     *
     * @param memory The memory not flagged as duplicate
     */
    public static void movePassed(final Memory memory)
    {
        try
        {
            if (memory.isImported() && !memory.isMatched())
            {
                if (!new File(PASSED_DIR + memory.getName()).exists())
                {
                    Files.move(Paths.get(memory.getPath()), Paths.get(PASSED_DIR + memory.getName()));
                }
                else
                {
                    int num = 0;
                    String save;
                    File saveFile = new File(PASSED_DIR + memory.getName());
                    while (saveFile.exists())
                    {
                        save = num++ + memory.getName();
                        saveFile = new File(PASSED_DIR + save);
                    }
                    Files.move(Paths.get(memory.getPath()), Paths.get(saveFile.toURI()));
                }
            }
        } catch (final IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    /**
     * This moves memories that are flagged as duplicate to the flagged folder
     *
     * @param memory The duplicate memory
     */
    public static void moveImportFileMatched(final Memory memory)
    {
        try
        {
            if (memory.isImported() && memory.isMatched() && new File(memory.getPath()).exists())
            {
                if (!new File(FLAGGED_DIR + memory.getName()).exists())
                {
                    Files.move(Paths.get(memory.getPath()), Paths.get(FLAGGED_DIR + memory.getName()));
                }
                else
                {
                    int num = 0;
                    String save;
                    File saveFile = new File(FLAGGED_DIR + memory.getName());
                    while (saveFile.exists())
                    {
                        save = num++ + memory.getName();
                        saveFile = new File(FLAGGED_DIR + save);
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