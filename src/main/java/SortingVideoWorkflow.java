import obj.Memory;
import obj.UserInput;
import processing.Shared;
import processing.load.RunnableMemoryLoader;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


class SortingVideoWorkflow {
    private static void mainProcess() {
        final String exportFolder = "D:\\Pictures and Videos\\AASorted" + File.separator;//where to put all the date sorted memories
        final String pattern2 = "yyyy-MM";
        final SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern2);
        final long startTime = System.nanoTime();
        final List<Memory> currentMemories;

        final UserInput userInput = new UserInput();
        userInput.setImported(false);
        userInput.setStartingFolder(new File("D:\\Pictures and Videos\\AAUnsorted"));
        System.out.println("Getting current video memories...");
        currentMemories = RunnableMemoryLoader.loadDateSortVideoMemories(userInput);

        String date;
        BasicFileAttributes attr;
        Date creationDate;
        int index = 0;
        try {
            System.out.println("Sorting...");
            for (final Memory memory : currentMemories) {
                index++;
                if (index % 100 == 0) {
                    System.out.println("Sorting: " + index + " out of " + currentMemories.size());
                }

                date = memory.getDate();
                String exportPath = String.valueOf(Paths.get(exportFolder + date.split("-")[0].replace("-","") + File.separator + date.split("-")[1].replace("-","")));
                Files.createDirectories(Paths.get(exportFolder + date.split("-")[0].replace("-", "") + File.separator + date.split("-")[1].replace("-", "")));
                if (!new File(exportPath + File.separator + memory.getName()).exists()) {
                    Files.copy(memory.getFile().toPath(), Paths.get(exportPath + File.separator + memory.getName()));
                } else {
                    int num = 0;
                    String save;
                    File saveFile = new File(exportPath + File.separator + memory.getName());
                    while (saveFile.exists()) {
                        save = num++ + memory.getName();
                        saveFile = new File(exportPath + File.separator + save);
                    }
                    Files.copy(memory.getFile().toPath(), Paths.get(saveFile.toURI()));
                }
            }
        } catch (final Exception ioe) {
            ioe.printStackTrace();
        }

        final long endTime = System.nanoTime();
        final long totalTime = endTime - startTime;
        System.out.println("Total  date sort  between " + currentMemories.size() + " memories took: " + Shared.printTotalTimeTaken(totalTime));

    }

    public static void main(final String[] args) {
        mainProcess();
    }
}