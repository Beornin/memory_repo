package obj;

import java.io.File;

public class UserInputObj
{
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
        this.startingFolder = startingFolder;
    }
}