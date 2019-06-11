package processing;

import io.CacheMemories;
import obj.Memory;
import obj.UserInputObj;
import org.apache.commons.io.FileUtils;

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

    public static List<Memory> gatherCurrentFiles(final UserInputObj uio, final boolean cache)
    {
        final long startTime1 = System.nanoTime();

        final List<Memory> possibleMatches = RunnableMemoryLoader.loadCurrentMemories(uio, cache);

        final long endTime1 = System.nanoTime();
        final long totalTime1 = endTime1 - startTime1;
        System.out.println(" took: " + Shared.printTotalTimeTaken(totalTime1) + " to get " + possibleMatches.size() + " files");

        return possibleMatches;
    }

    public static List<Memory> gatherNewFiles(final UserInputObj uio)
    {
        final long startTime1 = System.nanoTime();

        final List<Memory> possibleMatches = RunnableMemoryLoader.loadNewMemories(uio);

        final long endTime1 = System.nanoTime();
        final long totalTime1 = endTime1 - startTime1;
        System.out.println(" took: " + Shared.printTotalTimeTaken(totalTime1) + " to get " + possibleMatches.size() + " files");

        return possibleMatches;
    }

    private static List<Memory> loadCurrentMemories(final UserInputObj userInputObj, final boolean cache)
    {
        final Collection<File> files = FileUtils.listFiles(userInputObj.getStartingFolder(), ALL_EXTENSIONS, true);
        final List<Memory> memories = Collections.synchronizedList(new ArrayList<>());

        //if the cache file exists, process it,. This can trim down the files collection and
        if (cache && CacheMemories.cacheFileExists())
        {
            CacheMemories.readCurrentMemories(userInputObj, memories, files);
        }

        //this would load any remaining files from CacheMemories into memories
        loadMemories(userInputObj, files, memories);

        files.clear();

        return memories;
    }

    private static List<Memory> loadNewMemories(final UserInputObj userInputObj)
    {
        final Collection<File> files = FileUtils.listFiles(userInputObj.getStartingFolder(), ALL_EXTENSIONS, true);
        final List<Memory> memories = Collections.synchronizedList(new ArrayList<>());

        loadMemories(userInputObj, files, memories);

        files.clear();

        return memories;
    }

    private static void loadMemories(final UserInputObj userInputObj, final Collection<File> files, final List<Memory> memories)
    {
        final ExecutorService pool = Executors.newFixedThreadPool(2);

        if (!files.isEmpty())
        {
            System.out.println("Loading memories..");
            for (final File file : files)
            {
                final Runnable memoryCreate = new MemoryCreate(userInputObj, file, memories);
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