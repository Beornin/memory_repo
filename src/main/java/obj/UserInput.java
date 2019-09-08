package obj;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;

public class UserInput
{
    private final MetaData metaData = new MetaData();
    private boolean imported;
    private File startingFolder = new File("Y:" + File.separator + "SharedFolder" + File.separator + "Pictures and Videos");

    public boolean isImported()
    {
        return imported;
    }

    public void setImported(final boolean imported)
    {
        this.imported = imported;
    }

    public File getStartingFolder()
    {
        return startingFolder;
    }

    public void setStartingFolder(final File startingFolder)
    {
        this.metaData.setTotalRepoSize(FileUtils.sizeOfDirectoryAsBigInteger(startingFolder).longValue());
        this.startingFolder = startingFolder;
    }

    public void populateRepoMetaData(final List<Memory> memories)
    {
        this.metaData.populate(memories);
    }

    public void printMetaData()
    {
        this.metaData.printMetaData();
    }
}