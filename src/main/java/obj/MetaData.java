package obj;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Class to store data to print out if user wants. Data points are:
 * total size of repo
 * total # of files in repo
 * file types in repo with # counter with % from total
 */
public class MetaData
{
    private String totalRepoSize;
    private int memoriesInRepo;
    private Map<String, Long> fileExtCountMap;

    void populate(final List<Memory> memories)
    {
        setFileExtCountMap(memories);
        setMemoriesInRepo(memories.size());
    }

    private String getTotalRepoSize()
    {
        return totalRepoSize;
    }

    private int getMemoriesInRepo()
    {
        return memoriesInRepo;
    }

    private void setMemoriesInRepo(final int memoriesInRepo)
    {
        this.memoriesInRepo = memoriesInRepo;
    }

    void setTotalRepoSize(final long totalRepoSize)
    {
        this.totalRepoSize = formatFileSize(totalRepoSize);
    }

    private void setFileExtCountMap(final List<Memory> memories)
    {
        if (memories != null && memories.size() > 0)
        {
            this.fileExtCountMap = memories.stream().map(f -> f.getName().toUpperCase()).map(n ->
                    n.substring(n.lastIndexOf(".") + 1)).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        }
    }

    void printMetaData()
    {
        System.out.println("************************");
        System.out.println("***REPO META DATA***");
        System.out.println("Total Repo Size: " + getTotalRepoSize());
        System.out.println("Total Repo Memories: " + getMemoriesInRepo());

        System.out.println("***File Type - Count - Percent of Repo***");
        final Iterator it = fileExtCountMap.entrySet().iterator();
        BigDecimal bd;
        while (it.hasNext())
        {
            final Map.Entry pair = (Map.Entry) it.next();
            bd = new BigDecimal(Double.toString((((long) pair.getValue() / (double) getMemoriesInRepo()) * 100)));
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            System.out.println(pair.getKey() + " - " + pair.getValue() + " - " + bd.toPlainString() + "%");
            it.remove();
        }
        System.out.println("************************");
    }

    private static String formatFileSize(final long size)
    {
        final String hrSize;
        final double k = size / 1024.0;
        final double m = ((size / 1024.0) / 1024.0);
        final double g = (((size / 1024.0) / 1024.0) / 1024.0);
        final double t = ((((size / 1024.0) / 1024.0) / 1024.0) / 1024.0);

        final DecimalFormat dec = new DecimalFormat("0.00");

        if (t > 1)
        {
            hrSize = dec.format(t).concat(" TB");
        }
        else if (g > 1)
        {
            hrSize = dec.format(g).concat(" GB");
        }
        else if (m > 1)
        {
            hrSize = dec.format(m).concat(" MB");
        }
        else if (k > 1)
        {
            hrSize = dec.format(k).concat(" KB");
        }
        else
        {
            hrSize = dec.format((double) size).concat(" Bytes");
        }

        return hrSize;
    }
}