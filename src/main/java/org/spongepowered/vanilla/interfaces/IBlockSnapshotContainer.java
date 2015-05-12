package org.spongepowered.vanilla.interfaces;

import org.spongepowered.api.block.BlockSnapshot;

import java.util.ArrayList;

public interface IBlockSnapshotContainer {
    boolean isCapturingBlockSnapshots();
    boolean isRestoringBlockSnapshots();
    void captureBlockSnapshots(boolean captureSnapshots);
    void restoreBlockSnapshots(boolean restoreSnapshots);
    ArrayList<BlockSnapshot> getCapturedSnapshots();
}
