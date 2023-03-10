import obj.Memory;
import obj.UserInput;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import processing.Shared;
import processing.load.RunnableMemoryLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PapaSorting {
    public static void main(final String[] args) throws IOException {
        final String exportFolder = "D:\\Pictures and Videos\\Papas\\scannedSlidesProject\\Sort again";//where to put the sorted stuff
        final String level2Folder = "D:\\Pictures and Videos\\Papas\\scannedSlidesProject\\Level2Sort (common and scientific names mandatory)";
        final long startTime = System.nanoTime();
        final List<Memory> fullySortedMemories;

        final UserInput userInput = new UserInput();
        userInput.setImported(false);
        userInput.setStartingFolder(new File(level2Folder));
        System.out.println("Getting sorted memories...");
        fullySortedMemories = RunnableMemoryLoader.loadDateSortMemories(userInput);


        final List<Memory> toBeSortedMemories;

        final UserInput userInput2 = new UserInput();
        userInput2.setImported(false);
        userInput2.setStartingFolder(new File("D:\\Pictures and Videos\\Papas\\scannedSlidesProject\\L1A"));
        System.out.println("Getting to be sorted memories...");
        toBeSortedMemories = RunnableMemoryLoader.loadDateSortMemories(userInput2);

        String latinName;
        String[] latinNameParts;
        String commonName;
        int index = 0;
        System.out.println("Sorting...");
        String folder;
        String toMatchFileName;
        String[] parentSplit;
        String finalSaveFolder;
        ArrayList<String> alreadyCheckedFolders = new ArrayList<>();
        for (final Memory memory : fullySortedMemories) {
            index++;
            if (index % 100 == 0) {
                System.out.println("Sorting: " + index + " out of " + fullySortedMemories.size());
            }

            //if the folder contains scientific name
            if (StringUtils.countMatches(memory.getPath(), "(") > 1) {
                //D:\Pictures and Videos\Papas\scannedSlidesProject\Level2Sort (common and scientific names mandatory)\Alligators\American Alligator (Alligator mississippiensis)
                //System.out.println(memory.getFile().getParent());
                parentSplit = memory.getFile().getParent().split("\\\\");
                folder = parentSplit[parentSplit.length - 1];
                if (!alreadyCheckedFolders.contains(folder)) {
                    alreadyCheckedFolders.add(folder);

                    //System.out.println(folder.split("\\(")[0].replace(")", ""));
                    commonName = folder.split("\\(")[0].replace(")", "").toLowerCase();
                    if (folder.split("\\(").length > 1) {
                        latinName = folder.split("\\(")[1].replace(")", "").toLowerCase();
                        latinNameParts = latinName.split(" ");
                    } else {
                        latinName = "";
                        latinNameParts = new String[]{""};
                    }

                    for (int j = toBeSortedMemories.size() - 1; j >= 0; j--) {
                        toMatchFileName = toBeSortedMemories.get(j).getFile().getName().toLowerCase();

                        //if toMatch contains the full scientific name
                        if (!latinName.isEmpty() && toMatchFileName.contains(latinName)) {
                            finalSaveFolder = memory.getFile().getParent().replace(level2Folder, exportFolder);
                            Files.createDirectories(Paths.get(finalSaveFolder));
                            saveFile(finalSaveFolder, toBeSortedMemories.get(j));
                            toBeSortedMemories.remove(j);
                        } else if (!latinName.isEmpty() && latinName.contains(toMatchFileName)) {
                            finalSaveFolder = memory.getFile().getParent().replace(level2Folder, exportFolder);
                            Files.createDirectories(Paths.get(finalSaveFolder));
                            saveFile(finalSaveFolder, toBeSortedMemories.get(j));
                            toBeSortedMemories.remove(j);
                            //check full common names
                        } else if (toMatchFileName.contains(commonName)) {
                            finalSaveFolder = memory.getFile().getParent().replace(level2Folder, exportFolder);
                            Files.createDirectories(Paths.get(finalSaveFolder));
                            saveFile(finalSaveFolder, toBeSortedMemories.get(j));
                            toBeSortedMemories.remove(j);
                        } else if (commonName.contains(toMatchFileName)) {
                            finalSaveFolder = memory.getFile().getParent().replace(level2Folder, exportFolder);
                            Files.createDirectories(Paths.get(finalSaveFolder));
                            saveFile(finalSaveFolder, toBeSortedMemories.get(j));
                            toBeSortedMemories.remove(j);
                            //TODO to make wider sweep remove the condition here.
                            // maybe after initial move can enabled this
                        } else {
                            //hard code folder name types
                            if(toMatchFileName.contains("gator"))
                            {
                                finalSaveFolder = "D:\\Pictures and Videos\\Papas\\scannedSlidesProject\\WannaBeAiSorted\\Alligators";
                                Files.createDirectories(Paths.get(finalSaveFolder));
                                saveFile(finalSaveFolder, toBeSortedMemories.get(j));
                                toBeSortedMemories.remove(j);
                            }else if(toMatchFileName.contains("humm"))
                            {
                                finalSaveFolder = "D:\\Pictures and Videos\\Papas\\scannedSlidesProject\\WannaBeAiSorted\\Hummingbird";
                                Files.createDirectories(Paths.get(finalSaveFolder));
                                saveFile(finalSaveFolder, toBeSortedMemories.get(j));
                                toBeSortedMemories.remove(j);
                            }else if(toMatchFileName.contains("bat"))
                            {
                                finalSaveFolder = "D:\\Pictures and Videos\\Papas\\scannedSlidesProject\\WannaBeAiSorted\\Bat";
                                Files.createDirectories(Paths.get(finalSaveFolder));
                                saveFile(finalSaveFolder, toBeSortedMemories.get(j));
                                toBeSortedMemories.remove(j);
                            }else if(toMatchFileName.contains("bird") || toMatchFileName.contains("duck") || toMatchFileName.contains("warbler")
                            || toMatchFileName.contains("hawk") || toMatchFileName.contains("owl") || toMatchFileName.contains("sparrow"))
                            {
                                finalSaveFolder = "D:\\Pictures and Videos\\Papas\\scannedSlidesProject\\WannaBeAiSorted\\Birds";
                                Files.createDirectories(Paths.get(finalSaveFolder));
                                saveFile(finalSaveFolder, toBeSortedMemories.get(j));
                                toBeSortedMemories.remove(j);
                            }else if(toMatchFileName.contains("butterfly"))
                            {
                                finalSaveFolder = "D:\\Pictures and Videos\\Papas\\scannedSlidesProject\\WannaBeAiSorted\\Butterfly";
                                Files.createDirectories(Paths.get(finalSaveFolder));
                                saveFile(finalSaveFolder, toBeSortedMemories.get(j));
                                toBeSortedMemories.remove(j);
                            }else if(toMatchFileName.contains("wolf"))
                            {
                                finalSaveFolder = "D:\\Pictures and Videos\\Papas\\scannedSlidesProject\\WannaBeAiSorted\\Canine";
                                Files.createDirectories(Paths.get(finalSaveFolder));
                                saveFile(finalSaveFolder, toBeSortedMemories.get(j));
                                toBeSortedMemories.remove(j);
                            }else if(toMatchFileName.contains("crab"))
                            {
                                finalSaveFolder = "D:\\Pictures and Videos\\Papas\\scannedSlidesProject\\WannaBeAiSorted\\Crabs";
                                Files.createDirectories(Paths.get(finalSaveFolder));
                                saveFile(finalSaveFolder, toBeSortedMemories.get(j));
                                toBeSortedMemories.remove(j);
                            }else if(toMatchFileName.contains("crayfish"))
                            {
                                finalSaveFolder = "D:\\Pictures and Videos\\Papas\\scannedSlidesProject\\WannaBeAiSorted\\Crayfish";
                                Files.createDirectories(Paths.get(finalSaveFolder));
                                saveFile(finalSaveFolder, toBeSortedMemories.get(j));
                                toBeSortedMemories.remove(j);
                            }else if(toMatchFileName.contains("croc"))
                            {
                                finalSaveFolder = "D:\\Pictures and Videos\\Papas\\scannedSlidesProject\\WannaBeAiSorted\\Crocodile";
                                Files.createDirectories(Paths.get(finalSaveFolder));
                                saveFile(finalSaveFolder, toBeSortedMemories.get(j));
                                toBeSortedMemories.remove(j);
                            }else if(toMatchFileName.contains("tiger") || toMatchFileName.contains("panther")|| toMatchFileName.contains("cat"))
                            {
                                finalSaveFolder = "D:\\Pictures and Videos\\Papas\\scannedSlidesProject\\WannaBeAiSorted\\Feline";
                                Files.createDirectories(Paths.get(finalSaveFolder));
                                saveFile(finalSaveFolder, toBeSortedMemories.get(j));
                                toBeSortedMemories.remove(j);
                            }else if(toMatchFileName.contains("fish") || toMatchFileName.contains("shark"))
                            {
                                finalSaveFolder = "D:\\Pictures and Videos\\Papas\\scannedSlidesProject\\WannaBeAiSorted\\Fish";
                                Files.createDirectories(Paths.get(finalSaveFolder));
                                saveFile(finalSaveFolder, toBeSortedMemories.get(j));
                                toBeSortedMemories.remove(j);
                            }else if(toMatchFileName.contains("frog") || toMatchFileName.contains("tadpole"))
                            {
                                finalSaveFolder = "D:\\Pictures and Videos\\Papas\\scannedSlidesProject\\WannaBeAiSorted\\Frog";
                                Files.createDirectories(Paths.get(finalSaveFolder));
                                saveFile(finalSaveFolder, toBeSortedMemories.get(j));
                                toBeSortedMemories.remove(j);
                            }else if(toMatchFileName.contains("gecko"))
                            {
                                finalSaveFolder = "D:\\Pictures and Videos\\Papas\\scannedSlidesProject\\WannaBeAiSorted\\Geckos";
                                Files.createDirectories(Paths.get(finalSaveFolder));
                                saveFile(finalSaveFolder, toBeSortedMemories.get(j));
                                toBeSortedMemories.remove(j);
                            }else if(toMatchFileName.contains("liza"))
                            {
                                finalSaveFolder = "D:\\Pictures and Videos\\Papas\\scannedSlidesProject\\WannaBeAiSorted\\Lizards";
                                Files.createDirectories(Paths.get(finalSaveFolder));
                                saveFile(finalSaveFolder, toBeSortedMemories.get(j));
                                toBeSortedMemories.remove(j);
                            }else if(toMatchFileName.contains("sala"))
                            {
                                finalSaveFolder = "D:\\Pictures and Videos\\Papas\\scannedSlidesProject\\WannaBeAiSorted\\Salamander";
                                Files.createDirectories(Paths.get(finalSaveFolder));
                                saveFile(finalSaveFolder, toBeSortedMemories.get(j));
                                toBeSortedMemories.remove(j);
                            }else if(toMatchFileName.contains("skink"))
                            {
                                finalSaveFolder = "D:\\Pictures and Videos\\Papas\\scannedSlidesProject\\WannaBeAiSorted\\Skink";
                                Files.createDirectories(Paths.get(finalSaveFolder));
                                saveFile(finalSaveFolder, toBeSortedMemories.get(j));
                                toBeSortedMemories.remove(j);
                            }else if(toMatchFileName.contains("snake"))
                            {
                                finalSaveFolder = "D:\\Pictures and Videos\\Papas\\scannedSlidesProject\\WannaBeAiSorted\\Snakes";
                                Files.createDirectories(Paths.get(finalSaveFolder));
                                saveFile(finalSaveFolder, toBeSortedMemories.get(j));
                                toBeSortedMemories.remove(j);
                            }else if(toMatchFileName.contains("spider"))
                            {
                                finalSaveFolder = "D:\\Pictures and Videos\\Papas\\scannedSlidesProject\\WannaBeAiSorted\\Spiders";
                                Files.createDirectories(Paths.get(finalSaveFolder));
                                saveFile(finalSaveFolder, toBeSortedMemories.get(j));
                                toBeSortedMemories.remove(j);
                            }else if(toMatchFileName.contains("toad"))
                            {
                                finalSaveFolder = "D:\\Pictures and Videos\\Papas\\scannedSlidesProject\\WannaBeAiSorted\\Toad";
                                Files.createDirectories(Paths.get(finalSaveFolder));
                                saveFile(finalSaveFolder, toBeSortedMemories.get(j));
                                toBeSortedMemories.remove(j);
                            }else if(toMatchFileName.contains("turt"))
                            {
                                finalSaveFolder = "D:\\Pictures and Videos\\Papas\\scannedSlidesProject\\WannaBeAiSorted\\Turtles";
                                Files.createDirectories(Paths.get(finalSaveFolder));
                                saveFile(finalSaveFolder, toBeSortedMemories.get(j));
                                toBeSortedMemories.remove(j);
                            }else {
                                String temp = FilenameUtils.removeExtension(toMatchFileName).toLowerCase();

                            }
                        }
                    }
                }
            }
        }

        final long endTime = System.nanoTime();
        final long totalTime = endTime - startTime;
        System.out.println("Total sort  between " + fullySortedMemories.size() + " memories took: " + Shared.printTotalTimeTaken(totalTime));
    }

    private static void saveFile(String exportFolder, Memory memory) throws IOException {
        int num = 0;
        String save;
        File saveFile = new File(exportFolder + File.separator + memory.getName().trim().replaceAll(" +", " "));
        while (saveFile.exists()) {
            save = num++ + memory.getName().trim().replaceAll(" +", " ");
            saveFile = new File(exportFolder + File.separator + save);
        }
        Files.move(memory.getFile().toPath(), Paths.get(saveFile.toURI()));
    }
}
