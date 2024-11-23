package com.AngryPigeons.storage;

import com.AngryPigeons.domain.Killable;
import com.AngryPigeons.views.LevelScreen;

import java.util.ArrayList;
import java.util.List;

public class SavedLevel {

    // When winLevel() or loseLevel() is called, resetExistingLevelOrCreateNewLevel() creates a fresh level
    // then, isComplete is set to true for this fresh level (only in winLevel())
    // however, we don't call saveLevelInMemory() for this fresh level since a fresh level doesn't have any progress
    // instead, we merely disable loading for this savedLevel
    private boolean loadingDisabled;
    private boolean isComplete;

    private List<SavedKillable> iceBlocks;
    private List<SavedKillable> woodBlocks;
    private List<SavedKillable> stoneBlocks;

    private int birdPointer;

    private List<SavedKillable> smallPigs;
    private List<SavedKillable> mediumPigs;
    private List<SavedKillable> largePigs;

    // default constructor needed for GSON
    public SavedLevel() {
        this.iceBlocks = new ArrayList<>();
        this.woodBlocks = new ArrayList<>();
        this.stoneBlocks = new ArrayList<>();

        this.smallPigs = new ArrayList<>();
        this.mediumPigs = new ArrayList<>();
        this.largePigs = new ArrayList<>();

        this.birdPointer = 0;
        this.loadingDisabled = false;
    }

    public void save(LevelScreen levelScreen) {
        this.isComplete = levelScreen.isComplete();
        this.birdPointer = levelScreen.getBirdPointer();

        syncIn(iceBlocks, levelScreen.getIceBlocks());
        syncIn(woodBlocks, levelScreen.getWoodBlocks());
        syncIn(stoneBlocks, levelScreen.getStoneBlocks());

        syncIn(smallPigs, levelScreen.getSmallPigs());
        syncIn(mediumPigs, levelScreen.getMediumPigs());
        syncIn(largePigs, levelScreen.getLargePigs());
    }

    private void syncIn(List<SavedKillable> savedKillableList,  List<? extends Killable> killableList) {

        for(int i = 0; i < killableList.size(); i++) {
            Killable killable = killableList.get(i);
            SavedKillable savedKillable;

            if (i < savedKillableList.size()) {
                savedKillable = savedKillableList.get(i);
            } else {
                savedKillable = new SavedKillable( /*killable*/ );
                savedKillableList.add(savedKillable);
            }

            savedKillable.save(killable);
        }
    }

    private void syncOut(List<? extends Killable> killableList, List<SavedKillable> savedKillableList) {

        for(int i = 0; i < killableList.size(); i++) {
            Killable killable = killableList.get(i);
            SavedKillable savedKillable = savedKillableList.get(i);

            savedKillable.load(killable);
        }

    }

    public void load(LevelScreen levelScreen) {
        levelScreen.setComplete(isComplete);
        levelScreen.setBirdPointer(birdPointer);

        syncOut(levelScreen.getIceBlocks(), iceBlocks);
        syncOut(levelScreen.getWoodBlocks(), woodBlocks);
        syncOut(levelScreen.getStoneBlocks(), stoneBlocks);

        syncOut(levelScreen.getSmallPigs(), smallPigs);
        syncOut(levelScreen.getMediumPigs(), mediumPigs);
        syncOut(levelScreen.getLargePigs(), largePigs);
    }

    public boolean isComplete() {
        return isComplete;
    }

    public boolean isLoadingDisabled() {
        return loadingDisabled;
    }

    public void setLoadingDisabled(boolean loadingDisabled) {
        this.loadingDisabled = loadingDisabled;
    }

    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }
}
