package processing;

import obj.TestObj;
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

public class Loader
{
    private static final String[] ALL_EXTENSIONS = new String[]{
            "gif", "png", "bmp", "jpg", "jpeg", "heic", "mp4", "mov", "mp4", "mov", "avi", "vlc", "wmv", "raw", "cr2", "tiff", "tif",
            "GIF", "PNG", "BMP", "JPG", "JPEG", "HEIC", "MP4", "MOV", "MP4", "MOV", "AVI", "VLC", "WMV", "RAW", "CR2", "TIFF", "TIF"
    };

    public static List<TestObj> gatherCurrentFiles(final UserInputObj uio)
    {
        final long startTime1 = System.nanoTime();

        System.out.println("Getting current files...");

        final List<TestObj> possibleMatches = Loader.listFiles(uio);

        final long endTime1 = System.nanoTime();
        final long totalTime1 = endTime1 - startTime1;
        System.out.println(" took: " + Shared.printTotalTimeTaken(totalTime1) + " to get " + possibleMatches.size() + " files");

        return possibleMatches;
    }

    public static List<TestObj> gatherNewFiles(final UserInputObj uio)
    {
        final long startTime1 = System.nanoTime();

        System.out.print("Getting STAGE files...");

        final List<TestObj> possibleMatches = Loader.listFiles(uio);

        final long endTime1 = System.nanoTime();
        final long totalTime1 = endTime1 - startTime1;
        System.out.println(" took: " + Shared.printTotalTimeTaken(totalTime1) + " to get " + possibleMatches.size() + " files");

        return possibleMatches;
    }

    private static List<TestObj> listFiles(final UserInputObj userInputObj)
    {
        final Collection<File> files = FileUtils.listFiles(userInputObj.getStartingFolder(), ALL_EXTENSIONS, true);
        final List<TestObj> objs = Collections.synchronizedList(new ArrayList<>());

        final ExecutorService pool = Executors.newFixedThreadPool(3);

        if (files.size() > 0)
        {
            for (final File file : files)
            {
                final Runnable r1 = new MemoryCreate(userInputObj, file, objs);
                pool.execute(r1);
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
        files.clear();
        return objs;
    }
}