import de.ott.ivy.IvyLeeTask;

import java.io.File;

/**
 * TODO: Insert Description!
 * Project: ivy-lee
 * Package: PACKAGE_NAME
 * Created: 28.01.2020 18:05
 *
 * @author = manuel.ott
 * @since = 28. Januar 2020
 */
public class Main {

    public static void main(String[] args) {
        IvyLeeTask task = new IvyLeeTask();

        File x = new File("\\\\pluto\\z1\\1\\0852\\323.PDF".replace('\\', '/'));
        System.out.println(x.getName());
        System.out.println(x.getParentFile().getAbsolutePath());

        System.out.println(task);
    }

}
