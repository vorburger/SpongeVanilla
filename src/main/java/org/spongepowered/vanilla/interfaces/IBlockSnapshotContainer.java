package org.spongepowered.vanilla.interfaces;

import org.spongepowered.api.block.BlockSnapshot;

import java.util.ArrayList;

public interface IBlockSnapshotContainer {
    boolean isCapturingBlockSnapshots();
    void captureBlockSnapshots(boolean captureSnapshots);
    ArrayList<BlockSnapshot> getCapturedSnapshots();
}
