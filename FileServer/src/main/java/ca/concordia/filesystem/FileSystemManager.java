package ca.concordia.filesystem;

import ca.concordia.filesystem.datastructures.FEntry;

import java.io.RandomAccessFile;
import java.util.concurrent.locks.ReentrantLock;

public class FileSystemManager {

    private final int MAXFILES = 5;
    private final int MAXBLOCKS = 10;
    private final static FileSystemManager instance;
    private final RandomAccessFile disk;
    private final ReentrantLock globalLock = new ReentrantLock();

    private static final int BLOCK_SIZE = 128; // Example block size

    private FEntry[] inodeTable; // Array of inodes
    private boolean[] freeBlockList; // Bitmap for free blocks

    public FileSystemManager(String filename, int totalSize) {
   try {
        RandomAccessFile d = new RandomAccessFile(filename, "rw");


        d.setLength(totalSize);

        this.disk = d;

        inodeTable = new FEntry[MAXFILES];
        freeBlockList = new boolean[MAXBLOCKS];

        for (int i = 0; i < MAXBLOCKS; i++) {
            freeBlockList[i] = true;
        }

    } catch (IOException e) {
        throw new RuntimeException("Failed to initialize file system", e);
    }
}

    public void createFile(String fileName) throws Exception {
        // TODO
        throw new UnsupportedOperationException("Method not implemented yet.");
    }


    // TODO: Add readFile, writeFile and other required methods,
}
private void freeBlocksForFile(FEntry entry) {
    if (entry == null) {
        return;
    }
    short first = entry.getFirstBlock();
    short size = entry.getFilesize();

    if (first < 0 || size <= 0) {
        return;
    }

    int blocksUsed = blocksForSize(size);

    for (int k = 0; k < blocksUsed; k++) {
        int blockIndex = first + k;
        if (blockIndex >= 0 && blockIndex < MAXBLOCKS) {
            freeBlockList[blockIndex] = true;
        }
    }
}
