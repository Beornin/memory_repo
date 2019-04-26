package processing;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.concurrent.TimeUnit;

public class Shared
{
    static byte[] returnPixelVal(final File in)
    {
        final BufferedImage img;
        final File f;
        byte[] pixels = null;
        try
        {
            f = in;
            img = ImageIO.read(f);
            pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
        } catch (final Exception e)
        {
            System.out.println(in.getPath());
            e.printStackTrace();
        }

        return pixels;
    }

    public static String printTotalTimeTaken(final long totalTime)
    {
        final String res;
        final long hours = TimeUnit.NANOSECONDS.toHours(totalTime) - TimeUnit.DAYS.toHours(TimeUnit.NANOSECONDS.toDays(totalTime));
        final long minutes = TimeUnit.NANOSECONDS.toMinutes(totalTime) - TimeUnit.HOURS.toMinutes(TimeUnit.NANOSECONDS.toHours(totalTime));
        final long seconds = TimeUnit.NANOSECONDS.toSeconds(totalTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.NANOSECONDS.toMinutes(totalTime));
        final long millis = TimeUnit.NANOSECONDS.toMillis(totalTime) - TimeUnit.SECONDS.toMillis(TimeUnit.NANOSECONDS.toSeconds(totalTime));

        if (hours > 0)
        {
            res = String.format("%02d(h)%02d(m)%02d(s)%04d(ms)", hours, minutes, seconds, millis);
        }
        else if (minutes > 0)
        {
            res = String.format("%02d(m)%02d(s)%04d(ms)", minutes, seconds, millis);
        }
        else if (seconds > 0)
        {
            res = String.format("%02d(s)%04d(ms)", seconds, millis);
        }
        else
        {
            res = String.format("%04d(ms)", millis);
        }

        return res;
    }
}