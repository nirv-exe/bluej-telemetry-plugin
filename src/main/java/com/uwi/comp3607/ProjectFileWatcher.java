package com.uwi.comp3607;

import java.io.IOException;
import java.nio.file.*;

public class ProjectFileWatcher implements Runnable 
{
    private final Path directory;
    private final WatchService watchService;
    private volatile boolean running = true;

    public ProjectFileWatcher(String directoryPath)
            throws IOException {

        directory = Paths.get(directoryPath);

        watchService = FileSystems.getDefault()
                .newWatchService();

        directory.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE
        );
    }

    @Override
    public void run() 
    {
        while (running) {

            try {
                WatchKey key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents()) {

                    Path changedPath =
                            (Path) event.context();

                    if (event.kind() ==
                            StandardWatchEventKinds.ENTRY_CREATE) {

                        if (Files.isDirectory(
                                directory.resolve(changedPath))) {

                            System.out.println(
                                    "Project directory created: "
                                    + changedPath);
                        }

                    } else if (event.kind() ==
                            StandardWatchEventKinds.ENTRY_DELETE) {

                        System.out.println(
                                "Directory or file deleted: "
                                + changedPath);
                    }
                }

                if (!key.reset()) {
                    break;
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stopWatching() throws IOException {
        running = false;
        watchService.close();
    }
}

