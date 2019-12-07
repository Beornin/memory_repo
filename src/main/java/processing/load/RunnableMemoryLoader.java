package processing.load;

import io.CacheMemories;
import obj.Memory;
import obj.UserInput;
import org.apache.commons.io.FileUtils;
import processing.Shared;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RunnableMemoryLoader
{
    private static final String[] ALL_EXTENSIONS = new String[]{
            "gif", "png", "bmp", "jpg", "jpeg", "heic", "mp4", "mov", "mp4", "mov", "avi", "vlc", "wmv", "raw", "cr2", "tiff", "tif",
            "GIF", "PNG", "BMP", "JPG", "JPEG", "HEIC", "MP4", "MOV", "MP4", "MOV", "AVI", "VLC", "WMV", "RAW", "CR2", "TIFF", "TIF"
    };

    /**
     * This method gets all CURRENT memories. This should always be
     * pointed where the repo is and will try to utilize the cache if present.
     *
     * @param uio Input
     * @return List of memories
     */
    public static List<Memory> gatherCurrentRepoMemories(final UserInput uio)
    {
        final long startTime1 = System.nanoTime();
        final List<Memory> possibleMatches = RunnableMemoryLoader.loadCurrentMemories(uio);
        final long endTime1 = System.nanoTime();
        final long totalTime1 = endTime1 - startTime1;
        System.out.println(" took: " + Shared.printTotalTimeTaken(totalTime1) + " to get " + possibleMatches.size() + " files");
        return possibleMatches;
    }

    /**
     * This method gets NEW memories from the Import Folder
     *
     * @param uio Input
     * @return List of new memories
     */
    public static List<Memory> gatherMemories(final UserInput uio)
    {
        final long startTime1 = System.nanoTime();
        final List<Memory> possibleMatches = RunnableMemoryLoader.loadMemories(uio);
        final long endTime1 = System.nanoTime();
        final long totalTime1 = endTime1 - startTime1;
        System.out.println(" took: " + Shared.printTotalTimeTaken(totalTime1) + " to get " + possibleMatches.size() + " files");
        return possibleMatches;
    }

    /**
     * This loads memories specifically for the DateSortFolder
     *
     * @param userInput Input
     * @return List of memories to use in the date sort
     */
    public static List<Memory> loadDateSortMemories(final UserInput userInput)
    {
        final Collection<File> files = FileUtils.listFiles(userInput.getStartingFolder(), ALL_EXTENSIONS, true);
        final List<Memory> memories = Collections.synchronizedList(new ArrayList<>());

        //this would load any remaining files from CacheMemories into memories
        loadDateSortMemories(userInput, files, memories);
        userInput.populateRepoMetaData(memories);
        files.clear();

        return memories;
    }

    /**
     * Loads the CURRENT Repo Memories into a list using the cache file if available
     *
     * @param userInput Input
     * @return The List of current memories
     */
    private static List<Memory> loadCurrentMemories(final UserInput userInput)
    {
        final Collection<File> files = FileUtils.listFiles(userInput.getStartingFolder(), ALL_EXTENSIONS, true);
        final List<Memory> memories = Collections.synchronizedList(new ArrayList<>());

        //if the cache file exists, process it,. This can trim down the files collection and
        if (CacheMemories.cacheFileExists())
        {
            CacheMemories.readCurrentMemories(userInput, memories, files);
        }

        //this would load any remaining files from CacheMemories into memories
        loadMemories(userInput, files, memories);
        userInput.populateRepoMetaData(memories);
        files.clear();

        return memories;
    }

    /**
     * A more generic load Memories method, will not use the cache
     *
     * @param userInput Input
     * @return List of processed memories
     */
    public static List<Memory> loadMemories(final UserInput userInput)
    {
        final Collection<File> files = FileUtils.listFiles(userInput.getStartingFolder(), ALL_EXTENSIONS, true);
        final List<Memory> memories = Collections.synchronizedList(new ArrayList<>());

        loadMemories(userInput, files, memories);

        files.clear();

        return memories;
    }

    /**
     * A specific loading for the DateSortFolder
     *
     * @param userInput Input
     * @param files     Files that were not in the cache that exist
     * @param memories  Loaded memories
     */
    private static void loadDateSortMemories(final UserInput userInput, final Collection<File> files, final List<Memory> memories)
    {
        final ExecutorService pool = Executors.newFixedThreadPool(4);

        if (!files.isEmpty())
        {
            System.out.println("Loading memories..");
            for (final File file : files)
            {
                final Runnable memoryCreate = new MemoryCreateMeta(userInput, file, memories);
                pool.execute(memoryCreate);
            }
            try
            {
                pool.shutdown();
                //Wait for threads to all stop
                pool.awaitTermination(1, TimeUnit.DAYS);

            } catch (final InterruptedException ie)
            {
                ie.printStackTrace();
            }
        }
    }

    /**
     * The method that handles the thread pool runner to load memories
     * @param userInput Input
     * @param files The files to load into memories
     * @param memories The array list of memories to fill up
     */
    public static void loadMemories(final UserInput userInput, final Collection<File> files, final List<Memory> memories)
    {
        final ExecutorService pool = Executors.newFixedThreadPool(4);

        if (!files.isEmpty())
        {
            System.out.println("Loading memories..");
            for (final File file : files)
            {
                final Runnable memoryCreate = new MemoryCreate(userInput, file, memories);
                pool.execute(memoryCreate);
            }
            try
            {
                pool.shutdown();
                //Wait for threads to all stop
                pool.awaitTermination(1, TimeUnit.DAYS);

            } catch (final InterruptedException ie)
            {
                ie.printStackTrace();
            }
        }
    }
}