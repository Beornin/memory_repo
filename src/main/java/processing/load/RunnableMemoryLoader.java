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

    public static List<Memory> gatherCurrentFiles(final UserInput uio, final boolean cache)
    {
        final long startTime1 = System.nanoTime();

        final List<Memory> possibleMatches = RunnableMemoryLoader.loadCurrentMemories(uio, cache);

        final long endTime1 = System.nanoTime();
        final long totalTime1 = endTime1 - startTime1;
        System.out.println(" took: " + Shared.printTotalTimeTaken(totalTime1) + " to get " + possibleMatches.size() + " files");

        return possibleMatches;
    }

    public static List<Memory> gatherNewFiles(final UserInput uio)
    {
        final long startTime1 = System.nanoTime();

        final List<Memory> possibleMatches = RunnableMemoryLoader.loadNewMemories(uio);

        final long endTime1 = System.nanoTime();
        final long totalTime1 = endTime1 - startTime1;
        System.out.println(" took: " + Shared.printTotalTimeTaken(totalTime1) + " to get " + possibleMatches.size() + " files");

        return possibleMatches;
    }

    public static List<Memory> loadFolderMemories(final UserInput userInput)
    {
        final Collection<File> files = FileUtils.listFiles(userInput.getStartingFolder(), ALL_EXTENSIONS, true);
        final List<Memory> memories = Collections.synchronizedList(new ArrayList<>());

        //this would load any remaining files from CacheMemories into memories
        loadMemories2(userInput, files, memories);
        userInput.populateRepoMetaData(memories);
        files.clear();

        return memories;
    }

    private static List<Memory> loadCurrentMemories(final UserInput userInput, final boolean cache)
    {
        final Collection<File> files = FileUtils.listFiles(userInput.getStartingFolder(), ALL_EXTENSIONS, true);

        final List<Memory> memories = Collections.synchronizedList(new ArrayList<>());

        //if the cache file exists, process it,. This can trim down the files collection and
        if (cache && CacheMemories.cacheFileExists())
        {
            CacheMemories.readCurrentMemories(userInput, memories, files);
        }

        //this would load any remaining files from CacheMemories into memories
        loadMemories(userInput, files, memories);
        userInput.populateRepoMetaData(memories);
        files.clear();

        return memories;
    }

    private static List<Memory> loadNewMemories(final UserInput userInput)
    {
        final Collection<File> files = FileUtils.listFiles(userInput.getStartingFolder(), ALL_EXTENSIONS, true);
        final List<Memory> memories = Collections.synchronizedList(new ArrayList<>());

        loadMemories(userInput, files, memories);

        files.clear();

        return memories;
    }

    private static void loadMemories2(final UserInput userInput, final Collection<File> files, final List<Memory> memories)
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

    private static void loadMemories(final UserInput userInput, final Collection<File> files, final List<Memory> memories)
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