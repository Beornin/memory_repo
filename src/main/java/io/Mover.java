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