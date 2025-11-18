package ca.concordia.filesystem;

import ca.concordia.filesystem.datastructures.FEntry;
import java.io.IOException;
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
    globalLock.lock();
    try {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("Invalid filename");
        }
        if (fileName.length() > 11) {
            throw new IllegalArgumentException("Filename cannot be longer than 11 characters.");
        }
        int existingIndex = findFileEntryByName(fileName);
        if (existingIndex >= 0) {
            throw new Exception("File already exists");
        }
        int freeIndex = findFreeInodeIndex();
        if (freeIndex == -1) {
            throw new Exception("Maximum number of files reached");
        }

        // 4) Create new FEntry with size = 0 and firstBlock = -1 (no data yet)
        inodeTable[freeIndex] = new FEntry(fileName, (short) 0, (short) -1);

    } finally {
        globalLock.unlock();
    }
}
private int allocateBlock() {
    for (int i = 0; i < MAXBLOCKS; i++) {
        if (freeBlockList[i]) {      
            freeBlockList[i] = false; 
            return i;
        }
    }
    return -1; 
}

private int findFileEntryByName(String fileName) {
    if (fileName == null) {
        return -1;
    }
    for (int i = 0; i < MAXFILES; i++) {
        if (inodeTable[i] != null &&
                fileName.equals(inodeTable[i].getFilename())) {
            return i;
        }
    }
    return -1;
}

private int findFreeInodeIndex() {
    for (int i = 0; i < MAXFILES; i++) {
        if (inodeTable[i] == null) {
            return i;
        }
    }
    return -1;
}

private int blocksForSize(int size) {
    if (size <= 0) {
        return 0;
    }
    return (size + BLOCK_SIZE - 1) / BLOCK_SIZE;
}
private int[] allocateBlocks(int count) throws Exception {
    if (count <= 0) {
        return new int[0];
    }

    int[] blocks = new int[count];
    int allocatedSoFar = 0;

    for (int i = 0; i < count; i++) {
        int b = allocateBlock();
        if (b == -1) {
            for (int j = 0; j < allocatedSoFar; j++) {
                freeBlockList[blocks[j]] = true;
            }
            throw new Exception("Not enough space for file");
        }
        blocks[i] = b;
        allocatedSoFar++;
    }

    return blocks;
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


}
