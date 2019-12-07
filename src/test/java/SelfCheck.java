import obj.Memory;
import obj.UserInput;
import org.testng.annotations.Test;
import processing.check.MemoryChecker;
import processing.load.RunnableMemoryLoader;

import java.io.File;
import java.util.List;

public class SelfCheck
{
    static List<Memory> testMemories;
    @Test()
    public void testMemoriesLoad()
    {
        final UserInput uio = new UserInput();
        uio.setImported(false);
        uio.setStartingFolder(new File("src/test/java/data"));

        testMemories = RunnableMemoryLoader.loadMemories(uio);
        assert testMemories.size() == 9;
    }

    @Test(priority = 1, dependsOnMethods = "testMemoriesLoad")
    public void testMatchLogic()
    {
        MemoryChecker.checkForDuplicateMemories(testMemories, false);
        boolean result = true;
        for(final Memory temp: testMemories)
        {
            //if it isn't matched and it isn't from the 'should get matched' report it
            if(!temp.isMatched() && temp.getPath().contains("duplicate"))
            {
               System.out.println("ISSUE--" + temp.getName() + " was NOT matched!");
               result = false;
            }
            //if this is matched but is in the edited path, it should not have matched!
            else if(temp.isMatched() && temp.getPath().contains("edited"))
            {
                System.out.println("ISSUE--" + temp.getName() + " WAS matched and should not have been!");
                result = false;
            }
            else if(temp.isMatched() && temp.getPath().contains("duplicate"))
            {
                System.out.println("PASS--" + temp.getName() + " WAS matched correctly!");
            }
            else if(!temp.isMatched() && temp.getPath().contains("edited"))
            {
                System.out.println("PASS--" + temp.getName() + " WAS NOT matched correctly!");
            }
        }
        assert result;
    }
}