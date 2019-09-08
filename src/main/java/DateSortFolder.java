import obj.Memory;
import obj.UserInput;
import processing.RunnableMemoryLoader;
import processing.Shared;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


class DateSortFolder
{
    private static void mainProcess()
    {
        final String exportFolder = "Y:" + File.separator + "SharedFolder" + File.separator + "Pictures and Videos" + File.separator + "Tucker Dates" + File.separator;//where to put all the date sorted memories
        final String pattern2 = "yyyy-MM-dd";
        final SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern2);
        final long startTime = System.nanoTime();
        final List<Memory> currentMemories;

        final UserInput userInput = new UserInput();
        userInput.setImported(false);
        userInput.setStartingFolder(new File("Y:" + File.separator + "SharedFolder" + File.separator + "Pictures and Videos" + File.separator + "Tucker"));
        System.out.println("Getting current memories...");
        currentMemories = RunnableMemoryLoader.loadFolderMemories(userInput);

        String date;
        BasicFileAttributes attr;
        Date creationDate;
        int index = 0;
        try
        {
            System.out.println("Sorting...");
            for (final Memory memory : currentMemories)
            {
                index++;
                if (index % 1000 == 0)
                {
                    System.out.println("Sorting: " + index + " out of " + currentMemories.size());
                }

                date = memory.getDate();
                if (date == null)
                {
                    attr = Files.readAttributes(memory.getFile().toPath(), BasicFileAttributes.class);
                    creationDate = new Date(attr.creationTime().to(TimeUnit.MILLISECONDS));
                    Files.createDirectories(Paths.get(exportFolder + simpleDateFormat2.format(creationDate) + "-CRT"));
                    if (!new File(exportFolder + simpleDateFormat2.format(creationDate) + "-CRT" + File.separator + memory.getName()).exists())
                    {
                        Files.copy(memory.getFile().toPath(), Paths.get(exportFolder + simpleDateFormat2.format(creationDate) + "-CRT" + File.separator + memory.getName()));
                    }
                    else
                    {
                        int num = 0;
                        String save;
                        File saveFile = new File(exportFolder + simpleDateFormat2.format(creationDate) + "-CRT" + File.separator + memory.getName());
                        while (saveFile.exists())
                        {
                            save = num++ + memory.getName();
                            saveFile = new File(exportFolder + simpleDateFormat2.format(creationDate) + "-CRT" + File.separator + save);
                        }
                        Files.copy(memory.getFile().toPath(), Paths.get(saveFile.toURI()));
                    }
                }
                else
                {
                    Files.createDirectories(Paths.get(exportFolder + date));
                    if (!new File(exportFolder + date + File.separator + memory.getName()).exists())
                    {
                        Files.copy(memory.getFile().toPath(), Paths.get(exportFolder + date + File.separator + memory.getName()));
                    }
                    else
                    {
                        int num = 0;
                        String save;
                        File saveFile = new File(exportFolder + date + File.separator + memory.getName());
                        while (saveFile.exists())
                        {
                            save = num++ + memory.getName();
                            saveFile = new File(exportFolder + date + File.separator + save);
                        }
                        Files.copy(memory.getFile().toPath(), Paths.get(saveFile.toURI()));
                    }
                }
            }
        } catch (final IOException ioe)
        {
            ioe.printStackTrace();
        }

        final long endTime = System.nanoTime();
        final long totalTime = endTime - startTime;
        System.out.println("Total  date sort  between " + currentMemories.size() + " memories took: " + Shared.printTotalTimeTaken(totalTime));

    }

    public static void main(final String[] args)
    {
        mainProcess();
    }
}