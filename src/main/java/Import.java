import io.Mover;
import obj.TestObj;
import obj.UserInputObj;
import processing.LoadPictureRunnable;
import processing.Loader;
import processing.Shared;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Issue when 2 files named same in staging are put in same pass/flagged folder
 */
class Import
{
    private static void mainProcess()
    {
        final long startTime = System.nanoTime();
        ArrayList<TestObj> startingFiles = new ArrayList<>(0);

        //move all files from GDrive to staging folder
        Mover.moveToStaging();

        //Then gather up the staged files
        final UserInputObj importUio = new UserInputObj();
        importUio.setStartingFolder(new File("Z:\\Imports\\Stage"));
        importUio.setImported(true);
        final ArrayList<TestObj> stagedFiles = Loader.gatherNewFiles(importUio);

        //If we have any staged files
        if (stagedFiles != null && stagedFiles.size() > 0)
        {
        /*make a thread runner to take 1 stage file, and compare to all of startingFiles
             if passes startingFiles test, see if any files are in the passed on z drive, add those into the check for this thread
            if STILL passes, then add the 1 stage file to the passed on z drive
         */

            //Get all the current files on ShareDrive
            final UserInputObj userInputObj = new UserInputObj();
            userInputObj.setImported(false);
            userInputObj.setStartingFolder(new File("Y:\\SharedFolder\\Pictures and Videos"));
            startingFiles = Loader.gatherCurrentFiles(userInputObj);

            //Gather up all in Pass that have not been added to ShareDrive yet
            final UserInputObj passedUio = new UserInputObj();
            passedUio.setStartingFolder(new File("Z:\\Imports\\Pass"));
            passedUio.setImported(false);
            final ArrayList<TestObj> passedFiles = Loader.gatherCurrentFiles(passedUio);
            //add in to are shared ones since these previously passed validations
            startingFiles.addAll(passedFiles);

            final ExecutorService pool = Executors.newFixedThreadPool(3);
            for (final TestObj stageFile : stagedFiles)
            {
                final Runnable r1 = new LoadPictureRunnable(stageFile, stagedFiles, startingFiles);
                pool.execute(r1);
            }
            try
            {
                pool.shutdown();
                //Wait for threads to all stop
                pool.awaitTermination(1, TimeUnit.DAYS);

                //Move all staged files that were imported, and not matched to Z/imports/pass
                Mover.movePassed(stagedFiles);

            } catch (final InterruptedException ie)
            {
                ie.printStackTrace();
            }
        }
        final long endTime = System.nanoTime();
        final long totalTime = endTime - startTime;
        System.out.println("Total  check  between " + startingFiles.size() + " starting files and " + Objects.requireNonNull(stagedFiles).size() + " staged files took: " + Shared.printTotalTimeTaken(totalTime));
    }

    public static void main(final String[] args)
    {
        mainProcess();
    }
}