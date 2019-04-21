package processing;

import io.Reporter;
import obj.TestObj;

import java.util.ArrayList;
import java.util.List;

public class LoadPictureRunnable implements Runnable
{
    final private TestObj currentStaged;
    final private List<TestObj> currentFiles;
    final private List<TestObj> stagedFiles;

    public LoadPictureRunnable(final TestObj pic, final List<TestObj> stagedFiles, final List<TestObj> currentFiles)
    {
        this.currentStaged = pic;
        this.stagedFiles = stagedFiles;
        this.currentFiles = currentFiles;
    }

    public void run()
    {
        try
        {
            final long startTime = System.nanoTime();
            final ArrayList<TestObj> matches = new ArrayList<>();

            //if we have to fully scan the pic, save it so we don't do it each time
            byte[] tempByte = null;

            //check that none of the new files match what we already have
            //this makes sure someone doesn't add a file that is already stored
            for (final TestObj currentFile : currentFiles)
            {
                if (currentFile.isMatched() || currentStaged.equals(currentFile))
                {
                    continue;
                }
                try
                {
                    if (DetermineMatch.isProbablePictureMatch(currentStaged, currentFile))
                    {
                        if (tempByte == null && currentStaged.getMetadata() == null)
                        {
                            tempByte = Shared.returnPixelVal(currentStaged.getFile());
                        }

                        if (currentStaged.getMetadata() == null && currentFile.getMetadata() == null && DetermineMatch.isDuplicatePictureMatch(tempByte, currentFile.getFile()))
                        {
                            DetermineMatch.setMatchedItems(matches, currentStaged, currentFile);
                        }
                        if (currentStaged.getMetadata() != null && currentFile.getMetadata() != null && DetermineMatch.isDuplicatePictureMatchRAW(currentStaged, currentFile))
                        {
                            DetermineMatch.setMatchedItems(matches, currentStaged, currentFile);
                        }
                    }

                    if (DetermineMatch.isPossibleVideoMatch(currentStaged, currentFile))
                    {
                        if (DetermineMatch.isDuplicateVideo(currentStaged.getFile().toPath(), currentFile.getFile().toPath()))
                        {
                            DetermineMatch.setMatchedItems(matches, currentStaged, currentFile);
                        }
                    }
                } catch (final Exception e)
                {
                    e.printStackTrace();
                }
            }

            //check that none of the new files match what is coming in the single staged.
            //this makes sure someone doesn't add the same picture twice during import
            for (final TestObj otherStagedFile : stagedFiles)
            {
                if (otherStagedFile.isMatched() || currentStaged.equals(otherStagedFile))
                {
                    continue;
                }

                try
                {
                    if (DetermineMatch.isProbablePictureMatch(currentStaged, otherStagedFile))
                    {
                        if (tempByte == null && currentStaged.getMetadata() == null)
                        {
                            tempByte = Shared.returnPixelVal(currentStaged.getFile());
                        }

                        if (currentStaged.getMetadata() == null && otherStagedFile.getMetadata() == null && DetermineMatch.isDuplicatePictureMatch(tempByte, otherStagedFile.getFile()))
                        {
                            DetermineMatch.setMatchedItems(matches, currentStaged, otherStagedFile);
                        }
                        if (currentStaged.getMetadata() != null && otherStagedFile.getMetadata() != null && DetermineMatch.isDuplicatePictureMatchRAW(currentStaged, otherStagedFile))
                        {
                            DetermineMatch.setMatchedItems(matches, currentStaged, otherStagedFile);
                        }
                    }

                    if (DetermineMatch.isPossibleVideoMatch(currentStaged, otherStagedFile))
                    {
                        if (DetermineMatch.isDuplicateVideo(currentStaged.getFile().toPath(), otherStagedFile.getFile().toPath()))
                        {
                            DetermineMatch.setMatchedItems(matches, currentStaged, otherStagedFile);
                        }
                    }
                } catch (final Exception e)
                {
                    e.printStackTrace();
                }
            }

            if (!matches.isEmpty())
            {
                Reporter.reportDuplicates(currentStaged.getName(), matches);
            }
            else
            {
                //Mover.movePassed(currentStaged);
            }

            final long endTime = System.nanoTime();
            final long totalTime = endTime - startTime;
            System.out.println("Total  check  for file " + currentStaged.getName() + " took: " + Shared.printTotalTimeTaken(totalTime));
        } catch (final Exception e)
        {
            e.printStackTrace();
        }
    }
}