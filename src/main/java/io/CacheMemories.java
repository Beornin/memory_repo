package io;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import obj.Memory;
import obj.UserInput;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class handles
 * 1) saving the current memories as a file
 * 2) reading this file to the current list
 * 3) checking the current list against the repo to determine if any files are missing or added
 */
public class CacheMemories
{
    private final static String CACHE_NAME = "Y:\\SharedFolder\\Pictures and Videos\\cache.beo";

    public static boolean cacheFileExists()
    {
        return new File(CACHE_NAME).exists();
    }

    private static boolean deleteCacheFile()
    {
        return new File(CACHE_NAME).delete();
    }

    public static void cacheCurrentMemories(final List<Memory> currentMemories)
    {
        try
        {
            if (cacheFileExists())
            {
                if (deleteCacheFile())
                {
                    System.out.println("Writing Memories to cache at: " + CACHE_NAME);
                    final FileOutputStream fos = new FileOutputStream(CACHE_NAME);
                    final ObjectOutputStream oos = new ObjectOutputStream(fos);
                    for (final Memory memory : currentMemories)
                    {
                        memory.setMatched(false);
                    }
                    oos.writeObject(currentMemories);
                    oos.close();
                    System.out.println("Done writing to cache...");
                }
            } else
            {
                System.out.println("Writing Memories to cache at: " + CACHE_NAME);
                final FileOutputStream fos = new FileOutputStream(CACHE_NAME);
                final ObjectOutputStream oos = new ObjectOutputStream(fos);
                for (final Memory memory : currentMemories)
                {
                    memory.setMatched(false);
                }
                oos.writeObject(currentMemories);
                oos.close();
                System.out.println("Done writing to cache...");
            }
        } catch (final IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void readCurrentMemories(final UserInput userInput, final List<Memory> memories, final Collection<File> files)
    {
        try
        {
            System.out.println("Reading cached Memories...");
            final List<Memory> memoriesToDelete = new ArrayList<>(0);
            final List<File> filesToDelete = new ArrayList<>(0);
            final FileInputStream fis = new FileInputStream(CACHE_NAME);
            final ObjectInputStream ois = new ObjectInputStream(fis);
            memories.addAll((List<Memory>) ois.readObject());
            ois.close();
            System.out.println("Memories loaded from cache: " + memories.size());

            //remove any memories from the cache that the file no longer exists
            for (final Memory memory : memories)
            {
                boolean found = false;
                for (final File file : files)
                {
                    if (file.getPath().equals(memory.getPath()))
                    {
                        found = true;
                        break;
                    }
                }
                if (found)
                {
                    //remove the file so we don't process the same memory
                    filesToDelete.add(memory.getFile());

                    //since Metadata is not serializable, fill in any that should have it
                    if (memory.isMetaDataLoaded())
                    {
                        try
                        {
                            final Metadata metadata = ImageMetadataReader.readMetadata(memory.getFile());
                            memory.setMetadata(metadata);

                            final ExifIFD0Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
                            memory.setWidth(Integer.parseInt(exifIFD0Directory.getString(ExifIFD0Directory.TAG_IMAGE_WIDTH)));
                            memory.setHeight(Integer.parseInt(exifIFD0Directory.getString(ExifIFD0Directory.TAG_IMAGE_HEIGHT)));

                            final ExifSubIFDDirectory exifSubIFDDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
                            memory.setDate(exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL));

                        } catch (final ImageProcessingException | IOException | NullPointerException ipe)
                        {
                            System.out.println("Error loading memory during cache read Metadata: " + memory.getFile().getName());
                            ipe.printStackTrace();
                        }
                    }
                } else
                {
                    //file no longer exists, remove the memory
                    memoriesToDelete.add(memory);
                }
            }
            memories.removeAll(memoriesToDelete);
            System.out.println("Memories skipped from cache loading: " + filesToDelete.size());
            files.removeAll(filesToDelete);

            System.out.println("Finished reading cached Memories...");
        } catch (final IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}