package processing;

import io.Reporter;
import obj.Memory;

import java.util.ArrayList;
import java.util.List;

public class RunnableMemoryChecker implements Runnable
{
    final private Memory currentStaged;
    final private List<Memory> currentFiles;
    final private List<Memory> stagedFiles;

    public RunnableMemoryChecker(final Memory pic, final List<Memory> stagedFiles, final List<Memory> currentFiles)
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
            final ArrayList<Memory> matches = new ArrayList<>();

            //if we have to fully scan the pic, save it so we don't do it each time
            byte[] tempByte = null;

            //check that none of the new files match what we already have
            //this makes sure someone doesn't add a file that is already stored
            for (final Memory currentFile : currentFiles)
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

                    if (DetermineMatch.isPossibleVideoMatch(currentStaged, currentFile) &&
                            DetermineMatch.isDuplicateVideo(currentStaged.getFile().toPath(), currentFile.getFile().toPath()))
                    {
                        DetermineMatch.setMatchedItems(matches, currentStaged, currentFile);
                    }
                } catch (final Exception e)
                {
                    e.printStackTrace();
                }
            }

            //check that none of the new files match what is coming in the single staged.
            //this makes sure someone doesn't add the same picture twice during import
            for (final Memory otherStagedFile : stagedFiles)
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

                    if (DetermineMatch.isPossibleVideoMatch(currentStaged, otherStagedFile) &&
                            DetermineMatch.isDuplicateVideo(currentStaged.getFile().toPath(), otherStagedFile.getFile().toPath()))
                    {
                        DetermineMatch.setMatchedItems(matches, currentStaged, otherStagedFile);
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

            final long endTime = System.nanoTime();
            final long totalTime = endTime - startTime;
            System.out.println("Total  check  for file " + currentStaged.getName() + " took: " + Shared.printTotalTimeTaken(totalTime));
        } catch (final Exception e)
        {
            e.printStackTrace();
        }
    }
}