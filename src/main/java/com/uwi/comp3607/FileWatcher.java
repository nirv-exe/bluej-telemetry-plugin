package com.uwi.comp3607;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class FileWatcher implements Runnable 
{
    private final Path directory;
    private WatchService watchService;
    private volatile boolean running = true;

    public FileWatcher(String directoryPath)
            throws IOException {

        this.directory = Paths.get(directoryPath);

        watchService = FileSystems.getDefault()
                .newWatchService();

        registerDirectory();
    }

    private void registerDirectory() throws IOException 
    {
        directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
    }

    @Override
    public void run() 
    {
        while (running) {
            try {
                WatchKey key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents()) {

                    WatchEvent.Kind<?> kind = event.kind();

                    Path fileName = (Path) event.context();

                    if (fileName.toString().endsWith(".java")) {

                        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                            System.out.println(
                                    "Java class created: " + fileName);

                        } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                            System.out.println(
                                    "Java class modified: " + fileName);

                        } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                            System.out.println(
                                    "Java class deleted: " + fileName);
                        }
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
