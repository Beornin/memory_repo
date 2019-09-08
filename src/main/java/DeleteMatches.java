import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

class DeleteMatches
{

    public static void main(final String[] args)
    {
        deleteFiles();
    }

    private static void deleteFiles()
    {
        final File matchesFile = new File(System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "matches.txt");
        final ArrayList<File> files = new ArrayList<>();

        try (final BufferedReader br = new BufferedReader(new FileReader(matchesFile)))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                line = line.trim();
                if (StringUtils.isNotEmpty(line) && !StringUtils.startsWith(line, "xxx") && !StringUtils.equals("*******************************************************************************", line))
                {
                    final File file = new File(line);
                    files.add(file);
                }
            }
        } catch (final Exception ignored)
        {
        }

        for (final File temp : files)
        {
            try
            {
                Files.delete(Paths.get(temp.getPath()));
            } catch (final Exception ignored)
            {
            }
        }
    }
}